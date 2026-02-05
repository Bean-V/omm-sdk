package com.oort.weichat.fragment.dynamic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.adapter.PublicMessageRecyclerAdapter;
import com.oort.weichat.bean.circle.Comment;
import com.oort.weichat.bean.event.EventAvatarUploadSuccess;
import com.oort.weichat.bean.event.MessageEventHongdian;
import com.oort.weichat.db.dao.CircleMessageDao;
import com.oort.weichat.downloader.Downloader;
import com.oort.weichat.fragment.entity.DynamicBean;
import com.oort.weichat.fragment.entity.DynamicMyinfo;
import com.oort.weichat.fragment.entity.DynamicProfile;
import com.oort.weichat.fragment.entity.DynamicUser;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oort.weichat.fragment.entity.ResArr;
import com.oort.weichat.fragment.entity.ResData;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.circle.MessageEventComment;
import com.oort.weichat.ui.circle.MessageEventNotifyDynamic;
import com.oort.weichat.ui.circle.MessageEventReply;
import com.oort.weichat.ui.circle.SelectPicPopupWindow;
import com.oort.weichat.ui.circle.range.NewZanActivity;
import com.oort.weichat.ui.circle.range.SendAudioActivity;
import com.oort.weichat.ui.circle.range.SendFileActivity;
import com.oort.weichat.ui.circle.range.SendShuoshuoActivity;
import com.oort.weichat.ui.circle.range.SendVideoActivity;
import com.oort.weichat.util.CameraUtil;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.MergerStatus;
import com.oort.weichat.view.TrillCommentInputDialog;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;
import com.oortcloud.login.net.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.MessageEvent;
import okhttp3.Call;

/**
 * 朋友圈的Fragment
 * Created by Administrator
 */

public class FragmentDynamicList extends EasyFragment {
    private static final int REQUEST_CODE_SEND_MSG = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static int PAGER_SIZE = 10;
    private String mUserId;
    private String mUserName;
    private TextView mTvTitle;
    private ImageView mIvTitleRight;
    private SelectPicPopupWindow menuWindow;
    // 头部
    private View mHeadView;
    private ImageView ivHeadBg, ivHead;
    // 通知...
    private LinearLayout mTipLl;
    private ImageView mTipIv;
    private TextView mTipTv;
    // 页面
    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mListView;
    private DynamicListAdapter mAdapter;
    private List<OORTDynamic> mMessages = new ArrayList<>();
    private boolean more;
    private String messageId;
    private boolean showTitle = true;
    private ArrayList mDatas;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    private int contentType = 3;

    private int mLoadType = 3;

    public String getOort_tuuid() {
        return oort_tuuid;
    }

    public void setOort_tuuid(String oort_tuuid) {
        this.oort_tuuid = oort_tuuid;
    }

    private String oort_tuuid;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        requestData(1);
    }

    private String searchKey;

    public String getOort_duuid() {
        return oort_duuid;
    }

    public void setOort_duuid(String oort_duuid) {
        this.oort_duuid = oort_duuid;
    }

    private String oort_duuid;

    public String getOort_uuuid() {
        return oort_uuuid;
    }

    public void setOort_uuuid(String oort_uuuid) {
        this.oort_uuuid = oort_uuuid;
    }

    private String oort_uuuid;

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            if (menuWindow != null) {
                // 顶部一排按钮复用这个listener, 没有menuWindow,
                menuWindow.dismiss();
            }
            Intent intent = new Intent();
            int id = v.getId();
            if (id == R.id.btn_send_picture) {// 发表图文，
                intent.setClass(getActivity(), SendShuoshuoActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEND_MSG);
            } else if (id == R.id.btn_send_voice) {// 发表语音
                intent.setClass(getActivity(), SendAudioActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEND_MSG);
            } else if (id == R.id.btn_send_video) {// 发表视频
                intent.setClass(getActivity(), SendVideoActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEND_MSG);
            } else if (id == R.id.btn_send_file) {// 发表文件
                intent.setClass(getActivity(), SendFileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEND_MSG);
            } else if (id == R.id.new_comment) {// 最新评论&赞
                Intent intent2 = new Intent(getActivity(), NewZanActivity.class);
                intent2.putExtra("OpenALL", true);
                startActivity(intent2);
                mTipLl.setVisibility(View.GONE);
                EventBus.getDefault().post(new MessageEventHongdian(0));
            }
        }
    };
    private MergerStatus mergerStatus;
    private RelativeLayout rl_title;

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_dynamic_list;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initActionBar();
        Downloader.getInstance().init(MyApplication.getInstance().mAppDir + File.separator + coreManager.getSelf().getUserId()
                + File.separator + Environment.DIRECTORY_MOVIES);// 初始化视频下载目录
        initViews();
        initData();

//        EventBus.getDefault().register(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.stopVoice();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // 退出页面时关闭视频和语音，

    }

    private void initActionBar() {

    }

    public void initViews() {
        more = true;
        mUserId = coreManager.getSelf().getUserId();
        mUserName = coreManager.getSelf().getNickName();
        // ---------------------------初始化主视图-----------------------

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mListView = findViewById(R.id.recyclerView);
        mListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRefreshLayout = findViewById(R.id.refreshLayout);

        mRefreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(true);

        ClassicsFooter footer = new ClassicsFooter(mContext);
        footer.setFinishDuration(0); // 加载完成后不自动隐藏 Footer
        mRefreshLayout.setRefreshFooter(footer);

        //mRefreshLayout.setRefreshFooter(new CustomFooter(mContext));
       // mListView.addHeaderView(mHeadView);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            pageIndex = 1;
            requestData(1);
        });




        if(contentType != 10){
            mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
                requestData(2);
            });
        }else{
            mRefreshLayout.setEnableLoadMore(false);
        }



        //EventBus.getDefault().register(this);

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalScroll;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    public void initData() {
        mAdapter = new DynamicListAdapter(getActivity(), coreManager, mMessages,contentType);
        mAdapter.setSearchListener(new DynamicListAdapter.OnSearchListener() {
            @Override
            public void onSearch(String query) {
                setSearchKey(query);
            }
        });
        mListView.setAdapter(mAdapter);
        requestData(0);
        mAdapter.setOnDongTaiItemClickListener(new DynamicListAdapter.OnDongTaiItemClickListener() {
            @Override
            public void onItemClick() {
                //mListView.scrollTo(0,500);

               // int initialHeight = (int) (getResources().getDisplayMetrics().heightPixels * 1); // 50% 屏幕高度


                String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
                FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
                fragmentDynamicList.setContentType(16);
                fragmentDynamicList.setOort_uuuid(uuid);
                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "mydongtai");


            }
        });
    }


    void parasDatas(String s){

        if(getContext() == null){
            return;
        }


        String d = s;
        ResArr<DynamicBean> res = JSON.parseObject(d,new TypeToken<ResArr<DynamicBean>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null){

            if(mDatas == null) {
                mDatas = new ArrayList();
            }

            if(pageIndex == 1){
                mDatas.clear();
            }

            for(DynamicBean db : res.getData().getList()){
                OORTDynamic dy = new OORTDynamic();
                dy.setDynamic(db);
                dy.setUserInfo(res.getData().getUserInfo());
                mDatas.add(dy);
            }

            mAdapter.refresh(mDatas);


            if(mDatas.size() == 0){

                if(contentType != 8) {
                    showEmpty();
                }
            }else{
                hideEmpty();
            }

            mRefreshLayout.finishRefresh();
            if(res.getData().getPage() == res.getData().getPages()){
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            }else{
                mRefreshLayout.finishLoadMore();
                pageIndex ++ ;
            }
        }
    }


    void parasDatas_cache(String s){

        if(getContext() == null){
            return;
        }


        String d = s;
        ResArr<DynamicBean> res = JSON.parseObject(d,new TypeToken<ResArr<DynamicBean>>() {}.getType());//
        if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null){

            if(mDatas == null) {
                mDatas = new ArrayList();
            }

            if(pageIndex == 1){
                mDatas.clear();
            }

            for(DynamicBean db : res.getData().getList()){
                OORTDynamic dy = new OORTDynamic();
                dy.setDynamic(db);
                dy.setUserInfo(res.getData().getUserInfo());
                mDatas.add(dy);
            }

            if(mDatas.size() == 0){
                showEmpty();
            }else {
                hideEmpty();
                mAdapter.refresh(mDatas);
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DyamicListChangeEvent messageEvent) {
        Log.d("TAG", "onCreate: ===========执行");
        pageIndex = 1;

        requestData(contentType);
    }

    private int pageIndex = 1;
    private void requestData(int type) {

        mLoadType = type;

        String uuid= UserInfoUtils.getInstance(getContext()).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");



        if(contentType == 1){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_follow_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_follow_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_follow_list_" + uuid, s).apply();
                        }
                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }

                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 2){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_dept_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_dept_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_dept_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 3){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_all_List_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_all_List(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_all_List_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 4){

            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_isgrade2_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_isgrade2_list(mToken,pageIndex,oort_uuuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_isgrade2_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });


        }if(contentType == 5){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_isgrade1_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_isgrade1_list(mToken,pageIndex,oort_uuuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_isgrade1_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 6){

            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_follow_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_follow_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_follow_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
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
        if(contentType == 7){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_iscollect_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_iscollect_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_iscollect_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
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
        if(contentType == 8){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_user_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_user_list(mToken,pageIndex,uuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {

                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_user_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });


            HttpRequestParam.dynamic_myinfo(mToken).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    ResObj<DynamicMyinfo> res = JSON.parseObject(s,new TypeToken<ResObj<DynamicMyinfo>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){
                        mAdapter.setMyInfo(res.getData());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });


            HttpRequestParam.getUserInfo(uuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    ResObj<ResData<UserInfo>> res = JSON.parseObject(s,new TypeToken<ResObj<ResData<UserInfo>>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){
                        mAdapter.setUserInfo(res.getData().getUserInfo());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 9){

            HttpRequestParam.dynamic_topic_list(mToken,pageIndex,oort_tuuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                        parasDatas(s);
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 10){

            HttpRequestParam.dynamic_info(mToken, oort_duuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    ResObj<OORTDynamic> res = JSON.parseObject(s, new TypeToken<ResObj<OORTDynamic>>() {
                    }.getType());//
                    if (res.getCode() == 200 && res.getData() != null) {
                        ArrayList arr = new ArrayList();
                        arr.add(res.getData());
                        mAdapter.refresh(arr);
                    }
                    mRefreshLayout.closeHeaderOrFooter();
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg", e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });



        }if(contentType == 11) {

            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_like_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_like_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {
                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_like_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });
        }if(contentType == 12){


            HttpRequestParam.dynamic_comment_list(mToken,pageIndex).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                        parasDatas(s);
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });

        }if(contentType == 14){


            HttpRequestParam.dynamic_user_list(mToken,pageIndex,oort_uuuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                        parasDatas(s);

                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });


            HttpRequestParam.dynamic_profile(mToken,oort_uuuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    ResObj<DynamicProfile> res = JSON.parseObject(s,new TypeToken<ResObj<DynamicProfile>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){
                        mAdapter.setUserProfile(res.getData());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });



            if(uuid.equals(oort_duuid)){
                mAdapter.setFollowState(0);
                return;
            }

            HttpRequestParam.dynamic_followlist(mToken,uuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                        ResArr<DynamicUser> res = JSON.parseObject(s,new TypeToken<ResArr<DynamicUser>>() {}.getType());//
                        if(res.getCode() == 200 && res.getData() != null){
                            mAdapter.setFollowState(2);
                            for(DynamicUser info : res.getData().getList()){
                                if(info.getOort_userid().equals(oort_uuuid)){
                                    mAdapter.setFollowState(1);
                                }
                            }
                        }

                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });


        }if(contentType == 15){
            if(searchKey == null || searchKey.isEmpty()){
                mAdapter.refresh(new ArrayList());
                mRefreshLayout.finishLoadMoreWithNoMoreData();

                mRefreshLayout.closeHeaderOrFooter();

                return;
            }

            HttpRequestParam.dynamic_search_list(mToken,pageIndex,searchKey).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    parasDatas(s);
                }

                @Override
                public void onError(Throwable e) {
                    Log.v("msg" , e.toString());
                    mRefreshLayout.closeHeaderOrFooter();
                }
            });



        }
        if(contentType == 16){
            String  record2 =  FastSharedPreferences.get("httpRes").getString("dynamic_user_list_" + uuid,"");
            if(record2.length() > 0 && type == 0){
                parasDatas_cache(record2);
            }
            HttpRequestParam.dynamic_user_list(mToken,pageIndex,uuid).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    if(!s.equals(record2)) {

                        if(pageIndex == 1) {
                            FastSharedPreferences.get("httpRes").edit().putString("dynamic_user_list_" + uuid, s).apply();
                        }

                        parasDatas(s);
                    }else{
                        if(type != 0){
                            parasDatas(s);
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



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_SEND_MSG) {
            // 发布说说成功,刷新Fragment
            String messageId = data.getStringExtra(AppConstant.EXTRA_MSG_ID);
            CircleMessageDao.getInstance().addMessage(mUserId, messageId);
            requestData(1);
        } else if (requestCode == REQUEST_CODE_PICK_PHOTO) {
            if (data != null) {
                String path = CameraUtil.parsePickImageResult(data);
            } else {
                ToastUtil.showToast(requireContext(), R.string.c_photo_album_failed);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventAvatarUploadSuccess message) {
        if (message.event) {// 头像更新了，但该界面没有被销毁，不会去重新加载头像，所以这里更新一下

        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void helloEventBus(EventAvatarUploadSuccess message) {
//        if (message.event) {// 头像更新了，但该界面没有被销毁，不会去重新加载头像，所以这里更新一下
//
//        }
//    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEvent message) {
        if (message.message.equals("prepare")) {// 准备播放视频，关闭语音播放
            mAdapter.stopVoice();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(MessageEventNotifyDynamic message) {
        // 收到赞 || 评论 || 提醒我看  || 好友更新动态 协议 刷新页面
        requestData(1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventComment message) {
        TrillCommentInputDialog trillCommentInputDialog = new TrillCommentInputDialog(getActivity(),
                getString(R.string.enter_pinlunt),
                str -> {
                    Comment mComment = new Comment();
                    Comment comment = mComment.clone();
                    if (comment == null)
                        comment = new Comment();
                    comment.setBody(str);
                    comment.setUserId(mUserId);
                    comment.setNickName(mUserName);
                    comment.setTime(TimeUtils.sk_time_current_time());
                    addComment(message, comment);
                });
        Window window = trillCommentInputDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);// 软键盘弹起
            trillCommentInputDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventReply message) {
        if (message.event.equals("Reply")) {
            TrillCommentInputDialog trillCommentInputDialog = new TrillCommentInputDialog(getActivity(), getString(R.string.replay) + "：" + message.comment.getNickName(),
                    str -> {
                        Comment mComment = new Comment();
                        Comment comment = mComment.clone();
                        if (comment == null)
                            comment = new Comment();
                        comment.setToUserId(message.comment.getUserId());
                        comment.setToNickname(message.comment.getNickName());
                        comment.setToBody(message.comment.getToBody());
                        comment.setBody(str);
                        comment.setUserId(mUserId);
                        comment.setNickName(mUserId);
                        comment.setTime(TimeUtils.sk_time_current_time());
                        Reply(message, comment);
                    });
            Window window = trillCommentInputDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);// 软键盘弹起
                trillCommentInputDialog.show();
            }
        }
    }

    /**
     * 停止刷新动画
     */
    private void refreshComplete() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        }, 200);
    }

    public void updateTip() {

    }

    private void addComment(MessageEventComment message, final Comment comment) {
        String messageId = message.id;
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("messageId", messageId);
        if (comment.isReplaySomeBody()) {
            params.put("toUserId", comment.getToUserId() + "");
            params.put("toNickname", comment.getToNickname());
            params.put("toBody", comment.getToBody());
        }
        params.put("body", comment.getBody());

        HttpUtils.post().url(coreManager.getConfig().MSG_COMMENT_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<String>(String.class) {
                    @Override
                    public void onResponse(ObjectResult<String> result) {
                        // 评论成功
                        if (getContext() != null && Result.checkSuccess(requireContext(), result)) {
                            comment.setCommentId(result.getData());
                            message.pbmessage.setCommnet(message.pbmessage.getCommnet() + 1);
                            PublicMessageRecyclerAdapter.CommentAdapter adapter = (PublicMessageRecyclerAdapter.CommentAdapter) message.view.getAdapter();
                            adapter.addComment(comment);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(getActivity());
                    }
                });
    }

    /**
     * 回复
     */
    private void Reply(MessageEventReply event, final Comment comment) {
//        final int position = event.id;
//        final OORTDynamic message = mMessages.get(position);
//        Map<String, String> params = new HashMap<>();
//        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
//        params.put("messageId", message.getMessageId());
//
//        if (!TextUtils.isEmpty(comment.getToUserId())) {
//            params.put("toUserId", comment.getToUserId());
//        }
//        if (!TextUtils.isEmpty(comment.getToNickname())) {
//            params.put("toNickname", comment.getToNickname());
//        }
//        params.put("body", comment.getBody());
//
//        HttpUtils.post().url(coreManager.getConfig().MSG_COMMENT_ADD)
//                .params(params)
//                .build()
//                .execute(new BaseCallback<String>(String.class) {
//                    @Override
//                    public void onResponse(ObjectResult<String> result) {
//                        // 评论成功
//                        if (getContext() != null && Result.checkSuccess(requireContext(), result)) {
//                            comment.setCommentId(result.getData());
//                            message.setCommnet(message.getCommnet() + 1);
//                            PublicMessageRecyclerAdapter.CommentAdapter adapter = (PublicMessageRecyclerAdapter.CommentAdapter) event.view.getAdapter();
//                            adapter.addComment(comment);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        ToastUtil.showErrorNet(getActivity());
//                    }
//                });
    }

    /**
     * 定位到评论位置
     */
    public void showToCurrent(String mCommentId) {
//        int pos = -1;
//        for (int i = 0; i < mMessages.size(); i++) {
//            if (StringUtils.strEquals(mCommentId, mMessages.get(i).getMessageId())) {
//                pos = i + 2;
//                break;
//            }
//        }
//        // 如果找到就定位到这条说说
//        if (pos != -1) {
//            mListView.scrollToPosition(pos);
//        }
    }
}



