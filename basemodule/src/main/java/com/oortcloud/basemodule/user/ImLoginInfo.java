package com.oortcloud.basemodule.user;


/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/7/3-11:49.
 * Version 1.0
 * Description:
 */
public class ImLoginInfo {


    /**
     * code : 200
     * currentTime : 1754532464514
     * data : {"data":"CwtZ8Q4dHJf9hfak21dq+8F2RIrYVyWJcd258PRQfWLn6VJhDoL/wN/WMSMYuBX6RW5hGZp/zNgPf0MuQRfLce3EZlwcXsURxO9zKXo9MbfDtpl8/C9PdIRO/WV2vQBLrFlIBw7sfoEBNrLCZiXBYSOETb9LB5OtrFI2WKMhx8O6kwgngUAsiSFBa7B2axroCYmvzUtgIHfCh8Uy+uJqcJsgdTbdB5MSQ6i0y4OKYoSNeCUjbglxVXlm5pxc4MFXd98SSU3XVutB8vuW05XIA8orECsYgxHDY33UekSMycPjEVrPeKnzJdX7a6Fn8ceOR1eH8bgKK4JPFDYLNWmXie+R6TNRefT472X8zxf0Bt75YsXRquKAAjSsAYEKyaty9QdTqNl711iayxC4w1gpSg27rIz1vrl2fR9mcRIKRlTZFNb+4pikyTzYvPZIRYezLFNeSNeCJIfashmaxbblaPNp0gYnhwogJtBjQzmhNwy9mfgfSyb+tkhaEepIucARyR4LiTRYV75fkHl237W+WPYlRIrtz7Vqm50WVcI3tKownkkEIEvurPq9jaF90HZfC+PobKwCjecrbosiFBq6SzHU6sbCtPebKTcShNgEgu5XkZOMH1Ta/U5b3FT6AJEA80vAvZ8dOvPkS2Hmoz6+VkkrqtI4TdcUH43vlQwsewHpZqpPhP6j4KNuZunnmq/M7C0seZCvD0yf5d5S1kXYHrv0Lv/XktLkWL/Kfquodh8hp/mkeGWvLmSqAlHZ4jbjnLsTj/uO9SsCwGEEXpz4M30vE13uO+6HK3hq7LbrfI1mZ0OmZaU4UpkrZPM+iF2SPuj/Tft6Sa0+QzT4BMLO9C3gslV3bBhHUPeRe4t2bSa0KwViAYC9qiTC9fvtx3vugqtZrZL7CQdWlLfJGdhg6KvyupFIabFse7K98M4DNNX9g6sja7UNVpoe6fZU2vhzThfPs7Jam+LoNmUcencYM17QWloZetV/MWFcue4n38+ZfQpSTrehFKwN3KYHrFbwbEKqC/J7bHTK/5pkX4RNIm/OVyuzAacJHc+OEYtmgGQFyF2bU+tuDXC6rj6TLtvf7nIioPWWPb7Eq7I8IQptcW9V/VwPfuK7nMBWg4hL1iSVKBIfmOudwFSM7ied97u18PLo8IfwPVBNcJgkS3E+bNmUFVOmkh6YTjmkWCoEswDRfHquPaeFxCvbyltXJiJZtH1r7Fd6s7gfAuh/FzpSzrQrqpmFRd7lNqTV5shVwElO/iKpdqc8V5WEVHC8PCv3jMQn34PIYui9epphAK2+yJu5IhHoxI0hymBkZhK3xPQz0TO+00tZu0lJNBldnsk9Qc3ldf+/00H9PKqwUY5GwkVIeEqSikweP6YZFV7Axi58nro6h664FY6mmWgDpu6C","userInfo":{"userId":"922f3955-4f16-447f-b054-9d7711f35f7d","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userName":"李夜","isTenantAdmin":0,"isAdmin":0,"uniqueId":"7c7eda93-bec7-46b5-96d0-cca26e061438","loginTime":"2025-08-07 10:07:44","lastRequestTime":"2025-08-07 10:07:44","loginIP":"172.20.181.152","loginType":1,"client":"android","accessToken":"aee2f58690e84c3c99746689b13c03f0","loginfrom":"user","logincode":{"loginId":"18948726601","loginFrom":1,"timestamp":1754532460,"client":"android","code":"","captchaID":"","slideID":"","loginType":1,"identityType":0,"realIdentityType":0,"uniqueId":"7c7eda93-bec7-46b5-96d0-cca26e061438","token":"8304c5795cc04f0091bbe299c9b19082"},"login_step":null,"current_step":0,"auth_token":"","identifier":"","identifier_type":0}}
     * msg : 成功
     * resultCode : 1
     */

    private int code;
    private long currentTime;
    private DataBean data;
    private String msg;
    private int resultCode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public static class DataBean {
        /**
         * data : CwtZ8Q4dHJf9hfak21dq+8F2RIrYVyWJcd258PRQfWLn6VJhDoL/wN/WMSMYuBX6RW5hGZp/zNgPf0MuQRfLce3EZlwcXsURxO9zKXo9MbfDtpl8/C9PdIRO/WV2vQBLrFlIBw7sfoEBNrLCZiXBYSOETb9LB5OtrFI2WKMhx8O6kwgngUAsiSFBa7B2axroCYmvzUtgIHfCh8Uy+uJqcJsgdTbdB5MSQ6i0y4OKYoSNeCUjbglxVXlm5pxc4MFXd98SSU3XVutB8vuW05XIA8orECsYgxHDY33UekSMycPjEVrPeKnzJdX7a6Fn8ceOR1eH8bgKK4JPFDYLNWmXie+R6TNRefT472X8zxf0Bt75YsXRquKAAjSsAYEKyaty9QdTqNl711iayxC4w1gpSg27rIz1vrl2fR9mcRIKRlTZFNb+4pikyTzYvPZIRYezLFNeSNeCJIfashmaxbblaPNp0gYnhwogJtBjQzmhNwy9mfgfSyb+tkhaEepIucARyR4LiTRYV75fkHl237W+WPYlRIrtz7Vqm50WVcI3tKownkkEIEvurPq9jaF90HZfC+PobKwCjecrbosiFBq6SzHU6sbCtPebKTcShNgEgu5XkZOMH1Ta/U5b3FT6AJEA80vAvZ8dOvPkS2Hmoz6+VkkrqtI4TdcUH43vlQwsewHpZqpPhP6j4KNuZunnmq/M7C0seZCvD0yf5d5S1kXYHrv0Lv/XktLkWL/Kfquodh8hp/mkeGWvLmSqAlHZ4jbjnLsTj/uO9SsCwGEEXpz4M30vE13uO+6HK3hq7LbrfI1mZ0OmZaU4UpkrZPM+iF2SPuj/Tft6Sa0+QzT4BMLO9C3gslV3bBhHUPeRe4t2bSa0KwViAYC9qiTC9fvtx3vugqtZrZL7CQdWlLfJGdhg6KvyupFIabFse7K98M4DNNX9g6sja7UNVpoe6fZU2vhzThfPs7Jam+LoNmUcencYM17QWloZetV/MWFcue4n38+ZfQpSTrehFKwN3KYHrFbwbEKqC/J7bHTK/5pkX4RNIm/OVyuzAacJHc+OEYtmgGQFyF2bU+tuDXC6rj6TLtvf7nIioPWWPb7Eq7I8IQptcW9V/VwPfuK7nMBWg4hL1iSVKBIfmOudwFSM7ied97u18PLo8IfwPVBNcJgkS3E+bNmUFVOmkh6YTjmkWCoEswDRfHquPaeFxCvbyltXJiJZtH1r7Fd6s7gfAuh/FzpSzrQrqpmFRd7lNqTV5shVwElO/iKpdqc8V5WEVHC8PCv3jMQn34PIYui9epphAK2+yJu5IhHoxI0hymBkZhK3xPQz0TO+00tZu0lJNBldnsk9Qc3ldf+/00H9PKqwUY5GwkVIeEqSikweP6YZFV7Axi58nro6h664FY6mmWgDpu6C
         * userInfo : {"userId":"922f3955-4f16-447f-b054-9d7711f35f7d","tenantId":"0e391fd7-1033-4f09-88c0-187582fee462","userName":"李夜","isTenantAdmin":0,"isAdmin":0,"uniqueId":"7c7eda93-bec7-46b5-96d0-cca26e061438","loginTime":"2025-08-07 10:07:44","lastRequestTime":"2025-08-07 10:07:44","loginIP":"172.20.181.152","loginType":1,"client":"android","accessToken":"aee2f58690e84c3c99746689b13c03f0","loginfrom":"user","logincode":{"loginId":"18948726601","loginFrom":1,"timestamp":1754532460,"client":"android","code":"","captchaID":"","slideID":"","loginType":1,"identityType":0,"realIdentityType":0,"uniqueId":"7c7eda93-bec7-46b5-96d0-cca26e061438","token":"8304c5795cc04f0091bbe299c9b19082"},"login_step":null,"current_step":0,"auth_token":"","identifier":"","identifier_type":0}
         */
        private ImLoginUserIfo imLoginUserInfo;

        private String data;
        private UserInfoBean userInfo;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public ImLoginUserIfo getImLoginUserInfo() {
            return imLoginUserInfo;
        }

        public void setImLoginUserInfo(ImLoginUserIfo imLoginUserIfo) {
            this.imLoginUserInfo = imLoginUserIfo;
        }

        public static class UserInfoBean {
            /**
             * userId : 922f3955-4f16-447f-b054-9d7711f35f7d
             * tenantId : 0e391fd7-1033-4f09-88c0-187582fee462
             * userName : 李夜
             * isTenantAdmin : 0
             * isAdmin : 0
             * uniqueId : 7c7eda93-bec7-46b5-96d0-cca26e061438
             * loginTime : 2025-08-07 10:07:44
             * lastRequestTime : 2025-08-07 10:07:44
             * loginIP : 172.20.181.152
             * loginType : 1
             * client : android
             * accessToken : aee2f58690e84c3c99746689b13c03f0
             * loginfrom : user
             * logincode : {"loginId":"18948726601","loginFrom":1,"timestamp":1754532460,"client":"android","code":"","captchaID":"","slideID":"","loginType":1,"identityType":0,"realIdentityType":0,"uniqueId":"7c7eda93-bec7-46b5-96d0-cca26e061438","token":"8304c5795cc04f0091bbe299c9b19082"}
             * login_step : null
             * current_step : 0
             * auth_token :
             * identifier :
             * identifier_type : 0
             */

            private String userId;
            private String tenantId;
            private String userName;
            private int isTenantAdmin;
            private int isAdmin;
            private String uniqueId;
            private String loginTime;
            private String lastRequestTime;
            private String loginIP;
            private int loginType;
            private String client;
            private String accessToken;
            private String loginfrom;
            private LogincodeBean logincode;
            private Object login_step;
            private int current_step;
            private String auth_token;
            private String identifier;
            private int identifier_type;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getTenantId() {
                return tenantId;
            }

            public void setTenantId(String tenantId) {
                this.tenantId = tenantId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getIsTenantAdmin() {
                return isTenantAdmin;
            }

            public void setIsTenantAdmin(int isTenantAdmin) {
                this.isTenantAdmin = isTenantAdmin;
            }

            public int getIsAdmin() {
                return isAdmin;
            }

            public void setIsAdmin(int isAdmin) {
                this.isAdmin = isAdmin;
            }

            public String getUniqueId() {
                return uniqueId;
            }

            public void setUniqueId(String uniqueId) {
                this.uniqueId = uniqueId;
            }

            public String getLoginTime() {
                return loginTime;
            }

            public void setLoginTime(String loginTime) {
                this.loginTime = loginTime;
            }

            public String getLastRequestTime() {
                return lastRequestTime;
            }

            public void setLastRequestTime(String lastRequestTime) {
                this.lastRequestTime = lastRequestTime;
            }

            public String getLoginIP() {
                return loginIP;
            }

            public void setLoginIP(String loginIP) {
                this.loginIP = loginIP;
            }

            public int getLoginType() {
                return loginType;
            }

            public void setLoginType(int loginType) {
                this.loginType = loginType;
            }

            public String getClient() {
                return client;
            }

            public void setClient(String client) {
                this.client = client;
            }

            public String getAccessToken() {
                return accessToken;
            }

            public void setAccessToken(String accessToken) {
                this.accessToken = accessToken;
            }

            public String getLoginfrom() {
                return loginfrom;
            }

            public void setLoginfrom(String loginfrom) {
                this.loginfrom = loginfrom;
            }

            public LogincodeBean getLogincode() {
                return logincode;
            }

            public void setLogincode(LogincodeBean logincode) {
                this.logincode = logincode;
            }

            public Object getLogin_step() {
                return login_step;
            }

            public void setLogin_step(Object login_step) {
                this.login_step = login_step;
            }

            public int getCurrent_step() {
                return current_step;
            }

            public void setCurrent_step(int current_step) {
                this.current_step = current_step;
            }

            public String getAuth_token() {
                return auth_token;
            }

            public void setAuth_token(String auth_token) {
                this.auth_token = auth_token;
            }

            public String getIdentifier() {
                return identifier;
            }

            public void setIdentifier(String identifier) {
                this.identifier = identifier;
            }

            public int getIdentifier_type() {
                return identifier_type;
            }

            public void setIdentifier_type(int identifier_type) {
                this.identifier_type = identifier_type;
            }

            public static class LogincodeBean {
                /**
                 * loginId : 18948726601
                 * loginFrom : 1
                 * timestamp : 1754532460
                 * client : android
                 * code :
                 * captchaID :
                 * slideID :
                 * loginType : 1
                 * identityType : 0
                 * realIdentityType : 0
                 * uniqueId : 7c7eda93-bec7-46b5-96d0-cca26e061438
                 * token : 8304c5795cc04f0091bbe299c9b19082
                 */

                private String loginId;
                private int loginFrom;
                private int timestamp;
                private String client;
                private String code;
                private String captchaID;
                private String slideID;
                private int loginType;
                private int identityType;
                private int realIdentityType;
                private String uniqueId;
                private String token;

                public String getLoginId() {
                    return loginId;
                }

                public void setLoginId(String loginId) {
                    this.loginId = loginId;
                }

                public int getLoginFrom() {
                    return loginFrom;
                }

                public void setLoginFrom(int loginFrom) {
                    this.loginFrom = loginFrom;
                }

                public int getTimestamp() {
                    return timestamp;
                }

                public void setTimestamp(int timestamp) {
                    this.timestamp = timestamp;
                }

                public String getClient() {
                    return client;
                }

                public void setClient(String client) {
                    this.client = client;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getCaptchaID() {
                    return captchaID;
                }

                public void setCaptchaID(String captchaID) {
                    this.captchaID = captchaID;
                }

                public String getSlideID() {
                    return slideID;
                }

                public void setSlideID(String slideID) {
                    this.slideID = slideID;
                }

                public int getLoginType() {
                    return loginType;
                }

                public void setLoginType(int loginType) {
                    this.loginType = loginType;
                }

                public int getIdentityType() {
                    return identityType;
                }

                public void setIdentityType(int identityType) {
                    this.identityType = identityType;
                }

                public int getRealIdentityType() {
                    return realIdentityType;
                }

                public void setRealIdentityType(int realIdentityType) {
                    this.realIdentityType = realIdentityType;
                }

                public String getUniqueId() {
                    return uniqueId;
                }

                public void setUniqueId(String uniqueId) {
                    this.uniqueId = uniqueId;
                }

                public String getToken() {
                    return token;
                }

                public void setToken(String token) {
                    this.token = token;
                }
            }
        }
    }
}
