package com.oortcloud.oort_zhifayi.new_version.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.oort_zhifayi.R;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<Member> members;

    public MemberAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.tvName.setText(member.getName());

        // 设置在线状态
        holder.ivStatus.setImageResource(member.isOnline() ?
                R.drawable.ic_audio_on : R.drawable.ic_audio_on);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "选中：" + member.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> newMembers) {
        this.members = new ArrayList<>(newMembers); // 创建新列表避免引用问题
        notifyDataSetChanged(); // 通知 RecyclerView 刷新所有项
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus, ivAvatar;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.iv_status);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}