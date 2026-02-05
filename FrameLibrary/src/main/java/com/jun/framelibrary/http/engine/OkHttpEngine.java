package com.jun.framelibrary.http.engine;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jun.baselibrary.http.EngineCallBack;
import com.jun.baselibrary.http.HttpUtils;
import com.jun.baselibrary.http.IHttpEngine;
import com.jun.framelibrary.http.cache.CacheDataUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 16:03
 * Version 1.0
 * Description：网络框架-->使用OKHttp引擎实现类
 */
public class OkHttpEngine implements IHttpEngine {
    private static final String TAG = OkHttpEngine.class.getSimpleName();
    //切换主线程返回数据
//    private static final Handler mHandler = new Handler();
    private final static OkHttpClient mOkHttpClient = new OkHttpClient()
            .newBuilder()
            .addInterceptor(new TrafficStatsInterceptor())
            .build();
    @Override
    public void get(Context context, String url,Map<String, Object> headerParams, Map<String,
            Object> params,boolean cache, EngineCallBack callBack) {
        // 请求路径  参数 + 路径代表唯一标识
        String finalUrl = HttpUtils.jointParams(url, params);

        Log.e(TAG,"Get请求路径：-->"+ finalUrl);
        //是否从缓存中读取
        CacheDataUtils.isCache(cache,finalUrl, callBack);
        //可以省略，默认是GET请求
        Request request = new Request.Builder()
                .tag(context)
                .url(finalUrl)
                .headers(appendHeaders(headerParams))
                .build();
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
            // 两个回调方法都不是在主线程中
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException error) {
                //切换主线程
                failureHandler(callBack, error);

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resultJson = response.body().string();
                //判断缓存数据是否相同
                if(CacheDataUtils.whetherCache(cache,finalUrl, resultJson)){
                    // 内容一样，不需要在刷新界面
                    return;
                }
                //切换主线程
                responseHandler(callBack, resultJson);
                Log.e(TAG,"Get返回结果：-->"+ resultJson);
            }
        });

    }

    @Override
    public void post(Context context, String url,Map<String, Object> headerParams,Map<String,
            Object> params, boolean cache, EngineCallBack callBack) {
        String jointUrl = HttpUtils.jointParams(url,params);  //打印
        Log.e(TAG,"Post请求路径：-->"+ jointUrl);
        //是否从缓存中读取
        CacheDataUtils.isCache(cache, jointUrl, callBack);

        Request request = new Request.Builder()
                .tag(context)
                .url(url)
                .headers(appendHeaders(headerParams))
                .post(RequestParamHelper.appendBody(params))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            // 两个回调方法都不是在主线程中
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException error) {
                //切换到主线程
                failureHandler(callBack, error);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String resultJson = response.body().string();
                //判断缓存数据是否相同
                if(CacheDataUtils.whetherCache(cache, jointUrl, resultJson)){
                    // 内容一样，不需要在刷新界面
                    return;
                }
                //切换到主线程
                responseHandler(callBack, resultJson);
                Log.e(TAG,"Post返回结果：--->"+ resultJson);
//                logLargeString("TAG", resultJson);
            }
        });

    }
    /**
     * 组装请求头
     */
    protected Headers appendHeaders(Map<String, Object> headerParams) {
        Headers.Builder headers = new Headers.Builder();
        // 1. 添加固定 Headers（可选）
        headers.add("User-Agent", "MyApp/1.0 (Android)");
        headers.add("Accept-Language", "zh-CN");
        headers.add("Accept", "application/json");

        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : headerParams.entrySet()) {
                headers.add(entry.getKey(), (String) entry.getValue());
            }
        }
        return  headers.build();
    }

    /**
     * 主线程中处理错误
     */
    private void failureHandler(EngineCallBack callBack, Exception error) {
//        mHandler.post(() -> {
//            //  执行失败方法
//
//        }
//        );
        callBack.onError(error);
    }
    /**
     * 主线程中处理成功
     */
    private void responseHandler(EngineCallBack callBack, String resultJson) {
//        mHandler.post(() -> {
//            // 执行成功方法
//
//        }
//        );
        callBack.onSuccess(resultJson);
    }


    /**
     * 拦截器：
     * 在 Android 8.0（API 26）及以上版本，系统会强制检查网络请求是否使用 TrafficStats.setThreadSocketTag()
     * 标记 Socket，否则会抛出 Untagged socket detected 异常。
     */
    private static class TrafficStatsInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {

            TrafficStats.setThreadStatsTag(0x100);
            try {
                return chain.proceed(chain.request());
            } finally {
                TrafficStats.setThreadStatsTag(-1);
            }
        }
    }


}
