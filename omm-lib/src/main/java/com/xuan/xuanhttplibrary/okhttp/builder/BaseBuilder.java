package com.xuan.xuanhttplibrary.okhttp.builder;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求构建基类，负责处理通用参数和请求执行
 */
public abstract class BaseBuilder {

    @NonNull
    protected final Map<String, Object> params = new LinkedHashMap<>();
    protected String url;
    protected Object tag;
    protected Request build;
    private final Context context;
    private final MacGenerator macGenerator;
    private final String appVersion;
    private final Handler mainHandler;

    /**
     * 构造函数注入依赖，解耦具体实现
     *
     * @param context      上下文
     * @param macGenerator 验参生成器
     * @param appVersion   应用版本号
     */
    public BaseBuilder(@NonNull Context context,
                       @NonNull MacGenerator macGenerator,
                       @NonNull String appVersion) {
        this.context = context;
        this.macGenerator = macGenerator;
        this.appVersion = appVersion;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 生成自定义User-Agent
     */
    protected String getUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("oort_im/");
        result.append(appVersion);
        result.append(" (Linux; U; Android ");

        // 安全处理系统版本信息
        String version = Build.VERSION.RELEASE;
        result.append(version != null && !version.isEmpty() ? version : "1.0");

        // 添加设备型号（仅正式版）
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model != null && !model.isEmpty()) {
                result.append("; ");
                result.append(model);
            }
        }

        // 添加Build ID
        String id = Build.ID;
        if (id != null && !id.isEmpty()) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");
        return result.toString();
    }

    public abstract BaseBuilder url(String url);

    public abstract BaseBuilder tag(Object tag);

    public BaseCall build() {
        return build(true);
    }

    /**
     * 是否需要添加验参
     *
     * @param mac 是否添加验参
     */
    public BaseCall build(boolean mac) {
        return build(mac, false);
    }

    /**
     * 构建请求
     *
     * @param mac         是否添加验参
     * @param beforeLogin 是否按登录前接口处理
     */
    public BaseCall build(boolean mac, Boolean beforeLogin) {
        // 添加语言参数
        String language = Locale.getDefault().getLanguage();
        if ("tw".equalsIgnoreCase(language)) {
            language = "big5";
        }
        params("language", language);

        // 添加验参
        if (mac) {
            addMac(beforeLogin);
        }
        return abstractBuild();
    }

    public abstract BaseCall abstractBuild();

    public abstract BaseBuilder params(String k, String v);

    /**
     * 添加验参
     */
    public BaseBuilder addMac(Boolean beforeLogin) {
        macGenerator.generateHttpParam(context, params, beforeLogin);
        return this;
    }

    /**
     * 验参生成器接口，解耦具体实现
     */
    public interface MacGenerator {
        void generateHttpParam(Context context, Map<String, Object> params, Boolean beforeLogin);
    }

    public class BaseCall {
        private final OkHttpClient okHttpClient;

        public BaseCall(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
        }

        /**
         * 异步执行请求，回调在主线程
         */
        public void execute(Callback callback) {
            if (build == null) {
                throw new IllegalStateException("请求未构建，请先调用build()");
            }
            okHttpClient.newCall(build).enqueue(new MainThreadCallback(callback));
        }

        /**
         * 同步执行请求，必须在工作线程调用
         */
        @WorkerThread
        public void executeSync(Callback callback) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new IllegalStateException("同步请求不能在主线程调用");
            }
            if (build == null) {
                throw new IllegalStateException("请求未构建，请先调用build()");
            }

            Call call = okHttpClient.newCall(build);
            try {
                Response response = call.execute();
                callback.onResponse(call, response);
            } catch (IOException e) {
                callback.onFailure(call, e);
            }
        }
    }

    /**
     * 将回调切换到主线程的包装类
     */
    private class MainThreadCallback implements Callback {
        private final Callback originalCallback;

        MainThreadCallback(Callback originalCallback) {
            this.originalCallback = originalCallback;
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) {
            mainHandler.post(() -> {
                try {
                    originalCallback.onResponse(call, response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            mainHandler.post(() -> originalCallback.onFailure(call, e));
        }
    }
}
