package com.oortcloud.clouddisk.adapter.up;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.OpenFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/26 18:31
 * @version： v1.0
 * @function： 图片适配器
 */
public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mData;
    private FileUploadActivity mUploadActivity;

    public PicAdapter(Context context , List data){
        this.mContext = context;
        this.mData = data;
        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pic_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = mData.get(position);

        ViewHolder viewHolder = holder;

        Glide.with(mContext).load(path).into((viewHolder.image));
        viewHolder.mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {

            if (mUploadActivity.mDBManager.isExistUp("file_path"  , path) == null) {
                mUploadActivity.upload(isChecked , new File(path));
            }else {
                viewHolder.mCheckBox.setChecked(false);
                ToastUtils.showContent( "该文件已上传");
            }
        });

        if (mUploadActivity.mMap != null && mUploadActivity.mMap.containsKey(path)) {
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }
        viewHolder.itemView.setOnClickListener(v -> {

            mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(path));
        });
    }

    public void  setData(List list){
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View view ;
        ImageView image;
        CheckBox mCheckBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            image = itemView.findViewById(R.id.image);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

}
