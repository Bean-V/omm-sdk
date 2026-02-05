package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oortcloud.contacts.R;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.ImageLoader;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @filename:
 * @author: zzj/@date: 2020/11/18 17:20
 * @version： v1.0
 * @function：
 */
public class DepartAndUserAdapter extends RecyclerView.Adapter<DepartAndUserAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<? extends Sort> mData;

    private List<UserInfo> selectUsers;
    private final Context mContext;
    private final String mEVentType;
    private ItemClickListener mItemClickListener;
    public DepartAndUserAdapter(Context context , List<? extends Sort> data , String eventType) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = data;
        mEVentType = eventType;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_depanduser_layout, parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sort sort = mData.get(position);

        if (sort instanceof UserInfo){

            UserInfo userInfo = (UserInfo) sort;

            ImageLoader.loaderImage(holder.mUserPortraitImg , userInfo);

            holder.mUserPortraitImg.setVisibility(View.VISIBLE);
            holder.mDeptPortraitImg.setVisibility(View.GONE);

            holder.mName.setText(userInfo.getOort_name());

            if (TextUtils.isEmpty(userInfo.getOort_jobname())){

                holder.mJobTv.setVisibility ( View.GONE );

            }else {
                holder.mJobTv.setText (userInfo.getOort_jobname() );
                holder.mJobTv.setVisibility ( View.VISIBLE );
            }

            holder.mCountTv.setVisibility(View.GONE);

            holder.mRightImg.setVisibility(View.GONE);
            //事件
            holder.itemView.setOnClickListener(v -> PersonDetailActivity.actionStart(mContext ,userInfo));


        }else if (sort instanceof Department){

            Department department = (Department) sort;
            ImageLoader.divisionImage(holder.mDeptPortraitImg , department);
            holder.mDeptPortraitImg.setVisibility(View.VISIBLE);
            holder.mUserPortraitImg.setVisibility(View.GONE);

            holder.mName.setText(department.getOort_dname());

            holder.mJobTv.setVisibility ( View.GONE );
            holder.mCountTv.setVisibility ( View.GONE );
            //事件
            holder.itemView.setOnClickListener(v -> {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(position);
                        }
                    });

            holder.mRightImg.setVisibility(View.VISIBLE);
        }

        if(sort instanceof UserInfo){

           if(mEVentType.equals(Constants.SELECT_CONTACTS)) {
               holder.mBox.setVisibility(View.VISIBLE);
               holder.mBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                       mItemClickListener.onCheckItemClick(b ? 1 : 0, (UserInfo) sort);
                   }
               });

               holder.mBox.setChecked(false);
               if (selectUsers != null) {
                   for (int i = 0; i < selectUsers.size(); i++) {
                       UserInfo info = selectUsers.get(i);
                       if (info.getOort_uuid().equals(((UserInfo) sort).getOort_uuid())) {
                           holder.mBox.setChecked(true);
                           break;
                       }
                   }
               }
           }

            if(mEVentType.equals(Constants.TAG_USER)) {
                holder.mTagDelTv.setVisibility(View.VISIBLE);
                holder.mTagDelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemClickListener.onItemTagDelClick(position, (UserInfo) sort);
                    }
                });
            }



        }else{
            holder.mBox.setVisibility(View.GONE);
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mJobTv, mCountTv, mTagDelTv;

        ImageView mDeptPortraitImg ,mUserPortraitImg, mRightImg;
        CheckBox mBox;


        public ViewHolder(View itemView) {
            super(itemView);
            mName =  itemView.findViewById( R.id.name);
            mDeptPortraitImg =  itemView.findViewById( R.id.dept_portrait_img);
            mUserPortraitImg =  itemView.findViewById(R.id.user_portrait_img);
            mJobTv = itemView.findViewById ( R.id.job_tv);
            mCountTv = itemView.findViewById ( R.id.count_tv );

            mRightImg =  itemView.findViewById( R.id.right_img);
            mBox = itemView.findViewById( R.id.checkbox);
            mTagDelTv = itemView.findViewById( R.id.tv_del);

        }
    }
    public void  setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(List<? extends Sort> list){
        this.mData = list;
        notifyDataSetChanged();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
        void onCheckItemClick(int statu,UserInfo user);
        void onItemTagDelClick(int position,UserInfo user);
    }


    public void  refreshUserCheckStatu(List users){
        selectUsers = users;
        notifyDataSetChanged();
    }

}
