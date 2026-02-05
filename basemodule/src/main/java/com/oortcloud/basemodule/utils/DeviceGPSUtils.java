package com.oortcloud.basemodule.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 设备GPS定位工具类
 *
 * <p>提供系统GPS定位功能，支持获取当前位置、地址信息、距离计算等功能。</p>
 *
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>获取当前GPS位置信息（经纬度、海拔等）</li>
 *   <li>获取地址信息（城市、详细地址）</li>
 *   <li>计算两点间距离</li>
 *   <li>监听GPS状态变化</li>
 *   <li>支持GPS和网络定位</li>
 * </ul>
 *
 * <p><strong>使用方法：</strong></p>
 * <pre>{@code
 * // 初始化GPS工具
 * GPSUtils gpsUtils = new GPSUtils(context);
 *
 * // 获取当前位置
 * Location location = GPSUtils.getLocation();
 * if (location != null) {
 *     double latitude = location.getLatitude();
 *     double longitude = location.getLongitude();
 * }
 *
 * // 获取城市信息
 * String city = GPSUtils.getLocalCity();
 *
 * // 获取详细地址
 * String address = GPSUtils.getAddressStr();
 *
 * // 计算距离
 * long distance = GPSUtils.calculateDistance(lat1, lon1, lat2, lon2);
 * }</pre>
 *
 * <p><strong>权限要求：</strong></p>
 * <ul>
 *   <li>ACCESS_FINE_LOCATION - 精确位置权限</li>
 *   <li>ACCESS_COARSE_LOCATION - 粗略位置权限</li>
 * </ul>
 *
 * <p><strong>注意事项：</strong></p>
 * <ul>
 *   <li>需要适配Android 6.0+的运行时权限</li>
 *   <li>使用ApplicationContext避免内存泄漏</li>
 *   <li>GPS定位需要开启系统GPS开关</li>
 *   <li>网络定位需要网络连接</li>
 *   <li>地址解析需要网络连接</li>
 * </ul>
 *
 * <p><strong>兼容性：</strong></p>
 * <ul>
 *   <li>支持Android 7.0+的GNS(全球导航卫星系统  Global Navigation Satellite System)状态监听</li>
 *   <li>保留Android 7.0以下的GPS状态监听</li>
 *   <li>自动处理权限异常</li>
 * </ul>
 *
 * @author chenzhi
 * @since 2017/12/13
 * @version 2.0
 */
@SuppressLint("MissingPermission")
public class DeviceGPSUtils {

    private static final String TAG = "DeviceGPSUtils";
    private static final int REQUEST_LOCATION = 1002;
    private static final long LOCATION_UPDATE_INTERVAL_MS = 1000L;
    private static final float LOCATION_UPDATE_MIN_DISTANCE_M = 1.0f;

    private static LocationManager mLocationManager;
    private static Location mLocation = null;
    private static Context mAppContext;

    public DeviceGPSUtils(Context context) {
        if (context == null) {
            Log.e(TAG, "GPSUtils: context is null");
            return;
        }

        mAppContext = context.getApplicationContext();
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager == null) {
            Log.e(TAG, "GPSUtils: LocationManager is null");
            return;
        }

        // 判断GPS是否正常启动
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(TAG, "GPS provider is disabled, opening settings...");
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mAppContext.startActivity(intent);
            return;
        }

        // 为获取地理位置信息时设置查询条件
        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
        if (bestProvider == null) {
            Log.w(TAG, "No best provider available");
            return;
        }

        // 获取位置信息
        Location location = mLocationManager.getLastKnownLocation(bestProvider);
        mLocation = location;

        if (location == null) {
            // 尝试网络定位作为备选
            try {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                mLocation = location;
            } catch (SecurityException e) {
                Log.w(TAG, "No permission for network provider", e);
            }
            // 注册位置更新监听
            registerLocationUpdates();
        } else {
            // 已有缓存位置，仅注册GPS更新
            registerLocationUpdates();
        }

        // 注册GNS状态监听（Android 7.0+）
        registerGNSStatusCallback();
    }

    private void registerLocationUpdates() {
        try {
            // 优先使用GPS，备选网络定位
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL_MS, LOCATION_UPDATE_MIN_DISTANCE_M, locationListener);
            }
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_INTERVAL_MS, LOCATION_UPDATE_MIN_DISTANCE_M, locationListener);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "No location permission", e);
        }
    }
    //全球导航卫星系统
    private void registerGNSStatusCallback() {
        try {
            mLocationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    Log.i(TAG, "GNSS定位启动");
                }

                @Override
                public void onStopped() {
                    Log.i(TAG, "GNSS定位结束");
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    Log.i(TAG, "第一次定位，TTFF: " + ttffMillis + " 毫秒");
                }

                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    int satelliteCount = status.getSatelliteCount();
                    int usableSatellites = 0;

                    for (int i = 0; i < satelliteCount; i++) {
                        if (status.usedInFix(i)) {
                            usableSatellites++;
                        }
                    }

                    Log.i(TAG, "可用卫星数: " + usableSatellites + " / " + satelliteCount);
                }
            });
        } catch (SecurityException e) {
            Log.w(TAG, "No GNSS permission", e);
        }
    }

    /**
     * 返回查询条件
     */
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    /**
     * @return Location--->getLongitude()获取经度/getLatitude()获取纬度
     */
    public static Location getLocation() {
        if (mLocation == null) {
            Log.e(TAG, "setLocationData: 获取当前位置信息为空");
            return null;
        }
        return mLocation;
    }

    public static String getLocalCity(){
        if (mLocation == null){
            Log.e(TAG, "getLocalCity: 获取城市信息为空");
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String city = "";
        if (result != null && !result.isEmpty()) {
            city = result.get(0).getLocality();//获取城市
        }
        return city;
    }

    public static String getAddressStr(){
        if (mLocation == null){
            Log.e(TAG, "getAddressStr: 获取详细地址信息为空");
            return "";
        }
        List<Address> result = getAddress(mLocation);

        String address = "";
        if (result != null && !result.isEmpty()) {
            address = result.get(0).getAddressLine(0);//获取详细地址
        }
        return address;
    }

    // 位置监听
    private static final LocationListener locationListener = new LocationListener() {
        //位置信息变化时触发
        public void onLocationChanged(@NonNull Location location) {
            mLocation = location;
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        //GPS状态变化时触发
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        //GPS开启时触发
        public void onProviderEnabled(@NonNull String provider) {
            if (mLocationManager != null) {
                try {
                    mLocation = mLocationManager.getLastKnownLocation(provider);
                } catch (SecurityException e) {
                    Log.w(TAG, "No permission for provider: " + provider, e);
                }
            }
        }

        //GPS禁用时触发
        public void onProviderDisabled(@NonNull String provider) {
            mLocation = null;
        }
    };

    // 获取地址信息
    private static List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null && mAppContext != null) {
                Geocoder gc = new Geocoder(mAppContext, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "getAddress error", e);
        }
        return result;
    }

    // 状态监听（已废弃，使用GNSS回调替代）
    @SuppressWarnings("deprecation")
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    if (mLocationManager != null) {
                        try {
                            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                            if (gpsStatus != null) {
                                // 获取卫星颗数的默认最大值
                                int maxSatellites = gpsStatus.getMaxSatellites();
                                // 创建一个迭代器保存所有卫星
                                Iterator<GpsSatellite> iterators = gpsStatus.getSatellites().iterator();
                                int count = 0;
                                while (iterators.hasNext() && count <= maxSatellites) {
                                    GpsSatellite s = iterators.next();
                                    count++;
                                }
                                Log.i(TAG, "搜索到：" + count + "颗卫星");
                            }
                        } catch (SecurityException e) {
                            Log.w(TAG, "No GPS status permission", e);
                        }
                    }
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
            }
        }
    };

    private static final double EARTH_RADIUS = 6371000; // 地球半径，单位米

    // 计算两个经纬度之间的距离，返回值单位米
    public static long calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (long) (EARTH_RADIUS * c);
    }

    @SuppressLint("DefaultLocale")
    public static String calculateDistance_(double lat1, double lon1, double lat2, double lon2) {
        long d = calculateDistance(lat1, lon1, lat2, lon2);
        return d / 1000 == 0 ? String.valueOf(d) + "m" : String.format("%.1f", d / 1000.0) + "km";
    }
}
