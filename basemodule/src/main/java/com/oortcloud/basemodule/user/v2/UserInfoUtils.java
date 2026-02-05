package com.oortcloud.basemodule.user.v2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.constant.UserConstantKey;
import com.oortcloud.basemodule.user.Data;
import com.oortcloud.basemodule.user.ImLoginInfo;
import com.oortcloud.basemodule.user.LoginResponse;
import com.oortcloud.basemodule.user.NewUserInfo;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

/**
 * @filename: userInfoUtil.java
 * @function： 只适用于登录的接口,该接口Header不能传值accessToken,请注意使用
 * @version：
 * @author: zhangzhijun
 * @date: 2019/11/4 12:01
 */

public class UserInfoUtils {

    private Context mContext;

    public static String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
    private static UserInfoUtils mInstance;

    private UserInfoUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public static UserInfoUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (UserInfoUtils.class) {
                if (mInstance == null) {
                    mInstance = new UserInfoUtils(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }


    /**
     * 获取 name
     *
     * @return name 返回空说明没有登录
     */
    public String getUserName() {
        if (getLoginUserInfo() == null) return "";
        return getLoginUserInfo().getUser_name();
    }

    /**
     * 获取 userId
     *
     * @return id 返回空说明没有登录
     */
    public String getUserId() {
        if (getLoginResponse() == null) return null;
        return getLoginUserInfo().getUser_id();
    }

    public String getLoginId() {
        if (getLoginUserInfo() == null) return "";
        String loginId =getImLogin().getData().getUserInfo().getLogincode().getLoginId();
        return TextUtils.isEmpty(loginId) ? "" : loginId;
    }
    public String getDept_code() {
        NewUserInfo.DataBean userInfo = getLoginUserInfo();
        if (userInfo.getDept_list() != null && !userInfo.getDept_list().isEmpty()){
            return userInfo.getDept_list().get(0).getDeptinfo().getDept_code();
        }
        return "";
    }
    public String getDept_name() {
        NewUserInfo.DataBean userInfo = getLoginUserInfo();
        if (userInfo.getDept_list() != null && !userInfo.getDept_list().isEmpty()){
            return userInfo.getDept_list().get(0).getDeptinfo().getDept_name();
        }
        return "";
    }



    /**
     * 获取 登录用户信息,这里不包含userCode
     *
     * @return LoginUserInfo 返回空说明没有登录
     */
    public NewUserInfo.DataBean getLoginUserInfo() {
        if (getLoginResponse() == null) return null;
        NewUserInfo.DataBean userInfo = getLoginResponse().getData();
        return userInfo;
    }
    public String getImUserId() {
        if (getImLogin() == null) return "";
        Log.v("zq", "js----xxx>"+ getImLogin().getData().getImLoginUserInfo().getUserId());
        return String.valueOf(getImLogin().getData().getImLoginUserInfo().getUserId());

    }

    /**
     * 获取登录返回的JSON
     *
     * @return LoginResponse
     */
    private NewUserInfo getLoginResponse() {
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
        String response = sharedPreferences.getString(UserConstantKey.SSO_LOGIN_RESPONSE,"");
        return TextUtils.isEmpty(response) ? null : JSON.parseObject(response, NewUserInfo.class);
    }

    private ImLoginInfo getImLogin() {
        //对接IM数据
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
        String response = sharedPreferences.getString(UserConstantKey.IM_LOGIN,"");
        return TextUtils.isEmpty(response) ? null :  JSON.parseObject(response, ImLoginInfo.class);
    }

    /**
     * 删除登录信息
     */
    public void deleteUserInfo() {
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
        sharedPreferences.edit().putString(UserConstantKey.SSO_LOGIN_RESPONSE,setLoginRespinse());
    }


    public String getIdCard() {
//        if (getLoginUserInfo() == null) return "";
//        String id_card = getLoginUserInfo().getOort_idcard();
//        return TextUtils.isEmpty(id_card) ? "" : id_card;


        return "";
    }


    private String setLoginRespinse(){


        UserInfo mLoginUserInfo = new UserInfo();
        mLoginUserInfo.setOort_uuid("");

        mLoginUserInfo.setOort_idcard("");

        mLoginUserInfo.setOort_name("");
        mLoginUserInfo.setOort_loginid("");

        Data data = new Data();
        data.setUserInfo(mLoginUserInfo);

        LoginResponse mLoginResponse = new LoginResponse();
        mLoginResponse.setResultCode(200);
        mLoginResponse.setResultMsg("退出登录数据");
        mLoginResponse.setData(data);

        return JSON.toJSONString(mLoginResponse);
    }

}
