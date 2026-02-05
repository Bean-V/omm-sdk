package com.oort.weichat.util.offline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;


/**
 *案卷同步管理器
 *
 * 收到com.shenzhou.inFBC广播后，
 *  Intent intent=new Intent("com.shenzhou.inFBC");
 *     intent.putExtra("Downloaded",downloaded);
 *     gApp.sendStickyBroadcast(intent);
 *
 * 从公共路径
 * {ExternalStorageDirectory}/com.shenzhou.inFBC/Download
 * {ExternalStorageDirectory}/com.shenzhou.inFBC/Upload
 *
 * 获取下载文件，拷贝到自己到公共的外置路径下file，解析刷洗案卷
 * **/
public class OfflineSyncManager {

    private static Activity mContext;

    private static OfflineSyncManager mInstance;

    private OfflineFileloader mOfflineFileloader;

    private OfflineFileLoaderListenter mOfflineFileLoaderListenter;

    
    public static OfflineSyncManager init(Activity context){
        if (mInstance == null){
            mInstance = new OfflineSyncManager(context);
        }
        mContext = context;
        return mInstance;
    }

    public static OfflineSyncManager getInstance(){
        return mInstance;
    }

    public OfflineSyncManager(Activity context) {
        mContext = context;
        mOfflineFileloader = new OfflineFileloader(mContext);
    }

    public void setmArchiveFileLoaderListenter(OfflineFileLoaderListenter listenter){
        this.mOfflineFileLoaderListenter = listenter;
        mOfflineFileloader.setListenter(mOfflineFileLoaderListenter);

        registerinFBC();

//        testBroadcast();
    }


    //注册广播
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerinFBC(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.shenzhou.inFBC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String AppName = intent.getStringExtra("AppName");

                    if (!TextUtils.isEmpty(AppName) && AppName.equals("0")) {
                        // 接收到广播后开启下载任务
                        mOfflineFileloader.startLoading();
                    }
                }
            }, filter, Context.RECEIVER_NOT_EXPORTED); // 如果不需要被其他应用访问，使用 RECEIVER_NOT_EXPORTED
            // 如果需要被其他应用访问，使用 Context.RECEIVER_EXPORTED
        } else {
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String AppName = intent.getStringExtra("AppName");

                    if (!TextUtils.isEmpty(AppName) && AppName.equals("0")) {
                        // 接收到广播后开启下载任务
                        mOfflineFileloader.startLoading();
                    }
                }
            }, filter);
        }
    }

    //测试触发广播
    private void testBroadcast(){
        Intent intent=new Intent("com.shenzhou.inFBC");
        intent.putExtra("Downloaded",1);
        intent.putExtra("AppName","0");
        mContext.sendStickyBroadcast(intent);
    }


}
