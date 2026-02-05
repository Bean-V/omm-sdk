package com.oort.weichat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionHelper {
    // 悬浮窗权限请求码
    public static final int REQUEST_OVERLAY_PERMISSION = 1001;

    /**
     * 检查是否有悬浮窗权限（SYSTEM_ALERT_WINDOW）
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        // Android 6.0以下默认有权限
        return true;
    }

    /**
     * 跳转到悬浮窗权限设置页
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestOverlayPermission(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
    }

    /**
     * 检查是否有通知权限（POST_NOTIFICATIONS）
     */
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // Android 13以下默认有权限
        return true;
    }

    /**
     * 跳转到通知权限设置页（适用于拒绝后引导）
     */
    public static void openNotificationSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
