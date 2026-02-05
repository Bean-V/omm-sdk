package com.oortcloud.appstore.fragment.table;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.TypeAppAllActivity;
import com.oortcloud.appstore.adapter.RecommendGrodAdapter;
import com.oortcloud.appstore.adapter.TypeAppAllAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Constants;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.AppListLayoutBinding;
import com.oortcloud.appstore.databinding.FragmentRecommendLayoutBinding;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.fragment.BaseFragment;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.basemodule.views.AutoHeightRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @filename:
 * @function：推荐Fragment
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/11 17:51
 */
public class RecommendFragment extends BaseFragment {

    AppListLayoutBinding mChoicenessApp;
    AppListLayoutBinding mInstallationEssential;
    AppListLayoutBinding mAllPersionNeed;



    TextView mChoicenessName;
    TextView mEssentialName;
    TextView mAllInUseName;

    TextView mChoicenessAppSA;
    TextView mEssentialAppSA;
    TextView mAllInUseAppSA;

    AutoHeightRecyclerView mChoicenessAppGV;

    AutoHeightRecyclerView mEssentialAppGV;

    RecyclerView mAllInUseAppRV;

    private RecommendGrodAdapter mChoicenessAdapter;

    private RecommendGrodAdapter mEssentialAdapter;

    private TypeAppAllAdapter mAllInUseAdapter;
    private com.oortcloud.appstore.databinding.FragmentRecommendLayoutBinding binding;

    @Override
    protected View getRootView() {
        binding = FragmentRecommendLayoutBinding.inflate(getLayoutInflater());


         mChoicenessApp = binding.choicenessApp;
         mInstallationEssential = binding.installationEssential;
         mAllPersionNeed = binding.allPersonNeed;
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initView() {

        mChoicenessName = mChoicenessApp.getRoot().findViewById(R.id.tv_set_type);
        mChoicenessName.setText(getResources().getString(R.string.choiceness_app_str));

        mEssentialName = mInstallationEssential.getRoot().findViewById(R.id.tv_set_type);
        mEssentialName.setText(getResources().getString(R.string.installation_essential_str));

        mAllInUseName = mAllPersionNeed.getRoot().findViewById(R.id.tv_set_type);
        mAllInUseName.setText(getResources().getString(R.string.all_person_need_str));

        mChoicenessAppSA =   mChoicenessApp.getRoot().findViewById(R.id.tv_show_all);
        mChoicenessApp.getRoot().findViewById(R.id.more_duty_person_app).setVisibility(View.GONE);
        mChoicenessAppGV =  mChoicenessApp.getRoot().findViewById(R.id.recommend_grid_view);
        mChoicenessAppGV.setVisibility(View.VISIBLE);

        mEssentialAppSA =  mInstallationEssential.getRoot().findViewById(R.id.tv_show_all);
        mInstallationEssential.getRoot().findViewById(R.id.more_duty_person_app).setVisibility(View.GONE);
        mEssentialAppGV =  mInstallationEssential.getRoot().findViewById(R.id.recommend_grid_view);
        mEssentialAppGV.setVisibility(View.VISIBLE);

        mAllInUseAppSA =  mAllPersionNeed.getRoot().findViewById(R.id.tv_show_all);
        mAllInUseAppRV =  mAllPersionNeed.getRoot().findViewById(R.id.more_duty_person_app);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mAllInUseAppRV.getLayoutParams();
//        params.leftMargin = 0;
//        params.rightMargin = 0 ;
//        params.topMargin  = 0 ;
//        params.bottomMargin =10;
//        mAllInUseAppRV.setLayoutParams(params);



        mChoicenessAdapter = new RecommendGrodAdapter(mContext);
        mChoicenessAppGV.setAdapter(mChoicenessAdapter);

        mEssentialAdapter = new RecommendGrodAdapter(mContext);
        mEssentialAppGV.setAdapter(mEssentialAdapter);

        mAllInUseAdapter = new TypeAppAllAdapter(mContext , Constants.RANKING);
        mAllInUseAppRV.setLayoutManager(new LinearLayoutManager(mContext));

        mAllInUseAppRV.setAdapter(mAllInUseAdapter);

    }
    @Override
    protected void initData() {

        getRecommendAPP();

    }

    @Override
    protected void initEvent() {
        mChoicenessAppSA.setOnClickListener(view ->  {

            actionStart(mChoicenessName.getText().toString().trim() , 1);
        });
        mEssentialAppSA.setOnClickListener(view ->  {

            actionStart(mEssentialName.getText().toString().trim() , 2);
        });
        mAllInUseAppSA.setOnClickListener(view ->  {

               actionStart(mAllInUseName.getText().toString().trim() , 3);
        });
        EventBus.getDefault().register(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setEvent(String messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        if(messageEvent.equals("applyStatu")){
            mChoicenessAdapter.notifyDataSetChanged();
            mEssentialAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void actionStart(String title ,int item){
        TypeAppAllActivity.actionStart(mContext ,title ,null ,item);
    }

    private void getRecommendAPP(){
        HttpRequestCenter.postRecommend().subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.v("msg" , s);
                Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                if (result.isok()) {
                   List<AppInfo> excellentList  =  result.getData().getExcellent_list();
                    if (excellentList != null){
                        if (excellentList.size()> 4){
                            mChoicenessAdapter.setData(excellentList.subList(0 , 4));
                            mEssentialAdapter.setData(excellentList.subList(0 , 4));
                        }else {
                            mChoicenessAdapter.setData(excellentList);
                            mEssentialAdapter.setData(excellentList);
                        }

                    }

                    DataInit.saveAppInfos(excellentList);

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.v("msg" , e.toString());
            }
        });
        //获取大家都在用/ 修改为使用排名
        HttpRequestCenter.postRecommendMore(3 , 1).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                if (result.isok()){
                    List<AppInfo> mustList = result.getData().getApp_list();
                    if (mustList.size()> 5){
                        mAllInUseAdapter.setData(mustList.subList(0 , 5));
                    }
                    else {
                        mAllInUseAdapter.setData(mustList);
                    }

                }

            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser  && isInitUi) {

            mAllInUseAdapter.notifyDataSetChanged();
        }else {
            isRequest = false;
        }

    }
}
