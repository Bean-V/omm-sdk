package com.oortcloud.clouddisk.adapter.up;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.fragment.up.OtherFragment;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.OpenFileUtil;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/30 17:46
 * @version： v1.0
 * @function： 获取sd卡文件目录
 */
public class FileDirAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<File> mData;
    private FileUploadActivity mUploadActivity;
    private OtherFragment mOtherFragment;

    public FileDirAdapter(Context context, OtherFragment otherFragment) {
        this.mContext = context;
        this.mOtherFragment = otherFragment;

        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_dir_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        File file = mData.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        ImgHelper.setImageResource(file, viewHolder.fileImg);

        viewHolder.fileName.setText(file.getName());


        if (file.isDirectory()) {
            viewHolder.mCheckBox.setVisibility(View.GONE);
            viewHolder.fileSize.setVisibility(View.GONE);
        } else {
            viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            viewHolder.fileSize.setVisibility(View.VISIBLE);

            viewHolder.fileSize.setText(FileHelper.reckonFileSize(file.length()));

        }

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
            if (file.isDirectory()) {
                if (mOtherFragment != null) {
                    mOtherFragment.mPath.add(file);
                    mOtherFragment.initData();
                }
            } else {
                //打开文件
                mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(file.getPath()));
            }
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
