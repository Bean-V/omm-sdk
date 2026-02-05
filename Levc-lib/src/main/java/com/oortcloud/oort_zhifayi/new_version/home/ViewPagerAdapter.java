package com.oortcloud.oort_zhifayi.new_version.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回不同的 Fragment
        switch (position) {
            case 0:
                return new GroupFragment(); // 对讲群组的 Fragment
            case 1:
                return new CameraFragment(); // 摄像机的 Fragment
            case 2:
                return new RecorderFragment(); // 执法记录仪的 Fragment
            case 3:
                return new TaskFragment(); // 任务接收的 Fragment
            default:
                return new GroupFragment(); // 默认空 Fragment
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 返回 Tab 的数量
    }

    public static class CameraFragment extends GroupFragment {
    }

    public static class RecorderFragment extends GroupFragment {
    }

    public static class TaskFragment extends GroupFragment {
    }
}
