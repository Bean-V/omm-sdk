package com.oortcloud.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.adapter.MeetingAdapter;
import com.oortcloud.appstore.widget.RecyclerRefreshLayout;
import com.oortcloud.basemodule.navigationbar.DefaultNavigationBar;
import com.oortcloud.bean.Data;
import com.oortcloud.bean.Result;
import com.oortcloud.bean.meeting.MeetingInfo;
import com.oortcloud.dialog.MeetingDialog;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 14:13
 * @version： v1.0
 * @function： 会议列表
 */
public class MeetingActivity extends BaseActivity  implements RecyclerRefreshLayout.SuperRefreshLayoutListener{

    private RecyclerView mMeetingRV;
    private MeetingAdapter mAdapter;
    RecyclerRefreshLayout mRefreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_layout);
        if (getIntent() != null){

        }
        initActionBar();
        initView();
        initData();


    }

    private void  initActionBar(){
        getSupportActionBar().hide();

        new DefaultNavigationBar.Builder(this).setTitle("视频会议").setMoreClickListener(view ->  {

            new MeetingDialog(mContext).setConfirmListener((String theme , String notice) ->{

                RequesManager.createMeeting(theme , notice ).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {

                        Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());

                        if (result.isOk()) {
                           initData();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(mContext , "会议创建失败，请重新创建");
                    }
                });

            }).show();
        }).builder();

    }

    private void  initView(){
        mMeetingRV = findViewById(R.id.meeting_rl);

        mMeetingRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new MeetingAdapter(mContext , null);
        mMeetingRV.setAdapter(mAdapter);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mRefreshLayout.setColorSchemeResources(
                com.oortcloud.appstore.R.color.fresh_color1, com.oortcloud.appstore.R.color.fresh_color2,com.oortcloud.appstore.R.color.fresh_color3,
                com.oortcloud.appstore.R.color.fresh_color4, com.oortcloud.appstore.R.color.fresh_color5 ,com.oortcloud.appstore.R.color.fresh_color6);

    }

    public void initData(){
        RequesManager.meetingList("" , 0 , 0  , 1 , 50 , 99).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result<Data<List<MeetingInfo>>> result = new Gson().fromJson(s, new TypeToken<Result<Data<List<MeetingInfo>>>>() {}.getType());
                if (result.isOk()) {
                    if (result.getData() != null && result.getData().getLists() != null){
                        mAdapter.setData(result.getData().getLists());
                        mRefreshLayout.setCanLoadMore(false);
                        mRefreshLayout.onComplete();

                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mRefreshLayout.setCanLoadMore(false);
                mRefreshLayout.onComplete();

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    public void onRefreshing() {
        initData();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onScrollToBottom() {

    }
}
