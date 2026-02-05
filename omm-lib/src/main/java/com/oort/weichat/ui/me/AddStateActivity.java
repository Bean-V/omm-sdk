package com.oort.weichat.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.JsonCallback;

import java.util.HashMap;

import okhttp3.Call;

public class AddStateActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_state);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_title_left).setOnClickListener(v -> finish());
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_left);
        tvTitle.setText("我的状态");
        tvTitle.setTextSize(18);
        findViewById(R.id.ly_state_mzz).setOnClickListener(this);
        findViewById(R.id.ly_state_lk).setOnClickListener(this);
        findViewById(R.id.ly_state_qjl).setOnClickListener(this);
        findViewById(R.id.ly_state_dtq).setOnClickListener(this);
        findViewById(R.id.ly_state_pib).setOnClickListener(this);
        findViewById(R.id.ly_state_fd).setOnClickListener(this);
        findViewById(R.id.ly_state_emo).setOnClickListener(this);
        findViewById(R.id.ly_state_hslx).setOnClickListener(this);
        findViewById(R.id.ly_state_gzzh).setOnClickListener(this);
        findViewById(R.id.ly_state_cmxx).setOnClickListener(this);
        findViewById(R.id.ly_state_mang).setOnClickListener(this);
        findViewById(R.id.ly_state_my).setOnClickListener(this);
        findViewById(R.id.ly_state_cch).setOnClickListener(this);
        findViewById(R.id.ly_state_xb).setOnClickListener(this);
        findViewById(R.id.ly_state_wrms).setOnClickListener(this);
        findViewById(R.id.ly_state_lang).setOnClickListener(this);
        findViewById(R.id.ly_state_dk).setOnClickListener(this);
        findViewById(R.id.ly_state_pb).setOnClickListener(this);
        findViewById(R.id.ly_state_hkf).setOnClickListener(this);
        findViewById(R.id.ly_state_hnc).setOnClickListener(this);
        findViewById(R.id.ly_state_zp).setOnClickListener(this);
        findViewById(R.id.ly_state_gf).setOnClickListener(this);
        findViewById(R.id.ly_state_zdy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ly_state_mzz) {
            stateMzz();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_lk) {
            stateLk();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_qjl) {
            stateQjl();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_dtq) {
            stateDtq();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_pib) {
            statePib();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_fd) {
            stateFd();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_emo) {
            stateEmo();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_hslx) {
            stateHslx();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_gzzh) {
            stateGzzh();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_cmxx) {
            stateCmxx();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_mang) {
            stateMang();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_my) {
            stateMy();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_cch) {
            stateCch();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_xb) {
            stateXb();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_wrms) {
            stateWrms();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_lang) {
            stateLang();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_dk) {
            stateDk();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_pb) {
            statePb();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_hkf) {
            stateHkf();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_hnc) {
            stateHnc();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_zp) {
            stateZp();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_gf) {
            stateGf();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        } else if (id == R.id.ly_state_zdy) {
            stateZdy();
            startActivity(new Intent(AddStateActivity.this, SelectedActivity.class));
        }
    }

    private void stateMzz() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken", IMUserInfoUtil.getInstance().getToken());
        params.put("status","美滋滋");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateLk() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","裂开");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateQjl() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","求锦鲤");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateDtq() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","等天晴");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void statePib() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","疲惫");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateFd() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","发呆");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateEmo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","emo");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateHslx() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","胡思乱想");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateGzzh() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","工作中");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateCmxx() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","沉迷学习");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateMang() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","忙");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateMy() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","摸鱼");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateCch() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","出差");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateXb() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","下班");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateWrms() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","勿扰模式");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateLang() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","浪");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }private void stateDk() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","打卡");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void statePb() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","跑步");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateHkf() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","喝咖啡");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateHnc() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","喝奶茶");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateZp() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","自拍");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateGf() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","干饭");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
    private void stateZdy() {
        HashMap<String, String> params = new HashMap<>();
        params.put("accessToken",IMUserInfoUtil.getInstance().getToken());
        params.put("status","自定义");
        HttpUtils.get().url(Constant.STE_MY_STATUS)
                .params(params)
                .build()
                .execute ( new JsonCallback() {
                    @Override
                    public void onResponse(String result) {

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                } );
    }
}
