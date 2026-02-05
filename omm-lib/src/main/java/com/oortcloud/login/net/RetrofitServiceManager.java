package com.oortcloud.login.net;


import com.google.gson.Gson;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.login.net.utils.StringConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @filename: RetrofitServiceManager.java
 * @function： 只适用于登录的接口, 该接口Header不能传值accessToken, 请注意使用
 * @version：
 * @author: zhangzhijun
 * @date: 2019/11/4 10:57
 */
public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    private RetrofitServiceManager() {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(Constant.BASE_URL)//设置服务器路径
                .addConverterFactory(StringConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /*
     * 获取RetrofitServiceManager
     **/
    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }


    private static ApiService apiService = RetrofitServiceManager.getInstance().create(ApiService.class);

    /**
     * post Field 请求返回  Observable, 带请求参数 返回String
     *
     * @param url       请求地址
     * @param map       BODY
     * @param headerMap 请求头参数
     * @return String
     */
    static Observable<String> PostFieldObservable(String url, HashMap<String, Object> map, HashMap<String, Object> headerMap) {

        OperLogUtil.msg(url);
        return apiService.PostFieldObservable(url, map, headerMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    static Observable<String> Postbservable(String url, HashMap<String, Object> bodyMap, HashMap<String, Object> headerMap) {
        OperLogUtil.msg(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.Postbservable(url, requestBody, headerMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
