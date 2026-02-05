package com.oortcloud.appstore.download;

import com.oortcloud.appstore.utils.AppManager;

import java.io.File;
import java.io.IOException;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/18 17:57
 * @version： v1.0
 * @function 处理文件辅助类
 */
public class FileUtils {

    /**
     * 用于检查文件路径是否已经存在
     * @param localFilePath
     */
    public static void checkLocalFilePath(String localFilePath){
        File path = new File(localFilePath.substring(0 , localFilePath.lastIndexOf( File.separator)+ 1));
        File nomediaFile = new File(AppManager.BASE_PATH +".nomedia");
        File file = new File(localFilePath);
        if (!path.exists()){
            path.mkdirs();

        }

        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        if (!nomediaFile.exists()){
            try {

                nomediaFile.createNewFile();

            }catch (Exception e){

            }
        }
    }
}
