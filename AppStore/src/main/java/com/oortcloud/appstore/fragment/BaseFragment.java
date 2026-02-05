package com.oortcloud.appstore.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.oortcloud.appstore.dailog.LoadingDialog;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2020/1/7 14:51
 */
public abstract class BaseFragment extends Fragment {

    protected View mRoot;

    protected Context mContext;

    protected boolean isRequest = false;
    protected boolean isInitUi = false;

    protected LoadingDialog mLoadDialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mLoadDialog = new LoadingDialog(mContext);
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle(getArguments());
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

       if (mRoot == null){

           mRoot = getRootView() == null ? inflater.inflate(getLayoutId() , null) : getRootView();
           //unbinder = ButterKnife.bind(this , mRoot);

       }
        return mRoot;
    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        if (!isInitUi) {
            isInitUi = true;
            initView();
            initData();
            initEvent();
        }
    }
    protected abstract View getRootView();
    protected abstract int getLayoutId();
    protected abstract void initBundle(Bundle bundle);
    protected abstract void initView();
    protected abstract void initEvent();
    protected abstract void initData();

    @Override
    public void onStop() {
        super.onStop();
        isRequest = true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
