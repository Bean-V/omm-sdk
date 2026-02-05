package com.oortcloud.clouddisk.transfer.upload;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/16 09:48
 * @version： v1.0
 * @function：
 */
public class ExMultipartBody extends RequestBody {
    private RequestBody mRequestBody;
    private int mCurrentLength;
    private UploadResponseHandler handler;


    public ExMultipartBody(MultipartBody requestBody) {
        this.mRequestBody = requestBody;
    }

    public ExMultipartBody(MultipartBody requestBody, UploadResponseHandler handler) {
        this.mRequestBody = requestBody;
        this.handler = handler;

    }

    @Nullable
    @Override
    public MediaType contentType() {
        // 静态代理最终还是调用的代理对象的方法
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Log.e("msg","监听");
        // 总的长度
        //final long contentLength = contentLength();
        // BufferedSink Sink(okio 就是 io )就是一个 服务器的 输出流，我还是不知道写了多少数据

        // 代理 ForwardingSink
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {

                // 写数据都会来这里

                if(handler!=null){
                    if (handler.isDownloading()){
                        super.write(source, byteCount);
                        handler.sendProgressChangedMessage(byteCount);
                    }
                }

            }
        };

        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        mRequestBody.writeTo(bufferedSink);
        // 刷新，RealConnection 连接池
        bufferedSink.flush();
    }
}
