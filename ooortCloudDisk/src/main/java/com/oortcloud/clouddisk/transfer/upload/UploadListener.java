package com.oortcloud.clouddisk.transfer.upload;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/16 09:55
 * @version： v1.0
 * @function：
 */
public interface UploadListener {

    //开始
    void onStarted(long contentLength );
    //暂停
    void onPaused();
    //进度
    void onProgress( long byteCount);
    //成功
    void onFinished();
    //失败
    void onFailure();
    //是否暂停
    boolean isDownloading();
}
