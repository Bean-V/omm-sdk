package com.oort.weichat.fragment.vs.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.R;
import com.oort.weichat.fragment.vs.ControlFragment;
import com.oort.weichat.fragment.vs.bean.DeviceList;
import com.oort.weichat.ui.base.CoreManager;
import com.oortcloud.contacts.utils.ImageLoader;
import com.oortcloud.contacts.utils.omm.AvatarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 执法记录仪适配器
 */
public class LERecorderAdapter extends RecyclerView.Adapter<LERecorderAdapter.ViewHolder> {
    private List<DeviceList.DataBean.DeviceBean> mDeviceList = new ArrayList<>();
    private final ControlFragment mFragment;
    private  ActivityResultLauncher<Intent> launcher;
    private  CoreManager mCoreManager;
    public LERecorderAdapter(ControlFragment fragment, List<DeviceList.DataBean.DeviceBean> groupList) {
        mFragment = fragment;
        mDeviceList = groupList;
    }

    public void setCoreManager(CoreManager coreManager){
        mCoreManager = coreManager;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_device_, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDeviceList == null || mDeviceList.isEmpty()) {
            return;
        }

        DeviceList.DataBean.DeviceBean device = mDeviceList.get(position);
        // 设置设备名称
        String deviceName = device.getDept_name();
        deviceName = deviceName.isEmpty()?"执法记录仪-"+ (position+1) : deviceName;
        holder.tvGroupName.setText(deviceName);
        loaderImage(holder.ivGroupAvatar,   device);
        // 设置群组状态
//        holder.tvGroupStatus.setText(group.getGroupStatus());

//        // 设置状态信息 1：在线 2：离线
        if (device.getStatus() == 1){
            holder.tvGroupStatus.setText("在线");
        }
        else if (device.getStatus() == 2) {
            String disableReason = device.getDisable_reason();
            disableReason = disableReason.isEmpty()?"":"/"+ disableReason;
            holder.tvGroupStatus.setText("离线" + disableReason);
        }

        
        // 设置百分比
//        holder.tvPercentage.setText(group.getPercentage());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivGroupAvatar;
        TextView tvGroupName;
        TextView tvGroupStatus;
        TextView tvStatus;
        TextView tvPercentage;

        ViewHolder(View itemView) {
            super(itemView);
            ivGroupAvatar = itemView.findViewById(R.id.iv_group_avatar);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvGroupStatus = itemView.findViewById(R.id.tv_group_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
        }
    }



    // 群组点击监听器
    public interface OnGroupClickListener {
        void onGroupClick(DeviceList.DataBean.DeviceBean group);
    }

    private OnGroupClickListener onGroupClickListener;

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.onGroupClickListener = listener;
    }

    // 更新数据
    public void updateData(List<DeviceList.DataBean.DeviceBean> deviceList) {
        if (deviceList != null) {
            mDeviceList.clear();
            mDeviceList.addAll(deviceList);
            notifyDataSetChanged();
        }
    }

    // 添加数据
    public void addData(DeviceList.DataBean.DeviceBean deviceBean) {
        mDeviceList.add(deviceBean);
        notifyItemInserted(mDeviceList.size() - 1);
    }

    // 清空数据
    public void clearData() {
        mDeviceList.clear();
        notifyDataSetChanged();
    }
    public  static  void  loaderImage(ImageView imageView , DeviceList.DataBean.DeviceBean device){
        if (device != null){
            String path = device.getPhoto();
            if (TextUtils.isEmpty(path)){
//
                AvatarHelper.displayAvatar(imageView.getContext(),device.getDept_name(),device.getDept_name(),imageView);
                return;

            }
            ImageLoader.loadImage(imageView , path);

        }
    }
}