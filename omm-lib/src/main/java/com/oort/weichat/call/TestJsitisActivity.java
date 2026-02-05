package com.oort.weichat.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.oney.WebRTCModule.WebRTCModuleOptions;
import com.oort.weichat.ui.ReactNativeSSLHelper;
import com.oort.weichat.ui.ReactWebSocketSSLHelper;
import com.oortcloud.basemodule.constant.Constant;

import org.jitsi.meet.sdk.BuildConfig;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.webrtc.Logging;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;

public class TestJsitisActivity extends JitsiMeetActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE
            = (int) (Math.random() * Short.MAX_VALUE);

    private BroadcastReceiver broadcastReceiver;
    private boolean configurationByRestrictions = false;
    private String defaultURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        String  s = getResources().getConfiguration().locale.toLanguageTag();

        String displayLanguage = Locale.getDefault().getDisplayName();

        String Language = Locale.getDefault().getLanguage();
        String systemLang = getAppLanguage(this);
        Log.d("当前语言", systemLang);
        // 强制中文 Locale
//        Locale locale = new Locale("zh", "CN");
//        Locale.setDefault(locale);
//
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // SSL 配置（按你原来的逻辑保留）
        ReactNativeSSLHelper.initReactNativeUnsafeSSL(this);
        ReactWebSocketSSLHelper.initInsecureSSL();

        JitsiMeet.showSplashScreen(this);

        WebRTCModuleOptions options = WebRTCModuleOptions.getInstance();
        options.loggingSeverity = Logging.Severity.LS_ERROR;

        super.onCreate(null);

        // 获取系统当前语言（如 "zh-CN"）
        systemLang = getAppLanguage(this);
        Log.d("当前语言", systemLang);
    }

//    @Override
    protected void attachBaseContext(Context base) {
        //super.attachBaseContext(LocaleHelper.setChineseLocale(base));
        super.attachBaseContext(LocaleUtils.forceChineseLocale(base));

    }

         //   super.attachBaseContext(LocaleUtils.forceChineseLocale(base));

    public static String getAppLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = config.getLocales().get(0);
        } else {
            locale = config.locale;
        }
        return locale.getLanguage() + "-" + locale.getCountry();
    }
    @Override
    protected boolean extraInitialize() {
        Log.d(this.getClass().getSimpleName(), "LIBRE_BUILD=" + BuildConfig.LIBRE_BUILD);
        try {
            Class<?> cls = Class.forName("org.jitsi.meet.GoogleServicesHelper");
            Method m = cls.getMethod("initialize", JitsiMeetActivity.class);
            m.invoke(null, this);
        } catch (Exception e) {
            // Ignore
        }

        if (BuildConfig.DEBUG) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent
                        = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));

                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void initialize() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                leave();
                recreate();
            }
        };
        registerReceiver(broadcastReceiver,
                new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED));

        setJitsiMeetConferenceDefaultOptions();
        super.initialize();
    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }

    private void setJitsiMeetConferenceDefaultOptions() {
        defaultURL = Constant.IM_JIT_SI_URL;

        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(buildURL(defaultURL))
                // ✅ 正确的语言设置：覆盖 Web 层的默认语言
//                .setConfigOverride("defaultLanguage", "zh-CN")
//                .setConfigOverride("userLanguage", "zh-CN")
                .setFeatureFlag("welcomepage.enabled", true)
                .setFeatureFlag("server-url-change.enabled", !configurationByRestrictions)
                .build();

        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                initialize();
                return;
            }
            throw new RuntimeException("Overlay permission is required when running in Debug mode.");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_MENU) {
            JitsiMeet.showDevOptions();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        Log.d(TAG, "Is in picture-in-picture mode: " + isInPictureInPictureMode);
    }

    private @Nullable URL buildURL(String urlStr) {
        try {
            return new URL(urlStr);
        } catch (Exception e) {
            return null;
        }
    }
}
