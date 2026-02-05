package com.oortcloud.clouddisk.dialog;

/**
 * @ProjectName:
 * @FileName: LoadingDialog.java
 * @Function:
 * @Author: zzj / @CreateDate: 20/03/23 16:30
 * @Version: 1.0
 */


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.oortcloud.clouddisk.R;


/**
 * 加载中Dialog
 *
 * @author xm
 */
public class LoadingDialog  extends  Dialog{
    private String message;
    private boolean canCancel;
    private TextView textView;

    public LoadingDialog(Context context) {
        this(context , null);
    }
    public LoadingDialog(Context context, String message) {
        this(context, message, true);
    }

    public LoadingDialog(Context context, String message, boolean canCancel) {
        super(context, R.style.LoadProgressDialog);
        this.message = message;
        this.canCancel = canCancel;
    }

    public void setMessage(String message) {
        this.message = message;
        handler.sendEmptyMessage(0);
    }

    @SuppressLint("HandlerLeak")
    private
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                textView.setText(message);
            }
        }
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_load_layout);
        textView = findViewById(R.id.tv_message);
//        setCancelable(canCancel);
        setCanceledOnTouchOutside(canCancel);
        if (!TextUtils.isEmpty(message)){
            textView.setText(message);
        }

    }

}
