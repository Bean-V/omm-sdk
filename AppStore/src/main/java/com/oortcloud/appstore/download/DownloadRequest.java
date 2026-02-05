package com.oortcloud.appstore.download;

import android.util.Log;

import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.basemodule.constant.Constant;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 2020/2/1.
 *
 * @author zhangzhijun
 * @version 1.0
 * @date 2019/4/4
 * @function :负责获取下载文件信息 及启动多线程下载
 */

public class DownloadRequest implements Runnable {

    private DownloadResponseHandler downloadHandler;

    private DownLoadInfo mDownLoadInfo;

    public DownloadRequest(DownLoadInfo downLoadInfo, DownloadListener listener) {
        this.mDownLoadInfo = downLoadInfo;

        this.downloadHandler = new DownloadResponseHandler(mDownLoadInfo, listener);
    }

    public  void setDownlistener(DownloadListener listener){
        if(this.downloadHandler != null){
            this.downloadHandler.setDownloadListener(listener);
        }
    }

    @Override
    public void run() {
        try {
            makeRequest();

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

    }

    /**
     * 建立连接
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void makeRequest() throws IOException, InterruptedException {
        //判断是否中断状态
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(mDownLoadInfo.getDownloadurl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("appid",
                        Constant.APP_ID);
                connection.setRequestProperty("secretkey",
                        Constant.SECRET_KEY);
                connection.setRequestProperty("requestType",
                        "app");
                connection.setRequestProperty("accessToken",
                        AppStoreInit.getToken());

                if (!Thread.currentThread().isInterrupted()) {
                    if (downloadHandler != null) {
                        if (connection.getResponseCode() == 200) {
                            mDownLoadInfo.setContentLength(connection.getContentLength());
                            //检查文件是否存在
                            FileUtils.checkLocalFilePath(mDownLoadInfo.getFilePath());
                            startThread();
                        } else {
                            AppStatu.getInstance().appStatu = 0;
                            downloadHandler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
                        }
                    }
                }
            } catch (IOException e) {
                AppStatu.getInstance().appStatu = 0;
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }
        }
    }

    //启动多线程下载
    private void startThread() {

        int threadNum = 0;
        RandomAccessFile randomAccessFile = null;
        Log.v("msg", "文件大小：" + mDownLoadInfo.getContentLength());

        try {
            randomAccessFile = new RandomAccessFile(mDownLoadInfo.getFilePath(), "rwd");
            // 设置文件的大小
            randomAccessFile.setLength(mDownLoadInfo.getContentLength());
            randomAccessFile.close();

            //根据文件大小计算线程数及控件刷新频率
            if (mDownLoadInfo.getContentLength() >= 1024 * 1024 * 120) {
                threadNum = 5;
                mDownLoadInfo.setRate(1000);

            } else if (mDownLoadInfo.getContentLength() > 1024 * 1024 * 80) {
                threadNum = 4;
                mDownLoadInfo.setRate(800);

            } else if (mDownLoadInfo.getContentLength() > 1024 * 1024 * 40) {
                threadNum = 3;
                mDownLoadInfo.setRate(600);
            } else if (mDownLoadInfo.getContentLength() > 1024 * 1024 * 5) {
                threadNum = 2;
                mDownLoadInfo.setRate(400);
            } else {
                threadNum = 1;
                mDownLoadInfo.setRate(200);
            }
            mDownLoadInfo.setThreadNum(threadNum);

            // 每个线程下载的大小
            long blockSize = mDownLoadInfo.getContentLength() / threadNum;
            // 开线程 操作此文件
            for (int i = 1; i <= threadNum; i++) {

                // 计算出每个线程开始的位置
                long startSize = (i - 1) * blockSize;
                // 结束位置
                long endSize = (i) * blockSize;
                // 当线程是最后一个线程的时候
                if (i == threadNum) {

                    // 判断文件的大小是否大于计算出来的结束位置
                    if (mDownLoadInfo.getContentLength() > endSize) {
                        // 结束位置 等于 文件的大小
                        endSize = mDownLoadInfo.getContentLength();

                    }
                }
                // 创建线程下载线程
                DownLoadThreadInfo downLoadThreadInfo = new DownLoadThreadInfo();
                downLoadThreadInfo.setThreadid(i);
                downLoadThreadInfo.setStartpos(startSize);
                downLoadThreadInfo.setEndpos(endSize);
                downLoadThreadInfo.setBlock(blockSize);
                downLoadThreadInfo.setHandler(downloadHandler);
                ThreadPoolManager.getInstance().startThread(new DownLoadThread( downLoadThreadInfo, mDownLoadInfo.getDownloadurl(), mDownLoadInfo.getFilePath()));
                //添加到下载信息中
                mDownLoadInfo.addDownloadThreadInfo(downLoadThreadInfo);
            }


        } catch (IOException e) {
            AppStatu.getInstance().appStatu = 0;
            downloadHandler.stopDownloading();
            downloadHandler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);

        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

            } catch (IOException e) {
                AppStatu.getInstance().appStatu = 0;

                downloadHandler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
            }

        }
    }


    public boolean isDownloading() {
        return downloadHandler.isDownloading();
    }

    public void cancelRequest() {

        AppStatu.getInstance().appStatu = 0;
        downloadHandler.stopDownloading();
        downloadHandler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);

    }
}
