package com.oort.weichat.ui;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * FastSharedPreferences工具类，提供异步初始化和数据读取功能
 * 解决主线程磁盘IO导致的StrictMode违规问题
 */
public class FastSharedPreferencesUtils {
    private static final String TAG = "FspUtils";
    // 单线程池用于处理所有磁盘IO操作，避免并发问题
    private static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "FastSharedPreferences-IO");
        thread.setDaemon(true); // 设为守护线程，避免阻塞应用退出
        return thread;
    });
    // 主线程Handler，用于将结果回调切换到主线程
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 异步获取FastSharedPreferences实例
     * @param fileName 存储文件名（与FastSharedPreferences.get()参数一致）
     * @param callback 结果回调（在主线程执行）
     */
    public static void getInstanceAsync(String fileName, Consumer<FastSharedPreferences> callback) {
        if (fileName == null || fileName.isEmpty()) {
            MAIN_HANDLER.post(() -> callback.accept(null));
            return;
        }

        IO_EXECUTOR.submit(() -> {
            FastSharedPreferences fsp = null;
            try {
                // 后台线程执行初始化（可能涉及磁盘操作）
                fsp = FastSharedPreferences.get(fileName);
            } catch (Exception e) {
                Log.e(TAG, "获取FastSharedPreferences实例失败: " + fileName, e);
            } finally {
                // 无论成功失败，都在主线程回调结果
                FastSharedPreferences finalFsp = fsp;
                MAIN_HANDLER.post(() -> callback.accept(finalFsp));
            }
        });
    }

    /**
     * 异步读取String类型数据
     * @param fsp FastSharedPreferences实例（需先通过getInstanceAsync获取）
     * @param key 存储键
     * @param defaultValue 默认值
     * @param callback 结果回调（主线程）
     */
    public static void getStringAsync(FastSharedPreferences fsp, String key, String defaultValue, Consumer<String> callback) {
        readValueAsync(fsp, key, defaultValue, callback, FastSharedPreferences::getString);
    }

    /**
     * 异步读取int类型数据
     * @param fsp FastSharedPreferences实例
     * @param key 存储键
     * @param defaultValue 默认值
     * @param callback 结果回调（主线程）
     */
    public static void getIntAsync(FastSharedPreferences fsp, String key, int defaultValue, Consumer<Integer> callback) {
        readValueAsync(fsp, key, defaultValue, callback, (sp, k, def) -> sp.getInt(k, def));
    }

    /**
     * 异步读取boolean类型数据
     * @param fsp FastSharedPreferences实例
     * @param key 存储键
     * @param defaultValue 默认值
     * @param callback 结果回调（主线程）
     */
    public static void getBooleanAsync(FastSharedPreferences fsp, String key, boolean defaultValue, Consumer<Boolean> callback) {
        readValueAsync(fsp, key, defaultValue, callback, (sp, k, def) -> sp.getBoolean(k, def));
    }

    /**
     * 异步读取long类型数据
     * @param fsp FastSharedPreferences实例
     * @param key 存储键
     * @param defaultValue 默认值
     * @param callback 结果回调（主线程）
     */
    public static void getLongAsync(FastSharedPreferences fsp, String key, long defaultValue, Consumer<Long> callback) {
        readValueAsync(fsp, key, defaultValue, callback, (sp, k, def) -> sp.getLong(k, def));
    }

    /**
     * 通用异步读取方法
     * @param fsp FastSharedPreferences实例
     * @param key 存储键
     * @param defaultValue 默认值
     * @param callback 结果回调
     * @param reader 读取数据的函数
     */
    private static <T> void readValueAsync(FastSharedPreferences fsp, String key, T defaultValue,
                                           Consumer<T> callback, FspReader<T> reader) {
        if (fsp == null || key == null || callback == null) {
            MAIN_HANDLER.post(() -> callback.accept(defaultValue));
            return;
        }

        IO_EXECUTOR.submit(() -> {
            T value = defaultValue;
            try {
                // 后台线程执行读取操作（可能涉及磁盘IO）
                value = reader.read(fsp, key, defaultValue);
            } catch (Exception e) {
                Log.e(TAG, "读取数据失败: " + key, e);
            } finally {
                // 主线程回调结果
                T finalValue = value;
                MAIN_HANDLER.post(() -> callback.accept(finalValue));
            }
        });
    }

    /**
     * 函数式接口，用于定义不同类型的读取逻辑
     */
    @FunctionalInterface
    private interface FspReader<T> {
        T read(FastSharedPreferences fsp, String key, T defaultValue);
    }

    /**
     * 释放资源（可选，在应用退出时调用）
     */
    public static void shutdown() {
        IO_EXECUTOR.shutdown();
    }
}

