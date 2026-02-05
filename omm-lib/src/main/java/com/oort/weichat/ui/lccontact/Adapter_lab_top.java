package com.oort.weichat.ui.lccontact;
import com.oort.weichat.R;
import com.oort.weichat.bean.Label;
import com.oortcloud.contacts.bean.UserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_lab_top extends RecyclerView.Adapter {

    public class VH extends RecyclerView.ViewHolder {

        public TextView tv_name;


        public VH(@NonNull View itemView, Context mc) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_lab);
        }
    }

    protected Context mContext;
    protected List mlist;
    private List sList;
    private int selectIndex = 0;
    public Adapter_lab_top(Context context , List list) {
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

//        TextView tv = new TextView(mContext);
//        FlexboxLayoutManager.LayoutParams lp = new FlexboxLayoutManager.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT,FlexboxLayout.LayoutParams.WRAP_CONTENT);
//        tv.setLayoutParams(lp);
//        tv.setTextSize(14);
//        tv.setBackgroundColor(Color.parseColor("#9d9d9d"));
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_lab_top_item,parent,false);
        VH vh = new VH(v,mContext);
        return vh;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        VH vh = (VH) holder;
        Label obj = (Label) mlist.get(position);
        vh.tv_name.setText(obj.getName());
        vh.tv_name.setTextColor(Color.parseColor("#999999"));
        vh.tv_name.setBackgroundResource(R.drawable.button_shape_gray);
        if(selectIndex == position){
            vh.tv_name.setTextColor(Color.WHITE);
            vh.tv_name.setBackgroundResource(R.drawable.button_shape);
        }
//        if (sList != null) {
//            for (int i = 0; i < sList.size(); i++) {
//                Label info1 = (Label) sList.get(i);
//                if (info1.get_id() == obj.get_id()) {
//                    //vh.iv_header.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#4475FF")));
//                    vh.tv_name.setTextColor(Color.WHITE);
//
//                    //vh.tv_name.setBackgroundColor(Color.parseColor("#4475FF"));
//                    vh.tv_name.setBackgroundResource(R.drawable.button_shape);
//
//                    break;
//                }
//            }
//        }
        vh.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }


    public void refreshSelectStatu(List list){
        sList = list;
        notifyDataSetChanged();
    }

    public void selectIndex(int i){
        selectIndex = i;
        notifyDataSetChanged();
    }

    public void refreshData(List l){
        mlist = l;
        notifyDataSetChanged();
    }

}
