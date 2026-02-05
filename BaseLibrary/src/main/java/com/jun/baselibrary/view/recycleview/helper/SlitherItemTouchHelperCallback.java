package com.jun.baselibrary.view.recycleview.helper;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.jun.baselibrary.R;
import com.jun.baselibrary.view.recycleview.adapter.WrapperRecycleAdapter;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：RecycleView侧滑 / 拖拽事件处理类
 */
public class SlitherItemTouchHelperCallback extends ItemTouchHelper.Callback {
    //侧滑、拖拽事件回调
    private SlitherItemTouchListener mListener;
    //拖拽
    private int mDragFlags = 0;
    //侧滑
    private int mSwipeFlags = 0;

    private RecyclerView mRecyclerView;

    public SlitherItemTouchHelperCallback() {

    }

    public SlitherItemTouchHelperCallback(SlitherItemTouchListener listener) {
        mListener = listener;
    }

    public void setTouchEventListener(SlitherItemTouchListener listener) {
        mListener = listener;
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
        mDragFlags = dragFlags;
        mSwipeFlags = swipeFlags;
    }

    /**
     * 侧滑或拖拽响应开始回调的方法
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        /**
         * 获取触摸响应的方向：1.拖动dragFlags 2.侧滑删除swipeFlags
         * 向左侧滑：ItemTouchHelper.LEFT
         * 向左侧滑：ItemTouchHelper.RIGHT
         */
        int position = viewHolder.getAdapterPosition();
        //处理头部和底部，不需侧滑和拖拽
        if (checkHeaderFooter(recyclerView, position)) {
            return makeMovementFlags(0, 0);
        }
        if (mRecyclerView != recyclerView) {
            mRecyclerView = recyclerView;
        }
        return makeMovementFlags(mDragFlags, mSwipeFlags);
    }

    /**
     * 状态发生改变回调
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {

        // 拖拽状态  侧滑状态  正常状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            // ItemTouchHelper.ACTION_STATE_IDLE 不能在空闲状态设置
            // 侧滑或者拖动的时候背景设置为灰色
            viewHolder.itemView.setBackgroundColor(Color.GRAY);

        }
    }

    /**
     * 回到正常状态的时候回调
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 正常默认状态下背景恢复默认
        viewHolder.itemView.setBackgroundColor(R.color.colorPrimaryDark);

        //
        viewHolder.itemView.setTranslationX(0);
        viewHolder.itemView.setTranslationY(0);


    }

    /**
     * 拖动的时候不断的回调方法
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        if(viewHolder.getItemViewType()!=target.getItemViewType()){
//            return false;
//        }

        // 获取原来的位置
        int fromPosition = viewHolder.getAdapterPosition();//2

        // 得到目标的位置
        int targetPosition = target.getAdapterPosition();//3
        //目标位置是不是头部或者底部
        if (checkHeaderFooter(recyclerView, targetPosition)) {
            return false;
        }
//        if (fromPosition < targetPosition) {
//            for (int i = fromPosition; i < targetPosition; i++) {
//                Collections.swap(mItems, i, i + 1);// 改变实际的数据集
//            }
//        } else {
//            for (int i = fromPosition; i > targetPosition; i--) {
//
//                Collections.swap(mItems, i, i - 1);// 改变实际的数据集
//            }
//        }
        //回调处理数据源
        if (mListener != null) {
            WrapperRecycleAdapter adapter = getAdapter(recyclerView);
            if (adapter != null) {
                int  headerCount = adapter.getHeaderViewSize();
                mListener.onMove(fromPosition - headerCount, targetPosition - headerCount);
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition, targetPosition);
        //当连个子布局不同时，拖拽失败，需要返回false
        return false;
    }

    /**
     * 侧滑或拖拽后会回调的方法
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.v("TAG", "direction-----------" + direction);
                WrapperRecycleAdapter adapter = getAdapter(mRecyclerView);
        if (adapter != null) {
            // 获取当前删除的位置
            int position = viewHolder.getAdapterPosition();
            if (mListener != null) {
                mListener.onSwiped(position - adapter.getHeaderViewSize());
            }

            Log.v("TAG", "position-----------" + (position - adapter.getHeaderViewSize()));
            //删除数据源
//                mItems.remove(position);
            // adapter 更新notify当前位置删除
//                mAdapter.notifyDataSetChanged();
            //带有默认动画效果
            adapter.notifyItemChanged(position);
        }



    }

    /**
     * 检测是否头部或者底部
     *
     * @param recyclerView
     * @param position
     * @return
     */
    private boolean checkHeaderFooter(RecyclerView recyclerView, int position) {
        WrapperRecycleAdapter adapter = getAdapter(recyclerView);
        if (adapter != null) {
            if (position < adapter.getHeaderViewSize()
                    || position >= adapter.getItemCount() - adapter.getFooterViewSize()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 WrapperRecycleAdapter
     *
     * @param recyclerView
     * @return
     */
    private WrapperRecycleAdapter getAdapter(RecyclerView recyclerView) {
        WrapperRecycleAdapter adapter = null;
        if (recyclerView.getAdapter() instanceof WrapperRecycleAdapter) {
            adapter = (WrapperRecycleAdapter) recyclerView.getAdapter();
        }
        return adapter;
    }
}
