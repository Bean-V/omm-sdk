package com.oort.weichat.ui.offline;

import static com.oort.weichat.MyApplication.cordovaView;
import static com.oort.weichat.MyApplication.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.offline.adapter.OffLineHomeAdapter;
import com.oort.weichat.view.viewpage.SuperViewPager;
import com.oortcloud.custom.CordovaView;

import java.util.ArrayList;

public class OnffLineMainActivity extends BaseActivity {

    private SuperViewPager superViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onff_line_main);
        initWebView();
        initView();
        initData();
    }

    private void initWebView() {
        cordovaView = new CordovaView(getApplicationContext());
        cordovaView.initCordova(this);
        cordovaView.loadUrl("file:///android_asset/offline/index.html");
        WebSettings settings = cordovaView.getWebview().getSettings();
        // 1. 设置缓存路径
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + "cache/";
//        settings.setAppCachePath(cacheDirPath);
//        // 2. 设置缓存大小
//        settings.setAppCacheMaxSize(20 * 1024 * 1024);
//        // 3. 开启Application Cache存储机制
//        settings.setAppCacheEnabled(true);
        //4.开启DOM storage
        settings.setDomStorageEnabled(true);
        //5.只需设置支持JS就自动打开IndexedDB存储机制
        settings.setJavaScriptEnabled(true);
    }

    private void initData() {
//        superViewPager.setCurrentItem(2);
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new OffLineHomeFragment());
        list.add(new OfferLineMeFragment());
        OffLineHomeAdapter homeAdapter = new OffLineHomeAdapter(list, getSupportFragmentManager());
        for (int po = 0, size = list.size(); po < size; po++) {
            tabLayout.addTab(tabLayout.newTab());
            TabLayout.Tab tabAt = tabLayout.getTabAt(po);
            if (tabAt == null) return;
            tabAt.setCustomView(homeAdapter.getTabView(this, po));
        }
        superViewPager.setAdapter(homeAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                superViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
    }

    private void initView() {
        superViewPager = findViewById(R.id.superViewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }


    public static void start(Context context) {
        if (context == null) return;
        context.startActivity(new Intent(context, OnffLineMainActivity.class));
    }

    public static void startAndFinish(Activity context) {
        if (context == null) return;
        start(context);
        context.finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cordovaView != null) {
            cordovaView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cordovaView != null) {
            cordovaView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cordovaView != null) {
            cordovaView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (cordovaView != null) {
                cordovaView.onDestroy();
            }
        } catch (Exception e) {
        }

    }
}