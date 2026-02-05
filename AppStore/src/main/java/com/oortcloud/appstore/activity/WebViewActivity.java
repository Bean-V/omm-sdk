package com.oortcloud.appstore.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.fragment.app.FragmentActivity;

import com.oortcloud.appstore.R;

/**
 * @ProjectName: AppStore-master
 * @FileName: WebViewActivity.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/07 02:31
 * @Version: 1.0
 */
public class WebViewActivity extends FragmentActivity {

    private static String PATH_KEY = "path_key";
    private String mPath;

    private WebView webView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_webview_layout);
        mPath = getIntent().getStringExtra(PATH_KEY);
        initView();
    }


    protected void initView() {
        webView =  findViewById(R.id.webveiw);
        // 设置允许访问文件数据
        webView.getSettings().setAllowFileAccess(true);
        //支持放大网页功能
        webView.getSettings().setSupportZoom(true);
        //支持缩小网页功能
         webView.getSettings().setBuiltInZoomControls(true);
        //支持JAVA
         webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("file:///mnt/sdcard/appmall/dist/index.html");
//        Log.v("msg" ,"file:///"+Environment.getExternalStorageDirectory()+"/appmall/dist/index.html" );
//        webView.loadUrl("file:///"+ Environment.getExternalStorageDirectory()+"/appmall/dist/index.html");
//        webView.loadUrl("content://r"+ Environment.getExternalStorageDirectory().getAbsolutePath()+"/appmall/dist/index.html" );
        webView.loadUrl(mPath);
    }

    public static void actionStart(Context context , String path){
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(PATH_KEY , path);

        context.startActivity(intent);
    }


}
