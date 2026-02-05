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
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.dialog.CommonDialog;
import com.oortcloud.clouddisk.fragment.transfer.DownloadFragment;
import com.oortcloud.clouddisk.observer.CountObserver;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.down.DownloadListenerImpl;
import com.oortcloud.clouddisk.transfer.down.DownloadManager;
import com.oortcloud.clouddisk.transfer.down.DownloadService;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;
import com.oortcloud.clouddisk.utils.manager.FileUtils;
import com.oortcloud.clouddisk.widget.ProgressView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/20 19:12
 * @version： v1.0
 * @function：
 */
public class DownTransferAdapter extends RecyclerView.Adapter<DownTransferAdapter.ViewHolder>{

    private Context mContext;
    private List<DownLoadInfo> mData;
    private DownloadFragment mDownloadFragment;

    private boolean flag_upload = true;
    //成功数
    private int mSuccessCount  = 0;
    private DownloadManager mDownloadManager;
    private DBManager mDBManager;

    public DownTransferAdapter(DownloadFragment fragment){
        this.mContext = fragment.getContext();
        this.mDownloadFragment = fragment;
        mDownloadManager = DownloadManager.getInstance();
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
        DownLoadInfo downLoadInfo = mData.get(position);
        //处理是否上传完成 / 正在上传
        if (position == getPositionForSection( downLoadInfo.getStatus())){
            if (downLoadInfo.getStatus() ==  Status.SUCCESS){
                holder.status.setText("下载完成("+ mSuccessCount +")");
                holder.statusLL.setVisibility(View.VISIBLE);
            }else {
                if (flag_upload){
                    holder.status.setText("正在下载("+ (mData.size() - mSuccessCount) +")");
                    holder.statusLL.setVisibility(View.VISIBLE);
                    flag_upload = false;

                }else {
                    holder.statusLL.setVisibility(View.GONE);
                }

            }
        }else {
            holder.statusLL.setVisibility(View.GONE);
        }

        holder.fileName.setText(downLoadInfo.getFile_name());

        //上传完成
        if (downLoadInfo.getStatus() == Status.SUCCESS){
            holder.progressView.setVisibility(View.GONE);

            Date date = new Date(downLoadInfo.getCtime() *1000L);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
            holder.transfersType.setText(simpleDateFormat.format(date));

            holder.fileSize.setText(FileHelper.reckonFileSize(downLoadInfo.getContentLength()));
            holder.fileImg.setVisibility(View.VISIBLE);
            ImgHelper.setImageResource(new File(downLoadInfo.getFile_path()) , holder.fileImg);
            holder.itemView.setOnClickListener(v -> {
                FileHelper.getInstance(mContext).openFile(new FileInfo(downLoadInfo.getDir() ,downLoadInfo.getFile_name() ));
            });

        }else {

            holder.fileImg.setVisibility(View.GONE);
            holder.progressView.setCurrentStep(downLoadInfo.getProgress());
            DownloadListenerImpl listener =  mDownloadManager.getUploadListener(downLoadInfo.getFile_path());
            //正在上传状态处理  如果异常 可自动上传
            if (downLoadInfo.getStatus() == Status.PROGRESS){
                holder.progressView.setStatus(true);
                if (listener != null){
                    holder.transfersType.setText("正在下载");
                    listener.holder = holder;
                    listener.adapter = this;

                }else {
                    startDownload(downLoadInfo , holder , listener);
                }


            }else {
                //其它暂停状态
                holder.progressView.setStatus(false);
                if (downLoadInfo.getStatus() == Status.PAUSED){
                    holder.transfersType.setText("暂停");
                }else {
                    holder.transfersType.setText("下载失败");
                }

            }

            DownloadListenerImpl finalListener = listener;

            holder.progressView.setOnClickListener(v -> {

                progressView( downLoadInfo ,holder , finalListener);

            });
        }

        holder.mDelete.setOnClickListener(v -> {
            new CommonDialog(mContext).setTitle("删除记录").setContent("确定删除该条记录?")
                    .setConfirmClick(() ->{
                        if (downLoadInfo.getStatus() != Status.SUCCESS){

                            DownloadListenerImpl listener =  mDownloadManager.getUploadListener(downLoadInfo.getFile_path());
                            if (listener != null){
                                mDownloadManager.stopDownload( listener.mDownLoadInfo);
                            }else {
                                mDownloadManager.stopDownload( downLoadInfo);
                            }

                            DownloadService.mListeners.remove(downLoadInfo.getFile_path());
                            File file = new File(downLoadInfo.getFile_path());
                            if (file.exists()){
                                file.delete();
                            }

                            //删除下载的子线程信息
                            mDBManager.deleteThread("file_path" , downLoadInfo.getFile_path());
                        }
                        mDBManager.deleteDown("file_path" , downLoadInfo.getFile_path());
                        mDownloadFragment.initData();
                        CountObserver.getInstance().sendNotify();


                    }).show();
        });

    }
    private void  progressView(DownLoadInfo downLoadInfo , ViewHolder holder , DownloadListenerImpl listener){
        if (listener != null && listener.mDownLoadInfo != null){
            downLoadInfo = listener.mDownLoadInfo;
        }
        if (downLoadInfo.getStatus() == Status.PROGRESS){
            holder.progressView.setStatus(false);
            holder.transfersType.setText("暂停");
            downLoadInfo.setStatus(Status.PAUSED);
            mDownloadManager.stopDownload(downLoadInfo);

        }else if (downLoadInfo.getStatus() == Status.PAUSED || downLoadInfo.getStatus() == Status.FAIL){
            holder.progressView.setStatus(true);

            startDownload(downLoadInfo , holder , listener);

        }
    }
    private void  startDownload(DownLoadInfo downLoadInfo , ViewHolder holder ,  DownloadListenerImpl listener){
        holder.transfersType.setText("正在下载");

        if (listener == null){
            listener  = new DownloadListenerImpl(downLoadInfo);
            listener.addAllThreadInfo(mDBManager.queryThread("file_path" , downLoadInfo.getFile_path()));

        }
        listener.holder = holder;
        listener.adapter = this;
        downLoadInfo.setStatus(Status.PROGRESS);
        mDownloadManager.startDownload(downLoadInfo , listener);
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
