package com.oortcloud.clouddisk.http;


import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
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
    @FormUrlEncoded
    @POST
    Observable<String> PostDepartmentbservable(@Url String url, @FieldMap HashMap<String, Object> map, @HeaderMap() HashMap<String, Object> headerMap);

   @FormUrlEncoded
    @POST
    Observable<String> postOMMObservable(@Url String url, @FieldMap HashMap<String, Object> map, @HeaderMap() HashMap<String, Object> headerMap);


    @POST
    Observable<String> postObservable(@Url String url, @Body RequestBody bodyMap, @HeaderMap() HashMap<String, Object> headerMap);

    @Multipart
    @POST
    Observable<String> postFileObservable(@Url String url, @Part MultipartBody.Part part, @Part MultipartBody.Part part1);

    @Multipart
    @POST
    Observable<String> postFileObservable(@Url String url, @Part MultipartBody part);


}
