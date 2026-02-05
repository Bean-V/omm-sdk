package com.oortcloud.popwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.oort.weichat.R;
import com.oort.weichat.ui.nearby.UserSearchActivity;
import com.oortcloud.contacts.activity.ManagerSettingActivity;

/**
 * @filename:
 * @author: zzj/@date: 2022/4/9 15:05
 * @version： v1.0
 * @function：
 */
public class ContactsManagerPopWindow extends PopupWindow {
    private Context mContext;
    private ViewGroup menuView;
    public ContactsManagerPopWindow(final Activity context , View view){
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuView = (ViewGroup) inflater.inflate(R.layout.popup_contacts_manager, null);


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
        lp.alpha = 0.9f;
        context.getWindow().setAttributes(lp);

        setOnDismissListener(() -> {
            WindowManager.LayoutParams lp1 = context.getWindow().getAttributes();
            lp1.alpha = 1f;
            context.getWindow().setAttributes(lp1);
        });

        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        showAsDropDown(view, -(getContentView().getMeasuredWidth() - view.getWidth()-35),
                getContentView().getMeasuredHeight() - view.getHeight()*3  -view.getHeight()/3);

        initEvent();
    }
    private void initEvent(){
        menuView.findViewById(R.id.add_friends).setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, UserSearchActivity.class));
            dismiss();
        });

        menuView.findViewById(R.id.set_contacts).setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ManagerSettingActivity.class));
            dismiss();
        });
    }
}
