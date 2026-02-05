package kl.cds.tools.aidldemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * GSM：全球移动通讯系统（Global System for Mobile Communications），第二代（2G）移动电话系统
 * [IMSI]
 * <p>
 * CDMA：码分多址（Code Division Multiple Access），一种多址接入无线通信技术
 * [MEID]
 */
public class LittleToolsActivity extends AppCompatActivity implements View.OnClickListener {

    // 视图组件
    private TextView tvInfo;
    private Button btnImei;
    private Button btnMeid;
    private Button btnDeviceid;
    private Button btnSubscriberId;
    private Button btnAndroidId;
    private Button btnClearLog;

    // 系统服务
    private TelephonyManager mTelManager;

    // 权限请求码
    private static final int PERMISSION_REQUEST_IMEI = 1000;
    private static final int PERMISSION_REQUEST_MEID = 1001;
    private static final int PERMISSION_REQUEST_DEVICEID = 1002;
    private static final int PERMISSION_REQUEST_SUBSCRIBERID = 1003;

    // 运营商代码列表
    private static final List<String> MOBILE_OPERATORS = Arrays.asList("46000", "46002", "46004", "46007", "46008", "46020", "46099");
    private static final List<String> UNICOM_OPERATORS = Arrays.asList("46001", "46006", "46009");
    private static final List<String> TELECOM_OPERATORS = Arrays.asList("46003", "46005", "46011", "46012", "46013");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_little_tools);
        initViews();       // 初始化视图
        initServices();    // 初始化系统服务
        initDeviceInfo();  // 初始化设备信息展示
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        tvInfo = findViewById(R.id.tv_info);
        btnImei = findViewById(R.id.btn_imei);
        btnMeid = findViewById(R.id.btn_meid);
        btnDeviceid = findViewById(R.id.btn_deviceid);
        btnSubscriberId = findViewById(R.id.btn_SubscriberId);
        btnAndroidId = findViewById(R.id.btn_androidid);
        btnClearLog = findViewById(R.id.btnClearLog);

        // 设置点击监听
        btnImei.setOnClickListener(this);
        btnMeid.setOnClickListener(this);
        btnDeviceid.setOnClickListener(this);
        btnSubscriberId.setOnClickListener(this);
        btnAndroidId.setOnClickListener(this);
        btnClearLog.setOnClickListener(this);
    }

    /**
     * 初始化系统服务
     */
    private void initServices() {
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 初始化设备基础信息并展示
     */
    private void initDeviceInfo() {
        appendInfo("【手机厂商】：" + getDeviceBrand());
        appendInfo("【手机型号】：" + getSystemModel());
        appendInfo("【系统版本】：" + getSystemVersion());

        // 检查SIM卡状态并展示类型
        if (hasSimCard()) {
            appendInfo("【SIM卡类型】：" + getSIMCardType());
        } else {
            appendInfo("【SIM卡状态】：未插入SIM卡");
        }
    }

    /**
     * 点击事件统一处理
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_imei) {
            requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_IMEI);
        } else if (id == R.id.btn_meid) {
            requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_MEID);
        } else if (id == R.id.btn_deviceid) {
            requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_DEVICEID);
        } else if (id == R.id.btn_SubscriberId) {
            requestPermission(Manifest.permission.READ_PHONE_STATE, PERMISSION_REQUEST_SUBSCRIBERID);
        } else if (id == R.id.btn_androidid) {
            showAndroidId();
        } else if (id == R.id.btnClearLog) {
            tvInfo.setText("");
        }
    }

    /**
     * 权限请求封装
     */
    private void requestPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            // 已授权，直接处理对应逻辑
            handlePermissionGranted(requestCode);
        }
    }

    /**
     * 权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限授予，处理对应逻辑
            handlePermissionGranted(requestCode);
        } else {
            appendInfo("【权限提示】：需要授予电话权限才能获取相关信息");
        }
    }

    /**
     * 权限授予后处理对应逻辑
     */
    private void handlePermissionGranted(int requestCode) {
        switch (requestCode) {
            case PERMISSION_REQUEST_IMEI:
                showImeiInfo();
                break;
            case PERMISSION_REQUEST_MEID:
                showMeidInfo();
                break;
            case PERMISSION_REQUEST_DEVICEID:
                showDeviceIdInfo();
                break;
            case PERMISSION_REQUEST_SUBSCRIBERID:
                showSubscriberIdInfo();
                break;
        }
    }

    /**
     * 展示IMEI信息
     */
    private void showImeiInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String imei;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            imei = mTelManager.getImei();
        } else {
            imei = mTelManager.getDeviceId(); // 兼容低版本
        }

        if (TextUtils.isEmpty(imei)) {
            appendInfo("【IMEI】：获取失败（可能为空或不支持）");
            return;
        }
        appendInfo("【IMEI】：" + imei);
        appendInfo("【IMEI-SM3】：" + base64_SM3(imei.getBytes()).toUpperCase());
    }

    /**
     * 展示MEID信息
     */
    private void showMeidInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String meid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            meid = mTelManager.getMeid();
        } else {
            meid = mTelManager.getDeviceId(); // 兼容低版本
        }

        if (TextUtils.isEmpty(meid)) {
            appendInfo("【MEID】：获取失败（可能为空或不支持）");
            return;
        }
        appendInfo("【MEID】：" + meid);
        appendInfo("【MEID-SM3】：" + base64_SM3(meid.getBytes()).toUpperCase());
    }

    /**
     * 展示DeviceId信息
     */
    private void showDeviceIdInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String deviceId = mTelManager.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            appendInfo("【DeviceId】：获取失败（可能为空）");
            return;
        }
        appendInfo("【DeviceId】：" + deviceId);
        appendInfo("【DeviceId-SM3】：" + base64_SM3(deviceId.getBytes()).toUpperCase());
    }

    /**
     * 展示SubscriberId（IMSI）信息
     */
    private void showSubscriberIdInfo() {
        if (!hasSimCard()) {
            appendInfo("【SubscriberId】：未插入SIM卡，无法获取");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String subscriberId = mTelManager.getSubscriberId();
        if (TextUtils.isEmpty(subscriberId)) {
            appendInfo("【SubscriberId(IMSI)】：获取失败（可能为空）");
            return;
        }
        appendInfo("【SubscriberId(IMSI)】：" + subscriberId);
        appendInfo("【SubscriberId-SM3】：" + base64_SM3(subscriberId.getBytes()).toUpperCase());
    }

    /**
     * 展示AndroidId信息
     */
    private void showAndroidId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId)) {
            appendInfo("【AndroidId】：获取失败（可能为空）");
            return;
        }
        appendInfo("【AndroidId】：" + androidId);
        appendInfo("【AndroidId-SM3】：" + base64_SM3(androidId.getBytes()).toUpperCase());
    }

    // ------------------------------ 工具方法 ------------------------------

    /**
     * 检查SIM卡是否存在
     */
    private boolean hasSimCard() {
        int simState = mTelManager.getSimState();
        return simState != TelephonyManager.SIM_STATE_ABSENT && simState != TelephonyManager.SIM_STATE_UNKNOWN;
    }

    /**
     * 获取SIM卡运营商类型
     */
    private String getSIMCardType() {
        String simOperator = mTelManager.getSimOperator();
        if (TextUtils.isEmpty(simOperator)) {
            return "未知运营商（SIM卡信息异常）";
        }
        if (MOBILE_OPERATORS.contains(simOperator)) {
            return "中国移动";
        } else if (UNICOM_OPERATORS.contains(simOperator)) {
            return "中国联通";
        } else if (TELECOM_OPERATORS.contains(simOperator)) {
            return "中国电信";
        } else {
            return "其他运营商（代码：" + simOperator + "）";
        }
    }

    /**
     * 获取手机系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * SM3加密并Base64编码
     */
    public static String base64_SM3(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SM3", new BouncyCastleProvider());
            byte[] result = digest.digest(input);
            return new String(Base64.encode(result));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "SM3加密失败：" + e.getMessage();
        }
    }

    /**
     * 在UI线程追加信息
     */
    private void appendInfo(final String info) {
        runOnUiThread(() -> tvInfo.append(info + "\n"));
    }

    // ------------------------------ 权限检查封装 ------------------------------
    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }
}