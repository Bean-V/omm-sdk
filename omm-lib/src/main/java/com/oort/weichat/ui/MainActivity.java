package com.oort.weichat.ui;

import static com.oort.weichat.MyApplication.cordovaView;
import static com.oort.weichat.MyApplication.getContext;
import static com.oort.weichat.bean.Friend.ID_SYSTEM_MESSAGE;
import static com.oortcloud.basemodule.CommonApplication.getmSeralNum;
import static com.oortcloud.basemodule.constant.Constant.IsHomeUseAndroid;
import static com.oortcloud.basemodule.constant.Constant.IsHomeUsePad;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_NUM;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_USERID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bumptech.glide.Glide;
import com.coloros.mcssdk.PushManager;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.KeepLiveService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.BuildConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Contact;
import com.oort.weichat.bean.Contacts;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.UploadingFile;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.collection.Collectiion;
import com.oort.weichat.bean.event.EventCreateGroupFriend;
import com.oort.weichat.bean.event.EventQRCodeReady;
import com.oort.weichat.bean.event.EventSendVerifyMsg;
import com.oort.weichat.bean.event.MessageContactEvent;
import com.oort.weichat.bean.event.MessageEventBG;
import com.oort.weichat.bean.event.MessageEventHongdian;
import com.oort.weichat.bean.event.MessageLogin;
import com.oort.weichat.bean.event.MessageSendChat;
import com.oort.weichat.bean.event.MessageUpdate;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.bean.redpacket.TabConfig;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.MucgroupUpdateUtil;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.broadcast.TimeChangeReceiver;
import com.oort.weichat.broadcast.UpdateUnReadReceiver;
import com.oort.weichat.broadcast.UserLogInOutReceiver;
import com.oort.weichat.call.AudioOrVideoController;
import com.oort.weichat.call.CallConstants;
import com.oort.weichat.call.Jitsi_connecting_second;
import com.oort.weichat.call.Jitsi_pre;
import com.oort.weichat.call.MessageEventCancelOrHangUp;
import com.oort.weichat.call.MessageEventInitiateMeeting;
import com.oort.weichat.call.MessageEventMeetingInvite;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.ContactDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.MyZanDao;
import com.oort.weichat.db.dao.NewFriendDao;
import com.oort.weichat.db.dao.OnCompleteListener2;
import com.oort.weichat.db.dao.UploadingFileDao;
import com.oort.weichat.db.dao.UserDao;
import com.oort.weichat.db.dao.login.MachineDao;
import com.oort.weichat.downloader.UpdateManger;
import com.oort.weichat.fragment.AppHelper;
import com.oort.weichat.fragment.DynamicFragment;
import com.oort.weichat.fragment.Fragment_home_lang;
import com.oort.weichat.fragment.Fragment_home_parent;
import com.oort.weichat.fragment.FriendFragment;
import com.oort.weichat.fragment.HomeFragment;
import com.oort.weichat.fragment.MeFragment;
import com.oort.weichat.fragment.MessageFragment;
import com.oort.weichat.fragment.dynamic.DynamicFragment_tab;
import com.oort.weichat.fragment.vs.ControlFragment;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.map.MapHelper;
import com.oort.weichat.pay.PaymentReceiptMoneyActivity;
import com.oort.weichat.pay.ReceiptPayMoneyActivity;
import com.oort.weichat.ui.account.LoginActivity;
import com.oort.weichat.ui.backup.ReceiveChatHistoryActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.dialog.DialogRequestPermission;
import com.oort.weichat.ui.lock.DeviceLockActivity;
import com.oort.weichat.ui.lock.DeviceLockHelper;
import com.oort.weichat.ui.login.WebLoginActivity;
import com.oort.weichat.ui.message.AutoReplyMessageSendChat;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.message.RemindMessageEvent;
import com.oort.weichat.ui.other.BasicInfoActivity;
import com.oort.weichat.ui.other.QRcodeActivity;
import com.oort.weichat.ui.tabbar.config.LocalTabConfigProvider;
import com.oort.weichat.ui.tabbar.config.RemoteTabConfigProvider;
import com.oort.weichat.ui.tabbar.manager.BottomTabManager;
import com.oort.weichat.ui.tabbar.utils.SkinUtils;
import com.oort.weichat.ui.tool.GroupTool;
import com.oort.weichat.ui.tool.MultiImagePreviewActivity;
import com.oort.weichat.ui.tool.WebViewActivity;
import com.oort.weichat.util.AppUtils;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.ContactsUtil;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.DisplayUtil;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.util.JsonUtils;
import com.oort.weichat.util.LocaleHelper;
import com.oort.weichat.util.PermissionUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.ScreenUtil;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.util.log.LogUtils;
import com.oort.weichat.view.PermissionExplainDialog;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.VerifyDialog;
import com.oort.weichat.xmpp.CoreService;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.helloDemon.FirebaseMessageService;
import com.oort.weichat.xmpp.helloDemon.HuaweiClient;
import com.oort.weichat.xmpp.helloDemon.MeizuPushMsgReceiver;
import com.oort.weichat.xmpp.helloDemon.OppoPushMessageService;
import com.oort.weichat.xmpp.helloDemon.VivoPushMessageReceiver;
import com.oort.weichat.xmpp.listener.ChatMessageListener;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.utils.SllCallback;
import com.oortcloud.appstore.utils.SllInterface;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.im.AppUseInfo;
import com.oortcloud.basemodule.im.ApplicationMessageSendChat;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.im.MessageEventChangeUI;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.GPSUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.MainAppInfo;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.contacts.bean.Role;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.message.MessageEventInviteCall;
import com.oortcloud.custom.CordovaView;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.privacyview.AppUtil;
import com.oortcloud.privacyview.PrivacyDialog;
import com.oortcloud.privacyview.PrivacyPolicyActivity;
import com.oortcloud.privacyview.SPUtil;
import com.oortcloud.privacyview.TermsActivity;
import com.oortcloud.revision.fragment.NewFriendFragment;
import com.oortcloud.revision.fragment.NewMessageFragment;
import com.plugins.oortcloud.context.message.GroupCreateEvent;
import com.plugins.oortcloud.context.message.MessageEventPreviewPics;
import com.plugins.oortcloud.imshare.ChatPluginCreateRoomMessage;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;
import com.xuexiang.xui.XUI;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;

import org.apache.cordova.CordovaPlugin;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import io.reactivex.disposables.Disposable;
import koal.ssl.IAutoService;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Call;
import proxy.Proxy;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements PermissionUtil.OnRequestPermissionsResultCallbacks, SllInterface {
    // 小米推送
//    public static final String APP_ID = BuildConfig.XIAOMI_APP_ID;
//    public static final String APP_KEY = BuildConfig.XIAOMI_APP_KEY;
    // 是否重新走initView方法
    // 当切换语言、修改皮肤之后，将该状态置为true
    public static boolean isInitView = false;
    public static boolean isAuthenticated;
    public static MainAppInfo mainAppinfo;
    public static Friend PUBLIC_FRIEND;
    //gps位置信息获取
    private static double mlatitude;
    private static double mlongitude;
    private static String maddress;
    private Location location;
    private GPSUtils gpsUtils;

    private static boolean locationStatus = true;

    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    /**
     * 更新我的群组
     */
    Handler mHandler = new Handler();
    private UpdateUnReadReceiver mUpdateUnReadReceiver = null;
    private UserLogInOutReceiver mUserLogInOutReceiver = null;
    private TimeChangeReceiver timeChangeReceiver = null;
    private ActivityManager mActivityManager;
    // ╔═══════════════════════════════界面组件══════════════════════════════╗
    // ╚═══════════════════════════════界面组件══════════════════════════════╝
    private int mLastFragmentId;// 当前界面
    private RadioGroup mRadioGroup;
    private RadioButton mRbTab0, mRbTab1, mRbTab2, mRbTab3, mRbTab4;
    private TextView mTvHomepageNum;// 显示首页界面未读数量
    private TextView mTvMessageNum;// 显示消息界面未读数量
    private TextView mTvNewFriendNum;// 显示通讯录消息未读数量
    private TextView mTvCircleNum;// 显示朋友圈未读数量
    private int numMessage = 0;// 当前未读消息数量
    private int numCircle = 0; // 当前朋友圈未读数量
    private String mUserId;// 当前登陆的 UserID
    private My_BroadcastReceiver my_broadcastReceiver;
    private int mCurrtTabId;
    private boolean isCreate;
    /**
     * 在其他设备登录了，挤下线
     */
    private boolean isConflict;

//    private LocationClient mClient;
//    private MyLocationListener myLocationListener = new MyLocationListener();

    //隐私政策
    private String SP_PRIVACY = "sp_privacy";
    private String SP_VERSION_CODE = "sp_version_code";
    private boolean isCheckPrivacy = false;
    private long versionCode;
    private long currentVersionCode;
    private DynamicFragment dfrag;
    private DynamicFragment_tab dfrag_tab;
    private HomeFragment hfrag;
    private View mTapView;
    private Fragment_home_parent appsHome;
    private boolean isChangeLanguageFinsh = false;
    private List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> tabConfigs = new ArrayList<>();

    private BottomTabManager mTabManager;


    public MainActivity() {
        noLoginRequired();
    }

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 发起二维码扫描，
     * 仅供MainActivity下属Fragment调用，
     */
    public static void requestQrCodeScan(Activity ctx) {
        int size = ScreenUtil.getScreenWidth(MyApplication.getContext()) / 16 * 9;
        // 生成底部自己的二维码bitmap
        QRcodeActivity.getSelfQrCodeBitmap(size,
                CoreManager.requireSelf(ctx).getUserId(),
                CoreManager.requireSelf(ctx).getNickName());
    }

    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(option);
//            window.setStatusBarColor(Color.TRANSPARENT);//| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusbar_bg));
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XUI.initTheme(this);
        MyApplication.activity = this;

        //checkBadgePermission(this);

        //

        OperLogUtil.msg("进入主页");

        LogUtils.e("main", "进入主页");
        //Toast.makeText(this,"", Toast.LENGTH_LONG);

        // 自动解锁屏幕 | 锁屏也可显示 | Activity启动时点亮屏幕 | 保持屏幕常亮
/*
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
*/
        //获取任务消息
        if (coreManager.getSelf() == null) {
            LogUtils.e("main", "coreManager.getSelf() == null->去登录页面");
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
            return;
        }
        getTaskInfo(coreManager.getSelf().getUserId(), PUBLIC_USERID, PUBLIC_NUM);
        //获取对象并存储
        PUBLIC_FRIEND = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), PUBLIC_USERID);
//        FastSharedPreferences info = FastSharedPreferences.get("TASKINFO");
//        info.edit().putSerializable("friend",mFriend.toString()).apply();

        makeStatusBarTransparent(this);
        setContentView(R.layout.activity_main);

        initWebview();
        //检查隐私政策
//        check();

        LogUtils.e("main", "initWebview（）完成");
        // 启动保活
        if (PrivacySettingHelper.getPrivacySettings(this).getIsKeepalive() == 1) {
            LogUtils.e("main", "启动保活");
            initKeepLive();
        }
        initLog();
        //定时更新位置信息
        if (Build.VERSION.SDK_INT >= 23) {

            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions(permissions, 0);

                new DialogRequestPermission(this, DialogRequestPermission.per_type_position);
                locationStatus = false;
            } else {
                locationStatus = true;
            }
        }
        //模拟器跑需要注释
//        getMylocation();
//        getLocation();
        if (locationStatus && true) {
            gpsUtils = new GPSUtils(MainActivity.this);//初始化GPS

            LogUtils.e("main", "初始化GPS");
        }
        mUserId = coreManager.getSelf().getUserId();

//        getSupportActionBar().hide();
        //  initView();// 初始化控件
        initBroadcast();// 初始化广播
        initDatas();// 初始化一些数据

        // 初始化音视频Control
        AudioOrVideoController.init(mContext, coreManager);

        AsyncUtils.doAsync(this, mainActivityAsyncContext -> {
            // 获取app关闭之前还在上传的消息，将他们的发送状态置为失败
            List<UploadingFile> uploadingFiles = UploadingFileDao.getInstance().getAllUploadingFiles(coreManager.getSelf().getUserId());
            for (int i = uploadingFiles.size() - 1; i >= 0; i--) {
                ChatMessageDao.getInstance().updateMessageState(coreManager.getSelf().getUserId(), uploadingFiles.get(i).getToUserId(),
                        uploadingFiles.get(i).getMsgId(), ChatMessageListener.MESSAGE_SEND_FAILED);
            }
        });


//        UpdateManger.checkUpdate(this, coreManager.getConfig().androidAppUrl, coreManager.getConfig().androidVersion);


        EventBus.getDefault().post(new MessageLogin());
        // 设备锁，
        showDeviceLock();

        initMap();

        // 主页不要侧划返回，和ios统一，
        setSwipeBackEnable(false);

        //初始化获取位置信息
        mlatitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        mlongitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        maddress = sharedPreferences.getString("address", "无地址信息");
        ReportInfo.latitude = mlatitude;
        ReportInfo.longitude = mlongitude;
        ReportInfo.elements = maddress;
//        ReportInfo.photo = AvatarHelper.getAvatarUrl(coreManager.getSelf().getUserId(), true);
        ReportInfo.screen_num = sharedPreferences.getString("share_screen", "");
        ReportInfo.accessToken = getToken();
        AppUseInfo.accessToken = ReportInfo.accessToken;
        AppUseInfo.sn = getmSeralNum();
        //第一次登录，上报信息
        /*if (mlatitude == 0 || mlongitude == 0 || maddress.isEmpty() || maddress == ""){
            //如果定位未取到地址就改用百度地址
            double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
            double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
            String addr = MyApplication.getInstance().getBdLocationHelper().getAddress();

            //百度转wgs84坐标
            LatLng lalng = new LatLng(latitude, longitude);
            LatLng wgs84 = convertBaiduToGPS(lalng);

            double rp_lat = wgs84.latitude;
            double rp_long = wgs84.longitude;
            ReportInfo.latitude = (rp_lat);
            ReportInfo.longitude = (rp_long);
            ReportInfo.elements = addr;
            sharedPreferences.edit().putString("latitude", Double.toString(rp_lat));
            sharedPreferences.edit().putString("longitude", Double.toString(rp_long));
            sharedPreferences.edit().putString("address", addr).apply();
        }*/
        UpdatePoliceInfo();
//        启动连续上报轨迹信息
        if (locationStatus) {
            new Thread(new MyThread()).start();
        }


//       doubleTap();
        checkUpdate2();


        //pd = new ProgressDialog(this);

        //AppManager.mActivity = this;
        //bindSsl();


        LocaleHelper.setLocale(MyApplication.getInstance(), LocaleHelper.getLanguage(MyApplication.getInstance()));
        LogUtils.e("main", "设置Language");


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
//                Intent intent1 = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                //intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivity(intent1);
//            }
//        }

        AppStoreInit.setActivity(this);


        // 初始化Tab管理器
        initTabManager();

        // 示例：2秒后更新第一个Tab的未读数量
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mTabManager != null) {
                mTabManager.updateTabUnreadCount("tab_home", 3);
            }
        }, 2000);

        ReactNativeSSLHelper.initReactNativeUnsafeSSL(this);
        ReactWebSocketSSLHelper.initInsecureSSL();


    }


    // 通知权限请求码
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1008;
    // 悬浮窗权限请求码（在PermissionHelper中已定义，此处复用）
    private static final int REQUEST_OVERLAY_PERMISSION = PermissionHelper.REQUEST_OVERLAY_PERMISSION;

    @Override
    protected void onResume() {
        super.onResume();
        // 从权限设置页返回后，再次检查权限
        checkNecessaryPermissions();



    }

    /**
     * 检查所有必要权限（悬浮窗+通知）
     */
    private static final String PREFS_NAME = "popup_permission_prefs";
    private static final String KEY_GUIDED = "popup_permission_guided";
    private void checkNecessaryPermissions() {
        // 1. 检查悬浮窗权限
        if (!PermissionHelper.hasOverlayPermission(this)) {
            showOverlayPermissionDialog();
            return; // 悬浮窗权限优先，未开启则不检查其他权限
        }

        // 2. 检查通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                showNotificationPermissionDialog();
            }
        }

        // 2. 引导厂商特定的后台弹窗权限（使用系统API记录是否已引导）

        if(!com.oortcloud.basemodule.constant.Constant.HAVA_VERIFY) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean isGuided = prefs.getBoolean(KEY_GUIDED, false); // 系统API获取状态

            if (!isGuided) {
                // 显示引导弹窗
                BrandPopupPermissionHelper.showBrandGuideDialog(this);
                // 系统API保存状态（标记为已引导，避免重复弹窗）
                prefs.edit().putBoolean(KEY_GUIDED, true).apply();
            }
        }
    }

    /**
     * 悬浮窗权限申请对话框
     */
    private void showOverlayPermissionDialog() {
        new AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle("请开启悬浮窗权限")
                .setMessage("为了在后台正常接收通话请求并弹出接听界面，需要开启“显示在其他应用上层”权限")
                .setCancelable(false) // 强制用户处理
                .setPositiveButton("去开启", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermissionHelper.requestOverlayPermission(this);
                    }
                })
                .show();
    }

    /**
     * 通知权限申请对话框（Android 13+）
     */
    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle("请开启通知权限")
                .setMessage("为了不错过通话请求，需要开启通知权限")
                .setCancelable(false)
                .setPositiveButton("去开启", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                REQUEST_NOTIFICATION_PERMISSION
                        );
                    }
                })
                .show();
    }

    /**
     * 处理悬浮窗权限申请结果（从设置页返回）
     */


    /**
     * 处理通知权限申请结果
     */

    private void initTabManager() {
        mRadioGroup = findViewById(R.id.main_rg);
        LinearLayout tab_container = findViewById(R.id.tab_container);
        int fragmentContainerId = R.id.main_content;

        // 创建Tab管理器
        mTabManager = new BottomTabManager(
                this,
                tab_container,
                fragmentContainerId,
                SkinUtils.getSkin(this)
        );

        // 方式1：使用本地默认配置
       // useLocalConfig();

        // 方式2：使用后台配置（注释掉方式1，打开此方式）
        // useRemoteConfig();
        mTabManager.setOnTabSelectedListener((tabModel, fragment) -> {
            // 可选：做额外逻辑，比如统计点击
            Log.d("BottomTab", "点击了 Tab: " + tabModel.getLabel());
        });

        getBottomTabConfigs();
    }


    // 使用本地配置
    private void useLocalConfig() {
        LocalTabConfigProvider localConfig = new LocalTabConfigProvider(this);
        mTabManager.initTabs(localConfig);
    }

    // 使用后台配置
    private void useRemoteConfig(List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> tabConfigs) {
        // 注意：网络请求需在子线程执行
        new Thread(() -> {
            try {
                // 替换为实际的后台配置接口地址
                RemoteTabConfigProvider remoteConfig = new RemoteTabConfigProvider(
                        this,
                        tabConfigs
                );
                // remoteConfig.fetchRemoteConfig();

                // 切回主线程更新UI
                runOnUiThread(() -> mTabManager.initTabs(remoteConfig));
            } catch (Exception e) {
                e.printStackTrace();
                // 失败时使用本地配置作为 fallback
                runOnUiThread(this::useLocalConfig);
            }
        }).start();
    }

    private GestureDetector mGestureDetector;

    void doubleTap() {
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (mTapView.getId() == R.id.rb_tab_0) {
                            if (hfrag != null) {
                                hfrag.getAppInfo();
                            }
                        } else {
                            if (dfrag != null) {
                                dfrag.getApp();
                            }
                        }
                        return true;
                    }

                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        Intent intent = new Intent(OtherBroadcast.singledown);
                        MyApplication.getInstance().sendBroadcast(intent);
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        Intent intent = new Intent(OtherBroadcast.longpress);
                        MyApplication.getInstance().sendBroadcast(intent);
                    }
                });
        {
            RadioButton rd = findViewById(R.id.rb_tab_3);
            rd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (rd.isChecked()) {
                        mTapView = view;
                        mGestureDetector.onTouchEvent(motionEvent);
                    }
                    return false;
                }
            });
        }


        {
            RadioButton rd = findViewById(R.id.rb_tab_0);
            rd.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (rd.isChecked()) {
                        mTapView = view;
                        mGestureDetector.onTouchEvent(motionEvent);
                    }
                    return false;
                }
            });
        }
    }

    private void getLastAppInfo() {

        Log.d("zlm", ReportInfo.accessToken);
        Log.d("zlm", ReportInfo.oort_uuid);
        Log.d("zlm", ReportInfo.depart_code);
        String appname = getResources().getString(R.string.app_name);
        HttpRequestParam.appSearch(appname, ReportInfo.accessToken, ReportInfo.oort_uuid, ReportInfo.depart_code).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
            }

            @Override
            public void onNext(String s) {
                Log.d("zlm", s);
                //1.搜索是否成功
                DialogHelper.dismissProgressDialog();
                if (TextUtils.isEmpty(s)) {
//                    ToastUtils.showShortSafe("登录失败！");
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    int code = jsonObject.getIntValue("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int count = data.getIntValue("count");
                        if (count > 0) {
                            JSONArray array = data.getJSONArray("app_list");
                            Object jsonObject1 = array.get(0);
                            mainAppinfo = JSON.parseObject(String.valueOf(jsonObject1), MainAppInfo.class);
                            if (mainAppinfo != null) {
                                /*Log.d("zlm", mainAppinfo.getApk_url());
                                Log.d("zlm", mainAppinfo.getApppackage());
                                Log.d("zlm", mainAppinfo.getNew_versioncode()+"");
                                Log.d("zlm", mainAppinfo.getVer_description());*/
                                String url = StringUtil.getUrlRelativePath(mainAppinfo.getApk_url());
                                String download_url = com.oortcloud.basemodule.constant.Constant.BASE_URL + url;
                                UpdateManger.checkUpdate(MainActivity.this,
                                        download_url,
                                        mainAppinfo.getNew_versioncode(),
                                        mainAppinfo.getVer_description(),
                                        false);
                            }
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("appsearch", "json error!");
                }
                //2.搜索结果是否为空

                //3.应用包名是否一一致

                //4.检查是否有新版本更新


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("zlm", e.getMessage().toString());
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    private void initWebview() {
       // ConfigXmlParser
        cordovaView = new CordovaView(this);
        cordovaView.initCordova(this);
        cordovaView.loadUrl("file:///android_asset/home/index.html");
        WebSettings settings = cordovaView.getWebview().getSettings();
        // 1. 设置缓存路径
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + "cache/";

        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setAppCachePath(cacheDirPath);
//        // 2. 设置缓存大小
//        settings.setAppCacheMaxSize(20*1024*1024);
//        // 3. 开启Application Cache存储机制
//        settings.setAppCacheEnabled(true);
        //4.开启DOM storage
        settings.setDomStorageEnabled(true);
        //5.只需设置支持JS就自动打开IndexedDB存储机制
        settings.setJavaScriptEnabled(true);
    }

    /*private void getLocation() {
        // 定位初始化
        mClient = new LocationClient(this);
        LocationClientOption mOption = new LocationClientOption();
        mOption.setIsNeedAltitude(true);
        mOption.setScanSpan(1000 * 10);
        mOption.setCoorType("bd09ll");
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mClient.setLocOption(mOption);
        mClient.registerLocationListener(myLocationListener);
        mClient.start();
    }*/

    /*private void getMylocation() {

        LocationUtil.requestLocation(getApplicationContext(), LocationUtil.Mode.AUTO, onResponseListener);
    }

    private LocationUtil.OnResponseListener onResponseListener = new LocationUtil.OnResponseListener() {
        @Override
        public void onSuccessResponse(double latitude, double longitude) {
            if (latitude != 0.0f) {
                mlatitude = latitude;
            }
            if (longitude != 0.0f) {
                mlongitude = longitude;
            }
            if (latitude != 0.0f  &&  longitude != 0.0f) {
                Address addr = LocationUtil.getAddress(getApplicationContext(), mlatitude, mlongitude);
                if (addr != null){
                    maddress = addr.getAddressLine(0);
                }else{
                    maddress = "无地址信息";
                }
                CommonApplication.setMaddress(maddress);
                CommonApplication.setMlatitude(mlatitude);
                CommonApplication.setMlongitude(mlongitude);
                Log.d("gps", "获取位置成功");
                sharedPreferences.edit().putString("latitude", Double.toString(mlatitude));
                sharedPreferences.edit().putString("longitude", Double.toString(mlongitude));
                sharedPreferences.edit().putString("address", maddress).apply();
                //更新位置信息
                ReportInfo.elements = maddress;
                ReportInfo.latitude = mlatitude;
                ReportInfo.longitude = mlongitude;
                //位置更新上报信息
                UpdatePoliceInfo();
            }
            Log.d("gps", "onSuccessResponse");
        }

        @Override
        public void onErrorResponse(String provider, int status) {
            Log.d("gps","获取位置失败");
        }
    };*/


    public static void UpdatePoliceInfo() {

        //body
        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", ReportInfo.accessToken);
        map.put("depart_code", ReportInfo.depart_code);
        map.put("depart_name", ReportInfo.depart_name);
        map.put("electric", ReportInfo.electric);
        map.put("elements", ReportInfo.elements);
        map.put("interphone", ReportInfo.interphone);
        map.put("latitude", Double.toString(ReportInfo.latitude));
        map.put("longitude", Double.toString(ReportInfo.longitude));
        map.put("name", ReportInfo.name);
        map.put("phone", ReportInfo.phone);
        map.put("photo", ReportInfo.photo);
        map.put("police_id", ReportInfo.police_id);
        map.put("police_type", ReportInfo.police_type);
        map.put("position", ReportInfo.position);
        map.put("position_type", ReportInfo.position_type);
        map.put("remark", ReportInfo.remark);
        map.put("screen_num", ReportInfo.screen_num);
        map.put("shift", ReportInfo.shift);
        map.put("signal", ReportInfo.signal);
        map.put("unit", ReportInfo.unit);
        map.put("video_num", ReportInfo.video_num);
        if (TextUtils.isEmpty(ReportInfo.sn)) {
            ReportInfo.sn = getmSeralNum();
        }
        map.put("sn", ReportInfo.sn);
//        map.put("remark", "v" + BuildConfig.VERSION_NAME);
        /*Log.e("depart_code", ReportInfo.depart_code);
        Log.e("electric", ReportInfo.electric +"");
        Log.e("elements", ReportInfo.elements);
        Log.e("interphone", ReportInfo.interphone);
        Log.e("latitude", ReportInfo.latitude);
        Log.e("longitude", ReportInfo.longitude);
        Log.e("name", ReportInfo.name);
        Log.e("phone", ReportInfo.phone);
        Log.e("photo", ReportInfo.photo);
        Log.e("police_id", ReportInfo.police_id);
        Log.e("police_type", ReportInfo.police_type +"");
        Log.e("position", ReportInfo.position);
        Log.e("position_type", ReportInfo.position_type+"");
        Log.e("shift", ReportInfo.shift);
        Log.e("signal", ReportInfo.signal+"");
        Log.e("unit", ReportInfo.unit);
        Log.e("video_num", ReportInfo.video_num);*/

        String params = new Gson().toJson(map);
//        Log.e("params",params);
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(com.oortcloud.basemodule.constant.Constant.BASE_URL +
                com.oortcloud.basemodule.constant.Constant.POLICE_REPORT, params, new com.oortcloud.basemodule.utils.HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {
                if (requst != null) {
//                    Log.e("UpdatePoliceInfo",requst);
                }
            }
        });

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 要做的事情
            if (msg.what == 1) {
                if (location != null)
                    Log.d("gps", "刷新位置 经度：" + String.valueOf(location.getLongitude()) + "纬度：" + String.valueOf(location.getLatitude()));
            } else if (msg.what == 2) {

            }
            super.handleMessage(msg);
        }
    };

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
        map.put("sn", ReportInfo.sn);
        /*Log.e("elements",ReportInfo.elements);
        Log.e("latitude",ReportInfo.latitude);
        Log.e("longitude",ReportInfo.longitude);
        Log.e("name",ReportInfo.name);
        Log.e("phone",ReportInfo.phone);
        Log.e("police_id",ReportInfo.police_id);*/

        String params = new Gson().toJson(map);
//        Log.e("params",params);
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(com.oortcloud.basemodule.constant.Constant.BASE_3CLASSURL +
                com.oortcloud.basemodule.constant.Constant.POLICE_LOCATION, params, new com.oortcloud.basemodule.utils.HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {
                if (requst != null) {
//                    Log.e("updateMyLocation",requst);
                }
            }
        });

    }


    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    location = gpsUtils.getLocation();//获取位置信息
                    updateLocation();


                    if (BuildConfig.DEBUG && false) {
                        Thread.sleep(1000 * 200);
                    } else {
                        Thread.sleep(1000 * 10);
                    }
                    // 线程暂停60秒，单位毫秒
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);// 发送消息

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateLocation() {
        if (location != null) {
            String addr = GPSUtils.getAddressStr();
            if (addr != "") {
                maddress = addr;
            } else {
                maddress = "无地址信息";
            }
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();
            CommonApplication.setMaddress(maddress);
            CommonApplication.setMlatitude(mlatitude);
            CommonApplication.setMlongitude(mlongitude);
//            Log.d("gps", "获取位置成功");
            sharedPreferences.edit().putString("latitude", String.valueOf(mlatitude));
            sharedPreferences.edit().putString("longitude", String.valueOf(mlongitude));
            sharedPreferences.edit().putString("address", maddress).apply();
            //更新位置信息
            ReportInfo.elements = maddress;
            ReportInfo.latitude = mlatitude;
            ReportInfo.longitude = mlongitude;
            //位置更新
            UpdatePoliceInfo();
            //坐标上报
            updateMyLocation();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent1");
        if (isInitView) {
            Log.e(TAG, "onNewIntent2");
            // 皮肤深浅变化时需要改状态栏颜色，
            setStatusBarColor();
            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> lf = fm.getFragments();
            for (Fragment f : lf) {
                fm.beginTransaction().remove(f).commitNowAllowingStateLoss();
            }
            // initView();

            initTabManager();
        }
        MainActivity.isInitView = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //第一次未获取位置成功时，需要多次监听
//        getMylocation();
//        UpdatePoliceInfo();
        // 主要针对侧滑返回，刷新消息会话列表，
        MsgBroadcast.broadcastMsgUiUpdate(mContext);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!JCVideoPlayer.backPress()) {
                // 调用JCVideoPlayer.backPress()
                // true : 当前正在全屏播放视频
                // false: 当前未在全屏播放视频
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        /*if(mClient != null) {
            // 关闭前台定位服务
            mClient.disableLocInForeground(true);
            // 取消之前注册的 BDAbstractLocationListener 定位监听函数
            mClient.unRegisterLocationListener(myLocationListener);
            // 停止定位sdk
            mClient.stop();
        }*/
        // XMPP断开连接 必须调用disconnect 否则服务端不能立即检测出当前用户离线 导致推送延迟





        try {
            UpdatePoliceInfo();

            EventBus.getDefault().unregister(this);
            unregisterReceiver(mUpdateUnReadReceiver);
            unregisterReceiver(mUserLogInOutReceiver);
            unregisterReceiver(my_broadcastReceiver);
            unregisterReceiver(timeChangeReceiver);
            EventBus.getDefault().unregister(this);
            Glide.get(this).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (isChangeLanguageFinsh) {
                        return;
                    }
                    coreManager.disconnect();
                    Glide.get(getApplicationContext()).clearDiskCache();
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
//        unbindService(serviceConnection);
//        // 取消监听广播
//        unregisterReceiver(srvMonitor);
    }

    private void initKeepLive() {
        //启动保活服务
        KeepLive.startWork(getApplication(), KeepLive.RunMode.ENERGY,
                //你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
                new KeepLiveService() {
                    /**
                     * 运行中
                     * 由于服务可能会多次自动启动，该方法可能重复调用
                     */
                    @Override
                    public void onWorking() {
                        Log.e("xuan", "onWorking: ");
                    }

                    /**
                     * 服务终止
                     * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                     */
                    @Override
                    public void onStop() {
                        Log.e("xuan", "onStop: ");
                    }
                }
        );
    }

    private void initLog() {
//        String dir = FileUtil.getSaveDirectory("IMLogs");
//        LogUtils.setLogDir(dir);
        // LogUtils.setLogLevel(LogUtils.LogLevel.WARN);


        // OperLogUtil.setLogLevel(LogUtils.LogLevel.WARN);
    }


    private void changeFragment(int checkedId) {
        // 遍历RadioGroup找到选中的RadioButton


        if (com.oortcloud.basemodule.constant.Constant.ISTABFROMSERVER) {
            RadioButton checkedRb = (RadioButton) mRadioGroup.findViewById(checkedId);
            if (checkedRb == null) return;

            // 获取对应的TabConfig（通过位置关联）
            int position = mRadioGroup.indexOfChild(checkedRb);
            List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> currentTabs = mCurrentTabConfigs; // 保存合并后的配置列表
            TabConfig.DataBean.InnerData.AppSetting.BottomConfig config = currentTabs.get(position);

            // 根据relateApp的app_id判断加载哪个Fragment
            if (config.getRelateApp() != null) {
                String apk = config.getRelateApp().getApppackage();
                if (apk.equals("com.dispatch.oort")) {
                    showFragment(new ControlFragment());
                } else if (apk.equals("com.oortcloud.weichat")) {
                    //showFragment(new WeichatFragment());
                } else if (apk.equals("com.oortcloud.apps")) {
                    showFragment(HomeFragment.newInstance(apk));
                } else {
                    showFragment(HomeFragment.newInstance(apk));
                }

            } else {
                // 无关联应用时显示默认Fragment
                showFragment(new HomeFragment());
            }
        } else {
            changeFragment_(checkedId);
        }
    }

    // 用于记录当前显示的Fragment，避免重复创建
    private Fragment mCurrentFragment;
    // Fragment管理器（支持AndroidX）
    private FragmentManager mFragmentManager;

    List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> mCurrentTabConfigs;


    /**
     * 显示指定的Fragment，处理切换逻辑
     *
     * @param targetFragment 要显示的目标Fragment
     */
    private void showFragment(Fragment targetFragment) {
        // 1. 防御性检查：避免空指针或已销毁的Fragment
        if (targetFragment == null || targetFragment.isDetached() || targetFragment.isRemoving()) {
            return;
        }

        // 2. 避免重复显示同一个Fragment（实例级判断）
        if (mCurrentFragment == targetFragment) {
            // 额外检查：如果当前Fragment处于隐藏状态，强制显示（防止状态异常）
            if (mCurrentFragment.isHidden()) {
                showExistingFragment(targetFragment);
            }
            return;
        }

        // 3. 获取FragmentTag（使用类名+实例标识，确保唯一性）
        String targetTag = getFragmentTag(targetFragment);

        // 4. 检查Fragment是否已存在于FragmentManager中（比isAdded()更可靠）
        Fragment existingFragment = mFragmentManager.findFragmentByTag(targetTag);
        if (existingFragment != null) {
            // 复用已存在的Fragment（可能是之前被移除后又添加的场景）
            targetFragment = existingFragment;
        }

        // 5. 开启事务（使用commitNow()确保同步执行，避免异步状态不一致）
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        // 6. 处理目标Fragment：未添加则添加，已添加则显示
        if (!targetFragment.isAdded()) {
            // 添加前再次检查（双重保险，防止极端情况）
            if (mFragmentManager.findFragmentByTag(targetTag) == null) {
                transaction.add(R.id.main_content, targetFragment, targetTag);
            }
        } else {
            transaction.show(targetFragment);
        }

        // 7. 隐藏当前显示的Fragment（如果存在且未被销毁）
        if (mCurrentFragment != null && !mCurrentFragment.isRemoving()) {
            transaction.hide(mCurrentFragment);
        }

        // 8. 提交事务（使用commitNow()替代commit()，确保立即执行，避免状态滞后）
        try {
            transaction.commitNow(); // 同步执行事务，保证后续代码能获取最新状态
        } catch (IllegalStateException e) {
            // 捕获事务提交异常（如Activity已销毁）
            Log.e("showFragment", "Commit transaction failed", e);
            return;
        }

        // 9. 更新当前Fragment引用（事务已执行，状态可靠）
        mCurrentFragment = targetFragment;
    }

    /**
     * 生成唯一的Fragment Tag（类名+实例hashCode，避免同类型不同实例的Tag冲突）
     */
    private String getFragmentTag(Fragment fragment) {
        return fragment.getClass().getSimpleName() + "_" + fragment.hashCode();
    }

    /**
     * 单独处理已存在但被隐藏的Fragment显示逻辑（防止状态异常）
     */
    private void showExistingFragment(Fragment fragment) {
        if (fragment.isAdded() && fragment.isHidden()) {
            try {
                mFragmentManager.beginTransaction()
                        .show(fragment)
                        .commitNow();
            } catch (IllegalStateException e) {
                Log.e("showFragment", "Show existing fragment failed", e);
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void initBroadcast() {
        EventBus.getDefault().register(this);

        // 注册未读消息更新广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MsgBroadcast.ACTION_MSG_NUM_UPDATE);
        filter.addAction(MsgBroadcast.ACTION_MSG_NUM_UPDATE_NEW_FRIEND);
        filter.addAction(MsgBroadcast.ACTION_MSG_NUM_RESET);
        mUpdateUnReadReceiver = new UpdateUnReadReceiver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mUpdateUnReadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mUpdateUnReadReceiver, filter);
        }

        // 注册用户登录状态广播
        mUserLogInOutReceiver = new UserLogInOutReceiver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mUserLogInOutReceiver, LoginHelper.getLogInOutActionFilter(), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mUserLogInOutReceiver, LoginHelper.getLogInOutActionFilter());
        }

        // 刷新评论的广播和 关闭主界面的，用于切换语言，更改皮肤用
        filter = new IntentFilter();
        // 当存在阅后即焚文字类型的消息时，当计时器计时结束但聊天界面已经销毁时(即聊天界面收不到该广播，消息也不会销毁)，代替销毁
        filter.addAction(Constants.UPDATE_ROOM);
        filter.addAction(Constants.PING_FAILED);
        filter.addAction(Constants.CLOSED_ON_ERROR_END_DOCUMENT);
        filter.addAction(OtherBroadcast.SYNC_CLEAN_CHAT_HISTORY);
        filter.addAction(OtherBroadcast.SYNC_SELF_DATE);
        filter.addAction(OtherBroadcast.CollectionRefresh);
        filter.addAction(OtherBroadcast.SEND_MULTI_NOTIFY);  // 群发消息结束
        my_broadcastReceiver = new My_BroadcastReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(my_broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(my_broadcastReceiver, filter);
        }

        // 监听系统时间设置，
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeChangeReceiver = new TimeChangeReceiver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(timeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(timeChangeReceiver, filter);
        }
    }

    private void initDatas() {
        // 检查用户的状态，做不同的初始化工作
        Log.e("login-step", "initDatas");

        LogUtils.e("main", "initDatas");
        User loginUser = coreManager.getSelf();
        if (!LoginHelper.isUserValidation(loginUser)) {
            LoginHelper.prepareUser(this, coreManager);
        }

        mUserId = loginUser.getUserId();
        FriendDao.getInstance().checkSystemFriend(mUserId); // 检查 两个公众号

        // 更新所有未读的信息
        updateNumData();
        AppHelper.getInstance().initialize(this);
        if (com.oortcloud.basemodule.constant.Constant.HAVA_VERIFY) {
            return;
        }

        LogUtils.e("main", "myAutoLogin");

        // 进入主页后调的接口，都在刷新accessToken后再调用，
        Log.e("login-step", "initDatas-start");
        LogUtils.e("main", "initDatas-start");
        loginRequired();
        initCore();
        CoreManager.initLocalCollectionEmoji();
        CoreManager.updateMyBalance();

        LogUtils.e("main", "initOther");
        initOther();// 初始化第三方
        checkTime();
        // 上传本地通讯录
        if ((coreManager.getConfig().isSupportAddress
                && !coreManager.getConfig().registerUsername)) {
            addressBookOperation();
        }
        login();
        updateSelfData();
        ReportInfo.accessToken = getToken();
        AppUseInfo.accessToken = ReportInfo.accessToken;
        Log.e("login-step", ReportInfo.accessToken + "==" + ReportInfo.oort_uuid);
        Log.e("login-step", "initDatas-end");
        AppStoreInit.store_token = "";
        String token = FastSharedPreferences.get("USERINFO_SAVE").getString("token", "");
        String uuid = UserInfoUtils.getInstance(getContext()).getUserId();
        AppStoreInit.store_token = "";

        AppStoreInit.initData(token, uuid);
        if (false) {

            AppStoreInit.initData(token, uuid);
            LogUtils.e("main", "AppStoreInit.initData");
            if (hfrag != null) {
                hfrag.getAppInfo();
                LogUtils.e("main", "hfrag.getApp()");
            }
            if (appsHome != null) {
                appsHome.getData();
                LogUtils.e("main", "happsHome.getData()");
            }
        }
        //getpnlist();
        setStatusBarLight(true);
        mUserId = loginUser.getUserId();
        FriendDao.getInstance().checkSystemFriend(mUserId); // 检查 两个公众号

        // 更新所有未读的信息
        updateNumData();
        AppHelper.getInstance().initialize(this);


    }

    private void getBottomTabConfigs() {
        // 先使用缓存
        List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> cachedConfigs = getTabConfigsFromCache();
        if (cachedConfigs != null) {
            // 使用缓存配置
           // useRemoteConfig(cachedConfigs);
        }

        // 然后尝试从网络获取新数据
        RequesManager.getBottomTabs().subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                TabConfig config = new Gson().fromJson(s, new TypeToken<TabConfig>() {
                }.getType());
                try {
                    List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> newConfigs = config.getData().getData().getAppSetting().getBottomConfig();

                    // 1. 新数据与缓存做对比，若不一致，则更新缓存
                    if (cachedConfigs == null || !newConfigs.equals(cachedConfigs)) {
                        // 更新缓存
                        saveTabConfigsToCache(newConfigs);
                        // 使用新配置
                        useRemoteConfig(newConfigs);
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(MainActivity.this,e.getMessage().toString());
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                // 网络请求失败时使用缓存
                if (cachedConfigs != null) {
                    useRemoteConfig(cachedConfigs);
                }
            }
        });
    }


    public void saveTabConfigsToCache(List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> tabConfigs) {
        SharedPreferences preferences = getSharedPreferences("tab_configs_cache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(tabConfigs);
        editor.putString("tab_configs", json);
        editor.apply();
    }

    public List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> getTabConfigsFromCache() {
        SharedPreferences preferences = getSharedPreferences("tab_configs_cache", Context.MODE_PRIVATE);
        String json = preferences.getString("tab_configs", null);
        if (json != null) {
            Type type = new TypeToken<List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig>>() {
            }.getType();
            return new Gson().fromJson(json, type);
        }
        return null; // 返回 null 如果没有缓存数据
    }


    void getpnlist() {


        String record = FastSharedPreferences.get("httpRes").getString("publicNumbers_" + mUserId, "");
        HttpRequestCenter.getadminLists().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {


                com.oortcloud.contacts.bean.Result<List<Role>> result = new Gson().fromJson(s, new TypeToken<com.oortcloud.contacts.bean.Result<List<Role>>>() {
                }.getType());
                if (result.getResultCode() == 1) {


                    if (!s.equals(record)) {
                        FastSharedPreferences.get("httpRes").edit().putString("publicNumbers_" + mUserId, s).apply();
                    }


                }
            }

        });
    }

    private void showDeviceLock() {
        if (DeviceLockHelper.isLocked()) {
            // 有开启设备锁，
            DeviceLockActivity.start(this);
        } else {
            Log.e("DeviceLock", "没开启设备锁，不弹出设备锁");
        }
    }

    private void initMap() {
        // 中国大陆只能使用百度，
        // 墙外且有谷歌框架才能使用谷歌地图，
        String area = PreferenceUtils.getString(this, AppConstant.EXTRA_CLUSTER_AREA);
        if (TextUtils.equals(area, "CN")) {
            MapHelper.setMapType(MapHelper.MapType.BAIDU);
        } else {
            MapHelper.setMapType(MapHelper.MapType.GOOGLE);
        }
    }

    /**
     * 切换Fragment
     */


    private void changeFragment_(int checkedId) {
        if (mLastFragmentId == checkedId) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(checkedId));
        if (fragment == null) {

            if (tabConfigs.isEmpty()) {
                if (checkedId == R.id.rb_tab_0) {
                    if (IsHomeUsePad) {
                        fragment = new Fragment_home_lang();
                        //appsHome = (Fragment_home_parent) fragment;
                    } else if (IsHomeUseAndroid) {
                        fragment = new Fragment_home_parent();
                        appsHome = (Fragment_home_parent) fragment;


                    } else {
                        fragment = new HomeFragment();
                        hfrag = (HomeFragment) fragment;
                    }
                    OperLogUtil.msg("点击了首页");
                } else if (checkedId == R.id.rb_tab_1) {//                    fragment = new MessageFragment();
                    fragment = new NewMessageFragment();
                    OperLogUtil.msg("点击了消息");
                } else if (checkedId == R.id.rb_tab_2) {//                    fragment = new FriendFragment();
                    fragment = new NewFriendFragment();
//                    testSendMsg("10017742","2");
//                    EventBus.getDefault().post(new ApplicationMessageSendChat("10017743","content"));
                    OperLogUtil.msg("点击了通讯录");
                } else if (checkedId == R.id.rb_tab_3) {/*if (coreManager.getConfig().newUi) { // 切换新旧两种ui对应不同的发现页面，
                        fragment = new SquareFragment();
                    } else {
                        fragment = new DiscoverFragment();
                    }*/

                    if (!IsHomeUseAndroid) {
                        fragment = new DynamicFragment();
                        dfrag = (DynamicFragment) fragment;
                    } else {

                        fragment = new DynamicFragment_tab();
                        dfrag_tab = (DynamicFragment_tab) fragment;
                    }
                    OperLogUtil.msg("点击了动态");
                } else if (checkedId == R.id.rb_tab_4) {
                    fragment = new MeFragment();
                    OperLogUtil.msg("点击了我的");
                } else {
                    throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            } else {
                fragment = getTabFragment(checkedId);
            }
        }
        // fragment = null;
        assert fragment != null;

        if (!fragment.isAdded()) {// 未添加 add
            transaction.add(R.id.main_content, fragment, String.valueOf(checkedId));
        }

        Fragment lastFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(mLastFragmentId));

        if (lastFragment != null) {
            transaction.hide(lastFragment);
        }
        // 以防万一出现last和current都是同一个fragment的情况，先hide再show,
        transaction.show(fragment);

        // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);// 添加动画
        transaction.commitNowAllowingStateLoss();

        // getSupportFragmentManager().executePendingTransactions();

        mLastFragmentId = checkedId;

        setStatusBarLight(true);
        //判断动态是不是在tab打开
        if (checkedId == R.id.rb_tab_3 || checkedId == R.id.rb_tab_0) {
            CommonApplication.pIsTab = true;
        } else {
            CommonApplication.pIsTab = false;
        }
    }

    /**
     * OPPO手机：App的通知默认是关闭的，需要检查通知是否开启
     * OPPO手机：App后台时，调用StartActivity方法不起做用，需提示用户至 手机管家-权限隐私-自启动管理 内该App的自启动开启
     * <p>
     * 小米与魅族手机需要开启锁屏显示权限，否则在锁屏时收到音视频消息来电界面无法弹起（其他手机待测试，华为手机无该权限设置，锁屏时弹起后直接干掉弹起页面）
     */
    private void checkNotifyStatus() {
        int launchCount = PreferenceUtils.getInt(this, Constants.APP_LAUNCH_COUNT, 0);// 记录app启动的次数
        Log.e("zq", "启动app的次数:" + launchCount);
        if (launchCount == 1) {
            String tip = "";
            if (!AppUtils.isNotificationEnabled(this)) {
                tip = getString(R.string.title_notification) + "\n" + getString(R.string.content_notification);
            }
            if (DeviceInfoUtil.isOppoRom()) {// 如果Rom为OPPO，还需要提醒用户开启自启动
                tip += getString(R.string.open_auto_launcher);
            }
            if (!TextUtils.isEmpty(tip)) {
                SelectionFrame dialog = new SelectionFrame(this);
                dialog.setSomething(null, tip, new SelectionFrame.OnSelectionFrameClickListener() {
                    @Override
                    public void cancelClick() {

                    }

                    @Override
                    public void confirmClick() {
                        PermissionUtil.startApplicationDetailsSettings(MainActivity.this, 0x001);
                    }
                });
                dialog.show();
            }
        } else if (launchCount == 2) {
            if (DeviceInfoUtil.isMiuiRom() || DeviceInfoUtil.isMeizuRom()) {
                SelectionFrame dialog = new SelectionFrame(this);
                dialog.setSomething(getString(R.string.open_screen_lock_show),
                        getString(R.string.open_screen_lock_show_for_audio), new SelectionFrame.OnSelectionFrameClickListener() {
                            @Override
                            public void cancelClick() {

                            }

                            @Override
                            public void confirmClick() {
                                PermissionUtil.startApplicationDetailsSettings(MainActivity.this, 0x001);
                            }
                        });
                dialog.show();
            }
        }
    }

    private void initOther() {
        Log.d(TAG, "initOther() called");

        // 服务器端是根据最后调用的上传推送ID接口决定使用什么推送，
        // 也就是在这里最后初始化哪个推送就会用哪个推送，

        //noinspection ConstantConditions
        AsyncUtils.doAsync(this, t -> {
            Reporter.post("初始化推送失败", t);
        }, mainActivityAsyncContext -> {
            if (coreManager.getConfig().enableGoogleFcm && googleAvailable()) {
                if (HttpUtil.testGoogle()) {// 拥有谷歌服务且能翻墙 使用谷歌推送
                    FirebaseMessageService.init(MainActivity.this);
                } else {// 虽然手机内有谷歌服务，但是不能翻墙，还是根据机型判断使用哪种推送
                    selectPush();
                }
            } else {
                selectPush();
            }
        });
    }

    private boolean googleAvailable() {
        boolean isGoogleAvailability = true;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            // 存在谷歌框架但是不可用，
            // 官方做法弹个对话框提示，
            // if (googleApiAvailability.isUserResolvableError(resultCode)) {
            //     googleApiAvailability.getErrorDialog(this, resultCode, 2404).show();
            // }
            // 当成没有谷歌框架处理，
            isGoogleAvailability = false;
        }
        return isGoogleAvailability;
    }

    private Fragment getTabFragment(int index) {
        Fragment fragment = null;
        int localIndex = 0;
        if (index == R.id.rb_tab_0) {
            localIndex = 0;
        } else if (index == R.id.rb_tab_1) {
            localIndex = 1;
        } else if (index == R.id.rb_tab_2) {
            localIndex = 2;
        } else if (index == R.id.rb_tab_3) {
            localIndex = 3;
        } else if (index == R.id.rb_tab_4) {
            localIndex = 4;
        } else {
            localIndex = 0;
        }
        String packageName = tabConfigs.get(localIndex).getRelateApp().getApppackage();
        switch (packageName) {
            case "HomeFragment":
                fragment = new HomeFragment();
                break;
            case "MessageFragment":
                fragment = new MessageFragment();
                break;
            case "FriendFragment":
                fragment = new FriendFragment();
                break;
            case "DymcFragment":
                fragment = new DynamicFragment();
                break;
            case "MeFragment":
                fragment = new MeFragment();
                break;
            case "DispatchFragment":
                fragment = new ControlFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        return fragment;
    }

    @SuppressWarnings({"PointlessBooleanExpression", "ConstantConditions"})
    private void selectPush() {
        // 判断Rom使用推送
        if (DeviceInfoUtil.isEmuiRom()) {
            Log.e(TAG, "初始化推送: 华为推送，");
            // 华为手机 华为推送
            HuaweiClient client = new HuaweiClient(this);
            client.clientConnect();
        } else if (DeviceInfoUtil.isMeizuRom()) {
            Log.e(TAG, "初始化推送: 魅族推送，");
            MeizuPushMsgReceiver.init(this);
        } else if (PushManager.isSupportPush(this)) {
            Log.e(TAG, "初始化推送: OPPO推送，");
            OppoPushMessageService.init(this);
        } else if (DeviceInfoUtil.isVivoRom()) {
            Log.e(TAG, "初始化推送: VIVO推送，");
            VivoPushMessageReceiver.init(this);
        } else if (true || DeviceInfoUtil.isMiuiRom()) {
            Log.e(TAG, "初始化推送: 小米推送，");
            if (shouldInit()) {
                // 小米推送初始化
//                MiPushClient.registerPush(this, APP_ID, APP_KEY);
            }
        }
    }

    public void checkTime() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());

        long requestTime = System.currentTimeMillis();
        HttpUtils.get().url(coreManager.getConfig().GET_CURRENT_TIME)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        // 误差比config接口大，可能是主页线程做其他操作导致的，
                        // 和ios统一，进入主页时校准时间，
                        long responseTime = System.currentTimeMillis();
                        TimeUtils.responseTime(requestTime, result.getCurrentTime(), result.getCurrentTime(), responseTime);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        // 不需要提示，
                        Log.e("TimeUtils", "校准时间失败", e);
                    }
                });
    }

    public void cancelUserCheckIfExist() {
        Log.d(TAG, "cancelUserCheckIfExist() called");
    }

    /* 当注销当前用户时，将那些需要当前用户的Fragment销毁，以后重新登陆后，重新加载为初始状态 */
    public void removeNeedUserFragment() {
        mRadioGroup.clearCheck();
        mLastFragmentId = -1;
        isCreate = true;
    }

    /**
     * 登录方法
     */
    public void login() {
        Log.d(TAG, "login() called");
        User user = coreManager.getSelf();

        Intent startIntent = CoreService.getIntent(MainActivity.this, user.getUserId(), user.getPassword(), user.getNickName());
        ContextCompat.startForegroundService(MainActivity.this, startIntent);

        mUserId = user.getUserId();
        numMessage = FriendDao.getInstance().getMsgUnReadNumTotal(mUserId);
        numCircle = MyZanDao.getInstance().getZanSize(coreManager.getSelf().getUserId());
        updateNumData();
        if (isCreate) {
            mRbTab0.toggle();
        }
    }

    public void loginOut() {
        Log.d(TAG, "loginOut() called");
        coreManager.logout();
        removeNeedUserFragment();
        cancelUserCheckIfExist();
        if (MyApplication.getInstance().mUserStatus == LoginHelper.STATUS_USER_TOKEN_OVERDUE) {
            UserCheckedActivity.start(MyApplication.getContext());
        }
        finish();
    }

    @SuppressLint("MissingPermission")
    public void conflict() {
        Log.d(TAG, "conflict() called");
        isConflict = true;// 标记一下

        coreManager.logout();
        removeNeedUserFragment();
        cancelUserCheckIfExist();
        MyApplication.getInstance().mUserStatus = LoginHelper.STATUS_USER_TOKEN_CHANGE;
        UserCheckedActivity.start(this);
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        }
        mActivityManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
        finish();
    }

    public void need_update() {
        Log.d(TAG, "need_update() called");
        removeNeedUserFragment();
        cancelUserCheckIfExist();
        // 弹出对话框
        UserCheckedActivity.start(this);
    }

    public void login_give_up() {
        Log.d(TAG, "login_give_up() called");
        removeNeedUserFragment();
        cancelUserCheckIfExist();
        MyApplication.getInstance().mUserStatus = LoginHelper.STATUS_USER_NO_UPDATE;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupCreateEvent(GroupCreateEvent event) {

        GroupTool tool = new GroupTool();

        tool.setType(1);

        tool.ceateRoom(MainActivity.this, coreManager);
        tool.setSuc(new GroupTool.CreateSuc() {
            @Override
            public void suc(Friend friend, boolean finish) {

                List<String> data = new ArrayList<>();
                data.add(friend.getUserId());
                // EventBus.getDefault().post(new MessageEventMeetingInvite(roomId, "", mLoginUserId, mLoginUserId, mLoginNickName, voicejid, data, type));

                if (finish) {
                    // intent();
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageSendChat message) {
        if (!message.isGroup) {
            coreManager.sendChatMessage(message.toUserId, message.chat);
        } else {
            coreManager.sendMucChatMessage(message.toUserId, message.chat);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageUpdate message) {
        // getLastAppInfo();
    }

    // 更新发现模块新消息数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventHongdian message) {
        if (message.number == -1) {
            // 好友更新了动态
            int size = MyZanDao.getInstance().getZanSize(coreManager.getSelf().getUserId());
            if (size == 0) {
                // 本地社交圈无未读数量
                UiUtils.updateNum(mTvCircleNum, -1);
            }
            return;
        }
        numCircle = message.number;
        UiUtils.updateNum(mTvCircleNum, numCircle);
    }

    // 已上传的联系人注册了IM,更新到联系人表内
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageContactEvent mMessageEvent) {
        List<Contact> mNewContactList = ContactDao.getInstance().getContactsByToUserId(coreManager.getSelf().getUserId(),
                mMessageEvent.message);
        if (mNewContactList != null && mNewContactList.size() > 0) {
            updateContactUI(mNewContactList);
        }
    }


    // 打开消息TAB
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventChangeUI event) {
        if (event.getFragmentId() == 1) {
            mCurrtTabId = R.id.rb_tab_1;
            changeFragment(R.id.rb_tab_1);
            mRadioGroup.check(R.id.rb_tab_1);
        } else if (event.getFragmentId() == 0) {
            mCurrtTabId = R.id.rb_tab_0;
            changeFragment(R.id.rb_tab_0);
            mRadioGroup.check(R.id.rb_tab_0);
        } else if (event.getFragmentId() == 2) {
            mCurrtTabId = R.id.rb_tab_2;
            changeFragment(R.id.rb_tab_2);
            mRadioGroup.check(R.id.rb_tab_2);
        } else if (event.getFragmentId() == 3) {
            mCurrtTabId = R.id.rb_tab_3;
            changeFragment(R.id.rb_tab_3);
            mRadioGroup.check(R.id.rb_tab_3);
            dfrag_tab.setTabIndex(event.getTabIndex());
        }
        MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventChangeLanguage event) {
        finish();

        isChangeLanguageFinsh = true;
    }

    /**
     * 我方取消、挂断通话后发送XMPP消息给对方
     * copy by AudioOrVideoController
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventCancelOrHangUp event) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        ChatMessage message = new ChatMessage();
        message.setType(event.type);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(coreManager.getSelf().getNickName());
        message.setToUserId(event.toUserId);
        message.setContent(event.content);
        message.setTimeLen(event.callTimeLen);
        message.setMySend(true);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());


        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, event.toUserId, message)) {
            ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, message.getFromUserId(), message, false);
        }

        coreManager.sendChatMessage(event.toUserId, message);
        MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
    }


    /**
     * 提醒消息
     * copy by AudioOrVideoController
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(RemindMessageEvent event) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        ChatMessage message = event.getChatMessage();
        message.setFromUserId(ID_SYSTEM_MESSAGE);
        message.setFromUserName(getString(R.string.sys_notification));
        message.setContent(getString(R.string.msg_remind_pre) + message.getContent());
        message.setToUserId(mLoginUserId);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));

        //只能实现短定时，如果app被杀死则定时消失
        new Thread("counterDown") {
            @Override
            public void run() {
                try {
//                        Log.d("zlm", String.valueOf(event.getEndtime()));
//                        Log.d("zlm", String.valueOf(System.currentTimeMillis()));
                    long time = event.getEndtime() - System.currentTimeMillis();
//                        Log.d("zlm", String.valueOf(time));
                    sleep(time);

                    message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                    if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, ID_SYSTEM_MESSAGE, message)) {
                        ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, ID_SYSTEM_MESSAGE, message, false);
                    }
                    coreManager.sendChatMessage(mLoginUserId, message);
                    MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    /**
     * 应用中发送消息给对方
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(ApplicationMessageSendChat event) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_TEXT);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(coreManager.getSelf().getNickName());
        message.setToUserId(event.toUserId);
        message.setContent(event.content);
        message.setMySend(true);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, event.toUserId, message)) {
            ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, message.getFromUserId(), message, false);
        }

        coreManager.sendChatMessage(event.toUserId, message);
        MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
    }

    /**
     * 自动回复消息给对方
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(AutoReplyMessageSendChat event) {
        String selected = spUser.getString("selected", "");
        if (!TextUtils.isEmpty(selected)) {
            String mLoginUserId = coreManager.getSelf().getUserId();
            ChatMessage message = new ChatMessage();
            message.setType(XmppMessage.TYPE_TEXT);
            message.setFromUserId(mLoginUserId);
            message.setFromUserName(coreManager.getSelf().getNickName());
            message.setToUserId(event.getToUserid());
            message.setContent(selected);
            message.setMySend(true);
            message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
            message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

            if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, event.getToUserid(), message)) {
                ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, message.getFromUserId(), message, false);
            }

            coreManager.sendChatMessage(event.getToUserid(), message);
            MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
        } else {
            String mLoginUserId = coreManager.getSelf().getUserId();
            ChatMessage message = new ChatMessage();
            message.setType(XmppMessage.TYPE_TEXT);
            message.setFromUserId(mLoginUserId);
            message.setFromUserName(coreManager.getSelf().getNickName());
            message.setToUserId(event.getToUserid());
            message.setContent(autoReplayValueToString(event.getContent()));
            message.setMySend(true);
            message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
            message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

            if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, event.getToUserid(), message)) {
                ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, message.getFromUserId(), message, false);
            }

            coreManager.sendChatMessage(event.getToUserid(), message);
            MsgBroadcast.broadcastMsgUiUpdate(mContext);   // 更新消息界面
        }
    }

    private String autoReplayValueToString(int value) {
        String ret;
        switch (value) {
            case -1:
                ret = this.getResources().getString(R.string.auto_replay_close);
                break;
            case 1:
//                ret = this.getResources().getString(R.string.auto_replay_1);
//                break;
//            case 2:
//                ret = this.getResources().getString(R.string.auto_replay_2);
//                break;
//            case 3:
//                ret = this.getResources().getString(R.string.auto_replay_3);
//                break;
//            case 4:
//                ret = this.getResources().getString(R.string.auto_replay_4);
//                break;
//            case 5:
//                ret = R.string.auto_replay_5;
//                break;
            default:
//                Reporter.unreachable();
                ret = this.getResources().getString(R.string.auto_replay_5);
        }
        return ret;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventMeetingInvite message) {

        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginNickName = coreManager.getSelf().getNickName();
        for (int i = 0; i < message.meetinglist.size(); i++) {
            ChatMessage messagevoice = new ChatMessage();
            int type;
            String str;
            if (message.type == CallConstants.Audio_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_VOICE;
                str = getString(R.string.tip_invite_voice_meeting);
            } else if (message.type == CallConstants.Video_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_VIDEO;
                str = getString(R.string.tip_invite_video_meeting);
            } else if (message.type == CallConstants.Screen_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_SCREEN;
                str = getString(R.string.tip_invite_screen_meeting);
            } else {
                type = XmppMessage.TYPE_IS_MU_CONNECT_TALK;
                str = getString(R.string.tip_invite_talk_meeting);
            }
            messagevoice.setType(type);
            messagevoice.setFromUserId(mLoginUserId);//mLoginUserId
            messagevoice.setFromUserName(mLoginNickName);
            // 这里还是发送单聊消息  115 || 120
            messagevoice.setToUserId(message.meetinglist.get(i));
            messagevoice.setContent(str);
            messagevoice.setRoomId(message.roomid);
            messagevoice.setFilePath(message.roomid);
            messagevoice.setObjectId(message.objectId);
            messagevoice.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            coreManager.sendChatMessage(message.meetinglist.get(i), messagevoice);
            // 音视频会议消息不保存
/*
            ChatMessageDao.getInstance().saveNewSingleChatMessage(coreManager.getSelf().getUserId(), message.meetinglist.get(i), messagevoice);
            FriendDao.getInstance().updateFriendContent(coreManager.getSelf().getUserId(), message.meetinglist.get(i), str, type, TimeUtils.sk_time_current_time());
*/
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventInitiateMeeting message) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginNickName = coreManager.getSelf().getNickName();

        if(!message.inRooom) {
            Jitsi_connecting_second.start(this, mLoginUserId, mLoginUserId, message.type,"",false,message.roomId);
        }

        for (int i = 0; i < message.list.size(); i++) {
            ChatMessage mMeetingMessage = new ChatMessage();
            int type;
            String str;
            if (message.type == CallConstants.Audio_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_VOICE;
                str = getString(R.string.tip_invite_voice_meeting);
            } else if (message.type == CallConstants.Video_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_VIDEO;
                str = getString(R.string.tip_invite_video_meeting);
            } else if (message.type == CallConstants.Screen_Meet) {
                type = XmppMessage.TYPE_IS_MU_CONNECT_SCREEN;
                str = getString(R.string.tip_invite_screen_meeting);
            } else {
                type = XmppMessage.TYPE_IS_MU_CONNECT_TALK;
                str = getString(R.string.tip_invite_talk_meeting);
            }
            mMeetingMessage.setType(type);
            mMeetingMessage.setFromUserId(mLoginUserId);
            mMeetingMessage.setFromUserName(mLoginNickName);
            mMeetingMessage.setToUserId(message.list.get(i));
            mMeetingMessage.setContent(str);
            mMeetingMessage.setObjectId(mLoginUserId);
            mMeetingMessage.setRoomId(message.roomId);
            mMeetingMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
            mMeetingMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            coreManager.sendChatMessage(message.list.get(i), mMeetingMessage);
            // 音视频会议消息不保存
/*
            ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, message.list.get(i), mMeetingMessage);
            FriendDao.getInstance().updateFriendContent(mLoginUserId, message.list.get(i), str, type, TimeUtils.sk_time_current_time());
*/
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventInviteCall mes) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginNickName = coreManager.getSelf().getNickName();
        int type = mes.type;
        ChatMessage message = new ChatMessage();
        if (type == CallConstants.Audio) {// 语音通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VOICE);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.voice_call));
        } else if (type == CallConstants.Video) {// 视频通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VIDEO);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.video_call));
        } else if (type == CallConstants.Screen) {// 屏幕共享
            message.setType(XmppMessage.TYPE_IS_CONNECT_SCREEN);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.screen_call));
        }
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setToUserId(mes.touserid);
        message.setRoomId(mes.roomId);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        coreManager.sendChatMessage(mes.touserid, message);
        Intent intent = new Intent(this, Jitsi_pre.class);
        intent.putExtra("type", type);
        intent.putExtra("fromuserid", mLoginNickName);
        intent.putExtra("touserid", mes.touserid);
        intent.putExtra("username", mes.tousername);
        intent.putExtra("roomId", mes.roomId);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventPreviewPics mes) {
        Intent intent = new Intent(mContext, MultiImagePreviewActivity.class);
        intent.putExtra(AppConstant.EXTRA_IMAGES, mes.pics);
        intent.putExtra(AppConstant.EXTRA_POSITION, mes.index);
        intent.putExtra(AppConstant.EXTRA_CHANGE_SELECTED, false);
        startActivity(intent);
    }

    /**
     * 生成底部二维码返回，跳转扫一扫界面
     *
     * @param eventQRCodeReady
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventQRCodeReady eventQRCodeReady) {
        // todo 目前调用requestQrCodeScan方法内的ctx对象均为getActivity获取(即MainActivity)，当ctx对象为恰activity时，这里就不能直接用this了，后面有需求在调整吧
        int size = ScreenUtil.getScreenWidth(MyApplication.getContext()) / 16 * 9;
        Intent intent = new Intent(this, ScannerActivity.class);
        // 设置扫码框的宽
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, size);
        // 设置扫码框的高
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, size);
        // 设置扫码框距顶部的位置
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, DisplayUtil.dip2px(this, 250));
        // 可以从相册获取
        intent.putExtra(Constant.EXTRA_IS_ENABLE_SCAN_FROM_PIC, true);
        if (eventQRCodeReady.getBitmap() != null && false) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            eventQRCodeReady.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            intent.putExtra(Constant.EXTRA_SELF_QR_CODE_BITMAP, bytes);
        }
        startActivityForResult(intent, 888);
    }

    /**
     * 扫描二维码 || 全部群组内 加入群组时群主开启了群验证 发送入群请求给群主
     *
     * @param eventSendVerifyMsg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventSendVerifyMsg eventSendVerifyMsg) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginUserName = coreManager.getSelf().getNickName();
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_GROUP_VERIFY);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginUserName);
        message.setToUserId(eventSendVerifyMsg.getCreateUserId());
        String s = JsonUtils.initJsonContent(mLoginUserId, mLoginUserName, eventSendVerifyMsg.getGroupJid(), "1", eventSendVerifyMsg.getReason());
        message.setObjectId(s);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        if (coreManager.isLogin()) {
            coreManager.sendChatMessage(eventSendVerifyMsg.getCreateUserId(), message);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventBG mMessageEventBG) {
        if (mMessageEventBG.flag) {// 切换到前台
            // 设备锁，
            showDeviceLock();
            // 清除通知栏消息
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.cancelAll();
            }

            if (isConflict) {// 在其他设备登录了，不登录
                isConflict = false;// Reset Status
                Log.e("zq", "在其他设备登录了，不登录");
                return;
            }

            if (!coreManager.isServiceReady()) {
                // 小米手机在后台运行时，CoreService经常被系统杀死，需要兼容ta
                Log.e("zq", "CoreService为空，重新绑定");
                coreManager.relogin();
            } else {
                if (!coreManager.isLogin()) {// XMPP未验证
                    isAuthenticated = false;

                    Log.e("zq", "XMPP未验证，重新登录");
                    coreManager.login();

                    // 在集群模式下，(ex:端口改为5333)，当xmpp掉线后有一定概率连接不上
                    CountDownTimer mCountDownTimer = new CountDownTimer(6000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.e("zq", "XMPP未验证" + millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            Log.e("zq", "6s后xmpp还未连接上，重新创建一个mConnect对象登录xmpp");
                            if (!isAuthenticated) {
                                coreManager.autoReconnect(MainActivity.this);
                            }
                        }
                    };
                    mCountDownTimer.start();
                } else {// xmpp重新加入一遍群组 已加入不会重复加入
                    Log.e("zq", "XMPP已认证，检查群组是否加入");
                    coreManager.joinExistGroup();
                }
            }
        } else {
            if (mMessageEventBG.isCloseError) {
                // XMPP连接关闭 || 异常断开
                MachineDao.getInstance().resetMachineStatus();
            }
            AsyncUtils.doAsync(this, c -> coreManager.appBackstage(getApplicationContext(), coreManager.isLogin(), mMessageEventBG.isCloseError));
        }
    }

    /*
    扫描二维码 || 全部群组内 加入群组 将群组存入朋友表
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventCreateGroupFriend eventCreateGroupFriend) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginUserName = coreManager.getSelf().getNickName();
        MucRoom room = eventCreateGroupFriend.getMucRoom();

        MyApplication.getInstance().saveGroupPartStatus(room.getJid(), room.getShowRead(), room.getAllowSendCard(),
                room.getAllowConference(), room.getAllowSpeakCourse(), room.getTalkTime());

        Friend friend = new Friend();
        friend.setOwnerId(mLoginUserId);
        friend.setUserId(room.getJid());
        friend.setNickName(room.getName());
        friend.setDescription(room.getDesc());
        friend.setRoomId(room.getId());
        friend.setRoomCreateUserId(room.getUserId());
        friend.setChatRecordTimeOut(room.getChatRecordTimeOut());// 消息保存天数 -1/0 永久
        friend.setContent(mLoginUserName + " " + getString(R.string.Message_Object_Group_Chat));
        friend.setTimeSend(TimeUtils.sk_time_current_time());
        friend.setRoomFlag(1);
        friend.setStatus(Friend.STATUS_FRIEND);
        FriendDao.getInstance().createOrUpdateFriend(friend);

        // 调用smack加入群组的方法
        coreManager.joinMucChat(friend.getUserId(), 0);
    }


    List mSelects = new ArrayList();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(ChatPluginCreateRoomMessage createRoomMessage) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        String mLoginUserName = coreManager.getSelf().getNickName();
        mSelects.clear();
        for (String ids : createRoomMessage.userIds) {


            HttpRequestCenter.getUserInfo(ids).subscribe(new RxBusSubscriber<String>() {
                @Override
                protected void onEvent(String s) {

                    com.oortcloud.appstore.bean.Result<Data<com.oortcloud.basemodule.user.UserInfo>> result = new Gson().fromJson(s, new TypeToken<com.oortcloud.appstore.bean.Result<Data<com.oortcloud.basemodule.user.UserInfo>>>() {
                    }.getType());
                    if (result.getCode() == 200) {
                        com.oortcloud.basemodule.user.UserInfo userInfo = result.getData().getUserInfo();
                        if (userInfo != null) {

                            mSelects.add(userInfo.getImuserid());


                            if (mSelects.size() == createRoomMessage.userIds.length) {
                                GroupTool groupTool = new GroupTool();
                                groupTool.createGroupChatWithId(MainActivity.this, coreManager, createRoomMessage.roomId, createRoomMessage.roomName, mSelects);
                            }

                        }
                    }
                }

            });
        }


    }

    private boolean shouldInit() {
        ActivityManager activityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 关闭软键盘
     */
    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder token = getWindow().getDecorView().getWindowToken();
        if (imm != null && imm.isActive() && token != null) {
            imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 手机联系人相关操作
     */
    private void addressBookOperation() {
        boolean isReadContacts = PermissionUtil.checkSelfPermissions(this, new String[]{Manifest.permission.READ_CONTACTS});
        if (isReadContacts) {
            try {
                uploadAddressBook();
            } catch (Exception e) {
                String message = getString(R.string.tip_read_contacts_failed);
                //ToastUtil.showToast(this, message);
                Reporter.post(message, e);
                ContactsUtil.cleanLocalCache(this, coreManager.getSelf().getUserId());
            }
        } else {
            String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
            if (!PermissionUtil.deniedRequestPermissionsAgain(this, permissions)) {
                PermissionExplainDialog tip = new PermissionExplainDialog(this);
                tip.setPermissions(permissions);
                tip.setOnConfirmListener(() -> {
                    PermissionUtil.requestPermissions(this, 0x01, permissions);
                });
                tip.show();
            } else {
                PermissionUtil.requestPermissions(this, 0x01, permissions);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "通知权限已开启");
            } else {
                Log.d(TAG, "通知权限被拒绝，再次提示");
                showNotificationPermissionDialog(); // 拒绝后再次提示
            }
            return;
        }
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted) {
        if (isAllGranted) {// 已授权
            try {
                uploadAddressBook();
            } catch (Exception e) {
                String message = getString(R.string.tip_read_contacts_failed);
                //ToastUtil.showToast(this, message);
                Reporter.post(message, e);
                ContactsUtil.cleanLocalCache(this, coreManager.getSelf().getUserId());
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            Log.d(TAG, "从悬浮窗设置页返回，当前权限：" + PermissionHelper.hasOverlayPermission(this));
            checkNecessaryPermissions(); // 重新检查权限链

            return;
        }
        //处理返回数据
        CordovaPlugin cordovaPlugin = Proxy.getProxyCordovaPlugin();
        if (cordovaPlugin != null) {
            cordovaPlugin.onActivityResult(requestCode, resultCode, data);
            Proxy.setProxyCordovaPlugin(null);
        }

        switch (requestCode) {
            case 888:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null || data.getExtras() == null) {
                        return;
                    }
                    String result = data.getExtras().getString(Constant.EXTRA_RESULT_CONTENT);
                    Log.e("zq", "二维码扫描结果：" + result);
                    if (TextUtils.isEmpty(result)) {
                        return;
                    }
                    if (PaymentReceiptMoneyActivity.checkQrCode(result)) {
                        // 长度为19且 && 纯数字 扫描他人的付款码 弹起收款界面
                        Intent intent = new Intent(mContext, PaymentReceiptMoneyActivity.class);
                        intent.putExtra("PAYMENT_ORDER", result);
                        startActivity(intent);
                    } else if (result.contains("userId")
                            && result.contains("userName")) {
                        // 扫描他人的收款码 弹起付款界面
                        Intent intent = new Intent(mContext, ReceiptPayMoneyActivity.class);
                        intent.putExtra("RECEIPT_ORDER", result);
                        startActivity(intent);
                    } else if (ReceiveChatHistoryActivity.checkQrCode(result)) {
                        // 扫描他人的发送聊天记录的二维码，弹起接收聊天记录页面，
                        ReceiveChatHistoryActivity.start(this, result);
                    } else if (WebLoginActivity.checkQrCode(result) || WebLoginActivity.checkQrCode4oort(result)) {
                        // 扫描其他平台登录的二维码，确认登录页面，
                        WebLoginActivity.start(this, result);
                    } else if (result.contains("apppackage")) {
                        // 扫描其他平台登录的二维码，确认登录页面，


                        try {
                            JSONObject json = JSON.parseObject(result, JSONObject.class);
                            startService(json.getString("apppackage"), json.getString("path"));
                        } catch (Exception e) {
                            ToastUtil.showToast(this, "应用不存在");
                        }
                    } else {
                        if (result.contains("shikuId")) {
                            // 二维码
                            Map<String, String> map = WebViewActivity.URLRequest(result);
                            String action = map.get("action");
                            String userId = map.get("shikuId");
                            if (TextUtils.equals(action, "group")) {
                                getRoomInfo(userId);
                            } else if (TextUtils.equals(action, "user")) {
                                getUserInfo(userId);
                            } else {
                                Reporter.post("二维码无法识别，<" + result + ">");
                                ToastUtil.showToast(this, R.string.unrecognized);
                            }
                        } else if (!result.contains("shikuId")
                                && HttpUtil.isURL(result)) {
                            // 非二维码  访问其网页
                            Intent intent = new Intent(this, WebViewActivity.class);
                            intent.putExtra(WebViewActivity.EXTRA_URL, result);
                            startActivity(intent);
                        } else {
                            Reporter.post("二维码无法识别，<" + result + ">");
                            ToastUtil.showToast(this, R.string.unrecognized);
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startService(String packageName, String params) {

        if (getContext() == null) {
            return;
        }
        //String packageName = "com.jwb_home.oort";
        Intent intent = new Intent(getContext(), AppManagerService.class);
        intent.putExtra("packageName", packageName);
        intent.putExtra("params", params);
        getContext().startService(intent);


    }

    /**
     * 通过通讯号获得userId
     */
    private void getUserInfo(String account) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getInstance()).accessToken);
        params.put("account", account);

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).USER_GET_URL_ACCOUNT)
                .params(params)
                .build()
                .execute(new BaseCallback<User>(User.class) {
                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            User user = result.getData();
                            BasicInfoActivity.start(mContext, user.getUserId(), BasicInfoActivity.FROM_ADD_TYPE_QRCODE);
                        } else {
                            ToastUtil.showErrorData(MyApplication.getInstance());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(MyApplication.getInstance());
                    }
                });
    }

    /**
     * 获取房间信息
     */
    private void getRoomInfo(String roomId) {
        Friend friend = FriendDao.getInstance().getMucFriendByRoomId(coreManager.getSelf().getUserId(), roomId);
        if (friend != null) {
            if (friend.getGroupStatus() == 0) {
                interMucChat(friend.getUserId(), friend.getNickName());
                return;
            } else {// 已被踢出该群组 || 群组已被解散 || 群组已被后台锁定
                FriendDao.getInstance().deleteFriend(coreManager.getSelf().getUserId(), friend.getUserId());
                ChatMessageDao.getInstance().deleteMessageTable(coreManager.getSelf().getUserId(), friend.getUserId());
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", roomId);

        HttpUtils.get().url(coreManager.getConfig().ROOM_GET)
                .params(params)
                .build()
                .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                    @Override
                    public void onResponse(ObjectResult<MucRoom> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            final MucRoom mucRoom = result.getData();
                            if (mucRoom.getIsNeedVerify() == 1) {
                                VerifyDialog verifyDialog = new VerifyDialog(MainActivity.this);
                                verifyDialog.setVerifyClickListener(MyApplication.getInstance().getString(R.string.tip_reason_invite_friends), new VerifyDialog.VerifyClickListener() {
                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void send(String str) {
                                        EventBus.getDefault().post(new EventSendVerifyMsg(mucRoom.getUserId(), mucRoom.getJid(), str));
                                    }
                                });
                                verifyDialog.show();
                                return;
                            }
                            joinRoom(mucRoom, coreManager.getSelf().getUserId());
                        } else {
                            ToastUtil.showErrorData(MainActivity.this);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(MainActivity.this);
                    }
                });
    }

    /**
     * 加入房间
     */
    private void joinRoom(final MucRoom room, final String loginUserId) {
        DialogHelper.showDefaulteMessageProgressDialog(MainActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", room.getId());
        if (room.getUserId().equals(loginUserId))
            params.put("type", "1");
        else
            params.put("type", "2");

        MyApplication.mRoomKeyLastCreate = room.getJid();

        HttpUtils.get().url(coreManager.getConfig().ROOM_JOIN)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (Result.checkSuccess(MainActivity.this, result)) {
                            EventBus.getDefault().post(new EventCreateGroupFriend(room));
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {// 给500ms的时间缓存，防止群组还未创建好就进入群聊天界面
                                    interMucChat(room.getJid(), room.getName());
                                }
                            }, 500);
                        } else {
                            MyApplication.mRoomKeyLastCreate = "compatible";
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(MainActivity.this);
                        MyApplication.mRoomKeyLastCreate = "compatible";
                    }
                });
    }

    /**
     * 进入房间
     */
    private void interMucChat(String roomJid, String roomName) {
        Intent intent = new Intent(MainActivity.this, MucChatActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, roomJid);
        intent.putExtra(AppConstant.EXTRA_NICK_NAME, roomName);
        intent.putExtra(AppConstant.EXTRA_IS_GROUP_CHAT, true);
        startActivity(intent);

        MucgroupUpdateUtil.broadcastUpdateUi(MainActivity.this);
    }

    private void uploadAddressBook() {
        List<Contacts> mNewAdditionContacts = ContactsUtil.getNewAdditionContacts(this, coreManager.getSelf().getUserId());
        /**
         * 本地生成
         * [{"name":"15768779999","telephone":"8615768779999"},{"name":"好搜卡","telephone":"8615720966659"},
         * {"name":"zas","telephone":"8613000000000"},{"name":"客服助手","telephone":"864007883333"},]
         * 服务端要求
         * [{\"toTelephone\":\"15217009762\",\"toRemarkName\":\"我是电话号码备注\"},{\"toTelephone\":\"15217009762\",\"toRemarkName\":\"我是电话号码备注\"}]
         */
        if (mNewAdditionContacts.size() <= 0) {
            return;
        }

        String step1 = JSON.toJSONString(mNewAdditionContacts);
        String step2 = step1.replaceAll("name", "toRemarkName");
        String contactsListStr = step2.replaceAll("telephone", "toTelephone");
        Log.e("contact", "新添加的联系人：" + contactsListStr);

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("uploadJsonStr", contactsListStr);

        HttpUtils.post().url(coreManager.getConfig().ADDRESSBOOK_UPLOAD)
                .params(params)
                .build()
                .execute(new ListCallback<Contact>(Contact.class) {

                    @Override
                    public void onResponse(ArrayResult<Contact> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            List<Contact> mContactList = result.getData();
                            for (int i = 0; i < mContactList.size(); i++) {
                                Contact contact = mContactList.get(i);
                                if (ContactDao.getInstance().createContact(contact)) {
                                    if (contact.getStatus() == 1) {// 服务端自动成为好友，本地也需要添加
                                        NewFriendDao.getInstance().addFriendOperating(contact.getToUserId(), contact.getToUserName(), contact.getToRemarkName());
                                    }
                                }
                            }

                            if (mContactList.size() > 0) {// 显示数量新增数量  记录新增contacts id
                                updateContactUI(mContactList);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void updateRoom() {
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("type", "0");
        params.put("pageIndex", "0");
        params.put("pageSize", "1000");// 给一个尽量大的值

        HttpUtils.get().url(coreManager.getConfig().ROOM_LIST_HIS)
                .params(params)
                .build()
                .execute(new ListCallback<MucRoom>(MucRoom.class) {
                    @Override
                    public void onResponse(ArrayResult<MucRoom> result) {
                        if (result.getResultCode() == 1) {
                            FriendDao.getInstance().addRooms(mHandler, coreManager.getSelf().getUserId(), result.getData(), new OnCompleteListener2() {
                                @Override
                                public void onLoading(int progressRate, int sum) {

                                }

                                @Override
                                public void onCompleted() {
                                    if (coreManager.isLogin()) {
                                        // 1.调用smack内join方法加入群组
                                        List<Friend> mFriends = FriendDao.getInstance().getAllRooms(coreManager.getSelf().getUserId());
                                        for (int i = 0; i < mFriends.size(); i++) {// 已加入的群组不会重复加入，方法内已去重
                                            coreManager.joinMucChat(mFriends.get(i).getUserId(), 0);
                                        }
                                    }
                                    MsgBroadcast.broadcastMsgUiUpdate(MainActivity.this);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    /*
    消息 发现
     */
    public void msg_num_update(int operation, int count) {
        numMessage = (operation == MsgBroadcast.NUM_ADD) ? numMessage + count : numMessage - count;
        updateNumData();
    }

    public void msg_num_reset() {
        numMessage = FriendDao.getInstance().getMsgUnReadNumTotal(mUserId);
        numCircle = MyZanDao.getInstance().getZanSize(coreManager.getSelf().getUserId());
        updateNumData();
    }

    public void updateNumData() {
        numMessage = FriendDao.getInstance().getMsgUnReadNumTotal(mUserId);
        numCircle = MyZanDao.getInstance().getZanSize(coreManager.getSelf().getUserId());

        if (!ShortcutBadger.applyCount(this, numMessage)) {
            showNotificationBadge(this, numMessage);
        }

        UiUtils.updateNum(mTvMessageNum, numMessage);
        UiUtils.updateNum(mTvCircleNum, numCircle);
    }


    private void showNotificationBadge(Context context, int count) {
        String channelId = "badge_channel";

        // Android 8.0+ 需要创建 Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Badge Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true); // 启用角标
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // 锁屏可见

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notice) // 必须设置小图标
                .setContentTitle("新消息")
                .setContentText("您有" + count + "条未读消息")
                .setNumber(count) // 设置角标数字（部分设备需要）
                .setAutoCancel(true) // 点击后自动取消（可选）
                .setCategory(Notification.CATEGORY_MESSAGE) // 设置通知类别
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 关键：设置 importance（适用于 Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);
        }

        // 显示通知
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(1, builder.build());

        // 特殊处理：针对部分需要单独设置角标的厂商
        setBadgeForSpecificDevices(context, count);
    }

    private void setBadgeForSpecificDevices(Context context, int count) {
        try {
            // 华为
            if (isHuaweiDevice()) {
                Bundle extra = new Bundle();
                extra.putString("package", context.getPackageName());
                extra.putString("class", getLauncherClassName(context));
                extra.putInt("badgenumber", count);
                context.getContentResolver().call(
                        Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, extra);
            }
            if (ishonorDevice()) {
                Bundle extra = new Bundle();
                extra.putString("package", context.getPackageName());
                extra.putString("class", getLauncherClassName(context));
                extra.putInt("badgenumber", count);
                context.getContentResolver().call(
                        Uri.parse("content://com.hihonor.android.launcher.settings/badge/"),
                        "change_badge", null, extra);
            }
            // 小米（MIUI）
            else if (isXiaomiDevice()) {
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(context, "badge_channel")
                        .setSmallIcon(R.drawable.icon_notification)
                        .setContentTitle("新消息")
                        .setContentText("您有" + count + "条未读消息")
                        .build();

                try {
                    Field field = notification.getClass().getDeclaredField("extraNotification");
                    Object extraNotification = field.get(notification);
                    Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                    method.invoke(extraNotification, count);
                    mNotificationManager.notify(1, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 三星
            else if (isSamsungDevice()) {
                Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                intent.putExtra("badge_count", count);
                intent.putExtra("badge_count_package_name", context.getPackageName());
                intent.putExtra("badge_count_class_name", getLauncherClassName(context));
                context.sendBroadcast(intent);
            }
            // 其他厂商可按需添加
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 辅助方法：判断设备厂商
    private boolean isHuaweiDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("Huawei");
    }

    private boolean ishonorDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("Honor");
    }

    private boolean isXiaomiDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi");
    }

    private boolean isSamsungDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("Samsung");
    }

    // 获取 Launcher Activity 类名
    private String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            return resolveInfos.get(0).activityInfo.name;
        }
        return "";
    }

    // 在 Application 或 MainActivity 的 onCreate 中调用
    private void checkBadgePermission(Context context) {
        // 仅首次启动时显示（可通过 SharedPreferences 记录）
        SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        boolean hasShownBadgeGuide = prefs.getBoolean("has_shown_badge_guide", false);

        if (!hasShownBadgeGuide) {
            showBadgeGuideDialog(context);
            prefs.edit().putBoolean("has_shown_badge_guide", true).apply();
        }
    }

    private void showBadgeGuideDialog(Context context) {
        // 使用 Theme.MaterialComponents.Light.Dialog.Alert 主题（需添加 Material Design 依赖）
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

// 或者使用系统自带的浅色主题
        // AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);

// 继续设置对话框内容
        builder.setTitle("角标显示指南")
                .setMessage("为了获得更好的通知体验，建议您开启应用角标显示")
                .setPositiveButton("去设置", (dialog, which) -> {
                    openNotificationSettings(context);
                })
                .setNegativeButton("稍后", null)
                .show();
    }

    private void showBadgeSettingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        builder.setTitle("角标显示设置")
                .setMessage("您的设备可能需要手动开启应用角标显示")
                .setPositiveButton("去设置", (dialog, which) -> {
                    openNotificationSettings(context); // 跳转到通知设置页
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 跳转到应用通知设置页
    private void openNotificationSettings(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        context.startActivity(intent);
    }

    /*
    通讯录
     */
    public void updateNewFriendMsgNum(int msgNum) {
        int mNewContactsNumber = PreferenceUtils.getInt(this, Constants.NEW_CONTACTS_NUMBER + coreManager.getSelf().getUserId(),
                0);
        int totalNumber = msgNum + mNewContactsNumber;

        if (totalNumber == 0) {
            mTvNewFriendNum.setText("");
            mTvNewFriendNum.setVisibility(View.INVISIBLE);
        } else {
            mTvNewFriendNum.setText(totalNumber + "");
            mTvNewFriendNum.setVisibility(View.VISIBLE);
        }

        mTvNewFriendNum.setVisibility(View.INVISIBLE);
    }

    private void updateContactUI(List<Contact> mContactList) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        int mContactsNumber = PreferenceUtils.getInt(MainActivity.this, Constants.NEW_CONTACTS_NUMBER + mLoginUserId, 0);
        int mTotalContactsNumber = mContactsNumber + mContactList.size();
        PreferenceUtils.putInt(MainActivity.this, Constants.NEW_CONTACTS_NUMBER + mLoginUserId, mTotalContactsNumber);
        Friend newFriend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), Friend.ID_NEW_FRIEND_MESSAGE);
        updateNewFriendMsgNum(newFriend.getUnReadNum());

        List<String> mNewContactsIds = new ArrayList<>();
        for (int i = 0; i < mContactList.size(); i++) {
            mNewContactsIds.add(mContactList.get(i).getToUserId());
        }
        String mContactsIds = PreferenceUtils.getString(MainActivity.this, Constants.NEW_CONTACTS_IDS + mLoginUserId);
        List<String> ids = JSON.parseArray(mContactsIds, String.class);
        if (ids != null && ids.size() > 0) {
            mNewContactsIds.addAll(ids);
        }
        PreferenceUtils.putString(MainActivity.this, Constants.NEW_CONTACTS_IDS + mLoginUserId, JSON.toJSONString(mNewContactsIds));
    }

    // 服务器上与该人的聊天记录也需要删除
    private void emptyServerMessage(String friendId) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("type", String.valueOf(0));// 0 清空单人 1 清空所有
        params.put("toUserId", friendId);

        HttpUtils.get().url(coreManager.getConfig().EMPTY_SERVER_MESSAGE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void updateSelfData() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());

        HttpUtils.get().url(coreManager.getConfig().USER_GET_URL)
                .params(params)
                .build()
                .execute(new BaseCallback<User>(User.class) {
                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            User user = result.getData();
                            boolean updateSuccess = UserDao.getInstance().updateByUser(user);
                            // 设置登陆用户信息
                            if (updateSuccess) {
                                // 如果成功，保存User变量，
                                coreManager.setSelf(user);
                                // 通知MeFragment更新
                                sendBroadcast(new Intent(OtherBroadcast.SYNC_SELF_DATE_NOTIFY));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public void notifyCollectionList() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", coreManager.getSelf().getUserId());

        HttpUtils.get().url(coreManager.getConfig().Collection_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<Collectiion>(Collectiion.class) {
                    @Override
                    public void onResponse(ArrayResult<Collectiion> result) {
                        if (Result.checkSuccess(mContext, result)) {
                            MyApplication.mCollection = result.getData();
                            Collectiion collection = new Collectiion();
                            collection.setType(7);
                            MyApplication.mCollection.add(0, collection);
                            // 发送广播通知更新
                            sendBroadcast(new Intent(OtherBroadcast.CollectionRefresh_ChatFace));
                        }

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(MyApplication.getContext());
                    }
                });
    }

    private class My_BroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (action.equals(Constants.UPDATE_ROOM)) {
                updateRoom();
            } else if (action.equals(Constants.PING_FAILED)) {
                coreManager.autoReconnect(MainActivity.this);
            } else if (action.equals(Constants.CLOSED_ON_ERROR_END_DOCUMENT)) {
                Constants.IS_CLOSED_ON_ERROR_END_DOCUMENT = true;// 将该标志位置为true，这样当CoreService调用init()方法时，才用调用init()方法内的release(将所有xmpp有关对象清空重构)
                coreManager.autoReconnect(MainActivity.this);
            } else if (action.equals(OtherBroadcast.SYNC_CLEAN_CHAT_HISTORY)) {
                String friendId = intent.getStringExtra(AppConstant.EXTRA_USER_ID);
                emptyServerMessage(friendId);

                FriendDao.getInstance().resetFriendMessage(coreManager.getSelf().getUserId(), friendId);
                ChatMessageDao.getInstance().deleteMessageTable(coreManager.getSelf().getUserId(), friendId);
                sendBroadcast(new Intent(Constants.CHAT_HISTORY_EMPTY));// 清空聊天界面
                MsgBroadcast.broadcastMsgUiUpdate(mContext);
            } else if (action.equals(OtherBroadcast.SYNC_SELF_DATE)) {
                updateSelfData();
            } else if (action.equals(OtherBroadcast.CollectionRefresh)) {
                notifyCollectionList();
            } else if (action.equals(OtherBroadcast.SEND_MULTI_NOTIFY)) {
                mRbTab4.setChecked(false);
                mRbTab1.setChecked(true);
            }
        }
    }


    /**
     * 显示隐私政策或跳转到其他界面
     */
    private void check() {

        //先判断是否显示了隐私政策
        currentVersionCode = AppUtil.getAppVersionCode(MainActivity.this);
        versionCode = (long) SPUtil.get(MainActivity.this, SP_VERSION_CODE, 0L);
        isCheckPrivacy = (boolean) SPUtil.get(MainActivity.this, SP_PRIVACY, false);

        if (!isCheckPrivacy || versionCode != currentVersionCode) {
            showPrivacy();
        } else {
//            Toast.makeText(MainActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(MainActivity.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        dialog.show();

        String string = getResources().getString(R.string.privacy_tips);
        String key1 = getResources().getString(R.string.privacy_tips_key1);
        String key2 = getResources().getString(R.string.privacy_tips_key2);
        int index1 = string.indexOf(key1);
        int index2 = string.indexOf(key2);

        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.link_color));
        spannedString.setSpan(colorSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.link_color));
        spannedString.setSpan(colorSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan sizeSpan2 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());

        tv_privacy_tips.setText(spannedString);

        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(MainActivity.this, SP_PRIVACY, false);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(MainActivity.this, SP_PRIVACY, true);

//                Toast.makeText(MainActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
            }
        });

    }


   /* class  MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }

            mlatitude = bdLocation.getLatitude();
            mlongitude = bdLocation.getLongitude();
            maddress = bdLocation.getAddrStr();
            //百度转wgs84坐标
            LatLng lalng = new LatLng(mlatitude, mlongitude);
            LatLng wgs84 = convertBaiduToGPS(lalng);
            double rp_lat = wgs84.latitude;
            double rp_long = wgs84.longitude;

            CommonApplication.setMaddress(maddress);
            CommonApplication.setMlatitude(rp_lat);
            CommonApplication.setMlongitude(rp_long);

            sharedPreferences.edit().putString("latitude", Double.toString(rp_lat));
            sharedPreferences.edit().putString("longitude", Double.toString(rp_long));
            sharedPreferences.edit().putString("address", maddress).apply();
            //更新位置信息
            ReportInfo.elements = maddress;
            ReportInfo.latitude = (rp_lat);
            ReportInfo.longitude = (rp_long);
//            Log.d("gps", "获取位置成功:"+ ReportInfo.elements + "lat:"+ReportInfo.latitude + "long:" + ReportInfo.longitude);
            //位置更新上报信息
            updateMyLocation();
//            UpdatePoliceInfo();
        }
    }*/

    /**
     * Baidu to GPS  百度转GPS
     *
     * @param sourceLatLng
     * @return
     */
    public LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }

    //method for webview plugin
    public static void getTaskInfo(String userid, String publicId, int pagesize) {
        HashMap<String, Object> taskInfo = ChatMessageDao.getInstance().getTaskMsg(userid, publicId, pagesize);
        if (taskInfo != null) {
            JSONArray jArray = new JSONArray();
            jArray.add(taskInfo);
            String strtaskinfo = jArray.toString();
            FastSharedPreferences info = FastSharedPreferences.get("TASKINFO");
            info.edit().putString("task", strtaskinfo).apply();
        }
    }

    private void testSendMsg(String userid, String type) {
        String appid = this.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".sendmsg");
        intent.putExtra("userid", userid);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void checkUpdate2() {
        boolean isSilentInstall = false;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            HashMap<String, Object> p = new HashMap();

//            p.put("VersionCode", BuildConfig.VERSION_CODE);
//            p.put("app_package", BuildConfig.APPLICATION_ID);


            XUpdate.newBuild(this)
                    .isGet(false)
                    .updateUrl(com.oortcloud.basemodule.constant.Constant.BASE_URL + "oort/oortcloud-appupgrade/v1/getAppVersion")
                    .params(p)
                    .isAutoMode(isSilentInstall)
                    .supportBackgroundUpdate(true)
                    .promptThemeColor(getColor(R.color.main_color))
                    .updateParser(new CustomUpdateParser())
                    .update();// Set up a custom version update parser
        }
    }

    public class CustomUpdateParser implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            Log.d("zlm", json);

            Gson gson = new Gson();

            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(json);
            int code = jsonObject.getIntValue("code");
            String msg = jsonObject.getString("msg");

            if (code == 200) {
                com.alibaba.fastjson.JSONObject result = jsonObject.getJSONObject("data");
                long apkSize = result.getLongValue("ApkSize");
                int versionCode = result.getIntValue("VersionCode");
                String versionName = result.getString("VersionName");
                String updateLog = result.getString("ModifyContent");
                String apkUrl = result.getString("DownloadUrl");
                String md5 = result.getString("ApkMd5");
                boolean hasUpdate;
                boolean isIgnorable;
                boolean isForce;
                int status = result.getIntValue("UpdateStatus");
                if (status == 2) {  //强制更新
                    isForce = true;
                    isIgnorable = false;
                } else if (status == 1) {  //选择更新
                    isForce = false;
                    isIgnorable = true;
                } else {   //不更新
                    isForce = false;
                    isIgnorable = true;
                }
                long currentVersionCode = getAppVersionCode(MainActivity.this);
                if (versionCode > currentVersionCode && status > 0) {  //有新版本
                    hasUpdate = true;
                } else {
                    hasUpdate = false;
                }
                if (result != null) {
                    return new UpdateEntity()
                            .setHasUpdate(hasUpdate)
                            .setIsIgnorable(isIgnorable)
                            .setVersionCode(versionCode)
                            .setVersionName(versionName)
                            .setUpdateContent(updateLog)
                            .setDownloadUrl(apkUrl)
                            .setSize(apkSize / 1024)
                            .setForce(isForce)
                            .setMd5(md5);
                }
            }
            return null;
        }

        @Override
        public void parseJson(String json, IUpdateParseCallback callback) throws Exception {
            Log.d("zlm", json);
        }

        @Override
        public boolean isAsyncParser() {
            return false;
        }
    }


    public static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionUpdate", "[getAppVersionCode]-error：" + e.getMessage());
        }
        return appVersionCode;
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


                if (sslCallback != null) {
                    sslCallback.startCallback();
                }
                pddismiss();
            } else if (intent.getAction().equals(ACTION_INTENT_STARTSERVER_FAILURE)) {
                String str = "服务启动失败";
                pd.setMessage(str);
                pddismiss();
            } else if (intent.getAction().equals(ACTION_INTENT_DOWNLOADCFG_SUCCESS)) {
                String str = "下载策略成功";
                pd.setMessage(str);
            } else if (intent.getAction().equals(ACTION_INTENT_STOPSERVER_SUCCESS)) {
                String str = "停止服务成功";
                pd.setMessage(str);

                if (sslCallback != null) {
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


    public void bindSsl() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                autoService = IAutoService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                autoService = null;
            }
        };

        Intent intent = new Intent(SERVICE_ACTION_NAME);
        // 目前的测试包包名
        intent.setPackage("koal.ssl");

        // 若应用被卸载，则会抛出异常
        try {
            //Android 11 开始，无法判断某个应用是否安装，解决办法见AndroidManifest.xml
            boolean res = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            // 应用未安装
            if (!res) {
                Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "绑定失败", Toast.LENGTH_SHORT).show();
        }

        // 广播接收器，用来监听SSL服务发出的广播
        srvMonitor = new ServiceMon();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_STARTSERVER_INPROC);
        filter.addAction(ACTION_INTENT_STARTSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_STARTSERVER_FAILURE);
        filter.addAction(ACTION_INTENT_DOWNLOADCFG_SUCCESS);
        filter.addAction(ACTION_INTENT_STOPSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_UPGRADE);
        registerReceiver(srvMonitor, filter, Context.RECEIVER_NOT_EXPORTED);
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
        pd.show();
        if (autoService != null) {
            try {
                if (autoService.isStarted()) {
                    pd.setMessage("服务已经开启");
                    pddismiss();
                    sslCallback.startCallback();
                } else {
                    autoService.start();
                    String info = "开始启动服务";
                    pd.setMessage(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
                pd.setMessage(e.toString());
                pddismiss();
            }
        } else {
            Toast.makeText(getApplicationContext(), "未绑定服务，请检查是否安装了SSL客户端", Toast.LENGTH_SHORT).show();
        }
    }

    void pddismiss() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        }, 5000);
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


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            OperLogUtil.mPostionX = (int) ev.getX();
            OperLogUtil.mPostionY = (int) ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

}




