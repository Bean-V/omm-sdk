package com.oortcloud.contacts.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.adapter.HigherDepartmentAdapter;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.EventMessage;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.databinding.ActivitySearchContactOrgBinding;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.DeptUtils;
import com.oortcloud.contacts.utils.SortComparator;
import com.oortcloud.contacts.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @filename:
 * @author: zzj/@date: 2021/11/10 20:52
 * @version： v1.0
 * @function： 搜索（部门/用户）
 */
public class SearchActivity extends BaseActivity implements SearchView.SearchViewListener {
    // 日志标签
    private static final String TAG = "SearchActivity";
    // 视图控件
    private ImageView mBackView;
    private TextView mTitle;
    private TextView mConfirm;
    private SearchView mSearchView;
    private Button mBtnSearch;
    private RecyclerView mDepartPersonRV;
    private RecyclerView mDeptRv;
    private FrameLayout fl_empty;
    private TextView empty_tv;
    // 搜索关键词
    private CharSequence mSearchKey;
    // 数据与适配器
    private List<? extends Sort> mSortList = new ArrayList<>();
    private DepartAndUserAdapter mDepAndUsrAdapter;
    private HigherDepartmentAdapter mHigherAdapter;
    private LinearLayoutManager mDeptLayoutManager;
    private List<Department> mDeptList = new ArrayList<>(); // 初始化空列表，避免空指针
    // ViewBinding
    private ActivitySearchContactOrgBinding mBinding;

    /**
     * 重写：返回null，使用getLayoutId加载布局（确保水印生效）
     */
    @Override
    protected View getRootView() {
        return null;
    }

    /**
     * 布局ID
     */
    @Override
    protected int getLayoutId() {


        return R.layout.activity_search_contact_org;
    }

    /**
     * 初始化Bundle参数
     */
    @Override
    protected void initBundle(Bundle bundle) {
        // 初始加载弹窗：页面初始化完成后隐藏
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 初始化数据（无额外数据初始化，留空）
     */
    @Override
    protected void initData() {

    }

    /**
     * 初始化视图
     */
    @Override
    protected void initView() {


        // 1. 初始化ViewBinding（绑定根布局）
        View v = findViewById(R.id.root_layout);
        mBinding = ActivitySearchContactOrgBinding.bind(v);
        // 2. 绑定控件
        mBackView = mBinding.ivTitle.imgBack;
        mTitle = mBinding.ivTitle.tvTitle;
        mConfirm = mBinding.ivTitle.confirmBtn;
        mSearchView = mBinding.searchView;
        mBtnSearch = mBinding.btnSearch;
        mDepartPersonRV = mBinding.rvHailFellow;
        mDeptRv = mBinding.rvDepart;
        fl_empty = mBinding.flEmpty;
        empty_tv = mBinding.emptyTv;

        // 3. 初始化标题栏
        mConfirm.setVisibility(View.INVISIBLE);
        mTitle.setText(getString(R.string.search_org));

        // 4. 初始化“部门/用户”RecyclerView（垂直列表）
        mDepartPersonRV.setLayoutManager(new GridLayoutManager(mContext, 1));
        mDepAndUsrAdapter = new DepartAndUserAdapter(mContext, mSortList, Constants.SEARCH);
        mDepartPersonRV.setAdapter(mDepAndUsrAdapter);

        // 5. 初始化“上级部门”RecyclerView（水平列表）
        mDeptLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mDeptRv.setLayoutManager(mDeptLayoutManager);
        mHigherAdapter = new HigherDepartmentAdapter(mDeptList);
        mDeptRv.setAdapter(mHigherAdapter);

        // 6. 初始化“部门/用户”列表点击事件
        mDepAndUsrAdapter.setOnItemClickListener(new DepartAndUserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 点击部门：加载该部门的子部门和用户
                if (mSortList.get(position) instanceof Department) {
                    Department dept = (Department) mSortList.get(position);
                    String deptCode = dept.getOort_dcode();
                    if (TextUtils.isEmpty(deptCode)) {
                        return;
                    }

                    // 显示加载弹窗
                    mLoadingDialog.show();

                    // ① 加载部门详情（更新上级部门列表）
                    Disposable deptInfoDisposable = HttpRequestCenter.getDeptInfo(deptCode)
                            .subscribeWith(new RxBusSubscriber<String>() {
                                @Override
                                protected void onEvent(String s) {
                                    try {
                                        Result<Data<Department>> result = new Gson().fromJson(
                                                s, new TypeToken<Result<Data<Department>>>() {}.getType()
                                        );
                                        if (result != null && result.isOk() && result.getData() != null) {
                                            // 刷新上级部门列表
                                            flushDepartNameList(result.getData().getDeptInfo());
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "解析部门详情异常：" + e.getMessage());
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    Log.e(TAG, "获取部门详情失败：" + e.getMessage());
                                    if (mLoadingDialog.isShowing()) {
                                        mLoadingDialog.dismiss();
                                    }
                                }
                            });
                    addDisposable(deptInfoDisposable);

                    // ② 加载子部门和用户
                    Disposable deptUserDisposable = HttpRequestCenter.post(deptCode, 1)
                            .subscribeWith(new RxBusSubscriber<String>() {
                                @Override
                                protected void onEvent(String s) {
                                    try {
                                        Result<Data> result = new Gson().fromJson(
                                                s, new TypeToken<Result<Data>>() {}.getType()
                                        );
                                        if (result != null && result.isOk() && result.getData() != null) {
                                            List<Sort> sortList = new ArrayList<>();
                                            Data data = result.getData();

                                            // 添加子部门（排序）
                                            if (data.getDept() != null && !data.getDept().isEmpty()) {
                                                Collections.sort(data.getDept(), new SortComparator());
                                                sortList.addAll(data.getDept());
                                            }

                                            // 添加用户（排序）
                                            if (data.getUser() != null && !data.getUser().isEmpty()) {
                                                Collections.sort(data.getUser(), new SortComparator());
                                                sortList.addAll(data.getUser());
                                            }

                                            // 更新列表数据
                                            mSortList = sortList;
                                            mDepAndUsrAdapter.updateList(mSortList);
                                            setEmptyVisibility(!mSortList.isEmpty());
                                        } else {
                                            setEmptyVisibility(false);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "解析部门用户异常：" + e.getMessage());
                                        setEmptyVisibility(false);
                                    } finally {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    Log.e(TAG, "获取部门用户失败：" + e.getMessage());
                                    setEmptyVisibility(false);
                                    if (mLoadingDialog.isShowing()) {
                                        mLoadingDialog.dismiss();
                                    }
                                }
                            });
                    addDisposable(deptUserDisposable);

                    // ③ 触发部门树加载（原有逻辑保留）
                    HttpResult.getDeptAndUserTree(deptCode, 1, Constants.DEPT_USER);
                }
            }

            @Override
            public void onCheckItemClick(int statu, UserInfo user) {
                // 勾选用户事件（原有逻辑保留，无修改）
            }

            @Override
            public void onItemTagDelClick(int position, UserInfo user) {
                // 删除用户标签事件（原有逻辑保留，无修改）
            }
        });
    }

    /**
     * 初始化事件
     */
    @Override

    protected void initEvent() {
        // 1. 返回按钮：关闭页面
        mBackView.setOnClickListener(v -> finish());

        // 2. SearchView：监听搜索文本变化
        mSearchView.setSearchViewListener(this);

        // 3. 搜索按钮：执行搜索逻辑
        mBtnSearch.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mSearchKey)) {
                return;
            }

            // 显示加载弹窗
            mLoadingDialog.show();
            // 隐藏上级部门列表
            mDeptRv.setVisibility(View.GONE);
            // 重置标题
            mTitle.setText(getString(R.string.search_org));

            // 执行搜索请求
            Disposable searchDisposable = HttpRequestCenter.getDistList(mSearchKey.toString())
                    .subscribeWith(new RxBusSubscriber<String>() {
                        @Override
                        protected void onEvent(String s) {
                            try {
                                // 清空搜索框（关闭软键盘）
                                mSearchView.clear(true);
                                // 解析搜索结果
                                Result<Data> result = new Gson().fromJson(
                                        s, new TypeToken<Result<Data>>() {}.getType()
                                );

                                if (result != null && result.isOk() && result.getData() != null) {
                                    List<Sort> searchResult = new ArrayList<>();
                                    Data data = result.getData();

                                    // 添加部门结果
                                    if (data.getDept() != null && !data.getDept().isEmpty()) {
                                        searchResult.addAll(data.getDept());
                                    }

                                    // 添加用户结果
                                    if (data.getUser() != null && !data.getUser().isEmpty()) {
                                        searchResult.addAll(data.getUser());
                                    }

                                    // 更新列表
                                    mSortList = searchResult;
                                    mDepAndUsrAdapter.updateList(mSortList);
                                    setEmptyVisibility(!mSortList.isEmpty());
                                } else {
                                    setEmptyVisibility(false);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "解析搜索结果异常：" + e.getMessage());
                                setEmptyVisibility(false);
                            } finally {
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            Log.e(TAG, "搜索失败：" + e.toString());
                            setEmptyVisibility(false);
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                        }
                    });
            addDisposable(searchDisposable);
        });
    }

    /**
     * EventBus订阅：接收搜索结果事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        // 过滤无效事件
        if (event == null || !Constants.SEARCH.equals(event.getDataType())) {
            return;
        }

        // 更新列表数据
        mSortList = event.getList();
        if (mSortList != null && !mSortList.isEmpty()) {
            mDepAndUsrAdapter.updateList(mSortList);
            setEmptyVisibility(true);
        } else {
            setEmptyVisibility(false);
        }
    }

    /**
     * 控制空状态显示
     * @param hasData true：有数据（显示列表，隐藏空布局）；false：无数据（显示空布局，隐藏列表）
     */
    private void setEmptyVisibility(boolean hasData) {
        if (hasData) {
            mDepartPersonRV.setVisibility(View.VISIBLE);
            fl_empty.setVisibility(View.GONE);
        } else {
            empty_tv.setText(getString(R.string.unfind_org));
            fl_empty.setVisibility(View.VISIBLE);
            mDepartPersonRV.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新上级部门列表（显示当前部门的层级路径）
     */
    private void flushDepartNameList(Department department) {
        if (department == null) {
            return;
        }

        // 解析部门路径，生成上级部门列表
        mDeptList = DeptUtils.splitDepartment(department.getOort_dpath(), department.getOort_dcodepath());
        if (mDeptList == null) {
            mDeptList = new ArrayList<>();
        }

        // 更新适配器数据并滚动到最后一项（当前部门）
        mHigherAdapter.setNewData(mDeptList);
        mHigherAdapter.notifyDataSetChanged();
        if (mDeptLayoutManager != null && mDeptList.size() > 0) {
            mDeptRv.smoothScrollToPosition(mDeptList.size() - 1);
        }

        // 更新标题为当前部门名称
        if (!mDeptList.isEmpty()) {
            mTitle.setText(mDeptList.get(mDeptList.size() - 1).getOort_dname());
        }
    }

    /**
     * SearchView监听：搜索文本变化
     */
    @Override
    public void onSearch(CharSequence text) {
        mSearchKey = text;
        // 文本非空显示搜索按钮，空文本隐藏
        if (!TextUtils.isEmpty(text)) {
            mBtnSearch.setVisibility(View.VISIBLE);
        } else {
            mBtnSearch.setVisibility(View.GONE);
        }
    }
}