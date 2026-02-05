package com.oortcloud.basemodule.utils;

import android.app.Application;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {


    public static void loadImage(ImageView iv,String url,int defaultRes){
        if(defaultRes > 0) {
            Glide.with(getApplication()).load(url).placeholder(defaultRes).into(iv);
        }else{
            Glide.with(getApplication()).load(url).into(iv);
        }
    }


    public static Application getApplication() {
        Application currentApplication = null;
        try {
            if (currentApplication == null) {
                currentApplication = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            return currentApplication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
