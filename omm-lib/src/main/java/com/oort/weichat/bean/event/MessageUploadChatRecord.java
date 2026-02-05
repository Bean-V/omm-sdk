package com.oort.weichat.bean.event;

import com.oort.weichat.ui.message.ChatActivity;

/**
 * Created by Administrator on 2017/6/26 0026.
 */
public class MessageUploadChatRecord {
    public String chatIds;

    /**
     * @see ChatActivity
     */
    public MessageUploadChatRecord(String chatIds) {
        this.chatIds = chatIds;
    }
}