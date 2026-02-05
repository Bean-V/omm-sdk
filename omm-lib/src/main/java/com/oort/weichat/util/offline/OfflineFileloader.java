package com.oort.weichat.util.offline;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class OfflineFileloader {
    private DownloadManager mDownloadManager;
    private Context mContext;
    private OfflineFileLoaderListenter listenter;

    private String TAG = "upZipFile";

    //拷贝的公共外置路径
    public static String COPY_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.shenzhou.inFBC/Download/0/Download";
    //标志文件路径
    public static String TEMP_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.shenzhou.inFBC/Download/0/";

    //检务通zip的路径
    public static String SELF_UNZIP_PATH = "/sdcard/com.oortcloud.offlinefiles/";

    public OfflineFileloader(Context context){
        this.mContext = context;
    }

    public void setListenter(OfflineFileLoaderListenter listenter){
        this.listenter = listenter;
    }

    public void startLoading(){
        new Thread(new LoadRunnable()).start();
    }

    private class LoadRunnable implements Runnable {

        @Override
        public void run(){
            if (listenter==null){
                return;
            }
            try{
                if (copyFileToSelfDir()){
                    listenter.onLoadFish();
                }else {
                    listenter.onFail("copy file fail!");
                }
            }catch (Exception e){
                e.printStackTrace();
                listenter.onFail("load file fial!");
            }
        }

        //拷贝文件到自己到目录下
        private boolean copyFileToSelfDir(){
            /**
             *  * {ExternalStorageDirectory}/com.shenzhou.inFBC/Download
             *  * {ExternalStorageDirectory}/com.shenzhou.inFBC/Upload
             * */
            try{
                //判断拷贝文件夹是否存在
                File copyFolder = new File(COPY_FOLDER_PATH);
                if (!copyFolder.exists()){
                    return false;
                }

                //首先判断是否存在正在下载标志文件
                if (isDownloading()){
                    return false;
                }

                //复制开始时先创建标志文件
                createDownloadTempFile();

                //判断zip路径下是否有其他文件，有删除掉在拷贝
                File unzipFolder = new File(SELF_UNZIP_PATH);
                if (unzipFolder.exists()){
                    deleteFile(unzipFolder);
                }else{
                    unzipFolder.mkdirs();
                }
                //拷贝文件
                copyDirectory(copyFolder,unzipFolder);
                //拷贝完成后删除掉公共文件夹的文件
                deleteFile(copyFolder);
                //遍历解压zip文件
//                unZipInSelfFolder(unzipFolder);

                //操作完毕后删除标志文件
                deleteDownloadTempFile();

                return true;
            }catch (Exception e){
                e.printStackTrace();
            }

            return false;
        }


        //判断是否有正在下载的标志文件
        public boolean isDownloading(){
            File file = new File(TEMP_FILE_PATH+"/DataSyncDownloading.txt");
            if (file.exists()){
                return true;
            }
            return false;
        }

        //创建下载标志文件
        public void createDownloadTempFile(){
           try{
               File file = new File(TEMP_FILE_PATH+"/DataSyncDownloadPause.txt");
               if (!file.exists()){
                   file.createNewFile();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }

        //删除下载标志文件
        public void deleteDownloadTempFile(){
            try{
                File file = new File(TEMP_FILE_PATH+"/DataSyncDownloadPause.txt");
                if (file.exists()){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //删除文件夹文件
        /**
         * 删除文件夹所有内容
         *
         */
        public boolean deleteFile(File file) {
            System.out.println("file is==>" + file);
            boolean isSuccess = false;
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    System.out.println("is file");
                    file.delete(); // delete()方法 你应该知道 是删除的意思;
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    System.out.println("is dic");
                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
                isSuccess = true;
            } else {
                isSuccess = false;
            }
            return isSuccess;
        }

        //遍历解压文件
        public boolean unZipInSelfFolder(File unzipFolder){
            try{
                List<File> zipFiles = new ArrayList<>();
                File[] files = unzipFolder.listFiles();
                if (files!=null){
                    for (int i=0;i<files.length;i++){
                        String filename = files[i].getName();
                        if (isZipFile(filename)){
                            upZipFile(files[i],SELF_UNZIP_PATH);
                            zipFiles.add(files[i]);
                        }
                    }
                }
                //解压完毕后把zip文件删除
                for (int i=0;i<zipFiles.size();i++){
                    File zipfile = zipFiles.get(i);
                    zipfile.delete();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        //判断是否是zip后缀
        public boolean isZipFile(String filename){
            return  filename.endsWith(".zip");
        }

        //拷贝成功解压文件
        public  boolean upZipFile(File zipFile, String folderPath) throws IOException {
            boolean isSuccess = true;
            ZipFile zfile;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                zfile = new ZipFile(zipFile, Charset.forName("utf-8"));
            } else {
                zfile = new ZipFile(zipFile);
            }
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
//                    Log.d(TAG, "ze.getName() = " + ze.getName());
//                    String dirstr = folderPath + ze.getName();
//                    dirstr = new String(dirstr.getBytes("utf-8"), "utf-8");
//                    Log.d(TAG, "str = " + dirstr);
//                    File f = new File(dirstr);
//                    f.mkdir();
                    continue;
                }
                Log.d(TAG, "ze.getName() = " + ze.getName());
                try {
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                    InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
                    int readLen = 0;
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        os.write(buf, 0, readLen);
                    }
                    is.close();
                    os.close();
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e(TAG, "upZipFile: e = " + e.getMessage());
                }
            }
            zfile.close();

            return isSuccess;
        }


            /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public  File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("utf-8"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("utf-8"), "utf-8");
                Log.d(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }

        /**
         * 复制文件夹
         */
        public boolean copyDirectory(File src, File dest) {
            if (!src.isDirectory()) {
                return false;
            }
            if (!dest.isDirectory() && !dest.mkdirs()) {
                return false;
            }

            File[] files = src.listFiles();
            int lx = files.length;
            for (File file : files) {
                if (file.getName().equals("DataSyncDownloadPause.txt")){
                    continue;
                }
                File destFile = new File(dest, file.getName());
                if (file.isFile()) {
                    if (!copyFile(file, destFile)) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!copyDirectory(file, destFile)) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * 复制文件
         */
        public  boolean copyFile(File src, File des) {
            if (!src.exists()) {
                Log.e("cppyFile", "file not exist:" + src.getAbsolutePath());
                return false;
            }
            if (!des.getParentFile().isDirectory() && !des.getParentFile().mkdirs()) {
                Log.e("cppyFile", "mkdir failed:" + des.getParent());
                return false;
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(des));
                byte[] buffer = new byte[4 * 1024];
                int count;
                while ((count = bis.read(buffer, 0, buffer.length)) != -1) {
                    if (count > 0) {
                        bos.write(buffer, 0, count);
                    }
                }
                bos.flush();
                return true;
            } catch (Exception e) {
                Log.e("copyFile", "exception:", e);
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

    }

}
