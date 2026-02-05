package com.oort.weichat.fragment.vs;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jun.baselibrary.http.HttpUtils;
import com.jun.framelibrary.http.callback.HttpEngineCallBack;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.fragment.HomeFragment;
import com.oort.weichat.fragment.entity.ResArr;
import com.oort.weichat.fragment.vs.adapter.CameraAdapter;
import com.oort.weichat.fragment.vs.adapter.ContactAdapter;
import com.oort.weichat.fragment.vs.adapter.DispatchControlAdapter;
import com.oort.weichat.fragment.vs.adapter.FunctionGridAdapter;
import com.oort.weichat.fragment.vs.adapter.GroupChatAdapter;
import com.oort.weichat.fragment.vs.adapter.LERecorderAdapter;
import com.oort.weichat.fragment.vs.adapter.ObtainGroup;
import com.oort.weichat.fragment.vs.bean.DeviceList;
import com.oort.weichat.fragment.vs.bean.Tag;
import com.oort.weichat.fragment.vs.bean.TagUser;
import com.oort.weichat.fragment.vs.http.ApiConstants;
import com.oort.weichat.sortlist.BaseSortModel;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.util.Constants;
import com.oortcloud.appstore.adapter.TypeAppAllAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.widget.HistoryLaberView;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.contacts.http.HttpConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sound.wave.oort.OortAudioRecordRecActivity;

/**
 * 指挥调度Fragment
 */

public class ControlFragment extends EasyFragment {

    private RecyclerView rvFunctionGrid;
    private RecyclerView rvDispatchControls;
    private RecyclerView rvOnDutyLeader;
    private RecyclerView rvWatchatower;

    private TabLayout tlGroups;
    private RecyclerView mDeviceRV;
    private TextView mDeviceHint;
    private NestedScrollView mNestedScrollView;
    private ContactAdapter onDutyLeaderAdapter;
    private ContactAdapter watchtowerAdapter;
    private GroupChatAdapter groupChatAdapter;
    private CameraAdapter cameraAdapter;
    private LERecorderAdapter mLERecorderAdapter;
    private LinearLayout mLLSearch;
    private ImageView mIVSearch;
    private EditText mETSearch;
    private ImageView mIVScan;
    private ImageView mVoiceIV;
    private View mDispatchConsole;
    private View mDispatchConsoleSearch;
    // 当前选中的标签页
    private int currentTabPosition = 0;
    HomeFragment homeFragment;
    private RecyclerView mSearchRV;
    private HistoryLaberView mHistoryLaberView;
    private TypeAppAllAdapter mAllAppAdapter;
    private TextView  mTVEmpty;
    private TextView  mClearTV;
    private final static String TAG = ControlFragment.class.getSimpleName();

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 默认加载群组聊天数据
            loadGroupChatData();
        }
    };
    public ControlFragment(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
    }
    public ControlFragment(){
    }
    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_dispatch_console;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupChatAdapter = new GroupChatAdapter(this,new ArrayList<>());
        Log.e("zq", "onCreate---------");
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initViews(mRootView);
        Log.e("zq", "onActivityCreated0---------");
        setupRecyclerViews();
        initData();
        onClick();
        registerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(mUpdateReceiver);
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE);// 刷新页面Ui
        intentFilter.addAction(MsgBroadcast.ACTION_MSG_UI_UPDATE_SINGLE);// 刷新页面Ui
        intentFilter.addAction(Constants.NOTIFY_MSG_SUBSCRIPT);// 刷新"消息"角标
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 网络发生改变
        intentFilter.addAction(Constants.NOT_AUTHORIZED);// XMPP密码错误
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mUpdateReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }
    }
    private void initViews(View view) {
        rvFunctionGrid = view.findViewById(R.id.rv_function_grid);
        rvDispatchControls = view.findViewById(R.id.rv_dispatch_controls);
        rvOnDutyLeader = view.findViewById(R.id.rv_on_duty_leader);
        rvWatchatower = view.findViewById(R.id.rv_watchtower);

        tlGroups = view.findViewById(R.id.tb_groups);
        mDeviceRV = view.findViewById(R.id.rv_groups);
        mDeviceHint = view.findViewById(R.id.tv_device_hint);


        mNestedScrollView = view.findViewById(R.id.nested_scroll_view);

        mLLSearch = view.findViewById(R.id.ll_search);
        mIVSearch = view.findViewById(R.id.iv_search);
        mETSearch = view.findViewById(R.id.et_search);
        mIVScan = view.findViewById(R.id.iv_scan);
        mVoiceIV = view.findViewById(R.id.iv_voice);

        mDispatchConsole = view.findViewById(R.id.dispatch_console);
        mDispatchConsoleSearch = view.findViewById(R.id.dispatch_console_search);

        //单独抽取出来作为搜素
        mSearchRV = view.findViewById(R.id.rv_history);
        mSearchRV.setLayoutManager(new LinearLayoutManager(mContext));
        mAllAppAdapter = new TypeAppAllAdapter(mContext);
        mSearchRV.setAdapter(mAllAppAdapter);
        mHistoryLaberView = view.findViewById(R.id.history_lab_view);
        mTVEmpty = view.findViewById(R.id.tv_empty);
        mClearTV = view.findViewById(R.id.tv_clear);
    }
    private void setupRecyclerViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        // 功能网格
        rvFunctionGrid.setLayoutManager(layoutManager);

        // 调度台控制
        rvDispatchControls.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvDispatchControls.setAdapter(new DispatchControlAdapter(mContext));

        // 值班领导
        rvOnDutyLeader.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        onDutyLeaderAdapter = new ContactAdapter(getContext(), new ArrayList<>(),coreManager);
        rvOnDutyLeader.setAdapter(onDutyLeaderAdapter);

        // 值守台列表
        rvWatchatower.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        watchtowerAdapter = new ContactAdapter(getContext(), new ArrayList<>(),coreManager);
        rvWatchatower.setAdapter(watchtowerAdapter);

        // 群组列表
        mDeviceRV.setLayoutManager(new LinearLayoutManager(getContext()));

        cameraAdapter = new CameraAdapter(new ArrayList<>());

        mLERecorderAdapter = new LERecorderAdapter(this, new ArrayList<>());

        // 默认使用群组聊天适配器
        mDeviceRV.setAdapter(groupChatAdapter);
        //设置coreManager创建群组
        groupChatAdapter.setCoreManager(coreManager);
        
        // 设置TabLayout
        setupTabLayout();


        // 移除这个设置，让RecyclerView能够正常显示所有内容
        rvFunctionGrid.setNestedScrollingEnabled(false);
        mDeviceRV.setNestedScrollingEnabled(false);
    }
    
    private void setupTabLayout() {
//        isDeviceRVScrollBottom = false;
        if (tlGroups != null)
            tlGroups.removeAllTabs();
        // 添加标签页
        tlGroups.addTab(tlGroups.newTab().setText("对讲群组"));
        tlGroups.addTab(tlGroups.newTab().setText("摄像机"));
        tlGroups.addTab(tlGroups.newTab().setText("执法记录仪"));
//        tlGroups.addTab(tlGroups.newTab().setText("任务接收"));
        
        // 设置标签页选择监听器
        tlGroups.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 根据选中的标签页切换内容
                switch (tab.getPosition()) {
                    case 0: // 对讲群组
                        Log.e("zq","11----对讲群组" );
                        showGroupChat();
                        break;
                    case 1: // 摄像机
                        showCamera();
                        break;
                    case 2: // 执法记录仪
                        showLawEnforcementRecorder();
                        break;
//                    case 3: // 任务接收
//                        showTaskReception();
//                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 标签页取消选中时的处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 标签页重新选中时的处理
            }
        });

        tlGroups.selectTab(tlGroups.getTabAt(0));

        // 延迟选择第一个标签页
//        tlGroups.postDelayed(() -> {
//            if (getActivity() != null && !isDetached()) {
////                TabLayout.Tab firstTab = tlGroups.getTabAt(0);
//                tlGroups.selectTab(tlGroups.getTabAt(0));
////                if (firstTab != null && tlGroups.getSelectedTabPosition() != 0) {
////                    firstTab.select();
////                    tlGroups.selectTab(tlGroups.getTabAt(0));
////                }
//            }
//        }, 150);
    }
    private void showGroupChat() {
        // 显示群组聊天内容
        currentTabPosition = 0;
        mDeviceRV.setAdapter(groupChatAdapter);
        // 加载群组聊天数据
        loadGroupChatData();
    }
    
    private void showCamera() {
        // 显示摄像机内容
        currentTabPosition = 1;
        mDeviceRV.setAdapter(cameraAdapter);
        // 加载摄像机数据
        loadCameraData();
//        isDeviceRVScrollBottom = true;
    }
    
    private void showLawEnforcementRecorder() {
        // 显示执法记录仪内容
        currentTabPosition = 2;
        mDeviceRV.setAdapter(mLERecorderAdapter); // 暂时使用群组适配器
        // 加载执法记录仪数据
        loadLawEnforcementRecorderData();
//        isDeviceRVScrollBottom = true;
    }
    
//    private void showTaskReception() {
//        // 显示任务接收内容
//        currentTabPosition = 3;
//        rvGroups.setAdapter(groupChatAdapter); // 暂时使用群组适配器
//        rvGroups.setVisibility(VISIBLE);
//        // 加载任务接收数据
//        loadTaskReceptionData();
//    }
    //切换状态管理
//    private boolean   isDeviceRVScrollBottom;
    private void loadGroupChatData() {
        mDeviceRV.setVisibility(VISIBLE);
        mDeviceHint.setVisibility(GONE);
        ObtainGroup obtainGroup = new ObtainGroup(mContext);
        obtainGroup.updateRoom(coreManager);
        obtainGroup.setRoomListListener((mucRooms)->{
            mucRooms.add(new BaseSortModel<Friend>());
            groupChatAdapter.updateData(mucRooms);
        });
//        if (isDeviceRVScrollBottom){
//            deviceRVScrollBottom();
//        }

    }
    
    private void loadCameraData() {
        // 加载摄像机数据
        List<CameraAdapter.CameraInfo> cameraList = new ArrayList<>();
        if (cameraList.isEmpty()){
            mDeviceRV.setVisibility(GONE);
            mDeviceHint.setVisibility(VISIBLE);
            mDeviceHint.setText("暂无摄像机设备");

            return;
        }
        cameraList.add(new CameraAdapter.CameraInfo(
            "1", "监控摄像头01", "在线", "位置A"));
        cameraList.add(new CameraAdapter.CameraInfo(
            "2", "监控摄像头02", "在线", "位置B"));
        cameraList.add(new CameraAdapter.CameraInfo(
            "3", "监控摄像头03", "离线", "位置C"));
        cameraList.add(new CameraAdapter.CameraInfo(
            "4", "监控摄像头04", "在线", "位置D"));

        cameraAdapter.updateData(cameraList);
    }

    private void loadLawEnforcementRecorderData() {
        List<DeviceList.DataBean.DeviceBean> deviceList = new ArrayList<>();
        if (deviceList.isEmpty()){
            mDeviceRV.setVisibility(GONE);
            mDeviceHint.setVisibility(VISIBLE);
            mDeviceHint.setText("暂无执法记录仪设备");
            return;
        }
        //获取在线执法记录仪设备
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("keyword", "1");
        params.put("pageSize", "100");// 给一个尽量大的值
//        params.put("status", 2);
        HttpUtils.with(getContext())
                .get()
                .url(HttpConstants.DEVICE_LIST)
                .addBody(params)
                .execute(new HttpEngineCallBack<DeviceList>() {
                    @Override
                    public void onSuccess(DeviceList objResult) {
                       if (objResult.getCode() ==200){
                           new Handler(Looper.getMainLooper()).post(() -> {
                               mLERecorderAdapter.updateData(objResult.getData().getList());
                               deviceRVScrollBottom();
                           });
                       }

                    }
                });
    }
    private void deviceRVScrollBottom(){
        if (mNestedScrollView != null) {
            // 方法3: 平滑滚动到底部
            mNestedScrollView.post(() -> {
                View lastChild = mNestedScrollView.getChildAt(mNestedScrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + mNestedScrollView.getPaddingBottom();
                mNestedScrollView.smoothScrollTo(0, bottom);
            });
        }
    }
    private void initData(){
        getApps();
        loadMockData();
    }

    private void getApps() {
        String token=  IMUserInfoUtil.getInstance().getToken();;//FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        String  homeModuleListCache =  FastSharedPreferences.get("httpRes").getString("home_moduleList_" + uuid,"");
        if(!homeModuleListCache.isEmpty()){
            parasData(homeModuleListCache);
        }
        HttpRequestParam.moduleList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String result) {
                if(!result.equals(homeModuleListCache)) {
                    parasData(result);
                    FastSharedPreferences.get("httpRes").edit().putString("home_moduleList_" + uuid, result).apply();
                }
            }
            @Override
            public void onError(Throwable e) {
            }
        });
    }
    void parasData(String result){
        ResArr<ModuleInfo<AppInfo>> res = JSON.parseObject(result,
                new TypeToken<ResArr<ModuleInfo<AppInfo>>>() {}.getType());
        ResArr.DataBean<ModuleInfo<AppInfo>> data = res.getData();
        if(res.getCode() == 200 && data != null && data.getList() != null){
           if (!data.getList().isEmpty()){
               List<AppInfo> appInfoList = res.getData().getList().get(0).getApp_list();
               appInfoList.add(new AppInfo("8",getString(R.string.more)));

               rvFunctionGrid.setAdapter(new FunctionGridAdapter(getActivity(), appInfoList));
           }
        }
    }
    private void loadMockData() {
        //获取值班领导
        tagUserList(1, 1, new HttpEngineCallBack<Result<TagUser<UserInfo>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(Result<TagUser<UserInfo>> result) {
                List<UserInfo> userInfos = null;
                if (result.getCode() == 200 && result.getData() != null) {
                    userInfos = result.getData().getList();
                }
                List<UserInfo> dataToShow = (userInfos != null && !userInfos.isEmpty())
                        ? userInfos
                        : createPlaceholderData();

                new Handler(Looper.getMainLooper()).post(() -> {
                    onDutyLeaderAdapter.updateData(dataToShow);
                    onDutyLeaderAdapter.notifyDataSetChanged();
                });
            }
        });

        //获取值守台
        tagUserList(1, 2, new HttpEngineCallBack<Result<TagUser<UserInfo>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(Result<TagUser<UserInfo>> result) {
                List<UserInfo> userInfos = null;
                if (result.getCode() == 200 && result.getData() != null) {
                    userInfos = result.getData().getList();
                }

                List<UserInfo> dataToShow = (userInfos != null && !userInfos.isEmpty())
                        ? userInfos
                        : createPlaceholderData();

                new Handler(Looper.getMainLooper()).post(() -> {
                    watchtowerAdapter.updateData(dataToShow);
                    watchtowerAdapter.notifyDataSetChanged();
                });
            }
        });
    }
    /**
     * 创建占位数据
     */
    private List<UserInfo> createPlaceholderData() {
        List<UserInfo> placeholderList = new ArrayList<>();
        // 只创建一个提示项
        UserInfo placeholder = new UserInfo();
        placeholder.setOort_name("暂无数据");
        placeholder.setPlaceholder(true); // 标记为占位数据
        placeholderList.add(placeholder);

        return placeholderList;
    }
    <T> void getTagList(int is_open, int tag_type, HttpEngineCallBack<T> callBack) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("is_open", is_open);//公开/个人 1:公开 0:个人(默认)
        params.put("tag_type", tag_type);
        //获取值守类型标签
        HttpUtils.with(getContext())
                .get()
                .url(ApiConstants.Url.TAG_LIST)
                .addBody(params)
                .execute(callBack);
    }

    //tag_type 值守类型 1值班领导 2值守台
    <T>void tagUserList(int is_open, int tag_type, HttpEngineCallBack<T> callBack) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("page", 1);
        params.put("pagesize", 100);
        params.put("desensitize", false);
        getTagList(is_open, tag_type, new HttpEngineCallBack<Result<Data<Tag>>>() {
            @Override
            public void onSuccess(Result<Data<Tag>> objResult) {

                if (objResult.getCode() == 200){
                    List<Tag> tagList = objResult.getData().getList();
                    Log.e(TAG, "getTagList----" + tagList.size());

                    if (tagList != null && !tagList.isEmpty()){
                        params.put("tid", tagList.get(0).getTid());
                        HttpUtils.with(getContext())
                                .get()
                                .url(ApiConstants.Url.TAG_USER_LIST)
                                .addBody(params)
                                .execute(callBack);
                    }
                }

            }
        });
    }
    public void scrollTo(int x, int y){
        if(mNestedScrollView != null){
            mNestedScrollView.scrollTo(x, y);
        }
    }

    boolean isSearching = false;
    public void onClick(){
        mETSearch.setOnClickListener(v -> {
            if(!isSearching){
                setSearching(true);
            }

        });
        mIVSearch.setOnClickListener(v -> {
            if (!isSearching){
                setSearching(true);
            }else {
                setSearching(false);
            }
        });
        mIVScan.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null){
                MainActivity.requestQrCodeScan(activity);
            }
        });

        mVoiceIV.setOnClickListener(v -> {
//            setSearching(true);
            //启动带返回的Activity
            mVoiceIVResult.launch(new Intent(mContext, OortAudioRecordRecActivity.class));

        });
    }
    private final ActivityResultLauncher<Intent> mVoiceIVResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), // 使用预定义的合约
            result -> { // 这里是回调，结果返回时自动执行这里
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    // 在这里处理返回的数据
                    assert intent != null;
                    String audioText = intent.getStringExtra("audio_text");
                    assert audioText != null;
                    audioText = audioText.replaceAll("[\\p{P}\\p{S}]", "");
                    //去除标点符号
                    if(audioText.isEmpty()){
                        return;
                    }
                    if (!isSearching)
                        setSearching(true);
                    mETSearch.setText(audioText);
                    // 将光标移动到文本末尾
                    mETSearch.setSelection(audioText.length());

                }
            });
    /**
     *
     * 搜索框状态切换
     */
    void setSearching(boolean searching){
        // 3. 弹出软键盘
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mETSearch.setFocusable(searching);
        mETSearch.setFocusableInTouchMode(searching);
        if (!isSearching){
            mETSearch.requestFocus();
            mDispatchConsoleSearch.setVisibility(VISIBLE);
            mDispatchConsole.setVisibility(INVISIBLE);
            mIVSearch.setImageResource(R.drawable.ic_back);
            imm.showSoftInput(mETSearch, InputMethodManager.SHOW_IMPLICIT);
            initSearch();
        }else {
            mIVSearch.setImageResource(R.drawable.ic_search);
            mDispatchConsoleSearch.setVisibility(GONE);
            mDispatchConsole.setVisibility(VISIBLE);
            isSearching = false;
            imm.hideSoftInputFromWindow(mETSearch.getWindowToken(), 0);
            mETSearch.setText("");
        }
        isSearching = searching;
        //homeFragment.setBottomSheetState(searching);
    }
    private SearchHistoryManager historyManager;
    void initSearch(){
        mETSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                if (query.isEmpty()){
                    initHistory();
                    return;
                }
                onSearch(query);
            }
        });
        mClearTV.setOnClickListener(v -> {
           if ( historyManager.getHistoryCount() == 0){
               Toast.makeText(mContext, "暂无历史记录清空", Toast.LENGTH_SHORT).show();
               return;
           }
            com.oortcloud.appstore.dailog.DialogHelper.getConfirmDialog(mContext,
                    "是否确定删除历史记录", (dialogInterface, i) -> {
                        historyManager.clearHistory();
                        initHistory();
            }, (dialogInterface, i) -> {

            }).show();
        });

        initHistory();


    }
    public void onSearch(final String text) {
        if (!TextUtils.isEmpty(text)) {
            HttpRequestCenter.postSearch(text).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>() {}.getType());
                    List<AppInfo> appInfoList = result.getData().getApp_list();
                    if (result.isok() && appInfoList != null && !appInfoList.isEmpty()) {
                        initSHE();
                        mSearchRV.setVisibility(VISIBLE);
                        mAllAppAdapter.setData(appInfoList);
                        historyManager.saveSearchHistory(text);
                    }else {
                        initSHE();
                        mTVEmpty.setVisibility(VISIBLE);
                    }
                }
            });
        }
    }
    private void initHistory() {
        // 初始化历史管理器
        historyManager = new SearchHistoryManager(mContext);
        initSHE();
        mHistoryLaberView.setVisibility(VISIBLE);
        List<String> mHistoryList =  historyManager.getHistoryList();
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 20, 15, 10);

        mHistoryLaberView.removeAllViews();
        for (int i = 0; i < mHistoryList.size(); i++) {
            //有数据往下走
            final int j = i;
            //添加分类块
            View paramItemView = getLayoutInflater().inflate(R.layout.item_history_layout, null);
            final TextView keyWordTv = paramItemView.findViewById(R.id.tv_content);
            keyWordTv.setText(mHistoryList.get(i));
            mHistoryLaberView.addView(paramItemView, layoutParams);

            keyWordTv.setOnClickListener(view ->  {
//                onSearch(keyWordTv.getText().toString().trim());
                String text = keyWordTv.getText().toString().trim();
                mETSearch.setText(text);
                // 将光标移动到文本末尾
                mETSearch.setSelection(text.length());

            });

        }
    }
    //初始化搜索历史结果控制 全部置为GONE
    void initSHE(){
        mSearchRV.setVisibility(GONE);
        mHistoryLaberView.setVisibility(GONE);
        mTVEmpty.setVisibility(GONE);
    }

}