package com.oort.weichat.fragment.entity;

import java.io.Serializable;
public class OortNotice implements Serializable {

            /**
             * id : 108
             * created_on : 1691489635
             * modified_on : 1691489635
             * uid : da1847d7-b089-43d5-929e-9ef1614d7d7d
             * uuid : 938972c7-04f2-4b31-9833-c7e92fc721c2
             * organid : 9999900002
             * title : 古古怪怪
             * content : 富贵花欢迎哥哥
             * pic_url :
             * accessory_url :
             * notice_type : 0
             * start_time : 0
             * end_time : 0
             * status : 1
             * userrang_enable : 0
             * userrang_code :
             * userrang_person :
             * create_time : 2023-08-08
             * read_num : 0
             * read_time : 0
             * read_type : 0
             */

    private int id;
    private int created_on;
    private int modified_on;
    private String uid;
    private String uuid;
    private String organid;
    private String title;
    private String content;
    private String pic_url;
    private String accessory_url;
    private int notice_type;
    private int start_time;
    private int end_time;
    private int status;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getAccessory_url() {
        return accessory_url;
    }

    public void setAccessory_url(String accessory_url) {
        this.accessory_url = accessory_url;
    }

    public int getNotice_type() {
        return notice_type;
    }

    public void setNotice_type(int notice_type) {
        this.notice_type = notice_type;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserrang_enable() {
        return userrang_enable;
    }

    public void setUserrang_enable(int userrang_enable) {
        this.userrang_enable = userrang_enable;
    }

    public String getUserrang_code() {
        return userrang_code;
    }

    public void setUserrang_code(String userrang_code) {
        this.userrang_code = userrang_code;
    }

    public String getUserrang_person() {
        return userrang_person;
    }

    public void setUserrang_person(String userrang_person) {
        this.userrang_person = userrang_person;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public int getRead_time() {
        return read_time;
    }

    public void setRead_time(int read_time) {
        this.read_time = read_time;
    }

    public int getRead_type() {
        return read_type;
    }

    public void setRead_type(int read_type) {
        this.read_type = read_type;
    }

    private int userrang_enable;
    private String userrang_code;
    private String userrang_person;
    private String create_time;
    private int read_num;
    private int read_time;
    private int read_type;

}
