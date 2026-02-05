package com.oort.weichat.ui.lccontact;
import com.oort.weichat.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PersonTypeAdapter extends BaseAdapter {

    private Context mContext;
    private List<com.oort.weichat.ui.lccontact.PersonPickActivity.PickType> items;
    private int selectIndex = 0;

    public PersonTypeAdapter(Context c,List list){
        mContext = c;
        items = list;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_pick_type_item,viewGroup,false);
            vh.iv_icon = view.findViewById(R.id.icon_iv);
            vh.tv_name = view.findViewById(R.id.lab_name);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        vh.tv_name.setText((String) items.get(i).getmTitle());

        if(selectIndex == i){
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            vh.tv_name.setTextColor(Color.parseColor("#1156A6"));
            vh.iv_icon.setBackgroundResource(items.get(i).getsIcon());
        }else{
            view.setBackgroundColor(Color.parseColor("#f1f1f1"));
            vh.tv_name.setTextColor(Color.parseColor("#666666"));
            vh.iv_icon.setBackgroundResource(items.get(i).getmIcon());
        }

        return view;
    }


    private static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
    }

    public void selectIndex(int i){
        selectIndex = i;
        notifyDataSetChanged();
    }
}
