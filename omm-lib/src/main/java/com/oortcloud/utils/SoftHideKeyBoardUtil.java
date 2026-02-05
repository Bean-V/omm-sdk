package com.oortcloud.utils;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.Group;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/31 16:14
 * @version： v1.0
 * @function： 解决键盘档住输入框
 */
public class SoftHideKeyBoardUtil {
    public static void assistActivity(Activity activity) {
        new SoftHideKeyBoardUtil(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams; //为适应华为小米等手机键盘上方出现黑条或不适配
    private int contentHeight;//获取setContentView本来view的高度
    private boolean isfirst = true;//只用获取一次
    private int statusBarHeight;//状态栏高度

    private SoftHideKeyBoardUtil(Activity activity) {
        //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content); //2､获取到setContentView放进去的View

        mChildOfContent =  getScrollView((ViewGroup) content.getChildAt(0));
                //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
                        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { //4､软键盘弹起会使GlobalLayout发生变化
            public void onGlobalLayout() {
                if (isfirst) {
                    contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                    isfirst = false;
                }  //5､当前布局发生变化时，对Activity的xml布局进行重绘
                possiblyResizeChildOfContent();
            }
        }); //6､获取到Activity的xml布局的放置参数
        frameLayoutParams = mChildOfContent.getLayoutParams();
    } // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度

    private void possiblyResizeChildOfContent() { //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
        int usableHeightNow = computeUsableHeight(); //2､如果当前可用高度和原始值不一样
        if (usableHeightNow != usableHeightPrevious) {  //3､获取Activity中xml中布局在当前界面显示的高度
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();  //4､Activity中xml布局的高度-当前可用高度
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;  //5､高度差大于屏幕1/4时，说明键盘弹出
            if (heightDifference > (usableHeightSansKeyboard / 4)) {  // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference - 200 ;//statusBarHeight
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                }
            } else {
                frameLayoutParams.height = contentHeight;
            }  //7､ 重绘Activity的xml布局
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r); // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        return (r.bottom - r.top);
    }

    private View  getScrollView(ViewGroup content){
        if (content != null){
            for (int i = 0 ; i < content.getChildCount(); i++){
                View view =  content.getChildAt(i);
              if (view instanceof ScrollView){
                  return view;

                }else  if (view instanceof ViewGroup){
                  view  =  getScrollView((ViewGroup) view);
                  if (view != null && view instanceof ScrollView){
                      return view;
                  }
              }
            }
        }
        return null;
    }
}