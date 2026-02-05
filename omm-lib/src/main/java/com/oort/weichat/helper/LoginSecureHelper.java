package com.oort.weichat.helper;

import static com.oort.weichat.AppConfig.apiKey;
import static com.oortcloud.basemodule.constant.Constant.AUTOLOGIN_API;
import static com.oortcloud.basemodule.constant.Constant.BASE_URL;
import static com.oortcloud.basemodule.constant.Constant.GET_LOGIN_CODE;
import static com.oortcloud.basemodule.constant.Constant.NEW_LOGIN;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.EncryptedData;
import com.oort.weichat.bean.LoginAuto;
import com.oort.weichat.bean.LoginCode;
import com.oort.weichat.bean.LoginRegisterResult;
import com.oort.weichat.bean.LoginSsosign;
import com.oort.weichat.bean.PayPrivateKey;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.event.MessageUpdate;
import com.oort.weichat.db.dao.UserDao;
import com.oort.weichat.sp.UserSp;
import com.oort.weichat.ui.account.LoginActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.log.LogUtils;
import com.oort.weichat.util.secure.AES;
import com.oort.weichat.util.secure.LoginPassword;
import com.oort.weichat.util.secure.MAC;
import com.oort.weichat.util.secure.MD5;
import com.oort.weichat.util.secure.Parameter;
import com.oort.weichat.util.secure.RSA;
import com.oort.weichat.wxapi.WXHelper;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.im.AppUseInfo;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.utils.RSAUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.mdm.PolicyManager;
import okhttp3.Call;

/**
 * 登录加固工具类，
 * <p>
 * 线程比较混乱，主要是http回调在主线程，因此可能存在部分加密操作在主线程执行，应改为异步线程，
 */
public class LoginSecureHelper {
    private static final String TAG = "LoginSecureHelper";
    // 多点登录相关，一种设备只能有一个在登录，
    private static final String DEVICE_ID = "android";
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean logged = false;
    private static FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");

    private static String mloginKey;
    private static String maccess_token;

    /**
     * 封装普通接口mac验参，
     */
    public static void generateHttpParam(
            Context ctx,
            Map<String, String> params,
            Boolean beforeLogin
    ) {
        if (params.containsKey("secret")) {
            UserSp sp = UserSp.getInstance(MyApplication.getContext());
            String accessToken = sp.getAccessToken();
            if (!TextUtils.isEmpty(accessToken)) {
                params.put("access_token", accessToken);
            }
            return;
        }
        if (beforeLogin) {
            generateBeforeLoginParam(ctx, params);
            return;
        }
        CoreManager coreManager = CoreManager.getInstance(ctx);
        if (coreManager.getSelf() == null) {
            generateBeforeLoginParam(ctx, params);
            return;
        }
        String userId = coreManager.getSelf().getUserId();
        UserSp sp = UserSp.getInstance(ctx);
        String accessToken = sp.getAccessToken();
        if (accessToken == null) {
            generateBeforeLoginParam(ctx, params);
            return;
        }
        String httpKey = sp.getHttpKey();
        if (httpKey == null) {
            generateBeforeLoginParam(ctx, params);
            return;
        }
        String salt = params.remove("salt");
        if (salt == null) {
            salt = String.valueOf(System.currentTimeMillis());
        }
        // 旧代码手动添加的accessToken无视，
        params.remove("access_token");
        String macContent = apiKey + userId + accessToken + Parameter.joinValues(params) + salt;
        String mac = MAC.encodeBase64(macContent.getBytes(), Base64.decode(httpKey));
        params.put("access_token", accessToken);
        params.put("salt", salt);
        params.put("secret", mac);
    }

    private static void generateBeforeLoginParam(Context ctx, Map<String, String> params) {
        String salt = params.remove("salt");
        if (salt == null) {
            salt = String.valueOf(System.currentTimeMillis());
        }
        String macContent = apiKey + Parameter.joinValues(params) + salt;
        byte[] key = MD5.encrypt(apiKey);
        String mac = MAC.encodeBase64(macContent.getBytes(), key);
        params.put("salt", salt);
        params.put("secret", mac);
    }

    /**
     * 只用于登录后免调用自动登录接口，
     * 密码登录后有拿到自动登录返回的token和key就可以不调自动登录了，
     */
    public static void setLogged() {
        logged = true;
    }

    @MainThread
    public static void myAutoLogintest(
            Context ctx,
            CoreManager coreManager,
            Function<Throwable> onError,
            Runnable onSuccess
    ) {
        if (logged) {
            LogUtils.e("HTTP", "跳过自动登录");
            onSuccess.run();
            return;
        }
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("自动登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            User user = coreManager.getSelf();
            String phoneNumber = user.getTelephoneNoAreaCode();
            String password = user.getPassword();
            String userId = user.getUserId();
            String token = spUser.getString("token","");
            ReportInfo.name = user.getNickName();
            ReportInfo.phone = phoneNumber;
            ReportInfo.interphone = phoneNumber;
            ReportInfo.police_id = userId;
            ReportInfo.unit = "测试单位";

//            DialogHelper.showDefaulteMessageProgressDialog(ctx);
            RequesManager.login(phoneNumber,password).subscribe(new RxBus.BusObserver<String>(){
//            RequesManager.autologin(token,userId).subscribe(new RxBus.BusObserver<String>(){

                @Override
                public void onNext(String s) {
                    Log.v("autologin" , s);
//                    DialogHelper.dismissProgressDialog();
                    if (TextUtils.isEmpty(s)) {
//                    ToastUtils.showShortSafe("登录失败！");
                        Toast.makeText(ctx, "自动登录失败", Toast.LENGTH_SHORT).show();
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
                            }
                            afterLogin(objectResult, phoneNumber, password,ctx,coreManager,onSuccess);
                        }else{
                            Log.e("autologin", msg);
//                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("autologin", e.getMessage());
//                        Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
//                    DialogHelper.dismissProgressDialog();
//                dismissDialog();
//                ToastUtils.showShortSafe(e.getMessage());
                    Log.e("autologin", e.getMessage());
//                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {

                }
            });




        });
    }

    private static void afterLogin(ObjectResult<LoginRegisterResult> objectResult, String phoneNumber, String password, Context ctx, CoreManager coreManager, Runnable onSuccess) {
        boolean success = LoginHelper.setLoginUser(ctx, coreManager, phoneNumber, password, objectResult);
        if (success) {
            onSuccess.run();
        } else { //  登录出错 || 用户资料不全
            String message = objectResult.getResultMsg();
            ToastUtil.showToast(ctx, message);
        }
    }


    @MainThread
    public static void autoLogin(
            Context ctx,
            CoreManager coreManager,
            Function<Throwable> onError,
            Runnable onSuccess
    ) {
        if (logged) {
            LogUtils.e("HTTP", "跳过自动登录");
            onSuccess.run();
            return;
        }
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("自动登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            User user = coreManager.getSelf();
            String userId = user.getUserId();
            Map<String, String> params = new HashMap<>();
            params.put("serial", DeviceInfoUtil.getDeviceId(ctx));

            ReportInfo.name = user.getNickName();
            ReportInfo.phone = user.getTelephoneNoAreaCode();
            ReportInfo.interphone = user.getTelephoneNoAreaCode();
            ReportInfo.police_id = userId;
            ReportInfo.unit = "测试单位";

            // 地址信息
            double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
            double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
            if (latitude != 0)
                params.put("latitude", String.valueOf(latitude));
            if (longitude != 0)
                params.put("longitude", String.valueOf(longitude));

            if (MyApplication.IS_OPEN_CLUSTER) {// 服务端集群需要
                String area = PreferenceUtils.getString(MyApplication.getContext(), AppConstant.EXTRA_CLUSTER_AREA);
                if (!TextUtils.isEmpty(area)) {
                    params.put("area", area);
                }
            }
            LoginSecureHelper.generateAutoLoginParam(
                    ctx, userId,
                    params, t -> {
                        c.uiThread(r -> {
                            onError.apply(t);
                        });
                    },
                    (data, loginToken, loginKeyData, salt) -> {
                        Map<String, String> p = new HashMap<>();
                        p.put("salt", salt);
                        p.put("loginToken", loginToken);
                        p.put("data", data);
                        HttpUtils.get().url(coreManager.getConfig().USER_LOGIN_AUTO)
                                .params(p)
                                .build(true, true)
                                .executeSync(new BaseCallback<EncryptedData>(EncryptedData.class, false) {
                                    @Override
                                    public void onResponse(ObjectResult<EncryptedData> result) {
                                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null && result.getData().getData() != null) {
                                            String realData = LoginSecureHelper.decodeAutoLoginResult(loginKeyData, result.getData().getData());
                                            LoginAuto loginAuto = JSON.parseObject(realData, LoginAuto.class);
                                            UserSp.getInstance(ctx).saveAutoLoginResult(loginAuto);

                                            String accessToken = loginAuto.getAccessToken();
                                            UserSp.getInstance(ctx).setAccessToken(accessToken);

                                            user.setRole(loginAuto.getRole());
                                            user.setMyInviteCode(loginAuto.getMyInviteCode());
                                            UserDao.getInstance().saveUserLogin(user);
                                            MyApplication.getInstance().initPayPassword(user.getUserId(), loginAuto.getPayPassword());
                                            YeepayHelper.saveOpened(ctx, loginAuto.getWalletUserNo() == 1);
                                            PrivacySettingHelper.setPrivacySettings(MyApplication.getContext(), loginAuto.getSettings());
                                            MyApplication.getInstance().initMulti();

                                            c.uiThread(r -> {
                                                onSuccess.run();
                                            });
                                        } else {
                                            c.uiThread(r -> {
                                                if (Result.checkError(result, Result.CODE_LOGIN_TOKEN_INVALID)) {
                                                    onError.apply(new LoginTokenOvertimeException(Result.getErrorMessage(ctx, result)));
                                                } else {
                                                    onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Exception e) {
                                        onError.apply(e);
                                    }
                                });
                    });
        });
    }

    @MainThread
    public static void myAutoLogin(
            Context ctx,
            CoreManager coreManager,
            Function<Throwable> onError,
            Runnable onSuccess
    ) {
        if (logged) {
            Log.e("login-step", "myAutoLogin-1");
            UserInfo userInfo = UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo();
            //初始化应用市场安装应用列表信息
            if (userInfo != null){
                //初始化用户信息
                ReportInfo.depart_code = userInfo.getOort_depcode();
                ReportInfo.depart_name = userInfo.getOort_depname();
                ReportInfo.unit = userInfo.getOort_depname();
                ReportInfo.photo = userInfo.getOort_photo();
                ReportInfo.police_id = userInfo.getOort_code();
                if (userInfo.getOort_postname().isEmpty()){
                    ReportInfo.position = "无";
                }else {
                    ReportInfo.position = userInfo.getOort_postname();
                }
                ReportInfo.police_type = userInfo.getOort_policetype();
                ReportInfo.oort_uuid = userInfo.getOort_uuid();
                //初始化完成发送检查版本消息
                EventBus.getDefault().post(new MessageUpdate(true));
                //
//                AppStoreInit.initData(accessToken , UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo().getOort_uuid());
            }
            onSuccess.run();
            return;
        }
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("自动登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            Log.e("login-step", "executor-2");
            User user = coreManager.getSelf();
            String userId = user.getUserId();
            Map<String, String> params = new HashMap<>();
            params.put("serial", DeviceInfoUtil.getDeviceId(ctx));

            ReportInfo.name = user.getNickName();
            ReportInfo.phone = user.getTelephoneNoAreaCode();
            ReportInfo.interphone = user.getTelephoneNoAreaCode();
            ReportInfo.police_id = userId;

            // 地址信息
            double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
            double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
            if (latitude != 0)
                params.put("latitude", String.valueOf(latitude));
            if (longitude != 0)
                params.put("longitude", String.valueOf(longitude));

            if (MyApplication.IS_OPEN_CLUSTER) {// 服务端集群需要
                String area = PreferenceUtils.getString(MyApplication.getContext(), AppConstant.EXTRA_CLUSTER_AREA);
                if (!TextUtils.isEmpty(area)) {
                    params.put("area", area);
                }
            }
            LoginSecureHelper.generateAutoLoginParam(
                    ctx, userId,
                    params, t -> {
                        c.uiThread(r -> {
                            onError.apply(t);
                        });
                    },
                    (data, loginToken, loginKeyData, salt) -> {
                        Log.e("login-step", "executor-3");
                        Map<String, String> p = new HashMap<>();
                        p.put("salt", salt);
                        p.put("loginToken", loginToken);
                        p.put("data", data);

                        p.put("language",Locale.getDefault().getLanguage());
//                        p.put("secret",secret);

                        Map<String, String> param = new HashMap<>();
                        param.put("apiKey",apiKey);
                        param.put("access_token",maccess_token);
                        param.put("loginToken",loginToken);
                        param.put("loginKey",mloginKey);
                        param.put("client","android");

                        String json = new Gson().toJson(param);
                        //加密后的密文
                        String userInfo = null;
                        try {
                            String str = RSAUtils.encryptRSA(json);
                            userInfo = str.replace("\n", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        p.put("userInfo",userInfo);
                        HttpUtils.get().url(BASE_URL + AUTOLOGIN_API)
                                .params(p)
                                .build(true, true)
                                .executeSync(new BaseCallback<EncryptedData>(EncryptedData.class, false) {
                                    @Override
                                    public void onResponse(ObjectResult<EncryptedData> result) {
                                        Log.e("login-step", "executor-4");

                                        LogUtils.e("main","result：" + result.toString());
                                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null && result.getData().getData() != null) {
                                            String realData = LoginSecureHelper.decodeAutoLoginResult(loginKeyData, result.getData().getData());

                                            LogUtils.e("main","realData：" + realData);
                                            LoginAuto loginAuto = JSON.parseObject(realData, LoginAuto.class);
                                            UserSp.getInstance(ctx).saveAutoLoginResult(loginAuto);

                                            String accessToken = loginAuto.getAccessToken();
                                            UserSp.getInstance(ctx).setAccessToken(accessToken);
                                            //保存token
                                            spUser.edit().putString("token",accessToken).apply();
                                            ReportInfo.accessToken = accessToken;
                                            //自动登录成功后需要更新AppUseinfo的token
                                            AppUseInfo.accessToken = accessToken;
                                            //获取用户信息及初始化
                                            LogUtils.e("main","initUserInfoAndData");
                                                RequesManager.initUserInfoAndData(realData, accessToken, "", new RequesManager.Callback() {
                                                    @Override
                                                    public boolean sucessCallBack() {

                                                        LogUtils.e("main","sucessCallBack");
                                                        user.setRole(loginAuto.getRole());
                                                        user.setMyInviteCode(loginAuto.getMyInviteCode());
                                                        LogUtils.e("main","saveUserLogin");
                                                        UserDao.getInstance().saveUserLogin(user);
                                                        MyApplication.getInstance().initPayPassword(user.getUserId(), loginAuto.getPayPassword());
                                                        LogUtils.e("main","saveOpened");
                                                        YeepayHelper.saveOpened(ctx, loginAuto.getWalletUserNo() == 1);
                                                        LogUtils.e("main","setPrivacySettings");
                                                        PrivacySettingHelper.setPrivacySettings(MyApplication.getContext(), loginAuto.getSettings());
                                                        LogUtils.e("main","initMulti");
                                                        MyApplication.getInstance().initMulti();

                                                        LogUtils.e("main","onSuccessRun");

                                                        c.uiThread(r -> {
                                                            onSuccess.run();
                                                        });
                                                        return false;
                                                    }
                                                });


                                        } else {
                                            c.uiThread(r -> {
                                                if (Result.checkError(result, Result.CODE_LOGIN_TOKEN_INVALID)) {
                                                    LogUtils.e("main","LoginTokenOvertimeException");
                                                    onError.apply(new LoginTokenOvertimeException(Result.getErrorMessage(ctx, result)));
                                                } else {
                                                    LogUtils.e("main","IllegalStateException");
                                                    onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Exception e) {
                                        onError.apply(e);
                                    }
                                });
                    });
        });
    }

    @WorkerThread
    public static void generateAutoLoginParam(
            Context ctx, String userId,
            Map<String, String> params,
            Function<Throwable> onError,
            Function4<String, String, byte[], String> onSuccess) {
        UserSp sp = UserSp.getInstance(ctx);
        String loginToken = sp.getLoginToken();
        String loginKey = sp.getLoginKey();
        mloginKey = loginKey;
        maccess_token = sp.getAccessToken();

        if (TextUtils.isEmpty(loginToken) || TextUtils.isEmpty(loginKey)) {
            onError.apply(new IllegalStateException("本地没有登录信息"));
            return;
        }
        byte[] loginKeyData = Base64.decode(loginKey);
        String salt = createSalt();
        String mac = MAC.encodeBase64((apiKey + userId + loginToken + Parameter.joinValues(params) + salt).getBytes(), loginKeyData);
        JSONObject json = new JSONObject();
        json.putAll(params);
        json.put("mac", mac);
        String data = AES.encryptBase64(json.toJSONString(), Base64.decode(loginKey));
        onSuccess.apply(data, loginToken, loginKeyData, salt);
    }

    public static String decodeAutoLoginResult(byte[] loginKeyData, String data) {
        try {
            String ret = AES.decryptStringFromBase64(data, loginKeyData);
            LogUtils.e("HTTP", "autoLogin data: " + ret);
            return ret;
        } catch (Exception e) {
            Reporter.post("登录结果解密失败", e);
            return data;
        }
    }

    private static String decodeLoginResult(byte[] code, String data) {
        try {
            String ret = AES.decryptStringFromBase64(data, code);
            LogUtils.e("HTTP", "login data: " + ret);
            return ret;
        } catch (Exception e) {
            Reporter.post("登录结果解密失败", e);
            return data;
        }
    }

    private static void thirdLogin(
            Context ctx, CoreManager coreManager,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("第三方登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            String url = coreManager.getConfig().USER_THIRD_LOGIN;
            String salt = createSalt();
            byte[] code = MD5.encrypt(apiKey);
            String mac = MAC.encodeBase64((apiKey + Parameter.joinValues(params) + salt).getBytes(), code);
            JSONObject json = new JSONObject();
            json.putAll(params);
            json.put("mac", mac);
            String data = AES.encryptBase64(json.toJSONString(), code);
            Map<String, String> p = new HashMap<>();
            p.put("data", data);
            p.put("salt", salt);
            login(ctx, url, code, p, t -> {
                Log.i(TAG, "登录失败", t);
                c.uiThread(r -> {
                    onError.apply(t);
                });
            }, result -> {
                c.uiThread(r -> {
                    onSuccess.apply(result);
                });
            });
        });
    }

    public static void secureRegister(
            Context ctx, CoreManager coreManager,
            String thirdToken, String thirdTokenType,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        String tUrl = coreManager.getConfig().USER_REGISTER;
        if (!TextUtils.isEmpty(thirdToken)) {
            params.put("type", thirdTokenType);
            params.put("loginInfo", WXHelper.parseOpenId(thirdToken));
            tUrl = coreManager.getConfig().USER_THIRD_REGISTER;
        }
        final String url = tUrl;
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("第三方登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            String salt = createSalt();
            byte[] code = MD5.encrypt(apiKey);
            String mac = MAC.encodeBase64((apiKey + Parameter.joinValues(params) + salt).getBytes(), code);
            JSONObject json = new JSONObject();
            json.putAll(params);
            json.put("mac", mac);
            String data = AES.encryptBase64(json.toJSONString(), code);
            Map<String, String> p = new HashMap<>();
            p.put("data", data);
            p.put("salt", salt);
            login(ctx, url, code, p, t -> {
                Log.i(TAG, "登录失败", t);
                c.uiThread(r -> {
                    onError.apply(t);
                });
            }, result -> {
                c.uiThread(r -> {
                    onSuccess.apply(result);
                });
            });
        });
    }

    /**
     * 配置登录加密的参数，
     * <p>
     * 成功失败的回调都是在主线程调用的，
     *
     * @param account 不带区号的手机号，或者其他，手机号输入框里输入的其他登录号，
     * @param onError 确保失败时回调，不能出现既不成功也不失败的情况，因为外面可能有对话框等着关闭，
     */
    public static void mySecureLogin(
            Context ctx, CoreManager coreManager, String areaCode, String account, String loginPassword,String captchaID,String code,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        mySecureLogin(ctx, coreManager, areaCode, account, loginPassword,captchaID,code, null, null, false, params, onError, onSuccess);
    }

    /**
     * 配置登录加密的参数，
     * <p>
     * 成功失败的回调都是在主线程调用的，
     *
     * @param account 不带区号的手机号，或者其他，手机号输入框里输入的其他登录号，
     * @param onError 确保失败时回调，不能出现既不成功也不失败的情况，因为外面可能有对话框等着关闭，
     */
    public static void secureLogin(
            Context ctx, CoreManager coreManager, String areaCode, String account, String loginPassword,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        secureLogin(ctx, coreManager, areaCode, account, loginPassword, null, null, false, params, onError, onSuccess);
    }

    /**
     * 配置登录加密的参数，
     * <p>
     * 成功失败的回调都是在主线程调用的，
     *
     * @param account 不带区号的手机号，或者其他，手机号输入框里输入的其他登录号，
     * @param onError 确保失败时回调，不能出现既不成功也不失败的情况，因为外面可能有对话框等着关闭，
     */
    public static void mySecureLogin(
            Context ctx, CoreManager coreManager, String areaCode, String account, String loginPassword,String captchaID,String code,
            String thirdToken, String thirdTokenType, boolean thirdAutoLogin,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        String tUrl = coreManager.getConfig().USER_LOGIN;
        if (!TextUtils.isEmpty(thirdToken)) {
            params.put("type", thirdTokenType);
            if (TextUtils.equals(LoginActivity.THIRD_TYPE_WECHAT, thirdTokenType)) {
                params.put("loginInfo", WXHelper.parseOpenId(thirdToken));
            } else if (TextUtils.equals(LoginActivity.THIRD_TYPE_QQ, thirdTokenType)) {
                params.put("loginInfo", QQHelper.parseOpenId(thirdToken));
            } else {
                throw new IllegalStateException("unknown type: " + thirdTokenType);
            }
            tUrl = coreManager.getConfig().USER_THIRD_BIND_LOGIN;
        }
        if (thirdAutoLogin) {
            thirdLogin(ctx, coreManager,
                    params, onError, onSuccess);
            return;
        }
        final String url = BASE_URL + NEW_LOGIN;
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("登录失败", t);
            OperLogUtil.msg("登录失败"+ t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            byte[] key = LoginPassword.encode(loginPassword);
            myGetCode(ctx, coreManager, areaCode, account,key, loginPassword,captchaID,code, (t -> {
                Log.i(TAG, "获取code失败", t);
                OperLogUtil.msg("获取code失败"+ t);
                c.uiThread(r -> {
                    onError.apply(t);
                });
            }), (encryptedCode, userId,ssodata) -> {
                // 到这里说明登录密码正确，
                getRsaPrivateKey(ctx, coreManager, userId, key, t -> {
                    Log.i(TAG, "获取登录私钥失败", t);
                    OperLogUtil.msg("获取登录私钥失败"+ t);
                    c.uiThread(r -> {
                        onError.apply(t);
                    });
                }, new Function<byte[]>() {
                    @Override
                    public void apply(byte[] privateKey) {
                        byte[] code;
                        try {
                            code = RSA.decryptFromBase64(encryptedCode, privateKey);
                        } catch (Exception e) {
                            OperLogUtil.msg("私钥解密code失败"+ e);
                            Log.i(TAG, "私钥解密code失败", e);
                            requestPrivateKey(ctx, coreManager, userId, key, onError, this);
                            return;
                        }
                        String loginPasswordMd5 = LoginPassword.md5(key);
                        String salt = createSalt();
                        String mac = MAC.encodeBase64((apiKey + userId + Parameter.joinValues(params) + salt + loginPasswordMd5).getBytes(), code);
                        JSONObject json = new JSONObject();
                        json.putAll(params);
                        json.put("mac", mac);
                        String data = AES.encryptBase64(json.toJSONString(), code);
                        Map<String, String> p = new HashMap<>();
                        p.put("data", data);
                        p.put("userId", userId);
                        p.put("salt", salt);
                        p.put("ssosign",ssodata);
                        String rsacode = "";
                        String mycode = Base64.encode(code);
                        try {
                            String str = RSAUtils.encryptRSA(mycode);
                            rsacode = str.replace("\n", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        p.put("code",rsacode);
//                        Log.e("test basecode",mycode);
//                        Log.e("test rsacode",rsacode);
                        login(ctx, url, code, p, t -> {
                            OperLogUtil.msg("登录失败"+ t);
                            Log.i(TAG, "登录失败", t);
                            c.uiThread(r -> {
                                onError.apply(t);
                            });
                        }, result -> {
                            OperLogUtil.msg("登录成功" + result);
                            c.uiThread(r -> {
                                onSuccess.apply(result);
                            });
                        });
                    }
                });
            });
        });
    }


    /**
     * 配置登录加密的参数，
     * <p>
     * 成功失败的回调都是在主线程调用的，
     *
     * @param account 不带区号的手机号，或者其他，手机号输入框里输入的其他登录号，
     * @param onError 确保失败时回调，不能出现既不成功也不失败的情况，因为外面可能有对话框等着关闭，
     */
    public static void secureLogin(
            Context ctx, CoreManager coreManager, String areaCode, String account, String loginPassword,
            String thirdToken, String thirdTokenType, boolean thirdAutoLogin,
            Map<String, String> params,
            Function<Throwable> onError,
            Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        String tUrl = coreManager.getConfig().USER_LOGIN;
        if (!TextUtils.isEmpty(thirdToken)) {
            params.put("type", thirdTokenType);
            if (TextUtils.equals(LoginActivity.THIRD_TYPE_WECHAT, thirdTokenType)) {
                params.put("loginInfo", WXHelper.parseOpenId(thirdToken));
            } else if (TextUtils.equals(LoginActivity.THIRD_TYPE_QQ, thirdTokenType)) {
                params.put("loginInfo", QQHelper.parseOpenId(thirdToken));
            } else {
                throw new IllegalStateException("unknown type: " + thirdTokenType);
            }
            tUrl = coreManager.getConfig().USER_THIRD_BIND_LOGIN;
        }
        if (thirdAutoLogin) {
            thirdLogin(ctx, coreManager,
                    params, onError, onSuccess);
            return;
        }
        final String url = tUrl;
        AsyncUtils.doAsync(ctx, t -> {
            Reporter.post("登录失败", t);
            AsyncUtils.runOnUiThread(ctx, c -> {
                onError.apply(t);
            });
        }, executor, c -> {
            byte[] key = LoginPassword.encode(loginPassword);
            getCode(ctx, coreManager, areaCode, account, key, t -> {
                Log.i(TAG, "获取code失败", t);
                c.uiThread(r -> {
                    onError.apply(t);
                });
            }, (encryptedCode, userId) -> {
                // 到这里说明登录密码正确，
                getRsaPrivateKey(ctx, coreManager, userId, key, t -> {
                    Log.i(TAG, "获取登录私钥失败", t);
                    c.uiThread(r -> {
                        onError.apply(t);
                    });
                }, new Function<byte[]>() {
                    @Override
                    public void apply(byte[] privateKey) {
                        byte[] code;
                        try {
                            code = RSA.decryptFromBase64(encryptedCode, privateKey);
                        } catch (Exception e) {
                            Log.i(TAG, "私钥解密code失败", e);
                            requestPrivateKey(ctx, coreManager, userId, key, onError, this);
                            return;
                        }
                        String loginPasswordMd5 = LoginPassword.md5(key);
                        String salt = createSalt();
                        String mac = MAC.encodeBase64((apiKey + userId + Parameter.joinValues(params) + salt + loginPasswordMd5).getBytes(), code);
                        JSONObject json = new JSONObject();
                        json.putAll(params);
                        json.put("mac", mac);
                        String data = AES.encryptBase64(json.toJSONString(), code);
                        Map<String, String> p = new HashMap<>();
                        p.put("data", data);
                        p.put("userId", userId);
                        p.put("salt", salt);
                        login(ctx, url, code, p, t -> {
                            Log.i(TAG, "登录失败", t);
                            c.uiThread(r -> {
                                onError.apply(t);
                            });
                        }, result -> {
                            c.uiThread(r -> {
                                onSuccess.apply(result);
                            });
                        });
                    }
                });
            });
        });
    }

    public static void smsLogin(Context ctx, CoreManager coreManager, String smsCode, String areaCode, String telephone, Map<String, String> params, Function<Throwable> onError, Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        byte[] smsKey = MD5.encrypt(smsCode);
        String salt = createSalt();
        String mac = MAC.encodeBase64((apiKey + areaCode + telephone + Parameter.joinValues(params) + salt).getBytes(), smsKey);
        params.put("mac", mac);
        String data = AES.encryptBase64(JSON.toJSONString(params), smsKey);
        params = new HashMap<>();
        params.put("salt", salt);
        params.put("data", data);
        params.put("deviceId", DEVICE_ID);
        params.put("areaCode", areaCode);
        params.put("account", telephone);
        HttpUtils.get().url(coreManager.getConfig().USER_SMS_LOGIN)
                .params(params)
                .build(true, true)
                .execute(new BaseCallback<EncryptedData>(EncryptedData.class) {
                    @Override
                    public void onResponse(ObjectResult<EncryptedData> result) {
                        ObjectResult<LoginRegisterResult> objectResult = new ObjectResult<>();
                        objectResult.setCurrentTime(result.getCurrentTime());
                        objectResult.setResultCode(result.getResultCode());
                        objectResult.setResultMsg(result.getResultMsg());
                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null && result.getData().getData() != null) {
                            String realData = LoginSecureHelper.decodeLoginResult(smsKey, result.getData().getData());
                            if (realData != null) {
                                LoginRegisterResult realResult = JSON.parseObject(realData, LoginRegisterResult.class);
                                objectResult.setData(realResult);
                            }
                        }
                        onSuccess.apply(objectResult);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });
    }

    private static void login(Context ctx, String url, byte[] code, Map<String, String> p, Function<Throwable> onError, Function<ObjectResult<LoginRegisterResult>> onSuccess) {
        p.put("deviceId", DEVICE_ID);
        HttpUtils.get().url(url)
                .params(p)
                .build(true, true)
                .executeSync(new BaseCallback<EncryptedData>(EncryptedData.class, false) {
                    @Override
                    public void onResponse(ObjectResult<EncryptedData> result) {
//                        Log.e("test result",result.getData().getData());
                        ObjectResult<LoginRegisterResult> objectResult = new ObjectResult<>();
                        objectResult.setCurrentTime(result.getCurrentTime());
                        objectResult.setResultCode(result.getResultCode());
                        objectResult.setResultMsg(result.getResultMsg());
                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null && result.getData().getData() != null) {
                            String realData = LoginSecureHelper.decodeLoginResult(code, result.getData().getData());
                            if (realData != null) {
                                LoginRegisterResult realResult = JSON.parseObject(realData, LoginRegisterResult.class);
                                objectResult.setData(realResult);



//                                RequesManager.initUserInfoAndData(JSONObject.parseObject(String.valueOf(result)).getString("data"), realResult.getAccessToken(), "", new RequesManager.Callback() {
//                                    @Override
//                                    public boolean sucessCallBack() {
//
//
//                                       return false;
//                                    }
//                                });

                            }
                        }
                        onSuccess.apply(objectResult);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });
    }

    @NonNull
    private static String createSalt() {
        return String.valueOf(System.currentTimeMillis());
    }

    @WorkerThread
    private static void myGetCode(Context ctx, CoreManager coreManager, String areaCode, String account, byte[] key,String password,String captchaID,String code, Function<Throwable> onError, Function3<String, String,String> onSuccess) {
        String salt = createSalt();
        final Map<String, Object> params = new HashMap<>();
        params.put("username", account);
        params.put("password", password);
        params.put("timestamp", getSecondTimestampTwo());
        params.put("captchaID", captchaID);
        params.put("code", code);
        params.put("client", "android");
        params.put("apiKey", apiKey);
        params.put("areaCode", areaCode);
        params.put("language", Locale.getDefault().getLanguage());


        if(false) {

            com.oortcloud.basemodule.utils.LogUtils.log("ddddddddddd" + params.toString());
            PolicyManager mPolicyManager = PolicyManager.getInstance();
            String[] infos = mPolicyManager.getDeviceInfo();

            params.put("imsi1", StringUtil.getParamsString(infos[13]));
            params.put("imsi2", StringUtil.getParamsString(infos[14]));
            params.put("imei1", StringUtil.getParamsString(infos[0]));
            params.put("imei2", StringUtil.getParamsString(infos[1]));


            if(infos[0].contains("/")) {
                params.put("meid1", infos[0].split("/")[1]);
            }else {
                params.put("meid1","");
            }
            if(infos[1].contains("/")) {
                params.put("meid2", infos[1].split("/")[1]);
            }else {
                params.put("meid2","");
            }
        }


        com.oortcloud.basemodule.utils.LogUtils.log("cccccccc" + params.toString());
        String json = new Gson().toJson(params);
        //加密后的密文
        String userInfo_key = null;

        try {
            String str = RSAUtils.encryptRSA(json);
            userInfo_key = str.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //body
        HashMap<String, String> map = new HashMap<>();
        map.put("userInfo", userInfo_key);

        HttpUtils.post().url(BASE_URL + GET_LOGIN_CODE)
                .params(map)
                .build(true, true)
                .executeSync(new BaseCallback<LoginSsosign>(LoginSsosign.class, false) {

                    @Override
                    public void onResponse(ObjectResult<LoginSsosign> result) {
                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null) {
                            String userId = result.getData().getUserId();
                            if (result.getData() == null || TextUtils.isEmpty(result.getData().getCode())) {
                                // 服务器没有公钥，创建一对上传后从新调用getCode,
                                makeRsaKeyPair(ctx, coreManager, userId, key, onError, privateKey -> {
                                    myGetCode(ctx, coreManager, areaCode, account, key,password,captchaID,code, onError, onSuccess);
                                });
                            } else {
                                onSuccess.apply(result.getData().getCode(), userId,result.getData().getSsosign());
                            }
                        } else {
                            if (result.getData() != null && !TextUtils.isEmpty(result.getData().getCaptchaID())){
                                String msg = Result.getErrorMessage(ctx, result);
                                String captchaid = result.getData().getCaptchaID();
                                String res = msg + ":"+ captchaid;
                                onError.apply(new IllegalStateException(res));
                            }else {
                                onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });

    }

    @WorkerThread
    private static void getCode(Context ctx, CoreManager coreManager, String areaCode, String account, byte[] key, Function<Throwable> onError, Function2<String, String> onSuccess) {
        String loginPasswordMd5 = LoginPassword.md5(key);
        String salt = createSalt();
        String mac = MAC.encodeBase64((apiKey + areaCode + account + salt).getBytes(), loginPasswordMd5);
        final Map<String, String> params = new HashMap<>();
        params.put("areaCode", areaCode);
        params.put("account", account);
        params.put("mac", mac);
        params.put("salt", salt);
        params.put("deviceId", DEVICE_ID);

        HttpUtils.post().url(coreManager.getConfig().LOGIN_SECURE_GET_CODE)
                .params(params)
                .build(true, true)
                .executeSync(new BaseCallback<LoginCode>(LoginCode.class, false) {

                    @Override
                    public void onResponse(ObjectResult<LoginCode> result) {
                        if (Result.checkSuccess(ctx, result, false) && result.getData() != null) {
                            String userId = result.getData().getUserId();
                            if (result.getData() == null || TextUtils.isEmpty(result.getData().getCode())) {
                                // 服务器没有公钥，创建一对上传后从新调用getCode,
                                makeRsaKeyPair(ctx, coreManager, userId, key, onError, privateKey -> {
                                    getCode(ctx, coreManager, areaCode, account, key, onError, onSuccess);
                                });
                            } else {
                                onSuccess.apply(result.getData().getCode(), userId);
                            }
                        } else {
                            onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });

    }

    private static void getRsaPrivateKey(Context ctx, CoreManager coreManager, String userId, byte[] key, Function<Throwable> onError, Function<byte[]> onSuccess) {
        // 本地不保存密码登录私钥，每次都通过接口获取，
        requestPrivateKey(ctx, coreManager, userId, key, onError, onSuccess);
    }

    @WorkerThread
    private static void requestPrivateKey(Context ctx, CoreManager coreManager, String userId, byte[] key, Function<Throwable> onError, Function<byte[]> onSuccess) {
        String loginPasswordMd5 = LoginPassword.md5(key);
        String salt = createSalt();
        String mac = MAC.encodeBase64((apiKey + userId + salt).getBytes(), loginPasswordMd5);
        final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("mac", mac);
        params.put("salt", salt);

        HttpUtils.post().url(coreManager.getConfig().LOGIN_SECURE_GET_PRIVATE_KEY)
                .params(params)
                .build(true, true)
                .executeSync(new BaseCallback<PayPrivateKey>(PayPrivateKey.class, false) {

                    @Override
                    public void onResponse(ObjectResult<PayPrivateKey> result) {
                        if (Result.checkSuccess(ctx, result, false)) {
                            String encryptedPrivateKey;
                            if (result.getData() != null && !TextUtils.isEmpty(encryptedPrivateKey = result.getData().getPrivateKey())) {
                                byte[] privateKey;
                                try {
                                    privateKey = AES.decryptFromBase64(encryptedPrivateKey, key);
                                } catch (Exception e) {
                                    // 解密失败，登录密码错误，
                                    onError.apply(new IllegalArgumentException(ctx.getString(R.string.tip_wrong_pay_password)));
                                    return;
                                }
                                onSuccess.apply(privateKey);
                            } else {
                                // 走到这里说明服务器返回了公钥加密的code, 不可能没有私钥，
                                onError.apply(new IllegalStateException(ctx.getString(R.string.tip_server_error)));
                            }
                        } else {
                            onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });
    }

    @WorkerThread
    private static void makeRsaKeyPair(Context ctx, CoreManager coreManager, String userId,
                                       byte[] key, Function<Throwable> onError, Function<byte[]> onSuccess) {
        String salt = createSalt();
        RSA.RsaKeyPair rsaKeyPair = RSA.genKeyPair();
        String encryptedPrivateKeyBase64 = AES.encryptBase64(rsaKeyPair.getPrivateKey(), key);
        String publicKeyBase64 = rsaKeyPair.getPublicKeyBase64();
        String macKey = LoginPassword.md5(key);
        String macContent = apiKey + userId + encryptedPrivateKeyBase64 + publicKeyBase64 + salt;
        String mac = MAC.encodeBase64(macContent.getBytes(), macKey);
        final Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("publicKey", publicKeyBase64);
        params.put("privateKey", encryptedPrivateKeyBase64);
        params.put("salt", salt);
        params.put("mac", mac);

        HttpUtils.post().url(coreManager.getConfig().LOGIN_SECURE_UPLOAD_KEY)
                .params(params)
                .build(true, true)
                .executeSync(new BaseCallback<Void>(Void.class, false) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (Result.checkSuccess(ctx, result, false)) {
                            onSuccess.apply(rsaKeyPair.getPrivateKey());
                        } else {
                            onError.apply(new IllegalStateException(Result.getErrorMessage(ctx, result)));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onError.apply(e);
                    }
                });
    }

    public interface Function<T> {
        void apply(T t);
    }

    public interface Function2<T, R> {
        void apply(T t, R r);
    }

    public interface Function3<T, R, E> {
        void apply(T t, R r, E e);
    }

    public interface Function4<T, R, E, W> {
        void apply(T t, R r, E e, W w);
    }

    public static class LoginTokenOvertimeException extends IllegalStateException {
        public LoginTokenOvertimeException(String s) {
            super(s);
        }
    }

    //秒级时间戳
    public static int getSecondTimestampTwo(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

}
