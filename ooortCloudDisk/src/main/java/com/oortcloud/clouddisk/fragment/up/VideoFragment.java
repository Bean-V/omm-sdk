package com.oortcloud.clouddisk.fragment.up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.adapter.up.VideoAdapter;
import com.oortcloud.clouddisk.databinding.FragmentVideoBinding;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.utils.manager.FileManager;



/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 15:57
 * @version： v1.0
 * @function：
 */
public class VideoFragment  extends BaseFragment {

    RecyclerView mRecycleView;

    private VideoAdapter mVideoAdapter;

    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentVideoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initView() {
        FragmentVideoBinding binding = FragmentVideoBinding.inflate(getLayoutInflater());
        mRecycleView = binding.videoRv;
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mVideoAdapter = new VideoAdapter(mContext ,null);
        mRecycleView.setAdapter(mVideoAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        mVideoAdapter.setData(FileManager.getInstance(mContext).getVideos());
    }
}
