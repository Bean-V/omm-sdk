package com.jun.baselibrary.db;

import android.database.sqlite.SQLiteDatabase;

import com.jun.baselibrary.db.curd.QuerySupport;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 22:59
 * Version 1.0
 * Description：数据库接口--增删改查
 */
public interface DAO<T> {

    void init(SQLiteDatabase db, Class<T> clazz);
    //插入数据
    long insert(T t);
    //批量 插入
    void insert(List<T> data);
    //查询 所以
//    List<T> query();
    QuerySupport<T> query();
    //按条件查询
//    void query(String...args);

    int update(T obj, String whereClause, String... whereArgs);

    int delete(String whereClause, String... whereArgs);

}
