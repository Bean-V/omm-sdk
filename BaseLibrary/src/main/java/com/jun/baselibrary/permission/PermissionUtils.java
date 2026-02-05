package com.jun.baselibrary.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2023/1/12 1:58
 * Version 1.0
 * Description：6.0权限处理工具类
 */
public class PermissionUtils {

    public PermissionUtils(){

    }
    //判断是否6.0版本
    public static boolean isOverMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     *  //反射执行方法
     * @param object 执行对象
     * @param requestCode 请求码
     * @param result 是否申请成功
     */
    public static void executeResultMethod(Object object, int requestCode, boolean result){
        //获取class类中的方法，遍历找到类中标记的方法，并且请求码相同
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Permission permission =  method.getAnnotation(Permission.class);
            if (permission != null){
                int methodCode = permission.requestCode();
                if (methodCode == requestCode){
                    executeMethod(object, method, result);
                }
            }
        }
    }

    //执行回调
    private static void executeMethod(Object object, Method method, boolean result) {
        try {
            //操作私有
            method.setAccessible(true);
            method.invoke(object, result);
        } catch (Exception e) {
            Log.e("TAG", "Error  ", e);
        }
    }

    /**
     * 检测权限
     * @param object  Activity or Fragment
     * @param permissions 权限
     */
    public static List<String> getDeniedPermissions(Object object, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            //判断是否为未授权限
            if (ContextCompat.checkSelfPermission(getActivity(object), permission)
                    == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }

        }
        return deniedPermissions;
    }

    /**
     * 获取Context
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Activity){
            return (Activity) object;
        }
        if (object instanceof Fragment){
            return ((Fragment) object).getActivity();
        }
        return null;
    }


}
