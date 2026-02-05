package com.oortcloud.contacts.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/3 16:34
 * @version： v1.0
 * @function： 禁止ViewPager 左右滑动切换页面
 */
public class SlitherViewPager extends ViewPager {

    private boolean mIsSlither;
    public SlitherViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public SlitherViewPager(Context context) {
        this(context ,null);
    }
    /**
     * 1.dispatchTouchEvent一般情况不做处理
     *,如果修改了默认的返回值,子孩子都无法收到事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);   // return true;不行
    }
    /**
     * 是否拦截
     * 拦截:会走到自己的onTouchEvent方法里面来
     * 不拦截:事件传递给子孩子
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // return false;//可行,不拦截事件,
        // return true;//不行,孩子无法处理事件
        //return super.onInterceptTouchEvent(ev);//不行,会有细微移动
        if (mIsSlither){
            return false;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
//        return mIsSlither;
    }
    /**
     * 是否消费事件
     * 消费:事件就结束
     * 不消费:往父控件传
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //return false;// 可行,不消费,传给父控件
        //return true;// 可行,消费,拦截事件
        //super.onTouchEvent(ev); //不行,
        //虽然onInterceptTouchEvent中拦截了,
        //但是如果viewpage里面子控件不是viewgroup,还是会调用这个方法.
        if (mIsSlither){
            return true;// 可行,消费,拦截事件
        }else {

            return super.onTouchEvent(ev);
        }

    }
    public void setSlither(boolean slither) {
        mIsSlither  = slither;
    }
}
