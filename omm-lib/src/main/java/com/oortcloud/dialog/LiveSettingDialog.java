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
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/8 15:03
 * @version： v1.0
 * @function：创建或编辑的直播
 */
public class LiveSettingDialog extends Dialog {
    private Context mContext;
    private   View view;
    private TextView mTitle;
    private TextView mTheme;
    private TextView mNotice;
    private CheckBox mCheckBOx;
    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
    public LiveSettingDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
        mContext = context;
        initDialogStyle();


//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.8);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

    }
    private void initDialogStyle(){
       view=  LayoutInflater.from(getContext()).inflate(R.layout.dialog_live_setting_layout, null);
        setContentView(view);
        mTitle =  view.findViewById(R.id.title);
       mTheme =  view.findViewById(R.id.live_theme_et);
       mNotice =  view.findViewById(R.id.live_notice_et);
       mCheckBOx =  view.findViewById(R.id.is_check);
       String theme = sharedPreferences.getString("liveTheme",mContext.getString(R.string.my_live_push));
       String notice = sharedPreferences.getString("liveNotice",mContext.getString(R.string.live_push));
       boolean check = sharedPreferences.getBoolean("liveCheck",false);

       mTheme.setText(theme);
       mNotice.setText(notice);
       mCheckBOx.setChecked(check);
       view.findViewById(R.id.btn_exit).setOnClickListener(v -> {
           dismiss();
       });

    }
    public LiveSettingDialog setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitle.setText(title);
        }

        return this;
    }
    public LiveSettingDialog setConfirmListener(ConfirmListener confirmListener){

        if (confirmListener != null){
            view.findViewById(R.id.btn_enter).setOnClickListener(v -> {

                String theme = mTheme.getText().toString().trim();
                String notice = mNotice.getText().toString().trim();
                if (TextUtils.isEmpty(theme)){
                    ToastUtil.showToast(mContext , "请输入回传主题");
                   return;
                }
                if (TextUtils.isEmpty(notice)){
                    ToastUtil.showToast(mContext , "请输回传公告");
                    return;
                }
                //保存输入的直播间
                sharedPreferences.edit().putString("liveTheme",theme);
                sharedPreferences.edit().putString("liveNotice",notice);

                    confirmListener.onConfirmListener( theme,  notice, mCheckBOx.isChecked());
                    dismiss();
            });
        }
        return this;
    }

    public interface ConfirmListener {
       void onConfirmListener(String roomName , String roomDesc , boolean check);
    }

}
