package com.oortcloud.clouddisk;

import android.app.Application;
import android.content.Context;

import com.oortcloud.clouddisk.transfer.down.DownloadManager;
import com.oortcloud.clouddisk.transfer.upload.UploadManager;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/9 15:59
 * @version： v1.0
 * @function：
 */
public class BaseApplication extends Application {

    private static Context mContext;
    private static Application mApplication;
    private static BaseApplication bApplication;
    public static String  TOKEN = "";
    public static String  UUID = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        UploadManager.getInstance();
        DownloadManager.getInstance();
    }

    public static BaseApplication getInstance(){
        if (bApplication == null){
            synchronized (BaseApplication.class){
                if (bApplication == null){
                    bApplication = new BaseApplication();
                    mApplication = getApplication();//new BaseApplication();
                    mContext = getApplication();
                }
                return bApplication;
            }
        }
        return  bApplication;
    }

    public final  Context getContext(){
        return mContext;
    }

    public static Application getApplication() {
        Application currentApplication = null;
        try {
            if (currentApplication == null) {
                currentApplication = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            return currentApplication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
