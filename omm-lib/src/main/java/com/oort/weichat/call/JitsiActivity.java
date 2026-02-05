package com.oort.weichat.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.oort.weichat.R;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.ui.lccontact.PersonPickActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.BuildConfig;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Jitsi会议容器：继承SDK的Activity，负责会议启动与基础交互
 */
public class JitsiActivity extends JitsiMeetActivity {
    private static final String TAG = "JitsiActivity";
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1001;
    private JitsiMeetingManager mMeetingManager;
    private JitsiMeetView mJitsiView; // 保存Jitsi视图引用

    // 静态启动方法（支持单聊参数）
    public static void launch(Context context, JitsiMeetConferenceOptions options) {
        Intent intent = new Intent(context, JitsiActivity.class);
        intent.setAction("org.jitsi.meet.CONFERENCE");
        intent.putExtra("JitsiMeetConferenceOptions", options);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 调试模式检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BuildConfig.DEBUG && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }

        // 初始化Jitsi视图引用
        mJitsiView = getJitsiView();

        // 初始化会议管理器
       // initMeetingManager();

        // 添加挂断按钮
       // addHangupButton();
        // 初始化房间号显示控件
        if(BuildConfig.DEBUG) {
            initRoomNumberDisplay();
        }
        initAddParticipantButton();
    }

    // 在类中添加成员变量
    private DraggableAddParticipantButton addParticipantBtn;
    /** 初始化可拖拽的添加参会人按钮 */
    private void initAddParticipantButton() {
        addParticipantBtn = new DraggableAddParticipantButton(this);

        // 设置按钮点击事件（打开选择联系人界面）
        addParticipantBtn.setOnAddParticipantClickListener(v -> {
            // 这里实现添加参会人的逻辑，例如打开联系人选择界面
            openParticipantSelection();
        });

        // 设置布局参数（初始位置：右下角）
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END; // 右下角
        params.bottomMargin = dp2px(120); // 距离底部（避开挂断按钮）
        params.rightMargin = dp2px(20); // 距离右边

        // 添加到内容布局
        ((FrameLayout) findViewById(android.R.id.content)).addView(addParticipantBtn, params);
    }

    /** 打开参会人选择界面（示例方法） */
    private void openParticipantSelection() {
        showToast("选择参会人");
        {

            Intent in = new Intent(this, PersonPickActivity.class);

            startActivityForResult(in, 100);
            PersonPickActivity.pickFinish = null;
            PersonPickActivity.pickFinish_v2 = null;


            PersonPickActivity.pickFinish_v2 = new PersonPickActivity.PickFinish_v2() {
                @Override
                public void finish(List imids, List userIds, List names, List headerUrls, List extrs) {

                    JitsiMeetConferenceOptions options = getIntent().getParcelableExtra("JitsiMeetConferenceOptions");
                    if (options != null && options.getServerURL() != null) {
                        //String meetingUrl = options.getServerURL().toString();
                        String roomid = options.getRoom().toString();
                        EventBus.getDefault().post(new MessageEventInitiateMeeting(CallConstants.Video_Meet, imids, true, roomid));
                        PersonPickActivity.pickFinish_v2 = null;
                    }
                }
            };
        }

    }
    private TextView roomNumberTextView;
    /** 初始化房间号显示控件 */
    private void initRoomNumberDisplay() {
        // 创建TextView
        roomNumberTextView = new TextView(this);
        roomNumberTextView.setTextColor(0xFFFFFFFF); // 白色文字
        roomNumberTextView.setBackgroundColor(0xCC000000); // 半透明黑色背景
        roomNumberTextView.setPadding(dp2px(8), dp2px(4), dp2px(8), dp2px(4)); // 内边距
        roomNumberTextView.setTextSize(14); // 文字大小

        // 获取并显示房间号
        String roomNumber = getRoomNumberFromIntent();
        roomNumberTextView.setText("房间号: " + (roomNumber != null ? roomNumber : "未知"));

        // 设置布局参数（左上角显示）
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.TOP | Gravity.CENTER_HORIZONTAL; // 左上角
        params.topMargin = dp2px(80); // 顶部边距
       // params.leftMargin = dp2px(20); // 左边边距

        // 添加到内容布局
        ((FrameLayout) findViewById(android.R.id.content)).addView(roomNumberTextView, params);
    }

    /** 从Intent中提取房间号 */
    private String getRoomNumberFromIntent() {
        try {
            // 从Intent中获取会议配置
            JitsiMeetConferenceOptions options = getIntent().getParcelableExtra("JitsiMeetConferenceOptions");
            if (options != null && options.getServerURL() != null) {
               //String meetingUrl = options.getServerURL().toString();
                String meetingUrl = options.getRoom().toString();
                // 解析URL获取房间号（Jitsi房间号通常是URL的最后一部分）
                if (meetingUrl.contains("/")) {
                    return meetingUrl.substring(meetingUrl.lastIndexOf("/") + 1);
                }
                return meetingUrl;
            }
        } catch (Exception e) {
            Log.e(TAG, "获取房间号失败", e);
        }
        return "000000";
    }

    /** dp转px工具方法 */
    private int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    /** 初始化会议管理器 */
    private void initMeetingManager() {
        mMeetingManager = new JitsiMeetingManager(this, new JitsiMeetingManager.MeetingCallback() {
            @Override
            public void onConferenceJoined(String url,long start) {
                Log.i(TAG, "会议加入成功: " + url);
                // 传递JitsiView引用给管理器
                startTime = start;
                if (mJitsiView != null) {
                    mMeetingManager.setJitsiView(mJitsiView);
                }
            }

            @Override
            public void onConferenceTerminated(String error, long startTime) {
                Log.i(TAG, "会议结束: " + error);
                long duration = System.currentTimeMillis() - startTime;
                overCall_(duration, CallConstants.Video, getIntent().getStringExtra("touserid"), JitsiActivity.this);
                finish();
            }

            @Override
            public void onConferenceFailed(String error) {
                Log.e(TAG, "会议失败: " + error);
                showToast("会议失败: " + error);
                finish();
            }

            @Override
            public void onParticipantLeft(String error) {
                Log.i(TAG, "参与者离开: " + error);
                showToast(error);
                // 发送挂断广播
                Intent hangupIntent = BroadcastIntentHelper.buildHangUpIntent();
                LocalBroadcastManager.getInstance(JitsiActivity.this).sendBroadcast(hangupIntent);


            }
        });
    }

    /** 添加挂断按钮 */
    private void addHangupButton() {
        Button hangupBtn = new Button(this);
        hangupBtn.setText("挂断");
        hangupBtn.setBackgroundColor(0xFFFF4444);
        hangupBtn.setTextColor(0xFFFFFFFF);
        hangupBtn.setOnClickListener(v -> {
            if (mMeetingManager != null) {
                mMeetingManager.leaveMeeting();
            }
            finish();
        });

        // 添加到布局底部
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = 60; // 底部边距
        ((FrameLayout) findViewById(android.R.id.content)).addView(hangupBtn, params);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                showToast("调试模式需要悬浮窗权限");
                finish();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_MENU) {
            JitsiMeet.showDevOptions();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /** EventBus：接收参与者离开事件 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onParticipantLeftEvent(MesssageEventParticipantLeft event) {
        finish();
    }

    /** 显示Toast */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** 处理通话结束（统一实现） */
    public static void overCall_(long duration, int callType, String toUserId, Context ctx) {
        JitsistateMachine.isInCalling = false;
        if (ctx == null) return;

        String callTypeStr;
        int messageType;
        switch (callType) {
            case CallConstants.Audio:
                messageType = XmppMessage.TYPE_END_CONNECT_VOICE;
                callTypeStr = ctx.getString(R.string.voice_call);
                break;
            case CallConstants.Video:
                messageType = XmppMessage.TYPE_END_CONNECT_VIDEO;
                callTypeStr = ctx.getString(R.string.video_call);
                break;
            case CallConstants.Screen:
                messageType = XmppMessage.TYPE_END_CONNECT_SCREEN;
                callTypeStr = ctx.getString(R.string.screen_call);
                break;
            default:
                return;
        }

        String content = ctx.getString(R.string.sip_canceled) + " " + callTypeStr;
        EventBus.getDefault().post(new MessageEventCancelOrHangUp(messageType, toUserId, content, duration/1000));
    }
// 记录当前是否处于PiP模式
    private  long startTime;
    private boolean mIsInPiP = false;
    // 标记：PiP关闭后是否已回到前台
    private boolean mHasReturnedToForeground = false;
    // 记录PiP关闭时间（用于判断前台切换时效）
    private long mPiPClosedTime = 0;
    // 前台切换的有效时间窗（如1秒内视为主动回退）
    private static final int RETURN_TIMEOUT_MS = 3000;
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        mIsInPiP = isInPictureInPictureMode;

        if (!isInPictureInPictureMode) {
            // PiP悬浮窗已关闭
            mPiPClosedTime = System.currentTimeMillis();
            mHasReturnedToForeground = false; // 重置前台标记
            Log.i("JitsiPiP", "PiP关闭，等待前台状态...");

            // 延迟判断：若超过有效时间未回到前台，视为“关闭会议”
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!mHasReturnedToForeground) {
                    // 未回到前台→执行关闭会议逻辑
                    endMeetingIfNeeded();
                }
            }, RETURN_TIMEOUT_MS);
        } else {
            // 进入PiP模式→隐藏非必要UI
           // hideNonMeetingUI();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // 判断是否在PiP关闭后的有效时间内回到前台
        long timeSincePiPClosed = System.currentTimeMillis() - mPiPClosedTime;

        Log.i("JitsiPiP", "用户回退到主Activity，onResume" + timeSincePiPClosed);
        if (timeSincePiPClosed < RETURN_TIMEOUT_MS && !mIsInPiP) {
            // 在有效时间内→视为“回退到主Activity”
            mHasReturnedToForeground = true;
            Log.i("JitsiPiP", "用户回退到主Activity，恢复UI");
           // restoreFullMeetingUI(); // 恢复完整会议UI
        }
    }

    private void endMeetingIfNeeded() {
        // 业务判断：若会议仍在运行，且用户未回退，则终止
        if (!mHasReturnedToForeground) {
            Log.i("JitsiPiP", "用户回退到主Activity，endMeeting");
            Intent hangupIntent = BroadcastIntentHelper.buildHangUpIntent();
            LocalBroadcastManager.getInstance(JitsiActivity.this).sendBroadcast(hangupIntent);
        }
    }

    // 辅助方法：判断会议是否活跃（根据Jitsi SDK状态）
    private boolean isMeetingActive() {
        // Jitsi MeetView的状态可通过反射或SDK提供的接口获取
        // 示例：假设SDK有isJoined()方法
        try {
            Method isJoinedMethod = mJitsiView.getClass().getMethod("isJoined");
            return (boolean) isJoinedMethod.invoke(mJitsiView);
        } catch (Exception e) {
            return false; // 反射失败默认视为非活跃
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // 清理会议管理器
        if (mMeetingManager != null) {
            mMeetingManager.destroy();
            mMeetingManager = null;
        }
        mJitsiView = null; // 释放视图引用
//        if (isInPiP) {
//            // 触发“关闭悬浮窗”事件：从PiP模式关闭了窗口
//            Log.d("PiPEvent", "关闭悬浮窗");
//            // 执行清理操作（如停止视频播放、释放资源）
//            //releaseResources();
//
//            long duration = System.currentTimeMillis() - startTime;
//            overCall_(duration, CallConstants.Video, getIntent().getStringExtra("touserid"), JitsiActivity.this);
//        }

        if (addParticipantBtn != null) {
            ((FrameLayout) findViewById(android.R.id.content)).removeView(addParticipantBtn);
            addParticipantBtn = null;
        }
    }
}