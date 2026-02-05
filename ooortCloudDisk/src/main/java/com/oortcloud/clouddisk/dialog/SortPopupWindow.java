package com.oortcloud.clouddisk.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.db.SharedPreferenceManager;
import com.oortcloud.clouddisk.http.HttpResult;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/28 17:24
 * @version： v1.0
 * @function：
 */
public class SortPopupWindow extends PopupWindow {


    private Activity mContext;
    private ViewGroup menuView;
    private SharedPreferenceManager mshared;

    public SortPopupWindow(final Activity context  , View view){
        super(context);

        mContext = context;
        mshared = SharedPreferenceManager.getInstance();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuView = (ViewGroup) inflater.inflate(R.layout.popup_sort, null);


        // 白色背景，黑色文字图标，
        ColorStateList foreground = ColorStateList.valueOf(Color.DKGRAY);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View child = menuView.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) child;

                for (int j = 0; j < layout.getChildCount(); j++) {
                    View lChild = layout.getChildAt(j);
                    if (lChild instanceof ImageView) {
                        ImageViewCompat.setImageTintList((ImageView) lChild, foreground);
                    } else if (lChild instanceof TextView) {
                        ((TextView) lChild).setTextColor(foreground);
                    }
                }
            }
        }
        //设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        // this.setWidth(ViewPiexlUtil.dp2px(context,200));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

        this.setOutsideTouchable(true);

        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.Buttom_Popwindow);

        //设置SelectPicPopupWindow弹出窗体的背景
        // 透明背景，
        this.setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.7f;
        context.getWindow().setAttributes(lp);

        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });

        initEvent();
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        showAsDropDown(view,
                -(getContentView().getMeasuredWidth() - view.getWidth()),
                -120);
    }

    private void  initEvent() {
        menuView.findViewById(R.id.name_ll).setOnClickListener(v -> {
            mshared.putString("order" , "name");
            HttpResult.fileList("/" ,  "" , 1 , 50 , "");
            dismiss();
        });

        menuView.findViewById(R.id.size_ll).setOnClickListener(v -> {
            mshared.putString("order" , "size");
            HttpResult.fileList("/" ,  "" , 1 , 50 , "");
            dismiss();
        });
        menuView.findViewById(R.id.time_ll).setOnClickListener(v -> {
            mshared.putString("order" , "time");
            HttpResult.fileList("/" ,  "" , 1 , 50 , "");
            dismiss();
        });

    }

}
