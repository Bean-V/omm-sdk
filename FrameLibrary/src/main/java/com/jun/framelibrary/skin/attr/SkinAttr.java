package com.jun.framelibrary.skin.attr;

import android.view.View;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/15 23:54
 * Version 1.0
 * Description：
 */
public class SkinAttr {
    private String mResName;
    private SkinType mSkinType;
    public SkinAttr(String resName, SkinType skinType) {
        mResName = resName;
        mSkinType = skinType;
    }

    public String getResName() {
        return mResName;
    }

    public SkinType getSkinType() {
        return mSkinType;
    }

    public void skin(View view) {
        mSkinType.skin(view, mResName);
    }
}
