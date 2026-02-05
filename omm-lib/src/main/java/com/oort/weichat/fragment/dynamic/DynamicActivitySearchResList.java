package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;

public class DynamicActivitySearchResList extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_search_list);
       // getSupportActionBar().hide();

        String name = getIntent().getStringExtra("key");

        if(name != null) {

            FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
            fragmentDynamicList.setContentType(15);
            fragmentDynamicList.setSearchKey(name);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.ll_container, fragmentDynamicList);
            transaction.commitNow();
        }

        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText("搜索结果");
        ImageView iv_left = (ImageView) findViewById(R.id.iv_title_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public static void start(Context context,String key) {
        Intent starter = new Intent(context, DynamicActivitySearchResList.class);
        starter.putExtra("key",key);
        context.startActivity(starter);
    }
}