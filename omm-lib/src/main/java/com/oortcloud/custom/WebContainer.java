package com.oortcloud.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.android.floatview.floatview.BaseFloatingView;
import com.android.floatview.floatview.FloatingViewListener;
import com.android.floatview.floatview.FloatingViewManager;
import com.oort.weichat.R;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.engine.SystemWebChromeClient;

public class WebContainer extends CordovaActivity {
    private String url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent(this);
        //StatusBarUtil.setLightMode(this);
        String blank = "                    ";
//        if (!TextUtils.isEmpty(ReportInfo.phone) && ReportInfo.phone.length()>=4){
//            last4str = (ReportInfo.phone.substring(ReportInfo.phone.length()-4,ReportInfo.phone.length()));
//        }
        //textContent = ReportInfo.name + blank;
        Intent intent=getIntent();
        String webType = intent.getStringExtra("web_type");
        url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            url = "http://www.baidu.com";
        }
        loadUrl(url);

        FloatingViewManager.getInstance()
                .attach(this)
                .add()
                .listener(new FloatingViewListener() {
                    @Override
                    public void onClick(BaseFloatingView baseFloatingView) {
                        WebContainer.this.finish();
                    }

                    @Override
                    public void onUp(BaseFloatingView baseFloatingView) {

                    }

                    @Override
                    public void onDown(BaseFloatingView baseFloatingView) {

                    }
                });

        getWebview().setWebChromeClient(new SystemWebChromeClient(getSystemWebViewEngine()) {
            //
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //
                if (newProgress != 100){
                    textView.setVisibility(View.GONE);
//                    logoImg.setImageResource(R.drawable.load);
//                    try {
//
//                        gifDrawable =new GifDrawable(getResources(),R.drawable.load);
//
//                    }catch (IOException e) {
//
//                        e.printStackTrace();
//
//                    }
//                    logoGif.setImageDrawable(gifDrawable);
//                    Glide.with(WebContainer.this.getApplicationContext())
//                            .load(R.drawable.load)
//                            .asGif()
//                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                            .into(logoGif);
//                    logoGif.setVisibility(View.VISIBLE);
                   // logoGif.setImageResource(R.drawable.load);
                }else{
                    textView.setVisibility(View.GONE);
//                    logoImg.setVisibility(View.GONE);
                    //logoGif.setVisibility(View.GONE);
                   // MainActivity.makeStatusBarTransparent(WebContainer.this);
                }
                Log.i("newProgress","newProgress: "+newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //
            }
        });


       /* setContentView(R.layout.activity_web);

        cordovaView = findViewById(R.id.cv);
        cordovaView.initCordova(this);
        cordovaView.getWebview().clearHistory();

        cordovaView.loadUrl(url);

        cordovaView.setOnReceivedErrorListener(new CordovaView.OnReceivedErrorListener() {
            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
                Log.e("error",errorCode + description  + failingUrl);
            }
        });
        cordovaView.getWebview().setWebChromeClient(new SystemWebChromeClient(cordovaView.getSystemWebViewEngine()) {
            //
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //
                if (newProgress != 100){
                    cordovaView.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    cordovaView.setBackgroundColor(Color.WHITE);
                }
                Log.i("newProgress","newProgress: "+newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingViewManager.getInstance().detach(this);
    }
    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(option);
//            window.setStatusBarColor(Color.TRANSPARENT);//| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.setStatusBarColor(activity.getResources().getColor(R.color.statusbar_bg));
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
