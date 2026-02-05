package com.oortcloud.appstore.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Created by Admin on 2019/4/8.
 * @author :zhangzhijun
 * @version 1.0
 * @function:下载/消息转发器
 * @date:2020/2/1
 */

public class DownloadResponseHandler {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int NETWORK_OFF = 4;
    private static final int PROGRESS_CHANGED = 5;
    private static final int PAUSED_MESSAGE = 6;

    //线程进度和
    private  int mThreadSizeTote = 0;

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    //消息回调
    private DownloadListener downloadListener;
    //handler转发消息
    private Handler handler;
    //记录为限制的更新频率
    int limit = 0;
    //下载信息
    private DownLoadInfo mDownLoadInfo;

    public DownloadResponseHandler(DownLoadInfo downLoadInfo ,DownloadListener listener){

        this.mDownLoadInfo = downLoadInfo;
        this.downloadListener = listener;
        mDownLoadInfo.setDownloading(true);
        if (Looper.myLooper() != null){
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    handleSelfMessage(msg);
                }
            };
        }
    }

    public boolean isDownloading(){
        return  mDownLoadInfo.isDownloading();
    }

    public void stopDownloading(){
        mDownLoadInfo.setDownloading(false);

    }

    protected void handleSelfMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case START_MESSAGE:
                handleStartMessage();
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((FailureCode) response[0]);
                break;
            case PROGRESS_CHANGED:
                response = (Object[]) msg.obj;
                handleProgressChangedMessage(((Integer) response[0]).intValue());
                break;
            case PAUSED_MESSAGE:
                handlePausedMessage();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }


    protected void sendStartMessge(){
        sendMessage(obtainMessage(START_MESSAGE , null));
    }

    /**
     * 发送暂停消息
     */
    protected void sendPauseMessage(){
        sendMessage(obtainMessage(PAUSED_MESSAGE , null));
    }

    /**
     * 发送进度更新
     */
    protected  void sendProgressChangedMessage(int progress){
        mDownLoadInfo.setCompleteSize(mDownLoadInfo.getCompleteSize() + progress);

        if (mDownLoadInfo.getCompleteSize() < mDownLoadInfo.getContentLength()){

 //           try {

//                limit++;
//
//                int a = mDownLoadInfo.getCompleteSize();
//                long b  = mDownLoadInfo.getContentLength();
//                float d = (float)a/b;
//
//
//
//                if(d < 0.01){
//                    progress = 0;
//                }else {
//                    String e = getTwoPointFloatStr(
//                            (float) (mDownLoadInfo.getCompleteSize()) / (mDownLoadInfo.getContentLength()));
//
//                    float f = Float.parseFloat(e);
//
//
//                    float c = Float.parseFloat(getTwoPointFloatStr(
//                            (float) (mDownLoadInfo.getCompleteSize()) / (mDownLoadInfo.getContentLength())));


                    progress = (int) (Float.parseFloat(getTwoPointFloatStr(
                            (float) (mDownLoadInfo.getCompleteSize()) / (mDownLoadInfo.getContentLength()))) * 100);
//                }
//            }catch (Exception e){
//                Exception ez = e;
//            }

            //  为限制notification的更新频率
            if (limit % mDownLoadInfo.getRate() == 0 ){
                if (progress <= 100){
                    sendMessage(obtainMessage(PROGRESS_CHANGED ,new Object[]{progress} ));
                    mDownLoadInfo.setProgress(progress);
                }

            }
        }
        limit++;

    }

    /**
     * 发送下载完成消息
     */
    protected  void sendFinishMessage(long length) {
        mThreadSizeTote +=  length;
        if (mThreadSizeTote >= mDownLoadInfo.getContentLength()){
            //下载完成修改后缀
            File file = new File(mDownLoadInfo.getFilePath().replace("apk_1" , "apk"));
            new File(mDownLoadInfo.getFilePath()).renameTo(file);
            mDownLoadInfo.setFilePath(file.getPath());
            sendMessage(obtainMessage(FINISH_MESSAGE ,null));
            //移除下载表应用信息
            DownloadService.mDownLoadList.remove(mDownLoadInfo);

        }

    }

    /**
     * 发送失败消息
     * @param failureCode
     */
    protected void  sendFailureMessage(FailureCode failureCode){
        sendMessage(obtainMessage(FAILURE_MESSAGE , new Object[]{failureCode}));
    }

    /**
     * 封装messaage
     * @param resPonseMessage
     * @param response
     * @return
     */
    protected Message obtainMessage(int resPonseMessage , Object response){
        Message msg = null;
        if (handler != null){
            msg = this.handler.obtainMessage(resPonseMessage , response);
        }else {
            msg = Message.obtain();
            msg.what = resPonseMessage;
            msg.obj = response;
        }
        return msg;
    }
    /**
     * 发送消息
     * @param msg
     */
    protected void    sendMessage(Message msg){
        if (handler != null){
            handler.sendMessage(msg);
        }else {
            handleSelfMessage(msg);
        }

    }

    /**
     *下载完成 外部接口的回调
     */
    public void onFinish(){

            downloadListener.onFinished(mThreadSizeTote , mDownLoadInfo.getFilePath());


    }

    /**
     * 下载失败
     * @param failureCode
     */
    public void onFailure(FailureCode failureCode){
        downloadListener.onFailure();
    }
    /**
     * 准备下载
     */
    protected void handleStartMessage(){
        downloadListener.onStarted();
    }

    /**
     * 封装消暂停消息
     */
    protected void handlePausedMessage(){
        downloadListener.onPaused(mDownLoadInfo.getProgress() , mDownLoadInfo.getCompleteSize() , "");
    }

    /**
     * 封装进度更新消息
     * @param progress
     */
    protected void handleProgressChangedMessage(int progress){

        downloadListener.onPregressChanged(progress , "");
    }

    /**
     * handler发送失败消息接口回调
     * @param failureCode
     */
    protected void handleFailureMessage(FailureCode failureCode){
        onFailure(failureCode);
    }


    /**
     * 发送连接成功开始下载消息/文件 下载方法
     * @param  contentLength 文件大小
     * @param rate 刷新频率
     *
     * 弃用
     */
    void sendResponseMessage(long contentLength ,int rate){


    }

    /**
     * 格式化数字
     * @param value
     * @return
     */
    private String getTwoPointFloatStr(float value){
        Log.v("Fragment" , ""+ value);



        DecimalFormat fnum = new DecimalFormat("0.00");
        final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
        fnum.setDecimalFormatSymbols(decimalSymbol);
        return fnum.format(value);
    }

    /**
     * 包含了下载过程中所有可能出现的异常情况
     */
    public enum FailureCode {
        UnknownHost, Socket, SocketTimeout, ConnectTimeout, IO, HttpResponse, JSON, Interrupted
    }
}
