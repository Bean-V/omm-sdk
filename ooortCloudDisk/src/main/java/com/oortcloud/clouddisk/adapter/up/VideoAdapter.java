package com.oortcloud.clouddisk.adapter.up;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.OpenFileUtil;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.manager.FileManager;
import com.oortcloud.clouddisk.utils.manager.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 16:20
 * @version： v1.0
 * @function： 本地视频适配器
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<Video> mData;
    private FileUploadActivity mUploadActivity;
    private FileManager mFileManager;

    //缓存上传文件

    public VideoAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
        mFileManager = FileManager.getInstance(mContext);
        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Video video = mData.get(position);

        Bitmap bm = mFileManager.getVideoThumbnail(video.getId());
        //Drawable drawable=new BitmapDrawable(bm);
        viewHolder.fileImg.setImageBitmap(bm);
        //Glide.with(mContext).load(drawable).into( viewHolder.fileImg);
        viewHolder.fileName.setText(video.getName());

        viewHolder.fileSize.setText(FileHelper.reckonFileSize(video.getSize()));

        viewHolder.mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

            if (mUploadActivity.mDBManager.isExistUp("file_path"  ,video.getPath()) == null) {
                mUploadActivity.upload(isChecked , new File(video.getPath()));
            }else {
                viewHolder.mCheckBox.setChecked(false);
                ToastUtils.showContent( "该文件已上传");
            }

        });

        if (mUploadActivity.mMap != null && mUploadActivity.mMap.containsKey(video.getPath())) {
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(video.getPath()));
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
