package com.oort.weichat.fragment;

import java.io.Serializable;

public class LabelSessionRelation implements Serializable {
    private String labelId;       // 二级标签ID（如"sub_label_123456"）
    private String sessionId;     // 会话ID（单聊=用户ID，群聊=群ID）
    private int sessionType;      // 会话类型：0=单聊，1=群聊
    private long createTime;      // 创建时间

    public LabelSessionRelation(String labelId, String sessionId, int sessionType) {
        this.labelId = labelId;
        this.sessionId = sessionId;
        this.sessionType = sessionType;
        this.createTime = System.currentTimeMillis();
    }

    // Getter/Setter
    public String getLabelId() { return labelId; }
    public String getSessionId() { return sessionId; }
    public int getSessionType() { return sessionType; }
    // ...其他Getter/Setter
}
