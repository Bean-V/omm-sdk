package com.jun.framelibrary.http.engine;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/9/3-12:31.
 * Version 1.0
 * Description:
 */
import android.util.Log;

import com.google.gson.Gson;

import okhttp3.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class RequestParamHelper {

    static RequestBody appendBody(Map<String, Object> params) {
        if (isJsonFormat(params)) {
            // 使用JSON请求
            return jsonBody(params);
        }
        //使用From表单
        return formBody(params);
    }
    /**
     * 判断是否应该使用JSON还是From表单
     */
    private static boolean isJsonFormat(Map<String, Object> params) {
        // 如果包含文件，用Multipart
        if (params.containsKey("requestType")){
            Object requestType = params.get("requestType");
            if (requestType instanceof String){
                String requester = (String) requestType;
                return "json".equals(requester);
            }
        }
        // 默认使用From
        return false;
    }
    /**
     * 创建JSON请求体
     */
    public static RequestBody jsonBody(Map<String, Object> params) {
        return RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(params));
    }
    /**
     * 创建JSON请求体
     */
    public static RequestBody formBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    // 添加参数
    private static void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                Object value = params.get(key);

                if (value instanceof File) {
                    // 处理文件
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 处理 List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Error processing list", e);
                    }
                } else if (isJsonString(value)) {
                    // 特殊处理：JSON字符串
                    String jsonString = value.toString();
                    builder.addFormDataPart(key, null, RequestBody
                            .create(MediaType.parse("application/json; charset=utf-8"), jsonString));
                } else {
                    // 普通字符串参数
                    assert value != null;
                    builder.addFormDataPart(key, value.toString());
                }

            }

        }
    }

    /**
     * 判断是否是JSON格式的字符串
     */
    private static boolean isJsonString(Object value) {

        if (!(value instanceof String)) {
            return false;
        }

        String str = value.toString().trim();
        if (str.isEmpty()) {
            return false;
        }
        // 简单的JSON格式检测
        return (str.startsWith("{") && str.endsWith("}")) ||
                (str.startsWith("[") && str.endsWith("]"));
    }

    /**
     * 猜测文件MIME类型
     */
    private static String guessMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "mp4":
                return "video/mp4";
            default:
                return "application/octet-stream";
        }
    }

    // 添加请求头
    private  static Headers appendHeaders(Map<String, String> headerParams) {
        Headers.Builder headersBuilder = new Headers.Builder();
        if (headerParams != null && !headerParams.isEmpty()) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return headersBuilder.build();
    }
}