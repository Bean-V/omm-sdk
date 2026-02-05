package com.sentaroh.android.upantool;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

//import com.github.moduth.blockcanary.BlockCanary;
import com.sentaroh.android.upantool.languagelib.MultiLanguageUtil;
import com.sentaroh.android.upantool.sysTask.TastTool;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xupdate.XUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/9 15:59
 * @version： v1.0
 * @function：
 */
public class BaseApplication extends Application {

    private static final String TAG = "langage";
    private static Context mContext;
    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //BlockCanary.install(this, new AppBlockCanaryContext()).start();
        //changeAppLanguage();



        boolean isSilentInstall = false;//FastSharedPreferences.get("USERINFO_SAVE").getBoolean("openUpdate",false);
        XUpdate.get()
                .debug(false)
                .isWifiOnly(false)                                               // By default, only version updates are checked under WiFi
                .isGet(false)                                                    // The default setting uses Get request to check versions
                .isAutoMode(isSilentInstall)                                              // The default setting is non automatic mode
                .param("versionCode", UpdateUtils.getVersionCode(this))         // Set default public request parameters
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     // Set listening for version update errors
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          // Handling different errors
                            Log.d("zlm",error.toString());
                        }
                    }
                })
                .supportSilentInstall(isSilentInstall)                                     // Set whether silent installation is supported. The default is true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           // This must be set! Realize the network request function.
                .init(this);

        initWebView();


        TastTool.getInstance().initData(this);

    }





    @Override
    protected void attachBaseContext(Context base) {
        Log.e(TAG, "attachBaseContext");
        MultiLanguageUtil.getInstance().saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
        //app刚启动getApplicationContext()为空
        MultiLanguageUtil.getInstance().setConfiguration(getApplicationContext());
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //app刚启动不一定调用onConfigurationChanged
        Log.e(TAG, "onConfigurationChanged");
        MultiLanguageUtil.getInstance().setConfiguration(getApplicationContext());
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            WebView.setDataDirectorySuffix(processName);
        }
    }


    public void changeAppLanguage() {
        String sta = Store.getLanguageLocal(this);

        if(sta.equals("")){
            sta = "zh_CN";
        }
        if(sta != null && !"".equals(sta)){
            // 本地语言设置
     /*       Locale myLocale = new Locale(sta);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);*/
            // 本地语言设置
            //  Locale locale = new Locale("ug", Locale.CHINA.getCountry());
            Locale myLocale=null;
            if(sta.contains("zh")){
                myLocale = new Locale("zh_CN",Locale.CHINESE.getCountry());
            }else  if(sta.contains("en")){
                myLocale = new Locale( "en",Locale.ENGLISH.getCountry());
            }
            else  if(sta.contains("ja")){
                myLocale = new Locale( "ja",Locale.JAPANESE.getCountry());
            }
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

    }



    public static BaseApplication getInstance(){
        if (mApplication == null){
            synchronized (BaseApplication.class){
                if (mApplication == null){
                    mApplication = new BaseApplication();
                }
                return mApplication;
            }
        }
        return  mApplication;
    }

    public final  Context getContext(){
        return mContext;
    }


    private class OKHttpUpdateHttpService implements IUpdateHttpService {
        @Override
        public void asyncGet(@NonNull @NotNull String url, @NonNull @NotNull Map<String, Object> params, @NonNull @NotNull Callback callBack) {
            Log.d("zlm","asyncGet " + url);
        }

        @Override
        public void asyncPost(@NonNull @NotNull String url, @NonNull @NotNull Map<String, Object> params, @NonNull @NotNull Callback callBack) {
            Log.d("zlm","asyncPost " + url);
        }

        @Override
        public void download(@NonNull @NotNull String url, @NonNull @NotNull String path, @NonNull @NotNull String fileName, @NonNull @NotNull DownloadCallback callback) {
            Log.d("zlm","download " + url);
        }

        @Override
        public void cancelDownload(@NonNull @NotNull String url) {
            Log.d("zlm","cancelDownload " + url);
        }
    }

}
