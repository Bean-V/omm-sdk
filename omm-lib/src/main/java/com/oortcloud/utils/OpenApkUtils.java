package com.oortcloud.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class OpenApkUtils {

    public static String PACKNAME = "gansu_doudou.apk";
    public static String DOWNLOADFILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "vrv" + File.separator + "cache";
    public static String PKG = "com.vrv.emm";

    public static String apkVersion ="3.6.49";//和assets doudou.apk版本号一致
    /**
     * 使用Intent queryIntentActivities 判断应用是否安装
     *
     * @param
     * @return
     */
    @SuppressLint("WrongConstant")
    public static boolean isClientInstalled(Context context, String appPackageName) {
        Intent i = new Intent(Intent.ACTION_DEFAULT);
        i.setPackage(appPackageName);
        PackageManager pm = context.getPackageManager();
        List<?> ris = pm.queryIntentActivities(i, PackageManager.GET_ACTIVITIES);
        return ris != null && ris.size() > 0;
    }


    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRun(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改apk权限
     *
     * @param
     * @return
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开
     *
     * @param
     * @return
     */
    public static void openApp(final Context context, int page, int unreadMessage) {
        if (isClientInstalled(context,PKG)){
            Intent intent = new Intent();
            intent.setData(Uri.parse("vrv://pull.vrv/emm?type=" + page));
            intent.putExtra("page", page);
//            intent.putExtra("token", UserInfoUtil.getInstance(context).userToken());
//            intent.putExtra("idCard", UserInfoUtil.getInstance(context).getIdCard());
//            intent.putExtra("policeNo", UserInfoUtil.getInstance(context).userCode());//警号
            intent.putExtra("unreadMessage", unreadMessage);//动态未读消息
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
//            //复制至SD卡下载安装
//            copyWithInstall(context);
            Toast.makeText(context,"未安装甘警通",Toast.LENGTH_SHORT);
        }
    }

    public static void copyWithInstall(final Context context){
        File subFolder = getFile(OpenApkUtils.DOWNLOADFILEPATH);
        final File apkFile = new File(subFolder, OpenApkUtils.PACKNAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (OpenApkUtils.createFile(context, PACKNAME, apkFile)) {
                    OpenApkUtils.clientInstall(context, apkFile);
                }
            }
        }).start();
    }

    public static File getFile(String path) {
        File folder = new File(path);
        if(!folder.getParentFile().exists()) {
            folder.getParentFile().mkdir();
        }
        if(!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }


    /**
     * 安装apk
     *
     * @param
     * @return
     */

    public static void clientInstall(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "xxx", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    /**
     * 获取版本号
     *
     */
    public static String getVersion(Context context, String packName) {
        String version = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packName, 0);
            version = info.versionName;
            Log.i("Utils", "已安装版本号version:  "  + info.packageName +"--"+ version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }



    /**
     * 将assets文件下的复制至SD卡
     *
     * @param context
     */
    public static boolean createFile(Context context, String name, File file) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(name);

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            // TODO 可判断文件是否存在
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return false;
    }
}
