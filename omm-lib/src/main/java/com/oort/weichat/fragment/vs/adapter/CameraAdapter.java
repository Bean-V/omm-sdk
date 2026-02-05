package com.oort.weichat.fragment.vs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.oort.weichat.R;

import java.util.ArrayList;
import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    private List<CameraInfo> mCameraList = new ArrayList<>();

    public CameraAdapter(List<CameraInfo> cameraList) {
        mCameraList = cameraList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_camera;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCameraList == null || mCameraList.isEmpty()) {
            return;
        }
        
        CameraInfo camera = mCameraList.get(position);
        
        // 设置摄像机名称
        holder.tvCameraName.setText(camera.getCameraName());
        
        // 设置状态
        holder.tvCameraStatus.setText(camera.getStatus());
        
        // 设置右边状态信息
        holder.tvStatus.setText("空闲守候");
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onCameraClickListener != null) {
                onCameraClickListener.onCameraClick(camera);
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("CameraAdapter: getItemCount 返回 " + mCameraList.size() + " 条数据");
        return mCameraList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivCameraIcon;
        TextView tvCameraName;
        TextView tvCameraStatus;
        TextView tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ivCameraIcon = itemView.findViewById(R.id.iv_camera_icon);
            tvCameraName = itemView.findViewById(R.id.tv_camera_name);
            tvCameraStatus = itemView.findViewById(R.id.tv_camera_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }

    // 摄像机信息数据类
    public static class CameraInfo {
        private String id;
        private String cameraName;
        private String status;
        private String location;

        public CameraInfo(String id, String cameraName, String status, String location) {
            this.id = id;
            this.cameraName = cameraName;
            this.status = status;
            this.location = location;
        }

        // Getters
        public String getId() { return id; }
        public String getCameraName() { return cameraName; }
        public String getStatus() { return status; }
        public String getLocation() { return location; }

        // Setters
        public void setId(String id) { this.id = id; }
        public void setCameraName(String cameraName) { this.cameraName = cameraName; }
        public void setStatus(String status) { this.status = status; }
        public void setLocation(String location) { this.location = location; }
    }

    // 摄像机点击监听器
    public interface OnCameraClickListener {
        void onCameraClick(CameraInfo camera);
    }

    private OnCameraClickListener onCameraClickListener;

    public void setOnCameraClickListener(OnCameraClickListener listener) {
        this.onCameraClickListener = listener;
    }

    // 更新数据
    public void updateData(List<CameraInfo> newCameraList) {
        if (newCameraList != null) {
            mCameraList.clear();
            mCameraList.addAll(newCameraList);
            System.out.println("CameraAdapter: updateData 更新了 " + mCameraList.size() + " 条数据");
            notifyDataSetChanged();
        }
    }

    // 添加数据
    public void addData(CameraInfo camera) {
        mCameraList.add(camera);
        notifyItemInserted(mCameraList.size() - 1);
    }

    // 清空数据
    public void clearData() {
        mCameraList.clear();
        notifyDataSetChanged();
    }
} 