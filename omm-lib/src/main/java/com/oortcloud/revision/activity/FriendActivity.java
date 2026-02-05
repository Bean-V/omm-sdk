package com.oortcloud.revision.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.R;
import com.oort.weichat.fragment.FriendFragment;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.groupchat.RoomFragment;
import com.oort.weichat.ui.nearby.UserSearchActivity;
import com.oortcloud.appstore.adapter.TableViewPagerAdapter;
import com.oortcloud.contacts.fragment.ContactsFragment;
import com.oortcloud.revision.fragment.PublishNumberFragment;

import java.util.ArrayList;
import java.util.List;

//import butterknife.Unbinder;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/6 21:57
 * @version： v1.0
 * @function：消息Activtiy
 */
public class FriendActivity extends BaseActivity {
    TabLayout mTabLayout;
     ViewPager mViewPager;
     ImageView mAddUser;
     ImageView mBack;

    private TableViewPagerAdapter mAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        if (getIntent() != null) {

        }
        initActionBar();
        initView();
        initData();


    }

    private void initActionBar() {
        getSupportActionBar().hide();

    }

    private void initView() {

        mViewPager = findViewById(R.id.view_pager);
        mAddUser = findViewById(R.id.add_img);
        mBack = findViewById(R.id.back);
        mTabLayout = findViewById(R.id.tab_layout);
        mAdapter = new TableViewPagerAdapter(getSupportFragmentManager());
        mAdapter.reset(new String[]{"联系人","组织" ,"群组"  , "应用号"});

        mAdapter.reset(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mBack.setOnClickListener(v -> {
            finish();
        });
        mAddUser.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UserSearchActivity.class));
        });
    }

    private void initData() {

    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        //联系人
        fragments.add(new FriendFragment());
        //组织架构
        fragments.add( new ContactsFragment());
        //群组
        fragments.add(new RoomFragment());
        //公众号
        fragments.add(new PublishNumberFragment());
        return fragments;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
