package com.oortcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.oort.weichat.R;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.util.ScreenUtil;
import com.oortcloud.activity.RepeatActivity;
import com.oortcloud.activity.ReportActivity;

/**
 * @filename:
 * @author: zzj/@date: 2020/11/30 15:58
 * @version： v1.0
 * @function： 朋友圈选项对话框
 */
public class DiscoverDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View view;
    private PublicMessage mMessage;

    public DiscoverDialog(@NonNull Context context , PublicMessage message) {
        super(context, R.style.BottomDialog);
        mContext = context;
        mMessage = message;
        initDialogStyle();

        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.95);
        lp.gravity = Gravity.BOTTOM;
        o.setAttributes(lp);

    }
    private void initDialogStyle(){
        view=  LayoutInflater.from(getContext()).inflate(R.layout.dialog_discover_layout, null);
        setContentView(view);
        view.findViewById(R.id.share_tv).setOnClickListener(this);
        view.findViewById(R.id.repeat_tv).setOnClickListener(this);
        view.findViewById(R.id.report_tv).setOnClickListener(this);
        view.findViewById(R.id.cancel_tv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        if (id == R.id.share_tv) {
            shareEvent();
        } else if (id == R.id.repeat_tv) {
            intent.setClass(mContext, RepeatActivity.class);
            intent.putExtra("obj_key", mMessage);
            mContext.startActivity(intent);
        } else if (id == R.id.report_tv) {
            intent.setClass(mContext, ReportActivity.class);
            intent.putExtra("obj_key", mMessage);
            mContext.startActivity(intent);
        } else if (id == R.id.cancel_tv) {
        }
        dismiss();
    }

    public DiscoverDialog setTitle(String title){

        return this;
    }
    public DiscoverDialog setConfirmListener(LiveSettingDialog.ConfirmListener confirmListener){

        if (confirmListener != null){
            view.findViewById(R.id.btn_enter).setOnClickListener(v -> {


                dismiss();
            });
        }
        return this;
    }

    public interface ConfirmListener {
        void onConfirmListener(String roomName , String roomDesc , boolean check);
    }


    protected void shareEvent() {

            String time = String.valueOf(System.currentTimeMillis());

            //转化为uri
//            Uri imageUri = Uri.fromFile(new File(  "/sdcard/file/" + time + ".png"));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            shareIntent.putExtra(Intent.EXTRA_TEXT,"https://www.coolapk.com/feed/23315859?shareKey=YzI1YmVkY2E2NzczNWZjODQ3NjY~&shareUid=4251932&shareFrom=com.coolapk.market_10.5.3");
            shareIntent.setType("text/plain");
            mContext.startActivity(Intent.createChooser(shareIntent, "分享到:"));


    }

}
