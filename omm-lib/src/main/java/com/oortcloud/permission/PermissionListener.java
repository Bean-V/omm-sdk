package com.oortcloud.permission;

import java.util.List;


public interface PermissionListener {

    void onGranted();

    void onDenied(List<String> deniedPermission);

}
