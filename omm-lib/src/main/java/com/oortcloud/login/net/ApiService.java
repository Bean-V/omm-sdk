package com.oortcloud.login.net;


import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    @FormUrlEncoded
    @POST
    Observable<String> PostFieldObservable(@Url String url, @FieldMap HashMap<String, Object> map, @HeaderMap() HashMap<String, Object> headerMap);


    @POST
    Observable<String> Postbservable(@Url String url, @Body RequestBody map, @HeaderMap() HashMap<String, Object> headerMap);
    @GET
    Observable<String> Getbservable(@Url String url);



}
