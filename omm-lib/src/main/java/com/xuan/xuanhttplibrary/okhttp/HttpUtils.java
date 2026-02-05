package com.xuan.xuanhttplibrary.okhttp;

import android.content.Context;

import com.oort.weichat.BuildConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.helper.LoginSecureHelper;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.xuan.xuanhttplibrary.okhttp.builder.BaseBuilder;
import com.xuan.xuanhttplibrary.okhttp.builder.GetBuilder;
import com.xuan.xuanhttplibrary.okhttp.builder.PostBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * OkHttp工具类，管理客户端实例并提供请求构建器
 */
public class HttpUtils {

    public static String TAG = "HTTP";

    private static volatile HttpUtils instance;
    private OkHttpClient mOkHttpClient;
    private final Context context;
    private final BaseBuilder.MacGenerator defaultMacGenerator;
    private final String appVersion;

    /**
     * 私有构造函数，初始化基础依赖
     */
    private HttpUtils() {
        this.context = MyApplication.getContext();
        this.appVersion ="1";
        // 默认验参生成器（使用LoginSecureHelper）
        this.defaultMacGenerator = (ctx, params0, beforeLogin) -> {
            // 新建用于存储 String 类型值的 Map
            Map<String, String> stringParams = new HashMap<>();
            for (Map.Entry<String, Object> entry : params0.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // 将 Object 类型的值转为 String
                String stringValue = value != null ? value.toString() : "";
                stringParams.put(key, stringValue);
            }
            // 调用方法并传入转换后的 Map
            LoginSecureHelper.generateHttpParam(ctx, stringParams, beforeLogin);
            // 3. 清空原 params0 并填充更新后的 stringParams（实现“更新原参数”的效果）
            if (params0 != null) {
                params0.clear(); // 清空原参数
                // 将更新后的 stringParams 重新放入 params0
                for (Map.Entry<String, String> entry : stringParams.entrySet()) {
                    params0.put(entry.getKey(), entry.getValue());
                }
            }
        };
    }

    /**
     * 单例模式（双重校验锁）
     */
    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 创建POST请求构建器
     */
    public static PostBuilder post() {
        HttpUtils utils = getInstance();
        return new PostBuilder(
                utils.context,
                utils.defaultMacGenerator,
                utils.appVersion
        );
    }

    /**
     * 创建GET请求构建器
     */
    public static GetBuilder get() {
        HttpUtils utils = getInstance();
        return new GetBuilder(
                utils.context,
                utils.defaultMacGenerator,
                utils.appVersion
        );
    }

    /**
     * 获取OkHttpClient实例（单例+懒加载）
     */
    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS); // 新增写入超时

            // 调试模式添加日志拦截器
            if (BuildConfig.DEBUG) {

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        // 使用Log.e输出日志，可以根据需要改为其他日志级别
                        OperLogUtil.msg("lclog:" + message);
                    }
                });
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }

            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }

    /**
     * 允许自定义OkHttpClient（用于测试或特殊配置）
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }
}
