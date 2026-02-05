package com.oort.weichat.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.util.HashMap;

public class JitsiMeetingManager {

    public interface MeetingCallback {
        void onConferenceJoined(String url,long startTime);
        void onConferenceTerminated(String error, long startTime);
        void onConferenceFailed(String error);
        void onParticipantLeft(String error);
    }

    private final Context context;
    private final MeetingCallback callback;
    private BroadcastReceiver broadcastReceiver;
    private long startTime; // 通话开始时间（会议成功加入时记录）
    private String localParticipantId; // 本地参与者ID
    private JitsiMeetView jitsiView; // 持有Jitsi视图引用（用于主动终止会议）

    public JitsiMeetingManager(Context context, MeetingCallback callback) {
        this.context = context;
        this.callback = callback;
        registerForBroadcastMessages();
    }

    // 启动会议
    public void launchMeeting(JitsiMeetConferenceOptions options) {
        JitsiActivity.launch(context, options);
    }

    // 设置Jitsi视图引用（需从JitsiActivity中传递）
    public void setJitsiView(JitsiMeetView view) {
        this.jitsiView = view;
    }

    // 主动结束会议
    public void leaveMeeting() {
        if (jitsiView != null) {
            jitsiView.abort();
        }
    }

    // 注册广播监听
    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_JOINED.getAction());
        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
        intentFilter.addAction(BroadcastEvent.Type.PARTICIPANT_LEFT.getAction());
       // intentFilter.addAction(BroadcastEvent.Type.Cf.getAction()); // 恢复失败事件监听

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcastEvent(intent);
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
    }

    // 处理广播事件
    private void handleBroadcastEvent(Intent intent) {
        BroadcastEvent event = new BroadcastEvent(intent);
        switch (event.getType()) {
            case CONFERENCE_JOINED:
                // 会议成功加入时记录开始时间
                startTime = System.currentTimeMillis();
                // 获取本地参与者ID（实际项目需根据Jitsi SDK版本调整）
                localParticipantId = (String) event.getData().get("localParticipantId");
                callback.onConferenceJoined(event.getData().get("url").toString(),startTime);
                break;

            case CONFERENCE_TERMINATED:
                Object error = event.getData().get("error");
                callback.onConferenceTerminated(
                        error != null ? error.toString() : null,
                        startTime // 传递正确的开始时间
                );
                destroy(); // 终止后清理资源
                break;

            case PARTICIPANT_LEFT:
                handleParticipantLeft(event.getData());
                break;

//            case CONFERENCE_FAILED:
//                Object failReason = event.getData().get("error");
//                callback.onConferenceFailed(
//                        failReason != null ? failReason.toString() : "会议连接失败"
//                );
//                destroy(); // 失败后清理资源
//                break;
        }
    }

    // 处理参与者离开事件（单聊核心逻辑）
    private void handleParticipantLeft(HashMap<String, Object> data) {
        String leftParticipantId = (String) data.get("participantId");
        // 单聊场景：若离开的不是本地用户，则判定为对方离开
        if (leftParticipantId != null && !leftParticipantId.equals(localParticipantId)) {
            callback.onParticipantLeft("对方已离开会议");
//            Intent hangupIntent = BroadcastIntentHelper.buildHangUpIntent();
//            LocalBroadcastManager.getInstance(CommonApplication.getAppContext()).sendBroadcast(hangupIntent);
           // leaveMeeting(); // 主动终止本地会议
        }
    }

    // 清理资源（必须调用）
    public void destroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        jitsiView = null;
    }
}
