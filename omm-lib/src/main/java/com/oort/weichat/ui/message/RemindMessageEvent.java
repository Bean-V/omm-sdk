package com.oort.weichat.ui.message;

import com.oort.weichat.bean.message.ChatMessage;

/**
 * @Company: 奥尔特云（深圳）智慧科技有限公司
 * @Author: lukezhang
 * @Date: 2022/10/29 16:23
 */
public class RemindMessageEvent {
    private long endtime;
    private ChatMessage chatMessage;

    public RemindMessageEvent(long endtime, ChatMessage chatMessage) {
        this.endtime = endtime;
        this.chatMessage = chatMessage;
    }

    public long getEndtime() {
        return endtime;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
