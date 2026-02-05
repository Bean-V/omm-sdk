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
import android.widget.EditText;
import android.widget.TextView;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.utils.ScreenUtil;
import com.oortcloud.clouddisk.utils.ToastUtils;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/15 18:23
 * @version： v1.0
 * @function：创建 修改 文件夹/文件名称
 */
public class CreateFileDialog extends Dialog {

    /**
     * UI
     */
    private ViewGroup contentView;
    private EditText mTxtFrame;
    private TextView mTitle;
    private TextView mConfirmTV;
    private TextView mCancelTV;
    private Context mContext;


    public CreateFileDialog(Context context, DialogClickListener confirmClick, DialogClickListener cancelClick) {
        super(context, R.style.DialogStyles);
        mContext = context;

        initDialogStyle( confirmClick,  cancelClick);

    }

    private void initDialogStyle(DialogClickListener confirmClick , DialogClickListener cancelClick) {
        setContentView(createDialogView(R.layout.dialog_file_layout));
        setParams(0,0);

        mTxtFrame = (EditText) findChildViewById(R.id.file_name);
        mTitle = (TextView) findChildViewById(R.id.title);

        mConfirmTV = (TextView) findChildViewById(R.id.confirm_btn);
        mCancelTV = (TextView) findChildViewById(R.id.cancel_btn);

        mCancelTV.setOnClickListener(v ->  {

            dismiss();
            if (cancelClick != null) {
                cancelClick.onDialogClick(this , "");
            }

        });
        mConfirmTV.setOnClickListener(v ->  {

            if (confirmClick != null) {
                String fileName = mTxtFrame.getText().toString().trim();
                confirmClick.onDialogClick(this , fileName);

            }

        });


    }

    public CreateFileDialog setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitle.setText(title);
        }
        return this;
    }

    public CreateFileDialog setFileName(String fileName){
        if (!TextUtils.isEmpty(fileName)){
            mTxtFrame.setText(fileName);
        }
        return this;
    }
    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams(int width, int height) {
        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.9);
        lp.gravity = Gravity.CENTER;
        o.setAttributes(lp);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }

    public interface DialogClickListener {
        void onDialogClick(CreateFileDialog dialog  ,String content);
    }
}
