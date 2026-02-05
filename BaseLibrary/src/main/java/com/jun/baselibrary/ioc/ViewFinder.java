package com.jun.baselibrary.ioc;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/24 1:18
 * Version 1.0
 * Description：注入辅助类 findViewById 获取View对象
 */ 
class ViewFinder {
    private View mView;
    private Fragment mFragment;
    private Activity mActivity;
    public ViewFinder(View view) {
        mView = view;
    }

    public ViewFinder(Activity activity) {
        mActivity = activity;
    }

    public View findViewById(int viewId){
        return mActivity != null ? mActivity.findViewById(viewId) : mView.findViewById(viewId);
    }

    public Context getContext(){
        return mActivity != null ? mActivity.getBaseContext() : mView.getContext();
    }
}
