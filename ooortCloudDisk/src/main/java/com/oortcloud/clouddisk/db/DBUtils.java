package com.oortcloud.clouddisk.db;

import android.app.Activity;
import android.util.Log;

import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/17 16:22
 * @version： v1.0
 * @function： 查询工具累
 */
public class DBUtils {

    public static  List getUpAndDown(List<FileInfo> data , String value){
        getDowList(data , value);
        getUpList(data , value);
        return null;
    }
    private static void getUpList(List<FileInfo> data  , String value){
        DBManager dbManager = DBManager.getInstance();

        List<UploadInfo> uploadList = dbManager.queryUp("dir" ,value);

        for (UploadInfo uploadInfo : uploadList){
            if (uploadInfo.getStatus() != Status.SUCCESS){
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(uploadInfo.getFile_name());
                fileInfo.setType(Status.TYPE_UPLOAD);
                fileInfo.setCompleteSize(uploadInfo.getCompleteSize());
                fileInfo.setContentLength(uploadInfo.getContentLength());
                fileInfo.setProgress(uploadInfo.getProgress());
                fileInfo.setStatus(uploadInfo.getStatus());
                fileInfo.setFile_path(uploadInfo.getFile_path());
                data.add(fileInfo);
            }
        }


    }

    private static void getDowList(List<FileInfo> data , String value){
        DBManager dbManager = DBManager.getInstance();
        List<DownLoadInfo> downList = dbManager.queryDown("dir" ,value);

        for (DownLoadInfo downLoadInfo : downList){
            if (downLoadInfo.getStatus() != Status.SUCCESS) {

                FileInfo fileInfo =  new FileInfo(downLoadInfo.getDir() , downLoadInfo.getFile_name());

                int index = data.indexOf(fileInfo);

                if (index != -1){
                    fileInfo =  data.get(index);
                    fileInfo.setType(Status.TYPE_DOWN);
                    fileInfo.setCompleteSize(downLoadInfo.getCompleteSize());
                    fileInfo.setContentLength(downLoadInfo.getContentLength());
                    fileInfo.setProgress(downLoadInfo.getProgress());
                    fileInfo.setStatus(downLoadInfo.getStatus());
                    fileInfo.setFile_path(downLoadInfo.getFile_path());
                }
            }
        }

    }

}
