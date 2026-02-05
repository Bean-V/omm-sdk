package com.jun.baselibrary.cache;

import android.graphics.Bitmap;
import android.util.LruCache;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通用内存缓存类 - 基于LRU算法的快速内存缓存
 * 设计特点：
 * 1. 基于Android LruCache实现，自动管理内存
 * 2. 智能大小计算：Bitmap、String、byte[]等类型的大小精确计算
 * 3. 兼容性处理：支持低版本Android的Bitmap大小计算
 * 4. 统计功能：提供命中率、命中次数等统计信息
 * 5. 线程安全：使用AtomicLong保证统计数据的线程安全
 * 内存分配策略：
 * - 默认分配应用最大内存的1/8作为缓存大小
 * - 可根据实际需求调整缓存大小
 * 使用示例：
 * UniversalMemoryCache cache = new UniversalMemoryCache();
 * cache.put("key", bitmap);
 * Bitmap bitmap = cache.get("key");
 * 
 * @author zhang-zhi-jun
 * @email 465571041@qq.com
 * @version 1.0
 * @since 2025/8/8
 */
public class UniversalMemoryCache {
    
    /**
     * 核心LRU缓存实例
     * 使用String作为键，Object作为值，支持任意类型的数据
     */
    private final LruCache<String, Object> memoryCache;
    
    /**
     * 缓存命中次数统计 - 线程安全
     */
    private final AtomicLong hitCount = new AtomicLong(0);
    
    /**
     * 缓存未命中次数统计 - 线程安全
     */
    private final AtomicLong missCount = new AtomicLong(0);

    /**
     * 默认构造函数
     * 自动计算并分配合适的缓存大小
     */
    public UniversalMemoryCache() {
        // 获取应用最大可用内存（KB）
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 分配1/8作为缓存大小，平衡性能和内存占用
        final int cacheSize = maxMemory / 8;

        // 创建LRU缓存实例，重写sizeOf方法实现智能大小计算
        memoryCache = new LruCache<String, Object>(cacheSize) {
            
            /**
             * 重写大小计算方法，为不同类型提供精确的内存占用计算
             * 
             * @param key 缓存键（此方法中未使用）
             * @param value 缓存值
             * @return 该值占用的内存大小（KB）
             */
            @Override
            protected int sizeOf(String key, Object value) {
                if (value instanceof Bitmap) {
                    // Bitmap大小计算 - 兼容性处理
                    Bitmap bitmap = (Bitmap) value;
                    // API 19+ 使用getByteCount()，更准确
                    return bitmap.getByteCount() / 1024;
                } else if (value instanceof String) {
                    // String大小计算 - 使用实际字节数，更准确
                    // +1 确保即使很小的字符串也能被正确计算
                    return ((String) value).getBytes().length / 1024 + 1;
                } else if (value instanceof byte[]) {
                    // byte数组大小计算 - 直接使用数组长度
                    // +1 确保即使很小的数组也能被正确计算
                    return ((byte[]) value).length / 1024 + 1;
                } else {
                    // 其他对象类型 - 按固定大小计算（简化处理）
                    // 对于复杂对象，可以考虑使用反射获取实际字段大小
                    return 1;
                }
            }

            /**
             * 缓存项被移除时的回调方法
             * 可以在这里添加清理逻辑，如Bitmap回收等
             * 
             * @param evicted 是否因为缓存满而被移除
             * @param key 被移除的键
             * @param oldValue 被移除的旧值
             * @param newValue 新值（如果是因为替换而移除）
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Object oldValue, Object newValue) {
                // 可以在这里添加缓存项被移除时的处理逻辑
                // 例如：回收Bitmap、清理文件引用等
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    // ==================== 核心操作方法 ====================
    
    /**
     * 将对象放入缓存
     * 注意事项：
     * 1. 键和值都不能为null，否则操作会被忽略
     * 2. 如果缓存已满，LRU算法会自动移除最少使用的项
     * 3. 对于Bitmap等大对象，建议监控内存使用情况
     * 
     * @param key 缓存键，不能为null
     * @param value 缓存值，不能为null
     */
    public void put(String key, Object value) {
        if (key == null || value == null) {
            // 防御性编程：忽略null值，避免异常
            return;
        }
        memoryCache.put(key, value);
    }

    /**
     * 从缓存中获取对象
     * 功能说明：
     * 1. 自动更新命中/未命中统计
     * 2. 支持泛型类型转换
     * 3. 异常安全：转换失败时返回null而不是抛出异常
     * 
     * @param key 缓存键，不能为null
     * @param <T> 期望的返回类型
     * @return 缓存的对象，如果不存在或类型转换失败则返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key == null) {
            return null;
        }

        try {
            // 从LRU缓存获取值
            T value = (T) memoryCache.get(key);
            if (value != null) {
                // 命中：更新命中计数
                hitCount.incrementAndGet();
            } else {
                // 未命中：更新未命中计数
                missCount.incrementAndGet();
            }
            return value;
        } catch (Exception e) {
            // 类型转换异常：按未命中处理
            missCount.incrementAndGet();
            return null;
        }
    }

    // ==================== 查询和操作 ====================
    
    /**
     * 检查缓存中是否包含指定键
     * 实现说明：
     * 使用snapshot()方法获取当前缓存状态的快照
     * 注意：此操作会创建新的Map对象，性能开销相对较大
     * 如果只是检查存在性，建议直接使用get()方法
     * 
     * @param key 要检查的键
     * @return 如果包含该键返回true，否则返回false
     */
    public boolean contains(String key) {
        return key != null && memoryCache.snapshot().containsKey(key);
    }

    /**
     * 从缓存中移除指定键的对象
     * 功能说明：
     * 1. 移除指定键的缓存项
     * 2. 返回被移除的对象，如果键不存在则返回null
     * 3. 不会影响统计计数（因为不是通过get操作）
     * 
     * @param key 要移除的键
     * @return 被移除的对象，如果键不存在则返回null
     */
    public Object remove(String key) {
        if (key == null) {
            return null;
        }
        return memoryCache.remove(key);
    }

    /**
     * 清空所有缓存数据
     * 功能说明：
     * 1. 移除所有缓存项
     * 2. 释放所有缓存占用的内存
     * 3. 不会重置统计信息（统计信息独立于缓存数据）
     * 使用场景：
     * - 内存不足时主动清理
     * - 应用切换到后台时清理
     * - 用户主动清理缓存
     */
    public void clear() {
        memoryCache.evictAll();
    }

    // ==================== 状态查询 ====================
    
    /**
     * 获取当前缓存中的条目数
     *
     * @return 当前缓存中的条目数
     */
    public int size() {
        return memoryCache.size();
    }

    /**
     * 获取缓存的最大容量
     * 说明：
     * 这个值在构造函数中设置，通常是应用最大内存的1/8
     * 可以根据实际需求调整构造函数中的计算逻辑
     * 
     * @return 缓存的最大条目数
     */
    public int maxSize() {
        return memoryCache.maxSize();
    }

    // ==================== 统计信息 ====================
    
    /**
     * 获取缓存命中次数
     * 说明：
     * 命中：通过get()方法成功获取到缓存值
     * 未命中：通过get()方法未获取到缓存值
     * 
     * @return 命中次数
     */
    public long getHitCount() {
        return hitCount.get();
    }

    /**
     * 获取缓存未命中次数
     * 
     * @return 未命中次数
     */
    public long getMissCount() {
        return missCount.get();
    }

    /**
     * 获取缓存命中率
     * 计算公式：命中次数 / (命中次数 + 未命中次数)
     * 返回值范围：0.0 - 1.0
     * 命中率分析：
     * - 0.8+：缓存效果很好
     * - 0.6-0.8：缓存效果一般
     * - <0.6：可能需要调整缓存策略或大小
     * 
     * @return 命中率 (0.0 - 1.0)
     */
    public double getHitRate() {
        long total = hitCount.get() + missCount.get();
        return total > 0 ? (double) hitCount.get() / total : 0.0;
    }

    /**
     * 重置统计信息
     * 功能说明：
     * 1. 将命中次数和未命中次数都重置为0
     * 2. 不影响缓存数据本身
     * 3. 通常用于性能测试或定期统计
     * 使用场景：
     * - 性能测试前重置
     * - 定期清理统计数据
     * - 调试缓存性能问题
     */
    public void resetStats() {
        hitCount.set(0);
        missCount.set(0);
    }
}