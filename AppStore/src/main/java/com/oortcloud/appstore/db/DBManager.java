package com.oortcloud.appstore.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.oortcloud.appstore.AppStoreInit;

/**
 * @ProjectName: AppStore-master
 * @FileName: DBManager.java
 * @Function:数据库管理类 创建库 表  增删改查
 * @Author: zhangzhijun / @CreateDate: 20/02/29 06:37
 *
 * @Version: 1.0
 */
public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase sqLiteDatabase;
    private static DBManager mDBManager;
     private DBManager(){
         helper = new DBHelper(AppStoreInit.getInstance().getApplication());
         sqLiteDatabase = helper.getReadableDatabase();
     }

    /**
     * 单例模式
     * @return
     */
    public static DBManager getInstance() {
        if (mDBManager == null) {
            synchronized (DBManager.class) {
                if (mDBManager == null) {
                    mDBManager = new DBManager();
                }
                return mDBManager;
            }
        }
        return mDBManager;
    }

    /**
     * 创建表
     * @param tableName
     */
    public void createTable(String tableName , String dataStructure){
            try {
                deleteTable(tableName);
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + dataStructure);
            }catch (Exception e){}

    }

    /**
     * 移除表
     * @param tableName
     */
    public void deleteTable(String tableName){
        try {
            if (!TextUtils.isEmpty(tableName))
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
        }catch (Exception e){

        }
    }

    /**
     * 增
     * @param tableName
     * @param contentValues
     */
    public void insertData(String tableName  ,  ContentValues contentValues){
        try {
            if(!TextUtils.isEmpty(tableName)){

                Log.e("timetest", "insert: " );
               long res =  sqLiteDatabase.insert(tableName, null, contentValues);
                Log.e("timetest", "insert: "  + res);


            }
        }catch (Exception e){
            Log.e("timetest", "insert: "  + e.getLocalizedMessage());
        }
    }

    /**
     * 查
     * @param tableName
     * @return
     */
    public Cursor queryData(String tableName){

        Cursor cursor = null;
        try {
            //查询表中的所有数据
             cursor = sqLiteDatabase.query(tableName, null, null, null, null, null, null);

        }catch (SQLiteException e){
            Log.v("msg" , e.toString());

        }
        return cursor;
    }

    /**
     * 删
     * @param tableName
     * @param id
     */
    public void deleteData(String tableName ,int  id) {
        try {
           if (id == 0){
               sqLiteDatabase.execSQL("delete from " + tableName); // 删除表中所有数据
//               sqLiteDatabase.delete(tableName,null,null);//返回删除的数量
            }else {
               sqLiteDatabase.delete(tableName, "id = ?", new String[]{String.valueOf(id)});
            }
        }catch (Exception e){}


    }
    public void deleteData(String tableName ,String apppackage) {
        try {
           if (TextUtils.isEmpty(apppackage)){
               sqLiteDatabase.execSQL("delete from " + tableName); // 删除表中所有数据
//               sqLiteDatabase.delete(tableName,null,null);//返回删除的数量
            }else {
               sqLiteDatabase.delete(tableName, "apppackage = ?", new String[]{apppackage});
            }
        }catch (Exception e){}


    }

    /**
     * 改
     * @param tableName
     * @param id
     * @param contentValues
     */
    public void updateData(String tableName ,int id,  ContentValues contentValues) {
        try{
            sqLiteDatabase.update(tableName, contentValues , "id=?", new String[]{String.valueOf(id)});
        }catch (Exception e){}

    }

    public void updateData(String tableName ,String apppackage,  ContentValues contentValues) {
        try{
            sqLiteDatabase.update(tableName, contentValues , "apppackage = ?", new String[]{String.valueOf(apppackage)});

        }catch (Exception e){}


    }
}
