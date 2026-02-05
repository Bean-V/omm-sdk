package com.oortcloud.oort_zhifayi.new_version.home;

import android.content.Intent;
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
import com.oortcloud.oort_zhifayi.databinding.FragmentGroupContentBinding;
import com.oortcloud.oort_zhifayi.new_version.chat.ChatActivity;

import java.lang.reflect.Method;

public class GroupContentFragment extends Fragment {
    private FragmentGroupContentBinding binding; // 使用 ViewBinding
    private GroupViewModel viewModel;     // 使用 ViewModel

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 初始化 ViewBinding
        binding = FragmentGroupContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        // 设置观察者监听数据变化
        setupObservers();

        // 初始化 UI
        setupUI();
    }

    private void setupObservers() {
        // 观察群组数据
        viewModel.getGroupList().observe(getViewLifecycleOwner(), groupList -> {
            // 更新 UI，例如更新 RecyclerView 的适配器数据
            GroupAdapter adapter = (GroupAdapter) binding.recyclerView.getAdapter();
            if (adapter != null) {
                adapter.updateData(groupList);
            }
        });

        // 观察其他可能的状态
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            // 显示或隐藏加载指示器
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupUI() {
        // 设置 RecyclerView

        GroupAdapter ga = new GroupAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(ga);
        ga.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                getActivity().startActivity(new Intent(getContext(), ChatActivity.class));
            }
        });



        // 其他 UI 初始化逻辑
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 防止内存泄漏
    }
}


