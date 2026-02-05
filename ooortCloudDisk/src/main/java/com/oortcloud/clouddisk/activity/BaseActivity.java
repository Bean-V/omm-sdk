package com.oortcloud.clouddisk.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.permission.PmsManager;

import org.greenrobot.eventbus.EventBus;


/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/10 10:07
 */
public abstract class BaseActivity extends FragmentActivity {

    protected Context mContext;
    protected EventBus eventBus;
    // 声明ViewBinding成员变量（子类需初始化）
    protected ViewBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarLight(true);

        mContext = this;

        // 核心修改1：通过ViewBinding加载布局并设置为内容视图，替代setContentView(getLayoutId())
        binding = getViewBinding();
        if (binding != null) {
            setContentView(binding.getRoot()); // 用ViewBinding的根视图作为界面内容
        }

        // 权限请求逻辑保留
        if (!PmsManager.hasPermission(this, PmsManager.getPermission())) {
            PmsManager.requestPms(this, 1, PmsManager.getPermission());
        }

        initBundle(getIntent().getExtras());
        initActionBar();
        initView(); // 此时initView中使用的binding已关联屏幕视图
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus = EventBus.getDefault();
        initData();
    }

    // 核心修改2：删除getLayoutId()抽象方法（不再通过布局ID加载视图）

    protected abstract void initBundle(Bundle bundle);

    protected abstract void initActionBar();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent(View v);

    // 子类需实现此方法，返回对应的ViewBinding实例
    protected abstract ViewBinding getViewBinding();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            BaseApplication.TOKEN = intent.getExtras().getString("token");
            BaseApplication.UUID = intent.getExtras().getString("uuid");
        }
    }

    protected void setStatusBarLight(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (light) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
                window.setStatusBarColor(getResources().getColor(R.color.main_color));
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                if (light) {
                    window.setStatusBarColor(getResources().getColor(R.color.main_color));
                } else {
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}