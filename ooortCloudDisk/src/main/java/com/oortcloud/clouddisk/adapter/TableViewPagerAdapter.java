package com.oortcloud.clouddisk.adapter;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *  @filename:
 *  @author: zzj / @date: 2020/1/7 15:38
 *  @version： v1.0
 *  @function： TabLayout ViewPager 适配器
 *
 *
 */
public class TableViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
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


    public void reSet(List<Fragment> fragments) {
        mFragments.clear();
        mFragments.addAll(fragments);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof Fragment) {
            mCurFragment = (Fragment) object;
        }
    }

    public Fragment getCurFragment() {
        return mCurFragment;
    }

    public void reSet(String[] titles) {
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
