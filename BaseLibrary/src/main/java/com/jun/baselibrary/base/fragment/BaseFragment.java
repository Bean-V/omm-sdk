package com.jun.baselibrary.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jun.baselibrary.ioc.ViewUtils;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/25 22:23
 * Version 1.0
 * Description：Fragment基类
 */
public abstract class BaseFragment extends Fragment {
    //上下文
    private Context mContext;
    //UI视图
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(),null);
        //加入注解
        ViewUtils.inject(this, mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化
        initView();
        initData();
    }

    //获取布局id
    protected abstract int getLayoutId();
    //初始化View
    protected abstract void initView();
    //初始化数据
    protected abstract void initData();

    /**
     * 判断后台返回的数据是否成功
     */
//    protected  boolean isNetRequestOk(BaseResult result){
//        return result.message.equals("success");
//    }
}
