package com.oort.weichat.fragment.vs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class StableBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    public StableBottomSheetBehavior() {
        super();
    }

    public StableBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 正确的方法签名 - 这个是您需要重写的方法
    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                   @NonNull V child,
                                   @NonNull View target,
                                   int type) {
        // 首先调用父类的方法
        super.onStopNestedScroll(coordinatorLayout, child, target, type);

//        // 然后根据我们自己的逻辑来决定状态
//        int currentTop = child.getTop();
//        int parentHeight = coordinatorLayout.getHeight();
//        float halfExpandedRatio = getHalfExpandedRatio();
//        int halfExpandedHeight = (int) (parentHeight * halfExpandedRatio);
//
//        int targetState;
//
//        // 决策逻辑：如果当前视图顶部位置大于半展开高度，就去折叠状态
//        // 否则，就去半展开状态
//        if (currentTop > halfExpandedHeight) {
//            targetState = STATE_COLLAPSED; // 去折叠(Peek)状态
//        } else {
//            targetState = STATE_HALF_EXPANDED; // 回半展开状态
//        }
//
//        // 使用 setState 方法来实现状态切换
//        setState(targetState);
    }
}