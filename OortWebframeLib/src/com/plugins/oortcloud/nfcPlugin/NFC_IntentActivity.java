package com.plugins.oortcloud.nfcPlugin;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oortcloud.basemodule.utils.NetUtil;
import com.oortcloud.basemodule.utils.ToastUtil;
import com.senter.readcard.openapi.CardSDK;

import org.apache.cordova.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class NFC_IntentActivity extends Activity {

    public static final int REQUEST_CODE = 0x0aab;

    private TextView nameTextView;

    private TextView tv_enname;
    private TextView Labelehtnic;
    private TextView Lableaddress;

    private TextView sexTextView;
    private TextView folkTextView;
    private TextView birthTextView;
    private TextView addrTextView;
    private TextView codeTextView;
    private TextView policyTextView;
    private TextView validDateTextView;
    private ImageView photoView;

    private TextView tv_numberofissue;
    private TextView tv_passnumber;

    private TextView tv_info;

    private CardSDK cardSDK;

    private NfcAdapter nfcAdapter;

    //开始截至时间
    private long startTime;
    private long endTime;

    //计数
    private int successCount;
    private int failedCount;

    private boolean isReading;

    private JSONObject jsonObjectResult ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_intent);
//        getActionBar().setTitle("NFC Intent方式");
        initViews();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            ToastUtil.showToast(this, "该设备不支持NFC通信");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            ToastUtil.showToast(this, "NFC未开启");
            finish();
            return;
        }

        if (!CardSDK.isSupportNFC()) {
            ToastUtil.showToast(this, "不支持NFC方式读卡");
            finish();
            return;
        }

        cardSDK = CardSDK.getInstance(CardSDK.Mode.NFC);
        //每种不同的通信方式，需要单独设置
        cardSDK.setContext(this);
//        cardSDK.setServerAddress("20.137.128.18");
//        cardSDK.setServerPort(Integer.parseInt("10002"));
        cardSDK.setServerAddress("20.135.1.249");
        cardSDK.setServerPort(Integer.parseInt("10002"));
    }

    private void initViews() {
        nameTextView = (TextView) findViewById(R.id.tv_name);
        sexTextView = (TextView) findViewById(R.id.tv_sex);
        folkTextView = (TextView) findViewById(R.id.tv_ehtnic);
        birthTextView = (TextView) findViewById(R.id.tv_birthday);
        addrTextView = (TextView) findViewById(R.id.tv_address);
        codeTextView = (TextView) findViewById(R.id.tv_number);
        policyTextView = (TextView) findViewById(R.id.tv_signed);
        validDateTextView = (TextView) findViewById(R.id.tv_validate);

        tv_enname = (TextView) findViewById(R.id.tv_enname);
        Labelehtnic = (TextView) findViewById(R.id.Labelehtnic);
        Lableaddress = (TextView) findViewById(R.id.Lableaddress);

        tv_numberofissue = (TextView) findViewById(R.id.tv_numberofissue);
        tv_passnumber = (TextView) findViewById(R.id.tv_passnumber);

        photoView = (ImageView) findViewById(R.id.iv_photo);
        tv_info = (TextView) findViewById(R.id.tv_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null && adapter.isEnabled()) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cardSDK.registerNFCCard(intent)) {
                    isReading = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();
                            resetUI();
                        }
                    });
                    startTime = System.currentTimeMillis();
                    Log.e("NFC_IntentActivity", "本地Ip->" + NetUtil.getLocalIpV4Address(NFC_IntentActivity.this));
                    final CardSDK.IDCardResult result = cardSDK.readCard_Sync();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            parseIdCardData(result);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(NFC_IntentActivity.this, "注册失败");
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isReading) {
            ToastUtil.showToast(this, "有任务正在处理，停止后再退出");
            return;
        }
        super.onBackPressed();

    }

    /**
     * 转化身份证数据
     *
     * @param result
     */
    private void parseIdCardData(CardSDK.IDCardResult result) {
        try {
            if (result == null) {
                ToastUtil.showToast(this, "读卡失败");
                return;
            }

            if (result.getError() != CardSDK.Error.success) {
                ToastUtil.showToast(this, result.getError().getMsg());
                return;
            }

            JSONObject JsonObj;
            InputStream input = null;
            byte[] avatar = new byte[20 * 1024];

            try {
                JsonObj = new JSONObject(result.getResult());
                jsonObjectResult = JsonObj;
                String type = JsonObj.getString("type");

                String SignStr = JsonObj.getString("SignStr");

                if (type.equals("I")) {
                    nameTextView.setText(JsonObj.getString("name"));
                    String enname = JsonObj.getString("enname");
                    if (enname.isEmpty() || enname.equals("")){
                        tv_enname.setVisibility(View.GONE);
                    }else {
                        tv_enname.setText(enname);
                    }
                    Labelehtnic.setText("");
                    Lableaddress.setText("国籍");
                    folkTextView.setText("");
                    addrTextView.setText(JsonObj.getString("ethnicity"));
                    tv_numberofissue.setText("");
                    tv_passnumber.setText("");
                } else if (type.equals("")) {
                    Labelehtnic.setText("民族");
                    Lableaddress.setText("住址");
                    nameTextView.setText(JsonObj.getString("name"));
                    folkTextView.setText(JsonObj.getString("ethnicity"));
                    addrTextView.setText(JsonObj.getString("address"));
                    tv_numberofissue.setText("");
                    tv_passnumber.setText("");
                } else if (type.equals("J")) {
                    Labelehtnic.setText("民族");
                    Lableaddress.setText("住址");
                    nameTextView.setText(JsonObj.getString("name"));
                    folkTextView.setText(JsonObj.getString("ethnicity"));
                    addrTextView.setText(JsonObj.getString("address"));

                    String num = JsonObj.getString("numberofissue");
                    if (num.isEmpty() || num.equals("")){
                        tv_numberofissue.setVisibility(View.GONE);
                    }else {
                        tv_numberofissue.setText(num);
                    }
                    String passnumber = JsonObj.getString("passnumber");
                    if (num.isEmpty() || num.equals("")){
                        tv_passnumber.setVisibility(View.GONE);
                    }else {
                        tv_passnumber.setText(passnumber);
                    }
                }

                sexTextView.setText(JsonObj.getString("gender"));
                birthTextView.setText(JsonObj.getString("birth"));
                codeTextView.setText(JsonObj.getString("cardNo"));
                policyTextView.setText(JsonObj.getString("authority"));
                validDateTextView.setText(JsonObj.getString("period"));

                JSONArray javatar = JsonObj.getJSONArray("avatar");
                int len = javatar.length();

                for (int j = 0; j < len; j++) {
                    avatar[j] = (byte) javatar.getInt(j);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            try {
                Bitmap photobm = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                input = new ByteArrayInputStream(avatar);

                SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
                photobm = (Bitmap) softRef.get();
                photoView.setImageBitmap(photobm);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            isReading = false;
            endTime = System.currentTimeMillis();
            if (result != null && result.getError() == CardSDK.Error.success) {
                //成功
                successCount++;
                tv_info.setText("读取成功。耗时:" + (endTime - startTime) + " 成功:" + successCount + " 失败:" + failedCount);
//                LogSaveFile.saveLog("读取成功。耗时:" + (endTime - startTime) + "\n", "idcard_BT.txt");
//                Log.e("nfc",result.getResult());
                Intent intent = new Intent();
                // 获取用户计算后的结果
                try {
                    JSONObject jsonObject = new JSONObject(result.getResult());
                    intent.putExtra("Result", String.valueOf(jsonObject)); //将计算的值回传回去
                    // 通过intent对象返回结果，必须要调用一个setResult方法，
                    // setResult(888, data);第一个参数表示结果返回码，一般只要大于1就可以
                    setResult(REQUEST_CODE, intent);
                    finish(); //结束当前的activity的生命周期
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                failedCount++;
                tv_info.setText("读取失败。耗时:" + (endTime - startTime) + " 成功:" + successCount + " 失败:" + failedCount + " 原因: " + result.getError().getMsg() + " code: " + result
                        .getError().getCode());
//                LogSaveFile.saveLog("读取失败。耗时:" + (endTime - startTime) + " 原因: " + result.getError().getMsg() + " code: " + result
//                        .getError().getCode() + "\n", "idcard_BT.txt");

            }
        }
    }

    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在读卡中...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public void resetUI() {
        this.nameTextView.setText("");
        this.sexTextView.setText("");
        this.folkTextView.setText("");
        this.birthTextView.setText("");
        this.codeTextView.setText("");
        this.policyTextView.setText("");
        this.addrTextView.setText("");
        this.validDateTextView.setText("");
//        this.tv_info.setText("");
        this.tv_enname.setText("");
        tv_numberofissue.setText("");
        tv_passnumber.setText("");
        this.photoView.setImageResource(android.R.color.transparent);
    }

}
