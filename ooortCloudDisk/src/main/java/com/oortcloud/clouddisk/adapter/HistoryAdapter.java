package com.oortcloud.clouddisk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.bean.AppInfo;

import java.util.List;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/15 10:17
 * @version： v1.0
 * @function：
 */
public class HistoryAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<AppInfo> mData;

    public HistoryAdapter(Context context , List data){
        this.mContext = context;
        this.mData = data;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        AppInfo appInfo = mData.get(i);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.versionTV.setText(appInfo.getVersion()+ "版本介绍");
        viewHolder.contentTV.setText("-"+ appInfo.getVer_description());
    }

    public void  setData(List list){
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View view ;
        TextView versionTV;
        TextView contentTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            versionTV = itemView.findViewById(R.id.version_tv);
            contentTV = itemView.findViewById(R.id.content_tv);
        }
    }
}