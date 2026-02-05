package com.oortcloud.appstore.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @filename:
 * @function： module信息类
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/3 11:28
 */
public class ModuleInfo<T> implements Serializable {
    private int id ;
    private long created_on;
    private long modified_on;
    private String uuid;
    private String module_id;
    private String module_name;
    private String app_uids;
    private int module_order;
    private int homepage_type;
    private List<T> app_list;
    //是否可编辑（1-是，2-否）
    private  int is_edit;
    //是否显示模块名字（1-是，2-否）
    private  int is_show_name;
    //是否固定模块（1-是，2-否）
    private  int is_top_module;


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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getApp_uids() {
        return app_uids;
    }

    public void setApp_uids(String app_uids) {
        this.app_uids = app_uids;
    }

    public int getModule_order() {
        return module_order;
    }

    public void setModule_order(int module_order) {
        this.module_order = module_order;
    }

    public int getHomepage_type() {
        return homepage_type;
    }

    public void setHomepage_type(int homepage_type) {
        this.homepage_type = homepage_type;
    }

    public List<T> getApp_list() {
        return app_list;
    }

    public void setApp_list(List<T> app_list) {
        this.app_list = app_list;
    }

    public int getIs_edit() {
        return is_edit;
    }

    public void setIs_edit(int is_edit) {
        this.is_edit = is_edit;
    }

    public int getIs_show_name() {
        return is_show_name;
    }

    public void setIs_show_name(int is_show_name) {
        this.is_show_name = is_show_name;
    }

    public int getIs_top_module() {
        return is_top_module;
    }

    public void setIs_top_module(int is_top_module) {
        this.is_top_module = is_top_module;
    }

    @Override
    public String toString() {
        return "ModuleInfo{" +
                "id=" + id +
                ", created_on=" + created_on +
                ", modified_on=" + modified_on +
                ", uuid='" + uuid + '\'' +
                ", module_id='" + module_id + '\'' +
                ", module_name='" + module_name + '\'' +
                ", app_uids='" + app_uids + '\'' +
                ", module_order=" + module_order +
                ", homepage_type=" + homepage_type +
                ", app_list=" + app_list +
                '}';
    }
}
