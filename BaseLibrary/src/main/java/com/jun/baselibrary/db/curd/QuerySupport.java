package com.jun.baselibrary.db.curd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jun.baselibrary.db.DaoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/7 20:26
 * Version 1.0
 * Description：SQL查询支持类
 */
public final class QuerySupport<T> {
    // 查询的列
    private String[] mQueryColumns;
    // 查询的条件
    private String mQuerySelection;
    // 查询的参数
    private String[] mQuerySelectionArgs;
    // 查询分组
    private String mQueryGroupBy;
    // 查询对结果集进行过滤
    private String mQueryHaving;
    // 查询排序
    private String mQueryOrderBy;
    // 查询可用于分页
    private String mQueryLimit;

    private Class<T> mClass;
    private SQLiteDatabase mSQLiteDatabase;

    public QuerySupport(SQLiteDatabase sqLiteDatabase, Class<T> clazz){
        mSQLiteDatabase = sqLiteDatabase;
        mClass = clazz;
    }

    public QuerySupport columns(String... columns) {
        this.mQueryColumns = columns;
        return this;
    }

    public QuerySupport selectionArgs(String... selectionArgs) {
        this.mQuerySelectionArgs = selectionArgs;
        return this;
    }

    public QuerySupport having(String having) {
        this.mQueryHaving = having;
        return this;
    }

    public QuerySupport orderBy(String orderBy) {
        this.mQueryOrderBy = orderBy;
        return this;
    }

    public QuerySupport limit(String limit) {
        this.mQueryLimit = limit;
        return this;
    }

    public QuerySupport groupBy(String groupBy) {
        this.mQueryGroupBy = groupBy;
        return this;
    }

    public QuerySupport selection(String selection) {
        this.mQuerySelection = selection;
        return this;
    }

    public List<T> query() {
        //(String table, String[] columns, String selection,
        //            String[] selectionArgs, String groupBy, String having,
        //            String orderBy)
        Cursor cursor = mSQLiteDatabase.query(DaoUtils.getTableName(mClass), mQueryColumns, mQuerySelection,
                mQuerySelectionArgs, mQueryGroupBy, mQueryHaving, mQueryOrderBy, mQueryLimit);
        clearQueryParams();
        return cursorToList(cursor);
    }
    public List<T> queryAll() {
        //(String table, String[] columns, String selection,
        //            String[] selectionArgs, String groupBy, String having,
        //            String orderBy)
        Cursor cursor = mSQLiteDatabase.query(DaoUtils.getTableName(mClass), null, null,
                null, null, null, null, null);
        return cursorToList(cursor);
    }

    //Cursor对象数据转换为类对象
    private List<T> cursorToList(Cursor cursor) {
        List<T> list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    //需要保留空参构造函数
                    T instance = mClass.newInstance();
                    Field[] fields = mClass.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        //获取角标位置 - 列
                        int index = cursor.getColumnIndex(field.getName());
                        if (index == -1) {
                            continue;
                        }
                        //反射获取方法
                        Class fieldTypeClass = field.getType();
                        Method cursorMethod = cursorMethod(fieldTypeClass);
                        if (cursorMethod != null){
                            Object value = cursorMethod.invoke(cursor, index);
                            if (value == null){
                                continue;
                            }
                            //处理特殊部分
                            if (fieldTypeClass == boolean.class || fieldTypeClass == Boolean.class){
                                if ("0".equals(String.valueOf(value))){
                                    value = false;
                                }else if ("1".equals(String.valueOf(value))){
                                    value = true;
                                }
                            }else if (fieldTypeClass == char.class || fieldTypeClass == Character.class){
                                value = ((String)value).charAt(0);
                            }else if (fieldTypeClass == Date.class){
                                long date = (long) value;
                                if (date < 0){
                                    value = null;
                                }else {
                                    value = new Date(date);
                                }
                            }

                            //反射对象赋值
                            field.set(instance, value);
                        }

                    }
                    //加入集合
                    list.add(instance);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

        }
        cursor.close();
        return list;
    }

    private Method cursorMethod(Class<?> fieldType) throws Exception {
        String methodName = getColumnMethodName(fieldType);
        Method method = Cursor.class.getDeclaredMethod(methodName, int.class);
        return method;
    }

    private String getColumnMethodName(Class<?> fieldType) {
        String typeName;
        //是否时基本类型
        if (fieldType.isPrimitive()){
            typeName = DaoUtils.capitalize(fieldType.getName());
        }else {
            typeName = fieldType.getSimpleName();
        }
        String methodName = "get" + typeName;

        if ("getBoolean".equals(methodName) || "getInteger".equals(methodName)){
            methodName = "getInt";
        }else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getDate".equals(methodName)) {
            methodName = "getLong";
        }
        return methodName;
    }
    /**
     * 清空参数
     */
    private void clearQueryParams() {
        mQueryColumns = null;
        mQuerySelection = null;
        mQuerySelectionArgs = null;
        mQueryGroupBy = null;
        mQueryHaving = null;
        mQueryOrderBy = null;
        mQueryLimit = null;
    }


}
