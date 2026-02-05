package com.oort.weichat.fragment.dynamic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

@SuppressLint("RestrictedApi")
public class CustomFooter extends LinearLayout implements RefreshFooter {

    private TextView textView;
    private boolean noMoreData = false;

    public CustomFooter(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        textView = new TextView(context);
        textView.setText("正在加载...");
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);
        addView(textView);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        // Footer 将固定在内容下面
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {
        
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        // 开始动画时的处理逻辑
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        // 刷新完成时的逻辑，返回延迟关闭的时间（毫秒）
        textView.setText(success ? "加载完成" : "加载失败");
        return 0; // 不延迟关闭
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        this.noMoreData = noMoreData;
        textView.setText(noMoreData ? "没有更多数据" : "正在加载...");
        return true; // 状态发生变化，返回 true
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        // 初始化逻辑
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int extendHeight) {
        // 监听拖拽的逻辑
    }


    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int extendHeight) {
        // 释放时的操作
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        // 是否支持水平拖动
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }
}
