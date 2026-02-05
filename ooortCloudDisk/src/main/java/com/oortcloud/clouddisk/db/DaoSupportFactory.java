package com.oortcloud.clouddisk.db;

import android.database.sqlite.SQLiteDatabase;

import com.oortcloud.clouddisk.http.HttpConstants;

import java.io.File;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/12 14:50
 * @version： v1.0
 * @function：
 */
public class DaoSupportFactory {

    private static DaoSupportFactory mFactory;

    // 持有外部数据库的引用
    private SQLiteDatabase mSqLiteDatabase;


    private DaoSupportFactory(){
        // 把数据库放到内存卡里面  判断是否有存储卡 6.0要动态申请权限 还需加一层用户目录
        File dbRoot = new File(HttpConstants.USER_PATH + File.separator + "database");
        if (!dbRoot.exists()) {
            dbRoot.mkdirs();
        }

        File dbFile = new File(dbRoot, "cloud_disk01.db");

        // 打开或者创建一个数据库
        mSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    public static DaoSupportFactory getFactory(){
        if (mFactory == null){
            synchronized (DaoSupportFactory.class){
                if (mFactory == null){
                    mFactory = new DaoSupportFactory();
                }
            }
        }
        return mFactory;
    }

    public <T> IDaoSupport<T> getDaoSupport(Class<T> clazz) {
        IDaoSupport<T> daoSupport = new DaoSupport();
        daoSupport.init(mSqLiteDatabase,clazz);
        return daoSupport;
    }

}
