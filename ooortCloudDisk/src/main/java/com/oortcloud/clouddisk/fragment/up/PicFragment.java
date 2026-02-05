package com.oortcloud.clouddisk.fragment.up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.adapter.up.PicAdapter;
import com.oortcloud.clouddisk.databinding.FragmentVideoBinding;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.utils.manager.FileManager;

import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/27 15:50
 * @version： v1.0
 * @function： 处理图片fragment
 */
public class PicFragment extends BaseFragment {
    RecyclerView mPicRV;

    private PicAdapter mPicAdapter;

    private List<String> mData;

    private String mPath;
    private FileUploadActivity mFileUploadActivity;
    @Override
    protected FragmentVideoBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {


        return FragmentVideoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mPath =  bundle.getString("PATH");
        if (mContext instanceof FileUploadActivity) {
            mFileUploadActivity = (FileUploadActivity) mContext;
        }
    }

    @Override
    protected void initView() {

        FragmentVideoBinding binding = FragmentVideoBinding.inflate(getLayoutInflater());
        mPicRV = binding.videoRv;
        mPicRV.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mPicAdapter = new PicAdapter(mContext, null);
        mPicRV.setAdapter(mPicAdapter);
    }

    @Override
    protected void initData() {
        new Thread(() ->{
            mData = FileManager.getInstance(mContext).getImgListByDir(mPath);

            mFileUploadActivity.runOnUiThread(()->{
                mPicAdapter.setData(mData);

            });

        }).start();
    }

    @Override
    protected void initEvent() {

    }

    public static Fragment instantiate(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("PATH", path);
        Fragment fragment = new PicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
