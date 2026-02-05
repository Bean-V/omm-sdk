package com.oortcloud.contacts.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.TableViewPagerAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.databinding.ActivityManagerSettingBinding;
import com.oortcloud.contacts.fragment.BaseFragment;
import com.oortcloud.contacts.fragment.SettingDeptFragment;
import com.oortcloud.contacts.fragment.SettingUserFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2022/4/11 09:55
 * @version： v1.0
 * @function： 多选管理员设置
 */
public class ManagerSelectSettingActivity extends BaseActivity {
    private ImageView mBack;
    private TextView mRightTv;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LinearLayout mBottomLayout;
    private Button mCancelBtn;
    private Button mConfirmBtn;

    private TableViewPagerAdapter mAdapter;
    List<BaseFragment> mFragments;
    private int mCurrentPosition;
    ActivityManagerSettingBinding binding;
    @Override
    protected View getRootView() {
        binding = ActivityManagerSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_setting;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mLoadingDialog.dismiss();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

        // 通过 ViewBinding 赋值
        mBack = binding.back;
        mRightTv = binding.ivTitleRight;
        mTabLayout = binding.tabLayout;
        mViewPager = binding.viewPager;
        mBottomLayout = binding.bottomLl;
        mCancelBtn = binding.cancelBtn;
        mConfirmBtn = binding.confirmBtn;
        mBack.setOnClickListener(v -> {
            finish();
        });

        mViewPager.setOffscreenPageLimit(2);
        mAdapter = new TableViewPagerAdapter(getSupportFragmentManager());
        mAdapter.reset(new String[]{getResources().getString(R.string.dept_setting)
                , getResources().getString(R.string.personal_setting)});

        mAdapter.reset(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //监听当前选中位置
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //监听当前选中的位置
                mCurrentPosition = position;
            }
        });

    }

    @Override
    protected void initEvent() {
        mRightTv.setOnClickListener(v -> {
            mRightTv.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
            if (mFragments != null){
                mFragments.get(mCurrentPosition).notifyChanged(true);
            }
        });
        mCancelBtn.setOnClickListener(v -> {
            mRightTv.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.GONE);

        });
        mConfirmBtn.setOnClickListener(v -> {
//            mRightTv.setVisibility(View.VISIBLE);
//            mBottomLayout.setVisibility(View.GONE);

        });
    }
    
    protected List getFragments() {
        if (mFragments == null){
            mFragments = new ArrayList<>();
        }else {
            mFragments.clear();
        }
        //部门设置
        mFragments.add(SettingDeptFragment.instantiate(Constants.DEPT));
        //个人设置
        mFragments.add(SettingUserFragment.instantiate(Constants.USER));

        return mFragments;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(EventMessage event) {

    }

}
