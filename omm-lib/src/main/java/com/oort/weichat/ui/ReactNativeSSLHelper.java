package com.oort.weichat.ui;

import android.content.Context;
import android.util.Log;

import com.facebook.react.modules.network.OkHttpClientProvider; /**
 * 修正后的 React Native SSL 配置工具（基于 OkHttpClientProvider 源码）
 */
public class ReactNativeSSLHelper {
    private static final String TAG = "lclogReactNativeSSLHelper";
    private static boolean isConfigured = false; // 避免重复配置

    /**
     * 配置 React Native 全局 OkHttp（注入自定义工厂，无反射）
     * @param context 用于创建缓存（来自 Activity 或 Application）
     */
    public static void initReactNativeUnsafeSSL(Context context) {
        if (isConfigured) {
            Log.d(TAG, "React Native SSL 已配置，跳过重复执行");
            return;
        }

        Log.d(TAG, "开始配置 React Native SSL（基于 OkHttpClientProvider 工厂）");
        try {
            // 1. 创建自定义工厂（传入 Context 用于缓存）
            UnsafeOkHttpClientFactory factory = new UnsafeOkHttpClientFactory(context);

            // 2. 注入工厂到 OkHttpClientProvider（源码支持的方式）
            OkHttpClientProvider.setOkHttpClientFactory(factory);

            // 3. 触发 OkHttp 实例创建（避免延迟初始化导致首次请求用默认实例）
            OkHttpClientProvider.getOkHttpClient();

            isConfigured = true;
            Log.d(TAG, "React Native SSL 配置成功！");
        } catch (Exception e) {
            Log.e(TAG, "React Native SSL 配置失败", e);
        }
    }
}
