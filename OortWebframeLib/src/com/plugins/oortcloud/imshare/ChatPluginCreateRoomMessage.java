package com.plugins.oortcloud.imshare;

/**
 * 邀请 加入会议
 */
public class ChatPluginCreateRoomMessage {
    public final String packageName;
    public final String roomId;
    public final String roomName;
    public final String[] userIds;
    public final String ext;
    public final int type;

    public ChatPluginCreateRoomMessage(String roomId, String roomName, String[] userIds, String ext, int type,String packageName) {
        this.roomId = packageName.replaceAll("\\.","_") + "_" + roomId.replaceAll("-","_");
        this.roomName = roomName;
        this.userIds = userIds;
        this.ext = ext;
        this.type= type;
        this.packageName = packageName;

    }
}