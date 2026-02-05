package com.oortcloud.debug;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.oort.weichat.R;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.engine.SystemWebChromeClient;

public class DebugActivity extends CordovaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        String webType = intent.getStringExtra("web_type");
        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            url = "http://www.baidu.com";
        }
        loadUrl(url);
        getWebview().setWebChromeClient(new SystemWebChromeClient(getSystemWebViewEngine()) {
            //
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //
                if (newProgress != 100){
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.GONE);
//                    homeImg.setImageResource(R.mipmap.home);
                }
                Log.i("newProgress","newProgress: "+newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //
            }
        });
    }
}
