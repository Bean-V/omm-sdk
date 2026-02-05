package com.oortcloud.revision.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Area;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.PrivacySetting;
import com.oort.weichat.bean.RoomMember;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.MucgroupUpdateUtil;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.RoomMemberDao;
import com.oort.weichat.db.dao.UserDao;
import com.oort.weichat.fragment.MessageFragment;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.pay.new_ui.PaymentOrReceiptActivity;
import com.oort.weichat.pay.sk.SKPayActivity;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.groupchat.FaceToFaceGroup;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.me.NearPersonActivity;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.message.multi.RoomInfoActivity;
import com.oort.weichat.ui.nearby.PublicNumberSearchActivity;
import com.oort.weichat.ui.nearby.UserSearchActivity;
import com.oort.weichat.ui.other.BasicInfoActivity;
import com.oort.weichat.ui.search.SearchAllActivity;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DisplayUtil;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.util.secure.RSA;
import com.oort.weichat.util.secure.chat.SecureChatUtil;
import com.oort.weichat.view.ChatBottomView;
import com.oort.weichat.view.HeadView;
import com.oort.weichat.view.MessagePopupWindow;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.TipDialog;
import com.oort.weichat.view.VerifyDialog;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.XmppConnectionManager;
import com.oort.weichat.xmpp.listener.AuthStateListener;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.views.swiperecyclerview.OnItemMenuClickListener;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenu;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuBridge;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuCreator;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuItem;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/30 10:06
 * @version： v1.0
 * @function：
 */
 public  class GroupMessageFragment extends EasyFragment implements AuthStateListener {

    // 消息界面在前台展示中就不响铃新消息，
    public static boolean foreground = false;
    private boolean flag = false;
    private boolean search;
    private TextView mTvTitle;
    private ImageView mIvTitleRight;
    private ImageView mIvFriendRight;
    private View mHeadView;
    private TextView mEditText;
    private LinearLayout mNetErrorLl;
    private ImageView mIvNoData;
    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mListView;
    private MessageListAdapter mAdapter;
    private List<Friend> mFriendList;
    private String mLoginUserId;
    private MessagePopupWindow mMessagePopupWindow;
    private TextView mTvTitleLeft;
    private TextView tv_title_right;
    // 刷新的定时器，限制过快刷新，
    // 刷新全部的刷新器，
    private RefreshTimer refreshTimer = new RefreshTimer();
    // 刷新一个朋友的刷新器集合，
    private Map<String, RefreshTimer> timerMap = new HashMap<>();
    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(MsgBroadcast.ACTION_MSG_UI_UPDATE)) {// 刷新页面
                long lastRefreshTime = refreshTimer.refreshTime;
                long delta = System.currentTimeMillis() - lastRefreshTime;
                if (delta > TimeUnit.SECONDS.toMillis(1)) {
                    refreshTimer.refreshTime = System.currentTimeMillis();
                    refresh();
                } else if (!refreshTimer.timerRunning) {
                    refreshTimer.timerRunning = true;
                    refreshTimer.start();
                }
            } else if (action.equals(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE)) {// 刷新页面
                String fromUserId = intent.getStringExtra("fromUserId");
                RefreshTimer timer = timerMap.get(fromUserId);
                if (timer == null) {
                    timer = new RefreshTimer(fromUserId);
                    timerMap.put(fromUserId, timer);
                }
                long lastRefreshTime = timer.refreshTime;
                long delta = System.currentTimeMillis() - lastRefreshTime;
                if (delta > TimeUnit.SECONDS.toMillis(1)) {
                    timer.refreshTime = System.currentTimeMillis();
                    refresh(fromUserId);
                } else if (!timer.timerRunning) {
                    timer.timerRunning = true;
                    timer.start();
                }
            } else if (action.equals(Constants.NOTIFY_MSG_SUBSCRIPT)) {
                Friend friend = (Friend) intent.getSerializableExtra(AppConstant.EXTRA_FRIEND);
                if (friend != null) {
                    clearMessageNum(friend);
                }
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {// 网络发生改变
                if (!HttpUtil.isGprsOrWifiConnected(getActivity())) {
                    mNetErrorLl.setVisibility(View.VISIBLE);
                } else {
                    mNetErrorLl.setVisibility(View.GONE);
                }
            } else if (action.equals(Constants.NOT_AUTHORIZED)) {
                mTvTitle.setText(getString(R.string.password_error));
            }
        }
    };
    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = DisplayUtil.dip2px(requireContext(), 80f);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                Friend friend = mAdapter.getItem(position);
                final long time = friend.getTopTime();

                SwipeMenuItem top = new SwipeMenuItem(requireContext()).setBackgroundColorResource(R.color.Grey_400)
                        .setText(R.string.top_tv)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                if (time == 0) {
                    top.setText(getString(R.string.top));
                } else {
                    top.setText(getString(R.string.cancel_top));
                }
                if (friend.getIsDevice() != 1) {// 我的设备去掉置顶选项
                    swipeRightMenu.addMenuItem(top);// 添加菜单到右侧。
                }
                SwipeMenuItem mark_unread = new SwipeMenuItem(requireContext()).setBackgroundColorResource(R.color.color_read_unread_item)
                        .setText(R.string.mark_unread)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                if (friend.getUnReadNum() > 0) {
                    mark_unread.setText(getString(R.string.mark_read));
                } else {
                    mark_unread.setText(getString(R.string.mark_unread));
                }
                swipeRightMenu.addMenuItem(mark_unread);// 添加菜单到右侧。
                SwipeMenuItem delete = new SwipeMenuItem(requireContext()).setBackgroundColorResource(R.color.redpacket_bg)
                        .setText(R.string.delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(delete);// 添加菜单到右侧。
            }
        }
    };
    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            Friend friend = mAdapter.getData().get(position);
            if (friend.getIsDevice() == 1) {// 我的设备去掉了置顶
                menuPosition = menuPosition + 1;
            }
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    updateTopChatStatus(friend);
                } else if (menuPosition == 1) {
                    if (friend.getUnReadNum() > 0) {
                        clearMessageNum(friend);
                    } else {
                        FriendDao.getInstance().markUserMessageUnRead(mLoginUserId, friend.getUserId());
                        MsgBroadcast.broadcastMsgNumUpdate(MyApplication.getInstance(), true, 1);
                        MsgBroadcast.broadcastMsgUiUpdate(MyApplication.getInstance());
                    }
                } else {
                    delete(friend);
                    // 保留旧代码，
                    // 内部和外部的mFriendList都要更新到，
                    GroupMessageFragment.this.mFriendList.remove(position);
                    mAdapter.setData(GroupMessageFragment.this.mFriendList);
                }
            }

        }
    };

    private void refresh(String friendId) {
        if (TextUtils.isEmpty(friendId)) {
            refresh();
            return;
        }

        // 要更新的除了消息还有消息数量，所以不方便外面传入，只能查数据库，又拖慢了，
        Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, friendId);
        if (!mAdapter.updateContent(friend)) {
            // 当前列表没有这个好友，直接全部刷新，
            refresh();
        }
    }

    private void refresh() {
        if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            mEditText.setText("");// 内部调用了loadData
        } else {
            loadDatas();
        }
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initActionBar();
        // 不能用createView判断不初始化，因为Fragment复用时老activity可能被销毁了，
        initView();
        loadDatas();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        foreground = isVisibleToUser;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        foreground = !hidden;
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        foreground = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        foreground = true;
        int authState = XmppConnectionManager.mXMPPCurrentState;
        if (authState == 0 || authState == 1) {
            findViewById(R.id.pb_title_center).setVisibility(View.VISIBLE);
            mTvTitle.setText(getString(R.string.msg_view_controller_going_off));
        } else if (authState == 2) {
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.msg_view_controller_online));
        } else {
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.msg_view_controller_off_line));
        }

        if(Constant.IsSingleMsgTab) {
            mTvTitle.setText("通知");
            mTvTitleLeft.setVisibility(View.GONE);
            mIvFriendRight.setVisibility(View.GONE);
            mIvTitleRight.setVisibility(View.GONE);
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mUpdateReceiver);
        ListenerManager.getInstance().removeAuthStateChangeListener(this);
    }

    private void initActionBar() {
        findViewById(R.id.iv_title_left).setVisibility(View.GONE);
        mTvTitle = (TextView) findViewById(R.id.tv_title_center);
        mTvTitle.setText(getString(R.string.msg_view_controller_off_line));
        appendClick(mTvTitle);

        mIvTitleRight = (ImageView) findViewById(R.id.iv_title_right);
        mIvTitleRight.setImageResource(R.mipmap.more_icon);
        appendClick(mIvTitleRight);

        mTvTitleLeft = findViewById(R.id.tv_title_left);
        mTvTitleLeft.setText(getResources().getString(R.string.start_edit));
        appendClick(mTvTitleLeft);

        tv_title_right = findViewById(R.id.tv_title_right);
        tv_title_right.setText(getResources().getString(R.string.finish));
        tv_title_right.setVisibility(View.GONE);
        appendClick(tv_title_right);

        //增加通讯录切换
        mIvFriendRight = findViewById(R.id.iv_friend_right);
        mIvFriendRight.setImageResource(R.mipmap.tab_group);
        mIvFriendRight.setVisibility(View.VISIBLE);
        appendClick(mIvFriendRight);
    }

    private void initView() {
        mLoginUserId = coreManager.getSelf().getUserId();

        mFriendList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (mHeadView != null) {
            // Fragment复用时可能已经添加过headerView了，
            mListView.removeHeaderView(mHeadView);
            mAdapter.notifyDataSetChanged();
        }

        mListView = findViewById(R.id.recyclerView);
        mListView.setSwipeMenuCreator(swipeMenuCreator);
        mListView.setOnItemMenuClickListener(mMenuItemClickListener);
        mListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mHeadView = inflater.inflate(R.layout.head_for_messagefragment, mRefreshLayout, false);
        mEditText = mHeadView.findViewById(R.id.search_edit);
        mEditText.setOnClickListener(v -> SearchAllActivity.start(requireActivity(), "friend"));
        mNetErrorLl = (LinearLayout) mHeadView.findViewById(R.id.net_error_ll);
        mNetErrorLl.setOnClickListener(this);
        mIvNoData = mHeadView.findViewById(R.id.iv_no_nearly_msg);
        mListView.addHeaderView(mHeadView);
        mAdapter = new MessageListAdapter();
        mAdapter.setHasStableIds(true);
        mListView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(rl -> {
            refresh();
        });

        mEditText.setHint(getString(R.string.search_chatlog));
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (!TextUtils.isEmpty(str)) {
                    queryChatMessage(str);
                } else {
                    loadDatas();
                }
            }
        });

        ListenerManager.getInstance().addAuthStateChangeListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE);// 刷新页面Ui
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE);// 刷新页面Ui
        intentFilter.addAction(Constants.NOTIFY_MSG_SUBSCRIPT);// 刷新"消息"角标
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 网络发生改变
        intentFilter.addAction(Constants.NOT_AUTHORIZED);// XMPP密码错误
        getActivity().registerReceiver(mUpdateReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
    }

    /**
     * 加载朋友数据
     */
    private void loadDatas() {
        if (mFriendList != null) {
            mFriendList.clear();
        }
        for (Map.Entry<String, RefreshTimer> entry : timerMap.entrySet()) {
            entry.getValue().cancel();
        }
        timerMap.clear();
        search = false;
        mFriendList = FriendDao.getInstance().getNearlyFriendMsg(mLoginUserId);

        Log.v("msg" , mFriendList.size()+"---");

        List<Friend> mRemoveFriend = new ArrayList<>();
        if (mFriendList.size() > 0) {
            for (int i = 0; i < mFriendList.size(); i++) {
                Friend friend = mFriendList.get(i);
                if (friend != null) {

                   User user = UserDao.getInstance().getUserByUserId(friend.getUserId());
                    if (friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)){
                        continue;
                    }
                    if (!MessageFragment.getPns().contains(friend.getUserId())) {//friend.getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)|| friend.getUserId().equals(mLoginUserId) ||
                        mRemoveFriend.add(friend);
                    }

                    //if(user.getUserType() = 1)

                }
            }
            mFriendList.removeAll(mRemoveFriend);
        }

        mTvTitle.post(() -> {
            updataListView();
            mRefreshLayout.finishRefresh();
        });
    }

    private void clearMessageNum(Friend friend) {
        friend.setUnReadNum(0);
        FriendDao.getInstance().markUserMessageRead(mLoginUserId, friend.getUserId());
//        MainActivity mMainActivity = (MainActivity) getActivity();
//        if (mMainActivity != null) {
//            mMainActivity.updateNumData();
//        }
        mAdapter.updateUnReadNum(friend);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_title_left) {
            if (flag) {
                SelectionFrame selectionFrame = new SelectionFrame(getActivity());
                selectionFrame.setSomething(null, getString(R.string.tip_sure_delete_all_data),
                        new SelectionFrame.OnSelectionFrameClickListener() {
                            @Override
                            public void cancelClick() {

                            }

                            @Override
                            public void confirmClick() {
                                for (int i = 0; i < mFriendList.size(); i++) {
                                    final Friend friend = mFriendList.get(i);
                                    delete(friend);
                                    if (i == mFriendList.size() - 1) {
                                        mFriendList.clear();
                                        mAdapter.setData(GroupMessageFragment.this.mFriendList);
                                    }
                                }
                            }
                        });
                selectionFrame.show();
                return;
            }
            flag = true;
            mIvTitleRight.setVisibility(View.GONE);
            tv_title_right.setVisibility(View.VISIBLE);
            mTvTitleLeft.setText(getResources().getString(R.string.empty));
            mAdapter.notifyDataSetChanged();
        } else if (id == R.id.tv_title_right) {
            flag = false;
            tv_title_right.setVisibility(View.GONE);
            mTvTitleLeft.setText(getResources().getString(R.string.start_edit));
            mIvTitleRight.setVisibility(View.VISIBLE);

            mAdapter.notifyDataSetChanged();
        } else if (id == R.id.tv_title_center) {
        } else if (id == R.id.iv_title_right) {
            mMessagePopupWindow = new MessagePopupWindow(getActivity(), this, coreManager);
            mMessagePopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mMessagePopupWindow.showAsDropDown(v,
                    -(mMessagePopupWindow.getContentView().getMeasuredWidth() - v.getWidth() / 2 - 40),
                    0);
        } else if (id == R.id.search_public_number) {// 搜索公众号
            mMessagePopupWindow.dismiss();
            PublicNumberSearchActivity.start(requireContext());
        } else if (id == R.id.create_group) {// 发起群聊
//                mMessagePopupWindow.dismiss();
//                startActivity(new Intent(getActivity(), SelectContactsActivity.class));
//
//
//                mMessagePopupWindow.dismiss();
            //startActivity(new Intent(mContext, SelectContactsActivity.class));

            //                mMessagePopupWindow.dismiss();
//                startActivity(new Intent(getActivity(), SelectContactsActivity.class));
//
//
//                mMessagePopupWindow.dismiss();
            //startActivity(new Intent(mContext, SelectContactsActivity.class));

            PersonPickActivity.pickFinish = null;
            PersonPickActivity.pickFinish_v2 = null;
            Intent in = new Intent(mContext, PersonPickActivity.class);
            startActivityForResult(in, 100);
            PersonPickActivity.pickFinish = new PersonPickActivity.PickFinish() {
                @Override
                public void finish(List ids, String names) {
                    mSelectPositions.clear();
                    if (getContext() != null) {

                        if (ids.size() == 0) {
                            return;
                        }
                        mSelectPositions.addAll(ids);

                        mLoginUserId = UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getImuserid();

                        createGroupChat(names, "", 0, 1, 0, 1, 1, 0);
                    }
                }
            };
        } else if (id == R.id.face_group) {
            mMessagePopupWindow.dismiss();
            // 面对面建群
            startActivity(new Intent(getActivity(), FaceToFaceGroup.class));
        } else if (id == R.id.add_friends) {// 添加朋友
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), UserSearchActivity.class));
        } else if (id == R.id.scanning) {// 扫一扫
            mMessagePopupWindow.dismiss();
            MainActivity.requestQrCodeScan(getActivity());
        } else if (id == R.id.receipt_payment) {// 收付款
            mMessagePopupWindow.dismiss();
            PaymentOrReceiptActivity.start(getActivity(), coreManager.getSelf().getUserId());
        } else if (id == R.id.near_person) {// 附近的人
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), NearPersonActivity.class));
        } else if (id == R.id.net_error_ll) {//网络错误
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 查询聊天记录
     */
    private void queryChatMessage(String str) {
        List<Friend> data = new ArrayList<>();
        mFriendList = FriendDao.getInstance().getNearlyFriendMsg(mLoginUserId);
        for (int i = 0; i < mFriendList.size(); i++) {
            Friend friend = mFriendList.get(i);
            List<Friend> friends = ChatMessageDao.getInstance().queryChatMessageByContent(friend, str);
            if (friends != null && friends.size() > 0) {
                data.addAll(friends);
            }
        }

        if (mFriendList != null) {
            mFriendList.clear();
        }

        search = true;
        mFriendList.addAll(data);
        updataListView();
    }

    /**
     * 更新列表
     */
    private void updataListView() {
        mAdapter.setData(mFriendList);
    }

    /**
     * xmpp在线状态监听
     */
    @Override
    public void onAuthStateChange(int authState) {
        authState = XmppConnectionManager.mXMPPCurrentState;
        if (mTvTitle == null) {
            return;
        }
        if (authState == 0 || authState == 1) {
            // 登录中
            findViewById(R.id.pb_title_center).setVisibility(View.VISIBLE);
            mTvTitle.setText(getString(R.string.msg_view_controller_going_off));
        } else if (authState == 2) {
            // 在线
            MainActivity.isAuthenticated = true;
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.msg_view_controller_online));
            mNetErrorLl.setVisibility(View.GONE);// 网络判断对部分手机有时会失效，坐下兼容(当xmpp在线时，隐藏网络提示)
        } else {
            // 离线
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.msg_view_controller_off_line));
        }
    }

    private void updateTopChatStatus(Friend friend) {
        DialogHelper.showDefaulteMessageProgressDialog(getActivity());

        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getContext()).accessToken);
        params.put("userId", mLoginUserId);
        if (friend.getRoomFlag() == 0) {
            params.put("toUserId", friend.getUserId());
        } else {
            params.put("roomId", friend.getRoomId());
        }
        if (friend.getRoomFlag() == 0) {
            params.put("type", String.valueOf(2));
        } else {
            params.put("type", String.valueOf(1));
        }
        params.put("offlineNoPushMsg", friend.getTopTime() == 0 ? String.valueOf(1) : String.valueOf(0));

        String url;
        if (friend.getRoomFlag() == 0) {
            url = CoreManager.requireConfig(MyApplication.getContext()).FRIENDS_NOPULL_MSG;
        } else {
            url = CoreManager.requireConfig(MyApplication.getContext()).ROOM_DISTURB;
        }
        HttpUtils.get().url(url)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            if (friend.getTopTime() == 0) {
                                FriendDao.getInstance().updateTopFriend(friend.getUserId(), friend.getTimeSend());
                            } else {
                                FriendDao.getInstance().resetTopFriend(friend.getUserId());
                            }
                            loadDatas();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                    }
                });
    }

    private void emptyServerMessage(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("type", String.valueOf(0));// 0 清空单人 1 清空所有
        params.put("toUserId", userId);

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

    void delete(Friend friend) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        if (friend.getRoomFlag() == 0) {// 群组不支持删除服务器上记录，因为群组只有一张消息表
            //  获取首页侧滑删除服务器聊天记录状态，
            boolean isSlideClearServerMSG = PrivacySettingHelper.getPrivacySettings(getActivity()).getIsSkidRemoveHistoryMsg() == 1;
            if (isSlideClearServerMSG) {
                emptyServerMessage(friend.getUserId());
            }
        }
        // 如果是普通的人/群组，从好友表中删除最后一条消息的记录，这样就不会查出来了
        FriendDao.getInstance().resetFriendMessage(mLoginUserId, friend.getUserId());
        // 消息表中删除
        ChatMessageDao.getInstance().deleteMessageTable(mLoginUserId, friend.getUserId());
        if (friend.getUnReadNum() > 0) {
            MsgBroadcast.broadcastMsgNumUpdate(getActivity(), false, friend.getUnReadNum());
        }
    }

    public void filterMessageByGroup(String groupId) {
    }


    public void  filterMessageByChatIds(List chatIds){

    }
    private class RefreshTimer extends CountDownTimer {
        // 上次刷新时间，限制过快刷新，
        private long refreshTime;
        private boolean timerRunning;
        @Nullable
        private String friendId;

        RefreshTimer() {
            super(1000, 1000);
        }

        RefreshTimer(@Nullable String friendId) {
            this();
            this.friendId = friendId;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Log.e("notify", "计时结束，更新消息页面");
            timerRunning = false;
            refreshTime = System.currentTimeMillis();
            refresh(friendId);
        }
    }

    /**
     * 适配器
     */
    class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {

        private List<Friend> mFriendList = new ArrayList<>();

        @NonNull
        @Override
        public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_nearly_message, viewGroup, false);
            return new MessageListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageListViewHolder messageListViewHolder, int position) {
            Friend friend = mFriendList.get(position);
            messageListViewHolder.bind(friend, position);
        }

        // 当前页面list混乱，存在两个同时使用的list, 一个fragment持有，一个adapter持有，
        // 有可能因异步操作list导致listView崩溃，所以不能共用fragment持有的list，
        // 读取时尽量使用adapter持有的list确保数据一致，
        public Friend getItem(int position) {
            return mFriendList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFriendList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public List<Friend> getData() {
            return mFriendList;
        }

        public void setData(List<Friend> mFriendList) {
            this.mFriendList = new ArrayList<>(mFriendList);
            notifyDataSetChanged();
            mIvNoData.setVisibility(mFriendList.size() == 0 ? View.VISIBLE : View.GONE);
        }

        /**
         * 返回true表示成功刷新这个朋友消息，
         */
        boolean updateContent(Friend updateFriend) {
            if (updateFriend == null) {
                return false;
            }
            int newPosition = -1;
            int oldPosition = -1;
            for (int i = 0; i < mFriendList.size(); i++) {
                Friend friend = mFriendList.get(i);
                if (newPosition < 0 && friend.getTopTime() <= updateFriend.getTopTime() && friend.getTimeSend() <= updateFriend.getTimeSend()) {
                    newPosition = i;
                }
                if (TextUtils.equals(friend.getUserId(), updateFriend.getUserId())) {
                    oldPosition = i;
                    mFriendList.set(i, updateFriend);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
            if (newPosition >= 0 && oldPosition >= 0 && newPosition != oldPosition) {
                Friend remove = mFriendList.remove(oldPosition);
                mFriendList.add(newPosition, remove);
                mAdapter.notifyDataSetChanged();
            }
            return oldPosition >= 0;
        }

        /**
         * 返回true表示成功刷新这个朋友消息，
         */
        boolean updateUnReadNum(Friend updateFriend) {
            for (int i = 0; i < mFriendList.size(); i++) {
                Friend mF = mFriendList.get(i);
                if (TextUtils.equals(mF.getUserId(), updateFriend.getUserId())) {
                    mFriendList.set(i, updateFriend);
                    mAdapter.notifyItemChanged(i);
                    return true;
                }
            }
            return false;
        }
    }

    class MessageListViewHolder extends RecyclerView.ViewHolder {
        Context mContext = requireContext();
        RelativeLayout rl_warp = itemView.findViewById(R.id.item_friend_warp);
        ImageView iv_delete = itemView.findViewById(R.id.iv_delete);
        HeadView avatar = itemView.findViewById(R.id.avatar_imgS);
        TextView nick_name_tv = itemView.findViewById(R.id.nick_name_tv);
        TextView tip_tv = itemView.findViewById(R.id.item_message_tip);
        TextView content_tv = itemView.findViewById(R.id.content_tv);
        TextView time_tv = itemView.findViewById(R.id.time_tv);
        TextView num_tv = itemView.findViewById(R.id.num_tv);
        View replay_iv = itemView.findViewById(R.id.replay_iv);
        View not_push_ll = itemView.findViewById(R.id.not_push_iv);

        MessageListViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(Friend friend, int position) {
            itemView.setOnClickListener(v -> {
                // 在跳转之前关闭软键盘
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(findViewById(R.id.message_fragment).getWindowToken(), 0); // 强制隐藏键盘
                }

                Intent intent = new Intent();
                if (friend.getRoomFlag() == 0) { // 个人
                    if (TextUtils.equals(friend.getUserId(), Friend.ID_SK_PAY)) {
                        intent.setClass(getActivity(), SKPayActivity.class);
                    } else {
                        intent.setClass(getActivity(), ChatActivity.class);
                        intent.putExtra(ChatActivity.FRIEND, friend);
                    }
                } else {
                    intent.setClass(getActivity(), MucChatActivity.class);
                    intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                    intent.putExtra(AppConstant.EXTRA_NICK_NAME, friend.getNickName());
                }

                if (search) {
                    intent.putExtra("isserch", true);
                    // 为什么改取为chatRecordTimeOut，可至queryChatMessageByContent方法内查看原因
                    // intent.putExtra("jilu_id", friend.getTimeSend());
                    intent.putExtra("jilu_id", friend.getChatRecordTimeOut());
                } else {
                    intent.putExtra(Constants.NEW_MSG_NUMBER, friend.getUnReadNum());
                }
                startActivity(intent);
                clearMessageNum(friend);
            });

            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), friend, avatar);
            nick_name_tv.setText(!TextUtils.isEmpty(friend.getRemarkName()) ? friend.getRemarkName() : friend.getNickName());
            FastSharedPreferences.get("USERINFO_SAVE").edit().putString("tousername",friend.getNickName ()).apply();
            if (friend.getRoomFlag() != 0) {// 群组 @
                if (friend.getIsAtMe() == 1) {
                    tip_tv.setText("[有人@我]");
                    tip_tv.setVisibility(View.VISIBLE);
                } else if (friend.getIsAtMe() == 2) {
                    tip_tv.setText("[@全体成员]");
                    tip_tv.setVisibility(View.VISIBLE);
                } else {
                    tip_tv.setVisibility(View.GONE);
                }
            } else {
                tip_tv.setVisibility(View.GONE);
            }

            if (friend.getType() == XmppMessage.TYPE_TEXT) {// 文本消息 表情
                String s = friend.getContent();
                if (s == null) {
                    // 以防万一，bugly收到过崩溃，原因不明，
                    s = "";
                }
                // TODO: 这样匹配的话正常消息里的&8824也会被替换掉，
                if (s.contains("&8824")) {// 草稿
                    s = s.replaceFirst("&8824", "");

                    tip_tv.setText(getString(R.string.draft));
                    tip_tv.setVisibility(View.VISIBLE);
                }
                CharSequence content = HtmlUtils.addSmileysToMessage(
                        ChatMessage.getSimpleContent(requireContext(), friend.getType(), s),
                        false);
                content_tv.setText(content);
            } else {
                content_tv.setText(HtmlUtils.addSmileysToMessage(
                        ChatMessage.getSimpleContent(requireContext(), friend.getType(), friend.getContent()),
                        false));
            }

            // 搜索下匹配关键字高亮显示
            if (search) {
                String text = content_tv.getText().toString();
                SpannableString spannableString = StringUtils.matcherSearchTitle(Color.parseColor("#fffa6015"),
                        text, mEditText.getText().toString());
                content_tv.setText(spannableString);
            }

            time_tv.setText(TimeUtils.getFriendlyTimeDesc(getActivity(), friend.getTimeSend()));
            UiUtils.updateNum(num_tv, friend.getUnReadNum());

            if (num_tv.getVisibility() == View.VISIBLE) {
                replay_iv.setVisibility(View.GONE);
            } else {
                replay_iv.setVisibility(View.VISIBLE);
            }
            if (friend.getUserId().equals(Friend.ID_SK_PAY)) {
                replay_iv.setVisibility(View.GONE);
            }

            if (friend.getOfflineNoPushMsg() == 1) {
                not_push_ll.setVisibility(View.VISIBLE);
            } else {
                not_push_ll.setVisibility(View.GONE);
            }

            final long time = friend.getTopTime();

            if (time == 0) {
                rl_warp.setBackgroundResource(R.drawable.list_selector_background_ripple);
            } else {
                rl_warp.setBackgroundResource(R.color.grey_200_contact);
            }

            // 点击头像跳转详情
            avatar.setOnClickListener(view -> {
                if (!UiUtils.isNormalClick(view)) {
                    return;
                }
                if (friend.getRoomFlag() == 0) {   // 个人
                    if (!friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)
                            && !friend.getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)
                            && !friend.getUserId().equals(Friend.ID_SK_PAY)
                            && friend.getIsDevice() != 1) {
                        Intent intent = new Intent(getActivity(), BasicInfoActivity.class);
                        intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                        startActivity(intent);
                    }
                } else {   // 群组
                    if (friend.getGroupStatus() == 0) {
                        Intent intent = new Intent(getActivity(), RoomInfoActivity.class);
                        intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                        startActivity(intent);
                    }
                }
            });

            iv_delete.setVisibility(flag ? View.VISIBLE : View.GONE);
            iv_delete.setOnClickListener(v -> {
                delete(friend);
                // 保留旧代码，
                // 内部和外部的mFriendList都要更新到，
                GroupMessageFragment.this.mFriendList.remove(position);
                mAdapter.setData(GroupMessageFragment.this.mFriendList);
            });

            replay_iv.setOnClickListener(v -> {
                // TODO: hint是上一条消息，如果有草稿可能会是草稿，
                DialogHelper.verify(
                        requireActivity(),
                        getString(R.string.title_replay_place_holder, nick_name_tv.getText().toString()),
                        content_tv.getText().toString(),
                        "", ChatBottomView.LIMIT_MESSAGE_LENGTH,
                        new VerifyDialog.VerifyClickListener() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void send(String str) {
                                str = str.trim();
                                if (TextUtils.isEmpty(str)) {
                                    ToastUtil.showToast(requireContext(), R.string.tip_replay_empty);
                                    return;
                                }
                                if (!coreManager.isLogin()) {
                                    Reporter.unreachable();
                                    ToastUtil.showToast(requireContext(), R.string.tip_xmpp_offline);
                                    return;
                                }
                                if (friend.getRoomFlag() != 0) {
                                    // 用户可能不在群组里，
                                    int status = friend.getGroupStatus();
                                    if (1 == status) {
                                        ToastUtil.showToast(requireContext(), R.string.tip_been_kick);
                                        return;
                                    } else if (2 == status) {
                                        ToastUtil.showToast(requireContext(), R.string.tip_disbanded);
                                        return;
                                    } else if (3 == status) {
                                        ToastUtil.showToast(requireContext(), R.string.tip_group_disable_by_service);
                                        return;
                                    }
                                    RoomMember member = RoomMemberDao.getInstance().getSingleRoomMember(friend.getRoomId(), mLoginUserId);
                                    boolean isAllShutUp = PreferenceUtils.getBoolean(mContext, Constants.GROUP_ALL_SHUP_UP + friend.getUserId(), false);
                                    // 判断禁言状态，
                                    if (member != null && member.getRole() == 3) {// 普通成员需要判断是否被禁言
                                        if (friend.getRoomTalkTime() > (System.currentTimeMillis() / 1000)) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                        if (isAllShutUp) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                    } else if (member == null) {// 也需要判断是否被禁言
                                        if (friend.getRoomTalkTime() > (System.currentTimeMillis() / 1000)) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                        if (isAllShutUp) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                    }
                                    if (member != null && member.getRole() == 4) {
                                        ToastUtil.showToast(mContext, getString(R.string.hint_invisible));
                                        return;
                                    }
                                }
                                ChatMessage message = new ChatMessage();
                                // 文本类型，抄自，
                                // com.oort.weichat.ui.message.ChatActivity.sendText
                                // com.oort.weichat.ui.message.MucChatActivity.sendText
                                // 黑名单没考虑，正常情况黑名单会删除会话，
                                message.setType(XmppMessage.TYPE_TEXT);
                                message.setFromUserId(mLoginUserId);
                                message.setFromUserName(coreManager.getSelf().getNickName());
                                message.setContent(str);
                                // 获取阅后即焚状态(因为用户可能到聊天设置界面 开启/关闭 阅后即焚，所以在onResume时需要重新获取下状态)
                                int isReadDel = PreferenceUtils.getInt(mContext, Constants.MESSAGE_READ_FIRE + friend.getUserId() + mLoginUserId, 0);
                                message.setIsReadDel(isReadDel);
                                if (1 != friend.getRoomFlag()) {
                                    PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(requireContext());
                                    boolean isSupport = privacySetting.getMultipleDevices() == 1;
                                    if (isSupport) {
                                        message.setFromId("android");
                                    } else {
                                        message.setFromId("youjob");
                                    }
                                }
                                if (1 == friend.getRoomFlag()) {
                                    // 是群聊，
                                    if (friend.getIsLostChatKeyGroup() == 1) {
                                        ToastUtil.showToast(mContext, getString(R.string.is_lost_key_cannot_support_send_msg, friend.getNickName()));
                                        return;
                                    }
                                    message.setToUserId(friend.getUserId());
                                    if (friend.getChatRecordTimeOut() == -1 || friend.getChatRecordTimeOut() == 0) {// 永久
                                        message.setDeleteTime(-1);
                                    } else {
                                        long deleteTime = TimeUtils.sk_time_current_time() + (long) (friend.getChatRecordTimeOut() * 24 * 60 * 60);
                                        message.setDeleteTime(deleteTime);
                                    }
                                } else if (friend.getIsDevice() == 1) {
                                    message.setToUserId(mLoginUserId);
                                    message.setToId(friend.getUserId());
                                    // 我的设备消息不过期？
                                } else {
                                    message.setToUserId(friend.getUserId());

                                    // sz 消息过期时间
                                    if (friend.getChatRecordTimeOut() == -1 || friend.getChatRecordTimeOut() == 0) {// 永久
                                        message.setDeleteTime(-1);
                                    } else {
                                        long deleteTime = TimeUtils.sk_time_current_time() + (long) (friend.getChatRecordTimeOut() * 24 * 60 * 60);
                                        message.setDeleteTime(deleteTime);
                                    }
                                }
                                message.setReSendCount(ChatMessageDao.fillReCount(message.getType()));
                                message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                                message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                                message.setGroup(friend.getRoomFlag() != 0);// 需要设置一下这个，不然saveNewSingleChatMessage内的updateFriendContent有差
                                // 消息保存在数据库，
                                ChatMessageDao.getInstance().saveNewSingleChatMessage(message.getFromUserId(), friend.getUserId(), message);
                                for (Friend mFriend : mFriendList) {
                                    if (mFriend.getUserId().equals(friend.getUserId())) {
                                        mFriend.setType(XmppMessage.TYPE_TEXT);
                                        if (1 == friend.getRoomFlag()) {
                                            coreManager.sendMucChatMessage(message.getToUserId(), message);
                                            mFriend.setContent(message.getFromUserName() + " : " + message.getContent());
                                        } else {
                                            coreManager.sendChatMessage(message.getToUserId(), message);
                                            mFriend.setContent(message.getContent());
                                        }
                                        // 需要更新timeSend，不然不会重新排序
                                        mFriend.setTimeSend(message.getTimeSend());
                                        // 清除小红点，
                                        clearMessageNum(mFriend);
                                        mAdapter.updateContent(mFriend);
                                        break;
                                    }
                                }
                            }
                        });
            });
        }
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
        params.put("access_token", coreManager.getSelfStatus().accessToken);
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
                        if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkSuccess(mContext, result)) {
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
        params.put("access_token", coreManager.getSelfStatus().accessToken);
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
