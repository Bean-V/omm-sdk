package com.oort.weichat.call;

import static com.oort.weichat.R.id.call_answer;
import static com.oort.weichat.R.id.call_avatar;
import static com.oort.weichat.R.id.call_hang_up;
import static com.oort.weichat.R.id.call_invite_type;
import static com.oort.weichat.R.id.call_name;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.xmpp.ListenerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 来电显示界面（支持后台通过通知唤起）
 */
public class JitsiIncomingcall extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "JitsiIncomingcall";
    private Timer timer;
    private String mLoginUserId;
    private String mLoginUserName;
    private int mCallType;
    private String call_fromUser;
    private String call_toUser;
    private String call_Name;
    private String meetUrl;
    private String roomId;
    private AnimationDrawable talkingRippleDrawable;
    private AssetFileDescriptor mAssetFileDescriptor;
    private MediaPlayer mediaPlayer;
    private ImageView mInviteAvatar;
    private TextView mInviteName;
    private TextView mInviteInfo;
    private ImageButton mAnswer; // 接听
    private ImageButton mHangUp; // 挂断
    private boolean isAllowBack = false;

    // 超时任务（30秒未响应自动挂断）
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(() -> {
                abort();
                if (CallConstants.isSingleChat(mCallType)) {
                    sendHangUpMessage(); // 发送超时挂断消息
                }
                JitsistateMachine.reset();
                finish();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 关键窗口配置：解锁屏幕、锁屏显示、点亮屏幕、保持常亮


//        Log.d("JitsiIncomingcall", "界面启动，来源：" + (isFromNotification() ? "通知" : "直接启动"));

        // 强制设置为最高优先级窗口
        //getWindow().setpro(WindowManager.LayoutParams.PRIORITY_PHONE); // 通话级优先级
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.view_call_trying);
        // 取消通知（用户已通过通知进入界面，移除通知栏提示）
        CallNotificationHelper.cancelNotification(this);
        initData();
        initView();
        startTimeoutTimer(); // 启动超时计时器
        EventBus.getDefault().register(this);
        setSwipeBackEnable(false); // 禁止滑动返回
    }

    // 判断是否从通知启动（辅助调试）
    private boolean isFromNotification() {
        return getIntent().hasExtra("from_notification"); // 可在通知Intent中添加此标记
    }
    private void initData() {
        mLoginUserId = coreManager.getSelf().getUserId();
        mLoginUserName = coreManager.getSelf().getNickName();

        // 获取来电参数
        mCallType = getIntent().getIntExtra(CallConstants.AUDIO_OR_VIDEO_OR_MEET, 0);
        call_fromUser = getIntent().getStringExtra("fromuserid");
        call_toUser = getIntent().getStringExtra("touserid");
        call_Name = getIntent().getStringExtra("name");
        meetUrl = getIntent().getStringExtra("meetUrl");

        roomId = getIntent().getStringExtra("roomId");

        // 更新通话状态
        JitsistateMachine.isInCalling = true;
        JitsistateMachine.callingOpposite = call_toUser;

        playRingtone(); // 播放来电铃声
    }

    private void initView() {
        // 绑定控件
        findViewById(R.id.change).setOnClickListener(this);
        findViewById(R.id.replay_message).setOnClickListener(this);
        ImageView ivTalkingRipple = findViewById(R.id.ivTalkingRipple);
        talkingRippleDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.talk_btn_frame_busy_ripple);
        ivTalkingRipple.setImageDrawable(talkingRippleDrawable);
        talkingRippleDrawable.start();

        mInviteAvatar = findViewById(call_avatar);
        mInviteName = findViewById(call_name);
        mInviteInfo = findViewById(call_invite_type);
        mAnswer = findViewById(call_answer);
        mHangUp = findViewById(call_hang_up);

        // 设置头像和名称
        AvatarHelper.getInstance().displayAvatar(call_toUser, mInviteAvatar, true);
        mInviteName.setText(call_Name);

        // 根据通话类型显示不同提示
        setCallTypeInfo();

        // 绑定按钮事件
        mAnswer.setOnClickListener(this);
        mHangUp.setOnClickListener(this);
    }

    /**
     * 根据通话类型设置提示信息和按钮显示
     */
    private void setCallTypeInfo() {
        if (mCallType == CallConstants.Audio) {
            mInviteInfo.setText(getString(R.string.suffix_invite_you_voice));
            findViewById(R.id.rlReplayMessage).setVisibility(View.VISIBLE);
            findViewById(R.id.rlChange).setVisibility(View.VISIBLE);
            ImageView ivChange = findViewById(R.id.change);
            ivChange.setBackgroundResource(R.mipmap.switching_video_call);
            TextView tvChange = findViewById(R.id.change_tv);
            tvChange.setText(R.string.btn_meet_type_change_to_video);
        } else if (mCallType == CallConstants.Video) {
            mInviteInfo.setText(getString(R.string.suffix_invite_you_video));
            findViewById(R.id.rlReplayMessage).setVisibility(View.VISIBLE);
            findViewById(R.id.rlChange).setVisibility(View.VISIBLE);
        } else if (mCallType == CallConstants.Screen) {
            mInviteInfo.setText(getString(R.string.suffix_invite_you_screen));
        } else if (mCallType == CallConstants.Audio_Meet) {
            mInviteInfo.setText(getString(R.string.tip_invite_voice_meeting));
        } else if (mCallType == CallConstants.Video_Meet) {
            mInviteInfo.setText(getString(R.string.tip_invite_video_meeting));
        } else if (mCallType == CallConstants.Talk_Meet) {
            mInviteInfo.setText(getString(R.string.tip_invite_talk_meeting));
        } else if (mCallType == CallConstants.Screen_Meet) {
            mInviteInfo.setText(getString(R.string.tip_invite_screen_meeting));
        }
    }

    /**
     * 启动超时计时器（30秒）
     */
    private void startTimeoutTimer() {
        timer = new Timer();
        timer.schedule(timerTask, 30000); // 30秒后执行
    }

    /**
     * 播放来电铃声
     */
    private void playRingtone() {
        try {
            mAssetFileDescriptor = getAssets().openFd("dial.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(
                    mAssetFileDescriptor.getFileDescriptor(),
                    mAssetFileDescriptor.getStartOffset(),
                    mAssetFileDescriptor.getLength()
            );
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true); // 循环播放
        } catch (IOException e) {
            Log.e(TAG, "播放铃声失败: " + e.getMessage());
            Reporter.post("来电铃声播放异常", e);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.change) { // 语音/视频切换并接听
            abort();
            if (coreManager.isLogin()) {
                if (CallConstants.isSingleChat(mCallType)) {
                    sendAnswerMessage(true); // 通知对方切换类型
                }
                // 切换通话类型
                mCallType = (mCallType == CallConstants.Audio) ? CallConstants.Video : CallConstants.Audio;
                Jitsi_connecting_second.start(this, call_fromUser, call_toUser, mCallType, meetUrl, true,roomId);
                finish();
            }
        } else if (id == R.id.replay_message) { // 快速回复
            showQuickReplyDialog();
        } else if (id == call_answer) { // 接听
            abort();
            if (coreManager.isLogin()) {
                if (CallConstants.isSingleChat(mCallType)) {
                    sendAnswerMessage(false); // 正常接听
                }
                Jitsi_connecting_second.start(this, call_fromUser, call_toUser, mCallType, meetUrl, true,roomId);
                finish();
            }
        } else if (id == call_hang_up) { // 挂断
            abort();
            if (coreManager.isLogin() && CallConstants.isSingleChat(mCallType)) {
                sendHangUpMessage(); // 通知对方已挂断
            }
            JitsistateMachine.reset();
            finish();
        }
    }

    /**
     * 显示快速回复对话框
     */
    private void showQuickReplyDialog() {
        Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_meet_replay, null);
        bottomDialog.setContentView(contentView);
        // 设置对话框宽度为屏幕宽度
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        // 取消按钮
        contentView.findViewById(R.id.dialog_select_cancel).setOnClickListener(v -> bottomDialog.dismiss());

        // 跳转聊天按钮
        contentView.findViewById(R.id.tvHangUpChat).setOnClickListener(v -> {
            ChatActivity.start(this, FriendDao.getInstance().getFriend(mLoginUserId, call_toUser));
            bottomDialog.dismiss();
            mHangUp.callOnClick(); // 触发挂断
        });

        // 快速回复列表
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.full_divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Item> data = new ArrayList<>();
        data.add(new Item(getString(R.string.default_quick_replay_1)));
        data.add(new Item(getString(R.string.default_quick_replay_2)));
        data.add(new Item(getString(R.string.default_quick_replay_3)));
        recyclerView.setAdapter(new MessageReplayAdapter(this, data, item -> {
            sendReplay(item.message);
            bottomDialog.dismiss();
            mHangUp.callOnClick(); // 发送后挂断
        }));
    }

    /**
     * 发送快速回复消息
     */
    private void sendReplay(String text) {
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_TEXT);
        message.setContent(text);
        message.setFromUserId(mLoginUserId);
        message.setToUserId(call_toUser);
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setFromUserName(mLoginUserName);
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());

        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, call_toUser, message)) {
            ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, call_toUser, message, false);
        }
        coreManager.sendChatMessage(call_toUser, message);
        MsgBroadcast.broadcastMsgUiUpdate(this); // 更新消息界面
    }

    /**
     * 发送接听消息
     */
    private void sendAnswerMessage(boolean isChanged) {
        ChatMessage message = new ChatMessage();
        if (mCallType == CallConstants.Audio) {
            message.setType(XmppMessage.TYPE_CONNECT_VOICE);
        } else if (mCallType == CallConstants.Video) {
            message.setType(XmppMessage.TYPE_CONNECT_VIDEO);
        } else if (mCallType == CallConstants.Screen) {
            message.setType(XmppMessage.TYPE_CONNECT_SCREEN);
        }
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginUserName);
        message.setToUserId(call_toUser);
        message.setContent(isChanged ? "1" : "0"); // 1表示切换类型，0表示正常接听
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        coreManager.sendChatMessage(call_toUser, message);
    }

    /**
     * 发送挂断消息
     */
    private void sendHangUpMessage() {
        ChatMessage message = new ChatMessage();
        if (mCallType == CallConstants.Audio) {
            message.setType(XmppMessage.TYPE_NO_CONNECT_VOICE);
        } else if (mCallType == CallConstants.Video) {
            message.setType(XmppMessage.TYPE_NO_CONNECT_VIDEO);
        } else if (mCallType == CallConstants.Screen) {
            message.setType(XmppMessage.TYPE_NO_CONNECT_SCREEN);
        }
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginUserName);
        message.setToUserId(call_toUser);
        message.setMySend(true);
        message.setTimeLen(1); // 非0表示接电话方挂断
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        message.setContent(getString(R.string.sip_refused));

        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, call_toUser, message)) {
            ListenerManager.getInstance().notifyNewMesssage(mLoginUserId, call_toUser, message, false);
        }
        coreManager.sendChatMessage(call_toUser, message);
        MsgBroadcast.broadcastMsgUiUpdate(this); // 更新消息界面
    }

    /**
     * 停止铃声和计时器
     */
    private void abort() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "停止铃声异常: " + e.getMessage());
                Reporter.post("停止来电铃声失败", e);
            }
            mediaPlayer = null;
        }
        if (talkingRippleDrawable != null) {
            talkingRippleDrawable.stop();
        }

        CallForegroundService.stop(this);
    }

    /**
     * 接收对方挂断事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHangUpEvent(MessageHangUpPhone message) {
        // 过滤过期消息
        if (message.chatMessage.getTimeSend() < JitsistateMachine.lastTimeInComingCall) {
            Log.e(TAG, "忽略过期挂断消息");
            return;
        }
        // 验证是否是当前通话对象的消息
        if (message.chatMessage.getFromUserId().equals(call_toUser)
                || message.chatMessage.getFromUserId().equals(mLoginUserId)) {
            abort();
            JitsistateMachine.reset();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (isAllowBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        abort(); // 确保资源释放
        if (mAssetFileDescriptor != null) {
            try {
                mAssetFileDescriptor.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭资源异常: " + e.getMessage());
            }
        }
        EventBus.getDefault().unregister(this); // 注销事件总线
    }

    // 快速回复相关内部类
    interface OnReplayListener {
        void onReplay(Item item);
    }

    static class MessageReplayAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final Context ctx;
        private final OnReplayListener listener;
        private final List<Item> data;

        public MessageReplayAdapter(Context ctx, List<Item> data, OnReplayListener listener) {
            this.ctx = ctx;
            this.listener = listener;
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(ctx).inflate(R.layout.item_dialog_meet_replay, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Item item = data.get(i);
            viewHolder.itemView.setOnClickListener(v -> listener.onReplay(item));
            viewHolder.tvMessage.setText(item.message);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage = itemView.findViewById(R.id.tvMessage);

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class Item {
        String message;

        public Item(String message) {
            this.message = message;
        }
    }
}