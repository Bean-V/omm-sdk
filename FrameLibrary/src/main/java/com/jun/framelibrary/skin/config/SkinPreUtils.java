package com.jun.framelibrary.skin.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/16 16:23
 * Version 1.0
 * Description：保存皮肤路径
 */
public class SkinPreUtils {
    private volatile static SkinPreUtils mInstance;

    private Context mContext;
    private SkinPreUtils(Context context){
        if (context != null){
            mContext = context.getApplicationContext();
        }
    }

    public static SkinPreUtils getInstance(Context context) {
        if (mInstance == null){
            synchronized (SkinPreUtils.class){
                if (mInstance == null){
                    mInstance = new SkinPreUtils(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存路径
     * @param skinPath
     */
    public void saveSkinPath(String skinPath){
        mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
        .edit().putString(SkinConfig.SKIN_PATH_NAME, skinPath).commit();
    }

    /**
     * 获取路径
     */
    public String getSkinPath(){
        return mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
            .getString(SkinConfig.SKIN_PATH_NAME, "");
    }

    /**
     * 清空路径
     */
    public void clearSkinInfo() {
        saveSkinPath("");
    }
}
