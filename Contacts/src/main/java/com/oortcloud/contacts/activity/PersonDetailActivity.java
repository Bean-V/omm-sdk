package com.oortcloud.contacts.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ToastUtil;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.contacts.bean.omm.User;
import com.oortcloud.contacts.databinding.ActivityPersonDetailBinding;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.message.CallConstants;
import com.oortcloud.contacts.message.MessageEventInviteCall;
import com.oortcloud.contacts.utils.ImageLoader;
import com.oortcloud.contacts.view.window.BasicUserInfoWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;


public class PersonDetailActivity extends BaseActivity {


    private ImageView mHeadPortrait;
    private TextView mName;
    private TextView mAccountNumber;
    private RelativeLayout mNickNameLayout;
    private TextView mNickName;
    private RelativeLayout mSexLayout;
    private TextView mSex;
    private RelativeLayout mBirthDateLayout;
    private TextView mBirthDate;
    private RelativeLayout mIDCardRL;
    private TextView mIDCard;
    private RelativeLayout mUserEmailRL;
    private TextView mUserEmail;
    private RelativeLayout mPhoneNumberRL;
    private TextView mPhoneNumber;
    private RelativeLayout mUserCodeRL;
    private TextView mUserCode;
    private RelativeLayout mUserStatusRL;
    private TextView mUserStatus;
    private RelativeLayout mUserTypeRL;
    private TextView mUserType;
    private RelativeLayout mUserNameRL;
    private TextView mUserName;
    private RelativeLayout mQRCodeRL;
    private RelativeLayout mCategoryDeptRL;
    private TextView mCategoryDept;
    private RelativeLayout mDeptCodeRL;
    private TextView mDeptCode;
    private RelativeLayout mQuartersRL;
    private TextView mQuarters;
    private RelativeLayout mJobRL;
    private TextView mJob;
    private RelativeLayout mRelatedDeptNameRL;
    private TextView mRelatedDeptName;
    private RelativeLayout mHouseNumberRL;
    private TextView mHouseNumber;
    private RelativeLayout mOphRl;
    private TextView mOph;
    private LinearLayout mSendMessageLl;
    private Button mSendMessage;
    private ImageView ivRight;
    private Button btn_msg;
    private Button btn_call;
    private Button btn_video;
    private TextView tv_small_code;
// 替换为实际生成的 Binding 类（如 ActivityUserDetailBinding）
    private AttentionUser mAttentionUser;
    private UserInfo mUserInfo;
    //IMUser
    private User mUser;
    //区分好友直接跳转 非好友需要添加后发送消息
    private boolean FLAG_SEND = false;
    private com.oortcloud.contacts.databinding.ActivityPersonDetailBinding binding;

    @Override
    protected View getRootView() {
        binding = ActivityPersonDetailBinding.inflate(getLayoutInflater());



        // 通过 ViewBinding 赋值（ID 需与布局文件对应）
        mHeadPortrait = binding.headPortrait;
        mName = binding.name;
        mAccountNumber = binding.accountNumber;
        mNickNameLayout = binding.nickNameLayout;
        mNickName = binding.nickName;
        mSexLayout = binding.sexLayout;
        mSex = binding.sex;
        mBirthDateLayout = binding.birthDateLayout;
        mBirthDate = binding.birthDate;
        mIDCardRL = binding.iDCardRl; // 注意驼峰命名：i_d_card_rl → iDCardRl
        mIDCard = binding.iDCard;
        mUserEmailRL = binding.userEmailRl; // user_email_rl → userEmailRl
        mUserEmail = binding.userEmail;
        mPhoneNumberRL = binding.phoneNumberRl; // phone_number_rl → phoneNumberRl
        mPhoneNumber = binding.phoneNumber;
        mUserCodeRL = binding.userCodeRl; // user_code_rl → userCodeRl
        mUserCode = binding.userCode;
        mUserStatusRL = binding.userStatusRl; // user_status_rl → userStatusRl
        mUserStatus = binding.userStatus;
        mUserTypeRL = binding.userTypeRl; // user_type_rl → userTypeRl
        mUserType = binding.userType;
        mUserNameRL = binding.userNameRl; // user_name_rl → userNameRl
        mUserName = binding.userName;
        mQRCodeRL = binding.qrCodeRl; // qr_code_rl → qRCodeRl
        mCategoryDeptRL = binding.categoryDeptRl; // category_dept_rl → categoryDeptRl
        mCategoryDept = binding.categoryDept;
        mDeptCodeRL = binding.deptCodeRl; // dept_code_rl → deptCodeRl
        mDeptCode = binding.deptCode;
        mQuartersRL = binding.quartersRl; // quarters_rl → quartersRl
        mQuarters = binding.quarters;
        mJobRL = binding.jobRl; // job_rl → jobRl
        mJob = binding.job;
        mRelatedDeptNameRL = binding.relatedDeptNameRl; // related_dept_name_rl → relatedDeptNameRl
        mRelatedDeptName = binding.relatedDeptName;
        mHouseNumberRL = binding.houseNumberRl; // house_number_rl → houseNumberRl
        mHouseNumber = binding.houseNumber;
        mOphRl = binding.ophRl; // oph_rl → ophRl
        mOph = binding.oph;
        mSendMessageLl = binding.sendMessageLl; // send_message_ll → sendMessageLl
        mSendMessage = binding.sendMessage; // send_message → sendMessage
        ivRight = binding.ivTitleRight; // iv_title_right → ivTitleRight
        btn_msg = binding.btnMsg; // btn_msg → btnMsg
        btn_call = binding.btnCall; // btn_call → btnCall
        btn_video = binding.btnVideo; // btn_video → btnVideo
        tv_small_code = binding.samallCode; // s
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_person_detail;

    }

    @Override
    protected void initBundle(Bundle bundle) {
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//SYSTEM_UI_FLAG_VISIBLE
        if (getIntent() != null) {
            mUserInfo = (UserInfo) getIntent().getSerializableExtra(Constants.OBJ);

        }

//        ImageViewCompat.setImageTintList(ivRight, mContext.getResources().getColorStateList(R.color.white));

    }

    @Override
    protected void initView() {
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

        if (mUserInfo != null) {

            ImageLoader.loaderImage(mHeadPortrait, mUserInfo);

            mName.setText(mUserInfo.getOort_name());
            mAccountNumber.setText(mUserInfo.getOort_phone());

            mNickName.setText(mUserInfo.getOort_name());
            //0未知，1男，2女，3其它
            int sexType = mUserInfo.getOort_sex();
            findViewById(R.id.sex_layout).setVisibility(View.VISIBLE);
            if (sexType == 0) {
                mSex.setText(R.string.unknown);
                findViewById(R.id.sex_layout).setVisibility(View.GONE);
            } else if (sexType == 1) {
                mSex.setText(R.string.man);
            } else if (sexType == 2) {
                mSex.setText(R.string.woman);
            } else if (sexType == 3) {
                mSex.setText(R.string.other);
            }
            findViewById(R.id.birth_date_layout).setVisibility(View.GONE);

//            String birth = "";//mUserInfo.getB();
//            if (TextUtils.isEmpty(birth)) {
//                mBirthDate.setText(R.string.unknown);
//                findViewById(R.id.i_d_card_rl).setVisibility(View.GONE);
//            } else {
//                mBirthDate.setText(birth);
//            }

            mBirthDate.setText(R.string.unknown);

            String idCard = mUserInfo.getOort_idcard();


            findViewById(R.id.i_d_card_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(idCard)) {
                mIDCard.setText(R.string.unknown);
                findViewById(R.id.i_d_card_rl).setVisibility(View.GONE);
            } else {
                mIDCard.setText(idCard);
                findViewById(R.id.i_d_card_rl).setVisibility(View.GONE);
            }

            String email = mUserInfo.getOort_email();
            findViewById(R.id.user_email_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(email)) {
                mUserEmail.setText(R.string.unknown);
                findViewById(R.id.user_email_rl).setVisibility(View.GONE);
            } else {
                mUserEmail.setText(email);
            }

            String phoneNumber = mUserInfo.getOort_phone();
            findViewById(R.id.phone_number_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(phoneNumber)) {
                mPhoneNumber.setText(R.string.unknown);
                findViewById(R.id.phone_number_rl).setVisibility(View.GONE);
            } else {
                mPhoneNumber.setText(phoneNumber);
            }

            String userCode = mUserInfo.getOort_code();
            findViewById(R.id.user_code_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(userCode)) {
                mUserCode.setText(R.string.unknown);
                findViewById(R.id.user_code_rl).setVisibility(View.GONE);
            } else {
                mUserCode.setText(userCode);
            }
            //0禁用,1正常...9删除
            int userStatus = mUserInfo.getOort_status();
            findViewById(R.id.user_status_rl).setVisibility(View.VISIBLE);
            if (userStatus == 0) {
                mUserStatus.setText(R.string.forbidden);
            } else if (userStatus == 1) {
                mUserStatus.setText(R.string.normal);
            } else if (userStatus == 9) {
                mUserStatus.setText(R.string.delete);
            } else {
                mUserStatus.setText(R.string.unknown);
                findViewById(R.id.user_status_rl).setVisibility(View.GONE);
            }
            //1正式账号,2其它账号...9测试账号
            int userType = mUserInfo.getOort_usertype();
            findViewById(R.id.user_type_rl).setVisibility(View.VISIBLE);

            if (userType == 1) {
                mUserType.setText(R.string.formal_account_number);
            } else if (userType == 2) {
                mUserType.setText(R.string.other_account_number);
            } else if (userType == 9) {
                mUserType.setText(R.string.test_account_number);
            } else {
                mUserType.setText(R.string.unknown);
                findViewById(R.id.user_type_rl).setVisibility(View.GONE);
            }

            String userName = mUserInfo.getOort_name();
            findViewById(R.id.user_name_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(userName)) {
                mUserName.setText(R.string.unknown);
                findViewById(R.id.user_name_rl).setVisibility(View.GONE);
            } else {
                mUserName.setText(userName);
            }

            String deptName = mUserInfo.getOort_depname();
            findViewById(R.id.category_dept_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(deptName)) {
                mCategoryDept.setText(R.string.unknown);
                findViewById(R.id.category_dept_rl).setVisibility(View.GONE);
            } else {
                mCategoryDept.setText(deptName);
            }

            String deptCode = mUserInfo.getOort_depcode();

            findViewById(R.id.dept_code_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(deptCode)) {
                mDeptCode.setText(R.string.unknown);
                findViewById(R.id.dept_code_rl).setVisibility(View.GONE);
            } else {
                mDeptCode.setText(deptCode);
            }

            String quarters = mUserInfo.getOort_postname();
            findViewById(R.id.quarters_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(quarters)) {
                mQuarters.setText(R.string.unknown);
                findViewById(R.id.quarters_rl).setVisibility(View.GONE);
            } else {
                mQuarters.setText(quarters);
            }

            String job = mUserInfo.getOort_jobname();
            findViewById(R.id.job_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(job)) {
                mJob.setText(R.string.unknown);
                findViewById(R.id.job_rl).setVisibility(View.GONE);
            } else {
                mJob.setText(job);
            }

            String relatedDeptName = mUserInfo.getOort_rdepname();
            findViewById(R.id.related_dept_name_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(relatedDeptName)) {
                mRelatedDeptName.setText(R.string.unknown);
                findViewById(R.id.related_dept_name_rl).setVisibility(View.GONE);
            } else {
                mRelatedDeptName.setText(relatedDeptName);
            }

            String officeNumber = mUserInfo.getOort_office();
            findViewById(R.id.house_number_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(officeNumber)) {
                mHouseNumber.setText(R.string.unknown);
                findViewById(R.id.house_number_rl).setVisibility(View.GONE);
            } else {
                mHouseNumber.setText(officeNumber);
            }

            String oph = mUserInfo.getOort_tel();
            findViewById(R.id.oph_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(oph)) {
                mOph.setText(R.string.unknown);
                findViewById(R.id.oph_rl).setVisibility(View.GONE);
            } else {
                mOph.setText(oph);
            }

            String small_code = mUserInfo.getOort_pphone();
            findViewById(R.id.samall_code_rl).setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(oph)) {
                tv_small_code.setText(R.string.unknown);
                findViewById(R.id.samall_code_rl).setVisibility(View.GONE);
            } else {
                tv_small_code.setText(small_code);
            }

        }
    }

    @Override
    protected void initData() {
        if (mUserInfo != null) {
            if (mUserInfo.getOort_loginid().equals(UserInfoUtils.getInstance(CommonApplication.getAppContext()).getLoginId())) {
                mSendMessage.setVisibility(View.GONE);
                mLoadingDialog.dismiss();
            } else {
                //获取IM好友列表 判断是否为好友
                HttpResult.getIMFriendList(mUserInfo.getImuserid());
                //获取IMUserInfo
                HttpResult.getIMUser(mUserInfo.getImuserid());
                mLoadingDialog.dismiss();
            }

        }
    }

    @Override
    protected void initEvent() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mFriend = FriendDao.getInstance().getFriend(mLoginUserId, mUserId);

//                BasicUserInfoWindow menuWindow = new BasicUserInfoWindow(PersonDetailActivity.this, itemsOnClick, mFriend, mUser);
                BasicUserInfoWindow menuWindow = new BasicUserInfoWindow(PersonDetailActivity.this, null, null, null);
                // 显示窗口
                menuWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                // +x右,-x左,+y下,-y上
                // pop向左偏移显示
                menuWindow.showAsDropDown(view,
                        -(menuWindow.getContentView().getMeasuredWidth() - view.getWidth() / 2 - 40),
                        0);
                darkenBackground(0.6f);
                menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        darkenBackground(1f);
                    }
                });
            }
        });

        //跳转至聊天界面
        mSendMessage.setOnClickListener(v -> {
            if (mAttentionUser != null) {
                startChat();
            } else {
                FLAG_SEND = true;
                HttpResult.addIMFriend(mUserInfo.getImuserid());//mUserInfo.getImuserid()
            }
        });

        btn_msg.setOnClickListener(v -> {
            if (mAttentionUser != null) {
                startChat();
            } else {
                FLAG_SEND = true;
                HttpResult.addIMFriend(mUserInfo.getImuserid());
            }
        });
        btn_call.setOnClickListener(v -> {
            if (mAttentionUser != null) {
                startChatAudio();
            } else {
                FLAG_SEND = true;
                //HttpResult.addIMFriend(mUserInfo.getImuserid());
                HttpResult.addIMFriend(mUserInfo.getImuserid());
            }
        });
        btn_video.setOnClickListener(v -> {

//            if (coreManager.isLogin()) {
//                dial(CallConstants.Video);
//            } else {
//                coreManager.autoReconnectShowProgress(this);
//            }
            if (mAttentionUser != null) {
                startChatVideo();
                //startChat();
            } else {
                FLAG_SEND = true;
                //HttpResult.addIMFriend(mUserInfo.getImuserid());
                HttpResult.addIMFriend(mUserInfo.getImuserid());
            }
        });

        //跳转至二维码
        mQRCodeRL.setOnClickListener(v -> {
            if (mUser != null) {
                Intent intent = new Intent(mContext.getApplicationInfo().processName + ".qr_code");
                intent.putExtra("isgroup", false);
                String userId = mUser.getAccount();

                if (TextUtils.isEmpty(userId)) {
                    userId = mUser.getUserId();
                }
                intent.putExtra("userid", userId);
                intent.putExtra("userAvatar", mUser.getUserId());
                intent.putExtra("nickName", mUser.getNickName());
                intent.putExtra("sex", mUser.getSex());
                startActivity(intent);
            }
        });

        //跳转至拨号页
        mPhoneNumberRL.setOnClickListener(v -> {
            startActivity(new Intent(android.content.Intent.ACTION_DIAL,
                    Uri.parse("tel:" + mPhoneNumber.getText().toString().trim())));
        });
    }

//    private void realDial(int type, String meetUrl) {
//        ChatMessage message = new ChatMessage();
//        if (type == CallConstants.Audio) {// 语音通话
//            message.setType(XmppMessage.TYPE_IS_CONNECT_VOICE);
//            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.voice_call));
//        } else if (type == CallConstants.Video) {// 视频通话
//            message.setType(XmppMessage.TYPE_IS_CONNECT_VIDEO);
//            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.video_call));
//        } else if (type == CallConstants.Screen) {// 屏幕共享
//            message.setType(XmppMessage.TYPE_IS_CONNECT_SCREEN);
//            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.screen_call));
//        }
//        message.setFromUserId(mLoginUserId);
//        message.setFromUserName(mLoginNickName);
//        message.setToUserId(mFriend.getUserId());
//        if (!TextUtils.isEmpty(meetUrl)) {
//            message.setFilePath(meetUrl);
//        }
//        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
//        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
//        sendMsg(message);
//        Intent intent = new Intent(this, Jitsi_pre.class);
//        intent.putExtra("type", type);
//        intent.putExtra("fromuserid", mLoginUserId);
//        intent.putExtra("touserid", mFriend.getUserId());
//        intent.putExtra("username", mFriend.getNickName());
//        if (!TextUtils.isEmpty(meetUrl)) {
//            intent.putExtra("meetUrl", meetUrl);
//        }
//        startActivity(intent);
//    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(EventMessage event) {


        if (event.getT() instanceof User) {
            mUser = (User) event.getT();
        } else if (event.getT() instanceof AttentionUser) {

            mAttentionUser = (AttentionUser) event.getT();
            if (FLAG_SEND) {
                startChat();
            }
        }else {
            ToastUtil.showToast(this,event.getMessage());
        }
        mLoadingDialog.dismiss();
    }

    /**
     * 改变屏幕背景色
     */
    public void darkenBackground(Float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    /**
     * 跳转聊天界面
     */
    private void startChat() {
        Intent intent = new Intent(mContext.getApplicationInfo().processName + ".chat");
        intent.putExtra("friend", mAttentionUser);
        intent.putExtra("isserch", false);
        mContext.startActivity(intent);
        finish();
    }

    private void startChatVideo() {

//        Intent intent = new Intent(mContext.getApplicationInfo().processName + ".chat");
//        intent.putExtra("friend", mAttentionUser);
//        intent.putExtra("isserch", true);
//        intent.putExtra("isvideo", true);
//        mContext.startActivity(intent);
//        finish();
//        Intent intent = new Intent(mContext.getApplicationInfo().processName + ".chatVideo");
//        intent.putExtra("type", 2);
//        intent.putExtra("fromuserid", mUserInfo.getImuserid());
//        intent.putExtra("touserid", mAttentionUser.getUserId());
//        intent.putExtra("username", mAttentionUser.getRemarkName());
//        intent.putExtra("isvideo", false);
//
//        mContext.startActivity(intent);
//        finish();

        EventBus.getDefault().post(new MessageEventInviteCall(CallConstants.Video, mUserInfo.getImuserid(),mUserInfo.getOort_name(), UUID.randomUUID().toString()));
    }

    private void startChatAudio() {


        EventBus.getDefault().post(new MessageEventInviteCall(CallConstants.Audio, mUserInfo.getImuserid(),mUserInfo.getOort_name(), UUID.randomUUID().toString()));
    }
    /**
     * 启动Activity
     *
     * @param context
     * @param userInfo
     */
    public static void actionStart(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, PersonDetailActivity.class);
        intent.putExtra(Constants.OBJ, userInfo);
        context.startActivity(intent);

    }

    /**
     * 获取sso用户信息，并启动Activity
     *
     * @param context
     * @param imUserId
     */
    public static void actionStart(Context context, String imUserId) {
        HttpRequestCenter.getUserInfoByIMUserId(imUserId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {

                Result<UserInfo> result = new Gson().fromJson(s, new TypeToken<Result<UserInfo>>() {
                }.getType());
                if (result.isOk()) {
                    UserInfo userInfo = result.getData();
                    if (userInfo != null) {

                        actionStart(context, userInfo);
                    }
                }
            }

        });

    }

}
