package com.oortcloud.clouddisk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.CopyActivity;
import com.oortcloud.clouddisk.bean.FileInfo;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.transfer.TransferHelper;
import com.oortcloud.clouddisk.utils.ScreenUtil;
import com.oortcloud.clouddisk.utils.ToastUtils;
import com.oortcloud.clouddisk.utils.helper.ImgHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/30 11:41
 * @version： v1.0
 * @function：文件设置dialog
 */
public class FileSettingDialog extends Dialog {
    /**
     * UI
     */
    private ViewGroup contentView;
    private Context mContext;
    private FileInfo mFileInfo;

    private LinearLayout mDownloadll;
    private ImageView mDownloadImg;
    private TextView mDownloadTV;
    private LinearLayout mEditll;
    private LinearLayout mCopyll;
    private LinearLayout mDeletell;

    private ImageView mTypeImg;
    private TextView mFileNameTV;

    //文件后缀
    private String suffix = "";
    public interface DialogClickListener {
        void onDialogClick();
    }

    public FileSettingDialog(Context context, FileInfo fileInfo) {
        super(context, R.style.DialogStyles);
        mContext = context;
        mFileInfo = fileInfo;
        initDialogStyle();

    }

    private void initDialogStyle() {
        setContentView(createDialogView(R.layout.dialog_file_seting_layout));
        setParams();

        mDownloadll = (LinearLayout) findChildViewById(R.id.download_ll);
        mEditll = (LinearLayout) findChildViewById(R.id.edit_ll);
        mCopyll = (LinearLayout) findChildViewById(R.id.copy_ll);
        mDeletell = (LinearLayout) findChildViewById(R.id.delete_ll);
        mDownloadImg = (ImageView) findChildViewById(R.id.download_img);
        mDownloadTV = (TextView) findChildViewById(R.id.download_tv);
        mTypeImg = (ImageView) findChildViewById(R.id.img_type);
        mFileNameTV = (TextView) findChildViewById(R.id.file_name);
        mFileNameTV.setText(mFileInfo.getName());

        ImgHelper.setImageResource(mFileInfo, mTypeImg);

        //下载
        if (mFileInfo.getIs_dir() == 1) {
            mDownloadll.setAlpha(0.3f);

        } else {
            mDownloadll.setOnClickListener(v -> {

                TransferHelper.startDownload(mFileInfo);
                dismiss();
            });
        }
        //重命名
        mEditll.setOnClickListener(v -> {
            newName();
            dismiss();

        });
        //copy move
        mCopyll.setOnClickListener(v -> {
             List<FileInfo> fileInfoData = new ArrayList<>();
            fileInfoData.add(mFileInfo);
            CopyActivity.actionStart(mContext ,fileInfoData);
            dismiss();
        });
        //删除
        mDeletell.setOnClickListener(v -> {
            delete();
            dismiss();
        });


    }
    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams() {
        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 1);
        lp.gravity = Gravity.BOTTOM;
        o.setAttributes(lp);
        //不可以取消
        //setCancelable(false);
        //setCanceledOnTouchOutside(false);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }


    private void  newName(){
        String name ;

        if (mFileInfo.getName().contains(".")){
            name = mFileInfo.getName().substring(0 ,mFileInfo.getName().lastIndexOf("."));
            suffix = mFileInfo.getName().substring(mFileInfo.getName().lastIndexOf("."));
        }else {
            name = mFileInfo.getName();
        }

        new CreateFileDialog(mContext, (CreateFileDialog dialog , String fileName) ->{

            if (!TextUtils.isEmpty(fileName)){
                if (name.equals(fileName)){
                    ToastUtils.showContent("请修改文件名称");
                }else {
                    HttpResult.newName(mFileInfo.getDir() , mFileInfo.getName() , fileName + suffix);
                    dialog.dismiss();
                }
            }else {
                ToastUtils.showContent("请输入文件名称");
            }


        } , null)
                .setTitle("重命名文件").setFileName(name).show();

    }

    private void  delete(){
        new CommonDialog(mContext).setTitle("删除文件").setContent("确定将这个文件删除?")
                .setConfirmClick(() ->{
                    List<FileInfo> fileInfoData = new ArrayList<>();
                    fileInfoData.add(mFileInfo);
                    HttpResult.delete(true ,fileInfoData);

                }).show();
    }

}
