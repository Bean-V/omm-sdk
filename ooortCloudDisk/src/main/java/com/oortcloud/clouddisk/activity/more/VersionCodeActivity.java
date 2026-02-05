package com.oortcloud.clouddisk.activity.more;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BaseActivity;
import com.oortcloud.clouddisk.bean.AppInfo;
import com.oortcloud.clouddisk.bean.DirData;
import com.oortcloud.clouddisk.bean.Result;
import com.oortcloud.clouddisk.databinding.ActivityVersionCodeLayoutBinding;
import com.oortcloud.clouddisk.http.HttpRequestCenter;
import com.oortcloud.clouddisk.http.bus.RxBusSubscriber;
import com.oortcloud.clouddisk.utils.AppInfoUtil;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;


/**
 * @filename:
 * @author: zzj/@date: 2020/12/14 17:30
 * @version： v1.0
 * @function：历史版本
 */
public class VersionCodeActivity extends BaseActivity {

    private RecyclerView mVersionRV;

    @Override
    protected ViewBinding getViewBinding() {
        return ActivityVersionCodeLayoutBinding.inflate(getLayoutInflater());
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_version_code_layout;
//    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initActionBar() {
        new DefaultNavigationBar.Builder(this).setTitle("版本介绍").builder();
    }

    @Override
    protected void initView(){
        mVersionRV = findViewById(R.id.version_rv);
        mVersionRV.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    protected void initData() {
        HttpRequestCenter.history(AppInfoUtil.packageName(this) ).subscribe(new RxBusSubscriber<String>(){

            @Override
            public void onNext(String s) {
                super.onNext(s);

                Result<DirData<AppInfo>> result = new Gson().fromJson(s,  new TypeToken<Result<DirData<AppInfo>>>() {}.getType());

                if (result.isOk()){
//                    List<AppInfo> list =  result.getData().getLists();
//                    if (list != null){
//                        mVersionRV.setAdapter(new HistoryAdapter(VersionCodeActivity.this , list));
//                    }
                }

            }

            @Override
            protected void onEvent(String s) {

            }
        });
    }

    @Override
    protected void initEvent(View v) {

    }
}
