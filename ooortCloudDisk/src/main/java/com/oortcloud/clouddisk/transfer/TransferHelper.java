package com.oortcloud.clouddisk.transfer;

import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.http.HttpConstants;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.down.DownloadListenerImpl;
import com.oortcloud.clouddisk.transfer.down.DownloadManager;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadListenerImpl;
import com.oortcloud.clouddisk.transfer.upload.UploadManager;
import com.oortcloud.clouddisk.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/25 19:07
 * @version： v1.0
 * @function： 上传/下载 封装类
 */
public class TransferHelper {
   private final static DBManager dbManager = DBManager.getInstance();
    private final static UploadManager uploadManager = UploadManager.getInstance();
    private final static DownloadManager downloadManager = DownloadManager.getInstance();


    public static void startDownload(FileInfo fileInfo){
        List<FileInfo> fileInfoData = new ArrayList<>();
        fileInfoData.add(fileInfo);
        startDownload(fileInfoData);
    }
    public static void startDownload(List<FileInfo> data) {
        if (data != null){
            for (FileInfo fileInfo : data){
                //加上传文件夹路径 防止不同目录文件重复问题
                String path = HttpConstants.USER_PATH +  fileInfo.getDir()  + fileInfo.getName();
                //url
                String url = HttpConstants.GATEWAY_URL + HttpConstants.DOWN_FILE + "?accessToken=" + BaseApplication.TOKEN +
                        "&dir=" + fileInfo.getDir() + "&name=" + fileInfo.getName();
                if (downloadManager.getUploadListener(path) == null) {

                    DownLoadInfo downLoadInfo = dbManager.isExistDown("file_path" , path);
                    if (downLoadInfo != null) {

                        if (downLoadInfo.getStatus() == Status.SUCCESS) {
                            if (new File(path).exists()) {
                                ToastUtils.showContent(fileInfo.getName() + "文件已下载");
                                return;
                            }

                        }

                    } else {

                        downLoadInfo = new DownLoadInfo();
                        downLoadInfo.setDownloadUrl(url);
                        downLoadInfo.setFile_name(fileInfo.getName());
                        downLoadInfo.setFile_path(path);
                        downLoadInfo.setDir(fileInfo.getDir());
                        dbManager.insert(downLoadInfo);
                    }
                    DownloadListenerImpl listener =  new DownloadListenerImpl(downLoadInfo);

                    listener.addAllThreadInfo(dbManager.queryThread("file_path" , downLoadInfo.getFile_path()));
                    downloadManager.startDownload(downLoadInfo,listener);

                } else {
                    ToastUtils.showContent(fileInfo.getName() + "正在下载");
                }

            }

        }

    }

    /**
     * 上传文件
     */
    public static void uploadFile(String dir , List<File> data) {
        if (data != null && data.size() > 0) {

            for (File file : data) {

                if (file.length() == 0) {

                    ToastUtils.showContent(file.getName() + "文件无内容");
                } else {

                    if (uploadManager.isUpload(file.getPath())) {

                        UploadInfo uploadInfo =  dbManager.isExistUp("file_path" , file.getPath());

                        if (uploadInfo != null){
                            if ( uploadInfo.getStatus() == Status.SUCCESS){

                                ToastUtils.showContent(file.getName() + "文件已上传");
                                continue;
                            }

                        }else {

                            uploadInfo = new UploadInfo(file.getPath(), file.getName(), dir);
                            dbManager.insert(uploadInfo);

                        }
                        uploadInfo.setStatus(Status.PROGRESS);
                        uploadManager.startUpload(uploadInfo, new UploadListenerImpl(uploadInfo), file);

                    } else {
                        ToastUtils.showContent(file.getName() + "正在上传");
                    }
                }

            }
        } else {
            ToastUtils.showContent("请选择上传文件");
        }
    }
}
