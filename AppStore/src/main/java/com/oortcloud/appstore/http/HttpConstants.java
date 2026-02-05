package com.oortcloud.appstore.http;

import com.oortcloud.basemodule.constant.Constant;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:07
 */
public class HttpConstants {

//    public static final String BASE_URL = "http://oort.oortcloudsmart.com:31610/";
    public static final String BASE_URL = Constant.BASE_URL;
//    public static final String BASE_URL = "http://116.239.33.218:32610/";
    //sso接口
    public static final String BASE_SSO_URL = BASE_URL;
//    public static final String BASE_SSO_URL = "http://116.239.33.218:32610/";

    //获取应用接口
    public static final String OORT_URL = "oort/oortcloud-admin-platform/";

    public static final String CLIENT_APP_LIST =OORT_URL + "client/app/list";
    //推荐
    public static final String CLIENT_RECOMMEND = OORT_URL + "client/app/recommend";
    //分类
    public static final String CLIENT_CLASSFY_LIST = OORT_URL + "admin-platform/classifylist";
    //分类应用
    public static final String CLIENT_CLASSFY_APP_LIST = OORT_URL +  "client/app/classifyapplist";
    //分类的全部应用
    public static final String CLIENT_CLASSIFY_APP_MORE = OORT_URL + "client/app/classifyappmore";
    //显示全部(推荐页)
    public static final String CLIENT_RECOMMEND_MORE = OORT_URL + "client/app/recommendmore";
    //搜索
    public static final String CLIENT_SEARCH= OORT_URL + "client/app/search";
    //获取应用分类详情
    public static final String CLIENT_CLASSIFY_DETAIL= OORT_URL + "client/app/classifydetail";

   //获取责任人相关App
    public static final String CLIENT_PRINCIPAL_APP= OORT_URL + "client/app/principalapp";

    //获取责任人相关App(显示全部)
    public static final String CLIENT_PRINCIPAL_APP_MORE= OORT_URL + "client/app/principalappmore";

    //添加模块
    public static final String CLIENT_MODULE_ADD= OORT_URL +  "client/module/add";
    //删除模块
    public static final String CLIENT_MODULE_DELETE= OORT_URL + "client/module/delete";
    //模块编辑
    public static final String CLIENT_MODULE_EDIT= OORT_URL + "client/module/edit";
    //我的模块列表
    public static final String CLIENT_MODULE_list= OORT_URL + "client/module/list";
    public static final String CLIENT_StatusList= OORT_URL + "client/getAppApplyStatusList";
    public static final String CLIENT_applyApp= OORT_URL + "client/applyApp";
    //移动模块
    public static final String CLIENT_MODULE_shift= OORT_URL + "client/module/shift";
    //安装
    public static final String CLIENT_APP_INSTALL = OORT_URL + "client/appinstall";
    //我的安装列表
    public static final String CLIENT_APP_INSTALL_LIST = OORT_URL + "client/appinstalllist";
    //卸载
    public static final String CLIENT_APP_UNNINSTALL = OORT_URL + "client/appuninstall";
    //下载应用模块分类
    public static final String CLIENT_CALSSIFY_LIST = OORT_URL + "client/module/classifylist";
    //"模块下分类里的app"
    public static final String CLIENT_MODULE_APP_LIST = OORT_URL + "client/module/applist" ;

    //版块还原
    public static final String CLIENT_MODULE_INIT = OORT_URL + "client/module/init" ;
    //移动版块
    public static final String CLIENT_MODULE_SHIFT = OORT_URL + "client/module/shift" ;

    //校验版本号
    public static final String VERIFY_VERSION_CODE = OORT_URL + "client/verifyversioncode" ;

   //安装数加一 统计
    public static final String APP_INSTALL_PLUSONE = OORT_URL + "client/appinstallplusone" ;

    //APP更新列表
    public static final String APP_UPDATE_LIST = OORT_URL + "client/appupdatelist" ;
    //当前最新APP
    public static final String THE_SAME_MONTH_NEW_APP = OORT_URL + "client/app/newset" ;
    //我的应用
    public static final String MYAPP = OORT_URL + "/client/module/mylist";
    //根据应用包名获取应用信息
    public static final String GET_BY_PACKAGE = OORT_URL + "client/app/getbypackage" ;



    //获取用户信息
    public static final String SSO = "oort/oortcloud-sso/";

    //获取应责任人
    public static final String GET_USERINFO = SSO + "sso/v1/getUserInfo";
    //获取部门
    public static final String GET_DEPTUSER = SSO + "sso/v1/getdeptuser";

    public static final String GET_Statistic= SSO + "rank/v1/login.statistics";



    //评论API
    public static final String OORT_REPLY =  " oort/oortcloud-reply-system/";

    //获取评论
    public static final String REPLY_SYSTEM_LIST = OORT_REPLY + "replysystem/v1/list";

    //获取评分
    public static final String REPLY_SYSTEM_GET_GRADE = OORT_REPLY + "replysystem/v1/getgrade";

    //获取二级评论
    public static final String REPLY_SYSTEM_LIST_SECOND = OORT_REPLY + "replysystem/v1/listSecond";

    //评论
    public static final String REPLY_SYSTEM_ADD = OORT_REPLY + "replysystem/v1/add";

    //评分
    public static final String REPLY_SYSTEM_GRADE = OORT_REPLY + "replysystem/v1/grade";

    //获取评分梯级情况
    public static final String GET_GRADE_LEVEL = OORT_REPLY + "replysystem/v1/getgradelevel";



  public static final String replyservice = "oort/oortcloud-reply-system/";

  public static final String ReplyList = replyservice + "replysystem/v1/list";
  public static final String ReplyListSecond = replyservice + "replysystem/v1/listSecond";
  public static final String ReplyAdd = replyservice + "replysystem/v1/add";



    public static final String cetcnewsservice = "oort/oortcloud-cetcnewsservice/";

    //公安要问
    public static final String GAYWNEWS = cetcnewsservice + "api/v1/cetcnewservice/new_list";


    public static final String GAYWNEWSDetail = cetcnewsservice + "api/v1/cetcnewservice/detail";


    public static final String dynamic = "oort/oortcloud-dynamic/";

    //工作动态
    public static final String GZDTLists = dynamic + "dynamic/v1/dynamic.list";
    public static final String GZDTDetail = dynamic + "dynamic/v1/dynamic.info";

    public static final String JZDPLists = dynamic + "dynamic/v1/dynamic.reviews";
    public static final String TOPICLists = dynamic + "dynamic/v1/dynamic.topic";

    public static final String Dynamic_myinfo = dynamic + "dynamic/v1/dynamic.myinfo";

    public static final String Dynamic_like = dynamic + "dynamic/v1/dynamic.like";

    public static final String Dynamic_collect = dynamic + "dynamic/v1/dynamic.collect";

    public static final String Dynamic_comment_push = dynamic + "dynamic/v1/comment.push";

    public static final String Dynamic_comment_del = dynamic + "dynamic/v1/comment.del";

    public static final String Dynamic_push = dynamic + "dynamic/v1/dynamic.push";

    public static final String Dynamic_myAct = dynamic + "dynamic/v1/dynamic.myact";

    public static final String Dynamic_top = dynamic + "admin/v1/dynamic.top";

    public static final String Dynamic_del = dynamic + "dynamic/v1/dynamic.del";

    public static final String Dynamic_profile = dynamic + "user/v1/profile";

    public static final String Dynamic_followlist = dynamic + "user/v1/followlist";

    public static final String Dynamic_follow = dynamic + "user/v1/follow";
    public static final String Dynamic_fanslist = dynamic + "user/v1/fanslist";


    public static final String Dynamic_grade1 = dynamic + "admin/v1/dynamic.grade1";

    public static final String Dynamic_grade2 = dynamic + "admin/v1/dynamic.grade2";




    public static  final String UPLOAD_FILE = "oort/oortcloud-fastdfsservice/fastdfs/v1/uploadFile";



    public static final String notice = "oort/oortcloud-notice/";
    public static final String notice_list = notice + "notice/v1/list";


    public static final String classroom = "oort/oortcloud-cloud-classroom/";///oort/oortcloud-cloud-classroom/client-cloudclassroom/more
    public static final String cloudclassroom_more = classroom + "client-cloudclassroom/more";

}
