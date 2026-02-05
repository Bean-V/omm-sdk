package com.oortcloud.appstore.dailog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oortcloud.appstore.R;


/**
 * Created by Admin on 2019/4/10.
 *
 * @author zhangzhijun
 * @version 1.0
 * @date 2019/4/9
 * @function 显示信息dialog/通用对话框
 */

public class CommonDialog extends Dialog {
    /**
     * UI
     */
    private ViewGroup contentView;
    private TextView titleView;
    private TextView contentTextView;
    private TextView mConfrim;
    private TextView mCancel;

    public interface DialogClickListener {
        public void onDialogClick();
    }

    public CommonDialog( Context context,
                         DialogClickListener confirmClick, DialogClickListener cancelClick) {
        this(context , null , null ,confirmClick , cancelClick );

    }
    public CommonDialog(Context context, String title, String content,
                        DialogClickListener confirmClick, DialogClickListener cancelClick) {
        //super();
        super(context, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog);
//        DialogLoader.getInstance().showConfirmDialog(context, content, context.getString(R.string.dialog_ok), new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                if(confirmClick != null){
//                    confirmClick.onDialogClick();
//                }
//                ;
//            }
//        },context.getString(R.string.cancel));
        initDialogStyle(title, content, confirmClick, cancelClick);
    }


    private void initDialogStyle(String title, String content,
            final DialogClickListener confirmClick, final DialogClickListener cancelClick) {
        setContentView(createDialogView(R.layout.dialog_common_layout_dk));
//        setParams(dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout layout1 = (RelativeLayout)findChildViewById(R.id.all_layout);
        if (!TextUtils.isEmpty(content)){
            contentTextView = (TextView) findChildViewById(R.id.content_tv);
            contentTextView.setText(content);
        }

        titleView = (TextView) findChildViewById(R.id.title);

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }else {
            titleView.setText(getContext().getString(R.string.tip));
        }

            layout1.setVisibility(View.VISIBLE);

        mConfrim = (TextView) findChildViewById(R.id.confirm_btn);
        mCancel = (TextView) findChildViewById(R.id.cancel_btn);

        mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (cancelClick != null) {
                        cancelClick.onDialogClick();
                    }
                }
            });
        mConfrim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (confirmClick != null) {
                        confirmClick.onDialogClick();
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
