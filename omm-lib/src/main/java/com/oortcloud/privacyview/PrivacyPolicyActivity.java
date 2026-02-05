package com.oortcloud.privacyview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;


import com.oort.weichat.R;
import com.oortcloud.custom.CordovaView;

import org.apache.cordova.engine.SystemWebChromeClient;

/**
 * 隐私政策
 *
 * @author zhongfg
 */
public class PrivacyPolicyActivity extends Activity {

    private static final String TAG = PrivacyPolicyActivity.class.getSimpleName();

    private FrameLayout web_view_container;
//    private WebView web_view;
    private CordovaView mWebView;

    private final String LANGUAGE_CN = "zh-CN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy_policy);

        initView();
    }

    private void initView() {

        web_view_container = findViewById(R.id.web_view_container);
//        web_view = new WebView(getApplicationContext());
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        web_view.setLayoutParams(params);
//        web_view.setWebViewClient(new WebViewClient());
        //动态添加WebView，解决在xml引用WebView持有Activity的Context对象，导致内存泄露
//        web_view_container.addView(web_view);

        mWebView =  new CordovaView(this);
        mWebView.initCordova(this);
        mWebView.getWebview().setWebChromeClient(new SystemWebChromeClient(mWebView.getSystemWebViewEngine()) {
            //监听进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //设置页面加载进度
                Log.i("newProgress","newProgress: "+newProgress);
                if(newProgress != 100){
                    mWebView.showLoading();
                }else{
                    mWebView.hideLoading();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //设置标题
            }
        });

        web_view_container.addView(mWebView);

        String language = AppUtil.getLanguage(PrivacyPolicyActivity.this);
        Log.i(TAG, "当前语言：" + language);

        if (LANGUAGE_CN.equals(language)) {
            mWebView.loadUrl("https://www.oortcloudsmart.com/privacypolicy_jwb.html");
        } else {
            mWebView.loadUrl("https://www.oortcloudsmart.com/privacypolicy_jwb.html");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_view_container.removeAllViews();
        mWebView.onDestroy();
    }
}
