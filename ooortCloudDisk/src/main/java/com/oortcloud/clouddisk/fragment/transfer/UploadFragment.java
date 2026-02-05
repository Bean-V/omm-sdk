package com.oortcloud.clouddisk.fragment.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.adapter.TransfersAdapter;
import com.oortcloud.clouddisk.databinding.FragmentUploadBinding;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;

import java.util.Iterator;
import java.util.List;



/**
 * @filename:
 * @author: zzj/@date: 2021/1/11 11:45
 * @version： v1.0
 * @function： 上传列表  处理 正在上传/上传完成 删除上传完成信息等功能
 */
public class UploadFragment  extends BaseFragment {
    RecyclerView mRecycleView;
    LinearLayout mImgNull;
    private TransfersAdapter mTransfersAdapter;




    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {

        return  FragmentUploadBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {
    }

    @Override
    protected void initView() {
        FragmentUploadBinding ubinding = (FragmentUploadBinding)binding;

       mRecycleView = ubinding.recyclerView;
       mImgNull = ubinding.imgNullLl;
        mTransfersAdapter = new TransfersAdapter(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleView.setAdapter(mTransfersAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initData() {
       List<UploadInfo> uploadInfoData =  DBManager.getInstance().queryUp("" , "");

        if (uploadInfoData  != null ){
           Iterator<UploadInfo> iterator =  uploadInfoData.iterator();
            while (iterator.hasNext()){
                if (iterator.next().isHide()){
                    iterator.remove();
                }
            }
            if (uploadInfoData.size() > 0) {

                mTransfersAdapter.setData(uploadInfoData);
                mRecycleView.setVisibility(View.VISIBLE);
                mImgNull.setVisibility(View.GONE);
                return;
            }

        }
        mRecycleView.setVisibility(View.GONE);
        mImgNull.setVisibility(View.VISIBLE);

    }
}
