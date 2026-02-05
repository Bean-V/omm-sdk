package com.oort.weichat.ui.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oortcloud.basemodule.constant.Constant;

public class Activity_chat_announce extends BaseActivity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_announce);

        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_center);
        mTvTitle.setText(getString(R.string.group_record));

        //if(!Constant.HAVA_NOTE) {
            mTvTitle.setVisibility(View.GONE);
        //}
        web = findViewById(R.id.web);
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)

            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边

                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    return true;
                }

            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    web.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
            }


        });
        String name = getIntent().getStringExtra("name");
        String icon = getIntent().getStringExtra("icon");
        String announceId = getIntent().getStringExtra("announceId");

//web.loadUrl("https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693")

        String webUrl = String.format(Constant.BASE_URL + "oort/oortcloud-xpad/p/%s?userName=%s&icon=%s",announceId,name,icon);

        //webUrl = "https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693";
        web.loadUrl(webUrl);

//        v.findViewById(R.id.ll_check).setVisibility(View.GONE);
//        web.loadUrl("https://oortcloudsmart.com/");

        web.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本


    }
    public static void start(Context cxt,String name,String icon,String announceId){
        Intent in = new Intent(cxt,Activity_chat_announce.class);
        in.putExtra("name",name);
        in.putExtra("icon",icon);
        in.putExtra("announceId",announceId);
        cxt.startActivity(in);
    }
}