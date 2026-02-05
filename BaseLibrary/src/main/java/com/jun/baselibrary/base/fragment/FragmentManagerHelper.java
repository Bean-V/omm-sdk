package com.jun.baselibrary.base.fragment;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/27 14:29
 * Version 1.0
 * Description：Fragment切换帮助类
 */
public class FragmentManagerHelper {

    //容器布局id
    private int mContainerViewId;
    // fragment管理类
    private FragmentManager mFragmentManager;
    //显示当前Fragment
    private Fragment mCurrentFragment;

    /**
     * 构造函数
     *
     * @param fragmentManager fragment管理类
     * @param containerViewId 容器布局id
     */
    public FragmentManagerHelper(FragmentManager fragmentManager, @IdRes int containerViewId) {
        if (fragmentManager == null || containerViewId == 0)
            this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    public void add(Fragment fragment) {
        if (mCurrentFragment != fragment){
            //开启事务
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(mContainerViewId, fragment);
            mCurrentFragment = fragment;
            //提交
            transaction.commit();
        }
    }

    /**
     * 切换显示fragment
     *
     * @param fragment
     */
    public void switchFragment(Fragment fragment) {
        //不同才处理
        if (mCurrentFragment != fragment) {
            //开启事务
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            //获取所以Fragment隐藏
            List<Fragment> fragments = mFragmentManager.getFragments();
            for (Fragment childFragment : fragments) {
                transaction.hide(childFragment);
            }
            //判断是否存在，不存在添加 ，存在直接显示
            if (!fragments.contains(fragment)) {
                add(fragment);
            } else {
                transaction.show(fragment);
                mCurrentFragment = fragment;
            }

            //提交
            transaction.commit();

        }
    }
}
