package com.oortcloud.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
import com.oortcloud.contacts.utils.DeptAndUserSetUtils;
import com.oortcloud.contacts.utils.ToastUtils;
import com.oortcloud.contacts.view.ItemTabLayout;
import com.oortcloud.contacts.view.SlitherViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2022/4/11 09:55
 * @version： v1.0
 * @function： 管理员设置
 */
public class ManagerSettingActivity extends BaseActivity {
    // 声明变量
    private ImageView mBack;
    private TextView mRightTv;
    private ItemTabLayout mTabLayout;
    private SlitherViewPager mViewPager;
    private LinearLayout mBottomLayout;
    private Button mCancelBtn;
    private Button mConfirmBtn;


    private TableViewPagerAdapter mAdapter;
    List<BaseFragment> mFragments;
    private int mCurrentPosition;
    //是否恢复默认状态
    private boolean mIsNormalStatus;
    //是否拦截事件
    private boolean mIsOnTouchStatus;
    private com.oortcloud.contacts.databinding.ActivityManagerSettingBinding binding;

    @Override
    protected View getRootView() {

        // 通过 ViewBinding 赋值

        binding = ActivityManagerSettingBinding.inflate(getLayoutInflater());
        mBack = binding.back;
        mRightTv = binding.ivTitleRight;
        mTabLayout = binding.tabLayout;
        mViewPager = binding.viewPager;
        mBottomLayout = binding.bottomLl;
        mCancelBtn = binding.cancelBtn;
        mConfirmBtn = binding.confirmBtn;
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

        for (int i=0;i<mTabLayout.getTabCount();i++) {
            View view = getTabView(mTabLayout,i);
            if (view == null) continue;
            view.setTag(i);
            view.setOnTouchListener(tabOnClickListener);
        }
    }
    /** 拦截tablayout点击事件 */
    View.OnTouchListener tabOnClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int pos = (int) view.getTag();
            /** 下面都是自定义逻辑了 */

            return mIsOnTouchStatus; // 是否拦截
        }
    };
    /** 反射获取tabview */
    public View getTabView( TabLayout tabLayout,int index){
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab == null) return null;
        View tabView = null;
        Field view = null;
        try {
            view = TabLayout.Tab.class.getDeclaredField("view");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        view.setAccessible(true);
        try {
            tabView = (View) view.get(tab);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return tabView;
    }

    @Override
    protected void initEvent() {
        mBack.setOnClickListener(v -> {
            DeptAndUserSetUtils.clear();
            finish();

        });
        mRightTv.setOnClickListener(v -> {
            mRightTv.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.VISIBLE);
            if (mFragments != null){
                mFragments.get(mCurrentPosition).notifyChanged(true);
            }
            mIsOnTouchStatus = true;
            mViewPager.setSlither(mIsOnTouchStatus);
        });
        mCancelBtn.setOnClickListener(v -> {
            DeptAndUserSetUtils.clear();
            setNormalStatus();
        });

        mConfirmBtn.setOnClickListener(v -> {
            if (DeptAndUserSetUtils.getDeptSize() != 0 || DeptAndUserSetUtils.getUserSize() != 0  ){
                mContext.startActivity(new Intent(mContext, SettingUserInfoShowActivity.class));
                mIsNormalStatus = true;
            }else {
                ToastUtils.showBottom(R.string.select_set_dept_user);
            }

        });
    }

    private void  setNormalStatus(){
        mRightTv.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.GONE);
        if (mFragments != null){
            mFragments.get(mCurrentPosition).notifyChanged(false);
        }
        mIsOnTouchStatus = false;
        mViewPager.setSlither(mIsOnTouchStatus);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mIsNormalStatus){
            setNormalStatus();
        }

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
