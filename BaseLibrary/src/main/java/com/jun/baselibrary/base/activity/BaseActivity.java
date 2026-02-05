package com.jun.baselibrary.base.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jun.baselibrary.ioc.ViewUtils;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/25 18:08
 * Version 1.0
 * Description：Activity基类 模板设计模式 针对MVC架构
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected  void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        setContentView(initLayout());
        //IOC注入
        ViewUtils.inject(this);

        //初始化导航栏
        initTitle();

        //初始化界面
        initView();

        //初始化数据
        initData();
    }
    //初始化布局
    protected abstract int initLayout();
    //初始化title
    protected abstract void initTitle();
    //初始化界面
    protected abstract void initView();
    //初始化数据
    protected abstract void initData();

    //启动Activity
    protected void startActivity(Class<?> clazz){
        startActivity(new Intent(this, clazz));
    }

    //只放通用的方法，基本上每个Activity都会使用
    //如果两个或两个以上使用，封装为工具类
    //因为类加载-->方法加载-->都会加载进内存，而且使用频率不高，占有内存，性能方面就可能存在问题
    //要预留一层，以备功能扩展
}
