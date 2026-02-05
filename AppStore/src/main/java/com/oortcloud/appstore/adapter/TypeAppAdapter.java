package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.AppDetailedActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.databinding.ItemTypeAppLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DataInit;
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
public class TypeAppAdapter extends BaseRecyclerViewAdapter<AppInfo> {

    public TypeAppAdapter(Context context , List<AppInfo> list) {
        super(context);
        lists = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_type_app_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
      final   ViewHolder holder = (ViewHolder) viewHolder;
        final AppInfo info = lists.get(i);

        Glide.with(mContext).load(info.getIcon_url()).into((holder.appImg));
        holder.appName.setText(info.getApplabel());

        holder.appImg.setOnClickListener(view -> {
                AppDetailedActivity.actionStart(mContext, info);

        });
        holder.install.setState(DownloadProgressButton.STATE_DOWNLOADING);
        holder.install.setVisibility(View.VISIBLE);
        switch (info.getTerminal()){
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


        AppInfo applyApp = DataInit.getAppinfo(info.getApppackage());

        holder.itemView.setLayerType(View.LAYER_TYPE_NONE,null);
        if(applyApp != null)    {
            if (applyApp.getApply_status() == 2) {
                holder.install.setState(DownloadProgressButton.STATE_Apply);

            }
            if (applyApp.getApply_status() == 3) {
                holder.install.setState(DownloadProgressButton.STATE_Reviewing);
            }
            if (applyApp.getApply_status() == 4) {
                holder.install.setState(DownloadProgressButton.STATE_Reject);
            }



            if (applyApp.getApply_status() == 0) {
                holder.install.setState(DownloadProgressButton.STATE_Unused);
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);//灰度效果
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                holder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);

            }
        }
        if(DownloadListener.tmpListeners.size() > 0){

            if(DownloadListener.contain(info.getUid())) {

                DownloadListener ls = DownloadListener.getListener(info.getUid());
                ls.setProgressButton(holder.install);
            }
        }
        holder.install.setOnClickListener(view -> {
            //下载 安装
                AppEventUtil.onClick(info , new DownloadListener(info , holder.install , "") );
        });
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTypeAppLayoutBinding binding; // 替换为实际生成的 Binding 类

        ImageView appImg;
        TextView appName;
        DownloadProgressButton  install;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTypeAppLayoutBinding.bind(itemView); // 初始化 ViewBinding
            appImg = binding.appImg;
            appName = binding.tvAppName;
            install = binding.tvInstall;
        }


        // 使用 ViewBinding 访问视图


    }
}
