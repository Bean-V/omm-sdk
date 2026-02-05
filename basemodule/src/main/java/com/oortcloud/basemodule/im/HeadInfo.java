package com.oortcloud.basemodule.im;

import java.util.HashMap;

public class HeadInfo {
    public static HashMap<String, Object> headParams = new HashMap<>();
    static {
        headParams.put("Content-Type","application/json;charset=UTF-8");
        headParams.put("AuthorizeInfo","CB36-D219-C641-F2FE");
//        headParams.put("userCardId","application/json;charset=UTF-8");
//        headParams.put("userName","application/json;charset=UTF-8");
//        headParams.put("userDept","application/json;charset=UTF-8");
//        headParams.put("userIp","application/json;charset=UTF-8");
//        headParams.put("userCredential","application/json;charset=UTF-8");
//        headParams.put("appCredential","application/json;charset=UTF-8");
    }

}
