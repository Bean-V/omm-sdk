package com.oortcloud.appstore.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.db.AppInfoManager;
import com.oortcloud.appstore.db.DBConstant;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.db.ModuleTableManager;
import com.oortcloud.appstore.download.DownloadManager;
import com.oortcloud.appstore.download.ThreadPoolManager;
import com.oortcloud.appstore.http.HttpConstants;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.widget.listener.DownloadListener;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.im.MessageEventChangeUI;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.widget.xupdate.widget.OortUpdateDialog;
import com.oortcloud.clouddisk.activity.HomeActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @filename:
 * @function：处理应用事件
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/5/15 11:46
 */
public class AppEventUtil {
    private static  DownloadManager downloadManager;

    /**
     * 点击事件
     *
     * @param appInfo
     */

    public static void initDownLoadManager(){
        if(downloadManager == null){
            downloadManager = new DownloadManager();
        }
    }
    public static void onClick(AppInfo appInfo, DownloadListener listener) {
        Context mContext = AppStoreInit.getInstance().getApplication();
        Activity mActivity = AppStoreInit.getInstance().getActivity();
        String appid = mContext.getApplicationInfo().processName;
        //判断是否连续点击

        int ACTION_REQUEST_PERMISSIONS = 0x001;
        String[] NEEDED_PERMISSIONS = new String[]{

                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,

        };

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        }
//                    if (!checkPermissions(NEEDED_PERMISSIONS)) {
//
//                        OperLogUtil.msg("请求打开app所需权限");
//
//
//                        for (int i = 0; i < NEEDED_PERMISSIONS.length; i++) {
//                            if (TextUtils.isEmpty(NEEDED_PERMISSIONS[i])) {
//                                throw new IllegalArgumentException("Permission request for permissions "
//                                        + Arrays.toString(NEEDED_PERMISSIONS) + " must not contain null or empty values");
//                            }
//                        }
//                        ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
//                        return;
//                    }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mActivity.startActivity(intent);
                return;
            }
        }



        if(appInfo.getTerminal() == 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!mActivity.getPackageManager().canRequestPackageInstalls()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                    mActivity.startActivityForResult(intent, 10086);
                    return;
                }
            }
        }
        Log.v("msg", appInfo.toString());
        if (!isFastClick()) {
            //判断是否上的虚应用 _void_apply
            if (appInfo.getApppackage().contains("_void_apply")){
                //根据的配置启动页，完成内置页面打开

                if(listener.getParams().isEmpty()) {
                    startApp(appid + "." + appInfo.getAppentry());
                }else{
                    startApp(appid + "." + appInfo.getAppentry(), listener.getParams());
                }
                return;
            }

            switch (appInfo.getApppackage()) {
                case "com.voicemeet.oort":
                    //语音会商
//                    startApp(appid + ".voicemeet");
                    startApp(appid + ".voicemeet");
                    break;
                case "com.vediomeet.oort":
                    //视频会商
//                    startApp(appid + ".meeting");
                    startApp(appid + ".vediomeet");
                    break;
                case "com.douyin.oort":
                    //微视频  短视频
                    startApp(appid + ".douyin" );
                    break;
                case "com.live.oort":
                    //直播  视频回传
                    startApp(appid + ".classroom.live" );

                    break;
                case "com.nearby.oort":
                    //附件同事
                    startApp(appid + ".nearby" );
                    break;
                case "com.contact.oort":
                    //通讯录
                    startApp(appid + ".contact" );
                    break;
                case "com.discover.oort":
                    //朋友圈
                    startApp(appid + ".discover" );
                    break;

                case "com.oortcloud.clouddisk":
                    AppStatu.getInstance().appStatu = 0;
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("GATEWAY_URL", HttpConstants.BASE_URL);
                    bundle.putString("uuid", AppStoreInit.getUUID());
                    bundle.putString("token", AppStoreInit.getToken());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    //startApp("com.oort.clouddisk.home");
                    break;
                case "com.imapp.oort":
                    //消息
                    EventBus.getDefault().post(new MessageEventChangeUI(1));
                    startApp(appid + ".main" );
                    break;
                case "com.dispatch.oort":
                    startApp(appid + ".dispatch");
                    break;

                default:
                    checkAPP(appInfo, listener);
                    break;

            }

        }

    }



    private static void checkAPP(AppInfo appInfo, DownloadListener listener) {
        // 1. 日志：方法入口，记录入参信息
        String appInfoDesc = (appInfo != null) ?
                "AppPackage=" + appInfo.getApppackage() + ", VersionCode=" + appInfo.getVersioncode() + ", Terminal=" + appInfo.getTerminal() :
                "appInfo为null";
        String listenerDesc = (listener != null) ? "已传入DownloadListener" : "DownloadListener为null";
        OperLogUtil.msg("checkAPP - 方法入口：入参信息：" + appInfoDesc + "，" + listenerDesc);

        // 2. 日志：查询应用申请状态（DataInit.getAppinfo）
        AppInfo applyApp = DataInit.getAppinfo(appInfo != null ? appInfo.getApppackage() : null);
        String applyAppDesc = (applyApp != null) ?
                "AppPackage=" + applyApp.getApppackage() + ", ApplyStatus=" + applyApp.getApply_status() :
                "未查询到对应ApplyApp（DataInit.getAppinfo返回null）";
        OperLogUtil.msg("checkAPP - 查询应用申请状态：" + applyAppDesc);

        // 3. 处理已查询到ApplyApp的场景
        if (applyApp != null) {
            OperLogUtil.msg("checkAPP - 进入ApplyApp处理逻辑：ApplyStatus=" + applyApp.getApply_status());

            // 3.1 ApplyStatus > 1：打开应用申请页面
            if (applyApp.getApply_status() > 1) {
                OperLogUtil.msg("checkAPP - ApplyStatus=" + applyApp.getApply_status() + "（>1），执行AppManager.openApply");
                AppManager.openApply(appInfo);
                OperLogUtil.msg("checkAPP - AppManager.openApply执行完成，方法返回");
                return;
            }

            // 3.2 ApplyStatus == 0：暂未开放（注释了打开申请页，仅返回）
            if (applyApp.getApply_status() == 0) {
                OperLogUtil.msg("checkAPP - ApplyStatus=0（暂未开放），注释了AppManager.openApply，不执行打开操作，方法返回");
                // ToastUtils.showBottom("暂未开放"); // 原代码注释，保留日志记录
                return;
            }

            // 3.3 其他ApplyStatus（如1）：不处理，继续往下执行
            OperLogUtil.msg("checkAPP - ApplyStatus=" + applyApp.getApply_status() + "（非>1且非0），继续执行后续逻辑");
        }

        // 4. 校验appInfo非空（避免空指针）
        if (appInfo == null) {
            OperLogUtil.msg("checkAPP - 错误：appInfo为null，无法继续执行版本检测，方法返回");
            return;
        }

        // 5. 校验AppPackage非空（参数合法性检查）
        if (TextUtils.isEmpty(appInfo.getApppackage())) {
            String errorMsg = AppStoreInit.getInstance().getApplication().getResources().getString(R.string.parameter_error);
            OperLogUtil.msg("checkAPP - 错误：appInfo的AppPackage为空字符串，触发参数错误提示：" + errorMsg);
            ToastUtils.showBottom(errorMsg);
            AppStatu.getInstance().appStatu = 0;
            OperLogUtil.msg("checkAPP - 设置AppStatu.appStatu=0，方法返回");
            return;
        }

        // 6. 发起版本检测网络请求（HttpRequestCenter.verifyversioncode）
        String targetPackage = appInfo.getApppackage();
        int targetVersionCode = appInfo.getVersioncode();
        OperLogUtil.msg("checkAPP - 开始发起版本检测请求：verifyversioncode(package=" + targetPackage + ", versionCode=" + targetVersionCode + ")");

        HttpRequestCenter.verifyversioncode(targetPackage, targetVersionCode)
                .subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        // 6.1 日志：接收网络请求响应
                        OperLogUtil.msg("checkAPP - 版本检测请求成功，响应数据：" + s);

                        // 6.2 解析响应数据（Result<AppInfo>）
                        Result<AppInfo> result = null;
                        try {
                            Type resultType = new TypeToken<Result<AppInfo>>() {}.getType();
                            result = new Gson().fromJson(s, resultType);
                            OperLogUtil.msg("checkAPP - 响应数据解析成功：Code=" + (result != null ? result.getCode() : "null") +
                                    "，Data是否为空：" + (result != null && result.getData() != null ? "否" : "是"));
                        } catch (Exception e) {
                            OperLogUtil.msg("checkAPP - 错误：解析响应数据失败，异常信息：" + e.getMessage() +
                                    "，异常堆栈：" + android.util.Log.getStackTraceString(e));
                            return;
                        }

                        if (result == null) {
                            OperLogUtil.msg("checkAPP - 错误：解析后Result为null，无法继续处理");
                            return;
                        }

                        // 6.3 处理响应码：50010（无最新版本，使用原appInfo）
                        if (result.getCode() == 50010) {
                            OperLogUtil.msg("checkAPP - 响应码=50010（无最新版本），使用原appInfo执行对应逻辑，Terminal=" + appInfo.getTerminal());

                            switch (appInfo.getTerminal()) {
                                case 0:
                                    OperLogUtil.msg("checkAPP - Terminal=0（APK类型），调用apk()方法，isUpdate=false");
                                    apk(appInfo, null, listener, false);
                                    break;
                                case 1:
                                case 6:
                                    OperLogUtil.msg("checkAPP - Terminal=" + appInfo.getTerminal() + "（H5 APP类型），调用h5APP()方法，isUpdate=false");
                                    h5APP(appInfo, null, listener, false);
                                    break;
                                case 2:
                                    OperLogUtil.msg("checkAPP - Terminal=2（Web URL类型），调用webURL()方法，isUpdate=true");
                                    webURL(appInfo, listener, true);
                                    break;
                                case 3:
                                    OperLogUtil.msg("checkAPP - Terminal=3（PC桌面应用），提示“手机端不能使用”");
                                    ToastUtils.showBottom("PC桌面应用，手机端不能使用");
                                    break;
                                default:
                                    OperLogUtil.msg("checkAPP - 响应码50010：未知Terminal类型=" + appInfo.getTerminal() + "，不执行任何操作");
                                    break;
                            }
                        }
                        // 6.4 处理响应码：50011（有最新版本，使用新AppInfo）
                        else if (result.getCode() == 50011) {
                            AppInfo newAppInfo = result.getData();
                            String newAppInfoDesc = (newAppInfo != null) ?
                                    "NewPackage=" + newAppInfo.getApppackage() + ", NewVersionCode=" + newAppInfo.getVersioncode() + ", NewTerminal=" + newAppInfo.getTerminal() :
                                    "result.getData()返回null（无最新AppInfo）";
                            OperLogUtil.msg("checkAPP - 响应码=50011（有最新版本），" + newAppInfoDesc);

                            if (newAppInfo != null) {
                                OperLogUtil.msg("checkAPP - 使用最新newAppInfo执行逻辑，Terminal=" + newAppInfo.getTerminal());
                                switch (newAppInfo.getTerminal()) {
                                    case 0:
                                        OperLogUtil.msg("checkAPP - Terminal=0（APK类型），调用apk()方法，isUpdate=true");
                                        apk(newAppInfo, appInfo, listener, true);
                                        break;
                                    case 1:
                                        OperLogUtil.msg("checkAPP - Terminal=1（H5 APP类型），调用h5APP()方法，isUpdate=true");
                                        h5APP(newAppInfo, appInfo, listener, true);
                                        break;
                                    case 2:
                                        OperLogUtil.msg("checkAPP - Terminal=2（Web URL类型，无需下载），调用webURL()方法，isUpdate=true");
                                        webURL(newAppInfo, listener, true);
                                        break;
                                    case 3:
                                        OperLogUtil.msg("checkAPP - Terminal=3（PC桌面应用），提示“手机端不能使用”");
                                        ToastUtils.showBottom("PC桌面应用，手机端不能使用");
                                        break;
                                    default:
                                        OperLogUtil.msg("checkAPP - 响应码50011：未知Terminal类型=" + newAppInfo.getTerminal() + "，不执行任何操作");
                                        break;
                                }
                            } else {
                                OperLogUtil.msg("checkAPP - 响应码50011但newAppInfo为null，无法执行更新逻辑");
                            }
                        }
                        // 6.5 处理其他响应码（非50010/50011）
                        else {
                            OperLogUtil.msg("checkAPP - 响应码=" + result.getCode() + "（非50010/50011），不处理该响应");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 6.6 日志：网络请求失败（异常捕获）
                        OperLogUtil.msg("checkAPP - 错误：版本检测请求失败，异常信息：" + e.getMessage() +
                                "，异常堆栈：" + android.util.Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        // 6.7 日志：网络请求完成（无论成功失败都会调用）
                        OperLogUtil.msg("checkAPP - 版本检测请求执行完成（onComplete）");
                    }
                });

        OperLogUtil.msg("checkAPP - 版本检测请求已发起，方法主线程逻辑执行完成（后续等待异步响应）");
    }
    /**
     * 处理原生应用 下载 打开
     *
     * @param appInfo  应用信息
     * @param listener 回调
     * @param flag     新版本true刷新本地数据库
     */
    private static void apk(AppInfo appInfo, AppInfo oldAppInfo, DownloadListener listener, boolean flag) {

        File file = new File(AppManager.BASE_PATH + appInfo.getApplabel() + "_" + appInfo.getVersion() + ".apk");
        //获取安装的版本号
        int code = AppUtils.getVersionCode(AppStoreInit.getInstance().getApplication(), appInfo.getApppackage());
        //获取版本号
        String version = AppUtils.getVersionName(AppStoreInit.getInstance().getApplication(), appInfo.getApppackage());

        //判断是否安装  测试安装取得版本code 与应用信息的版本号不符合 增加version判断
        //AppInfoManager.getInstance().isContains(appInfo) &&

        File file1 = new File(AppStoreInit.getInstance().getApplication().getFilesDir().getPath() + appInfo.getApppackage());
        boolean fileExists = file1.exists();
        boolean versionCodeMatch = appInfo.getVersioncode() == code;
        boolean versionNameMatch = appInfo.getVersion().equals(version);

// 打印调试信息
        Log.d("CheckInstall", "文件路径: " + file.getAbsolutePath());
        Log.d("CheckInstall", "文件是否存在: " + fileExists);
        Log.d("CheckInstall", "VersionCode 是否匹配: " + versionCodeMatch + "（当前: " + appInfo.getVersioncode() + "，目标: " + code + "）");
        Log.d("CheckInstall", "VersionName 是否匹配: " + versionNameMatch + "（当前: " + appInfo.getVersion() + "，目标: " + version + "）");

// 最终判断
        if (fileExists && (versionCodeMatch || versionNameMatch)) {
            Log.d("CheckInstall", "满足条件，继续操作");
            // TODO: 继续逻辑
        } else {
            Log.d("CheckInstall", "不满足条件，终止");
        }


        if(flag && code != appInfo.getVersioncode()){

            OortUpdateDialog dialog = new OortUpdateDialog(CommonApplication.topActivity,2);
            dialog.show();

            dialog.setVersion(appInfo.getVersion());
            dialog.setUpdateInfo(appInfo.getVersion());


            dialog.setButtonClickCallback(new OortUpdateDialog.Callback() {
                @Override
                public void ok() {
//                        if (downloadManager == null) {
//                            downloadManager = new DownloadManager();
//                        }
//
//                        DownloadListener dl = new DownloadListener(appInfo, dialog);
//                        downloadManager.startDownload(appInfo, dl);
                }

                @Override
                public void cancel() {

                }

                @Override
                public void cancelDown() {
                    ThreadPoolManager.getInstance().cancelDown();

                }
            });

            if (downloadManager == null) {
                downloadManager = new DownloadManager();
            }


            if(DownloadManager.isConnected) {
                DownloadListener dl = new DownloadListener(appInfo, dialog);
                downloadManager.startDownload(appInfo, dl);
            }else{
                downloadManager.setCallback(new DownloadManager.Callback() {
                    @Override
                    public boolean connect() {
                        DownloadListener dl = new DownloadListener(appInfo, dialog);
                        downloadManager.startDownload(appInfo, dl);
                        return false;
                    }
                });
            }

        }else if (AppUtils.isAppInstalled(AppStoreInit.getInstance().getApplication(), appInfo.getApppackage()) && (appInfo.getVersioncode() == code || appInfo.getVersion().equals(version))) {
            AppStatu.getInstance().appStatu = 0;
            open(appInfo, listener);

        } else if (file.exists() && file.length() >= Integer.parseInt(appInfo.getApp_size())) {
            AppStatu.getInstance().appStatu = 0;
            AppManager.installAPP(appInfo, file.getPath());

        } else {
            if (oldAppInfo != null) {
                //删除旧apk文件
                AppManager.deleteApk(oldAppInfo);
            }

            {

                OortUpdateDialog dialog = new OortUpdateDialog(CommonApplication.topActivity,2);
                dialog.show();

                dialog.setVersion(appInfo.getVersion());
                dialog.setUpdateInfo(appInfo.getVer_description());


                dialog.setButtonClickCallback(new OortUpdateDialog.Callback() {
                    @Override
                    public void ok() {
//                        if (downloadManager == null) {
//                            downloadManager = new DownloadManager();
//                        }
//
//                        DownloadListener dl = new DownloadListener(appInfo, dialog);
//                        downloadManager.startDownload(appInfo, dl);
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void cancelDown() {
                        ThreadPoolManager.getInstance().cancelDown();

                    }
                });

                if (downloadManager == null) {
                    downloadManager = new DownloadManager();
                }


                if(DownloadManager.isConnected) {
                    DownloadListener dl = new DownloadListener(appInfo, dialog);
                    downloadManager.startDownload(appInfo, dl);
                }else{
                    downloadManager.setCallback(new DownloadManager.Callback() {
                        @Override
                        public boolean connect() {
                            DownloadListener dl = new DownloadListener(appInfo, dialog);
                            downloadManager.startDownload(appInfo, dl);
                            return false;
                        }
                    });
                }

            }
//                if (downloadManager == null) {
//                    downloadManager = new DownloadManager();
//                }
//                downloadManager.startDownload(appInfo, listener);
        }
        //更新列表 new
        if (flag) {

            upDateAppInfo(appInfo, listener);

        }


    }

    private static void h5APP(AppInfo appInfo, AppInfo oldAppInfo, DownloadListener listener, boolean flag) {
        //判断下载表是否存在该应用信息 及判断本地是否文件
        if(flag && !AppManager.checkFilePath(appInfo)){

            OortUpdateDialog dialog = new OortUpdateDialog(CommonApplication.topActivity,2);
            dialog.show();

            dialog.setVersion(appInfo.getVersion());
            dialog.setUpdateInfo(appInfo.getVer_description());


            dialog.setButtonClickCallback(new OortUpdateDialog.Callback() {
                @Override
                public void ok() {
//                        if (downloadManager == null) {
//                            downloadManager = new DownloadManager();
//                        }
//
//                        DownloadListener dl = new DownloadListener(appInfo, dialog);
//                        downloadManager.startDownload(appInfo, dl);
                }

                @Override
                public void cancel() {

                }

                @Override
                public void cancelDown() {
                    ThreadPoolManager.getInstance().cancelDown();

                }
            });

            if (downloadManager == null) {
                downloadManager = new DownloadManager();
            }

            if(DownloadManager.isConnected) {
                DownloadListener dl = new DownloadListener(appInfo, dialog);
                downloadManager.startDownload(appInfo, dl);
            }else{
                downloadManager.setCallback(new DownloadManager.Callback() {
                    @Override
                    public boolean connect() {
                        DownloadListener dl = new DownloadListener(appInfo, dialog);
                        downloadManager.startDownload(appInfo, dl);
                        return false;
                    }
                });
            }


        }else if (AppManager.checkFilePath(appInfo)) {//AppInfoManager.getInstance().isContains(appInfo) &&

            open(appInfo, listener);

        } else {
            if (oldAppInfo != null) {
                AppManager.unInstallZIP(appInfo);
            }

            if(flag || true){
                OortUpdateDialog dialog = new OortUpdateDialog(CommonApplication.topActivity,2);
                dialog.show();

                dialog.setVersion(appInfo.getVersion());
                dialog.setUpdateInfo(appInfo.getVersion());


                dialog.setButtonClickCallback(new OortUpdateDialog.Callback() {
                    @Override
                    public void ok() {
//                        if (downloadManager == null) {
//                            downloadManager = new DownloadManager();
//                        }
//
//                        DownloadListener dl = new DownloadListener(appInfo, dialog);
//                        downloadManager.startDownload(appInfo, dl);
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void cancelDown() {

                    }
                });

                if (downloadManager == null) {
                    downloadManager = new DownloadManager();
                }

                if(DownloadManager.isConnected) {
                    DownloadListener dl = new DownloadListener(appInfo, dialog);
                    downloadManager.startDownload(appInfo, dl);
                }else{
                    downloadManager.setCallback(new DownloadManager.Callback() {
                        @Override
                        public boolean connect() {
                            DownloadListener dl = new DownloadListener(appInfo, dialog);
                            downloadManager.startDownload(appInfo, dl);
                            return false;
                        }
                    });
                }
            }else {

                if (downloadManager == null) {
                    downloadManager = new DownloadManager();
                }
                if(DownloadManager.isConnected) {
                    downloadManager.startDownload(appInfo, listener);
                }else{
                    downloadManager.setCallback(new DownloadManager.Callback() {
                        @Override
                        public boolean connect() {
                            downloadManager.startDownload(appInfo, listener);
                            return false;
                        }
                    });
                }

            }
        }
        //重新设置应用信息
        if (flag) {
            if (listener != null) {
                listener.setAppInfo(appInfo);
            }
        }
    }

    private static void muiAPP(AppInfo appInfo, AppInfo oldAppInfo, DownloadListener listener, boolean flag) {
        //判断下载表是否存在该应用信息 及判断本地是否文件
        if (AppInfoManager.getInstance().isContains(appInfo) && AppManager.checkFilePath(appInfo)) {
            AppStatu.getInstance().appStatu = 0;
            open(appInfo, listener);

        } else {
            if (oldAppInfo != null) {
                AppManager.unInstallZIP(appInfo);
            }
            if(downloadManager == null){
                downloadManager = new DownloadManager();
            }
            downloadManager.startDownload(appInfo, listener);
        }
        //重新设置应用信息
        if (flag) {
            if (listener != null) {
                listener.setAppInfo(appInfo);
            }
        }


    }

    private static void webURL(AppInfo appInfo, DownloadListener listener, boolean flag) {
        //web不走下载 存在直接打开
        open(appInfo, listener);
        if (flag || !AppInfoManager.getInstance().isContains(appInfo)) {
            //不存在添加
            upDateAppInfo(appInfo, listener);
        }
    }

    private static void upDateAppInfo(AppInfo appInfo, DownloadListener listener) {

        //本地更新
        new Thread(() -> {
            AppInfoManager.getInstance().insertAppInfo(DBConstant.INSTALL_TABLE, appInfo);
            List<ModuleInfo> moduleInfoList = ModuleTableManager.getInstance().queryData(DBConstant.MODULE_TABLE);
            if (moduleInfoList != null) {
                for (ModuleInfo moduleInfo : moduleInfoList) {

                    AppInfoManager.getInstance().upDateAppInfo(DBConstant.TABLE + moduleInfo.getModule_id(), appInfo);
                }
            }


            //下载记录数
            HttpRequestCenter.appinstallplusone(appInfo.getApppackage(), appInfo.getVersioncode()).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                    }.getType());
                    if (result.isok()) {

                    }
                }

            });
            //安装列表
            HttpRequestCenter.appInstall(appInfo.getApplabel(), appInfo.getApppackage(), appInfo.getClassify(), appInfo.getUid(), appInfo.getVersioncode(), appInfo.getTerminal()).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                    }.getType());
                    if (result.isok() || result.getCode() == 50010) {
                        //数据库更新成功/已经是最高版本
                    }

                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);

                }
            });
        }).start();

    }

    /**
     * 五个内置应用 直接打开
     */
    public static void startApp(String action) {
        AppStatu.getInstance().appStatu = 0;
        try {
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle bundle = new Bundle();
            bundle.putString("GATEWAY_URL", HttpConstants.BASE_URL);
            bundle.putString("uuid", AppStoreInit.getUUID());
            bundle.putString("token", AppStoreInit.getToken());
            intent.putExtras(bundle);
            AppStoreInit.getInstance().getApplication().startActivity(intent);
        } catch (Exception e) {

            Exception ex = e;

        }

    }


    private static void startApp(String action,String params) {
        AppStatu.getInstance().appStatu = 0;
        try {
            Intent intent = new Intent(action);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(!params.isEmpty()){
                intent.putExtra("params", params);
            }
            AppStoreInit.getInstance().getApplication().startActivity(intent);
        } catch (Exception e) {

            Exception ex = e;

        }

    }

    /**
     * 打开
     *
     * @param appInfo
     * @param listener
     */
    private static void open(AppInfo appInfo, DownloadListener listener) {
        if (listener != null) {
            AppManager.open(appInfo, listener.getParams());
        } else {
            AppManager.open(appInfo, "");
        }
    }

    //增加0.5s连续点击判断
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
