package com.oortcloud.clouddisk.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;
import android.util.Log;


import com.oortcloud.clouddisk.db.curd.QuerySupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date:  2021/1/12 14:47
 * @version： v1.0
 * @function： 操作数据库 增删改查
 */
public class DaoSupport<T> implements IDaoSupport<T>{
    // SQLiteDatabase
    private SQLiteDatabase mSqLiteDatabase;
    // 泛型类
    private Class<T> mClazz;

    private static final Object[] mPutMethodArgs = new Object[2];

    private static final Map<String, Method> mPutMethods = new ArrayMap<>();
    @Override
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz) {
        this.mSqLiteDatabase =  sqLiteDatabase;
        this.mClazz = clazz;

        StringBuffer sb = new StringBuffer();

        sb.append("create table if not exists ")
                .append(DaoUtil.getTableName(mClazz))
                .append("(id integer primary key autoincrement, ");
        //反射获取所以属性
        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            //属性名称
            String name = field.getName();
            //属性类型
            String type =  field.getType().getSimpleName();
           // type进行转换 int --> integer, String text;
            sb.append(name).append(DaoUtil.getColumnType(type)).append(", ");
        }
        sb.replace(sb.length() -2 , sb.length() , ")");

        String createTableSql = sb.toString();
        // 创建表
        mSqLiteDatabase.execSQL(createTableSql);

    }

    @Override
    public void insert(T obj) {
       ContentValues values = contentValuesByObj(obj);

       mSqLiteDatabase.insert(obj.getClass().getSimpleName(), null ,values);
    }

    @Override
    public void insert(List<T> datas) {
        //增加事务优化性能
        mSqLiteDatabase.beginTransaction();
        for (T data : datas) {
            insert(data);
        }
        mSqLiteDatabase.setTransactionSuccessful();
        mSqLiteDatabase.endTransaction();
    }
    private ContentValues contentValuesByObj(T obj){
        ContentValues values = new ContentValues();

       Field[] fields = mClazz.getDeclaredFields();
       try {
           for (Field field : fields) {
               field.setAccessible(true);

               String key = field.getName();
               Object value = field.get(obj);

               mPutMethodArgs[0] = key;
               mPutMethodArgs[1] = value;

               String filedTypeName  =  field.getType().getName();
               Method putMethod = mPutMethods.get(filedTypeName);
               if (putMethod == null){
                   putMethod = values.getClass().getDeclaredMethod("put", key.getClass() , value.getClass());
                   mPutMethods.put(filedTypeName, putMethod);
               }
               putMethod.invoke(values , key , value);
           }
       }catch (Exception e){
            Log.v("msg" , e.toString());
       }

        return values;
    }

    private QuerySupport<T> mQuerySupport;
    @Override
    public QuerySupport<T> querySupport() {
        if (mQuerySupport == null){
            mQuerySupport = new QuerySupport<>(mSqLiteDatabase , mClazz);
        }
        return mQuerySupport;
    }

    private List<T> cursorToList(Cursor cursor) {
        List<T> list = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()){
            do {
                try {
                    T instance =  mClazz.newInstance();
                    Field[] fields =  mClazz.getDeclaredFields();

                    for (Field field : fields) {
                        field.setAccessible(true);
                       String name  =  field.getName();
                       int index = cursor.getColumnIndex(name);

                       if (index == -1){
                           continue;
                       }
                       Method cursorMethod = cursorToMethod(field.getType());
                        if (cursorMethod != null) {
                            Object value = cursorMethod.invoke(cursor, index);
                            if (value == null) {
                                continue;
                            }

                            // 处理一些特殊的部分
                            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                                if ("0".equals(String.valueOf(value))) {
                                    value = false;
                                } else if ("1".equals(String.valueOf(value))) {
                                    value = true;
                                }
                            } else if (field.getType() == char.class || field.getType() == Character.class) {
                                value = ((String) value).charAt(0);
                            } else if (field.getType() == Date.class) {
                                long date = (Long) value;
                                if (date <= 0) {
                                    value = null;
                                } else {
                                    value = new Date(date);
                                }
                            }

                            field.set(instance , value);
                        }
                      list.add(instance);
                    }
                }catch (Exception e){

                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private Method cursorToMethod(Class<?> fieldType) throws Exception{
      String  methodName =   getColumnMethodName(fieldType);
      Method method =  Cursor.class.getDeclaredMethod(methodName ,int.class);
        return method;
    }

    private String getColumnMethodName(Class<?> fieldType) {
        String typeName;
        if (fieldType.isPrimitive()){
            typeName = DaoUtil.capitalize(fieldType.getName());
        }else {
            typeName = fieldType.getSimpleName();
        }

        String methodName = "get" + typeName;
        if ("getBoolean".equals(methodName)) {
            methodName = "getBlob";
//            methodName = "getInt";
        } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getDate".equals(methodName)) {
            methodName = "getLong";
        } else if ("getInteger".equals(methodName)) {
            methodName = "getInt";
        }
        return methodName;
    }

    @Override
    public int delete(String whereClause, String... whereArgs) {
        return mSqLiteDatabase.delete(DaoUtil.getTableName(mClazz), whereClause, whereArgs);
    }

    @Override
    public int update(T obj, String whereClause, String... whereArgs) {
        ContentValues values = contentValuesByObj(obj);
        return mSqLiteDatabase.update(DaoUtil.getTableName(mClazz),
                values, whereClause, whereArgs);
    }
}
