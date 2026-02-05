package com.oortcloud.appstore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * @ProjectName: AppStore-master
 * @FileName: DBHelper.java
 * @Function: SQLite
 * @Author: zhangzhijun / @CreateDate: 20/02/29 05:41
 * @Version: 1.0
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appmall.db";  //数据库名字
    private static final int DATABASE_VERSION = 3 ;         //数据库版本号
    private String mTableName;

    private Context mContext;

    public DBHelper(Context context){
        this(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    /**
     * 构造方法
     * @param context
     * @param name 数据库名
     * @param factory 允许我们在查询数据的时候返回一个自定义的 Cursor，一般都是传入 null
     * @param version 当前数据库的版本号，可用于对数据库进行升级操作
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * 创建数据库
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行建表语句
        if (!TextUtils.isEmpty(mTableName))
            db.execSQL("CREATE TABLE " + mTableName);
    }

    /**
     * 升级数据库
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 若发现数据库中已存在 book 表或 category 表，将这两张表删除掉
//        db.execSQL("drop table if exists book");
//        db.execSQL("drop table if exists category");
        // 重新创建表
        onCreate(db);
    }

}
