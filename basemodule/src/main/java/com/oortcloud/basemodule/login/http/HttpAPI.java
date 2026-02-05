package com.oortcloud.basemodule.login.http;


import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * @filename:
 * @function：网络请求接口
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:34
 */
public interface HttpAPI {

    @POST
    Observable<String> PostAppListbservable(@Url String url, @Body RequestBody body, @HeaderMap() HashMap<String, Object> headerMap);

    @POST
    Observable<String> PostTableAPPbservable(@Url String url, @Body RequestBody body, @HeaderMap() HashMap<String, Object> headerMap);

    @Multipart
    @POST
    Observable<String> PostFileObservable(@Url String url, @HeaderMap() HashMap<String, Object> headerMap, @Part MultipartBody.Part... parts);
}
