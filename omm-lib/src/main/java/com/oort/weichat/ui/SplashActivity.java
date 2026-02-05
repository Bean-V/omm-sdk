package com.oort.weichat.ui;

import static com.oort.weichat.MyApplication.cordovaView;
import static com.oort.weichat.MyApplication.getContext;
import static java.lang.Thread.sleep;
import static kl.cds.utils.Settings.PIN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.citc.simkeylibrary.LogHandler;
//import com.citc.simkeylibrary.SimkeyListener;
//import com.citc.simkeylibrary.SimkeyManager;
//import com.citc.simkeylibrary.SimkeyResponse;
import com.google.gson.Gson;
import com.oort.weichat.AppConfig;
import com.oort.weichat.AppConstant;
import com.oort.weichat.BuildConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.ConfigBean;
import com.oort.weichat.bean.LoginRegisterResult;
import com.oort.weichat.bean.event.MessageLogin;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.helper.LoginSecureHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.helper.YeepayHelper;
import com.oort.weichat.ui.account.ChangePasswordActivity;
import com.oort.weichat.ui.account.LoginActivity;
import com.oort.weichat.ui.account.LoginHistoryActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.dialog.DialogRequestPermission;
import com.oort.weichat.ui.notification.NotificationProxyActivity;
import com.oort.weichat.ui.other.PrivacyAgreeActivity;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.EventBusHelper;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.VersionUtil;
import com.oort.weichat.util.log.LogUtils;
import com.oort.weichat.view.PermissionExplainDialog;
import com.oort.weichat.view.TipDialog;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.constant.ConstantKey;
import com.oortcloud.basemodule.dialog.PopupDialog;
import com.oortcloud.basemodule.im.HeadInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.DeviceUtil;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.loadingdialog.view.LoadingDialog;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.custom.CordovaView;
import com.oortcloud.privacyview.AppUtil;
import com.oortcloud.privacyview.PrivacyDialog;
import com.oortcloud.privacyview.PrivacyPolicyActivity;
import com.oortcloud.privacyview.SPUtil;
import com.oortcloud.privacyview.TermsActivity;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import cn.com.jit.provider.lib.JbProviderClient;
import cn.com.jit.provider.lib.Response;
import cn.com.jit.provider.lib.net.AppCredential;
import cn.com.jit.provider.lib.net.UserCredential;
import kl.cds.tools.aidldemo.CertManagerSdk;
import okhttp3.Call;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {
    //隐私政策
    private String SP_PRIVACY = "sp_privacy";
    private String SP_VERSION_CODE = "sp_version_code";
    private boolean isCheckPrivacy = false;
    private long versionCode;
    private long currentVersionCode;
    private int mobilePrefix = 86;
    // 配置是否成功
    private boolean mConfigReady = false;
    // 复用请求权限的说明对话框，
    private PermissionExplainDialog permissionExplainDialog;
    private boolean canJump;

    public SplashActivity() {
        // 这个页面不需要已经获取config, 也不需要已经登录，
        noConfigRequired();
        noLoginRequired();
    }

    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(option);
//            window.setStatusBarColor(Color.TRANSPARENT);//| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusbar_bg));
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ReactNativeSSLHelper.initReactNativeUnsafeSSL(this);
//        ReactWebSocketSSLHelper.initInsecureSSL();
//
//        Jitsi_connecting_second.start(this, "1", "1", CallConstants.Video);
//
//        if(true){
//            return;
//        }


        super.onCreate(savedInstanceState);
        LogUtils.e("msg", "configApi--->日志--------------------------");
        Intent intent = getIntent();



        setContentView(R.layout.activity_splash);

//        intent.setClass(this, TestJsitisActivity.class);
//        startActivity(intent);
//
//        if(true){
//            return;
//        }
//        LogHandler.setContext(this);
        if (NotificationProxyActivity.processIntent(intent)) {
            // 如果是通知点击进来的，带上参数转发给NotificationProxyActivity处理，
            intent.setClass(this, NotificationProxyActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        // 如果不是任务栈第一个页面，就直接结束，显示上一个页面，
        // 主要是部分设备上Jitsi_pre页面退后台再回来会打开这个启动页flg=0x10200000，此时应该结束启动页，回到Jitsi_pre,
        if (!isTaskRoot()) {
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //检查隐私政策
        check();
        //初始化webview
//        initWebview();



        setConfig(null);



//        int flag = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (flag != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        } else {
//            initData();
//            int a = 9980;
//            int b = a - 1;
//        }

        initData();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // 检查权限请求的结果
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
                // 用户授予了权限
               // Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // 用户拒绝了权限
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void initData(){
        sharedConfig();

        // 初始化配置
        initConfig();
        EventBusHelper.register(this);
        String token=  FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        canJump = true;
        if(token != null && uuid != null){
            AppStoreInit.store_token = "";
            canJump = false;
            DataInit.setCallBack(new DataInit.CallBack() {
                @Override
                public void requestAppsSuc() {
                    canJump = true;
                    jump();
                }

                @Override
                public void requestAppsFail(String msg) {

                    canJump = true;
                    ToastUtil.showToast(SplashActivity.this,msg);
                    jump();
                }
            });
            AppStoreInit.initData(token,uuid);

        }
    }

    private void initWebview() {
        cordovaView = new CordovaView(getApplicationContext());
        cordovaView.initCordova(this);
        cordovaView.loadUrl("file:///android_asset/home/index.html");
        WebSettings settings = cordovaView.getWebview().getSettings();
        // 1. 设置缓存路径
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath()+"cache/";
//        settings.setAppCachePath(cacheDirPath);
//        // 2. 设置缓存大小
//        settings.setAppCacheMaxSize(20*1024*1024);
//        // 3. 开启Application Cache存储机制
//        settings.setAppCacheEnabled(true);
        //4.开启DOM storage
        settings.setDomStorageEnabled(true);
        //5.只需设置支持JS就自动打开IndexedDB存储机制
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 请求权限过程中离开了回来就再请求吧，
        ready();
    }

    private boolean requestPermissions() {
        if (!TextUtils.isEmpty(coreManager.getConfig().privacyPolicyPrefix) &&
                !PreferenceUtils.getBoolean(this, Constants.PRIVACY_AGREE_STATUS, false)) {
            // 先同意隐私政策，
            PrivacyAgreeActivity.start(this);
            return false;
        } else {
            // 请求定位以外的权限，

            DialogRequestPermission per = new DialogRequestPermission(this,DialogRequestPermission.per_type_all);
           //per.show();

            return true;
            //return requestPermissions(permissionsMap.keySet().toArray(new String[]{}));
        }
    }

    public void sharedConfig() {
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);

        //Constant.IF_CONFIG_APP_SERVICE = sharedPreferences.getBoolean(ConstantKey.IF_CONFIG_APP_SERVICE, false);
        if (Constant.IF_CONFIG_APP_SERVICE) {

            String addressIP = sharedPreferences.getString(ConstantKey.ADDRESS_IP_KEY, "");
            if (!TextUtils.isEmpty(addressIP)) {
                Constant.BASE_IP = addressIP;
//             Log.e("msg" ,"BASE_IP--->"+Constant.BASE_IP);
            }
            String ipPort = sharedPreferences.getString(ConstantKey.IP_PORT_KEY, "");

            if (!TextUtils.isEmpty(ipPort)) {
                Constant.BASE_URL = addressIP + ":" + ipPort + "/";
//                 Log.e("msg" ,"BASE_URL--->"+Constant.BASE_URL);
            }
            String imPort = sharedPreferences.getString(ConstantKey.IM_PORT_KEY, "");
            if (!TextUtils.isEmpty(imPort)) {
                Constant.IM_API_BASE = addressIP + ":" + imPort + "/";
//                 Log.e("msg" ,"IM_API_BASE--->"+Constant.IM_API_BASE);
            }
            String upPort = sharedPreferences.getString(ConstantKey.UPLOAD_PORT_KEY, "");
            if (!TextUtils.isEmpty(upPort)) {
                Constant.IM_UPLOAD_URL = addressIP + ":" + upPort + "/";
//                 Log.e("msg" ,"IM_UPLOAD_URL--->"+Constant.IM_UPLOAD_URL);
            }
            String downPort = sharedPreferences.getString(ConstantKey.DOWN_PORT_KEY, "");
            if (!TextUtils.isEmpty(downPort)) {
                Constant.IM_DOWN_URL = addressIP + ":" + downPort + "/";
//                 Log.e("msg" ,"IM_DOWN_URL--->"+Constant.IM_DOWN_URL);
            }

            String livePort = sharedPreferences.getString(ConstantKey.LIVE_PORT_KEY, "");
            if (!TextUtils.isEmpty(livePort)) {
                Constant.IM_LIVE_URL = addressIP.replace("http", "rtmp") + ":" + livePort + "/live/";
//                 Log.e("msg" ,"IM_LIVE_URL--->"+Constant.IM_LIVE_URL);
            }
            String xmppPort = sharedPreferences.getString(ConstantKey.XM_PP_PORT_KEY, "");
            if (!TextUtils.isEmpty(xmppPort)) {
                Constant.IM_XM_PP_PORT = Integer.parseInt(xmppPort);
//                 Log.e("msg" ,"xmpp--->"+Constant.IM_XM_PP_PORT);
            }
            String xmppDomain = sharedPreferences.getString(ConstantKey.XM_PP_DO_MAIN_KEY, "");
            if (!TextUtils.isEmpty(xmppDomain)) {
                Constant.IM_XM_PP_DO_MAIN = xmppDomain;
//                 Log.e("msg" ,"xmpp--->"+Constant.IM_XM_PP_PORT);
            }

            String jitPort = sharedPreferences.getString(ConstantKey.JIT_SI_PORT_KEY, "");
            if (!TextUtils.isEmpty(downPort)) {
                Constant.IM_JIT_SI_URL =  jitPort + "/"; //https://im.oortcloud.com:8080/

//                 Log.e("msg" ,"IM_JIT_SI_URL--->"+Constant.IM_JIT_SI_URL);
            }
            String apiKey = sharedPreferences.getString(ConstantKey.API_KEY, "");
            if (!TextUtils.isEmpty(apiKey)) {
                Constant.IM_API_KEY = apiKey;
//                 Log.e("msg" ,"xmpp--->"+Constant.IM_XM_PP_PORT);
            }

        }
    }

    /**
     * 配置参数初始化
     */
    private void initConfig() {
        getConfig();
    }

    private void getConfig() {
        String mConfigApi = AppConfig.readConfigUrl(mContext);
        //String mConfigApi = "http://oort.oortcloudsmart.com:21300/config";
        OperLogUtil.e("zq", "configApi--->" + mConfigApi);
        Map<String, String> params = new HashMap<>();
        Reporter.putUserData("configUrl", mConfigApi);
        long requestTime = System.currentTimeMillis();

//        canJump = true;
//        ConfigBean configBean = coreManager.readConfigBean();
//        setConfig(configBean);

//        if(true){
//
//            return;
//        }
        HttpUtils.get().url(mConfigApi)
                .params(params)
                .build(true, true)
                .execute(new BaseCallback<ConfigBean>(ConfigBean.class) {
                    @Override
                    public void onResponse(ObjectResult<ConfigBean> result) {
                        if (result != null) {
                            long responseTime = System.currentTimeMillis();
                            TimeUtils.responseTime(requestTime, result.getCurrentTime(), result.getCurrentTime(), responseTime);
                        }
                        ConfigBean configBean;
                        if (result == null || result.getData() == null || result.getResultCode() != Result.CODE_SUCCESS) {
                            OperLogUtil.e("zq", "获取网络配置失败，使用已经保存了的配置");
                            if (BuildConfig.DEBUG) {
                                ToastUtil.showToast(SplashActivity.this, R.string.tip_get_config_failed);
                            }
                            // 获取网络配置失败，使用已经保存了的配置，
                            configBean = coreManager.readConfigBean();
                        } else {
                            OperLogUtil.e("zq", "获取网络配置成功，使用服务端返回的配置并更新本地配置");
                            configBean = result.getData();
                            if (!TextUtils.isEmpty(configBean.getAddress())) {
                                PreferenceUtils.putString(SplashActivity.this, AppConstant.EXTRA_CLUSTER_AREA, configBean.getAddress());
                            }
                            OperLogUtil.e(TAG, "获取网络配置------->" + result.toString());
                            ReportInfo.default_mm = configBean.getCopyright();
                            MyApplication.IS_OPEN_CLUSTER = configBean.getIsOpenCluster() == 1 ? true : false;
                        }
                        setConfig(configBean);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        OperLogUtil.e("zq", "获取网络配置失败，使用已经保存了的配置");
                        // ToastUtil.showToast(SplashActivity.this, R.string.tip_get_config_failed);
                        // 获取网络配置失败，使用已经保存了的配置，
                        ConfigBean configBean = coreManager.readConfigBean();
                        setConfig(configBean);
                    }
                });

    }

    private void setConfig(ConfigBean configBean) {
        if (configBean == null) {
            if (BuildConfig.DEBUG) {
//                ToastUtil.showToast(this, R.string.tip_get_config_failed);
            }

            // 如果没有保存配置，也就是第一次使用，就连不上服务器，使用默认配置
            configBean = CoreManager.getDefaultConfig(this);
            if (configBean == null) {
                // 不可到达，本地assets一定要提供默认config,
                DialogHelper.tip(this, getString(R.string.tip_get_config_failed));
                return;
            }
            //如果手动配置服务需要修改配置信息
            if (Constant.IF_CONFIG_APP_SERVICE) {
                String host = Constant.BASE_IP.replace("http://", "");
                configBean.setXMPPHost(host);
                configBean.setXMPPDomain(Constant.IM_XM_PP_DO_MAIN);
                configBean.setUploadUrl(Constant.IM_UPLOAD_URL);
                configBean.setDownloadUrl(Constant.IM_DOWN_URL);
                configBean.setDownloadAvatarUrl(Constant.IM_DOWN_URL);
                configBean.setApiUrl(Constant.IM_API_BASE);
                configBean.setJitsiServer(Constant.IM_JIT_SI_URL);
                configBean.setLiveUrl(Constant.IM_LIVE_URL);
                OperLogUtil.e(TAG, "修改网络配置------->" + JSON.toJSONString(configBean));
            }
            coreManager.saveConfigBean(configBean);
        }


//        if (!coreManager.getConfig().disableLocationServer) {// 定位
//            permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_location);
//            permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_location);
//        }
     // todo 定位权限放到应用内请求，class：LoginActivity、SelectAreaActivity、NearPersonActivity、MapPickerActivity

        // 配置完毕
        mConfigReady = true;
        MyApplication.IS_SUPPORT_SECURE_CHAT = configBean.getIsOpenSecureChat() == 1;
        // 如果没有androidDisable字段就不判断，
        // 当前版本没被禁用才继续打开，
        if (TextUtils.isEmpty(configBean.getAndroidDisable()) || !blockVersion(configBean.getAndroidDisable(), configBean.getAndroidAppUrl())) {
            // 进入主界面
            ready();
        }
    }

    /**
     * 如果当前版本被禁用，就自杀，
     *
     * @param disabledVersion 禁用该版本以下的版本，
     * @param appUrl          版本被禁用时打开的地址，
     * @return 返回是否被禁用，
     */
    private boolean blockVersion(String disabledVersion, String appUrl) {
//        String currentVersion = BuildConfig.VERSION_NAME;
//        if (VersionUtil.compare(currentVersion, disabledVersion) > 0) {
//            // 当前版本大于被禁用版本，
//            return false;
//        } else {
//            // 通知一下，
//            TipDialog tipDialog = new TipDialog(this);
//            tipDialog.setmConfirmOnClickListener(getString(R.string.tip_version_disabled), () -> {
//
//            });
//            tipDialog.setOnDismissListener(dialog -> {
//                try {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(appUrl));
//                    startActivity(i);
//                } catch (Exception e) {
//                    // 弹出浏览器失败的话无视，
//                    // 比如没有浏览器的情况，
//                    // 比如appUrl不合法的情况，
//                }
//                // 自杀，
//                finish();
//                MyApplication.getInstance().destory();
//            });
//            tipDialog.show();
            return true;
//        }
    }

    private void ready() {
        if (!mConfigReady) {// 配置失败
            return;
        }
        jump();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            jump();
        }
    }

//    SimkeyManager simkeyManager = SimkeyManager.getInstance();
    @SuppressLint("NewApi")
    private void jump() {



        if (isDestroyed() || !canJump) {
            return;
        }


//        LoginActivity.isVerify = true;
//        // 进行验证操作
//        ld = new LoadingDialog(SplashActivity.this);
//        ld.setLoadingText("认证中")
//                .setSuccessText("认证成功")
//                .setFailedText("认证失败")
//                .setInterceptBack(true)
//                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
//                .closeSuccessAnim()
//                .show();
//
//        mIntent = new Intent();
//        mIntent.setClass(mContext, LoginActivity.class);
//
//        //通过版本号区分新旧吉安宝
//        int versioncode = getVersionCode(this,"cn.com.jit.jianbao");
//        OperLogUtil.e(TAG, "versioncode:" + versioncode);
//        if(versioncode >= 2023011210){
//            //新版本
//            MyAsyncTask myAsyncTask = new MyAsyncTask();
//            myAsyncTask.execute("test");
//        }else{
//            //旧版本
//            initClient();
//            callClient();
//        }
//
//        if(true) {
//            return;
//        }




        Boolean isgeer = Constant.HAVA_VERIFY;//BuildConfig.DEBUG  ? false : Constant.HAVA_VERIFY;

        if(isgeer){

            if (isDestroyed()) {
                return;
            }


//            if(BuildConfig.DEBUG ){
//
//                login("413026199906103633",ReportInfo.default_mm);
//
//                return;
//            }

            // 初始化 SDK
            CertManagerSdk sdk = CertManagerSdk.getInstance(this);
            sdk.init(new CertManagerSdk.SdkListener() {
                @Override
                public void onServiceConnected() {
                    // 服务连接成功后，进行登录（假设证书索引为0）
                    sdk.login(0, PIN); // 证书PIN码
                }

                @Override
                public void onServiceDisconnected() {

                }

                @Override
                public void onServiceDied() {

                }

                @Override
                public void onRemoteAppNotInstalled() {

                }

                @Override
                public void onServiceNotConnected() {

                }

                @Override
                public void onLoginSuccess() {
                    // 登录成功后，获取身份证号
                    sdk.obtainIdCardFromCert();
                }

                @Override
                public void onLoginFailed(String errorMsg) {
                    OperLogUtil.e("SDK", "登录失败：" + errorMsg);
                }

                @Override
                public void onIdCardObtained(String idCard) {
                    OperLogUtil.d("SDK", "获取身份证号成功：" + idCard);

                    login(idCard,ReportInfo.default_mm);
                    // 在这里处理身份证号（如显示到UI）
                }

                @Override
                public void onIdCardObtainFailed(String errorMsg) {
                    OperLogUtil.e("SDK", "获取身份证号失败：" + errorMsg);
                }


            });

            if (true) {
                return;
            }

//            sdk.login(0,);





            // 进行验证操作
            ld = new LoadingDialog(SplashActivity.this);
            ld.setLoadingText("认证中")
                    .setSuccessText("认证成功")
                    .setFailedText("认证失败")
                    .setInterceptBack(true)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                    .closeSuccessAnim()
                    .show();

            mIntent = new Intent();
            mIntent.setClass(mContext, LoginActivity.class);


            OperLogUtil.e(TAG,"开始获取证书：");
//            simkeyManager.initSimkey(SplashActivity.this, new SimkeyListener() {
//                @Override
//                public void onConnected() {
//                    // tv.setText("simkey 连接成功！");
//                    OperLogUtil.e(TAG,"开始获取证书**：");
//                    SimkeyResponse response = simkeyManager.getCert(SimkeyManager.SIGN_CERT);
//                    if (response.getCode() != 0) {
//                        // tv.setText("获取证书失败。 error：" + response.getCode());
//                        ld.setFailedText("获取证书失败:"+ response.getCode());
//                        ld.loadFailed();
//                        OperLogUtil.e(TAG,"获取证书失败。 error：" + response.getCode());
//
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                finish(); // 2 秒后关闭当前 Activity
//                            }
//                        }, 2000);
//                    } else {
//                        //tv.setText("获取证书：" + Base64.encodeToString(response.getData(), Base64.DEFAULT));
//
//                        String result = new String(response.getData(), StandardCharsets.UTF_8);
//                        OperLogUtil.e(TAG,"获取证书String：" + result + "**" + response.getData().length);
//
//
//
////                                        StringBuilder sb = new StringBuilder();
////                                        for (byte b : response.getData()) {
////                                            sb.append(String.format("%02X", b));
////                                        }
////                                        String result01 = sb.toString();
//
//                        //OperLogUtil.e(TAG,"获取证书String：" + result + "**" + response.getData().length);
//                        String xfzh = Base64.encodeToString(response.getData(), Base64.DEFAULT);
//                        OperLogUtil.e(TAG,"获取证书：" + Base64.encodeToString(response.getData(), Base64.DEFAULT));
//
//
//                        CertificateFactory factory = null;
//                        try {
//                            factory = CertificateFactory.getInstance("x.509",new BouncyCastleProvider());
//                            X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(xfzh)));
//                            X500Principal principal = certificate.getSubjectX500Principal();
//                            OperLogUtil.msg("[LOG] principal.getName(): " + principal.getName());
//
//                            Map<String, String> attributes = parseDN(principal.getName());
//                            String cn = attributes.get("CN");
//                            String givenName = attributes.get("2.5.4.42");
//
//                            // 提取姓名和身份证号
//                            String[] cnParts = cn.split(" ");
//                            String name = cnParts[0];
//                            xfzh = cnParts.length > 1 ? cnParts[1] : decodeHex(givenName);
//
//                            OperLogUtil.msg("name: " + name + "xfzh:" + xfzh);
//                            ld.loadSuccess();
//                            if (xfzh == null) {
//                                xfzh = new String();
//                            }
//                            mIntent.putExtra("sfzh",xfzh);
//                            //startActivity(mIntent);
//
//
//                            login(xfzh,ReportInfo.default_mm);
//
//
//
//
//
////                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    finish(); // 2 秒后关闭当前 Activity
////                                                }
////                                            }, 2000);
//
//                        } catch (CertificateException e) {
//                            throw new RuntimeException(e);
//                        }
//
//
//
//
//
//
//                    }
//
//                }
//
//                @Override
//                public void onFailed() {
//                    // tv.setText("simekey 连接失败！");
//
//
//                    if(BuildConfig.DEBUG){
//                        login("17674046034","123456");
//
//                        return;
//                    }
//                    OperLogUtil.e(TAG,"获取证书失败。 error：" + "simekey 连接失败");
//                    ld.setFailedText("连接失败");
//                    ld.loadFailed();
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish(); // 2 秒后关闭当前 Activity
//                        }
//                    }, 2000);
//                }
//            });


//                new AlertDialog.Builder(this)
//                        .setTitle("验证提示")
//                        .setMessage("是否进行验证？")
//                        .setPositiveButton("是", (dialog, which) -> {
//                            LoginActivity.isVerify = true;
//
//
//                            if(BuildConfig.DEBUG){
//                                ld.loadSuccess();
//
////                                mIntent.putExtra("sfzh","612324199105023174");
////                                startActivity(mIntent);
//
//
//                                login("18948726601","12345678");
////                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        finish(); // 2 秒后关闭当前 Activity
////                                    }
////                                }, 2000);
//                                return;
//                            }
//                            OperLogUtil.e(TAG,"开始获取证书：");
//                            simkeyManager.initSimkey(SplashActivity.this, new SimkeyListener() {
//                                @Override
//                                public void onConnected() {
//                                   // tv.setText("simkey 连接成功！");
//                                    OperLogUtil.e(TAG,"开始获取证书**：");
//                                    SimkeyResponse response = simkeyManager.getCert(SimkeyManager.SIGN_CERT);
//                                    if (response.getCode() != 0) {
//                                       // tv.setText("获取证书失败。 error：" + response.getCode());
//                                        ld.setFailedText("获取证书失败:"+ response.getCode());
//                                        ld.loadFailed();
//                                        OperLogUtil.e(TAG,"获取证书失败。 error：" + response.getCode());
//
//                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                finish(); // 2 秒后关闭当前 Activity
//                                            }
//                                        }, 2000);
//                                    } else {
//                                        //tv.setText("获取证书：" + Base64.encodeToString(response.getData(), Base64.DEFAULT));
//
//                                        String result = new String(response.getData(), StandardCharsets.UTF_8);
//                                        OperLogUtil.e(TAG,"获取证书String：" + result + "**" + response.getData().length);
//
//
//
////                                        StringBuilder sb = new StringBuilder();
////                                        for (byte b : response.getData()) {
////                                            sb.append(String.format("%02X", b));
////                                        }
////                                        String result01 = sb.toString();
//
//                                        //OperLogUtil.e(TAG,"获取证书String：" + result + "**" + response.getData().length);
//                                        String xfzh = Base64.encodeToString(response.getData(), Base64.DEFAULT);
//                                        OperLogUtil.e(TAG,"获取证书：" + Base64.encodeToString(response.getData(), Base64.DEFAULT));
//
//
//                                        CertificateFactory factory = null;
//                                        try {
//                                            factory = CertificateFactory.getInstance("x.509",new BouncyCastleProvider());
//                                            X509Certificate certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(xfzh)));
//                                            X500Principal principal = certificate.getSubjectX500Principal();
//                                            OperLogUtil.msg("[LOG] principal.getName(): " + principal.getName());
//
//                                            Map<String, String> attributes = parseDN(principal.getName());
//                                            String cn = attributes.get("CN");
//                                            String givenName = attributes.get("2.5.4.42");
//
//                                            // 提取姓名和身份证号
//                                            String[] cnParts = cn.split(" ");
//                                            String name = cnParts[0];
//                                            xfzh = cnParts.length > 1 ? cnParts[1] : decodeHex(givenName);
//
//                                            OperLogUtil.msg("name: " + name + "xfzh:" + xfzh);
//                                            ld.loadSuccess();
//                                            if (xfzh == null) {
//                                                xfzh = new String();
//                                            }
//                                            mIntent.putExtra("sfzh",xfzh);
//                                            //startActivity(mIntent);
//
//
//                                            login(xfzh,ReportInfo.default_mm);
//
//
//
//
//
////                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    finish(); // 2 秒后关闭当前 Activity
////                                                }
////                                            }, 2000);
//
//                                        } catch (CertificateException e) {
//                                            throw new RuntimeException(e);
//                                        }
//
//
//
//
//
//
//                                    }
//
//                                }
//
//                                @Override
//                                public void onFailed() {
//                                   // tv.setText("simekey 连接失败！");
//                                    OperLogUtil.e(TAG,"获取证书失败。 error：" + "simekey 连接失败");
//                                    ld.setFailedText("连接失败");
//                                    ld.loadFailed();
//                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            finish(); // 2 秒后关闭当前 Activity
//                                        }
//                                    }, 2000);
//                                }
//                            });
//
//
//
//
//
//
//                            //通过版本号区分新旧吉安宝
////                            int versioncode1 = getVersionCode(this,"cn.com.jit.jianbao");
////                            OperLogUtil.e(TAG, "versioncode:" + versioncode1);
////                            if(versioncode1 >= 2023011210){
////                                //新版本
////                                MyAsyncTask myAsyncTask = new MyAsyncTask();
////                                myAsyncTask.execute("test");
////                            }else{
////                                //旧版本
////                                initClient();
////                                callClient();
////                            }
//                        })
//                        .setNegativeButton("否", (dialog, which) -> {
//                            OperLogUtil.e(TAG,"跳过获取证书**：");
//                            LoginActivity.isVerify = false;
//
//                            mIntent = new Intent();
//                            mIntent.setClass(mContext, LoginActivity.class);
//                            startActivity(mIntent);
//                            // 用户选择不验证，执行相关逻辑
//                            dialog.dismiss();
//                        })
//                        .setCancelable(false) // 防止用户通过点击对话框外部取消
//                        .show();




            return;
        }





        int userStatus = LoginHelper.prepareUser(mContext, coreManager);
        Intent intent = new Intent();

        switch (userStatus) {
            case LoginHelper.STATUS_USER_FULL:
            case LoginHelper.STATUS_USER_NO_UPDATE:
            case LoginHelper.STATUS_USER_TOKEN_OVERDUE:
                boolean login = PreferenceUtils.getBoolean(this, Constants.LOGIN_CONFLICT, false);
                if (login) {
                    // 登录冲突，直接跳转
                    intent.setClass(mContext, LoginHistoryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 异步自动登录
                    LoginSecureHelper.myAutoLogin(this, coreManager, t -> {
                        if (t instanceof LoginSecureHelper.LoginTokenOvertimeException) {
                            MyApplication.getInstance().mUserStatus = LoginHelper.STATUS_USER_TOKEN_OVERDUE;
                            loginOut();
                        }
                    }, () -> {
                        // 登录成功回调中执行跳转
                        Intent mainIntent = new Intent(mContext, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    });
                }
                break;

            case LoginHelper.STATUS_USER_SIMPLE_TELPHONE:
                intent.setClass(mContext, LoginHistoryActivity.class);
                startActivity(intent);
                finish();
                break;

            case LoginHelper.STATUS_NO_USER:
            default:
                intent.setClass(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
        }
    }

    // 解析 DN 字符串为键值对
    private static Map<String, String> parseDN(String dn) {
        Map<String, String> attributes = new HashMap<>();
        for (String part : dn.split(",\\s*")) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                attributes.put(keyValue[0], keyValue[1]);
            }
        }
        return attributes;
    }

    // 解析 2.5.4.42 中的十六进制 ASCII 编码
    private static String decodeHex(String hex) {
        if (hex == null || !hex.startsWith("#0c")) return null;
        hex = hex.substring(3); // 去掉 "#0c"
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            result.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return result.toString();
    }

    // 第一次进入，显示登录、注册按钮
    private void stay() {
        // 因为启动页有时会替换，无法做到按钮与图片的完美适配，干脆直接进入到登录页面
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    private PermissionExplainDialog getPermissionExplainDialog() {
        if (permissionExplainDialog == null) {
            permissionExplainDialog = new PermissionExplainDialog(this);
        }
        return permissionExplainDialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageLogin message) {
        finish();
    }
    /**
     * 显示隐私政策或跳转到其他界面
     */
    private void check() {

        //先判断是否显示了隐私政策
        currentVersionCode = AppUtil.getAppVersionCode(SplashActivity.this);
        versionCode = (long) SPUtil.get(SplashActivity.this, SP_VERSION_CODE, 0L);
        isCheckPrivacy = (boolean) SPUtil.get(SplashActivity.this, SP_PRIVACY, false);

        if (!isCheckPrivacy) {
            showPrivacy();
        } else {
//            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//            startActivity(intent);
            //Toast.makeText(MainActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(SplashActivity.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        //dialog.show();

        String string = getResources().getString(R.string.privacy_tips);
        String key1 = getResources().getString(R.string.privacy_tips_key1);
        String key2 = getResources().getString(R.string.privacy_tips_key2);
        int index1 = string.indexOf(key1);
        int index2 = string.indexOf(key2);

        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.link_color));
        spannedString.setSpan(colorSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.link_color));
        spannedString.setSpan(colorSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan sizeSpan2 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, TermsActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());

        tv_privacy_tips.setText(spannedString);

        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(SplashActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(SplashActivity.this, SP_PRIVACY, false);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(SplashActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(SplashActivity.this, SP_PRIVACY, true);
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
            }
        });

    }



    //统一认证
    JbProviderClient client = null;
    private final int SUCCESS = 0;
    Response response = null;
    private static int STATUS = -1;
    private LoadingDialog ld ;
    private volatile boolean mStart = false;
    final Boolean[] status = {false};
    private Intent mIntent;


    private void initClient() {
        client = JbProviderClient.getInstance(MyApplication.getAppContext());
        client.regBroadcast();
        client.setProviderListen(new JbProviderClient.ProviderListen() {
            @Override
            public void authSuccess() {//成功的回调
                response = client.call();

                OperLogUtil.e(TAG, "authSuccess response:" + response);
//                tv_result.setText(response.show());
                STATUS = response.getResultCode();
                //第一次获取不成功则再调用一次
                if (STATUS != SUCCESS){
                    response = client.call();
                    STATUS = response.getResultCode();
                }
                if (SUCCESS == STATUS) {
                    ld.loadSuccess();
//                    showSimpleDialog("认证成功");
//                    OperLogUtil.e(TAG, "AppCredential:" + response.getAppCredential());
//                    OperLogUtil.e(TAG, "UserCredential:" + response.getUserCredential());
                    try {
                        JSONObject userObject = JSON.parseObject(response.getUserCredential());
                        Gson gson = new Gson();
                        UserCredential userCredential = gson.fromJson(userObject.get("credential").toString(), UserCredential.class);
                        //                    OperLogUtil.e(TAG, "UserCredential:" + userCredential.getLoad());
                        String xfzh = userCredential.getLoad().getUserInfo().getSfzh();
                        String xm = userCredential.getLoad().getUserInfo().getXm();
                        HeadInfo.headParams.put("userCardId",xfzh);
                        HeadInfo.headParams.put("userName", URLEncoder.encode(xm,"UTF-8"));
                        String org = xfzh.substring(0,6);
                        HeadInfo.headParams.put("userDept",org);
                        String ip = DeviceUtil.getLocalIpAddress(mContext);
                        HeadInfo.headParams.put("userIp",ip);
                        OperLogUtil.e(TAG, "sfzh: " + xfzh);
                        OperLogUtil.e(TAG, "xm: " + xm);
                        JSONObject appObject = JSON.parseObject(response.getAppCredential());
                        AppCredential appCredential = gson.fromJson(appObject.get("credential").toString(), AppCredential.class);
                        //                    OperLogUtil.e(TAG, "AppCredential:" + appCredential.toString());
                        OperLogUtil.e(TAG, "appid:" + appCredential.getLoad().getAppInfo().getAppId());
                        String userEncode = URLEncoder.encode(userObject.get("credential").toString(),"UTF-8");
                        OperLogUtil.e(TAG, "AppCredential encode:" + userEncode);
                        String appEncode = URLEncoder.encode(appObject.get("credential").toString(),"UTF-8");
                        OperLogUtil.e(TAG, "UserCredential encode:" + appEncode);
                        HeadInfo.headParams.put("userCredential",userEncode);
                        HeadInfo.headParams.put("appCredential",appEncode);
                        mIntent.putExtra("sfzh",xfzh);
//                        startActivity(mIntent);
//                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(mIntent);
                                finish();
                            }
                        }, 1000);
                    } catch (Exception e) {
                        ld.loadFailed();
                        e.printStackTrace();
                        OperLogUtil.e(TAG, e.getMessage());
                        finish();
                    }

                } else {
                    ld.loadFailed();
                    finish();
                }
            }

            @Override
            public void authCanel() {//取消的回调
                ld.loadFailed();
                finish();
            }
        });
    }

    private void callClient() {

        Response response = client.call();
        if (response == null){
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    //更新UI
                    ld.loadFailed();
                }

            });
            PopupDialog.create(SplashActivity.this, "提示", "请先启动吉安宝", "确定",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SplashActivity.this.finish();
                        }
                    }, null,
                    null, false,
                    false, true).show();
            return;

        }
//        STATUS = response.getResultCode();
//        String message = response.getMessage();
//        OperLogUtil.e(TAG, "callClient response :" + response);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null){
            client.unRegBroadcast();
        }
    }


    class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            //这里是开始线程之前执行的,是在UI线程

            client = JbProviderClient.getInstance(MyApplication.getAppContext());
            client.regBroadcast();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //这是在后台子线程中执行的
            try {
                client.setProviderListen(new JbProviderClient.ProviderListen() {
                    @Override
                    public void authSuccess() {//成功的回调
                        response = client.call();

                        OperLogUtil.e(TAG, "authSuccess response:" + response);
                        STATUS = response.getResultCode();
                        //第一次获取不成功则再调用一次
                        if (STATUS != SUCCESS){
                            response = client.call();
                            STATUS = response.getResultCode();
                        }
                        if (SUCCESS == STATUS) {
                            try {
                                JSONObject userObject = JSON.parseObject(response.getUserCredential());
                                Gson gson = new Gson();
                                UserCredential userCredential = gson.fromJson(userObject.get("credential").toString(), UserCredential.class);
                                //                    OperLogUtil.e(TAG, "UserCredential:" + userCredential.getLoad());
                                String xfzh = userCredential.getLoad().getUserInfo().getSfzh();
                                String xm = userCredential.getLoad().getUserInfo().getXm();
                                HeadInfo.headParams.put("userCardId",xfzh);
                                HeadInfo.headParams.put("userName", URLEncoder.encode(xm,"UTF-8"));
                                String org = xfzh.substring(0,6);
                                HeadInfo.headParams.put("userDept",org);
                                String ip = DeviceUtil.getLocalIpAddress(mContext);
                                HeadInfo.headParams.put("userIp",ip);
                                OperLogUtil.e(TAG, "sfzh: " + xfzh);
                                OperLogUtil.e(TAG, "xm: " + xm);
                                JSONObject appObject = JSON.parseObject(response.getAppCredential());
                                AppCredential appCredential = gson.fromJson(appObject.get("credential").toString(), AppCredential.class);
                                //                    OperLogUtil.e(TAG, "AppCredential:" + appCredential.toString());
                                OperLogUtil.e(TAG, "appid:" + appCredential.getLoad().getAppInfo().getAppId());
                                String userEncode = URLEncoder.encode(userObject.get("credential").toString(),"UTF-8");
                                OperLogUtil.e(TAG, "AppCredential encode:" + userEncode);
                                String appEncode = URLEncoder.encode(appObject.get("credential").toString(),"UTF-8");
                                OperLogUtil.e(TAG, "UserCredential encode:" + appEncode);
                                HeadInfo.headParams.put("userCredential",userEncode);
                                HeadInfo.headParams.put("appCredential",appEncode);
                                mIntent.putExtra("sfzh",xfzh);
                                //设置成功标志
                                status[0] = true;
                                mStart = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                OperLogUtil.e(TAG, e.getMessage());
                                status[0] = false;
                                mStart = true;
                            }

                        } else {
                            status[0] = false;
                            mStart = true;
                        }
                    }

                    @Override
                    public void authCanel() {//取消的回调
                        status[0] = false;
                        mStart = true;
                    }
                });
                //同步方法执行
                response = client.call();

                OperLogUtil.e(TAG, "response result:" + response);
                STATUS = response.getResultCode();
                if(status[0])
                    return status[0];
                //第一次获取不成功则再调用一次
                /*if (STATUS != SUCCESS){
                    response = client.call();
                    STATUS = response.getResultCode();
                }
                if(status[0])
                    return status[0];*/

                if (SUCCESS == STATUS) {

                    try {
                        JSONObject userObject = JSON.parseObject(response.getUserCredential());
                        Gson gson = new Gson();
                        UserCredential userCredential = gson.fromJson(userObject.get("credential").toString(), UserCredential.class);
                        //                    OperLogUtil.e(TAG, "UserCredential:" + userCredential.getLoad());
                        String xfzh = userCredential.getLoad().getUserInfo().getSfzh();
                        String xm = userCredential.getLoad().getUserInfo().getXm();
                        HeadInfo.headParams.put("userCardId",xfzh);
                        HeadInfo.headParams.put("userName", URLEncoder.encode(xm,"UTF-8"));
                        String org = xfzh.substring(0,6);
                        HeadInfo.headParams.put("userDept",org);
                        String ip = DeviceUtil.getLocalIpAddress(mContext);
                        HeadInfo.headParams.put("userIp",ip);
                        OperLogUtil.e(TAG, "sfzh: " + xfzh);
                        OperLogUtil.e(TAG, "xm: " + xm);
                        JSONObject appObject = JSON.parseObject(response.getAppCredential());
                        AppCredential appCredential = gson.fromJson(appObject.get("credential").toString(), AppCredential.class);
                        //                    OperLogUtil.e(TAG, "AppCredential:" + appCredential.toString());
                        OperLogUtil.e(TAG, "appid:" + appCredential.getLoad().getAppInfo().getAppId());
                        String userEncode = URLEncoder.encode(userObject.get("credential").toString(),"UTF-8");
                        OperLogUtil.e(TAG, "AppCredential encode:" + userEncode);
                        String appEncode = URLEncoder.encode(appObject.get("credential").toString(),"UTF-8");
                        OperLogUtil.e(TAG, "UserCredential encode:" + appEncode);
                        HeadInfo.headParams.put("userCredential",userEncode);
                        HeadInfo.headParams.put("appCredential",appEncode);
                        mIntent.putExtra("sfzh",xfzh);
//                      设置标志
                        status[0] = true;
                    } catch (Exception e) {

                        e.printStackTrace();
                        OperLogUtil.e(TAG, e.getMessage());
                        status[0] = false;
                    }

                } else {
                    //等待监听执行结果
                    int i = 0;
                    while (!mStart){
                        //超过20秒没有返回结果则提示失败
                        if (i++ > 20){
                            status[0] = false;
                            break;
                        }
                        sleep(1000);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                status[0] = false;
            }
            return status[0];
        }

        @Override
        protected void onCancelled() {
            //当任务被取消时回调
            if (ld != null){
                ld.loadFailed();
            }
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //更新进度

        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            //当任务执行完成是调用,在UI线程
            if (ld != null){
                if (status){
                    ld.loadSuccess();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(mIntent);
                            finish();
                        }
                    }, 1000);
                }else{
                    ld.loadFailed();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }
        }
    }

    public static int getVersionCode(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }



    private void login(String userName,String pwd) {
        String phoneNumber = userName;

        // 加密之后的密码
       // final String digestPwd = LoginPassword.encodeMd5(pwd);

        DialogHelper.showDefaulteMessageProgressDialog(this);

        Map<String, String> params = new HashMap<>();
        params.put("xmppVersion", "1");
        // 附加信息+
        params.put("model", DeviceInfoUtil.getModel());
        params.put("osVersion", DeviceInfoUtil.getOsVersion());
        params.put("serial", DeviceInfoUtil.getDeviceId(mContext));
        // 地址信息
        double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
        double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
        if (latitude != 0)
            params.put("latitude", String.valueOf(latitude));
        if (longitude != 0)
            params.put("longitude", String.valueOf(longitude));

        if (MyApplication.IS_OPEN_CLUSTER) {// 服务端集群需要
            String area = PreferenceUtils.getString(this, AppConstant.EXTRA_CLUSTER_AREA);
            if (!TextUtils.isEmpty(area)) {
                params.put("area", area);
            }
        }

        LoginSecureHelper.mySecureLogin (
                this, coreManager, String.valueOf ( mobilePrefix ), phoneNumber, pwd, "", "", "", "", false,
                params,
                t -> {
                    DialogHelper.dismissProgressDialog();
                    String msg = t.getMessage();

                    SharedPreferences info = getSharedPreferences ( "info", MODE_PRIVATE );
                    SharedPreferences.Editor edit = info.edit ();
                    edit.putString ( "phoneNumber", phoneNumber );
                    edit.putString ( "password",pwd );
                    edit.apply ();
                    ToastUtil.showToast ( this, this.getString ( R.string.tip_login_secure_place_holder, msg ) );


                    OperLogUtil.msg("登录失败"+ this.getString ( R.string.tip_login_secure_place_holder, msg ));
                }, result -> {
                    DialogHelper.dismissProgressDialog();
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(result));
                        int code = jsonObject.getIntValue("resultCode");
                        String msg = jsonObject.getString("resultMsg");
                        ObjectResult<LoginRegisterResult> objectResult = new ObjectResult<>();
                        objectResult.setResultCode(code);
                        objectResult.setCurrentTime(jsonObject.getLongValue("currentTime"));
                        objectResult.setResultMsg(msg);
                        OperLogUtil.msg("登录数据lclog"+ jsonObject.toJSONString());
                        if(code == 1) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            if (data != null) {
                                OperLogUtil.msg("登录数据"+ "jdata != null");
                                LoginRegisterResult realResult = JSON.parseObject(String.valueOf(data), LoginRegisterResult.class);
                                objectResult.setData(realResult);
                                ReportInfo.name = realResult.getNickName();
                                //防止自动登录时name传值过慢，本地保存个name
                                FastSharedPreferences.get("USERINFO_SAVE").edit().putString("name",realResult.getNickName()).apply();

                                ReportInfo.phone = phoneNumber;
                                ReportInfo.interphone = phoneNumber;
                                ReportInfo.police_id = realResult.getUserId();
//                            ReportInfo.unit = "测试单位";
                            }
                            OperLogUtil.msg("登录数据"+ "afterLogin" + phoneNumber + pwd);
                            afterLogin(objectResult, phoneNumber, pwd);
                        }else{

                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

                        }
                    }catch (Exception e){
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        OperLogUtil.e("login", "json error!");
                    }
                }
        );
    }

    private void afterLogin(ObjectResult<LoginRegisterResult> result, String phoneNumber, String digestPwd) {

            start(digestPwd, result, phoneNumber, digestPwd);


    }
    private void start(String password, ObjectResult<LoginRegisterResult> result, String phoneNumber, String digestPwd) {

        if(password.length() < 0){
            android.app.AlertDialog alert = null;
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            alert = builder.setTitle("提示")
                    .setMessage("初始密码过于简单，去设置自己的密码")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent in = new Intent(mContext, ChangePasswordActivity.class);
                            startActivity(in);
                        }
                    }).create();             //创建AlertDialog对象
            alert.show();

            return;
        }

        OperLogUtil.msg("登录完成初始化数据");
        LoginHelper.setLoginUser(mContext, coreManager, phoneNumber, digestPwd, result);

        LoginRegisterResult.Settings settings = result.getData().getSettings();
        OperLogUtil.msg("登录完成初始化数据");
        MyApplication.getInstance().initPayPassword(result.getData().getUserId(), result.getData().getPayPassword());
        OperLogUtil.msg("登录完成初始化数据" + "initPayPassword");
        YeepayHelper.saveOpened(mContext, result.getData().getWalletUserNo() == 1);
        OperLogUtil.msg("登录完成初始化数据" + "saveOpened");
        PrivacySettingHelper.setPrivacySettings(SplashActivity.this, settings);
        OperLogUtil.msg("登录完成初始化数据" + "setPrivacySettings");
        MyApplication.getInstance().initMulti();
        OperLogUtil.msg("登录完成初始化数据" + "initMulti");

        // startActivity(new Intent(mContext, DataDownloadActivity.class));
//        String token = result.getData().getAccessToken();
//        String uuid = result.getData().getUserId();
////        if(result.getData().getAccessToken() != null && uuid != null){
////            AppStoreInit.store_token = "";
//            AppStoreInit.initData(token,uuid);
        DataInit.setCallBack(new DataInit.CallBack() {
            @Override
            public void requestAppsSuc() {
                //DataDownloadActivity.start(mContext, result.getData().getIsupdate(), password);
                OperLogUtil.msg("获取app数据成功");
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                /*}*/
                startActivity(intent);
                finish();
            }

            @Override
            public void requestAppsFail(String msg) {

                OperLogUtil.msg("获取app数据失败");
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                /*}*/
                startActivity(intent);
                finish();
            }
        });


    }
    public void loginOut() {
        Log.d(TAG, "loginOut() called");
        coreManager.logout();
        if (MyApplication.getInstance().mUserStatus == LoginHelper.STATUS_USER_TOKEN_OVERDUE) {
            UserCheckedActivity.start(MyApplication.getContext());
        }
        finish();
    }

}
