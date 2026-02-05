package com.oortcloud.clouddisk.adapter.up;

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

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.OpenFileUtil;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;
import com.oortcloud.clouddisk.utils.manager.FileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 22:14
 * @version： v1.0
 * @function： 文件适配器
 */
public class DOCAdapter extends RecyclerView.Adapter<DOCAdapter.ViewHolder>  {
    private Context mContext;
    private List<FileBean> mData;
    private FileUploadActivity mUploadActivity;

    public DOCAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;

        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_dir_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        File file = new File(mData.get(position).getPath()) ;

        ImgHelper.setImageResource(file, viewHolder.fileImg);

        viewHolder.fileName.setText(file.getName());

        viewHolder.fileSize.setText(FileHelper.reckonFileSize(file.length()));

        viewHolder.mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

          if (mUploadActivity.mDBManager.isExistUp("file_path"  , file.getPath()) == null) {
              mUploadActivity.upload(isChecked , file);
          }else {
              viewHolder.mCheckBox.setChecked(false);
              ToastUtils.showContent( "该文件已上传");
          }

        });

        if (mUploadActivity.mMap != null && mUploadActivity.mMap.containsKey(file.getPath())) {
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            //打开文件
            mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(file.getPath()));
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

}
