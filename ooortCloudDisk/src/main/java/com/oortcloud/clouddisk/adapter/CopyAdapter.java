package com.oortcloud.clouddisk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.CopyActivity;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/23 17:51
 * @version： v1.0
 * @function：  复制到指定目录 Adapter
 */
public class CopyAdapter extends RecyclerView.Adapter<CopyAdapter.ViewHolder> {

    private Context mContext;
    private List<FileInfo> mData;
    private String mDir;
    private CopyActivity mCopyActivity;
    public CopyAdapter(Context context , String dir){
        this.mContext = context;
        this.mDir = dir;
        if (mContext instanceof CopyActivity){
            mCopyActivity = (CopyActivity) mContext;
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_layout, viewGroup , false);
      ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FileInfo fileInfo = mData.get(position);
        fileInfo.setDir(mDir);

        String fileName = fileInfo.getName();
        if (fileInfo.getIs_dir() == 0){
            viewHolder.fileSize.setText((FileHelper.reckonFileSize(fileInfo.getSize())));
        }

        ImgHelper.setImageResource(fileInfo  , viewHolder.fileImg);
        viewHolder.fileName.setText(fileName);
        Date date = new Date(fileInfo.getCtime() *1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss

        viewHolder.fileTime.setText(simpleDateFormat.format(date));

//        ColorMatrix matrix = new ColorMatrix();
//        matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
//        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
//        viewHolder.fileImg.setColorFilter(filter);

        if (fileInfo.getIs_dir() == 1){
            viewHolder.itemView.setAlpha(1);
            viewHolder.moreImg.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(v -> {
                if (mCopyActivity != null){
                    mCopyActivity.startDir( fileInfo.getDir() , fileInfo.getName());
                }
            });
        }else {
            //打开文件
            viewHolder.itemView.setAlpha(0.5f);
            viewHolder.moreImg.setVisibility(View.VISIBLE);
        }


    }

    public void  setData(List list){
        mData = list;
        notifyDataSetChanged();
    }
    public void  setDir(String  dir){
        this.mDir = dir;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View itemView ;
        ImageView fileImg;
        TextView fileName;
        TextView fileTime;
        TextView fileSize;
        ImageView moreImg;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            fileImg = itemView.findViewById(R.id.file_img);
            fileName = itemView.findViewById(R.id.file_name);
            fileTime = itemView.findViewById(R.id.file_time);
            fileSize = itemView.findViewById(R.id.file_size);
            moreImg = itemView.findViewById(R.id.more_img);
        }
    }

}
