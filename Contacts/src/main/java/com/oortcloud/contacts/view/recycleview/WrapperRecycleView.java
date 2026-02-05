package com.oortcloud.contacts.view.recycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.contacts.view.recycleview.adapter.WrapperRecycleAdapter;
import com.oortcloud.contacts.view.recycleview.helper.SlitherItemTouchHelperCallback;
import com.oortcloud.contacts.view.recycleview.helper.SlitherItemTouchListener;


/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：
 */
public class WrapperRecycleView extends RecyclerView {
    private Context mContext;
    private WrapperRecycleAdapter mAdapter;
    //拖动触摸的工具类
    private ItemTouchHelper itemTouchHelper;
    //观察者对象
    private AdapterDataObserver mAdapterObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeChanged(positionStart , itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mAdapter.notifyItemRangeChanged(positionStart , itemCount , payload);

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
           mAdapter.notifyItemRangeInserted(positionStart ,itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeRemoved(positionStart ,itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };
    public WrapperRecycleView(@NonNull Context context) {
        this(context ,null);
    }

    public WrapperRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public WrapperRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
       if (adapter instanceof WrapperRecycleAdapter){
           mAdapter = (WrapperRecycleAdapter) adapter;
       }else {
           mAdapter = new WrapperRecycleAdapter(adapter);
       }
        /**
         * 删除问题，因为列表Adapter是包裹的，当Adapter删除数据时，WrapperRecycleAdapter并未知道，所以要进行关联
         * 注册
         */
        adapter.registerAdapterDataObserver(mAdapterObserver);

        super.setAdapter(mAdapter);
    }

    //添加头部
    public void addHeaderView(View view){
        if (mAdapter != null){
            mAdapter.addHeaderView(view);
        }

    }
    //添加底部
    public void addFooterView(View view){
        if (mAdapter != null){
            mAdapter.addFooterView(view);
        }
    }
    //移除头部
    public void removeHeaderView(View view){
        if (mAdapter != null){
            mAdapter.removeHeaderView(view);
        }
    }
    //移除底部
    public void removeFooterView(View view){
        if (mAdapter != null){
            mAdapter.removeFooterView(view);
        }
    }

    /**
     * 拖拽-侧滑
     * @param callback
     */
    public void setItemDragSlither(ItemTouchHelper.Callback callback , boolean isSlither){
        if (itemTouchHelper == null){
            itemTouchHelper = new ItemTouchHelper(callback);
        }
        if (!isSlither){
            //拖拽-侧滑 帮助类
            itemTouchHelper.attachToRecyclerView(this);
        }else {
            itemTouchHelper.attachToRecyclerView(null);
        }

    }
}
