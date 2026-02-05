package com.oortcloud.clouddisk.transfer.down;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.oortcloud.clouddisk.db.DBManager;

import java.util.HashMap;

/**
 * @filename:
 * @function： 下载应用
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/4 06:28
 */
public class DownloadService extends Service {

    //管理正在下载应用
    public static  final HashMap<String, DownloadListener> mListeners = new HashMap<>(); //用来存放各个下载的请求

    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }
    @Override
    public void onCreate() {

        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }


    public void startDownload(DownLoadInfo downLoadInfo , DownloadListenerImpl listener){
            mListeners.put(downLoadInfo.getFile_path() , listener);

            if (listener.getDownloadThreadInfo() != null && listener.getDownloadThreadInfo().size() > 0){
                //先创建回调
                DownloadResponseHandler  downloadHandler = new DownloadResponseHandler(downLoadInfo , listener);


                for (DownLoadThreadInfo downLoadThreadInfo : listener.getDownloadThreadInfo()){
                    //下载进度比总进度小才继续下载否则 该线程已经下载完成

                    ThreadPoolManager.getInstance().startThread(new DownLoadThread(downLoadThreadInfo , downloadHandler , DBManager.getInstance()));


                }
            } else {
                Log.v("msg"  ,  "ew Down");
                ThreadPoolManager.getInstance().startDownloads( new DownloadRequest( downLoadInfo, listener));
            }

    }



    /**
     * 获取当前Service的实例
     * @return
     */
    public class MsgBinder extends Binder {

        public DownloadService getService(){
            return DownloadService.this;
        }
    }



}
