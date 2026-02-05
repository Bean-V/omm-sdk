package com.oort.weichat.ui.tabbar.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.oort.weichat.R;

/**
 * 皮肤工具类：管理Tab文字/图标颜色
 */
public class SkinUtils {
    private static SkinUtils sInstance;
    private final Context mContext;

    private SkinUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    // 单例模式
    public static SkinUtils getSkin(Context context) {
        if (sInstance == null) {
            synchronized (SkinUtils.class) {
                if (sInstance == null) {
                    sInstance = new SkinUtils(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取Tab文字/图标颜色状态列表（选中/未选中）
     */
    public ColorStateList getMainTabColorState() {
        // 无换肤需求时，直接返回默认颜色
        int selectedColor = mContext.getResources().getColor(R.color.tab_selected_color);
        int normalColor = mContext.getResources().getColor(R.color.tab_unselected_color);

        // 构建颜色状态列表
        int[][] states = new int[2][];
        int[] colors = new int[2];

        // 选中状态
        states[0] = new int[]{android.R.attr.state_selected};
        colors[0] = selectedColor;

        // 默认状态
        states[1] = new int[]{};
        colors[1] = normalColor;

        return new ColorStateList(states, colors);
    }
}
