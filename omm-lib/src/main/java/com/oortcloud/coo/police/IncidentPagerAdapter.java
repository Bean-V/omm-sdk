package com.oortcloud.coo.police;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class IncidentPagerAdapter extends FragmentStateAdapter {
    
    private static final int TAB_COUNT = 3;
    private IncidentListFragment[] fragments = new IncidentListFragment[TAB_COUNT];
    
    public IncidentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragments[position] == null) {
            fragments[position] = IncidentListFragment.newInstance(position);
        }
        return fragments[position];
    }
    
    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
    
    /**
     * 获取指定位置的Fragment
     */
    public IncidentListFragment getFragment(int position) {
        return fragments[position];
    }
}

