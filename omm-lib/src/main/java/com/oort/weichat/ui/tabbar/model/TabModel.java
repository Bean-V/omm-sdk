package com.oort.weichat.ui.tabbar.model;


import androidx.fragment.app.Fragment;

import java.util.UUID;

/**
 * 统一Tab数据模型：整合本地/后台配置
 */
public class TabModel {
    // 图标类型
    public enum IconType {
        COLOR_IMAGE,  // 彩色图片（禁止着色）
        OUTLINE_ICON  // 轮廓图标（支持着色）
    }

    // Fragment类型
    public enum FragmentType {
        NATIVE,  // 原生Fragment
        WEB      // WebFragment
    }

    // 基础配置字段
    private final String tabId;
    private final String label;
    private final String iconUrl;
    private final String selectIconUrl;
    private final int defaultIconRes;
    private final String targetApk;

    public String getAppPackage() {
        return appPackage;
    }

    private final String appPackage;
    private final IconType iconType;
    private final boolean isVisible;
    private int unreadCount;

    // Fragment配置字段
    private final FragmentType fragmentType;
    private final String nativeFragmentClassName;
    private final String webUrl;

    public String getUid() {
        return uid;
    }

    private final String uid;

    /**
     * 本地默认配置构造器（仅支持原生Fragment）
     */
    public TabModel(String tabId, String label, int defaultIconRes,
                    Class<? extends Fragment> nativeFragmentClass, IconType iconType) {
        validateBasicParams(tabId, label, defaultIconRes);
        
        this.tabId = tabId;
        this.uid = tabId;
        this.label = label;
        this.defaultIconRes = defaultIconRes;
        this.iconUrl = null;
        this.selectIconUrl = null;
        this.targetApk = null;
        this.iconType = iconType;
        this.isVisible = true;
        this.unreadCount = 0;

        this.fragmentType = FragmentType.NATIVE;
        this.nativeFragmentClassName = nativeFragmentClass.getName();
        this.webUrl = null;
        this.appPackage = null;
    }

    /**
     * 后台配置构造器（支持原生/Web混合）
     */
    public TabModel(String tabId, String label, String iconUrl, String selectIconUrl, int defaultIconRes,
                    String targetApk, int unreadCount, IconType iconType, boolean isVisible,
                    FragmentType fragmentType, String nativeFragmentClassName, String webUrl, String appPackage) {
        validateBasicParams(tabId, label, defaultIconRes);
        
        this.tabId = tabId;
        this.uid = UUID.randomUUID().toString();
        this.label = label;
        this.iconUrl = iconUrl;
        this.selectIconUrl = selectIconUrl;
        this.defaultIconRes = defaultIconRes;
        this.targetApk = targetApk;
        this.iconType = iconType;
        this.isVisible = isVisible;
        this.unreadCount = Math.max(unreadCount, 0);

        this.fragmentType = fragmentType;
        validateFragmentParams(fragmentType, nativeFragmentClassName, webUrl);
        this.nativeFragmentClassName = nativeFragmentClassName;
        this.webUrl = webUrl;
        this.appPackage = appPackage;
    }

    private void validateBasicParams(String tabId, String label, int defaultIconRes) {
        if (tabId == null || tabId.isEmpty()) 
            throw new IllegalArgumentException("tabId不能为空");
        if (label == null || label.isEmpty()) 
            throw new IllegalArgumentException("label不能为空");
        if (defaultIconRes <= 0) 
            throw new IllegalArgumentException("无效的默认图标资源ID");
    }

    private void validateFragmentParams(FragmentType type, String className, String url) {
        if (type == FragmentType.NATIVE && (className == null || className.isEmpty())) {
            throw new IllegalArgumentException("原生Fragment必须指定类名");
        }
        if (type == FragmentType.WEB && (url == null || url.isEmpty())) {
            throw new IllegalArgumentException("WebFragment必须指定URL");
        }
    }

    // Getter/Setter
    public String getTabId() { return tabId; }
    public String getLabel() { return label; }
    public String getIconUrl() { return iconUrl; }
    public String getSelectIconUrl() { return selectIconUrl; }
    public int getDefaultIconRes() { return defaultIconRes; }
    public String getTargetApk() { return targetApk; }
    public IconType getIconType() { return iconType; }
    public boolean isVisible() { return isVisible; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int count) { this.unreadCount = Math.max(count, 0); }
    public FragmentType getFragmentType() { return fragmentType; }
    public String getNativeFragmentClassName() { return nativeFragmentClassName; }
    public String getWebUrl() { return webUrl; }
}
