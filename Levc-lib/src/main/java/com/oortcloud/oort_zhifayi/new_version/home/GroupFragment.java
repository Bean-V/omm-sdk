package com.oortcloud.oort_zhifayi.new_version.home;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.oortcloud.oort_zhifayi.R;
import com.oortcloud.oort_zhifayi.databinding.FragmentGroupBinding;

public class GroupFragment extends Fragment {
    private FragmentGroupBinding binding; // 使用 ViewBinding
    private GroupViewModel viewModel;     // 使用 ViewModel

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 初始化 ViewBinding
        binding = FragmentGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        setupTabsWithFragments();
    }



    private void setupTabsWithFragments() {
        // 初始化 TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchToFragment(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // 初始化 Tab 标题
        for (int i = 0; i < viewModel.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(viewModel.getTabTitle(i));
            binding.tabLayout.addTab(tab);
        }

        // 默认加载第一个 Fragment
        switchToFragment(0);
    }

    private SparseArray<Fragment> fragmentCache = new SparseArray<>();

    private void switchToFragment(int position) {
        Fragment fragment = fragmentCache.get(position);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // 如果目标 Fragment 不存在，则创建并添加
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new GroupContentFragment();
                    break;
                case 1:
                    fragment = new GroupFragment.CameraFragment();
                    break;
                case 2:
                    fragment = new GroupFragment.RecorderFragment();
                    break;
                case 3:
                    fragment = new GroupFragment.TaskFragment();
                    break;
                default:
                    fragment = new GroupContentFragment();
                    break;
            }
            fragmentCache.put(position, fragment); // 缓存 Fragment
            transaction.add(R.id.fragmentContainer, fragment); // 添加到容器
        }

        // 隐藏所有其他 Fragment
        for (int i = 0; i < fragmentCache.size(); i++) {
            int key = fragmentCache.keyAt(i);
            Fragment cachedFragment = fragmentCache.get(key);
            if (cachedFragment != null && cachedFragment.isAdded()) {
                if (key == position) {
                    transaction.show(cachedFragment); // 显示目标 Fragment
                } else {
                    transaction.hide(cachedFragment); // 隐藏其他 Fragment
                }
            }
        }

        transaction.commit();
    }

    public static class CameraFragment extends GroupContentFragment {
    }

    public static class RecorderFragment extends GroupContentFragment {
    }

    public static class TaskFragment extends GroupContentFragment {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 防止内存泄漏
    }
}


