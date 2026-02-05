package com.oort.weichat.ui.offline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.oort.weichat.R;


import java.util.List;

/**
 * Created by rui
 * on 2021/7/29
 */
public class OffLineHomeAdapter extends FragmentPagerAdapter {


    private List<Fragment> fragmentList;

    private FragmentManager fragmentManager;

    public OffLineHomeAdapter(List<Fragment> fragmentList,
                              FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
        this.fragmentList = fragmentList;

    }

    private String[] titles = {"首页", "我"};

    private int images[] = {
            R.drawable.btm_home_selector,
            R.drawable.btm_user_selector};

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentManager.beginTransaction().hide((Fragment) object).commitAllowingStateLoss();
    }

    public View getTabView(Context context, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_tablayout_icon, null);
        TextView textView = v.findViewById(R.id.tlText);
        ImageView imageView = v.findViewById(R.id.tlIcon);
        textView.setText(titles[position]);
        imageView.setImageResource(images[position]);
        return v;
    }

}
