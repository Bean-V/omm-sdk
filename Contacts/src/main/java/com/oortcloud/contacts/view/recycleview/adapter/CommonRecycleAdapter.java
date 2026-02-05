package com.oortcloud.contacts.view.recycleview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oortcloud.contacts.view.recycleview.adapter.listener.ItemClickListener;
import com.oortcloud.contacts.view.recycleview.adapter.listener.ItemLongClickListener;
import com.oortcloud.contacts.view.recycleview.adapter.viewholder.CommonViewHolder;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：RecycleView通用Adapter
 */
public abstract class CommonRecycleAdapter<DATA> extends RecyclerView.Adapter<CommonViewHolder> {
    protected Context mContext;
    //item不同，参数传递
    private int mLayoutId;
    //参数通用泛型
    protected List<DATA> mData;
    //实例化View的LayoutInflater
    private LayoutInflater mInflater;
    //点击、长按事件回调对象
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;

    private MultipleTypeSupport mTypeSupport;

    public CommonRecycleAdapter(Context context , List<DATA> data , int layoutId){
        mContext = context;
        mData = data;
        mLayoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 支持多布局
     * @param context
     * @param data
     * @param typeSupport
     */
    public CommonRecycleAdapter(Context context , List<DATA> data , MultipleTypeSupport typeSupport ){
        this(context , data , -1);
        this.mTypeSupport = typeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTypeSupport != null){
            return mTypeSupport.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if (mTypeSupport != null){
           mLayoutId = viewType;
       }
        View itemView =  mInflater.inflate(mLayoutId , parent , false);
        return new CommonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        convert(holder , mData.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void  refreshData(List<DATA> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * 把参数传递让子类实现
     * @param holder ViewHolder
     * @param itemData 当前位置item数据
     * @param position 当前位置
     */
    protected abstract void convert(CommonViewHolder holder, DATA itemData, int position);
}
