package com.oortcloud.clouddisk.transfer.down;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.utils.ScreenUtil;

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
    private  long mThreadSizeTote = 0;
    //消息回调
    private DownloadListener downloadListener;
    //handler转发消息
    private Handler handler;
    //记录为限制的更新频率
    int limit = 0;
    long mByteCoyunt;
    //下载信息
    DownLoadInfo mDownLoadInfo;
    boolean isFlag = true;
    @SuppressLint("HandlerLeak")
    public DownloadResponseHandler(DownLoadInfo downLoadInfo , DownloadListener listener){

        this.mDownLoadInfo = downLoadInfo;
        this.downloadListener = listener;



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
        return downloadListener.isDownloading();
    }

    protected void handleSelfMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case START_MESSAGE:
                response = (Object[]) msg.obj;
                handleStartMessage(((Long) response[0]).intValue());
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((FailureCode) response[0]);
                break;
            case PROGRESS_CHANGED:
                response = (Object[]) msg.obj;
                handleProgressChangedMessage(((Integer) response[0]).intValue() ,((Long) response[1]).intValue());
                break;
            case PAUSED_MESSAGE:
                handlePausedMessage();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }


    protected void sendStartMessage(long contentLength){
        if (isFlag){
            isFlag = false;
            mDownLoadInfo.setContentLength(contentLength);
            sendMessage(obtainMessage(START_MESSAGE , new Object[]{contentLength}));
            upDate();
        }

    }

    /**
     * 发送暂停消息
     */
    protected void sendPauseMessage(){
        if (!isFlag){
            isFlag =true;
            sendMessage(obtainMessage(PAUSED_MESSAGE , null));
            upDate();
        }

    }

    /**
     * 发送进度更新
     */
    protected  void sendProgressChangedMessage(long byteCount){
        mByteCoyunt += byteCount;
        mDownLoadInfo.setCompleteSize(mDownLoadInfo.getCompleteSize() + byteCount);
        if (mDownLoadInfo.getCompleteSize() < mDownLoadInfo.getContentLength()){
           int progress = (int) (Float.parseFloat(getTwoPointFloatStr(
                    (float) ( mDownLoadInfo.getCompleteSize()) / (mDownLoadInfo.getContentLength() ))) * 100);
            //  为限制notification的更新频率
            if (limit != progress ){ //limit % mDownLoadInfo.getRate() == 0
                limit = progress;
                if (progress <= 100){

                    synchronized (mDownLoadInfo){

                        sendMessage(obtainMessage(PROGRESS_CHANGED ,new Object[]{progress ,mByteCoyunt}));
                        mByteCoyunt = 0;
                        mDownLoadInfo.setProgress(progress);
                        upDate();
                    }

                }


            }
        }

    }

    /**
     * 发送下载完成消息
     */
    protected  void sendFinishMessage(long length) {
        mThreadSizeTote +=  length;

        if (mThreadSizeTote >= mDownLoadInfo.getContentLength()){

            mDownLoadInfo.setStatus(Status.SUCCESS);
            mDownLoadInfo.setCompleteSize(mThreadSizeTote);
            mDownLoadInfo.setCtime(ScreenUtil.getSecondTimestampTwo());
            upDate();
            DownloadService.mListeners.remove(mDownLoadInfo.getFile_path());
            sendMessage(obtainMessage(FINISH_MESSAGE ,null));

        }

    }

    /**
     * 发送失败消息
     * @param failureCode
     */
    protected void  sendFailureMessage(FailureCode failureCode){
        if (!isFlag){
            isFlag =true;
            mDownLoadInfo.setStatus(Status.FAIL);
            upDate();
            sendMessage(obtainMessage(FAILURE_MESSAGE , new Object[]{failureCode}));

        }

    }

    /**
     * 封装messaage
     * @param responseMessage
     * @param response
     * @return
     */
    protected Message obtainMessage(int responseMessage , Object response){
        Message msg = null;
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
    protected void    sendMessage(Message msg){
        if (handler != null){
            handler.sendMessage(msg);
        }else {
            handleSelfMessage(msg);
        }

    }


//    --------------------------------------------------------------------------------

    /**
     *下载完成 外部接口的回调
     */
    public void onFinish(){
        if (downloadListener != null){
            downloadListener.onFinished();
        }

    }

    /**
     * 下载失败
     * @param failureCode
     */
    public void onFailure(FailureCode failureCode){
        if (downloadListener != null){
            downloadListener.onFailure();
        }

    }
    /**
     * 准备下载
     */
    protected void handleStartMessage(long contentLength ){
        if (downloadListener != null){
            downloadListener.onStarted(contentLength);
        }

    }

    /**
     * 封装消暂停消息
     */
    protected void handlePausedMessage(){
        if (downloadListener != null){
            downloadListener.onPaused( mThreadSizeTote);
        }

    }

    /**
     * 封装进度更新消息
     * @param progress
     */
    protected void handleProgressChangedMessage(int progress , long byteCount){
        if (downloadListener != null){
            downloadListener.onProgressChanged(progress , byteCount);
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

    private void upDate(){
        DBManager.getInstance().update(mDownLoadInfo , "file_path" , mDownLoadInfo.getFile_path());
    }
    /**
     * 包含了下载过程中所有可能出现的异常情况
     */
    public enum FailureCode {
        UnknownHost, Socket, SocketTimeout, ConnectTimeout, IO, HttpResponse, JSON, Interrupted
    }
}
