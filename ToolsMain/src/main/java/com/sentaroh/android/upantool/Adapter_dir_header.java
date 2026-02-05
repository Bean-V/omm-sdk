package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sentaroh.android.upantool.R;

import java.util.List;

public class Adapter_dir_header extends RecyclerView.Adapter {

    public class VH extends RecyclerView.ViewHolder {

        public TextView tv_name;


        public VH(@NonNull View itemView, Context mc) {
            super(itemView);
            tv_name = (TextView) itemView;
        }
    }


    public static class RvItem {

        public String text;
        public Object obj;

        public RvItem(String text, Object obj) {
            this.text = text;
            this.obj = obj;
        }
    }

    protected Context mContext;
    protected List mlist;
    private List sList;
    public Adapter_dir_header(Context context , List list) {
        mContext = context;
        this.mlist =list;
    }



    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView tv = new TextView(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setTextSize(14);
        TextPaint paint=tv.getPaint();

        paint.setFakeBoldText(true);
        VH vh = new VH(tv,mContext);
        return vh;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        VH vh = (VH) holder;
        RvItem obj = (RvItem) mlist.get(position);
        vh.tv_name.setText(obj.text+"/");
        vh.tv_name.setTextColor(R.color.colorZT99);

        if(position == mlist.size() - 1){
            vh.tv_name.setText(obj.text);
            vh.tv_name.setTextColor(R.color.colorZT33);


        }else{
            vh.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }


    public void refreshSelectStatu(List list){
        sList = list;
        notifyDataSetChanged();
    }

    public void refreshData(List list){
        mlist = list;
        notifyDataSetChanged();



    }
}
