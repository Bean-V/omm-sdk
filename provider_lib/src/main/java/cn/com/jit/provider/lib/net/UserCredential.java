package cn.com.jit.provider.lib.net;


import java.io.Serializable;


public class UserCredential implements Serializable {


    /**
     * head : {"duration":{"startTime":"1633954941000","endTime":"1633998141000"},"credType":"1","version":"1.0","token":{"tokenId":"dff3800d-746f-4511-8fb8-56cf681254caf0","exten":"","orgId":"650000000000"}}
     * load : {"userInfo":{"sfzh":"650102196909016810","xm":"王光忠","exten":"exten","userId":"EC2F741A26G7F0926C","jh":"code","orgId":"650000000000"}}
     * serverSign : {"signature":"cDY0RrnqW5h8nyTrpgGB5DAgSCs3aKf4RfkECl1a5uE808tIBqOf5nO82ziR1N8ag5Qsn3bHBE4jJhlnL1pMx4hF7EyqQIYcKWl+EMJ9ORtibHkjO9rKYIx8TneKzQA8l05l5WVwEOqz1kdRBhu7RZBdQEe/jbCGqkFqkiBH9jJJYa13XkmtqdJf/LA/sDH/4xJeV6n8vnt5Sj0C+xUbIBV0i4dgHTVmFsKApOfMthbQm1YwC6yEvaKm9e3CpVnWZhiiazsEaGhBC06Jimoq5RHMzi4yBIEOP/JZzAMdDZ5C63J24VkR6cWgGRRIMXBUkKXyYOsw51b8t+iw1usndQ==","sn":"6C526B05B017236C","alg":"SHA256","url":"URL"}
     */

    private HeadBean head;
    private LoadBean load;
    private ServerSignBean serverSign;

    public HeadBean getHead() {
        return head;
    }

    public void setHead(HeadBean head) {
        this.head = head;
    }

    public LoadBean getLoad() {
        return load;
    }

    public void setLoad(LoadBean load) {
        this.load = load;
    }

    public ServerSignBean getServerSign() {
        return serverSign;
    }

    public void setServerSign(ServerSignBean serverSign) {
        this.serverSign = serverSign;
    }

    public static class HeadBean implements Serializable {
        /**
         * duration : {"startTime":"1633954941000","endTime":"1633998141000"}
         * credType : 1
         * version : 1.0
         * token : {"tokenId":"dff3800d-746f-4511-8fb8-56cf681254caf0","exten":"","orgId":"650000000000"}
         */

        private DurationBean duration;
        private String credType;
        private String version;
        private TokenBean token;

        public DurationBean getDuration() {
            return duration;
        }

        public void setDuration(DurationBean duration) {
            this.duration = duration;
        }

        public String getCredType() {
            return credType;
        }

        public void setCredType(String credType) {
            this.credType = credType;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public TokenBean getToken() {
            return token;
        }

        public void setToken(TokenBean token) {
            this.token = token;
        }

        public static class DurationBean implements Serializable {
            /**
             * startTime : 1633954941000
             * endTime : 1633998141000
             */

            private String startTime;
            private String endTime;

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }
        }

        public static class TokenBean implements Serializable {
            /**
             * tokenId : dff3800d-746f-4511-8fb8-56cf681254caf0
             * exten :
             * orgId : 650000000000
             */

            private String tokenId;
            private String exten;
            private String orgId;

            public String getTokenId() {
                return tokenId;
            }

            public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
            }

            public String getExten() {
                return exten;
            }

            public void setExten(String exten) {
                this.exten = exten;
            }

            public String getOrgId() {
                return orgId;
            }

            public void setOrgId(String orgId) {
                this.orgId = orgId;
            }
        }
    }

    public static class LoadBean implements Serializable {
        /**
         * userInfo : {"sfzh":"650102196909016810","xm":"王光忠","exten":"exten","userId":"EC2F741A26G7F0926C","jh":"code","orgId":"650000000000"}
         */

        private UserInfoBean userInfo;

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }

        public static class UserInfoBean implements Serializable {
            /**
             * sfzh : 650102196909016810
             * xm : 王光忠
             * exten : exten
             * userId : EC2F741A26G7F0926C
             * jh : code
             * orgId : 650000000000
             */

            private String sfzh;
            private String xm;
            private String exten;
            private String userId;
            private String jh;
            private String orgId;

            public String getSfzh() {
                return sfzh;
            }

            public void setSfzh(String sfzh) {
                this.sfzh = sfzh;
            }

            public String getXm() {
                return xm;
            }

            public void setXm(String xm) {
                this.xm = xm;
            }

            public String getExten() {
                return exten;
            }

            public void setExten(String exten) {
                this.exten = exten;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getJh() {
                return jh;
            }

            public void setJh(String jh) {
                this.jh = jh;
            }

            public String getOrgId() {
                return orgId;
            }

            public void setOrgId(String orgId) {
                this.orgId = orgId;
            }
        }
    }

    public static class ServerSignBean implements Serializable {
        /**
         * signature : cDY0RrnqW5h8nyTrpgGB5DAgSCs3aKf4RfkECl1a5uE808tIBqOf5nO82ziR1N8ag5Qsn3bHBE4jJhlnL1pMx4hF7EyqQIYcKWl+EMJ9ORtibHkjO9rKYIx8TneKzQA8l05l5WVwEOqz1kdRBhu7RZBdQEe/jbCGqkFqkiBH9jJJYa13XkmtqdJf/LA/sDH/4xJeV6n8vnt5Sj0C+xUbIBV0i4dgHTVmFsKApOfMthbQm1YwC6yEvaKm9e3CpVnWZhiiazsEaGhBC06Jimoq5RHMzi4yBIEOP/JZzAMdDZ5C63J24VkR6cWgGRRIMXBUkKXyYOsw51b8t+iw1usndQ==
         * sn : 6C526B05B017236C
         * alg : SHA256
         * url : URL
         */

        private String signature;
        private String sn;
        private String alg;
        private String url;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getAlg() {
            return alg;
        }

        public void setAlg(String alg) {
            this.alg = alg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
