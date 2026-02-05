package com.oortcloud.clouddisk.utils.helper;

import android.widget.ImageView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.utils.manager.FileBean;

import java.io.File;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/30 14:52
 * @version： v1.0
 * @function： 处理文件类型图片
 */
public class ImgHelper {
    public static void  setImageResource(FileInfo fileInfo , ImageView imageView){

        if (fileInfo.getIs_dir() == 1){
            imageView.setImageResource(R.mipmap.ic_file);
        }else {
            setImageResource( fileInfo.getName() , imageView);
        }

    }
    public static void setImageResource(File file , ImageView imageView){

        if (file.isDirectory()){
            imageView.setImageResource(R.mipmap.ic_file);
        }else {
            setImageResource(file.getName() , imageView);
        }

    }

    public static void  setImageResource(String fileName , ImageView imageView){

        if (fileName.endsWith(".ppt")){
            imageView.setImageResource(R.mipmap.ppt);
        }else if (fileName.endsWith(".zip")){
            imageView.setImageResource(R.mipmap.ic_zip);
        }else if (fileName.endsWith(".xlsx")){
            imageView.setImageResource(R.mipmap.ic_excel);
        }else if (fileName.endsWith(".pdf")){
            imageView.setImageResource(R.mipmap.pdf);
        }else if (fileName.endsWith(".docx") ||fileName.endsWith(".doc")){
            imageView.setImageResource(R.mipmap.word);
        }else if (fileName.endsWith(".txt")){
            imageView.setImageResource(R.mipmap.ic_txt);
        }else if (fileName.endsWith(".mp3")){
            imageView.setImageResource(R.mipmap.ic_music);
        }else if (fileName.endsWith(".mp4")){
            imageView.setImageResource(R.mipmap.ic_video);
        }else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")){
            imageView.setImageResource(R.mipmap.ic_pic);
        }else {
            imageView.setImageResource(R.mipmap.ic_default);
        }
    }
}
