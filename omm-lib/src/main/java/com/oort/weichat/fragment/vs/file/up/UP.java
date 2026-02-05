package com.oort.weichat.fragment.vs.file.up;


import android.util.Log;

import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/26-1:19.
 * Version 1.0
 * Description:
 */
public class UP {
    public final static  String APP_ID = "e1a36857e77c4e238703a06e0e57e7a0";
    public final static  String SECRET_KEY = "557d8735b655426cb21a4771b901de61";
    OkHttpClient client = new OkHttpClient();
    File file = new File("/storage/emulated/0/Android/data/com.oortcloud.apass_zhyl/files/Movies/aabb/VIDEO_20250826_012624180.mp4");
    public void ches(){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request.Builder request = new Request.Builder()
                .url( Constant.BASE_URL + Constant.UPLOAD_FILE)
                .post(requestBody);
        // 添加默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken", IMUserInfoUtil.getInstance().getToken());
//        headers.put("Content-Type", "multipart/form-data"); // 修改为正确的Content-Type
        headers.put("Content-Type", "application/json"); // 修改为正确的Content-Type
        headers.put("User-Agent", "OmmApp/1.0");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept", "*/*");
        //服务通用请求头配置
        headers.put("requestType","app");
        headers.put("appid", APP_ID);
        headers.put("secretkey", SECRET_KEY);
        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        client.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure (Call call, IOException e){
                // 上传失败
                Log.e("zq", "IOException---" +e.toString());
            }

            @Override
            public void onResponse (Call call, Response response) throws IOException {
                // 上传成功
                Log.e("zq", "response---" +response.body().string());
            }
        });
    }

}
