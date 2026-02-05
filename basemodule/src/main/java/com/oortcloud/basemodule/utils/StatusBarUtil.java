package com.oortcloud.basemodule.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

/**
 * Created by hcDarren on 2017/7/9.
 */

public class StatusBarUtil {
    /**
     * activity 的状态栏设置颜色
     */
    public static void setStatusBarColor(Activity activity,int colorRes){
        // 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //ARGB颜色值
            int color = ContextCompat.getColor(activity, colorRes);
            // 智能处理透明度（保留原始透明度但避免全透明）
            if (Color.alpha(color) < 10) { // 如果透明度<10%
                color = ColorUtils.setAlphaComponent(color, 10); // 设置最小透明度
            }

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //直接调用系统提供的方法 setStatusBarColor
            activity.getWindow().setStatusBarColor(color);
            // 设置状态栏图标深色/浅色模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = activity.getWindow().getDecorView();
                int flags = decorView.getSystemUiVisibility();
                // 自动适配深色/浅色图标
                if (ColorUtils.calculateLuminance(color) > 0.5) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//
                } else {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;// 白色图标
                }
                decorView.setSystemUiVisibility(flags);
            }
        }
        // 4.4 - 5.0 之间  采用一个技巧，首先把他弄成全屏，在状态栏的部分加一个布局
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // 首先把他弄成全屏（），在状态栏的部分加一个布局
            // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 电量 时间 网络状态 都还在
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 在状态栏的部分加一个布局 setContentView 源码分析，自己加一个布局 (高度是状态栏的高度)
            View view = new View(activity);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getStatusBarHeight(activity));
            view.setLayoutParams(params);
            view.setBackgroundColor(colorRes);

            //  android:fitsSystemWindows="true" 每个布局都要写
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(view);

            // 获取activity中setContentView布局的根布局
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            contentView.setPadding(0,getStatusBarHeight(activity),0,0);
            // View activityView = contentView.getChildAt(0);
            // activityView.setFitsSystemWindows(true);
            // activityView.setPadding(0,getStatusBarHeight(activity),0,0);
        }
    }

    /**
     * 获取状态栏的高度
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        // 插件式换肤：怎么获取资源的，先获取资源id，根据id获取资源
        Resources resources = activity.getResources();
        int statusBarHeightId = resources.getIdentifier("status_bar_height","dimen","android");
        Log.e("TAG",statusBarHeightId+" -> "+resources.getDimensionPixelOffset(statusBarHeightId));
        return resources.getDimensionPixelOffset(statusBarHeightId);
    }

    /**
     * 设置activity全屏
     * @param activity
     */
    public static void setActivityTranslucent(Activity activity){
        // 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        // 4.4 - 5.0 之间  采用一个技巧，首先把他弄成全屏，在状态栏的部分加一个布局
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void makeStatusBarTransparent1(Activity activity) {

        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 1. 清除所有可能冲突的Flags
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            );

            // 2. 启用绘制系统栏背景
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // 3. 设置全屏布局 + 稳定布局
            int options =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION; // 可选：导航栏透明

            window.getDecorView().setSystemUiVisibility(options);

            // 4. 真正设置透明色
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT); // 可选：导航栏透明

            // 5. Android 10+ 必须调用 setDecorFitsSystemWindows(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.setStatusBarContrastEnforced(false);
                window.setNavigationBarContrastEnforced(false);
            }
        } else {
            // KitKat 处理
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 6. 可选：浅色状态栏图标
        window.getDecorView().setSystemUiVisibility(
                window.getDecorView().getSystemUiVisibility() |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
    }

    public static void setLightMode(Activity activity) {

        makeStatusBarTransparent1(activity);
    }
}
