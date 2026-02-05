package com.oortcloud.clouddisk.utils.helper;

import android.content.Context;
import android.util.Log;

import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.http.HttpConstants;
import com.oortcloud.clouddisk.transfer.CopyInfo;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.file.OpenFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/13 16:13
 * @version： v1.0
 * @function：读写文件 检测文件
 */
public class FileHelper {
    private static Context mContext;
    private static FileHelper mInstance;
    private static Object mLock = new Object();


    public static FileHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileHelper();
                    mContext = context;
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取指定目录所有文件
     *
     * @param pathList
     * @param path
     */
    public static void scanDir(List<File> pathList, String path) {
        List<File> fileList = new ArrayList<>();
        File file = new File(path + "/");
        if (file == null) {
            return;
        }
        File[] files = file.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {

                    pathList.add(files[i]);
                } else {
                    fileList.add(files[i]);
                }

            }
            //排序
            Collections.sort(fileList);
            //排序
            Collections.sort(pathList);

            pathList.addAll(fileList);

        }
    }

    /**
     * 计算文件大小
     *
     * @param fileSize
     */
    public static String reckonFileSize(long fileSize) {

        if (fileSize >= 1024 * 1024 * 1024) {

            return String.format("%.1f", (double) fileSize / 1024 / 1024 / 1024) + "G";

        } else if (fileSize >= 1024 * 1024) {

            return String.format("%.1f", (double) fileSize / 1024 / 1024) + "MB";
        } else if (fileSize >= 1024) {

            return String.format("%.1f", (double) fileSize / 1024) + "KB";
        } else {
            return fileSize + " B";
        }
    }

    public void openFile(FileInfo fileInfo) {
        DBManager dbManager = DBManager.getInstance();
        //上传file路径 通过 fileName 查询本地库获取
        //打开文件 先判断上传路径是否存在
        UploadInfo uploadInfo = dbManager.isExistUp("file_name", fileInfo.getName());

        if (uploadInfo != null) {
            Log.v("msg" , fileInfo.getName());
            File file = new File(uploadInfo.getFile_path());
            if (file.exists()) {
                Log.v("msg" , fileInfo.getName());
                mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(uploadInfo.getFile_path()));
                return;
            }
        }

        //获取下载存储路径  是否存在本地
        String path = HttpConstants.USER_PATH + fileInfo.getDir() + fileInfo.getName();
        if ( new File(path).exists()) {
            mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(path));
            return;
        }

        //判断是否属于复制文件 //两种情况都未下载  二修改文件信息
        CopyInfo copyInfo =  dbManager.isExistCopy("file_path", path);
        if (copyInfo != null){

            path = copyInfo.getParent_path();
            fileInfo.setDir(copyInfo.getParent_Dir());
        }

        DownLoadInfo downLoadInfo = dbManager.isExistDown("file_path", path);
        if (downLoadInfo != null) {
                    if (downLoadInfo.getStatus() == Status.SUCCESS) {
                        if (new File(path).exists()) {
                    mContext.startActivity(OpenFileUtil.getInstance(mContext).openFile(path));
                    return;
                } else {

                    TransferHelper.startDownload(fileInfo);
                    ToastUtils.showContent("正在下载");
                }
            } else if (downLoadInfo.getStatus() == Status.FAIL) {
                ToastUtils.showContent("文件下载失败,请重新下载");
            } else if (downLoadInfo.getStatus() == Status.PROGRESS) {
                ToastUtils.showContent("文件正在下载");
            } else if (downLoadInfo.getStatus() == Status.PAUSED) {
                ToastUtils.showContent("文件暂停下载");
            }

        } else {

                TransferHelper.startDownload(fileInfo);
                ToastUtils.showContent("正在下载");

        }

    }


}

