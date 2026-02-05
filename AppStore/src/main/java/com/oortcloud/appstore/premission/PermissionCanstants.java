package com.oortcloud.appstore.premission;

import android.Manifest;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/18 17:40
 */
public class PermissionCanstants {

    public static String[] getPermission(){
      return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.REQUEST_INSTALL_PACKAGES,Manifest.permission.REQUEST_DELETE_PACKAGES ,
              Manifest.permission.SYSTEM_ALERT_WINDOW

               };
//
    }

}
