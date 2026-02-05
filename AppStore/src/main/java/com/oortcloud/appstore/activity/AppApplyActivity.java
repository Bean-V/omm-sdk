package com.oortcloud.appstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.databinding.ActivityAppApplyBinding;
import com.oortcloud.appstore.db.ClassifyManager;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.LocaleHelper;

public class AppApplyActivity extends AppCompatActivity {

    private com.oortcloud.appstore.databinding.ActivityAppApplyBinding mBinding;

    int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));


        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBinding = ActivityAppApplyBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.title.tvTitle.setText(getString(R.string.app_apply));
        mBinding.title.btItem.setVisibility(View.GONE);


        String packageName = getIntent().getStringExtra("packageName");

        int type = getIntent().getIntExtra("mType",0);

        AppInfo applyApp = DataInit.getAppinfo(packageName);
        if(type == 22){

            applyApp.setApply_status(2);
        }




        mBinding.tvAppName.setText(applyApp.getApplabel());
        mBinding.tvClass.setText(ClassifyManager.getClassify(applyApp.getClassify()));

        mBinding.tvAppDes.setText(applyApp.getOneword());
        ImageLoader.loadImage(mBinding.appIcon,applyApp.getIcon_url(), R.mipmap.default_app_icon);


        if (applyApp.getApply_status() == 0) {
//            ColorMatrix matrix = new ColorMatrix();
//            matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

//            Paint paint = new Paint();
//            ColorMatrix cm = new ColorMatrix();
//            cm.setSaturation(0);//灰度效果
//            paint.setColorFilter(new ColorMatrixColorFilter(cm));
//            holder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
//
//            holder.install.setVisibility(View.GONE);
        }

        if(applyApp.getApply_status() == 2){
            mBinding.llApply.setVisibility(View.VISIBLE);
            mBinding.llReject.setVisibility(View.GONE);
            mBinding.llReview.setVisibility(View.GONE);
            mBinding.ivApplyFlag.setImageResource(R.mipmap.icon_appapply_apply);
            mBinding.ivApplyFlag.setVisibility(View.GONE);
            mBinding.etReason.setText(applyApp.getApply_msg());

            mBinding.btnApply.setText(getString(R.string.submit_app_apply));
            mBinding.btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    HttpRequestParam.applyApp(AppStoreInit.getToken(),applyApp.getUid(),mBinding.etReason.getText().toString()).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Result<AppInfo> result = new Gson().fromJson(s, new TypeToken<Result<AppInfo>>() {}.getType());

                            if (result.isok()){
                                ToastUtils.showBottom(getString(R.string.submit_suc));
                                finish();
                            }
                        }

                    });
                }
            });
        }
        if(applyApp.getApply_status() == 4){
            mBinding.llApply.setVisibility(View.GONE);
            mBinding.llReject.setVisibility(View.VISIBLE);
            mBinding.llReview.setVisibility(View.GONE);
            mBinding.btnApply.setText(getString(R.string.resubmit));

            mBinding.tvReasonReject.setText(applyApp.getApply_msg());
            mBinding.tvRejectComent.setText(applyApp.getApply_report());

            mBinding.ivApplyFlag.setImageResource(R.mipmap.icon_app_apply_reject);
            mBinding.btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(AppApplyActivity.this, AppApplyActivity.class);
                    in.putExtra("packageName",packageName);

                    in.putExtra("mType",22);
                    startActivity(in);
                }
            });
        }
        if(applyApp.getApply_status() == 3){
            mBinding.llApply.setVisibility(View.GONE);
            mBinding.llReject.setVisibility(View.GONE);
            mBinding.llReview.setVisibility(View.VISIBLE);
            mBinding.btnApply.setVisibility(View.GONE);
            mBinding.tvReason.setText(applyApp.getApply_msg());
            mBinding.ivApplyFlag.setImageResource(R.mipmap.icon_appapply_apply);
        }


        mBinding.title.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static void start(Context cx,String packageName){
        Intent in = new Intent(cx, AppApplyActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra("packageName",packageName);
        cx.startActivity(in);
    }


}