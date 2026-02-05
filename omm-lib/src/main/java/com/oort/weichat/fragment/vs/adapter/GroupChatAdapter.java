package com.oort.weichat.fragment.vs.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.AppConstant;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.fragment.vs.ControlFragment;
import com.oort.weichat.sortlist.BaseSortModel;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.lccontact.PersonPickActivity;
import com.oort.weichat.ui.message.MucChatActivity;

import java.util.ArrayList;
import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {
    private List<BaseSortModel<Friend>> mGroupList = new ArrayList<>();
    private final ControlFragment mFragment;
    private  ActivityResultLauncher<Intent> launcher;
    private  CoreManager mCoreManager;
    public GroupChatAdapter(ControlFragment fragment, List<BaseSortModel<Friend>> groupList) {
        mFragment = fragment;
        mGroupList = groupList;
        //创建群聊回调
        createGroupChat();
    }

    public void setCoreManager(CoreManager coreManager){
        mCoreManager = coreManager;
    }
    private static final int TYPE_NORMAL = 0;  // 普通群聊项
    private static final int TYPE_CREATE = 1;  // 创建群聊项
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == TYPE_CREATE) {
            view = inflater.inflate(R.layout.item_group_chat_create, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_group_chat, parent, false);
        }
        return new ViewHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        // 根据数据或位置决定类型（示例：最后一项是“创建”按钮）
        return (position == getItemCount() - 1) ? TYPE_CREATE : TYPE_NORMAL;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mGroupList == null || mGroupList.isEmpty()) {
            return;
        }
        // 最后一项是“创建群聊”按钮
        if (position == getItemCount() - 1) {
            // 设置创建按钮的点击事件（不需要绑定数据）
            holder.itemView.setOnClickListener(v -> {
                Context context = mFragment.getContext();
                launcher.launch(new Intent(context, PersonPickActivity.class));
            });
            return;
        }

        BaseSortModel<Friend> group = mGroupList.get(position);
        Friend friend = group.getBean();

        // 设置群组名称
        holder.tvGroupName.setText(friend.getNickName());

        // 设置群组状态
        holder.tvGroupStatus.setText("在线");
//        holder.tvGroupStatus.setText(String.valueOf(friend.getGroupStatus()));

        
        // 设置状态信息
//        holder.tvStatus.setText(String.valueOf(friend.getStatus()));

//        String imUserId = UserSp.getInstance(mFragment.getContext()).getUserId("");
//        AvatarHelper.getInstance().displayAvatar(imUserId, friend, holder);
        // 设置百分比
//        holder.tvPercentage.setText(friend.getPercentage());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(mFragment.requireActivity(), MucChatActivity.class);
            intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
            intent.putExtra(AppConstant.EXTRA_NICK_NAME, friend.getNickName());
            mFragment.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivGroupAvatar;
        TextView tvGroupName;
        TextView tvGroupStatus;
        TextView tvStatus;
        TextView tvPercentage;

        ViewHolder(View itemView) {
            super(itemView);
            ivGroupAvatar = itemView.findViewById(R.id.iv_group_avatar);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvGroupStatus = itemView.findViewById(R.id.tv_group_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
        }
    }


    // 更新数据
    public void updateData(List<BaseSortModel<Friend>> newGroupList) {
        if (newGroupList != null) {
            mGroupList.clear();
            mGroupList.addAll(newGroupList);
            notifyDataSetChanged();
        }
    }

    // 添加数据
    public void addData(BaseSortModel<Friend> baseSortModel) {
        mGroupList.add(baseSortModel);
        notifyItemInserted(mGroupList.size() - 1);
    }

    // 清空数据
    public void clearData() {
        mGroupList.clear();
        notifyDataSetChanged();
    }

    public  void createGroupChat() {
       launcher = mFragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        Bundle bundle = intent.getExtras();
                        assert bundle != null;
                        ArrayList<String> imUserIds = bundle.getStringArrayList("imUserIds");
                        String names = bundle.getString("names");
                        if (imUserIds == null || imUserIds.isEmpty()) {
                            return;
                        }
                        IMUserCreateGroup imUserCreateGroup = new IMUserCreateGroup(mFragment.getContext());
                        imUserCreateGroup.setSelectUser(imUserIds ,mCoreManager);
                        // 处理返回数据
                        imUserCreateGroup.createGroupChat(names, "", 0, 1, 0, 1, 1, 0);
                    }
                }
        );
    }
}