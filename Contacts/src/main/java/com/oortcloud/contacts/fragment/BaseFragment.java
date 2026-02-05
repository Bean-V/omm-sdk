package com.oortcloud.contacts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import com.oortcloud.contacts.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;



/**
 * @filename:
 * @author: zzj/@date: 2022/4/11 11:48
 * @version： v1.0
 * @function： BaseFragment
 */
public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected Context mContext;

    protected boolean isRequest = false;
    protected boolean isInitUi = false;

    protected EventBus mEventBus;

    protected LoadingDialog dialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle(getArguments());
        mEventBus = EventBus.getDefault();
//        dialog = new LoadingDialog(mContext);
//        dialog.show();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mRootView == null){
            mRootView = getRootView() == null ? inflater.inflate(getLayoutId() , null) : getRootView();
        }
        return mRootView;
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

    @Override
    public void onStart() {
        if (mEventBus != null && !mEventBus.isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        super.onStart();
    }

    protected abstract View getRootView();
    protected abstract int getLayoutId();
    protected abstract void initBundle(Bundle bundle);
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initEvent();


    @Override
    public void onStop() {
        isRequest = true;
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {


        if(mEventBus.isRegistered(this)){
            mEventBus.unregister(this);

        }
        super.onDestroy();
    }

    public void notifyChanged(boolean changed){

    }
}
