package com.oort.weichat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.oort.weichat.R;

/**
 * 厂商特定后台弹窗权限引导工具类
 */
public class BrandPopupPermissionHelper {
    // 设备品牌识别
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_OPPO = "oppo";
    private static final String BRAND_VIVO = "vivo";
    private static final String BRAND_HUAWEI = "huawei";
    private static final String BRAND_MEIZU = "meizu";

    /**
     * 根据设备品牌显示对应的权限引导弹窗
     */
    public static void showBrandGuideDialog(Activity activity) {
        String brand = Build.BRAND.toLowerCase();
        String title = "开启后台弹窗权限";
        String message = getGuideMessageByBrand(brand);

        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("去设置", (dialog, which) -> {
                    // 跳转到应用详情页（厂商权限通常在这里）
                    goToAppDetailSetting(activity);
                })
                .show();
    }

    /**
     * 根据品牌生成精准引导文案（关键！减少用户操作成本）
     */
    private static String getGuideMessageByBrand(String brand) {
        if (brand.contains(BRAND_XIAOMI)) {
            return "小米手机需开启：\n" +
                    "1. 应用详情 → 权限 → 显示在其他应用上层（打开）\n" +
                    "2. 应用详情 → 权限 → 后台弹出界面（打开）";
        } else if (brand.contains(BRAND_OPPO)) {
            return "OPPO手机需开启：\n" +
                    "1. 应用详情 → 权限 → 悬浮窗（允许）\n" +
                    "2. 应用详情 → 权限 → 后台弹出界面（允许）";
        } else if (brand.contains(BRAND_VIVO)) {
            return "vivo手机需开启：\n" +
                    "1. 应用详情 → 权限 → 悬浮窗（允许）\n" +
                    "2. 应用详情 → 后台弹出界面（打开）";
        } else if (brand.contains(BRAND_HUAWEI)) {
            //"1. 应用详情 → 权限 → 显示在其他应用上层（允许）\n" +
            return "华为手机需开启：\n" +
                    "应用信息 → 其他权限 → 打开“后台弹窗”";
        } else if (brand.contains(BRAND_MEIZU)) {
            return "魅族手机需开启：\n" +
                    "1. 应用详情 → 权限管理 → 悬浮窗（允许）\n" +
                    "2. 应用详情 → 后台管理 → 允许后台运行（打开）";
        } else {
            // 其他品牌通用指引
            return "请开启：\n" +
                    "1. 应用详情 → 特殊权限 → 显示在其他应用上层\n" +
                    "2. 若有“后台弹出界面”选项，请一并开启";
        }
    }

    /**
     * 跳转到应用详情设置页
     */
    private static void goToAppDetailSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}