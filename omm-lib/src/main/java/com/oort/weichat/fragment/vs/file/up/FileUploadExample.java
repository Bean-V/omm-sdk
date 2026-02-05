package com.oort.weichat.fragment.vs.file.up;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传使用示例
 * 展示如何使用改进的文件上传功能，支持单个和批量文件上传
 */
public class FileUploadExample {
    private static final String TAG = "FileUploadExample";

    /**
     * 单个文件上传示例
     */
    public static void uploadSingleFileExample(Context context, String filePath) {
        Log.i(TAG, "=== 单个文件上传示例 ===");
        
        // 1. 检查文件
        if (!FileUtils.isFileReadable(filePath)) {
            Log.e(TAG, "文件不可读: " + filePath);
            return;
        }
        
        // 2. 显示文件信息
        String fileInfo = FileUtils.getFileInfo(filePath);
        Log.i(TAG, "准备上传文件: " + fileInfo);
        
        // 3. 执行上传
        FileUtils.uploadFile(context, filePath);
    }

    /**
     * 带参数的单文件上传示例
     */
    public static void uploadSingleFileWithParamsExample(Context context, String filePath) {
        Log.i(TAG, "=== 带参数的单文件上传示例 ===");
        
        // 自定义参数
        Map<String, String> params = new HashMap<>();
        params.put("userId", "12345");
        params.put("category", "documents");
        params.put("description", "重要文档");
        
        // 自定义请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Upload-Source", "AndroidApp");
        headers.put("X-Upload-Time", String.valueOf(System.currentTimeMillis()));
        
        // 执行上传
        FileUtils.uploadFile(context, filePath, params, headers);
    }

    /**
     * 批量文件上传示例
     */
    public static void uploadMultipleFilesExample(Context context, String... filePaths) {
        Log.i(TAG, "=== 批量文件上传示例 ===");
        
        if (filePaths == null || filePaths.length == 0) {
            Log.e(TAG, "文件路径数组为空");
            return;
        }
        
        // 1. 显示批量文件信息
        String batchInfo = FileUtils.getBatchFileInfo(filePaths);
        Log.i(TAG, batchInfo);
        
        // 2. 验证文件
        for (String path : filePaths) {
            if (!FileUtils.isFileReadable(path)) {
                Log.w(TAG, "跳过不可读文件: " + path);
            }
        }
        
        // 3. 执行批量上传
        FileUtils.uploadFiles(context, filePaths);
    }

    /**
     * 带参数的批量文件上传示例
     */
    public static void uploadMultipleFilesWithParamsExample(Context context, String... filePaths) {
        Log.i(TAG, "=== 带参数的批量文件上传示例 ===");
        
        // 自定义参数
        Map<String, String> params = new HashMap<>();
        params.put("userId", "12345");
        params.put("batchId", "batch_" + System.currentTimeMillis());
        params.put("totalFiles", String.valueOf(filePaths.length));
        
        // 自定义请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Batch-Upload", "true");
        headers.put("X-Upload-Source", "AndroidApp");
        
        // 执行批量上传
        FileUtils.uploadFiles(context, params, headers, filePaths);
    }

    /**
     * 混合上传示例（单个和批量）
     */
    public static void mixedUploadExample(Context context) {
        Log.i(TAG, "=== 混合上传示例 ===");
        
        // 模拟文件路径
        String[] filePaths = {
            "/storage/emulated/0/Download/document1.pdf",
            "/storage/emulated/0/Download/image1.jpg",
            "/storage/emulated/0/Download/video1.mp4"
        };
        
        // 1. 单个文件上传
        Log.i(TAG, "开始单个文件上传...");
        uploadSingleFileExample(context, filePaths[0]);
        
        // 2. 等待一段时间后开始批量上传
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Log.i(TAG, "开始批量文件上传...");
            uploadMultipleFilesExample(context, filePaths[1], filePaths[2]);
        }, 5000); // 延迟5秒
    }

    /**
     * 智能上传示例（根据文件数量自动选择上传方式）
     */
    public static void smartUploadExample(Context context, String... filePaths) {
        Log.i(TAG, "=== 智能上传示例 ===");
        
        if (filePaths == null || filePaths.length == 0) {
            Log.e(TAG, "文件路径数组为空");
            return;
        }
        
        if (filePaths.length == 1) {
            // 单个文件，使用单文件上传
            Log.i(TAG, "检测到单个文件，使用单文件上传模式");
            uploadSingleFileExample(context, filePaths[0]);
        } else {
            // 多个文件，使用批量上传
            Log.i(TAG, "检测到多个文件，使用批量上传模式");
            uploadMultipleFilesExample(context, filePaths);
        }
    }

    /**
     * 上传进度监控示例
     */
    public static void uploadWithProgressMonitoring(Context context, String... filePaths) {
        Log.i(TAG, "=== 上传进度监控示例 ===");
        
        // 显示文件信息
        String batchInfo = FileUtils.getBatchFileInfo(filePaths);
        Log.i(TAG, batchInfo);
        
        // 开始上传
        FileUtils.uploadFiles(context, filePaths);
        
        // 这里可以添加进度监控逻辑
        Log.i(TAG, "上传已开始，请查看日志监控进度...");
    }

    /**
     * 错误处理示例
     */
    public static void uploadWithErrorHandling(Context context, String... filePaths) {
        Log.i(TAG, "=== 错误处理示例 ===");
        
        for (String path : filePaths) {
            // 检查文件
            if (!FileUtils.isFileReadable(path)) {
                Log.e(TAG, "文件不可读，跳过: " + path);
                continue;
            }
            
            // 检查文件大小
            java.io.File file = new java.io.File(path);
            long maxSize = 100 * 1024 * 1024; // 100MB
            if (file.length() > maxSize) {
                Log.w(TAG, "文件过大，跳过: " + path + " (" + FileTransfer.formatFileSize(file.length()) + ")");
                continue;
            }
            
            // 上传文件
            FileUtils.uploadFile(context, path);
        }
    }

    /**
     * 获取上传状态信息
     */
    public static String getUploadStatus(Context context, String... filePaths) {
        StringBuilder status = new StringBuilder();
        status.append("=== 上传状态信息 ===\n");
        
        // 网络信息
        if (context != null) {
            android.net.ConnectivityManager connectivityManager = 
                (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
                status.append("网络状态: ").append(isConnected ? "已连接" : "未连接").append("\n");
            }
        }
        
        // 文件信息
        if (filePaths != null && filePaths.length > 0) {
            status.append(FileUtils.getBatchFileInfo(filePaths));
        }
        
        return status.toString();
    }
}
