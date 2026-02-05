package com.sentaroh.android.upantool;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioGroup;

import com.sentaroh.android.upantool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFind#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFind extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentFind() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFind.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFind newInstance(String param1, String param2) {
        FragmentFind fragment = new FragmentFind();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_find, container, false);
        WebView web = v.findViewById(R.id.web_find);

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
                    return false;
                }

            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    web.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
            }


        });

//web.loadUrl("https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693")
//        web.loadUrl("https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693");

        v.findViewById(R.id.ll_check).setVisibility(View.GONE);
        web.loadUrl("https://oortcloudsmart.com/");

        web.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本

        //web.loadUrl("http://www.baidu.com/");
        RadioGroup rg = v.findViewById(R.id.rg);
        final List<Fragment> frags = new ArrayList<Fragment>();
        frags.add(new FragmentHome());
        frags.add(new FragmentFind());
        frags.add(new FragmentSetting());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.home_tab) {//web.loadUrl("https://movespeed.tmall.com");

                    //web.loadUrl("https://movespeed.tmall.com/shop/view_shop.htm");

                    web.loadUrl("https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693");
                } else if (checkedRadioButtonId == R.id.find_tab) {//String data ="MOVESPEED移速官方旗舰店的个人主页】长按复制此条消息，长按复制打开抖音搜索，查看TA的更多作品##xiPSvpxpgx8##[抖:/ 音口令]";
                    //web.loadDataWithBaseURL(null,data, "text/html",  "utf-8", null);
                    web.loadUrl("https://v.douyin.com/kAwenty/");
                } else if (checkedRadioButtonId == R.id.set_tab) {
                    web.loadUrl("https://mall.jd.com/index-1000225307.html?from=pc");
                }
            }
        });
        return v;
    }
}