package com.oort.weichat.call;

/**
 * Created by Administrator on 2017/6/26 0026.
 */
public class MessageEventCancelOrHangUp {
    public final int type;
    public final String toUserId;
    public final String content;
    public final long callTimeLen;

    public MessageEventCancelOrHangUp(int type, String toUserId, String content, long callTimeLen) {
        this.type = type;
        this.toUserId = toUserId;
        this.content = content;
        this.callTimeLen = callTimeLen;
    }
}