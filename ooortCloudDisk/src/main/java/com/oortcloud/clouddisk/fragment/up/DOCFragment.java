package com.oortcloud.clouddisk.fragment.up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.up.DOCAdapter;
import com.oortcloud.clouddisk.databinding.FragmentVideoBinding;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.utils.manager.FileManager;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 15:58
 * @version： v1.0
 * @function： 处理上传文档
 */
public class DOCFragment extends BaseFragment {
    RecyclerView mRecycleView;

    private DOCAdapter mDOCAdapter;



    @Override
    protected FragmentVideoBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentVideoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initView() {
        mRecycleView = binding.getRoot().findViewById(R.id.video_rv);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mDOCAdapter = new DOCAdapter(mContext ,null);
        mRecycleView.setAdapter(mDOCAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {

        mDOCAdapter.setData(FileManager.getInstance(mContext).getFilesByType(FileManager.TYPE_DOC));
    }
}
