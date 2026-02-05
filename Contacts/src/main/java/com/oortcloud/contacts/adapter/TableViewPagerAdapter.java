package com.oortcloud.contacts.adapter;


import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun / @date: 2020/1/7 15:38
 */
public class TableViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    ;
    private String[] mTitles;
    private Fragment mCurFragment;

    public TableViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
    }

    public void addFragment(Fragment fragment) {
        if (mFragments != null){
            mFragments.add(fragment);
        }
    }


    public void reset(List<Fragment> fragments) {
        if (mFragments != null){

            mFragments.clear();
            mFragments.addAll(fragments);
        }

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

    public void reset(String[] titles) {
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0 ;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
