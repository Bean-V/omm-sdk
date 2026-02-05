package com.oort.weichat.call;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.oort.weichat.R;

public class CallForegroundService extends Service {
    private static final String TAG = "CallForegroundService";
    private static final String CHANNEL_ID = "call_foreground_channel";
    private static final int NOTIFICATION_ID = 10087;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "服务创建成功");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand 被调用，启动前台服务");
        try {
            // 构建包含全屏意图的通知（关键：即使在桌面后台，也尝试强制弹出）
            Intent fullScreenIntent = new Intent(this, JitsiIncomingcall.class);
            fullScreenIntent.putExtras(intent.getExtras()); // 传递通话参数
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingFlags |= PendingIntent.FLAG_MUTABLE;
            }
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                    this,
                    10088,
                    fullScreenIntent,
                    pendingFlags
            );

            // 前台服务通知（包含全屏意图）
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.icon_appicon)
                    .setContentTitle("通话服务运行中")
                    .setContentText("可接收视频/语音通话请求")
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // 提高优先级
                    .setOngoing(true)
                    .setFullScreenIntent(fullScreenPendingIntent, true) // 关键：全屏意图强制弹出
                    .setCategory(NotificationCompat.CATEGORY_CALL) // 声明为通话类
                    .build();

            startForeground(NOTIFICATION_ID, notification);
            Log.d(TAG, "前台服务启动成功，已绑定全屏意图");
        } catch (Exception e) {
            Log.e(TAG, "前台服务启动失败：" + e.getMessage(), e);
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "通话前台服务",
                        NotificationManager.IMPORTANCE_HIGH // 提高渠道优先级
                );
                channel.setDescription("用于后台接收通话请求并弹出界面");
                channel.setSound(null, null);
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, CallForegroundService.class);
        context.stopService(intent);
    }
}