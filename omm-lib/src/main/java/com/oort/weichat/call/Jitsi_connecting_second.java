package com.oort.weichat.call;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.VideoFile;
import com.oort.weichat.bean.event.EventNotifyByTag;
import com.oort.weichat.bean.event.MessageEventBG;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.VideoFileDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.CutoutHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.util.AppUtils;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.util.PermissionUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.TipDialog;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.bean.Result;
import com.oortcloud.bean.meeting.MeetingInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 音视频通话主页面：负责UI展示、权限处理、Jitsi视图管理、事件响应
 */
public class Jitsi_connecting_second extends BaseActivity implements JitsiMeetActivityInterface {
    // 常量定义（集中管理，便于修改）
    private static final String TAG = "JitsiCallActivity";
    private static final int RECORD_REQUEST_CODE = 0x01;
    private static final int SCREEN_RECORD_REQUEST_CODE = 123;
    private static final int NOTIFICATION_ID = 888;
    private static final String NOTIFICATION_CHANNEL_ID = "meeting_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "会议通知";
    private static final long COUNT_DOWN_TIMER_TOTAL = 18000000L; // 5小时
    private static final long COUNT_DOWN_TIMER_INTERVAL = 1000L; // 1秒间隔
    private static final long CALLING_TIMER_TOTAL = 3000L; // 3秒Ping间隔
    private static final int PING_MAX_FAIL_COUNT = 10; // 最大Ping失败次数

    // 成员变量（按功能分组）
    private final ReentrantLock stateLock = new ReentrantLock(); // 线程安全锁
    private final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());

    // Jitsi核心组件
    private FrameLayout mJitsiContainer;
    private JitsiMeetView mJitsiView;
    private JitsiMeetConferenceOptions.Builder mOptionsBuilder;
    private String mJitsiServerUrl;
    private String mRoomId;

    // 通话参数
    private int mCallType; // 通话类型：音频/视频/屏幕共享
    private String mFromUserId;
    private String mToUserId;
    private String mPeerName; // 对方名称
    private boolean mIsAnswer;
    private boolean mIsApi21HangUp;
    private boolean mIsOppositeHangUp;
    private boolean mIsClosedWhenLeave;
    private boolean mIsOldVersion = true;
    private boolean mIsShowing = false; // 页面是否可见
    private int mPingFailCount;
    private long mCallStartTime; // 通话开始时间
    private long mCallStopTime;

    // 服务与计时
    private RecordService mRecordService;
    private CountDownTimer mCallTimer; // 通话计时器（用于悬浮窗）
    private CountDownTimer mPingTimer; // Ping计时器（检测对方在线状态）
    private ServiceConnection mRecordServiceConn;

    // UI组件
    private ImageView mIvToggleCallType; // 切换音视频按钮
    private ImageView mIvOpenFloating; // 打开悬浮窗按钮
    private LinearLayout mRecordLayout; // 录屏控件容器
    private ImageView mIvRecord; // 录屏按钮
    private TextView mTvRecord; // 录屏状态文本

    // 会议相关
    private MeetingInfo mMeetingInfo;
    private String mMeetingId;

    // 静态变量（谨慎使用，避免内存泄漏）
    public static String sFloatTime; // 悬浮窗计时（全局唯一）


    // 对外启动方法（统一参数校验）
    public static void start(Context ctx, String fromUserId, String toUserId, int type) {
        start(ctx, fromUserId, toUserId, type, null);
    }

    public static void start(Context ctx, String fromUserId, String toUserId, int type, @Nullable String meetUrl) {
        start(ctx, fromUserId, toUserId, type, meetUrl, false, UUID.randomUUID().toString());
    }

    public static void start(Context ctx, String fromUserId, String toUserId, int type, @Nullable String meetUrl, boolean isAnswer,String roomId) {
        // 参数校验
        if (ctx == null || TextUtils.isEmpty(roomId) || TextUtils.isEmpty(toUserId)) {
            Log.e(TAG, "启动失败：参数为空（roomId=" + roomId + ", to=" + toUserId + ")");
            JitsistateMachine.isInCalling = false;
            Toast.makeText(ctx, "启动参数错误", Toast.LENGTH_SHORT).show();
            return;
        }

        // 会议类型跳转（单独页面）
        if (type == CallConstants.Talk_Meet) {
            Intent intent = new Intent(ctx, JitsiTalk.class);
            intent.putExtra("type", type);
            intent.putExtra("fromuserid", fromUserId);
            intent.putExtra("touserid", toUserId);
            if (!TextUtils.isEmpty(meetUrl)) {
                intent.putExtra("meetUrl", meetUrl);
            }
            ctx.startActivity(intent);
            return;
        }

        // 初始化核心配置
        CoreManager coreManager = CoreManager.getInstance(ctx);
        if (coreManager == null || coreManager.getConfig() == null) {
            Log.e(TAG, "启动失败：CoreManager未初始化");
            Toast.makeText(ctx, "系统初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 构建Jitsi配置
            JitsiMeetConferenceOptions options = buildJitsiOptions(ctx, coreManager, roomId, type, meetUrl);
            // 初始化会议管理器并启动
            JitsiMeetingManager meetingManager = new JitsiMeetingManager(ctx, new JitsiMeetingManager.MeetingCallback() {
                @Override
                public void onConferenceJoined(String url,long startTime) {
                    Log.i(TAG, "会议加入成功: " + url);
                    JitsistateMachine.isInCalling = true; // 标记通话中
                }

                @Override
                public void onConferenceTerminated(String error, long startTime) {
                    Log.i(TAG, "会议结束: " + error);
                    JitsistateMachine.isInCalling = false;
                    long duration = System.currentTimeMillis() - startTime;
                    if(type == CallConstants.Video || type == CallConstants.Audio) {
                        JitsiActivity.overCall_(duration, type, toUserId, ctx);
                    }
                }

                @Override
                public void onConferenceFailed(String error) {
                    Log.e(TAG, "会议失败: " + error);
                    Toast.makeText(ctx, "会议失败: " + error, Toast.LENGTH_SHORT).show();
                    JitsistateMachine.isInCalling = false;
                }

                @Override
                public void onParticipantLeft(String error) {
                    Log.i(TAG, "参与者离开: " + error);
                    Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show();

                    if(type == CallConstants.Video || type == CallConstants.Audio) {
                        JitsistateMachine.isInCalling = false;
                        Intent hangupIntent = BroadcastIntentHelper.buildHangUpIntent();
                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(hangupIntent);
                    }
                }
            });
            meetingManager.launchMeeting(options);
        } catch (Exception e) {
            Log.e(TAG, "启动会议失败", e);
            JitsistateMachine.isInCalling = false;

            Toast.makeText(ctx, "启动会议失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** 构建Jitsi会议配置（独立方法，减少冗余） */
    private static JitsiMeetConferenceOptions buildJitsiOptions(Context ctx, CoreManager coreManager, String roomId, int type, String meetUrl) throws MalformedURLException {
        // 基础配置（功能开关）
        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder()
                .setRoom(roomId) // 房间ID（基于发起者ID）
                .setAudioMuted(false)
                .setVideoMuted(CallConstants.isAudio(type) || (CallConstants.isScreenMode(type) && false))
                .setServerURL(new URL(TextUtils.isEmpty(meetUrl) ? coreManager.getConfig().JitsiServer : meetUrl))
                // 禁用非必要功能
                .setFeatureFlag("welcomepage.enabled", false)
                .setFeatureFlag("unsaferoomwarning.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("prejoinpage.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("conference-timer.enabled", false)
                .setFeatureFlag("add-people.enabled", false)
                .setFeatureFlag("security-options.enabled", false)
                .setFeatureFlag("replace.participant", true)
                .setFeatureFlag("notifications.enabled", false)
                .setFeatureFlag("kick-out.enabled", false)
                .setFeatureFlag("settings.enabled", false)
                .setFeatureFlag("reactions.enabled", false)
                .setFeatureFlag("close-captions.enabled", false)
                .setFeatureFlag("chat.enabled", false)
                .setConfigOverride("maxParticipants", 100) // 单聊限制2人
                .setFeatureFlag("tile-view.enabled", false);

        // 会议类型特殊配置
        if (type == CallConstants.Audio_Meet || type == CallConstants.Video_Meet) {
            builder.setFeatureFlag("overflow-menu.enabled", true); // 会议模式启用更多菜单
        }

        // 设置用户信息（昵称、头像）
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName(coreManager.getSelf().getNickName());
        String avatarUrl = UserInfoUtils.getInstance(ctx).getLoginUserInfo().getOort_photo();
        if (!TextUtils.isEmpty(avatarUrl)) {
            try {
                userInfo.setAvatar(new URL(avatarUrl));
            } catch (MalformedURLException e) {
                Log.w(TAG, "头像URL无效: " + avatarUrl, e);
            }
        }
        builder.setUserInfo(userInfo);
        builder.setToken(UserInfoUtils.getInstance(ctx).getLoginUserInfo().getImuserid());

        OperLogUtil.msg("Jitsi服务器: " + coreManager.getConfig().JitsiServer + ", 房间ID: " + roomId);
        return builder.build();
    }


    // 生命周期方法（规范资源管理）
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // 窗口配置（保持屏幕常亮、解锁显示）
            CutoutHelper.setWindowOut(getWindow());
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // 初始化流程（按顺序执行）
            setContentView(R.layout.jitsiconnecting);
            if (!parseIntentParams()) { // 解析参数失败则退出
                finish();
                return;
            }
            initViews(); // 初始化UI
            initServices(); // 初始化录屏服务
            initTimers(); // 初始化计时器
            initEvents(); // 初始化点击事件
            registerEventListeners(); // 注册广播和EventBus

            // 忽略证书验证（仅用于内部环境，生产环境需移除）
            initSSLIgnore();

            // 初始化Jitsi视图
            initJitsiView();

        } catch (Exception e) {
            Log.e(TAG, "初始化失败", e);
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onCoreReady() {
        super.onCoreReady();
        runOnUiThread(this::sendCallingPing); // 核心初始化完成后发送Ping
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLock.lock();
        try {
            mIsShowing = true;
        } finally {
            stateLock.unlock();
        }

        // 页面可见时：关闭悬浮窗和通知
        if (JitsistateMachine.isFloating) {
            sendBroadcast(new Intent(CallConstants.CLOSE_FLOATING));
        }
        cancelMeetingNotification();

        // Jitsi生命周期回调
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        stateLock.lock();
        try {
            mIsShowing = false;
        } finally {
            stateLock.unlock();
        }

        // 页面不可见时：启动悬浮窗或显示通知
        if (!isFinishing()) {
            if (AppUtils.checkAlertWindowsPermission(this)) {
                startService(new Intent(this, JitsiFloatService.class));
            }
            showMeetingNotification();
        }

        // Jitsi生命周期回调
        JitsiMeetActivityDelegate.onHostPause(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 释放Jitsi资源
        if (mJitsiView != null) {
            mJitsiView.abort();
            mJitsiView.dispose();
            mJitsiView = null;
        }
        JitsiMeetActivityDelegate.onHostDestroy(this);

        // 注销事件监听
        unregisterEventListeners();

        // 停止计时器
        if (mCallTimer != null) {
            mCallTimer.cancel();
        }
        if (mPingTimer != null) {
            mPingTimer.cancel();
        }

        // 解绑录屏服务
        if (mRecordServiceConn != null) {
            try {
                if (mRecordService != null && mRecordService.isRunning()) {
                    mRecordService.stopRecord();
                    saveScreenRecord();
                }
                unbindService(mRecordServiceConn);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "服务已解绑", e);
            }
        }

        // 重置状态
        JitsistateMachine.reset();
        cancelMeetingNotification();
        sFloatTime = null;

        Log.i(TAG, "资源释放完成");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 禁止返回键退出通话
        super.onBackPressed();
        Toast.makeText(this, "通话中禁止返回", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }


    // 核心初始化方法（拆分长逻辑）
    /** 解析Intent参数（带校验） */
    private boolean parseIntentParams() {
        // 会议参数解析
        mMeetingInfo = (MeetingInfo) getIntent().getSerializableExtra("meeting_obj");
        mMeetingId = getIntent().getStringExtra("meetingID");
        if (mMeetingInfo != null) {
            String uid = mMeetingInfo.getUid().replace("-", "");
            mFromUserId = uid;
            mToUserId = uid;
            meetingOpen(0);
            updateMeetingParticipantCount(0);
            return true;
        }

        // 通话参数解析
        mCallType = getIntent().getIntExtra("type", 0);
        mFromUserId = getIntent().getStringExtra("fromuserid");
        mToUserId = getIntent().getStringExtra("touserid");
        mIsAnswer = getIntent().getBooleanExtra("answer", false);

        // 校验参数
        if (TextUtils.isEmpty(mFromUserId) || TextUtils.isEmpty(mToUserId) || mCallType == 0) {
            Log.e(TAG, "通话参数异常（type=" + mCallType + ", from=" + mFromUserId + ", to=" + mToUserId + ")");
            DialogHelper.tip(this, "通话参数异常");
            return false;
        }

        // 初始化Jitsi服务器地址
        mJitsiServerUrl = getIntent().getStringExtra("meetUrl");
        CoreManager coreManager = CoreManager.getInstance(this);
        if (TextUtils.isEmpty(mJitsiServerUrl) && coreManager != null && coreManager.getConfig() != null) {
            mJitsiServerUrl = coreManager.getConfig().JitsiServer;
        }
        if (TextUtils.isEmpty(mJitsiServerUrl)) {
            DialogHelper.tip(this, getString(R.string.tip_meet_server_empty));
            return false;
        }

        // 获取对方名称
        try {
            Friend friend = FriendDao.getInstance().getFriend(
                    coreManager.getSelf().getUserId(),
                    CallConstants.isSingleChat(mCallType) ? mToUserId : mFromUserId
            );
            mPeerName = friend != null ? friend.getShowName() : "未知用户";
        } catch (Exception e) {
            Log.w(TAG, "获取联系人名称失败", e);
            mPeerName = "未知用户";
        }

        // 录屏模式初始化
        if (CallConstants.isScreenMode(mCallType)) {
            ScreenModeHelper.startScreenMode(this::requestScreenRecordPermission);
        } else {
            ScreenModeHelper.stopScreenMode();
        }

        // 标记通话状态
        JitsistateMachine.isInCalling = true;
        JitsistateMachine.callingOpposite = CallConstants.isSingleChat(mCallType) ? mToUserId : mFromUserId;
        return true;
    }

    /** 初始化UI组件 */
    private void initViews() {
        // 刘海屏适配
        CutoutHelper.initCutoutHolderTop(getWindow(), findViewById(R.id.vCutoutHolder));

        // 切换音视频按钮（仅单聊显示）
        if (mCallType == CallConstants.Audio || mCallType == CallConstants.Video) {
            mIvToggleCallType = findViewById(R.id.ivChange);
            mIvToggleCallType.setImageResource(CallConstants.isAudio(mCallType)
                    ? R.mipmap.call_change_to_video
                    : R.mipmap.call_change_to_voice);
            mIvToggleCallType.setVisibility(View.VISIBLE);
        }

        // Jitsi视图容器
        mJitsiContainer = findViewById(R.id.jitsi_view);

        // 悬浮窗与录屏按钮
        mIvOpenFloating = findViewById(R.id.open_floating);
        mRecordLayout = findViewById(R.id.record_ll);
        mIvRecord = findViewById(R.id.record_iv);
        mTvRecord = findViewById(R.id.record_tv);

        // 录屏功能仅支持Lollipop及以上
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mRecordLayout.setVisibility(View.GONE);
        }
    }

    /** 初始化Jitsi视图 */
    private void initJitsiView() {
        mJitsiView = new JitsiMeetView(this);
        mJitsiView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mJitsiContainer.addView(mJitsiView);

        // 构建Jitsi配置
        mOptionsBuilder = buildJitsiConferenceOptions();
        if (mOptionsBuilder == null) {
            finish();
            return;
        }

        // 加入会议
        mRoomId = mOptionsBuilder.build().getRoom();
        Log.i(TAG, "加入会议：房间ID=" + mRoomId + "，服务器=" + mJitsiServerUrl);
        mJitsiView.join(mOptionsBuilder.build());
    }

    /** 构建Jitsi会议配置 */
    @Nullable
    private JitsiMeetConferenceOptions.Builder buildJitsiConferenceOptions() {
        try {
            // 基础功能开关
            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder()
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("unsaferoomwarning.enabled", false)
                    .setFeatureFlag("meeting-name.enabled", false)
                    .setFeatureFlag("prejoinpage.enabled", false)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("conference-timer.enabled", false)
                    .setFeatureFlag("add-people.enabled", false)
                    .setFeatureFlag("overflow-menu.enabled", mCallType == CallConstants.Audio_Meet || mCallType == CallConstants.Video_Meet)
                    .setFeatureFlag("security-options.enabled", false)
                    .setFeatureFlag("replace.participant", true)
                    .setFeatureFlag("notifications.enabled", false)
                    .setFeatureFlag("kick-out.enabled", false)
                    .setFeatureFlag("settings.enabled", false)
                    .setFeatureFlag("reactions.enabled", false)
                    .setFeatureFlag("close-captions.enabled", false)
                    .setFeatureFlag("chat.enabled", false)
                    .setFeatureFlag("tile-view.enabled", false)
                    .setAudioMuted(false)
                    .setVideoMuted(CallConstants.isAudio(mCallType) || (CallConstants.isScreenMode(mCallType) && mIsAnswer));

            // 服务器地址
            builder.setServerURL(new URL(mJitsiServerUrl));

            // 房间ID（按类型区分，避免冲突）
            String roomSuffix = mFromUserId + getApplication().getPackageName();
            switch (mCallType) {
                case CallConstants.Audio_Meet:
                    builder.setRoom("audio_" + roomSuffix);
                    break;
                case CallConstants.Video_Meet:
                    builder.setRoom("video_" + roomSuffix);
                    break;
                case CallConstants.Video:
                    builder.setRoom("video_single_" + roomSuffix);
                    break;
                default:
                    builder.setRoom(roomSuffix);
                    break;
            }

            // 加载用户Token
            loadUserToken(builder);
            return builder;

        } catch (MalformedURLException e) {
            Log.e(TAG, "Jitsi服务器地址无效: " + mJitsiServerUrl, e);
            DialogHelper.tip(this, "服务器地址无效");
            return null;
        }
    }

    /** 初始化录屏服务连接 */
    private void initServices() {
        mRecordServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.i(TAG, "录屏服务连接成功");
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
                mRecordService = binder.getRecordService();
                mRecordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Log.w(TAG, "录屏服务断开");
                mRecordService = null;
            }
        };

        // 绑定录屏服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bindService(new Intent(this, RecordService.class), mRecordServiceConn, BIND_AUTO_CREATE);
            mRecordLayout.setVisibility(View.VISIBLE);
        }
    }

    /** 初始化计时器 */
    private void initTimers() {
        // 通话计时器（更新悬浮窗时间）
        mCallTimer = new CountDownTimer(COUNT_DOWN_TIMER_TOTAL, COUNT_DOWN_TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                sFloatTime = formatTime(System.currentTimeMillis() - mCallStartTime);
                sendBroadcast(new Intent(CallConstants.REFRESH_FLOATING));
            }

            @Override
            public void onFinish() {}
        };

        // Ping计时器（检测对方在线状态）
        mPingTimer = new CountDownTimer(CALLING_TIMER_TOTAL, COUNT_DOWN_TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                if (!isFinishing()) {
                    checkCallingState();
                }
            }
        };
    }

    /** 初始化点击事件 */
    private void initEvents() {
        // 邀请参会按钮（会议类型）
        ImageView ivInvite = findViewById(R.id.ysq_iv);
        CoreManager coreManager = CoreManager.getInstance(this);
        if (coreManager != null) {
            Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), mFromUserId);
            boolean isRoomOrMeet = (friend != null && friend.getRoomFlag() != 0)
                    || (mCallType == CallConstants.Audio_Meet || mCallType == CallConstants.Video_Meet);

            if (isRoomOrMeet) {
                ivInvite.setVisibility(View.VISIBLE);
                ivInvite.setOnClickListener(v -> {
                    if (mCallType == CallConstants.Audio_Meet || mCallType == CallConstants.Video_Meet) {
                        startPersonPickActivity();
                    } else {
                        JitsiInviteActivity.start(this, mCallType, mFromUserId);
                    }
                });
            }
        }

        // 打开悬浮窗按钮
        mIvOpenFloating.setOnClickListener(v -> {
            if (AppUtils.checkAlertWindowsPermission(this)) {
                moveTaskToBack(true); // 退到后台，显示悬浮窗
            } else {
                showFloatPermissionTip();
            }
        });

        // 录屏按钮
        mRecordLayout.setOnClickListener(v -> {
            if (mRecordService == null) return;
            if (mRecordService.isRunning()) {
                mRecordService.stopRecord();
               // mIvRecord.setImageResource(R.mipmap.icon_record);
                mTvRecord.setText(getString(R.string.start_record));
                saveScreenRecord();
            } else {
                mRecordService.startRecord();
               // mIvRecord.setImageResource(R.mipmap.icon_recording);
                mTvRecord.setText(getString(R.string.stop_recording));
            }
        });

        // 切换音视频按钮
        if (mIvToggleCallType != null) {
            mIvToggleCallType.setOnClickListener(v -> {
                toggleCallType();
                sendToggleCallTypeMsg();
            });
        }
    }

    /** 注册事件监听（广播+EventBus） */
    private void registerEventListeners() {
        // 注册Jitsi广播
        IntentFilter intentFilter = new IntentFilter();
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mJitsiBroadcastReceiver, intentFilter);

        // 注册EventBus
        EventBus.getDefault().register(this);
        setSwipeBackEnable(false);
    }

    /** 注销事件监听 */
    private void unregisterEventListeners() {
        // 注销广播
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mJitsiBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "广播已注销", e);
        }

        // 注销EventBus
        EventBus.getDefault().unregister(this);
    }

    /** 忽略SSL证书验证（仅调试用） */
    private void initSSLIgnore() {
        try {
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            HostnameVerifier hostnameVerifier = (String hostname, SSLSession session) -> true;

            // 反射替换Jitsi的OkHttp客户端
            Class<?> jitsiHttpClass = Class.forName("org.jitsi.meet.sdk.HttpClient");
            java.lang.reflect.Field okHttpClientField = jitsiHttpClass.getDeclaredField("client");
            okHttpClientField.setAccessible(true);

            okhttp3.OkHttpClient originalClient = (okhttp3.OkHttpClient) okHttpClientField.get(null);
            okhttp3.OkHttpClient newClient = originalClient.newBuilder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .hostnameVerifier(hostnameVerifier)
                    .build();

            okHttpClientField.set(null, newClient);
            Log.i(TAG, "证书忽略配置完成");

        } catch (Exception e) {
            Log.e(TAG, "证书忽略配置失败", e);
            DialogHelper.tip(this, "证书配置失败");
        }
    }


    // 事件处理方法
    /** Jitsi广播接收器 */
    private final BroadcastReceiver mJitsiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || isFinishing()) return;
            BroadcastEvent event = new BroadcastEvent(intent);
            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    handleConferenceJoined();
                    break;
                case PARTICIPANT_LEFT:
                    handleParticipantLeft();
                    break;
                case CONFERENCE_TERMINATED:
                    handleConferenceTerminated();
                    break;
                default:
                    Log.d(TAG, "未处理的事件: " + event.getType());
            }
        }
    };

    /** 处理会议加入成功 */
    private void handleConferenceJoined() {
        Log.i(TAG, "已加入会议");
        runOnUiThread(() -> {
            mIvOpenFloating.setVisibility(View.VISIBLE);
            mCallStartTime = System.currentTimeMillis();
            mCallTimer.start(); // 启动通话计时
        });
    }

    /** 处理参与者离开 */
    private void handleParticipantLeft() {
        Log.i(TAG, "参会者离开");
        if (mCallType != CallConstants.Audio && mCallType != CallConstants.Video) return;

        mIsClosedWhenLeave = true;
        mJitsiView.abort();
        if (!mIsApi21HangUp) {
            mCallStopTime = System.currentTimeMillis();
            finishCall((int) (mCallStopTime - mCallStartTime) / 1000);
        }
        sendBroadcast(new Intent(CallConstants.CLOSE_FLOATING));
        finish();
        meetingOpen(1);
        updateMeetingParticipantCount(1);
    }

    /** 处理会议终止 */
    private void handleConferenceTerminated() {
        Log.i(TAG, "会议结束");
        if (mIsClosedWhenLeave) return;

        if (!mIsApi21HangUp) {
            mCallStopTime = System.currentTimeMillis();
            finishCall((int) (mCallStopTime - mCallStartTime) / 1000);
        }
        sendBroadcast(new Intent(CallConstants.CLOSE_FLOATING));
        finish();
        meetingOpen(1);
        updateMeetingParticipantCount(1);
    }

    /** EventBus：接收中断通话事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventNotifyByTag event) {
        if (event.tag.equals(EventNotifyByTag.Interrupt)) {
            sendBroadcast(new Intent(CallConstants.CLOSE_FLOATING));
            leaveMeeting();
        }
    }

    /** EventBus：接收通话类型切换事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageCallTypeChange event) {
        ChatMessage msg = event.chatMessage;
        if (msg == null || msg.getType() != XmppMessage.TYPE_CHANGE_VIDEO_ENABLE) return;
        if (msg.getFromUserId().equals(mToUserId)) {
            toggleCallType(TextUtils.equals(msg.getContent(), "1")); // "1"表示视频开启
        }
    }

    /** EventBus：接收对方Ping消息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageCallingEvent event) {
        ChatMessage msg = event.chatMessage;
        if (msg == null || msg.getType() != XmppMessage.TYPE_IN_CALLING) return;
        if (msg.getFromUserId().equals(mToUserId)) {
            stateLock.lock();
            try {
                mIsOldVersion = false;
                mPingFailCount = 0; // 重置失败次数
            } finally {
                stateLock.unlock();
            }
            Log.i(TAG, "收到对方Ping，重置状态");
        }
    }

    /** EventBus：接收挂断事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageHangUpPhone event) {
        ChatMessage msg = event.chatMessage;
        if (msg == null) return;
        String fromId = msg.getFromUserId();
        if (!fromId.equals(mFromUserId) && !fromId.equals(mToUserId)) return;

        // Android 5.0特殊处理
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            mIsApi21HangUp = true;
            TipDialog tipDialog = new TipDialog(this);
            tipDialog.setmConfirmOnClickListener(getString(R.string.av_hand_hang), this::hideBottomUIMenu);
            tipDialog.show();
            return;
        }

        // 普通挂断处理
        mIsOppositeHangUp = true;
        sendBroadcast(new Intent(CallConstants.CLOSE_FLOATING));
        leaveMeeting();
    }

    /** EventBus：接收后台唤醒事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEventBG event) {
        if (!event.flag) return;

        AsyncUtils.postDelayed(this, activity -> {
            stateLock.lock();
            try {
                if (!mIsShowing) {
                    if (JitsistateMachine.isFloating) {
                        showMeetingNotification();
                    } else {
                        Intent intent = new Intent(getBaseContext(), Jitsi_connecting_second.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            } finally {
                stateLock.unlock();
            }
        }, 200);
    }


    // 工具方法（独立封装）
    /** 请求屏幕录制权限 */
    private void requestScreenRecordPermission() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager == null) {
            Toast.makeText(this, "不支持屏幕录制", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_RECORD_REQUEST_CODE);
    }

    /** 发送通话Ping消息（检测对方在线） */
    private void sendCallingPing() {
        stateLock.lock();
        try {
            // 标记等待对方响应
        } finally {
            stateLock.unlock();
        }

        ChatMessage msg = new ChatMessage();
        msg.setType(XmppMessage.TYPE_IN_CALLING);
        msg.setFromUserId(CoreManager.getInstance(this).getSelf().getUserId());
        msg.setFromUserName(CoreManager.getInstance(this).getSelf().getNickName());
        msg.setToUserId(mToUserId);
        msg.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        msg.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

        CoreManager.getInstance(this).sendChatMessage(mToUserId, msg);
        mPingTimer.start();
    }

    /** 切换通话类型（音频/视频） */
    private void toggleCallType() {
        toggleCallType(mCallType == CallConstants.Audio);
    }

    private void toggleCallType(boolean enableVideo) {
        stateLock.lock();
        try {
            if (enableVideo) {
                mCallType = CallConstants.Video;
                mIvToggleCallType.setImageResource(R.mipmap.call_change_to_voice);
                sendJitsiCommand(BroadcastIntentHelper.buildSetVideoMutedIntent(false));
            } else {
                mCallType = CallConstants.Audio;
                mIvToggleCallType.setImageResource(R.mipmap.call_change_to_video);
                sendJitsiCommand(BroadcastIntentHelper.buildSetVideoMutedIntent(true));
            }
        } finally {
            stateLock.unlock();
        }
    }

    /** 发送Jitsi命令（如静音、切换视频） */
    private void sendJitsiCommand(Intent intent) {
        runOnUiThread(() -> LocalBroadcastManager.getInstance(this).sendBroadcast(intent));
    }

    /** 发送通话类型切换消息给对方 */
    private void sendToggleCallTypeMsg() {
        ChatMessage msg = new ChatMessage();
        msg.setType(XmppMessage.TYPE_CHANGE_VIDEO_ENABLE);
        msg.setContent(mCallType == CallConstants.Audio ? "0" : "1"); // 0:音频，1:视频
        msg.setFromUserId(CoreManager.getInstance(this).getSelf().getUserId());
        msg.setFromUserName(CoreManager.getInstance(this).getSelf().getNickName());
        msg.setToUserId(mToUserId);
        msg.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        msg.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));

        CoreManager.getInstance(this).sendChatMessage(mToUserId, msg);
    }

    /** 加载用户Token到Jitsi配置 */
    private void loadUserToken(JitsiMeetConferenceOptions.Builder builder) {
        try {
            CoreManager coreManager = CoreManager.getInstance(this);
            if (coreManager == null) return;

            // 设置用户Token（用于身份验证）
            builder.setToken(coreManager.getSelf().getUserId());

            // 构建用户信息（Jitsi房间内显示）
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("avatar", AvatarHelper.getAvatarUrl(coreManager.getSelf().getUserId(), false));
            userInfo.put("name", coreManager.getSelf().getNickName());

            Map<String, Object> contextMap = new HashMap<>();
            contextMap.put("user", userInfo);

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("context", contextMap);

            // 注：JWT签名逻辑需根据实际需求实现
        } catch (Exception e) {
            Log.e(TAG, "加载用户Token失败", e);
        }
    }

    /** 显示会议通知（后台时） */
    private void showMeetingNotification() {
        if (TextUtils.isEmpty(mPeerName)) return;

        // 点击通知返回通话页面
        Intent intent = new Intent(this, Jitsi_connecting_second.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 通知管理器与渠道（Oreo+必需）
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW // 低优先级，避免弹窗干扰
            );
            channel.setSound(null, null); // 关闭声音
            channel.setVibrationPattern(new long[0]); // 关闭震动
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setPriority(Notification.PRIORITY_LOW);
            builder.setSound(null);
        }

        // 通知内容
        String callTypeStr = CallConstants.isAudio(mCallType)
                ? getString(R.string.chat_audio)
                : getString(R.string.chat_with_video);
        builder.setContentTitle(mPeerName)
                .setContentIntent(pendingIntent)
                .setContentText(getString(R.string.tip_meet_background_place_holder, callTypeStr))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true) // 会议中不允许删除
                .setSmallIcon(R.mipmap.icon_appicon);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /** 取消会议通知 */
    private void cancelMeetingNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    /** 格式化时间（mm:ss） */
    private String formatTime(long durationMs) {
        Date date = new Date(durationMs);
        return new SimpleDateFormat("mm:ss", Locale.getDefault()).format(date);
    }

    /** 隐藏底部导航栏 */
    private void hideBottomUIMenu() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.GONE);
    }

    /** 保存录屏文件到数据库 */
    private void saveScreenRecord() {
        try {
            String recordPath = PreferenceUtils.getString(getApplicationContext(), "IMScreenRecord");
            if (TextUtils.isEmpty(recordPath)) return;

            File file = new File(recordPath);
            if (!file.exists() || !file.getName().toLowerCase().endsWith(".mp4")) {
                Log.w(TAG, "录屏文件不存在或格式错误: " + recordPath);
                return;
            }

            // 保存到数据库
            VideoFile videoFile = new VideoFile();
            videoFile.setCreateTime(TimeUtils.f_long_2_str(getRecordCreateTime(file.getName())));
            videoFile.setFileLength(getRecordDuration(file.getPath()));
            videoFile.setFileSize(file.length());
            videoFile.setFilePath(file.getPath());
            videoFile.setOwnerId(CoreManager.getInstance(this).getSelf().getUserId());
            VideoFileDao.getInstance().addVideoFile(videoFile);

            ToastUtil.showToast(this, getString(R.string.save_success));

        } catch (Exception e) {
            Log.e(TAG, "保存录屏失败", e);
            ToastUtil.showToast(this, getString(R.string.save_failed));
        }
    }

    /** 从文件名解析录屏创建时间 */
    private long getRecordCreateTime(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) return System.currentTimeMillis();

        try {
            return Long.parseLong(fileName.substring(0, dotIndex));
        } catch (NumberFormatException e) {
            Log.w(TAG, "解析录屏时间失败", e);
            return System.currentTimeMillis();
        }
    }

    /** 获取录屏文件时长（秒） */
    private long getRecordDuration(String filePath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration() / 1000;
        } catch (Exception e) {
            Log.w(TAG, "获取录屏时长失败", e);
            return 10; // 默认10秒
        } finally {
            mediaPlayer.release(); // 确保释放
        }
    }

    /** 结束通话 */
    private void finishCall(int duration) {
        if (mIsOppositeHangUp) return;
        JitsiActivity.overCall_(duration * 1000L, mCallType, mToUserId, this);
    }

    /** 离开会议 */
    private void leaveMeeting() {
        Log.i(TAG, "离开会议");
        if (mJitsiView != null) {
            mJitsiView.abort();
        }
        finish();
    }

    /** 启动选人页面（邀请参会） */
    private void startPersonPickActivity() {
        Intent intent = new Intent(this, PersonPickActivity.class);
        startActivityForResult(intent, 100);
        PersonPickActivity.pickFinish = null;
        PersonPickActivity.pickFinish_v2 = null;

        JitsistateMachine.isInCalling = false;
        PersonPickActivity.pickFinish_v2 = new PersonPickActivity.PickFinish_v2() {
            @Override
            public void finish(List imids, List userIds, List names, List headerUrls, List extrs) {
                CoreManager coreManager = CoreManager.getInstance(Jitsi_connecting_second.this);
                if (coreManager == null) return;

                String loginUserId = coreManager.getSelf().getUserId();
                String loginNickName = coreManager.getSelf().getNickName();
                EventBus.getDefault().post(new MessageEventMeetingInvite(
                        mRoomId, "", loginUserId, loginUserId,
                        loginNickName, mFromUserId, imids, mCallType
                ));
                PersonPickActivity.pickFinish_v2 = null;
            }
        };
    }

    /** 显示悬浮窗权限提示 */
    private void showFloatPermissionTip() {
        SelectionFrame selectionFrame = new SelectionFrame(this);
        selectionFrame.setSomething(null, getString(R.string.av_no_float), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {
                hideBottomUIMenu();
            }

            @Override
            public void confirmClick() {
                PermissionUtil.startApplicationDetailsSettings(Jitsi_connecting_second.this, RECORD_REQUEST_CODE);
                hideBottomUIMenu();
            }
        });
        selectionFrame.show();
    }

    /** 检查通话状态（Ping失败处理） */
    private void checkCallingState() {
        // 无网络判断
        if (!HttpUtil.isGprsOrWifiConnected(this)) {
            TipDialog tipDialog = new TipDialog(this);
            tipDialog.setmConfirmOnClickListener(getString(R.string.check_network), this::leaveMeeting);
            tipDialog.show();
            return;
        }

        // 单聊状态检测
        if (CallConstants.isSingleChat(mCallType)) {
            stateLock.lock();
            try {
                if (mPingFailCount >= PING_MAX_FAIL_COUNT) {
                    if (mIsOldVersion) return;
                    Log.e(TAG, "Ping失败次数超限，自动挂断");
                    mCallStopTime = System.currentTimeMillis();
                    finishCall((int) (mCallStopTime - mCallStartTime) / 1000);
                    Toast.makeText(this, getString(R.string.tip_opposite_offline_auto__end_call), Toast.LENGTH_SHORT).show();
                    leaveMeeting();
                } else {
                    mPingFailCount++;
                    Log.e(TAG, "Ping失败次数=" + mPingFailCount);
                    sendCallingPing();
                }
            } finally {
                stateLock.unlock();
            }
        }
    }


    // 会议相关方法
    private void initMeeting() {
        if (mMeetingInfo == null) return;

        RequesManager.getMeetingDetail(mMeetingInfo.getUid())
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            Result<MeetingInfo> result = new Gson().fromJson(s, new TypeToken<Result<MeetingInfo>>() {}.getType());
                            if (result != null && result.isOk()) {
                                mMeetingInfo = result.getData();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "解析会议详情失败", e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "获取会议详情失败", e);
                    }
                });
    }

    private void meetingOpen(int open) {
        if (mMeetingInfo == null) return;

        String uuid = UserInfoUtils.getInstance(this).getLoginUserInfo().getOort_uuid();
        if (!mMeetingInfo.getUuid().equals(uuid)) return;

        RequesManager.meetingOpen(open, mMeetingInfo.getMeet_id(), uuid)
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                            if (result != null && result.isOk()) {
                                ToastUtil.showToast(Jitsi_connecting_second.this,
                                        open == 0 ? "开始会议" : "结束会议");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "解析会议状态失败", e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "更新会议状态失败", e);
                    }
                });
    }

    private void updateMeetingParticipantCount(int open) {
        if (mMeetingInfo == null) return;

        RequesManager.updateNumbarMeeting(open, mMeetingInfo.getMeet_id())
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                            if (result == null || !result.isOk()) {
                                Log.w(TAG, "更新参会人数失败");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "解析参会人数结果失败", e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "更新参会人数失败", e);
                    }
                });
    }


    // JitsiActivityInterface实现方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECORD_REQUEST_CODE:
                // 权限请求结果
                break;
            case SCREEN_RECORD_REQUEST_CODE:
                // 屏幕录制权限结果
                if (resultCode == RESULT_OK && data != null) {
                    ScreenModeHelper.startScreenMode(data);
                } else {
                    Toast.makeText(this, "屏幕录制权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG, "指针捕获状态变化: " + hasCapture);
    }
}