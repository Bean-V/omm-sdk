package com.oort.weichat.view.viewpage;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 1.禁止左右滑动
 * 2.点击立即切换，无切换过渡动画
 */
public class SuperViewPager extends ViewPager {

    private ViewPageHelper helper;

    public SuperViewPager(Context context) {
        this(context, null);
    }

    public SuperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper = new ViewPageHelper(this);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        MScroller scroller = helper.getScroller();
        scroller.setNoDuration(true);
        super.setCurrentItem(item, smoothScroll);
        scroller.setNoDuration(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 禁止左右滑动
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 可行,消费,拦截事件
        return true;
    }
}