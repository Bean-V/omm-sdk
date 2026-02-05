package com.oortcloud.appstore.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *  
 *  * 静默安装的实现类，调用install()方法执行具体的静默安装逻辑。 
 *  * 原文地址：http://blog.csdn.net/guolin_blog/article/details/47803149 
 *  * @author guolin 
 *  * @since 2015/12/7 
 *  
 */
public class SilentInstall{

  /** 
      * 执行具体的静默安装逻辑，需要手机ROOT。 
      * @param apkPath 
      *          要安装的apk文件的路径 
      * @return 安装成功返回true，安装失败返回false。 
      */
        public static boolean install(String apkPath){
            boolean result= false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try{
        // 申请su权限  
        Process process = Runtime.getRuntime().exec("su");

        dataOutputStream = new DataOutputStream(process.getOutputStream());
        // 执行pm install命令  
        String command = "pm install -r " + apkPath + "\n";
        dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
        dataOutputStream.flush();
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
       process.waitFor();
       errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
       String msg = "";
        String line;
       // 读取命令的执行结果  
    while((line = errorStream.readLine()) != null){
        msg += line;
       }
       Log.d("TAG","install msg is " + msg);
       // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功  
        if(!msg.contains("Failure")){
        result = true;
        }
        }catch(Exception e){
       Log.e("TAG",e.getMessage(),e);
       }finally{
        try{
        if(dataOutputStream!=null){
        dataOutputStream.close();
        }
        if(errorStream!=null){
        errorStream.close();
        }
        }catch(IOException e){
        Log.e("TAG",e.getMessage(), e);
        }
        }
        return result;
        }


    public static void getInstallIntent(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            Toast.makeText(context, "APK 文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 及以上使用 FileProvider
            apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileProvider",
                    apkFile
            );
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // ⚠️ 必须添加
        } else {
            // Android 7.0 以下直接使用 file://
            apkUri = Uri.fromFile(apkFile);
        }

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "没有找到安装程序", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
