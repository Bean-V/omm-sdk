package com.oort.weichat.ui.me;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.fri.libfriapkrecord.read.SignRecordTools;
import com.oort.weichat.AppConfig;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.downloader.UpdateManger;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.ShareSdkHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.DeviceInfoUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.view.ShareDialog;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.bean.MainAppInfo;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.utils.RxBus;
import com.oortcloud.privacyview.PrivacyPolicyActivity;
import com.oortcloud.privacyview.TermsActivity;

import io.reactivex.disposables.Disposable;

public class AboutActivity extends BaseActivity implements View.OnClickListener{
    private ShareDialog shareDialog;
    TextView checkUpdate;

    private String videourl = "http://map.oort.oortcloudsmart.com:32610/oort/oortwj1/group1/default/20210220/14/16/4/6b6515fb8e1cea0bc6cd80e0288b708f.mp4";
    ShareDialog.OnShareDialogClickListener onShareDialogClickListener = new ShareDialog.OnShareDialogClickListener() {
        @Override
        public void tv1Click() {
            ShareSdkHelper.shareWechat(AboutActivity.this, MyApplication.getContext().getString(R.string.app_name) + AboutActivity.this.getString(R.string.suffix_share_content),
                    MyApplication.getContext().getString(R.string.app_name) + AboutActivity.this.getString(R.string.suffix_share_content),
                    AboutActivity.this.coreManager.getConfig().website);
        }

        @Override
        public void tv2Click() {
            ShareSdkHelper.shareWechatMoments(AboutActivity.this, MyApplication.getContext().getString(R.string.app_name) + AboutActivity.this.getString(R.string.suffix_share_content),
                    MyApplication.getContext().getString(R.string.app_name) + AboutActivity.this.getString(R.string.suffix_share_content),
                    AboutActivity.this.coreManager.getConfig().website);
        }

        @Override
        public void tv3Click() {
            shareDialog.cancel();
        }
    };

    public void PrivacyAgree(View view) {
//        if (UiUtils.isNormalClick(view) && !TextUtils.isEmpty(coreManager.getConfig().privacyPolicyPrefix)) {
//            PrivacyAgreeActivity.startIntent(AboutActivity.this);
//        }

        if(true) {
            return;
        }
        Intent intent = new Intent(AboutActivity.this, TermsActivity.class);
        startActivity(intent);
    }

    public void Privacy(View view) {
//        if (UiUtils.isNormalClick(view) && !TextUtils.isEmpty(coreManager.getConfig().privacyPolicyPrefix)) {
//            PrivacyAgreeActivity.startPrivacy(AboutActivity.this, coreManager.getConfig().privacyPolicyPrefix );
//        }
        if(true) {
            return;
        }
        Intent intent = new Intent(AboutActivity.this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.about_us));
        ImageView ivRight = (ImageView) findViewById(R.id.iv_title_right);
        ivRight.setImageResource(R.mipmap.share_icon);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialog = new ShareDialog(AboutActivity.this, onShareDialogClickListener);
                shareDialog.show();
            }
        });

        findViewById(R.id.movie_rl).setOnClickListener(this);
        findViewById(R.id.update_log_rl).setOnClickListener(this);
        findViewById(R.id.api_doc_rl).setOnClickListener(this);
        findViewById(R.id.check_update_rl).setOnClickListener(this);
        /*checkUpdate = findViewById(R.id.checkupdate_tv);
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Beta.checkUpgrade(true,false);
            }
        });*/



        final String apkPath = getNativeApkPath(this.mContext.getApplicationContext());
        //读取备案号
//        String recordNum = SignRecordTools.readNumbers(apkPath);
        TextView tvBeianhao = (TextView) findViewById(R.id.beianhao);
//        tvBeianhao.setText("全国注册备案号："+recordNum);
//
        TextView versionTv = (TextView) findViewById(R.id.version_tv);
        versionTv.setText(getString(R.string.app_name));
        TextView versioncodeTv = (TextView) findViewById(R.id.versioncode_tv);
        versioncodeTv.setText(DeviceInfoUtil.getVersionName(mContext) + "(" + DeviceInfoUtil.getVersionCode(mContext) + ")");
        TextView tvCompany = findViewById(R.id.company_tv);
        TextView tvCopyright = findViewById(R.id.copy_right_tv);

        tvCompany.setText(coreManager.getConfig().companyName);
        tvCopyright.setText(coreManager.getConfig().copyright);

        if (!AppConfig.isShiku()) {
            tvCompany.setVisibility(View.GONE);
            tvCopyright.setVisibility(View.GONE);
            ivRight.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (!UiUtils.isNormalClick(v)) {
            return;
        }
        int id = v.getId();
        if (id == R.id.movie_rl) {// 品牌视频

            String appid = getApplicationInfo().processName;
            Intent intent = new Intent(appid + ".player");
            intent.putExtra("URL", videourl);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.update_log_rl) {// 更新日志

            String url = "http://oort.oortcloudsmart.com:31610/oort/oortcloud-cetcnewsservice/oortdocs/39";
            String appid = getApplicationInfo().processName;
            Intent intent = new Intent(appid + ".web.container");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", url);
            startActivity(intent);
        } else if (id == R.id.api_doc_rl) {// API文档
            String url = "http://oort.oortcloudsmart.com:31610/oort/oortcloud-cetcnewsservice/oortdocs/47";
            String appid = getApplicationInfo().processName;
            Intent intent = new Intent(appid + ".web.container");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", url);
            startActivity(intent);
        } else if (id == R.id.check_update_rl) {// 检查更新
//                Beta.checkUpgrade();
            getLastAppInfo();
        }
    }

    private void getLastAppInfo() {

        /*Log.d("zlm", ReportInfo.accessToken);
        Log.d("zlm",ReportInfo.oort_uuid);
        Log.d("zlm",ReportInfo.depart_code);*/
        String appname= getResources().getString(R.string.app_name);
        HttpRequestParam.appSearch(appname,ReportInfo.accessToken,ReportInfo.oort_uuid,ReportInfo.depart_code).subscribe(new RxBus.BusObserver<String>(){
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
            }

            @Override
            public void onNext(String s) {
                Log.d("zlm",s);
                //1.搜索是否成功
                DialogHelper.dismissProgressDialog();
                if (TextUtils.isEmpty(s)) {
//                    ToastUtils.showShortSafe("登录失败！");
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    int code = jsonObject.getIntValue("code");
                    String msg = jsonObject.getString("msg");
                    if(code == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int count = data.getIntValue("count");
                        if(count > 0){
                            JSONArray array = data.getJSONArray("app_list");
                            Object jsonObject1 = array.get(0);
                            MainAppInfo mainAppinfo = JSON.parseObject(String.valueOf(jsonObject1), MainAppInfo.class);
                            if (mainAppinfo != null) {
//                                Log.d("zlm", mainAppinfo.getApk_url());
//                                Log.d("zlm", mainAppinfo.getApppackage());
//                                Log.d("zlm", mainAppinfo.getNew_versioncode()+"");
//                                Log.d("zlm", mainAppinfo.getVer_description());
                                String url = StringUtil.getUrlRelativePath(mainAppinfo.getApk_url());
                                String download_url = Constant.BASE_URL + url;
                                UpdateManger.checkUpdate(AboutActivity.this,
                                        download_url,
                                        mainAppinfo.getNew_versioncode(),
                                        mainAppinfo.getVer_description(),
                                        true);
                            }
                        }

                    }
                }catch (Exception e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("appsearch", "json error!");
                }
                //2.搜索结果是否为空

                //3.应用包名是否一一致

                //4.检查是否有新版本更新


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("zlm",e.getMessage().toString());
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        });
    }

    public static String getNativeApkPath(@NonNull final Context context) {
        String apkPath = null;
        try {
            final ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo == null) {
                return null;
            }
            apkPath = applicationInfo.sourceDir;
        } catch (Throwable e) {
        }
        return apkPath;
    }
}
