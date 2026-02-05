package com.oortcloud.clouddisk.adapter.base.common;

/**
 * @FileName:
 * @Author: ZZJun / @CreateDate: 2021/1/7 4:03
 * @Version: 1.0
 * @Function: 多布局支持接口
 */
public interface MultiTypeSupport<T> {
    // 根据当前位置或者条目数据返回布局
     int getLayoutId(T item, int position);
}
