package com.oortcloud.appstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.TypeAppAllAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Constants;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ActivityTypeAppAllLayoutBinding;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.widget.RecyclerRefreshLayout;

import java.util.List;

public class TypeAppAllActivity extends BaseActivity implements RecyclerRefreshLayout.SuperRefreshLayoutListener {

    private ActivityTypeAppAllLayoutBinding binding;
    private RecyclerView mTpeAppAllRecycler;
    private RecyclerRefreshLayout mRefreshLayout;

    private static final String TITLE_KEY = "type_key";
    private static final String CLASSIFY_KEY = "classify_key";
    private static final String ITEM_KET = "item_key";
    private static final String APP_UID = "app_uid";

    private TypeAppAllAdapter mAllAppAdapter;
    private String mClassifyUID;
    private int mItem;
    private String appUid;
    private boolean isLoading = false;

    @Override
    protected ViewBinding getViewBinding() {
        binding = ActivityTypeAppAllLayoutBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle == null) {
            Toast.makeText(mContext, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTitle.setText(bundle.getString(TITLE_KEY));
        mBtnItem.setVisibility(View.GONE);
        mClassifyUID = bundle.getString(CLASSIFY_KEY);
        mItem = bundle.getInt(ITEM_KET, 0);
        appUid = bundle.getString(APP_UID);
    }

    @Override
    protected void initView() {
        mTpeAppAllRecycler = binding.recyclerView;
        mRefreshLayout = binding.refreshLayout;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTpeAppAllRecycler.setLayoutManager(layoutManager);

        if (mItem == 3) {
            mAllAppAdapter = new TypeAppAllAdapter(mContext, Constants.RANKING);
        } else {
            mAllAppAdapter = new TypeAppAllAdapter(mContext);
        }
        mTpeAppAllRecycler.setAdapter(mAllAppAdapter);

        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setColorSchemeResources(
                R.color.fresh_color1, R.color.fresh_color2, R.color.fresh_color3,
                R.color.fresh_color4, R.color.fresh_color5, R.color.fresh_color6);

        binding.typeAppLl.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        // 直接使用BaseActivity中的mLoadDialog，不依赖showLoadDialog方法
        if (mLoadDialog != null && !isFinishing()) {
            runOnUiThread(() -> mLoadDialog.show());
        }
        new Handler(Looper.getMainLooper()).post(this::onRefreshing);
    }

    @Override
    protected void initEvent() {
        mImgBack.setOnClickListener(v -> finish());
    }

    private void getAllAppRecycler() {
        if (isLoading) return;
        isLoading = true;

        try {
            if (mItem != 0) {
                getRecommendMore();
            } else if (!TextUtils.isEmpty(appUid)) {
                getPrincipalMore();
            } else {
                getClassifyAppMore();
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleLoadError("数据加载异常");
        }
    }

    private void getClassifyAppMore() {
        if (TextUtils.isEmpty(mClassifyUID)) {
            HttpRequestCenter.monthNewApp(1, 50)
                    .subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            setAppList(s);
                        }

                        @Override
                        public void onError(Throwable e) {
                            handleLoadError("获取最新应用失败");
                            e.printStackTrace();
                        }
                    });
        } else {
            HttpRequestCenter.postClassifyAppMore(mClassifyUID, 1, 50)
                    .subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            setAppList(s);
                        }

                        @Override
                        public void onError(Throwable e) {
                            handleLoadError("获取分类应用失败");
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void getRecommendMore() {
        HttpRequestCenter.postRecommendMore(mItem, 1)
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        setAppList(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleLoadError("获取推荐应用失败");
                        e.printStackTrace();
                    }
                });
    }

    private void getPrincipalMore() {
        HttpRequestCenter.principalAPPMore(1, appUid, 50)
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        setAppList(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleLoadError("获取责任人应用失败");
                        e.printStackTrace();
                    }
                });
    }

    private void setAppList(String s) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                if (isDestroyed() || isFinishing()) {
                    return;
                }

                Result<Data<AppInfo>> result = new Gson().fromJson(s,
                        new TypeToken<Result<Data<AppInfo>>>(){}.getType());

                if (result != null && result.isok()) {
                    mRefreshLayout.setCanLoadMore(false);
                    mRefreshLayout.onComplete();
                    List<AppInfo> appInfoList = result.getData().getApp_list();

                    if (appInfoList != null && !appInfoList.isEmpty()) {
                        mAllAppAdapter.setData(appInfoList);
                        mAllAppAdapter.setState(0, true);
                        binding.typeAppLl.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(mContext, "暂无应用数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "数据解析失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "数据处理异常", Toast.LENGTH_SHORT).show();
            } finally {
                // 直接操作mLoadDialog，不依赖dismissLoadDialog方法
                if (mLoadDialog != null && !isFinishing()) {
                    runOnUiThread(() -> mLoadDialog.dismiss());
                }
                isLoading = false;
            }
        });
    }

    private void handleLoadError(String msg) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (isDestroyed() || isFinishing()) return;

            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            mRefreshLayout.onComplete();

            // 直接关闭加载对话框
            if (mLoadDialog != null && !isFinishing()) {
                runOnUiThread(() -> mLoadDialog.dismiss());
            }

            isLoading = false;
            binding.typeAppLl.setVisibility(View.VISIBLE);
        });
    }

    public static void actionStart(Context context, String type, String classifyUID, int item) {
        Intent intent = new Intent(context, TypeAppAllActivity.class);
        intent.putExtra(TITLE_KEY, type);
        intent.putExtra(CLASSIFY_KEY, classifyUID);
        intent.putExtra(ITEM_KET, item);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String type, String uid) {
        Intent intent = new Intent(context, TypeAppAllActivity.class);
        intent.putExtra(TITLE_KEY, type);
        intent.putExtra(APP_UID, uid);
        context.startActivity(intent);
    }

    @Override
    public void onRefreshing() {
        getAllAppRecycler();
    }

    @Override
    public void onLoadMore() {
        mAllAppAdapter.setState(9, true);
        mRefreshLayout.onComplete();
        isLoading = false;
    }

    @Override
    public void onScrollToBottom() {}

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
