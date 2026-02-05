package com.oort.weichat.call;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LocaleHelper {

    /**
     * 强制设置 Locale 为「简体中文（中国）」
     * @param context 上下文（如 Activity 或 Application）
     * @return 应用新 Locale 后的上下文
     */
    public static Context setChineseLocale(Context context) {
        return setLocale(context, "zh", "CN", "Hans");
    }

    /**
     * 通用 Locale 设置方法（支持自定义语言、地区、脚本）
     */
    public static Context setLocale(Context context, String language, String country, String script) {
        Locale targetLocale = new Locale(language, country, script);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android N+：使用 LocaleList 管理多语言
            android.os.LocaleList localeList = new android.os.LocaleList(targetLocale);
            android.os.LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            // 生成新上下文（需用新上下文替代原上下文）
            context = context.createConfigurationContext(config);
        } else {
            // 旧版本：直接设置 locale
            config.locale = targetLocale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        return context;
    }
}