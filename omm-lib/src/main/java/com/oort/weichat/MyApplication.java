package com.oort.weichat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.engine.OkHttpEngine;
//import com.kedacom.kmedia.player.KPlayer;
//import com.kedacom.kmedia.player.KPlayerConfiguration;
import com.oort.weichat.bean.PrivacySetting;
import com.oort.weichat.bean.collection.Collectiion;
import com.oort.weichat.bean.event.MessageEventBG;
import com.oort.weichat.call.CallNotificationHelper;
import com.oort.weichat.db.SQLiteHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.map.MapHelper;
import com.oort.weichat.ui.base.ActivityStack;
import com.oort.weichat.ui.tool.MyFileNameGenerator;
import com.oort.weichat.util.AppUtils;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.util.LocaleHelper;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ScreenShotListenManager;
import com.oort.weichat.util.log.LogUtils;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.utils.SllCallback;
import com.oortcloud.appstore.utils.SllInterface;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.custom.CordovaView;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import koal.ssl.IAutoService;

public class MyApplication extends CommonApplication implements SllInterface {
    public static final String TAG = "MyApplication";
    public static final String MULTI_RESOURCE = "android";
    // 服务器是否开启集群 如开启，在登录、自动登录时需要传area，在发起音视频通话(单人)时会要调接口获取通话地址
    public static boolean IS_OPEN_CLUSTER = false;
    public static boolean IS_OPEN_RECEIPT = true;
    // 是否支持端到端加密
    public static boolean IS_SUPPORT_SECURE_CHAT = false;
    // 是否支持多端登录
    public static boolean IS_SUPPORT_MULTI_LOGIN;
    // 是否将消息转发给所有设备,当且仅当消息类型为上、下线消息(检测消息除外),该标志位才为true
    public static boolean IS_SEND_MSG_EVERYONE;
    public static String[] machine = new String[]{"ios", "pc", "mac", "web"};
    public static String IsRingId = "Empty";// 当前聊天对象的id/jid 用于控制消息来时是否响铃通知
    // 本地建群时的jid(给个初始值坐下兼容) 用于防止收到服务端的907消息时本地也在建群而造成群组重复
    public static String mRoomKeyLastCreate = "compatible";
    public static List<Collectiion> mCollection = new ArrayList<>();
    private static MyApplication INSTANCE = null;
    private static Context context;
    /* 文件缓存的目录 */
    public String mAppDir01;
    public String mPicturesDir01;
    public String mVoicesDir01;
    public String mVideosDir01;
    public String mFilesDir01;
    public int mActivityCount = 0;
    /* 文件缓存的目录 */
    public String mAppDir;
    public String mPicturesDir;
    public String mVoicesDir;
    public String mVideosDir;
    public String mFilesDir;
    public int mUserStatus;
    public boolean mUserStatusChecked = true;
    /*********************
     * 百度地图定位服务
     ************************/
    private BdLocationHelper mBdLocationHelper;
    private LruCache<String, Bitmap> mMemoryCache;
    // 抖音模块缓存
    private HttpProxyCacheServer proxy;

    public static CordovaView cordovaView;

    public static Activity activity;

//    private Intent locatIntent;
//    private Intent logIntent;
    public static MyApplication getInstance() {
        return INSTANCE;
    }

    public static Context getContext() {
        return context;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        //super.attachBaseContext(base);
//        super.attachBaseContext(LocaleUtils.forceChineseLocale(base));
//
//        MultiDex.install(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        String  s = getResources().getConfiguration().locale.toLanguageTag();

        String displayLanguage = Locale.getDefault().getDisplayName();

        String Language = Locale.getDefault().getLanguage();

            //获取动态Ip
//            IPRetrofitSreviceManager.getIP();
//            IPRetrofitSreviceManager.getConfig();
//        GifInfoHandle.init();
        INSTANCE = this;
        context = getApplicationContext();
        //Java7SmackInitializer.initialize(context);
        //AndroidSmackInitializer.initialize(context);

        MMKV.initialize(this);
        /*logIntent = new Intent(this , LogService.class);
        locatIntent = new Intent(this , LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LogService.class));
            context.startForegroundService(new Intent(context, LocationService.class));
        } else {
            context.startService(new Intent(context, LogService.class));
            context.startService(new Intent(context, LocationService.class));
        }*/
//        SDKInitializer.initialize(getApplicationContext());
 //       SDKInitializer.setCoordType(CoordType.BD09LL);
//        GifInfoHandle.init();
        if (AppConfig.DEBUG) {
            Log.d(AppConfig.TAG, "MyApplication onCreate");
        }

        initMulti();
//        ScreenCorder.init(this);
        // 在7.0的设备上，开启该模式访问相机或裁剪居然不会抛出FileUriExposedException异常，记录一下
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());

        // 初始化数据库
        SQLiteHelper.copyDatabaseFile(this);
        // 初始化定位
        //getBdLocationHelper();
        // 初始化App目录
        initAppDir();
        initAppDirsecond();
        // 初始化图片加载 缓存
        initLruCache();

        // 判断前后台切换
        getAppBackground();
        // 监听屏幕截图
        //ListeningScreenshots();

        int launchCount = PreferenceUtils.getInt(this, Constants.APP_LAUNCH_COUNT, 0);// 记录app启动的次数
        PreferenceUtils.putInt(this, Constants.APP_LAUNCH_COUNT, ++launchCount);

        //初始化应用市场
        AppStoreInit.setApplication(this);

        //获取动态Ip
//        IPRetrofitSreviceManager.getIP();
        initMap();

//        initLanguage();

        initReporter();

        disableAPIDialog();

        disableWatchdog();

        String dir1 = FileUtil.getSaveDirectory("OperationLogs");
        OperLogUtil.setLogDir(dir1);


//        pd = new ProgressDialog(this);
//
//        AppManager.mActivity = this;
//        initSsl();
//        bindSsl();
        //
        HttpUtils.initEngine(new OkHttpEngine());

        OperLogUtil.msg("进入app");
       // initLanguage();
        INSTANCE = this;

        String s1 = MyApplication.getInstance().getString(R.string.be_friendand_chat);

        Constant.updateConfig();
       // String s = MyApplication.getInstance().getString(R.string.be_friendand_chat);

//        BlockCanary.install(this, new AppBlockCanaryContext()).start();

        String dir = FileUtil.getSaveDirectory("IMLogs");
        LogUtils.setLogDir(dir);


          s = getResources().getConfiguration().locale.toLanguageTag();

         displayLanguage = Locale.getDefault().getDisplayName();

         Language = Locale.getDefault().getLanguage();

        CallNotificationHelper.createNotificationChannel(this);


// 在合适的位置（如Activity的onCreate方法中）初始化
//        KPlayerConfiguration.Builder builder = new KPlayerConfiguration.Builder()
//                .enableWaterMark(true)  // 开启水印
//                .waterMark(Arrays.asList("KPlayer"))  // 水印列表（Java中通过Arrays.asList创建List）
//                .enableSnapshot(true) // 开启截图
//                .enableRecord(true)  // 开启录像
//                .enablePtzCtrl(true)  // 开启ptz（云台）控制功能
//                .enableHistoryStream(true) // 开启历史流播放
//                    .enableHistoryStreamSpeed(true) // 开启历史流播放速度控制
//                    .enableSwitchResolution(true) // 开启分辨率切换功能
//                    .enableStarResource(false) // 关闭收藏功能
//                .playerTintColor(Color.parseColor("#F99BCA"));  // 播放器颜色（Java中用Color.parseColor解析色值）

//        KPlayerConfiguration configuration = builder.build();
//        KPlayer.init(this, configuration);  // 播放组件初始化（this为上下文，如Activity实例）
//        KPlayer.cleanupLog();  // 清除之前持久化的日志文件


    }




    /**
     * 有个watchdog负责监控垃圾对象回收，
     * 在oppo上总是超时导致崩溃，直接禁用，
     * https://www.jianshu.com/p/89e2719be9c7
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void disableWatchdog() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);
            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            method.invoke(field.get(null));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 安卓9以上的hide api警告对话框，
     * 各种库大量使用hide api, 无法都解决掉，
     * 用hide api解决hide api警告，感觉算漏洞，以后可能失效，
     * <p>
     * 反射 禁止弹窗
     */
    @SuppressWarnings("all")
    private void disableAPIDialog() {
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReporter() {
        Reporter.init(this);
    }

    private void initLanguage() {
        // 应用程序里设置的语言，否则程序杀死后重启又会是系统语言，
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));


    }

    private void initMap() {
        MapHelper.initContext(this);
        // 默认为百度地图，
        PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(this);
        boolean isGoogleMap = privacySetting.getIsUseGoogleMap() == 1;
        if (isGoogleMap) {
            MapHelper.setMapType(MapHelper.MapType.GOOGLE);
        } else {
            MapHelper.setMapType(MapHelper.MapType.BAIDU);
        }
    }

    private void ListeningScreenshots() {
        ScreenShotListenManager manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
            @Override
            public void onShot(String imagePath) {
                PreferenceUtils.putString(getApplicationContext(), Constants.SCREEN_SHOTS, imagePath);
            }
        });
        manager.startListen();
    }

    private void getAppBackground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (mActivityCount == 0) {
                    Log.e(TAG, "程序已到前台,检查XMPP是否验证");
                    EventBus.getDefault().post(new MessageEventBG(true, false));
                }
                mActivityCount++;
                Log.e(TAG, "onActivityStarted-->" + mActivityCount);
                topActivity = activity;

            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mActivityCount--;
                Log.e(TAG, "onActivityStopped-->" + mActivityCount);
                if (!AppUtils.isAppForeground(getContext())) {// 在app启动时，当启动页stop，而MainActivity还未start时，又会回调到该方法内，所以需要判断到底是不是真的处于后台
                    EventBus.getDefault().post(new MessageEventBG(false, false));
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public void initMulti() {
        // 只能在登录的时候修改，所以不能放到 setPrivacySettings 内
        PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(this);
        boolean isSupport = privacySetting.getMultipleDevices() == 1;
        if (isSupport) {
            IS_SUPPORT_MULTI_LOGIN = true;
        } else {
            IS_SUPPORT_MULTI_LOGIN = false;
        }
    }

    /*
    保存某群组的部分属性
     */
    public void saveGroupPartStatus(String groupJid, int mGroupShowRead, int mGroupAllowSecretlyChat,
                                    int mGroupAllowConference, int mGroupAllowSendCourse, long mGroupTalkTime) {
        // 是否显示群消息已读人数
        PreferenceUtils.putBoolean(this, Constants.IS_SHOW_READ + groupJid, mGroupShowRead == 1);
        // 是否允许普通成员私聊
        PreferenceUtils.putBoolean(this, Constants.IS_SEND_CARD + groupJid, mGroupAllowSecretlyChat == 1);
        // 是否允许普通成员发起会议
        PreferenceUtils.putBoolean(this, Constants.IS_ALLOW_NORMAL_CONFERENCE + groupJid, mGroupAllowConference == 1);
        // 是否允许普通成员发送课程
        PreferenceUtils.putBoolean(this, Constants.IS_ALLOW_NORMAL_SEND_COURSE + groupJid, mGroupAllowSendCourse == 1);
        // 是否开启了全体禁言
        PreferenceUtils.putBoolean(this, Constants.GROUP_ALL_SHUP_UP + groupJid, mGroupTalkTime > 0);
    }

    /**
     * 初始化支付密码设置状态，
     * 登录接口返回支付密码是否设置，在这里保存起来，
     *
     * @param payPassword 支付密码是否已经设置，
     */
    public void initPayPassword(String userId, int payPassword) {
        Log.d(TAG, "initPayPassword() called with: userId = [" + userId + "], payPassword = [" + payPassword + "]");
        // 和initPrivateSettingStatus中的其他变量保存方式统一，
        PreferenceUtils.putBoolean(this, Constants.IS_PAY_PASSWORD_SET + userId, payPassword == 1);
    }

    public BdLocationHelper getBdLocationHelper() {
        if (mBdLocationHelper == null) {
            mBdLocationHelper = new BdLocationHelper(this);
        }
        return mBdLocationHelper;
    }

    // 意义不明，
    private void initAppDirsecond() {
        File innerFile = new File(getFilesDir(), "external");
        File file = getExternalFilesDir(null);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = innerFile;
        }
        mAppDir01 = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_PICTURES);
        }
        mPicturesDir01 = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_MUSIC);
        }
        mVoicesDir01 = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_MOVIES);
        }
        mVideosDir01 = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_DOWNLOADS);
        }
        mFilesDir01 = file.getAbsolutePath();
    }

    private void initAppDir() {
        File innerFile = new File(getFilesDir(), "external");
        File file = getExternalFilesDir(null);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = innerFile;
        }
        mAppDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_PICTURES);
        }
        mPicturesDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_MUSIC);
        }
        mVoicesDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_MOVIES);
        }
        mVideosDir = file.getAbsolutePath();

        file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
        if (file == null) {
            // 不能为空，
            file = new File(innerFile, Environment.DIRECTORY_DOWNLOADS);
        }
        mFilesDir = file.getAbsolutePath();
    }

    /**
     * 在程序内部关闭时，调用此方法
     */
    public void destory() {
        if (AppConfig.DEBUG) {
            Log.d(AppConfig.TAG, "MyApplication destory");
        }
        // 结束百度定位
        if (mBdLocationHelper != null) {
            mBdLocationHelper.release();
        }
        if(cordovaView != null){
            cordovaView.onDestroy();
        }
        /*if(locatIntent != null)
        {
            stopService(locatIntent);
        }
        if(logIntent != null)
        {
            stopService(logIntent);
        }*/
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    public void destoryRestart() {
        if (AppConfig.DEBUG) {
            Log.d(AppConfig.TAG, "MyApplication destory");
        }
        // 结束百度定位
        if (mBdLocationHelper != null) {
            mBdLocationHelper.release();
        }
        if(cordovaView != null){
            cordovaView.onDestroy();
        }
        /*if(locatIntent != null)
        {
            stopService(locatIntent);
        }
        if(logIntent != null)
        {
            stopService(logIntent);
        }*/
    }

    /***********************
     * 保存其他用户坐标信息
     ***************/

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void initLruCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .fileNameGenerator(new MyFileNameGenerator()).build();
    }



    public static final String ACTION_INTENT_DATA = "data";

    private static final String SERVICE_ACTION_NAME = "koal.ssl.vpn.service";
    public static final String ACTION_INTENT_STARTSERVER_INPROC = "koal.ssl.broadcast.startserver.inproc";
    public static final String ACTION_INTENT_STARTSERVER_SUCCESS = "koal.ssl.broadcast.startserver.success";
    public static final String ACTION_INTENT_STARTSERVER_FAILURE = "koal.ssl.broacast.startserver.failure";
    public static final String ACTION_INTENT_DOWNLOADCFG_SUCCESS = "koal.ssl.broadcast.downloadcfg.success";
    public static final String ACTION_INTENT_STOPSERVER_SUCCESS = "koal.ssl.broadcast.stopserver.success";
    public static final String ACTION_INTENT_UPGRADE = "koal.ssl.broadcast.upgrade";


    private ServiceMon srvMonitor = null;


    private ProgressDialog pd = null;
    private ServiceConnection serviceConnection = null;
    private IAutoService autoService = null;

    public SllCallback getSslCallback() {
        return sslCallback;
    }

    public void setSslCallback(SllCallback sslCallback) {
        this.sslCallback = sslCallback;
    }

    private SllCallback sslCallback;


    private class ServiceMon extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(ACTION_INTENT_DATA);
            if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_INPROC)) {
                // 服务启动中，data中是当前状态
                pd.setMessage(data);
            } else if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_SUCCESS)) {
                String str = "服务启动成功";
                pd.setMessage(str);

                pddismiss();
                if(sslCallback != null){
                    sslCallback.startCallback();
                }

            } else if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_FAILURE)) {
                String str = "服务启动失败";
                pd.setMessage(str);

                if(serviceConnection != null) {
                    autoService = null;
                    unbindService(serviceConnection);




                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.setMessage("重新绑定");
                            bindSsl();
                            pddismiss();
                        }
                    }, 1000);
                }else {
                    pddismiss();
                }
            } else if (intent.getAction().equals(ACTION_INTENT_DOWNLOADCFG_SUCCESS)) {
                String str = "下载策略成功";
                pd.setMessage(str);
            } else if (intent.getAction().equals(ACTION_INTENT_STOPSERVER_SUCCESS)) {
                String str = "停止服务成功";
                pd.setMessage(str);

                if(sslCallback != null){
                    sslCallback.stopCallback();
                }
                pddismiss();
            } else if (intent.getAction().equals(ACTION_INTENT_UPGRADE)) {
                // 发现新版的客户端，可升级，data中是版本号
                Toast.makeText(context, "发现新版的客户端，版本号" + data, Toast.LENGTH_SHORT).show();
                try {
                    autoService.upgrade();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void initSsl(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                autoService = IAutoService.Stub.asInterface(service);

               // start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                autoService = null;
            }
        };
        // 广播接收器，用来监听SSL服务发出的广播
        srvMonitor = new ServiceMon();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_STARTSERVER_INPROC);
        filter.addAction(ACTION_INTENT_STARTSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_STARTSERVER_FAILURE);
        filter.addAction(ACTION_INTENT_DOWNLOADCFG_SUCCESS);
        filter.addAction(ACTION_INTENT_STOPSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_UPGRADE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(srvMonitor, filter,Context.RECEIVER_NOT_EXPORTED);
        }

    }
    public void bindSsl() {

        Intent intent = new Intent(SERVICE_ACTION_NAME);
        // 目前的测试包包名
        intent.setPackage("koal.ssl");

        // 若应用被卸载，则会抛出异常
        try {
            //Android 11 开始，无法判断某个应用是否安装，解决办法见AndroidManifest.xml
            boolean res = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            // 应用未安装
            if (!res) {

                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();//安全认证应用未安装
            }

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }





    public void isStarted(View view) {
//        if (autoService != null) {
//            try {
//                String res = "Is Started: " + autoService.isStarted();
//                tv.setText(res);
//            } catch (Exception e) {
//                e.printStackTrace();
//                tv.setText(e.toString());
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "未绑定服务，请检查是否安装了SSL客户端", Toast.LENGTH_SHORT).show();
//        }
    }


    public void start() {
        Activity act = ActivityStack.getInstance().getActivity(ActivityStack.getInstance().size() -1);
        if(pd != null){
            pd.dismiss();
        }
        pd = new ProgressDialog(act);
        pd.setCanceledOnTouchOutside(false);

        if (autoService != null) {
            try {
                if (autoService.isStarted()) {
                    pd.setMessage("服务已经开启");
                    pddismiss();
                    sslCallback.startCallback();
                } else {
                    pd.show();

                    int cersRes = autoService.checkCert(0);
//                    if(cersRes == 0) {
//
                        String info = "开始启动服务";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    autoService.start();
                                } catch (RemoteException e) {
                                    pd.setMessage(e.toString());
                                    pddismiss();
                                }
                            }
                        }, 1000);





                        pd.setMessage(info);
//                    }else{
//
//                        pd.setMessage("重新设置证书" + cersRes);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                setCurCert();
//                            }
//                        }, 4000);
//
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                pd.setMessage(e.toString());
                pddismiss();

                autoService = null;

            }
        } else {
            //bindSsl();
            pd.setMessage("未绑定服务，请检查是否安装了SSL客户端");
            pddismiss();
        }
    }


    public void setCurCert() {
        if (autoService != null) {
            new Thread(() -> {
                try {
                    // 这里省略了存在多张证书的情况
                    String cert = autoService.getCerts();
                    // 传入证书名称
                    autoService.setCurCert(cert);
                    String res = "证书设置成功:\n" + cert;
                    Activity act = ActivityStack.getInstance().getActivity(ActivityStack.getInstance().size() -1);
                    act.runOnUiThread(() -> pd.setMessage(res));
                    start();

                } catch (Exception e) {
                    e.printStackTrace();
                    pd.setMessage(e.toString());
                    pddismiss();
                }
            }).start();
        } else {
            pd.setMessage("未绑定服务，请检查是否安装了SSL客户端");
            pddismiss();
        }
    }

    void pddismiss(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        }, 1000);
    }

    public void stop(View view) {
        if (autoService != null) {
            try {
                autoService.stop();
                String info = "关闭服务中";
                pd.setMessage(info);
            } catch (Exception e) {
                e.printStackTrace();
                pd.setMessage(e.toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "未绑定服务，请检查是否安装了SSL客户端", Toast.LENGTH_SHORT).show();
        }
    }


}


