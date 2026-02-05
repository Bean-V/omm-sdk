package com.oortcloud.clouddisk.transfer.upload;

import android.util.Log;

import com.oortcloud.clouddisk.adapter.FileAdapter;
import com.oortcloud.clouddisk.adapter.TransfersAdapter;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.utils.ScreenUtil;
import com.oortcloud.clouddisk.utils.helper.FileHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/6 10:40
 * @version： v1.0
 * @function： 上传回调接口
 */
public class UploadListenerImpl implements UploadListener {

    //多个文件大小

    private  UploadInfo mUploadInfo ;

    //更新进度
    public TransfersAdapter.ViewHolder holder;
    public FileAdapter.ViewHolder fileHolder;
    public TransfersAdapter adapter;
    private  String mFileSize;
    //文件大小
    private long mCompleteSize;
    private long contentLength;
    private DBManager mDBManager;
    //刷新进度
    private int limit;
    public UploadListenerImpl(UploadInfo uploadInfo){
        this.mUploadInfo = uploadInfo;
        this.mCompleteSize = mUploadInfo.getCompleteSize();
        mDBManager = DBManager.getInstance();
    }
    @Override
    public void onStarted(long contentLength) {

        this.contentLength = contentLength;

        mUploadInfo.setContentLength(contentLength);

        mDBManager.update(mUploadInfo ,"file_path" ,  mUploadInfo.getFile_path());

        mFileSize =  FileHelper.reckonFileSize(contentLength);
        if (holder != null){
            if ( holder.transfersType != null) {
                holder.transfersType.setText("正在上传");
            }

            if (holder.fileSize != null) {

                holder.fileSize.setText(0 + "KB /" + mFileSize);
            }
        }
        if (fileHolder != null){
            if ( fileHolder.fileTime != null) {
                fileHolder.fileTime.setText("正在上传");
            }

            if (fileHolder.fileSize != null) {

                fileHolder.fileSize.setText(0 + "KB /" + mFileSize);
            }
        }else {
            Log.e("msg" , "-----------fileHolder-");
        }
        CountObserver.getInstance().sendNotify();


    }

    @Override
    public void onPaused() {
        //暂停
        mUploadInfo.setCompleteSize(mCompleteSize);
        mUploadInfo.setStatus(Status.PAUSED);
        mDBManager.update(mUploadInfo ,"file_path" , mUploadInfo.getFile_path());
        if (holder != null){
            if ( holder.transfersType != null) {
                holder.transfersType.setText("暂停");
            }

            if (holder.fileSize != null) {

                holder.fileSize.setText(0 + "KB /" + mFileSize);
            }

        }
        if (fileHolder != null){
            if ( fileHolder.fileTime != null) {
                fileHolder.fileTime.setText("暂停");
            }

            if (fileHolder.fileSize != null) {

                fileHolder.fileSize.setText(0 + "KB /" + mFileSize);
            }

        }
        CountObserver.getInstance().sendNotify();
    }

    @Override
    public void onProgress( long byteCount) {

        mCompleteSize +=  byteCount;

        if (mCompleteSize < contentLength){
            int progress = (int) (Float.parseFloat(getTwoPointFloatStr(
                    (float) (mCompleteSize) / (contentLength ))) * 100);

            //  为限制notification的更新频率
            if (limit !=  progress){
                if (progress <= 100){

                    if (holder != null ){
                        if (holder.progressView != null){
                            holder.progressView.setCurrentStep(progress);
                        }
                        if ( holder.fileSize != null){
                            if (byteCount != 0){
                                holder.fileSize.setText( FileHelper.reckonFileSize(byteCount)+"/" +mFileSize);
                            }else {
                                holder.fileSize.setText(0 + "KB /" + mFileSize);
                            }

                        }
                    }

                    if (fileHolder != null){
                        if (fileHolder.progressBar != null){
                            fileHolder.progressBar.setProgress(progress);
                        }
                        if ( fileHolder.fileSize != null){

                            if (byteCount != 0){
                                fileHolder.fileSize.setText( FileHelper.reckonFileSize(byteCount)+"/" +mFileSize);
                            }else {
                                fileHolder.fileSize.setText(0 + "KB /" + mFileSize);
                            }

                        }
                    }

                    mUploadInfo.setCompleteSize(mCompleteSize);
                    mUploadInfo.setByteCount(byteCount);
                    mUploadInfo.setProgress(progress);
                    mDBManager.update(mUploadInfo ,"file_path", mUploadInfo.getFile_path());
                }
                limit = progress;

            }

        }


    }
    @Override
    public void onFailure() {
        mUploadInfo.setStatus(Status.FAIL);
        mDBManager.update(mUploadInfo , "file_path" , mUploadInfo.getFile_path());
        if (holder != null && holder.transfersType != null){
            holder.transfersType.setText("上传失败");
        }
        if (fileHolder != null && fileHolder.fileTime != null){
            fileHolder.fileTime.setText("上传失败");
        }
        CountObserver.getInstance().sendNotify();
    }

    @Override
    public void onFinished() {
        mUploadInfo.setStatus(Status.SUCCESS);
        if (mUploadInfo.getCtime() == 0 ){
            mUploadInfo.setCtime(ScreenUtil.getSecondTimestampTwo());
        }

        mDBManager.update(mUploadInfo ,"file_path" ,  mUploadInfo.getFile_path());

        if (holder != null && holder.progressView !=  null){
            holder.progressView.setCurrentStep(100);
        }
        if (fileHolder != null && fileHolder.progressBar !=  null){
            fileHolder.progressBar.setProgress(100);
        }

        if (adapter != null){
            adapter.setData(mDBManager.queryUp(null , null));
        }

        CountObserver.getInstance().sendNotify();


    }

    @Override
    public boolean isDownloading() {
        return mUploadInfo.isDownloading();
    }

    public UploadInfo getUploadInfo(){
        return mUploadInfo;
    }

    public UploadInfo setUploadInfo(){
        return mUploadInfo;
    }
    /**
     * 格式化数字
     * @param value
     * @return
     */
    private String getTwoPointFloatStr(float value){
        DecimalFormat format  = new DecimalFormat("0.00");
        final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
        format.setDecimalFormatSymbols(decimalSymbol);
        return format.format(value);
    }

}
