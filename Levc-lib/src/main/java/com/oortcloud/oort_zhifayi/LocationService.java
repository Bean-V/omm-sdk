package com.oortcloud.oort_zhifayi;


import static com.oortcloud.basemodule.CommonApplication.getmSeralNum;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.oortcloud.basemodule.utils.DeviceGPSUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import java.util.HashMap;


/**
 * 适配Android 8.0/9.0限制后台定位的功能，新增允许后台定位的接口，即开启一个前台定位服务
 */
public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final long LOOP_INTERVAL_MS = 10_000L;
    private static final int NOTIFICATION_ID = 13691;
    private static final long DEFAULT_REPORT_MIN_INTERVAL_MS = 10_000L; // 默认最小上报间隔
    private static final long DEFAULT_REPORT_MIN_MOVE_METERS = 20L; // 默认最小上报移动距离

    //gps位置信息获取
    private static double mlatitude;
    private static double mlongitude;
    private static String mAddress;
    private Location location;
    private DeviceGPSUtils gpsUtils;
    private final Handler loopHandler = new Handler(Looper.getMainLooper());
    private final Runnable loopTask = new Runnable() {
        @Override
        public void run() {
            if (bdGpsUtils != null) {
                bdGpsUtils.start();
            }
            loopHandler.postDelayed(this, LOOP_INTERVAL_MS);
        }
    };

    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
    private BDGPSUtils bdGpsUtils;
    private long lastReportAtMs = 0L;
    private double lastReportLat = 0d;
    private double lastReportLon = 0d;
    private long reportMinIntervalMs = DEFAULT_REPORT_MIN_INTERVAL_MS;
    private long reportMinMoveMeters = DEFAULT_REPORT_MIN_MOVE_METERS;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
            intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Notification notification = NotificationUtils.createNotification(this,"Gps", "定位服务运行中", com.oortcloud.basemodule.R.drawable.title_bg_home, intent);
            startForeground(NOTIFICATION_ID, notification);
        }
        // 读取可配置阈值（无则使用默认）
        try {
            FastSharedPreferences cfg = FastSharedPreferences.get("LOCATION_CFG");
            String intervalStr = cfg.getString("min_interval_ms", String.valueOf(DEFAULT_REPORT_MIN_INTERVAL_MS));
            String moveStr = cfg.getString("min_move_meters", String.valueOf(DEFAULT_REPORT_MIN_MOVE_METERS));
            reportMinIntervalMs = Long.parseLong(intervalStr);
            reportMinMoveMeters = Long.parseLong(moveStr);
        } catch (Throwable ignore) {}



        //初始化获取位置信息
        mlatitude = Double.parseDouble(sharedPreferences.getString("latitude","0"));
        mlongitude = Double.parseDouble(sharedPreferences.getString("longitude","0"));
        mAddress = sharedPreferences.getString("address","无地址信息");
        ReportInfo.latitude = mlatitude > 1 ? mlatitude : ReportInfo.latitude;
        ReportInfo.longitude = mlongitude > 1 ? mlongitude :ReportInfo.longitude;
        ReportInfo.elements = mAddress.isEmpty() ? ReportInfo.elements : mAddress;


        bdGpsUtils = new BDGPSUtils(getApplicationContext());
        bdGpsUtils.setOnLocationUpdateListener(new BDGPSUtils.OnLocationUpdateListener() {
            @Override
            public void onValidLocation(double latitude, double longitude, long distanceMeters) {
                // 更新内存与本地缓存
                mlatitude = latitude;
                mlongitude = longitude;
                ReportInfo.latitude = latitude;
                ReportInfo.longitude = longitude;

                String addr = DeviceGPSUtils.getAddressStr();
                mAddress = (addr != null && !addr.isEmpty()) ? addr : "无地址信息";
                ReportInfo.elements = mAddress;

                Log.d(TAG, "定位成功: lat=" + latitude + ", lon=" + longitude + ", d=" + distanceMeters);
                sharedPreferences.edit()
                        .putString("latitude", String.valueOf(latitude))
                        .putString("longitude", String.valueOf(longitude))
                        .putString("address", mAddress)
                        .apply();

                // 上报节流：时间间隔与移动距离同时满足才上报
                long now = System.currentTimeMillis();
                boolean overInterval = (now - lastReportAtMs) >= reportMinIntervalMs;
                double distToLastReport = (lastReportLat == 0d && lastReportLon == 0d)
                        ? Double.MAX_VALUE
                        : DeviceGPSUtils.calculateDistance(latitude, longitude, lastReportLat, lastReportLon);
                boolean overDistance = distToLastReport >= reportMinMoveMeters;

                if (lastReportAtMs == 0L || (overInterval && overDistance)) {
                    updateMyLocation();
                    lastReportAtMs = now;
                    lastReportLat = latitude;
                    lastReportLon = longitude;
                    updateNotificationContent(formatReportTip(now, distToLastReport));
                }
            }
        });

//        MediaPlayer player = MediaPlayer.create(context, com.oortcloud.basemodule.R.raw.gps_weak);
//
//        if(Constant.IsDebbug) {
//            player.start();
//        }

        gpsUtils = new DeviceGPSUtils(getApplicationContext());//初始化GPS

//        if(true) {
//            return;
//        }
        // 使用Handler循环而非while(true)，避免线程泄漏
//        loopHandler.postDelayed(loopTask, LOOP_INTERVAL_MS);
        bdGpsUtils.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void updateLocation() {
        if (location != null) {
            String addr = DeviceGPSUtils.getAddressStr();
            if (addr != ""){
                mAddress = addr;
            }else{
                mAddress = "无地址信息";
            }
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();

            ReportInfo.latitude = mlatitude;
            ReportInfo.longitude = mlongitude;
            ReportInfo.elements = mAddress;

            Log.d("gps", "获取位置成功 "+ "Lat: "+ mlatitude + "  Long: "+ mlongitude);
            sharedPreferences.edit().putString("latitude", String.valueOf(mlatitude));
            sharedPreferences.edit().putString("longitude", String.valueOf(mlongitude));
            sharedPreferences.edit().putString("address", mAddress).apply();

            //坐标上报
            //updateMyLocation();
        }
    }

    private void updateMyLocation() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("elements", ReportInfo.elements);
        //x经度，y纬度
        map.put("y", ReportInfo.latitude);
        map.put("x", ReportInfo.longitude);
        map.put("name", ReportInfo.name);
        map.put("phone", ReportInfo.phone);
        map.put("tid", ReportInfo.sn);
        map.put("type", 1);
        if (ReportInfo.sn.isEmpty()) {
            ReportInfo.sn = getmSeralNum();
        }
        map.put("sn", ReportInfo.sn);

        String params = new Gson().toJson(map);
        Log.e("params",params);
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(com.oortcloud.basemodule.constant.Constant.BASE_URL +
                com.oortcloud.basemodule.constant.Constant.POLICE_LOCATION, params, new com.oortcloud.basemodule.utils.HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {
                if (requst != null){
                    Log.e("LocationService",requst);
                }
            }
        });

    }


    @SuppressLint("WrongConstant")
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止循环与定位
        loopHandler.removeCallbacks(loopTask);
        if (bdGpsUtils != null) {
            try { bdGpsUtils.stop(); } catch (Throwable ignore) {}
        }
        stopForeground(true);
    }

    private void updateNotificationContent(String subtitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
            intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
            Notification notification = NotificationUtils.createNotification(this, "Gps", subtitle, com.oortcloud.basemodule.R.drawable.title_bg_home, intent);
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private String formatReportTip(long timestampMs, double distanceMeters) {
        try {
            String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(timestampMs));
            return "上次上报 " + timeStr + " · 距离 " + ((int) distanceMeters) + "m";
        } catch (Throwable ignore) {
            return "已上报";
        }
    }
}
