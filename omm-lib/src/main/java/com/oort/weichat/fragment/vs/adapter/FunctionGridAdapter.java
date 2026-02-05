package com.oort.weichat.fragment.vs.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;
import com.oortcloud.appstore.AppStoreActivity;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.basemodule.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FunctionGridAdapter extends RecyclerView.Adapter<FunctionGridAdapter.ViewHolder> {
    private Context mContext;
    private List<AppInfo> mAppInfos = new ArrayList<>();

    public FunctionGridAdapter(Context context, List<AppInfo> appInfos){
        mContext = context;
        mAppInfos = appInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_function_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mAppInfos == null || mAppInfos.isEmpty()){
            return;
        }

        AppInfo appInfo = mAppInfos.get(position);
        // 设置功能名称
        holder.tvFunctionName.setText(appInfo.getApplabel());
        boolean isMore = appInfo.getApplabel().equals(mContext.getString(R.string.more));
        if (isMore){
            holder.ivFunctionIcon.setImageResource(R.mipmap.ic_more);
        }else {
            // 设置图标
            ImageLoader.loadImage( holder.ivFunctionIcon,appInfo.getIcon_url(), com.oortcloud.basemodule.R.mipmap.icon_collect_im_01);
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (isMore){
                Intent in = new Intent(mContext, AppStoreActivity.class);
                mContext.startActivity(in);
                return;
            }
            Intent intent = new Intent(mContext,  AppManagerService.class);
            intent.putExtra("packageName", appInfo.getApppackage());
            intent.putExtra("params", "");
            mContext.startService(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mAppInfos.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFunctionIcon;
        TextView tvFunctionName;

        ViewHolder(View itemView) {
            super(itemView);
            ivFunctionIcon = itemView.findViewById(R.id.iv_function_icon);
            tvFunctionName = itemView.findViewById(R.id.tv_function_name);
        }
    }

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position, AppInfo appInfo);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // 更新数据的方法
    public void updateData(List<AppInfo> newAppInfos) {
        if (newAppInfos != null) {
            mAppInfos.clear();
            mAppInfos.addAll(newAppInfos);
            notifyDataSetChanged();
        }
    }

    // 添加数据的方法
    public void addData(AppInfo appInfo) {
        mAppInfos.add(appInfo);
        notifyItemInserted(mAppInfos.size() - 1);
    }

    // 清空数据的方法
    public void clearData() {
        mAppInfos.clear();
        notifyDataSetChanged();
    }
}
