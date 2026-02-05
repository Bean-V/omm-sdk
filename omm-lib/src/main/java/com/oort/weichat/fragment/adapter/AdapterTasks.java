package com.oort.weichat.fragment.adapter;

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
import com.oortcloud.basemodule.im.TaskMsgInfoBean;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**

 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.ViewHolder> implements View.OnTouchListener, View.OnLongClickListener {

    private static final String TAG = "AdapterFMList";

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    // private final List<BeanFile> mValues;
    private Context mContext;

    public List<TaskMsgInfoBean> getmItems() {
        return mItems;
    }

    public void setmItems(List<TaskMsgInfoBean> mItems) {
        this.mItems = mItems;
    }

    private List<TaskMsgInfoBean> mItems = new ArrayList<>();
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

    public AdapterTasks(List<TaskMsgInfoBean> items) {
        mItems = items;
    }

    public AdapterTasks(List<TaskMsgInfoBean> items, boolean lonTapToDrag) {
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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_task_layout,parent,false);//item_fragment_filelist

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        TaskMsgInfoBean fileBean = mItems.get(position);
        holder.tv_name.setText(mItems.get(position).getSub());


        holder.tv_title.setText(mItems.get(position).getTitle());
        holder.tv_src.setText(mItems.get(position).getAppid());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // 根据时间戳创建 Date 对象
        Date date = new Date(fileBean.getTime());
        // 将 Date 对象格式化为字符串
        String dateStr= sdf.format(date);
        holder.tv_time.setText(dateStr);

        if(StringUtil.isBlank(fileBean.getImg())){
            holder.iv_icon.setVisibility(View.GONE);

        }else{
            Glide.with(mContext).load(fileBean.getName()).into(holder.iv_icon);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtil.isBlank(fileBean.getAppid())){
                    holder.iv_icon.setVisibility(View.GONE);
                    ToastUtil.showToast(mContext,"任务未关联应用");

                }else{
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(position);
                    }
                }

                OperLogUtil.msg("点击了查看任务详情"+fileBean.getAppid());
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
        public  TextView tv_title;

        public ViewHolder(View v) {
            super(v);
            iv_icon = v.findViewById(R.id.iv_icon);
            tv_title = v.findViewById(R.id.tv_title);
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
