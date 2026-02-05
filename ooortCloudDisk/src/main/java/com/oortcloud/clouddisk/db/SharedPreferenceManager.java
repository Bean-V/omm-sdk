package com.oortcloud.clouddisk.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.oortcloud.clouddisk.BaseApplication;


/**
 *
 * @author zhangzhijun
 * @version 1.0
 * @date 2019/4/4
 * @function 配置文件工具类
 */

public class SharedPreferenceManager {
    private static SharedPreferences sp = null;
    private static SharedPreferences.Editor editor;
    private static SharedPreferenceManager spManager = null;
    /**
     * preference文件名
     */
    private static final String SHARED_PREFERENCE_NAME = "cloud_disk.pre";

    private SharedPreferenceManager(){

        sp = BaseApplication.getInstance().getContext().getSharedPreferences(SHARED_PREFERENCE_NAME ,
                Context.MODE_PRIVATE);

        editor = sp.edit();
    }

    /**
     * 单例模式
     * @return
     */
    public static SharedPreferenceManager getInstance(){
        if (spManager  == null){
            synchronized (SharedPreferenceManager.class){
                if (spManager == null){
                    spManager = new SharedPreferenceManager();
                }
                return spManager;
            }
        }
        return spManager;
    }
    //对int类型写入
    public void putInt(String key , int value){
        editor.putInt(key , value);
        commit();
    }
    //对int类型读取
    public int getInt(String key , int defaultValue){
        return sp.getInt(key , defaultValue);
    }
    //对Boolean类型写入
    public void putBoolean(String key , Boolean value){
        editor.putBoolean(key , value);
        commit();

    }
    //对Boolean类型读取
    public Boolean getBoolean(String key , Boolean defaultValue){
        return sp.getBoolean(key , defaultValue);
    }
    //对类型写入
    public void putString(String key , String value){
        editor.putString(key , value);
        commit();

    }
    //对String类型写入
    public String getString(String key ){
        return sp.getString(key , null);
    }

    private void commit(){
        editor.commit();
    }
}
