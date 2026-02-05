package com.oortcloud.oort_zhifayi;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.oortcloud.oort_zhifayi.timetask.alarmmanager.Task;
import com.oortcloud.oort_zhifayi.timetask.alarmmanager.TaskScheduler;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/25-19:30.
 * Version 1.0
 * Description:执法记录仪初始化
 */
public class LEVidCams {
    public static void init(Context context){

        //启动定位服务
        context.startForegroundService(new Intent(context, LocationService.class));

        TaskScheduler.scheduleTask(context, new Task("1234",systemTimeMillis() + 1000*10, systemTimeMillis() + 1000 * 60 * 3, "" ));
    }

    private static long systemTimeMillis() {
        return System.currentTimeMillis();
    }
}
