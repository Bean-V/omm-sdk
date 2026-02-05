package com.oortcloud.clouddisk.fragment.up;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.adapter.up.DirAdapter;
import com.oortcloud.clouddisk.adapter.up.FileDirAdapter;
import com.oortcloud.clouddisk.databinding.FragmentVideoBinding;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.utils.helper.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 16:00
 * @version： v1.0
 * @function： sd文件目录
 */
public class OtherFragment extends BaseFragment {
    RecyclerView mFileRV;
    RecyclerView mDirRV;
    private List<File> mData = new ArrayList<>();
    private FileDirAdapter mFileDirAdapter;
    private DirAdapter mDirAdapter;
    private FileUploadActivity mFileUploadActivity;
    //存储上级目录 返回处理
    public LinkedList<File> mPath =  new LinkedList<>();


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
        mFileRV= binding.videoRv;
        mDirRV = binding.dirRv;
        //增加sd卡目录
        mPath.add(Environment.getExternalStorageDirectory() );

        mFileRV.setLayoutManager(new LinearLayoutManager(mContext));
        mFileDirAdapter = new FileDirAdapter(mContext , this);
        mFileRV.setAdapter(mFileDirAdapter);

        mDirRV.setLayoutManager(new LinearLayoutManager(mContext ,  LinearLayoutManager.HORIZONTAL  ,false));
        mDirAdapter = new DirAdapter(mContext , this);
        mDirRV.setAdapter(mDirAdapter);

        mDirRV.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initData() {
        if (mPath.size() > 0){
            new Thread(() ->{
                mData.clear();
                FileHelper.scanDir(mData , mPath.getLast().getPath());
                mFileUploadActivity.runOnUiThread(()->{
                    mDirAdapter.setData(mPath);
                    mFileDirAdapter.setData(mData);

                });

            }).start();
        }
    }

    public void finish() {
        if (mPath.size() > 1){
            mPath.removeLast();
            initData();
        }else {

        }
    }
}
