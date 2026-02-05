package com.oortcloud.contacts.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @filename:
 * @function： 基础Activity，统一处理水印、EventBus、Loading、Rx订阅管理
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/10 10:07
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    private EventBus eventBus;
    protected LoadingDialog mLoadingDialog;
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    private FastSharedPreferences spUser1 = FastSharedPreferences.get("USERPRIVACY_SAVE");
    // Rx订阅管理，防止内存泄漏
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏系统ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 加载布局（含水印）
        if (getRootView() == null) {
            setContentView(getLayoutId());
        } else {
            setContentView(getRootView());
        }

        // 初始化基础变量
        mContext = this;
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show(); // 初始显示加载弹窗，子类在initBundle中可dismiss

        // EventBus注册
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // 子类初始化逻辑
        initBundle(getIntent().getExtras());
        initView();
        initData();
        initEvent();
    }

    /**
     * 添加Rx订阅到管理容器，页面销毁时统一取消
     */
    protected void addDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            mCompositeDisposable.add(disposable);
        }
    }

    /**
     * 加载布局（含水印）
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // 加载基础布局（含水印容器）
        View baseView = LayoutInflater.from(this).inflate(R.layout.layout_base, null, false);
//        // 加载子类布局
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null, false);
//
//
//        // 绑定水印容器和内容容器
        FrameLayout flContainer = baseView.findViewById(R.id.flContainer);
        FrameLayout flWater = baseView.findViewById(R.id.flWater);
        flContainer.addView(contentView);
//
        // 初始化水印内容
        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> labels = new ArrayList<>();
        String blank = "                    ";
        String name = spUser.getString("name", "");
        String userprivacy = spUser1.getString("allowAtt", "") + "";
//
        // 根据隐私设置决定是否显示水印
        if (!TextUtils.isEmpty(userprivacy) && Integer.parseInt(userprivacy) != 0) {
            labels.add(TextUtils.isEmpty(name) ? getResources().getString(R.string.app_name) + blank : name + blank);
            labels.add(createTimeSdf1.format(new Date()));
        } else {
            labels.add(TextUtils.isEmpty(name) ? getResources().getString(R.string.app_name) + blank : "");
            labels.add("  ");
        }
//
//        // 设置水印背景
//        flWater.setBackground(new WaterMarkBg(this, labels, -30, 13));
        super.setContentView(baseView);


    }

    /**
     * 抽象方法：子类可重写返回自定义根布局（默认null，使用getLayoutId加载）
     */
    protected abstract View getRootView();

    /**
     * 抽象方法：子类布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 抽象方法：初始化Bundle参数
     */
    protected abstract void initBundle(Bundle bundle);

    /**
     * 抽象方法：初始化数据
     */
    protected abstract void initData();

    /**
     * 抽象方法：初始化视图
     */
    protected abstract void initView();

    /**
     * 抽象方法：初始化事件
     */
    protected abstract void initEvent();

    @Override
    protected void onDestroy() {
        // 1. 取消所有Rx订阅，防止内存泄漏
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        // 2. 注销EventBus
        if (eventBus != null && eventBus.isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 仅关闭页面，EventBus注销在onDestroy中统一处理
        finish();
    }
}