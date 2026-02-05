package com.oortcloud.utils;

import android.util.Log;

import com.oort.weichat.bean.Code;

import com.oort.weichat.bean.Depcode;
import com.oortcloud.appstore.AppStoreInit;

import com.oortcloud.appstore.utils.TokenManager;


import java.util.HashMap;
import java.util.Map;

/**
 * @author: Created by zzj on 2022/3/8
 * @Email: 465571041@qq.com
 * @Version: v1.0
 * @Descruotion: 地区检察院编码处理
 */
public class ProCodeUtils {
    private  Map<String , String> map = new HashMap<>();
    private static ProCodeUtils pro;
    public static ProCodeUtils getInstance(){
        if (pro == null){
            synchronized (AppStoreInit.class){
                if (pro == null){
                    pro = new ProCodeUtils();
                    pro.init();
                }
                return pro;
            }
        }
        return  pro;
    }

    private void init(){

        map.clear();
        map.put("深圳市人民检察院" ,"440300");
        map.put("前海蛇口自贸区检察院" ,"440391");
        map.put("福田区人民检察院" ,"440304");
        map.put("罗湖区人民检察院" ,"440303");
        map.put("盐田区人民检察院" ,"440308");
        map.put("南山区人民检察院" ,"440305");
        map.put("宝安区人民检察院" ,"440306");
        map.put("龙岗区人民检察院" ,"440307");
        map.put("龙华区人民检察院" ,"440309");
        map.put("坪山区人民检察院" ,"440310");
        map.put("光明区人民检察院" ,"440311");
        map.put("深汕特别合作区检察院" ,"440392");
    }

    public String getProCode(String name){
        String code = null;
        if (map != null){
            code = map.get(name);
            Log.v ( code,"------------xxx---------" );
        }
        return code;
    }
}

