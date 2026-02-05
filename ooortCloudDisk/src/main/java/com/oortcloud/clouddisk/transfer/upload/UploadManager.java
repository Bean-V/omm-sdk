package com.oortcloud.clouddisk.transfer.upload;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.transfer.Status;

import java.io.File;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/13 15:37
 * @version： v1.0
 * @function： 上传启动/暂停 管理类
 */
public class UploadManager {

    private Context mContext;
    private static UploadService mService;
    private static UploadManager mUploadManager;

    private UploadManager() {
        mContext = BaseApplication.getInstance().getContext();
        initBindService();
    }


    //获得一个单例类
    public static UploadManager getInstance() {
        if (mUploadManager == null) {
            synchronized (UploadManager.class) {
                if (mUploadManager == null) {
                    mUploadManager = new UploadManager();
                }
            }

        }
        return mUploadManager;
    }
    /**
     * 绑定服务
     */
    private void initBindService() {
        Intent intent = new Intent(mContext, UploadService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private static final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回一个MsgService对象
            mService = ((UploadService.UploadBinder) iBinder).getService();

        }
    };

    //启动服务下载
    public void startUpload(UploadInfo uploadInfo , UploadListener listener , File file) {

          if (mService != null && uploadInfo.getStatus() == Status.PROGRESS){

                mService.startUpload(uploadInfo, listener , file);

            }


    }
    //暂停服务下载
    public void stopUpload(UploadInfo uploadInfo) {
            if (mService != null && uploadInfo.getStatus() == Status.PAUSED){

                mService.cancel(uploadInfo.getFile_path());

            }
    }
    //判断文件是否在上传中
    public boolean isUpload(String path){

        return UploadService.downCalls.get(path) == null;


    }

      //判断文件是否在上传中
    public UploadListenerImpl getUploadListener(String path){

       return (UploadListenerImpl)UploadService.listeners.get(path);

    }

}
