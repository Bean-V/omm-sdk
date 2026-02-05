package com.oortcloud.appstore.download;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/22 10:07
 * @version： v1.0
 * @function：下载信息
 */
public class DownLoadInfo implements Serializable {

    private String downloadurl;//下载地址

    private String filePath;//文件路径
    //下载线程数
    private int threadNum;
    //文件大小
    private long contentLength;
    //刷新频率
    private int mRate;
    //是否下载
    private boolean isDownloading = false;
    //下载文件进度
    private  int mCompleteSize = 0;

    //进度条
    private  int progress = 0;

    //保存下载线程信息
    private List<DownLoadThreadInfo>  downLoadThreadInfos = new ArrayList<>();

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public int getRate() {
        return mRate;
    }

    public void setRate(int mRate) {
        this.mRate = mRate;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getCompleteSize() {
        return mCompleteSize;
    }

    public void setCompleteSize(int mCompleteSize) {
        this.mCompleteSize = mCompleteSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public List<DownLoadThreadInfo> getDownloadThreadInfo() {
        return downLoadThreadInfos;
    }

    public void addDownloadThreadInfo(DownLoadThreadInfo downLoadInfo) {
        this.downLoadThreadInfos.add(downLoadInfo);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DownLoadInfo){
            return this.getDownloadurl().equals(((DownLoadInfo) obj).getDownloadurl());
        }

        return false ;

    }
}
