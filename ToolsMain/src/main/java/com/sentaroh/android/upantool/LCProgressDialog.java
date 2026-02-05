package com.sentaroh.android.upantool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sentaroh.android.upantool.R;

public class LCProgressDialog extends Dialog {
    private String message;

    private int progress = 0;
    private boolean canCancel;
    private TextView textView;
    private ProgressBar pb;

    public LCProgressDialog(Context context, String message, int progress) {
        super(context);
        this.message = message;
        this.progress = progress;
    }

    public void setMessage(String message) {
        this.message = message;
        handler.sendEmptyMessage(0);
    }

    public void setProgress(int progress) {
        this.progress = progress;
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
            if (msg.what == 0) {
                pb.setProgress(progress);
            }

        }
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_layout);
        textView = findViewById(R.id.tv_message);

        pb = findViewById(R.id.pg_bar);
//        setCancelable(canCancel);
        setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(message)){
            textView.setText(message);
        }
        pb.setProgress(progress);

    }

}
