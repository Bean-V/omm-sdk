package com.oort.weichat.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oort.weichat.R;
import com.xuexiang.xui.widget.textview.marqueen.MarqueeFactory;

/**
 * 简单字幕
 */
public class OortNoticeMF extends MarqueeFactory<LinearLayout, String> {
    private LayoutInflater inflater;

    public OortNoticeMF(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public LinearLayout generateMarqueeItemView(String data) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.oort_marqueen_layout_notice_item, null);
        TextView tv = view.findViewById(R.id.tv_not);
        tv.setMaxLines(3);
        tv.setText(data);
        return view;
    }
}