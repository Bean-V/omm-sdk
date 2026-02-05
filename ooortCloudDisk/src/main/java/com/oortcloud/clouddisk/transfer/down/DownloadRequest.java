package com.oortcloud.clouddisk.transfer.down;

import android.util.Log;

import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.utils.manager.FileUtils;

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

    private DownloadResponseHandler handler;
    private DownloadListenerImpl listener;

    private DownLoadInfo mDownLoadInfo;


    public DownloadRequest(DownLoadInfo downLoadInfo, DownloadListener listener) {
        this.mDownLoadInfo = downLoadInfo;
        this.handler = new DownloadResponseHandler(downLoadInfo ,  listener);
        if (listener instanceof  DownloadListenerImpl){
            this.listener = (DownloadListenerImpl) listener;
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
                URL url = new URL(mDownLoadInfo.getDownloadUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
//                connection.setRequestProperty("Accept-Encoding", "identity");
//                connection.setAllowUserInteraction(true);
//                connection.connect();

                if (!Thread.currentThread().isInterrupted()) {
                    if (handler != null) {
                        if (connection.getResponseCode() == 200) {
                            if (handler.isDownloading()){

                                handler.sendStartMessage(connection.getContentLength());
                            }
                            //检查文件是否存在
                            FileUtils.checkLocalFilePath(mDownLoadInfo.getFile_path());
                            startThread();
                        } else {
                            handler.isFlag = false;
                            handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
                        }
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }
        }
    }

    //启动多线程下载
    private void startThread() {
        DBManager dbManager = DBManager.getInstance();
        int threadNum = 0;
        RandomAccessFile randomAccessFile = null;
        Log.v("msg", "文件大小：" + mDownLoadInfo.getContentLength());

        try {
            randomAccessFile = new RandomAccessFile(mDownLoadInfo.getFile_path(), "rwd");
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
                downLoadThreadInfo.setFile_path(mDownLoadInfo.getFile_path());
                downLoadThreadInfo.setDownloadUrl(mDownLoadInfo.getDownloadUrl());
                downLoadThreadInfo.setStartpos(startSize);
                downLoadThreadInfo.setEndpos(endSize);
                downLoadThreadInfo.setBlock(blockSize);
                downLoadThreadInfo.setMajor_key(mDownLoadInfo.getFile_path() +"_" + i);
                //插入
                dbManager.insert(downLoadThreadInfo);

                ThreadPoolManager.getInstance().startThread(new DownLoadThread(downLoadThreadInfo , handler , dbManager));

                //添加到下载信息中
                listener.addDownloadThreadInfo(downLoadThreadInfo);
            }


        } catch (IOException e) {
            handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);

        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

            } catch (IOException e) {

                handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
            }

        }
    }

}
