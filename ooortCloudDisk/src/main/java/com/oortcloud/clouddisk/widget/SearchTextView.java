package com.oortcloud.clouddisk.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.SearchActivity;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/14 10:37
 */
public class SearchTextView extends LinearLayout {



    public interface SearchTap{
        void tap(View v);
    }
    private Context mComtext;
    private LinearLayout mSearchLL;

    public SearchTap getSearchTap() {
        return searchTap;
    }

    public void setSearchTap(SearchTap searchTap) {
        this.searchTap = searchTap;
    }

    private SearchTap searchTap;
    public SearchTextView(Context context) {
        super(context);
    }

    public SearchTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mComtext = context;
        LayoutInflater.from(context).inflate(R.layout.search_text_view, this);
        initViews();
    }

    private void initViews() {
       findViewById(R.id.ll_1).setOnClickListener(view ->  {


           if(searchTap != null){
               searchTap.tap(view);
           }else {
               mComtext.startActivity(new Intent(mComtext, SearchActivity.class));
           }

        });



    }
}
