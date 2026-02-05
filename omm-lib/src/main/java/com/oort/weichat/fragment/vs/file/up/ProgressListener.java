package com.oort.weichat.fragment.vs.file.up;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/24-14:42.
 * Version 1.0
 * Description:
 */
public interface ProgressListener {
    void onProgress(long currentBytes, long totalBytes, boolean done);
    void onError(String message);
    void onSuccess(String result);
}