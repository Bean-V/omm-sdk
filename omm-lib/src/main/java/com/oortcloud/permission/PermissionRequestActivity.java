package com.oortcloud.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oort.weichat.R;

import java.util.ArrayList;
import java.util.List;


public class PermissionRequestActivity extends AppCompatActivity {

    private static PermissionListener listener;

    public static void startActivityForResult(Activity activity, int requestCode, String[] permissions, PermissionListener listener1) {
        listener = listener1;
        Intent intent = new Intent(activity, PermissionRequestActivity.class);
        intent.putExtra("permissions", permissions);
//        activity.startActivityForResult(intent, requestCode);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
        activity.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 26)
            setTheme(R.style.NotTransparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        requestRuntimePermission(getIntent().getStringArrayExtra("permissions"), listener);
    }


    PermissionListener mListener;

    public void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            mListener.onGranted();
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                mListener.onGranted();
            } else {
                mListener.onDenied(deniedPermissions);
            }
        }
        finish();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

}
