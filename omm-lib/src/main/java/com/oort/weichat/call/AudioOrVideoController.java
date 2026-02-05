package com.oort.weichat.call;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AudioOrVideoController {
    private static final String TAG = "CallController";
    private static AudioOrVideoController instance;
    private Context mContext;
    private CoreManager mCoreManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private AudioOrVideoController(Context context, CoreManager coreManager) {
        this.mContext = context;
        this.mCoreManager = coreManager;
        EventBus.getDefault().register(this);
        CallNotificationHelper.createNotificationChannel(context);
    }

    public static AudioOrVideoController init(Context context, CoreManager coreManager) {
        if (instance == null) {
            instance = new AudioOrVideoController(context, coreManager);
        }
        return instance;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventSipEVent messsage) {
        if (isCallType(messsage.message.getType())) {
            if (!JitsistateMachine.isInCalling) {
                Log.d(TAG, "收到通话请求，当前应用状态：" + (AppUtils.isAppForeground(mContext) ? "前台" : "后台"));
                int callType = getCallType(messsage.message.getType());
                String fromUserId = messsage.message.getFromUserId();
                String userName = messsage.message.getFromUserName();
                String meetUrl = "";//messsage.message.getFilePath();
                String roomId = messsage.message.getRoomId();
                // 构建包含通话参数的Intent（用于服务传递）
                Intent serviceIntent = new Intent(mContext, CallForegroundService.class);
                serviceIntent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
                serviceIntent.putExtra("fromuserid", fromUserId);
                serviceIntent.putExtra("name", userName);
                serviceIntent.putExtra("meetUrl", meetUrl);
                serviceIntent.putExtra("roomId", roomId);

                // 启动前台服务并传递参数
                startForegroundServiceWithParams(serviceIntent, () -> {
                    // 服务启动后，双重保险：直接启动界面
                    startIncomingCallActivity(fromUserId, userName, callType, meetUrl,roomId);
                });
            }
        }
    }

    // 启动前台服务并传递通话参数
    private void startForegroundServiceWithParams(Intent serviceIntent, Runnable onSuccess) {
        if (!isServiceRunning(CallForegroundService.class)) {
            Log.d(TAG, "启动前台服务并传递参数");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(serviceIntent);
            } else {
                mContext.startService(serviceIntent);
            }

            // 延迟1秒确保服务就绪（桌面后台需要更长时间）
            mainHandler.postDelayed(() -> {
                if (isServiceRunning(CallForegroundService.class)) {
                    Log.d(TAG, "服务启动成功，执行双重启动");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "服务启动失败，使用通知兜底");
                    CallNotificationHelper.sendCallNotification(
                            mContext,
                            serviceIntent.getStringExtra("fromuserid"),
                            serviceIntent.getStringExtra("name"),
                            serviceIntent.getIntExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, 0),
                            serviceIntent.getStringExtra("meetUrl"),
                            serviceIntent.getStringExtra("roomId")
                    );
                }
            }, 1000); // 桌面后台启动较慢，延长延迟
        } else {
            Log.d(TAG, "服务已运行，直接启动界面");
            onSuccess.run();
        }
    }

    // 启动通话界面（强化Flags）
    private void startIncomingCallActivity(String fromUserId, String userName, int callType, String meetUrl,String roomId) {
        Intent intent = new Intent(mContext, JitsiIncomingcall.class);
        intent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
        intent.putExtra("fromuserid", fromUserId);
        intent.putExtra("touserid", fromUserId);
        intent.putExtra("name", userName);
        intent.putExtra("meetUrl", meetUrl);
        intent.putExtra("roomId", roomId);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT // 新增：强制拉到前台
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );
        mContext.startActivity(intent);
        Log.d(TAG, "已发送Activity启动命令，Flags：" + intent.getFlags());
    }

    // 检查服务是否运行
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // 辅助方法：判断是否为通话类型
    private boolean isCallType(int type) {
        return type == XmppMessage.TYPE_IS_CONNECT_VOICE
                || type == XmppMessage.TYPE_IS_CONNECT_VIDEO
                || type == XmppMessage.TYPE_IS_MU_CONNECT_VIDEO
                || type == XmppMessage.TYPE_IS_MU_CONNECT_VOICE
                || type == XmppMessage.TYPE_IS_CONNECT_SCREEN;
    }

    // 转换通话类型
    private int getCallType(int xmppType) {
        if (xmppType == XmppMessage.TYPE_IS_CONNECT_VOICE) return CallConstants.Audio;
        if (xmppType == XmppMessage.TYPE_IS_CONNECT_VIDEO) return CallConstants.Video;
        if (xmppType == XmppMessage.TYPE_IS_MU_CONNECT_VIDEO) return CallConstants.Video_Meet;
        if (xmppType == XmppMessage.TYPE_IS_MU_CONNECT_VOICE) return CallConstants.Audio_Meet;
        return CallConstants.Screen;
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        mainHandler.removeCallbacksAndMessages(null);
    }

    // 群聊 会议 邀请
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventMeetingInvited messsage) {
        if (!JitsistateMachine.isInCalling) {
            if (!JitsistateMachine.isInCalling) {
                Log.d(TAG, "收到通话请求，当前应用状态：" + (AppUtils.isAppForeground(mContext) ? "前台" : "后台"));
                int callType = getCallType(messsage.message.getType());
                String roomId = messsage.message.getRoomId();
                String userName = messsage.message.getFromUserName();
                //String meetUrl = messsage.message.getFilePath();

                // 构建包含通话参数的Intent（用于服务传递）
                Intent serviceIntent = new Intent(mContext, CallForegroundService.class);
                serviceIntent.putExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, callType);
                serviceIntent.putExtra("fromuserid", messsage.message.getFromUserId());
                serviceIntent.putExtra("name", userName);
                serviceIntent.putExtra("meetUrl", "");
                serviceIntent.putExtra("roomId", roomId);

                // 启动前台服务并传递参数
                startForegroundServiceWithParams(serviceIntent, () -> {
                    // 服务启动后，双重保险：直接启动界面
                    startIncomingCallActivity(roomId, userName, callType, "",roomId);
                });
            }
        }
    }
}