package com.oort.weichat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.util.ScreenUtil;


/**
 * 隐私设置里各种允许范围相关的设置使用的底部对话框，
 */
public class AutoReplayDialog extends Dialog implements View.OnClickListener {
    private TextView tv1, tv2;

    private OnAutoReplayClickListener listener;

    public AutoReplayDialog(Context context, OnAutoReplayClickListener onAutoReplayClickListener) {
        super(context, R.style.BottomDialog);
        this.listener = onAutoReplayClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_replay_dialog);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
//        tv3 = (TextView) findViewById(R.id.tv3);
//        tv4 = (TextView) findViewById(R.id.tv4);
//        tv5 = (TextView) findViewById(R.id.tv5);
//        tv6 = (TextView) findViewById(R.id.tv6);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
//        tv3.setOnClickListener(this);
//        tv4.setOnClickListener(this);
//        tv5.setOnClickListener(this);
//        tv6.setOnClickListener(this);


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
            //            case R.id.tv3:
//                value = 2;
//                break;
//            case R.id.tv4:
//                value = 3;
//                break;
//            case R.id.tv5:
//                value = 4;
//                break;
//            case R.id.tv6:
//                value = 5;
//                break;
        } else {
            Reporter.unreachable();
            return;
        }
        listener.onNewValueClick(value);
    }

    public interface OnAutoReplayClickListener {
        void onNewValueClick(int value);


    }
}
