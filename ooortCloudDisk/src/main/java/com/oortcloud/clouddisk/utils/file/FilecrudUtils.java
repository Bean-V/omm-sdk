package com.oortcloud.clouddisk.utils.file;

import android.util.Log;

import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.http.HttpConstants;
import com.oortcloud.clouddisk.transfer.CopyInfo;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.utils.manager.FileUtils;

import java.io.File;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/25 18:39
 * @version： v1.0
 * @function：修改文件信息/ 及表信息
 */
public class FilecrudUtils {
    /**
     * 修改文件/文件夹 名称
     *
     * @param dir
     * @param name
     * @param newName
     */
    public static void newName(String dir, String name, String newName) {
        DBManager dbManager = DBManager.getInstance();
        String path = HttpConstants.USER_PATH + dir  + name;
        DownLoadInfo downLoadInfo = dbManager.isExistDown("file_path" , path);
        if (downLoadInfo != null) {
            downLoadInfo.setFile_name(newName);
            downLoadInfo.setFile_path(HttpConstants.USER_PATH + dir  + newName);
            dbManager.update(downLoadInfo, "file_path" , path);
            //修改文件
            File file = new File(path);
            if (file.exists()) {
                file.renameTo(new File(HttpConstants.USER_PATH + dir + File.separator + newName));
            }

        }

        //上传是否需要修改
        UploadInfo uploadInfo = dbManager.isExistUp("file_name", name);

        if (uploadInfo != null) {
            uploadInfo.setFile_name(newName);
            dbManager.update(uploadInfo, "file_name", name);
        }

    }

    /**
     * 删除文件/文件夹
     *
     * @param dir
     * @param name
     *
     */
    public static void  delete(String dir , String name){
        String path = HttpConstants.USER_PATH + dir  + name;
        DBManager dbManager = DBManager.getInstance();
        DownLoadInfo downLoadInfo = dbManager.isExistDown("file_path" , path);
        if (downLoadInfo != null) {
            dbManager.deleteDown( "file_path" , path);
            //修改文件
//            File file = new File(path);
//            if (file.exists()) {
//                file.delete();
//            }

        }

        //上传是否需要删除
        UploadInfo uploadInfo = dbManager.isExistUp("file_name", name);
        if (uploadInfo != null) {
            dbManager.deleteUp( "file_name", name);
        }

    }

    /**
     * 移动文件
     *
     * @param dir
     * @param name
     * @param newDir
     */
    public static void moveUpdate(String name, String dir, String newDir) {
        DBManager dbManager = DBManager.getInstance();
        String path = HttpConstants.USER_PATH + dir + name;
        DownLoadInfo downLoadInfo = dbManager.isExistDown("file_path" , path);
        if (downLoadInfo != null) {
            downLoadInfo.setFile_path(HttpConstants.USER_PATH + newDir + name);
            dbManager.update(downLoadInfo, "file_path" , path);
            //修改文件
            File file = new File(path);
            Log.v("msg" , path);
            if (file.exists()) {
                FileUtils.checkLocalFilePath(HttpConstants.USER_PATH + newDir);
                file.renameTo(new File(HttpConstants.USER_PATH + newDir + name));
            }
        }

    }
    /**
     * 复制文件
     *
     * @param dir
     * @param name
     * @param newDir
     */
    public static void copyUpdate(String name, String dir, String newDir) {
       DBManager dbManager =  DBManager.getInstance();
        String path = HttpConstants.USER_PATH + dir + name;
        String newPath = HttpConstants.USER_PATH + newDir + name;
        Log.v("msg" , path);
        //修改文件
        File file = new File(path);
        if (file.exists()) {
            new Thread(() -> {
                try {

                    CopyFileUtils.copyDir(path, HttpConstants.USER_PATH + newDir + name);

                } catch (Exception e) {
                    Log.v("msg" , e.toString());
                }
            }).start();
        }else {

            CopyInfo newCopyInfo = dbManager.isExistCopy("file_path" ,newPath);

            if (newCopyInfo == null){

                newCopyInfo = new CopyInfo();
                newCopyInfo.setFile_name(name);
                newCopyInfo.setDir(newDir);
                newCopyInfo.setFile_path(newPath);
                //当复制的文件也是其它路径复制过来
                CopyInfo copyInfo = dbManager.isExistCopy("file_path" ,path);
                if (copyInfo != null){
                    newCopyInfo.setParent_Dir(copyInfo.getParent_Dir());
                    newCopyInfo.setParent_path(copyInfo.getParent_path());
                }else {
                    newCopyInfo.setParent_Dir(dir);
                    newCopyInfo.setParent_path(path);
                }
                dbManager.insert(newCopyInfo);
            }
        }




    }

}
