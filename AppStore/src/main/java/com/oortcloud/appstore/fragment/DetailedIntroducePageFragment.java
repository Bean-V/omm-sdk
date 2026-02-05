package com.oortcloud.appstore.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.TypeAppAllActivity;
import com.oortcloud.appstore.adapter.AppInfoStyleAdapter;
import com.oortcloud.appstore.adapter.ImageAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ClassifyInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Department;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.bean.UserInfo;
import com.oortcloud.appstore.databinding.PageDetailedIntroduceLayoutBinding;
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @filename:
 * @function：应用详情 介绍
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/10 11:28
 */
public class DetailedIntroducePageFragment extends BaseFragment{


    private ViewPager mImgViewPager;
    private TextView mTypeName;
    private TextView mappSize;
    private TextView mAppVersion;
    private TextView mUpdateTime;
    private TextView mBelongCity;
    private TextView mUpdateLog;
    private TextView mAppDescribe;
    private TextView mRamheOf;
    private TextView mBelongLand;
    private TextView mDutyPerson;
    private TextView mBuildUnit;
    private TextView mContactPhone;
    private TextView mContactUnit;
    private TextView mOperationPhone;
    private RelativeLayout mPdfLayout;
    private TextView mPdfSize;
    private TextView mShowALl;
    private RecyclerView mMoreDutyPersonApp;
    private static final String OBJECT_KEY = "object_key";
     private AppInfo appInfo;

     private AppInfoStyleAdapter appStyleAdapter;
    private com.oortcloud.appstore.databinding.PageDetailedIntroduceLayoutBinding binding;

    public static Fragment instantiate(AppInfo info) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBJECT_KEY, info);
        Fragment fragment = new DetailedIntroducePageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getRootView() {
        binding = PageDetailedIntroduceLayoutBinding.inflate(getLayoutInflater());
        mImgViewPager = binding.introduceImg;
        mTypeName = binding.tvTypeName;
        mappSize = binding.tvAppSize;
        mAppVersion = binding.tvAppVersion;
        mUpdateTime = binding.tvUpdateTime;
        mBelongCity = binding.tvBelongCity;
        mUpdateLog = binding.tvUpdateLog;
        mAppDescribe = binding.tvAppDescribe;
        mRamheOf = binding.tvRangeOf;
        mBelongLand = binding.tvBelongLand;
        mDutyPerson = binding.tvDutyPerson;
        mBuildUnit = binding.tvBuildUnit;
        mContactPhone = binding.tvContactPhone;
        mContactUnit = binding.contractToBuildUnit;
        mOperationPhone = binding.tvOperationPhone;
        mPdfLayout = binding.pdfLayout;
        mPdfSize = binding.tvPdfSize;
        mShowALl = binding.tvShowAll;
        mMoreDutyPersonApp = binding.moreDutyPersonApp;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.page_detailed_introduce_layout;
    }


    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null){
            appInfo = (AppInfo) bundle.getSerializable(OBJECT_KEY);
        }

    }
    @Override
    protected void initData() {




        mTypeName.setText(ClassifyManager.getClassify(appInfo.getClassify()));



        mappSize.setText(String.format("%.1f", Float.parseFloat(appInfo.getApp_size())/1024/1024) +"MB");

        mAppVersion.setText("V" + appInfo.getVersion());

        Date date = new Date(appInfo.getModified_on() *1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        mUpdateTime.setText(simpleDateFormat.format(date));

        mBelongCity.setText(appInfo.getRegion());

        mUpdateLog.setText(appInfo.getVer_description());

        mAppDescribe.setText(appInfo.getIntro());

//        mRamheOf.setText(appInfo.getUserang_depart());

        mBelongLand.setText(appInfo.getRegion());

        HttpRequestCenter.getUserInfo(appInfo.getPrincipal()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result<Data<UserInfo>> result = new Gson().fromJson(s,new TypeToken<Result<Data<UserInfo>>>(){}.getType());

                if (result.isok()){

                    if(mDutyPerson == null){
                        return;
                    }
                    mDutyPerson.setText(result.getData().getUserInfo().getOort_name());
                }
            }
        });

        HttpRequestCenter.getdeptuser(appInfo.getConstruction_unit()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result<Data<Department>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Department>>>(){}.getType());
                if (result.isok()){
                    if (result.getData().getDept() != null && result.getData().getDept().size() > 0){
                            if(mBuildUnit == null){
                                return;
                            }
                            mBuildUnit.setText(result.getData().getDept().get(0).getOort_pdname()
                        );
                    }

                }

            }
        });


        mContactPhone.setText(appInfo.getPhone());

        mContactUnit.setText(appInfo.getDevelop_unit());

        mOperationPhone.setText(appInfo.getOperate_phone());

//        mPdfSize.setText("缺少--");


        List<String> fruitList = new Gson().fromJson(appInfo.getScreenshot_url(), new TypeToken<List<String>>(){}.getType());

        ImageAdapter mAdapter = new ImageAdapter(fruitList, mContext);
        mImgViewPager.setAdapter(mAdapter);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext ,LinearLayoutManager.HORIZONTAL ,false);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mMoreDutyPersonApp.setVisibility(View.VISIBLE);
       // mMoreDutyPersonApp.setLayoutManager(linearLayoutManager);
        appStyleAdapter = new AppInfoStyleAdapter(mContext);
        mMoreDutyPersonApp.setAdapter(appStyleAdapter);

        getAppRecycler();

    }
    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        mShowALl.setOnClickListener(view ->  {

                TypeAppAllActivity.actionStart(mContext , getString(R.string.more_app_from_developer), appInfo.getUid());
        });

        mContactPhone.setOnClickListener(view ->  {

                Intent Intent =  new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + mContactPhone.getText().toString().trim()));//跳转到拨号界面，同时传递电话号码
                startActivity(Intent);
        });

        mOperationPhone.setOnClickListener(view ->  {

                Intent Intent =  new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + mOperationPhone.getText().toString().trim()));//跳转到拨号界面，同时传递电话号码
                startActivity(Intent);
        });
    }
    //获取责任人相关更多应用
    private void getAppRecycler(){

        HttpRequestCenter.principalAPPMore(1,appInfo.getUid(),100).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                if (result.isok()){
                    if (result.getData().getApp_list() != null && result.getData().getApp_list().size() > 0){
                        if (result.getData().getApp_list().size() >= 4){
                            appStyleAdapter.setData(result.getData().getApp_list().subList(0 , 4));
                        }else {
                            appStyleAdapter.setData(result.getData().getApp_list());
                        }
                    }


                }
            }
        });

    }



    private Type getTypeClassfy(){
        return new TypeToken<Result<ClassifyInfo>>(){}.getType();

    }
}
