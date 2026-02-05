package com.oort.weichat.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.fragment.dynamic.DynamicListAdapter;
import com.oort.weichat.fragment.entity.NewsCommentRes;
import com.oort.weichat.fragment.entity.OORTGANews;
import com.oort.weichat.fragment.entity.Res;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.view.TrillCommentInputDialog;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.custom.JavascriptInterface;
import com.oortcloud.login.net.utils.RxBus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeActivityNewsDetail extends BaseActivity {

    private int mTid;
    private WebView mWebView;
    private RecyclerView rv_comment;
    private CommentAdpter commentAdp;
    private OORTGANews mNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news_detail);


        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.detail));
        ImageView iv_left = (ImageView) findViewById(R.id.iv_title_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView iv_right = (ImageView) findViewById(R.id.iv_title_right);
        iv_right.setImageResource(R.mipmap.icon_share);

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject obj = new JSONObject();
                obj.put("sub",mNews.getIntro());

                obj.put("title",mNews.getTitle());

                obj.put("type","share_news_content");

                if(mNews.getCoverImg() != null) {
                    obj.put("img", mNews.getCoverImg());
                }

                obj.put("param",String.valueOf(mTid));

                Context mContext = AppStoreInit.getInstance().getApplication();
                String appid = mContext.getApplicationInfo().processName;
                Intent in = new Intent(appid + ".shareFriend");
                in.putExtra("action","shareFriend");
                in.putExtra("content",obj.toString());

                startActivity(in);
            }
        });

        mTid = getIntent().getIntExtra("tid",-1);
        String name = getIntent().getStringExtra("name");

        if(mTid > -1) {
            initView();

            initClient();
            initEvent();;
            loadData();
        }



    }


    private void initView() {
        mWebView = (WebView) findViewById(R.id.mWebView);
        /* 设置支持Js */
        mWebView.getSettings().setJavaScriptEnabled(true);
        /* 设置为true表示支持使用js打开新的窗口 */
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//
//        /* 设置缓存模式 */
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
//
//        /* 设置为使用webview推荐的窗口 */
//        mWebView.getSettings().setUseWideViewPort(true);
////        /* 设置为使用屏幕自适配 */
        mWebView.getSettings().setLoadWithOverviewMode(true);
////        /* 设置是否允许webview使用缩放的功能,我这里设为false,不允许 */
//        mWebView.getSettings().setBuiltInZoomControls(true);
        /* 提高网页渲染的优先级 */
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        /* HTML5的地理位置服务,设置为true,启用地理定位 */
        mWebView.getSettings().setGeolocationEnabled(true);
        /* 设置可以访问文件 */
        mWebView.getSettings().setAllowFileAccess(true);
//
//
//
//        // 设置UserAgent标识
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " app-oortimapp");

//
//        mWebView.getSettings().setTextZoom(150);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);



        mWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");

        rv_comment = findViewById(R.id.rv_comment);
        commentAdp = new CommentAdpter(this);
        rv_comment.setAdapter(commentAdp);
    }

    private void initClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
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


                                "objs[i].onclick=function(){" +
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


        findViewById(R.id.tv_addComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNews == null){
                    return;
                }
                showCommentEnterView("","","",null);
            }
        });

    }

    public void showCommentEnterView(String toUserId, String toNickname, String toShowName, DynamicListAdapter.ViewHolder vh) {



        String hint;
        Boolean isReply = false;
        if (TextUtils.isEmpty(toUserId) || TextUtils.isEmpty(toNickname) || TextUtils.isEmpty(toShowName)) {
            // mPMsgBottomView.setHintText("");
            hint = mContext.getString(R.string.enter_pinlunt);

        } else {
            // mPMsgBottomView.setHintText(getString(R.string.replay_text, toShowName));
            hint = mContext.getString(R.string.replay_text, toShowName);
            isReply = true;
        }
        // mPMsgBottomView.show();
        Boolean finalIsReply = isReply;
        TrillCommentInputDialog trillCommentInputDialog = new TrillCommentInputDialog(mContext, hint, str -> {
            if (!StringUtil.isBlank(str)) {
               // mCommentReplyCache.text = str;
                //addComment(mCommentReplyCache);

                String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
                if(finalIsReply){
                    HttpRequestParam.replySystemAdd(str,String.valueOf(mNews.getId()),0,String.valueOf(mNews.getId())).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Res res = JSON.parseObject(s,Res.class);//
                            if(res.getCode() == 200){
                                loadReply();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.v("msg" , e.toString());
                        }
                    });

                    return;

                }
                HttpRequestParam.replySystemAdd(str,String.valueOf(mNews.getId()),0,String.valueOf(mNews.getId())).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Res res = JSON.parseObject(s,Res.class);//
                        if(res.getCode() == 200){
                            loadReply();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("msg" , e.toString());
                    }
                });

            }
        });
        Window window = trillCommentInputDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);// 软键盘弹起
            trillCommentInputDialog.show();
        }
    }

    private void loadData() {
        String uuid= UserInfoUtils.getInstance(this).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

        HttpRequestParam.GAYWNEWSDetail(mToken,mTid).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                    ResObj<OORTGANews> res = JSON.parseObject(s,new TypeToken<ResObj<OORTGANews>>() {}.getType());//
                    if(res.getCode() == 200 && res.getData() != null){


                       TextView time = findViewById(R.id.tv_time);
                       time.setText(res.getData().getTime());

                        TextView title = findViewById(R.id.tv_title);
                        title.setText(res.getData().getTitle());

                        mNews = res.getData();
                        String content = res.getData().getContent();

                        content = content.replace("<img", "<img style=\"max-width:100%;height:auto;margin:0 auto;display: flex;\"");

                        mWebView.loadDataWithBaseURL(Constant.BASE_URL,content,"text/html","utf-8",null);


                    }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                //mRefreshLayout.closeHeaderOrFooter();
            }
        });

        loadReply();



    }

    void loadReply(){
        String uuid= UserInfoUtils.getInstance(this).getUserId();
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        HttpRequestParam.reply_list(mToken,1,uuid, String.valueOf(mTid)).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                NewsCommentRes res = JSON.parseObject(s,NewsCommentRes.class);//
                if(res.getCode() == 200 && res.getData() != null && res.getData().getLists() != null){

                    commentAdp.setData(res.getData().getLists());

                    TextView tv = findViewById(R.id.tv_allcomment);
                    tv.setText(getString(R.string.comment_all)+"(" + res.getData().getLists().size() +")");

                }
            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
                //mRefreshLayout.closeHeaderOrFooter();
            }
        });
    }

    public static void start(Context context,int tid) {
        Intent starter = new Intent(context, HomeActivityNewsDetail.class);
        starter.putExtra("tid",tid);
        context.startActivity(starter);
    }




    public class CommentAdpter extends BaseRecyclerViewAdapter<NewsCommentRes.DataBean.ListsBean> {

        public CommentAdpter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_home_news_reply_list_item_layout, parent , false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            final ViewHolder holder = (ViewHolder) viewHolder;

            final NewsCommentRes.DataBean.ListsBean info = lists.get(position);
            holder.name.setText(info.getName());
            holder.content.setText(info.getContent());
            holder.time.setText(TimeUtils.getFriendlyTimeDesc(mContext, (int) info.getCreated_on()));


            ImageLoader.loadImage(holder.headerIcon,info.getPortrait(),com.oortcloud.contacts.R.mipmap.default_head_portrait);

            holder.reply.setVisibility(View.GONE);
            holder.reply.setOnClickListener(view ->  {


                if(info != null) {
                    if(1 == 0){


                    }

                }

            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView headerIcon;
            TextView name;
            TextView content;

            TextView time;

            Button reply;
            public ViewHolder( View itemView) {
                super(itemView);
                headerIcon = itemView.findViewById(R.id.iv_head_icon);
                name = itemView.findViewById(R.id.tv_name);
                content = itemView.findViewById(R.id.tv_content);
                time = itemView.findViewById(R.id.tv_time);
                reply = itemView.findViewById(R.id.btn_replay);

            }
        }

    }
}