package com.example.idcardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idcardreader.ZKUSBManager.ZKUSBManager;
import com.example.idcardreader.ZKUSBManager.ZKUSBManagerListener;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.IDCardType;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class MainActivity01 extends AppCompatActivity {
    private ZKUSBManager zkusbManager = null;
    private CheckBox checkRepeat = null;
    private ImageView imageView = null;
    private TextView textSuccessCount = null;
    private TextView textFailCount = null;
    private TextView textTimeCost = null;
    private TextView textResult = null;
    private TextView textMaxTimeCost = null;
    private CheckBox checkReadFp = null;
    private boolean bRepeatMode = false;
    private boolean bReadFp = false;

    private IDCardReader idCardReader = null;
    private boolean bStarted = false;
    private boolean bCancel = true;
    private CountDownLatch countDownLatch = null;

    private long timeCostAll = 0;
    private long  timeCostCurrent = 0;
    private int  readSuccessTimes = 0;
    private int  readFailTimes = 0;
    private long maxTimeCost = 0;

    private static final int VID = 1024;    //IDR VID
    private static final int PID = 50010;     //IDR PID

    private ZKUSBManagerListener zkusbManagerListener = new ZKUSBManagerListener() {
        @Override
        public void onCheckPermission(int result) {
            openDevice();
        }

        @Override
        public void onUSBArrived(UsbDevice device) {
            setResult("发现阅读器接入");
        }

        @Override
        public void onUSBRemoved(UsbDevice device) {
            setResult("阅读器USB被拔出");
        }
    };


    private void initUI()
    {
        checkRepeat = (CheckBox)findViewById(R.id.checkRepeat);
        imageView = (ImageView)findViewById(R.id.imageView);
        textSuccessCount = (TextView)findViewById(R.id.textSuccessCount);
        textFailCount = (TextView)findViewById(R.id.textFailCount);
        textTimeCost = (TextView)findViewById(R.id.textTimeCost);
        textResult = (TextView)findViewById(R.id.textResult);
        checkReadFp = (CheckBox)findViewById(R.id.checkReadFp);
        textMaxTimeCost = (TextView)findViewById(R.id.textMaxTimeCost);
    }

    private void startIDCardReader() {
        if (null != idCardReader)
        {
            IDCardReaderFactory.destroy(idCardReader);
            idCardReader = null;
        }
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
        idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.USB, idrparams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);
        initUI();
        zkusbManager = new ZKUSBManager(getApplicationContext(), zkusbManagerListener);
        zkusbManager.registerUSBPermissionReceiver();
    }

    private boolean enumSensor()
    {
        UsbManager usbManager = (UsbManager)this.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            int device_vid = device.getVendorId();
            int device_pid = device.getProductId();
            if (device_vid == VID && device_pid == PID)
            {
                return true;
            }
        }
        return false;
    }

    private void tryGetUSBPermission() {
        zkusbManager.initUSBPermission(VID, PID);
    }

    private void afterGetUsbPermission()
    {
        openDevice();
    }

    private void openDevice()
    {
        startIDCardReader();
        try {
            idCardReader.open(0);
            countDownLatch = new CountDownLatch(1);
            new Thread(new Runnable() {
                public void run() {
                    bCancel = false;
                    while (!bCancel) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateStatus();

                        boolean ret = false;
                        final long nTickstart = System.currentTimeMillis();
                        try {
                            idCardReader.findCard(0);
                            idCardReader.selectCard(0);
                        }catch (IDCardReaderException e)
                        {
                            if (!bRepeatMode)
                            {
                                continue;
                            }
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int cardType = 0;
                        try {
                            if (bReadFp)
                            {
                                cardType = idCardReader.readCardEx(0, 1);
                            }
                            else {
                                cardType = idCardReader.readCardEx(0, 0);
                            }
                        }
                        catch (IDCardReaderException e)
                        {
                            setResult("读卡失败，错误信息：" + e.getMessage());
                            readFailTimes++;
                            updateStatus();
                            continue;
                        }

                        if (cardType == IDCardType.TYPE_CARD_SFZ || cardType == IDCardType.TYPE_CARD_PRP ||
                                cardType == IDCardType.TYPE_CARD_GAT || cardType == IDCardType.TYPE_CARD_PRP2)
                        {
                            readSuccessTimes++;
                            timeCostCurrent = System.currentTimeMillis()-nTickstart;
                            timeCostAll += timeCostCurrent;
                            if (timeCostCurrent > maxTimeCost)
                            {
                                maxTimeCost = timeCostCurrent;
                            }
                            final long nTickCommuUsed = (System.currentTimeMillis()-nTickstart);
                            if (cardType == IDCardType.TYPE_CARD_SFZ || cardType == IDCardType.TYPE_CARD_GAT)
                            {
                                IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                                final String name = idCardInfo.getName();
                                final String sex = idCardInfo.getSex();
                                final String nation = idCardInfo.getNation();
                                final String born = idCardInfo.getBirth();
                                final String licid = idCardInfo.getId();
                                final String depart = idCardInfo.getDepart();
                                final String expireDate = idCardInfo.getValidityTime();
                                final String addr = idCardInfo.getAddress();
                                final String passNo = idCardInfo.getPassNum();
                                final int visaTimes = idCardInfo.getVisaTimes();
                                Bitmap bmpPhoto = null;
                                if (idCardInfo.getPhotolength() > 0) {
                                    byte[] buf = new byte[WLTService.imgLength];
                                    if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                                        bmpPhoto = IDPhotoHelper.Bgr2Bitmap(buf);
                                    }
                                }
                                final int final_cardType = cardType;
                                final Bitmap final_bmpPhoto = bmpPhoto;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        imageView.setImageBitmap(final_bmpPhoto);
                                        String result = "";
                                        if (final_cardType == IDCardType.TYPE_CARD_SFZ)
                                        {
                                            result += "读取居民身份证成功！";
                                            result += "姓名：" + name;
                                            result += ",性别：" + sex;
                                            result += ",民族：" + nation;
                                            result += ",出生日期：" + born;
                                            result += ",地址：" + addr;
                                            result += ",身份号码：" + licid;
                                            result += "，签发机关：" + depart;
                                            result += "，有效期：" + expireDate;
                                            setResult(result);
                                        }
                                        else
                                        {
                                            result += "读取港澳台居住证成功！";
                                            result += "姓名：" + name;
                                            result += ",性别：" + sex;
                                            result += ",出生日期：" + born;
                                            result += ",身份号码：" + licid;
                                            result += "，签发机关：" + depart;
                                            result += "，有效期：" + expireDate;
                                            result += "，签发次数：" + visaTimes;
                                            result += "，通行证号码：" + passNo;
                                            setResult(result);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                IDPRPCardInfo idprpCardInfo = idCardReader.getLastPRPIDCardInfo();
                                final String cnName = idprpCardInfo.getCnName();
                                final String enName = idprpCardInfo.getEnName();
                                final String sex = idprpCardInfo.getSex();
                                final String country = idprpCardInfo.getCountry() + "/" + idprpCardInfo.getCountryCode();//国家/国家地区代码
                                final String born = idprpCardInfo.getBirth();
                                final String licid = idprpCardInfo.getId();
                                final String expireDate = idprpCardInfo.getValidityTime();
                                final String relatecode = idprpCardInfo.getRelateCode();
                                final String oldLicId = idprpCardInfo.getOldId();
                                final int    visaTimes = idprpCardInfo.getVisaTimes();

                                Bitmap bmpPhoto = null;
                                if (idprpCardInfo.getPhotolength() > 0) {
                                    byte[] buf = new byte[WLTService.imgLength];
                                    if (1 == WLTService.wlt2Bmp(idprpCardInfo.getPhoto(), buf)) {
                                        bmpPhoto = IDPhotoHelper.Bgr2Bitmap(buf);
                                    }
                                }
                                final int final_cardType = cardType;
                                final Bitmap final_bmpPhoto = bmpPhoto;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        imageView.setImageBitmap(final_bmpPhoto);
                                        String result = "";
                                        if (final_cardType == IDCardType.TYPE_CARD_PRP)
                                        {
                                            result += "读取外国人永久居留身份证(2017)成功！";
                                            result += "中文姓名：" + cnName;
                                            result += "英文姓名：" + enName;
                                            result += ",性别：" + sex;
                                            result += ",国家：" + country;
                                            result += ",出生日期：" + born;
                                            result += ",身份号码：" + licid;
                                            result += "，有效期：" + expireDate;
                                            setResult(result);
                                        }
                                        else
                                        {
                                            result += "读取外国人永久居留身份证(2023)成功！";
                                            result += "中文姓名：" + cnName;
                                            result += "英文姓名：" + enName;
                                            result += ",性别：" + sex;
                                            result += ",国家：" + country;
                                            result +=",出生日期：" + born;
                                            result += ",身份号码：" + licid;
                                            result += "，有效期：" + expireDate;
                                            result += "，换证次数：" + visaTimes;
                                            if (!relatecode.isEmpty())
                                            {
                                                result += "，既往身份号码关联项：" + relatecode;
                                                result += "，既往身份号码：" + oldLicId;
                                            }
                                            setResult(result);
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            readFailTimes++;
                        }
                    }
                    countDownLatch.countDown();
                }
            }).start();
            bStarted = true;
            setResult("打开设备成功，SAMID:" + idCardReader.getSAMID(0));
        } catch (IDCardReaderException e) {
            e.printStackTrace();
            setResult("打开设备失败");
        }
    }

    public void onBnStart(View view)
    {
        bRepeatMode = false;
        bReadFp = false;
        readFailTimes = 0;
        readSuccessTimes = 0;
        timeCostAll = 0;
        timeCostCurrent = 0;
        maxTimeCost = 0;

        if (!enumSensor())
        {
            setResult("找不到设备");
            return;
        }


        if (checkRepeat.isChecked())
        {
            bRepeatMode = true;
        }

        if (checkReadFp.isChecked())
        {
            bReadFp = true;
        }
        tryGetUSBPermission();
    }

    private void closeDevice()
    {
        if (bStarted)
        {
            bCancel = true;
            if (null != countDownLatch)
            {
                try {
                    countDownLatch.await(2*1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch = null;
            }
            try {
                idCardReader.close(0);
            } catch (IDCardReaderException e) {
                e.printStackTrace();
            }
            bStarted = false;
        }
    }

    public void onBnStop(View view)
    {

        closeDevice();
        setResult("设备断开连接");
    }

    private void setResult(String strText)
    {
        final String flStrText = strText;
        runOnUiThread(new Runnable() {
            public void run() {
                textResult.setText(flStrText);
            }
        });
    }

    private void updateStatus()
    {
        runOnUiThread(new Runnable() {
            public void run() {
                textSuccessCount.setText(String.valueOf(readSuccessTimes));
                textFailCount.setText(String.valueOf(readFailTimes));
                if (readSuccessTimes > 0) {
                    textTimeCost.setText("本次读卡:" + timeCostCurrent + "ms, 平均耗时:" +
                            timeCostAll / readSuccessTimes + "ms");
                    textMaxTimeCost.setText(maxTimeCost + "ms");
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        closeDevice();
        zkusbManager.unRegisterUSBPermissionReceiver();
    }
}
