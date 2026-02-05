package com.oortcloud.revision.fragment;

public class LabSession {
    private String chatId;
    private long createTime;
    private String groupId;
    private String groupName;
    private long modifyTime;

    // 无参构造方法，用于 JSON 解析等场景
    public LabSession() {
    }

    // 有参构造方法，方便创建对象
    public LabSession(String chatId, long createTime, String groupId, String groupName, long modifyTime) {
        this.chatId = chatId;
        this.createTime = createTime;
        this.groupId = groupId;
        this.groupName = groupName;
        this.modifyTime = modifyTime;
    }

    // chatId 的 getter 和 setter 方法
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    // createTime 的 getter 和 setter 方法
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    // groupId 的 getter 和 setter 方法
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    // groupName 的 getter 和 setter 方法
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // modifyTime 的 getter 和 setter 方法
    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "LabSession{" +
                "chatId='" + chatId + '\'' +
                ", createTime=" + createTime +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
