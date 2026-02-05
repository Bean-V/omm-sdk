package com.oortcloud.contacts.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.Sort;
import com.oortcloud.contacts.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/21
 * Version 1.0
 * Description：
 */
public class JsonHelper {
    /**
     * 部门编码 转换为J'son数组
     * @param deptData
     * @return
     */
    public static String toJson(List<Department> deptData){
        String json = "";
        String[]  deptCodes = new String[deptData.size()];
        if (deptData != null && deptData.size() > 0) {
            for (int x = 0; x < deptData.size(); x++) {

                deptCodes[x] = deptData.get(x).getOort_dcode();
            }

            json = new Gson().toJson(deptCodes);
            Log.v("msg" , json);
        }
        return json;
    }

    /**
     * 部门编码转换为数组
     * @param deptData
     * @return
     */
    public static List toDeptCodeArray(List<Department> deptData){
        String json = "";
        List<String> deptCodes = new ArrayList();
        if (deptData != null && deptData.size() > 0) {
            for (int x = 0; x < deptData.size(); x++) {
                deptCodes.add(deptData.get(x).getOort_dcode());
            }
            json = new Gson().toJson(deptCodes);
        }
        return deptCodes;
    }
    /**
     * 用户UUID编码转换为数组
     * @param userData
     * @return
     */
    public static List toUUIDArray(List<Sort>  userData){
        String json = "";
        List<String> uuidArray = new ArrayList();
        if (userData != null && userData.size() > 0) {
            for (int x = 0; x < userData.size(); x++) {
                if (userData.get(x) instanceof UserInfo){
                    UserInfo userInfo = (UserInfo) userData.get(x);
                    uuidArray.add(userInfo.getOort_uuid());
                }
            }
            json = new Gson().toJson(uuidArray);
        }
        return uuidArray;
    }
    /**
     * 用户UUID编码转换为数组
     * @param userData
     * @return
     */
    public static List toUserArray(List<Sort>  userData){
        String json = "";
        List<UserInfo> uuidArray = new ArrayList();
        if (userData != null && userData.size() > 0) {
            for (int x = 0; x < userData.size(); x++) {
                if (userData.get(x) instanceof UserInfo){
                    UserInfo userInfo = (UserInfo) userData.get(x);
                    uuidArray.add(userInfo);
                }
            }
        }
        return uuidArray;
    }
}
