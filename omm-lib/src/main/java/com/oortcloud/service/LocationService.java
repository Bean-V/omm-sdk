package com.oortcloud.service;


import static com.oortcloud.basemodule.CommonApplication.getmSeralNum;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.GPSUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;

import java.util.HashMap;


/**
 * 适配Android 8.0/9.0限制后台定位的功能，新增允许后台定位的接口，即开启一个前台定位服务
 */
public class LocationService extends Service {

    //gps位置信息获取
    private static double mlatitude;
    private static double mlongitude;
    private static String mAddress;
    private Location location;
    private GPSUtils gpsUtils;
    private static int notifid = 1303100;

    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(notifid,new Notification());
        }
        //初始化获取位置信息
        mlatitude = Double.parseDouble(sharedPreferences.getString("latitude","0"));
        mlongitude = Double.parseDouble(sharedPreferences.getString("longitude","0"));
        mAddress = sharedPreferences.getString("address","无地址信息");
        ReportInfo.latitude = mlatitude;
        ReportInfo.longitude = mlongitude;
        ReportInfo.elements = mAddress;
//        gpsUtils = new GPSUtils(MyApplication.getContext());//初始化GPS

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Log.e("MyService"+ Thread.currentThread().getName(),"***********getGps");
                    try {
                        location = gpsUtils.getLocation();//获取位置信息
                        updateLocation();
                        Thread.sleep(1000*1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateLocation() {
        if (location != null) {
            String addr = GPSUtils.getAddressStr();
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
            updateMyLocation();
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
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(Constant.BASE_3CLASSURL +
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(notifid);
        }
    }
}
