package com.oort.weichat.ui.message;

/**
 * @Company: 奥尔特云（深圳）智慧科技有限公司
 * @Author: lukezhang
 * @Date: 2022/11/2 17:34
 */
public class AutoReplyMessageSendChat {
    private String toUserid;
    private int content;

    public AutoReplyMessageSendChat(String toUserid, int content) {
        this.toUserid = toUserid;
        this.content = content;
    }

    public String getToUserid() {
        return toUserid;
    }

    public void setToUserid(String toUserid) {
        this.toUserid = toUserid;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }
}
