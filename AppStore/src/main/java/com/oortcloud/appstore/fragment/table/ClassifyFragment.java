package com.oortcloud.appstore.fragment.table;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.TypeAppAllActivity;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.adapter.TypeAppAdapter;
import com.oortcloud.appstore.adapter.TypeClassifyAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ClassifyInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.FragmentClassifyLayoutBinding;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.fragment.BaseFragment;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;



/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/9 15:36
 */
public class ClassifyFragment extends BaseFragment implements BaseRecyclerViewAdapter.OnItemClickListener {

    // 声明变量
    private RecyclerView mTypeClassifyRecyclerView;
    private TextView mTypeName;
    private TextView mALlTextView;
    private RecyclerView mTypeAppRecyclerView;
    private TextView mCountShow;
    private int index = 0;
    private List<ClassifyInfo> mTypeClassify = new ArrayList<>();
    private TypeClassifyAdapter appTpyeAdapter;

    private StaggeredGridLayoutManager mStaggerdManager;

    private  TypeAppAdapter appAdapter;
    private com.oortcloud.appstore.databinding.FragmentClassifyLayoutBinding bind;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify_layout;
    }


    protected View getRootView(){
        bind = FragmentClassifyLayoutBinding.inflate(getLayoutInflater());

        // 通过 ViewBinding 赋值
        mTypeClassifyRecyclerView = bind.typeRecyclerView;
        mTypeName = bind.tvTypeName;
        mALlTextView = bind.tvShowAll;
        mTypeAppRecyclerView = bind.allAppRecyclerView;
        mCountShow = bind.countShowTv;
        return bind.getRoot();
    }
    @Override
    protected void initBundle(Bundle bundle) {
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initView() {

        if (appTpyeAdapter  == null){
            appTpyeAdapter= new TypeClassifyAdapter(mContext);
        }
        appTpyeAdapter= new TypeClassifyAdapter(mContext);
        mTypeClassifyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mTypeClassifyRecyclerView.setAdapter(appTpyeAdapter);

        mStaggerdManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

    }
    @Override
    protected void initData() {

        if (mTypeClassify != null && mTypeClassify.size() > 0 ){
            setPosition();
            initAllAppRecycler(mTypeClassify.get(index).getUid());
        }
        else {
            getClassList();

        }
    }

    @Override
    protected void initEvent() {

        appTpyeAdapter.setOnItemClickListener(this);
        try {
            if (mALlTextView != null){
                mALlTextView.setOnClickListener(view -> {
                        TypeAppAllActivity.actionStart(mContext , mTypeName.getText().toString().trim() , mTypeClassify.get(index).getUid() , 0);
            }
                );
            }
        }catch (Exception e){}

        mCountShow.setOnClickListener(view -> {
            if (mTypeClassify != null){
                if (index <= mTypeClassify.size()){
                    TypeAppAllActivity.actionStart(mContext , mTypeName.getText().toString().trim() , mTypeClassify.get(index).getUid() , 0);
                }
            }

        });

        EventBus.getDefault().register(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setEvent(String messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        if(messageEvent.equals("applyStatu")){
            appAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position ) {
        index = position;
        setPosition();
        initAllAppRecycler(mTypeClassify.get(index).getUid());

    }
    private void setPosition(){
        if (appTpyeAdapter != null){
            appTpyeAdapter.setmPosition(index);
        }
        if (mTypeName != null && mTypeClassify != null){
            mTypeName.setText(mTypeClassify.get(index).getName());
        }
    }
    //切换类型下的应用
    private void initAllAppRecycler(String classifyUID){


        if(mTypeAppRecyclerView == null){
            return;
        }

        mTypeAppRecyclerView.setVisibility(View.INVISIBLE);

        if (classifyUID == null){
            HttpRequestCenter.monthNewApp(1 , 9).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    setClassifyAppList(s);

                }
            });

        }else {
            HttpRequestCenter.postClassifyAppMore(classifyUID , 1 , 9).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    setClassifyAppList(s);
                }
            });
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void setClassifyAppList(String s){
        try {
            Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
            if (result.isok()){
                List<AppInfo> appInfoList = result.getData().getApp_list();

                if(mCountShow == null){
                    return;
                }
                if (result.getData().getCount() > 9){
                    mCountShow.setText(getString(R.string.all_app_count_show,String.valueOf(result.getData().getCount())));
                }else{
                    mCountShow.setText(getString(R.string.all_app_count,String.valueOf(result.getData().getCount())));
                }

                DataInit.saveAppInfos(appInfoList);

                if (appInfoList != null){
                    mTypeAppRecyclerView.setLayoutManager(mStaggerdManager);
                    appAdapter = new TypeAppAdapter(mContext ,appInfoList );
                    mTypeAppRecyclerView.setAdapter(appAdapter);

                    new Handler().postDelayed(() -> {
                        mTypeAppRecyclerView.setVisibility(View.VISIBLE);
                    }, 100);
                }

            }
        } catch (JsonSyntaxException e) {
            // 解析失败，说明是错误响应
            Result<String> errorResult = new Gson().fromJson(s, new TypeToken<Result<String>>(){}.getType());
            Log.e("API_ERROR", "错误信息: " + errorResult.getData());
        }
    }


    private void getClassList(){
        HttpRequestCenter.postClassifyList().subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onNext(String s) {
                Result<Data<ClassifyInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<ClassifyInfo>>>(){}.getType());

                if (result.isok()){
                    if (mTypeClassify != null){
                        mTypeClassify.clear();
                        mTypeClassify.add(new ClassifyInfo(getString(R.string.month_new)));
                        mTypeClassify.addAll(result.getData().getLists ());




                    }
                    if (appTpyeAdapter != null){

                        if (mTypeClassify != null && mTypeClassify.size() > 0 ){

                            appTpyeAdapter.setData(mTypeClassify);
                        }

                    }
                    setPosition();
                    initAllAppRecycler(mTypeClassify.get(index).getUid());
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser  && isInitUi) {
            if (mTypeClassify == null){
                initData();
            }

        }else {
            isRequest = false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
