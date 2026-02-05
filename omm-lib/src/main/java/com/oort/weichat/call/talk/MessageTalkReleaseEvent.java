package com.oort.weichat.call.talk;

import com.oort.weichat.bean.message.ChatMessage;

public class MessageTalkReleaseEvent {
    public ChatMessage chatMessage;

    public MessageTalkReleaseEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
