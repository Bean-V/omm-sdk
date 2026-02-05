package com.oort.weichat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.oort.weichat.Reporter;
import com.oort.weichat.R;
import com.oort.weichat.util.ScreenUtil;


/**
 * 隐私设置里各种允许范围相关的设置使用的底部对话框，
 */
public class AllowTypeDialog extends Dialog implements View.OnClickListener {
    private TextView tv1, tv2, tv3, tv4;

    private OnAllowTypeClickListener listener;

    public AllowTypeDialog(Context context, OnAllowTypeClickListener onAllowTypeClickListener) {
        super(context, R.style.BottomDialog);
        this.listener = onAllowTypeClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allow_type_dialog);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);

        findViewById(R.id.tvAllow).setOnClickListener(this);
        findViewById(R.id.tvBlock).setOnClickListener(this);

        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        // x/y坐标
        // lp.x = 100;
        // lp.y = 100;
        lp.width = ScreenUtil.getScreenWidth(getContext());
        o.setAttributes(lp);
        this.getWindow().setGravity(Gravity.BOTTOM);
        this.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int value;
        int id = v.getId();
        if (id == R.id.tv1) {
            value = -1;
        } else if (id == R.id.tv2) {
            value = 1;
        } else if (id == R.id.tv3) {
            value = 2;
        } else if (id == R.id.tv4) {
            value = 3;
        } else if (id == R.id.tvAllow) {
            listener.onWhitelistClick();
            return;
        } else if (id == R.id.tvBlock) {
            listener.onBlacklistClick();
            return;
        } else {
            Reporter.unreachable();
            return;
        }
        listener.onNewValueClick(value);
    }

    public interface OnAllowTypeClickListener {
        void onNewValueClick(int value);

        void onWhitelistClick();

        void onBlacklistClick();
    }
}
