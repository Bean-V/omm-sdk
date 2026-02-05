package com.oortcloud.contacts.utils;

import android.text.TextUtils;
import android.util.Log;


import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.DeptInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/21
 * Version 1.0
 * Description： 部门编码工具类
 */
public class DeptUtils {
    /**
     * 解析部门编码
     * @param dNamePath
     * @param dCodePath
     * @return
     */
    public static List splitDepartment(String dNamePath , String dCodePath) {
        String namePath = dNamePath.substring(1 , dNamePath.length() -1);
        String deptName[] = namePath.split("/");
        String codePath = dCodePath.substring(1 , dCodePath.length() -1);
        String deptCode[] = codePath.split("/");
        List<Department> departList = new ArrayList();
            for (int x = 0 ; x < deptName.length ; x++){
                Department department = new Department();
                department.setOort_dname(deptName[x]);
                department.setOort_dcode(deptCode[x]);
                departList.add(department);
            }
        return departList;
    }
    public static List splitDeptInfo(String dNamePath , String dCodePath) {
        List<DeptInfo> departList = new ArrayList();
        String dep[] = dNamePath.split("/");
        String code[] = dCodePath.split("/");
        for (int x = 0 ; x < dep.length; x++){
            if (TextUtils.isEmpty(dep[x])){
                continue;
            }
            DeptInfo deptInfo = new DeptInfo();
            deptInfo.setOort_dname(dep[x]);
            deptInfo.setOort_dcode(code[x]);
            departList.add(deptInfo);
        }
        Log.v("msg" ,"departList  " +departList.size() );
        return departList;
    }


}
