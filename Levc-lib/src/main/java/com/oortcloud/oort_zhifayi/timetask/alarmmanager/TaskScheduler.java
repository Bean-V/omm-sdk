package com.oortcloud.oort_zhifayi.timetask.alarmmanager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresPermission;
/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/28-18:35.
 * Version 1.0
 * Description:创建任务调度器
 */
public class TaskScheduler {
    private static final String TAG = "TaskScheduler";
    private static final int REQ_BASE = 20000;

    /**
     * 为单个任务安排开始与结束闹钟：
     * - 若开始时间在未来：安排开始闹钟；若已过期且当前时间仍小于结束时间，则立刻触发开始逻辑
     * - 若结束时间在未来：安排结束闹钟；若已过期则忽略
     */
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public static void scheduleTask(Context context, Task task) {
        if (task == null) return;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis();
        // 安排开始
        if (task.startTimeMillis > now) {
            Intent startIntent = new Intent(context, TaskAlarmReceiver.class);
            startIntent.setAction(TaskAlarmReceiver.ACTION_TASK_START);
            startIntent.putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, task.taskId);
            startIntent.putExtra(TaskAlarmReceiver.EXTRA_DESC, task.description);
            PendingIntent piStart = PendingIntent.getBroadcast(
                    context,
                    (task.taskId + "_start").hashCode(),
                    startIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.startTimeMillis, piStart);
            Log.d(TAG, "任务开始闹钟已设置: " + task.taskId);
        } else if (now < task.endTimeMillis) {
            // 开始时间已过且未到结束：立即发送开始广播
            Intent immediate = new Intent(context, TaskAlarmReceiver.class);
            immediate.setAction(TaskAlarmReceiver.ACTION_TASK_START);
            immediate.putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, task.taskId);
            immediate.putExtra(TaskAlarmReceiver.EXTRA_DESC, task.description);
            context.sendBroadcast(immediate);
            Log.d(TAG, "任务开始已即时触发: " + task.taskId);
        }

        // 安排结束
        if (task.endTimeMillis > now) {
            Intent endIntent = new Intent(context, TaskAlarmReceiver.class);
            endIntent.setAction(TaskAlarmReceiver.ACTION_TASK_END);
            endIntent.putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, task.taskId);
            PendingIntent piEnd = PendingIntent.getBroadcast(
                    context,
                    (task.taskId + "_end").hashCode(),
                    endIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.endTimeMillis, piEnd);
            Log.d(TAG, "任务结束闹钟已设置: " + task.taskId);
        }
    }

    /**
     * 取消某个任务的开始与结束闹钟
     */
    public static void cancelTask(Context context, String taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent startIntent = new Intent(context, TaskAlarmReceiver.class);
        startIntent.setAction(TaskAlarmReceiver.ACTION_TASK_START);
        PendingIntent piStart = PendingIntent.getBroadcast(
                context,
                (taskId + "_start").hashCode(),
                startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(piStart);

        Intent endIntent = new Intent(context, TaskAlarmReceiver.class);
        endIntent.setAction(TaskAlarmReceiver.ACTION_TASK_END);
        PendingIntent piEnd = PendingIntent.getBroadcast(
                context,
                (taskId + "_end").hashCode(),
                endIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(piEnd);
        Log.d(TAG, "已取消任务: " + taskId);
    }
}