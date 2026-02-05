package com.oortcloud.clouddisk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.CopyAdapter;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.EventMessage;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.databinding.ActivityCopyBinding;
import com.oortcloud.clouddisk.dialog.CreateFileDialog;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/23 16:36
 * @version： v1.0
 * @function： 复制到指定目录  可增加搜索框
 */
public class CopyActivity extends BaseActivity {

    private ActivityCopyBinding mbinding; // 声明绑定对象

    private CopyAdapter mCopyAdapter;
    // 复制移动的源文件集合
    private List<FileInfo> mData;
    // 列表数据
    private DirData mDirData;
    // 文件夹名称
    private String mDirName;
    // 上级目录
    private String mParentDir;
    // 当前目录 默认/
    private String mDir = "/";

    // 父目录集合 处理回退 进入
    public LinkedList<String> mDirs = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 移除重复的初始化代码：BaseActivity的onCreate已调用initBundle、initActionBar等
        // 无需手动调用initBundle、initView等，避免重复执行
    }

    @Override
    protected void initBundle(Bundle bundle) {
        // 从intent中获取数据（BaseActivity会传递getIntent().getExtras()）
        if (bundle != null) {
            mData = bundle.getParcelableArrayList("LIST");
        }
        mDirs.add(mDir); // 初始化目录栈
    }

    @Override
    protected void initActionBar() {
        new DefaultNavigationBar.Builder(this)
                .setTitle(getResources().getString(R.string.aim_folder))
                .setRightRes(R.mipmap.ic_add_folder)
                .setRightClickListener(v -> {
                    new CreateFileDialog(mContext, (dialog, fileName) -> {
                        if (!TextUtils.isEmpty(fileName)) {
                            HttpResult.mkdir(mDir, fileName);
                            dialog.dismiss();
                        } else {
                            ToastUtils.showContent("请输入文件夹名称");
                        }
                    }, null).show();
                }).builder();
    }

    @Override
    protected void initView() {
        // 核心修改：初始化mbinding（从BaseActivity的binding强转）
        if (binding == null) return; // 非空保护
        mbinding = (ActivityCopyBinding) binding;

        // 初始化RecyclerView
        mCopyAdapter = new CopyAdapter(mContext, mDir);
        mbinding.fileRv.setLayoutManager(new LinearLayoutManager(this));
        mbinding.fileRv.setAdapter(mCopyAdapter);

        // 绑定点击事件（替代setupClickListeners()）
        mbinding.cancelTv.setOnClickListener(this::initEvent);
        mbinding.copyTv.setOnClickListener(this::initEvent);
        mbinding.aimTv.setOnClickListener(this::initEvent);
    }

    @Override
    protected void initData() {
        // 加载当前目录文件列表
        HttpResult.fileList(mDir, "", 1, 50, "");
        // 设置目录名称显示
        mbinding.dirNameTv.setText(TextUtils.isEmpty(mDirName) ? "全部文件" : mDirName);
    }

    @Override
    protected void initEvent(View v) {
        int id = v.getId();
        if (id == R.id.cancel_tv) {
            mDirs.clear();
            mbinding.imgNullLl.setVisibility(View.GONE);
            finish();
        } else if (id == R.id.copy_tv) {
            HttpResult.copy(mData, mDir, (count) -> {
                mDirs.clear();
                finish();
            });
        } else if (id == R.id.aim_tv) {
            HttpResult.move(mData, mDir, (count) -> {
                mDirs.clear();
                finish();
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMessage event) {
        if (mDir.equals(event.getDir())) {
            mDirData = event.getData();
            if (mDirData != null) {
                List<FileInfo> data = mDirData.getList();
                if (data != null && data.size() > 0) {
                    mCopyAdapter.setDir(mDir);
                    mCopyAdapter.setData(data);
                    mbinding.fileRv.setVisibility(View.VISIBLE);
                    mbinding.imgNullLl.setVisibility(View.GONE);
                    return;
                }
            }
            mbinding.fileRv.setVisibility(View.GONE);
            mbinding.imgNullLl.setVisibility(View.VISIBLE);
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
        // 正确返回当前布局的ViewBinding
        return ActivityCopyBinding.inflate(getLayoutInflater());
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
            String str = mDir.substring(0, mDir.length() - 1); // 移除末尾的"/"
            mDirName = str.substring(str.lastIndexOf("/") + 1); // 提取最后一级目录名
            mParentDir = str.replace(mDirName, ""); // 计算父目录
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
        mDir = mParentDir + mDirName + "/"; // 拼接子目录路径
        mDirs.add(mDir); // 加入目录栈
        initData(); // 刷新数据
    }

    public static void actionStart(Context context, List<FileInfo> list) {
        Intent intent = new Intent(context, CopyActivity.class);
        intent.putParcelableArrayListExtra("LIST", (ArrayList<? extends Parcelable>) list);
        context.startActivity(intent);
    }
}