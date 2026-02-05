package com.oortcloud.basemodule.http;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.LocaleHelper;
import com.oortcloud.basemodule.utils.OperLogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhang-zhi-jun
 * @date: 2019/12/31 14:50
 */

class CustomInterceptor implements Interceptor {
    private static final Gson gson = new Gson();

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // 记录请求 URL
        OperLogUtil.msg(response.request().url().toString());

        try {
            // 获取响应体字符串
            String responseBodyString = response.body().string();
            // 解析 JSON 数据
            JsonObject jsonObject = gson.fromJson(responseBodyString, JsonObject.class);

            // 检查是否包含 code 字段
            if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200) {
                // 如果 code 为 200，不记录该响应
                OperLogUtil.msg("Response with code 200 skipped.");
            } else {
                // 记录其他响应
                OperLogUtil.msg(responseBodyString);
            }

            // 重新创建响应体，因为之前的响应体已经被消费
            MediaType contentType = response.body().contentType();
            ResponseBody newResponseBody = ResponseBody.create(contentType, responseBodyString);
            response = response.newBuilder()
                    .body(newResponseBody)
                    .build();
        } catch (Exception e) {
            // 记录异常信息
            OperLogUtil.msg(e.getLocalizedMessage());
        }

        return response;
    }
}


public class RetrofitServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 10;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    private RetrofitServiceManager() {

        this(Constant.BASE_URL);

    }


    private RetrofitServiceManager(String baseurl) {

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)//设置使用okhttp网络请求
                .baseUrl(baseurl)//设置服务器路径
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
    static Observable<String> PostObservable_url(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {

        headerMap.put("Accept-Language", LocaleHelper.getLanguage(CommonApplication.getAppContext()));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));

         HttpAPI apiService_ = new RetrofitServiceManager(Constant.BASE_3CLASSURL).create(HttpAPI.class);

        OperLogUtil.msg(url);
        return apiService_.post(url,headerMap, requestBody  ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    static Observable<String> PostObservable(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {

        headerMap.put("Accept-Language", LocaleHelper.getLanguage(CommonApplication.getAppContext()));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));

        OperLogUtil.msg(url);
        return apiService.post(url, headerMap, requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> PostTableAPPbservable(String url, HashMap<String, Object> headerMap, HashMap<String, Object> bodyMap) {
        headerMap.put("Accept-Language", LocaleHelper.getLanguage(CommonApplication.getAppContext()));
        OperLogUtil.msg(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(bodyMap));
        return apiService.post(url, headerMap ,requestBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<String> PostFileObservable(String url, HashMap<String, Object> headerMap, File file) {

        OperLogUtil.msg(url);
        //RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"),file);
        headerMap.put("Accept-Language", LocaleHelper.getLanguage(CommonApplication.getAppContext()));
        RequestBody imgBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), imgBody);
        return apiService.PostFileObservable(url, filePart ,headerMap ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }



}
