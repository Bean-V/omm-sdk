package com.oortcloud.clouddisk.transfer.down;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.oortcloud.clouddisk.adapter.DownTransferAdapter;
import com.oortcloud.clouddisk.adapter.FileAdapter;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.utils.helper.FileHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @ProjectName: ommadvance
 * @FileName: DownloadListener.java
 * @Function: 下载监听回调更新View进度
 * @Author: zhangzhijun / @CreateDate: 2020/5/17 3:57
 * @UpdateUser: 更新者 /@UpdateDate: 2020/5/17 3:57
 * @Version: 1.0
 */
public class DownloadListenerImpl implements DownloadListener {


    private String mFileSize;

    public DownTransferAdapter adapter;
    public DownTransferAdapter.ViewHolder holder;
    public FileAdapter.ViewHolder fileHolder;

    //保存下载线程信息
    private List<DownLoadThreadInfo> downLoadThreadInfoList;

    public DownLoadInfo mDownLoadInfo;

    public DownloadListenerImpl(DownLoadInfo downLoadInfo) {
        this.mDownLoadInfo = downLoadInfo;
        downLoadThreadInfoList = new ArrayList<>();

    }


    @Override
    public void onStarted(long contentLength) {
        mFileSize = FileHelper.reckonFileSize(contentLength);
        CountObserver.getInstance().sendNotify();
        if (holder != null){
            if ( holder.transfersType != null) {
                holder.transfersType.setText("正在下载");
            }

            if (holder.fileSize != null) {

                holder.fileSize.setText(0 + "KB /" + mFileSize);
            }
        }
        if (fileHolder != null){
            if ( fileHolder.fileTime != null) {
                fileHolder.fileTime.setText("正在下载");
            }

            if (fileHolder.fileSize != null) {

                fileHolder.fileSize.setText(0 + "KB /" + mFileSize);
            }
        }

    }

    @Override
    public void onPrepared(long contentLength, String downloadUrl) {

    }

    @Override
    public void onProgressChanged(int progress, long byteCount) {
        if (holder != null){
            if (holder.progressView != null) {
                holder.progressView.setCurrentStep(progress);
            }
            if (holder.fileSize != null) {
                if (byteCount != 0) {
                    holder.fileSize.setText(FileHelper.reckonFileSize(byteCount) + "/" + mFileSize);
                } else {
                    holder.fileSize.setText(0 + "KB /" + mFileSize);
                }

            }
        }
        if (fileHolder != null){
            if (fileHolder.progressBar != null) {
                fileHolder.progressBar.setProgress(progress);
            }
            if (fileHolder.fileSize != null) {
                if (byteCount != 0) {
                    fileHolder.fileSize.setText(FileHelper.reckonFileSize(byteCount) + "/" + mFileSize);
                } else {
                    fileHolder.fileSize.setText(0 + "KB /" + mFileSize);
                }

            }
        }

    }

    @Override
    public void onPaused(long threadSizeTote) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFinished() {

        if (holder != null && holder.progressView != null) {
            holder.progressView.setCurrentStep(100);
        }
        if (fileHolder != null && fileHolder.progressBar != null) {
            fileHolder.progressBar.setProgress(100);
        }

        if (adapter != null) {
            adapter.setData(DBManager.getInstance().queryDown(null , null));
        }


        CountObserver.getInstance().sendNotify();

        deleteThreadInfo();


    }

    @Override
    public void onFailure() {
        if (holder != null && holder.transfersType != null){
            holder.transfersType.setText("下载失败");
        }
        if (fileHolder != null && fileHolder.fileTime != null){
            fileHolder.fileTime.setText("下载失败");
        }

    }

    @Override
    public boolean isDownloading() {
        return mDownLoadInfo.getStatus() == Status.PROGRESS;
    }


    public List<DownLoadThreadInfo> getDownloadThreadInfo() {
        return downLoadThreadInfoList;
    }

    public void addDownloadThreadInfo(DownLoadThreadInfo downLoadInfo) {
        this.downLoadThreadInfoList.add(downLoadInfo);
    }

    public void addAllThreadInfo(List<DownLoadThreadInfo> downLoadThreadInfoList) {
        this.downLoadThreadInfoList = downLoadThreadInfoList;
    }

    public void deleteThreadInfo(){

        if (downLoadThreadInfoList != null){
            DBManager dbManager = DBManager.getInstance();
            for (DownLoadThreadInfo threadInfo : downLoadThreadInfoList){
                dbManager.deleteThread( "major_key" , threadInfo.getMajor_key());
            }
            downLoadThreadInfoList.clear();
        }

    }
    /**
     * 格式化数字
     *
     * @param value
     * @return
     */
    private String getTwoPointFloatStr(float value) {
        DecimalFormat fnum = new DecimalFormat("0.00");
        final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
        fnum.setDecimalFormatSymbols(decimalSymbol);
        return fnum.format(value);
    }

}
