package com.oortcloud.appstore.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.basemodule.CommonApplication;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/3 16:47
 */
public class AppManagerActivity extends FragmentActivity  {
    private Context mContext;

    private  Boolean CanShowFloat;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(AppStatu.getInstance().appStatu == 0){
//            AppStatu.getInstance().appStatu = 1;
//        }else{
//            return;
//        }







        Log.e("ceshi" ,"" + AppManager.mActivity);
        CommonApplication.pIsTab = false;
        mContext = this;



        int ACTION_REQUEST_PERMISSIONS = 0x001;
        String[] NEEDED_PERMISSIONS = new String[]{

                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,



        };

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(intent);
//                return;
//            }
//        }



        if(AppStatu.getInstance().progressStyle == 0) {
            //requestOverlayPermission();
        }
        startService();


    }




    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }


    private static final int REQUEST_OVERLAY = 5004;

    private void  startService(){
        if (getIntent() != null) {
            String packageName = getIntent().getStringExtra("packageName");
            String params = getIntent().getStringExtra("params");
            Intent intent = new Intent(this , AppManagerService.class);
            intent.putExtra("packageName" , packageName);
            intent.putExtra("params" , params);
            startService(intent);
            finish();

        }
    }

    // 动态请求悬浮窗权限
    private void requestOverlayPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!Settings.canDrawOverlays(this))
            {
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                this.startActivity(intent);
            }else {

            }

        }
    }
}
