package com.jun.framelibrary.skin.attr;

import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/16 0:33
 * Version 1.0
 * Description：保存前View 及换肤属性
 */
public class SkinView {
    private View mView;
    private List<SkinAttr> mSkinAttrs;

    public SkinView(View view, List<SkinAttr> skinAttrs) {
        mView = view;
        mSkinAttrs = skinAttrs;
    }

    public void skin() {
        for (SkinAttr mSkinAttr : mSkinAttrs) {
            mSkinAttr.skin(mView);
        }
    }
}
