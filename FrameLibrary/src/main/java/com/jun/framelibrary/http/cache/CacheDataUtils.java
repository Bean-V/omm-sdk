package com.jun.framelibrary.http.cache;

import android.text.TextUtils;
import android.util.Log;

import com.jun.baselibrary.db.DAO;
import com.jun.baselibrary.db.DAOFactory;
import com.jun.baselibrary.http.EngineCallBack;

import java.security.MessageDigest;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/8 16:50
 * Version 1.0
 * Description：缓存工具类
 */
public class CacheDataUtils {
    /**
     * 获取缓存数据
     * @param finalUrl
     * @return
     */
    private static String getCacheResultJson(String finalUrl){
        String finalUrlMD5 = string2MD5(finalUrl);
        //数据库查询
        DAO<CacheData> iDataPool = DAOFactory.getInstance().getDAO(CacheData.class);

        //url的http包含了：冒号 需要转换 简单加密进行转换处理
        List<CacheData> cacheDataList = iDataPool.query().selection("mUrlKey=?")
                .selectionArgs(finalUrlMD5).query();
        //判断是否有返回数据
        if (!cacheDataList.isEmpty()) {
            CacheData cacheData = cacheDataList.get(0);
            String resultJson = cacheData.getResultJson();
            // //判断是否为空
            if (!TextUtils.isEmpty(resultJson)){
                return resultJson;
            }
        }
        return null;
    }

    /**
     * 缓存数据
     */
    private static long cacheData(String finalUrl, String result) {
        String finalUrlMD5 = string2MD5(finalUrl);

        DAO<CacheData> iDataPool = DAOFactory.getInstance().getDAO(CacheData.class);
        //删除数据
        iDataPool.delete("mUrlKey=?", finalUrlMD5);
        //插入数据
        return iDataPool.insert(new CacheData(finalUrl, result));
    }

    /**利用MD5进行加密
     * @param finalUrl  待加密的字符串
     */
    private static String string2MD5(String finalUrl){
        MessageDigest md5;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            return "";
        }
        char[] charArray = finalUrl.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    //判断是否读取缓存数据
    public static void isCache(boolean cache,String finalUrl, EngineCallBack callBack) {
        if (cache) {
            String cacheResultJson = getCacheResultJson(finalUrl);
            if (!TextUtils.isEmpty(cacheResultJson)) {
                callBack.onSuccess(cacheResultJson);
                // 需要缓存，而且数据库有缓存,直接就去执行，里面执行成功
                Log.e("TAG","以读到缓存：-->"+ cacheResultJson);
            }
        }
    }
    // 缓存数据
    public static boolean whetherCache(boolean cache, String finalUrl, String resultJson) {
        if (cache) {
            String cacheResultJson = getCacheResultJson(finalUrl);
            if (!TextUtils.isEmpty(resultJson)) {
                // 内容一样，不需要在缓存
                if (resultJson.equals(cacheResultJson)){
                    Log.e("TAG","缓存数据与接口获取数据一致：-->");
                    return true;
                }else {
                    cacheData(finalUrl, resultJson);
                }
            }
        }
        return false;
    }
}
