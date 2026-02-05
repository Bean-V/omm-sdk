package com.jun.baselibrary.db;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/7 0:57
 * Version 1.0
 * Description：数据库辅助类 获取类名，数据类型转换 比较查询
 */
 public class DaoUtils {
    private DaoUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    //获取类名
     public static String getTableName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static String getColumnType(String type) {
        String value = null;
        if (type.contains("String")) {
            value = " text";
        } else if (type.contains("int")) {
            value = " integer";
        } else if (type.contains("boolean")) {
            value = " boolean";
        } else if (type.contains("float")) {
            value = " float";
        } else if (type.contains("double")) {
            value = " double";
        } else if (type.contains("char")) {
            value = " varchar";
        } else if (type.contains("long")) {
            value = " long";
        }
        return value;
    }

    public static String capitalize(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.substring(0, 1).toUpperCase(Locale.US) + str.substring(1);
        }
        return str == null ? null : "";
    }
}
