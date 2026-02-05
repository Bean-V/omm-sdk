package com.oortcloud.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oort.weichat.R;
import com.oort.weichat.bean.User;
import com.oort.weichat.ui.base.BaseActivity;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityWeb extends BaseActivity {


    private WebView mWebView;
    private ProgressBar mProgressBar;
    private int currentProgress;
    private boolean isAnimStart;
    private TextView mTitleTv;
    private ImageView mTitleLeftIv;
    private ImageView mTitleRightIv;

    String mContent;
    String mTitle;
    String mNewsId;
    String mUserId;
    String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        if (getIntent() != null) {
            mContent = getIntent().getStringExtra("content");
            mTitle = getIntent().getStringExtra("title");
            mNewsId = getIntent().getStringExtra("newsId");//"nf37cb223b459470d8cc1afa012b4769d";//
            mUserId = getIntent().getStringExtra("userId");//"7684";//
            mType = getIntent().getStringExtra("type");



            //ToastUtil.showToast(this,mNewsId +"///" + mUserId +"///" + mType);


            initActionBar();

            initView();

            initClient();
            initEvent();;
            loadData();
        }
    }
    private void getData() {

        HashMap map = new HashMap();
        map.put("newsId",mNewsId);
        map.put("userId","7684");
        HttpUtils.get().url("http://20.72.1.7:7006/gatt/news/detail")
                .params(map)
                .build()
                .execute(new BaseCallback<User>(User.class) {
                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {

                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void loadData() {


        if(mType != null ) {
            if (mType.equals("2")){
                HashMap map = new HashMap();
                map.put("newsId",mNewsId);
                map.put("userId",mUserId);
                HttpUtils.get().url("http://20.72.1.7:7006/gatt/news/detail")
                        .params(map)
                        .build(true, true)
                        .execute(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                String s = response.body().string();

                                JSONObject.parseObject(s);

                                JSONObject result = JSON.parseObject(s,JSONObject.class);
                                    JSONObject news = (JSONObject) result.get("news");
                                    mContent = getNewContent ((String) news.get("content"));
                                    mTitle = (String)news.get("title");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mWebView.loadDataWithBaseURL(null,mContent,"text/html","utf-8",null);
                                        mTitleTv.setText(mTitle);
                                    }
                                });


                                    //ToastUtil.showToast(ActivityWeb.this,e.getMessage());

                            }
                       });
            }
        }else{
            mWebView.loadDataWithBaseURL(null,mContent,"text/html","utf-8",null);
            mTitleTv.setText(mTitle);
        }


    }


    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleTv = findViewById(R.id.tv_title_center);
        mTitleLeftIv = findViewById(R.id.iv_title_left);
        mTitleLeftIv.setImageResource(R.drawable.icon_close);
        mTitleRightIv = findViewById(R.id.iv_title_right);
        mTitleRightIv.setImageResource(R.drawable.chat_more);
        mTitleRightIv.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //点击悬浮窗进来之后是不会有弹窗的，返回之后才有，这里判断一下进来的场景
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = (WebView) findViewById(R.id.mWebView);
        /* 设置支持Js */
        mWebView.getSettings().setJavaScriptEnabled(true);
        /* 设置为true表示支持使用js打开新的窗口 */
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        /* 设置缓存模式 */
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);

        /* 设置为使用webview推荐的窗口 */
        mWebView.getSettings().setUseWideViewPort(true);
        /* 设置为使用屏幕自适配 */
        mWebView.getSettings().setLoadWithOverviewMode(true);
        /* 设置是否允许webview使用缩放的功能,我这里设为false,不允许 */
        mWebView.getSettings().setBuiltInZoomControls(true);
        /* 提高网页渲染的优先级 */
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        /* HTML5的地理位置服务,设置为true,启用地理定位 */
        mWebView.getSettings().setGeolocationEnabled(true);
        /* 设置可以访问文件 */
        mWebView.getSettings().setAllowFileAccess(true);



        // 设置UserAgent标识
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " app-oortimapp");


        mWebView.getSettings().setTextZoom(250);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);



        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
    }

    private void initClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
//                int openStatus = openApp(url);
//                if (openStatus == 1) {// 该链接为跳转链接，方法内已跳转，直接return
//                    return true;
//                } else if (openStatus == 2) {// 该链接为跳转链接，但本地未安装该应用，加载该应用下载地址
//                    load(view, mDownloadUrl);
//                } else if (openStatus == 5) {// 该链接为跳转链接， 该链接为跳转链接，跳转到本地授权
//
//                } else { // 0 | 3 | 4
//                    load(view, url);
//                }

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                //mWebView.loadUrl("javascript:(" + readJS() + ")()");

                super.onPageFinished(webView, s);
                //mWebView.loadUrl("javascript:(alert('hello'))");
                mWebView.loadUrl(
                        "javascript:(function(){" +
                            "var objs = document.getElementsByTagName(\"img\"); " +
                            "for(var i=0;i<objs.length;i++){ " +

//                                "objs[i].ontap=function(){" +
//                                    "alert(this.src);" +
//
//                                "};" +
                                "objs[i].onclick=function(){" +
//                                "alert(this.src);" +
                                "window.imagelistner.openImage(this.src);" +
                                "};" +
                            "}" +

                        "}())");
//                        + "alert('hello');"
//                        + "var objs = document.getElementsByTagName(\"img\");"
//                        + " alert('hello');"
//                        + "for(var i=0;i<objs.length;i++)  " + "{"
//                        + " alert('hello') "
//                        + "    objs[i].οnclick=function()  " + "    {  "
//                        + " alert(this.src) "
//                        + "        window.imagelistner.openImage(this.src);  "
//                        + "    }  " + "}" + "})()");
            }
        });

        // 获取网页加载进度
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                currentProgress = mProgressBar.getProgress();
//                if (newProgress >= 100 && !isAnimStart) {
//                    // 防止调用多次动画
//                    isAnimStart = true;
//                    mProgressBar.setProgress(newProgress);
//                    // 开启属性动画让进度条平滑消失
//                    startDismissAnimation(mProgressBar.getProgress());
//                } else {
//                    // 开启属性动画让进度条平滑递增
//                    startProgressAnimation(newProgress);
//                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
               // mTitleTv.setText(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }



        });

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            try {
                // 不处理下载，直接抛出去，
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception ignored) {
                // 无论如何不要崩溃，比如没有浏览器，
                //ToastUtil.showToast(WebViewActivity.this, R.string.download_error);
            }
        });
//
//        jsSdkInterface = new JsSdkInterface(this, new WebViewActivity.MyJsSdkListener());
//        jsSdkInterface.setShareParams(mShareParams);
//        mWebView.addJavascriptInterface(jsSdkInterface, "AndroidWebView");
    }


    public static String getNewContent(String htmltext){
        try {
            Document doc= Jsoup.parse(htmltext);
            Elements elements=doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("width","100%").attr("height","auto");
            }
            return doc.toString();
        } catch (Exception e) {
            return htmltext;
        }
    }

    private String readJS() {
        try {
            InputStream inStream = getAssets().open("js.txt");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initEvent() {
        mTitleRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                WebMoreDialog mWebMoreDialog = new WebMoreDialog(WebViewActivity.this, getCurrentUrl(), new WebMoreDialog.BrowserActionClickListener() {
//                    @Override
//                    public void floatingWindow() {
//                        //showFloating();
//                    }
//
//                    @Override
//                    public void sendToFriend() {
//
//                        //forwardToFriend();
//                    }
//
//                    @Override
//                    public void shareToLifeCircle() {
//                        //shareMoment();
//                    }
//
//                    @Override
//                    public void collection() {
//                        //onCollection(getCurrentUrl());
//                    }
//
//                    @Override
//                    public void searchContent() {
//                        //search();
//                    }
//
//                    @Override
//                    public void copyLink() {
////                        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
////                        clipboardManager.setText(getCurrentUrl());
////                        Toast.makeText(WebViewActivity.this, getString(R.string.tip_copied_to_clipboard), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void openOutSide() {
////                        ExternalOpenDialog externalOpenDialog = new ExternalOpenDialog(mContext, getCurrentUrl());
////                        externalOpenDialog.show();
//                    }
//
//                    @Override
//                    public void modifyFontSize() {
//                        //setWebFontSiz();
//                    }
//
//                    @Override
//                    public void refresh() {
//                        mWebView.reload();
//                    }
//
//                    @Override
//                    public void complaint() {
//                        //report();
//                    }
//
//                    @Override
//                    public void shareWechat() {
////                        String title = mTitleTv.getText().toString().trim();
////                        String url = getCurrentUrl();
////                        ShareSdkHelper.shareWechat(
////                                WebViewActivity.this, title, url, url
////                        );
//                    }
//
//                    @Override
//                    public void shareWechatMoments() {
////                        String title = mTitleTv.getText().toString().trim();
////                        String url = getCurrentUrl();
////                        ShareSdkHelper.shareWechatMoments(
////                                WebViewActivity.this, title, url, url
////                        );
//                    }
//                });
//                mWebMoreDialog.show();
            }
        });
    }


    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                mProgressBar.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }


    public static List<String> GetHtmlImageSrcList(String htmlText)
    {
        List<String> imgSrc = new ArrayList<String>();
        Matcher m = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(htmlText);
        while(m.find())
        {
            imgSrc.add(m.group(1));
        }
        return imgSrc;
    }

}