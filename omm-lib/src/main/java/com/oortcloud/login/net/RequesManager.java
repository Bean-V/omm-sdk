package com.oortcloud.login.net;

import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.oort.weichat.AppConfig;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.bean.event.MessageUpdate;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.live.bean.LiveRoom;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.constant.UserConstantKey;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.utils.RSAUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import okhttp3.Call;

/**
 * @filename:
 * @function： 网络请求
 * @version：
 * @author: zhangzhijun
 * @date: 2019/11/8 17:00
 */
public class RequesManager {


    public static Observable login(String userName, String password){
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userName);
        userInfo.put("password", password);
        userInfo.put("timestamp",getSecondTimestampTwo());
        userInfo.put("client","android");

        userInfo.put("xmppVersion", "1");
        // 附加信息
        userInfo.put("model", DeviceInfoUtil.getModel());
        userInfo.put("osVersion", DeviceInfoUtil.getOsVersion());
        userInfo.put("serial", DeviceInfoUtil.getDeviceId(MyApplication.getContext()));
        // 地址信息
        double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
        double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
        if (latitude != 0)
            userInfo.put("latitude", String.valueOf(latitude));
        if (longitude != 0)
            userInfo.put("longitude", String.valueOf(longitude));

        userInfo.put("location",MyApplication.getInstance().getBdLocationHelper().getAddress());

        if (MyApplication.IS_OPEN_CLUSTER) {// 服务端集群需要
            String area = PreferenceUtils.getString(MyApplication.getContext(), AppConstant.EXTRA_CLUSTER_AREA);
            if (!TextUtils.isEmpty(area)) {
                userInfo.put("area", area);
            }
        }

        String json = new Gson().toJson(userInfo);
        //加密后的密文
        String userInfo_key = null;

        try {
            String str = RSAUtils.encryptRSA(json);
            userInfo_key = str.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("userInfo", userInfo_key);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("requestType", Constant.REQUEST_TYPE);

        return RetrofitServiceManager.PostFieldObservable(Constant.LOGIN_SERVICE, map, headerMap);

    }
    public static Observable depcode(){
//        return RetrofitServiceManager.PostFieldObservable(Constant.SZJCY_LIST);
        HashMap<String, Object> map = new HashMap<>();
        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("requestType", Constant.REQUEST_TYPE);
        return RetrofitServiceManager.PostFieldObservable ( Constant.SZJCY_LIST,map,headerMap );
    }
    public static Observable organization() {

        HashMap<String, Object> departmentInfo = new HashMap<>();

        departmentInfo.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        departmentInfo.put ("oort_depcode", "");
        departmentInfo.put("showUser", 1);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.PostFieldObservable ( Constant.ORGANIZATION,departmentInfo,headerMap );
    }
    public static Observable login(String userName, String password, String captchaID, String codeID) {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userName);
        userInfo.put("password", password);
        userInfo.put("timestamp",getSecondTimestampTwo());
        userInfo.put("client","android");
        userInfo.put("captchaID", captchaID);
        userInfo.put("code", codeID);
        userInfo.put("xmppVersion", "1");
        // 附加信息
        userInfo.put("model", DeviceInfoUtil.getModel());
        userInfo.put("osVersion", DeviceInfoUtil.getOsVersion());
        userInfo.put("serial", DeviceInfoUtil.getDeviceId(MyApplication.getContext()));
        // 地址信息
        double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
        double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
        if (latitude != 0)
            userInfo.put("latitude", String.valueOf(latitude));
        if (longitude != 0)
            userInfo.put("longitude", String.valueOf(longitude));

        userInfo.put("location",MyApplication.getInstance().getBdLocationHelper().getAddress());

        if (MyApplication.IS_OPEN_CLUSTER) {// 服务端集群需要
            String area = PreferenceUtils.getString(MyApplication.getContext(), AppConstant.EXTRA_CLUSTER_AREA);
            if (!TextUtils.isEmpty(area)) {
                userInfo.put("area", area);
            }
        }

        String json = new Gson().toJson(userInfo);
        //加密后的密文
        String userInfo_key = null;

        try {
            String str = RSAUtils.encryptRSA(json);
            userInfo_key = str.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("userInfo", userInfo_key);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("requestType", Constant.REQUEST_TYPE);

        return RetrofitServiceManager.PostFieldObservable(Constant.LOGIN_SERVICE, map, headerMap);

    }

    public static Observable autologin(String token, String userid ){
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("access_token", token);
        userInfo.put("userId", userid);

        // 附加信息
        userInfo.put("serial", DeviceInfoUtil.getDeviceId(MyApplication.getContext()));
        userInfo.put("appId","");

        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("requestType", Constant.REQUEST_TYPE);

        return RetrofitServiceManager.PostFieldObservable(Constant.AUTOLOGIN_SERVICE, userInfo, headerMap);

    }

    public static Observable resetPassword(String token,String oldpassword, String newpassword){
        HashMap<String, Object> resetPwdInfo = new HashMap<>();
        resetPwdInfo.put("oldpassword", oldpassword);
        resetPwdInfo.put("password", newpassword);
        resetPwdInfo.put("timestamp",getSecondTimestampTwo());

        String json = new Gson().toJson(resetPwdInfo);
        //加密后的密文
        String resetPwdInfo_key = null;

        try {
            String str = RSAUtils.encryptRSA(json);
            resetPwdInfo_key = str.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("resetPwdInfo", resetPwdInfo_key);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("requestType", Constant.REQUEST_TYPE);

        return RetrofitServiceManager.PostFieldObservable(Constant.RESET_PASSWORD, map, headerMap);

    }

    //秒级时间戳
    public static int getSecondTimestampTwo(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * 获取用户详细信息接口
     * @param token
     * @param oort_uuid
     * @return
     */
    public static Observable getUserInfo(String token ,String oort_uuid){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", token);
        map.put("oort_uuid", oort_uuid);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.PostFieldObservable(Constant.GET_USER_INFO, map, headerMap);

    }



    /**
     * 获取用户及其他数据初始化
     * @param accessToken
     * @param oort_uuid
     */

    public interface Callback {
        boolean sucessCallBack();
    }
    public static void initUserInfoAndData(String imData , String accessToken , String oort_uuid, Callback callback){
        new Thread(() -> {
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
        //保存IM用户信息 其它模块使用
        sharedPreferences.edit().putString(UserConstantKey.IM_USER_INFO_SAVE,imData).apply();
        //初始化短视频提供前端调用
//        initVideo(sharedPreferences);
//        //初始化朋友圈数据
//        initDiscover(sharedPreferences);
//        //初始化直播数据
//        initLive(sharedPreferences);
            OperLogUtil.e("login-step", "executor-5");
        RequesManager.getUserInfo(accessToken , oort_uuid).subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                OperLogUtil.e("login-step", "executor-6");       
                OperLogUtil.msg("login-step" + s);
                DialogHelper.dismissProgressDialog();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    int code = jsonObject.getIntValue("code");
                    String msg = jsonObject.getString("msg");
                    if(code == 200) {
                        JSONObject data = jsonObject.getJSONObject("data").getJSONObject("userInfo");
                        OperLogUtil.msg("login-step"+ data.toJSONString());
                        if (data != null) {
                            OperLogUtil.e("login-step", "executor-8");
                            sharedPreferences.edit().putString(UserConstantKey.SSO_LOGIN_RESPONSE, s).apply();
                            UserInfo userInfo = UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo();
                            //初始化应用市场安装应用列表信息
                            if (userInfo != null){
                                OperLogUtil.e("login-step", "executor-9");
                                OperLogUtil.e("login-step", userInfo.toString());
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
                                OperLogUtil.e("login-step", userInfo.getOort_uuid() + "===" + ReportInfo.oort_uuid);
                                //初始化完成发送检查版本消息
                                EventBus.getDefault().post(new MessageUpdate(true));
                                //
                                AppStoreInit.initData(accessToken , UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo().getOort_uuid());

                                callback.sucessCallBack();
                            }
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(CommonApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    OperLogUtil.e("login", "json error!");
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CommonApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
        }).start();
    }

    /**
     * 检查是否需要验证码
     * @return
     */
    public static Observable getCaptcha(String model){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", model);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.PostFieldObservable(Constant.GET_CAPTCHA, map, headerMap);

    }

    /**
     * 发送短信验证码
     * @param captchaID 图形验证码ID
     * @param code  图形验证码
     * @param phone 手机号码
     * @return
     */
    public static Observable sendsmscode(String captchaID , String code , String phone){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("captchaID", captchaID);
        map.put("code", code);
        map.put("phone", phone);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.SEND_SMS_CODE, map, headerMap);

    }
    /**
     * 注册接口,使用公钥加密下列的登录信息
     * {"phone":"手机号码","password":"密码","timestamp":时间戳,"code":"手机验证码","name":"姓名"}
     */
    public static Observable register(String phone , String password , String phoneCode){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("timestamp", getSecondTimestampTwo());
        map.put("code", phoneCode);
        map.put("name", phone+"_"+getRandomString());

        String json = new Gson().toJson(map);
        //加密后的密文
        String userInfo_key = null;

        try {
            String str = RSAUtils.encryptRSA(json);
            userInfo_key = str.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, Object> bodymap = new HashMap<>();
        bodymap.put("userInfo", userInfo_key);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.REGISTER, bodymap, headerMap);

    }

    /**
     * 上传 sso头像 姓名
     * @param oort_photo
     * @param oort_name
     * @return
     */
    public static Observable setUserInfo(String oort_photo  , String oort_name){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""));
        map.put("oort_name", oort_name);
        map.put("oort_photo", oort_photo);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.SET_USER_INFO, map, headerMap);

    }

    public static Observable setUserInfo_name(String oort_name){

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", FastSharedPreferences.get("USERINFO_SAVE").getString("token" , ""));
        map.put("oort_name", oort_name);
        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.SET_USER_INFO, map, headerMap);

    }



    //如果字符种类不够，可以自己再添加一些
    private static String range = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static synchronized String getRandomString() {

        Random random = new Random();

        StringBuffer result = new StringBuffer();
        //要生成几位，就把这里的数字改成几
        for (int i = 0; i < 6; i++) {

            result.append(range.charAt(random.nextInt(range.length())));

        }

        return result.toString();
    }

    //获取短视频数据
    public static void initVideo(FastSharedPreferences sharedPreferences){

        CoreManager coreManager =  CoreManager.getInstance(MyApplication.getContext());
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("pageIndex", Integer.toString(0));
        params.put("pageSize", "20");// 给一个尽量大的值

//        params.put("userId", coreManager.getSelf().getUserId());
        HttpUtils.get().url(coreManager.getConfig().GET_TRILL_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<PublicMessage>(PublicMessage.class) {
                    @Override
                    public void onResponse(ArrayResult<PublicMessage> result) {
                        OperLogUtil.v("msg" ,result.toString());
                        sharedPreferences.edit().putString("videoResult" , result.toString());
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });


    }
    //获取朋友圈数据
    public static void initDiscover(FastSharedPreferences sharedPreferences){

        CoreManager coreManager =  CoreManager.getInstance(MyApplication.getContext());
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("pageSize", String.valueOf(50));

        HttpUtils.get().url(coreManager.getConfig().MSG_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<PublicMessage>(PublicMessage.class) {
                    @Override
                    public void onResponse(ArrayResult<PublicMessage> result) {
                        sharedPreferences.edit().putString("discoverResult" , result.toString());
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });

    }

    ///获取直播列表数据
    public static void initLive(FastSharedPreferences sharedPreferences){
        CoreManager coreManager =  CoreManager.getInstance(MyApplication.getContext());
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(0));
        params.put("pageSize", String.valueOf(AppConfig.PAGE_SIZE));
        params.put("status", "1");
        HttpUtils.get().url(coreManager.getConfig().GET_LIVE_ROOM_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<LiveRoom>(LiveRoom.class) {
                    @Override
                    public void onResponse(ArrayResult<LiveRoom> result) {
                        OperLogUtil.v("msg" ,"--"+result.toString());
                        sharedPreferences.edit().putString("liveResult" , result.toString());

                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    /**
     * 获取会议详情
     * @param uid
     * @return
     */
    public static Observable getMeetingDetail(String uid){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("uid", uid);

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_DETAIL, bodyMap, headerMap);

    }
    /**
     *  会议开关
     * @param open  0 开启 1 关闭
     * @param meet_id 会议id
     * @return
     */
    public static Observable meetingOpen(int open , String meet_id ,String uuid){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("open", open);
        bodyMap.put("uid", meet_id);
        bodyMap.put("uuid", uuid);

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_OPEN, bodyMap, headerMap);

    }

    /**
     *  获取会议列表
     * @param content 搜索内容
     * @param start_time 开始时间
     * @param end_time 结束时间
     * @param pageNum 页数
     * @param pageSize 行数
     * @param status 处理情况（99-全部，1-未开始,2-进行中,3-已结束)
     *
     * @return
     */
    public static Observable meetingList(String content , int start_time , int end_time ,int pageNum , int pageSize , int status){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("content", content);
        bodyMap.put("start_time", start_time);
        bodyMap.put("end_time", end_time);
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", pageSize);
        bodyMap.put("status", status);

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_LIST, bodyMap, headerMap);

    }

    /**
     *  创建会议
     * @param name 会议标题
     * @param content 会议内容
     * @param
     * @param
     * @return
     */
    public static Observable createMeeting(String name ,String content ){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();
        UserInfo userInfo = UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo();
        bodyMap.put("name", name);
        bodyMap.put("content", content);
        bodyMap.put("creator", userInfo.getOort_name()); //creator 创建人
        bodyMap.put("start_time", getSecondTimestampTwo());//start_time 开始时间
        bodyMap.put("end_time", 0); //end_time 结束时间
        bodyMap.put("uuid", userInfo.getOort_uuid());

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_ADD, bodyMap, headerMap);

    }

    /**
     * 删除会议
     * @param uid 会议id
     * @return
     */
    public static Observable deleteMeeting(String uid ){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("uid", uid);
        bodyMap.put("uuid",  UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo().getOort_uuid());

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_DELETE, bodyMap, headerMap);

    }

    /**
     * 编辑会议
     * @param uid 会议id
     * @param name 会议名称
     * @param content 会议内容
     * @return
     */
    public static Observable editMeeting(String uid , String name ,String content){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("uid", uid);
        bodyMap.put("name", name);
        bodyMap.put("content", content);
        bodyMap.put("start_time", getSecondTimestampTwo());//start_time 开始时间
        bodyMap.put("end_time", 0); //end_time 结束时间

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_EDIT, bodyMap, headerMap);

    }

    /**
     * 会议在线人数
     * @param open    状态：0-开启,1-关闭
     * @param uid 会议id
     * @return
     */
    public static Observable updateNumbarMeeting(int open , String uid ){

        //body
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("open", open);
        bodyMap.put("uid", uid);
        bodyMap.put("uuid",  UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginUserInfo().getOort_uuid());

        //header
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.Postbservable(Constant.MEETING_UPDATE_NUMBAR, bodyMap, headerMap);

    }
    /**
     * 获取底部Tabs
     * @return
     */
    public static Observable getBottomTabs(){

        //body
        HashMap<String, Object> map = new HashMap<>();
        //header
        HashMap<String, Object> headerMap = new HashMap<>();
        map.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        return RetrofitServiceManager.Postbservable(Constant.BOTTOM_TAB_CONFIG, map, headerMap);

    }
}
