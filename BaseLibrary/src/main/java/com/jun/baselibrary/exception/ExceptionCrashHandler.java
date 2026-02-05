package com.jun.baselibrary.exception;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/26 13:26
 * Version 1.0
 * Description：全局异常收集
 */
public class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private volatile static ExceptionCrashHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private static final String TAG = "ExceptionCrashHandler";

    boolean mDEBUG = true;

    private ExceptionCrashHandler() {
    }


    public static ExceptionCrashHandler getInstance() {
        if (mInstance == null) {
            // 用来解决多并发的问题
            synchronized (ExceptionCrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new ExceptionCrashHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param debug   是否是debug模式
     */
    public void init(Context context, boolean debug) {
        this.mContext = context;
        this.mDEBUG = debug;
        Thread thread = Thread.currentThread();
        //设置当异常处理类
        thread.setUncaughtExceptionHandler(this);
        //获取默认线程处理
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        //debug 模式下，不处理
        if (mDEBUG) {
            //系统默认处理
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            // 收集崩溃日志保存到文件
            String crashFileName = saveInfoToSD(throwable);

            // 缓存崩溃日志文件
            cacheCrashFile(crashFileName);
            try {
                //系统默认处理
                mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
            } catch (Exception e) {
                Log.e(TAG, "日志--->");
            } finally {
                Log.e(TAG, "日志--->");
            }
        }
    }

    /**
     * 缓存崩溃日志文件
     *
     * @param fileName 崩溃日志文件
     */
    private void cacheCrashFile(String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        sp.edit().putString("CRASH_FILE_NAME", fileName).apply();
    }

    /**
     * 程序启动后-获取崩溃文件-把日志上传
     */
    public File getCrashFile() {
        String crashFileName = mContext.getSharedPreferences("crash",
                Context.MODE_PRIVATE).getString("CRASH_FILE_NAME", "");
        return new File(crashFileName);
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SD-card中
     */
    private synchronized String saveInfoToSD(Throwable ex) {
        String fileName = null;
        StringBuilder sb = new StringBuilder();
        // 1. 手机信息 + 应用信息   --> obtainSimpleInfo()
        for (Map.Entry<String, String> entry : obtainSimpleInfo(mContext)
                .entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }
        // 2.崩溃的详细信息
        sb.append(obtainExceptionInfo(ex));

        // 保存文件  手机应用的目录，并没有拿手机sdCard目录， 6.0 以上需要动态申请权限
        // 检查存储状态
        boolean isExternalStorageWritable =
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        File dir = null;
        if (isExternalStorageWritable) {
            // 优先使用外部存储的应用专属目录  某些设备可能返回null，回退到内部存储
            dir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }
        if (dir == null) {
            // 回退到内部存储
            dir = mContext.getFilesDir();
        }
         dir = new File(dir + File.separator + "crash" + File.separator);
        // 先删除之前的异常信息
        if (dir.exists()) {
            // 删除该目录下的所有子文件
            deleteDir(dir);
        }
        // 再从新创建文件夹
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
        }
        try {
            fileName = dir + File.separator + getAssignTime() + ".txt";
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "异常写入失败->" + e);
        }
        return fileName;
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "获取软件版本、手机版本信息>" + e);
        }
        assert mPackageInfo != null;
        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);
        map.put("MODEL", Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", Build.PRODUCT);
        //获取手机信息
        map.put("MOBILE_INFO", getMobileInfo());
        return map;
    }

    /**
     * 获取手机信息
     */
    public static String getMobileInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            // 利用反射获取 Build 的所有属性
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = Objects.requireNonNull(field.get(null)).toString();
                sb.append(name).append("=").append(value).append("\n");
                ;
            }
        } catch (Exception e) {
            Log.e(TAG, "获取手机信息->" + e);
        }
        return sb.toString();
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable 异常信息
     */
    private String obtainExceptionInfo(Throwable throwable) {
        // Java基础 异常
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    @SuppressLint("SimpleDateFormat")
    private String getAssignTime() {
        DateFormat dataFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        long currentTime = System.currentTimeMillis();
        return dataFormat.format(currentTime);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     */
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            // 递归删除目录中的子目录下
            assert children != null;
            for (File child : children) {
                boolean delete = child.delete();
            }
        }
        // 目录此时为空，可以删除
    }
}
