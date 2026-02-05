package com.oortcloud.clouddisk.adapter.base.wrap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @FileName: WrapRecyclerView.java
 * @Author: ZZJun / @CreateDate: 2021/1/8 2:58
 * @Version: 1.0
 * @Function: 可以添加头部和底部的RecyclerView
 */
public class WrapRecyclerView extends RecyclerView {
    //包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private Adapter mAdapter;
    // 增加一些通用功能
    // 空列表数据应该显示的空View
    // 正在加载数据页面，也就是正在获取后台接口页面
    private View mEmptyView, mLoadingView;
    public WrapRecyclerView(@NonNull Context context) {
        this(context , null);
    }

    public WrapRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public WrapRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        // 为了防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }
        this.mAdapter = adapter;
        if (adapter instanceof WrapRecyclerAdapter){
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        }else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(adapter);
        }
        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);

        super.setAdapter(mWrapRecyclerAdapter);
    }
    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();

            dataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart);
            dataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemMoved没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
            dataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart);
            dataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart, payload);
            dataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemInserted没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemInserted(positionStart);
            dataChanged();
        }
    };
    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view){
        if (mWrapRecyclerAdapter != null){
            mWrapRecyclerAdapter.addHeaderView(view);
        }

    }
    /**
     * 移除头部
     * @param view
     */
    public void removeHeaderView(View view){
        if (mWrapRecyclerAdapter != null){
            mWrapRecyclerAdapter.removeHeaderView(view);
        }


    }
    /**
     * 添加尾部
     * @param view
     */
    public void addFooterView(View view){
        if (mWrapRecyclerAdapter != null){
            mWrapRecyclerAdapter.addFooterView(view);
        }

    }
    /**
     * 移除头部
     * @param view
     */
    public void removeFooterView(View view){
        if (mWrapRecyclerAdapter != null){
            mWrapRecyclerAdapter.removeFooterView(view);
        }

    }
    /**
     * 添加一个空列表数据页面
     */
    public void addEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    /**
     * 添加一个正在加载数据的页面
     */
    public void addLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    /**
     * Adapter数据改变的方法
     */
    private void dataChanged() {
        if (mAdapter.getItemCount() == 0) {
            // 没有数据
            if (mEmptyView != null) {
                mEmptyView.setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(GONE);
            }
        }
    }
}
