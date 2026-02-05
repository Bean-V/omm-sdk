package com.oortcloud.clouddisk.activity.up;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BaseActivity;
import com.oortcloud.clouddisk.adapter.TableViewPagerAdapter;
import com.oortcloud.clouddisk.databinding.ActivityFileUploadLayoutBinding;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.fragment.up.DOCFragment;
import com.oortcloud.clouddisk.fragment.up.OtherFragment;
import com.oortcloud.clouddisk.fragment.up.PicDirFragment;
import com.oortcloud.clouddisk.fragment.up.PicFragment;
import com.oortcloud.clouddisk.fragment.up.VideoFragment;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/15 19:04
 * @version： v1.0
 * @function： 获取文件目录及上传处理
 */
public class FileUploadActivity  extends BaseActivity {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mFileSizeTV;
    private TextView mConfirmTV;
    private FrameLayout mPicFragment;
    private LinearLayout mTabLL;
    //上传目录
    private String mDir;
    private TableViewPagerAdapter mAdapter;
    private FragmentManager mFragmentManager;

    private int flag;
    public static final List<File> mFiles = new LinkedList<>();
    public static final Map<String, Boolean> mMap = new HashMap<>();

    public DBManager mDBManager;

    @Override
    protected ActivityFileUploadLayoutBinding getViewBinding() {
        return ActivityFileUploadLayoutBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_file_upload_layout;
//    }

    @Override
    protected void initBundle(Bundle bundle) {
       mDir =  bundle.getString("DIR");
       mDBManager = DBManager.getInstance();
    }

    @Override
    protected void initActionBar() {
//        mLoadDialog = new LoadingDialog(mContext);
//        mLoadDialog.show();
        new DefaultNavigationBar.Builder(this).setTitle("选择文件").builder();
//        .setRightText("分类")
//                .setRightClickListener(v -> {
//                    mFileDirAdapter.uploadFile(mDir);
//                    mPath.remove();
//                    finish();
//
//                })
    }

    @Override
    protected void initView() {
        // 通过 ViewBinding 赋值
        ActivityFileUploadLayoutBinding binding = getViewBinding();
        mTabLayout = binding.tabLayout;
        mViewPager = binding.viewPager;
        mFileSizeTV = binding.fileSizeTv;
        mConfirmTV = binding.confirmTv;
        mPicFragment = binding.picFl;
        mTabLL = binding.tabLl;
        mFragmentManager =  getSupportFragmentManager();
        mAdapter = new TableViewPagerAdapter(mFragmentManager);
        mAdapter.reSet(new String[]{"视频","图片" ,"文档", "其它"});
        mAdapter.reSet(getFragments());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        binding.confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initEvent(view);
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent(View v) {
        if (v.getId() == R.id.confirm_tv){
            //判断处理上传 关闭页面
            if (mFiles.size() == 0 ){
                ToastUtils.showContent("请选择文件");
            }else {
                TransferHelper.uploadFile(mDir , mFiles);
//                ToastUtils.showContent("正在上传中");
                flag = 0 ;
                finish();
            }

        }
    }


    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        //视频
        fragments.add(new VideoFragment());
        //图片
        fragments.add(new PicDirFragment());
        //文档
        fragments.add(new DOCFragment());
        //其它
        fragments.add(new OtherFragment());
        return fragments;
    }

    private int mFileSize;
    public void upload(boolean isChecked , File file){

        if (isChecked){
            if (mMap.get(file.getPath()) == null){
                mFiles.add(file);
                mFileSize += file.length();
                mMap.put(file.getPath() , true);
            }

        }else {
            mFiles.remove(file);
            mFileSize -= file.length();
            mMap.remove(file.getPath());
        }

        mFileSizeTV.setText("已选  " +FileHelper.reckonFileSize(mFileSize));
        mConfirmTV.setText("上传("+ mFiles.size()+")");
    }

    public  void actionStart( String path) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.pic_fl, PicFragment.instantiate(path));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mTabLL.setVisibility(View.GONE);
        flag = 1;

    }

    @Override
    public void finish() {
        if (flag ==1){
            onBackPressed();
        }else {
            super.finish();
            mFileSize = 0 ;
            mFiles.clear();
            mMap.clear();
        }

    }

    @Override
    public void onBackPressed() {
        flag = 0;
        mTabLL.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }
}
