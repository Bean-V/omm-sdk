package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.io.File;
import java.io.IOException;

public class USBBroadCastReceiver extends BroadcastReceiver {

    private UsbListener usbListener;

    public void setUsbListener(UsbListener usbListener) {

        this.usbListener = usbListener;
    }

    public static final String ACTION_USB_PERMISSION ="com.yzkj.switching.USB_PERMISSION";
    public USBBroadCastReceiver(UsbListener usbListener) {

        this.usbListener = usbListener;
}

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

         if (action ==null)
             return;

            switch (action) {

            case ACTION_USB_PERMISSION:

//用户授权广播

                 synchronized (this) {
                     {
                         LogHelper.getInstance().d("ACTION_USB_PERMISSION");
                         File f1 = new File("/storage/F47E-6905" + "/" + "1234333");
                         Boolean res = f1.mkdir();


                         LogHelper.getInstance().d("U盘路径----"+ res +"####" + f1.exists() + "****" + f1.getName());


                         File f2= new File("/storage/F47E-6905" + "/" + "1234333.txt");

                         LogHelper.getInstance().d("U盘路径----"+ f2.getPath() +"####" + f2.exists() + "****" + f2.getName());
                     }

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {//允许权限申请



                    LogHelper.getInstance().d("ACTION_USB_PERMISSION");
                    File f1 = new File("/storage/F47E-6905" + "/" + "1234333");
                    Boolean res = f1.mkdir();


                    LogHelper.getInstance().d("U盘路径----"+ res +"####" + f1.exists() + "****" + f1.getName());


                    File f2= new File("/storage/F47E-6905" + "/" + "1234333.txt");

                    LogHelper.getInstance().d("U盘路径----"+ f2.getPath() +"####" + f2.exists() + "****" + f2.getName());
                        if (usbListener !=null) {

                            try {
                                usbListener.getReadUsbPermission(usbDevice);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                            }else {

                            if (usbListener !=null) {

                                    usbListener.failedReadUsb(usbDevice);

                            }

                }

            }

                break;

                case UsbManager.ACTION_USB_DEVICE_ATTACHED:

//USB设备插入广播

                if (usbListener !=null) {

                    usbListener.insertUsb(usbDevice);

                }

                break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:

//USB设备拔出广播

                        if (usbListener !=null) {

                    usbListener.removeUsb(usbDevice);

                         }

                break;

         }

    }



    public interface UsbListener {

//USB 插入

    void insertUsb(UsbDevice device_add);

   //USB 移除

       void removeUsb(UsbDevice device_remove);

//获取读取USB权限

        void getReadUsbPermission(UsbDevice usbDevice) throws IOException;

 //读取USB信息失败

    void failedReadUsb(UsbDevice usbDevice);

    }

}
