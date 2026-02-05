package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.DynamicBean;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.view.SquareCenterImageView;

import java.util.List;

public class DynamicImageGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<DynamicBean.AttachBean> mDatas;

    public DynamicImageGridViewAdapter(Context context, List<DynamicBean.AttachBean> data) {
        mContext = context;
        mDatas = data;
    }

    @Override
    public int getCount() {
        if (mDatas.size() >= 9) {
            return 9;
        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_mu_normal, parent, false);
            holder.imageView = (SquareCenterImageView) convertView.findViewById(R.id.muc_normal);
            holder.icon_paly =  convertView.findViewById(R.id.icon_paly);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DynamicBean.AttachBean att = mDatas.get(position);
        String url = att.getUrl();
        holder.icon_paly.setVisibility(View.GONE);
        if(att.getType().equals("video")){
            holder.icon_paly.setVisibility(View.VISIBLE);
            url = att.getThumb();
        }

        if (mDatas.get(position).getUrl().endsWith(".gif")) {
            ImageLoadHelper.showGifWithPlaceHolder(
                    mContext,
                    mDatas.get(position).getUrl(),
                    R.drawable.default_gray,
                    R.drawable.image_download_fail_icon,
                    holder.imageView
            );
        } else {
            ImageLoadHelper.showImageWithPlaceHolder(
                    mContext,
                    mDatas.get(position).getUrl(),
                    R.drawable.default_gray,
                    R.drawable.image_download_fail_icon,
                    holder.imageView
            );
            //150, 150,
        }

        return convertView;
    }

    static class ViewHolder {
        SquareCenterImageView imageView;
        ImageView icon_paly;
    }
}