package com.oortcloud.contacts.http;

import com.oortcloud.basemodule.constant.Constant;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:07
 */
public class HttpConstants {

//    public static final String SSO_URL = "http://oort.oortcloudsmart.com:32610/";
    public static final String ORD_CONTACT_BASE_URL = Constant.BASE_URL;
    //sso接口通用段
    public static final String SSO_BASE_URL = "oort/oortcloud-sso/";
    //sso_v1
    public static final String SSO_V1 = SSO_BASE_URL +  "sso/v1/";
    //部门与用户树
    public static final String DEPARTMENT_USER_URL = SSO_V1 + "getdeptuser";
//    public static final String DEPARTMENT_USER_URL = "oort/oortcloud-userservice/user/v1/getdeptuser";
    //部门详情
    public static final String DEP_INFO_URL = SSO_V1 +"getDeptInfo";
   // 关键字搜索部门列表
    public static final String DEPT_LIST = SSO_V1 + "getdeptuser";
    // 获取用户信息
    public static final String GET_USER_INFO = SSO_V1 + "getUserInfo";
    //sso_im
    public static final String SSO_IM_V1 = SSO_BASE_URL +  "ssoim/v1/";
    //IM userId获取sso用户信息接口
    public static final String GET_USER_INFO_BY_USER_ID = SSO_IM_V1 +"getUserinfobyUserId";

    //管理员set_v1
    public static final String SET_V1 = SSO_BASE_URL +  "set/v1/";
    //获取我的权限
    public static final String GET_MY_AUTH = SET_V1 +"getMyauth";
    //部门列表(返回带有隐藏部门)
    public static final String GET_DEPT_LIST = SET_V1 +"deptlist";
    //用户列表(返回带有隐藏用户)
    public static final String GET_USER_LIST = SET_V1 +"userlist";
    //设置部门排序
    public static final String SORT_DEPT = SET_V1 +"sortset.dept";
    //设置部门用户排序
    public static final String SORT_USER = SET_V1 +"sortset.user";
   //获取默认配置
    public static final String GET_DEFAULT_CONFIG  = SET_V1 +"column.golbalget";
    //保存默认配置
    public static final String SET_DEFAULT_CONFIG  = SET_V1 +"column.golbalset";
   //获取部门配置
    public static final String GET_DEPT_CONFIG  = SET_V1 +"column.deptget";
    //获取用户配置
    public static final String GET_USER_CONFIG  = SET_V1 +"column.userget";
    //保存部门配置
    public static final String STORAGE_DEPT_CONFIG = SET_V1 +"column.deptset";
   //保存用户配置
    public static final String STORAGE_USER_CONFIG  = SET_V1 +"column.userset";

    //头像 IP + UUID
    public static final String PHOTO_URL = ORD_CONTACT_BASE_URL + SSO_BASE_URL +"/photo/";


    //IM服务接口
//    public static final String IM_URL = "http://10.157.134.31:31600/";
    public static final String IM_URL = Constant.IM_API_BASE;

    //获取好友
    public static final String FRIENDS_LIST = "friends/list";
    //获取好友关注列表
    public static final String FRIENDS_ATTENTION_LIST = "friends/attention/list";
    // 加关注 || 直接成为好友
    public static final String FRIENDS_ATTENTION_ADD =  "friends/attention/add";
    //加好友
    public static final String ADD_FRIENT =  "friends/add";

    // 查询搜索列表
    public static final String USER_NEAR = "nearby/user";
    // 获取用户资料，更新本地数据的接口
    public static final String IM_USER_GET = "user/get";

    // 公众号搜索列表
    public static final String PUBLIC_SEARCH =  "public/search/list";
    //创建群组
    public static final String ROOM_ADD =  "room/add";

    public static final String DEVICE_LIST = ORD_CONTACT_BASE_URL  +"/bus/apaas-location-service/manage/v1/device.list";




}
