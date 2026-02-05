package com.jun.baselibrary.base.dialog;

import android.view.View;
import android.widget.TextView;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/2 19:01
 * Version 1.0
 * Description：自定义获取文本 DialogOnClickListener
 */
public abstract class DialogOnClickListener implements View.OnClickListener {
    //获取文本控件Id
    private int mViewId;
    //文本
    private CharSequence mText;
    //Dialog帮助类
    private DialogViewHelper mHelper;

    /**
     *
     * @param viewId 获取文本控件Id
     */
    public DialogOnClickListener(int viewId){
        mViewId = viewId;
    }
    //绑定帮助类
    public void setHelper(DialogViewHelper helper) {
        this.mHelper = helper;
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mHelper != null){
            View view = mHelper.getView(mViewId);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                onClick(v, textView.getText().toString().trim());
                return;
            }
        }
        onClick(v, "");
    }

    /**
     * 自定义onClick 可以携带控件文本
     * @param v
     * @param text
     */
    public abstract void onClick(View v, CharSequence text);
}
