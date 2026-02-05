package com.oortcloud.appstore.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oortcloud.appstore.R;
import com.oortcloud.appstore.activity.AnenstActivity;
import com.oortcloud.appstore.activity.SearchActivity;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/14 10:37
 */
public class SearchLayout  extends LinearLayout {

    private TextView mSearchText;
    private TextView mProvinceText;
    private TextView mHome;
    private TextView mMore;
    private Context mComtext;
    public SearchLayout(Context context) {
        super(context);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mComtext = context;
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
        initViews();
    }

    private void initViews() {
        mSearchText = findViewById(R.id.tv_search);
        mProvinceText = findViewById(R.id.tv_province);
        mHome = findViewById(R.id.home_tv);
        mMore = findViewById(R.id.more_tv);

        mSearchText.setOnClickListener(view ->  {

            mComtext.startActivity(new Intent(mComtext, SearchActivity.class));

        });
        mHome.setOnClickListener(view -> {
            if (mComtext instanceof Activity){
                Activity activity = (Activity) mComtext;
                activity.finish();
            }
        });
        mMore.setOnClickListener(view -> {
            mComtext.startActivity(new Intent(mComtext , AnenstActivity.class));
        });

    }
}
