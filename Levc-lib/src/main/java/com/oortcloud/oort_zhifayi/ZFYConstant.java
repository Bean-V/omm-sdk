package com.oortcloud.oort_zhifayi;


import com.oortcloud.basemodule.BuildConfig;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

public class ZFYConstant {


    public static boolean IsDebbug = BuildConfig.DEBUG;


    public static final String KEY_SKIN_NAME = "KEY_SKIN_NAME";

    //登录信息存储
    public static String LOGIN_RESPONSE = "LOGIN_RESPONSE";
    //应用存储地址


//    public static String BASE_IP = "http://oort.oortcloudsmart.com";
//    //public static String BASE_IP = "http://workup.szkingdom.global";
//
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":31110/";
//
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31100/";
//    //服务网关地址
//
//
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31105/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31120/";
//    public static  int IM_XM_PP_PORT = 31101; //xmpp端口
//    public static  String IM_API_KEY = "DemotstTo2020";



//    public static String BASE_IP = "http://oort.oortcloudsmart.com";
//    public static  String BASE_IP = "http://mulit-env.oort.oortcloudsmart.com";
//
    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":31610/";
//    public static  String BASE_URL = Constant.BASE_IP + ":21410";
    public static  String A_PAAS_LOCATION = "/bus/apaas-location-service";
    public static  String TASK_V2 =   "/task/v2";
    public static String BASE_URL_A_PAAS_V1 = Constant.BASE_URL + A_PAAS_LOCATION + TASK_V2;
    //"/bus/apaas-location-service/location/v1
    public static  String LOCATION_V1 = "/location/v1";
    public static String BASE_URL_LOCATION_V1 = Constant.BASE_URL + A_PAAS_LOCATION + LOCATION_V1;

//    public static  String BASE_URL = "https://www.myoumuamua.com/";//http://oort.oortcloudsmart.com:21310/";//"http://192.168.60.75:32610/";
    //IM配置
    public static  String IM_API_BASE =  Constant.BASE_IP + ":21300/";
    //服务网关地址

    //内网没有域名服务时劫持域名指定ip，外网不用
    public static boolean IS_USE_DOMAIN = false;
    //域名劫持时SSL证书使用的域名
    public static String SSL_DOMAIN = "video.myoumuamua.com";//"oort.oortcloudsmart.com";
    //域名劫持时ssl域名对应的ip
    public static byte[] DOMAIN_ADDR = {(byte)113,(byte)116,(byte)135,(byte)190};


    public static final String POLICE_LOCATION = "oort/oortcloud-memsql-tools/trace_track/v1/reporttrack";

    //用户登录账号密码 加密公钥
    public static final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwQ9LMVuWA26f+pc4cyiZbZRY+XzJ6B6sC9ZHRU5x3C8g5Cu1MlpZ3v8baD8r+aZOE9t5NnmSLFqcVIlO3DY+bX03188m59zZqWmLhzdKPWJ2ibH4AjCHH0OlJFUIew3qzXOdZw4nk+nBmrRV7XeU7a/K4SYI7bKQg2hn4N9giKdSztvZcjO21ZS2/JiQQfSh7vZDWMsU9RH7MGAkaSkmcOmM4TVA5ponhinnpcf2cJBs94hJgFjC3JagnzqpD8ZPpPG37Ozjz3sG1iOVtC3SSh7Ejxxm75N0wjSpcVmzIitUqOrEiVyo8XoALsGUW24oLBW+LLUGZ/TxwRgHiFSLe5gTaTM+wZNZFK31lyJiZv1HYSRMzmN5SgSp5kh/8pRW42T8mPcSx6NrvZXN3BZKdjkOJ4/eEAY8PlgwKs3vF0DQt5TPrnJIuOo5RIhtbojofe6tFCukr2Fv3k6lPFTbqWRVyK0SVYRAk+V+VLEyj5bouX1gCDvh2evP4+/4/ZGHty04gGHlWWClcjo7iUP9EeWo1IftyuD4fPtFl8sPm/By+/vz3/meavzWEjxL28kOSpTJWIVC2UeVgjMS/0e0s5DllJI3jtAG6AhQTNYrQTtJbtc7SFY6SYptZ+LLZ8kn2pAA1bZUOUCuCnDICLiglEFpPrSPQlWJyzN3WvU4bU0CAwEAAQ==";




    public static String KEY_DEVICE_ID = "KEY_DEVICE_ID";


    public static String testToken = IMUserInfoUtil.getInstance().getToken();

}
