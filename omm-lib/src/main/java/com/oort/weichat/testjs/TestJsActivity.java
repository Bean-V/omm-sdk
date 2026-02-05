package com.oort.weichat.testjs;

import static com.oortcloud.basemodule.CommonApplication.canRefresh;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.oort.weichat.R;
import com.oortcloud.custom.CordovaView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;

import org.apache.cordova.engine.SystemWebChromeClient;

public class TestJsActivity extends AppCompatActivity {
    private static final String TAG = "HomeFragment";

    //    private static String url = com.oortcloud.basemodule.constant.Constant.HOME_PAGE;
    private static String url = "file:///android_asset/testreview/index.html";
    //    private CordovaView cordovaView;
    private SmartRefreshLayout mRefreshLayout;
    private int mScrollY;
    private CordovaView cordovaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_js);
        getSupportActionBar()
                .hide();
        cordovaView = new CordovaView(getApplicationContext());
        cordovaView.initCordova(this);
        initView();

        refresh();
    }
    public void getApp(){


    }




    private void  startService(){

//        if(this == null){
//            return;
//        }
//        String packageName = "com.jwb_home.oort";
//        String params = "";
//        Intent intent = new Intent(this , AppManagerService.class);
//        intent.putExtra("packageName" , packageName);
//        intent.putExtra("params" , params);
//        this.startService(intent);
    }

    // 动态请求悬浮窗权限
    private void requestOverlayPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!Settings.canDrawOverlays(this))
            {
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                this.startActivity(intent);
            }else {

            }

        }
    }
    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(rl -> {

        });
//        mRefreshLayout.setEnableRefresh(false);
//        mRefreshLayout.setEnableLoadMore(false);
        String launchUrl = "file:///android_asset/www/index.html";
        LinearLayout linearLayout = findViewById(R.id.oort_cv);
        linearLayout.addView(cordovaView);
//        cordovaView.getWebview().reload();
        cordovaView.setOnReceivedErrorListener(new CordovaView.OnReceivedErrorListener() {
            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
                Log.i("onReceivedError", "errorCode:" + errorCode + "     description:" + description + "     failingUrl:" + failingUrl);
                mRefreshLayout.finishRefresh();
            }
        });
//        cordovaView.getWebview().reload();
//        cordovaView.getWebview().goBack();
        cordovaView.getWebview().setWebChromeClient(new SystemWebChromeClient(cordovaView.getSystemWebViewEngine()) {
            //监听进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //设置页面加载进度
                Log.i("newProgress","newProgress: "+newProgress);
                if(newProgress != 100){
                    cordovaView.showLoading();
                }else{

                    //((BaseActivity)getActivity()).setStatusBarLight(true);
                    cordovaView.hideLoading();
                    mRefreshLayout.finishRefresh();
                    // MainActivity.makeStatusBarTransparent(getActivity());
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //设置标题
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cordovaView.getWebview().setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    mScrollY =scrollY;
                }
            });
        }

        mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
//                mRefreshLayout.setEnableRefresh(canRefresh);
                return canRefresh;
            }

            @Override
            public boolean canLoadMore(View content) {
                return false;
            }
        });
        mRefreshLayout.setEnableOverScrollBounce(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRefreshLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    mRefreshLayout.setEnableRefresh(canRefresh);
                }
            });
        }
    }

    private void refresh() {
//        MainActivity.getTaskInfo(coreManager.getSelf().getUserId(),PUBLIC_USERID,PUBLIC_NUM);
////        cordovaView.initCordova(getActivity());
//        //本地路径用这个刷新
//
//
////        AppInfo info = new AppInfo();
////        info.setAp
//
//        AppManager.mH5Info = DataInit.getAppinfo("com.jwb_home.oort");
        cordovaView.getWebview().loadUrl(url);
        //远程链接用这个刷新
//        cordovaView.getWebview().clearCache(true);
//        cordovaView.getWebview().reload();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cordovaView != null){
            cordovaView.onPause();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (cordovaView != null){
            cordovaView.onResume();

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cordovaView != null){
            cordovaView.onDestroy();
        }
    }
}