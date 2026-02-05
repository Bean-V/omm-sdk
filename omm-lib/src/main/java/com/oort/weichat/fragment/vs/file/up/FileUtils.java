package com.oort.weichat.fragment.vs.file.up;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/24-14:51.
 * Version 1.0
 * Description: 文件上传工具类
 */
public class FileUtils {

    public final static String APP_ID = "e1a36857e77c4e238703a06e0e57e7a0";
    public final static String SECRET_KEY = "557d8735b655426cb21a4771b901de61";
    private static final String TAG = "FileUtils";
    private static final int THREAD_TAG = 0x1000; // 网络标签
    
    // 初始化文件传输工具
    static FileTransferConfig config = new FileTransferConfig(30, 60, 60, true);
    private static FileTransfer fileTransfer = new FileTransfer(config);
    
    /**
     * 批量上传回调接口
     */
    public interface BatchUploadCallback {
        void onFileSuccess(String filePath, String result);
        void onFileError(String filePath, String error);
    }
    
    /**
     * 上传单个文件
     * @param context 上下文
     * @param path 文件路径
     */
    public static void uploadFile(Context context, String path) {
        uploadFile(context, path, null, null);
    }
    
    /**
     * 上传单个文件
     * @param context 上下文
     * @param path 文件路径
     * @param customParams 自定义参数
     * @param customHeaders 自定义请求头
     */
    public static void uploadFile(Context context, String path, Map<String, String> customParams, Map<String, String> customHeaders) {
        uploadSingleFile(path, context, customParams, customHeaders, null);
    }
    
    /**
     * 批量上传文件
     * @param context 上下文
     * @param paths 文件路径数组
     */
    public static void uploadFiles(Context context, String... paths) {
        uploadFiles(context, null, null, paths);
    }
    
    /**
     * 批量上传文件
     * @param context 上下文
     * @param customParams 自定义参数
     * @param customHeaders 自定义请求头
     * @param paths 文件路径数组
     */
    public static void uploadFiles(Context context, Map<String, String> customParams, Map<String, String> customHeaders, String... paths) {
        if (paths == null || paths.length == 0) {
            Log.e(TAG, "文件路径数组为空");
            return;
        }
        
        if (paths.length == 1) {
            // 单个文件，直接上传
            uploadSingleFile(paths[0], context, customParams, customHeaders, null);
        } else {
            // 多个文件，批量上传
            batchUploadFiles(context, customParams, customHeaders, paths);
        }
    }
    
    /**
     * 统一的文件验证方法
     */
    private static FileValidationResult validateFile(String path, Context context, BatchUploadCallback callback) {
        File file = new File(path);
        
        // 检查文件是否存在
        if (!file.exists()) {
            String error = "文件不存在: " + path;
            Log.e(TAG, error);
            if (callback != null) {
                callback.onFileError(path, error);
            }
            return new FileValidationResult(false, null, error);
        }
        
        // 检查文件大小
        long fileSize = file.length();
        if (fileSize == 0) {
            String error = "文件大小为0: " + path;
            Log.e(TAG, error);
            if (callback != null) {
                callback.onFileError(path, error);
            }
            return new FileValidationResult(false, null, error);
        }
        
        // 检查网络连接
        if (!isNetworkAvailable(context)) {
            String error = "网络不可用";
            Log.e(TAG, error);
            if (callback != null) {
                callback.onFileError(path, error);
            }
            return new FileValidationResult(false, null, error);
        }
        
        return new FileValidationResult(true, file, null);
    }
    
    /**
     * 构建上传参数
     */
    private static Map<String, String> buildUploadParams(Map<String, String> customParams) {
        Map<String, String> params = new HashMap<>();
        
        // 添加自定义参数
        if (customParams != null) {
            params.putAll(customParams);
        }
        
        return params;
    }
    
    /**
     * 构建上传请求头
     */
    private static Map<String, String> buildUploadHeaders(Map<String, String> customHeaders) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        headers.put("Content-Type", "application/json");
        headers.put("User-Agent", "OmmApp/1.0");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept", "*/*");
        headers.put("requestType", "app");
        headers.put("appid", APP_ID);
        headers.put("secretkey", SECRET_KEY);

        // 添加自定义请求头
        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }
        
        return headers;
    }
    
    /**
     * 获取上传URL
     */
    private static String getUploadUrl() {
        return Constant.BASE_URL + Constant.UPLOAD_FILE;
    }
    
    /**
     * 创建进度监听器
     */
    private static ProgressListener createProgressListener(File file, String path, BatchUploadCallback callback) {
        return new ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long totalBytes, boolean done) {
                int progress = totalBytes > 0 ? (int) (currentBytes * 100 / totalBytes) : 0;
                
                // 如果进度达到100%但done为false，记录这个情况
                if (progress == 100 && !done) {
                    Log.w(TAG, "上传进度达到100%但未完成，可能服务器响应有问题");
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "上传失败: " + message);
                
                if (callback != null) {
                    callback.onFileError(path, message);
                }
                
                // 检查是否是网络相关错误，如果是则重试
                if (shouldRetryUpload(message)) {
                    Log.d(TAG, "检测到可重试的错误，准备重试...");
                    retryUpload(file, getUploadUrl(), buildUploadParams(null), buildUploadHeaders(null));
                } else {
                    Log.e(TAG, "不可重试的错误: " + message);
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "上传成功: " + result);
                
                if (callback != null) {
                    callback.onFileSuccess(path, result);
                }
            }
        };
    }
    
    /**
     * 上传单个文件的具体实现
     */
    private static void uploadSingleFile(String path, Context context, Map<String, String> customParams, 
                                       Map<String, String> customHeaders, BatchUploadCallback callback) {
        
        // 验证文件
        FileValidationResult validation = validateFile(path, context, callback);
        if (!validation.isValid()) {
            return;
        }
        
        File file = validation.getFile();
        long fileSize = file.length();
        
        // 构建上传URL和参数
        String uploadUrl = getUploadUrl();
        Map<String, String> params = buildUploadParams(customParams);
        Map<String, String> headers = buildUploadHeaders(customHeaders);
        
        // 记录上传信息
        Log.d(TAG, "上传URL: " + uploadUrl);
        Log.d(TAG, "文件路径: " + path);
        Log.d(TAG, "文件大小: " + FileTransfer.formatFileSize(fileSize));

        // 创建新的FileTransfer实例，确保连接状态良好
        FileTransfer transfer = new FileTransfer(config);
        
        // 创建进度监听器
        ProgressListener progressListener = createProgressListener(file, path, callback);
        
        // 开始上传
        transfer.uploadFile(file, uploadUrl, params, headers, progressListener);
    }
    
    /**
     * 批量上传文件
     */
    private static void batchUploadFiles(Context context, Map<String, String> customParams, 
                                       Map<String, String> customHeaders, String... paths) {
        Log.i(TAG, "开始批量上传，文件数量: " + paths.length);
        
        // 检查网络连接
        if (!isNetworkAvailable(context)) {
            Log.e(TAG, "网络不可用，无法进行批量上传");
            return;
        }
        
        // 验证所有文件
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                Log.e(TAG, "文件不存在，跳过: " + path);
                continue;
            }
            if (file.length() == 0) {
                Log.e(TAG, "文件大小为0，跳过: " + path);
                continue;
            }
        }
        
        // 使用原子计数器跟踪上传进度
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        
        // 为每个文件创建上传任务
        for (int i = 0; i < paths.length; i++) {
            final String filePath = paths[i];
            final int fileIndex = i + 1;
            
            // 延迟上传，避免同时上传多个文件造成网络拥塞
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Log.i(TAG, "开始上传第 " + fileIndex + "/" + paths.length + " 个文件: " + filePath);
                
                uploadSingleFile(filePath, context, customParams, customHeaders, 
                    new BatchUploadCallback() {
                        @Override
                        public void onFileSuccess(String filePath, String result) {
                            int completed = completedCount.incrementAndGet();
                            Log.i(TAG, "文件上传成功 (" + completed + "/" + paths.length + "): " + filePath);
                            
                            if (completed == paths.length) {
                                Log.i(TAG, "所有文件上传完成！成功: " + completed + ", 失败: " + failedCount.get());
                            }
                        }
                        
                        @Override
                        public void onFileError(String filePath, String error) {
                            int failed = failedCount.incrementAndGet();
                            Log.e(TAG, "文件上传失败 (" + failed + "/" + paths.length + "): " + filePath + " - " + error);
                            
                            int completed = completedCount.get();
                            if (completed + failed == paths.length) {
                                Log.i(TAG, "批量上传结束！成功: " + completed + ", 失败: " + failed);
                            }
                        }
                    });
                    
            }, i * 1000); // 每个文件间隔1秒
        }
    }

    /**
     * 判断是否应该重试上传
     */
    private static boolean shouldRetryUpload(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        
        // 网络相关错误，可以重试
        return lowerMessage.contains("unexpected end of stream") ||
               lowerMessage.contains("timeout") ||
               lowerMessage.contains("connection") ||
               lowerMessage.contains("network") ||
               lowerMessage.contains("socket") ||
               lowerMessage.contains("broken pipe") ||
               lowerMessage.contains("connection reset") ||
               lowerMessage.contains("no route to host") ||
               lowerMessage.contains("network is unreachable") ||
               lowerMessage.contains("connection refused") ||
               lowerMessage.contains("connection timed out") ||
               lowerMessage.contains("read timeout") ||
               lowerMessage.contains("write timeout") ||
               lowerMessage.contains("connect timeout");
    }
    
    /**
     * 重试上传
     */
    private static void retryUpload(File file, String uploadUrl, Map<String, String> params, Map<String, String> headers) {
        Log.d(TAG, "尝试重试上传...");

        // 延迟重试，避免立即重试
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Log.i(TAG, "开始重试上传文件: " + file.getName());

            // 重新创建FileTransfer实例，确保使用新的连接
            FileTransfer retryTransfer = new FileTransfer(config);

            retryTransfer.uploadFile(file, uploadUrl, params, headers, new ProgressListener() {
                @Override
                public void onProgress(long currentBytes, long totalBytes, boolean done) {
                    Log.d(TAG, "重试上传进度: " + (totalBytes > 0 ? (currentBytes * 100 / totalBytes) : 0) + "%");
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "重试上传失败: " + message);
                    // 这里可以添加更多的重试逻辑，比如指数退避
                    if (shouldRetryUpload(message)) {
                        Log.d(TAG, "重试仍然失败，但错误可重试，准备第二次重试...");
                        // 可以在这里添加第二次重试逻辑
                    }
                }

                @Override
                public void onSuccess(String result) {
                    Log.i(TAG, "重试上传成功: " + result);
                }
            });
        }, 3000); // 延迟3秒重试
    }
    
    /**
     * 检查网络是否可用
     */
    private static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return true; // 如果没有context，假设网络可用
        }
        
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        
        return false;
    }
    
    /**
     * 检查文件是否可读
     */
    public static boolean isFileReadable(String path) {
        File file = new File(path);
        return file.exists() && file.canRead() && file.length() > 0;
    }
    
    /**
     * 获取文件信息
     */
    public static String getFileInfo(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "文件不存在";
        }
        
        return String.format("文件名: %s, 大小: %s, 可读: %s", 
            file.getName(), 
            FileTransfer.formatFileSize(file.length()),
            file.canRead() ? "是" : "否");
    }
    
    /**
     * 获取批量文件信息
     */
    public static String getBatchFileInfo(String... paths) {
        if (paths == null || paths.length == 0) {
            return "文件路径数组为空";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("批量文件信息 (共").append(paths.length).append("个文件):\n");
        
        long totalSize = 0;
        int validFiles = 0;
        
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            File file = new File(path);
            
            info.append(i + 1).append(". ");
            if (file.exists()) {
                long size = file.length();
                totalSize += size;
                validFiles++;
                info.append(file.getName())
                    .append(" (").append(FileTransfer.formatFileSize(size)).append(")")
                    .append(file.canRead() ? " [可读]" : " [不可读]");
            } else {
                info.append("文件不存在: ").append(path);
            }
            info.append("\n");
        }
        
        info.append("\n总计: ").append(validFiles).append("/").append(paths.length)
            .append(" 个有效文件，总大小: ").append(FileTransfer.formatFileSize(totalSize));
        
        return info.toString();
    }
    
    /**
     * 文件验证结果类
     */
    private static class FileValidationResult {
        private final boolean valid;
        private final File file;
        private final String error;
        
        public FileValidationResult(boolean valid, File file, String error) {
            this.valid = valid;
            this.file = file;
            this.error = error;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public File getFile() {
            return file;
        }
        
        public String getError() {
            return error;
        }
    }
}
