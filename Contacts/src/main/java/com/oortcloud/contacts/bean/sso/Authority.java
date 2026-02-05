package com.oortcloud.contacts.bean.sso;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/16
 * Version 1.0
 * Description：权限处理
 */
public class Authority {


    private int id;
    private String uuid;
    //排序权限
    private int issort;
    //部门用户显示隐藏权限 0 不显 ， 1显示
    private int ishidden;
    //用户字段显示隐藏权限
    private int iscolumn;
    //设置下级管理员权限
    private int issubordinate;
    //一人人最多管理三个部门
    private List<String> deptcode;
    private String created_at;
    private String updated_at;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setIssort(int issort) {
        this.issort = issort;
    }

    public int getIssort() {
        return issort;
    }

    public void setIshidden(int ishidden) {
        this.ishidden = ishidden;
    }

    public int getIshidden() {
        return ishidden;
    }

    public void setIscolumn(int iscolumn) {
        this.iscolumn = iscolumn;
    }

    public int getIscolumn() {
        return iscolumn;
    }

    public void setIssubordinate(int issubordinate) {
        this.issubordinate = issubordinate;
    }

    public int getIssubordinate() {
        return issubordinate;
    }

    public void setDeptcode(List<String> deptcode) {
        this.deptcode = deptcode;
    }

    public List<String> getDeptcode() {
        return deptcode;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }


}
