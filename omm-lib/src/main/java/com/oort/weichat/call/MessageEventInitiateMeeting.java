package com.oort.weichat.call;

import java.util.List;

public class MessageEventInitiateMeeting {
    public final int type;
    public final List<String> list;

    public final boolean inRooom;
    public final String roomId;
    public MessageEventInitiateMeeting(int type, List<String> list,boolean inRooom,String roomId) {
        this.type = type;
        this.list = list;
        this.inRooom = inRooom;
        this.roomId = roomId;
    }
}
