package com.oort.weichat.fragment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.ChildEntity;
import com.oort.weichat.fragment.entity.GroupEntity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.views.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.oortcloud.basemodule.views.groupedadapter.holder.BaseViewHolder;

import java.util.ArrayList;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    protected ArrayList<GroupEntity> mGroups;

    public GroupedListAdapter(Context context, ArrayList<GroupEntity> groups) {
        super(context);
        mGroups = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ChildEntity> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    public void clear(){
        mGroups.clear();
        notifyDataChanged();
    }

    public void setGroups(ArrayList<GroupEntity> groups){
        mGroups = groups;
        notifyDataChanged();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_fragment_icon_name_grid;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_header, entity.getHeader());
//        holder.get(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = mGroups.get(groupPosition);
       // holder.setText(R.id.tv_footer, entity.getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        ChildEntity entity = mGroups.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tv_name, entity.getChild());
        ImageLoader.loadImage(holder.get(R.id.img_icon),entity.getUrl(), com.oortcloud.basemodule.R.mipmap.icon_collect_im_01);

        AppInfo appInfo = (AppInfo) entity.getObj();

        TextView tv_apply = holder.get(R.id.tv_apply);


        holder.itemView.setLayerType(View.LAYER_TYPE_NONE,null);
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
                holder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);

                //tv_apply.setVisibility(View.GONE);
                tv_apply.setVisibility(View.VISIBLE);
                tv_apply.setText(com.oortcloud.appstore.R.string.unused_str);
                tv_apply.setTextColor(Color.parseColor("#00BA4A"));
            } else if (applyApp.getApply_status() == 2) {
                tv_apply.setVisibility(View.VISIBLE);
                tv_apply.setText(com.oortcloud.appstore.R.string.apply_str);
                tv_apply.setTextColor(Color.parseColor("#00BA4A"));
                tv_apply.setBackground(mContext.getResources().getDrawable(com.oortcloud.appstore.R.drawable.background_apply));
            } else if (applyApp.getApply_status() == 3) {
                tv_apply.setVisibility(View.VISIBLE);
                tv_apply.setText(com.oortcloud.appstore.R.string.review_str);
                tv_apply.setTextColor(Color.parseColor("#E36000"));
                tv_apply.setBackground(mContext.getResources().getDrawable(com.oortcloud.appstore.R.drawable.background_apply_review));
            } else if (applyApp.getApply_status() == 4) {
                tv_apply.setVisibility(View.VISIBLE);
                tv_apply.setText(com.oortcloud.appstore.R.string.reject_str);
                tv_apply.setTextColor(Color.parseColor("#E30000"));
                tv_apply.setBackground(mContext.getResources().getDrawable(com.oortcloud.appstore.R.drawable.background_apply_reject));
            } else {
                tv_apply.setVisibility(View.GONE);
            }
        }
    }
}
