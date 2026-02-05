package com.oortcloud.oort_zhifayi.new_version.home;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupViewModel extends ViewModel {

    private final MutableLiveData<List<Group>> groupList = new MutableLiveData<>(); // 群组数据
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>(); // 加载状态

    public GroupViewModel() {
        // 初始化数据
        loadGroupData();
    }

    // 获取群组数据
    public LiveData<List<Group>> getGroupList() {
        return groupList;
    }

    // 获取加载状态
    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    // 模拟加载群组数据
    private void loadGroupData() {
        loadingState.setValue(true);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<Group> groups = new ArrayList<>();
            groups.add(new Group(
                    "交流演示一组",
                    "共21人/8在线",
                    "空闲守候",
                    Arrays.asList("R.drawable.avatar1", "R.drawable.avatar1", "R.drawable.avatar1")
            ));
            groups.add(new Group(
                    "交流演示二组",
                    "共8人/3在线",
                    "空闲守候",
                    Arrays.asList("R.drawable.avatar1", "R.drawable.avatar1")
            ));
            groups.add(new Group(
                    "交流演示三组",
                    "共21人/8在线",
                    "空闲守候",
                    Collections.emptyList() // 没有头像
            ));
            groups.add(new Group(
                    "交流演示一组",
                    "共21人/8在线",
                    "空闲守候",
                    Arrays.asList("R.drawable.avatar1", "R.drawable.avatar1", "R.drawable.avatar1")
            ));
            groups.add(new Group(
                    "交流演示二组",
                    "共8人/3在线",
                    "空闲守候",
                    Arrays.asList("R.drawable.avatar1", "R.drawable.avatar1")
            ));
            groups.add(new Group(
                    "交流演示三组",
                    "共21人/8在线",
                    "空闲守候",
                    Collections.emptyList() // 没有头像
            ));
            groupList.setValue(groups);
            loadingState.setValue(false);
        }, 2000); // 模拟网络延迟
    }

    private final String[] tabTitles = {
            "对讲群组",
            "摄像机",
            "执法记录仪",
            "任务接收"
    };

    // 获取指定位置的标题
    public String getTabTitle(int position) {
        if (position >= 0 && position < tabTitles.length) {
            return tabTitles[position];
        }
        return ""; // 如果位置无效，返回空字符串
    }

    public int getTabCount() {
        return tabTitles.length;
    }
}

