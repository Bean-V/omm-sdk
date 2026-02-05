package com.oortcloud.clouddisk.transfer.down;

import androidx.annotation.Nullable;

import com.oortcloud.clouddisk.transfer.Status;

import java.io.Serializable;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/22 10:07
 * @version： v1.0
 * @function：下载信息
 */
public class DownLoadInfo implements Serializable , Comparable {

    private String downloadUrl;//下载地址

    private String file_path;//文件路径

    private String file_name;//文件名称

    private long ctime;//文件名称
    //下载线程数
    private int threadNum;
    //文件大小
    private long contentLength;
    //刷新频率
    private int mRate;
    //是否下载
    private boolean isDownloading = false;
    //下载文件进度
    private  long mCompleteSize = 0;
    //进度条
    private  int progress = 0;
    //状态
    private int status = Status.PROGRESS;
    //上传dir路径
    private String dir ="";

    public DownLoadInfo(){}

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_Path) {
        this.file_path = file_Path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
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

    public long getCompleteSize() {
        return mCompleteSize;
    }

    public void setCompleteSize(long mCompleteSize) {
        this.mCompleteSize = mCompleteSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DownLoadInfo){
            return this.getDownloadUrl().equals(((DownLoadInfo) obj).getDownloadUrl());
        }

        return false ;

    }

    @Override
    public int compareTo(Object o) {

        return this.getStatus() == 1 ? 1 : -1;
    }
}
