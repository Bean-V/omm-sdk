package com.oort.weichat.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oort.weichat.R;

/**
 * 火箭消息动画管理器
 */
public class RocketAnimationManager {
    
    private Activity activity;
    private ViewGroup rootView;
    private View rocketAnimationView;
    
    public RocketAnimationManager(Activity activity) {
        this.activity = activity;
        this.rootView = activity.findViewById(android.R.id.content);
    }
    
    /**
     * 播放火箭消息动画
     */
    public void playRocketAnimation() {
        if (rocketAnimationView != null) {
            // 如果已有动画在播放，先移除
            rootView.removeView(rocketAnimationView);
        }
        // 创建动画布局
        createRocketAnimationView();
        // 开始动画
        startRocketAnimation();
    }
    
    /**
     * 播放接收火箭消息动画
     */
    public void playRocketReceiveAnimation() {
        android.util.Log.e("RocketAnimation", "开始播放接收火箭动画");
        
        if (rocketAnimationView != null) {
            // 如果已有动画在播放，先移除
            rootView.removeView(rocketAnimationView);
        }
        
        // 创建接收动画布局
        createRocketReceiveAnimationView();
        
        // 开始接收动画
        startRocketReceiveAnimation();
    }
    
    /**
     * 创建火箭动画视图
     */
    private void createRocketAnimationView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        rocketAnimationView = inflater.inflate(R.layout.rocket_animation_layout, rootView, false);
        rootView.addView(rocketAnimationView);
    }
    
    /**
     * 创建接收火箭动画视图
     */
    private void createRocketReceiveAnimationView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        rocketAnimationView = inflater.inflate(R.layout.rocket_receive_animation_layout, rootView, false);
        rootView.addView(rocketAnimationView);
    }
    
    /**
     * 开始火箭动画
     */
    private void startRocketAnimation() {
        android.util.Log.e("RocketAnimation", "开始创建火箭动画");
        
        ImageView rocketIcon = rocketAnimationView.findViewById(R.id.rocket_icon);
        
        if (rocketIcon == null) {
            android.util.Log.e("RocketAnimation", "火箭图标为空！");
            return;
        }

        // 获取屏幕高度
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        
        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        
        // 1. 火箭极速起飞 - 主要动画，完全飞出屏幕
        ObjectAnimator rocketFlyUpAnimator = ObjectAnimator.ofFloat(rocketIcon, "translationY", 0, -screenHeight - 100);
        rocketFlyUpAnimator.setDuration(800); // 大幅缩短到0.8秒
        rocketFlyUpAnimator.setInterpolator(new AccelerateInterpolator(3.0f)); // 进一步增强加速效果
        
        // 2. 火箭起飞时放大效果 - 快速放大
        ObjectAnimator rocketScaleXAnimator = ObjectAnimator.ofFloat(rocketIcon, "scaleX", 1f, 2.0f);
        ObjectAnimator rocketScaleYAnimator = ObjectAnimator.ofFloat(rocketIcon, "scaleY", 1f, 2.0f);
        rocketScaleXAnimator.setDuration(800);
        rocketScaleYAnimator.setDuration(800);
        rocketScaleXAnimator.setInterpolator(new AccelerateInterpolator(3.0f));
        rocketScaleYAnimator.setInterpolator(new AccelerateInterpolator(3.0f));

        // 8. 整体淡出动画 - 火箭飞走后立即淡出
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(rocketAnimationView, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(150); // 大幅缩短淡出时间到0.15秒
        fadeOutAnimator.setStartDelay(1000); // 火箭起飞完成后立即开始淡出
        fadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束后移除视图
                if (rocketAnimationView != null && rocketAnimationView.getParent() != null) {
                    rootView.removeView(rocketAnimationView);
                    rocketAnimationView = null;
                }
            }
        });
        
        // 组合所有动画
        animatorSet.playTogether(
            rocketFlyUpAnimator,
            rocketScaleXAnimator,
            rocketScaleYAnimator,
            fadeOutAnimator
        );
        
        // 开始动画
        animatorSet.start();
        
        // 震动效果 - 火箭极速起飞时震动
        android.os.Vibrator vibrator = (android.os.Vibrator) activity.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 100, 50, 150, 50, 100}; // 快速震动模式
            vibrator.vibrate(pattern, -1);
        }
    }
    
    /**
     * 开始接收火箭动画
     */
    private void startRocketReceiveAnimation() {
        android.util.Log.e("RocketAnimation", "开始创建接收火箭动画");
        
        ImageView rocketIcon = rocketAnimationView.findViewById(R.id.rocket_icon);
        
        if (rocketIcon == null) {
            android.util.Log.e("RocketAnimation", "接收火箭图标为空！");
            return;
        }
        
        android.util.Log.e("RocketAnimation", "接收火箭图标找到，开始动画");
        
        // 获取屏幕高度
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        
        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        
        // 1. 火箭从右上角向下飞 - 接收动画
        ObjectAnimator rocketFlyDownAnimator = ObjectAnimator.ofFloat(rocketIcon, "translationY", 0, screenHeight + 100);
        rocketFlyDownAnimator.setDuration(800); // 0.8秒
        rocketFlyDownAnimator.setInterpolator(new AccelerateInterpolator(3.0f)); // 极强加速效果
        
        // 3. 火箭接收时放大效果
        ObjectAnimator rocketScaleXAnimator = ObjectAnimator.ofFloat(rocketIcon, "scaleX", 1f, 2.0f);
        ObjectAnimator rocketScaleYAnimator = ObjectAnimator.ofFloat(rocketIcon, "scaleY", 1f, 2.0f);
        rocketScaleXAnimator.setDuration(800);
        rocketScaleYAnimator.setDuration(800);
        rocketScaleXAnimator.setInterpolator(new AccelerateInterpolator(3.0f));
        rocketScaleYAnimator.setInterpolator(new AccelerateInterpolator(3.0f));
        
        // 3. 整体淡出动画 - 火箭飞走后立即淡出
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(rocketAnimationView, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(150); // 0.15秒淡出
        fadeOutAnimator.setStartDelay(1000); // 1秒后开始淡出
        fadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束后移除视图
                if (rocketAnimationView != null && rocketAnimationView.getParent() != null) {
                    rootView.removeView(rocketAnimationView);
                    rocketAnimationView = null;
                }
            }
        });
        
        // 组合所有动画
        animatorSet.playTogether(
            rocketFlyDownAnimator,
            rocketScaleXAnimator,
            rocketScaleYAnimator,
            fadeOutAnimator
        );
        
        // 开始动画
        animatorSet.start();
        
        // 震动效果 - 火箭接收时震动
        android.os.Vibrator vibrator = (android.os.Vibrator) activity.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 100, 50, 150, 50, 100}; // 快速震动模式
            vibrator.vibrate(pattern, -1);
        }
    }
    
    /**
     * 停止动画
     */
    public void stopAnimation() {
        if (rocketAnimationView != null && rocketAnimationView.getParent() != null) {
            rootView.removeView(rocketAnimationView);
            rocketAnimationView = null;
        }
    }
}
