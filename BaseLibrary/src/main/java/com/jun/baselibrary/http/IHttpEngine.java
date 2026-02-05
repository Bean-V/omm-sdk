package com.jun.baselibrary.http;

import android.content.Context;

import java.util.Map;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 15:56
 * Version 1.0
 * Description：网络框架 --请求接口
 */
public interface IHttpEngine {
    /**
     * get请求
     * @param context 上下文
     * @param url 请求地址
     * @param headerParams 请求头参
     * @param bodyParams 请求参数
     * @param cache  缓存
     * @param callBack 回调
     */
    void get(Context context,String url,Map<String, Object> headerParams,Map<String, Object> bodyParams,boolean cache, EngineCallBack callBack);
    /**
     * post请求
     * @param context 上下问
     * @param url 请求地址
     * @param headerParams 请求头参
     * @param bodyParams 请求参数
     * @param cache  缓存
     * @param callBack 回调
     */
    void post(Context context,String url,Map<String, Object> headerParams,Map<String, Object> bodyParams,boolean cache, EngineCallBack callBack);

    //上次文件

    //下载文件

    //https请求

}
