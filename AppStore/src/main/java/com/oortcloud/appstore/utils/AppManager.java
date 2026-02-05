package com.oortcloud.appstore.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.activity.AppApplyActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.http.HttpConstants;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.AppUseInfo;
import com.oortcloud.basemodule.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @filename:
 * @function：应用管理工具类 卸载  安装 打开功能
 * @version： v1.0
 * @author: zzj/@date: 2020/1/18 14:50
 */
public class AppManager {

    public OpenOtherWayListener getListener() {
        return listener;
    }

    public void setListener(OpenOtherWayListener listener) {
        this.listener = listener;
    }

    OpenOtherWayListener  listener;


    public static AppManager logUtil;

    //单例模式初始化
    public static AppManager getInstance() {
        if (logUtil == null) {
            logUtil = new AppManager();

        }
        return logUtil;
    }



    public interface OpenOtherWayListener{
       public boolean penOtherWay(String pakegeName,String url);
        public boolean notOpen(String pakegeName,String url);
    }
    private static Context mContext = AppStoreInit.getInstance().getApplication();
    public static SllInterface mActivity;
    //存储目录
    public final static String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + mContext.getPackageName() + File.separator;
    //个人目录
    public final static String PATH = BASE_PATH + AppStoreInit.getUUID() + File.separator;

    //缓存h5应用信息  提供Cordova 获取
    public static AppInfo mH5Info;

    public static void openApply(AppInfo appInfo) {
        AppApplyActivity.start(mContext,appInfo.getApppackage());
    }


    public static void open(AppInfo appInfo, String params) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppStatu.getInstance().appStatu = 0;
            }
        },2000);
        //打开应用不添加处理
//        DataInit.addToMyModule(appInfo);
        if (appInfo != null) {
            //执行打开应用上报
            doReportOpenApp(appInfo.getApplabel(), appInfo.getApp_id(), appInfo.getVersion());
            switch (appInfo.getTerminal()) {
                case 0:
                    startAPK(appInfo);
                    break;
                case 1:
                    startH5App(appInfo, params);
                    break;
                case 2:
                    startHTML(appInfo);
                    break;
                case 6:
                    startMUI(appInfo, params);
                    break;
            }
        }


    }


    /**
     * 原生应用打开
     */
    private static void startAPK(AppInfo appInfo) {


        Log.e("ceshi" ,"" + mActivity + appInfo.getNetwork());
        if(appInfo.getNetwork() == 2 && mActivity != null){
            mActivity.setSslCallback(new SllCallback() {
                @Override
                public void startCallback() {
                    startAPK_(appInfo);
                }

                @Override
                public void stopCallback() {

                }
            });
            mActivity.start();



        }else{
            startAPK_(appInfo);
        }

    }


    private static void startAPK_(AppInfo appInfo) {



        try {

            AppStatu.getInstance().appStatu = 0;
            if (AppUtils.isAppInstalled(AppStoreInit.getInstance().getApplication(),appInfo.getApppackage())) {//new File("/data/data/" + appInfo.getApppackage()).exists()
                if (!TextUtils.isEmpty(appInfo.getAppentry())) {
                    Intent intent;
                    Bundle bundle = new Bundle();
                    bundle.putString("GATEWAY_URL", HttpConstants.BASE_URL);
                    bundle.putString("uuid", AppStoreInit.getUUID());
                    bundle.putString("token", AppStoreInit.getToken());
                    intent = new Intent();//Intent.ACTION_MAIN
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.putExtras(bundle);
//                    if (!isAppAlive(mContext , appInfo.getApppackage())){
                    ComponentName cn = new ComponentName(appInfo.getApppackage(), appInfo.getAppentry());
                    intent.setComponent(cn);
//                    }
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppStoreInit.getInstance().getActivity().startActivity(intent);
                } else {

                    Intent LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage(appInfo.getApppackage());
                    //有null异常
                    if (LaunchIntent != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("GATEWAY_URL", HttpConstants.BASE_URL);
                        bundle.putString("token", AppStoreInit.getToken());
                        bundle.putString("uuid", AppStoreInit.getUUID());
                        LaunchIntent.putExtras(bundle);
                        AppStoreInit.getInstance().getActivity().startActivity(LaunchIntent);
                    } else {
                        Toast.makeText(mContext, "启动失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {

            Exception ex = e;
            String es = e.getLocalizedMessage();
        }

    }

    //打开H5应用
    private static void startH5App(AppInfo appInfo, String params) {

        LogUtils.log("设置appInfo" + appInfo.getDescription());
        mH5Info = appInfo;
        File file = isAddFilePath(appInfo);
        AppStatu.getInstance().appStatu = 0;
        if (file.exists()) {
            String filePath = recursionFile(file, checkAppEntry(appInfo.getAppentry()));
            //增加启动参数
            if (!TextUtils.isEmpty(params)) {

//                if(params.contains("?")){
//                    filePath += "/#/" + params;
//                }else  {
                    filePath += "?" + params;
//                }
            } else {
                filePath += "?" + "token=" + AppStoreInit.getToken() + "&szjcy_token=" + AppStoreInit.getSZJCYToken();
            }
            Log.v("msg", "filePath--->" + filePath);
            Log.v("msg", "file:///" + filePath);

            String appId = mContext.getApplicationInfo().processName;
            Intent intent = new Intent(appId + ".web.container");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", "file:///" + filePath);
//            intent.putExtra("url", "file:///" +PATH +"moa/mobileOA/html/gg/gg_list.html" );

            if(AppManager.getInstance().listener != null){

                Boolean res = false;
                if(appInfo.getApppackage().contains("com.jwb_home.oort")){

                    if(appInfo.getVersioncode() > 108){
                        res = AppManager.getInstance().getListener().penOtherWay(appInfo.getApppackage(),"file:///" + filePath);
                    }else {
                        AppManager.getInstance().getListener().notOpen(appInfo.getApppackage(),"file:///" + filePath);
                        mH5Info = appInfo;
                        AppManager.getInstance().setListener(null);
                        return;
                    }
                }else{
                    res = AppManager.getInstance().getListener().penOtherWay(appInfo.getApppackage(),"file:///" + filePath);
                }


               if(!res){
                   CommonApplication.pIsTab = false;
                   LogUtils.log("listener打开web");
                   mContext.startActivity(intent);
               }else{
                   AppManager.getInstance().setListener(null);
               }

            }else {
                LogUtils.log("打开web");
                CommonApplication.pIsTab = false;
                mContext.startActivity(intent);

            }

//            }
        }
    }

    //打开MUI应用
    private static void startMUI(AppInfo appInfo, String params) {
        File file = isAddFilePath(appInfo);
        if (file.exists()) {
            String filePath = queryFile(file, appInfo.getAppentry());
            Log.v("msg" , "mui---->" + filePath);
            String appId = mContext.getApplicationInfo().processName;
            Intent intent = new Intent(appId +".mui.web_app");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url",  filePath);
            intent.putExtra("args", TokenManager.getInstance().toJson());

            mContext.startActivity(intent);
        }
    }

    /**
     * web网页打开
     */
    private static void startHTML(AppInfo info) {
        String url = info.getAppweburl();
        Log.v("msg", url);
        String appid = mContext.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".web.container");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (url.contains("?")) {
            intent.putExtra("url", url + "&token=" + AppStoreInit.getToken());

        } else {
            intent.putExtra("url", url + "?token=" + AppStoreInit.getToken());
        }

        mContext.startActivity(intent);

    }

    /**
     * 安装APP  解压zip
     */
    public static void installAPP(AppInfo appInfo, String downloadPath) {
        AppStatu.getInstance().appStatu = 0;
        switch (appInfo.getTerminal()) {
            case 0:
                installAPK(appInfo, downloadPath);
                break;
            case 1:
            case 6:
                installZIP(appInfo, downloadPath);
                break;
            case 2:
                installHTML(appInfo);
                break;

        }
    }

    /**
     * 原生App安装
     */
    private static void installAPK(AppInfo appInfo, String downloadPath) {
        SilentInstall.getInstallIntent(mContext, downloadPath);

//        File zipFile = new File(PATH + appInfo.getApplabel() + ".apk");
//        deleteFile(zipFile);

    }


    /**
     * h5应用安装
     */
    private static void installZIP(AppInfo appInfo, String downloadPath) {
        //安装前先删除旧版
        // ,否则一个包可能多个子目录
        AppInfo ordAppInfo = AppInfoManager.getInstance().isContains(DBConstant.INSTALL_TABLE, appInfo.getApppackage());
        if (ordAppInfo != null) {
            File paths = new File(PATH + ordAppInfo.getApppackage() + "_" + ordAppInfo.getVersion());
            if (paths.exists()) {
                deleteFile(paths);
            }
        }
        try {

            String path = PATH + appInfo.getApppackage() + "_" + appInfo.getVersion() + File.separator;
            ZipUtils.UnZipFolder(downloadPath, path);
            //删除zip包
            File zipFile = new File(PATH + appInfo.getApplabel() + ".zip");
            deleteFile(zipFile);
        } catch (Exception e) {
            Log.v("msg", e.toString());
        }


    }

    /**
     * web网页安装
     */
    private static void installHTML(AppInfo appInfo) {

    }

    //卸载应用程序
    public static void unInstallApp(AppInfo appInfo) {
        switch (appInfo.getTerminal()) {
            case 0:
                unInstallAPK(appInfo);
                break;
            case 1:
                unInstallZIP(appInfo);
                break;
            case 2:
                unInstallHTML(appInfo);
                break;

        }

//
    }

    /**
     * 原生App卸载
     */
    private static void unInstallAPK(AppInfo appInfo) {

        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + appInfo.getApppackage()));
        uninstall_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        AppStoreInit.getInstance().getApplication().startActivity(uninstall_intent);

        deleteApk(appInfo);

    }

    public static void deleteApk(AppInfo appInfo) {
        File zipFile = new File(BASE_PATH + appInfo.getApplabel() + "_" + appInfo.getVersion() + ".apk");
        if (zipFile.exists() && zipFile.isFile()) {
            zipFile.delete();
        }
    }

    /**
     * h5应用安装卸载
     */
    public static void unInstallZIP(AppInfo appInfo) {

        File path = new File(PATH + appInfo.getApppackage() + "_" + appInfo.getVersion());
        if (path.exists()) {
            deleteFile(path);
        }
    }

    /**
     * web网页卸载
     */
    private static void unInstallHTML(AppInfo appInfo) {

    }


    //判断是否存在文件
    public static boolean checkFilePath(AppInfo appInfo) {

        File file = isAddFilePath(appInfo);
        String appEntry = checkAppEntry(appInfo.getAppentry());
        return recursionFile(file, appEntry).contains(appEntry);

    }

    public static void deleteFile(File file) {
        if (file.exists()) {//判断路径是否存在
            if (file.isFile()) {//boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
                file.delete();
            } else {//不是文件，对于文件夹的操作
                //保存 路径D:/1/新建文件夹2  下的所有的文件和文件夹到listFiles数组中
                File[] listFiles = file.listFiles();//listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                for (File file2 : listFiles) {
                    /*
                     * 递归作用：由外到内先一层一层删除里面的文件 再从最内层 反过来删除文件夹
                     *    注意：此时的文件夹在上一步的操作之后，里面的文件内容已全部删除
                     *         所以每一层的文件夹都是空的  ==》最后就可以直接删除了
                     */
                    deleteFile(file2);
                }
            }
            file.delete();
        } else {
            System.out.println("该file路径不存在！！");
        }
    }


    /**
     * 判断应用是否已经启动
     *
     * @param context     上下文对象
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 递归查询启动入口
     *
     * @param file     查询文件
     * @param appEntry 入口
     * @return
     */
    public static String recursionFile(File file, String appEntry) {
        if (file.exists()) {//判断路径是否存在
            if (file.isFile()) {//boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
                //获取绝对路径比较启动路径
                if (file.getAbsolutePath().contains(appEntry)) {
                    return file.getAbsolutePath();
                }
            } else {//不是文件，对于文件夹的操作
                //保存 路径D:/1/新建文件夹2  下的所有的文件和文件夹到listFiles数组中
                File[] listFiles = file.listFiles();//listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                ArrayList<File> arrayList = new ArrayList<>();
                for (File file2 : listFiles) {

                    if (file2.isFile()) {
                        if (file2.getAbsolutePath().contains(appEntry)) {
                            return file2.getAbsolutePath();
                        }
                    } else {
                        arrayList.add(file2);
                    }

                    /*
                     * 递归作用：由外到内先一层一层删除里面的文件 再从最内层 反过来删除文件夹
                     *    注意：此时的文件夹在上一步的操作之后，里面的文件内容已全部删除
                     *         所以每一层的文件夹都是空的  ==》最后就可以直接删除了
                     */

                }
                for (File file1 : arrayList) {
                    String path = recursionFile(file1, appEntry);

                    if (path.contains(appEntry)) {

                        return path;
                    }
                }

            }

        } else {
            System.out.println("该file路径不存在！！");
        }
        return "";

    }

    /**
     * MUI递归查询启动入口
     *
     * @param file
     * @param appEntry
     * @return
     */
    public static String queryFile(File file, String appEntry) {
        if (file.exists()) {//判断路径是否存在
            if (file.isDirectory()) {//boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
                //保存 路径D:/1/新建文件夹2  下的所有的文件和文件夹到listFiles数组中
                if (file.getAbsolutePath().contains(appEntry)) {
                    return file.getAbsolutePath();
                } else {
                    File[] listFiles = file.listFiles();//listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                    for (File file2 : listFiles) {
                        if (file2.isDirectory()) {
                            String path = queryFile(file2, appEntry);

                            if (path.contains(appEntry)) {
                                return path;
                            }
                        }
                    }


                }


            }

        } else {
            System.out.println("该file路径不存在！！");
        }
        return "";

    }

    /**
     * H5应用启动入口，多层目录
     *
     * @return
     */
    private static String checkAppEntry(String appEntry) {

        //判断有没有启动入口,默认index.html
        if (TextUtils.isEmpty(appEntry)) {
            appEntry = "index.html";
        }
//        else if (appEntry.contains(File.separator)){
//            //有,并且是多层目录,取出最后启动文件
//            appEntry = appEntry.substring(appEntry.lastIndexOf(File.separator) +1);
//        }
        return appEntry;
    }

    /**
     * H5应用启动入口，多层目录
     *
     * @return
     */
    private static File isAddFilePath(AppInfo appInfo) {
        String packageName = appInfo.getApppackage() + "_" + appInfo.getVersion();
        File file = new File(PATH + packageName);

        return file;
    }

    public static final String getH5Info() {

        return JSON.toJSONString(mH5Info);
    }


    //上报应用使用
    private static void doReportOpenApp(String label, String appid, String version) {

        HashMap<String, Object> map = new HashMap<>();
        //x经度，y纬度
        map.put("accessToken", AppUseInfo.accessToken);
        map.put("appId", appid);
        map.put("appName", label);
        map.put("appVersion", version);
        map.put("sn", AppUseInfo.sn);

        String params = new Gson().toJson(map);
        Log.d("params", params);
        //"http://160.184.100.10:32610/"

//com.oortcloud.basemodule.constant.Constant.BASE_URL
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(Constant.BASE_3CLASSURL +
                com.oortcloud.basemodule.constant.Constant.APP_USE_REPORT, params, new com.oortcloud.basemodule.utils.HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {
                if (requst != null) {
                    Log.d("report app:", requst);
                }
            }
        });
    }
    //上报api使用
    private static void doReportOpenApi(String label, String appid, String version) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("accessToken", AppUseInfo.accessToken);
        map.put("appId", appid);
        map.put("appName", label);
        map.put("appVersion", version);
        map.put("sn", AppUseInfo.sn);

        String params = new Gson().toJson(map);
        Log.d("params", params);
        com.oortcloud.basemodule.utils.HttpUtil.doPostAsyn(com.oortcloud.basemodule.constant.Constant.BASE_URL +
                com.oortcloud.basemodule.constant.Constant.API_USE_REPORT, params, new com.oortcloud.basemodule.utils.HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {
                if (requst != null) {
                    Log.d("report app:", requst);
                }
            }
        });
    }
}
