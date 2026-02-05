package com.oortcloud.clouddisk.bean;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/7 17:36
 * @version： v1.0
 * @function：
 */
public class BlockData {
    //是否上传完成
    private boolean file_up_finish;
    //偏移
    private long offset;

    public boolean isFile_up_finish() {
        return file_up_finish;
    }

    public void setFile_up_finish(boolean file_up_finish) {
        this.file_up_finish = file_up_finish;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
