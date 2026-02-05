package com.oortcloud.contacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Constants;
import com.oortcloud.contacts.bean.Data;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.http.HttpConstants;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.HttpResult;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.observer.DataHandle;
import com.oortcloud.contacts.observer.Observer;
import com.oortcloud.contacts.utils.ImageLoader;
import com.oortcloud.contacts.utils.SortComparator;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @filename:
 * @author: zzj/@date: 2021/4/21 20:47
 * @version： v1.0
 * @function：
 */
public class SelectDepartAndUserAdapter extends RecyclerView.Adapter<SelectDepartAndUserAdapter.ViewHolder> implements Observer {
    private LayoutInflater mInflater;
    private List<Sort> mData;
    private Context mContext;

    public SelectDepartAndUserAdapter(Context context, List<Sort> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;

        this.mContext = context;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_select_depanduser_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sort sort = mData.get(position);
        String uuid = "";
        if (sort instanceof UserInfo) {

            UserInfo userInfo = (UserInfo) sort;
            ;
            uuid = userInfo.getOort_uuid();
            ImageLoader.loadImage(holder.mPortrait, HttpConstants.PHOTO_URL + userInfo.getOort_uuid());

            holder.nameView.setText(userInfo.getOort_name());
            holder.mDivision.setVisibility(View.GONE);
            holder.mPortrait.setVisibility(View.VISIBLE);

            holder.mCheckBoxView.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if (isChecked) {
                    if (!DataHandle.getInstance().getUserData().contains(userInfo)) {
                        DataHandle.getInstance().addUser(userInfo);
                    }
                } else {
                    if (DataHandle.getInstance().getUserData().contains(userInfo)) {
                        DataHandle.getInstance().removeUser(userInfo);
                    }


                }

            });
        } else if (sort instanceof Department) {

            Department department = (Department) sort;
            uuid = department.getOort_dcode();
            holder.nameView.setText(department.getOort_dname());
            holder.itemView.setOnClickListener(v -> {
                HttpResult.getDeptAndUserTree(department.getOort_dcode(), 1, Constants.SELECT_CONTACTS);
            });
            holder.mDivision.setImageResource(R.mipmap.icon_division01);
            holder.mPortrait.setVisibility(View.GONE);
            holder.mDivision.setVisibility(View.VISIBLE);
            holder.mCheckBoxView.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if (isChecked) {
                    if (!DataHandle.getInstance().getDepartData().contains(department)) {
                        DataHandle.getInstance().addDepartment(department);
                        getDepartData(department.getOort_dcode() , 1 , Constants.SELECT_CONTACTS_ADD);
                    }
                } else {
                    if (DataHandle.getInstance().getDepartData().contains(department)) {
                        DataHandle.getInstance().removeDepartment(department);
                        getDepartData(department.getOort_dcode() , 1 , Constants.SELECT_CONTACTS_REMOVE);
                    }


                }

            });
        }


        if (DataHandle.getInstance().getMap() != null && DataHandle.getInstance().getMap().containsKey(uuid)) {
            holder.mCheckBoxView.setChecked(true);
        } else {
            holder.mCheckBoxView.setChecked(false);
        }

    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView jobView;
        ImageView mDivision;
        ImageView mPortrait;
        CheckBox mCheckBoxView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name_tv);
            jobView = itemView.findViewById(R.id.job_tv);
            mDivision = itemView.findViewById(R.id.division_img);
            mPortrait = itemView.findViewById(R.id.img_head_portrait);
            mCheckBoxView = itemView.findViewById(R.id.checkbox);

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateList(List<Sort> list) {
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

    @Override
    public void notifyMsg() {
        notifyDataSetChanged();
//        if (mData.contains(DataHandle.getInstance().getUser())) {
//            int index = mData.indexOf(DataHandle.getInstance().getUser());
//
//            mViewHolder.get(index).mCheckBoxView.setChecked(false);
//        }

    }

    private void  getDepartData(String depCode , int showUser ,String type){
        HttpRequestCenter.post(depCode, showUser ).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {

                Result<Data> result = new Gson().fromJson(s,  new TypeToken<Result<Data>>() {}.getType());

                if (result.isOk()) {
                    Data  data =  result.getData();
                    if (data != null){
                        List<UserInfo> userInfoData =  data.getUser();
                        if (userInfoData != null  && userInfoData.size() > 0){
                            Collections.sort(userInfoData, new SortComparator());
                            if (Constants.SELECT_CONTACTS_ADD.equals(type)){
                                DataHandle.getInstance().addUser(userInfoData);
                            }else if (Constants.SELECT_CONTACTS_REMOVE.equals(type)){
                                DataHandle.getInstance().removeUser(userInfoData);
                            }

                        }

                    }

                }
            }

        });
    }
}
