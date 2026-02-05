package com.sentaroh.android.upantool;

//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.SafFile3;
import com.zhihu.matisse.Config;


//import com.oort.upantool.databinding.FragmentFilelistBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**

 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterFMList extends RecyclerView.Adapter<AdapterFMList.ViewHolder> implements View.OnTouchListener, View.OnLongClickListener {

    private static final String TAG = "AdapterFMList";

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    // private final List<BeanFile> mValues;
    private Context mContext;

    public List<BeanFile> getmItems() {
        return mItems;
    }

    public void setmItems(List<BeanFile> mItems) {
        this.mItems = mItems;
    }

    private List<BeanFile> mItems = new ArrayList<>();
    private List sList;

    private boolean ischeck = false;

    public void setSelectUsers(List selectUsers) {
        this.selectUsers = selectUsers;
    }

    private List selectUsers;

    public Boolean longTapToDrag = false;

    public Boolean getLongTapToDrag() {
        return longTapToDrag;
    }

    public void setLongTapToDrag(Boolean longTapToDrag) {
        this.longTapToDrag = longTapToDrag;
    }

    public AdapterFMList(List<BeanFile> items) {
        mItems = items;
    }

    public AdapterFMList(List<BeanFile> items,boolean lonTapToDrag) {
        mItems.addAll(items);
        longTapToDrag = lonTapToDrag;
    }

    private ItemClickListener mItemClickListener;
    public interface ItemClickListener {
        void onItemClick(int position);
        void onItemCheckClick(int position);
        void onItemLongClick(boolean edit);
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_filelist_new,parent,false);//item_fragment_filelist

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        BeanFile fileBean = mItems.get(position);
        holder.iv_icon.setImageResource(mItems.get(position).getTypeIcon());
        holder.tv_name.setText(mItems.get(position).getName());
       // holder.tv_size.setText(mItems.get(position).getSize());

        holder.itemView.setTag(mItems.get(position));
        holder.iv_checkIcon.setImageResource(R.mipmap.ic_fm_list_item_uncheck);
        holder.iv_checkIcon.setVisibility(View.GONE);




        if(fileBean.getObj() != null){
            Object o = fileBean.getObj();
            if(o instanceof File){
                File fi = (File) o;

                if(fi.isDirectory()){
//                    if(!fi.canWrite() || !fi.canRead()){
//                        holder.tv_info.setText("无权限");
//                    }else {
//                        holder.tv_info.setText(fi.listFiles().length + " 项");
//                    }
                    holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(),"yyyy/MM/dd hh:mm:ss") );
                }else {
                    if(ischeck){
                        holder.iv_checkIcon.setVisibility(View.VISIBLE);
                    }
                    holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(),"yyyy/MM/dd hh:mm:ss") + "  " + mItems.get(position).getSize());
                }


                if(fi.getName().endsWith("jpg") || fi.getName().endsWith("png") || fi.getName().endsWith("jpeg") || fi.getName().endsWith("mp4")) {
                    Glide.with(mContext).load(fi).into(holder.iv_icon);
                }
            }
            if(o instanceof SafFile3){
                SafFile3 fi = (SafFile3) o;

                if(fi.isDirectory()){
//                    if(!fi.canWrite() || !fi.canRead()){
//                        holder.tv_info.setText("无权限");
//                    }else {
//                        holder.tv_info.setText(fi.getCount() + " 项");
//                    }
                    holder.iv_icon.setImageResource(R.mipmap.ic_fm_filetype_folder_s);
                    holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(),"yyyy/MM/dd hh:mm:ss") );

                }else {

                    if(ischeck){
                        holder.iv_checkIcon.setVisibility(View.VISIBLE);
                    }
                    holder.tv_info.setText(TimeUtil.formatMsecToString(fi.lastModified(),"yyyy/MM/dd hh:mm:ss") + "  " + mItems.get(position).getSize());

                }
                if(fi.getName().endsWith("jpg") || fi.getName().endsWith("png") || fi.getName().endsWith("jpeg") || fi.getName().endsWith("mp4")) {


                   if(!fi.isSafFile()) {
                       Glide.with(mContext).load(fi.getPath()).into(holder.iv_icon);
                   }else {
                       Glide.with(mContext).load(fi.getUri()).into(holder.iv_icon);
                   }
                }
            }
        }




        if (sList != null) {
            for (int i = 0; i < sList.size(); i++) {
                BeanFile info1 = (BeanFile) sList.get(i);
                if (info1.getPath().equals(mItems.get(position).getPath()) && info1.getName().equals(mItems.get(position).getName())) {
                    holder.iv_checkIcon.setImageResource(R.mipmap.ic_fm_list_item_check);

                    break;
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(position);
            }
        });

        holder.iv_checkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemCheckClick(position);
            }
        });



            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(Config.ImagePick){
                        return true;
                    }

                    if(!longTapToDrag) {


                        ischeck = !ischeck;
                        notifyDataSetChanged();
                        mItemClickListener.onItemLongClick(ischeck);
                    }else{
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            view.startDragAndDrop(data, shadowBuilder, view, 0);
                        } else {
                            view.startDrag(data, shadowBuilder, view, 0);
                        }
                    }
                    return true;
                }

            });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onLongClick(View view) {
//        Log.d(TAG, "onTouch: ACTION_BUTTON_PRESS"+ event.getAction()+ "%%%%%" +event.getDownTime());
//        switch (event.getAction()) {
//
//
//            case MotionEvent.ACTION_BUTTON_PRESS:
//                Log.d(TAG, "onTouch: ACTION_BUTTON_PRESS"+ event.getDownTime());
//            case MotionEvent.ACTION_HOVER_MOVE:
//                Log.d(TAG, "onTouch: ACTION_HOVER_MOVE"+ event.getDownTime());
//            case MotionEvent.ACTION_DOWN:
//
//                Log.d(TAG, "onTouch: "+ event.getDownTime());
        {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                } else {
                    view.startDrag(data, shadowBuilder, view, 0);
                }
                return true;
        }
        //return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  ImageView iv_icon;
        public  TextView tv_name;
        //public  TextView tv_size;
        public  ImageView iv_checkIcon;
        public  TextView tv_info;

        public ViewHolder(View v) {
            super(v);
            iv_icon = v.findViewById(R.id.iv_fm_list_item_icon);
            tv_name = v.findViewById(R.id.iv_fm_list_item_name);
           // tv_size = v.findViewById(R.id.iv_fm_list_item_filesize);
            iv_checkIcon = v.findViewById(R.id.iv_fm_list_item_checkicon);

            tv_info = v.findViewById(R.id.iv_fm_list_item_info);
        }

    }

    public void refresh(List list){
        mItems.clear();
        mItems.addAll(list);
        //mItems = list;
        notifyDataSetChanged();
    }

    public void  refreshUserCheckStatu(List users){
        sList = users;
        notifyDataSetChanged();
    }

    public void  refreshEdit(boolean edit){

        if(ischeck != edit) {
            ischeck = edit;
            notifyDataSetChanged();
        }
    }
}