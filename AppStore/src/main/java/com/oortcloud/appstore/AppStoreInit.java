package com.oortcloud.appstore;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

/**
 * @ProjectName: AppStore-master
 * @FileName: AppMallinit.java
 * @Function: 模块初始化
 * @Author: zhangzhijun / @CreateDate: 20/02/28 19:55
 * @Version: 1.0
 */
public class AppStoreInit {

    private static final String TAG = "AppStoreInitlclog";
    private static AppStoreInit mAppStoreInit;
    private static Application mApplication;
    private static Activity mActivity;
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

    public static void setActivity(Activity activity){
        mActivity = activity;
    }

    public  Activity getActivity(){



        return mActivity;
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
       UserInfo userInfo =  UserInfoUtils.getInstance(mApplication).getLoginUserInfo();
        return  userInfo != null ? userInfo.getOort_uuid() :"";
    }
     public static String getDecode() {
           UserInfo userInfo =  UserInfoUtils.getInstance(mApplication).getLoginUserInfo();
            return  userInfo != null ? userInfo.getOort_depcode() :"";
        }

    public static void  initData(String token  ,String uuid){
        OperLogUtil.d(TAG , token);
        OperLogUtil.d(TAG , uuid);
        DataInit.installinit(DBConstant.INSTALL_TABLE , token , uuid);
        DataInit.moduleinit(token , uuid , null);
    }

}
