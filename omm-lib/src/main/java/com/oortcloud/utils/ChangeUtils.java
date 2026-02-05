package com.oortcloud.utils;

import android.text.TextUtils;

import com.oort.weichat.bean.Friend;
import com.oortcloud.contacts.bean.omm.AttentionUser;


/**
 * @ProjectName: omm-master
 * @FileName: ChangeUtils.java
 * @Function: 转换类
 * @Author: zhangzhijun / @CreateDate: 20/03/17 14:46

 * @Version: 1.0
 */
public class ChangeUtils {
    public static Friend changeFriend(AttentionUser attentionUser){
        String userId = attentionUser.getToUserId();// 好友的Id
        Friend friend = new Friend() ;
        friend.setOwnerId(attentionUser.getUserId());
        friend.setUserId(attentionUser.getToUserId());
        if (!userId.equals(Friend.ID_SYSTEM_MESSAGE)) {
            friend.setNickName(attentionUser.getToNickname());
            friend.setRemarkName(attentionUser.getRemarkName());
            friend.setTimeCreate(attentionUser.getCreateTime());
            // 公众号的status为8，服务端返回的为2，不修改
            int status = (attentionUser.getBlacklist() == 0) ? attentionUser.getStatus() : -1;
            friend.setStatus(status);
        }

        if (attentionUser.getToUserType() == 2) {// 公众号
            friend.setStatus(Friend.STATUS_SYSTEM);
        }
        if (!TextUtils.isEmpty(attentionUser.getDescribe())) {
            friend.setDescribe(attentionUser.getDescribe());
        }

        if (attentionUser.getBlacklist() == 1) {
            friend.setStatus(Friend.STATUS_BLACKLIST);
        }
        if (attentionUser.getIsBeenBlack() == 1) {
            // friend.setStatus(Friend.STATUS_BLACKLIST);
            friend.setStatus(Friend.STATUS_19);
        }

        friend.setOfflineNoPushMsg(attentionUser.getOfflineNoPushMsg());
        friend.setTopTime(attentionUser.getOpenTopChatTime());

        friend.setChatRecordTimeOut(attentionUser.getChatRecordTimeOut());// 消息保存天数 -1/0 永久

        friend.setCompanyId(attentionUser.getCompanyId());
        friend.setRoomFlag(0);
//        friend.setVersion(newVersion);// 更新版本

        return friend;
    }

}
