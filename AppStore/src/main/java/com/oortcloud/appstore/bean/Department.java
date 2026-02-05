package com.oortcloud.appstore.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * @filename:
 * @function：部门类
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 09:55
 */
public class Department implements Serializable {
    //部门编码
    private String oort_dcode;
    //部门名称
    private String oort_dname;
    //上级部门编码
    private String oort_pdcode;
    //上级部门名称
    private String oort_pdname;
    //部门层级
    private int oort_dlevel;
    //部门关系
    private String oort_dpath;
    //部门关系编码
    private String oort_dcodepath;

    private int oort_dsort;

    private int hadchild;

    private int type;
    public Department(){}
    public Department(String oort_dcode , String oort_dname){
        this.oort_dcode = oort_dcode;
        this.oort_dname = oort_dname;
    }
    public String getOort_dcode() {
        return oort_dcode;
    }

    public void setOort_dcode(String oort_dcode) {
        this.oort_dcode = oort_dcode;
    }

    public String getOort_dname() {
        return oort_dname;
    }

    public void setOort_dname(String oort_dname) {
        this.oort_dname = oort_dname;
    }

    public String getOort_pdname() {
        return oort_pdname;
    }

    public void setOort_pdname(String oort_pdname) {
        this.oort_pdname = oort_pdname;
    }

    public int getOort_dlevel() {
        return oort_dlevel;
    }

    public void setOort_dlevel(int oort_dlevel) {
        this.oort_dlevel = oort_dlevel;
    }

    public String getOort_dpath() {
        return oort_dpath;
    }

    public void setOort_dpath(String oort_dpath) {
        this.oort_dpath = oort_dpath;
    }

    public String getOort_dcodepath() {
        return oort_dcodepath;
    }

    public void setOort_dcodepath(String oort_dcodepath) {
        this.oort_dcodepath = oort_dcodepath;
    }

    public int getOort_dsort() {
        return oort_dsort;
    }

    public void setOort_dsort(int oort_dsort) {
        this.oort_dsort = oort_dsort;
    }

    public int getHadchild() {
        return hadchild;
    }

    public void setHadchild(int hadchild) {
        this.hadchild = hadchild;
    }

    public int getType() {
        return type;
    }

    public void setType(int count) {
        this.type = count;
    }

    public String getOort_pdcode() {
        return oort_pdcode;
    }

    public void setOort_pdcode(String oort_pdcode) {
        this.oort_pdcode = oort_pdcode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Department){
            Department department = (Department) obj;
            return this.oort_dname.equals(department.getOort_dname());
        }
         return false;
    }

    @Override
    public String toString() {
        return "Department{" +
                "oort_dcode='" + oort_dcode + '\'' +
                ", oort_dname='" + oort_dname + '\'' +
                ", oort_pdcode='" + oort_pdcode + '\'' +
                ", oort_pdname='" + oort_pdname + '\'' +
                ", oort_dlevel=" + oort_dlevel +
                ", oort_dpath='" + oort_dpath + '\'' +
                ", oort_dcodepath='" + oort_dcodepath + '\'' +
                ", oort_dsort=" + oort_dsort +
                ", hadchild=" + hadchild +
                ", type=" + type +
                '}';
    }
}
