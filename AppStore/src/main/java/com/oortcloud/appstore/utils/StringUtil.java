package com.oortcloud.appstore.utils;

/**
 * @ProjectName: AppStore-master
 * @FileName: StringUtil.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 20/03/06 15:58
 * @Version: 1.0
 */
public class StringUtil {
    public static String isContains(String aapURL){
        if (aapURL.endsWith(".zip")){
            return ".zip";
        }
        else if (aapURL.endsWith(".apk")){
            return ".apk";
        }
        else if(aapURL.endsWith(".html")){
            return ".html";
        }
        return  null;
    }

}
