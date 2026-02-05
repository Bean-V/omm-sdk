package com.oortcloud.basemodule.http;


import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @filename:
 * @function：网络请求接口
 * @version：
 * @author: zhang-zhi-jun
 * @date: 2019/12/30 11:34
 */
public interface HttpAPI {
    class DataResponse {
        // 假设这里有一些字段
        private String message;

        public String getMessage() {
            return message;
        }
    }

    @GET("data")
    Single<DataResponse> getData();

    @GET
    Observable<String> get(@Url String url,@HeaderMap() Map<String, Object> headerMap, @Query("") RequestBody body);

    @POST
    Observable<String> post(@Url String url, @HeaderMap() Map<String, Object> headerMap, @Body RequestBody body);

    @Multipart
    @POST
    Observable<String> PostFileObservable(@Url String url, @Part MultipartBody.Part body, @HeaderMap() HashMap<String, Object> headerMap);
}


