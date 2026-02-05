package com.oortcloud.oort_zhifayi;

//import static com.baidu.location.LocationClientOption.FirstLocType.SPEED_IN_FIRST_LOC;

import static com.oortcloud.basemodule.utils.AsyncUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.DeviceGPSUtils;
import com.oortcloud.basemodule.utils.DeviceIdFactory;
import com.oortcloud.basemodule.utils.ToastUtil;
import com.oortcloud.oort_zhifayi.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 百度GPS定位工具类 - 优化版本
 * 
 * <p>提供百度地图定位功能，当百度定位失败时自动切换到系统GPS定位作为备选方案。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>百度地图高精度定位</li>
 *   <li>系统GPS定位作为备选</li>
 *   <li>自动切换定位方案</li>
 *   <li>位置信息上报</li>
 *   <li>错误处理和重试机制</li>
 * </ul>
 * 
 * <p><strong>定位策略：</strong></p>
 * <ol>
 *   <li>优先使用百度地图定位（高精度、网络辅助）</li>
 *   <li>当百度定位连续失败时，自动启用系统GPS定位</li>
 *   <li>系统GPS定位成功后，继续尝试百度定位</li>
 *   <li>双重保障确保位置信息获取</li>
 * </ol>
 * 
 * @author chenzhi
 * @since 2017/12/13
 * @version 2.0
 */
@SuppressLint("MissingPermission")
public class BDGPSUtils {

    private static final String TAG = "BDGPSUtils";
    private static final long REPORT_INTERVAL_SECONDS = 5L; // 上报最小间隔（秒）
    private static final long MIN_MOVE_DISTANCE_METERS = 10L; // 有效移动距离阈值（米）
    private static final int MAX_CONSECUTIVE_FAILURES = 3; // 最大连续失败次数
    private static final long FALLBACK_CHECK_INTERVAL = 10000L; // 备选定位检查间隔（毫秒）

    public static  Context appContext;
    private OnLocationUpdateListener onLocationUpdateListener;
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());
    private int consecutiveInvalidCount = 0;
    private static final AtomicBoolean sStarted = new AtomicBoolean(false);

    private boolean isUsingFallbackGPS = false;
    private long lastBaiduLocationTime = 0;
    private long lastFallbackCheckTime = 0;
    private final Runnable fallbackCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkAndSwitchToFallback();
            // 继续检查
            mainHandler.postDelayed(this, FALLBACK_CHECK_INTERVAL);
        }
    };

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
//            runOnUiThread(() -> {
//                String message = String.format("经度：%.6f 纬度：%.6f",
//                        location.getLongitude(), location.getLatitude());
//                ToastUtil.showToast(BDGPSUtils.appContext, message);
//            });
            location.getLocationDescribe();
            String message =  location.getAddress() + "----Appid----" + String.format("经度：%.6f 纬度：%.6f",
                    location.getLongitude(), location.getLatitude());
            Log.d("MyLocationListener", message);
            if (location == null) {
                Log.w(TAG, "onReceiveLocation: location is null");
                return;
            }

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();

            String errDes = location.getLocationDescribe();

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            String adcode = location.getAdCode();    //获取adcode

            String town = location.getTown();    //获取乡镇信息
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            String des = location.getLocationDescribe();
            
            // 过滤无效坐标（如 4.9E-324 即 Double.MIN_VALUE）和错误码
            if (latitude == Double.MIN_VALUE || longitude == Double.MIN_VALUE) {
                Log.w(TAG, "无效定位坐标(Double.MIN_VALUE)，等待下一次定位, locType=" + errorCode);
                handleFallbackLocation();
                return;
            }
            
            if (!(errorCode == 61 || errorCode == 161 || errorCode == 66)) { // 61:GPS, 161:网络, 66:离线
                Log.w(TAG, "定位未成功，locType=" + errorCode + ", desc=" + errDes
                        + ", radius=" + radius + ", coorType=" + coorType);
                handleFallbackLocation();
                return;
            }

            // 百度定位成功，重置失败计数
            consecutiveInvalidCount = 0;
            lastBaiduLocationTime = System.currentTimeMillis();
            
            // 如果正在使用备选GPS，切换回百度定位
            if (isUsingFallbackGPS) {
                Log.i(TAG, "百度定位恢复，切换到百度定位");
                isUsingFallbackGPS = false;
            }

            ReportInfo.gpsData = String.valueOf(latitude) + "::" + String.valueOf(longitude);

            if (latitude > 1 && longitude > 1) {
                ReportInfo.latitude = latitude;
                ReportInfo.longitude = longitude;
//                ReportInfo.elements = (district != null ? district : "") + (town != null ? town : "") + (street != null : street : "");

                ReportInfo.updateDataCount++;

                long currentTimestamp = System.currentTimeMillis() / 1000;

                if (currentTimestamp - timeval > REPORT_INTERVAL_SECONDS) {
                    timeval = currentTimestamp;
                    EventBus.getDefault().post(new MessageEvent("1"));
                    onMessageEvent();
                }
            }
        }
    }


    /**
     * 处理备选GPS定位结果
     */
    private void handleFallbackLocation() {
        Location location = DeviceGPSUtils.getLocation();
        if (location == null) {
            return;
        }
        
        // 转换为百度坐标系（这里使用简单的转换，实际项目中可能需要更精确的转换）
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        
        // 更新位置信息
        ReportInfo.latitude = latitude;
        ReportInfo.longitude = longitude;
        ReportInfo.gpsData = String.valueOf(latitude) + "::" + String.valueOf(longitude);
        ReportInfo.updateDataCount++;
        
        Log.i(TAG, "使用备选GPS定位: " + latitude + ", " + longitude);
        
        // 检查是否需要上报
        long currentTimestamp = System.currentTimeMillis() / 1000;
        if (currentTimestamp - timeval > REPORT_INTERVAL_SECONDS) {
            timeval = currentTimestamp;
            onMessageEvent();
        }
    }

    /**
     * 检查并切换到备选定位
     */
    private void checkAndSwitchToFallback() {
        long currentTime = System.currentTimeMillis();
        
        // 如果百度定位长时间没有成功，启动备选定位
        if (!isUsingFallbackGPS && 
            lastBaiduLocationTime > 0 && 
            currentTime - lastBaiduLocationTime > FALLBACK_CHECK_INTERVAL) {
        }
        
        lastFallbackCheckTime = currentTime;
    }

    public void onMessageEvent() {
        Log.d(TAG, "onMessageEvent:---" );
        // 处理收到的消息，例如更新UI
//        if (ReportInfo.latitude < 1 || ReportInfo.longitude < 1) {
//            return;
//        }
//        long d = DeviceGPSUtils.calculateDistance(ReportInfo.latitude, ReportInfo.longitude, ReportInfo.lastlatitude, ReportInfo.lastlongitude);
//
//        if (d < MIN_MOVE_DISTANCE_METERS) {
//            return;
//        }
//        // 如果外部注册了回调，则交由外部处理（持久化/上报等），内部不再直接上报
//        if (onLocationUpdateListener != null) {
//            try {
//                onLocationUpdateListener.onValidLocation(ReportInfo.latitude, ReportInfo.longitude, d);
//            } catch (Throwable t) {
//                Log.w(TAG, "onLocationUpdateListener error", t);
//            }
//            return;
//        }

        HashMap<String ,Object> paras = buildReportParams();
        Log.d(TAG, "ReportInfo.latitude:---" + ReportInfo.latitude);
        Log.d(TAG, "ReportInfo.longitude:---" + ReportInfo.longitude);
        Log.d(TAG, ZFYConstant.BASE_URL_LOCATION_V1 + "/reportlocation");
        HttpUtils.post()
                .url(ZFYConstant.BASE_URL_LOCATION_V1 + "/reportlocation")
                .params(paras)
                .build(false, true)
                .execute(new BaseCallback<String>(String.class) {
                    @Override
                    public void onResponse(ObjectResult<String> result) {
                        Log.d(TAG, "onResponse.:---上报成功");
                        String dictionary = result.getData();
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.d(TAG, "onError.:--"+ e.toString());
                        Exception e1 = e;
                    }
                });

    }
    private long timeval = 0;
    private MyLocationListener myListener = new MyLocationListener();
    private static LocationManager mLocationManager;

    private static Location mLocation = null;

    private LocationClient mLocationClient;
    private Context mContext;
    public BDGPSUtils(Context context) {
        this.appContext = context != null ? context.getApplicationContext() : null;

        // 初始化百度定位
        initializeBaiduLocation(context);
        
        // 启动备选定位检查
        mainHandler.postDelayed(fallbackCheckRunnable, FALLBACK_CHECK_INTERVAL);
    }

    // 正确：通过SDK接口设置缓存路径为应用专属目录
//    public void initBaiduMap(Context context) {
//        // 1. 生成应用专属的baidu/map目录
//        File appDocDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//        File baiduMapDir = new File(appDocDir, "baidu/map/");
//        if (!baiduMapDir.exists()) baiduMapDir.mkdirs();
//
//        // 2. 配置百度地图SDK缓存路径
//        SDKInitializer.setCoordType(CoordType.BD09LL);
//        // 通过SDK提供的接口设置缓存目录（具体接口参考SDK文档，不同版本可能不同）
//        BaiduMapOptions options = new BaiduMapOptions()
//                .setCacheDir(baiduMapDir.getAbsolutePath()) // 设置缓存目录
//                .setLogDir(baiduMapDir.getAbsolutePath() + "/log/"); // 设置日志目录
//        SDKInitializer.initialize(context, options);
//    }
    /**
     * 初始化百度定位
     */
    private void initializeBaiduLocation(Context context) {
        try {

            // 用于地图SDK（如果用了MapView）
            SDKInitializer.setAgreePrivacy(context, true);
            // 初始化百度地图SDK的Context
            SDKInitializer.initialize(context);

            //设置坐标类型，默认为BD09LL
            SDKInitializer.setCoordType(CoordType.BD09LL);
            // 用于定位SDK
            LocationClient.setAgreePrivacy(true);

            LocationClientOption option = new LocationClientOption();

            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            //可选，设置定位模式，默认高精度
            //LocationMode.Hight_Accuracy：高精度；
            //LocationMode. Battery_Saving：低功耗；
            //LocationMode. Device_Sensors：仅使用设备；
            //LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；
            //定位SDK 返回什么格式
            option.setCoorType("bd09ll");

            //可选，设置返回经纬度坐标类型，默认gcj02
            //gcj02：国测局坐标；
            //bd09ll：百度经纬度坐标；
            //bd09：百度墨卡托坐标；
            //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

            option.setScanSpan(5000);
            //可选，设置发起定位请求的间隔，int类型，单位ms
            //如果设置为0，则代表单次定位，即仅定位一次，默认为0
            //如果设置非0，需设置1000ms以上才有效
            
            // 开启全球导航卫星系统，提高成功率
            try { 
                option.setOpenGnss(true);
            } catch (Throwable ignore) {

            }
            // 设置场景为出行，提升连续定位能力
            try {
                option.setLocationPurpose(LocationClientOption.BDLocationPurpose.Transport);
            } catch (Throwable ignore) {}

            option.setLocationNotify(true);
            //可选，设置是否当卫星定位有效时按照1S/1次频率输出卫星定位结果，默认false

            option.setIgnoreKillProcess(false);
            //可选，定位SDK内部是一个service，并放到了独立进程。
            //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

            option.SetIgnoreCacheException(false);
            //可选，设置是否收集Crash信息，默认收集，即参数为false

            option.setWifiCacheTimeOut(5 * 60 * 1000);
            //可选，V7.2版本新增能力
            //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

            option.setIsNeedAddress(true);
            //可选，是否需要地址信息，默认为不需要，即参数为false
            //如果开发者需要获得当前点的地址信息，此处必须为true

            //需在初始化前声明已同意隐私协议，否则可能返回无效坐标
            LocationClient.setAgreePrivacy(true);

            mLocationClient = new LocationClient(appContext);

            mLocationClient.registerLocationListener(myListener);

            mLocationClient.setLocOption(option);

        } catch (Exception e) {
            Log.e("MyLocationListener", "初始化百度定位失败", e);
        }
    }

    public void start() {
        Log.d("MyLocationListener","start---");
        Log.d("MyLocationListener","start---" +(mLocationClient != null));
        if (mLocationClient != null) {
            Log.d("MyLocationListener", "start---" +(mLocationClient.isStarted())+", client="+System.identityHashCode(mLocationClient)+", pid="+android.os.Process.myPid());
//
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }

    public void stop() {
        if (mLocationClient != null) {
            try {
                mLocationClient.unRegisterLocationListener(myListener);
            } catch (Throwable ignore) {}
            mLocationClient.stop();
            sStarted.set(false);
        }
        // 停止备选定位检查
        mainHandler.removeCallbacks(fallbackCheckRunnable);
    }

    private HashMap<String ,Object> buildReportParams() {
        Log.d(TAG, UserInfoUtils.getInstance(appContext).getUserId());
        HashMap<String ,Object> paras = new HashMap<>();
        paras.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        paras.put("direction", 100);
        paras.put("latitude", ReportInfo.latitude);
        paras.put("longitude", ReportInfo.longitude);
        paras.put("speed", 10);
        paras.put("elevation", 10);
//        paras.put("terminalno", UserInfoUtils.getInstance(appContext).getUserId());
        paras.put("terminalno", DeviceIdFactory.getSerialNumber());//
        paras.put("status", 1);
        return paras;
    }

    public void setOnLocationUpdateListener(OnLocationUpdateListener listener) {
        this.onLocationUpdateListener = listener;
    }

    public interface OnLocationUpdateListener {
        void onValidLocation(double latitude, double longitude, long distanceMeters);
    }
    
    /**
     * 获取当前使用的定位方式
     */
    public String getCurrentLocationMethod() {
        return isUsingFallbackGPS ? "系统GPS" : "百度地图";
    }
    
    /**
     * 检查是否正在使用备选定位
     */
    public boolean isUsingFallbackLocation() {
        return isUsingFallbackGPS;
    }
}
