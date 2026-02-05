package com.oortcloud.clouddisk.adapter.base.wrap;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @FileName: WrapRecyclerAdapter.java
 * @Author: ZZJun / @CreateDate: 2021/1/8 1:18
 * @Version: 1.0
 * @Function: 添加头部和底部的Adapter
 */
public class WrapRecyclerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter mAdapter;
    //用来存放底部和头部View的集合
    private SparseArray<View>  mHeaderViews , mFooterViews;
    // 基本的头部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_HEADER = 10000000;
    // 基本的底部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_FOOTER = 20000000;
    public WrapRecyclerAdapter(RecyclerView.Adapter adapter){
        this.mAdapter = adapter;
        mHeaderViews = new SparseArray<>();
        mFooterViews = new SparseArray<>();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)){
            View headerView = mHeaderViews.get(viewType);
            return createHeaderFooterViewHolder(headerView);
        }

        if (isFooterViewType(viewType)){
            View footerView =  mFooterViews.get(viewType);
            return createHeaderFooterViewHolder(footerView);
        }

        return  mAdapter.onCreateViewHolder(parent , viewType);
    }
    /**
     * 是否头部类型
     */
    private boolean isHeaderViewType(int viewType){
        int position = mHeaderViews.indexOfKey(viewType);
        return position >= 0;
    }
    /**
     * 是否底部类型
     */
    private boolean isFooterViewType(int viewType){
        int position = mFooterViews.indexOfKey(viewType);
        return position >= 0;
    }
    /**
     * 创建头部或者底部的ViewHolder
     */
    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position < mHeaderViews.size() || position >= mAdapter.getItemCount() + mFooterViews.size() )
            return;
        mAdapter.onBindViewHolder(holder , position - mHeaderViews.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)){
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)){
            return mFooterViews.keyAt(position);
        }
        return mAdapter.getItemViewType(position - mHeaderViews.size());
    }
    /**
     * 是否尾部
     * @param position
     * @return
     */
    private boolean isFooterPosition(int position) {
        return position >= (mHeaderViews.size() + mAdapter.getItemCount());
    }

    /**
     * 是否头部
     * @param position
     * @return
     */
    private boolean isHeaderPosition(int position) {
        return position < mHeaderViews.size();
    }

    @Override
    public int getItemCount() {
        //头部条数 + 底部条数 + Adapter的条数
        return  mHeaderViews.size() + mFooterViews.size() + mAdapter.getItemCount() ;
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view){
        int position  = mHeaderViews.indexOfValue(view);
        if (position < 0 ){
            mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
        }
        notifyDataSetChanged();
    }
    /**
     * 移除头部
     * @param view
     */
    public void removeHeaderView(View view){
        int index  = mHeaderViews.indexOfValue(view);
        if (index  < 0) return;
        mHeaderViews.remove(index);
        notifyDataSetChanged();

    }
    /**
     * 添加尾部
     * @param view
     */
    public void addFooterView(View view){
        int position  = mFooterViews.indexOfValue(view);
        if (position < 0 ){
            mHeaderViews.put(BASE_ITEM_TYPE_FOOTER++, view);
        }
        notifyDataSetChanged();
    }
    /**
     * 移除头部
     * @param view
     */
    public void removeFooterView(View view){
        int index  = mFooterViews.indexOfValue(view);
        if (index  < 0) return;
        mFooterViews.remove(index);
        notifyDataSetChanged();
    }
}
