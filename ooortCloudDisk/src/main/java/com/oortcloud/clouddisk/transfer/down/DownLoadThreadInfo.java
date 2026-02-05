package com.oortcloud.clouddisk.transfer.down;

import java.io.Serializable;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/22 18:27
 * @version： v1.0
 * @function：记录下载线程信息
 */
public class DownLoadThreadInfo implements Serializable {

    private int threadid;//线程id
    private long startpos;//下载的起始位置
    private long endpos;//下载的结束位置
    private long block;//每条下载的大小
    private long downpos;//该条线程已经下载的大小
    private String file_path;//url
    private String downloadUrl;//url
    private String major_key;


    public DownLoadThreadInfo(){}
    public int getThreadid() {
        return threadid;
    }

    public void setThreadid(int threadid) {
        this.threadid = threadid;
    }

    public long getStartpos() {
        return startpos;
    }

    public void setStartpos(long startpos) {
        this.startpos = startpos;
    }

    public long getEndpos() {
        return endpos;
    }

    public void setEndpos(long endpos) {
        this.endpos = endpos;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public long getDownpos() {
        return downpos;
    }

    public void setDownpos(long downpos) {
        this.downpos = downpos;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMajor_key() {
        return major_key;
    }

    public void setMajor_key(String major_key) {
        this.major_key = major_key;
    }
}
