package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/14 15:01
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {

    protected Context mContext;
    protected LayoutInflater inflater;
    protected OnClickListener onClickListener;
    protected OnItemClickListener onItemClickListener;

    protected List<T> lists = new ArrayList<>();

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
//        initListener();
    }



//    private void initListener() {
//        onClickListener = new OnClickListener() {
//            @Override
//            public void onClick(int position, long itemId) {
//                if (onItemClickListener != null)
//                    onItemClickListener.onItemClick(position, itemId);
//            }
//        };
//    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    //设置数据
    public  void
    setData(List date){
//        lists.clear();
        lists = date;
        notifyDataSetChanged();

    }

    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }

    public void  setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
