package com.jun.baselibrary.view.recycleview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/12
 * Version 1.0
 * Description：添加RecycleView 底部分隔线 图片 资源
 */
public class RecycleViewLinearDecoration extends RecyclerView.ItemDecoration{
    //绘制分割线值
    private int top;
    private int bottom;
    private int left;
    private int right;
    //画笔
    private Paint mPaint;

    //使用系统属性设置更通用
    private Drawable mDivider;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecycleViewLinearDecoration(Context context , int drawableResId){
        mDivider =  context.getDrawable(drawableResId);
    }

    /**
     * 使用颜色值
     * @param size
     * @param colorValue
     */
    public RecycleViewLinearDecoration(int size , int colorValue){
        top = size;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setColor(colorValue);
    }

    /**
     * 预留分隔线位置
     *
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

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
            if (mDivider != null){
                outRect.top = mDivider.getIntrinsicHeight();
            }else {
                outRect.top = top;
            }
        }
    }

    /**
     *  绘制
     * @param canvas
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDivider != null){
            //drawable绘制分割线
            drawableDraw(canvas, parent);
        }

        if (mPaint != null){
            //使用画笔绘制
            paintDraw(canvas, parent);
        }
    }

    /**
     * drawable绘制分割线
     * @param canvas
     * @param parent
     */
    private void  drawableDraw(Canvas canvas, @NonNull RecyclerView parent){
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
            rect.top = rect.bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(rect);
            mDivider.draw(canvas);
        }

    }

    private void paintDraw(Canvas canvas, @NonNull RecyclerView parent){
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
            canvas.drawRect(rect , mPaint);
        }

    }
}
