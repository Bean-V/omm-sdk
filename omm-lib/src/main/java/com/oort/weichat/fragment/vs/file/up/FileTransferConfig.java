package com.oort.weichat.fragment.vs.file.up;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/24-14:43.
 * Version 1.0
 * Description:
 */
public class FileTransferConfig {
    private long connectTimeout = 30;
    private long readTimeout = 60;
    private long writeTimeout = 60;
    private boolean retryOnConnectionFailure = true;

    public FileTransferConfig() {
    }

    public FileTransferConfig(long connectTimeout, long readTimeout, long writeTimeout,
                              boolean retryOnConnectionFailure) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }

    // Getters and Setters
    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public void setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }
}
