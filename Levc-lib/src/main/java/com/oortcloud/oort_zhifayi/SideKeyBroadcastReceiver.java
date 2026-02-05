package com.oortcloud.oort_zhifayi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SideKeyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
//                case Intent.ACTION_BOOT_COMPLETED:
//                    // 处理设备启动完成的广播
//                    Log.d("MyBroadcastReceiver", "Device Boot Completed");
//                    break;
//                case Intent.ACTION_BATTERY_LOW:
//                    // 处理电池电量低的广播
//                    Log.d("MyBroadcastReceiver", "Battery Low");
//                    break;
                // 添加其他您希望监听的系统广播类型
            }
        }
    }
}
