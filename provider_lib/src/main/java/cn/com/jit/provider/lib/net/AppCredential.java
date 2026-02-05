package cn.com.jit.provider.lib.net;


import java.io.Serializable;

public class AppCredential implements Serializable {


    /**
     * head : {"duration":{"startTime":"1633958201000","endTime":"1634001401000"},"credType":"2","version":"1.0","token":{"tokenId":"54b2e224-2140-4969-9449-5029f2f5e00285","exten":"","orgId":"650000000000"}}
     * load : {"appInfo":{"csType":"2","appRegionalismCode":"650000000000","appId":"e2fb8cd3-b719-450b-8a44-f230761e3c2d","name":"克拉玛依","packageName":"","exten":"exten","networkAreaCode":"2","orgId":"650000000000"}}
     * serverSign : {"signature":"SpiJQkoyB7HgrFPE0abukBOG5DWJF8yHty7mu95B9a1F/TYA1RZRB/Q6BpcXsmI5wtpfh5O3nnHrxM/eqoF3/m44QR6yTiFFhvjlUai9gi8IuQIdDeQSWARFF7c1eno7b6RGy+Nq4JYEp4OVlmu/oEoBMEYe21w2XfaiKAI7Dv6YCEnwZ+8PdI8dMda1YCUI4m5aHOBMGH+RLUxZjlrqa2ImcIEcoiCJzsYnTxa7Oh3HbnK7xoQXNlxC+Ac5ICk6lTo2IHYqcIaT3SHawAIrm8w14a4VTRO/MiAfGYYpC3cNdTewKoRoarAmci4gnAZuLL03QqMGCxXlJQ3m2yLhyg==","sn":"6C526B05B017236C","alg":"SHA256","url":"URL"}
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
         * duration : {"startTime":"1633958201000","endTime":"1634001401000"}
         * credType : 2
         * version : 1.0
         * token : {"tokenId":"54b2e224-2140-4969-9449-5029f2f5e00285","exten":"","orgId":"650000000000"}
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
             * startTime : 1633958201000
             * endTime : 1634001401000
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
             * tokenId : 54b2e224-2140-4969-9449-5029f2f5e00285
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
         * appInfo : {"csType":"2","appRegionalismCode":"650000000000","appId":"e2fb8cd3-b719-450b-8a44-f230761e3c2d","name":"克拉玛依","packageName":"","exten":"exten","networkAreaCode":"2","orgId":"650000000000"}
         */

        private AppInfoBean appInfo;

        public AppInfoBean getAppInfo() {
            return appInfo;
        }

        public void setAppInfo(AppInfoBean appInfo) {
            this.appInfo = appInfo;
        }

        public static class AppInfoBean implements Serializable {
            /**
             * csType : 2
             * appRegionalismCode : 650000000000
             * appId : e2fb8cd3-b719-450b-8a44-f230761e3c2d
             * name : 克拉玛依
             * packageName :
             * exten : exten
             * networkAreaCode : 2
             * orgId : 650000000000
             */

            private String csType;
            private String appRegionalismCode;
            private String appId;
            private String name;
            private String packageName;
            private String exten;
            private String networkAreaCode;
            private String orgId;

            public String getCsType() {
                return csType;
            }

            public void setCsType(String csType) {
                this.csType = csType;
            }

            public String getAppRegionalismCode() {
                return appRegionalismCode;
            }

            public void setAppRegionalismCode(String appRegionalismCode) {
                this.appRegionalismCode = appRegionalismCode;
            }

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPackageName() {
                return packageName;
            }

            public void setPackageName(String packageName) {
                this.packageName = packageName;
            }

            public String getExten() {
                return exten;
            }

            public void setExten(String exten) {
                this.exten = exten;
            }

            public String getNetworkAreaCode() {
                return networkAreaCode;
            }

            public void setNetworkAreaCode(String networkAreaCode) {
                this.networkAreaCode = networkAreaCode;
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
         * signature : SpiJQkoyB7HgrFPE0abukBOG5DWJF8yHty7mu95B9a1F/TYA1RZRB/Q6BpcXsmI5wtpfh5O3nnHrxM/eqoF3/m44QR6yTiFFhvjlUai9gi8IuQIdDeQSWARFF7c1eno7b6RGy+Nq4JYEp4OVlmu/oEoBMEYe21w2XfaiKAI7Dv6YCEnwZ+8PdI8dMda1YCUI4m5aHOBMGH+RLUxZjlrqa2ImcIEcoiCJzsYnTxa7Oh3HbnK7xoQXNlxC+Ac5ICk6lTo2IHYqcIaT3SHawAIrm8w14a4VTRO/MiAfGYYpC3cNdTewKoRoarAmci4gnAZuLL03QqMGCxXlJQ3m2yLhyg==
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
