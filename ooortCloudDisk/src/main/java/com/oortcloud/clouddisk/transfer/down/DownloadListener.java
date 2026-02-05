package com.oortcloud.clouddisk.transfer.down;

/**
 *
 * @author zhangzhijun
 * @version 1.0
 * @date 2020/2/1
 * @function 事件的监听回调
 */

public interface DownloadListener {
    /**
     * 下载请求开始回调
     */
    public void onStarted(long contentLength );

    /**
     * 请求成功，下载前的准备回调
     * @param contentLength 文件长度
     * @param downloadUrl 下载地址
     */
    public void onPrepared(long contentLength, String downloadUrl);

    /**
     * 进度更新回调
     * @param progress
     */
    public void onProgressChanged(int progress , long byteCount);

    /**
     * 下载过程中暂停的回调
     *
     */
    public void onPaused(long threadSizeTote);
    /**
     * 下载完成回调
     */
    public void onFinished();

    /**
     * 下载失败回调
     */
    public void onFailure();

    //是否暂停
    boolean isDownloading();

}
