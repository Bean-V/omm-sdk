package com.oort.weichat.ui.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.oort.weichat.R;
import com.oort.weichat.ui.dialog.base.BaseDialog;
import com.oort.weichat.util.PermissionUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/21.
 * 创建课程的提示框
 */


public class DialogRequestPermission extends BaseDialog {
    public static int per_type_all = 0;
    public static int per_type_album = 1;
    public static int per_type_camera = 2;
    public static int per_type_record = 3;
    public static int per_type_position = 4;
    public static int per_type_msg = 5;

    public static int per_type_album_camera = 6;
    public static int per_type_all_file = 7;

    private Button mCommitBtn;

    private int per_type;

    private final Map<String, Integer> permissionsMap = new LinkedHashMap<>();
//        permissionsMap.put(Manifest.permission.READ_PHONE_STATE, R.string.permission_phone_status);
//    // 照相
//        permissionsMap.put(Manifest.permission.CAMERA, R.string.permission_photo);
//    // 麦克风
//        permissionsMap.put(Manifest.permission.RECORD_AUDIO, R.string.permission_microphone);
//    // 存储权限
//        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permission_storage);
//        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_storage);
//
//        permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_location);
//        permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_location);



    public DialogRequestPermission(Activity activity, int type) {
        per_type = type;


        int [] rid = {R.layout.dialog_permission_all,
                R.layout.dialog_permission_ablum,
                R.layout.dialog_permission_camera,
                R.layout.dialog_permission_record,
                R.layout.dialog_permission_position,
                R.layout.dialog_permission_msg,
                R.layout.dialog_permission_more,
                R.layout.dialog_permission_all_file};

       // int [] rid = {R.layout.dialog_permission_all,R.layout.dialog_permission_ablum,R.layout.dialog_permission_camera,R.layout.dialog_permission_record,R.layout.dialog_permission_position,R.layout.dialog_permission_msg};


        RID = rid[per_type];
        mActivity = activity;
        initView();
    }

    protected void initView() {
        super.initView();
        mCommitBtn = (Button) mView.findViewById(R.id.btn_on);

        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(per_type == per_type_all){

                    String [] pers = {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO};


                    PermissionUtil.requestPermissions(mActivity,100,pers);
                }

                if(per_type == per_type_camera || per_type == per_type_album_camera){

                    String [] pers = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

                    PermissionUtil.requestPermissions(mActivity,101,pers);
                }

                if(per_type == per_type_album){
                    String [] pers = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

                    PermissionUtil.requestPermissions(mActivity,102,pers);
                }

                if(per_type == per_type_position){
                    String [] pers = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

                    PermissionUtil.requestPermissions(mActivity,103,pers);
                }


                if(per_type == per_type_record){
                    String [] pers = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};

                    PermissionUtil.requestPermissions(mActivity,104,pers);
                }
                if(per_type == per_type_all_file){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    mActivity.startActivity(intent);
                }
                dismiss();
            }
        });

        mView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    @Override
    public BaseDialog show() {
        return super.show();
    }
    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


}
