package com.jun.baselibrary.cache;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/8-16:44.
 * Version 1.0
 * Description:
 */

import android.content.Context;
import android.graphics.Bitmap;

import java.io.Serializable;

public class UniversalCacheManager {
    private final UniversalMemoryCache memoryCache;
    private final UniversalDiskCache diskCache;

    public UniversalCacheManager(Context context) {
        memoryCache = new UniversalMemoryCache();
        diskCache = new UniversalDiskCache(context, "universal_cache");
    }

    // 加载图片
    public void loadBitmap(String url, final CacheCallback<Bitmap> callback) {
        // 1. 检查内存缓存
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            callback.onComplete(bitmap);
            return;
        }

        // 2. 检查磁盘缓存
        bitmap = diskCache.getBitmap(url);
        if (bitmap != null) {
            callback.onComplete(bitmap);
            memoryCache.put(url, bitmap);
            return;
        }
    }

    // 加载对象
    public <T extends Serializable> void loadObject(String key, final CacheCallback<T> callback) {
        // 1. 检查内存缓存
        T obj = memoryCache.get(key);
        if (obj != null) {
            callback.onComplete(obj);
            return;
        }

        // 2. 检查磁盘缓存
        obj = diskCache.getObject(key);
        if (obj != null) {
            callback.onComplete(obj);
            memoryCache.put(key, obj);
            return;
        }

    }

    // 加载JSON
    public void loadJson(String url, final CacheCallback<String> callback) {
        // 1. 检查内存缓存
        String json = memoryCache.get(url);
        if (json != null) {
            callback.onComplete(json);
            return;
        }

        // 2. 检查磁盘缓存
        json = diskCache.getJson(url);
        if (json != null) {
            callback.onComplete(json);
            memoryCache.put(url, json);
            return;
        }
    }

    public interface CacheCallback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public void clearMemoryCache() {
        memoryCache.clear();
    }

    public void clearDiskCache() {
        diskCache.clear();
    }

    public void clearAllCache() {
        clearMemoryCache();
        clearDiskCache();
    }
}