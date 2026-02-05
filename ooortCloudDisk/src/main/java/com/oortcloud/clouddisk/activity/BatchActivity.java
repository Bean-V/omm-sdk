package com.oortcloud.clouddisk.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.BatchAdapter;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.EventMessage;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.databinding.ActivityBatchBinding;
import com.oortcloud.clouddisk.dialog.CRUDPopupWindow;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/24 04:04
 * @version： v1.0
 * @function： 选择批量操作处理文件
 */
public class BatchActivity extends BaseActivity {

    private TextView mPresentSize;
    private TextView mTotalSize;
    private RecyclerView mFileRV;
    private TextView mCheckAllTV;
    private TextView mDirNameTV;
    private LinearLayout mImgNull;
    private BatchAdapter mBatchAdapter;
    // 数据
    private DirData mDirData;
    // 文件夹/文件 名称
    private String mDirName;
    // 上级目录
    private String mParentDir;
    // 当前目录
    private String mDir;
    // 被选中文件
    private String mCheckFileName;

    // 父目录集合 处理回退 进入
    public LinkedList<String> mDirs = new LinkedList<>();


    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null) {
            mDir = bundle.getString("DIR");
            mDirName = bundle.getString("DIRNAME");
            mCheckFileName = bundle.getString("Check_FILE_NAME");
        }
        if (mDirs.isEmpty()) { // 避免重复添加
            mDirs.add(mDir == null ? "/" : mDir); // 默认目录为"/"
        }
    }

    @Override
    protected void initActionBar() {
        new DefaultNavigationBar.Builder(this)
                .setTitle("选择批量操作")
                .setRightRes(R.mipmap.icon_more)
                .setRightClickListener(v -> {
                    // 确保适配器数据非空
                    if (mBatchAdapter != null) {
                        new CRUDPopupWindow(BatchActivity.this, mBatchAdapter.getFileInfoData(), v);
                    }
                }).builder();
    }

    @Override
    protected void initData() {
        // 加载当前目录文件列表（避免mDir为null）
        if (!TextUtils.isEmpty(mDir)) {
            HttpResult.fileList(mDir, "", 1, 50, "");
        }
        // 设置目录名称显示
        mDirNameTV.setText(TextUtils.isEmpty(mDirName) ? "全部文件" : mDirName);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initView() {
        // 核心修改：使用BaseActivity的binding成员变量（已关联屏幕视图）
        if (binding == null) return; // 非空保护
        ActivityBatchBinding batchBinding = (ActivityBatchBinding) binding;

        // 初始化视图引用（从屏幕显示的binding中获取）
        mPresentSize = batchBinding.presentSize;
        mTotalSize = batchBinding.totalSize;
        mFileRV = batchBinding.fileRv;
        mCheckAllTV = batchBinding.checkAllTv;
        mDirNameTV = batchBinding.dirNameTv;
        mImgNull = batchBinding.imgNullLl;

        // 初始化适配器
        mBatchAdapter = new BatchAdapter(mContext, mDir, mCheckFileName);
        mFileRV.setLayoutManager(new LinearLayoutManager(this));
        mFileRV.setAdapter(mBatchAdapter);

        // 绑定全选按钮点击事件（关联屏幕上的视图）
        batchBinding.checkAllTv.setOnClickListener(this::initEvent);
    }

    @Override
    protected void initEvent(View v) {
        if (v.getId() == R.id.check_all_tv) {
            mBatchAdapter.checkALl(); // 触发全选逻辑
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMessage event) {
        if (mDir != null && mDir.equals(event.getDir())) {
            mDirData = event.getData();
            if (mDirData != null) {
                mPresentSize.setText(FileHelper.reckonFileSize(mDirData.getUsed()));
                mTotalSize.setText("/" + mDirData.getQuota());
                List<FileInfo> data = mDirData.getList();
                if (data != null && data.size() > 0) {
                    mBatchAdapter.setDir(mDir);
                    mBatchAdapter.setData(data);
                    mFileRV.setVisibility(View.VISIBLE);
                    mImgNull.setVisibility(View.GONE);
                    return;
                }
            }
            // 无数据时显示空视图
            mFileRV.setVisibility(View.GONE);
            mImgNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    @Override
    protected ViewBinding getViewBinding() {
        // 返回当前布局的ViewBinding（会被BaseActivity设置为内容视图）
        return ActivityBatchBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    @Override
    public void finish() {
        if (mDirs.size() > 1) {
            mDirs.removeLast();
            mDir = mDirs.getLast();
            String str = mDir.substring(0, mDir.length() - 1); // 移除末尾"/"
            // 避免根目录处理异常（如mDir为"/"时，str为空）
            if (TextUtils.isEmpty(str)) {
                mDirName = "";
                mParentDir = "/";
            } else {
                mDirName = str.substring(str.lastIndexOf("/") + 1);
                mParentDir = str.replace(mDirName, "");
            }
            initData(); // 刷新当前目录数据
        } else {
            super.finish(); // 退出页面
        }
    }

    /**
     * 进入子目录
     */
    public void startDir(String parentDir, String dirName) {
        mParentDir = parentDir;
        mDirName = dirName;
        mDir = mParentDir + mDirName + "/";
        mDirs.add(mDir);
        initData(); // 刷新数据
    }

    public static void actionStart(Context context, String dir, String dirName, String checkFileName) {
        Intent intent = new Intent(context, BatchActivity.class);
        intent.putExtra("DIR", dir);
        intent.putExtra("DIRNAME", dirName);
        intent.putExtra("Check_FILE_NAME", checkFileName);
        context.startActivity(intent);
    }
}