package com.oortcloud.appstore.http;

import com.google.gson.Gson;

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
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/4 09:09
 */
public class SSORetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    private SSORetrofitServiceManager() {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(HttpConstants.BASE_SSO_URL)//设置服务器路径
                .addConverterFactory(StringConverterFactory.create())//添加转化库，默认是Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加回调库，采用RxJava
                .build();

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


    private static HttpAPI apiService = SSORetrofitServiceManager.getInstance().create(HttpAPI.class);

    /**
     * post Field 请求返回  Observable, 带请求参数 返回String
     *
     * @param url       请求地址
     *
     * @param headerMap 请求头参数
     *  @param bodyMap       BODY
     * @return String
     */
    static Observable<String> PostObservable(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.PostAppListbservable(url, requestBody ,headerMap ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
