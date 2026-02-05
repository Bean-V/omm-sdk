package com.oortcloud.clouddisk.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;


/**
 * @filename:
 * @function：应用信息
 * @version： v1.0
 * @author: zhangzhijun/@date:20/1/15 15:51
 */
public class AppInfo  implements Serializable {
    //编号
    private int id;
    //创建时间
    private long created_on;
    //修改时间
    private long modified_on;
    //应用uid
    private String  uid;
    //供应商uid
    private String  supplier_uid;
    //应用ID
    private String  app_id;
    //应用秘钥
    private String  app_secret;
    //应用Url地址
    private String  appweburl;
    //应用程序入口
    private String  appentry;
    //应用名称
    private String  applabel;
    //应用包名
    private String  apppackage;
    //应用版本号
    private String  version;
    //应用版本编码
    private int  versioncode;
    private String  app_size;
    //apk地址
    private String  apk_url;
    //apk图标地址
    private String  icon_url;
    //应用截图地址
    private String  screenshot_url;
    //状态
    private int  status;
    //激活状态
    private int  enabled;
    //说明 一句话
    private String  oneword;
    //版本描述
    private String  ver_description;
    //
    private String   putaway_time;
    //应用描述
    private String  description;
    //应用简介
    private String  intro;
    //应用备注
    private String  remark;
    //终端类型(0-移动端原生,1-移动的H5,2-PCWEB,3-PC桌面)
    private int  terminal;
    //状态
    private int  audit_status;
    //审核意见
    private String  auditreport;
    //状态
    private int  auditstatus;
    //开发商
    private String  developers;
    //供应商uuid
    private String  uuid;
    //应用标签
    private String  label;
    //应用分类
    private String  classify;
    //
    private int  force_upgrade;
    //依赖服务
    private String  relyservice;
    private int  network;
    //开发时间
    private long  develop_time;
    //地区
    private String  region;
    //详细地址
    private String  address;
    //建设单位
    private String  construction_unit;
    //应用责任人
    private String  principal;
    //联系电话
    private String  phone;
    //应用开发单位
    private String  develop_unit;
    //运营电话
    private String  operate_phone;
    //安装次数
    private int  install_num;
    //发送次数
    private int  send_num;
    //下载次数
    private int  download_num;
    //
    private int  userang_enable;
    //使用范围部门
    private String  userang_depart;
    //使用范围人员
    private String  userang_person;
    //帮助
    private String  helptext;
    //
    private int  step;
    //应用下载路径
    private String path;

    private float score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreated_on() {
        return created_on;
    }

    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }

    public long getModified_on() {
        return modified_on;
    }

    public void setModified_on(long modified_on) {
        this.modified_on = modified_on;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSupplier_uid() {
        return supplier_uid;
    }

    public void setSupplier_uid(String supplier_uid) {
        this.supplier_uid = supplier_uid;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }

    public String getAppweburl() {
        return appweburl;
    }

    public void setAppweburl(String appweburl) {
        this.appweburl = appweburl;
    }

    public String getAppentry() {
        return appentry;
    }

    public void setAppentry(String appentry) {
        this.appentry = appentry;
    }

    public String getApplabel() {
        return applabel;
    }

    public void setApplabel(String applabel) {
        this.applabel = applabel;
    }

    public String getApppackage() {
        return apppackage;
    }

    public void setApppackage(String apppackage) {
        this.apppackage = apppackage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getApp_size() {
        return app_size;
    }

    public void setApp_size(String app_size) {
        this.app_size = app_size;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getScreenshot_url() {
        return screenshot_url;
    }

    public void setScreenshot_url(String screenshot_url) {
        this.screenshot_url = screenshot_url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getOneword() {
        return oneword;
    }

    public void setOneword(String oneword) {
        this.oneword = oneword;
    }

    public String getVer_description() {
        return ver_description;
    }

    public void setVer_description(String ver_description) {
        this.ver_description = ver_description;
    }

    public String getPutaway_time() {
        return putaway_time;
    }

    public void setPutaway_time(String putaway_time) {
        this.putaway_time = putaway_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public int getAudit_status() {
        return audit_status;
    }

    public void setAudit_status(int audit_status) {
        this.audit_status = audit_status;
    }

    public String getAuditreport() {
        return auditreport;
    }

    public void setAuditreport(String auditreport) {
        this.auditreport = auditreport;
    }

    public int getAuditstatus() {
        return auditstatus;
    }

    public void setAuditstatus(int auditstatus) {
        this.auditstatus = auditstatus;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public int getForce_upgrade() {
        return force_upgrade;
    }

    public void setForce_upgrade(int force_upgrade) {
        this.force_upgrade = force_upgrade;
    }

    public String getRelyservice() {
        return relyservice;
    }

    public void setRelyservice(String relyservice) {
        this.relyservice = relyservice;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public long getDevelop_time() {
        return develop_time;
    }

    public void setDevelop_time(long develop_time) {
        this.develop_time = develop_time;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConstruction_unit() {
        return construction_unit;
    }

    public void setConstruction_unit(String construction_unit) {
        this.construction_unit = construction_unit;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDevelop_unit() {
        return develop_unit;
    }

    public void setDevelop_unit(String develop_unit) {
        this.develop_unit = develop_unit;
    }

    public String getOperate_phone() {
        return operate_phone;
    }

    public void setOperate_phone(String operate_phone) {
        this.operate_phone = operate_phone;
    }

    public int getInstall_num() {
        return install_num;
    }

    public void setInstall_num(int install_num) {
        this.install_num = install_num;
    }

    public int getSend_num() {
        return send_num;
    }

    public void setSend_num(int send_num) {
        this.send_num = send_num;
    }

    public int getDownload_num() {
        return download_num;
    }

    public void setDownload_num(int download_num) {
        this.download_num = download_num;
    }

    public int getUserang_enable() {
        return userang_enable;
    }

    public void setUserang_enable(int userang_enable) {
        this.userang_enable = userang_enable;
    }

    public String getUserang_depart() {
        return userang_depart;
    }

    public void setUserang_depart(String userang_depart) {
        this.userang_depart = userang_depart;
    }

    public String getUserang_person() {
        return userang_person;
    }

    public void setUserang_person(String userang_person) {
        this.userang_person = userang_person;
    }

    public String getHelptext() {
        return helptext;
    }

    public void setHelptext(String helptext) {
        this.helptext = helptext;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", created_on=" + created_on +
                ", modified_on=" + modified_on +
                ", uid='" + uid + '\'' +
                ", supplier_uid='" + supplier_uid + '\'' +
                ", app_id='" + app_id + '\'' +
                ", app_secret='" + app_secret + '\'' +
                ", appweburl='" + appweburl + '\'' +
                ", appentry='" + appentry + '\'' +
                ", applabel='" + applabel + '\'' +
                ", apppackage='" + apppackage + '\'' +
                ", version='" + version + '\'' +
                ", versioncode=" + versioncode +
                ", app_size='" + app_size + '\'' +
                ", apk_url='" + apk_url + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", screenshot_url='" + screenshot_url + '\'' +
                ", status=" + status +
                ", enabled=" + enabled +
                ", oneword='" + oneword + '\'' +
                ", ver_description='" + ver_description + '\'' +
                ", putaway_time='" + putaway_time + '\'' +
                ", description='" + description + '\'' +
                ", intro='" + intro + '\'' +
                ", remark='" + remark + '\'' +
                ", terminal=" + terminal +
                ", audit_status=" + audit_status +
                ", auditreport='" + auditreport + '\'' +
                ", auditstatus=" + auditstatus +
                ", developers='" + developers + '\'' +
                ", uuid='" + uuid + '\'' +
                ", label='" + label + '\'' +
                ", classify='" + classify + '\'' +
                ", force_upgrade=" + force_upgrade +
                ", relyservice='" + relyservice + '\'' +
                ", network=" + network +
                ", develop_time=" + develop_time +
                ", region='" + region + '\'' +
                ", address='" + address + '\'' +
                ", construction_unit='" + construction_unit + '\'' +
                ", principal='" + principal + '\'' +
                ", phone='" + phone + '\'' +
                ", develop_unit='" + develop_unit + '\'' +
                ", operate_phone='" + operate_phone + '\'' +
                ", install_num=" + install_num +
                ", send_num=" + send_num +
                ", download_num=" + download_num +
                ", userang_enable=" + userang_enable +
                ", userang_depart='" + userang_depart + '\'' +
                ", userang_person='" + userang_person + '\'' +
                ", helptext='" + helptext + '\'' +
                ", step=" + step +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof  AppInfo){
           return this.getApppackage().equals(((AppInfo) obj).getApppackage());
        }

        return false ;

    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
