package com.oortcloud.contacts.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.oortcloud.contacts.R;

/**
 * 加载中Dialog
 *
 * @author xm
 */
public class LoadingDialog {

    private Dialog mLoadingDialog;
    private Context mContext;
    private String mLoadingContent;

    public LoadingDialog(Context context) {
        this(context, null);
    }

    public LoadingDialog(Context context, String loadingContent) {
        super();
        this.mContext = context;
        this.mLoadingContent = loadingContent;
    }

    public void show() {
        mLoadingDialog = new Dialog(mContext, R.style.LoadingDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loadding, null, false);
        //显示自定义内容
        if (!TextUtils.isEmpty(mLoadingContent)) {
            TextView textView = view.findViewById(R.id.text_loading);
            textView.setText(mLoadingContent);
        }
        mLoadingDialog.setContentView(view);
        //点击外部不能取消
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
    }

    public void dismiss() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 判断对话框是否正在显示
     *
     * @return true：正在显示；false：未显示
     */
    public boolean isShowing() {
        // 先判断mLoadingDialog是否为null，避免空指针异常
        if (mLoadingDialog == null) {
            return false;
        }
        // 调用Dialog的isShowing()方法判断显示状态
        return mLoadingDialog.isShowing();
    }
}
