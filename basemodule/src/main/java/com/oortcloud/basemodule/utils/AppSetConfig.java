package com.oortcloud.basemodule.utils;

import com.alibaba.fastjson.JSON;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AppSetConfig implements Serializable {



    public interface ConfigCallback{
        public void callback(ConfigData.SolutionBean solutionBean);
    }



    public void addCallback(ConfigCallback callback) {
        if(!callbacks.contains(callback) && callback != null){
            callbacks.add(callback);


        }
        if(configData != null){
            if (isBasicConfigAvailable()) {
                callback.callback(getCuurrentSolutionBean());
            }
        }

    }
    private boolean isBasicConfigAvailable() {
        ConfigData.SolutionBean solutionBean = getCuurrentSolutionBean();
        if (solutionBean == null) {
            return false;
        }
        ConfigData.SolutionBean.AppSettingBean appSetting = solutionBean.getAppSetting();
        if (appSetting == null) {
            return false;
        }
        return appSetting.getBasicConfig() != null;
    }



    private List<ConfigCallback> callbacks = new ArrayList<>();
    private static AppSetConfig config;
    /**
     * solution : [{"appSetting":{"basicConfig":{"appBanner":"http://oort.oortcloudsmart.com:31110/oort/oortwj1/group1/default/20230913/18/08/4/icon_banner.png","appLaunch":"","appLogo":"","appName":"OA"},"bottomConfig":"","otherConfig":"","waterPrintConfig":""},"createTime":1672502400000,"creator":"Platform Default","updateTime":1672502400000}]
     * useIndex : 0
     */

    private ConfigData configData;

    private ConfigData.SolutionBean getCuurrentSolutionBean(){
        if(configData != null && configData.getSolution() != null){
            return configData.getSolution().get(configData.useIndex);
        }
        return null;
    }


    public static AppSetConfig getInstance(){
        if(config == null){
            config = new AppSetConfig();
            config.initConfig();
        }
        return config;
    }


    public void initConfig(){

        String  record3 =  FastSharedPreferences.get("httpRes").getString("moblie_config","");

        if(record3.length() > 0) {
            ConfigData res = JSON.parseObject(record3, ConfigData.class);//
            if (res != null) {
                configData = res;

            }
        }
        resquest();

        //HttpRequestParam


    }


    public final OkHttpClient client = new OkHttpClient();

    public void resquest() {
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "oort/oortcloud-sso/frontConf/v1/mobile_config.json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String  record3 =  FastSharedPreferences.get("httpRes").getString("moblie_config","");
                    String s = response.body().string();
                    if(!record3.equals(s)) {


                        ConfigData res = JSON.parseObject(s, ConfigData.class);//
                        if (res != null) {
                            configData = res;

                            FastSharedPreferences.get("httpRes").edit().putString("moblie_config", s).apply();

                            if(callbacks.size() > 0){
                                for(ConfigCallback cb : callbacks){
                                    if(cb != null && getCuurrentSolutionBean() != null){
                                        cb.callback(getCuurrentSolutionBean());
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });

    }



    public static class ConfigData implements Serializable {
        private int useIndex;

        public int getUseIndex() {
            return useIndex;
        }

        public void setUseIndex(int useIndex) {
            this.useIndex = useIndex;
        }

        public List<SolutionBean> getSolution() {
            return solution;
        }

        public void setSolution(List<SolutionBean> solution) {
            this.solution = solution;
        }

        private List<SolutionBean> solution;

        public static class SolutionBean implements Serializable {
            /**
             * appSetting : {"basicConfig":{"appBanner":"http://oort.oortcloudsmart.com:31110/oort/oortwj1/group1/default/20230913/18/08/4/icon_banner.png","appLaunch":"","appLogo":"","appName":"OA"},"bottomConfig":"","otherConfig":"","waterPrintConfig":""}
             * createTime : 1672502400000
             * creator : Platform Default
             * updateTime : 1672502400000
             */

            private AppSettingBean appSetting;
            private long createTime;

            public AppSettingBean getAppSetting() {
                return appSetting;
            }

            public void setAppSetting(AppSettingBean appSetting) {
                this.appSetting = appSetting;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getCreator() {
                return creator;
            }

            public void setCreator(String creator) {
                this.creator = creator;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }

            private String creator;
            private long updateTime;

            public static class AppSettingBean implements Serializable {
                /**
                 * basicConfig : {"appBanner":"http://oort.oortcloudsmart.com:31110/oort/oortwj1/group1/default/20230913/18/08/4/icon_banner.png","appLaunch":"","appLogo":"","appName":"OA"}
                 * bottomConfig :
                 * otherConfig :
                 * waterPrintConfig :
                 */

                private BasicConfigBean basicConfig;
                private List<BottomConfigBean> bottomConfig;

                public BasicConfigBean getBasicConfig() {
                    return basicConfig;
                }
                public void setBasicConfig(BasicConfigBean basicConfig) {
                    this.basicConfig = basicConfig;
                }

                public List<BottomConfigBean> getBottomConfig() {
                    return bottomConfig;
                }

                public void setBottomConfig(List<BottomConfigBean> bottomConfig) {
                    this.bottomConfig = bottomConfig;
                }

                public String getOtherConfig() {
                    return otherConfig;
                }

                public void setOtherConfig(String otherConfig) {
                    this.otherConfig = otherConfig;
                }

                public String getWaterPrintConfig() {
                    return waterPrintConfig;
                }

                public void setWaterPrintConfig(String waterPrintConfig) {
                    this.waterPrintConfig = waterPrintConfig;
                }

                private String otherConfig;
                private String waterPrintConfig;

                public static class BasicConfigBean implements Serializable {
                    /**
                     * appBanner : http://oort.oortcloudsmart.com:31110/oort/oortwj1/group1/default/20230913/18/08/4/icon_banner.png
                     * appLaunch :
                     * appLogo :
                     * appName : OA
                     */

                    private String appBanner;

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

                    private String appLaunch;
                    private String appLogo;
                    private String appName;
                }

            }
        }
    }
}
