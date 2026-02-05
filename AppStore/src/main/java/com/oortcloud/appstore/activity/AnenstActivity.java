package com.oortcloud.appstore.activity;

import android.os.Bundle;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.oortcloud.appstore.R;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/29 16:23
 */
public class AnenstActivity extends BaseActivity {
    @Override
    protected ViewBinding getViewBinding() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_anenst_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mTitle.setText(getString(R.string.about_str));
        mBtnItem.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {

    }
}
