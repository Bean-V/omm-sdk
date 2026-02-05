package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
 * @FileName: SelectContactsAdapter.java
 * @Function:
 * @Author: zzj / @CreateDate: 20/03/21 14:47
 * @UpdateUser: 更新者 /@UpdateDate: 20/03/21 14:47
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SelectContactsAdapter  extends RecyclerView.Adapter<SelectContactsAdapter.ViewHolder> implements Observer {
    private LayoutInflater mInflater;
    private List<UserInfo> mData;

    private Context mContext;

    public SelectContactsAdapter(Context context, List<UserInfo> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_select_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        UserInfo userInfo = mData.get(position);

        Glide.with(mContext).load(HttpConstants.PHOTO_URL + userInfo.getOort_uuid())
                .error(R.mipmap.default_head_portrait)
                .into(holder.portrait);

            holder.tvName.setText(userInfo.getOort_name());

        holder.itemView.setOnClickListener(v -> DataHandle.getInstance().removeUser(userInfo));

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvName;
        ImageView portrait;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            portrait = itemView.findViewById(R.id.img_head_portrait);

        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list 更新列表
     */
    public void updateList(List<UserInfo> list) {

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


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void notifyMsg() {
        updateList(DataHandle.getInstance().getUserData());
    }
}
