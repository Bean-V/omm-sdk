package com.oortcloud.appstore.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.oortcloud.appstore.R;
import com.oortcloud.appstore.dailog.LoadingDialog;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.widget.WaterMarkBg;
import com.xuexiang.xui.XUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseActivity extends FragmentActivity {

    protected ImageView mImgBack;
    protected TextView mTitle;
    protected ImageView mImgItem;
    protected TextView mBtnItem;

    protected Context mContext;
    protected LoadingDialog mLoadDialog;
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    private FastSharedPreferences spUser1 = FastSharedPreferences.get("USERPRIVACY_SAVE");
    private ViewBinding mViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        XUI.initTheme(this);

        // 初始化内容视图（修复核心逻辑）
        initContentView();

        mContext = this;
        mLoadDialog = new LoadingDialog(mContext);
        initTitleBar();

        initBundle(getIntent().getExtras());
        initView();
        initData();
        initEvent();

        mImgBack.setOnClickListener(view -> finish());
    }

    /**
     * 修复：正确处理ViewBinding和布局ID的加载逻辑，避免ID为0的错误
     */
    private void initContentView() {
        // 加载基础布局（包含标题栏和容器）
        View baseView = LayoutInflater.from(this).inflate(R.layout.layout_base, null, false);
        FrameLayout flContainer = baseView.findViewById(R.id.flContainer);
        FrameLayout flWater = baseView.findViewById(R.id.flWater);
        initWaterMark(flWater);

        // 优先使用ViewBinding
        mViewBinding = getViewBinding();
        if (mViewBinding != null) {
            // 添加ViewBinding的根视图到容器，无需布局ID
            flContainer.addView(mViewBinding.getRoot());
        } else {
            // 不使用ViewBinding时，才加载布局ID（确保ID有效）
            int layoutId = getLayoutId();
            if (layoutId != 0) { // 关键：跳过ID为0的无效布局
                View contentView = LayoutInflater.from(this).inflate(layoutId, null, false);
                flContainer.addView(contentView);
            } else {
                // 既没有ViewBinding也没有有效布局ID时，抛出明确异常
                throw new IllegalArgumentException("子类必须提供有效的ViewBinding或布局ID（不能为0）");
            }
        }

        super.setContentView(baseView);
    }

    private void initTitleBar() {
        mImgBack = findViewById(R.id.img_back);
        mTitle = findViewById(R.id.tv_title);
        mImgItem = findViewById(R.id.img_item);
        mBtnItem = findViewById(R.id.bt_item);
    }

    private void initWaterMark(FrameLayout flWater) {
        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> labels = new ArrayList<>();

        String blank = "                    ";
        String name = spUser.getString("name", "");
        String userprivacy = spUser1.getString("allowAtt", "") + "";

        if (!TextUtils.isEmpty(userprivacy) && Integer.parseInt(userprivacy) != 0) {
            if (TextUtils.isEmpty(name)) {
                labels.add(getResources().getString(R.string.app_name) + blank);
            } else {
                labels.add(name + blank);
            }
            labels.add(createTimeSdf1.format(new Date()));
        } else {
            if (TextUtils.isEmpty(name)) {
                labels.add(getResources().getString(R.string.app_name) + blank);
            }
            labels.add("  ");
        }
        flWater.setBackground(new WaterMarkBg(this, labels, -30, 13));
    }

    protected abstract ViewBinding getViewBinding();

    protected abstract int getLayoutId();

    protected abstract void initBundle(Bundle bundle);

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    @Override
    protected void onDestroy() {
        mViewBinding = null;
        super.onDestroy();
    }
}
