package com.oortcloud.contacts.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.oortcloud.contacts.R;

/**
 * @ProjectName: omm-master
 * @FileName: LetterIndexView.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/11 15:50
 * @Version: 1.0
 */
public class LetterIndexView extends View {
    private int itemWidth;//每个字母所占宽度
    private int itemHeight;//每个字母所占高度
    private Paint mPaint;//画笔
    private int selectIndex = -1;//选中索引
    private String[] letters = new String[]{ "#" ,"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int letterColor;//字母颜色
    private int selectLetterColor;//选中字母颜色
    private int selectBgColor;//选中背景颜色

    private OnLetterChangedListener mLetterChangedListener;// 触摸字母改变事件

    public LetterIndexView(Context context) {
        this(context, null);
    }

    public LetterIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //初始化文字颜色属性，画笔
    public LetterIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterIndexView, 0, 0);
        letterColor = typedArray.getColor(R.styleable.LetterIndexView_letterColor, Color.parseColor("#666666"));
        selectLetterColor = typedArray.getColor(R.styleable.LetterIndexView_selectLetterColor,  Color.parseColor("#FFFFFF"));
        selectBgColor = typedArray.getColor(R.styleable.LetterIndexView_selectBackgroundColor, 0);
        typedArray.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(30);
        mPaint.setAntiAlias(true);
    }

    //获取item宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemWidth = getMeasuredWidth();
        itemHeight = getMeasuredHeight() / letters.length;
    }

    //绘制字母列表
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //字母坐标从文字左下角开始
        for (int i = 0; i < letters.length; i++) {
            if (i == selectIndex) {
                mPaint.setColor(selectLetterColor);
            } else {
                mPaint.setColor(letterColor);
            }
            //获取x坐标 （行宽-字母宽度）除以 2
            float x = (itemWidth - mPaint.measureText(letters[i])) / 2;
            Rect rect = new Rect();
            mPaint.getTextBounds(letters[i], 0, letters[i].length(), rect);
            int textHeight = rect.height();
            //获取y坐标 （行高+字母高度）除以 2 + 行高*i
            float y = (itemHeight + textHeight) / 2 + itemHeight * i;
            canvas.drawText(letters[i], x, y, mPaint);
        }
        if (selectIndex == -1) {
            setBackgroundColor(0);
        } else {
            setBackgroundColor(selectBgColor);
        }
    }

    /**
     * 当手指触摸按下的时候改变字母背景颜色
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float downY = event.getY();
                int index = (int) (downY / itemHeight);//获取按下点的索引
                if (index != selectIndex)
                    selectIndex = index;
                //防止数组越界
                if (mLetterChangedListener != null && selectIndex > -1 && selectIndex < letters.length)
                    mLetterChangedListener.onChanged(letters[selectIndex], selectIndex);
                break;
            case MotionEvent.ACTION_UP:
                selectIndex = -1;
                break;
        }
        invalidate();
        return true;
    }
    //触摸选中字母回调接口
    public interface OnLetterChangedListener {
        void onChanged(String s, int position);
    }

    public void setOnLetterChangedListener(OnLetterChangedListener letterChangedListener) {
        mLetterChangedListener = letterChangedListener;
    }

}
