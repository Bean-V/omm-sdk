package com.oort.weichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oort.weichat.ui.SplashActivity;


public class BootBroadcastReceiver  extends BroadcastReceiver {
    static  final  String ACTION1 =  "android.intent.action.BOOT_COMPLETED" ;
    static  final  String ACTION2 =  "ndroid.intent.action.LOCKED_BOOT_COMPLETED" ;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onboot", "onReceive");
        if  (intent.getAction().equals(ACTION1) || intent.getAction().equals(ACTION2)) {
            Intent mainActivityIntent =  new  Intent(context, SplashActivity.class);   // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
            Log.e("onboot", "自启动了");
        }
    }
}
