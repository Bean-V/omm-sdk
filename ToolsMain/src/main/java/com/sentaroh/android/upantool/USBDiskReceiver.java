package com.sentaroh.android.upantool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class USBDiskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)) {
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                //System.out.println("U盘路径----"+ path);
                LogHelper.getInstance().d("U盘路径----"+ path);

                File f = new File(path);
                List<File> files  = FileTool.getAllFileInDir(f);

//                for(File file : files){
//                    LogHelper.getInstance().d("U盘路径----"+ file.getPath());
//                }
//
//                File f1 = new File(path + "/" + "1234");
//                Boolean res = f1.mkdir();
//
//
//                LogHelper.getInstance().d("U盘路径----"+ res +"####" + f1.exists() + "****" + f.getName());

                File f1 = new File("/storage/F47E-6905" + "/" + "1234333");
                Boolean res = f1.mkdir();


                LogHelper.getInstance().d("U盘路径----"+ res +"####" + f1.exists() + "****" + f1.getName());


                File f2= new File("/storage/F47E-6905" + "/" + "1234333.txt");

                LogHelper.getInstance().d("U盘路径----"+ f2.getPath() +"####" + f2.exists() + "****" + f2.getName());
                try {
                    Boolean res2 = f2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            if ("android.intent.action.MEDIA_REMOVED".equals(action)) {
                //System.out.println("U盘拨出----");
                LogHelper.getInstance().d("U盘拨出"+ path);
            }
        }
    }
}
