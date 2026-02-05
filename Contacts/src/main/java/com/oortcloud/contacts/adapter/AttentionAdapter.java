package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.contacts.utils.IntentFilter;
import com.oortcloud.contacts.utils.omm.AvatarHelper;

import java.util.List;

/**
 * @ProjectName: omm-master
 * @FileName: AttentionAdapter.java
 * @Function:
 * @Author: zzj / @CreateDate: 20/03/16 21:37
 * @Version: 1.0
 */
public class AttentionAdapter extends RecyclerView.Adapter<AttentionAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<AttentionUser> mData;
    private Context mContext;

    public AttentionAdapter(Context context, List<AttentionUser> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_personnel_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvTag.setVisibility(View.VISIBLE);
            holder.tvTag.setText(mData.get(position).getLetters());
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(AvatarHelper.getAvatarUrl(mData.get(position).getToUserId(), true))
                .error(R.mipmap.default_head_portrait)
                .into(holder.portrait);


        holder.itemView.setOnClickListener(v -> {
                startChatActivity(mData.get(position));


        });

        if (!TextUtils.isEmpty(mData.get(position).getRemarkName())) {
            holder.tvName.setText(this.mData.get(position).getRemarkName());

        } else {
            holder.tvName.setText(this.mData.get(position).getToNickname());
        }

        holder.portrait.setOnClickListener(v -> {
                Intent intent = new Intent(IntentFilter.ACTION_BASICINFO_ACTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId" , mData.get(position).getToUserId());
                mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName;
        ImageView portrait;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTag = itemView.findViewById(R.id.tv_tag);
            tvName = itemView.findViewById(R.id.tv_name);
            portrait = itemView.findViewById(R.id.portrait_view);

        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<AttentionUser> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 隐式启动消息页
     */
    private void startChatActivity(AttentionUser attentionUser) {
        Intent intent = new Intent(IntentFilter.ACTION_CHAT_ACTIVITY);
        intent.putExtra("friend", attentionUser);
        intent.putExtra("isserch", false);
       mContext.startActivity(intent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private UserInfoAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(UserInfoAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
