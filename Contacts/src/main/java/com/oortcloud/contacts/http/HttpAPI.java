package com.oortcloud.contacts.http;


import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
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
    Observable<String> postDepartmentObservable(@Url String url, @HeaderMap() HashMap<String, Object> headerMap, @FieldMap HashMap<String, Object> bodyMap);

    @FormUrlEncoded
    @POST
    Observable<String> postDepartmentObservable(@Url String url, @HeaderMap() HashMap<String, Object> headerMap, @FieldMap HashMap<String, Object> map, @Body RequestBody body);

    @FormUrlEncoded
    @POST
    Observable<String> postOMMObservable(@Url String url, @FieldMap HashMap<String, Object> map, @HeaderMap() HashMap<String, Object> headerMap);

    @POST
    Observable<String> postObservable(@Url String url, @HeaderMap() HashMap<String, Object> headerMap,   @Body RequestBody body);

}
