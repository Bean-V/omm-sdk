package com.oort.weichat.ui.lccontact;
import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.R;
import com.oort.weichat.view.CircleImageView;
import com.oortcloud.appstore.widget.RoundImageView;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class Adapter_contacts_seleted extends RecyclerView.Adapter {

    public class VH extends RecyclerView.ViewHolder {



        //public final LinearLayout layout;
        //public ShapeableImageView iv_header;
        public RoundedImageView iv_header;
        //public RoundImageView header;
        public TextView tv_name;
        public Context mContext;


        public VH(@NonNull View itemView, Context mc) {
            super(itemView);
            View view = itemView;
            mContext = mc;
            iv_header = view.findViewById(R.id.iv_pt_header);
            tv_name = view.findViewById(R.id.tv_pt_name);
            mContext = mc;
        }
    }


    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    protected Context mContext;
    protected List mlist;
    private List sList;
    public Adapter_contacts_seleted(Context context , List list) {
        mContext = context;
        this.mlist =list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_picked_item,parent,false);
        VH vh = new VH(v,mContext);
        return vh;
    }

    @SuppressLint({"ResourceType", "RecyclerView"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        VH vh = (VH) holder;
        UserInfo obj = (UserInfo) mlist.get(position);
//        vh.iv_header;
//        vh.tv_name;

        vh.iv_header.setBackgroundResource(R.mipmap.labuser);
        ImageLoader.loaderImage(vh.iv_header,obj);

        vh.tv_name.setText(obj.getOort_name());

        vh.iv_header.setBorderWidth((float) 0.001);
        //vh.iv_header.setStrokeWidth(0);
        vh.tv_name.setTextColor(Color.BLACK);
        vh.tv_name.setBackgroundColor(Color.WHITE);
        vh.tv_name.setBackgroundResource(0);

        vh.iv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(position);
            }
        });

        if (sList != null) {
            for (int i = 0; i < sList.size(); i++) {
                UserInfo info1 = (UserInfo) sList.get(i);
                if (info1.getOort_uuid().equals(obj.getOort_uuid())) {
                    //vh.iv_header.setBorderWidth(2);
                    vh.iv_header.setBorderWidth((float) 2);
                    vh.iv_header.setBorderColor(Color.parseColor("#4475FF"));
                    //vh.iv_header.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#4475FF")));
                    vh.tv_name.setTextColor(Color.WHITE);

                    //vh.tv_name.setBackgroundColor(Color.parseColor("#4475FF"));
                    vh.tv_name.setBackgroundResource(R.drawable.button_shape);

                    break;
                }
            }
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
