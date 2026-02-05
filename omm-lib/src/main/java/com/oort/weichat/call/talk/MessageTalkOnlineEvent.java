package com.oort.weichat.call.talk;

import com.oort.weichat.bean.message.ChatMessage;

public class MessageTalkOnlineEvent {
    public ChatMessage chatMessage;

    public MessageTalkOnlineEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
