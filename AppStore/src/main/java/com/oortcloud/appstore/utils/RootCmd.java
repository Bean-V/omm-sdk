package com.oortcloud.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.oortcloud.appstore.bean.AppInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @function： 静默 安装 卸载  待开发
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/18 15:13
 */

//adb命令翻译执行类 pm
public class RootCmd {
    /***
     * @param command
     * @return
     */
    public static boolean exusecmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e("updateFile", "======000==writeSuccess======");
            process.waitFor();
        } catch (Exception e) {
            Log.e("updateFile", "======111=writeError======" + e.toString());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void unInstallApk(String pageName) {
        //adb  pm
        exusecmd("adb shell pm uninstall " + pageName);
    }


    /**
     *     * 静默卸载App
     *      *
     *       * @param packageName 包名
     *      * @return 是否卸载成功
     *
     */
    public static boolean uninstall(String packageName) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "uninstall", packageName).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }

        } catch (Exception e) {
            Log.d("msg" , "e = " + e.toString());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                Log.d("msg" , "Exception : " + e.toString());
            }
            if (process != null) {
                process.destroy();
            }
        }
        //如果含有"success"单词则认为卸载成功
        return successMsg.toString().equalsIgnoreCase("success");
    }


    public static List<AppInfo> getAllAppInfos(PackageManager packageManager) {

        List<AppInfo> list = new ArrayList<AppInfo>();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(intent, 0);
        // 遍历
        for (ResolveInfo ri : ResolveInfos) {
            Log.v("msg" , ri.nonLocalizedLabel + "");
//            String packageName = ri.activityInfo.packageName;
//            Drawable icon = ri.loadIcon(packageManager);
//            String appName = ri.loadLabel(packageManager).toString();
//            AppInfo appInfo = new AppInfo(icon, appName, packageName);
//            list.add(appInfo);
        }
        return list;
    }


    public static void uninstall(Context context , String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            Log.v("msg" ,pm.toString() + "");
            Method[] methods = pm!=null?pm.getClass().getDeclaredMethods():null;
            Method mDel = null;
            if (methods != null && methods.length>0) {
                for (Method method : methods) {
                    if (method.getName().toString().equals("deletePackage")) {
                        mDel = method;
                        break;
                    }
                }
            }
            if (mDel != null) {
                mDel.setAccessible(true);
                mDel.invoke(pm,packageName,null,0);
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.v("msg" , e.toString()+ "");
        }
    }



}