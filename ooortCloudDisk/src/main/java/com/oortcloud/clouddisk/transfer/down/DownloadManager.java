package com.oortcloud.clouddisk.transfer.down;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.transfer.Status;

/**
 * @filename:
 * @function： 下载启动/暂停  进度 管理类
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/5/14 14:42
 */
public class DownloadManager {

    private static DownloadService mService;

    private  Context mContext;
    private static DownloadManager mDownloadManager;

    public DownloadManager(){
        mContext = BaseApplication.getInstance().getContext();
        initBindService();
    }
    /**
     * 绑定服务
     */
    private void initBindService() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private static final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回一个MsgService对象
            mService = ((DownloadService.MsgBinder) iBinder).getService();
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
    public void startDownload(DownLoadInfo downLoadInfo , DownloadListenerImpl listener) {
            if (mService != null) {
                downLoadInfo.setStatus(Status.PROGRESS);
                mService.startDownload(downLoadInfo, listener);
            }else {
            }
    }
    //启动服务下载
    public void stopDownload(DownLoadInfo downLoadInfo ) {
         downLoadInfo.setStatus(Status.PAUSED);
    }

    //判断文件是否在上传中
    public DownloadListenerImpl getUploadListener(String path){

        return (DownloadListenerImpl) DownloadService.mListeners.get(path);

    }

}
