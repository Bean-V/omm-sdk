package com.oortcloud.oort_zhifayi;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.utils.DeviceGPSUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.oort_zhifayi.event.MessageEvent;
//import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;


public class app extends CommonApplication {

    private Thread thread;
    private Thread postionThread;

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashReport.initCrashReport(this, "e4357bc1b0", false);

//        Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );


//        startActivity(intent);
        //getPostPostion();

        FastSharedPreferences.init(this);

        Context context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocationService.class));
        } else {
            context.startService(new Intent(context, LocationService.class));


        }

        EventBus.getDefault().register(this);
    }

    int tcount = 0;
    public void getScanRes() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean test = true;
                while (test) {

                    try {
                        tcount++;
                        if (tcount > 400) {
                            //LogHelper.getInstance().setIS_WRITE_FILE(false);
                        }
                        Thread.sleep(2000);//每隔1s执行一次

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        String message = event.message;
        // 处理收到的消息，例如更新UI

        if(ReportInfo.latitude < 1 || ReportInfo.longitude < 1){

            return;
        }


        if(ReportInfo.lastlatitude < 1 || ReportInfo.lastlongitude < 1){

        }else {
            long d = DeviceGPSUtils.calculateDistance(ReportInfo.latitude, ReportInfo.longitude, ReportInfo.lastlatitude, ReportInfo.lastlongitude);

            if (d < 20) {
                return;
            }
        }
        HashMap paras = new HashMap();
        paras.put("accessToken",ZFYConstant.testToken);
        paras.put("direction",100);;
        paras.put("latitude",ReportInfo.latitude);
        paras.put("longitude",ReportInfo.longitude);
        paras.put("speed",10);
        paras.put("elevation",10);
        paras.put("terminalno",getmSeralNum());

        paras.put("status",1);

        HttpUtils.post()
                .url(Constant.BASE_URL + "multi/apaas-location-service/location/v1/reportlocation")
                .params(paras)
                .build(false,true)
                .execute(new BaseCallback<HashMap>(HashMap.class) {

                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        HashMap dictionary = result.getData();

                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                    }
                } );

    }
    int pcount = 0;
//    public void getPostPostion() {
//
//        postionThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                boolean test = true;
//                while (test) {
//
//                    try {
//                        pcount++;
//                        if (pcount > 400) {
//                            //LogHelper.getInstance().setIS_WRITE_FILE(false);
//                        }
//
////
////                        {
////                            "accuracy": 10,
////                                "direction": 180,
////                                "elevation": 10,
////                                "latitude": 34.232443,
////                                "longitude": 119.12313,
////                                "speed": 10,
////                                "terminalno": "123456"
////                        }
//
//                        HashMap paras = new HashMap();
//                        paras.put("accessToken",Constant.testToken);
//                        paras.put("direction",100);;
//                        paras.put("latitude",ReportInfo.latitude);
//                        paras.put("longitude",ReportInfo.longitude);
//                        paras.put("speed",10);
//                        paras.put("elevation",10);
//                        paras.put("terminalno",getmSeralNum());
//
//                        paras.put("status",1);
//
//                        HttpUtils.post()
//                                .url(Constant.BASE_URL + "multi/apaas-location-service/location/v1/reportlocation")
//                                .params(paras)
//                                .build(false,true)
//                                .execute(new BaseCallback<HashMap>(HashMap.class) {
//
//                                    @Override
//                                    public void onResponse(ObjectResult<HashMap> result) {
//                                        HashMap dictionary = result.getData();
//
//                                    }
//
//                                    @Override
//                                    public void onError(okhttp3.Call call, Exception e) {
//                                        Exception e1 = e;
//                                    }
//                                } );
//
//
//                        Thread.sleep(30000);//每隔1s执行一次
//
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        });
//        postionThread.start();
//    }
}
