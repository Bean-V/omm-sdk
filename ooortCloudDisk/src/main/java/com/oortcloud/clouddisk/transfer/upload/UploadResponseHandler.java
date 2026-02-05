package com.oortcloud.clouddisk.transfer.upload;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * Created by Admin on 2019/4/8.
 * @author :zhangzhijun
 * @version 1.0
 * @function:上传/消息转发器
 * @date:2020/2/1
 */

public class UploadResponseHandler {


    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int NETWORK_OFF = 4;
    private static final int PROGRESS_CHANGED = 5;
    private static final int PAUSED_MESSAGE = 6;

    //消息回调
    private UploadListener mUploadListener;
    //handler转发消息
    private Handler handler;

    public UploadResponseHandler( UploadListener listener){


        this.mUploadListener = listener;
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
        return  mUploadListener.isDownloading();
    }

    protected void handleSelfMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case START_MESSAGE:
                response = (Object[]) msg.obj;
                handleStartMessage(((Long) response[0]).longValue());
                break;
            case PROGRESS_CHANGED:
                response = (Object[]) msg.obj;
                handleProgressChangedMessage(((Long) response[0]).intValue());
                break;
            case PAUSED_MESSAGE:
                handlePausedMessage();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((FailureCode) response[0]);
                break;
        }
    }


    public void sendStartMessage(long contentLength){

        sendMessage(obtainMessage(START_MESSAGE , new Object[]{contentLength} ));
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
    protected  void sendProgressChangedMessage(long byteCount){
        sendMessage(obtainMessage(PROGRESS_CHANGED ,new Object[]{ byteCount} ));

    }

    /**
     * 发送下载完成消息
     */
    protected  void sendFinishMessage() {

            sendMessage(obtainMessage(FINISH_MESSAGE  ,null));


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
     * @param responseMessage
     * @param response
     * @return
     */
    protected Message obtainMessage(int responseMessage , Object response){
        Message msg ;
        if (handler != null){
            msg = this.handler.obtainMessage(responseMessage , response);
        }else {
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }
    /**
     * 发送消息
     * @param msg
     */
    protected void   sendMessage(Message msg){
        if (handler != null){
            handler.sendMessage(msg);
        }else {
            handleSelfMessage(msg);
        }

    }


//    ------------------------------------------------------

    /**
     *下载完成 外部接口的回调
     */
    public void onFinish(){
            if (mUploadListener != null){
                mUploadListener.onFinished();
            }

    }

    /**
     * 下载失败
     * @param failureCode
     */
    public void onFailure(FailureCode failureCode){
        mUploadListener.onFailure();
    }
    /**
     * 准备下载
     */
    protected void handleStartMessage(long contentLength){
        if (mUploadListener != null){
            mUploadListener.onStarted(contentLength );
        }

    }

    /**
     * 封装消暂停消息
     */
    protected void handlePausedMessage(){
        if (mUploadListener != null){
            mUploadListener.onPaused();
        }

    }

    /**
     * 封装进度更新消息
     * @param byteCount
     */
    protected void handleProgressChangedMessage(long byteCount){
        if (mUploadListener != null){
            mUploadListener.onProgress( byteCount );
        }

    }

    /**
     * handler发送失败消息接口回调
     * @param failureCode
     */
    protected void handleFailureMessage(FailureCode failureCode){
        onFailure(failureCode);
    }

    /**
     * 包含了下载过程中所有可能出现的异常情况
     */
    public enum FailureCode {
        UnknownHost, Socket, SocketTimeout, ConnectTimeout, IO, HttpResponse, JSON, Interrupted
    }

}
