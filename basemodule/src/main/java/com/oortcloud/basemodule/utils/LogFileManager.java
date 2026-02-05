package com.oortcloud.basemodule.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 日志文件管理器：优化存储路径（适配Android 10+分区存储），保留日志数量/大小限制
 */
public class LogFileManager {
    private static final String TAG = "LogFileManager";
    private static final int LOG_FILES_MAX_NUM = 5; // 最多保留5个日志文件
    private static final int LOG_FILE_MAX_SIZE = 1000 * 1000; // 单个日志文件最大1MB
    private static final SimpleDateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("MM-dd-HH-mm");

    private final Context mContext; // 应用上下文（用于获取合规存储路径）
    private final String mLogSubDirName; // 日志子目录名（如"OperationLogs"、"IMLogs"）
    private File mCurrentLogFile;
    private File mLogRootDir; // 合规的日志根目录（应用专属目录下）

    // 日志文件过滤器：只识别 "LogMM-dd-HH-mm.txt" 格式的文件
    private final FileFilter fileFilter = file -> {
        String fileName = file.getName().toLowerCase();
        return fileName.startsWith("log") && fileName.endsWith(".txt");
    };


    /**
     * 构造方法（推荐）：通过Context获取合规的应用专属目录
     * @param context 应用上下文（建议传Application Context，避免内存泄漏）
     * @param logSubDirName 日志子目录名（如"OperationLogs"，会创建在应用专属目录下）
     */
    public LogFileManager(Context context, String logSubDirName) {
        this.mContext = context.getApplicationContext(); // 强制用应用上下文，防止泄漏
        this.mLogSubDirName = logSubDirName;
        // 初始化合规的日志根目录（应用专属目录）
        initLogRootDir();
    }

    /**
     * 初始化合规的日志根目录：优先外部存储应用专属目录， fallback到内部存储
     */
    private void initLogRootDir() {
        // 1. 优先获取外部存储的应用专属目录（Documents子目录，无需权限）
        File externalAppDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (externalAppDir != null && externalAppDir.exists()) {
            mLogRootDir = new File(externalAppDir, mLogSubDirName);
        } else {
            // 2. 外部存储不可用，fallback到内部存储（/data/data/包名/files/）
            mLogRootDir = new File(mContext.getFilesDir(), mLogSubDirName);
            Log.w(TAG, "External storage unavailable, use internal storage: " + mLogRootDir.getAbsolutePath());
        }

        // 3. 确保日志目录存在（不存在则创建）
        if (!mLogRootDir.exists()) {
            boolean isDirCreated = mLogRootDir.mkdirs(); // 支持多级目录创建
            if (isDirCreated) {
                Log.d(TAG, "Log directory created: " + mLogRootDir.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to create log directory: " + mLogRootDir.getAbsolutePath());
            }
        }
    }


    /**
     * 写入日志到文件：自动判断文件大小，超过限制则创建新文件
     */
    public void writeLogToFile(String logMessage) {
        // 1. 检查日志目录是否有效（防止初始化失败）
        if (mLogRootDir == null || !mLogRootDir.exists()) {
            Log.e(TAG, "Log directory is invalid, skip writing log");
            return;
        }

        // 2. 检查当前日志文件：为空或超过大小限制，创建新文件
        if (mCurrentLogFile == null || mCurrentLogFile.length() >= LOG_FILE_MAX_SIZE) {
            mCurrentLogFile = getNewLogFile();
        }

        // 3. 写入日志（避免因日志问题导致崩溃）
        if (mCurrentLogFile != null) {
            FileUtils.writeToFile(logMessage, mCurrentLogFile.getPath());
        } else {
            Log.e(TAG, "Current log file is null, skip writing log");
        }
    }


    /**
     * 获取新日志文件：处理文件数量限制（超过则删除最老文件）
     */
    private File getNewLogFile() {
        // 1. 获取目录下所有符合条件的日志文件
        File[] logFiles = mLogRootDir.listFiles(fileFilter);
        if (logFiles == null || logFiles.length == 0) {
            // 无现有日志文件，直接创建新文件
            return createNewLogFile();
        }

        // 2. 按修改时间排序（老文件在前，新文件在后）
        List<File> sortedLogFiles = sortFilesByModifyTime(logFiles);
        // 3. 超过文件数量限制，删除最老的文件
        if (sortedLogFiles.size() >= LOG_FILES_MAX_NUM) {
            File oldestFile = sortedLogFiles.get(0);
            boolean isDeleted = FileUtils.delete(oldestFile);
            Log.d(TAG, "Delete oldest log file: " + oldestFile.getName() + " | Result: " + isDeleted);
        }

        // 4. 检查最新文件是否未满，未满则复用，否则创建新文件
        File newestFile = sortedLogFiles.get(sortedLogFiles.size() - 1);
        if (newestFile.length() < LOG_FILE_MAX_SIZE) {
            Log.d(TAG, "Reuse newest log file: " + newestFile.getName());
            return newestFile;
        } else {
            return createNewLogFile();
        }
    }


    /**
     * 创建新的日志文件（文件名格式：LogMM-dd-HH-mm.txt）
     */
    private File createNewLogFile() {
        // 用File构造方法代替字符串拼接，避免双斜杠问题
        String fileName = "Log" + LOG_FILE_DATE_FORMAT.format(new Date()) + ".txt";
        File newLogFile = new File(mLogRootDir, fileName);

        // 检查文件是否创建成功（依赖FileUtils.createFile）
        if (FileUtils.createFile(newLogFile.getPath()) == null) {
            Log.e(TAG, "Failed to create new log file: " + newLogFile.getAbsolutePath());
            return null;
        }

        Log.d(TAG, "Create new log file: " + newLogFile.getAbsolutePath());
        return newLogFile;
    }


    /**
     * 按文件修改时间排序（升序：老文件在前，新文件在后）
     */
    private List<File> sortFilesByModifyTime(File[] files) {
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                // 按最后修改时间升序排列
                return Long.compare(file1.lastModified(), file2.lastModified());
            }
        });
        return fileList;
    }


    /**
     * 获取当前日志目录路径（用于调试或导出）
     */
    public String getLogDirPath() {
        return mLogRootDir != null ? mLogRootDir.getAbsolutePath() : "Invalid log directory";
    }
}