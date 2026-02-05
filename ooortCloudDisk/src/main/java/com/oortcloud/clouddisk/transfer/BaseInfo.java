package com.oortcloud.clouddisk.transfer;

import java.io.Serializable;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/17 14:16
 * @version： v1.0
 * @function：
 */
public class BaseInfo implements Serializable {
    //源文件路径
    protected String file_path;
    //源文件名称
    protected String file_name;
    //上传路径
    protected String dir;
    //文件大小
    protected long contentLength;
    //上传/下载文件进度
    protected  long mCompleteSize = 0;
    //上传/下载进度条
    protected  int progress = 0;
    //下载/上传时间
    protected long ctime ;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getCompleteSize() {
        return mCompleteSize;
    }

    public void setCompleteSize(long completeSize) {
        this.mCompleteSize = completeSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }
}
