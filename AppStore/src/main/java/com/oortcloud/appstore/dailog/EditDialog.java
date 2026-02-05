package com.oortcloud.appstore.dailog;

import static com.oortcloud.appstore.AppStoreInit.MODNAME_LEN;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oortcloud.appstore.R;
/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/6 07:05
 */
public class EditDialog  extends Dialog {

    private Context mContext;
    /**
     * UI
     */
    private ViewGroup contentView;
    private TextView titleView;
    private EditText contentEdit;
    private TextView btnComfirm;
    private TextView btnCancle;


    public interface DialogClickListener {
        void onDialogClick();
        void onDialogClick(String moduleName);
    }

    public EditDialog(Context context,
                       InputDialog.DialogClickListener confirmClick, InputDialog.DialogClickListener cancelClick) {
        this(context , null , null ,confirmClick , cancelClick );

    }
    public EditDialog(Context context, String title, String content,
                       InputDialog.DialogClickListener confirmClick, InputDialog.DialogClickListener cancelClick) {
        super(context, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog);
        mContext = context;
        initDialogStyle(title, content, confirmClick, cancelClick);
    }


    private void initDialogStyle(String title, final String content,
                                 final InputDialog.DialogClickListener confirmClick, final InputDialog.DialogClickListener cancelClick) {
        setContentView(createDialogView(R.layout.dialog_edit_layout));
//        setParams(dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        titleView = (TextView) findChildViewById(R.id.title);
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }

        contentEdit = (EditText) findChildViewById(R.id.message);
        if (!TextUtils.isEmpty(content)) {
            contentEdit.setText(content);
            contentEdit.setSelection(content.length());
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        btnComfirm = (TextView) findChildViewById(R.id.confirm_btn);
        btnCancle = (TextView) findChildViewById(R.id.cancle_btn);

        btnCancle.setOnClickListener(view -> {

                dismiss();
                if (cancelClick != null) {
                    cancelClick.onDialogClick();
                }

        });
        btnComfirm.setOnClickListener(view -> {

                if (confirmClick != null) {
                    String contentStr =   contentEdit.getText().toString();

                    if (!TextUtils.isEmpty(contentStr)){

                        if (contentStr.length() <= MODNAME_LEN){
                            dismiss();
                            confirmClick.onDialogClick(contentEdit.getText().toString());
                        }else {//模块名称不能大于"+MODNAME_LEN+"个字符,请修改
                            Toast.makeText(mContext ,mContext.getString(R.string.module_name_lenght_not_use,String.valueOf(MODNAME_LEN)) ,Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(mContext , mContext.getString(R.string.please_input_module_name) ,Toast.LENGTH_SHORT).show();
                    }

                }

        });
    }
    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams(int width, int height) {
        WindowManager.LayoutParams dialogParams = this.getWindow().getAttributes();
        dialogParams.width = width;
        dialogParams.height = height;
        this.getWindow().setAttributes(dialogParams);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }

}
