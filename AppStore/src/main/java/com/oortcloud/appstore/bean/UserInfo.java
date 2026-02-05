package com.oortcloud.appstore.bean;

import java.io.Serializable;

/**
 * @filename:
 * @function：部门人员信息
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 10:18
 */
public class UserInfo  implements Serializable {
    //UUID
    private String oort_uuid;
    //登录id
    private String oort_loginid;
    //名称
    private String oort_name;
    //姓名全拼
    private String oort_namepy;
    //姓名首字母
    private String oort_namefl;
    //个人编号
    private String oort_code;
    //部门名称
    private String oort_depname;
    //部门编码
    private String oort_depcode;
    //
    private String oort_rdepname;
    //
    private String oort_idcard;
    //性别
    private int oort_sex;
    //头像
    private String oort_photo;
    //号码
    private String oort_phone;
    //邮箱
    private String oort_email;

    private int oort_usertype;

    private String oort_postname;

    private String oort_jobname;

    private String oort_office;

    private String oort_tel;

    private String imstatus;

    private int oort_status;

    private int oort_sort;

    public String getOort_uuid() {
        return oort_uuid;
    }

    public void setOort_uuid(String oort_uuid) {
        this.oort_uuid = oort_uuid;
    }

    public String getOort_loginid() {
        return oort_loginid;
    }

    public void setOort_loginid(String oort_loginid) {
        this.oort_loginid = oort_loginid;
    }

    public String getOort_name() {
        return oort_name;
    }

    public void setOort_name(String oort_name) {
        this.oort_name = oort_name;
    }

    public String getOort_namepy() {
        return oort_namepy;
    }

    public void setOort_namepy(String oort_namepy) {
        this.oort_namepy = oort_namepy;
    }

    public String getOort_namefl() {
        return oort_namefl;
    }

    public void setOort_namefl(String oort_namefl) {
        this.oort_namefl = oort_namefl;
    }

    public String getOort_code() {
        return oort_code;
    }

    public void setOort_code(String oort_code) {
        this.oort_code = oort_code;
    }

    public String getOort_depname() {
        return oort_depname;
    }

    public void setOort_depname(String oort_depname) {
        this.oort_depname = oort_depname;
    }

    public String getOort_depcode() {
        return oort_depcode;
    }

    public void setOort_depcode(String oort_depcode) {
        this.oort_depcode = oort_depcode;
    }

    public String getOort_rdepname() {
        return oort_rdepname;
    }

    public void setOort_rdepname(String oort_rdepname) {
        this.oort_rdepname = oort_rdepname;
    }

    public String getOort_idcard() {
        return oort_idcard;
    }

    public void setOort_idcard(String oort_idcard) {
        this.oort_idcard = oort_idcard;
    }

    public int getOort_sex() {
        return oort_sex;
    }

    public void setOort_sex(int oort_sex) {
        this.oort_sex = oort_sex;
    }

    public String getOort_photo() {
        return oort_photo;
    }

    public void setOort_photo(String oort_photo) {
        this.oort_photo = oort_photo;
    }

    public String getOort_phone() {
        return oort_phone;
    }

    public void setOort_phone(String oort_phone) {
        this.oort_phone = oort_phone;
    }

    public String getOort_email() {
        return oort_email;
    }

    public void setOort_email(String oort_email) {
        this.oort_email = oort_email;
    }

    public int getOort_usertype() {
        return oort_usertype;
    }

    public void setOort_usertype(int oort_usertype) {
        this.oort_usertype = oort_usertype;
    }

    public String getOort_postname() {
        return oort_postname;
    }

    public void setOort_postname(String oort_postname) {
        this.oort_postname = oort_postname;
    }

    public String getOort_jobname() {
        return oort_jobname;
    }

    public void setOort_jobname(String oort_jobname) {
        this.oort_jobname = oort_jobname;
    }

    public String getOort_office() {
        return oort_office;
    }

    public void setOort_office(String oort_office) {
        this.oort_office = oort_office;
    }

    public String getOort_tel() {
        return oort_tel;
    }

    public void setOort_tel(String oort_tel) {
        this.oort_tel = oort_tel;
    }

    public String getImstatus() {
        return imstatus;
    }

    public void setImstatus(String imstatus) {
        this.imstatus = imstatus;
    }

    public int getOort_status() {
        return oort_status;
    }

    public void setOort_status(int oort_status) {
        this.oort_status = oort_status;
    }

    public int getOort_sort() {
        return oort_sort;
    }

    public void setOort_sort(int oort_sort) {
        this.oort_sort = oort_sort;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "oort_uuid='" + oort_uuid + '\'' +
                ", oort_loginid='" + oort_loginid + '\'' +
                ", oort_name='" + oort_name + '\'' +
                ", oort_namepy='" + oort_namepy + '\'' +
                ", oort_namefl='" + oort_namefl + '\'' +
                ", oort_code='" + oort_code + '\'' +
                ", oort_depname='" + oort_depname + '\'' +
                ", oort_depcode='" + oort_depcode + '\'' +
                ", oort_rdepname='" + oort_rdepname + '\'' +
                ", oort_idcard='" + oort_idcard + '\'' +
                ", oort_sex=" + oort_sex +
                ", oort_photo='" + oort_photo + '\'' +
                ", oort_phone='" + oort_phone + '\'' +
                ", oort_email='" + oort_email + '\'' +
                ", oort_usertype=" + oort_usertype +
                ", oort_postname='" + oort_postname + '\'' +
                ", oort_jobname='" + oort_jobname + '\'' +
                ", oort_office='" + oort_office + '\'' +
                ", oort_tel='" + oort_tel + '\'' +
                ", imstatus='" + imstatus + '\'' +
                ", oort_status=" + oort_status +
                ", oort_sort=" + oort_sort +
                '}';
    }
}
