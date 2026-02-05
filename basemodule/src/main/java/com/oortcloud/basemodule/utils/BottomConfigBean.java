package com.oortcloud.basemodule.utils;

import java.io.Serializable;

public class BottomConfigBean implements Serializable {

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RelateAppBean getRelateApp() {
        return relateApp;
    }

    public void setRelateApp(RelateAppBean relateApp) {
        this.relateApp = relateApp;
    }

    /**
     * icon : http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20250226/18/54/4/a6a6abcd741e96e94012ffe1a1b41b3b.png?name=a6a6abcd741e96e94012ffe1a1b41b3b.png&download=1
     * label : 首页
     * relateApp : {"address":"","angle_mark_url":"","apk_url":"http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20250221/10/55/4/测试图片.zip?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.zip&download=1","app_id":"95d6935b05c5463ea45530209ef7ae46","app_secret":"47dff4b38a0c4cfda5797f666733763b","app_size":6448,"appentry":"","applabel":"测试添加应用","apppackage":"com.test.oort","appweburl":"","attachment":"","auditreport":"","auditstatus":1,"classify":"7b1af605-09cc-4a7d-add8-7975d9987652","client_show_type":0,"construction_unit":"99998888","created_on":1740128336,"description":"","develop_time":1740128355,"develop_unit":"奥特莱斯","developers":"","download_num":0,"enabled":0,"force_upgrade":0,"helptext":"","icon_url":"http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20241105/14/19/4/测试图片.png?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.png&download=1","id":1337,"install_num":0,"intro":"","is_angle_mark":1,"is_sync_userang":2,"label":"","large_screen_show_type":2,"low_code_config_id":"","low_code_flow_id":"","low_code_form_id":"","low_code_step":0,"modified_on":1740128450,"network":0,"new_version":"1.0.0","new_versioncode":100,"offline":1,"oneword":"测试","operate_phone":"13800123800","phone":"13800138000","principal":"0a042ed3-cc98-47d6-bd38-78053b509f78","putaway_time":1740128450,"radio":true,"region":"","relyservice":"","remark":"","screenshot_url":"[\"http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20241105/14/19/4/测试图片.png?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.png&download=1\"]","send_num":0,"status":3,"step":3,"supplier_uid":"9a997c93-db58-4f6b-9df5-f85293b6018d","terminal":1,"timeout":0,"uid":"2e7b5099-4073-4d7f-9f2b-5fb4a1adc30d","useauth_depart":"","useauth_depart_apply":"","useauth_depart_cant":"","useauth_enable":1,"useauth_person":"","useauth_person_apply":"","useauth_person_cant":"","useauth_policetype":null,"useauth_policetype_apply":null,"useauth_policetype_cant":null,"useauth_policetype_use":null,"userang_depart":"[]","userang_enable":1,"userang_person":"[]","uuid":"3703d6b7-eba0-488d-8db3-564d8f6167ec","ver_description":"测试增加应用审核显示应用范围信息","version":"1.0.0","versioncode":100}
     */

    private String icon;
    private String label;
    private RelateAppBean relateApp;

    public static class RelateAppBean implements Serializable {
        /**
         * address :
         * angle_mark_url :
         * apk_url : http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20250221/10/55/4/测试图片.zip?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.zip&download=1
         * app_id : 95d6935b05c5463ea45530209ef7ae46
         * app_secret : 47dff4b38a0c4cfda5797f666733763b
         * app_size : 6448
         * appentry :
         * applabel : 测试添加应用
         * apppackage : com.test.oort
         * appweburl :
         * attachment :
         * auditreport :
         * auditstatus : 1
         * classify : 7b1af605-09cc-4a7d-add8-7975d9987652
         * client_show_type : 0
         * construction_unit : 99998888
         * created_on : 1740128336
         * description :
         * develop_time : 1740128355
         * develop_unit : 奥特莱斯
         * developers :
         * download_num : 0
         * enabled : 0
         * force_upgrade : 0
         * helptext :
         * icon_url : http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20241105/14/19/4/测试图片.png?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.png&download=1
         * id : 1337
         * install_num : 0
         * intro :
         * is_angle_mark : 1
         * is_sync_userang : 2
         * label :
         * large_screen_show_type : 2
         * low_code_config_id :
         * low_code_flow_id :
         * low_code_form_id :
         * low_code_step : 0
         * modified_on : 1740128450
         * network : 0
         * new_version : 1.0.0
         * new_versioncode : 100
         * offline : 1
         * oneword : 测试
         * operate_phone : 13800123800
         * phone : 13800138000
         * principal : 0a042ed3-cc98-47d6-bd38-78053b509f78
         * putaway_time : 1740128450
         * radio : true
         * region :
         * relyservice :
         * remark :
         * screenshot_url : ["http://oort.oortcloudsmart.com:21310/oort/oortwj1/group1/default/20241105/14/19/4/测试图片.png?name=%E6%B5%8B%E8%AF%95%E5%9B%BE%E7%89%87.png&download=1"]
         * send_num : 0
         * status : 3
         * step : 3
         * supplier_uid : 9a997c93-db58-4f6b-9df5-f85293b6018d
         * terminal : 1
         * timeout : 0
         * uid : 2e7b5099-4073-4d7f-9f2b-5fb4a1adc30d
         * useauth_depart :
         * useauth_depart_apply :
         * useauth_depart_cant :
         * useauth_enable : 1
         * useauth_person :
         * useauth_person_apply :
         * useauth_person_cant :
         * useauth_policetype : null
         * useauth_policetype_apply : null
         * useauth_policetype_cant : null
         * useauth_policetype_use : null
         * userang_depart : []
         * userang_enable : 1
         * userang_person : []
         * uuid : 3703d6b7-eba0-488d-8db3-564d8f6167ec
         * ver_description : 测试增加应用审核显示应用范围信息
         * version : 1.0.0
         * versioncode : 100
         */

        private String address;
        private String angle_mark_url;
        private String apk_url;
        private String app_id;
        private String app_secret;
        private int app_size;
        private String appentry;
        private String applabel;
        private String apppackage;
        private String appweburl;
        private String attachment;
        private String auditreport;
        private int auditstatus;
        private String classify;
        private int client_show_type;
        private String construction_unit;
        private int created_on;
        private String description;
        private int develop_time;
        private String develop_unit;
        private String developers;
        private int download_num;
        private int enabled;
        private int force_upgrade;
        private String helptext;
        private String icon_url;
        private int id;
        private int install_num;
        private String intro;
        private int is_angle_mark;
        private int is_sync_userang;
        private String label;
        private int large_screen_show_type;
        private String low_code_config_id;
        private String low_code_flow_id;
        private String low_code_form_id;
        private int low_code_step;
        private int modified_on;
        private int network;
        private String new_version;
        private int new_versioncode;
        private int offline;
        private String oneword;
        private String operate_phone;
        private String phone;
        private String principal;
        private int putaway_time;
        private boolean radio;
        private String region;
        private String relyservice;
        private String remark;
        private String screenshot_url;
        private int send_num;
        private int status;
        private int step;
        private String supplier_uid;
        private int terminal;
        private int timeout;
        private String uid;
        private String useauth_depart;
        private String useauth_depart_apply;
        private String useauth_depart_cant;
        private int useauth_enable;
        private String useauth_person;
        private String useauth_person_apply;
        private String useauth_person_cant;
        private Object useauth_policetype;
        private Object useauth_policetype_apply;
        private Object useauth_policetype_cant;

        public Object getUseauth_policetype_use() {
            return useauth_policetype_use;
        }

        public void setUseauth_policetype_use(Object useauth_policetype_use) {
            this.useauth_policetype_use = useauth_policetype_use;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAngle_mark_url() {
            return angle_mark_url;
        }

        public void setAngle_mark_url(String angle_mark_url) {
            this.angle_mark_url = angle_mark_url;
        }

        public String getApk_url() {
            return apk_url;
        }

        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
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

        public int getApp_size() {
            return app_size;
        }

        public void setApp_size(int app_size) {
            this.app_size = app_size;
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

        public String getAppweburl() {
            return appweburl;
        }

        public void setAppweburl(String appweburl) {
            this.appweburl = appweburl;
        }

        public String getAttachment() {
            return attachment;
        }

        public void setAttachment(String attachment) {
            this.attachment = attachment;
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

        public String getClassify() {
            return classify;
        }

        public void setClassify(String classify) {
            this.classify = classify;
        }

        public int getClient_show_type() {
            return client_show_type;
        }

        public void setClient_show_type(int client_show_type) {
            this.client_show_type = client_show_type;
        }

        public String getConstruction_unit() {
            return construction_unit;
        }

        public void setConstruction_unit(String construction_unit) {
            this.construction_unit = construction_unit;
        }

        public int getCreated_on() {
            return created_on;
        }

        public void setCreated_on(int created_on) {
            this.created_on = created_on;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDevelop_time() {
            return develop_time;
        }

        public void setDevelop_time(int develop_time) {
            this.develop_time = develop_time;
        }

        public String getDevelop_unit() {
            return develop_unit;
        }

        public void setDevelop_unit(String develop_unit) {
            this.develop_unit = develop_unit;
        }

        public String getDevelopers() {
            return developers;
        }

        public void setDevelopers(String developers) {
            this.developers = developers;
        }

        public int getDownload_num() {
            return download_num;
        }

        public void setDownload_num(int download_num) {
            this.download_num = download_num;
        }

        public int getEnabled() {
            return enabled;
        }

        public void setEnabled(int enabled) {
            this.enabled = enabled;
        }

        public int getForce_upgrade() {
            return force_upgrade;
        }

        public void setForce_upgrade(int force_upgrade) {
            this.force_upgrade = force_upgrade;
        }

        public String getHelptext() {
            return helptext;
        }

        public void setHelptext(String helptext) {
            this.helptext = helptext;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getInstall_num() {
            return install_num;
        }

        public void setInstall_num(int install_num) {
            this.install_num = install_num;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public int getIs_angle_mark() {
            return is_angle_mark;
        }

        public void setIs_angle_mark(int is_angle_mark) {
            this.is_angle_mark = is_angle_mark;
        }

        public int getIs_sync_userang() {
            return is_sync_userang;
        }

        public void setIs_sync_userang(int is_sync_userang) {
            this.is_sync_userang = is_sync_userang;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getLarge_screen_show_type() {
            return large_screen_show_type;
        }

        public void setLarge_screen_show_type(int large_screen_show_type) {
            this.large_screen_show_type = large_screen_show_type;
        }

        public String getLow_code_config_id() {
            return low_code_config_id;
        }

        public void setLow_code_config_id(String low_code_config_id) {
            this.low_code_config_id = low_code_config_id;
        }

        public String getLow_code_flow_id() {
            return low_code_flow_id;
        }

        public void setLow_code_flow_id(String low_code_flow_id) {
            this.low_code_flow_id = low_code_flow_id;
        }

        public String getLow_code_form_id() {
            return low_code_form_id;
        }

        public void setLow_code_form_id(String low_code_form_id) {
            this.low_code_form_id = low_code_form_id;
        }

        public int getLow_code_step() {
            return low_code_step;
        }

        public void setLow_code_step(int low_code_step) {
            this.low_code_step = low_code_step;
        }

        public int getModified_on() {
            return modified_on;
        }

        public void setModified_on(int modified_on) {
            this.modified_on = modified_on;
        }

        public int getNetwork() {
            return network;
        }

        public void setNetwork(int network) {
            this.network = network;
        }

        public String getNew_version() {
            return new_version;
        }

        public void setNew_version(String new_version) {
            this.new_version = new_version;
        }

        public int getNew_versioncode() {
            return new_versioncode;
        }

        public void setNew_versioncode(int new_versioncode) {
            this.new_versioncode = new_versioncode;
        }

        public int getOffline() {
            return offline;
        }

        public void setOffline(int offline) {
            this.offline = offline;
        }

        public String getOneword() {
            return oneword;
        }

        public void setOneword(String oneword) {
            this.oneword = oneword;
        }

        public String getOperate_phone() {
            return operate_phone;
        }

        public void setOperate_phone(String operate_phone) {
            this.operate_phone = operate_phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public int getPutaway_time() {
            return putaway_time;
        }

        public void setPutaway_time(int putaway_time) {
            this.putaway_time = putaway_time;
        }

        public boolean isRadio() {
            return radio;
        }

        public void setRadio(boolean radio) {
            this.radio = radio;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getRelyservice() {
            return relyservice;
        }

        public void setRelyservice(String relyservice) {
            this.relyservice = relyservice;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getScreenshot_url() {
            return screenshot_url;
        }

        public void setScreenshot_url(String screenshot_url) {
            this.screenshot_url = screenshot_url;
        }

        public int getSend_num() {
            return send_num;
        }

        public void setSend_num(int send_num) {
            this.send_num = send_num;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public String getSupplier_uid() {
            return supplier_uid;
        }

        public void setSupplier_uid(String supplier_uid) {
            this.supplier_uid = supplier_uid;
        }

        public int getTerminal() {
            return terminal;
        }

        public void setTerminal(int terminal) {
            this.terminal = terminal;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUseauth_depart() {
            return useauth_depart;
        }

        public void setUseauth_depart(String useauth_depart) {
            this.useauth_depart = useauth_depart;
        }

        public String getUseauth_depart_apply() {
            return useauth_depart_apply;
        }

        public void setUseauth_depart_apply(String useauth_depart_apply) {
            this.useauth_depart_apply = useauth_depart_apply;
        }

        public String getUseauth_depart_cant() {
            return useauth_depart_cant;
        }

        public void setUseauth_depart_cant(String useauth_depart_cant) {
            this.useauth_depart_cant = useauth_depart_cant;
        }

        public int getUseauth_enable() {
            return useauth_enable;
        }

        public void setUseauth_enable(int useauth_enable) {
            this.useauth_enable = useauth_enable;
        }

        public String getUseauth_person() {
            return useauth_person;
        }

        public void setUseauth_person(String useauth_person) {
            this.useauth_person = useauth_person;
        }

        public String getUseauth_person_apply() {
            return useauth_person_apply;
        }

        public void setUseauth_person_apply(String useauth_person_apply) {
            this.useauth_person_apply = useauth_person_apply;
        }

        public String getUseauth_person_cant() {
            return useauth_person_cant;
        }

        public void setUseauth_person_cant(String useauth_person_cant) {
            this.useauth_person_cant = useauth_person_cant;
        }

        public Object getUseauth_policetype() {
            return useauth_policetype;
        }

        public void setUseauth_policetype(Object useauth_policetype) {
            this.useauth_policetype = useauth_policetype;
        }

        public Object getUseauth_policetype_apply() {
            return useauth_policetype_apply;
        }

        public void setUseauth_policetype_apply(Object useauth_policetype_apply) {
            this.useauth_policetype_apply = useauth_policetype_apply;
        }

        public Object getUseauth_policetype_cant() {
            return useauth_policetype_cant;
        }

        public void setUseauth_policetype_cant(Object useauth_policetype_cant) {
            this.useauth_policetype_cant = useauth_policetype_cant;
        }

        public String getUserang_depart() {
            return userang_depart;
        }

        public void setUserang_depart(String userang_depart) {
            this.userang_depart = userang_depart;
        }

        public int getUserang_enable() {
            return userang_enable;
        }

        public void setUserang_enable(int userang_enable) {
            this.userang_enable = userang_enable;
        }

        public String getUserang_person() {
            return userang_person;
        }

        public void setUserang_person(String userang_person) {
            this.userang_person = userang_person;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getVer_description() {
            return ver_description;
        }

        public void setVer_description(String ver_description) {
            this.ver_description = ver_description;
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

        private Object useauth_policetype_use;
        private String userang_depart;
        private int userang_enable;
        private String userang_person;
        private String uuid;
        private String ver_description;
        private String version;
        private int versioncode;
    }
}
