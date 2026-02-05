package com.oort.weichat.fragment.vs.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.call.CallConstants;
import com.oort.weichat.call.Jitsi_pre;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.util.TimeUtils;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.ToastUtil;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.utils.ChangeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final Context mContext;
    private final List<UserInfo> mContactList;
    String mLoginUserId = IMUserInfoUtil.getInstance().getUserId();
    String mLoginUserName;

    private final CoreManager coreManager;
    public ContactAdapter(Context context, List<UserInfo> contactList, CoreManager coreManager) {
        mContext = context;
        mContactList = contactList;
        mLoginUserName = UserInfoUtils.getInstance(mContext).getUserName();
        this.coreManager = coreManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mContactList == null || mContactList.isEmpty()) {
            return;
        }

        UserInfo userInfo = mContactList.get(position);

        if (userInfo.isPlaceholder()) {
            // 显示占位数据
            holder.tvNoData.setVisibility(View.VISIBLE);
            holder.layoutContactInfo.setVisibility(View.GONE);
            return;
        }

        // 设置头像
        if (userInfo.getOort_photo() != null && !userInfo.getOort_photo().isEmpty()) {
            ImageLoader.loadImage(holder.ivContactAvatar, userInfo.getOort_photo(), R.mipmap.ic_default_avatar);
        } else {
            holder.ivContactAvatar.setImageResource(R.mipmap.ic_default_avatar);
        }

        // 设置姓名
        holder.tvContactName.setText(userInfo.getOort_name());
        String phone = userInfo.getOort_phone();
        if (phone.isEmpty()){
            phone = userInfo.getOort_pphone();
        }
        // 设置电话号码
        holder.tvContactPhone.setText(phone.isEmpty()? "号码未绑定" : phone);

        String depName = userInfo.getOort_depname();
        // 设置部门
        holder.tvContactDepartment.setText(depName);

        // 设置点击事件
        setupClickListeners(holder, userInfo,  phone);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    // 在类顶部定义常量
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private void setupClickListeners(ViewHolder holder, UserInfo newUserInfoKY, String phone) {

        // 视频通话
        holder.ivVideoCall.setOnClickListener(v -> getFriend(newUserInfoKY, friend -> {
            realDial(friend , CallConstants.Video, null);
        }));

        // 电话通话
        holder.ivPhoneCall.setOnClickListener(v -> handlePhoneCall(phone, newUserInfoKY));

        // 聊天
        holder.ivChat.setOnClickListener(v -> getFriend(newUserInfoKY, this::startChat));

        // 整个卡片点击
        holder.itemView.setOnClickListener(v -> {
            // 处理卡片点击事件
        });
    }

    /**
     * 处理电话拨打逻辑
     */
    private void handlePhoneCall(String phone,  UserInfo newUserInfoKY) {
        if (isValidPhoneNumber(phone)) {
            dialPhoneNumber(phone);
        } else {
            // 号码无效，跳转到语音通话
            getFriend(newUserInfoKY, friend -> {
                realDial(friend , CallConstants.Audio, null);
            });
        }
    }

    /**
     * 验证手机号码有效性
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return phone.trim().matches(PHONE_REGEX);
    }

    /**
     * 跳转到拨号界面
     */
    private void dialPhoneNumber(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber.trim()));

            // 检查是否有应用可以处理这个Intent
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, "未找到拨号应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "无法拨号", Toast.LENGTH_SHORT).show();
        }
    }


    void getFriend(UserInfo userInfo , Listener listener){
        String imUserId = userInfo.getImuserid();

        if (imUserId.isEmpty()) {
            ToastUtil.showToast(mContext, "请先关联IM用户");
            return;
        }

        Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, imUserId);
        if (friend == null){
            HttpResult.addIMFriend(imUserId, attentionUser -> {
                if (attentionUser != null)
                    listener.onClick(ChangeUtils.changeFriend(attentionUser));
                else {
                    listener.onClick(null);
                    ToastUtil.showToast(mContext, "请稍后再试");

                }
            });

        }else {
            listener.onClick(friend);
        }
    }

    void  startChat(Friend friend){
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(ChatActivity.FRIEND, friend);
        intent.putExtra("isserch", false);
        mContext.startActivity(intent);
    }
    private void realDial(Friend friend , int type, String meetUrl) {
        ChatMessage message = new ChatMessage();
        if (type == CallConstants.Audio) {// 语音通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VOICE);
            message.setContent(mContext.getString(R.string.sip_invite) + " " + mContext.getString(R.string.voice_call));
        } else if (type == CallConstants.Video) {// 视频通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VIDEO);
            message.setContent(mContext.getString(R.string.sip_invite) + " " + mContext.getString(R.string.video_call));
        } else if (type == CallConstants.Screen) {// 屏幕共享
            message.setType(XmppMessage.TYPE_IS_CONNECT_SCREEN);
            message.setContent(mContext.getString(R.string.sip_invite) + " " + mContext.getString(R.string.screen_call));
        }
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginUserName);
        message.setToUserId(friend.getUserId());
        if (!TextUtils.isEmpty(meetUrl)) {
            message.setFilePath(meetUrl);
        }
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        sendMsg(friend, message);
        Intent intent = new Intent(mContext, Jitsi_pre.class);
        intent.putExtra("type", type);
        intent.putExtra("fromuserid", mLoginUserId);
        intent.putExtra("touserid", friend.getUserId());
        intent.putExtra("username", friend.getNickName());
        if (!TextUtils.isEmpty(meetUrl)) {
            intent.putExtra("meetUrl", meetUrl);
        }
        mContext.startActivity(intent);
    }
    private void sendMsg(Friend friend , ChatMessage message) {
        // 一些异步回调进来的也要判断xmpp是否在线，
        // 比如图片上传成功后，
//        if (isAuthenticated()) {
//            return;
//        }
        if (friend.getIsDevice() == 1) {
            coreManager.sendChatMessage(mLoginUserId, message);
        } else {
            coreManager.sendChatMessage(friend.getUserId(), message);
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivContactAvatar;
        TextView tvContactName;
        TextView tvContactPhone;
        TextView tvContactDepartment;
        ImageView ivVideoCall;
        ImageView ivPhoneCall;
        ImageView ivChat;
        TextView tvNoData;
        View layoutContactInfo;
        ViewHolder(View itemView) {
            super(itemView);
            tvNoData = itemView.findViewById(R.id.tv_no_data);
            layoutContactInfo = itemView.findViewById(R.id.layout_contact_info);
            ivContactAvatar = itemView.findViewById(R.id.iv_contact_avatar);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            tvContactPhone = itemView.findViewById(R.id.tv_contact_phone);
            tvContactDepartment = itemView.findViewById(R.id.tv_contact_department);
            ivVideoCall = itemView.findViewById(R.id.iv_video_call);
            ivPhoneCall = itemView.findViewById(R.id.iv_phone_call);
            ivChat = itemView.findViewById(R.id.iv_chat);
        }
    }


    // 更新数据
    public void updateData(List<UserInfo> newUserInfoKYList) {
        mContactList.clear();
        mContactList.addAll(newUserInfoKYList);
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(UserInfo newUserInfoKY) {
        mContactList.add(newUserInfoKY);
        notifyItemInserted(mContactList.size() - 1);
    }

    // 清空数据
    public void clearData() {
        mContactList.clear();
        notifyDataSetChanged();
    }

    public interface Listener {
        void onClick(Friend friend);
    }
} 