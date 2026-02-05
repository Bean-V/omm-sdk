package com.oort.weichat.ui.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;

import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.util.LocaleHelper;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.widget.WaterMarkBg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class BaseActivity extends BaseLoginActivity {

    private View swipeBackLayout;
    private FastSharedPreferences spUser;
    private FastSharedPreferences spUser1;

    private final Executor diskIoExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private View pendingBaseView;
    private int pendingLayoutResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLanguageAndOrientation();
        super.onCreate(savedInstanceState);
        // 先初始化非依赖部分
        swipeBackLayout = this.getSwipeBackLayout();
    }

    private void initLanguageAndOrientation() {
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        if ("ar".equals(LocaleHelper.getLanguage(this))) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        pendingLayoutResId = layoutResID;
        prepareBaseView();

        // 异步加载SharedPreferences
        diskIoExecutor.execute(() -> {
            spUser = FastSharedPreferences.get("USERINFO_SAVE");
            spUser1 = FastSharedPreferences.get("USERPRIVACY_SAVE");

            mainHandler.post(this::applyWatermarkAndContentView);
        });
    }

    private void prepareBaseView() {
        pendingBaseView = LayoutInflater.from(this).inflate(R.layout.layout_base, null, false);
        pendingBaseView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                OperLogUtil.mPostionX = (int)(event.getX());
                OperLogUtil.mPostionY = (int)(event.getY());
                return false;
            }
        });

        View contentView = LayoutInflater.from(this).inflate(pendingLayoutResId, null, false);
        FrameLayout flContainer = pendingBaseView.findViewById(R.id.flContainer);
        flContainer.addView(contentView);

        // 先设置无水印的视图
        super.setContentView(pendingBaseView);
    }

    private void applyWatermarkAndContentView() {
        if (pendingBaseView == null) return;

        FrameLayout flWater = pendingBaseView.findViewById(R.id.flWater);
        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> labels = new ArrayList<>();
        String blank = "                    ";
        String name = spUser != null ? spUser.getString("name", "") : "";
        String userprivacy = spUser1 != null ? spUser1.getString("allowAtt", "") : "";

        if (!TextUtils.isEmpty(userprivacy)) {
            if (Integer.valueOf(userprivacy) != 0) {
                if (TextUtils.isEmpty(name)) {
                    labels.add(getResources().getString(R.string.app_name) + blank);
                } else {
                    labels.add(name + blank);
                }
                labels.add(createTimeSdf1.format(new Date()));
                flWater.setBackground(new WaterMarkBg(this, labels, -30, 13));
            }
        } else {
            if (!TextUtils.isEmpty(name) && Constant.IsShowWaterPrint) {
                labels.add(name + blank);
            }
            labels.add("  ");
            flWater.setBackground(new WaterMarkBg(this, labels, -30, 13));
        }

        // 重新设置视图以触发水印更新
        super.setContentView(pendingBaseView);
    }

    public void shake(int type) {
        if (swipeBackLayout != null) {
            Animation shake;
            if (type == 0) {
                shake = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.shake_from);
            } else {
                shake = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.shake_to);
            }
            swipeBackLayout.startAnimation(shake);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacksAndMessages(null);
    }
}