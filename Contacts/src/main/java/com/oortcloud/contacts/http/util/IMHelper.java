package com.oortcloud.contacts.http.util;

import androidx.annotation.NonNull;

import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/11 19:49
 */
public class IMHelper {
    //IM接口验参
    public static void generateHttpParam( HashMap<String, Object> params){

        String time = String.valueOf(System.currentTimeMillis());

        IMUserInfoUtil ommUserInfoUtil = IMUserInfoUtil.getInstance();
        String macContent = Constant.IM_API_KEY + ommUserInfoUtil.getUserId() + ommUserInfoUtil.getToken() + joinValues(params) + time;

        String mac = MAC.encodeBase64(macContent.getBytes(), Base64.decode(ommUserInfoUtil.getHttpKey()));

        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("salt",time);
        params.put("secret", mac);

    }

    public static String joinValues(Map<String, Object> map) {
        TreeMap<String, Object> treeMap = new TreeMap<>(String::compareTo);
        treeMap.putAll(map);
        return joinSortedMap(treeMap);
    }

    @NonNull
    public static String joinSortedMap(TreeMap<String, Object> treeMap) {
        StringBuilder sb = new StringBuilder();
        for (Object value : treeMap.values()) {
            sb.append(value);
        }
        return sb.toString();
    }
}
