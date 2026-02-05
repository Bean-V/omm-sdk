package com.oortcloud.appstore.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.R;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.listener.DownloadListener;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.utils.OperLogUtil; // 引入日志工具类

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * @filename:
 * @function： 首页启动处理逻辑
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/5/21 11:14
 */
public class AppManagerService extends Service {
    private static final String TAG = "AppManagerService";

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        OperLogUtil.msg(TAG + " - onBind：服务绑定调用，返回null");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OperLogUtil.msg(TAG + " - onCreate：服务创建，初始化完成");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OperLogUtil.msg(TAG + " - onStartCommand：服务启动，flags=" + flags + ", startId=" + startId);

        // 记录intent参数信息
        String intentInfo = (intent != null) ?
                "Intent包含数据，packageName=" + intent.getStringExtra("packageName") +
                        ", params=" + intent.getStringExtra("params") :
                "Intent为null";
        OperLogUtil.msg(TAG + " - onStartCommand：" + intentInfo);

        // 原注释代码保留，添加日志记录
        OperLogUtil.msg(TAG + " - onStartCommand：执行super.onStartCommand");
        super.onStartCommand(intent, flags, startId);

        // 初始化下载管理器
        OperLogUtil.msg(TAG + " - onStartCommand：初始化下载管理器(AppEventUtil.initDownLoadManager)");
        AppEventUtil.initDownLoadManager();

        // 获取应用信息
        OperLogUtil.msg(TAG + " - onStartCommand：调用getAppInfo处理intent数据");
        getAppInfo(intent);

        int result = super.onStartCommand(intent, flags, startId);
        OperLogUtil.msg(TAG + " - onStartCommand：服务启动流程完成，返回值=" + result);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OperLogUtil.msg(TAG + " - onDestroy：服务销毁，停止定时器运行");
        // Service被终止的同时也停止定时器继续运行
        if (timer != null) {
            timer.cancel();
            timer = null;
            OperLogUtil.msg(TAG + " - onDestroy：定时器已取消");
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        OperLogUtil.msg(TAG + " - getHomes：开始获取桌面应用包名列表");
        List<String> names = new ArrayList<String>();
        try {
            PackageManager packageManager = this.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            OperLogUtil.msg(TAG + " - getHomes：查询到" + resolveInfo.size() + "个桌面应用");

            for (ResolveInfo ri : resolveInfo) {
                names.add(ri.activityInfo.packageName);
                OperLogUtil.msg(TAG + " - getHomes：桌面应用包名=" + ri.activityInfo.packageName);
            }
        } catch (Exception e) {
            OperLogUtil.msg(TAG + " - getHomes：获取桌面应用失败，异常信息=" + e.getMessage() +
                    "，堆栈=" + Log.getStackTraceString(e));
        }
        OperLogUtil.msg(TAG + " - getHomes：返回桌面应用列表，大小=" + names.size());
        return names;
    }

    //通过包名获取应用信息
    private void getAppInfo(Intent intent) {
        OperLogUtil.msg(TAG + " - getAppInfo：开始处理应用信息获取");

        if (intent != null) {
            String packageName = intent.getStringExtra("packageName");
            String params = intent.getStringExtra("params");
            OperLogUtil.msg(TAG + " - getAppInfo：intent非空，packageName=" + packageName + ", params=" + params);
            getByPackage(packageName, params);
        } else {
            OperLogUtil.msg(TAG + " - getAppInfo：intent为null，无法获取应用信息");
        }
    }

    /**
     * 通过包名获取应用
     * @param packageName 应用包名
     * @param params 附加参数
     */
    private static void getByPackage(String packageName, String params) {
        String logTag = "getByPackage";
        OperLogUtil.msg(logTag + " - 开始执行，packageName=" + packageName + ", params=" + params);

        ProgressDialog tmpPd = null;

        // 校验包名非空
        if (!TextUtils.isEmpty(packageName)) {
            OperLogUtil.msg(logTag + " - 包名非空，开始查询本地应用信息");

            // 查询本地应用信息
            AppInfo tmpAppInfo = DataInit.getAppinfo(packageName);
            String tmpAppInfoDesc = (tmpAppInfo != null) ?
                    "存在本地应用信息，package=" + tmpAppInfo.getApppackage() +
                            ", versionCode=" + tmpAppInfo.getVersioncode() :
                    "本地无此应用信息";
            OperLogUtil.msg(logTag + " - DataInit.getAppinfo查询结果：" + tmpAppInfoDesc);

            // 处理本地存在应用信息的情况
            if (tmpAppInfo != null) {
                // 特殊应用版本校验：com.jwb_home.oort
                if (tmpAppInfo.getApppackage().contains("com.jwb_home.oort") &&
                        tmpAppInfo.getVersioncode() <= 108 &&
                        AppManager.getInstance().getListener() != null) {

                    OperLogUtil.msg(logTag + " - 命中特殊逻辑：com.jwb_home.oort版本<=108，清除监听器并返回");
                    AppManager.getInstance().setListener(null);
                    return;
                }

                // 特殊应用版本校验：com.work_dynamics.oort
                if (tmpAppInfo.getApppackage().contains("com.work_dynamics.oort") &&
                        tmpAppInfo.getVersioncode() <= 1420 &&
                        AppManager.getInstance().getListener() != null) {

                    OperLogUtil.msg(logTag + " - 命中特殊逻辑：com.work_dynamics.oort版本<=1420，清除监听器并返回");
                    AppManager.getInstance().setListener(null);
                    return;
                }

                // 触发点击事件
                OperLogUtil.msg(logTag + " - 触发AppEventUtil.onClick，使用本地应用信息");
                AppEventUtil.onClick(tmpAppInfo, new DownloadListener(tmpAppInfo, params));
            } else {
                // 本地无应用信息，处理特殊包名
                if (packageName.contains("com.jwb_home.oort") || packageName.contains("com.work_dynamics.oort")) {
                    OperLogUtil.msg(logTag + " - 本地无应用信息，但包名属于特殊应用，清除监听器并返回");
                    AppManager.getInstance().setListener(null);
                    return;
                }

                // 显示进度对话框
                OperLogUtil.msg(logTag + " - 本地无应用信息，准备显示进度对话框");
                ProgressDialog pd = new ProgressDialog(CommonApplication.topActivity);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage(CommonApplication.topActivity.getString(R.string.get_app_info));
                tmpPd = pd;

                Activity context = CommonApplication.topActivity;
                if (context != null && !context.isFinishing() && !context.isDestroyed()) {
                    pd.show();
                    OperLogUtil.msg(logTag + " - 进度对话框显示成功");
                } else {
                    OperLogUtil.msg(logTag + " - 进度对话框显示失败：上下文活动状态异常");
                    tmpPd = null;
                }
            }

            // 发起网络请求获取应用信息
            ProgressDialog finalTmpPd = tmpPd;
            OperLogUtil.msg(logTag + " - 发起网络请求：HttpRequestParam.getByPackage(" + packageName + ")");

            HttpRequestParam.getByPackage(packageName)
                    .subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            OperLogUtil.msg(logTag + " - 网络请求成功，响应数据：" + s);
                            Log.v("msg", "packageName---->" + packageName);
                            Log.v("msg", "getByPackage---->" + s);

                            try {
                                // 解析响应数据
                                Result<AppInfo> result = new Gson().fromJson(s,
                                        new TypeToken<Result<AppInfo>>() {}.getType());
                                OperLogUtil.msg(logTag + " - 响应数据解析完成，code=" + result.getCode() +
                                        ", isok=" + result.isok());

                                if (result.isok()) {
                                    OperLogUtil.msg(logTag + " - 响应成功，准备处理应用信息");

                                    // 延迟处理，模拟加载效果
                                    new Handler().postDelayed(() -> {
                                        // 关闭进度对话框
                                        if (finalTmpPd != null) {
                                            finalTmpPd.dismiss();
                                            OperLogUtil.msg(logTag + " - 进度对话框已关闭");
                                        }

                                        AppInfo appInfo = result.getData();
                                        String appInfoDesc = (appInfo != null) ?
                                                "package=" + appInfo.getApppackage() + ", versionCode=" + appInfo.getVersioncode() :
                                                "appInfo为null";
                                        OperLogUtil.msg(logTag + " - 服务器返回应用信息：" + appInfoDesc);

                                        // 处理服务器返回的应用信息
                                        if (tmpAppInfo != null) {
                                            if (appInfo != null && appInfo.getVersioncode() > tmpAppInfo.getVersioncode()) {
                                                OperLogUtil.msg(logTag + " - 服务器版本(" + appInfo.getVersioncode() +
                                                        ")高于本地版本(" + tmpAppInfo.getVersioncode() + ")，保存最新信息");
                                                DataInit.saveAppInfo(appInfo);
                                            } else {
                                                OperLogUtil.msg(logTag + " - 服务器版本不高于本地版本，不更新本地信息");
                                            }
                                        } else {
                                            if (appInfo != null) {
                                                OperLogUtil.msg(logTag + " - 本地无应用信息，保存服务器返回信息并触发点击事件");
                                                DataInit.saveAppInfo(appInfo);
                                                AppEventUtil.onClick(appInfo, new DownloadListener(appInfo, params));
                                            } else {
                                                OperLogUtil.msg(logTag + " - 服务器返回appInfo为null，无法保存和处理");
                                            }
                                        }
                                    }, 500);

                                } else {
                                    // 响应失败处理
                                    OperLogUtil.msg(logTag + " - 响应失败，msg=" + result.getMsg() + ", code=" + result.getCode());
                                    ToastUtils.showBottom(result.getMsg());
                                    AppStatu.getInstance().appStatu = 0;
                                    OperLogUtil.msg(logTag + " - 设置AppStatu.appStatu=0");

                                    // 处理特定错误码
                                    if (result.getCode() > 4000) {
                                        OperLogUtil.msg(logTag + " - 错误码>4000，准备启动登录历史页面");
                                        Context mContext = AppStoreInit.getInstance().getApplication();
                                        String appid = mContext.getApplicationInfo().processName;
                                        String action = appid + ".LoginHistoryActivity";
                                        Intent intent = new Intent(action);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        AppStoreInit.getInstance().getApplication().startActivity(intent);
                                        OperLogUtil.msg(logTag + " - 已发送启动LoginHistoryActivity的intent，action=" + action);
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                OperLogUtil.msg(logTag + " - 解析响应数据异常：" + e.getMessage() +
                                        "，堆栈=" + Log.getStackTraceString(e));
                                // 发生异常时关闭进度对话框
                                if (finalTmpPd != null) {
                                    finalTmpPd.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            OperLogUtil.msg(logTag + " - 网络请求异常：" + e.getMessage() +
                                    "，堆栈=" + Log.getStackTraceString(e));
                            // 错误时关闭进度对话框
                            if (finalTmpPd != null) {
                                finalTmpPd.dismiss();
                            }
                        }

                        @Override
                        public void onComplete() {
                            OperLogUtil.msg(logTag + " - 网络请求完成（onComplete）");
                        }
                    });

        } else {
            // 包名为空处理
            OperLogUtil.msg(logTag + " - 包名为空，显示提示并设置状态");
            ToastUtils.showBottom("包名为空");
            AppStatu.getInstance().appStatu = 0;
            OperLogUtil.msg(logTag + " - 设置AppStatu.appStatu=0");
        }

        OperLogUtil.msg(logTag + " - 方法执行完成");
    }
}
