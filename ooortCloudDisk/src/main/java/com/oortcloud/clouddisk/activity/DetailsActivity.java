package com.oortcloud.clouddisk.activity;

import android.content.Context;
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
import com.oortcloud.clouddisk.databinding.ActivityDetailsBinding;
import com.oortcloud.clouddisk.dialog.FileDialog;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.observer.Observer;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/15 20:38
 * @version： v1.0
 * @function： 二级目录 详情
 */
public class DetailsActivity extends BaseActivity  implements Observer {

    private TextView mPresentSize;
    private TextView mTotalSize;
    private RecyclerView mFileRV;
    private FloatingActionButton mFab;
    private LinearLayout mImgNullLl;
    LinearLayout mImgNull;
    TextView title ;
    private FileAdapter mDetailsAdapter;
    //数据
    private DirData mDirData;
    //文件夹名称
    public String mDirName;
    //上级目录
    private String mParentDir;
    //当前目录
    private String mDir;

    //父目录集合 处理回退 进入
    public LinkedList<String> mDirs =  new LinkedList<>();

    private CountObserver mCountObserver;
    private DefaultNavigationBar defaultNavigationBar;
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_details;
//    }

    @Override
    protected void initBundle(Bundle bundle) {
        mParentDir =   bundle.getString("PARENT_DIR");
        mDirName =   bundle.getString("DIR_NAME");
        mDir = mParentDir + mDirName + "/";
        mDirs.add(mDir);
    }

    @Override
    protected void initActionBar() {
        mCountObserver = CountObserver.getInstance();
         defaultNavigationBar = new DefaultNavigationBar.Builder(this).setTitle(mDirName)
                .setRightRes(R.mipmap.ic_swap_vert).setRightClickListener(v -> {

                    startActivity(new Intent(mContext , TransfersActivity.class));

        }).builder();
        mCountObserver.addBuyUser(defaultNavigationBar);
        mCountObserver.addBuyUser(this);

        title = (TextView) defaultNavigationBar.findViewById(R.id.title_tv);
    }

    @Override
    protected void initData() {
        mCountObserver.sendNotify();

        if (title != null){
            title.setText(mDirName);
        }

    }

    @Override
    protected void initView() {
        ActivityDetailsBinding binding = getViewBinding();
        mPresentSize = binding.presentSize;
        mTotalSize = binding.totalSize;
        mFileRV = binding.fileRv;
        mFab = binding.fab;
        mImgNullLl = binding.imgNullLl;
        mDetailsAdapter = new FileAdapter(mContext , mDir);
        mFileRV.setLayoutManager(new LinearLayoutManager(this));
        mFileRV.setAdapter(mDetailsAdapter);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (mDir.equals(event.getDir())){
            mDirData = event.getData();
            if (mDirData != null ){
                mPresentSize.setText(FileHelper.reckonFileSize(mDirData.getUsed()));
                mTotalSize.setText("/"+ mDirData.getQuota());
                List<FileInfo> data = mDirData.getList();
                if (data != null && data.size() > 0 ){
                    mDetailsAdapter.setDir(mDir);
                    mDetailsAdapter.setData(mDirData.getList());
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

        if (!TextUtils.isEmpty(BaseApplication.UUID) && !TextUtils.isEmpty(BaseApplication.TOKEN)){

            HttpResult.fileList(mDir  , "" , 1 , 50 , "");
        }
    }
    @Override
    protected void onStart() {
        Log.v("msg" , "onStart");
        super.onStart();
        if (!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    @Override
    protected ActivityDetailsBinding getViewBinding() {
        return ActivityDetailsBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
        mCountObserver.deleteBuyUser(defaultNavigationBar);
        mCountObserver.deleteBuyUser(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("msg" , "onRestart");
    }

    @Override
    public void finish() {
        if (mDirs.size() > 1){
            mDirs.removeLast();
            mDir = mDirs.getLast();
            String str = mDir.substring(0 , mDir.length() -1);
            mDirName = str.substring(str.lastIndexOf("/")+ 1);
            mParentDir = str.replace(mDirName , "");
            initData();

        }else {
            super.finish();
        }

    }

    /**
     * 进入子目录
     */
    public void startDir(String parentDir , String dirName){
        mParentDir =   parentDir;
        mDirName =   dirName;
        mDir = mParentDir + mDirName + "/";
        mDirs.add(mDir);
        initData();
    }

    public static void actionStart(Context context,String parentDir , String dirName) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("PARENT_DIR",  parentDir);
        intent.putExtra("DIR_NAME",  dirName);
        context.startActivity(intent);
    }
}
