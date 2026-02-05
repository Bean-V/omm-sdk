package com.oort.weichat.call.talk;

import com.oort.weichat.bean.message.ChatMessage;

public class MessageTalkJoinEvent {
    public ChatMessage chatMessage;

    public MessageTalkJoinEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
