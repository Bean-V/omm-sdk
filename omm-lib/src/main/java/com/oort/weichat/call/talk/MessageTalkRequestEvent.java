package com.oort.weichat.call.talk;

import com.oort.weichat.bean.message.ChatMessage;

public class MessageTalkRequestEvent {
    public ChatMessage chatMessage;

    public MessageTalkRequestEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
