package com.oortcloud.clouddisk.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @filenale: PermissionManagr.java
 * @function: android6.0动态权限管理类
 * @version: 1.0
 * @author: zhangzhijun
 * @date: 2019/4/24
 */

public class PmsManager {
    //读写SD权限
    private static final int EXTERNAL_STORAGE_PERMISSION = 0x01;

    //权限
    public static String[] getPermission(){
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,
//                Manifest.permission.REQUEST_INSTALL_PACKAGES,Manifest.permission.REQUEST_DELETE_PACKAGES ,
                Manifest.permission.CALL_PHONE,Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
        };

    }
    /**
     * 判断是否拥有对应的权限
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(Context context , String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String permission : permissions){
                if (ContextCompat.checkSelfPermission(context , permission) !=
                        PackageManager.PERMISSION_GRANTED){
                    return false;
                }

            }
        }

        return true;
    }

    /**
     * 权限申请封装
     * @param activity
     * @param requestCode
     * @param permissions
     */
    public static void requestPms(Activity activity , int requestCode , String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            activity.requestPermissions(permissions , requestCode);

        }

    }

    /**
     * 权限申请后的处理结果
     * @param requestCode
     * @param grantResults
     * @param listener
     */
    public static void onRequestPmsResult(int requestCode,  int[] grantResults , PmsListener listener){
        switch (requestCode){
            case EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    listener.permissionSuccess();
                }else {

                }
                break;
        }
    }


}
