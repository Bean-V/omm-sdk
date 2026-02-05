package com.oort.weichat.ui.account;

import static com.oortcloud.basemodule.CommonApplication.getmSeralNum;
import static com.oortcloud.basemodule.constant.Constant.IsNotShowOffline;
import static com.oortcloud.basemodule.constant.Constant.isOffLine;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConfig;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.Depcode;
import com.oort.weichat.bean.LoginRegisterResult;
import com.oort.weichat.bean.QQLoginResult;
import com.oort.weichat.bean.WXUploadResult;
import com.oort.weichat.bean.event.MessageLogin;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.helper.LoginSecureHelper;
import com.oort.weichat.helper.PasswordHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.helper.QQHelper;
import com.oort.weichat.helper.UsernameHelper;
import com.oort.weichat.helper.YeepayHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.dialog.DialogRequestPermission;
import com.oort.weichat.ui.live.adapter.DepcodeAdapter;
import com.oort.weichat.ui.me.SetConfigActivity;
import com.oort.weichat.ui.offline.OffLineManagerActivity;
import com.oort.weichat.ui.other.PrivacyAgreeActivity;
import com.oort.weichat.util.AppUtils;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.EventBusHelper;
import com.oort.weichat.util.PermissionUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.offline.OfflineFileLoaderListenter;
import com.oort.weichat.util.offline.OfflineSyncManager;
import com.oort.weichat.util.offline.OfflineUtil;
import com.oort.weichat.util.secure.LoginPassword;
import com.oort.weichat.view.PermissionExplainDialog;
import com.oort.weichat.view.VerifyDialog;
import com.oort.weichat.wxapi.WXEntryActivity;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.basemodule.BuildConfig;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.dialog.PopupDialog;
import com.oortcloud.basemodule.im.HeadInfo;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.utils.DeviceUtil;
import com.oortcloud.basemodule.utils.GuidUtil;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.loadingdialog.view.LoadingDialog;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.utils.ProCodeUtils;
import com.tencent.tauth.Tencent;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.com.jit.provider.lib.JbProviderClient;
import cn.com.jit.provider.lib.Response;
import cn.com.jit.provider.lib.net.AppCredential;
import cn.com.jit.provider.lib.net.UserCredential;
import okhttp3.Call;

/**
 * 登陆界面
 *
 * @author Dean Tao
 * @version 1.0
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String THIRD_TYPE_WECHAT = "2";
    public static final String THIRD_TYPE_QQ = "1";

    private EditText mPhoneNumberEdit;
    private EditText mPasswordEdit;
    private TextView tv_prefix;
    private int mobilePrefix = 86;
    private String thirdToken;
    private String thirdTokenType;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    // 声明一个数组，用来存储所有需要动态申请的权限
    private static final int REQUEST_CODE = 0;
    private final Map<String, Integer> permissionsMap = new LinkedHashMap<>();
    // 复用请求权限的说明对话框，
    private PermissionExplainDialog permissionExplainDialog;
    private Button forgetPasswordBtn, registerBtn, loginBtn;
    private boolean third;
    private VerifyDialog mVerifyDialog;

    private String captchaID;
    private EditText mCodeEdit;
    private ImageView mCodeImage;
    private LinearLayout mCodeLinear;
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");

    private Spinner departmentSp,departmentSp1;
    private String departmentStr;

    private OfflineSyncManager mOfflineSyncManager;
    private LinearLayout offlinegone;
    private View gonev;

    private ProCodeUtils mProcode;

    public static String LOGIN_UUID;

    private String sfzh;
    //统一认证
    JbProviderClient client = null;
    private final int SUCCESS = 0;
    Response response = null;
    private static int STATUS = -1;
    private LoadingDialog ld ;

    private volatile boolean mStart = false;
    final Boolean[] status = {false};


    public static boolean isVerify = false;
    public static boolean isAdmin = false;

    public LoginActivity() {
        noLoginRequired();
        // 手机状态
        permissionsMap.put(Manifest.permission.READ_PHONE_STATE, R.string.permission_phone_status);
        // 照相
        permissionsMap.put(Manifest.permission.CAMERA, R.string.permission_photo);
        // 麦克风
        permissionsMap.put(Manifest.permission.RECORD_AUDIO, R.string.permission_microphone);
        // 存储权限
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permission_storage);
        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_storage);

        permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_location);
        permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_location);
    }

    public static void bindThird(Context ctx, String thirdToken, String thirdTokenType, boolean testLogin) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.putExtra("thirdToken", thirdToken);
        intent.putExtra("thirdTokenType", thirdTokenType);
        intent.putExtra("testLogin", testLogin);
        ctx.startActivity(intent);
    }

    public static void bindThird(Context ctx, String thirdToken, String thirdTokenType) {
        bindThird(ctx, thirdToken, thirdTokenType, false);
    }

    public static void bindThird(Context ctx, WXUploadResult thirdToken) {
        bindThird(ctx, JSON.toJSONString(thirdToken), THIRD_TYPE_WECHAT, true);
    }

    public static void bindThird(Context ctx, QQLoginResult thirdToken) {
        bindThird(ctx, JSON.toJSONString(thirdToken), THIRD_TYPE_QQ, true);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if (intent != null && isVerify){//BuildConfig.DEBUG// && false


            sfzh = intent.getStringExtra("sfzh");
            if (sfzh == null || sfzh.isEmpty()){
                ld = new LoadingDialog(this);
                ld.setLoadingText("认证中")
                        .setSuccessText("认证成功")
                        .setFailedText("认证失败")
                        .setInterceptBack(true)
                        .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                        .closeSuccessAnim()
                        .show();
                //统一认证
                //通过版本号区分新旧吉安宝
                int versioncode = getVersionCode(this,"cn.com.jit.jianbao");
                OperLogUtil.e(TAG, "versioncode:" + versioncode);
                if(versioncode >= 2023011210){
                    //新版本
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute("test");
                }else{
                    //旧版本
                    initClient();
                    callClient();
                }
            }
        }

        if(isAdmin){
            isAdmin = false;
            findViewById(R.id.tbEye).setVisibility(View.GONE);
        }


        isOffLine = spUser.getBoolean("isoffline",false);

        // 同时请求定位以外的权限，
        requestPermissions();
//        PermissionUtil.requestLocationPermissions(this, 0x01);
        thirdToken = getIntent().getStringExtra("thirdToken");
        thirdTokenType = getIntent().getStringExtra("thirdTokenType");
        initActionBar();
        initView();
        initEvent();

        IntentFilter filter = new IntentFilter();
        filter.addAction("CHANGE_CONFIG");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, filter,Context.RECEIVER_NOT_EXPORTED);
        }else{
            registerReceiver(broadcastReceiver, filter);
        }

        if (!TextUtils.isEmpty(thirdToken) && getIntent().getBooleanExtra("testLogin", false)) {
            // 第三方进来直接登录，
            // 清空手机号以标记是第三方登录，
            mPhoneNumberEdit.setText("");
            login(true);
        }


        if (sfzh != null && !sfzh.isEmpty()){
            mPhoneNumberEdit.setText(sfzh);
            String text;
            if (ReportInfo.default_mm == null || ReportInfo.default_mm.isEmpty()) {
                text = spUser.getString("MYWORD","");
            }else{
                text = ReportInfo.default_mm;
            }
            mPasswordEdit.setText(text);
            OperLogUtil.e("zlm", "mm:"+ReportInfo.default_mm);
            OperLogUtil.msg(sfzh + "自动登录");
            login(false);
        }
        EventBusHelper.register(this);


        OperLogUtil.e("3444444","reeeee");
    }
    public void initEvent(){

        
        mOfflineSyncManager = OfflineSyncManager.init(this);
        mOfflineSyncManager.setmArchiveFileLoaderListenter(new OfflineFileLoaderListenter() {
            @Override
            public void onLoadFish() {

            }

            @Override
            public void onFail(String err) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果没有保存用户定位信息，那么去地位用户当前位置
//        if (!MyApplication.getInstance().getBdLocationHelper().isLocationUpdate()) {
//            MyApplication.getInstance().getBdLocationHelper().requestLocation();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null){
            client.unRegBroadcast();
        }
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (Exception e){}

    }

    private void initActionBar() {
//        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setVisibility(View.GONE);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        if (TextUtils.isEmpty(thirdToken)) {
            tvTitle.setText(getString(R.string.login));
        } else {
            // 第三方登录的不要提示登录，而是绑定手机号码，
            tvTitle.setText(getString(R.string.bind_old_account));
        }
        TextView tvRight = (TextView) findViewById(R.id.tv_title_right);
        // 定制包隐藏设置服务器按钮，
        if (!AppConfig.isShiku() || !BuildConfig.DEBUG) {
            // 为方便测试，留个启用方法，adb shell命令运行"setprop log.tag.ShikuServer D"启用，
            if (!Log.isLoggable("ShikuServer", Log.DEBUG)) {
                tvRight.setVisibility(View.GONE);
            }
        }
        // 隐藏开关，方便测试人员调试
        tvTitle.setOnLongClickListener(v -> {

            if(true){
                return false;
            }
            tvRight.setVisibility(View.VISIBLE);
            return false;
        });
        tvRight.setText(R.string.settings_server_address);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SetConfigActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        gonev = findViewById(R.id.gone_v);
        offlinegone = findViewById(R.id.offline_gone_ly);
        departmentSp = findViewById(R.id.regist_department_sp);

        departmentSp1 = findViewById(R.id.regist_department_sp1);
        mPhoneNumberEdit = (EditText) findViewById(R.id.phone_numer_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        //获取部门下拉选择框数据
        departmentSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] departmentArray = getResources().getStringArray(R.array.departmentarray);
                departmentStr = departmentArray[i].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //获取部门下拉选择框数据
        departmentSp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] departmentArray = getResources().getStringArray(R.array.departmentarray);
                departmentStr = departmentArray[i].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        PasswordHelper.bindPasswordEye(mPasswordEdit, findViewById(R.id.tbEye));
        tv_prefix = (TextView) findViewById(R.id.tv_prefix);
        if (coreManager.getConfig().registerUsername) {
            tv_prefix.setVisibility(View.GONE);
        } else {
            tv_prefix.setOnClickListener(this);
        }
        mobilePrefix = PreferenceUtils.getInt(this, Constants.AREA_CODE_KEY, mobilePrefix);
        tv_prefix.setText("+" + mobilePrefix);

        // 登陆账号
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        // 注册账号
        registerBtn = (Button) findViewById(R.id.register_account_btn);
        if (coreManager.getConfig().isOpenRegister) {
            if (TextUtils.isEmpty(thirdToken)) {
                registerBtn.setOnClickListener(this);
            } else {
                // 第三方登录的不需要这个注册按钮，登录后没有账号直接跳到注册，
                registerBtn.setVisibility(View.GONE);
            }
        } else {
            registerBtn.setVisibility(View.GONE);
        }
        // 忘记密码
        forgetPasswordBtn = (Button) findViewById(R.id.forget_password_btn);
        if (!TextUtils.isEmpty(thirdToken) || coreManager.getConfig().registerUsername) {
            forgetPasswordBtn.setVisibility(View.GONE);
        }
        forgetPasswordBtn.setOnClickListener(this);
        UsernameHelper.initEditText(mPhoneNumberEdit, coreManager.getConfig().registerUsername);
        loginBtn.setText(getString(R.string.login));
        registerBtn.setText(getString(R.string.register));
        forgetPasswordBtn.setText(getString(R.string.forget_password));

        findViewById(R.id.sms_login_btn).setOnClickListener(this);

        if (TextUtils.isEmpty(thirdToken)) {
            findViewById(R.id.wx_login_btn).setOnClickListener(this);
            if (QQHelper.ENABLE) {
                findViewById(R.id.qq_login_btn).setOnClickListener(this);
            } else {
                findViewById(R.id.qq_login_fl).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.wx_login_fl).setVisibility(View.GONE);
            findViewById(R.id.qq_login_fl).setVisibility(View.GONE);
        }

        findViewById(R.id.main_content).setOnClickListener(this);

        if (!coreManager.getConfig().thirdLogin) {
            findViewById(R.id.wx_login_fl).setVisibility(View.GONE);
            findViewById(R.id.qq_login_fl).setVisibility(View.GONE);
        }

        if (coreManager.getConfig().registerUsername) {
            // 开启用户名注册登录的情况隐藏短信登录，
            findViewById(R.id.sms_login_fl).setVisibility(View.GONE);
        }

        mCodeLinear = findViewById(R.id.code_linear);
        mCodeEdit = findViewById(R.id.code_tv);
        mCodeImage = findViewById(R.id.code_image);
        findViewById(R.id.code_image_refresh).setOnClickListener(this);
        findViewById(R.id.tvOffManager).setOnClickListener(this);
        if (isOffLine){
            offlinegone.setVisibility (View.GONE);
            gonev.setVisibility (View.GONE);
            OfflineUtil.offlineInit(LoginActivity.this);
        }


        if(IsNotShowOffline){
            findViewById(R.id.ll_offline).setVisibility(View.GONE);
        }
        Switch switch1 = findViewById(R.id.switch1);
        switch1.setChecked(isOffLine);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean b = OfflineUtil.isHasInit(LoginActivity.this);
                    if (!b) {
                        switch1.setChecked(false);
                        isOffLine = false;
                    } else {

                        departmentSp.setVisibility(View.GONE);
                        departmentSp1.setVisibility(View.VISIBLE);
                        offlinegone.setVisibility (View.GONE);
                        gonev.setVisibility (View.GONE);
                        OfflineUtil.offlineInit(LoginActivity.this);
                        isOffLine = true;
                    }
                } else {
                    offlinegone.setVisibility (View.VISIBLE);
                    gonev.setVisibility (View.VISIBLE);
                    isOffLine = false;
                }
                spUser.edit().putBoolean("isoffline",isOffLine).apply();
            }
        });


        if(Constant.IsLoginWithoutDepart){
            findViewById(R.id.ll_depart).setVisibility(View.GONE);
        }else{
            depCode ();
        }
        if(!isVerify){
            autoFillCredentials();
        }

        if(BuildConfig.DEBUG){
            mPhoneNumberEdit.setText("18948726601");
            mPasswordEdit.setText("12345678");

//            mPhoneNumberEdit.setText("140702198909117031");//142602197405030014//140702198909117031
//            mPasswordEdit.setText("123456");
//            mPhoneNumberEdit.setText("652901198010011438");
//            mPasswordEdit.setText("123456");
//            mPhoneNumberEdit.setText("612324199105023174");
//            mPasswordEdit.setText("123456789");

            findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mPhoneNumberEdit.getText().toString().equals("1")) {
                        mPhoneNumberEdit.setText("18811110001");
                        mPasswordEdit.setText("123456");
                    }else if (mPhoneNumberEdit.getText().toString().equals("2")){
                        mPhoneNumberEdit.setText("18948726601");
                        mPasswordEdit.setText("12345678");
                    }else if (mPhoneNumberEdit.getText().toString().equals("3")){
                        mPhoneNumberEdit.setText("612324199105023174");
                        mPasswordEdit.setText("123456789");
                    }else if (mPhoneNumberEdit.getText().toString().equals("4")){
                        mPhoneNumberEdit.setText("17097227961");
                        mPasswordEdit.setText("zlm12345678");
                    }else{
                        mPhoneNumberEdit.setText("43290219831222061x");
                        mPasswordEdit.setText("12345678");
                    }
//                    if (mPhoneNumberEdit.getText().toString().equals("140702198909117031")) {
//                        mPhoneNumberEdit.setText("142602197405030014");
//                        mPasswordEdit.setText("123456");
//                    }else{
//                        mPhoneNumberEdit.setText("140702198909117031");
//                        mPasswordEdit.setText("12345678");
//                    }

                }
            });
        }

    }

    /**
     * 是否离线
     */
//    private boolean isOffLine = false;
    public void depCode(){
        RequesManager.depcode ().subscribe(new RxBus.BusObserver<String> (){
            @Override
            public void onNext(String s) {
                com.oortcloud.bean.Result<List<Depcode>> result = new Gson().fromJson(s, new TypeToken<com.oortcloud.bean.Result<List<Depcode>>>() {}.getType());
                List<Depcode> depCodes = result.getData ();
                if (depCodes != null){
                    DepcodeAdapter adapter = new DepcodeAdapter (depCodes,getApplicationContext ());
                    departmentSp.setAdapter (adapter);

                }

                if (TextUtils.isEmpty ( s )) {
                    return;
                }
//                String ssj = s.substring ( 29,35 );
//                OperLogUtil.v ( "------vvvvvvvvvv------",ssj );
            }
        });
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_prefix) {// 选择国家区号
            Intent intent = new Intent(this, SelectPrefixActivity.class);
            startActivityForResult(intent, SelectPrefixActivity.REQUEST_MOBILE_PREFIX_LOGIN);
        } else if (id == R.id.login_btn) {// 登陆
            if (isOffLine) {
                offlineLogin();
            } else {

//                    if (Build.VERSION.SDK_INT >= 23)
//                    {
//                        if (!Settings.canDrawOverlays(this))
//                        {
//                            String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
//                            Intent intent1 = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
//
//                            this.startActivity(intent1);
//
//                            return;
//                        }else {
//
//                        }
//
//                    }

                OperLogUtil.msg("点击了登录");
                login(false);
            }
        } else if (id == R.id.wx_login_btn) {
            if (!AppUtils.isAppInstalled(mContext, "com.tencent.mm")) {
                Toast.makeText(mContext, getString(R.string.tip_no_wx_chat), Toast.LENGTH_SHORT).show();
            } else {
                WXEntryActivity.wxLogin(this);
            }
        } else if (id == R.id.qq_login_btn) {
            if (!QQHelper.qqInstalled(mContext)) {
                Toast.makeText(mContext, getString(R.string.tip_no_qq_chat), Toast.LENGTH_SHORT).show();
            } else {
                QQHelper.qqLogin(this);
            }
        } else if (id == R.id.register_account_btn) {// 注册
            register();
        } else if (id == R.id.forget_password_btn) {// 忘记密码
            startActivity(new Intent(mContext, FindPwdActivity.class));
        } else if (id == R.id.sms_login_btn) {
            startActivity(new Intent(mContext, AuthCodeActivity.class));
        } else if (id == R.id.main_content) {// 点击空白区域隐藏软键盘
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(findViewById(R.id.main_content).getWindowToken(), 0); //强制隐藏键盘
            }
        } else if (id == R.id.code_image_refresh) {
            if (captchaID != null) {
                getCodeImage();
            }
        } else if (id == R.id.tvOffManager) {
            OffLineManagerActivity.start(this);
        }
    }

    private void offlineLogin() {

        mProcode = ProCodeUtils.getInstance();
        String phoneNumber = mPhoneNumberEdit.getText().toString().trim() ;//+ LOGIN_UUID;
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.showLongToast(this, "姓名不能为空");
            return;
        }
        LoginPresenter.offlineLogin(LoginActivity.this, phoneNumber);
    }


    private void register() {
        RegisterActivity.registerFromThird(
                this,
                mobilePrefix,
                mPhoneNumberEdit.getText().toString(),
                mPasswordEdit.getText().toString(),
                thirdToken,
                thirdTokenType
        );
    }

    /**
     * @param third 第三方自动登录，
     */
    private void login(boolean third) {
        this.third = third;
        login();
//        mylogin();
//        captchaLogin();
    }

    private void mylogin(){
        final String phoneNumber = mPhoneNumberEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        String codeID = mCodeEdit.getText().toString().trim();
        // 加密之后的密码
        final String digestPwd = LoginPassword.encodeMd5(password);
        spUser.edit().putString("MYWORD", password).apply();
        RequesManager.login(phoneNumber, password, captchaID, codeID).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                OperLogUtil.v("msg" , s);
                DialogHelper.dismissProgressDialog();
                if (TextUtils.isEmpty(s)) {
//                    ToastUtils.showShortSafe("登录失败！");
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    int code = jsonObject.getIntValue("resultCode");
                    String msg = jsonObject.getString("resultMsg");
                    ObjectResult<LoginRegisterResult> objectResult = new ObjectResult<>();
                    objectResult.setResultCode(code);
                    objectResult.setCurrentTime(jsonObject.getLongValue("currentTime"));
                    objectResult.setResultMsg(msg);
                    if(code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        if (data != null) {
                            LoginRegisterResult realResult = JSON.parseObject(String.valueOf(data), LoginRegisterResult.class);
                            objectResult.setData(realResult);
                            ReportInfo.name = realResult.getNickName();
                            ReportInfo.phone = phoneNumber;
                            ReportInfo.interphone = phoneNumber;
                            ReportInfo.police_id = realResult.getUserId();
//                            ReportInfo.unit = "测试单位";
                        }

                        afterLogin(objectResult, phoneNumber, password);
                    }else{
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data != null) {
                            captchaID = data.getString("CaptchaID");
                            if (captchaID != null) {
                                getCodeImage();
                                mCodeLinear.setVisibility(View.VISIBLE);
                            }
                        }
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();


                    }
                }catch (Exception e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    OperLogUtil.e("login", "json error!");
                }
            }

            @Override
            public void onError(Throwable e) {

//                dismissDialog();
//                ToastUtils.showShortSafe(e.getMessage());
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
    String phoneNumber = "";


    private void saveCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", mPhoneNumberEdit.getText().toString());
        editor.putString("password", mPasswordEdit.getText().toString());
        editor.apply();  // 异步保存数据
    }


    private void autoFillCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");

        mPhoneNumberEdit.setText(savedUsername);
        mPasswordEdit.setText(savedPassword);
    }

    private void login() {
//        login()
//        mProcode = ProCodeUtils.getInstance();
//        final String phoneNumber = mProcode.getProCode ( departmentStr )+mPhoneNumberEdit.getText ().toString ().trim ();
        saveCredentials();

        Depcode depcode = (Depcode) departmentSp.getSelectedItem ();
        phoneNumber = mPhoneNumberEdit.getText().toString().trim();
        if(depcode != null) {
            phoneNumber = depcode.getCode() + mPhoneNumberEdit.getText().toString().trim();
        }
        String password = mPasswordEdit.getText ().toString ().trim ();
        String codeID = mCodeEdit.getText ().toString ().trim ();
        // 加密之后的密码
        spUser.edit().putString("MYWORD",password).apply();

        if (TextUtils.isEmpty(thirdToken)) {
            // 第三方登录的不处理账号密码，
            if (TextUtils.isEmpty(phoneNumber) && TextUtils.isEmpty(password)) {
                Toast.makeText(mContext, getString(R.string.please_input_account_and_password), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(mContext, getString(R.string.please_input_account), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(mContext, getString(R.string.input_pass_word), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 加密之后的密码
        final String digestPwd = LoginPassword.encodeMd5(password);

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
                this, coreManager, String.valueOf ( mobilePrefix ), phoneNumber, password, captchaID, codeID, thirdToken, thirdTokenType, third,
                params,
                t -> {
                    DialogHelper.dismissProgressDialog();
                    String msg = t.getMessage();
                    if(msg.contains(":")){
                        String[] str = msg.split(":");
                        if (str != null && str.length == 2) {
                            msg = str[0];
                            captchaID = str[1];
                        }
                    }
                    if (!TextUtils.isEmpty ( captchaID ) && captchaID.length () == 32) {
                        getCodeImage ();
                        mCodeLinear.setVisibility ( View.VISIBLE );
                    }
                    SharedPreferences info = getSharedPreferences ( "info", MODE_PRIVATE );
                    SharedPreferences.Editor edit = info.edit ();
                    edit.putString ( "phoneNumber", phoneNumber );
                    edit.putString ( "password",password );
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
                        OperLogUtil.msg("登录数据"+ jsonObject.toJSONString());
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
                            OperLogUtil.msg("登录数据"+ "afterLogin" + phoneNumber + password);
                            afterLogin(objectResult, phoneNumber, password);
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
        if (third) {
            if (MyApplication.IS_SUPPORT_SECURE_CHAT
                    && result.getData().getIsSupportSecureChat() == 1) {// 新用户才需要，老用户不支持端到端加密，不需要
                // SecureFlag
                // 微信/QQ登录，如未绑定手机号码，则需要输入账号密码进行绑定登录，如账号未注册，走注册流程
                // 微信/QQ登录，如绑定手机号码，可直接登录，此时将因为不需要输入登录密码，将无法解密服务端返回的私钥，需要让用户输入密码解密
                mVerifyDialog = new VerifyDialog(mContext);
                mVerifyDialog.setVerifyClickListener(getString(R.string.input_password_to_decrypt_keys), new VerifyDialog.VerifyClickListener() {
                    @Override
                    public void cancel() {
                        mVerifyDialog.dismiss();
                        String sAreaCode = result.getData().getAreaCode();
                        String rTelephone = result.getData().getTelephone();
                        if (!TextUtils.isEmpty(rTelephone)) {
                            if (!TextUtils.isEmpty(sAreaCode) && rTelephone.startsWith(sAreaCode)) {
                                rTelephone = rTelephone.substring(sAreaCode.length());
                            }
                            FindPwdActivity.start(mContext, Integer.valueOf(sAreaCode), rTelephone);
                        } else {
                            startActivity(new Intent(mContext, FindPwdActivity.class));
                        }
                    }

                    @Override
                    public void send(String str) {
                        checkPasswordWXAuthCodeLogin(str, result, phoneNumber, digestPwd);
                    }
                });
                mVerifyDialog.setDismiss(false);
                mVerifyDialog.setCancelButton(R.string.forget_password);
                mVerifyDialog.show();
            } else {
                start(mPasswordEdit.getText().toString().trim(), result, phoneNumber, digestPwd);
            }
        } else {

            start(mPasswordEdit.getText().toString().trim(), result, phoneNumber, digestPwd);
        }
/*
        boolean success = LoginHelper.setLoginUser(mContext, coreManager, phoneNumber, digestPwd, result);
        if (success) {
            // SecureFlag 本地先保存明文登录密码，之后要使用
            if (third) {
                if (MyApplication.IS_SUPPORT_SECURE_CHAT
                        && result.getData().getIsSupportSecureChat() == 1) {// 新用户才需要，老用户不支持端到端加密，不需要
                    // 微信/QQ登录，如未绑定手机号码，则需要输入账号密码进行绑定登录，如账号未注册，走注册流程
                    // 微信/QQ登录，如绑定手机号码，可直接登录，此时将因为不需要输入登录密码，将无法解密服务端返回的私钥，需要让用户输入密码解密
                    mVerifyDialog = new VerifyDialog(mContext);
                    mVerifyDialog.setVerifyClickListener(getString(R.string.input_password_to_decrypt_keys), new VerifyDialog.VerifyClickListener() {
                        @Override
                        public void cancel() {
                            mVerifyDialog.dismiss();
                            startActivity(new Intent(mContext, FindPwdActivity.class));
                        }

                        @Override
                        public void send(String str) {
                            checkPasswordWXAuthCodeLogin(str, result);
                        }
                    });
                    mVerifyDialog.setDismiss(false);
                    mVerifyDialog.setCancelButton(R.string.forget_password);
                    mVerifyDialog.show();
                } else {
                    start("", result);
                }
            } else {
                start(mPasswordEdit.getText().toString().trim(), result);
            }
        } else {
            // 偶现该异常，退出app重进又没有了
            // java.sql.SQLException: Unable to run insert stmt on object com.oort.weichat.bean.User@d9c51ec: INSERT INTO
            // `user` (`account` ,`areaId` ,`attCount` ,`birthday` ,`cityId` ,`company_id` ,`countryId` ,`description` ,`fansCount` ,`friendsCount` ,`integral` ,`integralTotal` ,`isAuth` ,`level` ,`money` ,`moneyTotal` ,`msgBackGroundUrl` ,`nickName` ,`offlineTime` ,`password` ,`phone` ,`provinceId` ,`setAccountCount` ,`sex` ,`showLastLoginTime` ,`status` ,`telephone` ,`userId` ,`userType` ,`vip` )
            // VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ToastUtil.showToast(mContext, result.getResultMsg());
        }
*/
    }

    private void waitAuth(CheckAuthLoginRunnable authLogin) {
        authLogin.waitAuthHandler.postDelayed(authLogin, 3000);
    }

    private void checkPasswordWXAuthCodeLogin(String password, ObjectResult<LoginRegisterResult> registerResult,
                                              String extra1, String extra2) {

        LoginHelper.saveUserForThirdSmsVerifyPassword(mContext, coreManager,
                extra1, extra2, registerResult);

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("password", LoginPassword.encodeMd5(password));

        DialogHelper.showDefaulteMessageProgressDialog(mContext);

        HttpUtils.get().url(coreManager.getConfig().USER_VERIFY_PASSWORD)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (Result.checkSuccess(mContext, result)) {
                            mVerifyDialog.dismiss();
                            start(password, registerResult, extra1, extra2);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    private void start(String password, ObjectResult<LoginRegisterResult> result, String phoneNumber, String digestPwd) {

        if(password.length() < 0){
            AlertDialog alert = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        PrivacySettingHelper.setPrivacySettings(LoginActivity.this, settings);
        OperLogUtil.msg("登录完成初始化数据" + "setPrivacySettings");
        MyApplication.getInstance().initMulti();
        OperLogUtil.msg("登录完成初始化数据" + "initMulti");

        // startActivity(new Intent(mContext, DataDownloadActivity.class));
        //String token = result.getData().getAccessToken();
//        String uuid = result.getData().getO
//        if(result.getData().getAccessToken() != null && uuid != null){
//            AppStoreInit.store_token = "";
//            AppStoreInit.initData(token,uuid);
            DataInit.setCallBack(new DataInit.CallBack() {
                @Override
                public void requestAppsSuc() {
                    DataDownloadActivity.start(mContext, result.getData().getIsupdate(), password);
                    OperLogUtil.msg("获取app数据成功");
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    /*}*/
//                    startActivity(intent);
                    finish();
                }

                @Override
                public void requestAppsFail(String msg) {

                    OperLogUtil.msg("获取app数据失败");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    /*}*/
                    startActivity(intent);
                    finish();
                }
            });


    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SelectPrefixActivity.REQUEST_MOBILE_PREFIX_LOGIN:
                if (resultCode != SelectPrefixActivity.RESULT_MOBILE_PREFIX_SUCCESS) {
                    return;
                }
                mobilePrefix = data.getIntExtra(Constants.MOBILE_PREFIX, 86);
                tv_prefix.setText("+" + mobilePrefix);
                break;
            case com.tencent.connect.common.Constants.REQUEST_LOGIN:
            case com.tencent.connect.common.Constants.REQUEST_APPBAR:
                Tencent.onActivityResultData(requestCode, resultCode, data, QQHelper.getLoginListener(mContext));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageLogin message) {
        finish();
    }

    private class CheckAuthLoginRunnable implements Runnable {
        private final String phoneNumber;
        private final String digestPwd;
        private Handler waitAuthHandler = new Handler();
        private int waitAuthTimes = 10;
        private String authKey;

        public CheckAuthLoginRunnable(String authKey, String phoneNumber, String digestPwd) {
            this.authKey = authKey;
            this.phoneNumber = phoneNumber;
            this.digestPwd = digestPwd;
        }

        @Override
        public void run() {
            HttpUtils.get().url(coreManager.getConfig().CHECK_AUTH_LOGIN)
                    .params("authKey", authKey)
                    .build(true, true)
                    .execute(new BaseCallback<LoginRegisterResult>(LoginRegisterResult.class) {
                        @Override
                        public void onResponse(ObjectResult<LoginRegisterResult> result) {
                            if (Result.checkError(result, Result.CODE_AUTH_LOGIN_SCUESS)) {
                                DialogHelper.dismissProgressDialog();
                                ;
                            } else if (Result.checkError(result, Result.CODE_AUTH_LOGIN_FAILED_1)) {
                                waitAuth(CheckAuthLoginRunnable.this);
                            } else {
                                DialogHelper.dismissProgressDialog();
                                if (!TextUtils.isEmpty(result.getResultMsg())) {
                                    ToastUtil.showToast(mContext, result.getResultMsg());
                                } else {
                                    ToastUtil.showToast(mContext, R.string.tip_server_error);
                                }
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            DialogHelper.dismissProgressDialog();
                            ToastUtil.showErrorNet(mContext);
                        }
                    });
        }
    }

    private void getCodeImage() {
        Glide.with(mContext).load(Constant.CODE_IMAGE + captchaID + ".png")
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .into((mCodeImage));

    }

    /**
     * 验证登录
     */
    private void captchaLogin(){
        //13510219864
        final String phoneNumber = mPhoneNumberEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        String codeID = mCodeEdit.getText().toString().trim();

        // 加密之后的密码
        final String digestPwd = LoginPassword.encodeMd5(password);
        RequesManager.login(phoneNumber, password, captchaID, codeID).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                OperLogUtil.v("msg" , s);
                DialogHelper.dismissProgressDialog();
                if (TextUtils.isEmpty(s)) {
//                    ToastUtils.showShortSafe("登录失败！");
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    int code = jsonObject.getIntValue("code");
                    String msg = jsonObject.getString("msg");
                    ObjectResult<UserInfo> objectResult = new ObjectResult<>();
                    objectResult.setResultCode(code);
                    objectResult.setCurrentTime(jsonObject.getLongValue("currentTime"));
                    objectResult.setResultMsg(msg);

                    if(code == 200) {
                        JSONObject data = jsonObject.getJSONObject("data").getJSONObject("userInfo");
                        if (data != null) {
                            UserInfo realResult = JSON.parseObject(String.valueOf(data), UserInfo.class);
                            objectResult.setData(realResult);

                            FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);

                            sharedPreferences.edit().putString("login_response", s).apply();
                        }
                        mylogin();
                    }else{
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data != null) {
                            captchaID = data.getString("CaptchaID");
                            if (captchaID != null) {
                                getCodeImage();
                                mCodeLinear.setVisibility(View.VISIBLE);
                            }
                        }
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();


                    }
                }catch (Exception e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    OperLogUtil.e("login", "json error!");
                }
            }

            @Override
            public void onError(Throwable e) {

//                dismissDialog();
//                ToastUtils.showShortSafe(e.getMessage());
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        // 请求权限过程中离开了回来就再请求吧，
        ready();
    }
    private void ready() {

        // 检查 || 请求权限
        boolean hasAll = requestPermissions();
        if (hasAll) {// 已获得所有权限
            ReportInfo.sn = getmSeralNum();
            //获取或生成guid,需求获得文件读取权限之后操作
            try {
                CommonApplication.GUID = GuidUtil.createGUID(LoginActivity.this);
                OperLogUtil.e(TAG, "guid: " + CommonApplication.GUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            OperLogUtil.d("zlm",ReportInfo.sn);
            jump();
        }
    }

    @SuppressLint("NewApi")
    private void jump() {
        if (isDestroyed()) {
            return;
        }

        int userStatus = LoginHelper.prepareUser(mContext, coreManager);
        Intent intent = new Intent();
        switch (userStatus) {
            case LoginHelper.STATUS_USER_FULL:
            case LoginHelper.STATUS_USER_NO_UPDATE:
            case LoginHelper.STATUS_USER_TOKEN_OVERDUE:
                boolean login = PreferenceUtils.getBoolean(this, Constants.LOGIN_CONFLICT, false);
                if (login) {// 登录冲突，退出app再次进入，跳转至历史登录界面
                    intent.setClass(mContext, LoginHistoryActivity.class);
                } else {
                    intent.setClass(mContext, MainActivity.class);
                }
                break;
            case LoginHelper.STATUS_USER_SIMPLE_TELPHONE:
                intent.setClass(mContext, LoginHistoryActivity.class);
                break;
            case LoginHelper.STATUS_NO_USER:
            default:
                return;// must return
        }
        startActivity(intent);
        finish();
    }

    private boolean requestPermissions() {
        if (!TextUtils.isEmpty(coreManager.getConfig().privacyPolicyPrefix) &&
                !PreferenceUtils.getBoolean(this, Constants.PRIVACY_AGREE_STATUS, false)) {
            // 先同意隐私政策，
            PrivacyAgreeActivity.start(this);
            return false;
        } else {
            // 请求定位以外的权限，

           boolean isFirst = spUser.getBoolean("first_open",true);

           if(isFirst) {
               DialogRequestPermission per = new DialogRequestPermission(this, DialogRequestPermission.per_type_all);
               per.show();
               spUser.edit().putBoolean("first_open",false);
           }
            DialogRequestPermission per = new DialogRequestPermission(this,DialogRequestPermission.per_type_all);

            return true;
            //return requestPermissions(permissionsMap.keySet().toArray(new String[]{}));
        }
    }
    private boolean requestPermissions(String... permissions) {
        List<String> deniedPermission = PermissionUtil.getDeniedPermissions(this, permissions);
        if (deniedPermission != null) {
            PermissionExplainDialog tip = getPermissionExplainDialog();
            tip.setPermissions(deniedPermission.toArray(new String[0]));
            tip.setOnConfirmListener(() -> {
                PermissionUtil.requestPermissions(this, LoginActivity.REQUEST_CODE, permissions);
            });
            tip.show();
            return false;
        }
        return true;
    }
    private PermissionExplainDialog getPermissionExplainDialog() {
        if (permissionExplainDialog == null) {
            permissionExplainDialog = new PermissionExplainDialog(this);
        }
        return permissionExplainDialog;
    }

    private void requestOverlayPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!Settings.canDrawOverlays(this))
            {
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));

                this.startActivity(intent);
            }else {

            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            OperLogUtil.mPostionX = (int) ev.getX();
            OperLogUtil.mPostionY = (int) ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            OperLogUtil.mPostionX = (int) event.getX();
            OperLogUtil.mPostionY = (int) event.getY();
        }

        return super.onTouchEvent(event);
    }

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
                        HeadInfo.headParams.put("userName",URLEncoder.encode(xm,"UTF-8"));

                        OperLogUtil.userName =xm;
                        OperLogUtil.sfzh = sfzh;
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
                        mPhoneNumberEdit.setText(xfzh);
                        loginBtn.setEnabled(true);
                        loginBtn.setBackground(getResources().getDrawable(R.drawable.ql_l_g_bg_ripple));

                    } catch (Exception e) {
                        ld.loadFailed();
                        e.printStackTrace();
                        OperLogUtil.e(TAG, e.getMessage());
                    }

                } else {
//                    showSimpleDialog(response.getMessage());
                    ld.loadFailed();
                }
            }

            @Override
            public void authCanel() {//取消的回调
//                showSimpleDialog("server canel success.");
                ld.loadFailed();
            }
        });
    }

    private void callClient() {
        Response response = client.call();
        if (response == null){
            ld.loadFailed();
            PopupDialog.create(LoginActivity.this, "提示", "请先启动吉安宝", "确定",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginActivity.this.finish();
                        }
                    }, null,
                    null, false,
                    false, true).show();
            return;

        }
//        STATUS = response.getResultCode();
//        String message = response.getMessage();
//
//        OperLogUtil.e(TAG, "callClient response :" + response);
        /*if( -9 == STATUS) {
            //吉安宝未启动
//            OperLogUtil.e(TAG, "吉安宝未启动");
//            showSimpleDialog("吉安宝未启动,请先启动吉安宝再登录");
            Intent intent = new Intent();
            intent.setAction(message);
//            intent.putExtras(params);
            mContext.startActivity(intent);

        }*/

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
                                mPhoneNumberEdit.setText(xfzh);
                                loginBtn.setEnabled(true);
                                loginBtn.setBackground(getResources().getDrawable(R.drawable.ql_l_g_bg_ripple));
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

                        OperLogUtil.sfzh = sfzh;
                        OperLogUtil.userName = xm;
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
                        mPhoneNumberEdit.setText(xfzh);
                        loginBtn.setEnabled(true);
                        loginBtn.setBackground(getResources().getDrawable(R.drawable.ql_l_g_bg_ripple));
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
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(mIntent);
                            finish();
                        }
                    }, 1000);*/
                }else{
                    ld.loadFailed();
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);*/
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

}
