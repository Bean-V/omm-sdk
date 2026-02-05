package com.oort.weichat.fragment;


import static com.oortcloud.basemodule.CommonApplication.canRefresh;
import static com.oortcloud.basemodule.CommonApplication.getAppContext;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_NUM;
import static com.oortcloud.basemodule.constant.Constant.PUBLIC_USERID;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import com.oort.weichat.R;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.custom.CordovaView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;

import org.apache.cordova.engine.SystemWebChromeClient;


public class HomeFragment extends EasyFragment {

    private static final String TAG = "HomeFragment";
    private String currentPackageName; // 当前要打开的小程序包名
    private String currentUrl; // 当前小程序对应的URL
    private SmartRefreshLayout mRefreshLayout;
    private int mScrollY;
    private CordovaView cordovaView;

    // 静态方法创建Fragment并传递包名参数
    public static HomeFragment newInstance(String packageName) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("package_name", packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebview();
        // 获取传入的包名参数
        if (getArguments() != null) {
            currentPackageName = getArguments().getString("package_name");
        }
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initView();
        // 初始化时根据包名加载对应的小程序
        if (currentPackageName != null) {
            loadAppByPackageName(currentPackageName);
        } else {
            // 默认加载首页小程序
            loadAppByPackageName("com.jwb_home.oort");
        }
    }

    /**
     * 根据包名加载对应的小程序
     *
     * @param packageName 小程序包名
     */
    private void initWebview() {
        cordovaView = new CordovaView(getAppContext());
        cordovaView.initCordova(getActivity());
        cordovaView.loadUrl("file:///android_asset/home/index.html");
        WebSettings settings = cordovaView.getWebview().getSettings();
        // 1. 设置缓存路径
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + "cache/";

        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setAppCachePath(cacheDirPath);
//        // 2. 设置缓存大小
//        settings.setAppCacheMaxSize(20*1024*1024);
//        // 3. 开启Application Cache存储机制
//        settings.setAppCacheEnabled(true);
        //4.开启DOM storage
        settings.setDomStorageEnabled(true);
        //5.只需设置支持JS就自动打开IndexedDB存储机制
        settings.setJavaScriptEnabled(true);
    }

    public void getAppInfo() {
        loadAppByPackageName(currentPackageName);
    }

    public void loadAppByPackageName(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            Log.e(TAG, "包名不能为空");
            return;
        }
        Log.e(TAG, "包名"+packageName);
//        if (packageName.equals("com.dispatch.oort")) {
//            FragmentManager fm = requireActivity().getSupportFragmentManager();
//            fm.beginTransaction()
//                    .add(R.id.content, new DispatchConsoleFragment(), "yl")
//                    .addToBackStack(null)
//                    .commit();
//        } else {
//            return;
//        }
        this.currentPackageName = packageName;
        CommonApplication.pIsTab = true;
        AppStoreInit.store_token = "";

        // 设置AppManager监听，处理小程序打开逻辑
        AppManager.getInstance().setListener(new AppManager.OpenOtherWayListener() {
            @Override
            public boolean penOtherWay(String pn, String rl) {
                // 匹配目标包名，更新URL并刷新
                if (pn.equals(currentPackageName)) {
                    currentUrl = rl;
                    refresh();
                    return true;
                }
                return false;
            }

            @Override
            public boolean notOpen(String packageName, String url) {
                cordovaView.hideLoading();
                mRefreshLayout.finishRefresh();
                Log.e(TAG, "无法打开小程序: " + packageName);
                return false;
            }
        });

        // 启动服务加载小程序信息
        startAppService(packageName);
    }

    /**
     * 启动服务获取小程序信息
     *
     * @param packageName 小程序包名
     */
    private void startAppService(String packageName) {
        if (getContext() == null) {
            return;
        }

        Intent intent = new Intent(getContext(), AppManagerService.class);
        intent.putExtra("packageName", packageName);
        intent.putExtra("params", ""); // 可传递额外参数
        getContext().startService(intent);
    }

    // 动态请求悬浮窗权限
    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getContext())) {
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(intent);
            }
        }
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(rl -> {
            AppStatu.getInstance().appStatu = 0;
            if (currentPackageName != null) {
                startAppService(currentPackageName); // 刷新时重新加载当前包名对应的小程序
            }
            DataInit.moduleinit(AppStoreInit.getToken(), AppStoreInit.getUUID(), null);
        });

        // 初始化CordovaView
        LinearLayout linearLayout = findViewById(R.id.oort_cv);
        linearLayout.addView(cordovaView);

        // 错误监听
        cordovaView.setOnReceivedErrorListener((errorCode, description, failingUrl) -> {
            Log.i("onReceivedError", "errorCode:" + errorCode + " description:" + description + " failingUrl:" + failingUrl);
            mRefreshLayout.finishRefresh();
        });

        // WebView进度监听
        cordovaView.getWebview().setWebChromeClient(new SystemWebChromeClient(cordovaView.getSystemWebViewEngine()) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i("newProgress", "newProgress: " + newProgress);
                if (newProgress != 100) {
                    cordovaView.showLoading();
                } else {
                    cordovaView.hideLoading();
                    mRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 可在这里设置页面标题
            }
        });

        // 滚动监听（用于刷新控制）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cordovaView.getWebview().setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                mScrollY = scrollY;
            });
        }

        // 刷新边界设置
        mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
                return canRefresh;
            }

            @Override
            public boolean canLoadMore(View content) {
                return false;
            }
        });
        mRefreshLayout.setEnableOverScrollBounce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRefreshLayout.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                mRefreshLayout.setEnableRefresh(canRefresh);
            });
        }
    }

    /**
     * 刷新当前小程序内容
     */
    private void refresh() {
        MainActivity.getTaskInfo(coreManager.getSelf().getUserId(), PUBLIC_USERID, PUBLIC_NUM);

        // 获取小程序信息并加载URL
        AppInfo appInfo = DataInit.getAppinfo(currentPackageName);
        if (appInfo != null) {
            AppManager.mH5Info = appInfo;
        }

        // 加载当前URL（如果为空则使用默认本地路径）
        if (currentUrl != null) {
            cordovaView.getWebview().loadUrl(currentUrl);
        } else {
            cordovaView.getWebview().loadUrl("file:///android_asset/home/index.html");
        }
    }

    /**
     * 外部调用更新包名并刷新
     *
     * @param packageName 新的小程序包名
     */
    public void updatePackageName(String packageName) {
        loadAppByPackageName(packageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cordovaView != null) {
            cordovaView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cordovaView != null) {
            cordovaView.onResume();

            if (AppStatu.homeRefrash == 2) {
                AppStatu.homeRefrash = 0;
                refresh();
            } else {
                AppStatu.homeRefrash = 0;
            }

            CommonApplication.pIsTab = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cordovaView != null) {
            cordovaView.onDestroy();
        }
    }
}
