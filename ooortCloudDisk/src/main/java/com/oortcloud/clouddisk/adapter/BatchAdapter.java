package com.oortcloud.clouddisk.adapter;

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
import com.oortcloud.clouddisk.activity.BatchActivity;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/24 04:06
 * @version： v1.0
 * @function： 批量操作 Adapter
 */
public class BatchAdapter  extends RecyclerView.Adapter<BatchAdapter.ViewHolder> {
    private Context mContext;
    private List<FileInfo> mData;
    private String mDir;
    private String mDirName;
    private BatchActivity mBatchActivity;
    //缓存批量操作数据
    private ArrayList<FileInfo> mFileIfoData = new ArrayList<>();
    //记录checkBox状态
    private Map<String, Boolean> mMap = new HashMap<>();
    public BatchAdapter(Context context , String dir , String dirName){
        this.mContext = context;
        this.mDir = dir;
        this.mDirName = dirName;
        if (mContext instanceof BatchActivity){
            mBatchActivity = (BatchActivity) mContext;
        }
        mMap.put(mDir + mDirName , true);
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

        viewHolder.moreImg.setVisibility(View.GONE);
        viewHolder.mCheckBox.setVisibility(View.VISIBLE);

        viewHolder.mCheckBox.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {

                if (!mFileIfoData.contains(fileInfo)) {
                    mFileIfoData.add(fileInfo);
                    mMap.put(fileInfo.getDir() + fileInfo.getName(), true);
                }

            } else {

                if (mFileIfoData.contains(fileInfo)) {
                    mFileIfoData.remove(fileInfo);
                }
                mMap.remove(fileInfo.getDir() + fileInfo.getName());
            }
        });

        if (mMap != null && mMap.containsKey(fileInfo.getDir() + fileInfo.getName())) {
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }


        viewHolder.itemView.setOnClickListener(v -> {

            if (fileInfo.getIs_dir() == 1){
                if (mBatchActivity != null){
                    mBatchActivity.startDir( fileInfo.getDir() , fileInfo.getName());
                }
            }else {
                //打开文件
            }
        });

    }

    public void  setData(List list){
        mData = list;
        notifyDataSetChanged();
    }

    public void  setDir(String  dir){
        this.mDir = dir;
    }

    public void  checkALl(){
       for (FileInfo fileInfo : mData ){
           if (!mFileIfoData.contains(fileInfo)){
               mMap.put(fileInfo.getDir() + fileInfo.getName() , true);
               mFileIfoData.add(fileInfo);
           }

       }
        notifyDataSetChanged();
    }
    public List  getFileInfoData(){
      return mFileIfoData;
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
        CheckBox mCheckBox;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            fileImg = itemView.findViewById(R.id.file_img);
            fileName = itemView.findViewById(R.id.file_name);
            fileTime = itemView.findViewById(R.id.file_time);
            fileSize = itemView.findViewById(R.id.file_size);
            moreImg = itemView.findViewById(R.id.more_img);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
    }

}
