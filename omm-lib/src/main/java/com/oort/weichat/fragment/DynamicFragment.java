package com.oort.weichat.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.oort.weichat.R;
import com.oort.weichat.ui.base.EasyFragment;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.custom.CordovaView;

import org.apache.cordova.engine.SystemWebChromeClient;


public class DynamicFragment extends EasyFragment {

    private static final String TAG = "DynamicFragment";

//    private static String url = com.oortcloud.basemodule.constant.Constant.HOME_PAGE;
    private static String url = "file:///android_asset/dynamic/index.html";
    private CordovaView cordovaView;
//    private SmartRefreshLayout mRefreshLayout;
    private int mScrollY;
    public DynamicFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initView();
        //getApp();
    }

    public void getApp(){

        CommonApplication.pIsTab = true;
        AppStoreInit.store_token = "";
        startService();
        AppManager.getInstance().setListener(new AppManager.OpenOtherWayListener() {
            @Override
            public boolean penOtherWay(String pn,String rl) {

                if(pn.contains("com.work_dynamics.oort")) {
                    url = rl;
                    refresh();
                    return true;
                }else{

                    return false;
                }
            }

            @Override
            public boolean notOpen(String pakegeName, String url) {

                cordovaView.hideLoading();
                return false;
            }
        });
    }


    private void  startService(){
        String packageName = "com.work_dynamics.oort";
        String params = "";
        Intent intent = new Intent(getContext() , AppManagerService.class);
        intent.putExtra("packageName" , packageName);
        intent.putExtra("params" , params);
        getContext().startService(intent);
    }

    private void initView() {
//        mRefreshLayout = findViewById(R.id.refreshLayout);
       /* mRefreshLayout.setOnRefreshListener(rl -> {
            refresh();
        });*/
//        mRefreshLayout.setEnableRefresh(false);
//        mRefreshLayout.setEnableLoadMore(false);

        AppManager.mH5Info = DataInit.getAppinfo("com.work_dynamics.oort");
        String launchUrl = "file:///android_asset/www/index.html";
        cordovaView = findViewById(R.id.oort_cv);
        cordovaView.initCordova(getActivity());
        cordovaView.loadUrl(url);
        cordovaView.setOnReceivedErrorListener(new CordovaView.OnReceivedErrorListener() {
            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
                Log.i("onReceivedError", "errorCode:" + errorCode + "     description:" + description + "     failingUrl:" + failingUrl);
//                mRefreshLayout.finishRefresh();
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
                    cordovaView.hideLoading();
//                    mRefreshLayout.finishRefresh();
                    //MainActivity.makeStatusBarTransparent(getActivity());
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

        /*mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
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
        }*/
    }

    private void refresh() {
//        cordovaView.initCordova(getActivity());
        //本地路径用这个刷新
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