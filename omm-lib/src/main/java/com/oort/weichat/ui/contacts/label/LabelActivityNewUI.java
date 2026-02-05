package com.oort.weichat.ui.contacts.label;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oortcloud.basemodule.utils.SkinUtils;

import java.util.ArrayList;
import java.util.List;

public class LabelActivityNewUI extends BaseActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private LabelFragment lab;

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, LabelActivityNewUI.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_new_ui);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(R.string.tag);

        TextView rtvTitle = (TextView) findViewById(R.id.tv_title_right);
        rtvTitle.setText(getString(R.string.edit));
        rtvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lab.setEdit(rtvTitle.getText().equals(R.string.edit) ? true : false);
                rtvTitle.setText(rtvTitle.getText().equals(getString(R.string.edit)) ? getString(R.string.done) : getString(R.string.edit));
            }
        });
    }

    private void initView() {

        lab = new LabelFragment();
        mViewPager = (ViewPager) findViewById(R.id.label_tab_vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(lab);
        mViewPager.setAdapter(new LabelActivityNewUI.MyTabAdapter(getSupportFragmentManager(), fragments));

        tabLayout = (TabLayout) findViewById(R.id.label_tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_black), SkinUtils.getSkin(this).getAccentColor());
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_title_left) {
                finish();
        }
    }

    class MyTabAdapter extends FragmentPagerAdapter {
        List<String> listTitle = new ArrayList<>();
        private List<Fragment> mFragments;

        MyTabAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            if (mFragments != null) {
                return mFragments.size();
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}
