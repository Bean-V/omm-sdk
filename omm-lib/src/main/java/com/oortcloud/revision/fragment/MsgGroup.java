package com.oortcloud.revision.fragment;

import com.alibaba.fastjson.annotation.JSONField;
import com.oort.weichat.R;

import java.util.ArrayList;
import java.util.List;

public class MsgGroup {
    // 原有类型
    public static final int TYPE_DEFAULT = 0; // 一级默认分组（全部/未读等）
    public static final int TYPE_CUSTOM = 1;  // 一级自定义分组
    public static final int TYPE_SUB_LABEL = 2; // 二级标签（label下的子标签）


    @JSONField(name = "id")
    private String groupId;
    private String groupName;
    private int iconRes;
    private int type;
    private long modifyTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    private long createTime;


    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getName() {
        return groupName;
    }

    public void setGroupName(String name) {
        this.groupName = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSubLabels(List<MsgGroup> subLabels) {
        this.subLabels = subLabels;
    }

    private int unreadCount;
    // 新增：二级标签列表（仅label分组需要）
    private List<MsgGroup> subLabels;
    public MsgGroup(){

    }
    // 构造方法（一级分组）
    public MsgGroup(String id, String name, int iconRes, int type) {
        this.groupId = id;
        this.groupName = name;
        this.iconRes = iconRes;
        this.type = type;
        this.subLabels = new ArrayList<>();
    }

    // 构造方法（二级标签）
    public MsgGroup(String id, String name,int iconRes) {
        this.groupId = id;
        this.groupName = name;
        this.iconRes = iconRes;
        this.type = TYPE_SUB_LABEL;
        this.iconRes = R.drawable.ic_sub_label; // 二级标签默认图标
        this.subLabels = new ArrayList<>();
    }

    // getter/setter
    public List<MsgGroup> getSubLabels() {
        return subLabels;
    }

    public void addSubLabel(MsgGroup subLabel) {
        subLabels.add(subLabel);
    }

    public void removeAllSubLabel() {
         subLabels.clear();
    }

    public void removeSubLabel(String subLabelId) {
        subLabels.removeIf(label -> label.getGroupId().equals(subLabelId));
    }

    // 其他原有getter/setter（id/name/iconRes/type/unreadCount）
    // ...
}


