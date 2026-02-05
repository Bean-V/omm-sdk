package com.oort.weichat.bean.event;

import com.oort.weichat.bean.message.ChatMessage;

public class EventTransfer {
    private ChatMessage chatMessage;

    public EventTransfer(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
