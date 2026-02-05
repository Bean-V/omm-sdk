package com.jun.baselibrary.ioc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/24 1:18
 * Version 1.0
 * Description：检测网络
 */
class CheckNetUtil {
    /**
     * 检测网络
     * 只能依附于事件处理，因为反射执行只是一种调用方式，
     * 单独处在时，直接调用并不能检测网络
     */
    static void injectNet(Context context, Object object) {
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            CheckNet checkNet = method.getAnnotation(CheckNet.class);
            if (checkNet != null){
                if (!isNetworkAvailable(context)){
                    Toast.makeText(context, "亲，您当前网络不给力...",Toast.LENGTH_SHORT)
                            .show();;
                }else {
                    try {
                        method.setAccessible(true);
                        method.invoke(object);

                    } catch (Exception e) {
                        Log.e("TAG", "Error ", e);
                    }
                }
            }
        }

    }

    /**
     * 检查当前网络是否可用
     */
    private static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
