package com.plugins.oortcloud.context.message;

import org.apache.cordova.CallbackContext;

import java.util.Collections;
import java.util.List;

// GroupCreateEvent.java
public class GroupCreateEvent {
    public enum Status { PENDING, SUCCESS, FAILED }

    // 建群参数
    private final String groupName;
    private final List<String> groupMembers;
    private final String groupDesc;
    private final String groupType;
    private final String callbackUrl;

    // 结果参数
    private Status status;
    private String groupId;
    private String errorMsg;
    private final CallbackContext callbackContext;

    public static class Builder {
        // 必填参数
        private final String groupName;
        private final List<String> groupMembers;
        private final CallbackContext callbackContext;

        // 可选参数
        private String groupDesc = "";
        private String groupAvatar;
        private String OtherOptions ;

        public String getGroupType() {
            return groupType;
        }

        public String getGroupName() {
            return groupName;
        }

        public List<String> getGroupMembers() {
            return groupMembers;
        }

        public CallbackContext getCallbackContext() {
            return callbackContext;
        }

        public String getGroupDesc() {
            return groupDesc;
        }

        public String getCallbackUrl() {
            return callbackUrl;
        }

        private String groupType = "normal";
        private String callbackUrl = "";

        public Builder(String groupName, List<String> groupMembers, CallbackContext callbackContext) {
            this.groupName = groupName;
            this.groupMembers = Collections.unmodifiableList(groupMembers);
            this.callbackContext = callbackContext;
        }

        public Builder setGroupDesc(String desc) {
            this.groupDesc = desc;
            return this;
        }

        public Builder setGroupType(String type) {
            this.groupType = type;
            return this;
        }

        public Builder setCallbackUrl(String url) {
            this.callbackUrl = url;
            return this;
        }

        public GroupCreateEvent buildPending() {
            GroupCreateEvent event = new GroupCreateEvent(this);
            event.status = Status.PENDING;
            return event;
        }
    }

    private GroupCreateEvent(Builder builder) {
        this.groupName = builder.groupName;
        this.groupMembers = builder.groupMembers;
        this.groupDesc = builder.groupDesc;
        this.groupType = builder.groupType;
        this.callbackUrl = builder.callbackUrl;
        this.callbackContext = builder.callbackContext;
    }

    // 状态变更方法
    public void markSuccess(String groupId) {
        this.status = Status.SUCCESS;
        this.groupId = groupId;
    }

    public void markFailed(String errorMsg) {
        this.status = Status.FAILED;
        this.errorMsg = errorMsg;
    }


}
