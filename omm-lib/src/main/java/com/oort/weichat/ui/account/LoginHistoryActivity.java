package com.oort.weichat.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.LoginRegisterResult;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.event.MessageLogin;
import com.oort.weichat.db.dao.UserDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.helper.LoginSecureHelper;
import com.oort.weichat.helper.PasswordHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.helper.YeepayHelper;
import com.oort.weichat.sp.UserSp;
import com.oort.weichat.ui.base.ActivityStack;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.tool.ButtonColorChange;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.EventBusHelper;
import com.oort.weichat.util.Md5Util;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.secure.LoginPassword;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.UUID;

import okhttp3.Call;

/**
 * 历史登陆界面
 */

public class LoginHistoryActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mAvatarImgView;
    private TextView mNickNameTv;
    private EditText mPasswordEdit;
    private int mobilePrefix = 86;
    private User mLastLoginUser;

    private LinearLayout mCaptchaLl;
    private EditText mCaptchaEt;
    private ImageView mCaptchaImg;
    private String mCaptcha;
//    private LinearLayout mProvingCodeLl;
//    private String captchaID;
//    private EditText mProvingCode;
//    private ImageView mProvingCodeImg;

    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    public LoginHistoryActivity() {
        noLoginRequired();
    }

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, LoginHistoryActivity.class);
        // 清空activity栈，
        // 重建期间白屏，暂且放弃，
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_history);
        PreferenceUtils.putBoolean(this, Constants.LOGIN_CONFLICT, false);// 重置登录冲突记录
        String userId = UserSp.getInstance(this).getUserId("");
        mLastLoginUser = UserDao.getInstance().getUserByUserId(userId);
        if (!LoginHelper.isUserValidation(mLastLoginUser)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        initActionBar();
        initView();
        EventBusHelper.register(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityStack.getInstance().exit();
    }

    private void initActionBar() {
        getSupportActionBar().hide();


        findViewById(R.id.iv_title_left).setVisibility(View.GONE);
        TextView tv1 = (TextView) findViewById(R.id.tv_title_left);
        TextView tv2 = (TextView) findViewById(R.id.tv_title_right);
        tv1.setText(R.string.app_name);
        tv2.setText(R.string.switch_account);
        tv2.setOnClickListener(v -> {

            LoginActivity.isAdmin = true;
            Intent intent = new Intent(LoginHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
        });

       // tv2.setVisibility(View.GONE);

        tv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                tv2.setVisibility(tv2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void initView() {
        mAvatarImgView = (ImageView) findViewById(R.id.avatar_img);
        mNickNameTv = (TextView) findViewById(R.id.nick_name_tv);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        mPasswordEdit.setText(spUser.getString("MYWORD",""));
        PasswordHelper.bindPasswordEye(mPasswordEdit, findViewById(R.id.tbEye));
        mobilePrefix = PreferenceUtils.getInt(this, Constants.AREA_CODE_KEY, mobilePrefix);
        Button loginBtn, registerBtn, forgetPasswordBtn;
        loginBtn = (Button) findViewById(R.id.login_btn);
        ButtonColorChange.colorChange(this, loginBtn);
        loginBtn.setOnClickListener(this);
        registerBtn = (Button) findViewById(R.id.register_account_btn);
        registerBtn.setOnClickListener(this);
        if (coreManager.getConfig().isOpenRegister) {
            registerBtn.setVisibility(View.VISIBLE);
        } else {
            registerBtn.setVisibility(View.GONE);
        }
        forgetPasswordBtn = (Button) findViewById(R.id.forget_password_btn);
        if (coreManager.getConfig().registerUsername) {
            forgetPasswordBtn.setVisibility(View.GONE);
        } else {
            forgetPasswordBtn.setOnClickListener(this);
        }
        loginBtn.setText(getString(R.string.login));
        registerBtn.setText(getString(R.string.register_account));
        forgetPasswordBtn.setText(getString(R.string.forget_password));

        try {

            AvatarHelper.getInstance().displayRoundAvatar(mLastLoginUser.getNickName(), mLastLoginUser.getUserId(), mAvatarImgView, true);
            mNickNameTv.setText(mLastLoginUser.getNickName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCaptchaLl = findViewById(R.id.captcha_ll);
        mCaptchaEt = findViewById(R.id.captcha_et);
        mCaptchaImg = findViewById(R.id.captcha_img);
        findViewById(R.id.captcha_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            login();
//                mylogin();
//                captchaLogin();
        } else if (id == R.id.register_account_btn) {
            startActivity(new Intent(LoginHistoryActivity.this, RegisterActivity.class));
        } else if (id == R.id.forget_password_btn) {
            startActivity(new Intent(this, FindPwdActivity.class));
        } else if (id == R.id.captcha_refresh) {
            if (!TextUtils.isEmpty(mCaptcha)) {
                getCodeImage();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageLogin message) {
        finish();
    }

    private void mylogin(){
        String phoneNumber = mLastLoginUser.getTelephoneNoAreaCode();
        String password = mPasswordEdit.getText().toString().trim();
        String codeID = mCaptchaEt.getText().toString().trim();
        spUser.edit().putString("MYWORD",password).apply();
        final String digestPwd = new String(Md5Util.toMD5(password));
        DialogHelper.showDefaulteMessageProgressDialog(this);
        RequesManager.login(phoneNumber, password, mCaptcha, codeID).subscribe(new RxBus.BusObserver<String>(){

            @Override
            public void onNext(String s) {
                Log.v("msg" , s);
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
                        if (data != null){
                            mCaptcha= data.getString("CaptchaID");
                            if (mCaptcha != null){
                                getCodeImage();
                                mCaptchaLl.setVisibility(View.VISIBLE);
                            }
                        }
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("login", "json error!");
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                DialogHelper.dismissProgressDialog();
//                dismissDialog();
//                ToastUtils.showShortSafe(e.getMessage());
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
    private void login() {
        String phoneNumber = mLastLoginUser.getLoginItem();//getTelephoneNoAreaCode();
        String password = mPasswordEdit.getText().toString().trim();
        String codeID = mCaptchaEt.getText().toString().trim();
        spUser.edit().putString("MYWORD",password).apply();
        PreferenceUtils.putInt(this, Constants.AREA_CODE_KEY, mobilePrefix);
        if (TextUtils.isEmpty(password)) {
            return;
        }
        final String digestPwd = LoginPassword.encodeMd5(password);

        DialogHelper.showDefaulteMessageProgressDialog(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("xmppVersion", "1");
        // 附加信息
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

        LoginSecureHelper.mySecureLogin(
                this, coreManager, String.valueOf(mobilePrefix), phoneNumber, password, mCaptcha, codeID,
                params,
                (t) -> {
                    DialogHelper.dismissProgressDialog();
                    String msg = t.getMessage();
                    if(msg.contains(":")){
                      String[] str = msg.split(":");
                      if (str != null && str.length == 2) {
                          msg = str[0];
                          mCaptcha = str[1];
                      }
                    }
                    if (!TextUtils.isEmpty(mCaptcha) && mCaptcha.length() == 32){
                        getCodeImage();
                        mCaptchaLl.setVisibility(View.VISIBLE);
                    }
                    ToastUtil.showToast(this, this.getString(R.string.tip_login_secure_place_holder, msg));
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

                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

                        }
                    }catch (Exception e){
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("login", "json error!");
                    }
                });
    }

    private void afterLogin(ObjectResult<LoginRegisterResult> result, String phoneNumber, String digestPwd) {
        boolean success = LoginHelper.setLoginUser(mContext, coreManager, phoneNumber, digestPwd, result);
        if (success) {
            LoginRegisterResult.Settings settings = result.getData().getSettings();
            MyApplication.getInstance().initPayPassword(result.getData().getUserId(), result.getData().getPayPassword());
            YeepayHelper.saveOpened(mContext, result.getData().getWalletUserNo() == 1);
            PrivacySettingHelper.setPrivacySettings(LoginHistoryActivity.this, settings);
            MyApplication.getInstance().initMulti();

            // 登陆成功
            DataDownloadActivity.start(mContext, result.getData().getIsupdate(), mPasswordEdit.getText().toString().trim());
            finish();
        } else {
            // 登录失败
            String message = TextUtils.isEmpty(result.getResultMsg()) ? getString(R.string.login_failed) : result.getResultMsg();
            ToastUtil.showToast(mContext, message);
        }
    }

    private void waitAuth(CheckAuthLoginRunnable authLogin) {
        authLogin.waitAuthHandler.postDelayed(authLogin, 3000);
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
                            if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkError(result, com.xuan.xuanhttplibrary.okhttp.result.Result.CODE_AUTH_LOGIN_SCUESS)) {
                                DialogHelper.dismissProgressDialog();
                                login();
                            } else if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkError(result, com.xuan.xuanhttplibrary.okhttp.result.Result.CODE_AUTH_LOGIN_FAILED_1)) {
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

    private void getCodeImage(){
        Glide.with(mContext).load(Constant.CODE_IMAGE + mCaptcha + ".png")
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .into((mCaptchaImg));

    }

    /**
     * 验证登录
     */
    private void captchaLogin(){
        //13510219864
        final String phoneNumber = mLastLoginUser.getTelephoneNoAreaCode();
        String password = mPasswordEdit.getText().toString().trim();
        String codeID = mCaptchaEt.getText().toString().trim();

        // 加密之后的密码
        final String digestPwd = LoginPassword.encodeMd5(password);
        RequesManager.login(phoneNumber, password, mCaptcha, codeID).subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                Log.v("msg" , s);
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
                        if (data != null){
                            mCaptcha= data.getString("CaptchaID");
                            if (mCaptcha != null){
                                getCodeImage();
                                mCaptchaLl.setVisibility(View.VISIBLE);
                            }
                        }
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();


                    }
                }catch (Exception e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("login", "json error!");
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

}
