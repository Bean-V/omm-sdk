package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.sentaroh.android.upantool.R;

public class ActivityWeb extends BaseActivity {

    private WebView web;
    private Toolbar tb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        //getSupportActionBar().hide();
        setStatusBarLight(true);
        web = findViewById(R.id.web_pric);


        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        getData();
    }


    void getData(){
        String url = getIntent().getStringExtra("url");
        if(url != null){
            web.loadUrl(url);
            tb.setTitle("移速科技");
        }else{
            web.loadUrl("https://oortcloudsmart.com/privacypolicy_dab.html");
            tb.setTitle(R.string.privacy_tips_key2);
        }
    }

    public static void goWeb(Context context,String url){
        Intent in = new Intent(context,ActivityWeb.class);
        in.putExtra("url",url);
        context.startActivity(in);

    }
}