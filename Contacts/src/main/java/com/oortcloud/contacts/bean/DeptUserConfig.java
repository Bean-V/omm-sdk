package com.oortcloud.contacts.bean;

import androidx.annotation.NonNull;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/26
 * Version 1.0
 * Description：用户与部门配置信息
 *
 * oort_code	integer($int64)
 * example: 1
 * 用户编号
 *
 * oort_depcode	string
 * example: 9900
 * 部门编码
 *
 * oort_depname	integer($int64)
 * example: 1
 * 部门名称
 *
 * oort_email	integer($int64)
 * example: 1
 * 电子邮箱
 *
 * oort_idcard	integer($int64)
 * example: 1
 * 身份证号码
 *
 * oort_jobname	integer($int64)
 * example: 1
 * 职务名称
 *
 * oort_loginid	integer($int64)
 * example: 1
 * 登录ID
 *
 * oort_name	integer($int64)
 * example: 1
 * 姓名
 *
 * oort_namefl	integer($int64)
 * example: 1
 * 姓名拼音首字母
 *
 * oort_namepy	integer($int64)
 * example: 1
 * 姓名拼音
 *
 * oort_office	integer($int64)
 * example: 1
 * 办公室门牌号
 *
 * oort_phone	integer($int64)
 * example: 1
 * 手机号码
 *
 * oort_photo	integer($int64)
 * example: 1
 * 头像
 *
 * oort_postname	integer($int64)
 * example: 1
 * 岗位名称
 *
 * oort_pphone	integer($int64)
 * example: 1
 * 备用号码
 *
 * oort_remark	integer($int64)
 * example: 1
 * 备注/介绍
 *
 * oort_sex	integer($int64)
 * example: 1
 * 性别
 *
 * oort_tel	integer($int64)
 * example: 1
 * 办公室电话
 */
public class DeptUserConfig implements Cloneable{
    private int oort_code;
    private int oort_depname;
    private int oort_email;
    private int oort_idcard;
    private int oort_jobname;
    private int oort_loginid;
    private int oort_name;
    private int oort_namefl;
    private int oort_namepy;
    private int oort_office;
    private int oort_phone;
    private int oort_photo;
    private int oort_postname;
    private int oort_pphone;
    private int oort_remark;
    private int oort_sex;
    private int oort_tel;
    public void setOort_code(int oort_code) {
        this.oort_code = oort_code;
    }

    public int getOort_code() {
        return oort_code;
    }

    public void setOort_depname(int oort_depname) {
        this.oort_depname = oort_depname;
    }

    public int getOort_depname() {
        return oort_depname;
    }

    public void setOort_email(int oort_email) {
        this.oort_email = oort_email;
    }

    public int getOort_email() {
        return oort_email;
    }

    public void setOort_idcard(int oort_idcard) {
        this.oort_idcard = oort_idcard;
    }

    public int getOort_idcard() {
        return oort_idcard;
    }

    public void setOort_jobname(int oort_jobname) {
        this.oort_jobname = oort_jobname;
    }

    public int getOort_jobname() {
        return oort_jobname;
    }

    public void setOort_loginid(int oort_loginid) {
        this.oort_loginid = oort_loginid;
    }

    public int getOort_loginid() {
        return oort_loginid;
    }

    public void setOort_name(int oort_name) {
        this.oort_name = oort_name;
    }

    public int getOort_name() {
        return oort_name;
    }

    public void setOort_namefl(int oort_namefl) {
        this.oort_namefl = oort_namefl;
    }

    public int getOort_namefl() {
        return oort_namefl;
    }

    public void setOort_namepy(int oort_namepy) {
        this.oort_namepy = oort_namepy;
    }

    public int getOort_namepy() {
        return oort_namepy;
    }

    public void setOort_office(int oort_office) {
        this.oort_office = oort_office;
    }

    public int getOort_office() {
        return oort_office;
    }

    public void setOort_phone(int oort_phone) {
        this.oort_phone = oort_phone;
    }

    public int getOort_phone() {
        return oort_phone;
    }

    public void setOort_photo(int oort_photo) {
        this.oort_photo = oort_photo;
    }

    public int getOort_photo() {
        return oort_photo;
    }

    public void setOort_postname(int oort_postname) {
        this.oort_postname = oort_postname;
    }

    public int getOort_postname() {
        return oort_postname;
    }

    public void setOort_pphone(int oort_pphone) {
        this.oort_pphone = oort_pphone;
    }

    public int getOort_pphone() {
        return oort_pphone;
    }

    public void setOort_remark(int oort_remark) {
        this.oort_remark = oort_remark;
    }

    public int getOort_remark() {
        return oort_remark;
    }

    public void setOort_sex(int oort_sex) {
        this.oort_sex = oort_sex;
    }

    public int getOort_sex() {
        return oort_sex;
    }

    public void setOort_tel(int oort_tel) {
        this.oort_tel = oort_tel;
    }

    public int getOort_tel() {
        return oort_tel;
    }


    @NonNull
    @Override
    public DeptUserConfig clone(){
        DeptUserConfig config  = null;
        try {
            config  = (DeptUserConfig)super.clone();
        }catch (CloneNotSupportedException e){

        }
        return config;
    }

    private String oort_depcode;
    private String oort_uuid;

    public void setOort_depcode(String oort_depcode) {
        this.oort_depcode = oort_depcode;
    }

    public String getOort_depcode() {
        return oort_depcode;
    }

    public void setOort_uuid(String oort_uuid) {
        this.oort_uuid = oort_uuid;
    }

    public String getOort_uuid() {
        return oort_uuid;
    }
    @Override
    public String toString() {
        return "DeptUserConfig{" +
                "oort_code=" + oort_code +
                ", oort_depname=" + oort_depname +
                ", oort_email=" + oort_email +
                ", oort_idcard=" + oort_idcard +
                ", oort_jobname=" + oort_jobname +
                ", oort_loginid=" + oort_loginid +
                ", oort_name=" + oort_name +
                ", oort_namefl=" + oort_namefl +
                ", oort_namepy=" + oort_namepy +
                ", oort_office=" + oort_office +
                ", oort_phone=" + oort_phone +
                ", oort_photo=" + oort_photo +
                ", oort_postname=" + oort_postname +
                ", oort_pphone=" + oort_pphone +
                ", oort_remark=" + oort_remark +
                ", oort_sex=" + oort_sex +
                ", oort_tel=" + oort_tel +
                '}';
    }
}
