package com.oortcloud.basemodule.constant;

import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.oortcloud.basemodule.utils.HttpUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import java.io.Serializable;

public class Constant {

    // 通用开关配置
    public static boolean IsShowTwoLevelPart = true;
    public static boolean IsSingleMsgTab = false;
    public static boolean IsHomeUseAndroid = true;
    public static boolean IsHomeUsePad = false;
    public static boolean IsAddAPPSToHomeWhenUse = false;
    public static boolean NotAddSomeAPPSToHomeWhenUse = true;
    public static boolean IsUseFullScreenWhenDownApp = true;
    public static boolean IsLoginWithoutDepart = true;
    public static boolean isOffLine = false;
    public static boolean IsNotShowOffline = true;
    public static boolean IsShowWaterPrint = false;
    public static boolean GetTDSDKDeviceInfo = false;
    public static boolean IF_CONFIG_APP_SERVICE = true;
    public static boolean IS_USE_DOMAIN = false;
    public static boolean HAVA_NOTE = true;


    public static boolean DEFAULT_MSG_NO_FIRE = true;
    public static boolean ISTABFROMSERVER = true;

    // 存储相关配置
    public static final String KEY_SKIN_NAME = "KEY_SKIN_NAME";
    // 调试的黑历史记录存储sp名字
    public static final String HISTORY_RECORD = "DEBUG_HISTORY";
    // 轻应用配置信息存储sp名字
    public static final String LOCAL_APP_CONFIG = "LOCAL_APP_CONFIG";
    // 登录信息存储
    public static final String LOGIN_RESPONSE = "LOGIN_RESPONSE";
    // 应用存储地址
    public static final String BASE_PATH = "file:///android_asset/";
    // 本地存储根路径
    public static final String LOCAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    // 网络请求相关配置
    public static final String GET_IP = "http://www.myoumuamua.com/";
    public static final String IP_PARAM = "public-api/api/v1/getip?domain=oort.oortcloudsmart.com";

    // 基础服务IP和端口配置
    public static boolean HAVA_VERIFY = false;
    public static  String BASE_IP = "http://oort.oortcloudsmart.com";
    public static  String BASE_URL = BASE_IP + ":21310/";
    public static  String IM_API_BASE = BASE_IP + ":21300/";
    public static  String IM_UPLOAD_URL = BASE_IP + ":21305/";
    public static  String IM_DOWN_URL = BASE_IP + ":21320/";
    public static  int IM_XM_PP_PORT = 21301;
    public static  String IM_API_KEY = "DemotstTo2020";
    public static  String BASE_3CLASSURL = BASE_URL;
    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
    public static  String IM_JIT_SI_URL = "https://183.62.103.20:14443/";
    public static final String PUBLIC_USERID = "10000907";

    public static  String APP_ID = "c5504f7f6f864260ac6a11d1c2fff090";//""e1a36857e77c4e238703a06e0e57e7a0";//c5504f7f6f864260ac6a11d1c2fff090
    public static  String SECRET_KEY = "35c028eade3b40e9b4669df57e2afb0a";//""557d8735b655426cb21a4771b901de61";//35c028eade3b40e9b4669df57e2afb0a
    public static  String OORT_REQUEST_TYPE = "app";

//    public static boolean SXJJDebug = false;
//    public static boolean HAVA_VERIFY = false;
//
//    public static String BASE_IP = SXJJDebug ? "http://20.72.151.66" : "http://20.72.151.66";
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + (SXJJDebug ? ":80/" : ":32610/");
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String BASE_3CLASSURL = BASE_URL;
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
//    public static final String PUBLIC_USERID = "10000907";

//    public static String BASE_IP = "http://oort.oortcloudsmart.com";
//    public static  String BASE_URL = BASE_IP + ":31610/";
//    public static  String IM_API_BASE = BASE_IP + ":31500/";
//    public static String IM_UPLOAD_URL = BASE_IP + ":31505/";
//    public static  String IM_DOWN_URL = BASE_IP + ":31520/";
//    public static  int IM_XM_PP_PORT = 31501; //xmpp端口
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String BASE_3CLASSURL = BASE_URL;
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
//    public static final String PUBLIC_USERID = "10000907";

//    public static boolean HAVA_VERIFY = true;
//    public static String BASE_IP = "http://172.16.20.56";//http://172.16.20.64";//"http://192.168.88.125";//ks
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//    public static  String IM_API_KEY = "gsyjtkey2025";
//    public static  String BASE_3CLASSURL = "http://172.16.20.56:32610/oort/oortcloud-servicebus-proxy/";//http://192.168.10.2
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
//    public static  String IM_JIT_SI_URL = "https://172.16.20.56:9443";
//    public static final String PUBLIC_USERID = "10000270";


//    public static String BASE_IP = "http://192.168.10.2";//甘肃三类网
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//    public static  String IM_API_KEY = "gsyjtkey2025";
//    public static  String BASE_3CLASSURL = BASE_IP + ":32610/oort/oortcloud-servicebus-proxy/";
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
//    public static final String PUBLIC_USERID = "10000270";

//    public static String BASE_IP = "http://oort.oortcloudsmart.com";
//    //public static String BASE_IP = "http://workup.szkingdom.global";
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":31110/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31100/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31105/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31120/";
//    public static  int IM_XM_PP_PORT = 31101; //xmpp端口
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";
//    public static  String BASE_3CLASSURL = BASE_URL;
//      public static final String PUBLIC_USERID = "10000270";


    // 任务中心配置
    //public static final String PUBLIC_USERID = "10000270";
    public static final int PUBLIC_NUM = 3;

    // 域名劫持相关配置
    public static final String SSL_DOMAIN = "video.myoumuamua.com";
    public static final byte[] DOMAIN_ADDR = {(byte) 113, (byte) 116, (byte) 135, (byte) 190};

    // IM服务相关配置
    public static final String IM_CONFIG_URL = IM_API_BASE + "config";
    //public static  String IM_JIT_SI_URL = "https://116.63.163.33:9443/";//"https://" + SSL_DOMAIN + ":1443/";
    public static  String IM_LIVE_URL = BASE_IP.replace("http", "rtmp") + ":1935/live/";

    // 视频相关配置
    public static final String VIDEO_URL = "http://map.oort.oortcloudsmart.com:32610/oort/flv/";

    // 用户登录相关接口
    public static final String LOGIN_SERVICE = "oort/oortcloud-sso/ssoim/v1/login";
    public static final String GET_LOGIN_CODE = "oort/oortcloud-sso/ssoim/v2/getLoginCode";
    public static final String NEW_LOGIN = "oort/oortcloud-sso/ssoim/v2/login";
    public static final String AUTOLOGIN_SERVICE = "oort/oortcloud-sso/ssoim/v1/autoLogin";
    public static final String AUTOLOGIN_API = "oort/oortcloud-sso/ssoim/v2/autologin";
    public static final String REQUEST_TYPE = "zuul";

    // 警力上报和轨迹信息相关接口
    public static final String POLICE_REPORT = "oort/oortcloud-memsql-tools/trace_track/v1/editvideoterminal";
    public static final String POLICE_LOCATION = "oort/oortcloud-memsql-tools/trace_track/v1/reporttrack";

    // 文件上传相关接口
    public static final String UPLOAD_FILE = "oort/oortcloud-fastdfsservice/fastdfs/v1/uploadFile";

    // 首页地址
    public static final String HOME_PAGE = "http://oort.oortcloudsmart.com:32610/oort/oortcloud-frontservice/aomomo_home/index.html#/";

    // 用户登出和修改密码相关接口
    public static final String LOGOUT_SERVICE = "oort/oortcloud-sso/ssoim/v1/logout";
    public static final String RESET_PASSWORD = "oort/oortcloud-sso/ssoim/v1/resetPassword";
    public static final String RESET_PASSWORD_V2 = "oort/oortcloud-sso/ssoim/v2/resetPassword";

    // 获取用户详细信息接口
    public static final String GET_USER_INFO = "oort/oortcloud-sso/sso/v1/getUserInfo";

    // 用户登录账号密码加密公钥
    public static final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwQ9LMVuWA26f+pc4cyiZbZRY+XzJ6B6sC9ZHRU5x3C8g5Cu1MlpZ3v8baD8r+aZOE9t5NnmSLFqcVIlO3DY+bX03188m59zZqWmLhzdKPWJ2ibH4AjCHH0OlJFUIew3qzXOdZw4nk+nBmrRV7XeU7a/K4SYI7bKQg2hn4N9giKdSztvZcjO21ZS2/JiQQfSh7vZDWMsU9RH7MGAkaSkmcOmM4TVA5ponhinnpcf2cJBs94hJgFjC3JagnzqpD8ZPpPG37Ozjz3sG1iOVtC3SSh7Ejxxm75N0wjSpcVmzIitUqOrEiVyo8XoALsGUW24oLBW+LLUGZ/TxwRgHiFSLe5gTaTM+wZNZFK31lyJiZv1HYSRMzmN5SgSp5kh/8pRW42T8mPcSx6NrvZXN3BZKdjkOJ4/eEAY8PlgwKs3vF0DQt5TPrnJIuOo5RIhtbojofe6tFCukr2Fv3k6lPFTbqWRVyK0SVYRAk+V+VLEyj5bouX1gCDvh2evP4+/4/ZGHty04gGHlWWClcjo7iUP9EeWo1IftyuD4fPtFl8sPm/By+/vz3/meavzWEjxL28kOSpTJWIVC2UeVgjMS/0e0s5DllJI3jtAG6AhQTNYrQTtJbtc7SFY6SYptZ+LLZ8kn2pAA1bZUOUCuCnDICLiglEFpPrSPQlWJyzN3WvU4bU0CAwEAAQ==";

    // 验证登录相关接口
    public static final String LOGIN_CAPTCHA = "oort/oortcloud-sso/sso/v1/login";
    public static final String CODE_IMAGE = BASE_URL + "oort/oortcloud-sso/captcha/v1/";
    public static final String GET_CAPTCHA = "oort/oortcloud-sso/sso/v1/getCaptcha";
    public static final String SEND_SMS_CODE = "oort/oortcloud-sso/sso/v1/sendsmscode";
    public static final String REGISTER = "oort/oortcloud-sso/sso/v1/register";
    public static final String SET_USER_INFO = "oort/oortcloud-sso/sso/v1/setUserInfo";

    // 会议相关接口
    public static final String MEETING_API = "oort/oortcloud-meeting";
    public static final String MEETING_DETAIL = MEETING_API + "/v1/detail";
    public static final String MEETING_OPEN = MEETING_API + "/v1/open";
    public static final String MEETING_LIST = MEETING_API + "/v1/list";
    public static final String MEETING_EDIT = MEETING_API + "/v1/edit";
    public static final String MEETING_ADD = MEETING_API + "/v1/add";
    public static final String MEETING_DELETE = MEETING_API + "/v1/delete";
    public static final String MEETING_UPDATE_NUMBAR = MEETING_API + "/v1/updatenumber";

    // app和api使用上报相关接口
    public static final String APP_USE_REPORT = "oort/oortcloud-sso/rank/v1/app.use";
    public static final String API_USE_REPORT = "oort/oortcloud-log-manage/api/v1/reportLog";

    // 部门和用户列表相关接口
    public static final String SZJCY_LIST = "oort/oortcloud-sso/sso/v1/szjcyList";
    public static final String ORGANIZATION = "oort/oortcloud-sso/sso/v1/getdeptuser";

    // 标签相关接口
    public static final String FRIENDLAB_LIST = "oort/oortcloud-sso/address/v1/tagList";
    public static final String FRIENDLAB_ADD = "oort/oortcloud-sso/address/v1/tagSet";
    public static final String FRIENDLAB_DELETE = "oort/oortcloud-sso/address/v1/tagDel";
    public static final String FRIENDLAB_UPDATE = "oort/oortcloud-sso/address/v1/tagSet";
    public static final String FRIENDLAB_UPDATEGROUPUSERLIST = "oort/oortcloud-sso/address/v1/tagUserList";
    public static final String FRIENDLAB_UPDATEFRIEND = "oort/oortcloud-sso/friendGroup/updateFriend";

    public static final String TAG_LIST = "oort/oortcloud-sso/tag/v1/tagList";
    public static final String TAG_ADD = "oort/oortcloud-sso/tag/v1/tagSet";
    public static final String TAG_DELETE = "oort/oortcloud-sso/tag/v1/tagDel";
    public static final String TAG_USERADD = "oort/oortcloud-sso/tag/v1/tagUserAdd";
    public static final String TAG_USERDEL = "oort/oortcloud-sso/tag/v1/tagUserDel";
    public static final String TAG_USERLIST = "oort/oortcloud-sso/tag/v1/tagUserList";

    public static final String ADDRESS_TAG_LIST = "oort/oortcloud-sso/address/v1/tagList";
    public static final String ADDRESS_TAG_ADD = "oort/oortcloud-sso/address/v1/tagSet";
    public static final String ADDRESS_TAG_DELETE = "oort/oortcloud-sso/address/v1/tagDel";
    public static final String ADDRESS_TAG_USERADD = "oort/oortcloud-sso/address/v1/tagUserAdd";
    public static final String ADDRESS_TAG_USERDEL = "oort/oortcloud-sso/address/v1/tagUserDel";
    public static final String ADDRESS_TAG_USERLIST = "oort/oortcloud-sso/address/v1/tagUserList";

    public static final String USED_TAG_USERCLEAR = "oort/oortcloud-sso/tag/v1/usedDel";
    public static final String USED_TAG_USERGET = "oort/oortcloud-sso/tag/v1/usedGet";
    public static final String USED_TAG_USERSET = "oort/oortcloud-sso/tag/v1/usedSet";

    // 用户状态相关接口
    public static final String STE_MY_STATUS = BASE_URL + "oort/oortcloud-sso/sso/v1/setMyStatus";

    // 应用和收藏相关接口
    public static final String GETALLAPP = "oort/oortcloud-admin-platform/client/getallapp";
    public static final String MYCollectSave = "oort/oortcloud-log-manage/api/v1/myCollectSave";
    public static final String MYCollectList = "oort/oortcloud-log-manage/api/v1/myCollectList";
    public static final String MYCollectDel = "oort/oortcloud-log-manage/api/v1/myCollectDel";
    public static final String USER_LIST = "oort/oortcloud-sso/sso/v1/userlist";

    // 设备ID相关配置
    public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";

    // 日志上报相关接口
    public static final String Aks_Log_Report_Setting = BASE_URL + "oort/oortcloud-log-manage/anshen/v1/getSetting";
    public static final String Aks_Log_Report = BASE_URL + "oort/oortcloud-log-manage/anshen/v1/report";


    public static final String BOTTOM_TAB_CONFIG = BASE_URL + "oort/oortcloud-sso/frontConf/v1/front_setting_get_mysetting";




//    public static String BASE_IP = "http://oort.oortcloudsmart.com";
//    //public static String BASE_IP = "http://OA.szkingdom.global";
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
//
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":31610/";
//
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31500/";
//    //服务网关地址
//
//
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31505/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31520/";
//    public static  int IM_XM_PP_PORT = 31501; //xmpp端口
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String BASE_3CLASSURL = BASE_IP + ":32610/oort/oortcloud-servicebus-proxy/";

//    public static String BASE_IP = "http://20.74.1.30";
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//    public static  String IM_API_KEY = "TYJG43To2023";


//    public static String BASE_IP = "http://192.168.213.100";//"http://192.168.88.125";//ks
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//
//    public static  String IM_API_KEY = "ksKey2025";
//    public static  String BASE_3CLASSURL = BASE_IP + ":32610/oort/oortcloud-servicebus-proxy/";
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";


//    public static String BASE_IP = "http://20.137.176.11";//"http://192.168.88.125";//克州
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String BASE_3CLASSURL = BASE_IP + ":32610/oort/oortcloud-servicebus-proxy/";
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";

//    public static String BASE_IP = "http://192.168.91.230";//"http://192.168.88.125";//肖志强本地
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":31605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":31620/";
//
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//
//    public static  String IM_API_KEY = "DemotstTo2020";
//    public static  String BASE_3CLASSURL = BASE_IP + ":32610/oort/oortcloud-servicebus-proxy/";
//    public static  String IM_XM_PP_DO_MAIN = "im.oortcloudsmart.com";


    //http://192.168.88.125:31310

    ///104-73-131-46

//    public static String BASE_IP = "http://104.73.131.46";
//    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":32610/";
//    //IM配置
//    public static  String IM_API_BASE = BASE_IP + ":31600/";
//    //服务网关地址
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":32605/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":32620/";
//
//    public static  int IM_XM_PP_PORT = 31601; //xmpp端口
//
//    public static  String IM_API_KEY = "DemotstTo2024";




//    public static String BASE_IP ="http://192.168.120.30";
//    //    //服务网关地址 + 端口
//    public static  String BASE_URL = BASE_IP + ":16026/";
//    public static  String IM_API_KEY = "DemotstTo2024";//"VIsidivSJy2020";
//    //xmpp端口
//    public static  int IM_XM_PP_PORT = 16027; //xmpp端口
//
//    //文件上传配置
//    public static String IM_UPLOAD_URL = BASE_IP + ":16025/";
//    //文件头像下载服务
//    public static  String IM_DOWN_URL = BASE_IP + ":16024/";
//    public static  String IM_API_BASE = BASE_IP + ":16023/";






    /**
     * currentTime : 1700734905455
     * data : {"XMPPDomain":"oort.oortcloudsmart.com","XMPPHost":"workup.szkingdom.global","XMPPTimeout":180,"address":"CN","androidVersion":0,"apiUrl":"http://workup.szkingdom.global:31110/","appleId":"","chatRecordTimeOut":30,"companyName":"","copyright":"","displayRedPacket":0,"distance":20,"downloadAvatarUrl":"http://workup.szkingdom.global:31120/","downloadUrl":"http://workup.szkingdom.global:31120/","enableAliPay":1,"enableMpModule":1,"enableOpenModule":1,"enablePayModule":1,"enableWxPay":1,"fileValidTime":-1,"hideSearchByFriends":1,"iosVersion":0,"ipAddress":"172.20.98.192","isCommonCreateGroup":0,"isCommonFindFriends":0,"isOpenAPNSorJPUSH":0,"isOpenAuthSwitch":0,"isOpenCloudWallet":0,"isOpenCluster":0,"isOpenGoogleFCM":0,"isOpenManualPay":0,"isOpenOnlineStatus":0,"isOpenPositionService":0,"isOpenReadReceipt":1,"isOpenReceipt":1,"isOpenRegister":1,"isOpenRoomSearch":0,"isOpenSMSCode":0,"isOpenSecureChat":0,"jitsiServer":"https://oort.oortcloudsmart.com:1443/","liveUrl":"rtmp://workup.szkingdom.global:1935/live/","macVersion":0,"manualPaywithdrawFee":0.006,"maxRedpacktAmount":10,"maxRedpacktNumber":10,"nicknameSearchUser":2,"pcVersion":0,"popularAPP":"{\"lifeCircle\":1,\"videoMeeting\":1,\"liveVideo\":1,\"shortVideo\":1,\"peopleNearby\":1}","privacyPolicyPrefix":"","regeditPhoneOrName":1,"registerInviteCode":0,"showContactsUser":1,"uploadUrl":"http://workup.szkingdom.global:31105/","videoLength":600,"website":"","xMPPDomain":"oort.oortcloudsmart.com","xMPPHost":"workup.szkingdom.global","xMPPTimeout":180,"xmppPingTime":72}
     * resultCode : 1
     */

    private long currentTime;
    private DataBean data;

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

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    private int resultCode;


    public static void updateConfig(){


        String  record =  FastSharedPreferences.get("Config").getString("config","");

        if(record.length() > 0){
            Constant config = JSON.parseObject(record, Constant.class);
            if(config.getResultCode() == 1){
                IM_XM_PP_DO_MAIN = config.getData().getXMPPDomain();
                IM_DOWN_URL = config.getData().getDownloadUrl();
                //BASE_URL = config.getData().getApiUrl();
                //服务网关地址
                //文件上传配置
                IM_UPLOAD_URL = config.getData().getUploadUrl();
                IM_LIVE_URL = config.getData().getLiveUrl();
                IM_JIT_SI_URL = config.getData().getJitsiServer();
            }
        }else{
            //cv_apps.setVisibility(View.GONE);
        }
        HttpUtil.doGetAsyn(IM_API_BASE + "config", new HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String requst) {

                Constant config = JSON.parseObject(requst, Constant.class);
                if(config.getResultCode() == 1){
                    FastSharedPreferences.get("Config").edit().putString("config",requst).apply();

                    if(record.equals(requst)){
                        return;
                    }

                    IM_XM_PP_DO_MAIN = config.getData().getXMPPDomain();
                    IM_DOWN_URL = config.getData().getDownloadUrl();
                    //BASE_URL = config.getData().getApiUrl();
                    //服务网关地址
                    //文件上传配置
                    IM_UPLOAD_URL = config.getData().getUploadUrl();
                    IM_LIVE_URL = config.getData().getLiveUrl();
                    IM_JIT_SI_URL = config.getData().getJitsiServer();
                }
            }
        });
    }


    public static class DataBean implements Serializable {
        /**
         * XMPPDomain : oort.oortcloudsmart.com
         * XMPPHost : workup.szkingdom.global
         * XMPPTimeout : 180
         * address : CN
         * androidVersion : 0
         * apiUrl : http://workup.szkingdom.global:31110/
         * appleId :
         * chatRecordTimeOut : 30
         * companyName :
         * copyright :
         * displayRedPacket : 0
         * distance : 20
         * downloadAvatarUrl : http://workup.szkingdom.global:31120/
         * downloadUrl : http://workup.szkingdom.global:31120/
         * enableAliPay : 1
         * enableMpModule : 1
         * enableOpenModule : 1
         * enablePayModule : 1
         * enableWxPay : 1
         * fileValidTime : -1
         * hideSearchByFriends : 1
         * iosVersion : 0
         * ipAddress : 172.20.98.192
         * isCommonCreateGroup : 0
         * isCommonFindFriends : 0
         * isOpenAPNSorJPUSH : 0
         * isOpenAuthSwitch : 0
         * isOpenCloudWallet : 0
         * isOpenCluster : 0
         * isOpenGoogleFCM : 0
         * isOpenManualPay : 0
         * isOpenOnlineStatus : 0
         * isOpenPositionService : 0
         * isOpenReadReceipt : 1
         * isOpenReceipt : 1
         * isOpenRegister : 1
         * isOpenRoomSearch : 0
         * isOpenSMSCode : 0
         * isOpenSecureChat : 0
         * jitsiServer : https://oort.oortcloudsmart.com:1443/
         * liveUrl : rtmp://workup.szkingdom.global:1935/live/
         * macVersion : 0
         * manualPaywithdrawFee : 0.006
         * maxRedpacktAmount : 10
         * maxRedpacktNumber : 10
         * nicknameSearchUser : 2
         * pcVersion : 0
         * popularAPP : {"lifeCircle":1,"videoMeeting":1,"liveVideo":1,"shortVideo":1,"peopleNearby":1}
         * privacyPolicyPrefix :
         * regeditPhoneOrName : 1
         * registerInviteCode : 0
         * showContactsUser : 1
         * uploadUrl : http://workup.szkingdom.global:31105/
         * videoLength : 600
         * website :
         * xMPPDomain : oort.oortcloudsmart.com
         * xMPPHost : workup.szkingdom.global
         * xMPPTimeout : 180
         * xmppPingTime : 72
         */

        private String XMPPDomain;
        private String XMPPHost;
        private int XMPPTimeout;
        private String address;
        private int androidVersion;
        private String apiUrl;
        private String appleId;
        private int chatRecordTimeOut;
        private String companyName;
        private String copyright;
        private int displayRedPacket;
        private int distance;
        private String downloadAvatarUrl;
        private String downloadUrl;
        private int enableAliPay;
        private int enableMpModule;
        private int enableOpenModule;
        private int enablePayModule;
        private int enableWxPay;
        private int fileValidTime;
        private int hideSearchByFriends;
        private int iosVersion;
        private String ipAddress;
        private int isCommonCreateGroup;
        private int isCommonFindFriends;
        private int isOpenAPNSorJPUSH;
        private int isOpenAuthSwitch;
        private int isOpenCloudWallet;
        private int isOpenCluster;
        private int isOpenGoogleFCM;
        private int isOpenManualPay;
        private int isOpenOnlineStatus;
        private int isOpenPositionService;
        private int isOpenReadReceipt;
        private int isOpenReceipt;
        private int isOpenRegister;
        private int isOpenRoomSearch;
        private int isOpenSMSCode;
        private int isOpenSecureChat;
        private String jitsiServer;
        private String liveUrl;
        private int macVersion;
        private double manualPaywithdrawFee;
        private int maxRedpacktAmount;
        private int maxRedpacktNumber;
        private int nicknameSearchUser;
        private int pcVersion;
        private String popularAPP;
        private String privacyPolicyPrefix;
        private int regeditPhoneOrName;
        private int registerInviteCode;
        private int showContactsUser;
        private String uploadUrl;
        private int videoLength;
        private String website;

        public String getXMPPDomain() {
            return XMPPDomain;
        }

        public void setXMPPDomain(String XMPPDomain) {
            this.XMPPDomain = XMPPDomain;
        }

        public String getXMPPHost() {
            return XMPPHost;
        }

        public void setXMPPHost(String XMPPHost) {
            this.XMPPHost = XMPPHost;
        }

        public int getXMPPTimeout() {
            return XMPPTimeout;
        }

        public void setXMPPTimeout(int XMPPTimeout) {
            this.XMPPTimeout = XMPPTimeout;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getAndroidVersion() {
            return androidVersion;
        }

        public void setAndroidVersion(int androidVersion) {
            this.androidVersion = androidVersion;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getAppleId() {
            return appleId;
        }

        public void setAppleId(String appleId) {
            this.appleId = appleId;
        }

        public int getChatRecordTimeOut() {
            return chatRecordTimeOut;
        }

        public void setChatRecordTimeOut(int chatRecordTimeOut) {
            this.chatRecordTimeOut = chatRecordTimeOut;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public int getDisplayRedPacket() {
            return displayRedPacket;
        }

        public void setDisplayRedPacket(int displayRedPacket) {
            this.displayRedPacket = displayRedPacket;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getDownloadAvatarUrl() {
            return downloadAvatarUrl;
        }

        public void setDownloadAvatarUrl(String downloadAvatarUrl) {
            this.downloadAvatarUrl = downloadAvatarUrl;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public int getEnableAliPay() {
            return enableAliPay;
        }

        public void setEnableAliPay(int enableAliPay) {
            this.enableAliPay = enableAliPay;
        }

        public int getEnableMpModule() {
            return enableMpModule;
        }

        public void setEnableMpModule(int enableMpModule) {
            this.enableMpModule = enableMpModule;
        }

        public int getEnableOpenModule() {
            return enableOpenModule;
        }

        public void setEnableOpenModule(int enableOpenModule) {
            this.enableOpenModule = enableOpenModule;
        }

        public int getEnablePayModule() {
            return enablePayModule;
        }

        public void setEnablePayModule(int enablePayModule) {
            this.enablePayModule = enablePayModule;
        }

        public int getEnableWxPay() {
            return enableWxPay;
        }

        public void setEnableWxPay(int enableWxPay) {
            this.enableWxPay = enableWxPay;
        }

        public int getFileValidTime() {
            return fileValidTime;
        }

        public void setFileValidTime(int fileValidTime) {
            this.fileValidTime = fileValidTime;
        }

        public int getHideSearchByFriends() {
            return hideSearchByFriends;
        }

        public void setHideSearchByFriends(int hideSearchByFriends) {
            this.hideSearchByFriends = hideSearchByFriends;
        }

        public int getIosVersion() {
            return iosVersion;
        }

        public void setIosVersion(int iosVersion) {
            this.iosVersion = iosVersion;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public int getIsCommonCreateGroup() {
            return isCommonCreateGroup;
        }

        public void setIsCommonCreateGroup(int isCommonCreateGroup) {
            this.isCommonCreateGroup = isCommonCreateGroup;
        }

        public int getIsCommonFindFriends() {
            return isCommonFindFriends;
        }

        public void setIsCommonFindFriends(int isCommonFindFriends) {
            this.isCommonFindFriends = isCommonFindFriends;
        }

        public int getIsOpenAPNSorJPUSH() {
            return isOpenAPNSorJPUSH;
        }

        public void setIsOpenAPNSorJPUSH(int isOpenAPNSorJPUSH) {
            this.isOpenAPNSorJPUSH = isOpenAPNSorJPUSH;
        }

        public int getIsOpenAuthSwitch() {
            return isOpenAuthSwitch;
        }

        public void setIsOpenAuthSwitch(int isOpenAuthSwitch) {
            this.isOpenAuthSwitch = isOpenAuthSwitch;
        }

        public int getIsOpenCloudWallet() {
            return isOpenCloudWallet;
        }

        public void setIsOpenCloudWallet(int isOpenCloudWallet) {
            this.isOpenCloudWallet = isOpenCloudWallet;
        }

        public int getIsOpenCluster() {
            return isOpenCluster;
        }

        public void setIsOpenCluster(int isOpenCluster) {
            this.isOpenCluster = isOpenCluster;
        }

        public int getIsOpenGoogleFCM() {
            return isOpenGoogleFCM;
        }

        public void setIsOpenGoogleFCM(int isOpenGoogleFCM) {
            this.isOpenGoogleFCM = isOpenGoogleFCM;
        }

        public int getIsOpenManualPay() {
            return isOpenManualPay;
        }

        public void setIsOpenManualPay(int isOpenManualPay) {
            this.isOpenManualPay = isOpenManualPay;
        }

        public int getIsOpenOnlineStatus() {
            return isOpenOnlineStatus;
        }

        public void setIsOpenOnlineStatus(int isOpenOnlineStatus) {
            this.isOpenOnlineStatus = isOpenOnlineStatus;
        }

        public int getIsOpenPositionService() {
            return isOpenPositionService;
        }

        public void setIsOpenPositionService(int isOpenPositionService) {
            this.isOpenPositionService = isOpenPositionService;
        }

        public int getIsOpenReadReceipt() {
            return isOpenReadReceipt;
        }

        public void setIsOpenReadReceipt(int isOpenReadReceipt) {
            this.isOpenReadReceipt = isOpenReadReceipt;
        }

        public int getIsOpenReceipt() {
            return isOpenReceipt;
        }

        public void setIsOpenReceipt(int isOpenReceipt) {
            this.isOpenReceipt = isOpenReceipt;
        }

        public int getIsOpenRegister() {
            return isOpenRegister;
        }

        public void setIsOpenRegister(int isOpenRegister) {
            this.isOpenRegister = isOpenRegister;
        }

        public int getIsOpenRoomSearch() {
            return isOpenRoomSearch;
        }

        public void setIsOpenRoomSearch(int isOpenRoomSearch) {
            this.isOpenRoomSearch = isOpenRoomSearch;
        }

        public int getIsOpenSMSCode() {
            return isOpenSMSCode;
        }

        public void setIsOpenSMSCode(int isOpenSMSCode) {
            this.isOpenSMSCode = isOpenSMSCode;
        }

        public int getIsOpenSecureChat() {
            return isOpenSecureChat;
        }

        public void setIsOpenSecureChat(int isOpenSecureChat) {
            this.isOpenSecureChat = isOpenSecureChat;
        }

        public String getJitsiServer() {
            return jitsiServer;
        }

        public void setJitsiServer(String jitsiServer) {
            this.jitsiServer = jitsiServer;
        }

        public String getLiveUrl() {
            return liveUrl;
        }

        public void setLiveUrl(String liveUrl) {
            this.liveUrl = liveUrl;
        }

        public int getMacVersion() {
            return macVersion;
        }

        public void setMacVersion(int macVersion) {
            this.macVersion = macVersion;
        }

        public double getManualPaywithdrawFee() {
            return manualPaywithdrawFee;
        }

        public void setManualPaywithdrawFee(double manualPaywithdrawFee) {
            this.manualPaywithdrawFee = manualPaywithdrawFee;
        }

        public int getMaxRedpacktAmount() {
            return maxRedpacktAmount;
        }

        public void setMaxRedpacktAmount(int maxRedpacktAmount) {
            this.maxRedpacktAmount = maxRedpacktAmount;
        }

        public int getMaxRedpacktNumber() {
            return maxRedpacktNumber;
        }

        public void setMaxRedpacktNumber(int maxRedpacktNumber) {
            this.maxRedpacktNumber = maxRedpacktNumber;
        }

        public int getNicknameSearchUser() {
            return nicknameSearchUser;
        }

        public void setNicknameSearchUser(int nicknameSearchUser) {
            this.nicknameSearchUser = nicknameSearchUser;
        }

        public int getPcVersion() {
            return pcVersion;
        }

        public void setPcVersion(int pcVersion) {
            this.pcVersion = pcVersion;
        }

        public String getPopularAPP() {
            return popularAPP;
        }

        public void setPopularAPP(String popularAPP) {
            this.popularAPP = popularAPP;
        }

        public String getPrivacyPolicyPrefix() {
            return privacyPolicyPrefix;
        }

        public void setPrivacyPolicyPrefix(String privacyPolicyPrefix) {
            this.privacyPolicyPrefix = privacyPolicyPrefix;
        }

        public int getRegeditPhoneOrName() {
            return regeditPhoneOrName;
        }

        public void setRegeditPhoneOrName(int regeditPhoneOrName) {
            this.regeditPhoneOrName = regeditPhoneOrName;
        }

        public int getRegisterInviteCode() {
            return registerInviteCode;
        }

        public void setRegisterInviteCode(int registerInviteCode) {
            this.registerInviteCode = registerInviteCode;
        }

        public int getShowContactsUser() {
            return showContactsUser;
        }

        public void setShowContactsUser(int showContactsUser) {
            this.showContactsUser = showContactsUser;
        }

        public String getUploadUrl() {
            return uploadUrl;
        }

        public void setUploadUrl(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public int getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(int videoLength) {
            this.videoLength = videoLength;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

//        public String getxMPPDomain() {
//            return xMPPDomain;
//        }
//
//        public void setxMPPDomain(String xMPPDomain) {
//            this.xMPPDomain = xMPPDomain;
//        }
//
//        public String getxMPPHost() {
//            return xMPPHost;
//        }
//
//        public void setxMPPHost(String xMPPHost) {
//            this.xMPPHost = xMPPHost;
//        }
//
//        public int getxMPPTimeout() {
//            return xMPPTimeout;
//        }
//
//        public void setxMPPTimeout(int xMPPTimeout) {
//            this.xMPPTimeout = xMPPTimeout;
//        }
//
//        public int getXmppPingTime() {
//            return xmppPingTime;
//        }
//
//        public void setXmppPingTime(int xmppPingTime) {
//            this.xmppPingTime = xmppPingTime;
//        }
//
//        private String xMPPDomain;
//        private String xMPPHost;
//        private int xMPPTimeout;
//        private int xmppPingTime;
    }
}


