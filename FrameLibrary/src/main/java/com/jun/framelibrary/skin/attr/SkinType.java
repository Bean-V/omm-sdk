package com.jun.framelibrary.skin.attr;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jun.framelibrary.skin.SkinManager;
import com.jun.framelibrary.skin.SkinResource;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/16 0:01
 * Version 1.0
 * Description：
 */
public enum  SkinType {

    TEXT_COLOR("textColor"){
        @Override
        public void skin(View view, String resName) {
            SkinResource skinResource = getSkinResource();
            ColorStateList color = skinResource.getColorByName(resName);
            //设置文本颜色
            if(color != null){
                TextView textView = (TextView) view;
                textView.setTextColor(color);
            }
        }
    },BACKGROUND("background"){
        @Override
        public void skin(View view, String resName) {
            SkinResource skinResource = getSkinResource();
            //背景可能是图片或颜色
            Drawable drawable = skinResource.getDrawableByName(resName);
            if (drawable != null){
                view.setBackground(drawable);
            }
            ColorStateList color = skinResource.getColorByName(resName);
            if (color != null){
                view.setBackgroundColor(color.getDefaultColor());
            }


        }
    },SRC("src"){
        @Override
        public void skin(View view, String resName) {
            SkinResource skinResource = getSkinResource();
            Drawable drawable = skinResource.getDrawableByName(resName);
            if (drawable != null){
                ImageView imageView = (ImageView) view;
                imageView.setImageDrawable(drawable);
            }
        }
    };


    public abstract void skin(View view, String resName);

    private String mResName;

    SkinType(String resName) {
        mResName = resName;
    }

    public String getResName(){
        return mResName;
    }

    SkinResource getSkinResource(){
       return SkinManager.getInstance().getSkinResource();
    }
}
