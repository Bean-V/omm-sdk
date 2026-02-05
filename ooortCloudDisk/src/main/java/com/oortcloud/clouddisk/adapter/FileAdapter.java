package com.oortcloud.clouddisk.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.BatchActivity;
import com.oortcloud.clouddisk.activity.DetailsActivity;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.dialog.FileSettingDialog;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.down.DownloadListenerImpl;
import com.oortcloud.clouddisk.transfer.down.DownloadManager;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadListenerImpl;
import com.oortcloud.clouddisk.transfer.upload.UploadManager;
import com.oortcloud.clouddisk.utils.helper.FileHelper;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @filename:
 * @author: zzj/@date: 2020/12/15 19:35
 * @version： v1.0
 * @function： 文件适配器
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Context mContext;
    private List<FileInfo> mData;
    private String mDir;
    //上传管理
    private UploadManager mUploadManager;
    //下载管理
    private DownloadManager mDownloadManager;
    //数据库管理
    private DBManager mDBManager;

    private DetailsActivity mDetailsActivity;
    public FileAdapter(Context context, String dir) {
        this.mContext = context;
        this.mDir = dir;
        if (mContext instanceof  DetailsActivity){
            mDetailsActivity = (DetailsActivity) mContext;
        }
        mDBManager = DBManager.getInstance();
        mUploadManager = UploadManager.getInstance();
        mDownloadManager = DownloadManager.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FileInfo fileInfo = mData.get(position);

        viewHolder.fileName.setText(fileInfo.getName());
        ImgHelper.setImageResource(fileInfo, viewHolder.fileImg);

        if (fileInfo.getType() == 0) {
            fileInfo.setDir(mDir);
            if (fileInfo.getIs_dir() == 0) {
                viewHolder.fileSize.setText(FileHelper.reckonFileSize(fileInfo.getSize()));
            }
            Date date = new Date(fileInfo.getCtime() * 1000L);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
            viewHolder.fileTime.setText(simpleDateFormat.format(date));

            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.moreImg.setVisibility(View.VISIBLE);
            viewHolder.moreImg.setOnClickListener(v -> {
                new FileSettingDialog(mContext, fileInfo).show();
            });

            viewHolder.itemView.setOnClickListener(v -> {
                if (fileInfo.getIs_dir() == 1) {
                    if (mDetailsActivity != null){
                        mDetailsActivity.startDir( fileInfo.getDir() , fileInfo.getName());
                    }else {
                        DetailsActivity.actionStart(mContext, fileInfo.getDir(), fileInfo.getName());
                    }

                } else {
                    //处理打开文件
                    FileHelper.getInstance(mContext).openFile(fileInfo);
                }
            });
            viewHolder.itemView.setOnLongClickListener(v -> {

                BatchActivity.actionStart(mContext, fileInfo.getDir(), "", fileInfo.getName());
                return true;
            });

        } else {
            //上传 或者 下载状态
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.moreImg.setVisibility(View.GONE);
            viewHolder.progressBar.setProgress(fileInfo.getProgress());
            Log.v("msg" ,fileInfo.getStatus()+"-----" );
            if (fileInfo.getStatus() == Status.PROGRESS) {
                if (fileInfo.getType() == Status.TYPE_UPLOAD) {
                    UploadListenerImpl listener = mUploadManager.getUploadListener(fileInfo.getFile_path());
                    List<UploadInfo> list = mDBManager.queryUp("file_path", fileInfo.getFile_path());

                    if (list != null && list.size() > 0) {
                        UploadInfo uploadInfo = list.get(0);
                        if (listener == null) {
                            listener = new UploadListenerImpl(uploadInfo);

                            if (mUploadManager.isUpload(uploadInfo.getFile_path())) {

                                mUploadManager.startUpload(uploadInfo, listener, new File(uploadInfo.getFile_path()));

                            }
                        }
                        viewHolder.fileSize.setText(FileHelper.reckonFileSize(uploadInfo.getByteCount()) +"/" + FileHelper.reckonFileSize(uploadInfo.getContentLength()));

                    }
                    listener.fileHolder = viewHolder;
                    viewHolder.fileTime.setText("正在上传");
                } else if (fileInfo.getType() == Status.TYPE_DOWN) {

                    DownloadListenerImpl listener = mDownloadManager.getUploadListener(fileInfo.getFile_path());
                    List<DownLoadInfo> list = mDBManager.queryDown("file_path", fileInfo.getFile_path());

                    if (list != null && list.size() > 0) {
                        DownLoadInfo downLoadInfo = list.get(0);
                        if (listener == null) {

                            listener = new DownloadListenerImpl(downLoadInfo);
                            listener.addAllThreadInfo(mDBManager.queryThread("file_path", downLoadInfo.getFile_path()));
                            mDownloadManager.startDownload(downLoadInfo, listener);

                        }

                    }
                    listener.fileHolder = viewHolder;
                    viewHolder.fileTime.setText("正在下载");
                }

            } else if (fileInfo.getStatus() == Status.PAUSED) {

                viewHolder.fileTime.setText("暂停");

            } else if (fileInfo.getStatus() == Status.FAIL) {

                if (fileInfo.getType() == Status.TYPE_UPLOAD) {
                    viewHolder.fileTime.setText("上传失败");
                } else if (fileInfo.getType() == Status.TYPE_DOWN) {
                    viewHolder.fileTime.setText("下载失败");
                }

            }

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

    public void  setDir(String  dir){
        this.mDir = dir;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView fileImg;
        TextView fileName;
        public TextView fileTime;
        public TextView fileSize;
        ImageView moreImg;
        public ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            fileImg = itemView.findViewById(R.id.file_img);
            fileName = itemView.findViewById(R.id.file_name);
            fileTime = itemView.findViewById(R.id.file_time);
            fileSize = itemView.findViewById(R.id.file_size);
            moreImg = itemView.findViewById(R.id.more_img);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}
