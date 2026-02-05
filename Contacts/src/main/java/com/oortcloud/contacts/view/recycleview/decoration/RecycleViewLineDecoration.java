package com.oortcloud.contacts.view.recycleview.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：添加RecycleView 底部分隔线
 */
public class RecycleViewLineDecoration extends RecyclerView.ItemDecoration {
    //绘制分割线值
    private int top;
    private int bottom;
    private int left;
    private int right;
    //画笔
    private Paint mPaint;

    public RecycleViewLineDecoration(int size , int colorValue){
        top = size;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setColor(colorValue);
    }

    /**
     * 预留分隔线位置
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        /**
         * 如果对四个面都做边框处理 ，复杂比较复杂
        outRect.top = top;
        outRect.bottom = bottom;
        outRect.left = left;
        outRect.right = right;
         */
        //获取当前子View位置
        int position =  parent.getChildAdapterPosition(view);
        /**
         * 底部添加分隔线
         * 因为parent.getChildCount()是随着子View加载变化，无法保证最后一条分隔线不被添加
         *

        if (position != parent.getChildCount()-1){
            outRect.bottom = bottom;
        }
        //通过获取RecycleView .getAdapter().getItemCount() 的总数保证最后一条不被添加
        if (position != parent.getAdapter().getItemCount()-1){
            outRect.bottom = bottom;
        }
         *
         */

        /**
         * 顶部添加分隔线
         * 因 第一条是0，作非0判断，保证第一条顶部不被添加分割线
         */
        if (position != 0){
            outRect.top = top;
        }
    }

    /**
     *  绘制
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        /**
         * 利用Canvas绘制，在每个item头部绘制
         *
         */
        int childCount = parent.getChildCount();

        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 1 ; i  < childCount ; i++){
            //因绘制的是矩形 itemView头部是 Rect的底部
            rect.bottom = parent.getChildAt(i).getTop();
            rect.top = rect.bottom - top;
            c.drawRect(rect , mPaint);
        }


    }
}
