package com.oortcloud.appstore.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.adapter.AppManagerAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.PageManageLayoutBinding;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function： 管理下的  更新 卸载 Fragment
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/9 14:45
 */
public class ManagePageFragment extends BaseFragment{

    RelativeLayout mManagerRL;
    RecyclerView mManagerRV;
    TextView mHintTV;

    private  List<AppInfo> newLists = new ArrayList();
    private static final String FRAGMENT_TYPE = "fragment_type";
    public static final String FRAGMENT_UPDATE = "fragment_update";
    public static final String FRAGMENT_UNINSTALL = "fragment_uninstall";
    private String type ;

    AppManagerAdapter appManagerAdapter;
    private com.oortcloud.appstore.databinding.PageManageLayoutBinding binding;

    public static Fragment instantiate(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_TYPE, type);
        Fragment fragment = new ManagePageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getRootView() {
        binding = PageManageLayoutBinding.inflate(getLayoutInflater());
         mManagerRL = binding.manageRl;
         mManagerRV = binding.managerRecyclerView;
         mHintTV = binding.tvHint;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.page_manage_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null){
            type =  bundle.getString(FRAGMENT_TYPE);
        }
    }

    @Override
    protected void initView() {
        if (FRAGMENT_UPDATE.equals(type)) {
            appManagerAdapter = new AppManagerAdapter(mContext, AppManagerAdapter.ADAPTER_UPDATE);
        }
        else if (FRAGMENT_UNINSTALL.equals(type)){
            appManagerAdapter = new AppManagerAdapter(mContext , AppManagerAdapter.ADAPTER_UNINSTALL);
        }

        mManagerRV.setLayoutManager(new LinearLayoutManager(mContext));
        mManagerRV.setAdapter(appManagerAdapter);
    }

    @Override
    protected void initData() {
//        if (mLoadDialog != null)
//            mLoadDialog.show();
        if (FRAGMENT_UPDATE.equals(type)){
            getUpdateApp();
        }else if (FRAGMENT_UNINSTALL.equals(type)){
            getUninstallApp();
        }

    }

    @Override
    protected void initEvent() {


    }

    //卸载应用
    private void getUninstallApp() {
        List<AppInfo> lists = AppInfoManager.getInstance().queryAppInfo(DBConstant.INSTALL_TABLE );


        List<AppInfo> list = new ArrayList<>();
        for(AppInfo info :lists){
            if(info.getTerminal() < 2){
                list.add(info);
            }
        }

        Log.e("lc_log", "getUninstallApp: " + lists.size() + "*****" + list.size());
       setVisibility(list);
    }

    //更新应用
    private void getUpdateApp() {

        HttpRequestCenter.appUpdateList().subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                if (result.isok()){
                    setVisibility( result.getData().getLists());
                }
            }
        });

    }
    private void setVisibility(List<AppInfo> lists){
        if (lists != null  && lists.size() > 0){
            mHintTV.setVisibility(View.GONE);
            mManagerRV.setVisibility(View.VISIBLE);
            appManagerAdapter.setData(lists);
        }else {
            mManagerRV.setVisibility(View.GONE);
            mHintTV.setVisibility(View.VISIBLE);

            //mHintTV.setText(R.string.hint_uninstall_str);

            if (FRAGMENT_UPDATE.equals(type)){
                mHintTV.setText(R.string.hint_update_str);
            }else if (FRAGMENT_UNINSTALL.equals(type)){

                mHintTV.setText(R.string.hint_uninstall_str);
            }
        }

        //延时显示
        new Handler().postDelayed(() -> {
            if (mManagerRL != null){
                mManagerRL.setVisibility(View.VISIBLE);
            }
            if (mLoadDialog != null){
                mLoadDialog.dismiss();
            }
        }, 100);

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser  && isInitUi) {
            if (FRAGMENT_UPDATE.equals(type)){
                getUpdateApp();
            }else if (FRAGMENT_UNINSTALL.equals(type)){

                getUninstallApp();
            }
        }else {
            isRequest = false;
        }
    }

}
