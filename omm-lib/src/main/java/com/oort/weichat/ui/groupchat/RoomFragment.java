package com.oort.weichat.ui.groupchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.adapter.FriendSortAdapter;
import com.oort.weichat.bean.Area;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.RoomMember;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.MucgroupUpdateUtil;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.OnCompleteListener2;
import com.oort.weichat.db.dao.RoomMemberDao;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.sortlist.BaseComparator;
import com.oort.weichat.sortlist.BaseSortModel;
import com.oort.weichat.sortlist.SideBar;
import com.oort.weichat.sortlist.SortHelper;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.secure.RSA;
import com.oort.weichat.util.secure.chat.SecureChatUtil;
import com.oort.weichat.view.ClearEditText;
import com.oort.weichat.view.TipDialog;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.login.okhttp.result.Result;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;


/**
 * 我的群组
 */
public class RoomFragment extends EasyFragment {
    private PullToRefreshListView mPullToRefreshListView;
    private FriendSortAdapter mAdapter;
    private List<BaseSortModel<Friend>> mSortFriends;
    private BaseComparator<Friend> mBaseComparator;
    private SideBar mSideBar;
    private TextView mTextDialog;
    private ClearEditText mEditText;
    private FrameLayout fl_empty;

    private FloatingActionButton mFab;
    private String mLoginUserId;
    private Handler mHandler = new Handler();
    private boolean mNeedUpdate = true;
    private String searchKey;


    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MucgroupUpdateUtil.ACTION_UPDATE)) {
                update();
            }
        }
    };

    public RoomFragment() {
        mSortFriends = new ArrayList<BaseSortModel<Friend>>();
        mBaseComparator = new BaseComparator<Friend>();
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_room;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        mLoginUserId = coreManager.getSelf().getUserId();
        if (createView) {
            initView();
            updateRoom();
        }
    }

    public void update() {
        if (isResumed()) {
            loadData();
        } else {
            mNeedUpdate = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedUpdate) {
            loadData();
            mNeedUpdate = false;
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mUpdateReceiver);
        super.onDestroy();
    }

    private void initView() {
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mHeadView = inflater.inflate(R.layout.head_for_room_fragment, null);
        mEditText = mHeadView.findViewById(R.id.search_edit);
        mEditText.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), RoomSearchActivity.class));
            });


        mFab = findViewById(R.id.fab);


        mPullToRefreshListView.getRefreshableView().addHeaderView(mHeadView, null, false);
        mAdapter = new FriendSortAdapter(getActivity(), mSortFriends);
        mPullToRefreshListView.setAdapter(mAdapter);
        mPullToRefreshListView.getRefreshableView().setAdapter(mAdapter);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                updateRoom();
            }
        });

        mPullToRefreshListView.setOnItemClickListener((parent, view, position, id) -> {
            Friend friend = mSortFriends.get((int) id).getBean();
            Intent intent = new Intent(getActivity(), MucChatActivity.class);
            intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
            intent.putExtra(AppConstant.EXTRA_NICK_NAME, friend.getNickName());
            FastSharedPreferences.get("USERINFO_SAVE").edit().putString("tousername",friend.getNickName ()).apply();
            intent.putExtra(AppConstant.EXTRA_IS_GROUP_CHAT, true);
            startActivity(intent);
            if (friend.getUnReadNum() > 0) {// 如该群组未读消息数量大于1, 刷新MessageFragment
                MsgBroadcast.broadcastMsgNumReset(getActivity());
                MsgBroadcast.broadcastMsgUiUpdate(getActivity());
            }
        });

        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mTextDialog = (TextView) findViewById(R.id.text_dialog);
        mSideBar.setTextView(mTextDialog);

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mPullToRefreshListView.getRefreshableView().setSelection(position);
                }
            }
        });
       /* mPullToRefreshListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                BaseSortModel<Friend> sortFriend = mSortFriends.get((int) id);
                if (sortFriend == null || sortFriend.getBean() == null) {
                    return false;
                }
                showLongClickOperationDialog(sortFriend);
                return true;
            }
        });*/
        mFab.setOnClickListener(v -> {
            //startActivity(new Intent(getActivity(), SelectContactsActivity.class));
            Intent in = new Intent(mContext, PersonPickActivity.class);
            startActivityForResult(in,100);
            PersonPickActivity.pickFinish = new PersonPickActivity.PickFinish() {
                @Override
                public void finish(List ids,String names) {
                    mSelectPositions.clear();
                    if(getContext() != null){

                        if(ids.size() == 0){
                            return;
                        }
                        mSelectPositions.addAll(ids);

                        mLoginUserId = UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getImuserid();

                        createGroupChat(names, "", 0, 1, 0, 1, 1, 0);
                    }
                }
            };

        });
        fl_empty = findViewById(R.id.fl_empty);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mUpdateReceiver, MucgroupUpdateUtil.getUpdateActionFilter(),Context.RECEIVER_NOT_EXPORTED);
        }else {
            getActivity().registerReceiver(mUpdateReceiver, MucgroupUpdateUtil.getUpdateActionFilter());
        }

    }

    private void loadData() {
        AsyncUtils.doAsync(this, e -> {
            Reporter.post("加载数据失败，", e);
            AsyncUtils.runOnUiThread(requireContext(), ctx -> {
                ToastUtil.showToast(ctx, R.string.data_exception);
            });
        }, c -> {
            long startTime = System.currentTimeMillis();
            final List<Friend> friends;
            friends = FriendDao.getInstance().getAllRooms(mLoginUserId);
            if (!TextUtils.isEmpty(searchKey)) {
                Iterator<Friend> iterator = friends.iterator();
                while (iterator.hasNext()) {
                    Friend friend = iterator.next();
                    if (friend.getNickName().contains(searchKey)) {
                        continue;
                    }
                    RoomMember roomMember = RoomMemberDao.getInstance().searchMemberContains(friend, searchKey);
                    if (roomMember != null) {
                        continue;
                    }
                    iterator.remove();
                }
            }
            Map<String, Integer> existMap = new HashMap<>();
            List<BaseSortModel<Friend>> sortedList = SortHelper.toSortedModelList(friends, existMap, Friend::getShowName);

            long delayTime = 200 - (startTime - System.currentTimeMillis());// 保证至少200ms的刷新过程
            if (delayTime < 0) {
                delayTime = 0;
            }

            c.postDelayed(r -> {
                mSideBar.setExistMap(existMap);
                mSortFriends = sortedList;
                mAdapter.setData(sortedList);
                mPullToRefreshListView.onRefreshComplete();

                if (sortedList != null && sortedList.size() > 0){
                    fl_empty.setVisibility(View.GONE);
                }else {
                    fl_empty.setVisibility(View.VISIBLE);
                }
            }, delayTime);


        });
    }

    /**
     * 下载我的群组
     */
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
                            FriendDao.getInstance().addRooms(mHandler, mLoginUserId, result.getData(),
                                    new OnCompleteListener2() {
                                        @Override
                                        public void onLoading(int progressRate, int sum) {
                                        }

                                        @Override
                                        public void onCompleted() {// 下载完成
                                            if (coreManager.isLogin()) {
                                                // 1.调用smack内join方法加入群组
                                                List<Friend> mFriends = FriendDao.getInstance().getAllRooms(mLoginUserId);
                                                for (int i = 0; i < mFriends.size(); i++) {// 已加入的群组不会重复加入，方法内已去重
                                                    coreManager.joinMucChat(mFriends.get(i).getUserId(), 0);
                                                }
                                                // 2.更新我的群组列表
                                                loadData();
                                            }
                                        }
                                    });
                        } else {
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(getActivity());
                        mPullToRefreshListView.onRefreshComplete();
                    }
                });
    }


    //String mLoginUserId = "";
    String chatKey = "";
    boolean mQuicklyCreate = false;
    List  mSelectPositions = new ArrayList();

    private void createGroupChat(final String roomName, final String roomDesc, int isRead, int isLook,
                                 int isNeedVerify, int isShowMember, int isAllowSendCard, int isSecretGroup) {


        final String roomJid = coreManager.createMucRoom(roomName);
        if (TextUtils.isEmpty(roomJid)) {
            ToastUtil.showToast(mContext, getString(R.string.create_room_failed));
            return;
        }
        MyApplication.mRoomKeyLastCreate = roomJid;
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("jid", roomJid);
        params.put("name", roomName);
        params.put("desc", roomDesc);
        params.put("countryId", String.valueOf(Area.getDefaultCountyId()));

        params.put("showRead", isRead + "");
        // 显示已读人数
        PreferenceUtils.putBoolean(mContext, Constants.IS_SHOW_READ + roomJid, isRead == 1);
        // 是否公开
        params.put("isLook", isLook + "");
        // 是否开启进群验证
        params.put("isNeedVerify", isNeedVerify + "");
        // 其他群管理
        params.put("showMember", isShowMember + "");
        params.put("allowSendCard", isAllowSendCard + "");

        params.put("allowInviteFriend", "1");
        params.put("allowUploadFile", "1");
        params.put("allowConference", "1");
        params.put("allowSpeakCourse", "1");

        PreferenceUtils.putBoolean(mContext, Constants.IS_SEND_CARD + roomJid, isAllowSendCard == 1);

        Area area = Area.getDefaultProvince();
        if (area != null) {
            params.put("provinceId", String.valueOf(area.getId()));    // 省份Id
        }
        area = Area.getDefaultCity();
        if (area != null) {
            params.put("cityId", String.valueOf(area.getId()));            // 城市Id
            area = Area.getDefaultDistrict(area.getId());
            if (area != null) {
                params.put("areaId", String.valueOf(area.getId()));        // 城市Id
            }
        }

        double latitude = MyApplication.getInstance().getBdLocationHelper().getLatitude();
        double longitude = MyApplication.getInstance().getBdLocationHelper().getLongitude();
        if (latitude != 0)
            params.put("latitude", String.valueOf(latitude));
        if (longitude != 0)
            params.put("longitude", String.valueOf(longitude));

        // SecureFlagGroup
        params.put("isSecretGroup", String.valueOf(isSecretGroup));
        if (isSecretGroup == 1) {
            chatKey = UUID.randomUUID().toString().replaceAll("-", "");
            String chatKeyGroup = RSA.encryptBase64(chatKey.getBytes(),
                    Base64.decode(SecureChatUtil.getRSAPublicKey(coreManager.getSelf().getUserId())));
            Map<String, String> keys = new HashMap<>();
            keys.put(coreManager.getSelf().getUserId(), chatKeyGroup);
            String keysStr = JSON.toJSONString(keys);
            params.put("keys", keysStr);
        }

        DialogHelper.showDefaulteMessageProgressDialog(getActivity());

        HttpUtils.get().url(coreManager.getConfig().ROOM_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                    @Override
                    public void onResponse(ObjectResult<MucRoom> result) {
                        DialogHelper.dismissProgressDialog();
                        if (Result.checkSuccess(mContext, result)) {
                            if (mQuicklyCreate) {
                                getContext().sendBroadcast(new Intent(OtherBroadcast.QC_FINISH)); // 快速建群成功，发送广播关闭之前的单聊界面
                            }
                            createRoomSuccess(result.getData());
                        } else {
                            MyApplication.mRoomKeyLastCreate = "compatible";// 还原回去
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        MyApplication.mRoomKeyLastCreate = "compatible";// 还原回去
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }


    // 创建成功的时候将会调用此方法，将房间也存为好友
    private void createRoomSuccess(MucRoom mucRoom) {

        Friend friend = new Friend();
        friend.setOwnerId(mLoginUserId);
        friend.setUserId(mucRoom.getJid());
        friend.setNickName(mucRoom.getName());
        friend.setDescription(mucRoom.getDesc());
        friend.setRoomId(mucRoom.getId());
        friend.setRoomCreateUserId(mLoginUserId);
        friend.setRoomFlag(1);
        friend.setStatus(Friend.STATUS_FRIEND);
        // timeSend作为取群聊离线消息的标志，所以要在这里设置一个初始值
        friend.setTimeSend(TimeUtils.sk_time_current_time());
        // SecureFlagGroup
        friend.setIsSecretGroup(mucRoom.getIsSecretGroup());
        if (friend.getIsSecretGroup() == 1) {
            friend.setChatKeyGroup(SecureChatUtil.encryptChatKey(mucRoom.getJid(), chatKey));
        }
        FriendDao.getInstance().createOrUpdateFriend(friend);

        // 更新群组
        MucgroupUpdateUtil.broadcastUpdateUi(getContext());

        // 本地发送一条消息至该群 否则未邀请其他人时在消息列表不会显示
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(XmppMessage.TYPE_TIP);
        chatMessage.setFromUserId(mLoginUserId);
        chatMessage.setFromUserName(coreManager.getSelf().getNickName());
        chatMessage.setToUserId(mucRoom.getJid());
        chatMessage.setContent(getString(R.string.new_friend_chat));
        chatMessage.setPacketId(coreManager.getSelf().getNickName());
        chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mucRoom.getJid(), chatMessage)) {
            // 更新聊天界面
            MsgBroadcast.broadcastMsgUiUpdate(getContext());
        }

        // 邀请好友
        List<String> inviteUsers = new ArrayList<>(mSelectPositions);
        if (mQuicklyCreate) {
            //inviteUsers.add(mQuicklyId);
        }
        // SecureFlagGroup
        Map<String, String> keys = new HashMap<>();
        String keysStr = "";
        if (mucRoom.getIsSecretGroup() == 1) {
            for (int i = 0; i < inviteUsers.size(); i++) {
                Friend inviteUser = FriendDao.getInstance().getFriend(mLoginUserId, inviteUsers.get(i));
                String chatKeyGroup = RSA.encryptBase64(chatKey.getBytes(),
                        Base64.decode(inviteUser.getPublicKeyRSARoom()));
                keys.put(inviteUsers.get(i), chatKeyGroup);
            }
            keysStr = JSON.toJSONString(keys);
        }

        if (inviteUsers.size() + 1 <= mucRoom.getMaxUserSize()) {
            inviteFriend(JSON.toJSONString(inviteUsers), keysStr, mucRoom);
        } else {// 超过群组人数上限
            TipDialog tipDialog = new TipDialog(mContext);
            tipDialog.setmConfirmOnClickListener(getString(R.string.tip_over_member_size, mucRoom.getMaxUserSize()), () -> start(mucRoom.getJid(), mucRoom.getName()));
            tipDialog.show();
            tipDialog.setOnDismissListener(dialog -> start(mucRoom.getJid(), mucRoom.getName()));
        }
    }

    /**
     * 邀请好友
     */
    private void inviteFriend(String text, String keysStr, MucRoom mucRoom) {
        if (mSelectPositions.size() <= 0) {
            start(mucRoom.getJid(), mucRoom.getName());
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", mucRoom.getId());
        params.put("text", text);
        // SecureFlagGroup
        params.put("isSecretGroup", String.valueOf(mucRoom.getIsSecretGroup()));
        if (mucRoom.getIsSecretGroup() == 1) {
            params.put("keys", keysStr);
        }

        DialogHelper.showDefaulteMessageProgressDialog(getContext());

        HttpUtils.get().url(coreManager.getConfig().ROOM_MEMBER_UPDATE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        //setResult(RESULT_OK);
                        start(mucRoom.getJid(), mucRoom.getName());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }
    private void start(String jid, String name) {
        // 进入群聊界面，结束当前的界面
        Intent intent = new Intent(getContext(), MucChatActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, jid);
        intent.putExtra(AppConstant.EXTRA_NICK_NAME, name);
        intent.putExtra(AppConstant.EXTRA_IS_GROUP_CHAT, true);
        startActivity(intent);
    }
}
