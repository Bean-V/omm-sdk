package com.oort.weichat.ui.message;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.RoomMember;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.MucRoomMember;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.RoomMemberDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.TrillStatisticsHelper;
import com.oort.weichat.sortlist.BaseSortModel;
import com.oort.weichat.sortlist.SortHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.tool.ButtonColorChange;
import com.oort.weichat.ui.tool.GroupTool;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DisplayUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.HeadView;
import com.oort.weichat.view.HorizontalListView;
import com.oort.weichat.view.MessageAvatar;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

/**
 * 转发 最近联系人
 */
public class InstantMessageActivity extends BaseActivity implements OnClickListener {
    private TextView mCreateChat;
    private ListView mLvRecentlyMessage;
    private MessageRecentlyAdapter messageRecentlyAdapter;
    private List<Friend> friends;
    private List<BaseSortModel<Friend>> mSortFriends;

    private HorizontalListView mHorizontalListView;
    private HorListViewAdapter mHorAdapter;
    private List<String> mSelectPositions;
    private Button mOkBtn;
    private InstantMessageConfirmNew menuWindow;

    private boolean isMoreSelected; // 是否为多选转发
    private boolean isSingleOrMerge; // 逐条还是合并转发
    // 在ChatActivity || MucChatActivity内，通过toUserId与messageId获得该条转发消息 在进行封装
    private String toUserId;
    private String messageId;

    private String mLoginUserId;
    private RoomMember mRoomMember;
    // 转发数量，当前转发序号
    private int sendSize;
    private int currentSendSerialNumber;
    private TextView mCreateGroupChat;
    private RadioButton rb_single;
    private RadioButton rb_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageinstant);
        isMoreSelected = getIntent().getBooleanExtra(Constants.IS_MORE_SELECTED_INSTANT, false);
        isSingleOrMerge = getIntent().getBooleanExtra(Constants.IS_SINGLE_OR_MERGE, false);
        // 在ChatContentView内长按转发才需要以下参数
        toUserId = getIntent().getStringExtra("fromUserId");
        messageId = getIntent().getStringExtra("messageId");

        mSortFriends = new ArrayList<>();
        mSelectPositions = new ArrayList<>();

        mLoginUserId = coreManager.getSelf().getUserId();
        initActionBar();
        loadData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.most_recent_contact));
    }

    private void loadData() {
        friends = FriendDao.getInstance().getNearlyFriendMsg(coreManager.getSelf().getUserId());
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)
                    || friends.get(i).getUserId().equals(Friend.ID_SK_PAY)) {
                friends.remove(i);
            }
        }
        loadDataFriend(friends);
    }

    @SuppressLint("StringFormatMatches")
    private void initView() {
//        mCreateChat = findViewById(R.id.tv_create_newmessage);
//        mCreateChat.setOnClickListener(this);

        LinearLayout ll = findViewById(R.id.ll_select_p);
        ll.setOnClickListener(this);

        rb_single = findViewById(R.id.rb_single);
        rb_group = findViewById(R.id.rb_group);

        mLvRecentlyMessage = findViewById(R.id.lv_recently_message);
        messageRecentlyAdapter = new MessageRecentlyAdapter();
        mLvRecentlyMessage.setAdapter(messageRecentlyAdapter);
        mLvRecentlyMessage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Friend friend;
                friend = mSortFriends.get(position).bean;
                for (int i = 0; i < mSortFriends.size(); i++) {
                    if (mSortFriends.get(i).getBean().getUserId().equals(friend.getUserId())) {
                        if (friend.getStatus() != 100) {
                            friend.setStatus(100);
                            mSortFriends.get(i).getBean().setStatus(100);
                            addSelect(friend.getUserId());
                        } else {
                            friend.setStatus(101);
                            mSortFriends.get(i).getBean().setStatus(101);
                            removeSelect(friend.getUserId());
                        }
                    }
                    messageRecentlyAdapter.setData(mSortFriends);
                }
            }
        });

        mHorAdapter = new HorListViewAdapter();
        mHorizontalListView = (HorizontalListView) findViewById(R.id.horizontal_list_view);
        mHorizontalListView.setAdapter(mHorAdapter);
        mHorAdapter.notifyDataSetChanged();
        mOkBtn = (Button) findViewById(R.id.ok_btn);
        ButtonColorChange.colorChange(mContext, mOkBtn);
        mOkBtn.setText(getString(R.string.add_chat_ok_btn, mSelectPositions.size()));

        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                for (int i = 0; i < mSortFriends.size(); i++) {
                    if (mSortFriends.get(i).getBean().getUserId().equals(mSelectPositions.get(position))) {
                        mSortFriends.get(i).getBean().setStatus(101);
                        messageRecentlyAdapter.setData(mSortFriends);
                    }
                }
                mSelectPositions.remove(position);
                mHorAdapter.notifyDataSetInvalidated();
                mOkBtn.setText(getString(R.string.add_chat_ok_btn, mSelectPositions.size()));
            }
        });

        mOkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopuWindow(v, mSelectPositions);
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    private void addSelect(String userId) {
        mSelectPositions.add(userId);
        mHorAdapter.notifyDataSetInvalidated();
        mOkBtn.setText(getString(R.string.add_chat_ok_btn, mSelectPositions.size()));
    }

    @SuppressLint("StringFormatMatches")
    private void removeSelect(String userId) {
        for (int i = 0; i < mSelectPositions.size(); i++) {
            if (mSelectPositions.get(i).equals(userId)) {
                mSelectPositions.remove(i);
            }
        }
        mHorAdapter.notifyDataSetInvalidated();
        mOkBtn.setText(getString(R.string.add_chat_ok_btn, mSelectPositions.size()));
    }

    private void showPopuWindow(View view, List<String> stringsId) {
        if (menuWindow != null) {
            menuWindow.dismiss();
        }
        List<Friend> friendList = new ArrayList<>();
        for (int i = 0; i < stringsId.size(); i++) {
            Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), stringsId.get(i));
            friendList.add(friend);
        }
        menuWindow = new InstantMessageConfirmNew(InstantMessageActivity.this, new ClickListener(friendList), friendList);
        menuWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_create_newmessage) {
        } else if (id == R.id.ll_select_p) {
            GroupTool tool = new GroupTool();


            if (rb_single.isChecked()) {
                tool.setType(1);

                tool.ceateRoom(InstantMessageActivity.this, coreManager);
                tool.setSuc(new GroupTool.CreateSuc() {
                    @Override
                    public void suc(Friend friend, boolean finish) {


                        //EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
                        forwardingStep(friend, finish);
                        //finish();
                    }
                });

            } else {
                tool.ceateRoom(InstantMessageActivity.this, coreManager);
                tool.setSuc(new GroupTool.CreateSuc() {
                    @Override
                    public void suc(Friend friend, boolean isFinish) {


                        //EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
                        forwardingStep(friend, isFinish);
                        //finish();
                    }
                });
            }
            //            case R.id.tv_create_newgroup:
//                GroupTool tool = new GroupTool();
//
//
//                tool.ceateRoom(InstantMessageActivity.this,coreManager);
//                tool.setSuc(new GroupTool.CreateSuc() {
//                    @Override
//                    public void suc(Friend friend,boolean isFinish) {
//
//
//                        //EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
//                        forwardingStep(friend,isFinish);
//                        //finish();
//                    }
//                });
//                break;
        }
    }

    private void forwardingStep(Friend friend,boolean isFinish) {
        if (isMoreSelected) {// 多选转发 通知多选页面(即多选消息的单聊 || 群聊页面，在该页面获取选中的消息在发送出去)
            EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
        } else {// 普通转发
            if (friend.getRoomFlag() == 0) {// 单聊
                ChatMessage chatMessage = ChatMessageDao.getInstance().findMsgById(mLoginUserId, toUserId, messageId);
                TrillStatisticsHelper.share(this, coreManager, chatMessage);
                chatMessage.setFromUserId(mLoginUserId);
                chatMessage.setFromUserName(coreManager.getSelf().getNickName());
                chatMessage.setToUserId(friend.getUserId());
                chatMessage.setUpload(true);
                chatMessage.setMySend(true);
                chatMessage.setSendRead(false);
                chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, friend.getUserId(), chatMessage);
                coreManager.sendChatMessage(friend.getUserId(), chatMessage);
            } else {  // 群聊
                instantChatMessage(friend, toUserId, messageId);
            }
        }
        if(isFinish) {
            finishThis();
        }
    }

    private void forwardingStep(Friend friend) {
        if (isMoreSelected) {// 多选转发 通知多选页面(即多选消息的单聊 || 群聊页面，在该页面获取选中的消息在发送出去)
            EventBus.getDefault().post(new EventMoreSelected(friend.getUserId(), isSingleOrMerge, friend.getRoomFlag() != 0));
        } else {// 普通转发
            if (friend.getRoomFlag() == 0) {// 单聊
                ChatMessage chatMessage = ChatMessageDao.getInstance().findMsgById(mLoginUserId, toUserId, messageId);
                TrillStatisticsHelper.share(this, coreManager, chatMessage);
                chatMessage.setFromUserId(mLoginUserId);
                chatMessage.setFromUserName(coreManager.getSelf().getNickName());
                chatMessage.setToUserId(friend.getUserId());
                chatMessage.setUpload(true);
                chatMessage.setMySend(true);
                chatMessage.setSendRead(false);
                chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, friend.getUserId(), chatMessage);
                coreManager.sendChatMessage(friend.getUserId(), chatMessage);
            } else {  // 群聊
                instantChatMessage(friend, toUserId, messageId);
            }
        }
        finishThis();
    }

    /**
     * 针对获取群组信息接口回调时，未走到forwardingStep方法内调用
     */
    private void faultTolerance() {
        currentSendSerialNumber++;
        finishThis();
    }

    /**
     * 转发到最后一个结束当前界面
     */
    public void finishThis() {
        if (currentSendSerialNumber == sendSize) {
            if (isMoreSelected) {// 发送多选收藏的event，但此event仅做关闭聊天界面多选状态用
                EventBus.getDefault().post(new EventMoreSelected("MoreSelectedCollection", false, true));
            } else {
                MsgBroadcast.broadcastMsgUiUpdate(mContext);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    public boolean isAuthenticated() {
        boolean isLogin = coreManager.isLogin();
        if (!isLogin) {
            coreManager.autoReconnect(this);
        }
        // Todo 离线时发消息也不能return，自动重连...，让消息转圈(有重发)
        return false;
    }

    private void send(String UserId, ChatMessage message) {
        // 一些异步回调进来的也要判断xmpp是否在线，
        // 比如图片上传成功后，
        if (isAuthenticated()) {
            return;
        }
        coreManager.sendMucChatMessage(UserId, message);
    }

    public boolean isOk() {// 群主与管理员不受限制
        boolean isOk = true;
        if (mRoomMember != null) {
            if (mRoomMember.getRole() == 1 || mRoomMember.getRole() == 2) {
                isOk = true;
            } else {
                isOk = false;
            }
        }
        return isOk;
    }

    private void instantChatMessage(Friend mFriend, String toUserId, String messageId) {
        if (!TextUtils.isEmpty(messageId)) {
            ChatMessage chatMessage = ChatMessageDao.getInstance().findMsgById(mLoginUserId, toUserId, messageId);
            boolean isAllowSendFile = PreferenceUtils.getBoolean(mContext, Constants.IS_ALLOW_NORMAL_SEND_UPLOAD + mFriend.getUserId(), true);
            if (mFriend.getGroupStatus() == 0) {// 正常状态
                List<RoomMember> roomMemberList = RoomMemberDao.getInstance().getRoomMember(mFriend.getRoomId());
                if (roomMemberList.size() > 0) {
                    mRoomMember = RoomMemberDao.getInstance().getSingleRoomMember(mFriend.getRoomId(), mLoginUserId);
                }
            }

            if (chatMessage.getType() == ChatMessage.TYPE_FILE && !isAllowSendFile && !isOk()) {
                Toast.makeText(this, getString(R.string.tip_cannot_upload), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mRoomMember != null && MucRoomMember.disallowPublicAction(mRoomMember.getRole())) {
                ToastUtil.showToast(mContext, getString(R.string.tip_action_disallow_place_holder, getString(MucRoomMember.getRoleName(mRoomMember.getRole()))));
                return;
            }
            TrillStatisticsHelper.share(this, coreManager, chatMessage);
            chatMessage.setFromUserId(mLoginUserId);
            chatMessage.setFromUserName(coreManager.getSelf().getNickName());
            chatMessage.setToUserId(mFriend.getUserId());
            chatMessage.setUpload(true);
            chatMessage.setMySend(true);
            chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
            chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mFriend.getUserId(), chatMessage);
            send(mFriend.getUserId(), chatMessage);
        }
    }

    private void loadDataFriend(List<Friend> friends) {
        DialogHelper.showDefaulteMessageProgressDialog(this);
        AsyncUtils.doAsync(this, e -> {
            Reporter.post("加载数据失败，", e);
            AsyncUtils.runOnUiThread(this, ctx -> {
                DialogHelper.dismissProgressDialog();
                ToastUtil.showToast(ctx, R.string.data_exception);
            });
        }, c -> {
            Map<String, Integer> existMap = new HashMap<>();
            List<BaseSortModel<Friend>> sortedList = SortHelper.toSortedModelList(friends, existMap, Friend::getShowName);
            c.uiThread(r -> {
                DialogHelper.dismissProgressDialog();
                mSortFriends = sortedList;
                messageRecentlyAdapter.setData(sortedList);
            });
        });
    }

    /**
     * 获取自己在该群组的信息(职位、昵称、禁言时间等)以及群属性
     */
    private void isSupportSend(final Friend friend) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", friend.getRoomId());

        HttpUtils.get().url(coreManager.getConfig().ROOM_GET_ROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                             @Override
                             public void onResponse(ObjectResult<MucRoom> result) {// 数据结果与room/get接口一样，只是服务端没有返回群成员列表的数据
                                 if (result.getResultCode() == 1 && result.getData() != null) {
                                     final MucRoom mucRoom = result.getData();
                                     if (mucRoom.getMember() == null) {// 被踢出该群组
                                         FriendDao.getInstance().updateFriendGroupStatus(mLoginUserId, mucRoom.getJid(), 1);// 更新本地群组状态
                                         DialogHelper.tip(InstantMessageActivity.this, getString(R.string.tip_forward_kick));
                                         faultTolerance();
                                     } else {// 正常状态
                                         if (mucRoom.getS() == -1) {// 该群组已被锁定
                                             FriendDao.getInstance().updateFriendGroupStatus(mLoginUserId, mucRoom.getJid(), 3);// 更新本地群组状态
                                             DialogHelper.tip(InstantMessageActivity.this, getString(R.string.tip_group_disable_by_service));
                                             faultTolerance();
                                             return;
                                         }
                                         int role = mucRoom.getMember().getRole();
                                         // 更新禁言状态
                                         FriendDao.getInstance().updateRoomTalkTime(mLoginUserId, mucRoom.getJid(), mucRoom.getMember().getTalkTime());

                                         // 更新部分群属性
                                         MyApplication.getInstance().saveGroupPartStatus(mucRoom.getJid(), mucRoom.getShowRead(),
                                                 mucRoom.getAllowSendCard(), mucRoom.getAllowConference(),
                                                 mucRoom.getAllowSpeakCourse(), mucRoom.getTalkTime());

                                         // 更新个人职位
                                         RoomMemberDao.getInstance().updateRoomMemberRole(mucRoom.getId(), mLoginUserId, role);

                                         if (role == 4) {
                                             DialogHelper.tip(mContext, getString(R.string.hint_invisible));
                                             faultTolerance();
                                             return;
                                         }
                                         if (role == 1 || role == 2) {// 群组或管理员 直接转发出去
                                             currentSendSerialNumber++;
                                             forwardingStep(friend);
                                         } else {
                                             if (mucRoom.getTalkTime() > 0) {// 全体禁言
                                                 DialogHelper.tip(InstantMessageActivity.this, getString(R.string.tip_now_ban_all));
                                                 faultTolerance();
                                             } else if (mucRoom.getMember().getTalkTime() > System.currentTimeMillis() / 1000) {// 禁言
                                                 DialogHelper.tip(InstantMessageActivity.this, getString(R.string.tip_forward_ban));
                                                 faultTolerance();
                                             } else {
                                                 currentSendSerialNumber++;
                                                 forwardingStep(friend);
                                             }
                                         }
                                     }
                                 } else {// 群组已解散
                                     FriendDao.getInstance().updateFriendGroupStatus(mLoginUserId, friend.getUserId(), 2);// 更新本地群组状态
                                     DialogHelper.tip(InstantMessageActivity.this, getString(R.string.tip_forward_disbanded));
                                     faultTolerance();
                                 }
                             }

                             @Override
                             public void onError(Call call, Exception e) {
                                 ToastUtil.showNetError(mContext);
                                 faultTolerance();
                             }
                         }
                );
    }

    /**
     * 事件的监听
     */
    class ClickListener implements OnClickListener {
        private List<Friend> friends;

        public ClickListener(List<Friend> friends) {
            sendSize = friends.size();
            this.friends = friends;
        }

        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            int id = v.getId();
            if (id == R.id.btn_send) {// 发送
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).getRoomFlag() != 0) {// 群组，调接口判断一些群属性状态
                        if (friends.get(i).getIsLostChatKeyGroup() == 1) {
                            ToastUtil.showToast(mContext, getString(R.string.is_lost_key_cannot_support_send_msg, friends.get(i).getNickName()));
                            faultTolerance();
                        } else {
                            isSupportSend(friends.get(i));
                        }
                    } else {
                        currentSendSerialNumber++;
                        forwardingStep(friends.get(i));
                    }
                }
            }
        }
    }

    class MessageRecentlyAdapter extends BaseAdapter implements SectionIndexer {
        List<BaseSortModel<Friend>> mSortFriends;

        public MessageRecentlyAdapter() {
            mSortFriends = new ArrayList<>();
        }

        public void setData(List<BaseSortModel<Friend>> sortFriends) {
            mSortFriends = sortFriends;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mSortFriends != null) {
                return mSortFriends.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mSortFriends != null) {
                return mSortFriends.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (mSortFriends != null) {
                return position;
            }

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(InstantMessageActivity.this, R.layout.item_recently_contacts, null);
                holder = new ViewHolder();
                holder.mIvHead = (MessageAvatar) convertView.findViewById(R.id.iv_recently_contacts_head);
                holder.mTvName = (TextView) convertView.findViewById(R.id.tv_recently_contacts_name);
                holder.checkBox = convertView.findViewById(R.id.cb_instant);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.checkBox.setVisibility(View.VISIBLE);

            Friend friend = mSortFriends.get(position).getBean();
            ColorStateList tabColor = SkinUtils.getSkin(InstantMessageActivity.this).getTabColorState();
            if (friend != null) {
                holder.checkBox.setChecked(false);
                if (friend.getStatus() == 100) {
                    holder.checkBox.setChecked(true);
                    Drawable drawable = getResources().getDrawable(R.drawable.sel_check_wx2);
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTintList(drawable, tabColor);
                    holder.checkBox.setButtonDrawable(drawable);
                } else {
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setButtonDrawable(getResources().getDrawable(R.drawable.sel_nor_wx2));
                }
            }
            holder.mIvHead.fillData(friend);
            holder.mTvName.setText(TextUtils.isEmpty(friend.getRemarkName()) ? friend.getNickName() : friend.getRemarkName());
            return convertView;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = mSortFriends.get(i).getFirstLetter();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return mSortFriends.get(position).getFirstLetter().charAt(0);
        }
    }

    class ViewHolder {
        MessageAvatar mIvHead;
        TextView mTvName;
        CheckBox checkBox;
    }

    private class HorListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSelectPositions.size();
        }

        @Override
        public Object getItem(int position) {
            return mSelectPositions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new HeadView(mContext);
                int size = DisplayUtil.dip2px(mContext, 37);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(size, size);
                convertView.setLayoutParams(param);
            }
            HeadView imageView = (HeadView) convertView;
            String selectPosition = mSelectPositions.get(position);
            Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), selectPosition);
            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), friend, imageView);
            return convertView;
        }
    }
}
