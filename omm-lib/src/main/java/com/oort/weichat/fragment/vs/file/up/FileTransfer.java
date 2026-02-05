package com.oort.weichat.fragment.vs.file.up;

import android.net.TrafficStats;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/24-14:40.
 * Version 1.0
 * Description: 改进的文件传输类，解决网络稳定性问题
 */
public class FileTransfer {
    private static final String TAG = "FileTransfer";
    private static final int MAX_RETRY_COUNT = -1;
    private static final int RETRY_DELAY_MS = 2000;
    private static final int THREAD_TAG = 0x1000; // 自定义线程标签

    private OkHttpClient client;
    private Call currentCall;
    private int retryCount = 0;

    public FileTransfer() {
        this.client = createDefaultClient();
    }

    public FileTransfer(FileTransferConfig config) {
        this.client = createClientWithConfig(config);
    }

    public FileTransfer(OkHttpClient client) {
        this.client = client;
    }

    /**
     * 拦截器：
     */
    private static class TrafficStatsInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            /**
             * 在 Android 8.0（API 26）及以上版本，系统会强制检查网络请求是否使用 TrafficStats.setThreadSocketTag()
             * 标记 Socket，否则会抛出 Untagged socket detected 异常。
             */
            TrafficStats.setThreadStatsTag(0x100);
            try {
                return chain.proceed(chain.request());
            } finally {
                TrafficStats.setThreadStatsTag(-1);
            }
        }
    }

    /**
     * 创建默认的OkHttpClient，包含更好的网络稳定性配置
     */
    private OkHttpClient createDefaultClient() {


        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)  // 增加读取超时时间
                .writeTimeout(120, TimeUnit.SECONDS) // 增加写入超时时间
                .retryOnConnectionFailure(true)
                .addInterceptor(new TrafficStatsInterceptor())
                .addInterceptor(chain -> {
                    // 设置网络标签
                    TrafficStats.setThreadStatsTag(THREAD_TAG);
                    return chain.proceed(chain.request());
                })
                .build();
    }

    /**
     * 根据配置创建OkHttpClient
     */
    private OkHttpClient createClientWithConfig(FileTransferConfig config) {


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(config.isRetryOnConnectionFailure())
                .addInterceptor(new TrafficStatsInterceptor())
                .addInterceptor(chain -> {
                    // 设置网络标签
                    TrafficStats.setThreadStatsTag(THREAD_TAG);
                    return chain.proceed(chain.request());
                });

        return builder.build();
    }

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     * @param url      上传URL
     * @param params   额外参数
     * @param headers  请求头
     * @param listener 进度监听器
     */
    public void uploadFile(String filePath, String url, Map<String, String> params,
                           Map<String, String> headers, ProgressListener listener) {
        uploadFile(new File(filePath), url, params, headers, listener);
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param url      上传URL
     * @param params   额外参数
     * @param headers  请求头
     * @param listener 进度监听器
     */
    public void uploadFile(File file, String url, Map<String, String> params,
                           Map<String, String> headers, ProgressListener listener) {
        if (file == null || !file.exists()) {
            if (listener != null) {
                listener.onError("文件不存在");
            }
            return;
        }

        // 重置重试计数
        retryCount = 0;

        // 取消之前的请求
        cancelCurrentRequest();

        performUpload(file, url, params, headers, listener);
    }

    /**
     * 执行上传操作
     */
    private void performUpload(File file, String url, Map<String, String> params,
                               Map<String, String> headers, ProgressListener listener) {
        try {
            // 创建多部分请求体
//            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM);

            // 创建基础的RequestBody
            RequestBody fileBody = RequestBody.create(
                    MediaType.parse(getMimeType(file.getName())),
                    file
            );

            ProgressRequestBody progressRequestBody = new ProgressRequestBody(
                    fileBody,
                    new ProgressListener() {
                        @Override
                        public void onProgress(long currentBytes, long totalBytes, boolean done) {
                            if (listener != null) {
                                listener.onProgress(currentBytes, totalBytes, done);
                            }
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("zq", " onError" + message);
                            if (listener != null) {
                                listener.onError(message);
                            }
                        }

                        @Override
                        public void onSuccess(String result) {
                            // 上传成功在响应中处理
                        }
                    }
            );

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), progressRequestBody);

            // 添加额外参数
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            RequestBody requestBody = multipartBuilder.build();

            // 创建请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(requestBody);

            // 添加请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }


            // 执行请求
            currentCall = client.newCall(requestBuilder.build());
            currentCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (!call.isCanceled()) {
                        handleUploadFailure(file, url, params, headers, listener, e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    handleUploadResponse(call, response, listener);
                }
            });

        } catch (Exception e) {
            if (listener != null) {
                listener.onError("上传错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理上传失败
     */
    private void handleUploadFailure(File file, String url, Map<String, String> params,
                                     Map<String, String> headers, ProgressListener listener, IOException e) {
        String errorMessage = "上传失败: " + e.getMessage();

        // 检查是否是网络相关错误
        if (e instanceof SocketTimeoutException) {
            errorMessage = "上传超时，请检查网络连接";
        } else if (e.getMessage() != null && e.getMessage().contains("unexpected end of stream")) {
            errorMessage = "网络连接中断，正在重试...";
        }

        // 尝试重试
        if (retryCount < MAX_RETRY_COUNT && shouldRetry(e)) {
            retryCount++;
            if (listener != null) {
                listener.onError(errorMessage + " (重试 " + retryCount + "/" + MAX_RETRY_COUNT + ")");
            }

            // 延迟重试
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                performUpload(file, url, params, headers, listener);
            }, RETRY_DELAY_MS);
        } else {
            if (listener != null) {
                listener.onError(errorMessage + " (已达到最大重试次数)");
            }
        }
    }

    /**
     * 处理上传响应
     */
    private void handleUploadResponse(Call call, Response response, ProgressListener listener) throws IOException {
        try {
            if (response.isSuccessful() && listener != null) {
                String responseBody = "";
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                } catch (IOException e) {
                    Log.w(TAG, "读取响应体失败: " + e.getMessage());
                    // 即使读取响应体失败，如果状态码是成功的，也认为是上传成功
                    responseBody = "上传成功（响应体读取失败）";
                }
                listener.onSuccess("上传成功: " + responseBody);
            } else if (listener != null) {
                String errorMessage = "上传失败: " + response.code() + " " + response.message();
                try {
                    if (response.body() != null) {
                        String errorBody = response.body().string();
                        if (errorBody != null && !errorBody.isEmpty()) {
                            errorMessage += " - " + errorBody;
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "读取错误响应体失败: " + e.getMessage());
                }

                // 对于某些HTTP状态码，尝试重试
                if (shouldRetryHttpError(response.code())) {
                    handleHttpErrorRetry(response.code(), errorMessage, listener);
                } else {
                    listener.onError(errorMessage);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "处理上传响应时发生异常", e);
            if (listener != null) {
                listener.onError("处理响应失败: " + e.getMessage());
            }
        } finally {
            try {
                if (response.body() != null) {
                    response.body().close();
                }
            } catch (Exception e) {
                Log.w(TAG, "关闭响应体失败: " + e.getMessage());
            }
        }
    }

    /**
     * 判断是否应该重试
     */
    private boolean shouldRetry(Exception e) {
        if (e instanceof SocketTimeoutException) {
            return true;
        }

        String message = e.getMessage();
        if (message != null) {
            return message.contains("unexpected end of stream") ||
                    message.contains("Connection reset") ||
                    message.contains("Broken pipe") ||
                    message.contains("Network is unreachable") ||
                    message.contains("No route to host");
        }

        return false;
    }

    /**
     * 判断是否应该重试HTTP错误
     */
    private boolean shouldRetryHttpError(int statusCode) {
        // 对于这些状态码，可以尝试重试
        return statusCode == 408 || // Request Timeout
                statusCode == 429 || // Too Many Requests
                statusCode == 500 || // Internal Server Error
                statusCode == 502 || // Bad Gateway
                statusCode == 503 || // Service Unavailable
                statusCode == 504;   // Gateway Timeout
    }

    /**
     * 处理HTTP错误重试
     */
    private void handleHttpErrorRetry(int statusCode, String errorMessage, ProgressListener listener) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            if (listener != null) {
                listener.onError(errorMessage + " (HTTP " + statusCode + ", 重试 " + retryCount + "/" + MAX_RETRY_COUNT + ")");
            }

            // 延迟重试，使用指数退避
            long delay = RETRY_DELAY_MS * (long) Math.pow(2, retryCount - 1);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Log.i(TAG, "开始HTTP错误重试，状态码: " + statusCode);
                // 这里需要重新执行上传，但我们需要保存原始参数
                // 由于这里没有原始参数，我们只能记录日志
                Log.w(TAG, "HTTP错误重试需要原始上传参数，请在上层处理重试逻辑");
            }, delay);
        } else {
            if (listener != null) {
                listener.onError(errorMessage + " (HTTP " + statusCode + ", 已达到最大重试次数)");
            }
        }
    }

    /**
     * 下载文件
     *
     * @param url      下载URL
     * @param savePath 保存路径
     * @param fileName 文件名
     * @param headers  请求头
     * @param listener 进度监听器
     */
    public void downloadFile(String url, String savePath, String fileName,
                             Map<String, String> headers, ProgressListener listener) {
        // 取消之前的请求
        cancelCurrentRequest();

        try {
            // 创建请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .get();

            // 添加请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
            }

            Request request = requestBuilder.build();

            // 执行请求
            currentCall = client.newCall(request);
            currentCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (!call.isCanceled() && listener != null) {
                        listener.onError("下载失败: " + e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        if (listener != null) {
                            listener.onError("下载失败: " + response.code() + " " + response.message());
                        }
                        return;
                    }

                    // 处理文件名
                    String finalFileName = fileName;
                    if (finalFileName == null || finalFileName.isEmpty()) {
                        // 从URL或Content-Disposition头中提取文件名
                        finalFileName = extractFileName(url, response.headers());
                    }

                    // 创建保存目录
                    File saveDir = new File(savePath);
                    if (!saveDir.exists()) {
                        if (!saveDir.mkdirs()) {
                            if (listener != null) {
                                listener.onError("无法创建保存目录");
                            }
                            return;
                        }
                    }

                    // 创建文件
                    File file = new File(saveDir, finalFileName);
                    if (file.exists()) {
                        // 如果文件已存在，添加时间戳
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        String nameWithoutExt = finalFileName.contains(".") ?
                                finalFileName.substring(0, finalFileName.lastIndexOf('.')) : finalFileName;
                        String ext = finalFileName.contains(".") ?
                                finalFileName.substring(finalFileName.lastIndexOf('.') + 1) : "";
                        finalFileName = ext.isEmpty() ?
                                nameWithoutExt + "_" + timestamp :
                                nameWithoutExt + "_" + timestamp + "." + ext;
                        file = new File(saveDir, finalFileName);
                    }

                    // 获取响应体
                    okhttp3.ResponseBody body = response.body();
                    if (body == null) {
                        if (listener != null) {
                            listener.onError("响应体为空");
                        }
                        return;
                    }

                    long contentLength = body.contentLength();
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        long totalRead = 0;
                        int read;

                        while ((read = body.byteStream().read(buffer)) != -1) {
                            // 检查是否取消
                            if (call.isCanceled()) {
                                return;
                            }

                            outputStream.write(buffer, 0, read);
                            totalRead += read;

                            // 更新进度
                            if (listener != null) {
                                listener.onProgress(totalRead, contentLength, false);
                            }
                        }

                        outputStream.flush();

                        if (listener != null) {
                            listener.onProgress(totalRead, contentLength, true);
                            listener.onSuccess("下载完成: " + file.getAbsolutePath());
                        }

                    } catch (Exception e) {
                        if (listener != null) {
                            listener.onError("下载错误: " + e.getMessage());
                        }
                    }
                }
            });

        } catch (Exception e) {
            if (listener != null) {
                listener.onError("下载错误: " + e.getMessage());
            }
        }
    }

    /**
     * 从URL或响应头中提取文件名
     */
    private String extractFileName(String url, Headers headers) {
        // 首先尝试从Content-Disposition头中获取文件名
        String contentDisposition = headers.get("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            int start = contentDisposition.indexOf("filename=") + 9;
            int end = contentDisposition.indexOf(";", start);
            if (end == -1) end = contentDisposition.length();
            String filename = contentDisposition.substring(start, end).replace("\"", "");
            if (!filename.isEmpty()) {
                return filename;
            }
        }

        // 从URL中提取文件名
        if (url != null && url.contains("/")) {
            String path = url.substring(url.lastIndexOf("/") + 1);
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            if (!path.isEmpty()) {
                return path;
            }
        }

        // 默认文件名
        return "download_" + System.currentTimeMillis();
    }

    /**
     * 取消当前请求
     */
    public void cancelCurrentRequest() {
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
    }

    /**
     * 带进度监听的请求体
     */
    private static class ProgressRequestBody extends RequestBody {
        private final RequestBody requestBody;
        private final ProgressListener listener;

        public ProgressRequestBody(RequestBody requestBody, ProgressListener listener) {
            this.requestBody = requestBody;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }
//        public long totalBytesWritten = 0;
//        @Override
//        public void writeTo(BufferedSink sink) throws IOException {
//            // 使用 CountingSink 来跟踪写入进度
//            okio.ForwardingSink forwardingSink = new okio.ForwardingSink(sink) {

        /// /                public long totalBytesWritten = 0;
//
//                @Override
//                public void write(Buffer source, long byteCount) throws IOException {
//                    super.write(source, byteCount);
//
//                    totalBytesWritten += byteCount;
//
//                    if (listener != null) {
//                        listener.onProgress(totalBytesWritten, contentLength(), false);
//                    }
//                }
//            };
//            BufferedSink bufferedSink = Okio.buffer(forwardingSink);
//
//            // 写入原始请求体的数据
//            requestBody.writeTo(bufferedSink);
//            // 确保所有数据都被刷新
//            bufferedSink.flush();
//
//            // 通知完成
//            if (listener != null) {
//                listener.onProgress(totalBytesWritten, contentLength(), true);
//            }
//        }
        @Override
        public void writeTo(BufferedSink sink) throws IOException {

            okio.ForwardingSink forwardingSink = new okio.ForwardingSink(sink) {
                private long bytesWritten = 0;
                private long contentLength = 0;
                private boolean isCompleted = false;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        contentLength = contentLength();
                    }

                    bytesWritten += byteCount;

                    // 检查是否完成
                    boolean done = bytesWritten >= contentLength;

                    if (listener != null) {
                        listener.onProgress(bytesWritten, contentLength, done);
                    }

                    // 如果完成但之前没有标记，记录日志
                    if (done && !isCompleted) {
                        isCompleted = true;
                        Log.d(TAG, "文件上传写入完成，等待服务器响应...");
                    }
                }
            };
//            BufferedSink bufferedSink = Okio.buffer(forwardingSink);
//            requestBody.writeTo(bufferedSink);
//            // 确保所有数据都写入
//            bufferedSink.flush();

            //作用域 BufferedSink bufferedSink try 块结束时自动关闭，这可能会导致底层流被意外关闭
//            try (BufferedSink bufferedSink = Okio.buffer(forwardingSink)) {
                BufferedSink bufferedSink = Okio.buffer(forwardingSink);
                try {
                requestBody.writeTo(bufferedSink);
                // 确保所有数据都写入
                bufferedSink.flush();
                Log.d(TAG, "文件上传数据写入完成，等待服务器响应...");
            } catch (IOException e) {
                Log.e(TAG, "文件上传写入失败: " + e.getMessage());
                if (listener != null) {
                    listener.onError("上传错误: " + e.getMessage());
                }
                throw e;
            }
        }
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format(Locale.getDefault(), "%.1f %s",
                size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 根据文件名获取MIME类型
     *
     * @param fileName 文件名
     * @return MIME类型字符串
     */
    private String getMimeType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }

        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        switch (extension) {
            // 图片类型
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";

            // 视频类型
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "flv":
                return "video/x-flv";
            case "webm":
                return "video/webm";
            case "3gp":
                return "video/3gpp";

            // 音频类型
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "ogg":
                return "audio/ogg";
            case "aac":
                return "audio/aac";
            case "flac":
                return "audio/flac";

            // 文档类型
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "rtf":
                return "application/rtf";

            // 压缩文件类型
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            case "tar":
                return "application/x-tar";
            case "gz":
                return "application/gzip";

            // 其他常见类型
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "apk":
                return "application/vnd.android.package-archive";

            default:
                return "application/octet-stream";
        }
    }
}