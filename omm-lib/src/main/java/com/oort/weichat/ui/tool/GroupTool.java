package com.oort.weichat.ui.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.Area;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.event.EventCreateGroupFriend;
import com.oort.weichat.bean.event.EventSendVerifyMsg;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.MucgroupUpdateUtil;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.secure.RSA;
import com.oort.weichat.util.secure.chat.SecureChatUtil;
import com.oort.weichat.view.TipDialog;
import com.oort.weichat.view.VerifyDialog;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.omm.AddAttentionResult;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.utils.ChangeUtils;
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

public class GroupTool {



    String chatKey = "";
    boolean mQuicklyCreate = false;
    List  mSelectPositions = new ArrayList();
    Context mContext;
    private String mLoginUserId;
    private CoreManager coreManager;
    private Friend mFriend;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type = 0;

    public CreateSuc getSuc() {
        return suc;
    }

    public void setSuc(CreateSuc suc) {
        this.suc = suc;
    }

    private CreateSuc suc;



    public interface CreateSuc{

        void suc(Friend friend,boolean finsh);
    }

    public void ceateRoom(Context context,CoreManager coreManager0){
        ceateRoom(context,coreManager0,null);
    }

    public void ceateRoom(Context context,CoreManager coreManager0,List addtions){
        mContext = context;
        coreManager = coreManager0;
        Intent in = new Intent(mContext, PersonPickActivity.class);
        if(addtions != null){
            in.putStringArrayListExtra("addtions", (ArrayList<String>) addtions);
        }
        ((Activity)mContext).startActivityForResult(in,100);

        PersonPickActivity.pickFinish = null;
        PersonPickActivity.pickFinish_v2 = null;
        PersonPickActivity.pickFinish = new PersonPickActivity.PickFinish() {
            @Override
            public void finish(List ids, String names) {
                mSelectPositions.clear();
                if(mContext != null){

                    if(ids.size() == 0){
                        return;
                    }
                    if(ids.size() == 1){

//                        Intent intent = new Intent(mContext.getApplicationInfo().processName + ".chat");
//                        intent.putExtra("friend", mAttentionUser);
//                        intent.putExtra("isserch", false);
//                        mContext.startActivity(intent);

                        //HttpResult.addIMFriend(mUserInfo.getImuserid());


                        String userId = (String) ids.get(0);
                        startChat(userId,true);



                        return;
                    }

                    if(type == 1){

                        int i = 0;
                        for(Object o : ids){
                            String userId = (String) o;
                            startChat(userId,i == (ids.size() - 1));
                            i ++;
                        }

                        return;
                    }
                    mSelectPositions.addAll(ids);

                    mLoginUserId = UserInfoUtils.getInstance(mContext).getLoginUserInfo().getImuserid();

                    createGroupChat(names, "", 0, 1, 0, 1, 1, 0);
                }
            }
        };
    }




    private void startChat(String userId,boolean isLast) {

        HttpRequestCenter.getIMFriendList().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<List<AttentionUser>> result = new Gson().fromJson(s, new TypeToken<Result<List<AttentionUser>>>() {
                }.getType());
                if (result.getResultCode() == 1) {
                    List<AttentionUser> mAttentionList = result.getData();

                    if (mAttentionList != null) {
                        AttentionUser attentionUserTmp = null;
                        for (AttentionUser attentionUser : mAttentionList) {
                            if (attentionUser.getToUserId().equals(userId)) {

                                attentionUserTmp = attentionUser;



                                if(suc != null){

                                    suc.suc(ChangeUtils.changeFriend((AttentionUser) attentionUser),isLast);
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            suc.suc(ChangeUtils.changeFriend((AttentionUser) attentionUser));
//                                        }
//                                    }, 1000);

                                }else{
                                    Intent intent = new Intent(mContext.getApplicationInfo().processName + ".chat");
                                    intent.putExtra("friend", attentionUser);
                                    intent.putExtra("isserch", false);
                                    mContext.startActivity(intent);
                                }

                                break;
                            }
                        }

                        if(attentionUserTmp == null){

                            HttpRequestCenter.addIMFriend(userId).subscribe(new RxBusSubscriber<String>() {
                                @Override
                                protected void onEvent(String s) {
                                    Result<AddAttentionResult> result = new Gson().fromJson(s, new TypeToken<Result<AddAttentionResult>>() {
                                    }.getType());
                                    if (result.getResultCode() == 1) {
                                        startChat(userId,isLast);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

    }


    public void createGroupChatWithId(Context cx,CoreManager manager,final String roomJid,final String roomName,List userIds) {

        coreManager = manager;
        mContext = cx;
        mSelectPositions.clear();
        mSelectPositions.addAll(userIds);
        mSelectPositions.add("10000716");

        mLoginUserId = UserInfoUtils.getInstance(mContext).getLoginUserInfo().getImuserid();


        String imuserid = UserInfoUtils.getInstance(cx).getLoginUserInfo().getImuserid();
        if(mSelectPositions.size() == 2 && mSelectPositions.contains(imuserid)) {
            mSelectPositions.remove(imuserid);
            String userId = (String) mSelectPositions.get(0);
            startChat(userId, true);

            return;
        }




        final String roomDesc = ""; int isRead = 0; int isLook = 1;
        int isNeedVerify = 0; int isShowMember = 1; int isAllowSendCard = 1; int isSecretGroup = 0;

        final boolean isExist = coreManager.createMucRoomId(roomJid);
        if (isExist) {


            Map<String, String> params = new HashMap<>();
            params.put("access_token", IMUserInfoUtil.getInstance().getToken());
            params.put("roomId", roomJid);

            HttpUtils.get().url(coreManager.getConfig().ROOM_GET)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                        @Override
                        public void onResponse(ObjectResult<MucRoom> result) {
                            if (result.getResultCode() == 1 && result.getData() != null) {
                                final MucRoom mucRoom = result.getData();
                                if (mucRoom.getIsNeedVerify() == 1) {
                                    VerifyDialog verifyDialog = new VerifyDialog(mContext);
                                    verifyDialog.setVerifyClickListener(MyApplication.getInstance().getString(R.string.tip_reason_invite_friends), new VerifyDialog.VerifyClickListener() {
                                        @Override
                                        public void cancel() {

                                        }

                                        @Override
                                        public void send(String str) {
                                            EventBus.getDefault().post(new EventSendVerifyMsg(mucRoom.getUserId(), mucRoom.getJid(), str));
                                        }
                                    });
                                    verifyDialog.show();
                                    return;
                                }
                                joinRoom(mucRoom, coreManager.getSelf().getUserId());
                            } else {
                                ToastUtil.showErrorData(mContext);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            ToastUtil.showNetError(mContext);
                        }
                    });

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

        DialogHelper.showDefaulteMessageProgressDialog(mContext);

        HttpUtils.get().url(coreManager.getConfig().ROOM_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                    @Override
                    public void onResponse(ObjectResult<MucRoom> result) {
                        DialogHelper.dismissProgressDialog();
                        if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkSuccess(mContext, result)) {
                            if (mQuicklyCreate) {
                                mContext.sendBroadcast(new Intent(OtherBroadcast.QC_FINISH)); // 快速建群成功，发送广播关闭之前的单聊界面
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

    private void createGroupChat(final String roomName, final String roomDesc, int isRead, int isLook,
                                 int isNeedVerify, int isShowMember, int isAllowSendCard, int isSecretGroup) {


        final String roomJid = coreManager.createMucRoom(roomName);
        if (TextUtils.isEmpty(roomJid)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.create_room_failed));
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

        DialogHelper.showDefaulteMessageProgressDialog(mContext);

        HttpUtils.get().url(coreManager.getConfig().ROOM_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<MucRoom>(MucRoom.class) {

                    @Override
                    public void onResponse(ObjectResult<MucRoom> result) {
                        DialogHelper.dismissProgressDialog();
                        if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkSuccess(mContext, result)) {
                            if (mQuicklyCreate) {
                                mContext.sendBroadcast(new Intent(OtherBroadcast.QC_FINISH)); // 快速建群成功，发送广播关闭之前的单聊界面
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
        mFriend = friend;
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
        MucgroupUpdateUtil.broadcastUpdateUi(mContext);

        // 本地发送一条消息至该群 否则未邀请其他人时在消息列表不会显示
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(XmppMessage.TYPE_TIP);
        chatMessage.setFromUserId(mLoginUserId);
        chatMessage.setFromUserName(coreManager.getSelf().getNickName());
        chatMessage.setToUserId(mucRoom.getJid());
        chatMessage.setContent(mContext.getString(R.string.new_friend_chat));
        chatMessage.setPacketId(coreManager.getSelf().getNickName());
        chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mucRoom.getJid(), chatMessage)) {
            // 更新聊天界面
            MsgBroadcast.broadcastMsgUiUpdate(mContext);


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
            tipDialog.setmConfirmOnClickListener(mContext.getString(R.string.tip_over_member_size, mucRoom.getMaxUserSize()), () -> start(mucRoom.getJid(), mucRoom.getName()));
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

        DialogHelper.showDefaulteMessageProgressDialog(mContext);

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



        if(suc != null){
            suc.suc(mFriend,true);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    suc.suc(mFriend);
//                }
//            }, 100);


        }else{
            Intent intent = new Intent(mContext, MucChatActivity.class);
            intent.putExtra(AppConstant.EXTRA_USER_ID, jid);
            intent.putExtra(AppConstant.EXTRA_NICK_NAME, name);
            intent.putExtra(AppConstant.EXTRA_IS_GROUP_CHAT, true);
            mContext.startActivity(intent);
        }
    }


    Handler mHandler = new Handler();

    /**
     * 加入房间
     */
    private void joinRoom(final MucRoom room, final String loginUserId) {
        DialogHelper.showDefaulteMessageProgressDialog(mContext);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", room.getId());
        if (room.getUserId().equals(loginUserId))
            params.put("type", "1");
        else
            params.put("type", "2");

        MyApplication.mRoomKeyLastCreate = room.getJid();

        HttpUtils.get().url(coreManager.getConfig().ROOM_JOIN)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (com.xuan.xuanhttplibrary.okhttp.result.Result.checkSuccess(mContext, result)) {
                            EventBus.getDefault().post(new EventCreateGroupFriend(room));
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {// 给500ms的时间缓存，防止群组还未创建好就进入群聊天界面
                                    interMucChat(room.getJid(), room.getName());
                                }
                            }, 500);
                        } else {
                            MyApplication.mRoomKeyLastCreate = "compatible";
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                        MyApplication.mRoomKeyLastCreate = "compatible";
                    }
                });
    }

    /**
     * 进入房间
     */
    private void interMucChat(String roomJid, String roomName) {
        Intent intent = new Intent(mContext, MucChatActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, roomJid);
        intent.putExtra(AppConstant.EXTRA_NICK_NAME, roomName);
        intent.putExtra(AppConstant.EXTRA_IS_GROUP_CHAT, true);
        mContext.startActivity(intent);

        MucgroupUpdateUtil.broadcastUpdateUi(mContext);
    }


}
