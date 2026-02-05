package com.oort.weichat.fragment;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.appstore.utils.AppEventUtil;
import com.oortcloud.appstore.utils.ToastUtils;
import com.oortcloud.appstore.widget.listener.DownloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TabAppService extends Service {

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
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getAppInfo(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行

    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    //通过包名获取应用信息
    private void  getAppInfo(Intent intent){

        if (intent != null) {
            getByPackage(intent.getStringExtra("packageName") ,intent.getStringExtra("params") );
        }

    }

    /**
     * 通过包名获取应用
     * @param packageName
     */
    private static void getByPackage(String packageName, String params) {

        if (!TextUtils.isEmpty(packageName)) {

            HttpRequestParam.getByPackage( packageName ).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {
                    Log.v("msg", "packageName---->"+packageName);
                    Log.v("msg", "getByPackage---->"+s);
                    Result<AppInfo> result = new Gson().fromJson(s, new TypeToken<Result<AppInfo>>() {}.getType());
                    if (result.isok()){
                        AppInfo appInfo = result.getData();
                        if (appInfo != null){
                            AppEventUtil.onClick(appInfo  , new DownloadListener(appInfo , params));
                        }

                    }else {
                        ToastUtils.showBottom(result.getMsg());
                    }

                }
            });

        }else {
            ToastUtils.showBottom("包名为空");
        }

    }
}
