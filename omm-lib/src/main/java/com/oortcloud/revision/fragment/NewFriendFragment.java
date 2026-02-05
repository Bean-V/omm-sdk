package com.oortcloud.revision.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.oort.weichat.fragment.FriendFragment;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.groupchat.FaceToFaceGroup;
import com.oort.weichat.ui.groupchat.RoomFragment;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.nearby.PublicNumberSearchActivity;
import com.oort.weichat.ui.nearby.UserSearchActivity;
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
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.sso.Authority;
import com.oortcloud.contacts.fragment.ContactsFragment;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

/**
 * @filename:
 * @author: zzj/@date: 2021/12/28 12:27
 * @version： v1.0
 * @function：
 */
public class NewFriendFragment extends EasyFragment {
    ViewPager mViewPager;
    ImageView mAddUser;
    ImageView mBack;
    TabLayout mTabLayout;

    private Context mContext;
    private TableViewPagerAdapter mAdapter;


    private Authority mAuthority;
    private MessagePopupWindow mMessagePopupWindow;

    @Override
    protected int inflateLayoutId() {
        return R.layout.activity_friend;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        mContext = getActivity();

        mViewPager = findViewById(R.id.view_pager);
        mAddUser = findViewById(R.id.add_img);
        mBack = findViewById(R.id.back);
        mTabLayout = findViewById(R.id.tab_layout);
        initActionBar();
        initView();
        initData();
    }

    private void initActionBar() {
        mBack.setVisibility(View.GONE);
    }

    private void initView() {




        mAdapter = new TableViewPagerAdapter(getChildFragmentManager());
        mAdapter.reset(new String[]{getString(R.string.recent_contact),getString(R.string.org) ,getString(R.string.group_0)});//,getString(R.string.service_number)

        mAdapter.reset(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mBack.setOnClickListener(v -> {
            getActivity().finish();
        });
        mAddUser.setOnClickListener(v -> {


            mMessagePopupWindow = new MessagePopupWindow(getActivity(), this, coreManager);
            mMessagePopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mMessagePopupWindow.showAsDropDown(v,
                    -(mMessagePopupWindow.getContentView().getMeasuredWidth() - v.getWidth() / 2 - 40),
                    0);
//            if (mAuthority != null && mAuthority.getIshidden() == 1) {
//                new ContactsManagerPopWindow((Activity) mContext  ,v);
//            }else {
//                startActivity(new Intent(mContext, UserSearchActivity.class));
//            }

        });
    }

    private void initData() {
        HttpRequestCenter.getMyAuthority().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<Authority> result = new Gson().fromJson(s,  new TypeToken<Result<Authority>>() {}.getType());
                if (result.isOk()) {

                    mAuthority =  result.getData();

                }
            }

        });
    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        //联系人
        fragments.add(new FriendFragment());
        //组织架构
        fragments.add( new ContactsFragment());
        //群组
        fragments.add(new RoomFragment());
        //公众号
//        fragments.add(new PublishNumberFragment());
        return fragments;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.create_task) {
            mMessagePopupWindow.dismiss();
            String packagename = "com.task_management.oort";
            Intent intent = new Intent(mContext, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", packagename);

            mContext.startActivity(intent);
            OperLogUtil.msg("联系人界面创建任务");
        } else if (id == R.id.search_public_number) {// 搜索公众号
            mMessagePopupWindow.dismiss();
            PublicNumberSearchActivity.start(mContext);
        } else if (id == R.id.create_group) {// 发起群聊
            OperLogUtil.msg("联系人界面创建群聊");
            mMessagePopupWindow.dismiss();
            //startActivity(new Intent(mContext, SelectContactsActivity.class));

            Intent in = new Intent(mContext, PersonPickActivity.class);
            startActivityForResult(in, 100);
            PersonPickActivity.pickFinish = null;
            PersonPickActivity.pickFinish_v2 = null;
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
        } else if (id == R.id.add_friends) {
            OperLogUtil.msg("联系人界面添加朋友");
            // 添加朋友
            mMessagePopupWindow.dismiss();
            startActivity(new Intent(mContext, UserSearchActivity.class));
        } else if (id == R.id.scanning) {
            OperLogUtil.msg("联系人界面扫一扫");
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

    String mLoginUserId = "";
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
