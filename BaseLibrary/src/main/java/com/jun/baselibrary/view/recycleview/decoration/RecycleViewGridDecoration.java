package com.jun.baselibrary.view.recycleview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：添加RecycleView 网格分隔线 图片 资源
 */
public class RecycleViewGridDecoration extends RecyclerView.ItemDecoration {
    //使用系统属性设置更通用
    private Drawable mDivider;

    public RecycleViewGridDecoration(Context context, int drawableResId) {
        mDivider = context.getDrawable(drawableResId);
    }

    /**
     * 预留分隔线位置
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {


        /**
         * 预留分隔线位置，因预留是下边和右边
         * 网格分割线会导致每行最右边和最下边也会有分割线
         * 需要处理不预留
         *
         */

        int right = mDivider.getIntrinsicWidth();
        int bottom = mDivider.getIntrinsicHeight();

        //计算最后一列  当前位置/ 列数 == 0
        if (isLastColumn(view , parent)){
            right = 0;

        }
        //计算最后一行
        if (isLastRow(view , parent)){
            bottom = 0;

        }
        outRect.right = right;
        outRect.bottom = bottom;

    }

    /**
     * 计算最后一列
     * @param view
     * @param parent
     * @return
     */
    private boolean isLastColumn(View view , RecyclerView parent) {
        //获取当前位置
       int currentPosition =  ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        //获取列数
        int spanCount = getSpanCount(parent);
//       int itemCount = parent.getAdapter().getItemCount();
//       return currentPosition == itemCount -1 ? true: (currentPosition + 1) % spanCount == 0;
        return  (currentPosition + 1) % spanCount == 0;

    }

    /**
     * 计算最后一行
     * @param view
     * @param parent
     * @return
     */
    private boolean isLastRow(View view , RecyclerView parent) {
        //获取列数
        int spanCount = getSpanCount(parent);
        //获取item总数
        int itemCount = parent.getAdapter().getItemCount();
        //得到行数
        int row = itemCount / spanCount;
        //是否最后一行
        int rowNumber = itemCount % spanCount == 0 ? row : row + 1;
        //获取当前位置
        int currentPosition =  ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        //当前列 > 列数 * （行数 -1）
        return (currentPosition + 1) > spanCount * (rowNumber -1);
    }

    /**
     * 获取RecycleView 的列数
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager =  parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.getSpanCount();

        }
        return 1;
    }

    /**
     * 绘制
     * @param canvas
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        /**
         * 利用Canvas绘制，在每个item头部绘制
         *
         */
        onDrawHorizontal(canvas, parent);
        onDrawVertical(canvas, parent);

    }

    /**
     * 绘制 水平方式分隔线
     * @param canvas
     * @param parent
     */
    public void onDrawHorizontal(@NonNull Canvas canvas, @NonNull RecyclerView parent) {
        /**
         * 利用Canvas绘制，在每个item底部绘制
         *
         */
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int left = childView.getLeft();
            int right = childView.getRight() + mDivider.getIntrinsicWidth();
            int top = childView.getBottom() ;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }

    }

    /**
     * 绘制 垂直方式分隔线
     *
     * @param canvas
     * @param parent
     */
    public void onDrawVertical(@NonNull Canvas canvas, @NonNull RecyclerView parent) {
        /**
         * 利用Canvas绘制，在tem右边绘制
         *
         */
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            //最后一列不画
            if (i == childCount -1){
                return;
            }
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int left = childView.getRight();
            int right = left + mDivider.getIntrinsicWidth();
            int top = childView.getTop() ;
            int bottom = childView.getBottom()  + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }

    }

}
