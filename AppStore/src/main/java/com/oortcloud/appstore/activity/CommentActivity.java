package com.oortcloud.appstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ActivityCommentLayoutBinding;
import com.oortcloud.appstore.fragment.MessageEventCommentUpdate;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.RatingBarView;

import org.greenrobot.eventbus.EventBus;


/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/9 10:31
 */
public class CommentActivity extends BaseActivity {

    TextView mCommentFen;
    RatingBarView mRatingBar;
    TextView mCommentEt;

    private AppInfo appInfo;

    @Override
    protected ActivityCommentLayoutBinding getViewBinding() {
        return ActivityCommentLayoutBinding.inflate(getLayoutInflater());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_layout;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        ActivityCommentLayoutBinding binding = getViewBinding();
         mCommentFen = binding.commentFen;
         mRatingBar = binding.ratingBarView;
         mCommentEt = binding.commentEt;
        mTitle.setText("写评论");
        mBtnItem.setText("提交");

        Intent intent = getIntent();
        if (intent != null){
            appInfo = (AppInfo) intent.getSerializableExtra("object_key");
            mRatingBar.setStar(appInfo.getScore());
            mCommentFen.setText(appInfo.getScore() + "");

        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    int flag = 0;
    @Override
    protected void initEvent() {
        mImgBack.setOnClickListener(view ->  {
            finish();
        });

        mRatingBar.setOnRatingChangeListener((float ratingCount) -> {
            mCommentFen.setText(ratingCount + "");
        });

        mBtnItem.setOnClickListener(v-> {
            String commentStr = mCommentEt.getText().toString().trim();
            String grade  = mCommentFen.getText().toString().trim();
            if (TextUtils.isEmpty(commentStr)){
                ToastUtils.showBottom("请输入您的反馈和建议");
            }else {
                if (appInfo != null) {
                    HttpRequestCenter.replySystemAdd(commentStr, appInfo.getUid(), 0, appInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>(){}.getType());
                            if (result.isok()){
                                ToastUtils.showBottom("评论成功");
                                if(flag == 1) {

                                    EventBus.getDefault().post(new MessageEventCommentUpdate());
                                    finish();

                                    return;
                                }
                                flag = 1;
                            }else{
                                ToastUtils.showBottom(result.getMsg());
                            }

                        }
                    });

                    HttpRequestCenter.replySystemGrade(appInfo.getUid() , Float.parseFloat(grade)).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Result result = new Gson().fromJson(s, new TypeToken<Result>(){}.getType());
                            if (result.isok()){
                                if(flag == 1) {
                                    EventBus.getDefault().post(new MessageEventCommentUpdate());
                                    finish();

                                    return;
                                }
                                flag = 1;
                            }else{
                                ToastUtils.showBottom(result.getMsg());
                            }

                        }
                    });

                }
            }

        });
    }

    public  static  void actionStart(Context context , AppInfo appInfo){
        Intent intent = new Intent(context , CommentActivity.class);

        intent.putExtra("object_key" , appInfo);

        context.startActivity(intent);
    }
}
