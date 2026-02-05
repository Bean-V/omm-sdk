package com.oortcloud.clouddisk.transfer.down;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2020/2/1.
 * @author:zhangzhijun
 * @version:1.0
 * @function:下载调度管理器，调用UpdateDownloadRequest
 * @date:2020/2/1
 * 线程池管理
 */

public class ThreadPoolManager {
    private static ThreadPoolManager mMnager;
    private ThreadPoolExecutor threadPool;
    private DownloadRequest downloadRequest;

    static {
        mMnager = new ThreadPoolManager();
    }

    private ThreadPoolManager(){
        //容易造成oom
//        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        threadPool = new ThreadPoolExecutor(10, 20,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue(20));

    }

    public static ThreadPoolManager getInstance(){
        return mMnager;
    }


    public void startDownloads(DownloadRequest downloadRequest){
//        if (downloadRequest != null && downloadRequest.isDownloading()){
//            return;
//        }

        //开始正正在的下载任务
        this.downloadRequest = downloadRequest;
        Future<?> request = threadPool.submit(downloadRequest);
        new WeakReference<Future<?>>(request);
    }

    //处理线程启动
    public void startThread(Runnable runnable){

        //开始正正在的下载任务
        Future<?> request = threadPool.submit(runnable);
        new WeakReference<Future<?>>(request);
    }




}
