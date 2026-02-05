package com.oort.weichat.ui.yeepay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.bean.event.EventNotifyByTag;
import com.oort.weichat.bean.redpacket.Balance;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.tool.ButtonColorChange;
import com.oort.weichat.util.EventBusHelper;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;

public class YeepayWallet extends BaseActivity {
    private TextView mBalanceTv;
    private TextView mRechargeTv;
    private TextView mWithdrawTv;

    public static void start(Context context) {
        Intent starter = new Intent(context, YeepayWallet.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeepay);
        initActionBar();
        initView();
        EventBusHelper.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_center);
        mTvTitle.setText(getString(R.string.my_yeepay));
        ImageView mImageView = findViewById(R.id.iv_title_right);
        mImageView.setImageDrawable(getResources().getDrawable(R.mipmap.navigation));
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 访问接口 获取记录
                Intent intent = new Intent(mContext, YeepayPaymentCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mBalanceTv = (TextView) findViewById(R.id.myblance);
        mRechargeTv = (TextView) findViewById(R.id.chongzhi);
        mWithdrawTv = (TextView) findViewById(R.id.quxian);
        ButtonColorChange.rechargeChange(this, mWithdrawTv, R.drawable.recharge_icon);
        ButtonColorChange.rechargeChange(this, mRechargeTv, R.drawable.chongzhi_icon);
        mWithdrawTv.setTextColor(SkinUtils.getSkin(this).getAccentColor());

        mRechargeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YeepayRecharge.start(mContext);
            }
        });

        mWithdrawTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YeepayWithdraw.start(mContext);
            }
        });
    }

    private void initData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());

        HttpUtils.get().url(coreManager.getConfig().YOP_MONEY)
                .params(params)
                .build()
                .execute(new BaseCallback<Balance>(Balance.class) {

                    @Override
                    public void onResponse(ObjectResult<Balance> result) {
                        if (Result.checkSuccess(mContext, result) && result.getData() != null) {
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            final DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
                            decimalFormat.setDecimalFormatSymbols(decimalSymbol);
                            Balance balance = result.getData();
                            mBalanceTv.setText("￥" + decimalFormat.format(((balance.getBalance()))));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(YeepayWallet.this);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventNotifyByTag message) {
        if (TextUtils.equals(message.tag, EventNotifyByTag.Withdraw)) {
            initData();
        }
    }
}
