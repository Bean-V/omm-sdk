package com.jun.baselibrary.view.recycleview.adapter;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：包装添加头部或底部Adapter
 */
public class WrapperRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //数据列表Adapter,不包含头部和底部
    private RecyclerView.Adapter mAdapter;

    //头部和底部集合 ArrayList 如果多个头部或底部怎么区分，需要标识，用Map集合
    private final SparseArray<View> mHeaderViews , mFooterViews;

    //头部起始key 区分头部
    private int BASE_HEADER_KEY = 0x11;
    private int BASE_FOOTER_KEY = 0x22;

    public WrapperRecycleAdapter(RecyclerView.Adapter  adapter){
        mAdapter = adapter;
        mHeaderViews = new SparseArray<>();
        mFooterViews = new SparseArray<>();
    }
    //添加头部
    public void addHeaderView(View view){
        //去除重复添加
        if (mHeaderViews.indexOfValue(view) == -1){
            mHeaderViews.put(BASE_HEADER_KEY++ , view);
            notifyDataSetChanged();
        }

    }
    //添加底部
    public void addFooterView(View view){
        //去除重复添加
        if (mFooterViews.indexOfValue(view) == -1){
            mFooterViews.put(BASE_FOOTER_KEY++ , view);
            notifyDataSetChanged();
        }
    }
    //移除头部
    public void removeHeaderView(View view){

        if (mHeaderViews.indexOfValue(view) >= 0){
            mHeaderViews.removeAt(mHeaderViews.indexOfValue(view));
            notifyDataSetChanged();
        }
    }
    //移除底部
    public void removeFooterView(View view){

        if (mFooterViews.indexOfValue(view)  >= 0){
            mFooterViews.removeAt(mFooterViews.indexOfValue(view));
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 根据当前位置给 viewType
        //头部
        int numHeaders = mHeaderViews.size();
        if (position < numHeaders) {
            return mHeaderViews.keyAt(position);
        }
        // Adapter
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }
        return mFooterViews.keyAt(adjPosition - adapterCount);
     
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //头部 列表、底部 viewType
        if (mHeaderViews.indexOfKey(viewType) >= 0){
            //头部
            return createHeaderFooterViewHolder(mHeaderViews.get(viewType));

        }else if (mFooterViews.indexOfKey(viewType) >=0){

            //底部
            return createHeaderFooterViewHolder(mFooterViews.get(viewType));
        }
        //列表
        return mAdapter.onCreateViewHolder(parent , viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int numHeaders = mHeaderViews.size();
        if (position < numHeaders ) {
            //头部 不需要绑定数据
            return ;
        }

        // Adapter
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                 mAdapter.onBindViewHolder(holder , adjPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        return  mHeaderViews.size() + mAdapter.getItemCount() + mFooterViews.size();
    }
    /**
     * 获取头部count
     * @return
     */
    public int getHeaderViewSize(){
        return mHeaderViews.size();
    }

    /**
     * 获取底部count
     * @return
     */
    public int getFooterViewSize(){
        return mHeaderViews.size();
    }

    /**
     * 创建头部和底部ViewHolder
     * @param view
     * @return
     */
    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {};
    }


}
