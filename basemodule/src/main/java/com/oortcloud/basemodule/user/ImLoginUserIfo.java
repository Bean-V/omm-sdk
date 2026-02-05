package com.oortcloud.basemodule.user;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/7/3-12:00.
 * Version 1.0
 * Description:
 */
public class ImLoginUserIfo {


    /**
     * birthday : 0
     * messageKey : APJkcK4L8kjxRbt8HccpxA==
     * settings : {"allowAtt":1,"allowGreet":1,"authSwitch":0,"chatRecordTimeOut":"-1.0","chatSyncTimeLen":30,"closeTelephoneFind":1,"friendFromList":"1,2,3,4,5","friendsVerify":1,"isEncrypt":0,"isKeepalive":1,"isOpenPrivacyPosition":0,"isShowMsgState":1,"isSkidRemoveHistoryMsg":1,"isTyping":0,"isUseGoogleMap":0,"isVibration":0,"multipleDevices":1,"nameSearch":1,"openService":0,"phoneSearch":1,"showLastLoginTime":-1,"showTelephone":2}
     * friendCount : 1
     * offlineNoPushMsg : 0
     * loginKey : pEQrPgFrwVhJScSqb32Aww==
     * httpKey : wQagjnOVtT3RFU838kBSxg==
     * sex : 1
     * loginToken : c29e0b4d3ff14174b71b9e7a78e1f4f9
     * multipleDevices : 1
     * walletUserNo : 0
     * login : {"isFirstLogin":0,"latitude":0,"loginTime":1751515042,"longitude":0,"model":"ALP-AL00","offlineTime":0,"osVersion":"8.1.0","serial":"61dd4ac5d19c4011802cc80df0894f54"}
     * userId : 10000006
     * myInviteCode :
     * access_token : fe886ab71e804b2a9640a0e90252716f
     * nickname : 李夜
     * payKey : YaIz14d4llrKZF24xgFmNQ==
     * isupdate : 0
     * payPassword : 0
     */

    private int birthday;
    private String messageKey;
    private SettingsBean settings;
    private int friendCount;
    private int offlineNoPushMsg;
    private String loginKey;
    private String httpKey;
    private int sex;
    private String loginToken;
    private int multipleDevices;
    private int walletUserNo;
    private LoginBean login;
    private int userId;
    private String myInviteCode;
    private String access_token;
    private String nickname;
    private String payKey;
    private int isupdate;
    private int payPassword;

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public SettingsBean getSettings() {
        return settings;
    }

    public void setSettings(SettingsBean settings) {
        this.settings = settings;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public int getOfflineNoPushMsg() {
        return offlineNoPushMsg;
    }

    public void setOfflineNoPushMsg(int offlineNoPushMsg) {
        this.offlineNoPushMsg = offlineNoPushMsg;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public String getHttpKey() {
        return httpKey;
    }

    public void setHttpKey(String httpKey) {
        this.httpKey = httpKey;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public int getMultipleDevices() {
        return multipleDevices;
    }

    public void setMultipleDevices(int multipleDevices) {
        this.multipleDevices = multipleDevices;
    }

    public int getWalletUserNo() {
        return walletUserNo;
    }

    public void setWalletUserNo(int walletUserNo) {
        this.walletUserNo = walletUserNo;
    }

    public LoginBean getLogin() {
        return login;
    }

    public void setLogin(LoginBean login) {
        this.login = login;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMyInviteCode() {
        return myInviteCode;
    }

    public void setMyInviteCode(String myInviteCode) {
        this.myInviteCode = myInviteCode;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }

    public int getIsupdate() {
        return isupdate;
    }

    public void setIsupdate(int isupdate) {
        this.isupdate = isupdate;
    }

    public int getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(int payPassword) {
        this.payPassword = payPassword;
    }

    public static class SettingsBean {
        /**
         * allowAtt : 1
         * allowGreet : 1
         * authSwitch : 0
         * chatRecordTimeOut : -1.0
         * chatSyncTimeLen : 30.0
         * closeTelephoneFind : 1
         * friendFromList : 1,2,3,4,5
         * friendsVerify : 1
         * isEncrypt : 0
         * isKeepalive : 1
         * isOpenPrivacyPosition : 0
         * isShowMsgState : 1
         * isSkidRemoveHistoryMsg : 1
         * isTyping : 0
         * isUseGoogleMap : 0
         * isVibration : 0
         * multipleDevices : 1
         * nameSearch : 1
         * openService : 0
         * phoneSearch : 1
         * showLastLoginTime : -1
         * showTelephone : 2
         */

        private int allowAtt;
        private int allowGreet;
        private int authSwitch;
        private String chatRecordTimeOut;
        private double chatSyncTimeLen;
        private int closeTelephoneFind;
        private String friendFromList;
        private int friendsVerify;
        private int isEncrypt;
        private int isKeepalive;
        private int isOpenPrivacyPosition;
        private int isShowMsgState;
        private int isSkidRemoveHistoryMsg;
        private int isTyping;
        private int isUseGoogleMap;
        private int isVibration;
        private int multipleDevices;
        private int nameSearch;
        private int openService;
        private int phoneSearch;
        private int showLastLoginTime;
        private int showTelephone;

        public int getAllowAtt() {
            return allowAtt;
        }

        public void setAllowAtt(int allowAtt) {
            this.allowAtt = allowAtt;
        }

        public int getAllowGreet() {
            return allowGreet;
        }

        public void setAllowGreet(int allowGreet) {
            this.allowGreet = allowGreet;
        }

        public int getAuthSwitch() {
            return authSwitch;
        }

        public void setAuthSwitch(int authSwitch) {
            this.authSwitch = authSwitch;
        }

        public String getChatRecordTimeOut() {
            return chatRecordTimeOut;
        }

        public void setChatRecordTimeOut(String chatRecordTimeOut) {
            this.chatRecordTimeOut = chatRecordTimeOut;
        }

        public double getChatSyncTimeLen() {
            return chatSyncTimeLen;
        }

        public void setChatSyncTimeLen(double chatSyncTimeLen) {
            this.chatSyncTimeLen = chatSyncTimeLen;
        }

        public int getCloseTelephoneFind() {
            return closeTelephoneFind;
        }

        public void setCloseTelephoneFind(int closeTelephoneFind) {
            this.closeTelephoneFind = closeTelephoneFind;
        }

        public String getFriendFromList() {
            return friendFromList;
        }

        public void setFriendFromList(String friendFromList) {
            this.friendFromList = friendFromList;
        }

        public int getFriendsVerify() {
            return friendsVerify;
        }

        public void setFriendsVerify(int friendsVerify) {
            this.friendsVerify = friendsVerify;
        }

        public int getIsEncrypt() {
            return isEncrypt;
        }

        public void setIsEncrypt(int isEncrypt) {
            this.isEncrypt = isEncrypt;
        }

        public int getIsKeepalive() {
            return isKeepalive;
        }

        public void setIsKeepalive(int isKeepalive) {
            this.isKeepalive = isKeepalive;
        }

        public int getIsOpenPrivacyPosition() {
            return isOpenPrivacyPosition;
        }

        public void setIsOpenPrivacyPosition(int isOpenPrivacyPosition) {
            this.isOpenPrivacyPosition = isOpenPrivacyPosition;
        }

        public int getIsShowMsgState() {
            return isShowMsgState;
        }

        public void setIsShowMsgState(int isShowMsgState) {
            this.isShowMsgState = isShowMsgState;
        }

        public int getIsSkidRemoveHistoryMsg() {
            return isSkidRemoveHistoryMsg;
        }

        public void setIsSkidRemoveHistoryMsg(int isSkidRemoveHistoryMsg) {
            this.isSkidRemoveHistoryMsg = isSkidRemoveHistoryMsg;
        }

        public int getIsTyping() {
            return isTyping;
        }

        public void setIsTyping(int isTyping) {
            this.isTyping = isTyping;
        }

        public int getIsUseGoogleMap() {
            return isUseGoogleMap;
        }

        public void setIsUseGoogleMap(int isUseGoogleMap) {
            this.isUseGoogleMap = isUseGoogleMap;
        }

        public int getIsVibration() {
            return isVibration;
        }

        public void setIsVibration(int isVibration) {
            this.isVibration = isVibration;
        }

        public int getMultipleDevices() {
            return multipleDevices;
        }

        public void setMultipleDevices(int multipleDevices) {
            this.multipleDevices = multipleDevices;
        }

        public int getNameSearch() {
            return nameSearch;
        }

        public void setNameSearch(int nameSearch) {
            this.nameSearch = nameSearch;
        }

        public int getOpenService() {
            return openService;
        }

        public void setOpenService(int openService) {
            this.openService = openService;
        }

        public int getPhoneSearch() {
            return phoneSearch;
        }

        public void setPhoneSearch(int phoneSearch) {
            this.phoneSearch = phoneSearch;
        }

        public int getShowLastLoginTime() {
            return showLastLoginTime;
        }

        public void setShowLastLoginTime(int showLastLoginTime) {
            this.showLastLoginTime = showLastLoginTime;
        }

        public int getShowTelephone() {
            return showTelephone;
        }

        public void setShowTelephone(int showTelephone) {
            this.showTelephone = showTelephone;
        }
    }

    public static class LoginBean {
        /**
         * isFirstLogin : 0
         * latitude : 0.0
         * loginTime : 1751515042
         * longitude : 0.0
         * model : ALP-AL00
         * offlineTime : 0
         * osVersion : 8.1.0
         * serial : 61dd4ac5d19c4011802cc80df0894f54
         */

        private int isFirstLogin;
        private double latitude;
        private int loginTime;
        private double longitude;
        private String model;
        private int offlineTime;
        private String osVersion;
        private String serial;

        public int getIsFirstLogin() {
            return isFirstLogin;
        }

        public void setIsFirstLogin(int isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(int loginTime) {
            this.loginTime = loginTime;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getOfflineTime() {
            return offlineTime;
        }

        public void setOfflineTime(int offlineTime) {
            this.offlineTime = offlineTime;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }
    }
}
