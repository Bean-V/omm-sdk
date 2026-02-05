package com.oortcloud.clouddisk.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.dialog.LoadingDialog;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zzj
 * @date: 2020/1/7 14:51
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    protected VB binding; // ViewBinding 对象
    protected Context mContext;
    protected LoadingDialog mLoadDialog;

    protected boolean isRequest = false;
    protected boolean isInitUi = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mLoadDialog = new LoadingDialog(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用泛型创建 ViewBinding 实例
        binding = getViewBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (!isInitUi) {
            isInitUi = true;
            initView();
            initData();
            initEvent();
        }
    }

    /**
     * 通过泛型创建 ViewBinding 实例（由子类实现）
     */
    protected abstract VB getViewBinding(LayoutInflater inflater, ViewGroup container);

    protected abstract void initBundle(Bundle bundle);
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initEvent();

    @Override
    public void onStop() {
        super.onStop();
        isRequest = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 释放 ViewBinding 引用
    }
}