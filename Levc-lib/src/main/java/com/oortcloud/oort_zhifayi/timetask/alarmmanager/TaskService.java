package com.oortcloud.oort_zhifayi.timetask.alarmmanager;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/28-18:34.
 * Version 1.0
 * Description:创建任务服务
 */
// TaskService.java

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.utils.DeviceGPSUtils;
import com.oortcloud.basemodule.utils.DeviceIdFactory;
import com.oortcloud.oort_zhifayi.R;
import com.oortcloud.oort_zhifayi.ReportInfo;
import com.oortcloud.oort_zhifayi.ZFYConstant;
import com.oortcloud.oort_zhifayi.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class TaskService extends Service {
    private static final String TAG = "TaskService";
    private static final long REPORT_INTERVAL_MS = 3000L; // 上报间隔
    private static final long MIN_MOVE_DISTANCE_METERS = 20L; // 最小移动距离过滤
    private static final int MAX_RETRY = 3; // 单次请求失败重试次数
    private static final java.util.concurrent.atomic.AtomicBoolean sRunning = new java.util.concurrent.atomic.AtomicBoolean(false);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private HandlerThread workerThread;
    private Handler workerHandler;
    private final Runnable reportRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateAndReportPosition();
            } catch (Throwable t) {
                Log.w(TAG, "reportRunnable error", t);
            } finally {
                // 周期调度
                if (workerHandler != null) {
                    workerHandler.postDelayed(this, REPORT_INTERVAL_MS);
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "任务服务启动");

        // 防重复启动：若已在运行则忽略
        if (!sRunning.compareAndSet(false, true)) {
            Log.d(TAG, "TaskService 已在运行，忽略重复启动");
            return START_NOT_STICKY;
        }
        // 执行启动提示与循环上报
        performTask();

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        stopLoopReport();
        sRunning.set(false);
        audioStartTask(R.raw.end_the_inspection);
        EventBus.getDefault().post(new MessageEvent("1002"));
        super.onDestroy();
    }

    private void performTask() {
        // 你的任务逻辑
        Log.d(TAG, "执行定时任务: " + System.currentTimeMillis());
        EventBus.getDefault().post(new MessageEvent("1001"));
        audioStartTask(R.raw.start_the_inspection);
        startLoopReport();
    }



    private void audioStartTask(int audioId){
        //语音提示开始任务
        MediaPlayer mediaPlayer = MediaPlayer.create(this,audioId);
        mediaPlayer.setOnCompletionListener(player -> {
            // 播放完成后，例如停止播放或者释放资源
            player.stop(); //停止播放
            player.release(); //释放 MediaPlayer 对象
        });
        mediaPlayer.start();
    }

    private void startLoopReport() {
        if (workerThread == null) {
            workerThread = new HandlerThread("task-report-worker");
            workerThread.start();
            workerHandler = new Handler(workerThread.getLooper());
        }
        workerHandler.removeCallbacks(reportRunnable);
        workerHandler.post(reportRunnable);
    }

    private void stopLoopReport() {
        if (workerHandler != null) {
            workerHandler.removeCallbacks(reportRunnable);
        }
        if (workerThread != null) {
            Looper looper = workerThread.getLooper();
            workerThread.quitSafely();
            workerThread = null;
            workerHandler = null;
        }
    }

    private void updateAndReportPosition() {
        if (ReportInfo.latitude < 1 || ReportInfo.longitude < 1) {
            return;
        }

        if (ReportInfo.lastlatitude > 1 && ReportInfo.lastlongitude > 1) {
            long d = DeviceGPSUtils.calculateDistance(
                    ReportInfo.latitude, ReportInfo.longitude,
                    ReportInfo.lastlatitude, ReportInfo.lastlongitude
            );
            if (d < MIN_MOVE_DISTANCE_METERS) {
                return;
            }
        }

        Map<String, Object> paras = getStringObjectMap();

        doPostWithRetry(paras, 0);
    }

    @NonNull
    private static Map<String, Object> getStringObjectMap() {
        Map<String, Object> point = new HashMap<>();
        point.put("address", ReportInfo.elements);
        point.put("lat", ReportInfo.latitude);
        point.put("lng", ReportInfo.longitude);

        Map<String, Object> paras = new HashMap<>();
        paras.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        // TODO: 任务id来源待对接，这里先用 ReportInfo.sn 或外部传入的taskId
        paras.put("id", ReportInfo.sn);
        paras.put("terminal_no", DeviceIdFactory.getSerialNumber());
        paras.put("point", point);
        return paras;
    }

    private void doPostWithRetry(Map<String, Object> paras, int retry) {
        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_report")
                .params(paras)
                .build(false, true)
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        ReportInfo.lastlatitude = ReportInfo.latitude;
                        ReportInfo.lastlongitude = ReportInfo.longitude;
                        ReportInfo.postCount++;
                        Log.d(TAG, "上报成功，累计次数=" + ReportInfo.postCount);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.w(TAG, "上报失败 retry=" + retry + ", err=" + e);
//                        if (retry + 1 < MAX_RETRY && workerHandler != null) {
//                            workerHandler.postDelayed(() -> doPostWithRetry(paras, retry + 1),
//                                    Math.min(5000L, (retry + 1) * 1000L));
//                        }
                    }
                });
    }


}
