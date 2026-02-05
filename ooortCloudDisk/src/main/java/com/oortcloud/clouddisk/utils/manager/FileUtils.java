package com.oortcloud.clouddisk.utils.manager;

import android.os.Environment;

import com.oortcloud.clouddisk.http.HttpConstants;

import java.io.IOException;
import java.util.Calendar;

import java.io.File;
import java.text.SimpleDateFormat;
/**
 * @filename:
 * @author: zzj/@date: 2021/1/24 18:51
 * @version： v1.0
 * @function： 文件判断检测 工具类
 */
public class FileUtils {

    /**文档类型*/
    public static final int TYPE_DOC = 0;
    /**apk类型*/
    public static final int TYPE_APK = 1;
    /**压缩包类型*/
    public static final int TYPE_ZIP = 2;


    /**
     * 判断文件是否存在
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx") ||path.endsWith(".pdf")) {
            return TYPE_DOC;
        }else if (path.endsWith(".apk")) {
            return TYPE_APK;
        }else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return TYPE_ZIP;
        }else{
            return -1;
        }
    }


    /**通过文件名获取文件图标*/
    public static int getFileIconByPath(String path){
        path = path.toLowerCase();
//        int iconId = R.mipmap.unknow_file_icon;
//        if (path.endsWith(".txt")){
//            iconId = R.mipmap.type_txt;
//        }else if(path.endsWith(".doc") || path.endsWith(".docx")){
//            iconId = R.mipmap.type_doc;
//        }else if(path.endsWith(".xls") || path.endsWith(".xlsx")){
//            iconId = R.mipmap.type_xls;
//        }else if(path.endsWith(".ppt") || path.endsWith(".pptx")){
//            iconId = R.mipmap.type_ppt;
//        }else if(path.endsWith(".xml")){
//            iconId = R.mipmap.type_xml;
//        }else if(path.endsWith(".htm") || path.endsWith(".html")){
//            iconId = R.mipmap.type_html;
//        }
        return 0;
    }

    /**是否是图片文件*/
    public static boolean isPicFile(String path){
        path = path.toLowerCase();
        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
            return true;
        }
        return false;
    }


    /** 判断SD卡是否挂载 */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从文件的全名得到文件的拓展名
     *
     * @param filename
     * @return
     */
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }
    /**
     * 读取文件的修改时间
     * @param f
     * @return
     */
    public static String getModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        // System.out.println("修改时间[2] " + formatter.format(cal.getTime()));
        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }
    /**
     * 用于检查文件路径是否已经存在
     * @param localFilePath
     */
    public static void checkLocalFilePath(String localFilePath){
        File path = new File(localFilePath.substring(0 , localFilePath.lastIndexOf( File.separator)+ 1));
        File nomeDiaFile = new File(HttpConstants.BASE_PATH +".nomedia");
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

        if (!nomeDiaFile.exists()){
            try {

                nomeDiaFile.createNewFile();

            }catch (Exception e){

            }
        }
    }

}
