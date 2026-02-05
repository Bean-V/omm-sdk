package com.oortcloud.appstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.AppStoreChangeMessage;
import com.oortcloud.appstore.adapter.GridViewAdapter;
import com.oortcloud.appstore.adapter.ModuleTypeClassifyAppAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ClassifyInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.dailog.EditDialog;
import com.oortcloud.appstore.dailog.InputDialog;
import com.oortcloud.appstore.databinding.ActivityEditorLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.DragGridView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块编辑Activity（适配BaseActivity的布局结构）
 */
public class EditorActivity extends BaseActivity implements GridViewAdapter.OnItemListener {

    // ViewBinding对象（核心：替代ButterKnife控件引用）
    private ActivityEditorLayoutBinding binding;

    private static final String OBJECT_KEY = "object_key";
    private ModuleInfo mModuleInfo;
    private List<ClassifyInfo> mTypeClassify;
    private boolean isClick;
    private List<AppInfo> mAppList = new ArrayList<>();
    private GridViewAdapter mGridViewAdapter;
    private AppInfoManager mAppInfoManager;
    private ModuleTableManager moduleTableManager;
    private int home_type; // 0-非首页显示,1-首页显示

    // 实现BaseActivity的抽象方法：提供ViewBinding
    @Override
    protected ViewBinding getViewBinding() {
        // 初始化绑定对象（此时还未添加到布局）
        binding = ActivityEditorLayoutBinding.inflate(getLayoutInflater());
        return binding;
    }

    // 实现BaseActivity的抽象方法：不使用传统布局ID（返回0）
    @Override
    protected int getLayoutId() {
        return 0; // 因为使用ViewBinding，无需布局ID
    }

    @Override
    protected void initBundle(Bundle bundle) {
        // 初始化标题（BaseActivity的标题栏控件）
        mTitle.setText(getString(R.string.edit));
        mBtnItem.setVisibility(View.GONE);

        // 获取传递的模块信息
        if (bundle != null) {
            mModuleInfo = (ModuleInfo) bundle.getSerializable(OBJECT_KEY);
        }
        mAppInfoManager = AppInfoManager.getInstance();
        moduleTableManager = ModuleTableManager.getInstance();
    }

    @Override
    protected void initView() {
        // 初始化模块信息显示
        if (mModuleInfo != null) {
            // 通过ViewBinding访问当前布局的控件
            if (mModuleInfo.getHomepage_type() == 1) {
                home_type = 1;
                binding.checkbox.setChecked(true);
            } else {
                home_type = 0;
                binding.checkbox.setChecked(false);
            }
            binding.tvModuleName.setText(mModuleInfo.getModule_name());
        }

        // 初始化RecyclerView布局管理器
        binding.editRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    protected void initData() {
        // 加载分类列表和应用列表
        if (mTypeClassify != null) {
            binding.editRecyclerView.setAdapter(new ModuleTypeClassifyAppAdapter(mContext, mTypeClassify));
        } else {
            getClassifyList();
        }
        getTableAPP();
    }

    @Override
    protected void initEvent() {
        // 拖拽GridView事件
        binding.dragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {
            @Override
            public void onChange(int from, int to) {
                if (mAppList != null) {
                    AppInfo appInfo = mAppList.get(from);
                    mAppList.set(from, mAppList.get(to));
                    mAppList.set(to, appInfo);
                    mGridViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinsh() {}
        });

        // TabLayout选择事件
        binding.editorTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isClick = true;
                int position = tab.getPosition();
                LinearLayoutManager l = (LinearLayoutManager) binding.editRecyclerView.getLayoutManager();
                int firstPosition = l.findFirstVisibleItemPosition();
                int lastPosition = l.findLastVisibleItemPosition();
                if (position > lastPosition) {
                    binding.editRecyclerView.smoothScrollToPosition(position);
                } else if (position < firstPosition) {
                    binding.editRecyclerView.smoothScrollToPosition(position);
                } else {
                    int top = binding.editRecyclerView.getChildAt(position - firstPosition).getTop();
                    binding.editRecyclerView.smoothScrollBy(0, top);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // RecyclerView滚动事件
        binding.editRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) isClick = false;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isClick) {
                    LinearLayoutManager l = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstPosition = l.findFirstVisibleItemPosition();
                    binding.editorTabLayout.setScrollPosition(firstPosition, 0f, true);
                }
            }
        });

        // 首页显示复选框事件
        binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            home_type = b ? 1 : 0;
        });

        // 模块名称修改事件
        binding.tvModuleName.setOnClickListener(view -> {
            new EditDialog(mContext, getString(R.string.change_module_name),
                    binding.tvModuleName.getText().toString().trim(),
                    new InputDialog.DialogClickListener() {
                        @Override
                        public void onDialogClick() {}

                        @Override
                        public void onDialogClick(String moduleName) {
                            binding.tvModuleName.setText(moduleName);
                        }
                    }, null).show();
        });
    }

    // 启动当前Activity的方法
    public static void actionStart(Context context, ModuleInfo moduleInfo) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(OBJECT_KEY, moduleInfo);
        context.startActivity(intent);
    }

    // 获取分类列表
    private void getClassifyList() {
        if (mLoadDialog != null) mLoadDialog.show();
        HttpRequestCenter.meduleClassifyList().subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Data<ClassifyInfo>> result = new Gson().fromJson(s,
                        new TypeToken<Result<Data<ClassifyInfo>>>() {}.getType());

                if (result.isok()) {
                    mTypeClassify = result.getData().getList();
                    if (mTypeClassify != null && mTypeClassify.size() > 0) {
                        // 添加分类标签到TabLayout
                        for (ClassifyInfo classifyInfo : mTypeClassify) {
                            binding.editorTabLayout.addTab(
                                    binding.editorTabLayout.newTab().setText(classifyInfo.getName()));
                        }
                        // 设置RecyclerView适配器
                        binding.editRecyclerView.setAdapter(
                                new ModuleTypeClassifyAppAdapter(mContext, mTypeClassify));
                    } else {
                        // 无分类数据时隐藏相关控件
                        binding.editorTabLayout.setVisibility(View.GONE);
                        binding.editRecyclerView.setVisibility(View.GONE);
                        binding.tvHint.setVisibility(View.VISIBLE);
                    }

                    // 延迟显示编辑区域（避免加载动画闪烁）
                    new Handler().postDelayed(() -> {
                        binding.editRl.setVisibility(View.VISIBLE);
                        mLoadDialog.dismiss();
                    }, 500);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showBottom("数据获取失败");
                if (mLoadDialog != null) mLoadDialog.dismiss();
            }
        });
    }

    // 获取模块下的应用列表
    private void getTableAPP() {
        if (mAppInfoManager != null && mModuleInfo != null) {
            List<AppInfo> appInfoList = mAppInfoManager.queryAppInfo(
                    DBConstant.TABLE + mModuleInfo.getModule_id());
            if (appInfoList != null) {
                mAppList = appInfoList;
                mGridViewAdapter = new GridViewAdapter(mContext, mAppList, "delete");
                binding.dragGridView.setAdapter(mGridViewAdapter);
                resetLayout(); // 调整GridView高度
            }
        }
    }

    // 根据应用数量调整GridView高度
    private void resetLayout() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.dragGridView.getLayoutParams();
        if (mAppList.size() > 16) {
            lp.height = 320;
            binding.dragGridView.setDragEnable(true);
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            binding.dragGridView.setDragEnable(false);
        }
        binding.dragGridView.setLayoutParams(lp);
    }

    // 添加应用回调
    @Override
    public void onAddItemListener(AppInfo appInfo) {
        if (appInfo != null && mAppList != null) {
            // 检查是否已添加
            for (AppInfo app : mAppList) {
                if (appInfo.getApppackage().equals(app.getApppackage())) {
                    Toast.makeText(mContext,
                            mModuleInfo.getModule_name() + getText(R.string.has_added_app),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // 添加新应用
            mAppList.add(appInfo);
            resetLayout();
            mGridViewAdapter.notifyDataSetChanged();

            // 本地数据库更新
            AppInfoManager.getInstance().insertAppInfo(DBConstant.INSTALL_TABLE, appInfo);
            new Thread(() -> {
                HttpRequestCenter.appInstall(
                        appInfo.getApplabel(), appInfo.getApppackage(), appInfo.getClassify(),
                        appInfo.getUid(), appInfo.getVersioncode(), appInfo.getTerminal()
                ).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        // 无需处理返回结果，仅确保请求发出
                    }
                });
            }).start();
        }
    }

    // 删除应用回调
    @Override
    public void onDeleteItemListener(AppInfo appInfo) {
        if (appInfo != null && mAppList != null && mAppList.contains(appInfo)) {
            mAppList.remove(appInfo);
            mGridViewAdapter.notifyDataSetChanged();
            resetLayout(); // 重新调整高度
        }
    }

    // 重写返回键逻辑
    @Override
    public void onBackPressed() {
        finish();
    }

    // 结束Activity时保存数据
    @Override
    public void finish() {
        if (mModuleInfo != null) {
            // 更新模块信息
            mModuleInfo.setHomepage_type(home_type);
            mModuleInfo.setModule_name(binding.tvModuleName.getText().toString());
            ModuleTableManager.getInstance().upDate(DBConstant.MODULE_TABLE, mModuleInfo);

            // 清空旧应用数据并插入新数据
            AppInfoManager.getInstance().deleteAppInfo(
                    DBConstant.TABLE + mModuleInfo.getModule_id(), null);
            if (mAppList != null) {
                for (AppInfo appInfo : mAppList) {
                    mAppInfoManager.insertAppInfo(
                            DBConstant.TABLE + mModuleInfo.getModule_id(), appInfo);
                }
            }

            // 提交服务器更新
            String json = "";
            if (mAppList != null && mAppList.size() > 0) {
                String[] uids = new String[mAppList.size()];
                for (int x = 0; x < mAppList.size(); x++) {
                    uids[x] = mAppList.get(x).getUid();
                }
                json = new Gson().toJson(uids);
            }

            HttpRequestCenter.editModule(json, home_type,
                            mModuleInfo.getModule_id(), binding.tvModuleName.getText().toString())
                    .subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());
                            if (result.isok()) {
                                EventBus.getDefault().post(new AppStoreChangeMessage());
                            }
                        }
                    });
        }
        super.finish();
    }

    // 销毁时释放绑定对象
    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
