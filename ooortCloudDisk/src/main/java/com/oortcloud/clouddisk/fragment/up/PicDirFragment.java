package com.oortcloud.clouddisk.fragment.up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.adapter.up.PicDirAdapter;
import com.oortcloud.clouddisk.databinding.FragmentVideoBinding;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.utils.manager.FileManager;

import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 15:58
 * @version： v1.0
 * @function： 图片文件夹信息
 */
public class PicDirFragment extends BaseFragment {

    RecyclerView mRecycleView;
    private PicDirAdapter mPicDirAdapter;
    private FileUploadActivity mFileUploadActivity;
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentVideoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (mContext instanceof FileUploadActivity) {
            mFileUploadActivity = (FileUploadActivity) mContext;
        }
    }

    @Override
    protected void initView() {
        FragmentVideoBinding binding = FragmentVideoBinding.inflate(getLayoutInflater());
        mRecycleView = binding.videoRv;
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mPicDirAdapter = new PicDirAdapter(mContext ,null);
        mRecycleView.setAdapter(mPicDirAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        new Thread(()->{

           List  data = FileManager.getInstance(mContext).getImageFolders();
            mFileUploadActivity.runOnUiThread(()->{
                mPicDirAdapter.setData(data);
            });

        }).start();

    }
}
