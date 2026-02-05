package com.oort.weichat.call;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.oort.weichat.MyApplication;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AudioOrVideoController01 {
    private static AudioOrVideoController01 instance;
    private Context mContext;
    private CoreManager mCoreManager;

    private AudioOrVideoController01(Context context, CoreManager coreManager) {
        this.mContext = context;
        this.mCoreManager = coreManager;
        EventBus.getDefault().register(this);
        // 初始化通知渠道
        CallNotificationHelper.createNotificationChannel(context);
    }

    public static AudioOrVideoController01 init(Context context, CoreManager coreManager) {
        if (instance == null) {
            instance = new AudioOrVideoController01(context, coreManager);
        }
        return instance;
    }

    // 单聊 通话 来电
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventSipEVent messsage) {
        if (messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_VOICE
                || messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_VIDEO
                || messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_SCREEN) {
            if (!JitsistateMachine.isInCalling) {
               if(true) {


                    int callType = (messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_VOICE)
                            ? CallConstants.Audio : CallConstants.Video;
                    String fromUserId = messsage.touserid;
                    String userName = messsage.message.getFromUserName();
                   String roomId = messsage.message.getRoomId();

                    // 关键：启动前台服务，维持前台状态
                    startForegroundServiceIfNeeded();

                    // 直接启动通话界面（此时应用处于前台状态，系统允许）
                    Intent intent = new Intent(mContext, JitsiIncomingcall.class);
                    intent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
                    intent.putExtra("fromuserid", fromUserId);
                    intent.putExtra("name", userName);
                   intent.putExtra("roomId", roomId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);

                    return;
                }
                int callType;
                if (messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_VOICE) {
                    callType = CallConstants.Audio;
                } else if (messsage.message.getType() == XmppMessage.TYPE_IS_CONNECT_VIDEO) {
                    callType = CallConstants.Video;
                } else {
                    callType = CallConstants.Screen;
                }

                String fromUserId = messsage.touserid;
                String userName = messsage.message.getFromUserName();
                String meetUrl = messsage.message.getFilePath();
                String roomId = messsage.message.getRoomId();

                // 判断应用是否在前台
                boolean isForeground = AppUtils.isAppForeground(MyApplication.getContext());
                if (isForeground) {
                    // 前台：直接启动来电界面
                    startIncomingCallActivity(fromUserId, userName, callType, meetUrl);
                } else {
                    // 后台：发送通知引导用户点击
                    CallNotificationHelper.sendCallNotification(
                            mContext,
                            fromUserId,
                            userName,
                            callType,
                            meetUrl,
                            roomId
                    );
                }
            }
        } else if (messsage.message.getType() == XmppMessage.TYPE_NO_CONNECT_VOICE
                || messsage.message.getType() == XmppMessage.TYPE_NO_CONNECT_VIDEO
                || messsage.message.getType() == XmppMessage.TYPE_NO_CONNECT_SCREEN) {
            Log.e("AVI", "收到对方取消协议");
            if (messsage.message.getTimeLen() == 0) {
                EventBus.getDefault().post(new MessageHangUpPhone(messsage.message));
            }
        }
    }

    // 启动前台服务（如果未启动）
    private void startForegroundServiceIfNeeded() {
        // 检查服务是否已启动（可选，避免重复启动）
        if (!isServiceRunning(CallForegroundService.class)) {
            Intent serviceIntent = new Intent(mContext, CallForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(serviceIntent);
            } else {
                mContext.startService(serviceIntent);
            }
        }
    }

    // 检查服务是否正在运行（工具方法）
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    // 群聊 会议 邀请
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventMeetingInvited event) {
        if (!JitsistateMachine.isInCalling) {
            // 群聊会议同样处理前后台逻辑
            boolean isForeground = AppUtils.isAppForeground(MyApplication.getContext());
            if (isForeground) {
                Intent intent = new Intent(mContext, JitsiIncomingcall.class);
                intent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, event.type);
                intent.putExtra("fromuserid", event.message.getFromUserId());
                intent.putExtra("touserid", event.message.getFromUserId());
                intent.putExtra("name", event.message.getFromUserName());
                intent.putExtra("roomId", event.message.getRoomId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            } else {
                // 群聊会议通知（可复用通知工具类，参数按需调整）
                CallNotificationHelper.sendCallNotification(
                        mContext,
                        event.message.getObjectId(),
                        event.message.getFromUserName(),
                        event.type,
                        null,
                        event.message.getRoomId()

                );
            }
        }
    }

    // 启动来电界面（抽取为方法复用）
    private void startIncomingCallActivity(String fromUserId, String userName, int callType, String meetUrl) {
        Intent intent = new Intent(mContext, JitsiIncomingcall.class);
        intent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
        intent.putExtra("fromuserid", fromUserId);
        intent.putExtra("touserid", fromUserId);
        intent.putExtra("name", userName);
        if (!TextUtils.isEmpty(meetUrl)) {
            intent.putExtra("meetUrl", meetUrl);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void release() {
        EventBus.getDefault().unregister(this);
    }
}
