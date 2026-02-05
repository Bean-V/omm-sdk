package com.jun.framelibrary.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.jun.framelibrary.skin.attr.SkinView;
import com.jun.framelibrary.skin.callback.ISkinChangeListener;
import com.jun.framelibrary.skin.config.SkinConfig;
import com.jun.framelibrary.skin.config.SkinPreUtils;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/15 16:48
 * Version 1.0
 * Description：皮肤管理类
 */
public class SkinManager {
    private Context mContext;
    private  final static SkinManager mInstance;

    private SkinResource mSkinResource;

    private Map<ISkinChangeListener,List<SkinView>> mSkinViews;

    static {
        mInstance = new SkinManager();
    }

    private SkinManager(){
        mSkinViews = new HashMap<>();
    }

    public static SkinManager getInstance() {
        return mInstance;
    }

    public void init(Context context){

        this.mContext = context.getApplicationContext();
        //判断文件是否存在，防止被误删后找不到文件
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        File file = new File(currentSkinPath);
        if (!file.exists()){
            //清空皮肤信息
            SkinPreUtils.getInstance(mContext).clearSkinInfo();
            return;
        }
        //获取SKin皮肤包名
        String skinPackageName = Objects.requireNonNull(context.getPackageManager()
                .getPackageArchiveInfo(currentSkinPath, PackageManager.GET_ACTIVITIES))
                .packageName;
        //校验包名
        if (TextUtils.isEmpty(skinPackageName)){
            SkinPreUtils.getInstance(mContext).saveSkinPath("");
            return;
        }

        //校验签名，防止被修改

        //初始化
        mSkinResource = new SkinResource(mContext, currentSkinPath);

    }

    /**
     * 通过当前Activity获取内部的SkinView
     * @param listener
     * @return
     */
    public List<SkinView> getSkinViews(ISkinChangeListener listener) {
        return mSkinViews.get(listener);
    }

    /**
     * 注册SkinView
     * @param listener
     * @param skinViews
     */
    public void register(ISkinChangeListener listener, List<SkinView> skinViews) {
        mSkinViews.put(listener, skinViews);
    }

    /**
     * 获取当前的皮肤资源
     * @return
     */
    public SkinResource getSkinResource() {
        return mSkinResource;
    }

    /**
     * 是否需要换肤
     * @param skinView
     */
    public void checkChangeSKin(SkinView skinView) {
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if (!TextUtils.isEmpty(currentSkinPath)){
            skinView.skin();
        }
    }

    /**
     * 保存皮肤状态
     * @param skinPath
     */
    private void saveSkinStatus(String skinPath) {
        SkinPreUtils.getInstance(mContext).saveSkinPath(skinPath);
    }


    /**
     * 加载皮肤
     * @param skinPath
     * @return
     */
    public int loadSkin(String skinPath) {
        // 校验签名  增量更新再说
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        //如果当前资源一样
        if (skinPath.equals(currentSkinPath)){
            return SkinConfig.SKIN_CHANGE_NO_THING;
        }
        File file = new File(currentSkinPath);
        if (!file.exists()){
            return SkinConfig.SKIN_FILE_NO_EXIST;
        }
        //获取SKin皮肤包名
        String skinPackageName = Objects.requireNonNull(mContext.getPackageManager()
                .getPackageArchiveInfo(currentSkinPath, PackageManager.GET_ACTIVITIES))
                .packageName;
        //校验包名
        if (TextUtils.isEmpty(skinPackageName)){
            SkinPreUtils.getInstance(mContext).saveSkinPath("");
            return SkinConfig.SKIN_FILE_ERROR;
        }
        mSkinResource = new SkinResource(mContext,skinPath);
        // 改变皮肤
        changeSkin();

        //保存Skin路径
        saveSkinStatus(skinPath);
        return  SkinConfig.SKIN_CHANGE_SUCCESS;
    }

    /**
     * 恢复默认
     */
    public int restoreDefault(){
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if (TextUtils.isEmpty(currentSkinPath)){
            return SkinConfig.SKIN_CHANGE_NO_THING;
        }
        //获取运行APK资源路径
        String resourcePath =   mContext.getPackageResourcePath();
        mSkinResource = new SkinResource(mContext,resourcePath);
        // 改变皮肤
        changeSkin();
        //清空，下次进入直接使用默认，不会在加载换肤
        SkinPreUtils.getInstance(mContext).clearSkinInfo();

        return SkinConfig.SKIN_CHANGE_SUCCESS;
    }

    /**
     * 改变皮肤
     */
    private void changeSkin(){
        Set<ISkinChangeListener> keys = mSkinViews.keySet();
        for (ISkinChangeListener key : keys){
            List<SkinView> skinViews = mSkinViews.get(key);
            for (SkinView skinView : skinViews) {
                skinView.skin();
            }
            key.changeSkin(mSkinResource);
        }
    }

    /**
     * 处理内存回收
     * @param listener
     */
    public void unRegister(ISkinChangeListener listener) {
        mSkinViews.remove(listener);
    }
}
