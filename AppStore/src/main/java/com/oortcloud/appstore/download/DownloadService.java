package com.oortcloud.appstore.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function： 下载应用
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/4 06:28
 */
public class DownloadService extends Service {

    //管理正在下载应用
    public static final List<DownLoadInfo> mDownLoadList = new ArrayList() {};


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


    public void startDownload(DownLoadInfo downLoadInfo , DownloadListener listener){

        int index =  mDownLoadList.indexOf(downLoadInfo);
        //如果存在获取当前得下载信息
        if (index != -1){

            DownLoadInfo loadInfo = mDownLoadList.get(index);
            //判断是否处于下载还是暂停状态   后期可用文件存储暂停后的状态
            if (loadInfo.isDownloading()){
                loadInfo.setDownloading(false);
            }else {
                loadInfo.setDownloading(true);
                for (DownLoadThreadInfo downLoadThreadInfo :loadInfo.getDownloadThreadInfo()){
                    //下载进度比总进度小才继续下载否则 该线程已经下载完成
                    if (downLoadThreadInfo.getDownpos() < downLoadThreadInfo.getBlock()){

                        if(listener != null){
                            ThreadPoolManager.getInstance().startThread_(new DownLoadThread(downLoadThreadInfo, loadInfo.getDownloadurl(), loadInfo.getFilePath()),listener);

                        }else {
                            ThreadPoolManager.getInstance().startThread(new DownLoadThread(downLoadThreadInfo, loadInfo.getDownloadurl(), loadInfo.getFilePath()));
                        }

                    }

                }
            }

        }else {
            mDownLoadList.add(downLoadInfo);
            ThreadPoolManager.getInstance().startDownloads(downLoadInfo, listener);
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
