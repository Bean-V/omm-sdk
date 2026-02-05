package com.oortcloud.basemodule.login.http;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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
                .baseUrl(HttpConstants.BASE_URL)//设置服务器路径
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
     *
     * @param headerMap 请求头参数
     *  @param bodyMap       BODY
     * @return String
     */
    static Observable<String> PostObservable(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.PostAppListbservable(url, requestBody ,headerMap ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> PostTableAPPbservable(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.PostTableAPPbservable(url, requestBody ,headerMap ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<String> PostFileObservable(String url, HashMap<String, Object> headerMap, File file,HashMap<String, Object> params) {

        //RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"),file);
        List<MultipartBody.Part> partList = new ArrayList<>();
        RequestBody imgBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), imgBody);


        partList.add(filePart);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("text/plain"), (String) value);

            // 创建 MultipartBody.Part 对象
            MultipartBody.Part filePart_ = MultipartBody.Part.createFormData(key, "", fileRequestBody);

            // 将 MultipartBody.Part 对象添加到集合中
            partList.add(filePart_);
            // 使用key和value做一些操作
        }

            // 读取文件内容，创建 RequestBody

//        RequestBody tokenBody = RequestBody.create(MediaType.parse("plain/text"), token);
//
//        MultipartBody.Part tokenPart = MultipartBody.Part.createFormData("accessToken","",tokenBody);


        return apiService.PostFileObservable(url ,headerMap,partList.toArray(new MultipartBody.Part[0]) ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }



}
