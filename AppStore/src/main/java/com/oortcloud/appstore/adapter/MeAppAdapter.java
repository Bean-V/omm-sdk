package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.databinding.ItemMeAppLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.widget.DownloadProgressButton;
import com.oortcloud.appstore.widget.listener.DownloadListener;

import java.util.List;



/**
 * @filename:
 * @function： 类型应用Adapter
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/15 20:45
 */
public class MeAppAdapter extends BaseRecyclerViewAdapter<AppInfo> {

    public MeAppAdapter(Context context , List<AppInfo> list) {
        super(context);
        lists = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_me_app_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
      final   ViewHolder holder = (ViewHolder) viewHolder;
        final AppInfo info = lists.get(i);
        holder.appInfo = info;
        Glide.with(mContext).load(info.getIcon_url()).into((holder.appImg));
        holder.appName.setText(info.getApplabel());

        holder.appImg.setOnClickListener(view -> {
            //holder.appImg.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            AppEventUtil.onClick(info , new DownloadListener(info , holder.install , "") );
        });
//        holder.appImg.clearColorFilter();//清除滤镜
//        holder.install.setState(DownloadProgressButton.STATE_DOWNLOADING);
        switch (holder.appInfo.getTerminal()){
            case 0:
                if (AppInfoManager.getInstance().isContains(info)) {
                    holder.install.setState(DownloadProgressButton.STATE_OPEN);
                    holder.install.setCurrentText(mContext.getString(R.string.open_str));

                } else {
                    holder.install.setState(DownloadProgressButton.STATE_NORMAL);
                    holder.install.setCurrentText(mContext.getString(R.string.load_str));
                }
                break;
            case 1 :
            case 2 :
            case 6 :
                holder.install.setState(DownloadProgressButton.STATE_OPEN);
                holder.install.setCurrentText(mContext.getString(R.string.open_str));
                break;

        }

        holder.install.setOnClickListener(view -> {
            //下载 安装
                AppEventUtil.onClick(info , new DownloadListener(info , holder.install , "") );
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemMeAppLayoutBinding binding; // 替换为实际生成的 Binding 类名

        ImageView appImg;
        TextView appName;
        DownloadProgressButton install;
        AppInfo appInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMeAppLayoutBinding.bind(itemView); // 初始化 ViewBinding

            // 通过 ViewBinding 初始化视图变量
            appImg = binding.appImg;
            appName = binding.tvAppName;
            install = binding.tvInstall;
        }


    }







}
