package com.jun.baselibrary.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.jun.baselibrary.db.curd.QuerySupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 23:11
 * Version 1.0
 * Description：数据库接口实现--增删改查
 */
public final class DAOImpl<T> implements DAO<T> {
    private SQLiteDatabase mSQLiteDatabase;
    //泛型需要处理类
    private Class<T> mClazz;
    //插入参数封装
    private static final Object[] mPutMethodArgs = new Object[2];
    //缓存反射获取到的put方法
    private static final Map<String, Method> mPutMethods
            = new ArrayMap<>();

    @Override
    public void init(SQLiteDatabase db, Class<T> clazz) {
        mSQLiteDatabase = db;
        mClazz = clazz;
        //创建表语句
     /*"create table if not exists Person ("
                + "id integer primary key autoincrement, "
                + "name text, "
                + "age integer, "
                + "flag boolean)";*/

        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ")
                .append(DaoUtils.getTableName(mClazz))//获取类名
                .append("(id integer primary key autoincrement,");
        //反射获取属性
        Field[] fields = mClazz.getDeclaredFields();
        //拼接表语句
        for (Field field : fields) {
            field.setAccessible(true);//设置权限
            String fieldName = field.getName();//获取属性名
            //属性类型转换
            String fieldType = DaoUtils.getColumnType(field.getType().getSimpleName());
            sb.append(fieldName).append(fieldType).append(",");
        }
        //创建表语句
        String table =
                sb.replace(sb.length() - 2, sb.length(), ")").toString();
        Log.v("TAG", "create table语句--->" + table);
        mSQLiteDatabase.execSQL(table);
    }

    @Override
    public long insert(T obj) {
        //Android原生插入方式
        ContentValues values = contentValuesByObj(obj);
        return mSQLiteDatabase.insert(DaoUtils.getTableName(mClazz), null, values);
    }

    @Override
    public void insert(List<T> data) {
        //开启事物处理，提高效率
        mSQLiteDatabase.beginTransaction();
        for (T obj : data) {
             insert(obj);
        }
        mSQLiteDatabase.setTransactionSuccessful();
        mSQLiteDatabase.endTransaction();
    }

    /**
     * 封装ContentValues
     *
     * @param obj
     * @return
     */
    private ContentValues contentValuesByObj(T obj) {
        ContentValues values = new ContentValues();
        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                //获取属性名
                String key = field.getName();
                //获取对象属性值
                Object value = field.get(obj);
                mPutMethodArgs[0] = key;
                mPutMethodArgs[1] = value;

                String fieldTypeName = field.getType().getName();

                Method putMethod = mPutMethods.get(fieldTypeName);
                if (putMethod == null) {
                    //ContentValues在put时需要指定数据类型，正常处理value需要判断强制,
                    // 所以通过反射处理
                    assert value != null;
                    putMethod = values.getClass()
                            .getDeclaredMethod("put", String.class, value.getClass());
                    mPutMethods.put(fieldTypeName, putMethod);

                }
                //反射执行put方法
//                putMethod.invoke(values, key, value);
                putMethod.invoke(values, mPutMethodArgs);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mPutMethodArgs[0] = null;
                mPutMethodArgs[1] = null;
            }
        }
        return values;
    }

    /**
     * 查询
     * @return
     */
    @Override
    public QuerySupport<T> query() {
        return new QuerySupport(mSQLiteDatabase, mClazz);
    }

    /**
     * 修改
     * @param obj
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public int update(T obj, String whereClause, String... whereArgs) {

        ContentValues values = contentValuesByObj(obj);
        return mSQLiteDatabase.update(DaoUtils.getTableName(mClazz),
                values, whereClause, whereArgs);
    }

    /**
     * 删除
     * @param whereClause
     * @param whereArgs
     * @return
     */
    @Override
    public int delete(String whereClause, String... whereArgs) {

        return mSQLiteDatabase.delete(DaoUtils.getTableName(mClazz), whereClause, whereArgs);
    }
}
