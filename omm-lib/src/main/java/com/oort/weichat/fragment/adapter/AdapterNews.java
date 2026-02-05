package com.oort.weichat.fragment.adapter;

//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.OORTGANews;
import com.oort.weichat.fragment.home.HomeActivityNewsDetail;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**

 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterNews extends RecyclerView.Adapter<AdapterNews.ViewHolder> implements View.OnTouchListener, View.OnLongClickListener {

    private static final String TAG = "AdapterFMList";

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    // private final List<BeanFile> mValues;
    private Context mContext;

    public List<OORTGANews> getmItems() {
        return mItems;
    }

    public void setmItems(List<OORTGANews> mItems) {
        this.mItems = mItems;
    }

    private List<OORTGANews> mItems = new ArrayList<>();
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

    public AdapterNews(List<OORTGANews> items) {
        mItems = items;
    }

    public AdapterNews(List<OORTGANews> items, boolean lonTapToDrag) {
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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_news_layout,parent,false);//item_fragment_filelist

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        OORTGANews fileBean = mItems.get(position);
        holder.tv_name.setText(mItems.get(position).getIntro());



        holder.tv_src.setText(mItems.get(position).getAuthor());

        holder.tv_time.setText(fileBean.getTime());

        if(StringUtil.isBlank(fileBean.getCoverImg())){
            holder.iv_icon.setVisibility(View.GONE);

        }else{
            Glide.with(mContext).load(fileBean.getCoverImg()).into(holder.iv_icon);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivityNewsDetail.start(mContext,fileBean.getId());

                OperLogUtil.msg("点击了查看新闻详情"+fileBean.getId());
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
        public  TextView tv_src;
        public  TextView tv_time;
        public  TextView tv_comment_cout;

        public ViewHolder(View v) {
            super(v);
            iv_icon = v.findViewById(R.id.iv_icon);
            tv_name = v.findViewById(R.id.tv_content);
            // tv_size = v.findViewById(R.id.iv_fm_list_item_filesize);

            tv_src = v.findViewById(R.id.tv_src);
            tv_time = v.findViewById(R.id.tv_time);
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


