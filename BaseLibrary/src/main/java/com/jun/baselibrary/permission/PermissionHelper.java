package com.jun.baselibrary.permission;

import android.app.Activity;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2023/1/11 18:43
 * Version 1.0
 * Description：Android 6.0动态权限帮助类 注解+反射方式
 */
public class PermissionHelper {
    //参数
    private Object mObject;
    //申请权限
    private String[] mPermissions;
    //请求码
    private int mRequestCode;

    private PermissionHelper(Object object){
        mObject = object;
    }
    //2.参数传入 activity or fragment
    //2.1、静态方式，直接带入参数
    private static void requestPermission(Activity activity, String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
    //2.2、链式调用
    public static PermissionHelper with(Activity activity){

        return new PermissionHelper(activity);
    }
    public static PermissionHelper with(Fragment fragment){

        return new PermissionHelper(fragment);
    }

    //申请权限
    public  PermissionHelper permissions(String... permissions){
        mPermissions = permissions;
        return this;
    }
    //申请权限请求码
    public  PermissionHelper requestCode(int requestCode){
        mRequestCode = requestCode;
        return this;
    }
    //权限请求
    public void request(){
        //判断版本
        if (!PermissionUtils.isOverMarshmallow()){
            //6.0以下直接执行
            PermissionUtils.executeResultMethod(mObject, mRequestCode, true);
            return;
        }
        //6.0以上
        //检测是否有授权
        List<String> deniedPermissions = PermissionUtils
                .getDeniedPermissions(mObject, mPermissions);

        if (deniedPermissions.isEmpty()){
            //已经授权，直接执行
            PermissionUtils.executeResultMethod(mObject, mRequestCode, true);
        }else {
            //申请未授权限
            ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject)
                    , deniedPermissions.toArray(new String[deniedPermissions.size()])
                    , mRequestCode);
        }

    }
    /**
     * 申请权限回调
     * @param object
     * @param permissions
     */
    public static void requestPermissionResult(Object object, int requestCode, String... permissions) {
        //再次检测是否有授权
        List<String> deniedPermissions = PermissionUtils
                .getDeniedPermissions(object, permissions);
        boolean result;
        if (deniedPermissions.size() == 0){
            result = true;
        }else {
            result = false;
        }
        PermissionUtils.executeResultMethod(object, requestCode, result);
    }
}
