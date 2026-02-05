package com.oort.weichat.call;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.oort.weichat.R;

public class CallNotificationHelper {
    private static final String CHANNEL_ID = "video_call_channel";
    private static final int NOTIFICATION_ID = 10086;
    private static final int PERMISSION_REQUEST_CODE = 1002;

    // 初始化通知渠道
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "视频通话请求",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("接收视频和语音通话通话请求通知");
                channel.setSound(null, null); // 禁用通知音（避免与来电铃声铃声冲突）
                channel.enableVibration(false);
                manager.createNotificationChannel(channel);
            }
        }
    }

    // 发送通话请求通知（包含权限检查和兼容性处理）
    public static void sendCallNotification(Context context, String fromUserId, String userName,
                                            int callType, String meetUrl,String roomId) {
        // 检查通知权限（Android 13+必需）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // 引导用户开启通知权限
                Intent permissionIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(permissionIntent);
                return;
            }
        }

        // 检查后台弹窗权限（针对Android 11+和国产ROM）
        checkBackgroundStartPermission(context);

        // 构建启动通话界面的Intent
        Intent intent = new Intent(context, JitsiIncomingcall.class);
        intent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
        intent.putExtra("fromuserid", fromUserId);
        intent.putExtra("touserid", fromUserId);
        intent.putExtra("name", userName);
        intent.putExtra("meetUrl", meetUrl);
        intent.putExtra("roomId", roomId);
        // 关键Flags：确保后台启动时能正确创建任务栈
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 构建PendingIntent（适配Android 12+的可变性要求）
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 启动Activity的PendingIntent需用FLAG_MUTABLE（部分设备强制要求）
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID, // 固定请求码，确保唯一性
                intent,
                flags
        );

        // 通知内容
        String content = getCallContent(userName, callType);

        // 构建通知（强化通话属性，提高系统优先级）
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_appicon) // 替换为你的应用图标
                .setContentTitle("收到通话请求")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // 绑定点击事件
                .setFullScreenIntent(pendingIntent, true) // 全屏弹窗（类似系统来电）
                .setAutoCancel(true)
                .setOngoing(true) // 通话中不允许删除
                .setCategory(NotificationCompat.CATEGORY_CALL) // 声明为通话类通知
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // 锁屏可见

        // 发送通知
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    // 检查后台启动Activity权限（Android 11+）
    private static void checkBackgroundStartPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                // 检查是否允许后台启动Activity
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                // 忽略设备不支持的情况
            }
        }
    }

    // 根据通话类型生成通知内容
    private static String getCallContent(String userName, int callType) {
        switch (callType) {
            case CallConstants.Audio:
                return userName + "正在邀请你进行语音通话";
            case CallConstants.Video:
                return userName + "正在邀请你进行视频通话";
            case CallConstants.Screen:
                return userName + "正在邀请你进行屏幕共享";
            case CallConstants.Audio_Meet:
                return userName + "邀请你加入语音会议";
            default:
                return userName + "发起通话请求";
        }
    }

    // 取消通知
    public static void cancelNotification(Context context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }

    // 请求Android 13+通知权限（在MainActivity中调用）
    public static void requestNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }
}