package com.oort.weichat.ui.tabbar.config;

import com.oort.weichat.ui.tabbar.model.TabModel;

import java.util.List;

/**
 * 配置提供器接口：统一本地/后台配置的获取方式
 */
public interface TabConfigProvider {
    /**
     * 获取Tab配置列表（统一转为TabModel格式）
     * @return 标准化Tab配置列表
     */
    List<TabModel> getTabConfigs();

    /**
     * 获取当前配置源类型
     * @return TabConfigType
     */
    TabConfigType getConfigType();
}
