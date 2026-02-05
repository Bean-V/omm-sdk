package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName:
 * @FileName: FriendAdapter.java
 * @Function: 添加好友及群聊 Adapter
 * @Author: zzj / @CreateDate: 20/03/19 12:52
 * @Version: 1.0
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<UserInfo> mData;
    private Context mContext;

    //记录被选中的item
    private Map<Integer, Boolean> map = new HashMap<>();
    //记录被选中的对象
    private UserInfo userInfo ;

    private boolean onBind;
    private int checkedPosition = -1;
    public FriendAdapter(Context context, List<UserInfo> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_personnel_layout, parent,false);
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
        Glide.with(mContext).load(mData.get(position).getOort_photo()).error(R.mipmap.default_head_portrait).into(holder.portrait);

        holder.tvName.setText(this.mData.get(position).getOort_name());

        holder.checkBox.setVisibility(View.VISIBLE);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    map.clear();
                    map.put(position, true);
                    checkedPosition = position;
                    userInfo = mData.get(position);
                } else {
                    map.remove(position);
                    if (map.size() == 0) {
                        checkedPosition = -1; //-1 代表一个都未选择
                        userInfo = null;
                    }
                }
                if (!onBind) {
                    notifyDataSetChanged();
                }
            }
        });

        onBind = true;
        if (map != null && map.containsKey(position)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        onBind = false;
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private UserInfoAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(UserInfoAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName;
        ImageView portrait;
        View itemView ;
        CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTag =  itemView.findViewById(R.id.tv_tag);
            tvName =  itemView.findViewById(R.id.tv_name);
            portrait =  itemView.findViewById(R.id.portrait_view);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
    /**
     * 提供给Activity刷新数据
     * @param list
     */
    public void updateList(List<UserInfo> list){
        this.mData = list;
        map.clear();
        checkedPosition = -1;
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

    public UserInfo getUserInfo(){
            return userInfo;
    }
}
