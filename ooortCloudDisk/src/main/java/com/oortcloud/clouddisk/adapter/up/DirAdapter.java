package com.oortcloud.clouddisk.adapter.up;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.up.FileUploadActivity;
import com.oortcloud.clouddisk.fragment.up.OtherFragment;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/27 10:01
 * @version： v1.0
 * @function： 适配文件目录
 */
public class DirAdapter  extends RecyclerView.Adapter <DirAdapter.ViewHolder>{
    private Context mContext;
    private List<File> mData;
    private FileUploadActivity mUploadActivity;
    private OtherFragment mOtherFragment;
    //缓存上传文件
    public DirAdapter(Context context, OtherFragment otherFragment) {
        this.mContext = context;
        this.mOtherFragment = otherFragment;
        if (mContext instanceof FileUploadActivity) {
            mUploadActivity = (FileUploadActivity) mContext;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dir_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        File file = mData.get(position);
        if (position == 0){
            viewHolder.dirName.setText("SD卡");
        }else {
            viewHolder.dirName.setText(file.getName());
        }

        if (position == mData.size()-1){
            viewHolder.dirCut.setVisibility(View.GONE);
            viewHolder.dirName.setTextColor(mContext.getResources().getColor(R.color.color_333));

        }else {
            viewHolder.dirCut.setVisibility(View.VISIBLE);
            viewHolder.dirName.setTextColor(mContext.getResources().getColor(R.color.color_999));
            viewHolder.dirCut.setTextColor(mContext.getResources().getColor(R.color.color_999));

            viewHolder.dirName.setOnClickListener(v -> {
                if (mOtherFragment != null){

                    for (int i = mOtherFragment.mPath.size() -1 ; i > position ; i--){
                        mOtherFragment.mPath.remove(i);
                    }

                    mOtherFragment.initData();
                }
            });
        }


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

        TextView dirName;
        TextView dirCut;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dirName = itemView.findViewById(R.id.dir_name);
            dirCut = itemView.findViewById(R.id.dir_cut);
        }
    }

}
