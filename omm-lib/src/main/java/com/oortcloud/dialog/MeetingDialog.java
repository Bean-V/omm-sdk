package com.oortcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.oort.weichat.R;
import com.oort.weichat.util.ScreenUtil;
import com.oort.weichat.util.ToastUtil;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 17:10
 * @version： v1.0
 * @function： 创建会议
 */
public class MeetingDialog extends Dialog {

    private Context mContext;
    private View view;
    private TextView mTitle;
    private TextView mTheme;
    private TextView mNotice;
    private CheckBox mCheckBOx;

    public MeetingDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
        mContext = context;
        initDialogStyle();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.8);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }
    private void initDialogStyle(){
        view=  LayoutInflater.from(getContext()).inflate(R.layout.dialog_meeting_layout, null);
        setContentView(view);
        mTitle =  view.findViewById(R.id.title);
        mTheme =  view.findViewById(R.id.meeting_theme_et);
        mNotice =  view.findViewById(R.id.meeting_notice_et);
        mCheckBOx =  view.findViewById(R.id.is_check);

        view.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            dismiss();
        });

    }
    public MeetingDialog setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitle.setText(title);
        }
        return this;
    }
    public MeetingDialog setTheme(String theme){
        if (!TextUtils.isEmpty(theme)){
            mTheme.setText(theme);
        }
        return this;
    }
    public MeetingDialog setNotice(String content){
        if (!TextUtils.isEmpty(content)){
            mNotice.setText(content);
        }
        return this;
    }
    public MeetingDialog setConfirmListener(ConfirmListener confirmListener){

        if (confirmListener != null){
            view.findViewById(R.id.btn_enter).setOnClickListener(v -> {

                String theme = mTheme.getText().toString().trim();
                String notice = mNotice.getText().toString().trim();
                if (TextUtils.isEmpty(theme)){
                    ToastUtil.showToast(mContext , "请输入会议名称");
                    return;
                }
                if (TextUtils.isEmpty(notice)){
                    ToastUtil.showToast(mContext , "请输入会议简介");
                    return;
                }

                confirmListener.onConfirmListener(theme , notice);
                dismiss();
            });
        }
        return this;
    }

    public interface ConfirmListener {
        void onConfirmListener( String theme , String notice);
    }

}
