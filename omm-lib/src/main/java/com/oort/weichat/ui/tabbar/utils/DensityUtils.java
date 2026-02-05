package com.oort.weichat.ui.tabbar.utils;

import android.content.Context;

/**
 * 屏幕密度工具类：dp转px、px转dp
 */
public class DensityUtils {
    private DensityUtils() {}

    /**
     * dp转px（适配不同屏幕密度）
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, int pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }
}
