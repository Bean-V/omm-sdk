package com.oort.weichat.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
//import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.bean.Code;
import com.oort.weichat.bean.event.MessageLogin;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.helper.LoginSecureHelper;
import com.oort.weichat.helper.PasswordHelper;
import com.oort.weichat.helper.UsernameHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.EventBusHelper;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.secure.LoginPassword;
import com.oort.weichat.R;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.bean.CaptchaInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.me.bean.Result;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import okhttp3.Call;

/**
 * 注册-1.输入手机号
 */
public class RegisterActivity extends BaseActivity {
    public static final String EXTRA_AUTH_CODE = "auth_code";
    public static final String EXTRA_PHONE_NUMBER = "phone_number";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_SMS_CODE = "sms_code";
    public static final String EXTRA_INVITE_CODE = "invite_code";
    public static int isSmsRegister = 0;
    private EditText mPhoneNumEdit;
    private EditText mPassEdit;
    private EditText mInviteCodeEdit;
    private EditText mImageCodeEdit;
    private ImageView mImageCodeIv;
    private ImageView mRefreshIv;
    private EditText mAuthCodeEdit;
    private Button mSendAgainBtn;
    private Button mNextStepBtn;
    private Button mNoAuthCodeBtn;
    private TextView tv_prefix;
    private int mobilePrefix = 86;
    private String mRandCode;
    private int reckonTime = 60;
    private String thirdToken;
    private String thirdTokenType;
    // 短信码是否已经发送，启用时必须发送了才能下一步，
    private boolean mSmsSent;
    private boolean privacyAgree = true;
    private Handler mReckonHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                mSendAgainBtn.setText(reckonTime + " " + "S");
                if (reckonTime == 30) {
                    // 剩下30秒时显示收不到验证码按钮，
                    if (AppConfig.isShiku()) {
                        // 30秒后可以跳过验证码功能不在定制版生效，
//                        mNoAuthCodeBtn.setVisibility(View.VISIBLE);
                    }
                }
                reckonTime--;
                if (reckonTime < 0) {
                    mReckonHandler.sendEmptyMessage(0x2);
                } else {
                    mReckonHandler.sendEmptyMessageDelayed(0x1, 1000);
                }
            } else if (msg.what == 0x2) {
//                requestImageCode();
                getCaptcha();
                // 60秒结束
                mSendAgainBtn.setText(getString(R.string.send));
                mSendAgainBtn.setEnabled(true);
                reckonTime = 60;
            }
        }
    };

    private CaptchaInfo mCaptchaInfo;
    public RegisterActivity() {
        noLoginRequired();
    }

    public static void registerFromThird(Context ctx, int mobilePrefix, String phone, String password, String thirdToken, String thirdTokenType) {
        Intent intent = new Intent(ctx, RegisterActivity.class);
        intent.putExtra("mobilePrefix", mobilePrefix);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        intent.putExtra("thirdToken", thirdToken);
        intent.putExtra("thirdTokenType", thirdTokenType);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mobilePrefix = getIntent().getIntExtra("mobilePrefix", 86);
        thirdToken = getIntent().getStringExtra("thirdToken");
        thirdTokenType = getIntent().getStringExtra("thirdTokenType");
        initActionBar();
        initView();
        initEvent();
        EventBusHelper.register(this);
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.register_account));
    }

    private void initView() {
        if (TextUtils.isEmpty(thirdToken)) {
            findViewById(R.id.btnBindOldAccount).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btnBindOldAccount).setOnClickListener(v -> {
                LoginActivity.bindThird(this, thirdToken, thirdTokenType);
            });
        }
        mPhoneNumEdit = (EditText) findViewById(R.id.phone_numer_edit);
        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(phone)) {
            mPhoneNumEdit.setText(phone);
        }
        tv_prefix = (TextView) findViewById(R.id.tv_prefix);
        tv_prefix.setText("+" + mobilePrefix);
        mPassEdit = (EditText) findViewById(R.id.password_edit);
        PasswordHelper.bindPasswordEye(mPassEdit, findViewById(R.id.tbEye));
        String password = getIntent().getStringExtra("password");
        if (!TextUtils.isEmpty(password)) {
            mPassEdit.setText(password);
        }
        mInviteCodeEdit = (EditText) findViewById(R.id.etInvitationCode);
        mImageCodeEdit = (EditText) findViewById(R.id.image_tv);
        mImageCodeIv = (ImageView) findViewById(R.id.image_iv);
        mRefreshIv = (ImageView) findViewById(R.id.image_iv_refresh);
        mAuthCodeEdit = (EditText) findViewById(R.id.auth_code_edit);
        mSendAgainBtn = (Button) findViewById(R.id.send_again_btn);
        mNextStepBtn = (Button) findViewById(R.id.next_step_btn);
        mNoAuthCodeBtn = (Button) findViewById(R.id.go_no_auth_code);

        UsernameHelper.initEditText(mPhoneNumEdit, false);

        if (coreManager.getConfig().registerInviteCode > 0) {
            // 启用邀请码，
            findViewById(R.id.llInvitationCode).setVisibility(View.VISIBLE);
        }

        if (coreManager.getConfig().registerUsername) {
            tv_prefix.setVisibility(View.GONE);
        } else if (coreManager.getConfig().isOpenSMSCode) {// 启用短信验证码
            findViewById(R.id.iv_code_ll).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_code_view).setVisibility(View.VISIBLE);
            findViewById(R.id.auth_code_ll).setVisibility(View.VISIBLE);
            findViewById(R.id.auth_code_view).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.main_content).setOnClickListener(v -> {
            // 点击空白区域隐藏软键盘
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(findViewById(R.id.main_content).getWindowToken(), 0); //强制隐藏键盘
            }
        });

        if (MyApplication.IS_SUPPORT_SECURE_CHAT) {
            findViewById(R.id.tip_tv).setVisibility(View.VISIBLE);
        }
        getCaptcha();
    }

    /**
     * 请求图形验证码
     */
    private void requestImageCode() {
        if (isFinishing()) {
            // 可能是http回调到这里的，可能activity已经销毁，不再继续，
            return;
        }
        if (coreManager.getConfig().registerUsername || !coreManager.getConfig().isOpenSMSCode) {
            // 用户名注册或者没开启验证码，就不请求图形码，
            return;
        }
        if (TextUtils.isEmpty(mPhoneNumEdit.getText().toString())) {
            ToastUtil.showToast(mContext, getString(R.string.tip_no_phone_number_get_v_code));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("telephone", mobilePrefix + mPhoneNumEdit.getText().toString().trim());
        String url = HttpUtils.get().url(coreManager.getConfig().USER_GETCODE_IMAGE)
                .params(params)
                .buildUrl();
        ImageLoadHelper.loadBitmapWithoutCache(
                mContext,
                url,
                b -> {
                    mImageCodeIv.setImageBitmap(b);
                }, e -> {
                    Toast.makeText(RegisterActivity.this, R.string.tip_verification_code_load_failed, Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void verifyPhoneNumber(String phoneNumber, final Runnable onSuccess) {
        if (!UsernameHelper.verify(this, phoneNumber, coreManager.getConfig().registerUsername)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("telephone", phoneNumber);
        params.put("areaCode", "" + mobilePrefix);

        Log.v("msg"  , coreManager.getConfig().VERIFY_TELEPHONE);
        HttpUtils.get().url(coreManager.getConfig().VERIFY_TELEPHONE)
                .params(params)
                .build(true, true)
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result == null) {
                            ToastUtil.showToast(RegisterActivity.this,
                                    R.string.data_exception);
                            return;
                        }

                        if (result.getResultCode() == 1) {
                            onSuccess.run();
                        } else {
                            onSuccess.run();
                            requestImageCode();
                            // 手机号已经被注册
//                            if (!TextUtils.isEmpty(result.getResultMsg())) {
//                                ToastUtil.showToast(RegisterActivity.this,
//                                        result.getResultMsg());
//                            } else {
//                                ToastUtil.showToast(RegisterActivity.this,
//                                        R.string.tip_server_error);
//                            }

                        }

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(RegisterActivity.this);
                    }
                });
    }

    /**
     * 跳过短信验证码到下一步，
     */
    private void nextStepWithOutAuthCode(final String phoneStr, final String passStr) {
        verifyPhoneNumber(phoneStr, new Runnable() {
            @Override
            public void run() {
                realNextStep(phoneStr, passStr);
            }
        });
    }

    private void realNextStep(String phoneStr, String passStr) {
        if (coreManager.getConfig().registerInviteCode == 1
                && TextUtils.isEmpty(mInviteCodeEdit.getText())) {
            ToastUtil.showToast(mContext, getString(R.string.tip_invite_code_empty));
            return;
        }

        RegisterUserBasicInfoActivity.start(
                this,
                "" + mobilePrefix,
                phoneStr,
                LoginPassword.encodeMd5(passStr),
                mAuthCodeEdit.getText().toString().trim(),
                mInviteCodeEdit.getText().toString(),
                thirdToken,
                thirdTokenType,
                passStr
        );
        // 不需要结束，登录后通过EventBus消息结束这些，
        // finish();
    }

    /**
     * 验证手机是否注册
     */
    private void verifyPhoneIsRegistered(final String phoneStr, final String imageCodeStr) {
        verifyPhoneNumber(phoneStr, new Runnable() {
            @Override
            public void run() {
//                requestAuthCode(phoneStr, imageCodeStr);
                sendsmscode(imageCodeStr , phoneStr);
            }
        });
    }

    /**
     * 请求验证码
     */
    private void requestAuthCode(String phoneStr, String imageCodeStr) {
        Map<String, String> params = new HashMap<>();
        String language = Locale.getDefault().getLanguage();
        params.put("language", language);
        params.put("areaCode", String.valueOf(mobilePrefix));
        params.put("telephone", phoneStr);
        params.put("imgCode", imageCodeStr);
        params.put("isRegister", String.valueOf(1));
        params.put("version", "1");

        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().SEND_AUTH_CODE)
                .params(params)
                .build(true, true)
                .execute(new BaseCallback<Code>(Code.class) {

                    @Override
                    public void onResponse(ObjectResult<Code> result) {
                        DialogHelper.dismissProgressDialog();
                        Log.v("msg" , result.toString());
                        if (result.getResultCode() == 1) {
                            mSmsSent = true;
                            if (result.getData() != null && result.getData().getCode() != null) {
                                Log.e(TAG, "onResponse: " + result.getData().getCode());
                                mRandCode = result.getData().getCode();// 记录验证码
                            }
                            mSendAgainBtn.setEnabled(false);
                            // 开始倒计时
                            mReckonHandler.sendEmptyMessage(0x1);
                        } else {
                            if (!TextUtils.isEmpty(result.getResultMsg())) {
                                ToastUtil.showToast(RegisterActivity.this,
                                        result.getResultMsg());
                            } else {
                                ToastUtil.showToast(RegisterActivity.this,
                                        getString(R.string.tip_server_error));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(mContext);
                    }
                });
    }

    private void initEvent() {
        mPhoneNumEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // 注册页面手机号输入完成后自动刷新验证码，
                    // 只在移开焦点，也就是点击其他EditText时调用，
                    requestImageCode();
                }
            }
        });
        mPhoneNumEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 手机号码修改时让图形验证码和短信验证码失效，
                // 每输入一个字符调用一次，
                mRandCode = null;
                mImageCodeEdit.setText("");
                mAuthCodeEdit.setText("");
            }
        });

        tv_prefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SelectPrefixActivity.class);
                startActivityForResult(intent, SelectPrefixActivity.REQUEST_MOBILE_PREFIX_LOGIN);
            }
        });

        mRefreshIv.setOnClickListener(new View.OnClickListener() {// 刷新图形码
            @Override
            public void onClick(View v) {
//                requestImageCode();
                getCaptcha();
            }
        });
        mNoAuthCodeBtn.setOnClickListener(new View.OnClickListener() {// 刷新图形码
            @Override
            public void onClick(View v) {
                // 不检查验证码就前往下一步，
                nextStepWithoutAuthCode();
            }
        });

        mSendAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPhoneStr = mPhoneNumEdit.getText().toString().trim();
                String mPassStr = mPassEdit.getText().toString().trim();
                if(!isValidMobiNumber(mPhoneStr)){
                    ToastUtil.showToast(mContext, "请输入正确的手机号");
                    return ;
                }
                if (checkInput(mPhoneStr, mPassStr))
                    return;
//                if (isOrderChar(mPassStr , 3)) {
//                    ToastUtil.showToast(mContext, "密码的连续字符(包含数字和字母)不能超过3位");
//                    return ;
//                }
                String mImageCodeStr = mImageCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mImageCodeStr)) {
                    ToastUtil.showToast(mContext, getString(R.string.tip_verification_code_empty));
                    return;
                }

                // 验证手机号是否注册
                verifyPhoneIsRegistered(mPhoneStr, mImageCodeStr);

            }
        });

        // 注册
        mNextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putInt(RegisterActivity.this, Constants.AREA_CODE_KEY, mobilePrefix);

//                if (!coreManager.getConfig().registerUsername && coreManager.getConfig().isOpenSMSCode) {
//                    nextStep();
//                } else {
//                    nextStepWithoutAuthCode();
//                }

                register();
            }
        });
    }

    /**
     * 验证验证码
     */
    private void nextStep() {
        String mPhoneStr = mPhoneNumEdit.getText().toString().trim();
        String mPassStr = mPassEdit.getText().toString().trim();
        if (checkInput(mPhoneStr, mPassStr))
            return;
        String mAuthCodeStr = mAuthCodeEdit.getText().toString().trim();
        if (TextUtils.isEmpty(mAuthCodeStr)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_auth_code));
            return;
        }

        isSmsRegister = 1;
        if (!TextUtils.isEmpty(mRandCode)) {
            if (mAuthCodeStr.equals(mRandCode)) {// 验证码正确,进入填写资料页面
                realNextStep(mPhoneStr, mPassStr);
            } else {
                // 验证码错误
                Toast.makeText(this, R.string.auth_code_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!mSmsSent) {
                ToastUtil.showToast(mContext, getString(R.string.please_send_sms_code));
                return;
            }
            // 没有短信码的情况，可能没有检查手机号是否注册，
            verifyPhoneNumber(mPhoneStr, new Runnable() {
                @Override
                public void run() {
                    realNextStep(mPhoneStr, mPassStr);
                }
            });
        }
    }

    private void nextStepWithoutAuthCode() {
        String mPhoneStr = mPhoneNumEdit.getText().toString().trim();
        String mPassStr = mPassEdit.getText().toString().trim();
        if (checkInput(mPhoneStr, mPassStr))
            return;
        nextStepWithOutAuthCode(mPhoneStr, mPassStr);
    }

    /**
     * 检查是否需要停止注册，
     *
     * @return 测试不合法返回true, 停止继续注册，
     */
    private boolean checkInput(String mPhoneStr, String mPassStr) {
        if (!privacyAgree) {
            ToastUtil.showToast(mContext, R.string.tip_privacy_not_agree);
            return true;
        }
        if (!UsernameHelper.verify(this, mPhoneStr, coreManager.getConfig().registerUsername)) {
            return true;
        }
        if (TextUtils.isEmpty(mPassStr)) {
            ToastUtil.showToast(mContext, getString(R.string.tip_password_empty));
            return true;
        }
        if (mPassStr.length() < 8) {
            ToastUtil.showToast(mContext, getString(R.string.tip_password_too_short));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        if (resultCode != SelectPrefixActivity.RESULT_MOBILE_PREFIX_SUCCESS)
            return;
        mobilePrefix = data.getIntExtra(Constants.MOBILE_PREFIX, 86);
        tv_prefix.setText("+" + mobilePrefix);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageLogin message) {
        finish();
    }

    /**
     * 检查是否需要验证码
     */
    private void getCaptcha(){
        RequesManager.getCaptcha("reg").subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                Result<CaptchaInfo> result =  new Gson().fromJson(s , new TypeToken<Result<CaptchaInfo>>(){}.getType());
                Log.v("msg" , s );
                if (result.isOk()){
                    mCaptchaInfo = result.getData();
                    getCaptchaCode();
                }

            }
        });
    }

    /**
     * 获取图形验证码图片
     */
    private void getCaptchaCode(){
        if (mCaptchaInfo != null){
            Glide.with(mContext).load(Constant.CODE_IMAGE+mCaptchaInfo.getCaptchaID()+".png")
                    .signature(new ObjectKey(mCaptchaInfo.getCaptchaID()))
                    .into(mImageCodeIv);
        }

    }


    /**
     * 检查是否需要验证码
     */
    private void sendsmscode(String code , String phone){
        if (mCaptchaInfo != null){
            RequesManager.sendsmscode(mCaptchaInfo.getCaptchaID() , code , phone).subscribe(new RxBus.BusObserver<String>(){
                @Override
                public void onNext(String s) {
                    Log.v("msg" ,s);
                    Result<CaptchaInfo> result =  new Gson().fromJson(s , new TypeToken<Result<CaptchaInfo>>(){}.getType());
                   if (result.isOk()){
                       mSendAgainBtn.setEnabled(false);
                       // 开始倒计时
                       mReckonHandler.sendEmptyMessage(0x1);
                   }
//                   else if (result.getCode() == 4091){
//                       ToastUtil.showToast(mContext, result.getMsg());
//                   }
                   else if (result.getCode() == 4083){
                       ToastUtil.showToast(mContext, "获取验证码太频繁,间隔时间为60秒");
                   }
                   else if (result.getCode() == 4010){
                       ToastUtil.showToast(mContext, result.getMsg());
                   }
                   else {
                       ToastUtil.showToast(mContext, result.getMsg());
                       getCaptchaCode();
                   }
                }
            });
        }

    }

    /**
     * 注册
     */
    private void register(){
        String mPhoneStr = mPhoneNumEdit.getText().toString().trim();
        String mPassStr = mPassEdit.getText().toString().trim();
        String mImageCodeStr = mImageCodeEdit.getText().toString().trim();
        String mAuthCodeStr = mAuthCodeEdit.getText().toString().trim();
        if (TextUtils.isEmpty(mPhoneStr)) {
            ToastUtil.showToast(mContext, getString(R.string.tip_phone_number_empty));
            return ;
        }
        if(!isValidMobiNumber(mPhoneStr)){
            ToastUtil.showToast(mContext, "请输入正确的手机号");
            return ;
        }
        if (TextUtils.isEmpty(mPassStr)) {
            ToastUtil.showToast(mContext, getString(R.string.tip_password_empty));
            return ;
        }
        if (mPassStr.length() < 8) {
            ToastUtil.showToast(mContext, getString(R.string.tip_password_too_short));
            return ;
        }
//        if (isOrderChar(mPassStr , 3)) {
//            ToastUtil.showToast(mContext, "密码的连续字符(包含数字和字母)不能超过3位");
//            return ;
//        }
        if (TextUtils.isEmpty(mImageCodeStr)) {
            ToastUtil.showToast(mContext, getString(R.string.tip_verification_code_empty));
            return;
        }
        if (TextUtils.isEmpty(mAuthCodeStr)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_auth_code));
            return;
        }

        RequesManager.register(mPhoneStr , mPassStr , mAuthCodeStr).subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                Log.v("msg" ,s);
                Result result =  new Gson().fromJson(s , new TypeToken<Result>(){}.getType());
                if (result.isOk()){
                    ToastUtil.showToast(mContext, "注册成功");
//                    realNextStep(mPhoneStr, mPassStr);
                    finish();
//                    LoginSecureHelper.autoLogin();
                }else {
                    ToastUtil.showToast(mContext, result.getMsg());
                }
            }
        });
    }

    /**
     * 是否存在连续 i 位顺序字符
     * @param str
     * @param i 存在 i 位顺序字符，i 应大于等于1
     * @return 如果 i 位顺序的字符，则返回 true，否则返回 false
     */
    private boolean isOrderChar(String str, int i) {
        char[] charArr = str.toCharArray();
        int len = charArr.length;
        int count = 0;
        int t = charArr[0];
        for (int j = 1; j < len; j++) {
            if ((t + 1) == charArr[j]) {
                count ++;
                if (count == i) {
                    return true;
                }
            } else {
                count = 0;
            }
            t = charArr[j];
        }
        return false;
    }

    public static boolean isValidMobiNumber(String paramString) {
        String regex = "^1\\d{10}$";
        if (paramString.matches(regex)) {
            return true;
        }
        return false;
    }
}
