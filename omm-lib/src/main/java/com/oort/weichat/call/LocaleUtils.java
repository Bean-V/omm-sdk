package com.oort.weichat.call;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LocaleUtils {

    /**
     * 强制设置应用 Locale 为「简体中文（中国）」，包含脚本信息 Hans
     */
    public static Context forceChineseLocale(Context context) {
        // 1. 构造包含语言、地区、脚本的 Locale（关键步骤）
        Locale chineseLocale = new Locale.Builder()
                .setLanguage("zh")       // 语言：中文
                .setRegion("CN")         // 地区：中国
                .setScript("Hans")       // 脚本：简体中文
                .build();

        // 2. 获取当前资源配置
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        // 3. 根据 Android 版本更新配置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+：用 LocaleList 管理，支持多语言和脚本
            android.os.LocaleList localeList = new android.os.LocaleList(chineseLocale);
            android.os.LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            // 生成新上下文并替换（必须用新上下文才能生效）
            context = context.createConfigurationContext(config);
        } else {
            // 旧版本（API < 24）：直接设置 locale（脚本信息可能被忽略，但语言和地区生效）
            config.locale = chineseLocale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        // 4. 更新应用全局上下文（可选，确保所有页面生效）
        if (context instanceof android.app.Application) {
            ((android.app.Application) context).getBaseContext().getResources()
                    .updateConfiguration(config, resources.getDisplayMetrics());
        }
        return context;
    }
}