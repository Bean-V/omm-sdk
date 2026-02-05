package com.oortcloud.oort_zhifayi.timetask.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.oortcloud.oort_zhifayi.LocationService;

/**
 * 接收开始/结束的闹钟广播，根据action分发。
 * 业务层可在这里启动/停止服务或上报等。
 *
 *
 */

/**
 *
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/28-18:33.
 * Version 1.0
 * Description:接收开始/结束的闹钟广播，根据action分发。
 * 业务层可在这里启动/停止服务或上报等。
 *
 */
public class TaskAlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_TASK_START = "com.oortcloud.action.TASK_START";
    public static final String ACTION_TASK_END = "com.oortcloud.action.TASK_END";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_DESC = "extra_desc";

    private static final String TAG = "TaskAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        String taskId = intent.getStringExtra(EXTRA_TASK_ID);
        String desc = intent.getStringExtra(EXTRA_DESC);
        if (ACTION_TASK_START.equals(action)) {
            Log.d(TAG, "任务开始: id=" + taskId + ", desc=" + desc);
            // 启动定位前台服务，持续上报位置（服务内已实现节流/上报逻辑）
            try {
                Intent serviceIntent = new Intent(context, TaskService.class);
                serviceIntent.putExtra(EXTRA_TASK_ID, taskId);
//                context.startForegroundService(serviceIntent);
                context.startService(serviceIntent);
            } catch (Throwable t) {
                Log.w(TAG, "启动LocationService失败", t);
            }
        } else if (ACTION_TASK_END.equals(action)) {
            Log.d(TAG, "任务结束: id=" + taskId);
            // 停止定位前台服务
            try {
                Intent stopIntent = new Intent(context, TaskService.class);
                boolean stopped = context.stopService(stopIntent);
                Log.d(TAG, "停止LocationService: " + stopped);
            } catch (Throwable t) {
                Log.w(TAG, "停止LocationService失败", t);
            }
        }
    }
}



