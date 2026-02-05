package com.oortcloud.appstore.bean;

/**
 * @ProjectName: AppStore-master
 * @FileName: ClassifyInfo.java
 * @Function: 分类信息
 * @Author: zhangzhijun / @CreateDate: 20/02/28 13:29
 * @Version: 1.0
 */
public class ClassifyInfo {
    private int id;
    //创建时间
    private long created_on;
    //修改时间
    private long modified_on;
    //类型uid
    private String uid;
    //类型名称
    private String name;
    //分类类型
    private int classify_type;
    private int classify_order;
    //uuid
    private String uuid;
    //描述
    private String description;
    //状态
    private int status;

    public ClassifyInfo(String name) {
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClassify_type() {
        return classify_type;
    }

    public void setClassify_type(int classify_type) {
        this.classify_type = classify_type;
    }

    public int getClassify_order() {
        return classify_order;
    }

    public void setClassify_order(int classify_order) {
        this.classify_order = classify_order;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClassifyInfo{" +
                "id=" + id +
                ", created_on=" + created_on +
                ", modified_on=" + modified_on +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", classify_type=" + classify_type +
                ", classify_order=" + classify_order +
                ", uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
