package com.oort.weichat.ui.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.helper.UploadEngine;
import com.oort.weichat.ui.SplashActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.message.InstantMessageConfirm;
import com.oort.weichat.ui.systemshare.ShareBroadCast;
import com.oort.weichat.ui.systemshare.ShareLifeCircleProxyActivity;
import com.oort.weichat.ui.systemshare.ShareNewFriend;
import com.oort.weichat.ui.tool.GroupTool;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.LoadFrame;
import com.oort.weichat.view.MessageAvatar;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.listener.ChatMessageListener;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * 分享 最近联系人
 */
public class ShareToChatActivity extends BaseActivity implements OnClickListener, ChatMessageListener {
    private ListView mShareLv;
    private List<Friend> mFriends;

    private InstantMessageConfirm menuWindow;
    private LoadFrame mLoadFrame;

    private ChatMessage mShareChatMessage;

    private boolean isNeedExecuteLogin;
    private BroadcastReceiver mShareBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(intent.getAction())
                    && intent.getAction().equals(com.oort.weichat.ui.systemshare.ShareBroadCast.ACTION_FINISH_ACTIVITY)) {
                finish();
            }
        }
    };

    public ShareToChatActivity() {
        noLoginRequired();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_share);

        // 判断本地登录状态
        int userStatus = LoginHelper.prepareUser(mContext, coreManager);
        switch (userStatus) {
            case LoginHelper.STATUS_USER_FULL:
            case LoginHelper.STATUS_USER_NO_UPDATE:
            case LoginHelper.STATUS_USER_TOKEN_OVERDUE:
                boolean isConflict = PreferenceUtils.getBoolean(this, Constants.LOGIN_CONFLICT, false);
                if (isConflict) {
                    isNeedExecuteLogin = true;
                }
                break;
            case LoginHelper.STATUS_USER_SIMPLE_TELPHONE:
                isNeedExecuteLogin = true;
                break;
            case LoginHelper.STATUS_NO_USER:
            default:
                isNeedExecuteLogin = true;
        }

        if (isNeedExecuteLogin) {// 需要先执行登录操作
            startActivity(new Intent(mContext, SplashActivity.class));
            finish();
            return;
        }

        coreManager.relogin();// 连接xmpp 发消息需要

        mShareChatMessage = new ChatMessage();
//        if (ShareUtil.shareInit(this, mShareChatMessage)) return;

        Intent in = getIntent();
        if(in.getIntExtra("type",0) > 0){
            mShareChatMessage.setType(in.getIntExtra("type",0));
            ImageLoadHelper.loadFile(mContext, in.getStringExtra("content"), new ImageLoadHelper.FileSuccessCallback() {
                @Override
                public void onSuccess(File f) {


                    mShareChatMessage.setFilePath(f.getAbsolutePath());
                }
            });

        }

        initActionBar();
        loadData();
        initView();

        ListenerManager.getInstance().addChatMessageListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mShareBroadCast, new IntentFilter(ShareBroadCast.ACTION_FINISH_ACTIVITY),Context.RECEIVER_NOT_EXPORTED);
        }
    }


    public static void startShareImageUrl(Context cx,String imgUrl){
        Intent in = new Intent(cx, ShareToChatActivity.class);
        in.putExtra("type",XmppMessage.TYPE_IMAGE);
        in.putExtra("content",imgUrl);
        cx.startActivity(in);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().removeChatMessageListener(this);
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.most_recent_contact));
    }

    private void loadData() {
        mFriends = FriendDao.getInstance().getNearlyFriendMsg(coreManager.getSelf().getUserId());
        for (int i = 0; i < mFriends.size(); i++) {
            if (mFriends.get(i).getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)) {
                mFriends.remove(i);
            }
        }
    }

    private RadioButton rb_single;
    private RadioButton rb_group;
    private void initView() {

        LinearLayout ll = findViewById(R.id.ll_select_p);
        ll.setOnClickListener(this);

        rb_single = findViewById(R.id.rb_single);
        rb_group = findViewById(R.id.rb_group);
        findViewById(R.id.tv_create_newmessage).setOnClickListener(this);
        findViewById(R.id.ll_send_life_circle).setVisibility(View.GONE);

        JSONObject json = null;

        if(mShareChatMessage.getContent() != null) {
            try {
                json = new JSONObject(mShareChatMessage.getContent());
                String mType = json.getString("type");
                if (mType.equals("dynamic")) {
                    findViewById(R.id.ll_send_life_circle).setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        findViewById(R.id.tv_send_life_circle).setOnClickListener(this);

        mShareLv = findViewById(R.id.lv_recently_message);
        mShareLv.setAdapter(new MessageRecentlyAdapter());
        mShareLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Friend friend = mFriends.get(position);
                showPopuWindow(view, friend);
            }
        });
    }

    private void showPopuWindow(View view, Friend friend) {
        if (menuWindow != null) {
            menuWindow.dismiss();
        }
        menuWindow = new InstantMessageConfirm(this, new ClickListener(friend), friend);
        menuWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onMessageSendStateChange(int messageState, String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        // 更新消息Fragment的广播
        MsgBroadcast.broadcastMsgUiUpdate(mContext);
        if (mShareChatMessage != null && TextUtils.equals(mShareChatMessage.getPacketId(), msgId)) {
            if (messageState == ChatMessageListener.MESSAGE_SEND_SUCCESS) {// 发送成功
                if (mLoadFrame != null) {
                    mLoadFrame.change();
                }
            }
        }
    }

    @Override
    public boolean onNewMessage(String fromUserId, ChatMessage message, boolean isGroupMsg) {
        return false;
    }

    /**
     * 事件的监听
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_select_p) {
            GroupTool tool = new GroupTool();


            if (rb_single.isChecked()) {
                tool.setType(1);

                tool.ceateRoom(ShareToChatActivity.this, coreManager);
                tool.setSuc(new GroupTool.CreateSuc() {
                    @Override
                    public void suc(Friend friend, boolean finish) {


                        //EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
                        //forwardingStep(friend,finish);

                        share(friend);
                        //sendMessage(friend);
                        //finish();
                    }
                });

            } else {
                tool.ceateRoom(ShareToChatActivity.this, coreManager);
                tool.setSuc(new GroupTool.CreateSuc() {
                    @Override
                    public void suc(Friend friend, boolean isFinish) {


                        //EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
                        //forwardingStep(friend, isFinish);
                        //finish();


                        //sendMessage(friend);
                        share(friend);
                    }
                });
            }
        } else if (id == R.id.tv_create_newmessage) {
            verificationShare(1, null);
        } else if (id == R.id.tv_send_life_circle) {
            verificationShare(2, null);
        }
    }

    private void verificationShare(final int type, Friend friend) {
        if (type == 1) {// 选择好友
            Intent intent = getIntent();
            intent.setClass(ShareToChatActivity.this, ShareNewFriend.class);
            startActivity(intent);
        } else if (type == 2) {// 生活圈
            Intent intent = getIntent();
            intent.setClass(ShareToChatActivity.this, ShareLifeCircleProxyActivity.class);
            startActivity(intent);
        } else {// 直接发送
            share(friend);
        }
    }

    String noteTitle;
    public void share(Friend friend) {
        if (friend.getRoomFlag() != 0) {
            if (friend.getRoomTalkTime() > (System.currentTimeMillis() / 1000)) {// 禁言时间 > 当前时间 禁言还未结束
                DialogHelper.tip(mContext, getString(R.string.tip_forward_ban));
                return;
            } else if (friend.getGroupStatus() == 1) {
                DialogHelper.tip(mContext, getString(R.string.tip_forward_kick));
                return;
            } else if (friend.getGroupStatus() == 2) {
                DialogHelper.tip(mContext, getString(R.string.tip_forward_disbanded));
                return;
            } else if ((friend.getGroupStatus() == 3)) {
                DialogHelper.tip(mContext, getString(R.string.tip_group_disable_by_service));
                return;
            }
        }

        mLoadFrame = new LoadFrame(ShareToChatActivity.this);


        noteTitle = getString(R.string.done);//getString(R.string.open_im, getString(R.string.app_name));
        if(mShareChatMessage.getContent() != null) {
            try {
                JSONObject json = new JSONObject(mShareChatMessage.getContent());
                String mType = json.getString("type");
                if (mType.equals("dynamic")) {
                    noteTitle = getString(R.string.done);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mLoadFrame.setSomething(getString(R.string.back_last_page), noteTitle, new LoadFrame.OnLoadFrameClickListener() {
            @Override
            public void cancelClick() {
                if (DeviceInfoUtil.isOppoRom()) {
                    // 调试发现OPPO手机被调起后当前界面不会自动回到后台，手动调一下
                    moveTaskToBack(true);
                }
                finish();
            }

            @Override
            public void confirmClick() {

                if(noteTitle.equals("完成")){
                    finish();
                    return;
                }
//                startActivity(new Intent(ShareToChatActivity.this, MainActivity.class));
//                finish();
            }
        });
        mLoadFrame.show();

        mShareChatMessage.setFromUserId(coreManager.getSelf().getUserId());
        mShareChatMessage.setFromUserName(coreManager.getSelf().getNickName());
        mShareChatMessage.setToUserId(friend.getUserId());
        mShareChatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        mShareChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        ChatMessageDao.getInstance().saveNewSingleChatMessage(coreManager.getSelf().getUserId(), friend.getUserId(), mShareChatMessage);
        switch (mShareChatMessage.getType()) {
            case XmppMessage.TYPE_TEXT:
            case XmppMessage.TYPE_IMAGE_TEXT:
            case XmppMessage.TYPE_APP_REVIEW:
            case XmppMessage.TYPE_VIDEO_DISPATCH:
                sendMessage(friend);
                break;
            case XmppMessage.TYPE_IMAGE:
            case XmppMessage.TYPE_VIDEO:
            case XmppMessage.TYPE_FILE:
                if (!mShareChatMessage.isUpload()) {// 未上传
                    UploadEngine.uploadImFile(IMUserInfoUtil.getInstance().getToken(), coreManager.getSelf().getUserId(), friend.getUserId(), mShareChatMessage, new UploadEngine.ImFileUploadResponse() {
                        @Override
                        public void onSuccess(String toUserId, ChatMessage message) {
                            sendMessage(friend);
                        }

                        @Override
                        public void onFailure(String toUserId, ChatMessage message) {
                            mLoadFrame.dismiss();
                            ToastUtil.showToast(ShareToChatActivity.this, getString(R.string.upload_failed));
                        }
                    });
                } else {// 已上传 自定义表情默认为已上传
                    sendMessage(friend);
                }
                break;
            default:
                Reporter.unreachable();
        }
    }

    private void sendMessage(Friend friend) {
        if (friend.getRoomFlag() == 1) {
            coreManager.sendMucChatMessage(friend.getUserId(), mShareChatMessage);
        } else {
            coreManager.sendChatMessage(friend.getUserId(), mShareChatMessage);
        }
    }

    class ClickListener implements OnClickListener {
        private Friend friend;

        ClickListener(Friend friend) {
            this.friend = friend;
        }

        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            int id = v.getId();
            if (id == R.id.btn_send) {
                verificationShare(3, friend);
            } else if (id == R.id.btn_cancle) {
            }
        }
    }

    class MessageRecentlyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mFriends != null) {
                return mFriends.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mFriends != null) {
                return mFriends.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (mFriends != null) {
                return position;
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(ShareToChatActivity.this, R.layout.item_recently_contacts, null);
                holder = new ViewHolder();
                holder.mIvHead = convertView.findViewById(R.id.iv_recently_contacts_head);
                holder.mTvName = convertView.findViewById(R.id.tv_recently_contacts_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Friend friend = mFriends.get(position);
            holder.mIvHead.fillData(friend);
            holder.mTvName.setText(TextUtils.isEmpty(friend.getRemarkName()) ? friend.getNickName() : friend.getRemarkName());
            return convertView;
        }
    }

    class ViewHolder {
        MessageAvatar mIvHead;
        TextView mTvName;
    }
}
