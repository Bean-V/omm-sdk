package com.jun.baselibrary.base.navigation;

import android.view.View;
import android.view.ViewGroup;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/18 18:39
 * Version 1.0
 * Description：NavigationBar公共接口
 */
 interface INavigation {
    /**
     * 创建NavigationBar
     */
    void create();

    /**
     * 将navigationBar添加到父布局
     * @param navigationBar
     * @param parent
     */
     void attachParent(View navigationBar, ViewGroup parent);

    /**
     * 绑定参数
     */
     void attachParams();
}
