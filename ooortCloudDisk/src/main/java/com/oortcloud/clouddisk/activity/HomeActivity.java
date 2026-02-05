package com.oortcloud.clouddisk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.adapter.FileAdapter;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.EventMessage;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.databinding.ActivityHomeLayoutBinding;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.dialog.FileDialog;
import com.oortcloud.clouddisk.http.HttpConstants;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.observer.Observer;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.widget.SearchTextView;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/14 17:30
 * @version： v1.0
 * @function：首页
 */
public class HomeActivity extends BaseActivity implements Observer {


    private TextView mPresentSize;
    private TextView mTotalSize;
    private RecyclerView mFileRV;
    private FloatingActionButton mFab;
    private LinearLayout mImgNull;
    private SearchTextView mSearchView;


    private FileAdapter mFileAdapter;

    private DirData mDirData;

    private String mDir = "/";

    private DBManager mDBManager;

    private CountObserver mCountObserver;
    private DefaultNavigationBar defaultNavigationBar;

    // 核心修改1：移除getLayoutId()（BaseActivity已删除该抽象方法）

    @Override
    protected void initBundle(Bundle bundle) {
        init(bundle);
    }

    @Override
    protected void initActionBar() {
        mCountObserver = CountObserver.getInstance();
        defaultNavigationBar = new DefaultNavigationBar.Builder(this).setTitle(getString(R.string.cloud_disk)).setStyle(true)
                .setRightRes(R.mipmap.ic_swap_vert).setRightClickListener(v -> {
                    startActivity(new Intent(mContext, TransfersActivity.class));
                }).builder();
        //添加订阅
        mCountObserver.addBuyUser(defaultNavigationBar);
        mCountObserver.addBuyUser(this);

    }

    @Override
    protected void initData() {
        if (eventBus != null && !eventBus.isRegistered(this)) {
            eventBus.register(this);
        }

        //获取需要下载数
        mCountObserver.sendNotify();
    }

    @Override
    protected void initView() {
        // 核心修改2：通过父类的binding成员变量获取视图（已关联屏幕显示的视图）
        ActivityHomeLayoutBinding homeBinding = (ActivityHomeLayoutBinding) binding;
        mPresentSize = homeBinding.presentSize;
        mTotalSize = homeBinding.totalSize;
        mFileRV = homeBinding.fileRv;
        mFab = homeBinding.fab;
        mImgNull = homeBinding.imgNullLl;
        mSearchView = homeBinding.searchView;

        mFileAdapter = new FileAdapter(mContext, mDir);
        mFileRV.setLayoutManager(new LinearLayoutManager(this));
        mFileRV.setAdapter(mFileAdapter);


        mSearchView.setSearchTap(new SearchTextView.SearchTap() {
            @Override
            public void tap(View v) {
                Intent in = new Intent(HomeActivity.this, SearchActivity.class);
                in.putExtra("dir", mDir);
                startActivity(in);
            }
        });

        // 核心修改3：通过binding直接绑定FAB点击事件（此时绑定的是屏幕上的FAB）
        homeBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAB_Click", "ViewBinding绑定的FAB被点击"); // 测试日志
//                Toast.makeText(HomeActivity.this, "FAB点击成功", Toast.LENGTH_SHORT).show(); // 测试提示
                initEvent(view);
            }
        });
    }


    @Override
    protected void initEvent(View v) {
        if (v.getId() == R.id.fab) {
            new FileDialog(mContext, mDir).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMessage event) {
        if (mDir.equals(event.getDir())) {
            mDirData = event.getData();
            if (mDirData != null) {
                mPresentSize.setText(FileHelper.reckonFileSize(mDirData.getUsed()));
                mTotalSize.setText("/" + mDirData.getQuota());
                List<FileInfo> data = mDirData.getList();

                if (data != null && data.size() > 0) {
                    mFileAdapter.setData(mDirData.getList());
                    mFileRV.setVisibility(View.VISIBLE);
                    mImgNull.setVisibility(View.GONE);
                    return;
                }
            }
            mFileRV.setVisibility(View.GONE);
            mImgNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifyMsg(int msg) {
        if (!TextUtils.isEmpty(BaseApplication.UUID) && !TextUtils.isEmpty(BaseApplication.TOKEN)) {
            //获取目录下文件
            HttpResult.fileList(mDir, "", 1, 50, "");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent.getExtras());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // 核心修改4：实现getViewBinding()，返回当前布局的ViewBinding实例
    @Override
    protected ActivityHomeLayoutBinding getViewBinding() {
        // 加载布局并返回绑定对象（后续会被BaseActivity设置为内容视图）
        return ActivityHomeLayoutBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
        mCountObserver.deleteBuyUser(defaultNavigationBar);
        mCountObserver.deleteBuyUser(this);
    }

    private void init(Bundle bundle) {
        if (bundle != null) {
            String gatewayURL = bundle.getString("GATEWAY_URL");
            if (!TextUtils.isEmpty(gatewayURL)) {
                HttpConstants.GATEWAY_URL = gatewayURL;
            }
            BaseApplication.TOKEN = bundle.getString("token");
            BaseApplication.UUID = bundle.getString("uuid");
        }else{

        }
    }
}