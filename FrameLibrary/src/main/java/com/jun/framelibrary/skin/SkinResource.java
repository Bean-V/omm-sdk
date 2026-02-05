package com.jun.framelibrary.skin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ComplexColorCompat;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/15 17:55
 * Version 1.0
 * Description：皮肤资源管理类
 */
public class SkinResource {
    // 资源通过这个对象获取
    private Resources mSkinResource;
    private String mSkinPackageName;
    private static final String TAG = "SkinResource";
    @SuppressLint("DiscouragedPrivateApi")
    public SkinResource(Context context, String skinPath){

        Resources superRes = context.getResources();
        try {
            AssetManager assetManage = AssetManager.class.newInstance();

            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);

            method.invoke(assetManage, skinPath);

            mSkinResource = new Resources(assetManage,
                    superRes.getDisplayMetrics(), superRes.getConfiguration());
            //获取SKin皮肤包名
            mSkinPackageName = context.getPackageManager()
                    .getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES)
                    .packageName;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过名字获取Drawable资源
     * @param resName
     * @return
     */
    public Drawable getDrawableByName(String resName){
        Drawable drawable = null;
        try {
            int drawableId = mSkinResource.getIdentifier(resName, "drawable", mSkinPackageName);
            drawable = mSkinResource.getDrawable(drawableId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 通过名字获取Color资源
     * @param resName
     * @return
     */
    public ColorStateList getColorByName(String resName){
        ColorStateList color = null;
        try {
            int colorId = mSkinResource.getIdentifier(resName, "color", mSkinPackageName);
            color = mSkinResource.getColorStateList(colorId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return color;
    }
}
