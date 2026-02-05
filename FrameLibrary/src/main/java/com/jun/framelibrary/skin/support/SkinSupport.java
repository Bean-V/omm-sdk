package com.jun.framelibrary.skin.support;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.jun.framelibrary.skin.attr.SkinAttr;
import com.jun.framelibrary.skin.attr.SkinType;

import java.util.ArrayList;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/15 23:55
 * Version 1.0
 * Description：皮肤属性解析的支持类
 */
public class SkinSupport {

    public static List<SkinAttr> getSkinAttrs(Context context, AttributeSet attrs) {
        //解析 background   src  textColor
        List<SkinAttr> skinAttrs = new ArrayList<>();
        int attrsLength = attrs.getAttributeCount();

        for (int index = 0; index < attrsLength; index++) {
            String attrName = attrs.getAttributeName(index);
            String attrValue = attrs.getAttributeValue(index);

            //只获取需要的属性
            SkinType skinType = getSkinType(attrName);

            if (skinType != null){
                //获取资源名称 目前只有attrValue 是一个 @int类型
                String resName = getResName(context, attrValue);
                if (TextUtils.isEmpty(resName)){
                    continue;
                }

                SkinAttr skinAttr = new SkinAttr(resName, skinType);
                skinAttrs.add(skinAttr);
            }
        }

        return skinAttrs;
    }

    /**
     * 获取资源名称
     * @param context
     * @param attrValue
     * @return
     */
    private static String getResName(Context context, String attrValue) {
        String resName = null;
        // @color/white 如果直接设置颜色值 可能在换肤资源中找不到， 所以必须规范，统一
        if (attrValue.startsWith("@")){
             attrValue = attrValue.substring(1);
             //资源id
             int resId = Integer.parseInt(attrValue);
             //转换为资源名称
            resName = context.getResources().getResourceEntryName(resId);
        }
        return resName;
    }

    /**
     * 通过属性名，获取SkinType
     * @param attrName
     * @return
     */
    private static SkinType getSkinType(String attrName) {
        SkinType[] skinTypes = SkinType.values();
        for (SkinType skinType : skinTypes) {
            if(skinType.getResName().equals(attrName)){
                return skinType;
            }
        }
        return null;
    }
}
