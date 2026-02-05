package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;

import java.util.List;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/20 02:00
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo> data;
    private String mType;

    private OnItemListener onItemListener;

    public GridViewAdapter( Context context, List<AppInfo> objects ,String type) {
        mContext = context;
        data = objects;
        mType = type;
        onItemListener = (OnItemListener) mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_drag_grad_layout ,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final AppInfo appInfo = getItem(position);

        Glide.with(mContext).load(appInfo.getIcon_url()).into((viewHolder.appIcon));
        viewHolder.appName.setText(appInfo.getApplabel());

        if (mType.equals("add")){
            viewHolder.imgAdd.setVisibility(View.VISIBLE);
            viewHolder.imgAdd.setImageResource(R.mipmap.icon_add);
        }else if (mType.equals("delete")){
            viewHolder.imgAdd.setVisibility(View.VISIBLE);
        }


        viewHolder.converView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType.equals("add")){
                   onItemListener.onAddItemListener(appInfo);
                }else if (mType.equals("delete")){
                  onItemListener.onDeleteItemListener(appInfo);
                }

            }
        });

        return convertView;
    }


    class ViewHolder{
        protected View  converView;
        protected ImageView appIcon;
        protected ImageView imgAdd;
        protected TextView appName;
        public ViewHolder(View convertView) {
            this.converView = convertView;
            appIcon = convertView.findViewById(R.id.app_icon);
            imgAdd = convertView.findViewById(R.id.img_remove);
            appName = convertView.findViewById(R.id.tv_app_name);
        }
    }

    public interface OnItemListener{
        void onAddItemListener(AppInfo appInfo);
        void onDeleteItemListener(AppInfo appInfo);
    }
}
