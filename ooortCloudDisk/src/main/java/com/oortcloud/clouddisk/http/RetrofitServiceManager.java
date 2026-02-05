package com.oortcloud.clouddisk.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 14:50
 */
public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 5;
    private static final int DEFAULT_WRITE_TIME = 5;
    private static final int DEFAULT_READ_TIME = 5;
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
                .baseUrl(HttpConstants.GATEWAY_URL)//设置服务器路径
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


    private static HttpAPI apiService = RetrofitServiceManager.getInstance().create(HttpAPI.class);

    /**
     * post Field 请求返回  Observable, 带请求参数 返回String
     *
     * @param url       请求地址
     * @param map       BODY
     * @param headerMap 请求头参数
     * @return String
     */
    public static Observable<String> postDepartmentObservable(String url, HashMap<String, Object> map, HashMap<String, Object> headerMap) {
        return apiService.PostDepartmentbservable(url, map, headerMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> postObservable(String url, HashMap<String, Object> bodyMap, HashMap<String, Object> headerMap) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.postObservable(url, requestBody, headerMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> postFileObservable(String url,   MultipartBody.Part body ,   MultipartBody.Part body1) {


        return apiService.postFileObservable(url, body , body1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    public static Observable<String> postFileObservable(String url,   MultipartBody body ) {


        return apiService.postFileObservable(url, body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    static class StringConverterFactory extends Converter.Factory {

        public static StringConverterFactory create() {
            return new StringConverterFactory();
        }

        @Override
        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return super.stringConverter(type, annotations, retrofit);

        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new ConfigurationServiceConverter();
        }

        final class ConfigurationServiceConverter implements Converter<ResponseBody, String> {
            @Override
            public String convert(ResponseBody value) throws IOException {
                String result = value.string();
                return result;
            }
        }
    }
}
