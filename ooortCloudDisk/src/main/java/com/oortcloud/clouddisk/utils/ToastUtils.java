package com.oortcloud.clouddisk.utils;


import android.widget.Toast;

import com.oortcloud.clouddisk.BaseApplication;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/30 16:54
 */
public class ToastUtils {

    public static void  showContent(String content){
        Toast toast =  Toast.makeText(BaseApplication.getInstance().getContext(), content , Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }
}
