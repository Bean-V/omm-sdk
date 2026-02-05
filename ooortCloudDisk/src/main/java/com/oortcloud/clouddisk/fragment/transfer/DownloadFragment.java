package com.oortcloud.clouddisk.fragment.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.DownTransferAdapter;
import com.oortcloud.clouddisk.databinding.FragmentDownloadBinding;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.fragment.BaseFragment;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;

import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/11 11:44
 * @version： v1.0
 * @function： 下载列表  处理 正在下载/下载完成  删除下载完成 信息等功能
 */
public class DownloadFragment  extends BaseFragment {

    RecyclerView mRecycleView;
    LinearLayout mImgNull;

    private DownTransferAdapter mDownTransfersAdapter;



    @Override
    protected ViewBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDownloadBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initView() {

        mRecycleView = binding.getRoot().findViewById(R.id.recycler_view);
        mImgNull = binding.getRoot().findViewById(R.id.img_null_ll);
        mDownTransfersAdapter = new DownTransferAdapter(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycleView.setAdapter(mDownTransfersAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initData() {
        List<DownLoadInfo> downLoadInfoData =  DBManager.getInstance().queryDown(null , null);
        if (downLoadInfoData  != null && downLoadInfoData.size() > 0){
            mDownTransfersAdapter.setData( downLoadInfoData);
            mRecycleView.setVisibility(View.VISIBLE);
            mImgNull.setVisibility(View.GONE);
        }else {
            mRecycleView.setVisibility(View.GONE);
            mImgNull.setVisibility(View.VISIBLE);
        }


    }
}
