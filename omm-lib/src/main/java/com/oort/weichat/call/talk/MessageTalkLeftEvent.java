package com.oort.weichat.call.talk;

import com.oort.weichat.bean.message.ChatMessage;

public class MessageTalkLeftEvent {
    public ChatMessage chatMessage;

    public MessageTalkLeftEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
