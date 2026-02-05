package com.oortcloud.appstore.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.AppDetailedActivity;
import com.oortcloud.appstore.activity.CommentActivity;
import com.oortcloud.appstore.adapter.CommentAdapter;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Comment;
import com.oortcloud.appstore.bean.Count;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Grade;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.PageDetailedCommentLayoutBinding;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.widget.RatingBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @filename:
 * @function： 应用详情评论
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/11 14:32
 */
public class DetailedCommentPageFragment  extends BaseFragment{


    private TextView mScore;
    private TextView mTinySpot1;
    private ProgressBar mProgreScore1;
    private TextView mTinySpot2;
    private ProgressBar mProgreScore2;
    private TextView mTinySpot3;
    private ProgressBar mProgreScore3;
    private TextView mTinySpot4;
    private ProgressBar mProgreScore4;
    private TextView mTinySpot5;
    private ProgressBar mProgreScore5;
    private LinearLayout mCommentClockLayout;
    private RatingBarView mCommentRating;
    private RecyclerView mCommentRV;
    private TextView mCountTv;
 // 替换为实际生成的 Binding 类（如 ActivityMainBinding）
    private static final String OBJECT_KEY = "object_key";

    private CommentAdapter mCommentAdapter;
    private AppInfo appInfo;
    private com.oortcloud.appstore.databinding.PageDetailedCommentLayoutBinding binding;

    public static Fragment instantiate(AppInfo info) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBJECT_KEY, info);
        Fragment fragment = new DetailedCommentPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getRootView() {
        binding = PageDetailedCommentLayoutBinding.inflate(getLayoutInflater());

        mScore = binding.tvScore;
        mTinySpot1 = binding.tvTinySpotScore1;
        mProgreScore1 = binding.progressScore1;
        mTinySpot2 = binding.tvTinySpotScore2;
        mProgreScore2 = binding.progressScore2;
        mTinySpot3 = binding.tvTinySpotScore3;
        mProgreScore3 = binding.progressScore3;
        mTinySpot4 = binding.tvTinySpotScore4;
        mProgreScore4 = binding.progressScore4;
        mTinySpot5 = binding.tvTinySpotScore5;
        mProgreScore5 = binding.progressScore5;
        mCommentClockLayout = binding.commentClickLayout;
        mCommentRating = binding.ratingBarView;
        mCommentRV = binding.commentRecycleView;
        mCountTv = binding.countTv;
        return binding.getRoot();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.page_detailed_comment_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        if (bundle != null){
            appInfo = (AppInfo) bundle.getSerializable(OBJECT_KEY);
        }

        EventBus.getDefault().register(this);
    }




    @Override
    protected void initData() {

        HttpRequestCenter.replySystemList(1  , appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Data<Comment>> result = new Gson().fromJson(s, new TypeToken<Result<Data<Comment>>>(){}.getType());
                if (result.isok()){
                    List list = result.getData().getLists();
                    if (list != null){
                        mCommentAdapter.setData(list);
                        if (list.size() > 0){
                            mCountTv.setText(list.size() + "人评论");
                        }else {
                            mCountTv.setText("");
                        }

                    }
                }
            }

        });

        HttpRequestCenter.getGrade(appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Grade> result = new Gson().fromJson(s, new TypeToken<Result<Grade>>(){}.getType());
                if (result.isok()){
                    Grade score  = result.getData();
                    if (score != null){
                        mScore.setText(String.valueOf(score.getScore()));
                        if (mContext instanceof AppDetailedActivity){
                            AppDetailedActivity appDetailedActivity = (AppDetailedActivity)mContext;
                            appDetailedActivity.setScore(score.getScore());
                        }
                    }
                }
            }

        });

        HttpRequestCenter.getGradelevel(appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Result<Count> result = new Gson().fromJson(s, new TypeToken<Result<Count>>(){}.getType());
                if (result.isok()){
                    Count count = result.getData();

                    if (count.getCount() != 0 ){
                        mProgreScore1.setMax(count.getCount());
                        mProgreScore2.setMax(count.getCount());
                        mProgreScore3.setMax(count.getCount());
                        mProgreScore4.setMax(count.getCount());
                        mProgreScore5.setMax(count.getCount());
                        mProgreScore1.setProgress(count.getCount5() / count.getCount());
                        mProgreScore2.setProgress(count.getCount4() / count.getCount());
                        mProgreScore3.setProgress(count.getCount3() / count.getCount());
                        mProgreScore4.setProgress(count.getCount2() / count.getCount());
                        mProgreScore5.setProgress(count.getCount1() / count.getCount());
                    }

                }
            }

        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventCommentUpdate message) {
        initData();
    }

    @Override
    protected void initView() {
        mCommentAdapter = new CommentAdapter(mContext);
        mCommentRV.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.VERTICAL,false));
        mCommentRV.setAdapter(mCommentAdapter);
    }

    @Override
    protected void initEvent() {
        mCommentClockLayout.setOnClickListener(v->{
            CommentActivity.actionStart(mContext , appInfo);
        });
        mCommentRating.setOnRatingChangeListener((float ratingCount) -> {
                appInfo.setScore(ratingCount);
                CommentActivity.actionStart(mContext , appInfo);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isRequest) {
            initData();
            isRequest = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
