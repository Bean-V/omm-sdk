package com.oortcloud.basemodule.http;

import android.util.Log;

import com.google.gson.Gson;
import com.oortcloud.basemodule.constant.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/4 09:09
 */
public class SSORetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final Retrofit retrofit;

    private SSORetrofitServiceManager() {

        //连接超时时间
        //设置写操作超时时间
        //设置读操作超时时间
        OkHttpClient mOKHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(mOKHttpClient)//设置使用okhttp网络请求
                .baseUrl(Constant.BASE_URL)//设置服务器路径
                .addConverterFactory(StringConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

    }

    public static HttpAPI getApi_Service() {
        return mAPI_Service;
    }

    public static void setApi_Service(HttpAPI api_Service) {
        SSORetrofitServiceManager.mAPI_Service = api_Service;
    }

    public static HttpAPI getAPI_Service() {
        return mAPI_Service;
    }

    public static void setAPI_Service(HttpAPI mAPI_Service) {
        SSORetrofitServiceManager.mAPI_Service = mAPI_Service;
    }

    private static class SingletonHolder {
        private static final SSORetrofitServiceManager INSTANCE = new SSORetrofitServiceManager();
    }

    /*
     * 获取RetrofitServiceManager
     **/
    public static SSORetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }


    private static HttpAPI mAPI_Service = SSORetrofitServiceManager.getInstance().create(HttpAPI.class);

    /**
     * post Field 请求返回  Observable, 带请求参数 返回String
     *
     * @param url       请求地址
     *
     * @param headerMap 请求头参数
     *  @param bodyMap       BODY
     * @return String
     */
    public static Observable<String> get(String url, Map<String, Object> headerMap, Map<String, Object> bodyMap) {

        RequestBody requestBody = RequestBody.create(Objects.requireNonNull(MediaType.parse("application/json")),new Gson().toJson(bodyMap));
        return mAPI_Service.get(url, headerMap,requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

   public static Observable<String> post(String url, Map<String, Object> headerMap, Map<String, Object> bodyMap) {

        RequestBody requestBody = RequestBody.create(Objects.requireNonNull(MediaType.parse("application/json")),new Gson().toJson(bodyMap));
        return mAPI_Service.post(url, headerMap,requestBody ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
