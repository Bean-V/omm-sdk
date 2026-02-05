package com.oortcloud.contacts.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.oortcloud.basemodule.CommonApplication;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zzj/@date: 2020/4/30 16:54
 */
public class ToastUtils {

    public static void  showBottom(int hintContent){
        Toast toast =  Toast.makeText(CommonApplication.getAppContext(), hintContent , Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }
}
