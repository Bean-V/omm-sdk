package com.oort.weichat.fragment.adapter;

//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oort.weichat.R;
import com.oort.weichat.fragment.dynamic.DynamicActivityDynamicInfo;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oortcloud.appstore.utils.StringTimeUtils;
import com.oortcloud.basemodule.utils.OperLogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**

 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterDynamicHome extends RecyclerView.Adapter<AdapterDynamicHome.ViewHolder> implements View.OnTouchListener, View.OnLongClickListener {

    private static final String TAG = "AdapterFMList";

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    // private final List<BeanFile> mValues;
    private Context mContext;

    public List<OORTDynamic> getmItems() {
        return mItems;
    }

    public void setmItems(List<OORTDynamic> mItems) {
        this.mItems = mItems;
    }

    private List<OORTDynamic> mItems = new ArrayList<>();
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

    public AdapterDynamicHome(List<OORTDynamic> items) {
        mItems = items;
    }

    public AdapterDynamicHome(List<OORTDynamic> items, boolean lonTapToDrag) {
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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_dynamic_layout,parent,false);//item_fragment_filelist

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        OORTDynamic fileBean = mItems.get(position);

        String commentBody = mItems.get(position).getDynamic().getContent();
        commentBody = commentBody.replace("\n\n","\n");
        holder.tv_name.setText(commentBody);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;

        long date =(long) mItems.get(position).getDynamic().getCreated_at();


        holder.tv_src.setText(StringTimeUtils.formatSomeAgo((long)(date * 1000),mContext));


        if(mItems.get(position).getDynamic().getLikes() != null) {
            holder.tv_zan_cout.setText(mItems.get(position).getDynamic().getLikes().getCounts() + "");
        }
        if(mItems.get(position).getDynamic().getComments() != null) {
            holder.tv_comment_cout.setText(mItems.get(position).getDynamic().getComments().getCounts() + "");
        }

        if(fileBean.getDynamic().getAttach().size() > 0) {
            Glide.with(mContext).load(fileBean.getDynamic().getAttach().get(0).getUrl()).into(holder.iv_icon);
        }else{
            holder.iv_icon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicActivityDynamicInfo.start(mContext,fileBean.getDynamic().getOort_duuid());
                OperLogUtil.msg("点击了查看动态详情"+fileBean.getDynamic().getOort_duuid());
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }
    @Override
    public int getItemCount() {

        Log.d(TAG, "000000getItemCount: " + mItems.size());
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
        public  TextView tv_zan_cout;
        public  TextView tv_comment_cout;

        public ViewHolder(View v) {
            super(v);
            iv_icon = v.findViewById(R.id.iv_icon);
            tv_name = v.findViewById(R.id.tv_content);
           // tv_size = v.findViewById(R.id.iv_fm_list_item_filesize);

            tv_src = v.findViewById(R.id.tv_src);
            tv_zan_cout = v.findViewById(R.id.tv_zan_count);
            tv_comment_cout = v.findViewById(R.id.tv_comment_count);
        }

    }

    public void refresh(List list){
        mItems.clear();
        mItems.addAll(list);

        Log.d(TAG, "000000getItemCount: refresh" + mItems.size());
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