package com.sentaroh.android.upantool.contact;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

//import com.oortcloud.synhelperout.BaseApplication;
import com.sentaroh.android.upantool.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/13 16:13
 * @version： v1.0
 * @function：读写文件
 */
public class FileUtil {

    public static final String CONTACT_TXT = "contact.txt";
    public static final String TEXT_TXT = "text.txt";
    public static final String FILE_ZIP = "file.zip";
    public static final String PIC_ZIP = "pic.zip";
    public static final String YISU_CACHE = "yisucache";
    //存储目录
    public final static String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + BaseApplication.getInstance().getContext().getPackageName()+ File.separator ;


    public static File write(String fileName , String content) throws IOException {

            String file =  BASE_PATH + fileName;
            checkLocalFilePath(file);
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

            oStreamWriter.append(content);
            oStreamWriter.close();

            return new File(file);
    }


    public static File _write_(String filePath , String content) throws IOException {

        String file =  filePath;
        OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

        oStreamWriter.append(content);
        oStreamWriter.close();

        return new File(file);
    }
    public static String read(String path) throws IOException {

        if (!TextUtils.isEmpty(path)){
            BufferedReader bre=new BufferedReader(new FileReader(new File(path)));
            String str;

            StringBuilder sb = new StringBuilder();

            while ((str = bre.readLine()) != null){
                sb.append(str +"\n");
            }
            if (bre != null){
                bre.close();
            }

            return sb.toString();
        }
        return "";
    }

    /**
     * 用于检查文件路径是否已经存在
     * @param localFilePath
     */
    public static void checkLocalFilePath(String localFilePath){
        File path = new File(localFilePath.substring(0 , localFilePath.lastIndexOf( File.separator)+ 1));
        File nomediaFile = new File(BASE_PATH +".nomedia");
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

    public static void scanFile(List<File> fileList , String path){

        File file = new File(path+"/" );
        if (file == null ){
            return;
        }
        File[] files = file.listFiles();

        if ( files != null){
            for (int  i = 0 ; i < files.length ; i++) {

                //是否是文件夹
                if (files[i].isDirectory()){
                    scanFile( fileList , files[i].getPath());
                }else {
                    String  fileName = files[i].getName();
                    if (fileName.endsWith(".ppt") || fileName.endsWith(".zip") ||fileName.endsWith(".xlsx")
                            ||fileName.endsWith(".pdf") ||fileName.endsWith(".docx") ){
                        Log.v("msg" , "------------"+fileName);
                        //        Log.v("msg" , files.length +"");
                        fileList.add(files[i]);

                    }
                }
            }
        }


    }

}
