package com.oort.weichat.fragment;


import static com.oortcloud.basemodule.constant.Constant.PUBLIC_NUM;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_USERID;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.fragment.adapter.AdapterDynamicHome;
import com.oort.weichat.fragment.adapter.AdapterNews;
import com.oort.weichat.fragment.adapter.AdapterTasks;
import com.oort.weichat.fragment.adapter.GroupedListAdapter;
import com.oort.weichat.fragment.adapter.RecyclerViewBannerAdapter;
import com.oort.weichat.fragment.entity.DynamicBean;
import com.oort.weichat.fragment.entity.GroupModel;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oort.weichat.fragment.entity.OORTDynamicReview;
import com.oort.weichat.fragment.entity.OORTGANews;
import com.oort.weichat.fragment.entity.OORTStatistics;
import com.oort.weichat.fragment.entity.OORtCloudRoomData;
import com.oort.weichat.fragment.entity.OortNotice;
import com.oort.weichat.fragment.entity.ResArr;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.fragment.home.HomeActivityTaskLsit;
import com.oort.weichat.ui.base.EasyFragment;
import com.oortcloud.appstore.AppStoreActivity;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.adapter.AppStoreChangeMessage;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.bean.UserInfo;
import com.oortcloud.appstore.db.MessageRefrashHomeApp;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.im.MessageEventChangeUI;
import com.oortcloud.basemodule.im.TaskMsgInfoBean;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.AppSetConfig;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.oortcloud.basemodule.views.groupedadapter.holder.BaseViewHolder;
import com.oortcloud.basemodule.views.groupedadapter.layoutmanger.GroupedGridLayoutManager;
import com.oortcloud.basemodule.widget.mzbanner.MZBannerView;
import com.oortcloud.basemodule.widget.mzbanner.holder.MZHolderCreator;
import com.oortcloud.basemodule.widget.mzbanner.holder.MZViewHolder;
import com.oortcloud.basemodule.widget.video.ActivityVideoPlayer;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.login.net.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.widget.textview.marqueen.MarqueeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_home_parent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_home_parent extends EasyFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyAdapter mAdapter;
    private ArrayList mlist;
    private SmartRefreshLayout mRefreshLayout;

    private ArrayList mCTypes = new ArrayList();;
    private ArrayList mTypes = new ArrayList();;



    private RecyclerView rvList;
    private RecyclerView rvList1;
    private RecyclerView rvList2;
    private RecyclerView rvList3;
    private RecyclerView rvList4;

    private GroupedListAdapter adapter;
    private AdapterDynamicHome adpDynamic_hot;
    private AdapterDynamicHome adpDynamic_leader;
    private AdapterNews adpNews;
    private AdapterTasks adpTasks;
    private ImageView user_header;
    private TextView tv_user_name;
    private TextView tv_user_depart;
    //private LeafSquareChart leafSquareChart;
    private BarChart leafSquareChart;
    private int mChartColor;
    private CardView cv_apps;

    private CardView cv_class_room;
    private CardView cv_gnyw;
    private CardView cv_rwzx;
    private CardView cv_jxrt;
    private CardView cv_lddp;
    private CardView cv_level;
    private MarqueeView mv_not;
    private int lddpCount;
    private RecyclerViewBannerAdapter mAdapterHorizontal;
    private ArrayList mRooms;
    private MZBannerView bannerLayout;
    private BarChart barChart;

    public ArrayList getDatas() {
        return datas;
    }

    public void setDatas(ArrayList ds) {

        datas.clear();
        if(ds != null){
            datas.addAll(ds);

            if(adapter != null) {
                adapter.setGroups(datas);
            }
        }
        this.datas = datas;
    }

    private ArrayList datas = new ArrayList();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_home_parent.
     */
    // TODO: Rename and change types and number of parameters


    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_home_parent;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        rvList = findViewById(R.id.rv_list);
        rvList1 = findViewById(R.id.rv_gayw);
        rvList2 = findViewById(R.id.rv_jxrt);
        rvList3 = findViewById(R.id.rv_lddp);
        rvList4 = findViewById(R.id.rv_rwzx);


        cv_apps = findViewById(R.id.cv_app);

        cv_class_room = findViewById(R.id.cv_school);
        cv_gnyw = findViewById(R.id.cv_gayw);
        cv_rwzx = findViewById(R.id.cv_rwzx);
        cv_jxrt = findViewById(R.id.cv_jxrt);
        cv_lddp = findViewById(R.id.cv_lddp);
        cv_level = findViewById(R.id.cv_level);


//        if(BuildConfig.DEBUG){
//            cv_class_room.setVisibility(View.GONE);
//            cv_gnyw.setVisibility(View.GONE);
//            cv_jxrt.setVisibility(View.GONE);
//            cv_lddp.setVisibility(View.GONE);
//            cv_level.setVisibility(View.GONE);
//
//        }


        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupedListAdapter(getContext(),datas);

        adapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {

                Intent in = new Intent(getContext(), AppStoreActivity.class);
                getContext().startActivity(in);

                OperLogUtil.msg("点击了应用更多" + "进入应用商城");

            }
        });
        adapter.setOnFooterClickListener(new GroupedRecyclerViewAdapter.OnFooterClickListener() {
            @Override
            public void onFooterClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {

            }
        });
        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {

                ModuleInfo m = (ModuleInfo) mTypes.get(groupPosition);
                if(m != null){

                    int ACTION_REQUEST_PERMISSIONS = 0x001;
                    String[] NEEDED_PERMISSIONS = new String[]{

                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.REQUEST_INSTALL_PACKAGES,

                    };

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.REQUEST_INSTALL_PACKAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 101);
                    }



                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // 向用户解释为什么需要该权限
                            new AlertDialog.Builder(getContext())
                                    .setTitle("权限请求")
                                    .setMessage("我们需要写入外部存储权限来保存重要数据，麻烦您允许。")
                                    .setPositiveButton("去设置", (dialog, which) -> {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        }
                    }
//                    if (!checkPermissions(NEEDED_PERMISSIONS)) {
//
//                        OperLogUtil.msg("请求打开app所需权限");
//
//
//                        for (int i = 0; i < NEEDED_PERMISSIONS.length; i++) {
//                            if (TextUtils.isEmpty(NEEDED_PERMISSIONS[i])) {
//                                throw new IllegalArgumentException("Permission request for permissions "
//                                        + Arrays.toString(NEEDED_PERMISSIONS) + " must not contain null or empty values");
//                            }
//                        }
//                        ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
//                        return;
//                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (!Environment.isExternalStorageManager()) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                            return;
                        }
                    }



                    List ls = m.getApp_list();
                    AppInfo app = (AppInfo) ls.get(childPosition);
                    startService(app.getApppackage());

                    OperLogUtil.msg("点击了" + app.getApplabel() +"包名:" + app.getApppackage());
                }

            }
        });
        rvList.setAdapter(adapter);

        GroupedGridLayoutManager gridLayoutManager = new GroupedGridLayoutManager(getContext(),4 , adapter);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rvList.setLayoutManager(gridLayoutManager);
        //getApps();



        adpNews = new AdapterNews(new ArrayList<>());
        adpTasks = new AdapterTasks(new ArrayList<>());
        adpDynamic_hot = new AdapterDynamicHome(new ArrayList<>());
        adpDynamic_leader = new AdapterDynamicHome(new ArrayList<>());

        rvList1.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList2.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList3.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList4.setLayoutManager(new LinearLayoutManager(getContext()));

        adpNews.setmContext(getContext());
        adpTasks.setmContext(getContext());
        adpDynamic_hot.setmContext(getContext());
        adpDynamic_leader.setmContext(getContext());




        rvList1.setAdapter(adpNews);
        rvList4.setAdapter(adpTasks);
        rvList2.setAdapter(adpDynamic_hot);
        rvList3.setAdapter(adpDynamic_leader);

        rvList.setNestedScrollingEnabled(false);
        rvList1.setNestedScrollingEnabled(false);
        rvList2.setNestedScrollingEnabled(false);
        rvList3.setNestedScrollingEnabled(false);
        rvList4.setNestedScrollingEnabled(false);


        user_header = findViewById(R.id.iv_user_header);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_depart = findViewById(R.id.tv_user_depart);


        mv_not = findViewById(R.id.mv_not);


        bannerLayout = findViewById(R.id.bl_horizontal);




//        bannerLayout.setOnIndicatorIndexChangedListener(position -> ToastUtil.showToast(mContext,"轮播到第" + (position + 1) + "个"));
//
//        mAdapterHorizontal = new RecyclerViewBannerAdapter(new ArrayList<>());
//        bannerLayout.setAdapter(mAdapterHorizontal);
        //blVertical.setAdapter(mAdapterVertical = new RecyclerViewBannerAdapter(DemoDataProvider.urls));

        cv_class_room.setVisibility(View.GONE);
        cv_level.setVisibility(View.GONE);

//        mAdapterHorizontal.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                OORtCloudRoomData.CourseListBean bean = (OORtCloudRoomData.CourseListBean) mRooms.get(position);
//                DynamicActivityPlayVideo.start(mContext, bean.getVideo_url());
//                return;
//            }
//        });


        findViewById(R.id.tv_school_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService("oort.yum");
                OperLogUtil.msg("点击了更多" + "云课堂" +"包名:" + "com.cloud_classroom_mobile3.0.oort");
            }
        });
        findViewById(R.id.tv_gnyw_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService("com.news_center_app.oort");
                OperLogUtil.msg("点击了更多" + "新闻" +"包名:" + "com.news_center_app.oort");
            }
        });
        findViewById(R.id.tv_rwzx_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivityTaskLsit.start(mContext);
                OperLogUtil.msg("点击了更多" + "任务中心");
            }
        });

        findViewById(R.id.tv_jxrt_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startService("com.work_dynamics.oort");
                MessageEventChangeUI ev = new MessageEventChangeUI(3);
                ev.setTabIndex(3);

                EventBus.getDefault().post(ev);

                OperLogUtil.msg("点击了更多" + "警信热帖");
            }
        });

        findViewById(R.id.tv_lddp_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startService("com.work_dynamics.oort");

                MessageEventChangeUI ev = new MessageEventChangeUI(3);
                ev.setTabIndex(0);

                EventBus.getDefault().post(ev);
                OperLogUtil.msg("点击了更多" + "领导点评");
            }
        });

        findViewById(R.id.tv_sypm_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startService("com.work_dynamics.oort");

                startService("com.daily_activity.oort");
                OperLogUtil.msg("点击了更多" + "使用排名" +"包名:" + "com.daily_activity.oort");
            }
        });
        findViewById(R.id.tv_add_module).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(getContext(), AppStoreActivity.class);
                getContext().startActivity(in);
            }
        });

        findViewById(R.id.tv_add_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(getContext(), AppStoreActivity.class);
                getContext().startActivity(in);
            }
        });







        leafSquareChart = findViewById(R.id.leaf_chart);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mChartColor = getContext().getColor(R.color.main_color);
        }else {
            mChartColor = MyApplication.getAppContext().getApplicationContext().getResources().getColor(R.color.main_color);

        }
        //initSquareChart();



        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(rl -> {
            OperLogUtil.msg("首页下拉刷新");
            AppStatu.getInstance().appStatu = 0;

            AppSetConfig.getInstance().resquest();
            getData();
        });


        getData();EventBus.getDefault().register(this);

        AppSetConfig.getInstance().addCallback(new AppSetConfig.ConfigCallback() {
            @Override
            public void callback(AppSetConfig.ConfigData.SolutionBean solutionBean) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(solutionBean.getAppSetting() != null && solutionBean.getAppSetting().getBasicConfig() != null ) {
                            ImageLoader.loadImage(findViewById(R.id.iv_banner), solutionBean.getAppSetting().getBasicConfig().getAppBanner(), R.mipmap.icon_banner);
                        }
                    }
                });

            }
        });

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        if(messageEvent.equals("applyStatu")){
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppStoreChangeMessage messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        getData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageRefrashHomeApp messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");

        getApps();
    }





    private void  startService(String packageName){

        if(getContext() == null){
            return;
        }
        //String packageName = "com.jwb_home.oort";
        String params = "";
        Intent intent = new Intent(getContext() , AppManagerService.class);
        intent.putExtra("packageName" , packageName);
        intent.putExtra("params" , params);
        getContext().startService(intent);


    }






    public void getApps() {

        String token = FastSharedPreferences.get("USERINFO_SAVE").getString("token", "");
        String uuid = UserInfoUtils.getInstance(getContext()).getUserId();
        String imuserid = UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getImuserid();

        String record = FastSharedPreferences.get("httpRes").getString("home_moduleList_" + uuid, "");


        if (cv_apps == null) {
            return;
        }
        if (record.length() > 0) {
            parasDatas(record);
        } else {
            //cv_apps.setVisibility(View.GONE);
        }
        HttpRequestParam.moduleList(token, uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                if (!s.equals(record)) {


                    parasDatas(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg", e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });
    }



        public void getData(){

        String token=  FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        String imuserid= UserInfoUtils.getInstance(getContext()).getLoginUserInfo().getImuserid();

        String  record =  FastSharedPreferences.get("httpRes").getString("home_moduleList_" + uuid,"");


        if(cv_apps == null){
            return;
        }
        if(record.length() > 0){
            parasDatas(record);
        }else{
            //cv_apps.setVisibility(View.GONE);
        }
        HttpRequestParam.moduleList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                if(!s.equals(record)) {



                    parasDatas(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });

//        if(BuildConfig.DEBUG){
//            return;
//        }

        String  record01 =  FastSharedPreferences.get("httpRes").getString("cloudSchool_" + uuid,"");
        if(record01.length() > 0){
            parasDatas_classroom(record01);
        }else {
            cv_gnyw.setVisibility(View.GONE);

        }
        HttpRequestParam.cloudSchool(token , uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                if(!s.equals(record01)) {

                    FastSharedPreferences.get("httpRes").edit().putString("cloudSchool_" + uuid, s).apply();
//
                    parasDatas_classroom(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });



        getTaskInfo(imuserid,PUBLIC_USERID,PUBLIC_NUM);
        FastSharedPreferences info = FastSharedPreferences.get("TASKINFO");
        String taskinfo = info.getString("task","");




        String  record1 =  FastSharedPreferences.get("httpRes").getString("GAYWList_" + uuid,"");
        if(record1.length() > 0){
            parasDatas_news(record1);
        }else {
            cv_gnyw.setVisibility(View.GONE);

        }
        HttpRequestParam.GAYWList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                if(!s.equals(record1)) {

                    FastSharedPreferences.get("httpRes").edit().putString("GAYWList_" + uuid, s).apply();
//
                    parasDatas_news(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });

        String  record2 =  FastSharedPreferences.get("home_httpRes").getString("JXRTList_" + uuid,"");
        if(record2.length() > 0){
            parasDatas_hot(record2);
        }else {
            cv_jxrt.setVisibility(View.GONE);

        }
        HttpRequestParam.JXRTList(token).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                if(!s.equals(record2)) {
                    cv_jxrt.setVisibility(View.VISIBLE);
                    FastSharedPreferences.get("home_httpRes").edit().putString("JXRTList_" + uuid, s).apply();

                    parasDatas_hot(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });

        String  record3 =  FastSharedPreferences.get("home_httpRes").getString("lddpList_" + uuid,"");
        if(record3.length() > 0){
            //parasDatas_leader(record3);
        }else {
            cv_lddp.setVisibility(View.GONE);
        }
        HttpRequestParam.lddpList(token).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
//                if(!s.equals(record3)) {
                cv_lddp.setVisibility(View.VISIBLE);
                FastSharedPreferences.get("home_httpRes").edit().putString("lddpList_" + uuid, s).apply();
                parasDatas_leader(s);
//                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });


        String  record4 =  FastSharedPreferences.get("home_httpRes").getString("statistics_" + uuid,"");
        if(record4.length() > 0){
            parasDatas_level(record4);
        }else {

        }
//        cv_level.setVisibility(View.VISIBLE);
        HttpRequestParam.statistics(token).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                if(!s.equals(record4)) {
                    FastSharedPreferences.get("home_httpRes").edit().putString("statistics_" + uuid, s).apply();
                    parasDatas_level(s);;
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                mRefreshLayout.closeHeaderOrFooter();
            }
        });




        com.oortcloud.basemodule.user.UserInfo userInfo =  UserInfoUtils.getInstance(getContext()).getLoginUserInfo();


        if(userInfo != null){
            ImageLoader.loadImage(user_header,userInfo.getOort_photo(),R.mipmap.ic_default_avatar);
            tv_user_name.setText(userInfo.getOort_name());
            tv_user_depart.setText(userInfo.getOort_depname());
            getNotice(userInfo.getOort_depcode());
        }
        HttpRequestCenter.GetUserInfo().subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {

                OperLogUtil.msg(s);
                Result<Data<UserInfo>> result = new Gson().fromJson(s,new TypeToken<Result<Data<UserInfo>>>(){}.getType());
                if (result.isok()){
                    if (result.getData().getUserInfo().getImstatus()!=null){
                        String nameState = result.getData().getUserInfo().getImstatus();
                        ImageLoader.loadImage(user_header,result.getData().getUserInfo().getOort_photo(),R.mipmap.ic_default_avatar);
                        tv_user_name.setText(result.getData().getUserInfo().getOort_name());
                        tv_user_depart.setText(result.getData().getUserInfo().getOort_depname());
                        getNotice(result.getData().getUserInfo().getOort_depcode());

                    }else{
                    }
                }
            }
        });


    }

    void getNotice(String orginId){



//        if(BuildConfig.DEBUG){
//            mv_not.setVisibility(View.VISIBLE);
//
//            ArrayList datas = new ArrayList<>();
//
//            datas.add("ssdafffaffwfwef");
//            datas.add("3333333333333");
//            datas.add("344444444444");
//            datas.add("5555555555");
//            datas.add("666666666666666");
//            OortNoticeMF marqueeFactory1 = new OortNoticeMF(getContext());
//            mv_not.setMarqueeFactory(marqueeFactory1);
//            mv_not.startFlipping();
//            marqueeFactory1.setOnItemClickListener((view, holder) -> startService("com.notice_center_app.oort"));
//            marqueeFactory1.setData(datas);
//            return;
//        }



        String token=  FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        HttpRequestParam.notice_list_unread(token,uuid,orginId).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                ResArr<OortNotice> res = new Gson().fromJson(s,new TypeToken<ResArr<OortNotice>>(){}.getType());
                if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null) {


                    if(res.getData().getList().size() > 0){
                        mv_not.setVisibility(View.VISIBLE);

                        ArrayList datas = new ArrayList<>();

                        for(OortNotice no : res.getData().getList()){
                            datas.add(no.getContent());

                        }

//
//                        final List<String> datas = Arrays.asList("《赋得古原草送别》", "离离原上草，一岁一枯荣。", "野火烧不尽，春风吹又生。", "远芳侵古道，晴翠接荒城。", "又送王孙去，萋萋满别情。","测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");

                        OortNoticeMF marqueeFactory1 = new OortNoticeMF(getContext());
                        mv_not.setMarqueeFactory(marqueeFactory1);
                        mv_not.startFlipping();
                        marqueeFactory1.setOnItemClickListener((view, holder) -> startService("com.notice_center_app.oort"));
                        marqueeFactory1.setData(datas);
                    }else {
                        mv_not.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    void getTaskInfo(String userid,String publicId,int pagesize){
        List<TaskMsgInfoBean> taskInfos =  ChatMessageDao.getInstance().getTaskMsgList(userid,publicId,pagesize);
        if(taskInfos != null && taskInfos.size() > 0){
            cv_rwzx.setVisibility(View.VISIBLE);
            adpTasks.refresh(taskInfos);
            adpTasks.setOnItemClickListener(new AdapterTasks.ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    TaskMsgInfoBean t = taskInfos.get(position);
                    startService(t.getAppid());
                    OperLogUtil.msg("startService_taskInfos" + JSON.toJSONString(taskInfos));
                }

                @Override
                public void onItemCheckClick(int position) {

                }

                @Override
                public void onItemLongClick(boolean edit) {

                }
            });
        }else{
            cv_rwzx.setVisibility(View.GONE);
        }

    }

    void parasDatas(String s){

        if(getContext() == null){
            return;
        }
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;

        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        ResArr<ModuleInfo<AppInfo>> res = JSON.parseObject(d,new TypeToken<ResArr<ModuleInfo<AppInfo>>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null){

            cv_apps.setVisibility(View.VISIBLE);
            FastSharedPreferences.get("httpRes").edit().putString("home_moduleList_" + uuid, s).apply();

            mTypes.clear();
            mCTypes.clear();

            for(ModuleInfo b : res.getData().getList()){
                if(b.getHomepage_type() == 0){
                    continue;
                }else{
                    mTypes.add(b);
                }
            }

            for(Object o : mTypes){
                ModuleInfo b = (ModuleInfo) o;
                if(b.getApp_list() == null){
                    continue;
                }
                for(Object o1 : b.getApp_list()){
                    AppInfo app = (AppInfo) o1;
                    mCTypes.add(app);
                    if(mCTypes.size() == 10){
                        break;
                    }
                }
                if(mCTypes.size() == 10){
                    break;
                }
            }
            setDatas(GroupModel.getAppGroups(mTypes));

        }
    }

    void parasDatas_classroom(String s){

        if(getContext() == null){
            return;
        }
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;
        ResObj<OORtCloudRoomData> res = JSON.parseObject(d,new TypeToken<ResObj<OORtCloudRoomData>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null){



            OORtCloudRoomData data = res.getData();
            if(data.getCounts() < 1){
                cv_class_room.setVisibility(View.GONE);
            }else{

                cv_class_room.setVisibility(View.VISIBLE);
                ArrayList lis = new ArrayList();
                mRooms = new ArrayList();
                for(OORtCloudRoomData.CourseListBean bean : data.getCourse_list()){

                    if(!StringUtil.isBlank(bean.getCover_url()) && !StringUtil.isBlank(bean.getVideo_url())) {
                        lis.add(bean.getCover_url());
                        mRooms.add(bean);
                    }else{

                    }

                }

                bannerLayout.setPages(mRooms, new MZHolderCreator<BannerViewHolder>() {
                    @Override
                    public BannerViewHolder createViewHolder() {
                        return new BannerViewHolder();
                    }
                });
                // mAdapterHorizontal.refresh(lis);
            }

        }else{
            cv_class_room.setVisibility(View.GONE);
        }
    }



    void parasDatas_news(String s){

        if(getContext() == null){
            return;
        }
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;
        ResArr<OORTGANews> res = JSON.parseObject(d,new TypeToken<ResArr<OORTGANews>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null && res.getData().getList().size() > 0){


            cv_gnyw.setVisibility(View.VISIBLE);
            adpNews.refresh(res.getData().getList());

        }else{
            cv_gnyw.setVisibility(View.GONE);
        }
    }

    void parasDatas_hot(String s){

        if(getContext() == null){
            return;
        }

        Log.d("TAG", "000000getItemCount: parasDatas_hot" + s);
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;
        ResArr<DynamicBean> res = JSON.parseObject(d,new TypeToken<ResArr<DynamicBean>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null && res.getData().getList().size() > 0){


            cv_jxrt.setVisibility(View.VISIBLE);
            ArrayList arr = new ArrayList();
            for(DynamicBean db : res.getData().getList()){
                OORTDynamic dy = new OORTDynamic();
                dy.setDynamic(db);
                dy.setUserInfo(res.getData().getUserInfo());
                arr.add(dy);
            }

            Log.d("TAG", "000000getItemCount: parasDatas_hotsss" + arr.size());
            adpDynamic_hot.refresh(arr);
        }else{
            cv_jxrt.setVisibility(View.GONE);
        }
    }

    void parasDatas_leader(String s){


        String token=  FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();

        String  record =  FastSharedPreferences.get("httpRes").getString("moduleList_" + uuid,"");

        if(getContext() == null){
            return;
        }
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;
        ResArr<OORTDynamicReview> res = JSON.parseObject(d,new TypeToken<ResArr<OORTDynamicReview>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null && res.getData().getList().size() > 0){

            cv_lddp.setVisibility(View.VISIBLE);
            ArrayList arr = new ArrayList();
            lddpCount = 0;

            for(OORTDynamicReview db : res.getData().getList()){
                OORTDynamic dy = new OORTDynamic();
                dy.setDynamic(db.getDynamic());
                dy.setUserInfo(res.getData().getUserInfo());
                arr.add(dy);
                int index = arr.indexOf(dy);

                HttpRequestParam.dynamic_info(token,dy.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s1) {

                        ResObj<OORTDynamic> res = JSON.parseObject(s1,new TypeToken<ResObj<OORTDynamic>>() {}.getType());//
                        if(res.getCode() == 200 && res.getData() != null){
                            //dy.setDynamic(res.getData());
                            arr.set(index,res.getData());
                            lddpCount ++ ;
                            if(lddpCount == arr.size()){
                                adpDynamic_leader.refresh(arr);
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("msg" , e.toString());
                        mRefreshLayout.closeHeaderOrFooter();
                    }
                });
            }

            //adpDynamic_leader.refresh(arr);
        }else {
            cv_lddp.setVisibility(View.GONE);
        }
    }

    void parasDatas_level(String s){

        if(getContext() == null){
            return;
        }
        if(mRefreshLayout != null) {
            mRefreshLayout.closeHeaderOrFooter();
        }

        String d = s;
        ResArr<OORTStatistics> res = JSON.parseObject(d,new TypeToken<ResArr<OORTStatistics>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null){

            if(!res.getData().getList().isEmpty()){
                cv_level.setVisibility(View.VISIBLE);
            }
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            // 获取最大数量，用于 Y 轴标签
            int maxNum = 10;
            for (int i = 0; i < res.getData().getList().size(); i++) {
                OORTStatistics sc = res.getData().getList().get(i);
                entries.add(new BarEntry(i, sc.getCount()));//sc.getCount()



                String deptName = sc.getDeptname();
                if (deptName.length() > 5) {
                    deptName = deptName.substring(0, 5);
                }
                labels.add(deptName);
                if (sc.getCount() > maxNum) {
                    maxNum = sc.getCount();
                }
            }



            // 配置柱状图数据
            BarDataSet dataSet = new BarDataSet(entries, "部门统计");
            dataSet.setColor(getContext().getColor(R.color.btn_color));
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(12f);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int)value);
                }
            });
            barChart = leafSquareChart;
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setAxisMinimum(-0.5f);
            xAxis.setAxisMaximum(res.getData().getList().size() - 0.5f);
            xAxis.setTextSize(11);
            xAxis.setLabelRotationAngle(-45);

            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

            barChart.setVisibleXRangeMaximum(7);
            barChart.moveViewToX(-0.5f);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(maxNum);
            leftAxis.setDrawGridLines(false);

            barChart.getAxisRight().setEnabled(false);



            BarData data = new BarData(dataSet);
            data.setBarWidth(0.5f);
            barChart.setData(data);
            barChart.invalidate();


//        {
//
//            int maxNum = 5;
//            List<AxisValue> axisValues = new ArrayList<>();
//            List<PointValue> pointValues = new ArrayList<>();
//            int index = 0;
//            for (OORTStatistics sc : res.getData().getList()) {
//
//                if (maxNum < sc.getCount()) {
//                    maxNum = sc.getCount();
//                }
//                AxisValue value = new AxisValue();
//                value.setLabel(sc.getDeptname());
//                axisValues.add(value);
//
//
//                PointValue pointValue = new PointValue();
//                pointValue.setX((index) / (res.getData().getList().size()-1));
//                int var = (int) (sc.getCount());
//                pointValue.setLabel(String.valueOf(var));
//                pointValue.setShowLabel(true);
//                pointValue.setY(var);
//                pointValues.add(pointValue);
//
//
//                index ++ ;
//            }
//
//            List<AxisValue> axisValuesY = new ArrayList<>();
//            for (int i = 0; i < 6; i++) {
//                AxisValue value = new AxisValue();
//                value.setLabel(String.valueOf(i * (maxNum / 5)));
//                axisValuesY.add(value);
//            }
//            Square square = new Square(pointValues);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                square.setBorderColor(getContext().getColor(R.color.main_color))
//                        .setWidth(20)
//                        .setFill(true)
//                        .setHasLabels(true)
//                        .setLabelColor(getContext().getColor(R.color.main_color));
//            }else {
//                square.setBorderColor(MyApplication.getAppContext().getApplicationContext().getResources().getColor(R.color.main_color))
//                        .setWidth(20)
//                        .setFill(true)
//                        .setHasLabels(true)
//                        .setLabelColor(MyApplication.getAppContext().getApplicationContext().getResources().getColor(R.color.main_color));
//            }
//            Axis axisX = new Axis(axisValues);
//            axisX.setAxisColor(mChartColor).setTextColor(Color.DKGRAY).setHasLines(false).setAxisWidth(2);
//            Axis axisY = new Axis(axisValuesY);
//            axisY.setAxisColor(mChartColor).setTextColor(Color.DKGRAY).setHasLines(false).setAxisWidth(2).setShowText(true);
//
//            leafSquareChart.setAxisX(axisX);
//            leafSquareChart.setAxisY(axisY);
//            leafSquareChart.setChartData(square);
//
//
        }

    }





    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }





//    private void initSquareChart() {
//        Axis axisX = new Axis(getAxisValuesX());
//        axisX.setAxisColor(mChartColor).setTextColor(Color.DKGRAY).setHasLines(false).setAxisWidth(2);
//        Axis axisY = new Axis(getAxisValuesY());
//        axisY.setAxisColor(mChartColor).setTextColor(Color.DKGRAY).setHasLines(false).setAxisWidth(2).setShowText(true);
//
//        leafSquareChart.setAxisX(axisX);
//        leafSquareChart.setAxisY(axisY);
//        leafSquareChart.setChartData(getSquares());
//    }
//
//
//    private Square getSquares(){
//        List<PointValue> pointValues = new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//            PointValue pointValue = new PointValue();
//            pointValue.setX( (i - 1) / 11f);
//            int var = (int) (Math.random() * 100);
//            pointValue.setLabel(String.valueOf(var));
//            pointValue.setShowLabel(true);
//            pointValue.setY(var / 100f);
//            pointValues.add(pointValue);
//        }
//
//        Square square = new Square(pointValues);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            square.setBorderColor(getContext().getColor(R.color.main_color))
//                    .setWidth(20)
//                    .setFill(true)
//                    .setHasLabels(true)
//                    .setLabelColor(getContext().getColor(R.color.main_color));
//        }else {
//            square.setBorderColor(MyApplication.getAppContext().getApplicationContext().getResources().getColor(R.color.main_color))
//                    .setWidth(20)
//                    .setFill(true)
//                    .setHasLabels(true)
//                    .setLabelColor(MyApplication.getAppContext().getApplicationContext().getResources().getColor(R.color.main_color));
//        }
//        return square;
//    }
//
//
//    private List<AxisValue> getAxisValuesX(){
//        List<AxisValue> axisValues = new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//            AxisValue value = new AxisValue();
//            value.setLabel(i + "");
//            axisValues.add(value);
//        }
//        return axisValues;
//    }
//
//    private List<AxisValue> getAxisValuesY(){
//        List<AxisValue> axisValues = new ArrayList<>();
//        for (int i = 0; i < 11; i++) {
//            AxisValue value = new AxisValue();
//            value.setLabel(String.valueOf(i * 10));
//            axisValues.add(value);
//        }
//        return axisValues;
//    }
//
//
//    private Line getFoldLine(){
//        List<PointValue> pointValues = new ArrayList<>();
//        for (int i = 1; i <= 12; i++) {
//            PointValue pointValue = new PointValue();
//            pointValue.setX( (i - 1) / 11f);
//            float var = (float) (Math.random() * 100);
//            pointValue.setLabel(String.valueOf(var));
//            pointValue.setY(var / 100);
//            pointValues.add(pointValue);
//        }
//
//        Line line = new Line(pointValues);
//        line.setLineColor(Color.parseColor("#33B5E5")).setPointColor(Color.YELLOW).
//                setCubic(false).setPointRadius(3).setHasLabels(true);
//        return line;
//    }

    public static class BannerViewHolder implements MZViewHolder<OORtCloudRoomData.CourseListBean> {
        private ImageView mImageView;
        private ImageView play;
        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.home_banner_item,null);
            mImageView = (ImageView) view.findViewById(R.id.banner_image);
            play = (ImageView) view.findViewById(R.id.banner_play);
            return view;
        }

        @Override
        public void onBind(Context context, int position, OORtCloudRoomData.CourseListBean data) {
            // 数据绑定
            ImageLoader.loadImage(mImageView,data.getCover_url(),R.mipmap.icon_banner);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {;
                    ActivityVideoPlayer.start(context, data.getVideo_url());
                }
            });

        }
    }
}