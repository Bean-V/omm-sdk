package com.oortcloud.revision.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.oort.weichat.R;

public class ChatTabBar extends LinearLayout {
    public static final int TAB_ALL = 0;
    public static final int TAB_UNREAD = 1;
    public static final int TAB_SINGLE = 2;
    public static final int TAB_GROUP = 3;
    private final LinearLayout[] tabItems = new LinearLayout[4];
    private final ImageView[] tabIcons = new ImageView[4];
    private final TextView[] tabTexts = new TextView[4];
    private View selectedBackground;
    private RelativeLayout tabsContainer;
    private final int[] tabWidths = new int[4]; // 存储每个Tab的宽度（解决消失问题的核心）
    private final int[] tabLefts = new int[4]; // 存储每个Tab的left坐标
    private int currentTabIndex = 0;
    private int selectedColor;
    private int unselectedColor;
    private OnTabSelectedListener onTabSelectedListener;

    // 构造方法不变
    public ChatTabBar(Context context) {
        super(context);
        initView(context);
    }

    public ChatTabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatTabBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.chat_tab_bar, this);

        selectedColor = ContextCompat.getColor(context, R.color.main_color);
        unselectedColor = ContextCompat.getColor(context, android.R.color.darker_gray);

        tabsContainer = findViewById(R.id.tabs_container);
        selectedBackground = findViewById(R.id.selected_background);

        // 绑定Tab控件
        tabItems[TAB_ALL] = findViewById(R.id.tab_all);
        tabItems[TAB_UNREAD] = findViewById(R.id.tab_unread);
        tabItems[TAB_SINGLE] = findViewById(R.id.tab_single);
        tabItems[TAB_GROUP] = findViewById(R.id.tab_group);

        tabIcons[TAB_ALL] = findViewById(R.id.iv_all);
        tabIcons[TAB_UNREAD] = findViewById(R.id.iv_unread);
        tabIcons[TAB_SINGLE] = findViewById(R.id.iv_single);
        tabIcons[TAB_GROUP] = findViewById(R.id.iv_group);

        tabTexts[TAB_ALL] = findViewById(R.id.tv_all);
        tabTexts[TAB_UNREAD] = findViewById(R.id.tv_unread);
        tabTexts[TAB_SINGLE] = findViewById(R.id.tv_single);
        tabTexts[TAB_GROUP] = findViewById(R.id.tv_group);

        // 关键修复：监听布局完成后计算Tab的宽度和left坐标（使用getLeft()而非hitRect）
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 计算每个可见Tab的宽度和在父容器中的left坐标
                for (int i = 0; i < tabItems.length; i++) {
                    if (tabItems[i].getVisibility() == View.VISIBLE) {
                        // 直接获取Tab在父容器中的left坐标（相对父容器）
                        tabLefts[i] = tabItems[i].getLeft();
                        // 获取Tab的实际宽度
                        tabWidths[i] = tabItems[i].getWidth();
                    }
                }

                // 初始化默认选中第一个Tab（强制显示滑块）
                setTabSelected(TAB_ALL, false);

                // 移除监听
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        // 绑定Tab点击事件
        for (int i = 0; i < tabItems.length; i++) {
            final int tabIndex = i;
            tabItems[i].setOnClickListener(v -> {
                if (tabIndex != currentTabIndex && tabItems[tabIndex].getVisibility() == View.VISIBLE) {
                    setTabSelected(tabIndex, true);
                }
            });
        }
    }

    /**
     * 设置选中的Tab，修复滑块消失问题
     */
    public void setTabSelected(int tabIndex, boolean animate) {
        if (tabIndex < 0 || tabIndex >= tabItems.length
                || tabIndex == currentTabIndex
                || tabItems[tabIndex].getVisibility() != View.VISIBLE) {
            return;
        }

        int previousIndex = currentTabIndex;
        currentTabIndex = tabIndex;
        updateTabColors();
        // 使用修复后的坐标计算方式滑动背景
        slideBackground(previousIndex, tabIndex, animate);

        if (onTabSelectedListener != null) {
            onTabSelectedListener.onTabSelected(tabIndex);
        }
    }

    private void updateTabColors() {
        for (int i = 0; i < tabItems.length; i++) {
            if (tabItems[i].getVisibility() != View.VISIBLE) continue;

            if (i == currentTabIndex) {
                tabIcons[i].setColorFilter(selectedColor);
                tabTexts[i].setTextColor(selectedColor);
            } else {
                tabIcons[i].setColorFilter(unselectedColor);
                tabTexts[i].setTextColor(unselectedColor);
            }
        }
    }

    /**
     * 修复滑动背景逻辑：使用预存的left和width，确保滑块不消失
     */
    private void slideBackground(int fromIndex, int toIndex, boolean animate) {
        // 强制显示滑块（防止意外隐藏）
        selectedBackground.setVisibility(View.VISIBLE);

        // 目标Tab的位置和宽度（从预存数组中获取，确保不为空）
        final int targetLeft = tabLefts[toIndex];
        final int targetWidth = tabWidths[toIndex];

        if (animate) {
            // 起点Tab的位置和宽度
            int startLeft = tabLefts[fromIndex];
            int startWidth = tabWidths[fromIndex];

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(300);
            animator.setInterpolator(new DecelerateInterpolator(1.5f));
            animator.addUpdateListener(animation -> {
                float fraction = animation.getAnimatedFraction();
                // 计算当前位置（确保不会出现负数或异常值）
                int currentLeft = Math.max(0, (int) (startLeft + (targetLeft - startLeft) * fraction));
                int currentWidth = Math.max(0, (int) (startWidth + (targetWidth - startWidth) * fraction));

                // 更新滑块布局（使用RelativeLayout参数）
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectedBackground.getLayoutParams();
                params.leftMargin = currentLeft;
                params.width = currentWidth;
                selectedBackground.setLayoutParams(params);
            });
            animator.start();
        } else {
            // 无动画时直接定位
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectedBackground.getLayoutParams();
            params.leftMargin = targetLeft;
            params.width = targetWidth;
            selectedBackground.setLayoutParams(params);
        }
    }

    // 接口和其他方法不变
    public interface OnTabSelectedListener {
        void onTabSelected(int tabIndex);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.onTabSelectedListener = listener;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }
}