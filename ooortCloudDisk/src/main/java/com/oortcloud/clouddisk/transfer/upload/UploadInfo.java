package com.oortcloud.clouddisk.transfer.upload;

import com.oortcloud.clouddisk.transfer.Status;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/6 19:01
 * @version： v1.0
 * @function： 记录 上传状态信息 进度信息  源文件文件信息
 */
public class UploadInfo  implements Comparable{


    //源文件路径
    private String file_path;
    //源文件名称
    private String file_name;
    //上传路径
    private String dir;
    //文件大小
    private long contentLength;
    //当前文件上传进度
    private long completeSize;
    //当前上传进度条进度
    private int progress;
    //当前上传状态
    private int status = Status.PROGRESS;
    //上传时间
    private long ctime ;

    private long byteCount;

    //是否隐藏  主要用于上传成功数据库记录不被删除
    private  boolean isHide;  //true 隐藏 false 显示
    public UploadInfo(){}
    public UploadInfo(String file_path ,String file_name , String dir ){
        this.file_path = file_path;
        this.file_name = file_name;
        this.dir = dir;
    }

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
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
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

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public boolean isDownloading() {
        return status == Status.PROGRESS;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public long getByteCount() {
        return byteCount;
    }

    public void setByteCount(long byteCount) {
        this.byteCount = byteCount;
    }

    @Override
    public int compareTo(Object o) {

            return this.getStatus() == 1 ? 1 : -1;
    }
}

