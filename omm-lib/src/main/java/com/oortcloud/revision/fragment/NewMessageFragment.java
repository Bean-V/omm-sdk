package com.oortcloud.revision.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.Area;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.MucgroupUpdateUtil;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.fragment.LabelSessionRelation;
import com.oort.weichat.fragment.LabelSessionSelectDialog;
import com.oort.weichat.fragment.MessageFragment;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.groupchat.FaceToFaceGroup;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.nearby.PublicNumberSearchActivity;
import com.oort.weichat.ui.nearby.UserSearchActivity;
import com.oort.weichat.ui.search.SearchAllActivity;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.secure.RSA;
import com.oort.weichat.util.secure.chat.SecureChatUtil;
import com.oort.weichat.view.MessagePopupWindow;
import com.oort.weichat.view.TipDialog;
import com.oortcloud.appstore.activity.AppManagerActivity;
import com.oortcloud.appstore.adapter.TableViewPagerAdapter;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/30 10:36
 * @version： v1.0
 * @function： 新版消息界面
 */

public class NewMessageFragment extends EasyFragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mGroupMenuBtn; // 对应iv_friend_right（分组菜单按钮）
    private ImageView mAddUser;
    private ImageView mBack;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mGroupRecycler;
    private Button mAddGroupBtn;
    private ImageButton mBtn_menu; // 仅保留一个实例
    private ChatTabBar mChatTabBar;
    String mLoginUserId = "";
    String chatKey = "";
    boolean mQuicklyCreate = false;
    List mSelectPositions = new ArrayList();
    private TableViewPagerAdapter mAdapter;
    private MessagePopupWindow mMessagePopupWindow;
    private Context mContext;
    private GroupAdapter mGroupAdapter;
    private List<MsgGroup> mGroupList = new ArrayList<>();
    private String mCurrentGroupId;  // 当前选中分组ID

    private MsgGroup mCurrentGroup;

    private List<Friend> mFriendList = new ArrayList<>();
    private List<Friend> mOriginalFriendList = new ArrayList<>();

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(MsgBroadcast.ACTION_MSG_UI_UPDATE)) {// 刷新页面
                loadDatas();
            }
        }
    };
    private String mLabGroupId;

    private void loadDatas() {
        mLoginUserId = UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getImuserid();
        mFriendList.clear();
        mOriginalFriendList.clear();

        // 1. 获取原始数据
        mOriginalFriendList = FriendDao.getInstance().getNearlyFriendMsg(mLoginUserId);

        // 移除不需要的系统消息
        List<Friend> mRemoveFriend = new ArrayList<>();
        if (mOriginalFriendList.size() > 0) {
            for (Friend friend : mOriginalFriendList) {
                if (friend != null && friend.getUserId().equals("10001")) {
                    mRemoveFriend.add(friend);
                }
            }
            mOriginalFriendList.removeAll(mRemoveFriend);
        }
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_message_new;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        mContext = getActivity();
        initActionBar();

        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mGroupMenuBtn = findViewById(R.id.iv_friend_right); // 绑定分组菜单按钮
        mAddUser = findViewById(R.id.add_img);
        mBack = findViewById(R.id.back);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mGroupRecycler = findViewById(R.id.group_recycler);
        mAddGroupBtn = findViewById(R.id.add_group_btn);
        mBtn_menu = findViewById(R.id.btn_menu); // 初始化菜单按钮
        mChatTabBar = findViewById(R.id.chat_tab_bar); // 初始化自定义聊天TabBar
        initView();
        initData();
        initGroupData();  // 初始化分组数据
        setListeners();   // 设置事件监听

        getGroupList();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE);
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE);
        intentFilter.addAction(Constants.NOTIFY_MSG_SUBSCRIPT);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Constants.NOT_AUTHORIZED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mUpdateReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }

        loadDatas();


        findViewById(R.id.iv_friend_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchAllActivity.start(requireActivity(), "friend");
            }
        });
    }

    // 初始化分组数据
    private void initGroupData() {
        // 初始化一级分组
        mGroupList.add(new MsgGroup("all", "全部", R.drawable.ic_msg_group_all, MsgGroup.TYPE_DEFAULT));
        mGroupList.add(new MsgGroup("unread", "未读", R.drawable.ic_msg_group_unreae, MsgGroup.TYPE_DEFAULT));
        mGroupList.add(new MsgGroup("single", "单聊", R.drawable.ic_msg_group_single, MsgGroup.TYPE_DEFAULT));
        mGroupList.add(new MsgGroup("group", "群聊", R.drawable.ic_msg_group_groups, MsgGroup.TYPE_DEFAULT));
        mGroupList.add(new MsgGroup("group_collect", "收藏群聊", R.drawable.ic_msg_group_groups, MsgGroup.TYPE_DEFAULT));

// 初始化“标签”一级分组，并添加二级标签
        MsgGroup labelGroup = new MsgGroup("label", "标签", R.drawable.ic_msg_group_label, MsgGroup.TYPE_DEFAULT);
// 添加默认二级标签
//        labelGroup.addSubLabel(new MsgGroup("label_1", "工作", R.drawable.ic_sub_label));
//        labelGroup.addSubLabel(new MsgGroup("label_2", "生活", R.drawable.ic_sub_label));
//        labelGroup.addSubLabel(new MsgGroup("label_3", "重要", R.drawable.ic_sub_label));
        mGroupList.add(labelGroup);

// 初始化适配器（保持不变）
        mGroupAdapter = new GroupAdapter(mContext, mGroupList);
        mGroupRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mGroupRecycler.setAdapter(mGroupAdapter);

// 设置点击事件（关键：处理二级标签展开/新增）
        mGroupAdapter.setOnGroupClickListener(group -> {
            if (group.getGroupId() != null && group.getGroupId().equals("label")) {
                // 点击“标签”分组，展开/收起二级列表
                mGroupAdapter.toggleLabelExpand();
            } else if (group.getType() == MsgGroup.TYPE_SUB_LABEL) {
                // 点击二级标签（如“工作”）
                mChatTabBar.setVisibility(View.GONE);
                TextView tv_lab_name = findViewById(R.id.tv_group);
                tv_lab_name.setText("标签");

                mCurrentGroup = group;
                handleSubLabelClick(group);
                mDrawerLayout.closeDrawer(GravityCompat.START);

            } else {
                findViewById(R.id.ll_lab).setVisibility(View.GONE);
                // 原有一级分组点击逻辑
                mCurrentGroupId = group.getGroupId();
                // 通知ViewPager中的Fragment更新数据
                if (mCurrentGroupId.equals("all")) {
                    mChatTabBar.setVisibility(View.VISIBLE);
                } else {
                    mChatTabBar.setVisibility(View.GONE);
                    TextView tv_lab_name = findViewById(R.id.tv_group);
                    tv_lab_name.setText(group.getName());
                }
                updateMessageByGroup(mCurrentGroupId);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

// 设置长按事件（用于删除二级标签）
        mGroupAdapter.setOnGroupLongClickListener(group -> {
            if (group.getType() == MsgGroup.TYPE_SUB_LABEL) {
                showDeleteSubLabelDialog(group);
            }
        });

    }

    // 处理二级标签点击
    private void handleSubLabelClick(MsgGroup subLabel) {
        if (subLabel.getGroupId() != null && subLabel.getGroupId().equals("add_label")) {
            // 点击“新增标签”按钮，显示输入框
            // showAddSubLabelDialog();
            showAddGroupDialog();
        } else {
            // 选择二级标签的逻辑（如筛选对应标签的消息）
            Toast.makeText(mContext, "选中标签：" + subLabel.getName(), Toast.LENGTH_SHORT).show();

            findViewById(R.id.ll_lab).setVisibility(View.VISIBLE);
            TextView tv_lab = findViewById(R.id.tv_lab_name);
            tv_lab.setText(subLabel.getName());


            getGroupChatList(subLabel.getGroupId());

        }
    }

    // 在需要显示选择对话框的地方（如Activity或Fragment中）
    private void showLabelSessionSelectDialog() {
        // 1. 准备目标标签ID（当前要添加会话的标签ID）
        String targetLabelId = mCurrentGroup.getGroupId(); // 实际应从标签数据中获取

        // 2. 获取所有可选会话列表（通常从消息列表或好友列表中获取）
        List<Friend> allSessions = getAvailableSessions(); // 自定义方法获取会话列表

        // 3. 获取该标签已关联的会话关系（用于初始化已选状态）
        List<LabelSessionRelation> existingRelations = getLabelExistingRelations(targetLabelId); // 从本地存储或网络获取

        // 4. 创建并显示对话框
        LabelSessionSelectDialog dialog = new LabelSessionSelectDialog(
                getContext(), // 上下文，Activity或Fragment的context
                targetLabelId,
                allSessions,
                existingRelations
        );

        // 5. 设置确认选择的回调
        dialog.setOnConfirmSelectListener(selectedRelations -> {
            // 处理选中的关联关系，通常需要保存到本地或上传到服务器
            saveLabelSessionRelations(targetLabelId, selectedRelations);
        });

        // 6. 显示对话框
        dialog.show();
    }

    // 示例：获取所有可选会话（实际实现需根据项目需求）
    private List<Friend> getAvailableSessions() {
//        List<Friend> sessions = new ArrayList<>();
        // 模拟数据 - 实际应从数据库或API获取
//        sessions.add(new Friend("user_001", "张三", "老张", 0)); // 单聊
//        sessions.add(new Friend("user_002", "李四", "", 0)); // 单聊
//        sessions.add(new Friend("group_001", "开发群", "", 1)); // 群聊
        return mOriginalFriendList;
    }

    // 示例：获取标签已关联的会话关系（实际实现需根据项目需求）
    private List<LabelSessionRelation> getLabelExistingRelations(String labelId) {
        List<LabelSessionRelation> relations = new ArrayList<>();
//        // 模拟数据 - 实际应从数据库或API获取
 //       relations.add(new LabelSessionRelation(labelId, "user_001", 0));
        return relations;
    }

    // 示例：保存标签与会话的关联关系（实际实现需根据项目需求）
    private void saveLabelSessionRelations(String labelId, List<LabelSessionRelation> relations) {
        // 1. 保存到本地数据库
        // 2. 上传到服务器同步
        // 3. 刷新相关UI展示
        addChatTOLabGroup(labelId, relations);
    }

    // 新增二级标签的对话框
    private void showAddSubLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("新增子标签");

        final EditText input = new EditText(mContext);
        input.setHint("请输入标签名称");
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String labelName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(labelName)) {
                // 找到标签分组，添加新的二级标签
                MsgGroup labelGroup = findLabelGroup();
                if (labelGroup != null) {
                    String labelId = "sub_label_" + System.currentTimeMillis();
                    labelGroup.addSubLabel(new MsgGroup(labelId, labelName, R.drawable.ic_sub_label));
                    // 刷新列表（保持展开状态）
                    mGroupAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(mContext, "标签名称不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 删除二级标签的对话框
    private void showDeleteSubLabelDialog(MsgGroup subLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除标签")
                .setMessage("确定要删除标签“" + subLabel.getName() + "”吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    MsgGroup labelGroup = findLabelGroup();
                    if (labelGroup != null) {
                        labelGroup.removeSubLabel(subLabel.getGroupId());
                        mGroupAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 查找标签分组
    private MsgGroup findLabelGroup() {
        for (MsgGroup group : mGroupList) {
            if (group.getGroupId().equals("label")) {
                return group;
            }
        }
        return null;
    }

    // 设置事件监听
    private void setListeners() {
        // 分组菜单按钮点击 - 打开侧滑菜单
//        mGroupMenuBtn.setOnClickListener(v ->
//                mDrawerLayout.openDrawer(GravityCompat.START));

        // 分组点击事件 - 切换消息筛选
//        mGroupAdapter.setOnGroupClickListener(group -> {
//            mCurrentGroupId = group.getId();
//            // 通知ViewPager中的Fragment更新数据
//            if(mCurrentGroupId.equals("all")){
//                mChatTabBar.setVisibility(View.VISIBLE);
//            }else{
//                mChatTabBar.setVisibility(View.GONE);
//            }
//            updateMessageByGroup(mCurrentGroupId);
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//        });

        // 分组长按事件 - 删除自定义分组
//        mGroupAdapter.setOnGroupLongClickListener(group -> {
//            showDeleteGroupDialog(group);
//            //return true;
//        });
//
//        // 新增分类按钮点击
//        mAddGroupBtn.setOnClickListener(v -> showAddGroupDialog());

        mBtn_menu.setOnClickListener(v -> {
            mDrawerLayout.openDrawer(GravityCompat.START);
            getGroupList();
        });

        // 标签栏选择事件
        mChatTabBar.setOnTabSelectedListener(tabIndex -> {
            // 根据选中的标签更新内容
            updateContentByTab(tabIndex);
        });

    }

    // 显示新增分组对话框
    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("新增标签");

        // 自定义输入框布局（含美化的 EditText）
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, mContext.getResources().getDisplayMetrics());
        layout.setPadding(padding, padding, padding, padding);

        final EditText input = new EditText(mContext);
        input.setHint("请输入标签名称");
        input.setHintTextColor(Color.parseColor("#999999")); // 提示文字浅灰色
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);   // 文字大小 16sp

        // 输入框背景（圆角、边框）
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(8); // 圆角半径 8dp
        drawable.setStroke(1, Color.parseColor("#E0E0E0")); // 边框 1dp 浅灰色
        drawable.setColor(Color.WHITE); // 输入框背景白色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            input.setBackground(drawable);
        } else {
            input.setBackgroundDrawable(drawable);
        }

        // 输入框内边距
        int editPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, mContext.getResources().getDisplayMetrics());
        input.setPadding(editPadding, editPadding, editPadding, editPadding);

        layout.addView(input);
        builder.setView(layout);

        // 确认按钮（主题色，突出操作）
        builder.setPositiveButton("确定", (dialog, which) -> {
            String groupName = input.getText().toString().trim();
            if (!TextUtils.isEmpty(groupName)) {
//                String groupId = "custom_" + System.currentTimeMillis();
//                mGroupList.add(new MsgGroup(
//                        groupId, groupName, R.drawable.ic_msg_group_groups_s, MsgGroup.TYPE_CUSTOM));
//                mGroupAdapter.notifyDataSetChanged();
                addLabGroup(groupName);
            } else {
                ToastUtil.showToast(mContext, "分组名称不能为空");
            }
        });
        // 取消按钮（灰色，弱化次要操作）
        builder.setNegativeButton("取消", null);

        // 显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 进一步美化按钮（可选，若想更精细控制）
        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positiveBtn != null) {
            positiveBtn.setTextColor(Color.parseColor("#1677FF")); // 主题蓝色
            positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        if (negativeBtn != null) {
            negativeBtn.setTextColor(Color.parseColor("#666666")); // 灰色
            negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        }

    }


    void getGroupChatList(String groupId) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", IMUserInfoUtil.getInstance().getUserId());
        params.put("groupId", groupId);


        HttpUtils.get().url(Constant.IM_API_BASE + "/room/conversation/groups/summary/list")
                .params(params)
                .build()
                .execute(new ListCallback<LabSession>(LabSession.class) {

                    @Override
                    public void onResponse(ArrayResult<LabSession> result) {
                        if (result.getResultCode() == 1) {

                            List chatIds = new ArrayList();
                            for (LabSession session : result.getData()) {
                                chatIds.add(session.getChatId());
                            }
                            updateMessageByChatIds(chatIds,groupId);
                        } else {
                            Toast.makeText(getContext(), result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    void getGroupList() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", IMUserInfoUtil.getInstance().getUserId());


        HttpUtils.get().url(Constant.IM_API_BASE + "room/conversation/groups/list")
                .params(params)
                .build()
                .execute(new ListCallback<MsgGroup>(MsgGroup.class) {

                    @Override
                    public void onResponse(ArrayResult<MsgGroup> result) {
                        if (result.getResultCode() == 1) {


                            MsgGroup labelGroup = findLabelGroup();
                            if (labelGroup != null) {
                                labelGroup.removeAllSubLabel();
                                for (MsgGroup mg : result.getData()) {
                                    labelGroup.addSubLabel(new MsgGroup(mg.getGroupId(), mg.getName(), R.drawable.ic_sub_label));
                                }
                                // 刷新列表（保持展开状态）
                                mGroupAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    void addLabGroup(String labStr) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", IMUserInfoUtil.getInstance().getUserId());
        params.put("groupName", labStr);
        params.put("sort", 1);


        HttpUtils.post().url(Constant.IM_API_BASE + "room/conversation/groups/save")
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast(mContext, "添加成功");
                            getGroupList();
                        } else {
                            Toast.makeText(getContext(), result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    void delLabGroup(String groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("groupId", groupId);


        HttpUtils.post().url(Constant.IM_API_BASE + "room/conversation/groups/delete")
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast(mContext, "添加成功");
                            getGroupList();
                        } else {
                            Toast.makeText(getContext(), result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    void addChatTOLabGroup(String groupId, List<LabelSessionRelation> relations) {


        // 1. 构建单个 summary 的 Map

        // 2. 构建 summary 数组
        List<Map<String, Object>> summaryItems = new ArrayList<>();

        for (LabelSessionRelation relation : relations) {
            Map<String, Object> summaryMap = new HashMap<>();
            summaryMap.put("chatId", relation.getSessionId());       // 实际业务值
            summaryMap.put("createTime", System.currentTimeMillis());
            summaryMap.put("groupId", groupId);
            // summaryMap.put("groupName", "技术部");
            // summaryMap.put("id", "summary_789");
            summaryMap.put("modifyTime", System.currentTimeMillis());
            // summaryMap.put("userId", 10001L);
            summaryItems.add(summaryMap);
        }
        // 3. 外层 Map，指定参数名为 "summaryList"
        Map<String, Object> params = new HashMap<>();
        params.put("summaryStr", JSON.toJSONString(summaryItems));  // 关键：参数名 "summaryL
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("groupId", groupId);


        HttpUtils.post().url(Constant.IM_API_BASE + "room/conversation/groups/summary/add")
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast(mContext, "添加成功");
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

    // 显示删除分组对话框
    private void showDeleteGroupDialog(MsgGroup group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除分组")
                .setMessage("确定要删除 " + group.getName() + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    mGroupList.remove(group);
                    mGroupAdapter.notifyDataSetChanged();
                    // 如果删除的是当前选中分组，自动切换到"全部消息"
                    if (group.getGroupId().equals(mCurrentGroupId)) {
                        mCurrentGroupId = "all";
                        updateMessageByGroup(mCurrentGroupId);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 根据分组更新消息列表
    private void updateMessageByGroup(String groupId) {
        // 获取ViewPager中的Fragment并调用刷新方法
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MessageFragment) {
                ((MessageFragment) fragment).filterMessageByGroup(groupId);
            } else if (fragment instanceof GroupMessageFragment) {
                ((GroupMessageFragment) fragment).filterMessageByGroup(groupId);
            }

        }
    }
    private void updateMessageByChatIds(List chatIds,String labGroupId) {
       // mLabGroupId = labGroupId;
        // 获取ViewPager中的Fragment并调用刷新方法
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MessageFragment) {
                ((MessageFragment) fragment).filterMessageByChatIds(chatIds,labGroupId);
            } else if (fragment instanceof GroupMessageFragment) {
                ((GroupMessageFragment) fragment).filterMessageByChatIds(chatIds);
            }

        }
    }

    private void initActionBar() {
    }

    private void initView() {




        mChatTabBar = findViewById(R.id.chat_tab_bar);

         mTabLayout = findViewById(R.id.tab_layout);
         mViewPager = findViewById(R.id.view_pager);
         //mIvFriend = findViewById(R.id.iv_friend_right);
         mAddUser = findViewById(R.id.add_img);
         mBack = findViewById(R.id.back);
        mAdapter = new TableViewPagerAdapter(getChildFragmentManager());
        mAdapter.reset(new String[]{getString(R.string.message), getString(R.string.task)});

        mAdapter.reset(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Tab 被选中时触发（切换到该 Tab 时）
                int position = tab.getPosition(); // 获取当前选中的 Tab 位置
                String tabText = tab.getText().toString(); // 获取 Tab 文字

                if(position == 0){
                    findViewById(R.id.ll_msg_menu).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.ll_msg_menu).setVisibility(View.GONE);

                }
                // 处理切换逻辑（例如刷新数据、更新UI等）
                //Log.d("Tab切换", "选中了第" + position + "个Tab：" + tabText);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Tab 从选中状态变为未选中时触发（可选实现）
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab 已选中时再次被点击触发（可选实现，例如刷新当前 Tab 数据）
            }
        });

        mBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatTabBar.setVisibility(View.VISIBLE);
                findViewById(R.id.ll_lab).setVisibility(View.GONE);
                updateMessageByGroup("all");
            }
        });
//        mIvFriend.setOnClickListener(v -> {
//
//            MessageEventChangeUI ev = new MessageEventChangeUI(2);
//            EventBus.getDefault().post(ev);
//            OperLogUtil.msg("消息界面点击通讯录");
//        });

        mAddUser.setOnClickListener(v -> {
            mMessagePopupWindow = new MessagePopupWindow(getActivity(), this, coreManager);
            mMessagePopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mMessagePopupWindow.showAsDropDown(v,
                    -(mMessagePopupWindow.getContentView().getMeasuredWidth() - v.getWidth() / 2 - 40),
                    0);
        });

        mChatTabBar.setTabSelected(0, false);


        findViewById(R.id.iv_lab_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLabelOperationBottomSheet();
            }
        });
    }


    // 显示标签操作底部弹窗的方法
    private void showLabelOperationBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_sheet_label_op, null);
        bottomSheetDialog.setContentView(view);

        Button btnRename = view.findViewById(R.id.btn_rename);
        Button btnAddChat = view.findViewById(R.id.btn_add_chat);
        Button btnDeleteLabel = view.findViewById(R.id.btn_delete_label);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // 重命名点击事件
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // 调用重命名标签的方法，这里需要传入当前标签对象等参数，根据实际业务补充
                renameLabel();
            }
        });

        // 添加聊天点击事件
        btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // 调用添加聊天到标签的方法，可复用之前的 LabelSessionSelectDialog
                showLabelSessionSelectDialog();
            }
        });

        // 删除标签点击事件
        btnDeleteLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // 调用删除标签的方法，这里需要传入当前标签对象等参数，根据实际业务补充
                deleteLabel();
            }
        });

        // 取消点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    // 重命名标签的方法（示例，需根据实际业务完善）
    private void renameLabel() {
        // 这里实现重命名标签的逻辑，比如弹出输入框让用户输入新名称，然后更新标签数据等
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("重命名标签");

        final EditText input = new EditText(mContext);
        // 可设置当前标签名称作为默认值，需根据实际选中的标签获取
        input.setHint("请输入新标签名称");
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!TextUtils.isEmpty(newName)) {
                    // 调用更新标签名称的接口或本地方法，根据实际业务补充
                    updateLabelName(newName);
                } else {
                    ToastUtil.showToast(mContext, "标签名称不能为空");
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 删除标签的方法（示例，需根据实际业务完善）
    private void deleteLabel() {
        // 这里实现删除标签的逻辑，比如弹出确认对话框，然后调用删除接口或本地方法
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除标签")
                .setMessage("确定要删除当前标签吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 调用删除标签的接口或本地方法，根据实际业务补充
                        // 例如：delLabGroup(当前标签ID);
                        ToastUtil.showToast(mContext, "标签删除成功");
                        // 刷新标签列表等操作

                        delLabGroup(mCurrentGroup.getGroupId());

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 更新标签名称的方法（示例，需根据实际业务完善，比如调用接口）
    private void updateLabelName(String newName) {
        // 这里模拟调用更新标签名称的接口，实际需替换为真实接口调用
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        // 假设当前要更新的标签ID为 labelId，需根据实际情况获取
        params.put("groupId", mCurrentGroup.getGroupId());
        params.put("groupName", newName);

        HttpUtils.post().url(Constant.IM_API_BASE + "room/conversation/groups/update")
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast(mContext, "重命名成功");
                            // 刷新标签列表
                            getGroupList();
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

    // 根据选中的标签更新内容
    private void updateContentByTab(int tabIndex) {
        String tabId = "";
        switch (tabIndex) {
            case ChatTabBar.TAB_ALL:
                tabId = "all";
                break;
            case ChatTabBar.TAB_UNREAD:
                tabId = "unread";
                break;
            case ChatTabBar.TAB_SINGLE:
                tabId = "single";
                break;
            case ChatTabBar.TAB_GROUP:
                tabId = "group";
                break;
        }

        updateMessageByGroup(tabId);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.create_task) {
            mMessagePopupWindow.dismiss();
            OperLogUtil.msg("消息界面发起任务");
            String packagename = "com.task_management.oort";
            Intent intent = new Intent(mContext, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", packagename);

            mContext.startActivity(intent);
        } else if (id == R.id.search_public_number) {// 搜索公众号
            mMessagePopupWindow.dismiss();
            PublicNumberSearchActivity.start(mContext);
        }else if (id == R.id.create_group) {
            OperLogUtil.msg("消息界面发起群聊");
            // 发起群聊
            mMessagePopupWindow.dismiss();
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
            startActivity(new Intent(mContext, FaceToFaceGroup.class));
        } else if (id == R.id.add_friends) {// 添加朋友
            OperLogUtil.msg("消息界面添加朋友");
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(mContext, UserSearchActivity.class));
        }else if (id == R.id.create_video) {// 添加朋友
            OperLogUtil.msg("消息界面开启会议");
            String appid = mContext.getApplicationInfo().processName;
            AppEventUtil.startApp(appid + ".vediomeet");
        }else if (id == R.id.scanning) {
            OperLogUtil.msg("消息界面扫一扫");
            // 扫一扫
            mMessagePopupWindow.dismiss();

            // 扫一扫
            mMessagePopupWindow.dismiss();

            int ACTION_REQUEST_PERMISSIONS = 0x001;
            String[] NEEDED_PERMISSIONS = new String[]{

                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE
            };
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
                return;
            }

//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (!Environment.isExternalStorageManager()) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                        startActivity(intent);
//
//                        return;
//                    }
//                }
            MainActivity.requestQrCodeScan(getActivity());
        }
    }

    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        //个人消息
        fragments.add(new MessageFragment());
        //群组消息（任务）

        //fragments.add(new MessageFragment());
        fragments.add( new GroupMessageFragment());

        return fragments;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

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



