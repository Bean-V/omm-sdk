package com.oortcloud.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/11/30 18:04
 * @version： v1.0
 * @function： 举报Activity
 */
public class ReportActivity extends BaseActivity implements View.OnClickListener {
    private PublicMessage mMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_layout);
        if (getIntent() != null){

            mMessage = (PublicMessage) getIntent().getSerializableExtra("obj_key");
        }
        initActionBar();
        initData();
        initView();

    }

    private void initActionBar() {
        getSupportActionBar().hide();

        findViewById(R.id.iv_title_left).setOnClickListener(view -> {

            finish();
        });
        TextView tvTitle = findViewById(R.id.tv_title_center);
        tvTitle.setText("动态举报");

    }

    private void  initData(){}

    private void initView(){
        findViewById(R.id.tv_1).setOnClickListener(this);
        findViewById(R.id.tv_2).setOnClickListener(this);
        findViewById(R.id.tv_3).setOnClickListener(this);
        findViewById(R.id.tv_4).setOnClickListener(this);
        findViewById(R.id.tv_5).setOnClickListener(this);
        findViewById(R.id.tv_6).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_1) {
            report(101);
        } else if (id == R.id.tv_2) {
            report(105);
        } else if (id == R.id.tv_3) {
            report(104);
        } else if (id == R.id.tv_4) {
            report(120);
        } else if (id == R.id.tv_5) {
            report(140);
        } else if (id == R.id.tv_6) {
            report(130);
        }

    }


    private void report( int report) {

        if (mMessage == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("toUserId", mMessage.getUserId());
        params.put("reason", String.valueOf(report));

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).USER_REPORT)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (Result.checkSuccess(mContext, result)) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.report_success));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }
}
