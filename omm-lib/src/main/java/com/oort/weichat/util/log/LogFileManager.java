package com.oort.weichat.util.log;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.oort.weichat.Reporter;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 日志文件管理器（IM相关日志）：优化存储路径以适配Android 10+分区存储规范
 */
public class LogFileManager {
    private static final String TAG = "LogFileManager";
    private static final int LOG_FILES_MAX_NUM = 5; // 最多保留5个日志文件
    private static final int LOG_FILE_MAX_SIZE = 1000 * 1000; // 单个文件最大1MB
    private static final SimpleDateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("MM-dd-HH-mm");

    private final Context mContext; // 应用上下文，用于获取合规存储路径
    private final String mLogSubDir; // 日志子目录名（如"IMLogs"）
    private File mCurrentLogFile;
    private File mLogRootDir; // 合规的日志根目录（应用专属目录下）

    // 日志文件过滤器：仅匹配"log开头、.txt结尾"的文件
    private final FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            String fileName = file.getName().toLowerCase();
            return fileName.startsWith("log") && fileName.endsWith(".txt");
        }
    };


    /**
     * 构造方法：使用Context获取合规的应用专属目录（替代硬编码路径）
     * @param context 应用上下文（建议传Application Context，避免内存泄漏）
     * @param logSubDir 日志子目录名（如"IMLogs"，会创建在应用专属目录下）
     */
    public LogFileManager(Context context, String logSubDir) {
        this.mContext = context.getApplicationContext(); // 强制使用应用上下文，防止内存泄漏
        this.mLogSubDir = logSubDir;
        this.mLogRootDir = initLogDir(); // 初始化合规的日志目录
    }


    /**
     * 初始化合规的日志目录（应用专属目录，无需存储权限）
     * 路径示例：/storage/emulated/0/Android/data/[包名]/files/Documents/IMLogs/
     */
    private File initLogDir() {
        // 1. 优先获取外部存储的应用专属目录（Documents子目录）
        File externalAppDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (externalAppDir != null) {
            return createLogDir(externalAppDir);
        }

        // 2. 外部存储不可用，fallback到内部存储（/data/data/[包名]/files/IMLogs/）
        Log.w(TAG, "External storage unavailable, use internal storage");
        File internalAppDir = mContext.getFilesDir();
        return createLogDir(internalAppDir);
    }


    /**
     * 创建日志子目录（确保目录存在）
     */
    private File createLogDir(File parentDir) {
        File logDir = new File(parentDir, mLogSubDir);
        // 检查并创建目录（支持多级目录）
        if (!logDir.exists() && !logDir.mkdirs()) {
            Log.e(TAG, "Failed to create log directory: " + logDir.getAbsolutePath());
        }
        return logDir;
    }


    /**
     * 写入日志到文件：自动管理文件大小和数量
     */
    public void writeLogToFile(String logMessage) {
        // 检查日志目录是否有效
        if (mLogRootDir == null || !mLogRootDir.exists()) {
            Log.e(TAG, "Log directory is invalid, skip writing");
            return;
        }

        // 若当前文件为空或已满，获取新文件
        if (mCurrentLogFile == null || mCurrentLogFile.length() >= LOG_FILE_MAX_SIZE) {
            mCurrentLogFile = getNewLogFile();
        }

        // 写入日志（避免因日志问题导致崩溃）
        if (mCurrentLogFile != null) {
            FileUtils.writeToFile(logMessage, mCurrentLogFile.getPath());
        } else {
            Log.e(TAG, "Current log file is null, skip writing");
        }
    }


    /**
     * 获取新日志文件：处理文件数量限制，删除最老文件
     */
    private File getNewLogFile() {
        File dir = mLogRootDir;
        File[] files = dir.listFiles(fileFilter);

        // 无现有日志文件，直接创建新文件
        if (files == null || files.length == 0) {
            return createNewLogFile();
        }

        // 按修改时间排序（老文件在前）
        List<File> sortedFiles = sortFiles(files);

        // 超过最大数量，删除最老的文件
        if (sortedFiles.size() >= LOG_FILES_MAX_NUM) {
            File oldestFile = sortedFiles.get(0);
            boolean isDeleted = FileUtils.delete(oldestFile);
            Log.d(TAG, "Delete oldest log: " + oldestFile.getName() + " | Result: " + isDeleted);
        }

        // 检查最新文件是否未满，未满则复用
        File lastLogFile = sortedFiles.get(sortedFiles.size() - 1);
        if (lastLogFile.length() < LOG_FILE_MAX_SIZE) {
            Log.d(TAG, "Reuse log file: " + lastLogFile.getName());
            return lastLogFile;
        } else {
            // 已满则创建新文件
            return createNewLogFile();
        }
    }


    /**
     * 创建新日志文件（使用File构造方法避免路径拼接错误）
     */
    private File createNewLogFile() {
        // 文件名格式：LogMM-dd-HH-mm.txt
        String fileName = "Log" + LOG_FILE_DATE_FORMAT.format(new Date()) + ".txt";
        // 用File构造方法代替字符串拼接，自动处理路径分隔符（避免双斜杠）
        File newFile = new File(mLogRootDir, fileName);

        // 创建文件并检查结果
        File createdFile = FileUtils.createFile(newFile.getPath());
        if (createdFile == null) {
            Reporter.post("错误日志文件生成失败，" + newFile.getAbsolutePath());
            return null;
        }

        Log.d(TAG, "Created new log file: " + newFile.getAbsolutePath());
        return newFile;
    }


    /**
     * 按文件修改时间排序（升序：老文件在前，新文件在后）
     */
    private List<File> sortFiles(File[] files) {
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return Long.compare(file1.lastModified(), file2.lastModified());
            }
        });
        return fileList;
    }


    /**
     * 获取当前日志目录路径（用于调试或日志导出）
     */
    public String getLogDirPath() {
        return mLogRootDir != null ? mLogRootDir.getAbsolutePath() : "Invalid log directory";
    }
}
