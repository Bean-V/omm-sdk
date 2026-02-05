package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;

public class DynamicActivityUserHome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_user_home);


        //getSupportActionBar().hide();
        ImageView iv_left = (ImageView) findViewById(R.id.iv_back);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String mTid = getIntent().getStringExtra("tid");



        if(mTid != null) {

            FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
            fragmentDynamicList.setContentType(14);
            fragmentDynamicList.setOort_uuuid(mTid);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.ll_container, fragmentDynamicList);
            transaction.commitNow();
        }
    }

    public static void start(Context context, String tid) {
        Intent starter = new Intent(context, DynamicActivityUserHome.class);
        starter.putExtra("tid",tid);
        context.startActivity(starter);
    }
}