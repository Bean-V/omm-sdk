package com.xuan.xuanhttplibrary.okhttp.builder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.oort.weichat.Reporter;
import com.oort.weichat.util.log.LogUtils;
import com.oortcloud.basemodule.constant.Constant;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * POST请求构建器，支持纯JSON数组作为请求体（无外层key）
 */
public class PostBuilder extends BaseBuilder {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    // 新增：纯JSON数组请求体（优先级高于params）
    private Object jsonArrayBody;
    // 是否使用表单提交（默认true）
    private boolean useFormFormat = true;

    public PostBuilder(Context context, MacGenerator macGenerator, String appVersion) {
        super(context, macGenerator, appVersion);
    }

    @Override
    public PostBuilder url(String url) {
        LogUtils.d("PostBuilder", url);
        if (!TextUtils.isEmpty(url)) {
            this.url = url;
        }
        return this;
    }

    @Override
    public PostBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 新增：设置纯JSON数组作为请求体（无外层key）
     * 支持：List、数组等可转换为JSON数组的对象
     */
    public PostBuilder jsonArrayBody(Object jsonArray) {
        this.jsonArrayBody = jsonArray;
        this.useFormFormat = false; // 启用JSON提交模式
        return this;
    }

    @Override
    public BaseCall abstractBuild() {
        if (TextUtils.isEmpty(url)) {
            Log.e(HttpUtils.TAG, "POST请求URL不能为空");
            throw new IllegalArgumentException("POST请求URL不能为空");
        }

        RequestBody requestBody;
        if (!useFormFormat && jsonArrayBody != null) {
            // 核心：直接使用JSON数组作为请求体（无外层key）
            requestBody = buildPureJsonArrayBody();
        } else {
            // 原有表单提交逻辑
            FormBody.Builder formBuilder = new FormBody.Builder();
            appenParams(formBuilder);
            requestBody = formBuilder.build();
        }

        build = new Request.Builder()
                .header("User-Agent", getUserAgent())
                .addHeader("appid", Constant.APP_ID)
                .addHeader("secretkey", Constant.SECRET_KEY)
                .addHeader("requestType", "app")
                .url(url)
                .tag(tag)
                .post(requestBody)
                .build();

        return new BaseCall(HttpUtils.getInstance().getOkHttpClient());
    }

    /**
     * 构建纯JSON数组请求体（无外层key）
     */
    private RequestBody buildPureJsonArrayBody() {
        try {
            // 直接将数组/集合转换为JSON数组字符串（无外层key）
            String json = JSON.toJSONString(jsonArrayBody);
            Log.i(HttpUtils.TAG, "纯JSON数组请求体: " + json);
            return RequestBody.create(json, MEDIA_TYPE_JSON);
        } catch (Exception e) {
            Log.e(HttpUtils.TAG, "JSON数组转换失败", e);
            Reporter.unreachable(e);
            return RequestBody.create("[]", MEDIA_TYPE_JSON); // 失败时返回空数组
        }
    }

    // 以下为原有代码（保持不变）
    private void appenParams(FormBody.Builder builder) {
        if (params.isEmpty()) {
            Log.i(HttpUtils.TAG, "POST请求无参数");
            return;
        }

        StringBuffer logSb = new StringBuffer(url).append("?");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (TextUtils.isEmpty(key) || value == null) {
                Log.w(HttpUtils.TAG, "忽略空参数（key为空或value为null）");
                continue;
            }

            if (value.getClass().isArray()) {
                handleArrayParam(key, value, builder, logSb);
            } else if (value instanceof Collection) {
                handleCollectionParam(key, (Collection<?>) value, builder, logSb);
            } else {
                String valueStr = convertObjectToString(value);
                String encodedValue = encodeParamValue(valueStr);
                builder.addEncoded(key, encodedValue);
                logSb.append(key).append("=").append(encodedValue).append("&");
            }
        }

        if (logSb.charAt(logSb.length() - 1) == '&') {
            logSb.deleteCharAt(logSb.length() - 1);
        }
        Log.i(HttpUtils.TAG, "表单请求参数：" + logSb.toString());
    }

    private void handleArrayParam(String key, Object array, FormBody.Builder builder, StringBuffer logSb) {
        if (array instanceof Object[]) {
            Object[] objArray = (Object[]) array;
            for (Object item : objArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else if (array instanceof int[]) {
            int[] intArray = (int[]) array;
            for (int item : intArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else if (array instanceof long[]) {
            long[] longArray = (long[]) array;
            for (long item : longArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else if (array instanceof float[]) {
            float[] floatArray = (float[]) array;
            for (float item : floatArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else if (array instanceof double[]) {
            double[] doubleArray = (double[]) array;
            for (double item : doubleArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else if (array instanceof boolean[]) {
            boolean[] boolArray = (boolean[]) array;
            for (boolean item : boolArray) {
                addSingleParam(key, item, builder, logSb);
            }
        } else {
            addSingleParam(key, array, builder, logSb);
        }
    }

    private void handleCollectionParam(String key, Collection<?> collection, FormBody.Builder builder, StringBuffer logSb) {
        for (Object item : collection) {
            addSingleParam(key, item, builder, logSb);
        }
    }

    private void addSingleParam(String key, Object value, FormBody.Builder builder, StringBuffer logSb) {
        String valueStr = convertObjectToString(value);
        String encodedValue = encodeParamValue(valueStr);
        builder.addEncoded(key, encodedValue);
        logSb.append(key).append("=").append(encodedValue).append("&");
    }

    private String convertObjectToString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Integer || value instanceof Long ||
                value instanceof Float || value instanceof Double ||
                value instanceof Boolean || value instanceof Character) {
            return value.toString();
        }
        return value.toString();
    }

    private String encodeParamValue(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            Log.e(HttpUtils.TAG, "参数编码失败: " + value, e);
            Reporter.unreachable(e);
            return value;
        }
    }

    @Override
    public PostBuilder params(String k, String v) {
        if (!TextUtils.isEmpty(k)) {
            params.put(k, v == null ? "" : v);
        }
        return this;
    }

    public PostBuilder params(String key, Object value) {
        if (!TextUtils.isEmpty(key)) {
            params.put(key, value);
        }
        return this;
    }

    public PostBuilder params(Map<?, ?> paramsMap) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return this;
        }
        for (Map.Entry<?, ?> entry : paramsMap.entrySet()) {
            String key = Objects.toString(entry.getKey(), "");
            Object value = entry.getValue();
            this.params(key, value);
        }
        return this;
    }

    public <T> PostBuilder params(String key, T[] array) {
        if (!TextUtils.isEmpty(key) && array != null && array.length > 0) {
            params.put(key, array);
        }
        return this;
    }

    public <T> PostBuilder params(String key, Collection<T> collection) {
        if (!TextUtils.isEmpty(key) && collection != null && !collection.isEmpty()) {
            params.put(key, collection);
        }
        return this;
    }

    public PostBuilder stringParams(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                this.params(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
}
