package com.oort.weichat.ui.lccontact;
import com.oort.weichat.R;
import com.oortcloud.contacts.adapter.DepartAndUserAdapter;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.UserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_org_header extends RecyclerView.Adapter {

    public class VH extends RecyclerView.ViewHolder {

        public TextView tv_name;


        public VH(@NonNull View itemView, Context mc) {
            super(itemView);
            tv_name = (TextView) itemView;
        }
    }

    protected Context mContext;
    protected List mlist;
    private List sList;
    public Adapter_org_header(Context context , List list) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        VH vh = (VH) holder;
        Department obj = (Department) mlist.get(position);
        vh.tv_name.setText(obj.getOort_dname()+"/");
        vh.tv_name.setTextColor(Color.parseColor("#999999"));

        if(position == mlist.size() - 1){
            vh.tv_name.setText(obj.getOort_dname());
            vh.tv_name.setTextColor(Color.parseColor("#333333"));


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
