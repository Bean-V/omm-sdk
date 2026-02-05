package com.jun.baselibrary.cache;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一缓存管理器 - 整合内存缓存和磁盘缓存
 * 设计特点：
 * 1. 两级缓存策略：内存优先，磁盘兜底
 * 2. 命名单例模式：支持多个独立缓存实例
 * 3. 智能写入优化：同引用短路 + JSON未变更短路
 * 4. 灵活的JSON序列化：通过接口解耦具体实现
 * 使用示例：
 * UniversalCache cache = UniversalCache.of(context, "user_cache");
 * cache.put("user_info", userObject, gsonConverter);
 * User user = cache.get("user_info", User.class, gsonConverter);
 */
public class UniversalCache {
    
    /**
     * 命名单例池 - 确保每个缓存名称只有一个实例
     * 支持应用内多个模块使用独立的缓存空间
     */
    private static final ConcurrentHashMap<String, UniversalCache> INSTANCES = new ConcurrentHashMap<>();

    /**
     * 获取指定名称的缓存实例
     * @param context 应用上下文
     * @param uniqueName 唯一缓存名称
     * @return 缓存实例
     * @throws IllegalArgumentException 如果参数为null
     */
    public static UniversalCache of(Context context, String uniqueName) {
        if (context == null) throw new IllegalArgumentException("context == null");
        if (uniqueName == null) throw new IllegalArgumentException("uniqueName == null");
        Context app = context.getApplicationContext();
        return INSTANCES.computeIfAbsent(uniqueName, n -> new UniversalCache(app, n));
    }

    /**
     * 获取默认缓存实例
     * @param context 应用上下文
     * @return 默认缓存实例
     */
    public static UniversalCache getDefault(Context context) {
        return of(context, "default_cache");
    }

    /**
     * 移除指定名称的缓存实例
     * 会同时清空该实例的所有数据
     * @param uniqueName 缓存名称
     */
    public static void removeInstance(String uniqueName) {
        UniversalCache c = INSTANCES.remove(uniqueName);
        if (c != null) c.clear();
    }

    // ==================== 实例成员 ====================
    
    /**
     * 内存缓存组件 - 基于LRU算法，快速访问
     */
    private final UniversalMemoryCache memoryCache;
    
    /**
     * 磁盘缓存组件 - 持久化存储，大容量
     */
    private final UniversalDiskCache diskCache;

    /**
     * 缓存命中统计
     */
    private long hitCount = 0;
    
    /**
     * 缓存未命中统计
     */
    private long missCount = 0;

    /**
     * 私有构造函数 - 通过静态方法创建实例
     * @param context 应用上下文
     * @param uniqueName 唯一名称
     */
    private UniversalCache(Context context, String uniqueName) {
        this.memoryCache = new UniversalMemoryCache();
        this.diskCache = new UniversalDiskCache(context, uniqueName);
    }

    // ==================== 核心写入方法 ====================

    /**
     * 通用写入方法 - 智能优化写入策略
     * 优化策略：
     * 1. 同引用短路：如果内存中已有相同引用，跳过写入
     * 2. JSON未变更短路：如果JSON内容相同，跳过磁盘写入
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 值类型
     */
    public <T> void put(String key, T value) {
        if (key == null || value == null) return;
        
        // 同引用短路检查
        Object inMem = memoryCache.get(key);
        if (inMem == value) return; // 内存中已有相同引用，跳过写入
        
        // 先写入内存缓存
        memoryCache.put(key, value);
        
        // 根据类型选择磁盘存储策略
        if (value instanceof Bitmap) {
            // Bitmap直接存储为图片文件
            diskCache.putBitmap(key, (Bitmap) value);
        } else if (value instanceof String) {
            // String类型：检查JSON是否变更
            String newJson = (String) value;
            String oldJson = diskCache.getJson(key);
            if (newJson.equals(oldJson)) return; // JSON未变更，跳过磁盘写入
            diskCache.putJson(key, newJson);
        } else {
            // 其他类型：转换为JSON后检查是否变更
            String newJson = String.valueOf(value);
            String oldJson = diskCache.getJson(key);
            if (newJson.equals(oldJson)) return; // JSON未变更，跳过磁盘写入
            diskCache.putJson(key, newJson);
        }
    }

    /**
     * 带JSON转换器的写入方法
     * 默认启用优化策略
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param converter JSON转换器
     * @param <T> 值类型
     */
    public <T> void put(String key, T value, JsonConverter<T> converter) {
        put(key, value, converter, true);
    }

    /**
     * 带JSON转换器的写入方法 - 可控制优化策略
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param converter JSON转换器
     * @param skipIfUnchanged 是否跳过未变更的写入
     * @param <T> 值类型
     */
    public <T> void put(String key, T value, JsonConverter<T> converter, boolean skipIfUnchanged) {
        if (key == null || value == null) return;
        
        // 可选的同引用短路检查
        Object inMem = memoryCache.get(key);
        if (skipIfUnchanged && inMem == value) return; // 内存中已有相同引用，跳过写入
        
        // 先写入内存缓存
        memoryCache.put(key, value);
        
        if (value instanceof Bitmap) {
            // Bitmap直接存储
            diskCache.putBitmap(key, (Bitmap) value);
        } else {
            // 使用转换器生成JSON
            String newJson = (converter != null) ? converter.toJson(value) : String.valueOf(value);
            
            // 可选的JSON未变更检查
            if (skipIfUnchanged) {
                String oldJson = diskCache.getJson(key);
                if (newJson != null && newJson.equals(oldJson)) return; // JSON未变更，跳过磁盘写入
            }
            diskCache.putJson(key, newJson);
        }
    }

    /**
     * 直接写入JSON到磁盘 - 不回填内存缓存
     * 适用于只需要持久化，不需要快速访问的场景
     * 
     * @param key 缓存键
     * @param json JSON字符串
     */
    public void putJson(String key, String json) {
        if (key == null || json == null) return;
        diskCache.putJson(key, json);
    }

    /**
     * 写入Bitmap - 同时回填内存缓存
     * 包含同引用短路优化
     * 
     * @param key 缓存键
     * @param bitmap 位图对象
     */
    public void putBitmap(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) return;
        
        // 同引用短路检查
        Bitmap inMem = memoryCache.get(key);
        if (inMem == bitmap) return; // 内存中已有相同引用，跳过写入
        
        // 同时写入内存和磁盘
        memoryCache.put(key, bitmap);
        diskCache.putBitmap(key, bitmap);
    }

    // ==================== 核心读取方法 ====================
    
    /**
     * 通用读取方法 - 支持类型安全的反序列化
     * 读取策略：
     * 1. 优先从内存读取
     * 2. 内存未命中时从磁盘读取
     * 3. 磁盘命中时回填内存缓存
     * 
     * @param key 缓存键
     * @param type 目标类型
     * @param converter JSON转换器
     * @param <T> 目标类型
     * @return 缓存的值，未找到时返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type, JsonConverter<T> converter) {
        if (key == null || type == null) return null;

        // 1. 优先从内存读取
        Object inMem = memoryCache.get(key);
        if (inMem != null && type.isInstance(inMem)) { 
            hitCount++; 
            return (T) inMem; 
        }

        // 2. 特殊处理Bitmap类型
        if (Bitmap.class.isAssignableFrom(type)) {
            return (T) getBitmap(key);
        }

        // 3. 从磁盘读取JSON并反序列化
        String json = diskCache.getJson(key);
        if (json != null && converter != null) {
            T value = converter.fromJson(json);
            if (value != null) {
                // 4. 回填内存缓存
                memoryCache.put(key, value);
                hitCount++; 
                return value;
            }
        }

        missCount++; 
        return null;
    }

    /**
     * 获取JSON文本 - 带内存缓存回填
     * 适用于需要频繁访问JSON字符串的场景
     * 
     * @param key 缓存键
     * @return JSON字符串，未找到时返回null
     */
    public String getJsonCached(String key) {
        if (key == null) return null;
        
        // 优先从内存读取
        String inMem = memoryCache.get(key);
        if (inMem != null) { 
            hitCount++; 
            return inMem; 
        }
        
        // 从磁盘读取并回填内存
        String json = diskCache.getJson(key);
        if (json != null) { 
            memoryCache.put(key, json); 
            hitCount++; 
        } else { 
            missCount++; 
        }
        return json;
    }

    /**
     * 获取Bitmap - 内存优先，磁盘兜底，自动回填
     * 
     * @param key 缓存键
     * @return 位图对象，未找到时返回null
     */
    public Bitmap getBitmap(String key) {
        if (key == null) return null;
        
        // 1. 优先从内存读取
        Bitmap bm = memoryCache.get(key);
        if (bm != null) { 
            hitCount++; 
            return bm; 
        }
        
        // 2. 从磁盘读取
        bm = diskCache.getBitmap(key);
        if (bm != null) { 
            // 3. 回填内存缓存
            memoryCache.put(key, bm); 
            hitCount++; 
        } else { 
            missCount++; 
        }
        return bm;
    }

    // ==================== 工具方法 ====================
    
    /**
     * 检查键是否存在
     * @param key 缓存键
     * @return 如果存在返回true
     */
    public boolean contains(String key) {
        if (key == null) return false;
        if (memoryCache.contains(key)) return true;
        return diskCache.contains(key);
    }

    /**
     * 移除指定键的缓存
     * @param key 缓存键
     * @return 如果成功移除返回true
     */
    public boolean remove(String key) {
        if (key == null) return false;
        boolean removed = memoryCache.remove(key) != null;
        return diskCache.remove(key) || removed;
    }

    /**
     * 清空所有缓存数据
     * 同时清空内存和磁盘缓存
     */
    public void clear() {
        memoryCache.clear();
        diskCache.clear();
    }

    // ==================== 统计信息 ====================
    
    /**
     * 获取缓存命中次数
     * @return 命中次数
     */
    public long getHitCount() { return hitCount; }
    
    /**
     * 获取缓存未命中次数
     * @return 未命中次数
     */
    public long getMissCount() { return missCount; }
    
    /**
     * 获取缓存命中率
     * @return 命中率 (0.0 - 1.0)
     */
    public double getHitRate() { 
        long t = hitCount + missCount; 
        return t > 0 ? (double) hitCount / t : 0.0; 
    }
    
    /**
     * 重置统计信息
     */
    public void resetStats() { 
        hitCount = 0; 
        missCount = 0; 
    }

    // ==================== 接口定义 ====================
    
    /**
     * JSON转换器接口 - 解耦具体的JSON库实现
     * 使用示例：
     * JsonConverter<User> converter = new JsonConverter<User>() {
     *     public String toJson(User value) { return gson.toJson(value); }
     *     public User fromJson(String json) { return gson.fromJson(json, User.class); }
     * };
     * 
     * @param <T> 转换的目标类型
     */
    public interface JsonConverter<T> {
        /**
         * 将对象转换为JSON字符串
         * @param value 要转换的对象
         * @return JSON字符串
         */
        String toJson(T value);
        
        /**
         * 将JSON字符串转换为对象
         * @param json JSON字符串
         * @return 转换后的对象
         */
        T fromJson(String json);
    }
}
