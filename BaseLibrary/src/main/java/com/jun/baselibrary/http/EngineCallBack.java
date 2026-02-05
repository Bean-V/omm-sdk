package com.jun.baselibrary.http;

import android.content.Context;

import java.util.Map;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 15:57
 * Version 1.0
 * Description：网络框架 请求回调接口
 */
public interface EngineCallBack {
    //开始请求，处理公共参数
    void onPreExecute(Context context,Map<String, Object> headerParams, Map<String, Object> bodyParams);
    //请求成功回调
    void onSuccess(String result);
    //请求失败回调
    void onError(Exception error);

    /**
     * 默认回调
     */
    EngineCallBack DEFAULT_ENGINE_CALLBACK = new EngineCallBack() {
        @Override
        public void onPreExecute(Context context,Map<String, Object> headerParams, Map<String, Object> params) {

        }

        @Override
        public void onSuccess(String data) {

        }

        @Override
        public void onError(Exception error) {

        }

    };
}
