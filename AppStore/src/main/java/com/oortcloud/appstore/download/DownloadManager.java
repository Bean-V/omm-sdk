package com.oortcloud.appstore.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.utils.AppManager;

/**
 * @filename:
 * @function： 管理所有下载任务 进度
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/5/14 14:42
 */
public class DownloadManager {

    private static DownloadService mService;

    public static boolean isConnected = false;


    public interface Callback {
        boolean connect();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private static Callback callback;


    private  Context mContext;
    private static DownloadManager mDownloadManager;

    public DownloadManager(){
        mContext = AppStoreInit.getInstance().getApplication();
        initBindService();
    }
    /**
     * 绑定服务
     */
    private void initBindService() {
        Intent intent = new Intent(mContext, DownloadService.class);
        boolean res = mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private static ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            String s = componentName.getPackageName();
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回一个MsgService对象
            mService = ((DownloadService.MsgBinder) iBinder).getService();
            isConnected = true;
            if(callback != null){
                callback.connect();
            }
        }
    };
    public static DownloadManager getInstance(){
        if (mDownloadManager  == null){
            synchronized (DownloadManager.class){
                if (mDownloadManager == null){
                    mDownloadManager = new DownloadManager();
                }
                return mDownloadManager;
            }
        }
        return mDownloadManager;
    }

    //启动服务下载
    public void startDownload(AppInfo appInfo  , DownloadListener listener) {


            DownLoadInfo downLoadInfo = new DownLoadInfo();

            downLoadInfo.setDownloadurl(appInfo.getApk_url());

            if (appInfo.getTerminal() == 0) {
                //apk_1代表正下载未完成
                downLoadInfo.setFilePath(AppManager.BASE_PATH + appInfo.getApplabel() +"_"+ appInfo.getVersion()+ ".apk_1");


            } else if (appInfo.getTerminal() == 1 || appInfo.getTerminal() == 6) {
                downLoadInfo.setFilePath(AppManager.PATH + appInfo.getApplabel() + ".zip");

            } else if (appInfo.getTerminal() == 3) {

                AppStatu.getInstance().appStatu = 0;
                Toast.makeText(mContext, "PC桌面应用，手机端不能使用", Toast.LENGTH_SHORT).show();
                return;
            }


            if (mService != null) {
                mService.startDownload(downLoadInfo, listener);
            }

    }

}
