package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.contacts.R;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.ImageLoader;

import java.util.List;


/**
 * @ProjectName: omm-master
 * @FileName: UserInfoAdapter.java
 * @Function: 部门树 用户信息Adapter
 * @Author: zhangzhijun / @CreateDate: 20/03/13 07:08
 * @Version: 1.0
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<UserInfo> mData;
    private Context mContext;

    public UserInfoAdapter(Context context, List<UserInfo> data) {
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
        holder.tvTag.setVisibility(View.GONE);
        UserInfo userInfo = mData.get(position);

        ImageLoader.loaderImage(holder.portrait, userInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    mOnItemClickListener.onItemClick(holder.itemView, position);

                PersonDetailActivity.actionStart(mContext, userInfo);
            }
        });


        holder.tvName.setText(userInfo.getOort_name());

//        holder.tvName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext, mData.get(position).getOort_name(),Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

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
    public void updateList(List<UserInfo> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
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

}
