package com.oortcloud.appstore.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.TypeAppAllAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ActivitySearchActivityBinding;
import com.oortcloud.appstore.db.SharedPreferenceManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.widget.HistoryLaberView;
import com.oortcloud.appstore.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 修复点：
 * 1. 验证SearchView初始化，添加日志排查
 * 2. 完善网络请求错误处理
 * 3. 修复历史记录点击逻辑
 * 4. 优化UI状态切换
 */
public class SearchActivity extends BaseActivity implements SearchView.SearchViewListener {
    private static final String TAG = "SearchActivity";
    private static final String HISTORY_KEY = "history_key";
    private SharedPreferenceManager mSharedManager;
    private HistoryLaberView mHistoryLaberView;
    private TextView mContent;
    private ImageView mDeleteImg;
    private RecyclerView mRV;
    private SearchView mSearchView;
    private ActivitySearchActivityBinding binding;

    private TypeAppAllAdapter mAllAppAdapter;
    private List<AppInfo> mAppList;
    private List<String> mHistoryList = new ArrayList<>();

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAppList != null && !mAppList.isEmpty()) {
            mAllAppAdapter.setData(mAppList);
            mRV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ActivitySearchActivityBinding getViewBinding() {
        binding = ActivitySearchActivityBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected int getLayoutId() {
        return 0; // 使用ViewBinding，无需布局ID
    }

    @Override
    protected void initBundle(Bundle bundle) {
        mSharedManager = SharedPreferenceManager.getInstance();
    }

    @Override
    protected void initView() {
        // 初始化控件
        mHistoryLaberView = binding.historyLabView;
        mContent = binding.tvCentont;
        mDeleteImg = binding.deleteImg;
        mRV = binding.recyclerView;
        mSearchView = binding.searchview;

        // 关键：验证SearchView是否初始化成功
        if (mSearchView == null) {
            Log.e(TAG, "SearchView初始化失败！请检查布局文件中的searchview控件");
            Toast.makeText(mContext, "搜索功能初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 初始化列表
        mRV.setLayoutManager(new LinearLayoutManager(mContext));
        mAllAppAdapter = new TypeAppAllAdapter(mContext);
        mRV.setAdapter(mAllAppAdapter);

        // 加载历史记录
        initHistory();
    }

    @Override
    protected void initEvent() {
        if (mSearchView == null) return;

        // 设置搜索监听器
        mSearchView.setSearchViewListener(this);

        // 清除历史记录
        mDeleteImg.setOnClickListener(v -> {
            mSharedManager.putString(HISTORY_KEY, "");
            initHistory();
            mHistoryLaberView.setVisibility(View.VISIBLE);
            mRV.setVisibility(View.GONE);
            mContent.setText(getString(R.string.search_str));
        });
    }

    @Override
    protected void initData() {
        // 初始显示历史记录
        if (!mHistoryList.isEmpty()) {
            mHistoryLaberView.setVisibility(View.VISIBLE);
        } else {
            mContent.setText("没有记录");
        }
    }

    /**
     * 初始化历史记录标签
     */
    private void initHistory() {
        getHistoryList();
        mHistoryLaberView.removeAllViews();

        if (mHistoryList.isEmpty()) {
            mDeleteImg.setVisibility(View.GONE);
            return;
        }

        mDeleteImg.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(15, 20, 15, 10);

        for (String history : mHistoryList) {
            View itemView = getLayoutInflater().inflate(R.layout.item_history_layout, null);
            TextView tvContent = itemView.findViewById(R.id.tv_content);
            tvContent.setText(history);
            mHistoryLaberView.addView(itemView, layoutParams);

            // 历史记录点击事件
            tvContent.setOnClickListener(v -> {
                String text = tvContent.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    onSearch(text); // 触发搜索
                }
            });
        }
    }

    @Override
    public void onRefreshAutoComplete(String text) {
        // 自动补全逻辑（按需实现）
    }

    @Override
    public void onSearch(final String text) {
        Log.d(TAG, "开始搜索：" + text);
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(mContext, getString(R.string.input_app_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载中
        mLoadDialog.show();

        // 发起搜索请求
        HttpRequestCenter.postSearch(text)
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        mLoadDialog.dismiss();
                        Log.d(TAG, "搜索结果：" + s);
                        try {
                            Result<Data<AppInfo>> result = new Gson().fromJson(
                                    s, new TypeToken<Result<Data<AppInfo>>>() {}.getType()
                            );
                            if (result.isok()) {
                                mAppList = result.getData().getApp_list();
                                updateSearchResult(text);
                                // 保存历史记录
                                if (!mHistoryList.contains(text)) {
                                    addHistory(text);
                                }
                            } else {
                                Toast.makeText(mContext, "搜索失败：" + result.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "数据解析失败", e);
                            Toast.makeText(mContext, "数据解析错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoadDialog.dismiss();
                        Log.e(TAG, "搜索请求失败", e);
                        Toast.makeText(mContext, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 更新搜索结果UI
     */
    private void updateSearchResult(String text) {
        if (mAppList != null && !mAppList.isEmpty()) {
            mRV.setVisibility(View.VISIBLE);
            mHistoryLaberView.setVisibility(View.GONE);
            mDeleteImg.setVisibility(View.GONE);
            mContent.setText(getString(R.string.search_text_res, text));
            mAllAppAdapter.setData(mAppList);
        } else {
            mRV.setVisibility(View.GONE);
            mHistoryLaberView.setVisibility(View.GONE);
            mDeleteImg.setVisibility(View.GONE);
            mContent.setText(getString(R.string.no_app_search));
        }
    }

    @Override
    public void onDelete() {
        // 清除输入时回调
        mContent.setText(getString(R.string.search_str));
        mHistoryLaberView.setVisibility(View.VISIBLE);
        mDeleteImg.setVisibility(View.VISIBLE);
        mRV.setVisibility(View.GONE);
        initHistory();
    }

    /**
     * 获取历史记录列表
     */
    private void getHistoryList() {
        mHistoryList.clear();
        String historyStr = mSharedManager.getString(HISTORY_KEY);
        if (!TextUtils.isEmpty(historyStr)) {
            String[] items = historyStr.split("&");
            for (String item : items) {
                if (!TextUtils.isEmpty(item)) { // 过滤空项
                    mHistoryList.add(item);
                }
            }
        }
    }

    /**
     * 添加历史记录
     */
    private void addHistory(String historyName) {
        String historyStr = mSharedManager.getString(HISTORY_KEY);
        if (TextUtils.isEmpty(historyStr)) {
            mSharedManager.putString(HISTORY_KEY, historyName);
        } else {
            mSharedManager.putString(HISTORY_KEY, historyStr + "&" + historyName);
        }
        initHistory(); // 刷新历史记录UI
    }

    @Override
    protected void onDestroy() {
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
        super.onDestroy();
    }
}
