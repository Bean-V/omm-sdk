package com.oort.weichat.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Label {

    @DatabaseField(generatedId = true)
    private int _id;

    @DatabaseField(canBeNull = false)
    private String userId;// 标签拥有者

    @DatabaseField
    private String groupId;// 标签Id

    @DatabaseField
    private String groupName;// 标签名字

    @DatabaseField
    private String userIdList;// 该标签下的用户id     [100,120]


    @DatabaseField
    private int is_open;

    @DatabaseField
    private String name;

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @DatabaseField
    private String sort;

    @DatabaseField
    private String tid;

    @DatabaseField
    private String uuid;

    private boolean isSelected;// 该标签是否选中
    private boolean isSelectedInBelong;// 该标签是否选中

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(String userIdList) {
        this.userIdList = userIdList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelectedInBelong() {
        return isSelectedInBelong;
    }

    public void setSelectedInBelong(boolean selectedInBelong) {
        isSelectedInBelong = selectedInBelong;
    }
}
