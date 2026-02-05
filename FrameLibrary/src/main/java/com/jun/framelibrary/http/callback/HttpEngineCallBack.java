package com.jun.framelibrary.http.callback;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jun.baselibrary.http.EngineCallBack;
import com.jun.baselibrary.http.HttpUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 21:53
 * Version 1.0
 * Description：网络请求回调 --- 处理返回数据
 */
public abstract class HttpEngineCallBack<T> implements EngineCallBack {
    public final static  String APP_ID = "e1a36857e77c4e238703a06e0e57e7a0";
    public final static  String SECRET_KEY = "557d8735b655426cb21a4771b901de61";
    public final static  String REQUEST_TYPE = "app";
    private final Type mType;

    public HttpEngineCallBack() {
        // 获取完整的泛型类型信息
        mType = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }
    //封装公共参数
    @Override
    public void onPreExecute(Context context,Map<String, Object> headerParams, Map<String, Object> params) {
        headerParams.put("appid", APP_ID);
        headerParams.put("secretkey", SECRET_KEY);
        headerParams.put("requestType",REQUEST_TYPE);

        onPreExecute();
    }

    public void onPreExecute(){

    }

    @Override
    public void onError(Exception error) {
        Log.e("TAG","onError：-->" + error.toString());
    }

    @Override
    public void onSuccess(String result) {
        try {
            // Gson解析对象  data:{"name","darren"}   data:"请求失败"
            T objResult = (T)  new Gson().fromJson(result,
                    HttpUtils.analysisTypeInfo(this));
            onSuccess(objResult);
        } catch (Exception error) {
            Log.e("TAG", "onError：-->" + error);
        }

    }
    public abstract void onSuccess(T objResult);

}
