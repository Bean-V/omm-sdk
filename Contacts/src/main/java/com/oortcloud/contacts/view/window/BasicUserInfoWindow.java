package com.oortcloud.contacts.view.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.omm.Friend;
import com.oortcloud.contacts.bean.omm.User;


/**
 * @filename:
 * @author: zzj/@date: 2022/3/29 17:06
 * @version： v1.0
 * @function：
 */
public class BasicUserInfoWindow extends PopupWindow {
    private TextView setName, addBlackList, removeBlackList, delete, reportTv;
    private View mMenuView;

    public BasicUserInfoWindow(FragmentActivity context, View.OnClickListener itemsOnClick, Friend friend, User user) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popu_im_user_basicinfo, null);

        setName =  mMenuView.findViewById(R.id.set_remark_nameS);
        addBlackList =  mMenuView.findViewById(R.id.add_blacklist);
        removeBlackList =  mMenuView.findViewById(R.id.remove_blacklist);
        delete =  mMenuView.findViewById(R.id.delete_tv);
        reportTv =  mMenuView.findViewById(R.id.report_tv);
        LinearLayout ll_basic = mMenuView.findViewById(R.id.ll_basic);
        ll_basic.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.show_icon));
        int status = Friend.STATUS_UNKNOW;
        if (friend != null) {
            status = friend.getStatus();
        }
        if (user != null && user.getUserType() == 2) {// 公众号
            status = Friend.STATUS_SYSTEM;
        }

        if (status == Friend.STATUS_SYSTEM) {
            // 公众号只需要举报
            setName.setVisibility(View.GONE);
            addBlackList.setVisibility(View.GONE);
            removeBlackList.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            ll_basic.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.new_company_bg));
        }

        if (status != Friend.STATUS_FRIEND) {
            // 非好友 不显示拉黑、删除、设置生活圈权限
            addBlackList.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        if (status != Friend.STATUS_BLACKLIST) {
            // 非黑名单 不支持移出黑名单
            removeBlackList.setVisibility(View.GONE);
        }

        reportTv = (TextView) mMenuView.findViewById(R.id.report_tv);

        setName.setText(context.getString(R.string.set_remark_name));
        addBlackList.setText(context.getString(R.string.add_black));
        removeBlackList.setText(context.getString(R.string.remove_black));
        delete.setText(context.getString(R.string.delete_friend));
        //设置按钮监听
        setName.setOnClickListener(itemsOnClick);
        addBlackList.setOnClickListener(itemsOnClick);
        removeBlackList.setOnClickListener(itemsOnClick);
        delete.setOnClickListener(itemsOnClick);
        reportTv.setOnClickListener(itemsOnClick);

        this.setContentView(mMenuView);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);

        this.setOutsideTouchable(true);

        this.setAnimationStyle(R.style.Down_PopWindow);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }
}
