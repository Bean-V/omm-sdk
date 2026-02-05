package com.oort.weichat.fragment.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class ResArr<T> extends Res implements Serializable {

    public void setData(DataBean<T> data) {
        this.data = data;
    }
    public DataBean<T> getData() {
        return data;
    }
    private DataBean<T> data;
    public static class DataBean<T> implements Serializable {


//            @JSONField(name = "list")
//            private List<DataBean.CollectBean> collectList;
//
//            public List<CollectBean> getCollectList() {
//                return collectList;
//            }
//
//            public void setCollectList(List<CollectBean> collectList) {
//                this.collectList = collectList;
//            }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPagesize() {
            return pagesize;
        }

        public void setPagesize(int pagesize) {
            this.pagesize = pagesize;
        }

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public int getCountX() {
            return countX;
        }

        public void setCountX(int countX) {
            this.countX = countX;
        }

        public int getCount() {
            return count;
        }

        private int page;
        private int pages;
        private int pagesize;
        private int counts;
        @JSONField(name = "count")
        private int countX;

        //            public static class CollectBean {
//                private String id;
//
//                public String getId() {
//                    return id;
//                }
//
//                public void setId(String id) {
//                    this.id = id;
//                }
//
//                public String getUuid() {
//                    return uuid;
//                }
//
//                public void setUuid(String uuid) {
//                    this.uuid = uuid;
//                }
//
//                public int getType() {
//                    return type;
//                }
//
//                public void setType(int type) {
//                    this.type = type;
//                }
//
//                public String getAppid() {
//                    return appid;
//                }
//
//                public void setAppid(String appid) {
//                    this.appid = appid;
//                }
//
//                public ContentBean getContent() {
//                    return content;
//                }
//
//                public void setContent(ContentBean content) {
//                    this.content = content;
//                }
//
//                public String getCreatedat() {
//                    return createdat;
//                }
//
//                public void setCreatedat(String createdat) {
//                    this.createdat = createdat;
//                }
//
//                private String uuid;
//                private int type;
//                private String appid;
//                private ContentBean content;
//                private String createdat;
//
//
//                public static class ContentBean {
//                    public String getText() {
//                        return text;
//                    }
//
//                    public void setText(String text) {
//                        this.text = text;
//                    }
//
//                    private String text;
//
//                    public String getMediaUrl() {
//                        return mediaUrl;
//                    }
//
//                    public void setMediaUrl(String mediaUrl) {
//                        this.mediaUrl = mediaUrl;
//                    }
//
//                    private String mediaUrl;
//                }
//            }
        public void setCount(int count) {
            this.count = count;
        }

        public void setList(List<T> lists) {
            this.list = lists;
        }

        private int count;

        public List<T> getList() {
            return list;
        }

        private List<T> list;


        public void setUserInfo(List<UserInfoBean> lists) {
            this.UserInfo = lists;
        }


        public List<UserInfoBean> getUserInfo() {
            return UserInfo;
        }

        private List<UserInfoBean> UserInfo;




//            public static class ListsBean {
//                public void setClassifyUid(String classifyUid) {
//                    this.classifyUid = classifyUid;
//                }
//
//                public void setClassifyName(String classifyName) {
//                    this.classifyName = classifyName;
//                }
//
//                public void setAppNum(int appNum) {
//                    this.appNum = appNum;
//                }
//
//                public void setApps(List<AppsBean> apps) {
//                    this.apps = apps;
//                }
//
//                private String classifyUid;
//
//                public String getClassifyUid() {
//                    return classifyUid;
//                }
//
//                public String getClassifyName() {
//                    return classifyName;
//                }
//
//                public int getAppNum() {
//                    return appNum;
//                }
//
//                public List<AppsBean> getApps() {
//                    return apps;
//                }
//
//                private String classifyName;
//                private int appNum;
//                private List<AppsBean> apps;
//
//                public static class AppsBean {
//                    private int id;
//                    private int created_on;
//
//                    public void setId(int id) {
//                        this.id = id;
//                    }
//
//                    public void setCreated_on(int created_on) {
//                        this.created_on = created_on;
//                    }
//
//                    public void setModified_on(int modified_on) {
//                        this.modified_on = modified_on;
//                    }
//
//                    public void setUid(String uid) {
//                        this.uid = uid;
//                    }
//
//                    public void setSupplier_uid(String supplier_uid) {
//                        this.supplier_uid = supplier_uid;
//                    }
//
//                    public void setApp_id(String app_id) {
//                        this.app_id = app_id;
//                    }
//
//                    public void setApp_secret(String app_secret) {
//                        this.app_secret = app_secret;
//                    }
//
//                    public void setAppweburl(String appweburl) {
//                        this.appweburl = appweburl;
//                    }
//
//                    public void setAppentry(String appentry) {
//                        this.appentry = appentry;
//                    }
//
//                    public void setApplabel(String applabel) {
//                        this.applabel = applabel;
//                    }
//
//                    public void setApppackage(String apppackage) {
//                        this.apppackage = apppackage;
//                    }
//
//                    public void setVersion(String version) {
//                        this.version = version;
//                    }
//
//                    public void setNew_version(String new_version) {
//                        this.new_version = new_version;
//                    }
//
//                    public void setVersioncode(int versioncode) {
//                        this.versioncode = versioncode;
//                    }
//
//                    public void setNew_versioncode(int new_versioncode) {
//                        this.new_versioncode = new_versioncode;
//                    }
//
//                    public void setApp_size(int app_size) {
//                        this.app_size = app_size;
//                    }
//
//                    public void setApk_url(String apk_url) {
//                        this.apk_url = apk_url;
//                    }
//
//                    public void setIcon_url(String icon_url) {
//                        this.icon_url = icon_url;
//                    }
//
//                    public void setScreenshot_url(String screenshot_url) {
//                        this.screenshot_url = screenshot_url;
//                    }
//
//                    public void setStatus(int status) {
//                        this.status = status;
//                    }
//
//                    public void setTimeout(int timeout) {
//                        this.timeout = timeout;
//                    }
//
//                    public void setEnabled(int enabled) {
//                        this.enabled = enabled;
//                    }
//
//                    public void setOneword(String oneword) {
//                        this.oneword = oneword;
//                    }
//
//                    public void setVer_description(String ver_description) {
//                        this.ver_description = ver_description;
//                    }
//
//                    public void setPutaway_time(int putaway_time) {
//                        this.putaway_time = putaway_time;
//                    }
//
//                    public void setDescription(String description) {
//                        this.description = description;
//                    }
//
//                    public void setIntro(String intro) {
//                        this.intro = intro;
//                    }
//
//                    public void setRemark(String remark) {
//                        this.remark = remark;
//                    }
//
//                    public void setTerminal(int terminal) {
//                        this.terminal = terminal;
//                    }
//
//                    public void setClient_show_type(int client_show_type) {
//                        this.client_show_type = client_show_type;
//                    }
//
//                    public void setLarge_screen_show_type(int large_screen_show_type) {
//                        this.large_screen_show_type = large_screen_show_type;
//                    }
//
//                    public void setAuditreport(String auditreport) {
//                        this.auditreport = auditreport;
//                    }
//
//                    public void setAuditstatus(int auditstatus) {
//                        this.auditstatus = auditstatus;
//                    }
//
//                    public void setDevelopers(String developers) {
//                        this.developers = developers;
//                    }
//
//                    public void setUuid(String uuid) {
//                        this.uuid = uuid;
//                    }
//
//                    public void setLabel(String label) {
//                        this.label = label;
//                    }
//
//                    public void setClassify(String classify) {
//                        this.classify = classify;
//                    }
//
//                    public void setForce_upgrade(int force_upgrade) {
//                        this.force_upgrade = force_upgrade;
//                    }
//
//                    public void setRelyservice(String relyservice) {
//                        this.relyservice = relyservice;
//                    }
//
//                    public void setNetwork(int network) {
//                        this.network = network;
//                    }
//
//                    public void setDevelop_time(int develop_time) {
//                        this.develop_time = develop_time;
//                    }
//
//                    public void setRegion(String region) {
//                        this.region = region;
//                    }
//
//                    public void setAddress(String address) {
//                        this.address = address;
//                    }
//
//                    public void setConstruction_unit(String construction_unit) {
//                        this.construction_unit = construction_unit;
//                    }
//
//                    public void setPrincipal(String principal) {
//                        this.principal = principal;
//                    }
//
//                    public void setPhone(String phone) {
//                        this.phone = phone;
//                    }
//
//                    public void setDevelop_unit(String develop_unit) {
//                        this.develop_unit = develop_unit;
//                    }
//
//                    public void setOperate_phone(String operate_phone) {
//                        this.operate_phone = operate_phone;
//                    }
//
//                    public void setInstall_num(int install_num) {
//                        this.install_num = install_num;
//                    }
//
//                    public void setSend_num(int send_num) {
//                        this.send_num = send_num;
//                    }
//
//                    public void setDownload_num(int download_num) {
//                        this.download_num = download_num;
//                    }
//
//                    public void setUserang_enable(int userang_enable) {
//                        this.userang_enable = userang_enable;
//                    }
//
//                    public void setUserang_depart(String userang_depart) {
//                        this.userang_depart = userang_depart;
//                    }
//
//                    public void setUserang_person(String userang_person) {
//                        this.userang_person = userang_person;
//                    }
//
//                    public void setHelptext(String helptext) {
//                        this.helptext = helptext;
//                    }
//
//                    public void setStep(int step) {
//                        this.step = step;
//                    }
//
//                    public void setOffline(int offline) {
//                        this.offline = offline;
//                    }
//
//                    public void setAngle_mark_url(String angle_mark_url) {
//                        this.angle_mark_url = angle_mark_url;
//                    }
//
//                    public void setIs_angle_mark(int is_angle_mark) {
//                        this.is_angle_mark = is_angle_mark;
//                    }
//
//                    public void setIs_sync_userang(int is_sync_userang) {
//                        this.is_sync_userang = is_sync_userang;
//                    }
//
//                    public void setAttachment(String attachment) {
//                        this.attachment = attachment;
//                    }
//
//                    public int getIconResId() {
//                        return iconResId;
//                    }
//
//                    public void setIconResId(int iconResId) {
//                        this.iconResId = iconResId;
//                    }
//
//                    private int iconResId;
//                    private int modified_on;
//                    private String uid;
//                    private String supplier_uid;
//                    private String app_id;
//                    private String app_secret;
//                    private String appweburl;
//                    private String appentry;
//                    private String applabel;
//                    private String apppackage;
//                    private String version;
//                    private String new_version;
//                    private int versioncode;
//                    private int new_versioncode;
//                    private int app_size;
//                    private String apk_url;
//                    private String icon_url;
//                    private String screenshot_url;
//                    private int status;
//                    private int timeout;
//                    private int enabled;
//                    private String oneword;
//                    private String ver_description;
//                    private int putaway_time;
//                    private String description;
//                    private String intro;
//                    private String remark;
//                    private int terminal;
//                    private int client_show_type;
//                    private int large_screen_show_type;
//                    private String auditreport;
//                    private int auditstatus;
//                    private String developers;
//                    private String uuid;
//                    private String label;
//                    private String classify;
//                    private int force_upgrade;
//                    private String relyservice;
//                    private int network;
//                    private int develop_time;
//                    private String region;
//                    private String address;
//                    private String construction_unit;
//                    private String principal;
//                    private String phone;
//                    private String develop_unit;
//                    private String operate_phone;
//                    private int install_num;
//                    private int send_num;
//                    private int download_num;
//
//                    public int getId() {
//                        return id;
//                    }
//
//                    public int getCreated_on() {
//                        return created_on;
//                    }
//
//                    public int getModified_on() {
//                        return modified_on;
//                    }
//
//                    public String getUid() {
//                        return uid;
//                    }
//
//                    public String getSupplier_uid() {
//                        return supplier_uid;
//                    }
//
//                    public String getApp_id() {
//                        return app_id;
//                    }
//
//                    public String getApp_secret() {
//                        return app_secret;
//                    }
//
//                    public String getAppweburl() {
//                        return appweburl;
//                    }
//
//                    public String getAppentry() {
//                        return appentry;
//                    }
//
//                    public String getApplabel() {
//                        return applabel;
//                    }
//
//                    public String getApppackage() {
//                        return apppackage;
//                    }
//
//                    public String getVersion() {
//                        return version;
//                    }
//
//                    public String getNew_version() {
//                        return new_version;
//                    }
//
//                    public int getVersioncode() {
//                        return versioncode;
//                    }
//
//                    public int getNew_versioncode() {
//                        return new_versioncode;
//                    }
//
//                    public int getApp_size() {
//                        return app_size;
//                    }
//
//                    public String getApk_url() {
//                        return apk_url;
//                    }
//
//                    public String getIcon_url() {
//                        return icon_url;
//                    }
//
//                    public String getScreenshot_url() {
//                        return screenshot_url;
//                    }
//
//                    public int getStatus() {
//                        return status;
//                    }
//
//                    public int getTimeout() {
//                        return timeout;
//                    }
//
//                    public int getEnabled() {
//                        return enabled;
//                    }
//
//                    public String getOneword() {
//                        return oneword;
//                    }
//
//                    public String getVer_description() {
//                        return ver_description;
//                    }
//
//                    public int getPutaway_time() {
//                        return putaway_time;
//                    }
//
//                    public String getDescription() {
//                        return description;
//                    }
//
//                    public String getIntro() {
//                        return intro;
//                    }
//
//                    public String getRemark() {
//                        return remark;
//                    }
//
//                    public int getTerminal() {
//                        return terminal;
//                    }
//
//                    public int getClient_show_type() {
//                        return client_show_type;
//                    }
//
//                    public int getLarge_screen_show_type() {
//                        return large_screen_show_type;
//                    }
//
//                    public String getAuditreport() {
//                        return auditreport;
//                    }
//
//                    public int getAuditstatus() {
//                        return auditstatus;
//                    }
//
//                    public String getDevelopers() {
//                        return developers;
//                    }
//
//                    public String getUuid() {
//                        return uuid;
//                    }
//
//                    public String getLabel() {
//                        return label;
//                    }
//
//                    public String getClassify() {
//                        return classify;
//                    }
//
//                    public int getForce_upgrade() {
//                        return force_upgrade;
//                    }
//
//                    public String getRelyservice() {
//                        return relyservice;
//                    }
//
//                    public int getNetwork() {
//                        return network;
//                    }
//
//                    public int getDevelop_time() {
//                        return develop_time;
//                    }
//
//                    public String getRegion() {
//                        return region;
//                    }
//
//                    public String getAddress() {
//                        return address;
//                    }
//
//                    public String getConstruction_unit() {
//                        return construction_unit;
//                    }
//
//                    public String getPrincipal() {
//                        return principal;
//                    }
//
//                    public String getPhone() {
//                        return phone;
//                    }
//
//                    public String getDevelop_unit() {
//                        return develop_unit;
//                    }
//
//                    public String getOperate_phone() {
//                        return operate_phone;
//                    }
//
//                    public int getInstall_num() {
//                        return install_num;
//                    }
//
//                    public int getSend_num() {
//                        return send_num;
//                    }
//
//                    public int getDownload_num() {
//                        return download_num;
//                    }
//
//                    public int getUserang_enable() {
//                        return userang_enable;
//                    }
//
//                    public String getUserang_depart() {
//                        return userang_depart;
//                    }
//
//                    public String getUserang_person() {
//                        return userang_person;
//                    }
//
//                    public String getHelptext() {
//                        return helptext;
//                    }
//
//                    public int getStep() {
//                        return step;
//                    }
//
//                    public int getOffline() {
//                        return offline;
//                    }
//
//                    public String getAngle_mark_url() {
//                        return angle_mark_url;
//                    }
//
//                    public int getIs_angle_mark() {
//                        return is_angle_mark;
//                    }
//
//                    public int getIs_sync_userang() {
//                        return is_sync_userang;
//                    }
//
//                    public String getAttachment() {
//                        return attachment;
//                    }
//
//                    private int userang_enable;
//                    private String userang_depart;
//                    private String userang_person;
//                    private String helptext;
//                    private int step;
//                    private int offline;
//                    private String angle_mark_url;
//                    private int is_angle_mark;
//                    private int is_sync_userang;
//                    private String attachment;
//                }
//            }


    }
}
