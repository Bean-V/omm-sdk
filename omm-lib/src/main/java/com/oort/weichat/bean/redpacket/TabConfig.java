package com.oort.weichat.bean.redpacket;

import java.util.List;

public class TabConfig {
    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    // 第一层 data
    public static class DataBean {
        private String id;
        private int mod_type;
        private String create_uuid;
        private String username;
        private String name;
        private InnerData data;   // 注意：这里再嵌套一个 data

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getMod_type() {
            return mod_type;
        }

        public void setMod_type(int mod_type) {
            this.mod_type = mod_type;
        }

        public String getCreate_uuid() {
            return create_uuid;
        }

        public void setCreate_uuid(String create_uuid) {
            this.create_uuid = create_uuid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InnerData getData() {
            return data;
        }

        public void setData(InnerData data) {
            this.data = data;
        }

        // 第二层 data
        public static class InnerData {
            private AppSetting appSetting;

            public AppSetting getAppSetting() {
                return appSetting;
            }

            public void setAppSetting(AppSetting appSetting) {
                this.appSetting = appSetting;
            }

            public static class AppSetting {
                private BasicConfig basicConfig;
                private List<BottomConfig> bottomConfig;

                public BasicConfig getBasicConfig() {
                    return basicConfig;
                }

                public void setBasicConfig(BasicConfig basicConfig) {
                    this.basicConfig = basicConfig;
                }

                public List<BottomConfig> getBottomConfig() {
                    return bottomConfig;
                }

                public void setBottomConfig(List<BottomConfig> bottomConfig) {
                    this.bottomConfig = bottomConfig;
                }

                public static class BasicConfig {
                    private String appBanner;
                    private String appLaunch;
                    private String appLogo;
                    private String appName;

                    public String getAppBanner() {
                        return appBanner;
                    }

                    public void setAppBanner(String appBanner) {
                        this.appBanner = appBanner;
                    }

                    public String getAppLaunch() {
                        return appLaunch;
                    }

                    public void setAppLaunch(String appLaunch) {
                        this.appLaunch = appLaunch;
                    }

                    public String getAppLogo() {
                        return appLogo;
                    }

                    public void setAppLogo(String appLogo) {
                        this.appLogo = appLogo;
                    }

                    public String getAppName() {
                        return appName;
                    }

                    public void setAppName(String appName) {
                        this.appName = appName;
                    }
                }

                public static class BottomConfig {
                    private String icon;

                    public String getHighlightIcon() {
                        return highlightIcon;
                    }

                    public void setHighlightIcon(String highlightIcon) {
                        this.highlightIcon = highlightIcon;
                    }

                    private String highlightIcon;
                    private boolean isDefine;
                    private String label;
                    private RelateApp relateApp;

                    public String getIcon() {
                        return icon;
                    }

                    public void setIcon(String icon) {
                        this.icon = icon;
                    }

                    public boolean isDefine() {
                        return isDefine;
                    }

                    public void setDefine(boolean define) {
                        isDefine = define;
                    }

                    public String getLabel() {
                        return label;
                    }

                    public void setLabel(String label) {
                        this.label = label;
                    }

                    public RelateApp getRelateApp() {
                        return relateApp;
                    }

                    public void setRelateApp(RelateApp relateApp) {
                        this.relateApp = relateApp;
                    }

                    public static class RelateApp {
                        private String app_id;
                        private String applabel;
                        private String apppackage;
                        private String apk_url;

                        public String getApp_id() {
                            return app_id;
                        }

                        public void setApp_id(String app_id) {
                            this.app_id = app_id;
                        }

                        public String getApplabel() {
                            return applabel;
                        }

                        public void setApplabel(String applabel) {
                            this.applabel = applabel;
                        }

                        public String getApppackage() {
                            return apppackage;
                        }

                        public void setApppackage(String apppackage) {
                            this.apppackage = apppackage;
                        }

                        public String getApk_url() {
                            return apk_url;
                        }

                        public void setApk_url(String apk_url) {
                            this.apk_url = apk_url;
                        }
                    }
                }
            }
        }
    }

}
