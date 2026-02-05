package com.oortcloud.clouddisk.transfer.down;

import android.util.Log;

import com.oortcloud.clouddisk.db.DBManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/18 19:00
 * @version： v1.0
 * @function：多线程处理写文件
 */
public class DownLoadThread implements Runnable {
    // 下载文件的封装
    public RandomAccessFile randomAccessFile;
    //消息回调
    public DownloadResponseHandler handler;
    //下载线程信息
    private DownLoadThreadInfo mDownLoadThreadInfo;

    //下载地址
    private String downloadUrl;

    //存储路径
    private String localFilePath;

    private DBManager mDBManager ;

    public DownLoadThread( DownLoadThreadInfo downLoadThreadInfo ,DownloadResponseHandler handler , DBManager dbManager) {

        this.handler = handler;

        this.mDownLoadThreadInfo = downLoadThreadInfo;

        this.downloadUrl = downLoadThreadInfo.getDownloadUrl();

        this.localFilePath = downLoadThreadInfo.getFile_path();

        this.mDBManager = dbManager;

    }

    @Override
    public void run() {
        if (mDownLoadThreadInfo.getDownpos() < mDownLoadThreadInfo.getBlock()) {
            start();

        } else {
            handler.sendFinishMessage(mDownLoadThreadInfo.getDownpos());
        }
    }

    private void start(){

        InputStream is = null;
        // 执行run方法
        try {
            // 创建文件
            // 创建URL对象
            URL url = new URL(downloadUrl);
            // 创建HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置请求的头
            httpURLConnection.setRequestMethod("GET");
            // 设置请求是否超时时间
            httpURLConnection.setConnectTimeout(5000);
            // 设置
            httpURLConnection
                    .setRequestProperty("User-Agent",
                            " Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");

            Log.v("msg" , "------------"+mDownLoadThreadInfo.getDownpos());
            // 关键的设置
            httpURLConnection.setRequestProperty("Range", "bytes="
                    + (mDownLoadThreadInfo.getStartpos() + mDownLoadThreadInfo.getDownpos())+ "-" + mDownLoadThreadInfo.getEndpos());
            // 输出当前线程
//            Log.v("msg" , "当前线程" + mDownLoadThreadInfo.getThreadid() + " 下载开始位置:" + mDownLoadThreadInfo.getStartpos()
//                    + " 下载结束位置:" + mDownLoadThreadInfo.getEndpos());

//            Log.v("msg" , "------------"+mDownLoadThreadInfo.getDownpos());
            // 响应成功
            if (httpURLConnection.getResponseCode() == 206 || httpURLConnection.getResponseCode() == 200){
                // 获取相应流对象
                is = httpURLConnection.getInputStream();
                if (randomAccessFile == null) {

                    randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                }
                // 设置随机读取文件的 开始位置
                randomAccessFile.seek(mDownLoadThreadInfo.getStartpos() + mDownLoadThreadInfo.getDownpos());
                byte[] bffer = new byte[1024  * 100];
                int length = -1;
//            Log.v("Fragment" , "存储地址：" + localFilePath);

                boolean isPaused = false;
                if (handler.isDownloading()){

                    handler.sendStartMessage(handler.mDownLoadInfo.getContentLength());
                }
                while ((length = is.read(bffer)) != -1) {
                    if (handler.isDownloading()) {
                        randomAccessFile.write(bffer, 0, length);
                        handler.sendProgressChangedMessage(length);
                        mDownLoadThreadInfo.setDownpos(mDownLoadThreadInfo.getDownpos() + length);
                        if (mDBManager != null){
                            mDBManager.update(mDownLoadThreadInfo ,"major_key" , mDownLoadThreadInfo.getMajor_key());
                        }
                    } else {
                        isPaused = true;
                        handler.sendPauseMessage();
                        //刷新
                        mDBManager.update(mDownLoadThreadInfo ,"major_key" , mDownLoadThreadInfo.getMajor_key());
                        break;
                    }
                }

                if (!isPaused) {

                    Log.v("msg" , "thrread--->"+mDownLoadThreadInfo.getThreadid()+ "-----------"+ mDownLoadThreadInfo.getDownpos() );
                    synchronized (handler){
                        handler.sendFinishMessage(mDownLoadThreadInfo.getDownpos());
                    }
                }
            }else {
                handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
            }

        } catch (IOException e) {

            handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);

        } finally {
            try {

                if (is != null) {
                    is.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }


            } catch (IOException e) {

                handler.sendFailureMessage(DownloadResponseHandler.FailureCode.IO);
            }

        }

    }


}
