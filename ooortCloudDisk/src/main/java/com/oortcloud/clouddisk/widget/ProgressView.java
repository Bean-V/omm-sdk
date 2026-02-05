package com.oortcloud.clouddisk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.oortcloud.clouddisk.R;

/**
 * Email 240336124@qq.com
 * Created by Darren on 2017/5/20.
 * Version 1.0
 * Description: 实现 上传下载进度展示 及开始暂停处理
 */
public class ProgressView extends View {

    private int mOuterColor = Color.parseColor("#C6DBEE");
    private int mInnerColor = Color.parseColor("#FF1156A6");
    private int mBorderWidth = 20;// 20px
    private int mStepTextSize;
    private int mStepTextColor;

    private Paint mOutPaint, mInnerPaint, mTextPaint, mPaint;

    // 总共的，当前的步数
    private int mStepMax = 100;
    private int mCurrentStep = 0;
    private boolean mStatus = true;
    private Path mPath;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 1.分析效果；
        // 2.确定自定义属性，编写attrs.xml
        // 3.在布局中使用
        // 4.在自定义View中获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        mOuterColor = array.getColor(R.styleable.ProgressView_outerColor, mOuterColor);
        mInnerColor = array.getColor(R.styleable.ProgressView_innerColor, mInnerColor);
        mBorderWidth = (int) array.getDimension(R.styleable.ProgressView_borderWidth, mBorderWidth);
        mStepTextSize = array.getDimensionPixelSize(R.styleable.ProgressView_stepTextSize, mStepTextSize);
        mStepTextColor = array.getColor(R.styleable.ProgressView_stepTextColor, mStepTextColor);
        array.recycle();

        mOutPaint = new Paint();
        mOutPaint.setAntiAlias(true);
        mOutPaint.setStrokeWidth(mBorderWidth);
        mOutPaint.setColor(mOuterColor);
        mOutPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutPaint.setStyle(Paint.Style.STROKE);// 画笔空心

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(mBorderWidth);
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStyle(Paint.Style.STROKE);// 画笔空心


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTextSize(mStepTextSize);
        // 5.onMeasure()
        // 6.画外圆弧 ，内圆弧 ，文字
        // 7.其他
        mPaint = new Paint();
        mPaint.setColor(mInnerColor);
        mPaint.setAntiAlias(true);
    }


    // 5.onMeasure()
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 调用者在布局文件中可能  wrap_content
        // 获取模式 AT_MOST  40DP

        // 宽度高度不一致 取最小值，确保是个正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width > height ? height : width, width > height ? height : width);
    }

    // 6.画外圆弧 ，内圆弧 ，文字
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 6.1 画外圆弧    分析：圆弧闭合了  思考：边缘没显示完整  描边有宽度 mBorderWidth  圆弧

        // int center = getWidth()/2;
        // int radius = getWidth()/2 - mBorderWidth/2;
        // RectF rectF = new RectF(center-radius,center-radius
        // ,center+radius,center+radius);

        RectF rectF = new RectF(mBorderWidth / 2, mBorderWidth / 2
                , getWidth() - mBorderWidth / 2, getHeight() - mBorderWidth / 2);
        canvas.drawArc(rectF, -90, 360, false, mOutPaint);

        if (mStepMax == 0) return;
        // 6.2 画内圆弧  怎么画肯定不能写死  百分比  是使用者设置的从外面传
        float sweepAngle = (float) mCurrentStep / mStepMax;
        canvas.drawArc(rectF, -90, sweepAngle * 360, false, mInnerPaint);
        if (mStatus) {
            int marginWidth = getWidth() /3;
            int marginHeight = getHeight() / 3;
            canvas.drawRect(marginWidth, marginHeight, getWidth() - marginWidth, getHeight() - marginHeight, mPaint);

        } else {
            if (mPath == null) {
                int marginWidth = getWidth() / 4 + 10;
                int marginHeight = getHeight() / 3 -5;
                // 画路径
                mPath = new Path();
                mPath.moveTo(marginWidth, marginHeight);
                mPath.lineTo(marginWidth, getHeight() - marginHeight);
                mPath.lineTo(getWidth() - marginWidth + 8, getHeight() / 2);
                // path.lineTo(getWidth()/2,0);
                mPath.close();// 把路径闭合
            }
            canvas.drawPath(mPath, mPaint);
        }

    }


    public synchronized void setStepMax(int stepMax) {
        this.mStepMax = stepMax;
    }

    public synchronized void setCurrentStep(int currentStep) {
        this.mCurrentStep = currentStep;
        // 不断绘制  onDraw()
        invalidate();
    }

    public synchronized void setStatus(boolean status) {
        this.mStatus = status;
        invalidate();
    }
}
