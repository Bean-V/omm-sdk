package com.oortcloud.clouddisk.adapter.base.drag;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * @FileName: DragItemTouchHelper.java
 * @Author: ZZJun / @CreateDate: 2021/1/10 1:47
 * @Version: 1.0
 * @Function: 实现左边侧滑删除 和 拖动排序
 */
public class DragItemTouchHelper extends ItemTouchHelper {

    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    public DragItemTouchHelper(@NonNull Callback callback ,RecyclerView recyclerView ) {
        super(callback);
        this.attachToRecyclerView(recyclerView);
    }

    public DragItemTouchHelper(RecyclerView recyclerView ,RecyclerView.Adapter adapter  , List data ) {
        this(new Callback(adapter , data) ,recyclerView);
    }

    private final static class Callback extends ItemTouchHelper.Callback {
        private   RecyclerView.Adapter  mAdapter;
        private List mItems;
        private Callback( RecyclerView.Adapter adapter , List data){
            this.mAdapter = adapter;
            this.mItems = data;
        }
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            int swipeFlags = ItemTouchHelper.LEFT;


            // 拖动
            int dragFlags = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                // GridView 样式四个方向都可以
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.LEFT |
                        ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
            } else {
                // ListView 样式不支持左右
                dragFlags = ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN;
            }

            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 拖动的时候不断的回调方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // 获取原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            // 得到目标的位置
            int targetPosition = target.getAdapterPosition();
            if (fromPosition > targetPosition) {
                for (int i = fromPosition; i < targetPosition; i++) {
                    Collections.swap(mItems, i, i + 1);// 改变实际的数据集
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    Collections.swap(mItems, i, i - 1);// 改变实际的数据集
                }
            }
            mAdapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        /**
         * 侧滑删除后会回调的方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // 获取当前删除的位置
            int position = viewHolder.getAdapterPosition();
            mItems.remove(position);
            // adapter 更新notify当前位置删除
            mAdapter.notifyItemRemoved(position);
        }

        /**
         * 拖动选择状态改变回调
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                // ItemTouchHelper.ACTION_STATE_IDLE 看看源码解释就能理解了
                // 侧滑或者拖动的时候背景设置为灰色
                viewHolder.itemView.setBackgroundColor(Color.GRAY);
            }
        }


        /**
         * 回到正常状态的时候回调
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 正常默认状态下背景恢复默认
            viewHolder.itemView.setBackgroundColor(0);
//            ViewCompat.setTranslationX(viewHolder.itemView,0);
            ViewCompat.setTranslationZ(viewHolder.itemView,0);
        }
    };
}
