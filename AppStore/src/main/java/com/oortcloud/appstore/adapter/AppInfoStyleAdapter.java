package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.oortcloud.appstore.databinding.ItemAppInfoStyleLayoutBinding;
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.db.DataInit;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/17 10:34
 */
public class AppInfoStyleAdapter extends BaseRecyclerViewAdapter<AppInfo> {

    public AppInfoStyleAdapter(Context context) {
        super(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_app_info_style_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder viewHolder1, int i) {
      ViewHolder holder = (ViewHolder) viewHolder1;
      final AppInfo info = lists.get(i);
      Glide.with(mContext).load(info.getIcon_url()).into((holder.appIcon));
      holder.appName.setText(info.getApplabel());


      holder.appType.setText(ClassifyManager.getClassify(info.getClassify()));



      holder.itemView.setOnClickListener(view ->  {

              AppDetailedActivity.actionStart(mContext , info);
      });
        ViewHolder viewHolder =  holder;
        viewHolder.itemView.setLayerType(View.LAYER_TYPE_NONE,null);
        AppInfo applyApp = DataInit.getAppinfo(info.getApppackage());
        if(applyApp != null) {
            if (applyApp.getApply_status() == 0) {
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);//灰度效果
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                viewHolder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);

                ((ViewHolder) viewHolder).appApplyStatu.setVisibility(View.GONE);
            } else if (applyApp.getApply_status() == 2) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.apply_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#00BA4A"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply));
            } else if (applyApp.getApply_status() == 3) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.review_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#E36000"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply_review));
            } else if (applyApp.getApply_status() == 4) {
                viewHolder.appApplyStatu.setVisibility(View.VISIBLE);
                viewHolder.appApplyStatu.setText(R.string.reject_str);
                viewHolder.appApplyStatu.setTextColor(Color.parseColor("#E30000"));
                viewHolder.appApplyStatu.setBackground(mContext.getResources().getDrawable(R.drawable.background_apply_reject));
            } else {
                viewHolder.appApplyStatu.setVisibility(View.GONE);
            }
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAppInfoStyleLayoutBinding binding; // 替换为实际生成的 Binding 类（如 ItemAppBinding）

        ImageView appIcon;
        TextView appName;
        TextView appType;
        TextView appApplyStatu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化 ViewBinding（假设布局文件为 item_app.xml）
            binding = ItemAppInfoStyleLayoutBinding.bind(itemView);

            // 通过 ViewBinding 赋值给成员变量
            appIcon = binding.appIcon;
            appName = binding.tvAppName;
            appType = binding.tvAppType;
            appApplyStatu = binding.tvApply;
        }


    }

}
