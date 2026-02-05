package com.oortcloud.clouddisk.activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.TableViewPagerAdapter;
import com.oortcloud.clouddisk.databinding.ActivityTransfersBinding;
import com.oortcloud.clouddisk.fragment.transfer.DownloadFragment;
import com.oortcloud.clouddisk.fragment.transfer.UploadFragment;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import java.util.ArrayList;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2021/1/7 19:19
 * @version： v1.0
 * @function： 传输列表  上传/下载
 */
public class TransfersActivity  extends BaseActivity {


    TabLayout mTabLayout;

    ViewPager mViewPager;

    private TableViewPagerAdapter mAdapter;

    @Override
    protected ActivityTransfersBinding getViewBinding() {
        return ActivityTransfersBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_transfers;
//    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initActionBar() {

        new DefaultNavigationBar.Builder(this).setTitle(getResources().getString(R.string.transfers_list)).builder();
    }

    @Override
    protected void initView() {
        ActivityTransfersBinding tbinding = (ActivityTransfersBinding) binding;
        TabLayout mTabLayout = tbinding.tabLayout;
        ViewPager mViewPager = tbinding.viewPager;
        mAdapter = new TableViewPagerAdapter(getSupportFragmentManager());
        mAdapter.reSet(new String[]{getString(R.string.download_list),getString(R.string.upload_list)});
        mAdapter.reSet(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent(View v) {

    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        //下载列表
        fragments.add(new DownloadFragment());
        //上传列表
        fragments.add(new UploadFragment());
        return fragments;
    }
}
