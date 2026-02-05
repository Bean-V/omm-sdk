package com.oort.weichat.fragment.vs.http;

import com.oortcloud.basemodule.constant.Constant;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/25-4:04.
 * Version 1.0
 * Description:指挥调度 模块API常量
 */
public final class ApiConstants {

    private ApiConstants() {
        // 防止实例化
    }

    // 基础URL
    public static final String BASE_LOCATION_URL = Constant.BASE_URL; // 请替换为实际的base URL

    // Base模块
    public static class Base {

        public static final String OORT = "oort";
        public static final String SSO_SERVICE = "oortcloud-sso";


        private Base() {
        }
    }
    // 业务模块
    public static class Module {
        private Module() {
        }
        //标签
        public static final String TAG = "tag";
    }

    // API 版本
    public static class APIVersion {
        private APIVersion() {
        }

        public static final String API_VERSION_V1 = "v1";
    }


    // 端点路径
    public static class Endpoint {
        private Endpoint() {
        }

        // 获取标签列表
        public static final String TAG_LIST = "dispatchTagList";

        //获取标签关联的用户
        public static final String TAG_USER_LIST = "dispatchTagUserList";

    }

    // 完整的API URL常量
    public static class Url {
        private Url() {
        }
        private static final String BASE_TAG_URL = BASE_LOCATION_URL + "/" +
                Base.OORT + "/" +
                Base.SSO_SERVICE + "/" +
                Module.TAG + "/" +
                APIVersion.API_VERSION_V1;
        // 获取标签列表
        public static final String TAG_LIST = BASE_TAG_URL + "/" +
                Endpoint.TAG_LIST;

        //获取标签关联的用户
        public static final String TAG_USER_LIST = BASE_TAG_URL + "/" +
                Endpoint.TAG_USER_LIST;


    }
}
