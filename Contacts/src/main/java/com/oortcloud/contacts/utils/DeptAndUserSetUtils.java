package com.oortcloud.contacts.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/25
 * Version 1.0
 * Description：管理多选集合列表
 */
public class DeptAndUserSetUtils {
    private static Map<String, List<Department>> deptMap = new TreeMap<>();
    private static Map<String, List<UserInfo>> userMap = new TreeMap<>();
    private static String mDeptCode ="";
    private static List<Department> getDepartments(String deptCode){

          return deptMap.get(deptCode);
    }

    public static void add(String deptCode, Department department){
        List<Department> departments = getDepartments(deptCode);
        mDeptCode = deptCode;
        if (departments == null ){
            departments = new LinkedList<>();
            deptMap.put(deptCode, departments);
        }
        if (!departments.contains(department)){
            departments.add(department);
        }

    }

    public static void addAll(String deptCode, List<Department> departments){
        mDeptCode = deptCode;
        deptMap.put(deptCode, departments);
    }

    public static void remove(String deptCode, Department department){
        List<Department> departments = getDepartments(deptCode);
        if (departments != null ){
            departments.remove(department);
        }
    }

    public static boolean contains(String deptCode ,Department childDept){
        boolean contains = false;
        List<Department> departments = getDepartments(deptCode);
        if (departments != null ){
            contains = departments.contains(childDept);
        }
        return contains;
    }
    public static List<Department> getDeptList(){

        return getDepartments(mDeptCode);
    }
    public static int getDeptSize(String deptCode){
        List<Department> departments = getDepartments(deptCode);
        return  departments != null ? departments.size():0;
    }
    public static int getDeptSize(){
        List<Department> departments = getDepartments(mDeptCode);
        return  departments != null ? departments.size():0;
    }
    public static String getDeptGather(){
        String deptGather = "";
        List<Department> departments = getDeptList();
        for (Department department : departments){
            deptGather += department.getOort_dcode() +",";
        }
        return deptGather.substring(0 , deptGather.length()-1);
    }

    public static void clearDept(String deptCode){
        deptMap.remove(deptCode);
    }


    //--User--------------------------------------------
    private static List<UserInfo> getUsers(String deptCode){
        return userMap.get(deptCode);
    }

    public static void add(String deptCode, UserInfo userInfo){
        List<UserInfo> UserList =  getUsers(deptCode);
        mDeptCode = deptCode;
        if (UserList == null ){
            UserList = new LinkedList<>();
            userMap.put(deptCode, UserList);
        }
        if (!UserList.contains(userInfo)){
            UserList.add(userInfo);
        }
    }

    public static void addUserAll(String deptCode, List<UserInfo> UserInfoList){
        mDeptCode = deptCode;
        userMap.put(deptCode, UserInfoList);
    }

    public static void remove(String deptCode, UserInfo userInfo){
        List<UserInfo> UserList =  getUsers(deptCode);
        if (UserList != null ){
            UserList.remove(userInfo);
        }
    }
    public static int getUserSize(String deptCode){
        List<UserInfo> UserList =  getUsers(deptCode);
        return  UserList != null ? UserList.size(): 0;
    }
    public static int getUserSize(){
        List<UserInfo> UserList =  getUsers(mDeptCode);
        return  UserList != null ? UserList.size(): 0;
    }
    public static boolean contains(String deptCode ,UserInfo userInfo){
        boolean contains = false;
        List<UserInfo> UserList =  getUsers(deptCode);
        if (UserList != null ){
            contains = UserList.contains(userInfo);
        }
        return contains;
    }
    public static List<UserInfo> getUserList(){

        return  getUsers(mDeptCode);
    }
    public static String getUserGather(){
        String userGather = "";
        List<UserInfo> UserList = getUserList();
        for (UserInfo userInfo : UserList){
            userGather += userInfo.getOort_uuid() +",";
        }
        return userGather.substring(0 , userGather.length()-1);
    }
    public static void clearUser(String deptCode){
        userMap.remove(deptCode);
    }

    public static void clear(){
        deptMap.clear();
        userMap.clear();
    }
    public static String getDeptCode(){
        return mDeptCode;
    }
}
