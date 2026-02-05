package com.oort.weichat.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.PrivacySetting;
import com.oort.weichat.bean.RoomMember;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.RoomMemberDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.pay.new_ui.PaymentOrReceiptActivity;
import com.oort.weichat.pay.sk.SKPayActivity;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.groupchat.FaceToFaceGroup;
import com.oort.weichat.ui.groupchat.SelectContactsActivity;
import com.oort.weichat.ui.me.NearPersonActivity;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.message.multi.RoomInfoActivity;
import com.oort.weichat.ui.nearby.PublicNumberSearchActivity;
import com.oort.weichat.ui.nearby.UserSearchActivity;
import com.oort.weichat.ui.search.SearchAllActivity;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.DisplayUtil;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.view.ChatBottomView;
import com.oort.weichat.view.HeadView;
import com.oort.weichat.view.MessagePopupWindow;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.VerifyDialog;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.XmppConnectionManager;
import com.oort.weichat.xmpp.listener.AuthStateListener;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.swiperecyclerview.OnItemMenuClickListener;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenu;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuBridge;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuCreator;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeMenuItem;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Role;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

/**
 * 消息界面（带分组筛选功能，支持label分组侧滑移除标签）
 */
public class MessageFragment extends EasyFragment implements AuthStateListener {
    // 消息界面在前台展示中就不响铃新消息
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
    private List<Friend> mOriginalFriendList; // 原始数据列表（未筛选）
    private String mLoginUserId;
    private MessagePopupWindow mMessagePopupWindow;
    private TextView mTvTitleLeft;
    private TextView tv_title_right;
    private String mCurrentGroupId = "all"; // 当前选中的分组ID

    // 刷新的定时器，限制过快刷新
    private final RefreshTimer refreshTimer = new RefreshTimer();
    private final Map<String, RefreshTimer> timerMap = new HashMap<>();

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
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
            } else if (action.equals(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE)) {// 刷新单个消息
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
     * 菜单创建器（新增：label分组时显示「移除标签」选项）
     */
    private final SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = DisplayUtil.dip2px(requireContext(), 80f);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 右侧菜单
            {
                Friend friend = mAdapter.getItem(position);
                final long time = friend.getTopTime();
                boolean isLabelGroup = "label".equals(mCurrentGroupId); // 判断是否为标签分组

                // 1. 置顶/取消置顶（原有逻辑）
                SwipeMenuItem top = new SwipeMenuItem(requireContext())
                        .setBackgroundColorResource(R.color.Grey_400)
                        .setText(time == 0 ? getString(R.string.top) : getString(R.string.cancel_top))
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                if (friend.getIsDevice() != 1) {
                    swipeRightMenu.addMenuItem(top);
                }

                // 2. 标为已读/未读（原有逻辑）
                SwipeMenuItem markUnread = new SwipeMenuItem(requireContext())
                        .setBackgroundColorResource(R.color.color_read_unread_item)
                        .setText(friend.getUnReadNum() > 0 ? getString(R.string.mark_read) : getString(R.string.mark_unread))
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markUnread);

                // 新增：3. 移除标签（仅label分组显示）
                if (isLabelGroup) {
                    SwipeMenuItem removeLabel = new SwipeMenuItem(requireContext())
                            .setBackgroundColorResource(R.color.colorAccent) // 区别于其他菜单的颜色
                            .setText(getString(R.string.remove_label))
                            .setTextColor(Color.WHITE)
                            .setTextSize(15)
                            .setWidth(width)
                            .setHeight(height);
                    swipeRightMenu.addMenuItem(removeLabel);
                }

//

                // 4. 删除（原有逻辑，label分组时位置后移）
                SwipeMenuItem delete = new SwipeMenuItem(requireContext())
                        .setBackgroundColorResource(R.color.redpacket_bg)
                        .setText(R.string.delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(15)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(delete);
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听（新增：label分组处理「移除标签」）
     */
    private final OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection();
            int menuPosition = menuBridge.getPosition();
            boolean isLabelGroup = "label".equals(mCurrentGroupId); // 判断是否为标签分组

            if(mAdapter.getData() == null || mAdapter.getData().size() <= position){
                return;
            }
            Friend friend = mAdapter.getData().get(position);
            if (friend.getIsDevice() == 1) {
                menuPosition = menuPosition + 1;
            }

            // 仅处理右侧菜单
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                // 原有逻辑：置顶（位置0）
                if (menuPosition == 0) {
                    updateTopChatStatus(friend);
                    OperLogUtil.msg("将"+friend.getNickName()+"的聊天置顶");
                }
                // 原有逻辑：标为已读/未读（位置1）
                else if (menuPosition == 1) {
                    if (friend.getUnReadNum() > 0) {
                        clearMessageNum(friend);
                        OperLogUtil.msg("将"+friend.getNickName()+"的聊天设为已读");
                    } else {
                        // 使用AsyncUtils在后台执行，并通过uiThread切换回主线程
                        AsyncUtils.doAsync(MessageFragment.this, context -> {
                            FriendDao.getInstance().markUserMessageUnRead(mLoginUserId, friend.getUserId());
                            // 切回主线程更新UI
                            context.uiThread(fragment -> {
                                MsgBroadcast.broadcastMsgNumUpdate(MyApplication.getInstance(), true, 1);
                                MsgBroadcast.broadcastMsgUiUpdate(MyApplication.getInstance());
                                OperLogUtil.msg("将"+friend.getNickName()+"的聊天设为未读");
                            });
                        });
                    }
                }
                // 新增：label分组时，menuPosition=2是「移除标签」
                else if (isLabelGroup && menuPosition == 2) {
                    removeFriendFromLabel(friend);
                    OperLogUtil.msg("将"+friend.getNickName()+"从当前标签中移除");
                }
                // 原有逻辑：删除（label分组位置3，非label分组位置2）
                else {
                    int deleteMenuPos = isLabelGroup ? 3 : 2;
                    if (menuPosition == deleteMenuPos) {
                        delete(friend);
                        mFriendList.remove(position);
                        mOriginalFriendList.remove(friend);
                        mAdapter.setData(mFriendList);
                        OperLogUtil.msg("将"+friend.getNickName()+"的聊天删除");
                    }
                }
            }
        }
    };
    private String mLabGroupId;

    /**
     * 从当前标签中移除好友（label分组专属逻辑）
     */
    private void removeFriendFromLabel(Friend friend) {
        // 1. 校验必要数据
        if (friend == null || TextUtils.isEmpty(friend.getSid())) {
            ToastUtil.showToast(requireContext(), R.string.tip_data_error);
            return;
        }

        delChatFromLabelName(friend.getSid(),mLabGroupId);
        // 2. 获取当前标签关联的chatIds（根据项目实际存储逻辑修改，此处示例用Preference）
        String labelChatIdsStr = PreferenceUtils.getString(requireContext(), "current_label_chat_ids", "");
        List<String> currentChatIds = new ArrayList<>();
        if (!TextUtils.isEmpty(labelChatIdsStr)) {
            currentChatIds = new Gson().fromJson(labelChatIdsStr, new TypeToken<List<String>>() {}.getType());
        }

        // 3. 移除当前好友的sid并更新存储
        if (currentChatIds.contains(friend.getSid())) {
            currentChatIds.remove(friend.getSid());
            // 同步更新存储的chatIds
            PreferenceUtils.putString(
                    requireContext(),
                    "current_label_chat_ids",
                    new Gson().toJson(currentChatIds)
            );
            // 重新筛选列表
            filterMessageByChatIds(currentChatIds,mLabGroupId);
            // 提示成功
            ToastUtil.showToast(requireContext(), String.format(getString(R.string.tip_remove_label_success), friend.getNickName()));
        } else {
            ToastUtil.showToast(requireContext(), R.string.tip_friend_not_in_label);
        }
    }


    private void delChatFromLabelName(String chatId,String groupId) {
        // 这里模拟调用更新标签名称的接口，实际需替换为真实接口调用
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        // 假设当前要更新的标签ID为 labelId，需根据实际情况获取
        params.put("groupId", groupId);
        params.put("summaryId", chatId);

        HttpUtils.post().url(Constant.IM_API_BASE + "room/conversation/groups/summary/delete")
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            // 刷新标签列表
                        } else {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    private void refresh(String friendId) {
        if (TextUtils.isEmpty(friendId)) {
            refresh();
            return;
        }

        // 使用AsyncUtils在后台查询单条数据
        AsyncUtils.doAsync(this, context -> {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, friendId);
            // 切回主线程更新UI
            context.uiThread(fragment -> {
                if (friend != null && !fragment.mAdapter.updateContent(friend)) {
                    fragment.refresh();
                }
            });
        });
    }

    public static List getPns(){
        String mLoginUserId = UserInfoUtils.getInstance(CommonApplication.getAppContext()).getUserId();
        String  record =  FastSharedPreferences.get("httpRes").getString("publicNumbers_" + mLoginUserId,"");
        List ls = new ArrayList();
        if(record.length() > 0){
            Result<List<Role>> result = new Gson().fromJson(record,  new TypeToken<Result<List<Role>>>() {}.getType());
            List<Role> datas = result.getData();
            for(Role r : datas){
                ls.add(String.valueOf(r.getUserId()));
            }
        }else{
            String [] dt = {"10017781","10017664","10017663","10000310","10000309","10000269","10000268","10000270","10000267","10000193","10000191","10000"};
            ls.addAll(Arrays.asList(dt));
        }
        return ls;
    }

    private void refresh() {
        if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            mEditText.setText("");// 清空搜索时重新加载
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
        DialogHelper.showDefaulteMessageProgressDialog(getActivity());
        initActionBar();
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
            mTvTitle.setText(getString(R.string.message));
            mTvTitleLeft.setVisibility(View.GONE);
            mIvFriendRight.setVisibility(View.GONE);
            mIvTitleRight.setVisibility(View.GONE);
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
        }
        coreManager.getChatList();
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
        mOriginalFriendList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (mHeadView != null) {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (!TextUtils.isEmpty(str)) {
                    queryChatMessage(str);
                } else {
                    filterMessageByGroup(mCurrentGroupId); // 清空搜索时用当前分组筛选
                    updataListView();
                }
            }
        });

        ListenerManager.getInstance().addAuthStateChangeListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE);
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE);
        intentFilter.addAction(Constants.NOTIFY_MSG_SUBSCRIPT);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Constants.NOT_AUTHORIZED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mUpdateReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }
    }

    /**
     * 加载朋友数据并根据当前分组筛选 - 适配AsyncUtils
     */
    private void loadDatas() {
        mFriendList.clear();
        mOriginalFriendList.clear();

        for (Map.Entry<String, RefreshTimer> entry : timerMap.entrySet()) {
            entry.getValue().cancel();
        }
        timerMap.clear();
        search = false;

        // 使用AsyncUtils在后台加载数据
        AsyncUtils.doAsync(this, context -> {
            // 1. 在后台获取原始数据
            List<Friend> originalList = FriendDao.getInstance().getNearlyFriendMsg(mLoginUserId);

            // 移除不需要的系统消息
            List<Friend> removeList = new ArrayList<>();
            if (originalList.size() > 0) {
                for (Friend friend : originalList) {
                    if (friend != null && friend.getUserId().equals("10001")) {
                        removeList.add(friend);
                    }
                }
                originalList.removeAll(removeList);
            }

            // 保存到临时变量，用于主线程处理
            final List<Friend> resultList = new ArrayList<>(originalList);

            // 2. 切回主线程处理结果
            context.uiThread(fragment -> {
                fragment.mOriginalFriendList.addAll(resultList);
                // 3. 根据当前分组筛选
                fragment.filterMessageByGroup(fragment.mCurrentGroupId);
                fragment.mListView.post(() -> {
                    fragment.updataListView();
                    fragment.mRefreshLayout.finishRefresh();
                });
            });
        });
    }

    /**
     * 根据分组ID筛选消息列表
     * @param groupId 分组ID："all"（全部）、"unread"（未读）、"single"（单聊）、"group"（群聊）、"label"（标签）
     */
    public void filterMessageByGroup(String groupId) {
        mCurrentGroupId = groupId;
        mFriendList.clear();

        if (TextUtils.isEmpty(groupId) || "all".equals(groupId)) {
            // 全部消息
            mFriendList.addAll(mOriginalFriendList);
        } else if ("unread".equals(groupId)) {
            // 未读消息
            for (Friend friend : mOriginalFriendList) {
                if (friend.getUnReadNum() > 0) {
                    mFriendList.add(friend);
                }
            }
        } else if ("single".equals(groupId)) {
            // 单聊消息（非群聊）
            for (Friend friend : mOriginalFriendList) {
                if (friend.getRoomFlag() == 0) {
                    mFriendList.add(friend);
                }
            }
        } else if ("group".equals(groupId)) {
            // 群聊消息
            for (Friend friend : mOriginalFriendList) {
                if (friend.getRoomFlag() != 0) {
                    mFriendList.add(friend);
                }
            }
        }
        else if ("group_collect".equals(groupId)) {
            // 群聊消息
            for (Friend friend : mOriginalFriendList) {
                if (friend.getIsCollect() != 0) {
                    mFriendList.add(friend);
                }
            }
        }
        mListView.post(() -> {
            updataListView();
            mRefreshLayout.finishRefresh();
        });
    }

    /**
     * 根据chatIds筛选消息列表（标签分组专用）
     */
    public void filterMessageByChatIds(List chatIds,String labGroupId) {
        mLabGroupId = labGroupId;
        mCurrentGroupId = "label";
        mFriendList.clear();

        for (Friend friend : mOriginalFriendList) {
            if (chatIds.contains(friend.getSid())) {
                mFriendList.add(friend);
            }
        }

        mListView.post(() -> {
            updataListView();
            mRefreshLayout.finishRefresh();
            // 标签分组空数据提示
            if (mFriendList.isEmpty()) {
                mIvNoData.setImageResource(R.drawable.no_data_for_the_time_being);
                mIvNoData.setContentDescription(getString(R.string.tip_no_label_friend));
            }
        });
    }

    /**
     * 外部调用：切换分组
     */
    public void switchGroup(String groupId) {
        if (TextUtils.equals(mCurrentGroupId, groupId)) {
            return;
        }
        // 重置搜索状态
        if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            mEditText.setText("");
        }
        filterMessageByGroup(groupId);
        updataListView();
    }

    private void clearMessageNum(Friend friend) {
        friend.setUnReadNum(0);
        // 使用AsyncUtils在后台执行
        AsyncUtils.doAsync(this, context -> {
            FriendDao.getInstance().markUserMessageRead(mLoginUserId, friend.getUserId());
            // 切回主线程更新UI
            context.uiThread(fragment -> {
                if (fragment.getActivity() != null && fragment.getActivity() instanceof MainActivity) {
                    ((MainActivity) fragment.getActivity()).updateNumData();
                }
                fragment.mAdapter.updateUnReadNum(friend);
            });
        });
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
                                // 使用AsyncUtils在后台执行批量删除
                                AsyncUtils.doAsync(MessageFragment.this, context -> {
                                    for (Friend friend : mFriendList) {
                                        delete(friend);
                                    }
                                    // 切回主线程更新UI
                                    context.uiThread(fragment -> {
                                        fragment.mFriendList.clear();
                                        fragment.mOriginalFriendList.clear();
                                        fragment.mAdapter.setData(fragment.mFriendList);
                                    });
                                });
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
        } else if (id == R.id.search_public_number) {
            mMessagePopupWindow.dismiss();
            PublicNumberSearchActivity.start(requireContext());
        } else if (id == R.id.create_group) {
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), SelectContactsActivity.class));
        } else if (id == R.id.face_group) {
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), FaceToFaceGroup.class));
        } else if (id == R.id.add_friends) {
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), UserSearchActivity.class));
        } else if (id == R.id.scanning) {
            mMessagePopupWindow.dismiss();
            MainActivity.requestQrCodeScan(getActivity());
        } else if (id == R.id.receipt_payment) {
            mMessagePopupWindow.dismiss();
            PaymentOrReceiptActivity.start(getActivity(), coreManager.getSelf().getUserId());
        } else if (id == R.id.near_person) {
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(getActivity(), NearPersonActivity.class));
        } else if (id == R.id.net_error_ll) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 查询聊天记录（在当前分组内搜索）- 适配AsyncUtils
     */
    private void queryChatMessage(String str) {
        // 使用AsyncUtils在后台执行搜索
        AsyncUtils.doAsync(this, context -> {
            List<Friend> data = new ArrayList<>();
            // 仅在当前分组范围内搜索
            for (Friend friend : mFriendList) {
                List<Friend> friends = ChatMessageDao.getInstance().queryChatMessageByContent(friend, str);
                if (friends != null && friends.size() > 0) {
                    data.addAll(friends);
                }
            }
            // 切回主线程更新UI
            context.uiThread(fragment -> {
                fragment.mFriendList.clear();
                fragment.search = true;
                fragment.mFriendList.addAll(data);
                fragment.updataListView();
            });
        });
    }

    /**
     * 更新列表
     */
    private void updataListView() {
        mAdapter.setData(mFriendList);
        DialogHelper.dismissProgressDialog();
        mIvNoData.setVisibility(mFriendList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * xmpp在线状态监听
     */
    @Override
    public void onAuthStateChange(int authState) {
        authState = XmppConnectionManager.mXMPPCurrentState;
        if(Constant.IsSingleMsgTab){
            return;
        }
        if (mTvTitle == null) {
            return;
        }
        if (authState == 0 || authState == 1) {
            findViewById(R.id.pb_title_center).setVisibility(View.VISIBLE);
            mTvTitle.setText(getString(R.string.msg_view_controller_going_off));
        } else if (authState == 2) {
            MainActivity.isAuthenticated = true;
            findViewById(R.id.pb_title_center).setVisibility(View.GONE);
            mTvTitle.setText(getString(R.string.msg_view_controller_online));
            mNetErrorLl.setVisibility(View.GONE);
        } else {
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
                            // 使用AsyncUtils在后台更新数据库
                            AsyncUtils.doAsync(MessageFragment.this, context -> {
                                if (friend.getTopTime() == 0) {
                                    FriendDao.getInstance().updateTopFriend(friend.getUserId(), friend.getTimeSend());
                                } else {
                                    FriendDao.getInstance().resetTopFriend(friend.getUserId());
                                }
                                // 切回主线程刷新列表
                                context.uiThread(fragment -> fragment.loadDatas());
                            });
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
        params.put("type", String.valueOf(0));
        params.put("toUserId", userId);

        HttpUtils.get().url(coreManager.getConfig().EMPTY_SERVER_MESSAGE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {}

                    @Override
                    public void onError(Call call, Exception e) {}
                });
    }

    void delete(Friend friend) {
        String mLoginUserId = coreManager.getSelf().getUserId();
        // 使用AsyncUtils在后台执行删除
        AsyncUtils.doAsync(this, context -> {
            if (friend.getRoomFlag() == 0) {
                boolean isSlideClearServerMSG = PrivacySettingHelper.getPrivacySettings(getActivity()).getIsSkidRemoveHistoryMsg() == 1;
                if (isSlideClearServerMSG) {
                    emptyServerMessage(friend.getUserId());
                }
            }
            FriendDao.getInstance().resetFriendMessage(mLoginUserId, friend.getUserId());
            ChatMessageDao.getInstance().deleteMessageTable(mLoginUserId, friend.getUserId());
            final int unReadNum = friend.getUnReadNum();

            // 切回主线程发送广播
            context.uiThread(fragment -> {
                if (unReadNum > 0) {
                    MsgBroadcast.broadcastMsgNumUpdate(getActivity(), false, unReadNum);
                }
            });
        });
    }

    private class RefreshTimer extends CountDownTimer {
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
        public void onTick(long millisUntilFinished) {}

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

        private List<Friend> mFriendLists = new ArrayList<>();

        @NonNull
        @Override
        public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_nearly_message, viewGroup, false);
            return new MessageListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageListViewHolder messageListViewHolder, int position) {
            Friend friend = mFriendLists.get(position);
            messageListViewHolder.bind(friend, position);
        }

        public Friend getItem(int position) {
            return mFriendLists.get(position);
        }

        @Override
        public int getItemCount() {
            return mFriendLists.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public List<Friend> getData() {
            return mFriendLists;
        }

        public void setData(List<Friend> mFriendList) {
            this.mFriendLists = mFriendList;
            notifyDataSetChanged();
            mIvNoData.setVisibility(mFriendList.size() == 0 ? View.VISIBLE : View.GONE);
        }

        boolean updateContent(Friend updateFriend) {
            if (updateFriend == null) {
                return false;
            }
            int newPosition = -1;
            int oldPosition = -1;
            for (int i = 0; i < mFriendLists.size(); i++) {
                Friend friend = mFriendLists.get(i);
                if (newPosition < 0 && friend.getTopTime() <= updateFriend.getTopTime() && friend.getTimeSend() <= updateFriend.getTimeSend()) {
                    newPosition = i;
                }
                if (TextUtils.equals(friend.getUserId(), updateFriend.getUserId())) {
                    oldPosition = i;
                    mFriendLists.set(i, updateFriend);
                    notifyItemChanged(i);
                    break;
                }
            }
            if (newPosition >= 0 && oldPosition >= 0 && newPosition != oldPosition) {
                Friend remove = mFriendLists.remove(oldPosition);
                mFriendLists.add(newPosition, remove);
                notifyDataSetChanged();
            }
            return oldPosition >= 0;
        }

        boolean updateUnReadNum(Friend updateFriend) {
            for (int i = 0; i < mFriendLists.size(); i++) {
                Friend mF = mFriendLists.get(i);
                if (TextUtils.equals(mF.getUserId(), updateFriend.getUserId())) {
                    mFriendLists.set(i, updateFriend);
                    notifyItemChanged(i);
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
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(findViewById(R.id.message_fragment).getWindowToken(), 0);
                }

                Intent intent = new Intent();
                if (friend.getRoomFlag() == 0) {
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
                    intent.putExtra("jilu_id", friend.getChatRecordTimeOut());
                } else {
                    intent.putExtra(Constants.NEW_MSG_NUMBER, friend.getUnReadNum());
                }
                startActivity(intent);
                clearMessageNum(friend);
            });

            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), friend, avatar);
            nick_name_tv.setText(!TextUtils.isEmpty(friend.getRemarkName()) ? friend.getRemarkName() : friend.getNickName());
            FastSharedPreferences.get("USERINFO_SAVE").edit().putString("tousername",friend.getNickName()).apply();

            if (friend.getRoomFlag() != 0) {
                if (friend.getIsAtMe() == 1) {
                    tip_tv.setText(getString(R.string.some_at_me));
                    tip_tv.setVisibility(View.VISIBLE);
                } else if (friend.getIsAtMe() == 2) {
                    tip_tv.setText(getString(R.string.at_all));
                    tip_tv.setVisibility(View.VISIBLE);
                } else {
                    tip_tv.setVisibility(View.GONE);
                }
            } else {
                tip_tv.setVisibility(View.GONE);
            }

            if (friend.getType() == XmppMessage.TYPE_TEXT) {
                String s = friend.getContent() == null ? "" : friend.getContent();
                if (s.contains("&8824")) {
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

            if (search) {
                String text = content_tv.getText().toString();
                SpannableString spannableString = StringUtils.matcherSearchTitle(Color.parseColor("#fffa6015"),
                        text, mEditText.getText().toString());
                content_tv.setText(spannableString);
            }

            time_tv.setText(TimeUtils.getFriendlyTimeDesc(getActivity(), friend.getTimeSend()));
            UiUtils.updateNum(num_tv, friend.getUnReadNum());

            replay_iv.setVisibility(num_tv.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (friend.getUserId().equals(Friend.ID_SK_PAY)) {
                replay_iv.setVisibility(View.GONE);
            }

            not_push_ll.setVisibility(friend.getOfflineNoPushMsg() == 1 ? View.VISIBLE : View.GONE);

            final long time = friend.getTopTime();
            rl_warp.setBackgroundResource(time == 0 ? R.drawable.list_selector_background_ripple : R.color.grey_200_contact);

            // 点击头像跳转详情
            avatar.setOnClickListener(view -> {
                if (!UiUtils.isNormalClick(view)) {
                    return;
                }
                if (friend.getRoomFlag() == 0) {
                    if (!friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)
                            && !friend.getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)
                            && !friend.getUserId().equals(Friend.ID_SK_PAY)
                            && friend.getIsDevice() != 1) {
                        PersonDetailActivity.actionStart(mContext ,friend.getUserId());
                    }
                } else {
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
                mFriendList.remove(position);
                mOriginalFriendList.remove(friend);
                mAdapter.setData(mFriendList);
            });

            replay_iv.setOnClickListener(v -> {
                DialogHelper.verify(
                        requireActivity(),
                        getString(R.string.title_replay_place_holder, nick_name_tv.getText().toString()),
                        content_tv.getText().toString(),
                        "", ChatBottomView.LIMIT_MESSAGE_LENGTH,
                        new VerifyDialog.VerifyClickListener() {
                            @Override
                            public void cancel() {}

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
                                    if (member != null && member.getRole() == 3) {
                                        if (friend.getRoomTalkTime() > (System.currentTimeMillis() / 1000)) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                        if (isAllShutUp) {
                                            ToastUtil.showToast(mContext, getString(R.string.has_been_banned));
                                            return;
                                        }
                                    } else if (member == null) {
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
                                message.setType(XmppMessage.TYPE_TEXT);
                                message.setFromUserId(mLoginUserId);
                                message.setFromUserName(coreManager.getSelf().getNickName());
                                message.setContent(str);
                                int isReadDel = PreferenceUtils.getInt(mContext, Constants.MESSAGE_READ_FIRE + friend.getUserId() + mLoginUserId, 0);
                                message.setIsReadDel(isReadDel);
                                if (1 != friend.getRoomFlag()) {
                                    PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(requireContext());
                                    boolean isSupport = privacySetting.getMultipleDevices() == 1;
                                    message.setFromId(isSupport ? "android" : "youjob");
                                }
                                if (1 == friend.getRoomFlag()) {
                                    if (friend.getIsLostChatKeyGroup() == 1) {
                                        ToastUtil.showToast(mContext, getString(R.string.is_lost_key_cannot_support_send_msg, friend.getNickName()));                                        return;
                                    }
                                    message.setToUserId(friend.getUserId());
                                    if (friend.getChatRecordTimeOut() == -1 || friend.getChatRecordTimeOut() == 0) {
                                        message.setDeleteTime(-1);
                                    } else {
                                        long deleteTime = TimeUtils.sk_time_current_time() + (long) (friend.getChatRecordTimeOut() * 24 * 60 * 60);
                                        message.setDeleteTime(deleteTime);
                                    }
                                } else if (friend.getIsDevice() == 1) {
                                    message.setToUserId(mLoginUserId);
                                    message.setToId(friend.getUserId());
                                } else {
                                    message.setToUserId(friend.getUserId());
                                    if (friend.getChatRecordTimeOut() == -1 || friend.getChatRecordTimeOut() == 0) {
                                        message.setDeleteTime(-1);
                                    } else {
                                        long deleteTime = TimeUtils.sk_time_current_time() + (long) (friend.getChatRecordTimeOut() * 24 * 60 * 60);
                                        message.setDeleteTime(deleteTime);
                                    }
                                }
                                message.setReSendCount(ChatMessageDao.fillReCount(message.getType()));
                                message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                                message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                                message.setGroup(friend.getRoomFlag() != 0);

                                // 使用AsyncUtils保存消息并更新UI
                                AsyncUtils.doAsync(MessageFragment.this, context -> {
                                    ChatMessageDao.getInstance().saveNewSingleChatMessage(
                                            message.getFromUserId(), friend.getUserId(), message);

                                    // 切回主线程更新列表
                                    context.uiThread(fragment -> {
                                        for (Friend mFriend : fragment.mFriendList) {
                                            if (mFriend.getUserId().equals(friend.getUserId())) {
                                                mFriend.setType(XmppMessage.TYPE_TEXT);
                                                if (1 == friend.getRoomFlag()) {
                                                    coreManager.sendMucChatMessage(message.getToUserId(), message);
                                                    mFriend.setContent(message.getFromUserName() + " : " + message.getContent());
                                                } else {
                                                    coreManager.sendChatMessage(message.getToUserId(), message);
                                                    mFriend.setContent(message.getContent());
                                                }
                                                mFriend.setTimeSend(message.getTimeSend());
                                                fragment.clearMessageNum(mFriend);
                                                fragment.mAdapter.updateContent(mFriend);
                                                break;
                                            }
                                        }
                                    });
                                });
                            }
                        });
            });
        }
    }
}
