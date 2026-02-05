package com.oortcloud.contacts.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.oortcloud.contacts.R;
import com.oortcloud.contacts.utils.ScreenUtil;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/14 17:30
 * @version： v1.0
 * @function： 显示信息dialog/通用对话框
 */
public class OrdCommonDialog extends Dialog {
    /**
     * UI
     */
    private ViewGroup contentView;
    private TextView mContent;
    private TextView mTitle;
    private TextView mCancelTV;
    private TextView mConfirmTV;
    private Context mContext;
    private DialogClickListener cancelClick;
    private DialogClickListener confirmClick;

    public interface DialogClickListener {
         void onDialogClick();
    }

    public OrdCommonDialog(Context context) {
        super(context, R.style.DialogStyles);
        mContext = context;
        initDialogStyle();

    }

    private void initDialogStyle() {
        setContentView(createDialogView(R.layout.dialog_ord_common_layout));
        setParams(0.8f);
        mCancelTV = (TextView) findChildViewById(R.id.cancel_ord_btn);
        mConfirmTV = (TextView)findChildViewById(R.id.confirm_ord_btn);
        mCancelTV.setOnClickListener(v ->  {

            dismiss();
            if (cancelClick != null) {
                cancelClick.onDialogClick();
            }

        });
        mConfirmTV.setOnClickListener(v ->  {

            dismiss();
            if (confirmClick != null) {
                confirmClick.onDialogClick();
            }

        });
    }
    public OrdCommonDialog setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitle.setText(title);
        }
        return this;
    }
    public OrdCommonDialog setConfirmClick(DialogClickListener confirmClick){
        this.confirmClick = confirmClick;
        return this;
    }
    public OrdCommonDialog setCancelClick(DialogClickListener cancelClick){
        this.cancelClick = cancelClick;
        return this;
    }
    public OrdCommonDialog setContent(String content){
        if (!TextUtils.isEmpty(content)){
            mContent.setText(content);
        }
        return this;
    }
    private ViewGroup createDialogView(int layoutId) {
        contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutId, null);
        return contentView;
    }

    public void setParams(float ratio) {
        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * ratio);
        lp.gravity = Gravity.CENTER;
        o.setAttributes(lp);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public View findChildViewById(int id) {
        return contentView.findViewById(id);
    }

}
