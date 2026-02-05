package com.oortcloud.appstore.adapter;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.oortcloud.basemodule.utils.OperLogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun / @date: 2020/1/7 15:38
 */
public class TableViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments = new ArrayList<>();
    ;
    private String[] mTitles;
    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

    public TableViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragmentManager = fm;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }


    public void reset(List<Fragment> fragments) {
        mFragments.clear();
        mFragments.addAll(fragments);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof Fragment) {
            mCurFragment = (Fragment) object;
        }
        OperLogUtil.msg("点击了tab"+ position + object.getClass());
    }

    public Fragment getCurFragment() {
        return mCurFragment;
    }

    public void reset(String[] titles) {
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
