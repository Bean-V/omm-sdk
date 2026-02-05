package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oortcloud.contacts.R;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.utils.ImageLoader;

import java.util.List;


/**
 * 部门树 部门Adapter
 */
public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private final int TYPE_BUMEN=0;//部门类型的
    private final int TYPE_PERSON=1;//个人的

    private LayoutInflater mInflater;
    private List<Sort> mData;
    private Context mContext;
    public DepartmentAdapter(Context context ,   List<Sort> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_department_layout, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      final Sort sort = mData.get(position);

      if (sort instanceof UserInfo){

          UserInfo userInfo = (UserInfo) sort;;

          ImageLoader.loaderImage(holder.portrait , userInfo);

          holder.itemView.setOnClickListener(v ->  {

              PersonDetailActivity.actionStart(mContext ,userInfo);

          });

          holder.tvName.setText(userInfo.getOort_name());
//          holder.portait.setVisibility(View.VISIBLE);

      }else if (sort instanceof  Department){

          Department department = (Department) sort;
          holder.tvName.setText(department.getOort_dname());
          holder.itemView.setOnClickListener(v ->  {
              HttpResult.getDeptAndUserTree(department.getOort_dcode() , 1 , "ordContact");

          });
          holder.iv_left.setVisibility(View.VISIBLE);
      }


    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        View itemView;
        ImageView iv_left;
        ImageView portrait;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName =  itemView.findViewById(R.id.tv_name);
            iv_left =  itemView.findViewById(R.id.iv_left);
//            portrait =  itemView.findViewById(R.id.img_head_portait);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(List<Sort> list){
        this.mData = list;
        notifyDataSetChanged();
    }
}
