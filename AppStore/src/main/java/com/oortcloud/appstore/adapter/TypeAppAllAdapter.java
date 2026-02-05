package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.AppDetailedActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Constants;
import com.oortcloud.appstore.databinding.ItemTypeAppAllLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.widget.DownloadProgressButton;
import com.oortcloud.appstore.widget.listener.DownloadListener;

import java.util.List;

/**
 * @filename:
 * @function： 类型下所有应用adapter
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/16 12:04
 */
public class TypeAppAllAdapter extends BaseRecyclerViewAdapter<AppInfo> {
    private String mRanking;

    public TypeAppAllAdapter(Context context) {
        this(context, "");

    }

    public TypeAppAllAdapter(Context context, String ranking) {
        this(context, null, ranking);

    }

    public TypeAppAllAdapter(Context context, List list, String ranking) {
        super(context);
        mRanking = ranking;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_type_app_all_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, lists.get(i));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final AppInfo info = lists.get(i);
        holder.appInfo = info;
        if (Constants.RANKING.equals(mRanking)) {
            if (i == 0 || i == 1 || i == 2) {
                holder.rankingText.setTextColor(mContext.getResources().getColor(R.color.color_F51500));
            } else {
                holder.rankingText.setTextColor(mContext.getResources().getColor(R.color.color_1A1A1A));
            }
            holder.rankingText.setText(String.valueOf(++i));
            holder.rankingText.setVisibility(View.VISIBLE);
            holder.downloadNum.setText(String.valueOf(info.getInstall_num()));
            holder.appSize.setText(String.format("%.1f", Float.parseFloat(info.getApp_size()) / 1024 / 1024) + "M");
            holder.down_layout.setVisibility(View.VISIBLE);
        }

        Glide.with(mContext).load(info.getIcon_url()).into((holder.appImg));

        holder.appName.setText(info.getApplabel());

        holder.appType.setText(ClassifyManager.getClassify(info.getClassify()));

        holder.introduce.setText(info.getOneword());

        holder.itemView.setOnClickListener(view -> {

            AppDetailedActivity.actionStart(mContext, info);

        });

        switch (holder.appInfo.getTerminal()) {
            case 0:

                if (AppInfoManager.getInstance().isContains(info)) {
                    holder.install.setState(DownloadProgressButton.STATE_OPEN);
                    holder.install.setCurrentText(mContext.getString(R.string.open_str));

                } else {
                    holder.install.setState(DownloadProgressButton.STATE_NORMAL);
                    holder.install.setCurrentText(mContext.getString(R.string.load_str));
                }
                break;

            case 1:
            case 2:
            case 6:
                holder.install.setState(DownloadProgressButton.STATE_OPEN);
                holder.install.setCurrentText(mContext.getString(R.string.open_str));
                break;

        }
        int type = 10000;

        AppInfo applyApp = DataInit.getAppinfo(info.getApppackage());

        holder.install.setVisibility(View.VISIBLE);
        holder.itemView.setLayerType(View.LAYER_TYPE_NONE,null);
        if(applyApp != null) {


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
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.install.setState(DownloadProgressButton.STATE_Unused);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);//灰度效果
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                holder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
                holder.install.setVisibility(View.GONE);


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
            AppEventUtil.onClick(info, new DownloadListener(info, holder.install, ""));
        });
    }

    public void setState(int mState, boolean isUpdate) {

        updateItem(getItemCount() - 1);
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rankingText;
        private ImageView appImg;
        private TextView appName;
        private TextView appType;
        private TextView introduce;
        private DownloadProgressButton install;
        private LinearLayout down_layout;
        private TextView downloadNum;
        private TextView appSize;
        private ItemTypeAppAllLayoutBinding binding; // 替换为实际生成的 Binding 类（如 ItemRankingBinding）
        AppInfo appInfo;



        // 构造方法中接收数据并绑定视图
        public ViewHolder(@NonNull View itemView, AppInfo appInfo) {
            super(itemView);
            this.appInfo = appInfo;
            binding = ItemTypeAppAllLayoutBinding.bind(itemView);
            rankingText = binding.rankingTv;
            appImg = binding.appIcon;
            appName = binding.tvAppName;
            appType = binding.tvTypeName;
            introduce = binding.tvIntroduce;
            install = binding.tvInstall;
            down_layout = binding.downLayout;
            downloadNum = binding.tvDownloadNum;
            appSize = binding.tvAppSize;
        }


    }


}
