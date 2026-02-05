package com.oortcloud.appstore.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.ClassifyInfo;

import java.util.List;

/**
 * @filename:
 * @function： 类型分类Adapter
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/14 14:39
 */
public class TypeClassifyAdapter extends BaseRecyclerViewAdapter<ClassifyInfo> {

    private int index;
    public TypeClassifyAdapter(Context context) {
        super(context);
    }
    public TypeClassifyAdapter(Context context , List listData) {
        super(context);
        lists = listData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.item_type_classify_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        holder.typeName.setText(lists.get(position).getName());

        holder.itemView.setOnClickListener(view ->  {

                onItemClickListener.onItemClick(position);
                notifyDataSetChanged();
        });

        setAttributes(holder , position);


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView typeName;
        View view;
        View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            typeName = itemView.findViewById(R.id.tv_type_name);
            view = itemView.findViewById(R.id.line_view);
        }
    }


    private void setAttributes(ViewHolder holder , int position){
//        如果下标和传回来的下标相等 那么确定是点击的条目 把背景设置一下颜色
        if (position == getmPosition()) {

            holder.view.setVisibility(View.VISIBLE);
            holder.typeName.setBackgroundResource(R.color.color_FAFFFFFF);
            holder.typeName.setTextColor(mContext.getResources().getColor(R.color.color_1A1A1A));
        }else{
//            否则的话就全白色初始化背景
//            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.view.setVisibility(View.GONE);
            holder.typeName.setBackgroundResource(R.color.color_F2F2F2);
            holder.typeName.setTextColor(mContext.getResources().getColor(R.color.color_666666));
        }

    }

    public int getmPosition() {
        return index;
    }

    public void setmPosition(int mPosition) {
        this.index = mPosition;
    }

}
