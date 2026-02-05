package com.oortcloud.appstore.fragment.table;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.TableViewPagerAdapter;
import com.oortcloud.appstore.databinding.FragmentManageLayoutBinding;
import com.oortcloud.appstore.fragment.BaseFragment;
import com.oortcloud.appstore.fragment.ManagePageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：管理Fragment
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/7 16:22
 */
public class ManageFragment extends BaseFragment {

    TabLayout mAmongTabLayout;
    ViewPager mAmongViewpager;

    private TableViewPagerAdapter mAdapter;
    private com.oortcloud.appstore.databinding.FragmentManageLayoutBinding binding;

    @Override
    protected View getRootView() {
        binding = FragmentManageLayoutBinding.inflate(getLayoutInflater());
        mAmongTabLayout = binding.amongTabLayout;
        mAmongViewpager = binding.amongViewPager;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_manage_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {


    }

    @Override
    protected void initView() {
        mAdapter = new TableViewPagerAdapter(getChildFragmentManager());
        mAdapter.reset(getFragments());
        mAdapter.reset(getResources().getStringArray(R.array.manage_titles));
        mAmongViewpager.setAdapter(mAdapter);
        if (mAmongTabLayout != null) {
            mAmongTabLayout.setupWithViewPager(mAmongViewpager);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ManagePageFragment.instantiate(ManagePageFragment.FRAGMENT_UPDATE));
        fragments.add(ManagePageFragment.instantiate(ManagePageFragment.FRAGMENT_UNINSTALL));
        return fragments;
    }

}
