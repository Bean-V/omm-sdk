package com.oortcloud.clouddisk.db;

import android.database.sqlite.SQLiteDatabase;

import com.oortcloud.clouddisk.db.curd.QuerySupport;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/7/14 14:37
 * @version： v1.0
 * @function： 数据库接口
 */
public interface IDaoSupport<T> {

    void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz);

    // 插入数据
    void insert(T t);
    // 批量插入数据
    void insert(List<T> datas);
    // 获取专门查询的支持类
    QuerySupport<T> querySupport();
    //删除数据
    int delete(String whereClause, String... whereArgs);
    //修改数据
    int update(T obj, String whereClause, String... whereArgs);

}
