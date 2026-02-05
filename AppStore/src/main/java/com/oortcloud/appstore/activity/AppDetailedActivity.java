package com.oortcloud.appstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.TableViewPagerAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Comment;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Grade;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ActivityAppDetailedLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.fragment.DetailedCommentPageFragment;
import com.oortcloud.appstore.fragment.DetailedIntroducePageFragment;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.widget.DownloadProgressButton;
import com.oortcloud.appstore.widget.listener.DownloadListener;

import java.util.ArrayList;
import java.util.List;

public class AppDetailedActivity extends BaseActivity {
    private TableViewPagerAdapter mAdapter;
    private ActivityAppDetailedLayoutBinding binding; // 仅保留ViewBinding对象
    private static final String OBJECT_KEY = "object_key";
    private AppInfo appInfo;

    // 提供ViewBinding（核心）
    @Override
    protected ActivityAppDetailedLayoutBinding getViewBinding() {
        binding = ActivityAppDetailedLayoutBinding.inflate(getLayoutInflater());
        return binding;
    }

    // 使用ViewBinding时返回0，避免布局资源冲突
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        // 初始化标题（使用BaseActivity的标题栏控件）
        mTitle.setText(getString(R.string.app_info));
        mBtnItem.setVisibility(View.GONE);

        // 获取传递的应用信息
        Intent intent = getIntent();
        if (intent != null) {
            appInfo = (AppInfo) intent.getSerializableExtra(OBJECT_KEY);
        }

        // 校验appInfo非空（避免空指针）
        if (appInfo == null) {
            finish(); // 数据异常时关闭页面
            return;
        }

        // 检查应用状态，控制安装按钮显示
        AppInfo applyApp = DataInit.getAppinfo(appInfo.getApppackage());
        if (applyApp != null && applyApp.getApply_status() == 0) {
            binding.tvInstall.setVisibility(View.GONE);
        }

        // 初始化ViewPager适配器
        mAdapter = new TableViewPagerAdapter(getSupportFragmentManager());
        mAdapter.reset(getFragments());

        // 加载评论数据并设置Tab
        HttpRequestCenter.replySystemList(1, appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Data<Comment>> result = new Gson().fromJson(s,
                        new TypeToken<Result<Data<Comment>>>(){}.getType());
                String[] tabTitles = {
                        getString(R.string.introduce_str),
                        getString(R.string.comment_str)
                };

                if (result.isok() && result.getData() != null) {
                    List<?> commentList = result.getData().getLists();
                    if (commentList != null && !commentList.isEmpty()) {
                        int count = commentList.size();
                        tabTitles[1] = getString(R.string.comment_str) + "(" +
                                (count > 999 ? "999+" : count) + ")";
                    }
                }

                mAdapter.reset(tabTitles);
                binding.detailedViewPager.setAdapter(mAdapter);
                binding.detailedTabLayout.setupWithViewPager(binding.detailedViewPager);
            }
        });
    }

    @Override
    protected void initData() {
        // 加载应用信息（直接通过binding访问控件）
        Glide.with(this).load(appInfo.getIcon_url()).into(binding.appIcon);
        binding.tvAppName.setText(appInfo.getApplabel());
        binding.tvDownloadNum.setText(getString(R.string.download) + "  " +
                appInfo.getInstall_num() + getString(R.string.times));
        binding.tvAppSynopsis.setText(appInfo.getOneword());

        // 加载评分数据
        HttpRequestCenter.getGrade(appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                try {
                    Result<Grade> result = new Gson().fromJson(s,
                            new TypeToken<Result<Grade>>(){}.getType());
                    if (result.isok() && result.getData() != null) {
                        setScore(result.getData().getScore());
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // 捕获JSON解析异常
                }
            }
        });
    }

    @Override
    protected void initView() {
        // 根据终端类型设置安装按钮状态
        switch (appInfo.getTerminal()) {
            case 0:
                if (AppInfoManager.getInstance().isContains(appInfo)) {
                    binding.tvInstall.setState(DownloadProgressButton.STATE_OPEN);
                    binding.tvInstall.setCurrentText(getString(R.string.open_str));
                } else {
                    binding.tvInstall.setState(DownloadProgressButton.STATE_NORMAL);
                    binding.tvInstall.setCurrentText(getString(R.string.load_str));
                }
                break;
            case 1:
            case 2:
            case 6:
                binding.tvInstall.setState(DownloadProgressButton.STATE_OPEN);
                binding.tvInstall.setCurrentText(getString(R.string.open_str));
                break;
        }
    }

    @Override
    protected void initEvent() {
        // 恢复下载进度监听
        if (!DownloadListener.tmpListeners.isEmpty() &&
                DownloadListener.contain(appInfo.getUid())) {
            DownloadListener listener = DownloadListener.getListener(appInfo.getUid());
            listener.setProgressButton(binding.tvInstall);
        }

        // 安装/打开按钮点击事件
        binding.tvInstall.setOnClickListener(v ->
                AppEventUtil.onClick(appInfo, new DownloadListener(appInfo, binding.tvInstall, "")));

        // 返回按钮事件（BaseActivity已处理，可保留冗余处理）
        mImgBack.setOnClickListener(v -> finish());
    }

    // 初始化ViewPager碎片
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DetailedIntroducePageFragment.instantiate(appInfo));
        fragments.add(DetailedCommentPageFragment.instantiate(appInfo));
        return fragments;
    }

    // 启动当前Activity的方法
    public static void actionStart(Context context, AppInfo appInfo) {
        Intent intent = new Intent(context, AppDetailedActivity.class);
        intent.putExtra(OBJECT_KEY, appInfo);
        context.startActivity(intent);
    }

    // 设置评分
    public void setScore(float score) {
        binding.tvScore.setText(String.valueOf(score));
        binding.ratingbarview.setStar(score);
    }

    @Override
    protected void onDestroy() {
        binding = null; // 释放ViewBinding，避免内存泄漏
        super.onDestroy();
    }
}
