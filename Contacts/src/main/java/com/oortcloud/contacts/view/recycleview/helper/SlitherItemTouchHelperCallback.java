package com.oortcloud.contacts.view.recycleview.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.contacts.R;

import java.util.Collections;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：RecycleView侧滑回调类
 */
public class SlitherItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private Context mContext;
    private RecyclerView.Adapter mAdapter;
    private SlitherItemTouchListener mCallback;

    public SlitherItemTouchHelperCallback(Context context, RecyclerView.Adapter adapter){
        this.mContext = context;
        this.mAdapter = adapter;
    }
    /**
     * 侧滑或拖拽响应开始回调的方法
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
        int swipeFlags = ItemTouchHelper.LEFT;

        // 拖动
        int dragFlags = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            // GridView 样式四个方向都可以
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.LEFT |
                    ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
        } else {
            // ListView 样式不支持左右，只支持上下
            dragFlags = ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }
    /**
     * 回到正常状态的时候回调
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 正常默认状态下背景恢复默认
        viewHolder.itemView.setBackgroundColor(0);
        ViewCompat.setTranslationX(viewHolder.itemView,0);
        if (mCallback != null){
            mCallback.stop();
        }


    }
    /**
     * 拖动的时候不断的回调方法
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // 获取原来的位置
        int fromPosition = viewHolder.getAdapterPosition();
        // 得到目标的位置
        int targetPosition = target.getAdapterPosition();
        if (mCallback != null){
            mCallback.move(fromPosition , targetPosition);
        }
        mAdapter.notifyItemMoved(fromPosition, targetPosition);
        return true;
    }

    /**
     * 状态发生改变回调
     * @param viewHolder
     * @param actionState
     */
    @SuppressLint({"MissingPermission", "ResourceAsColor"})
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        // 拖拽状态  侧滑状态  正常状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200); //震动一下
            // ItemTouchHelper.ACTION_STATE_IDLE 不能在空闲状态设置
            // 侧滑或者拖动的时候背景设置为灰色
            viewHolder.itemView.setBackgroundColor(R.color.color_999);

        }
    }

    /**
     * 侧滑后会回调的方法
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 获取当前删除的位置
        int position = viewHolder.getAdapterPosition();
//        //删除数据源
//        mItems.remove(position);
//        // adapter 更新notify当前位置删除
//        mAdapter.notifyDataSetChanged();
//        //带有默认动画效果
        if (mCallback != null){
            mCallback.onSwiped(position);
        }

    }
    public void setItemSlitherListener(SlitherItemTouchListener listener){
        this.mCallback = listener;
    }
}
