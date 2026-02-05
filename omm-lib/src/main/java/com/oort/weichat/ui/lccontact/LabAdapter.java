package com.oort.weichat.ui.lccontact;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.bean.Label;

import java.util.List;

public class LabAdapter extends BaseAdapter {

    private Context mContext;
    private List items;
    private int selectIndex = 0;

    public LabAdapter(Context c,List list){
        mContext = c;
        items = list;
    }
    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return i < items.size() ? items.get(i) : "";
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;



        if(view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_lab_item,viewGroup,false);
            vh.tv_flag = view.findViewById(R.id.lab_flag);
            vh.tv_name = view.findViewById(R.id.lab_name);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }


        if(selectIndex == i && selectIndex != items.size()){
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            vh.tv_name.setTextColor(Color.parseColor("#1156A6"));
            vh.tv_flag.setVisibility(View.VISIBLE);
        }else{
            view.setBackgroundColor(Color.parseColor("#f1f1f1"));
            vh.tv_name.setTextColor(Color.parseColor("#666666"));
            vh.tv_flag.setVisibility(View.GONE);
        }
        if(i == items.size()){
            vh.tv_name.setTextColor(Color.parseColor("#1156A6"));
            vh.tv_name.setText(mContext.getString(R.string.add_tag_plus));
        }else {
            Label lab = (Label) items.get(i);
            vh.tv_name.setTextColor(Color.parseColor("#666666"));
            vh.tv_name.setText(lab.getName());
        }
        return view;
    }


    private static class ViewHolder{
        TextView tv_flag;
        TextView tv_name;
    }

    public void selectIndex(int i){
        selectIndex = i;
        notifyDataSetChanged();
    }

    public void refreshData(List l){
        items = l;
        notifyDataSetChanged();
    }
}
