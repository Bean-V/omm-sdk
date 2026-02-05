package com.oortcloud.contacts.bean;

import com.oortcloud.basemodule.constant.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @filename:
 * @function：部门人员信息
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 10:18
 */
public class UserInfo extends Sort implements Serializable {
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
    //关联其他部门名称
    private String oort_rdepname;
    //关联其他部门编码
    private String oort_rdepcode;
    //证件号码如身份证号
    private String oort_idcard;
    //性别 0未知，1男，2女，3其它
    private int oort_sex;
    //头像地址
    private String oort_photo;
    //手机号码
    private String oort_phone;
    //私有号码
    private String oort_pphone;
    //邮箱
    private String oort_email;
    //用户类型 用户账号类型,1正式账号,2其它账号...9测试账号
    private int oort_usertype;
    //岗位名称
    private String oort_postname;
    //职务名称
    private String oort_jobname;
    //办公室门牌号
    private String oort_office;
    //办公室电话
    private String oort_tel;
    //用户状态 0禁用,1正常...9删除
    private int oort_status;
    //排序ID(越小越前)
    protected   int oort_sort;

    //部门编码 深圳检察院
    private String bmbm;
    //贡献值(10分钟同步一次)
    private int contribute;
    //单位编码 深圳检察院
    private String dwbm;
    //粉丝数(10分钟同步一次)
    private int fans;
    //父部门编码
    private String fbmbm;
    //荣誉值(10分钟同步一次)
    private int honor;
    //IM通讯号
    private String imaccount;
    //IM用户ID
    private String imuserid;
    //用户离职状态 1:在职;2:离职
    private int iswork;

    public List<DeptInfo> getOort_dept_list() {
        return oort_dept_list;
    }

    public void setOort_dept_list(List<DeptInfo> oort_dept_list) {
        this.oort_dept_list = oort_dept_list;
    }

    public String getTwoLevelDepart() {

        if(oort_dept_list != null && oort_dept_list.size() > 0){
            DeptInfo info =  oort_dept_list.get(0);
            String path = info.getOort_dpath();
            String [] names = path.split("/");
            String pname = "";
            pname = oort_depname;
            if(names.length > 0) {
                ArrayList list = new ArrayList<>();
                list.addAll(Arrays.asList(names));
                int index = list.indexOf(oort_depname);
                if (index > 1) {
                    pname = list.get(index - 1) + "/" + oort_depname;
                } else {
                    pname = oort_depname;
                }
            }
            return pname;
        }
        return oort_depname;
    }

    private List<DeptInfo> oort_dept_list;

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
        if(Constant.IsShowTwoLevelPart){
            return getTwoLevelDepart();
        }
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

    public String getOort_rdepcode() {
        return oort_rdepcode;
    }

    public void setOort_rdepcode(String oort_rdepcode) {
        this.oort_rdepcode = oort_rdepcode;
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

    public String getOort_pphone() {
        return oort_pphone;
    }

    public void setOort_pphone(String oort_pphone) {
        this.oort_pphone = oort_pphone;
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

    public int getOort_status() {
        return oort_status;
    }

    public void setOort_status(int oort_status) {
        this.oort_status = oort_status;
    }

    public int getOort_dsort() {
        return oort_sort;
    }

    public void setOort_dsort(int oort_dsort) {
        this.oort_sort = oort_dsort;
    }

    public String getBmbm() {
        return bmbm;
    }

    public void setBmbm(String bmbm) {
        this.bmbm = bmbm;
    }

    public int getContribute() {
        return contribute;
    }

    public void setContribute(int contribute) {
        this.contribute = contribute;
    }

    public String getDwbm() {
        return dwbm;
    }

    public void setDwbm(String dwbm) {
        this.dwbm = dwbm;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getFbmbm() {
        return fbmbm;
    }

    public void setFbmbm(String fbmbm) {
        this.fbmbm = fbmbm;
    }

    public int getHonor() {
        return honor;
    }

    public void setHonor(int honor) {
        this.honor = honor;
    }

    public String getImaccount() {
        return imaccount;
    }

    public void setImaccount(String imaccount) {
        this.imaccount = imaccount;
    }

    public String getImuserid() {
        return imuserid;
    }

    public void setImuserid(String imuserid) {
        this.imuserid = imuserid;
    }

    public int getIswork() {
        return iswork;
    }

    public void setIswork(int iswork) {
        this.iswork = iswork;
    }



    @Override
    public int getSort() {
        return oort_sort;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;
        UserInfo that = (UserInfo) o;
        return this.getOort_uuid().equals(that.getOort_uuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOort_uuid());
    }
}
