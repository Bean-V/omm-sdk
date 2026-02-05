package com.oortcloud.basemodule.login.http;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.oortcloud.basemodule.user.NewUserInfo;
import com.oortcloud.basemodule.user.v2.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

/**
 * @ProjectName: AppStore-master
 * @FileName: AppMallinit.java
 * @Function: 模块初始化
 * @Author: zhangzhijun / @CreateDate: 20/02/28 19:55
 * @Version: 1.0
 */
public class AppStoreInit {

    private static final String TAG = "AppStoreInit";
    private static AppStoreInit mAppStoreInit;
    private static Application mApplication;
    public static String store_token;
    public static String store_uuid;

    public static int MODNAME_LEN  = 18;

    private AppStoreInit(){}

    public static AppStoreInit getInstance(){
        if (mAppStoreInit == null){
            synchronized (AppStoreInit.class){
                if (mAppStoreInit == null){
                    mAppStoreInit = new AppStoreInit();
                }
                return mAppStoreInit;
            }
        }
        return  mAppStoreInit;
    }
    public static void setApplication(Application application){
        mApplication = application;
    }

    public  Application getApplication(){
        return mApplication;
    }


    public static String getToken() {
        if (TextUtils.isEmpty(store_token)){
            store_token = FastSharedPreferences.get("USERINFO_SAVE").getString("token" , "");
        }

        return store_token;
    }
    public static String getSZJCYToken() {

        return FastSharedPreferences.get("USERINFO_SAVE").getString("SZJCYToken" , "");
    }
    public static String getUUID() {
       NewUserInfo.DataBean userInfo =  UserInfoUtils.getInstance(mApplication).getLoginUserInfo();
        return  userInfo != null ? userInfo.getUser_id() :"";
    }
     public static String getDecode() {
         String depCode  =  UserInfoUtils.getInstance(mApplication).getDept_code();
            return  depCode.isEmpty() ? "" : depCode;
        }

    public static void  initData(String token  ,String uuid){
        Log.v(TAG , token);
        Log.v(TAG , uuid);
//        DataInit.installinit(DBConstant.INSTALL_TABLE , token , uuid);
//        DataInit.moduleinit(token , uuid , null);
    }

}
