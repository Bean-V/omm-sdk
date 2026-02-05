package com.oortcloud.contacts.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/26
 * Version 1.0
 * Description：
 */
public class ItemTabLayout  extends TabLayout {
    private boolean isClick;
    public ItemTabLayout(Context context) {
        this(context, null);
    }

    public ItemTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {

    }

    @Override
    public boolean performClick() {
//        if (isClick){
//            return true;
//        }
        return super.performClick();
    }

    public void isClick(boolean isClick) {
       this.isClick = isClick;
    }

}
