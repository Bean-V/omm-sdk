package com.oortcloud.contacts.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.Window;

import com.oortcloud.contacts.R;


/**
 * Created by Administrator on 2016/5/3.
 */
public abstract class BaseDialog {
    protected View mView;
    protected Activity mActivity;
    protected int RID;
    protected AlertDialog mDialog;
    protected boolean mCancelAble = true;

    protected void initView() {
        if (RID != 0)
            mView = mActivity.getLayoutInflater().inflate(RID, null);
    }

    public BaseDialog show() {
        mDialog = new AlertDialog.Builder(mActivity).setView(mView).create();
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.loading_dialog_bg);
        mDialog.setCancelable(mCancelAble);//点击空白的地方取消
        mDialog.show();
        return this;
    }

    public <T> T $(int rid) {
        return (T) mView.findViewById(rid);
    }

    public String getString(int str) {
        return mActivity.getString(str);
    }
}
