package com.oortcloud.contacts.message;

public class MessageEventInviteCall {
    public int type;

    public MessageEventInviteCall(int type, String touserid, String toUserName,String roomId) {
        this.type = type;
        this.touserid = touserid;
        this.tousername = toUserName;
        this.roomId = roomId;
    }

    public String touserid;
    public String tousername;
    public String roomId;

}
