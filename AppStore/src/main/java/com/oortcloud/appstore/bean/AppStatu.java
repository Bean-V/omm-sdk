package com.oortcloud.appstore.bean;

public class AppStatu {



    public static int homeRefrash = 0;
    public int appStatu = 0;
    private static AppStatu logUtil;

    public int progressStyle = 0;

    //单例模式初始化
    public static AppStatu getInstance() {
        if (logUtil == null) {
            logUtil = new AppStatu();

        }
        return logUtil;
    }
}
