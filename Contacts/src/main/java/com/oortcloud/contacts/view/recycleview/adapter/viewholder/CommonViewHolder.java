package com.oortcloud.contacts.view.recycleview.adapter.viewholder;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：Adapter通用ViewHolder
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {
    //用于缓存已找到的View
    private SparseArray<View> mViews;
    public CommonViewHolder(@NonNull View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    /**
     * 从itemView中获取子View
     * @param viewId 子ViewID
     * @param <T> 返回T类型
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        //使用缓存方式减少findViewById()次数
        if (view == null){
            view = itemView.findViewById(viewId);
            mViews.put(viewId , view);
        }
        return (T) view;
    }

    // 通用功能封装 设置文本、图片、点击事件
    public CommonViewHolder setText(int viewId , CharSequence text){
        TextView tv = getView(viewId);
        tv.setText(text);
        //实现链式调用
        return this;
    }

    /**
     * 设置本地图片资源
     * @param viewId
     * @param resId
     * @return
     */
    public CommonViewHolder setImageResource(int viewId , int resId){
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }
    /**
     * 设置网络图片资源
     * @param viewId
     * @param imageLoader
     * @return
     * 为了通用兼容第三方图片加载库，定义规则
     *
     */
    public CommonViewHolder setImagePath( int viewId , HolderImageLoader imageLoader){
        ImageView imageView = getView(viewId);
        imageLoader.loadImage(imageView , imageLoader.getPath());
        return this;
    }

    public CommonViewHolder setItemClickListener(int viewId , View.OnClickListener listener){
        getView(viewId).setOnClickListener(listener);
        //实现链式调用
        return this;
    }

    /**
     * 图片加载规则
     */
    public abstract static class  HolderImageLoader{

        private String mPath;
        public HolderImageLoader(String path){
            mPath = path;
        }

        /**
         * 子类覆写该方法，自定义时使用第三方图片加载库需求
         * @param imageView
         * @param path
         */
        protected abstract void loadImage(ImageView imageView , String path);

        public String getPath() {
            return mPath;
        }
    }
}
