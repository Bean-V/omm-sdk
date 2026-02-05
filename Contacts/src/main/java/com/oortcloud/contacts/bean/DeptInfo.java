package com.oortcloud.contacts.bean;

import java.io.Serializable;

/**
 * @filename:
 * @function：部门详情
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/7/1 15:01
 */
public class DeptInfo extends Sort implements Serializable {
    //部门标记
    private  String oort_udid;
    //部门编码
    private  String oort_dcode;
    //部门名称
    private  String oort_dname;
    //部门名称拼音首字母
    private  String oort_dnamefl;
    //上级部门编码
    private  String oort_pdcode;
    //上级部门名称
    private  String oort_pdname;
    //部门类型，0其它单位，1编制单位，2临时单位...9测试单位
    private  String oort_dtype;
    //部门职级，0其它，1科级，2处级，3局级，4厅级...
    private  String oort_dpost;
    //部门状态，0禁用，1正常，...9删除
    private  int oort_status;
    //部门联系电话
    private  String oort_dtel;
    //部门联系地址
    private  String oort_daddr;
    //部门层级
    private  int oort_dlevel;
    //部门层级路径 从最顶层到所在部门的层级路径
    private  String oort_dpath;
    //部门编码层级路径 从最顶层到所在部门的编码层级路径
    private  String oort_dcodepath;
    //部门创建时间
    private  String oort_dtcreate;
    //部门更新时间
    private  String oort_dupdate;
    //备注
    private  String oort_dremark;
    //排序ID(越小越前)
    protected   int oort_dsort;

    public int getOort_dsort() {
        return oort_dsort;
    }

    public void setOort_dsort(int oort_dsort) {
        this.oort_dsort = oort_dsort;
    }

    public String getOort_udid() {
        return oort_udid;
    }

    public void setOort_udid(String oort_udid) {
        this.oort_udid = oort_udid;
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

    public String getOort_dnamefl() {
        return oort_dnamefl;
    }

    public void setOort_dnamefl(String oort_dnamefl) {
        this.oort_dnamefl = oort_dnamefl;
    }

    public String getOort_pdcode() {
        return oort_pdcode;
    }

    public void setOort_pdcode(String oort_pdcode) {
        this.oort_pdcode = oort_pdcode;
    }

    public String getOort_pdname() {
        return oort_pdname;
    }

    public void setOort_pdname(String oort_pdname) {
        this.oort_pdname = oort_pdname;
    }

    public String getOort_dtype() {
        return oort_dtype;
    }

    public void setOort_dtype(String oort_dtype) {
        this.oort_dtype = oort_dtype;
    }

    public String getOort_dpost() {
        return oort_dpost;
    }

    public void setOort_dpost(String oort_dpost) {
        this.oort_dpost = oort_dpost;
    }

    public int getOort_status() {
        return oort_status;
    }

    public void setOort_status(int oort_status) {
        this.oort_status = oort_status;
    }

    public String getOort_dtel() {
        return oort_dtel;
    }

    public void setOort_dtel(String oort_dtel) {
        this.oort_dtel = oort_dtel;
    }

    public String getOort_daddr() {
        return oort_daddr;
    }

    public void setOort_daddr(String oort_daddr) {
        this.oort_daddr = oort_daddr;
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

    public String getOort_dtcreate() {
        return oort_dtcreate;
    }

    public void setOort_dtcreate(String oort_dtcreate) {
        this.oort_dtcreate = oort_dtcreate;
    }

    public String getOort_dupdate() {
        return oort_dupdate;
    }

    public void setOort_dupdate(String oort_dupdate) {
        this.oort_dupdate = oort_dupdate;
    }

    public String getOort_dremark() {
        return oort_dremark;
    }

    public void setOort_dremark(String oort_dremark) {
        this.oort_dremark = oort_dremark;
    }

    @Override
    public int getSort() {
        return oort_dsort;
    }
}
