package com.oortcloud.clouddisk.adapter.up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.utils.manager.FileManager;
import com.oortcloud.clouddisk.utils.manager.ImgFolderBean;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 16:50
 * @version： v1.0
 * @function： 图片文件夹适配器
 */
public class PicDirAdapter extends RecyclerView.Adapter<PicDirAdapter.ViewHolder>{
    private Context mContext;
    private List<ImgFolderBean> mData;
    private FileUploadActivity mUploadActivity;


    public PicDirAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pic_dir_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ImgFolderBean imgFolderBean = mData.get(position);

        Glide.with(mContext).load(imgFolderBean.getFistImgPath()).into( viewHolder.fileImg);

        viewHolder.fileName.setText(imgFolderBean.getName());

        viewHolder.fileSize.setText(imgFolderBean.getCount()+"张");

//        viewHolder.mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
//            mUploadActivity.upload(isChecked , new File(imgFolderBean.getDir()));
//
//        });
//
//        if (mUploadActivity.mMap != null && mUploadActivity.mMap.containsKey(imgFolderBean.getDir())) {
//            viewHolder.mCheckBox.setChecked(true);
//        } else {
//            viewHolder.mCheckBox.setChecked(false);
//        }

        viewHolder.itemView.setOnClickListener(v -> {
//            PicUploadActivity.actionStart(mContext , imgFolderBean.getDir());
            mUploadActivity.actionStart(imgFolderBean.getDir());

        });
    }

    public void setData(List list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView fileImg;
        TextView fileName;
        TextView fileSize;
        CheckBox mCheckBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            fileImg = itemView.findViewById(R.id.file_img);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public void uploadFile(String dir) {
//        TransferHelper.uploadFile(dir, mFileList);
//        map.clear();
//        mFileList.clear();
//        notifyDataSetChanged();
    }
}
