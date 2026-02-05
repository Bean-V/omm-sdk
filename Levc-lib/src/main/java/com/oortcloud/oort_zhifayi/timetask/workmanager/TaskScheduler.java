//package com.oortcloud.oort_zhifayi.timetask.workmanager;
//
//import android.content.Context;
//import android.util.Log;
//import androidx.work.Constraints;
//import androidx.work.ExistingPeriodicWorkPolicy;
//import androidx.work.NetworkType;
//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;
//
//import java.util.concurrent.TimeUnit;
//
//
///**
// * Email 465571041@qq.com
// * Created by zhang-zhi-jun on 2025/8/28-17:26.
// * Version 1.0
// * Description:
// */
//public class TaskScheduler {
//    private static final String TAG = "TaskScheduler";
//    private static final String WORK_TAG = "periodic_task_work";
//
//    /**
//     * 启动定时任务
//     */
//    public static void startPeriodicTask(Context context, long intervalMinutes) {
//        try {
//            // 设置任务约束条件（可选）
//            Constraints constraints = new Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED) // 需要网络连接
//                    .setRequiresBatteryNotLow(true) // 电量充足时执行
//                    .build();
//
//            // 创建定时工作请求
//            PeriodicWorkRequest periodicWorkRequest =
//                    new PeriodicWorkRequest.Builder(PeriodicTaskWorker.class,
//                            intervalMinutes, TimeUnit.MINUTES)
//                            .setConstraints(constraints)
//                            .addTag(WORK_TAG)
//                            .build();
//
//            // 安排工作（保持唯一性，如果已有相同任务则替换）
//            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                    WORK_TAG,
//                    ExistingPeriodicWorkPolicy.REPLACE,
//                    periodicWorkRequest
//            );
//
//            Log.d(TAG, "定时任务已启动，间隔: " + intervalMinutes + " 分钟");
//
//        } catch (Exception e) {
//            Log.e(TAG, "启动定时任务失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 停止定时任务
//     */
//    public static void stopPeriodicTask(Context context) {
//        try {
//            WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
//            Log.d(TAG, "定时任务已停止");
//        } catch (Exception e) {
//            Log.e(TAG, "停止定时任务失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 检查定时任务状态
//     */
//    public static void checkTaskStatus(Context context) {
//        WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORK_TAG)
//                .observeForever(workInfos -> {
//                    if (workInfos != null && !workInfos.isEmpty()) {
//                        for (androidx.work.WorkInfo workInfo : workInfos) {
//                            Log.d(TAG, "任务状态: " + workInfo.getState());
//                        }
//                    }
//                });
//    }
//}