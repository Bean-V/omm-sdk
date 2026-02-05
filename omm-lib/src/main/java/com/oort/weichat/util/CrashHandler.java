package com.oort.weichat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 崩溃处理类：优化异常捕获逻辑，确保稳定工作
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static final CrashHandler INSTANCE = new CrashHandler();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );
    private static final ThreadLocal<SimpleDateFormat> FILE_NAME_FORMATTER = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    );
    private static final ExecutorService CRASH_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Map<String, String> crashInfos = new HashMap<>();
    private UncaughtExceptionHandler mDefaultHandler; // 保存系统默认处理器
    private Context mContext;
    private boolean isInitialized = false; // 初始化状态标记

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化崩溃处理器
     * @param context 必须传入Application Context
     */
    public void init(Context context) {
        if (context == null) {
            Log.e(TAG, "初始化失败：context为null");
            return;
        }

        // 确保使用Application Context，避免内存泄漏
        this.mContext = context.getApplicationContext();

        // 保存系统默认处理器（用于后续链式调用）
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置当前处理器为默认
        Thread.setDefaultUncaughtExceptionHandler(this);

        // 验证是否设置成功
        if (Thread.getDefaultUncaughtExceptionHandler() == this) {
            isInitialized = true;
            Log.d(TAG, "CrashHandler初始化成功");
        } else {
            Log.e(TAG, "CrashHandler设置失败，可能被其他处理器覆盖");
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 未初始化则直接交给系统处理
        if (!isInitialized) {
            Log.e(TAG, "未初始化，交给系统处理异常");
            handleByDefault(thread, ex);
            return;
        }

        // 异常为空的极端情况处理
        if (ex == null) {
            Log.w(TAG, "捕获到空异常");
            handleByDefault(thread, ex);
            return;
        }

        Log.d(TAG, "捕获到未处理异常，开始处理");

        // 同步处理日志（确保执行完成），超时时间3秒
        try {
            CRASH_EXECUTOR.submit(() -> {
                try {
                    collectCrashInfo();
                    saveCrashLogToLocal(ex);
                    Log.d(TAG, "异常处理完成");
                } catch (Exception e) {
                    Log.e(TAG, "处理异常过程中发生错误", e);
                }
            }).get(3, TimeUnit.SECONDS); // 等待处理完成，最多3秒
        } catch (Exception e) {
            Log.e(TAG, "异常处理超时或失败", e);
        } finally {
            // 最终交给系统处理，确保应用退出
            handleByDefault(thread, ex);
        }
    }

    /**
     * 交给默认处理器处理
     */
    private void handleByDefault(Thread thread, Throwable ex) {
        if (mDefaultHandler != null && mDefaultHandler != this) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 如果没有默认处理器，强制退出应用
            Log.d(TAG, "没有默认处理器，强制退出应用");
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    @SuppressLint("HardwareIds")
    private void collectCrashInfo() {
        crashInfos.clear(); // 先清空之前的信息
        collectAppVersionInfo();
        collectDeviceInfo();
    }

    private void collectAppVersionInfo() {
        if (mContext == null) return;

        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            if (pi != null) {
                crashInfos.put("app_version_name", pi.versionName == null ? "unknown" : pi.versionName);
                crashInfos.put("app_version_code", String.valueOf(pi.versionCode));
                crashInfos.put("app_package_name", mContext.getPackageName());
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "收集应用版本信息失败", e);
            crashInfos.put("app_version_error", e.getMessage());
        }
    }

    private void collectDeviceInfo() {
        crashInfos.put("crash_time", DATE_FORMATTER.get().format(new Date()));
        crashInfos.put("android_version", Build.VERSION.RELEASE);
        crashInfos.put("android_api", String.valueOf(Build.VERSION.SDK_INT));
        crashInfos.put("manufacturer", Build.MANUFACTURER);
        crashInfos.put("model", Build.MODEL);
        crashInfos.put("brand", Build.BRAND);

        // 反射收集更多设备信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                if (value != null) {
                    crashInfos.put("build_" + field.getName(), value.toString());
                }
            } catch (Exception e) {
                Log.w(TAG, "收集设备信息字段失败: " + field.getName(), e);
            }
        }
    }

    private void saveCrashLogToLocal(Throwable ex) throws Exception {
        if (mContext == null) {
            throw new IllegalStateException("context为null，无法保存日志");
        }

        String logContent = buildCrashLogContent(ex);
        if (logContent.isEmpty()) {
            Log.w(TAG, "日志内容为空，不保存");
            return;
        }

        // 获取日志目录
        File logDir = new File(
                mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "IMErrorLogs"
        );

        // 验证目录是否可用
        if (!createDirIfNotExist(logDir)) {
            // 尝试备选路径：内部存储
            logDir = new File(mContext.getFilesDir(), "IMErrorLogs");
            Log.w(TAG, "外部存储目录不可用，尝试内部存储: " + logDir.getAbsolutePath());
            if (!createDirIfNotExist(logDir)) {
                throw new RuntimeException("内部存储目录也无法创建: " + logDir.getAbsolutePath());
            }
        }

        // 创建日志文件
        String fileName = "crash-" + FILE_NAME_FORMATTER.get().format(new Date()) + ".log";
        File logFile = new File(logDir, fileName);

        // 写入日志
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            fos.write(logContent.getBytes("UTF-8"));
            fos.flush();
            Log.d(TAG, "日志保存成功: " + logFile.getAbsolutePath());
        }
    }

    private String buildCrashLogContent(Throwable ex) {
        StringBuilder sb = new StringBuilder();

        // 日志头部
        sb.append("===================== 崩溃日志开始 =====================\n");
        sb.append("崩溃时间: ").append(DATE_FORMATTER.get().format(new Date())).append("\n\n");

        // 设备和应用信息
        sb.append("-------- 设备与应用信息 --------\n");
        for (Map.Entry<String, String> entry : crashInfos.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("\n");

        // 异常堆栈
        sb.append("-------- 异常堆栈信息 --------\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        // 打印嵌套异常
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append(writer.toString());

        // 日志尾部
        sb.append("===================== 崩溃日志结束 =====================\n\n");
        return sb.toString();
    }

    private boolean createDirIfNotExist(File dir) {
        if (dir == null) return false;

        if (dir.exists()) {
            return dir.isDirectory();
        }

        // 尝试创建目录
        boolean result = dir.mkdirs();
        Log.d(TAG, "创建目录: " + dir.getAbsolutePath() + ", 结果: " + result);
        return result;
    }

    public void putCustomInfo(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            Log.w(TAG, "自定义信息key不能为空");
            return;
        }
        crashInfos.put("custom_" + key, value == null ? "unknown" : value);
    }

    public void release() {
        CRASH_EXECUTOR.shutdown();
        try {
            if (!CRASH_EXECUTOR.awaitTermination(1, TimeUnit.SECONDS)) {
                CRASH_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            CRASH_EXECUTOR.shutdownNow();
        }
        Log.d(TAG, "CrashHandler已释放资源");
    }
}
