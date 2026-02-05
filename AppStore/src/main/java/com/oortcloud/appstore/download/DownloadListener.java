package com.oortcloud.appstore.download;

import java.io.Serializable;

/**
 *
 * @author zhangzhijun
 * @version 1.0
 * @date 2020/2/1
 * @function 事件的监听回调
 */

public interface DownloadListener extends Serializable {
    /**
     * 下载请求开始回调
     */
    public void onStarted();

    /**
     * 请求成功，下载前的准备回调
     * @param contentLength 文件长度
     * @param downloadUrl 下载地址
     */
    public void onPrepared(long contentLength, String downloadUrl);

    /**
     * 进度更新回调
     * @param progress
     * @param downloadUrl
     */
    public void onPregressChanged(int progress, String downloadUrl);

    /**
     * 下载过程中暂停的回调
     * @param progress
     * @param completeSize
     * @param downloadUrl
     */
    public void onPaused(int progress, int completeSize, String downloadUrl);
    /**
     * 下载完成回调
     * @param completeSize
     * @param downloadUrl
     */
    public void onFinished(int completeSize, String downloadUrl);

    /**
     * 下载失败回调
     */
    public void onFailure();

}
