package com.oort.weichat.ui.tabbar.config;

/**
 * 配置源类型：区分本地默认配置和后台接口配置
 */
public enum TabConfigType {
    LOCAL_DEFAULT,  // 本地默认配置（客户端内置）
    REMOTE_BACKEND  // 后台接口配置（动态获取）
}
