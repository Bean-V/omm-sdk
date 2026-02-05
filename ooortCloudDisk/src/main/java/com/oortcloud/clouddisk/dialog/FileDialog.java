package com.oortcloud.clouddisk.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oort.imagepicksdk.ImagePickSdk;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ScreenUtil;
import com.oortcloud.clouddisk.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/30 15:17
 * @version： v1.0
 * @function： 创建文件夹或上传文件 dialog
 */
public class FileDialog extends Dialog {
    /**
     * UI
     */
    private ViewGroup contentView;
    private Context mContext;
    private LinearLayout mUploadLL;
    private LinearLayout mCreateFile;
    private String mDir;
    public FileDialog(Context context , String dir) {
        super(context, R.style.DialogStyles);
        mContext = context;
        mDir = dir;
        initDialogStyle();


    }

    private void initDialogStyle() {
        setContentView(createDialogView(R.layout.dialog_create_upload_layout));
        setParams();

        mUploadLL = (LinearLayout) findChildViewById(R.id.upload_ll);
        mCreateFile = (LinearLayout) findChildViewById(R.id.create_mkdir_ll);

        mUploadLL.setOnClickListener(v -> {
            int ACTION_REQUEST_PERMISSIONS = 0x001;
            String[] NEEDED_PERMISSIONS = new String[]{

                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE
            };
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
                return;
            }
            ImagePickSdk.imagePick(mContext, new ImagePickConfig(), new ImagePickSdk.ImagePickFinish() {
                @Override
                public void imagePickFinsh(int code, List uris, List<String> paths) {


                    ArrayList files = new ArrayList();

                    for(Object o : paths) {
                        String s = (String) o;
                        // Uri uri = Uri.parse(s);
                        if(new File(s).exists()){
                            files.add(new File(s));
                        }else{
                            Uri uri = Uri.parse(s);
                            files.add(new File(uri.getPath()));
                        }

                    }

                    TransferHelper.uploadFile(mDir , files);
                }
            });

            dismiss();










//            Intent intent = new Intent(mContext , FileUploadActivity.class);
//            intent.putExtra("DIR" , mDir);
//            mContext.startActivity(intent);
//            dismiss();
        });
        mCreateFile.setOnClickListener(v -> {

            new CreateFileDialog(mContext, (CreateFileDialog dialog ,String fileName) ->{
                    if (!TextUtils.isEmpty(fileName)){
                        HttpResult.mkdir(mDir , fileName);
                        dialog.dismiss();
                    }else {
                        ToastUtils.showContent("请输入文件夹名称");
                    }



                } , null).show();
                dismiss();
        });

    }

    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(mContext, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams() {
        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width =  (ScreenUtil.getScreenWidth(getContext()) * 1);
        lp.gravity = Gravity.BOTTOM;
        o.setAttributes(lp);
//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }


}