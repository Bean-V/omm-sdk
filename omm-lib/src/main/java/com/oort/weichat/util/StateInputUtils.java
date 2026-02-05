package com.oort.weichat.util;

import android.util.Log;

import com.oort.weichat.R;
import com.oortcloud.appstore.AppStoreInit;

import java.util.HashMap;
import java.util.Map;

public class StateInputUtils {
    private Map<String , String> map = new HashMap<>();
    private static StateInputUtils pro;
    public static StateInputUtils getInstance(){
        if (pro == null){
            synchronized (AppStoreInit.class){
                if (pro == null){
                    pro = new StateInputUtils();
                    pro.init();
                }
                return pro;
            }
        }
        return  pro;
    }

    private void init(){
        map.clear();
        map.put("美滋滋" , String.valueOf(R.drawable.state_mzz));
        map.put("裂开" ,String.valueOf(R.drawable.state_lk));
        map.put("求锦鲤" ,String.valueOf(R.drawable.state_qjl));
        map.put("等天晴" ,String.valueOf(R.drawable.state_dtq));
        map.put("疲惫" ,String.valueOf(R.drawable.state_pib));
        map.put("发呆" ,String.valueOf(R.drawable.state_fd));
        map.put("emo" ,String.valueOf(R.drawable.state_emo));
        map.put("胡思乱想" ,String.valueOf(R.drawable.state_hslx));
        map.put("工作中" ,String.valueOf(R.drawable.state_gzzh));
        map.put("沉迷学习" ,String.valueOf(R.drawable.state_cmxx));
        map.put("忙" ,String.valueOf(R.drawable.state_mang));
        map.put("摸鱼" ,String.valueOf(R.drawable.state_my));
        map.put("出差" ,String.valueOf(R.drawable.state_cch));
        map.put("下班" ,String.valueOf(R.drawable.state_xb));
        map.put("勿扰模式" ,String.valueOf(R.drawable.state_wrms));
        map.put("浪" ,String.valueOf(R.drawable.state_lang));
        map.put("打卡" ,String.valueOf(R.drawable.state_dk));
        map.put("跑步" ,String.valueOf(R.drawable.state_pb));
        map.put("喝咖啡" ,String.valueOf(R.drawable.state_hkf));
        map.put("喝奶茶" ,String.valueOf(R.drawable.state_hnc));
        map.put("自拍" ,String.valueOf(R.drawable.state_zp));
        map.put("干饭" ,String.valueOf(R.drawable.state_gf));
        map.put("自定义" ,"");
    }

    public String getProCode(String name){
        String code = null;
        if (map != null){
            code = map.get(name);
            Log.v ("msgxks",code);
        }
        return code;
    }
}
