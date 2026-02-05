package com.oortcloud.appstore.utils;

import com.alibaba.fastjson.JSON;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.appstore.AppStoreInit;

import java.util.HashMap;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/12/20 09:56
 * @version： v1.0
 * @function： 管理使用的token
 */
public class TokenManager {
    private static TokenManager mTokenManager;
    private FastSharedPreferences mFP;
    public static TokenManager getInstance(){
        if (mTokenManager == null){
            synchronized (AppStoreInit.class){
                if (mTokenManager == null){
                    mTokenManager = new TokenManager();
                }
                return mTokenManager;
            }
        }
        return  mTokenManager;
    }

    private TokenManager(){
        mFP =  FastSharedPreferences.get("USERINFO_SAVE");
    }
    public String  toJson(){
        Map<String , String> jsonMap = new HashMap<>();
        jsonMap.put("token" ,  mFP.getString("token" , ""));
        jsonMap.put("szjcy_token" ,  mFP.getString("SZJCYToken" , "testszjcytoken"));
        return JSON.toJSONString(jsonMap);
    }
}
