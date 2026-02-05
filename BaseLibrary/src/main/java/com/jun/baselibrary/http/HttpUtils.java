package com.jun.baselibrary.http;

import android.content.Context;
import android.util.ArrayMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 15:56
 * Version 1.0
 * Description：网络框架--- 网络请求工具类
 * <p>
 * 第三方网络框架很多,切换框架，如何解决
 * * 1.请求问题
 * * 2.回调问题
 */
public final class HttpUtils {
    //引擎
    private static IHttpEngine mHttpEngine = null;

    private final Context mContext;

    private String mUrl;

    //请求方式
    private int METHOD_MODE = METHOD_GET;
    private static final int METHOD_GET = 0x01;
    private static final int METHOD_POST = 0x02;


    //请求参数
    private final Map<String, Object> mHeaderParams;
    private final Map<String, Object> mBodyParams;
    //是否需要缓存
    private boolean mCache;

    private HttpUtils(Context context) {
        mContext = context;
        mBodyParams = new ArrayMap<>();
        mHeaderParams = new ArrayMap<>();
    }

    /**
     * 初始化引擎 在Application
     */
    public static void initEngine(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
    }

    /**
     * 切换引擎
     */
    public HttpUtils exchangeEngine(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
        return this;
    }

    public static HttpUtils with(Context context) {

        return new HttpUtils(context);
    }

    public HttpUtils url(String url) {
        mUrl = url;
        return this;
    }

    public HttpUtils get() {
        METHOD_MODE = METHOD_GET;
        return this;
    }

    public HttpUtils post() {
        METHOD_MODE = METHOD_POST;
        return this;
    }

    //添加Header参数
    public HttpUtils addHeader(String key, Object value) {
        mHeaderParams.put(key, value);
        return this;
    }

    public HttpUtils addHeader(Map<String, Object> params) {
        mHeaderParams.putAll(params);
        return this;
    }

    //添加请求body参数
    public HttpUtils addBody(String key, Object value) {
        mBodyParams.put(key, value);
        return this;
    }

    public HttpUtils addBody(Map<String, Object> params) {
        mBodyParams.putAll(params);
        return this;
    }

    public HttpUtils cache(boolean cache) {
        mCache = cache;
        return this;
    }

    public void execute() {
        execute(null);
    }

    public void execute(EngineCallBack callBack) {
        if (mHttpEngine == null) {
            throw new NullPointerException("请设置网络请求引擎");
        }
        if (callBack == null) {
            callBack = EngineCallBack.DEFAULT_ENGINE_CALLBACK;
        }
        //网络请求执行之前进行公共参数添加
        callBack.onPreExecute(mContext, mHeaderParams, mBodyParams);

        switch (METHOD_MODE) {
            case METHOD_GET:
                get(mContext, mUrl, mHeaderParams, mBodyParams, mCache, callBack);
                break;
            case METHOD_POST:
                post(mContext, mUrl, mHeaderParams, mBodyParams, mCache, callBack);
                break;
        }
    }

    private void get(Context context, String url, Map<String, Object> headerParams, Map<String, Object> bodyParams, boolean cache, EngineCallBack callBack) {
        mHttpEngine.get(context, url, headerParams, bodyParams, cache, callBack);
    }

    private void post(Context context, String url, Map<String, Object> headerParams, Map<String, Object> bodyParams, boolean cache, EngineCallBack callBack) {
        mHttpEngine.post(context, url, headerParams, bodyParams, cache, callBack);
    }

    /**
     * 拼接参数
     */
    public static String jointParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder stringBuffer = new StringBuilder(url);
        if (!url.contains("?")) {
            stringBuffer.append("?");
        } else {
            if (!url.endsWith("?")) {
                stringBuffer.append("&");
            }
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            stringBuffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        return stringBuffer.toString();
    }

    /**
     * 解析一个类的泛型信息
     */
//    public static Class<?> analysisClazzInfo(Object object) {
//        Type genType = object.getClass().getGenericSuperclass();
//        assert genType != null;
//        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//        return (Class<?>) params[0];
//    }
    public static Type analysisTypeInfo(Object object) {
        Type superclass = object.getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
        throw new IllegalArgumentException("类型参数缺失");
    }

}
