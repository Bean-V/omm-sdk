package com.oort.weichat.call;

import com.oort.weichat.bean.message.ChatMessage;

/**
 * Created by Administrator on 2017/6/26 0026.
 */
public class MessageCallingEvent {
    public ChatMessage chatMessage;

    public MessageCallingEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}