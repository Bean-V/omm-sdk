package com.oortcloud.appstore.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.TableViewPagerAdapter;
import com.oortcloud.appstore.databinding.FragmentTableLayoutBinding;
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.fragment.table.ClassifyFragment;
import com.oortcloud.appstore.fragment.table.ManageFragment;
import com.oortcloud.appstore.fragment.table.MineFragment;
import com.oortcloud.appstore.fragment.table.RecommendFragment;
import com.oortcloud.appstore.premission.PermissionCanstants;
import com.oortcloud.appstore.premission.PermissionManager;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2020/1/6 16:19
 */
public class TableFragment extends BaseFragment {


    ViewPager mPageContent;
    BottomNavigationView mBtmNavigation;
    private MenuItem mMenuItem;

    private int position;
    private com.oortcloud.appstore.databinding.FragmentTableLayoutBinding binding;

    @Override
    protected void initBundle(Bundle bundle) {
        if (!PermissionManager.hasPermission(getActivity() , PermissionCanstants.getPermission()))
        {
            PermissionManager.requestPermission(getActivity() ,0 , PermissionCanstants.getPermission());
        }
        ClassifyManager.initClassify();

    }

    @Override
    protected View getRootView() {


        binding = FragmentTableLayoutBinding.inflate(getLayoutInflater());
         mPageContent = binding.pageContent;
         mBtmNavigation = binding.btmNavigation;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_table_layout;
    }

    protected void initView() {

    }

    @Override
    protected void initData() {
        TableViewPagerAdapter adapter = new TableViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MineFragment());
        adapter.addFragment(new RecommendFragment());
        adapter.addFragment(new ClassifyFragment());
        adapter.addFragment(new ManageFragment());
        mPageContent.setAdapter(adapter);
//        mPageContent.setScroll(true);
        if (position != 0)
            mPageContent.setCurrentItem(position, false);
    }


    @Override
    protected void initEvent() {
        mBtmNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

                if (itemID == R.id.item_mine) {

                    mPageContent.setCurrentItem(0, false);

                }
                else if (itemID == R.id.item_recommend) {

                    mPageContent.setCurrentItem(1, false);

                } else if (itemID == R.id.item_classify) {

                    mPageContent.setCurrentItem(2, false);

                }
                else if (itemID == R.id.item_manage) {

                    mPageContent.setCurrentItem(3, false);
                }
                return false;
            }

        });

        mPageContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mMenuItem != null) {
                    mMenuItem.setChecked(false);
                } else {
                    mBtmNavigation.getMenu().getItem(0).setChecked(false);
                }
                TableFragment.this.position = position;
                mMenuItem = mBtmNavigation.getMenu().getItem(position);
                mMenuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
