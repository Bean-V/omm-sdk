package com.oortcloud.oort_zhifayi.new_version.home;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

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