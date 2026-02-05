package com.oort.weichat.call;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.oort.weichat.R;

public class DraggableAddParticipantButton extends androidx.appcompat.widget.AppCompatButton {
    private int lastX, lastY;
    private int startX, startY;
    private boolean isDragging = false;
    private int screenWidth, screenHeight;
    private int touchSlop; // 最小滑动距离（判断是否为拖拽）

    public DraggableAddParticipantButton(Context context) {
        super(context);
        init(context);
    }

    public DraggableAddParticipantButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 获取屏幕尺寸
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        // 获取系统最小滑动距离（判断拖拽的阈值）
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // 初始化按钮样式
        initButtonStyle();
    }

    /** 初始化按钮样式（圆角、阴影、图标等） */
    private void initButtonStyle() {
        // 设置文字
        setText("添加参会人");
        setTextColor(Color.WHITE);
        setTextSize(14);

        // 设置图标（左侧）
        Drawable addIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_add_white_24dp); // 需要自己添加图标资源
        if (addIcon != null) {
            addIcon.setBounds(0, 0, dp2px(20), dp2px(20)); // 图标大小
            setCompoundDrawables(addIcon, null, null, null);
            setCompoundDrawablePadding(dp2px(8)); // 图标与文字间距
        }

        // 设置圆角背景（正常/按下状态）
        setBackground(createStateListDrawable());

        // 设置内边距
        setPadding(dp2px(12), dp2px(8), dp2px(12), dp2px(8));

        // 设置阴影（API 21+）
        setElevation(dp2px(4));
        setTranslationZ(dp2px(2));
    }

    /** 创建状态选择器（正常/按下状态） */
    private StateListDrawable createStateListDrawable() {
        StateListDrawable drawable = new StateListDrawable();

        // 按下状态背景（深一点的颜色）
        GradientDrawable pressedBg = new GradientDrawable();
        pressedBg.setColor(ContextCompat.getColor(getContext(), R.color.primary_dark)); // 深色
        pressedBg.setCornerRadius(dp2px(20)); // 圆角

        // 正常状态背景
        GradientDrawable normalBg = new GradientDrawable();
        normalBg.setColor(ContextCompat.getColor(getContext(), R.color.primary)); // 主色调
        normalBg.setCornerRadius(dp2px(20)); // 圆角

        // 添加状态
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
        drawable.addState(new int[]{}, normalBg);

        return drawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的初始位置
                lastX = x;
                lastY = y;
                startX = x;
                startY = y;
                isDragging = false;
                // 按下时放大效果
                setScaleX(1.05f);
                setScaleY(1.05f);
                break;

            case MotionEvent.ACTION_MOVE:
                // 计算移动距离
                int dx = x - lastX;
                int dy = y - lastY;

                // 判断是否达到拖拽阈值
                if (!isDragging) {
                    int moveDistance = (int) Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));
                    if (moveDistance > touchSlop) {
                        isDragging = true;
                    }
                }

                if (isDragging) {
                    // 计算新位置（限制在屏幕内）
                    int left = getLeft() + dx;
                    int top = getTop() + dy;
                    int right = getRight() + dx;
                    int bottom = getBottom() + dy;

                    // 边界检查（避免移出屏幕）
                    if (left < 0) left = 0;
                    if (top < 0) top = 0;
                    if (right > screenWidth) left = screenWidth - (right - left);
                    if (bottom > screenHeight) top = screenHeight - (bottom - top);

                    // 更新位置
                    layout(left, top, left + getWidth(), top + getHeight());
                    lastX = x;
                    lastY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 恢复缩放
                setScaleX(1.0f);
                setScaleY(1.0f);

                // 如果不是拖拽，触发点击事件
                if (!isDragging) {
                    performClick();
                }
                break;
        }
        return true; // 消费事件，避免底层视图干扰
    }

    /** dp转px */
    private int dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /** 设置点击事件回调 */
    public void setOnAddParticipantClickListener(OnClickListener listener) {
        super.setOnClickListener(listener);
    }
}