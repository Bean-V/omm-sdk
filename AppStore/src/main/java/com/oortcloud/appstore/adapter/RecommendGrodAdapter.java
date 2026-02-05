package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.LayoutInflater;
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
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.db.DataInit;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：推荐页Adapter
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/4 13:16
 */
public class RecommendGrodAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<AppInfo> data = new ArrayList<>();

    public RecommendGrodAdapter( Context context) {
        mContext = context;

    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
            View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info_style_layout ,null);
            viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfo appInfo = data.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        Glide.with(mContext).load(appInfo.getIcon_url()).into((viewHolder.appIcon));
        viewHolder.appName.setText(appInfo.getApplabel());
        viewHolder.appType.setText(ClassifyManager.getClassify(appInfo.getClassify()));

        viewHolder.converView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDetailedActivity.actionStart(mContext , appInfo);
            }
        });



        viewHolder.converView.setLayerType(View.LAYER_TYPE_NONE,null);
        AppInfo applyApp = DataInit.getAppinfo(appInfo.getApppackage());
        if(applyApp != null) {
            if (applyApp.getApply_status() == 0) {
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);//灰度效果
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                viewHolder.converView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);

                viewHolder.appApplyStatu.setVisibility(View.GONE);
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder {
        protected View  converView;
        protected ImageView appIcon;
        protected TextView appName;
        protected TextView appType;
        protected TextView appApplyStatu;
        public ViewHolder(View convertView) {
            super(convertView);
            this.converView = convertView;
            appIcon = convertView.findViewById(R.id.app_icon);
            appName = convertView.findViewById(R.id.tv_app_name);
            appType = convertView.findViewById(R.id.tv_app_type);
            appApplyStatu = convertView.findViewById(R.id.tv_apply);
        }
    }

    public void setData(List<AppInfo> data1) {
        data.clear();;
        data.addAll(data1);
        notifyDataSetChanged();
    }
}
