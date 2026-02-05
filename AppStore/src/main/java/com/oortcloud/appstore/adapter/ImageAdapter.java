package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.http.HttpConstants;
import com.oortcloud.basemodule.widget.wimagepreviewlib.WImagePreviewBuilder;
import com.oortcloud.basemodule.widget.wimagepreviewlib.listener.OnPageListener;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * pagerview 图片适配器
 */

public class ImageAdapter extends PagerAdapter {

    private List<String> mUrl;
    private Context mContext;

    public ImageAdapter(List<String> url, Context context) {
        mUrl = url;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mUrl.size();
    }

    /**
     * 该函数用来判断instantiateItem(ViewGroup, int)
     * 函数所返回来的Key与一个页面视图是否是代表的同一个视图(即它俩是否是对应的，对应的表示同一个View)
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        //几乎是固定的写法,
        return view == object;
    }

    /**
     * 返回要显示的view,即要显示的视图
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_image_layout, null);
        ImageView imageView = view.findViewById(R.id.imv_icon);
        String url = mUrl.get(position);
        //修改为相对地址
        if (!TextUtils.isEmpty(url)&& url.contains("oort/")){

            url =  HttpConstants.BASE_URL +url.substring(url.indexOf("oort/") );

        }
        if (!TextUtils.isEmpty(url)) {
            Glide.with(mContext).load(url).into(imageView);
        }

        container.addView(view);    //这一步很重要


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> lists = new ArrayList<String>();
                for (int i = 0; i < mUrl.size(); i++) {

                    String att0 = mUrl.get(i);
                    lists.add(att0);
                }
                WImagePreviewBuilder
                        .load(mContext)
                        .setData(lists)
                        .setPosition(position)
                        .setOnPageListener(new OnPageListener() {
                            @Override
                            public void onClick(Object o, int position) {
                                super.onClick(o, position);
                            }
                        })
                        .start();
            }
        });
        return view;
    }

    /**
     * 销毁条目
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public float getPageWidth(int position) {
        return  (float) 0.33;
    }
}


