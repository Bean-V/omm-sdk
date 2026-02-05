package com.oortcloud.bean;

import java.io.Serializable;


public class MainAppInfo implements Serializable {

    /**
     * id : 201
     * created_on : 1611313188
     * modified_on : 1622626348
     * uid : c550080e-ec18-4f56-8b6b-75cd81cc399a
     * supplier_uid : 9a997c93-db58-4f6b-9df5-f85293b6018d
     * app_id : 9332dbd4a1254e6cb730e62721bd67f8
     * app_secret : 664998aecab44f74bfff7f86a5d7d902
     * appweburl :
     * appentry :
     * applabel : 工作汇报
     * apppackage : com.work_report.oort
     * version : 1.0.3
     * new_version : 1.0.3
     * versioncode : 103
     * new_versioncode : 103
     * app_size : 1269568
     * apk_url : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20210122/18/59/4/dist.zip
     * icon_url : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20210203/10/08/4/ic_工作汇报.png
     * screenshot_url : ["http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20201103/16/55/4/工作汇报-首页-我发出的.png"]
     * status : 3
     * timeout : 0
     * enabled : 0
     * oneword : 工作汇报
     * ver_description : 列表下拉刷新
     * putaway_time : 1611313630
     * description :
     * intro :
     * remark :
     * terminal : 1
     * client_show_type : 0
     * large_screen_show_type : 2
     * auditreport :
     * auditstatus : 1
     * developers :
     * uuid : 1bd1fa99-b957-4023-9cf5-7e876f042e9b
     * label :
     * classify : 014249bb-a6d6-42b5-855b-b7feb6483015
     * force_upgrade : 0
     * relyservice :
     * network : 0
     * develop_time : 1604393756
     * region : 深圳
     * address :
     * construction_unit : 99999000
     * principal : 45363941-29c3-4b08-bf64-e3c37a4651c4
     * phone : 16620805419
     * develop_unit : 奥尔特云（深圳）智慧科技有限公司
     * operate_phone : 16620805419
     * install_num : 29
     * send_num : 0
     * download_num : 0
     * userang_enable : 2
     * userang_depart : []
     * userang_person : []
     * helptext :
     * step : 3
     */

    private int id;
    private int created_on;
    private int modified_on;
    private String uid;
    private String supplier_uid;
    private String app_id;
    private String app_secret;
    private String appweburl;
    private String appentry;
    private String applabel;
    private String apppackage;
    private String version;
    private String new_version;
    private int versioncode;
    private int new_versioncode;
    private int app_size;
    private String apk_url;
    private String icon_url;
    private String screenshot_url;
    private int status;
    private int timeout;
    private int enabled;
    private String oneword;
    private String ver_description;
    private int putaway_time;
    private String description;
    private String intro;
    private String remark;
    private int terminal;
    private int client_show_type;
    private int large_screen_show_type;
    private String auditreport;
    private int auditstatus;
    private String developers;
    private String uuid;
    private String label;
    private String classify;
    private int force_upgrade;
    private String relyservice;
    private int network;
    private int develop_time;
    private String region;
    private String address;
    private String construction_unit;
    private String principal;
    private String phone;
    private String develop_unit;
    private String operate_phone;
    private int install_num;
    private int send_num;
    private int download_num;
    private int userang_enable;
    private String userang_depart;
    private String userang_person;
    private String helptext;
    private int step;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreated_on() {
        return created_on;
    }

    public void setCreated_on(int created_on) {
        this.created_on = created_on;
    }

    public int getModified_on() {
        return modified_on;
    }

    public void setModified_on(int modified_on) {
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

    public String getNew_version() {
        return new_version;
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public int getNew_versioncode() {
        return new_versioncode;
    }

    public void setNew_versioncode(int new_versioncode) {
        this.new_versioncode = new_versioncode;
    }

    public int getApp_size() {
        return app_size;
    }

    public void setApp_size(int app_size) {
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public int getPutaway_time() {
        return putaway_time;
    }

    public void setPutaway_time(int putaway_time) {
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

    public int getClient_show_type() {
        return client_show_type;
    }

    public void setClient_show_type(int client_show_type) {
        this.client_show_type = client_show_type;
    }

    public int getLarge_screen_show_type() {
        return large_screen_show_type;
    }

    public void setLarge_screen_show_type(int large_screen_show_type) {
        this.large_screen_show_type = large_screen_show_type;
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

    public int getDevelop_time() {
        return develop_time;
    }

    public void setDevelop_time(int develop_time) {
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
}
