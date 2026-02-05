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
import com.oortcloud.contacts.http.HttpConstants;
import com.oortcloud.contacts.observer.DataHandle;
import com.oortcloud.contacts.observer.Observer;

import java.util.List;

/**
 * @ProjectName: omm-master
 * @FileName: UNSelectContactsAdapter.java
 * @Function:
 * @Author: zzj / @CreateDate: 20/03/21 13:12
 * @UpdateUser:
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UNSelectContactsAdapter extends RecyclerView.Adapter<UNSelectContactsAdapter.ViewHolder> implements Observer {
    private LayoutInflater mInflater;
    private List<UserInfo> mData;

    private Context mContext;

    public UNSelectContactsAdapter(Context context, List<UserInfo> data) {
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
        UserInfo userInfo = mData.get(position);
        Glide.with(mContext).load(HttpConstants.PHOTO_URL + userInfo.getOort_uuid())
                .error(R.mipmap.default_head_portrait)
                .into(holder.mPortraitView);

        holder.mNameView.setText(userInfo.getOort_name());

        holder.mCheckBoxView.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                if (!DataHandle.getInstance().getUserData().contains(userInfo)) {
                    DataHandle.getInstance().addUser(userInfo);

                }
            } else {
                if (DataHandle.getInstance().getUserData().contains(userInfo)) {
                    DataHandle.getInstance().removeUser(userInfo);

                }

            }

        });

        if (DataHandle.getInstance().getMap() != null && DataHandle.getInstance().getMap().containsKey(userInfo.getOort_uuid())) {
            holder.mCheckBoxView.setChecked(true);
        } else {
            holder.mCheckBoxView.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, mNameView, mJobView;
        ImageView mPortraitView;
        CheckBox mCheckBoxView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tv_tag);
            mNameView = itemView.findViewById(R.id.name_tv);
            mJobView = itemView.findViewById(R.id.job_tv);
            mPortraitView = itemView.findViewById(R.id.portrait_view);
            mCheckBoxView = itemView.findViewById(R.id.checkbox);

        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<UserInfo> list) {
        if (list != null) {
            this.mData = list;
        }
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void notifyMsg() {
        notifyDataSetChanged();
//        if (mData.contains(DataHandle.getInstance().getUser())) {
//            int index = mData.indexOf(DataHandle.getInstance().getUser());
//            mViewHolder.get(index).mCheckBoxView.setChecked(false);
//        }

    }
}
