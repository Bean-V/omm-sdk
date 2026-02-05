package com.oortcloud.contacts.bean.omm;

import java.io.Serializable;

/**
 * 用户表
 */

public class User implements Serializable {


    private static final long serialVersionUID = 8216104856016715922L;

    private String account;

    private int active;

    private int attCount;        // 关注总数

    private double balance; // 余额

    private int createTime;// 注册日期，

    private int fansCount;     // 粉丝总数

    private int friendsCount; // 朋友总数

    private int isAuth;// 是否认证

    private int isPasuse;

    private Loc loc;

    private int msgNum;

    private String nickname;// 昵称

    private String userId;// 用户Id

    private boolean notLetSeeHim;//朋友圈访问权限，不让他看

    private boolean notSeeHim;//朋友圈访问权限，不看他

    private int num;

    private int offlineNoPushMsg;

    private int onlinestate;// 好友是否在线

    private int setAccountCount;

    private int sex;// 性别 0表示女，1表示男

    private long showLastLoginTime;// 上次登录时间，

    private int status;// 状态(未知)

    private float totalConsume;

    private float totalRecharge;


    private int userType;// 用户类型


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getAttCount() {
        return attCount;
    }

    public void setAttCount(int attCount) {
        this.attCount = attCount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(int isAuth) {
        this.isAuth = isAuth;
    }

    public int getIsPasuse() {
        return isPasuse;
    }

    public void setIsPasuse(int isPasuse) {
        this.isPasuse = isPasuse;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }

    public int getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(int msgNum) {
        this.msgNum = msgNum;
    }

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String nickName) {
        this.nickname = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getNotLetSeeHim() {
        return notLetSeeHim;
    }

    public void setNotLetSeeHim(boolean notLetSeeHim) {
        this.notLetSeeHim = notLetSeeHim;
    }

    public boolean getNotSeeHim() {
        return notSeeHim;
    }

    public void setNotSeeHim(boolean notSeeHim) {
        this.notSeeHim = notSeeHim;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getOfflineNoPushMsg() {
        return offlineNoPushMsg;
    }

    public void setOfflineNoPushMsg(int offlineNoPushMsg) {
        this.offlineNoPushMsg = offlineNoPushMsg;
    }

    public int getOnlinestate() {
        return onlinestate;
    }

    public void setOnlinestate(int onlinestate) {
        this.onlinestate = onlinestate;
    }

    public int getSetAccountCount() {
        return setAccountCount;
    }

    public void setSetAccountCount(int setAccountCount) {
        this.setAccountCount = setAccountCount;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getShowLastLoginTime() {
        return showLastLoginTime;
    }

    public void setShowLastLoginTime(long showLastLoginTime) {
        this.showLastLoginTime = showLastLoginTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(float totalConsume) {
        this.totalConsume = totalConsume;
    }

    public float getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(float totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public static class Loc {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", active=" + active +
                ", attCount=" + attCount +
                ", balance=" + balance +
                ", createTime=" + createTime +
                ", fansCount=" + fansCount +
                ", friendsCount=" + friendsCount +
                ", isAuth=" + isAuth +
                ", isPasuse=" + isPasuse +
                ", loc=" + loc +
                ", msgNum=" + msgNum +
                ", nickname='" + nickname + '\'' +
                ", userId='" + userId + '\'' +
                ", notLetSeeHim=" + notLetSeeHim +
                ", notSeeHim=" + notSeeHim +
                ", num=" + num +
                ", offlineNoPushMsg=" + offlineNoPushMsg +
                ", onlinestate=" + onlinestate +
                ", setAccountCount=" + setAccountCount +
                ", sex=" + sex +
                ", showLastLoginTime=" + showLastLoginTime +
                ", status=" + status +
                ", totalConsume=" + totalConsume +
                ", totalRecharge=" + totalRecharge +
                ", userType=" + userType +
                '}';
    }
}
