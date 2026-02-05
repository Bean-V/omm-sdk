package com.oort.weichat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.event.MessageEventHongdian;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.course.LocalCourseActivity;
import com.oort.weichat.db.SQLiteHelper;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.LoginHelper;
import com.oort.weichat.sp.UserSp;
import com.oort.weichat.testjs.TestJsActivity;
import com.oort.weichat.ui.account.ChangePasswordActivity;
import com.oort.weichat.ui.account.LoginActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.circle.BusinessCircleActivity;
import com.oort.weichat.ui.circle.SelectPicPopupWindow;
import com.oort.weichat.ui.circle.range.NewZanActivity;
import com.oort.weichat.ui.circle.range.SendAudioActivity;
import com.oort.weichat.ui.circle.range.SendFileActivity;
import com.oort.weichat.ui.circle.range.SendShuoshuoActivity;
import com.oort.weichat.ui.circle.range.SendVideoActivity;
import com.oort.weichat.ui.live.LiveActivity;
import com.oort.weichat.ui.lock.DeviceLockHelper;
import com.oort.weichat.ui.me.AboutActivity;
import com.oort.weichat.ui.me.AddStateActivity;
import com.oort.weichat.ui.me.BasicInfoEditActivity;
import com.oort.weichat.ui.me.MyCollection;
import com.oort.weichat.ui.me.SettingActivity;
import com.oort.weichat.ui.me.redpacket.MyWalletActivity;
import com.oort.weichat.ui.other.QRcodeActivity;
import com.oort.weichat.ui.tool.SingleImagePreviewActivity;
import com.oort.weichat.ui.trill.TrillActivity;
import com.oort.weichat.util.Md5Util;
import com.oort.weichat.util.StateInputUtils;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.view.SelectionFrame;
import com.oortcloud.appstore.adapter.MeAppAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.bean.UserInfo;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.basemodule.BuildConfig;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.debug.AppDebugActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.sentaroh.android.upantool.UsbHelper;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

public class MeFragment extends EasyFragment implements View.OnClickListener {

    private ImageView mAvatarImg;
    private TextView mNickNameTv;
    private TextView mPhoneNumTv;
    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, OtherBroadcast.SYNC_SELF_DATE_NOTIFY)) {
                updateUI();
            }
        }
    };
    private SelectPicPopupWindow menuWindow;
    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            if (menuWindow != null) {
                // 顶部一排按钮复用这个listener, 没有menuWindow,
                menuWindow.dismiss();
            }
            Intent intent = new Intent();
            int id = v.getId();
            if (id == R.id.btn_send_picture) {// 发表图文，
                intent.setClass(getActivity(), SendShuoshuoActivity.class);
                startActivity(intent);
            } else if (id == R.id.btn_send_voice) {// 发表语音
                intent.setClass(getActivity(), SendAudioActivity.class);
                startActivity(intent);
            } else if (id == R.id.btn_send_video) {// 发表视频
                intent.setClass(getActivity(), SendVideoActivity.class);
                startActivity(intent);
            } else if (id == R.id.btn_send_file) {// 发表文件
                intent.setClass(getActivity(), SendFileActivity.class);
                startActivity(intent);
            } else if (id == R.id.new_comment) {// 最新评论&赞
                Intent intent2 = new Intent(getActivity(), NewZanActivity.class);
                intent2.putExtra("OpenALL", true);
                startActivity(intent2);
                EventBus.getDefault().post(new MessageEventHongdian(0));
            }
        }
    };
    private User mUser;
    private RecyclerView myrv;
    private MeAppAdapter meAppAdapter;
    private StaggeredGridLayoutManager mStaggerdManager;
    private ScrollView svmyapp;
    private StateInputUtils mStateInput;
    private ImageView addstateiv;
    private ImageView addstate1iv;
    private TextView addstate1tv;
    private SmartRefreshLayout mRefreshLayout;

    public MeFragment() {
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        if (createView) {
            initTitleBackground();
            initView();
            initEvent();
        }
    }

    private void initData() {

        String  record2 =  FastSharedPreferences.get("httpRes").getString("myApp_" + coreManager.getSelf().getUserId(),"");
        if(record2.length() > 0){
            setClassifyAppList_(record2);
        }
        HttpRequestCenter.myApp(4 , 2).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.v("msgxksmy",s);


                mRefreshLayout.finishRefresh();
                if(!s.equals(record2)) {
                    setClassifyAppList(s);
                }
            }
        });
    }
    private void getUserInfo() {
        mStateInput = StateInputUtils.getInstance();
        HttpRequestCenter.GetUserInfo().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Data<UserInfo>> result = new Gson().fromJson(s,new TypeToken<Result<Data<UserInfo>>>(){}.getType());
                if (result.isok()){

                    if(result.getData().getUserInfo() != null){
                        if (mAvatarImg != null) {
                            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), mAvatarImg, true);
                        }
                        if (mNickNameTv != null) {
                            mNickNameTv.setText(result.getData().getUserInfo().getOort_name());
                        }

                        if (mPhoneNumTv != null) {
                            String phoneNumber = result.getData().getUserInfo().getOort_phone();
                            mPhoneNumTv.setText(phoneNumber);
                        }
                    }
                    if (result.getData().getUserInfo().getImstatus()!=null){

                        if (result.getData().getUserInfo().getImstatus().length() > 0)
                        {

                            String nameState = result.getData().getUserInfo().getImstatus();
                            String imageState = mStateInput.getProCode(nameState);
//                        addstateiv.setBackgroundDrawable(getActivity().getDrawable(Integer.parseInt(imageState)));
//                        Glide.with(mContext).load(imageState).into(addstateiv);
                            addstateiv.setVisibility(View.GONE);
//                        addstate1iv.setVisibility(View.VISIBLE);
//                        addstate1tv.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(Integer.parseInt(imageState)).into((addstate1iv));
                            addstate1iv.setColorFilter(Color.WHITE, PorterDuff.Mode.OVERLAY);
//                        addstate1iv.setBackgroundDrawable(getActivity().getDrawable(Integer.parseInt(imageState)));
//                        addstate1iv.setColorFilter(Color.WHITE, PorterDuff.Mode.LIGHTEN);
                            addstate1tv.setTextColor(Color.WHITE);
                            addstate1tv.setText(nameState);
                            Log.v("msgxkss", nameState);
                            Log.v("msgxksss", imageState);
                        }
                    }else{
//                        addstateiv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setClassifyAppList_(String s){


        Result<Data<ModuleInfo<AppInfo>>> result = new Gson().fromJson(s,
                new TypeToken<Result<Data<ModuleInfo<AppInfo>>>>(){}.getType());
        if (result.getCode()==200){
            if (result.isok()){
                List<ModuleInfo<AppInfo>> moduleInfoList = result.getData().getList();
                if(moduleInfoList != null) {
                    for (ModuleInfo moduleInfo : moduleInfoList) {
                        if (moduleInfo.getApp_list() != null) {
                            myrv.setLayoutManager(mStaggerdManager);
                            meAppAdapter = new MeAppAdapter(mContext, moduleInfo.getApp_list());
                            myrv.setAdapter(meAppAdapter);
                            myrv.setVisibility(View.VISIBLE);
                        } else {
                           // svmyapp.setVisibility(View.GONE);
                            myrv.setLayoutManager(mStaggerdManager);
                            meAppAdapter = new MeAppAdapter(mContext, new ArrayList<>());
                            myrv.setAdapter(meAppAdapter);
                            //myrv.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    //svmyapp.setVisibility(View.GONE);
                }
            } else {
               // svmyapp.setVisibility(View.GONE);
            }
        }else{
           // svmyapp.setVisibility(View.GONE);
            //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        }
    }
    private void setClassifyAppList(String s){


        Result<Data<ModuleInfo<AppInfo>>> result = new Gson().fromJson(s,
                new TypeToken<Result<Data<ModuleInfo<AppInfo>>>>(){}.getType());
        if (result.getCode()==200){
            if (result.isok()){

                FastSharedPreferences.get("httpRes").edit().putString("myApp_" + coreManager.getSelf().getUserId(), s).apply();
                List<ModuleInfo<AppInfo>> moduleInfoList = result.getData().getList();
                if(moduleInfoList != null) {
                    for (ModuleInfo moduleInfo : moduleInfoList) {
                        if (moduleInfo.getApp_list() != null) {
                            myrv.setLayoutManager(mStaggerdManager);
                            meAppAdapter = new MeAppAdapter(mContext, moduleInfo.getApp_list());
                            myrv.setAdapter(meAppAdapter);
                            myrv.setVisibility(View.VISIBLE);
                        } else {
                            //svmyapp.setVisibility(View.GONE);
                            myrv.setLayoutManager(mStaggerdManager);
                            meAppAdapter = new MeAppAdapter(mContext, new ArrayList<>());
                            myrv.setAdapter(meAppAdapter);
                        }
                    }
                }else {
                    //svmyapp.setVisibility(View.GONE);
                }
            } else {
                //svmyapp.setVisibility(View.GONE);
            }
        }else{
            //svmyapp.setVisibility(View.GONE);
            //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        initData();
        getUserInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

    private void initTitleBackground() {
        SkinUtils.Skin skin = SkinUtils.getSkin(requireContext());
        //findViewById(R.id.tool_bar).setBackgroundColor(skin.getAccentColor());
        findViewById(R.id.rlInfoBackground).setBackgroundColor(skin.getAccentColor());
    }

    private void initView() {
        // 关闭支付功能，隐藏我的钱包
        if (!coreManager.getConfig().enablePayModule) {
            findViewById(R.id.my_monry).setVisibility(View.GONE);
        }
        // 切换新旧两种ui对应我的页面是否显示视频会议、直播、短视频，
        if (coreManager.getConfig().newUi) {
            findViewById(R.id.ll_more).setVisibility(View.GONE);
        }
        addstateiv = (ImageView)findViewById(R.id.add_state_iv);
        addstate1iv = (ImageView)findViewById(R.id.add_state1_iv);
        addstate1tv = (TextView) findViewById(R.id.add_state1_tv);
       // svmyapp = (ScrollView)findViewById(R.id.sv_my_app);
        myrv =  findViewById(R.id.my_hrv);
        mAvatarImg = (ImageView) findViewById(R.id.avatar_img);
        mNickNameTv = (TextView) findViewById(R.id.nick_name_tv);
        mPhoneNumTv = (TextView) findViewById(R.id.phone_number_tv);
        AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getNickName(), coreManager.getSelf().getUserId(), mAvatarImg, false);
        mNickNameTv.setText(coreManager.getSelf().getNickName());

        //长按头像显示调试栏
        mAvatarImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                findViewById(R.id.my_debug_rl).setVisibility(View.VISIBLE);
                return true;
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtherBroadcast.SYNC_SELF_DATE_NOTIFY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mUpdateReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }else {
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);

        }        mStaggerdManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
    }

    private void initEvent() {

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(rl -> {
            OperLogUtil.msg("我的下拉刷新");
            initData();
            getUserInfo();

        });

        findViewById(R.id.iv_title_add).setOnClickListener(v -> {
            menuWindow = new SelectPicPopupWindow(getActivity(), itemsOnClick);
            menuWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            menuWindow.showAsDropDown(v,
                    -(menuWindow.getContentView().getMeasuredWidth() - v.getWidth() / 2 - 40),
                    0);
        });

        mAvatarImg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SingleImagePreviewActivity.class);
            intent.putExtra(AppConstant.EXTRA_IMAGE_URI, coreManager.getSelf().getUserId());
            startActivity(intent);
            OperLogUtil.msg("查看头像预览");
        });
        mNickNameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(getActivity(), TestJsActivity.class);
                startActivity(intent);
                OperLogUtil.msg("查看js");
                return false;
            }
        });

        findViewById(R.id.info_rl).setOnClickListener(this);
        addstateiv.setOnClickListener(this);
        addstate1iv.setOnClickListener(this);
        addstate1tv.setOnClickListener(this);
        findViewById(R.id.imageView3).setOnClickListener(this);
        findViewById(R.id.my_monry).setOnClickListener(this);
        findViewById(R.id.my_space_rl).setOnClickListener(this);
        findViewById(R.id.my_collection_rl).setOnClickListener(this);
        findViewById(R.id.local_course_rl).setOnClickListener(this);

        findViewById(R.id.meeting_rl).setOnClickListener(this);
        findViewById(R.id.live_rl).setOnClickListener(this);
        findViewById(R.id.douyin_rl).setOnClickListener(this);
        findViewById(R.id.up_password_rl).setOnClickListener(this);
        findViewById(R.id.setting_rl).setOnClickListener(this);
        findViewById(R.id.about_rl).setOnClickListener(this);
		findViewById(R.id.my_debug_rl).setOnClickListener(this);
        findViewById(R.id.exit_btn).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);


        if(Constant.HAVA_VERIFY) {
            findViewById(R.id.exit_btn).setVisibility(View.GONE);
            findViewById(R.id.up_password_rl).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        mUser = coreManager.getSelf();
        if (!UiUtils.isNormalClick(v)) {
            return;
        }
        int id = v.getId();
        if (id == R.id.info_rl) {// 我的资料
            startActivityForResult(new Intent(getActivity(), BasicInfoEditActivity.class), 1);
            OperLogUtil.msg("查看个人信息");
        } else if (id == R.id.add_state_iv) {
            startActivity(new Intent(getActivity(), AddStateActivity.class));
        } else if (id == R.id.add_state1_iv) {
            startActivity(new Intent(getActivity(), AddStateActivity.class));
        } else if (id == R.id.add_state1_tv) {
            startActivity(new Intent(getActivity(), AddStateActivity.class));
        } else if (id == R.id.imageView3) {
            Intent intent2 = new Intent(getActivity(), QRcodeActivity.class);
            intent2.putExtra("isgroup", false);
            if (!TextUtils.isEmpty(mUser.getAccount())) {
                intent2.putExtra("userid", mUser.getAccount());
            } else {
                intent2.putExtra("userid", mUser.getUserId());
            }
            intent2.putExtra("userAvatar", mUser.getUserId());
            intent2.putExtra("nickName", mUser.getNickName());
            intent2.putExtra("sex", mUser.getSex());
            startActivity(intent2);
            OperLogUtil.msg("查看二维码信息");
        } else if (id == R.id.my_monry) {// 我的钱包
            MyWalletActivity.start(requireContext());
        } else if (id == R.id.my_space_rl) {// 我的动态
            Intent intent = new Intent(getActivity(), BusinessCircleActivity.class);
            intent.putExtra(AppConstant.EXTRA_CIRCLE_TYPE, AppConstant.CIRCLE_TYPE_PERSONAL_SPACE);
            startActivity(intent);
        } else if (id == R.id.my_collection_rl) {// 我的收藏
            startActivity(new Intent(getActivity(), MyCollection.class));
        } else if (id == R.id.local_course_rl) {// 我的课件
            startActivity(new Intent(getActivity(), LocalCourseActivity.class));
        } else if (id == R.id.meeting_rl) {// 视频会议
//                SelectContactsActivity.startQuicklyInitiateMeeting(requireContext());
        } else if (id == R.id.live_rl) {// 我的直播
            startActivity(new Intent(getActivity(), LiveActivity.class));
        } else if (id == R.id.douyin_rl) {// 短视频
            startActivity(new Intent(getActivity(), TrillActivity.class));
        } else if (id == R.id.my_debug_rl) {// 轻应用调试
            startActivity(new Intent(getActivity(), AppDebugActivity.class));
        } else if (id == R.id.up_password_rl) {//修改密码


            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            OperLogUtil.msg("点击修改密码");
        } else if (id == R.id.setting_rl) {// 设置
            startActivity(new Intent(getActivity(), SettingActivity.class));
            OperLogUtil.msg("点击设置");
        } else if (id == R.id.about_rl) {//关于我们
            startActivity(new Intent(getActivity(), AboutActivity.class));
            OperLogUtil.msg("点击关于我们");
        } else if (id == R.id.exit_btn) {//退出当前账号


            if (BuildConfig.DEBUG && false) {
                showExitDialog();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        Intent intent1 = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent1);
                        return;
                    }
                }

                UsbHelper.getInstance().initData(getContext());
                SQLiteHelper.copy(getContext());

            } else {
                showExitDialog();
                OperLogUtil.msg("点击退出当前账号");
            }
        } else if (id == R.id.btn_clear) {
            clear();
            OperLogUtil.msg("点击注销当前账号");
        }
    }

    private void clear() {
        UserSp.getInstance(getContext()).clearUserInfo();
        DeviceLockHelper.clearPassword();
        UserSp.getInstance(getActivity()).clearAllUserInfo();
        MyApplication.getInstance().mUserStatus = LoginHelper.STATUS_NO_USER;
        coreManager.logout();
        LoginHelper.broadcastLogout(getActivity());
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
    // 退出当前账号
    private void showExitDialog() {
        SelectionFrame mSF = new SelectionFrame(getActivity());
        mSF.setSomething(null, getString(R.string.sure_exit_account), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {
                OperLogUtil.msg("取消退出登录");
            }

            @Override
            public void confirmClick() {
                logout();
                // 退出时清除设备锁密码，
                DeviceLockHelper.clearPassword();
                UserSp.getInstance(getActivity()).clearUserInfo();
                MyApplication.getInstance().mUserStatus = LoginHelper.STATUS_USER_SIMPLE_TELPHONE;
                coreManager.logout();
                LoginHelper.broadcastLogout(getActivity());
                startActivity(new Intent(getActivity(), LoginActivity.class));
//                LoginHistoryActivity.start(getActivity());

                OperLogUtil.msg("退出登录");
            }
        });
        mSF.show();
    }
    private void logout() {
        HashMap<String, String> params = new HashMap<String, String>();
        // 得到电话
        String phoneNumber = coreManager.getSelf().getTelephone();
        String digestTelephone = Md5Util.toMD5(phoneNumber);
        params.put("telephone", digestTelephone);
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        // 默认为86
        params.put("areaCode", String.valueOf(86));
        params.put("deviceKey", "android");

        HttpUtils.get().url(coreManager.getConfig().USER_LOGOUT)
                .params(params)
                .build()
                .execute(new BaseCallback<String>(String.class) {

                    @Override
                    public void onResponse(ObjectResult<String> result) {
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || resultCode == Activity.RESULT_OK) {// 个人资料更新了
            updateUI();
            getUserInfo();
        }
    }

    /**
     * 用户的信息更改的时候，ui更新
     */
    private void updateUI() {
        if (mAvatarImg != null) {
            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), mAvatarImg, true);
        }
        if (mNickNameTv != null) {
            mNickNameTv.setText(coreManager.getSelf().getNickName());
        }

        if (mPhoneNumTv != null) {
            String phoneNumber = coreManager.getSelf().getTelephoneNoAreaCode();
            mPhoneNumTv.setText(phoneNumber);
        }
    }
}
