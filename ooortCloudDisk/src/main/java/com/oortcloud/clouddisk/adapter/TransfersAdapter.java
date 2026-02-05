package com.oortcloud.clouddisk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.dialog.CommonDialog;
import com.oortcloud.clouddisk.fragment.transfer.UploadFragment;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadListenerImpl;
import com.oortcloud.clouddisk.transfer.upload.UploadManager;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;
import com.oortcloud.clouddisk.widget.ProgressView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/11 17:51
 * @version： v1.0
 * @function： 适配上传/下载列表 Adapter
 */
public class TransfersAdapter extends RecyclerView.Adapter<TransfersAdapter.ViewHolder> {
    private Context mContext;
    private List<UploadInfo> mData;
    private UploadFragment mUploadFragment;
    private boolean flag_upload = true;
    //成功数
    private int mSuccessCount  = 0;
    private UploadManager mUploadManager;
    private DBManager mDBManager;
    public TransfersAdapter(UploadFragment fragment){
        this.mContext = fragment.getContext();
        this.mUploadFragment = fragment;
        mUploadManager = UploadManager.getInstance();
        mDBManager = DBManager.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transfers, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UploadInfo uploadInfo = mData.get(position);
        //处理是否上传完成 / 正在上传
       if (position == getPositionForSection( uploadInfo.getStatus())){
            if (uploadInfo.getStatus() ==  Status.SUCCESS){
                holder.status.setText("上传完成("+ mSuccessCount +")");
                holder.statusLL.setVisibility(View.VISIBLE);
            }else {
                if (flag_upload){
                    holder.status.setText("正在上传("+ (mData.size() - mSuccessCount) +")");
                    holder.statusLL.setVisibility(View.VISIBLE);
                    flag_upload = false;

                }else {
                    holder.statusLL.setVisibility(View.GONE);
                }

            }
       }else {
           holder.status.setVisibility(View.GONE);
       }

        holder.fileName.setText(uploadInfo.getFile_name());

        //上传完成
       if (uploadInfo.getStatus() == Status.SUCCESS){
           holder.progressView.setVisibility(View.GONE);

           Date date = new Date(uploadInfo.getCtime() *1000L);
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
           holder.transfersType.setText(simpleDateFormat.format(date));

           holder.fileSize.setText(FileHelper.reckonFileSize(uploadInfo.getContentLength()));
           holder.fileImg.setVisibility(View.VISIBLE);
           ImgHelper.setImageResource(new File(uploadInfo.getFile_path()) , holder.fileImg);

//           holder.itemView.setOnClickListener(v -> {
//               FileHelper.getInstance(mContext).openFile(new FileInfo(uploadInfo.getDir() ,uploadInfo.getFile_name() ));
//           });

       }else {

           holder.fileImg.setVisibility(View.GONE);
           holder.progressView.setCurrentStep(uploadInfo.getProgress());
           UploadListenerImpl listener =  mUploadManager.getUploadListener(uploadInfo.getFile_path());
           //正在上传状态处理  如果异常 可以自动上传
           if (uploadInfo.getStatus() == Status.PROGRESS){
               holder.progressView.setStatus(true);
                if (listener == null){
                    listener = new UploadListenerImpl(uploadInfo);

                    if (mUploadManager.isUpload(uploadInfo.getFile_path())){

                        mUploadManager.startUpload(uploadInfo , listener , new File(uploadInfo.getFile_path()));

                    }
                }
                listener.holder = holder;
                listener.adapter = this;
                holder.transfersType.setText("正在上传");
                holder.fileSize.setText(FileHelper.reckonFileSize(uploadInfo.getByteCount()) +"/" + FileHelper.reckonFileSize(uploadInfo.getContentLength()));

           }else {
                //其它暂停状态
               holder.progressView.setStatus(false);
               if (uploadInfo.getStatus() == Status.PAUSED){
                   holder.transfersType.setText("暂停");
               }else {
                   holder.transfersType.setText("上传失败");
               }

           }

           UploadListenerImpl finalListener = listener;

           holder.progressView.setOnClickListener(v -> {

               progressView(uploadInfo ,holder , finalListener);

           });
       }

        holder.mDelete.setOnClickListener(v -> {
            new CommonDialog(mContext).setTitle("删除记录").setContent("确定删除该条记录?")
                    .setConfirmClick(() ->{
                        if (uploadInfo.getStatus() != Status.SUCCESS){
                            if (!mUploadManager.isUpload(uploadInfo.getFile_path())){
                                mUploadManager.stopUpload(uploadInfo);
                                CountObserver.getInstance().sendNotify();
                            }
                            mDBManager.deleteUp("file_path" , uploadInfo.getFile_path());
                        }else {
                            uploadInfo.setHide(true);
                            mDBManager.update(uploadInfo , "file_path" , uploadInfo.getFile_path());
                        }
                        mUploadFragment.initData();
                    }).show();
        });

    }
    private void  progressView(UploadInfo uploadInfo , ViewHolder holder , UploadListenerImpl listener){
        if (listener != null && listener.getUploadInfo() != null){
            uploadInfo = listener.getUploadInfo();
        }
        if (uploadInfo.getStatus() == Status.PROGRESS){
            holder.progressView.setStatus(false);
            holder.transfersType.setText("暂停");
            uploadInfo.setStatus(Status.PAUSED);
            mUploadManager.stopUpload(uploadInfo);

        }else if (uploadInfo.getStatus() == Status.PAUSED || uploadInfo.getStatus() == Status.FAIL){
            holder.progressView.setStatus(true);

            if (listener == null){
                listener  = new UploadListenerImpl(uploadInfo);

            }
            uploadInfo.setStatus(Status.PROGRESS);
            listener.holder = holder;
            listener.adapter = this;
            holder.transfersType.setText("正在上传");
            mUploadManager.startUpload(uploadInfo , listener, new File(uploadInfo.getFile_path()));

        }
    }

    public void  setData(List list){

        flag_upload = true;
        mData = list;
        Collections.sort(mData);
        getSuccessCount();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return mData == null ? 0 : mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
         TextView status;
         LinearLayout statusLL;
        public ProgressView progressView;
        ImageView fileImg;
        TextView fileName;
        public TextView transfersType;
        public TextView fileSize;
        ImageView mDelete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status_tv);
            statusLL = itemView.findViewById(R.id.status_ll);
            progressView = itemView.findViewById(R.id.progress_view);
            fileImg = itemView.findViewById(R.id.file_img);
            fileName = itemView.findViewById(R.id.file_name);
            transfersType = itemView.findViewById(R.id.transfers_type);
            fileSize = itemView.findViewById(R.id.file_size);
            mDelete = itemView.findViewById(R.id.delete_img);
        }
    }

    /**
     * 根据分类的值获取其第一次出现的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            int sortStr = mData.get(i).getStatus();
            if (sortStr == section) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 上传完成
     */
    public void getSuccessCount() {
        mSuccessCount = 0 ;
        for (int i = 0; i < getItemCount(); i++) {
            if (mData.get(i).getStatus() ==  Status.SUCCESS){
                mSuccessCount++;
            }
        }
    }


}
