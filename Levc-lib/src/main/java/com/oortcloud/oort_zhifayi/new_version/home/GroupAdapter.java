package com.oortcloud.oort_zhifayi.new_version.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.oort_zhifayi.databinding.ItemGroupBinding;


import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList = new ArrayList<>();

    private OnItemClickListener listener;

    // 定义接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 设置接口实例
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.bind(group);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void updateData(List<Group> groups) {
        groupList = groups;
        notifyDataSetChanged();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupBinding binding;

        public GroupViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Group group) {
            // 设置群组名称
            binding.groupName.setText(group.getName());

            // 设置群组信息
            binding.groupInfo.setText(group.getInfo());

            // 设置状态信息
            binding.groupStatus.setText(group.getStatus());

            // 设置头像列表 (最多显示 3 个头像)
            List<String> avatarIds = group.getAvatarIds();
            String testUrl = "http://gips0.baidu.com/it/u=3602773692,1512483864&fm=3028&app=3028&f=JPEG&fmt=auto?w=960&h=1280";
            Glide.with(itemView.getContext())
                    .load(testUrl) // 图片的 URL 或本地路径
                    .into(binding.avatar1);
//            binding.avatar1.setImageResource(avatarIds.get(0));
            binding.avatar1.setVisibility(View.VISIBLE);
//            if (avatarIds.size() > 0) {
//                binding.avatar1.setImageResource(avatarIds.get(0));
//                binding.avatar1.setVisibility(View.VISIBLE);
//            } else {
//                binding.avatar1.setVisibility(View.GONE);
//            }
//
//            if (avatarIds.size() > 1) {
//                binding.avatar2.setImageResource(avatarIds.get(1));
//                binding.avatar2.setVisibility(View.VISIBLE);
//            } else {
//                binding.avatar2.setVisibility(View.GONE);
//            }

//            if (avatarIds.size() > 2) {
//                binding.avatar3.setImageResource(avatarIds.get(2));
//                binding.avatar3.setVisibility(View.VISIBLE);
//            } else {
//                binding.avatar3.setVisibility(View.GONE);
//            }
        }
    }
}
