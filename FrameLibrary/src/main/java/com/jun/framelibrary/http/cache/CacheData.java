package com.jun.framelibrary.http.cache;
/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 21:53
 * Version 1.0
 * Description：缓存的实体类
 */
public class CacheData {
    // 请求接口
    private String mUrlKey;

    // 后台返回的Json
    private String mResultJson;

    public CacheData() {

    }

    public CacheData(String urlKey, String resultJson) {
        this.mUrlKey = urlKey;
        this.mResultJson = resultJson;
    }

    public String getUrlKey() {
        return mUrlKey;
    }
    public String getResultJson() {
        return mResultJson;
    }
}
