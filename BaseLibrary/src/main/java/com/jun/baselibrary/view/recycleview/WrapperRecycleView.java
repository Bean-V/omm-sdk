package com.jun.baselibrary.view.recycleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.jun.baselibrary.view.recycleview.adapter.WrapperRecycleAdapter;
import com.jun.baselibrary.view.recycleview.helper.SlitherItemTouchHelperCallback;
import com.jun.baselibrary.view.recycleview.helper.SlitherItemTouchListener;


/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：封装RecycleView 添加头部底部功能
 */
public class WrapperRecycleView extends SlideRecyclerView {
    //添加头部底部Adapter
    private WrapperRecycleAdapter mAdapter;
    //拖拽、侧滑、处理
    private SlitherItemTouchHelperCallback mCallback;
    //观察者对象
    private final AdapterDataObserver mAdapterObserver = new AdapterDataObserver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChanged() {
            mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeChanged(positionStart + mAdapter.getHeaderViewSize(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mAdapter.notifyItemRangeChanged(positionStart + mAdapter.getHeaderViewSize(), itemCount, payload);

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeInserted(positionStart + mAdapter.getHeaderViewSize(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeRemoved(positionStart + mAdapter.getHeaderViewSize(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mAdapter.notifyItemMoved(fromPosition + mAdapter.getHeaderViewSize(),toPosition + mAdapter.getHeaderViewSize());
        }
    };

    public WrapperRecycleView(@NonNull Context context) {
        this(context, null);
    }

    public WrapperRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapperRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (adapter instanceof WrapperRecycleAdapter) {
            mAdapter = (WrapperRecycleAdapter) adapter;
        } else {
            mAdapter = new WrapperRecycleAdapter(adapter);

             //删除问题，因为列表Adapter是包裹的，当Adapter删除数据时，WrapperRecycleAdapter并未知道，所以要进行关联
             //注册
            assert adapter != null;
            adapter.registerAdapterDataObserver(mAdapterObserver);
        }
        //
        super.setAdapter(mAdapter);
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if (layout instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
            gridLayoutManager.setSpanSizeLookup(
                    new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                          //GridLayoutManager 中头部底部都是占一行
                            return position < mAdapter.getHeaderViewSize() ||
                                    position >= mAdapter.getItemCount()-mAdapter.getFooterViewSize()
                                    ? gridLayoutManager.getSpanCount() : 1;
                        }
                    }
            );
        }
        super.setLayoutManager(layout);
    }

    //添加头部
    public void addHeaderView(View view) {
        if (mAdapter != null) {
            mAdapter.addHeaderView(view);
        }

    }

    //添加底部
    public void addFooterView(View view) {
        if (mAdapter != null) {
            mAdapter.addFooterView(view);
        }
    }

    //移除头部
    public void removeHeaderView(View view) {
        if (mAdapter != null) {
            mAdapter.removeHeaderView(view);
        }
    }

    //移除底部
    public void removeFooterView(View view) {
        if (mAdapter != null) {
            mAdapter.removeFooterView(view);
        }
    }

    /**
     * 绑定拖拽、侧滑事件
     */
    public void attachTouchEvent(SlitherItemTouchListener listener) {
        if (mCallback == null){
            mCallback = new SlitherItemTouchHelperCallback(listener);
        }else {
            mCallback.setTouchEventListener(listener);
        }
        ItemTouchHelper helper = new ItemTouchHelper(mCallback);
        helper.attachToRecyclerView(this);
    }
    /**
     * @param dragFlags  设置拖拽方向
     *                   GridLayoutManager 样式四个方向都可以
     *                   dragFlags = ItemTouchHelper.UP | ItemTouchHelper.LEFT |ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
     *                   <p>
     *                   ListView 样式不支持左右，只支持上下
     *                   dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
     * @param swipeFlags 设置侧滑方向
     *                   ItemTouchHelper.UP
     *                   ItemTouchHelper.LEFT
     *                   ItemTouchHelper.DOWN
     *                   ItemTouchHelper.RIGHT;
     */
    public void setDragSwipeFlags(int dragFlags, int swipeFlags) {
        if (mCallback == null){
            mCallback = new SlitherItemTouchHelperCallback();
        }
        mCallback.setDragSwipeFlags(dragFlags, swipeFlags);
    }
}
