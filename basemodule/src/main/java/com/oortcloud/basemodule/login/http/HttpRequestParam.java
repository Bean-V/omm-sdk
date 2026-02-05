package com.oortcloud.basemodule.login.http;

import android.text.TextUtils;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/15 17:07
 */
public class HttpRequestParam {
    //静态token失效问题
//    private static String accessToken = AppMallinit.store_token;
//    private static String uuid = AppMallinit.store_uuid;


    public interface Callback{
        public void sucCallback(Object o);
        public void failCallback(Object o);

    }
    private static Observable<String> post(String url) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(url, headerMap, bodyMap);
    }

    /**
     * 手机端我的应用
     *
     * @return
     */
    public static Observable<String> postAppList() {
        return post(HttpConstants.CLIENT_APP_LIST);
    }

    /**
     * 推荐页
     *
     * @return
     */
    public static Observable<String> postRecommend() {

        return post(HttpConstants.CLIENT_RECOMMEND);

    }

    /**
     * 分类
     * @return
     */
    public static Observable<String> postClassfyList() {
        return post(HttpConstants.CLIENT_CLASSFY_LIST);
    }

    /**
     * 分类 APP
     * @return
     */
    public static Observable<String> postClassfyAPPList(String classifyUID) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("classify", classifyUID);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_CLASSFY_APP_LIST, headerMap, bodyMap);
    }

    /**
     * 分类页 ALL_APP
     * @return
     */
    public static Observable<String> postClassfyAPPMore(String classifyUID , int pageNum , int pageSize) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("classify", classifyUID);
        bodyMap.put("pageNum" , pageNum);
        bodyMap.put("pageSize" , pageSize);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_CLASSIFY_APP_MORE, headerMap, bodyMap);
    }

    /**
     * 显示全部(推荐页)
     * @param item
     * @param pageNum
     * @return
     */
    public static Observable<String> postRecommendMore(int item , int pageNum ) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("item", item);
        bodyMap.put("pageNum" , pageNum);
        bodyMap.put("pageSize" , 100);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_RECOMMEND_MORE, headerMap, bodyMap);
    }

    public static Observable<String> postSearch(String keyword ) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("Content", keyword);
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_SEARCH, headerMap, bodyMap);
    }

    public static Observable<String> appSearch(String keyword ,String token, String uuid, String depcode) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("befrom", 0);
        bodyMap.put("Content", keyword);
        bodyMap.put("accessToken", token);
        bodyMap.put("uuid", uuid);
        bodyMap.put("depCode", depcode);
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_SEARCH, headerMap, bodyMap);
    }

    public static Observable<String> postClassifyDetail(String classify) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("classify", classify);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_CLASSIFY_DETAIL, headerMap, bodyMap);
    }

    /**
     * 获取责任人相关应用
     * @param pageNum
     * @param appUid
     * @return
     */
    public static Observable<String> postPrincipalAppMore(int pageNum , String appUid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", 10);
        bodyMap.put("uid", appUid);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_PRINCIPAL_APP_MORE, headerMap, bodyMap);
    }

    /**
     * 添加模块
     * @param module_name
     * @return
     */
    public static Observable<String> addModule(String module_name) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("module_name", module_name);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_ADD,  headerMap, bodyMap);
    }

    /**
     * 移除模块
     * @param module_id
     * @return
     */
    public static Observable<String> deleteModule(String module_id) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("module_id", module_id);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_DELETE,  headerMap, bodyMap);
    }
    /**
     * 模块编辑
     * @param module_id
     * @return
     */
    public static Observable<String> editModule(String app_uids ,int homepage_type ,String module_id , String module_name) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("app_uids", app_uids);
        bodyMap.put("homepage_type", homepage_type);
        bodyMap.put("module_id", module_id);
        bodyMap.put("module_name", module_name);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_EDIT,  headerMap, bodyMap);
    }
    /**
     * 我的模块
     * @param
     * @return
     */
    public static Observable<String> moduleList(int pageNum) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", 50);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_list,  headerMap, bodyMap);
    }

    /**
     * 移动模块
     * @param
     * @return
     */
    public static Observable<String> shiftModule(String module_id , String module_order) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("module_id", module_id);
        bodyMap.put("module_order", module_order);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_list,  headerMap, bodyMap);
    }

    /**
     * 应用安装
     * @param applabel
     * @param classify
     * @param uid
     * @return
     */
    public static Observable<String> appInstall( String applabel ,String apppackage  ,String classify , String uid , int versioncode , int  termainal) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken",  AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("applabel", applabel);
        bodyMap.put("apppackage", apppackage);
        bodyMap.put("classify", classify);
        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        bodyMap.put("versioncode", versioncode);

        bodyMap.put("termainal", termainal);

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_APP_INSTALL,  headerMap, bodyMap);
    }
    /**
     * 我的安装列表
     * @param
     * @return
     */
    public static Observable<String> appInstallList(String classify) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("classify", classify);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_APP_INSTALL_LIST,  headerMap, bodyMap);
    }


    /**
     * 卸载应用
     * @param uid
     * @return
     */
    public static Observable<String> appUNInstall(String uid) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_APP_UNNINSTALL,  headerMap, bodyMap);
    }
    /**
     * 获取下载应用的分类
     * @param
     * @return
     */
    public static Observable<String> meduleClassifyList() {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_CALSSIFY_LIST,  headerMap, bodyMap);
    }
    /**
     * 获取此应用负责人的app
     * @param
     * @return
     */
    public static Observable<String> principalAPP(String uid) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_PRINCIPAL_APP,  headerMap, bodyMap);
    }

    /**
     * 获取此应用负责人的app（显示全部）
     * @param
     * @return
     */
    public static Observable<String> principalAPPMore(int pageNum , String uid , int pageSize) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", pageSize);

        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_PRINCIPAL_APP_MORE,  headerMap, bodyMap);
    }



    /**
     * 模块下分类里的app
     * @param
     * @return
     */
    public static Observable<String> postModuleApplist(String classify) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("classify", classify);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_APP_LIST,  headerMap, bodyMap);
    }
    /**
     *版块还原
     * @param
     * @return
     */
    public static Observable<String> postModuleInit() {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_INIT,  headerMap, bodyMap);
    }


    /**
     *移动模块
     * @param module_id
     * @param module_order
     * @return
     */
    public static Observable<String> postModuleShift(String module_id ,int module_order) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("module_id", module_id);
        bodyMap.put("module_order", module_order);

        bodyMap.put("uuid", AppStoreInit.getUUID());

        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_SHIFT,  headerMap, bodyMap);
    }


    /**
     * 校验版本号
     * @param apppackage
     * @param versioncode
     * @return
     */
    public static Observable<String> verifyVersionCode(String apppackage , int versioncode) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("apppackage", apppackage);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("versioncode", versioncode);
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.VERIFY_VERSION_CODE,  headerMap, bodyMap);
    }


    /**
     * 安装数加一 统计
     * @param apppackage
     * @param versioncode
     * @return
     */
    public static Observable<String> appInstallplusone(String apppackage , int versioncode) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("apppackage", apppackage);
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("versioncode", versioncode);

        return RetrofitServiceManager.PostObservable(HttpConstants.APP_INSTALL_PLUSONE,  headerMap, bodyMap);
    }

    /**
     *  APP更新列表
     * @return
     */
    public static Observable<String> appUpdateList() {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("uuid", AppStoreInit.getUUID());
        bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.APP_UPDATE_LIST,  headerMap, bodyMap);
    }
    /**
     *  当月最新APP
     * @return
     */
    public static Observable<String> monthNewApp(int pageNum ,int pageSize) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("pageNum",  pageNum);
        bodyMap.put("pageSize", pageSize );
        bodyMap.put("uuid", AppStoreInit.getUUID());

        bodyMap.put("depCode", AppStoreInit.getDecode());

        return RetrofitServiceManager.PostObservable(HttpConstants.THE_SAME_MONTH_NEW_APP,  headerMap, bodyMap);
    }
    /**
     *  我的APP
     * @return
     */
    public static Observable<String> MyApp(int pageNum ,int pageSize) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  AppStoreInit.getToken());
        bodyMap.put("pageNum",  pageNum);
        bodyMap.put("pageSize", pageSize );
        bodyMap.put("uuid", AppStoreInit.getUUID());

        bodyMap.put("depCode", AppStoreInit.getDecode());

        return RetrofitServiceManager.PostObservable(HttpConstants.MYAPP,  headerMap, bodyMap);
    }



    /**
     * 获取应用责任人
     * @param
     * @return
     */
    public static Observable<String>  getUserInfo(String oort_uuid) {
        HashMap<String, Object> headerMap = new HashMap<>();

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());

        bodyMap.put("oort_uuid", oort_uuid);

        return SSORetrofitServiceManager.PostObservable(HttpConstants.GET_USERINFO,  headerMap, bodyMap);
    }

    /**
     * 获取部门
     * @param
     * @return
     *
     */
    public static Observable<String>  getdeptuser(String oort_depcode) {
        HashMap<String, Object> headerMap = new HashMap<>();

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());

        bodyMap.put("oort_depcode", oort_depcode);
        bodyMap.put("showUser", 0);

        return SSORetrofitServiceManager.PostObservable(HttpConstants.GET_DEPTUSER,  headerMap, bodyMap);
    }


    /**
     *  获取评论
     * @param pageNum
     * @param uid 目标的uid
     * @return
     */
    public static Observable<String>  replySystemList(int pageNum  ,String uid) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", 100);
        bodyMap.put("taguser", "app");

        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return SSORetrofitServiceManager.PostObservable(HttpConstants.REPLY_SYSTEM_LIST,  headerMap, bodyMap);
    }

    /**
     *  获取评分
     * @param uid 目标的uid
     * @return
     */
    public static Observable<String>  replySystemGetGrade(String uid) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());

        bodyMap.put("taguser", "app");

        bodyMap.put("uid", uid);

        return SSORetrofitServiceManager.PostObservable(HttpConstants.REPLY_SYSTEM_GET_GRADE,  headerMap, bodyMap);
    }

    /**
     *  获取二级评论
     * @param pageNum 页数
     * @param uid       目标的uid
     * @return
     */
    public static Observable<String>  replySystemListSecond(String pageNum , String uid) {
        HashMap<String, Object> headerMap = new HashMap<>();

        headerMap.put("accessToken", AppStoreInit.getToken());
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("pageNum", pageNum);
        bodyMap.put("pageSize", 10);
        bodyMap.put("taguser", "text");

        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return SSORetrofitServiceManager.PostObservable(HttpConstants.REPLY_SYSTEM_LIST_SECOND,  headerMap, bodyMap);
    }

    /**
     * 评论
     * @param content 评论内容
     * @param parent_id 父评论id
     * @param reply_type  0-直接评论,1-评论的评论
     * @param uid   目标的uid
     * @return
     */
    public static Observable<String>  replySystemAdd(String content , String parent_id , int reply_type ,String uid ) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("content", content);
        if (!TextUtils.isEmpty(parent_id)){
            bodyMap.put("parent_id", parent_id);
        }
        bodyMap.put("reply_type", reply_type);
        bodyMap.put("taguser", "huawei_news");

        bodyMap.put("uid", uid);
        bodyMap.put("uuid", AppStoreInit.getUUID());

        return SSORetrofitServiceManager.PostObservable(HttpConstants.REPLY_SYSTEM_ADD,  headerMap, bodyMap);
    }

    /**
     * 评分
     * @param reply_id  评论uid
     * @param score
     * @return
     */
    public static Observable<String>  replySystemGrade(String reply_id ,float score ) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("reply_id", reply_id);
        bodyMap.put("score", score);
        bodyMap.put("taguser", "app");
        bodyMap.put("type", 1);

        bodyMap.put("uuid", AppStoreInit.getUUID());
        return SSORetrofitServiceManager.PostObservable(HttpConstants.REPLY_SYSTEM_GRADE,  headerMap, bodyMap);
    }

    /**
     *  获取评分梯级情况
     * @param uid
     * @return
     */
    public static Observable<String>  getGradelevel(String uid ) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("taguser", "app");
        bodyMap.put("uid", uid);

        return SSORetrofitServiceManager.PostObservable(HttpConstants.GET_GRADE_LEVEL,  headerMap, bodyMap);
    }


    /**
     * 初始化 安装的表应用
     * @param
     * @return
     */
    public static Observable<String> appInstallList(String token , String uuid) {
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",  token);
        bodyMap.put("classify", "");
        bodyMap.put("uuid",  uuid);

        return RetrofitServiceManager.PostTableAPPbservable(HttpConstants.CLIENT_APP_INSTALL_LIST,  headerMap, bodyMap);
    }
    /**
     * 初始化我的模块
     * @param
     * @return
     */
    public static Observable<String> moduleList(String token , String uuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("pageNum", 1);
        bodyMap.put("pageSize", 50);
        bodyMap.put("uuid", uuid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_MODULE_list,  headerMap, bodyMap);
    }

    public static Observable<String> statusList(String token , List appUids) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("uids", appUids);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_StatusList,  headerMap, bodyMap);
    }

    public static Observable<String> applyApp(String token , String appUid,String content) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("uid", appUid);
        bodyMap.put("content", content);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.CLIENT_applyApp,  headerMap, bodyMap);
    }




    public static Observable<String> GAYWList(String token , String uuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        //bodyMap.put("accessToken", token);
        bodyMap.put("pageIndex", 1);
        bodyMap.put("pageSize", 3);
        bodyMap.put("newsType", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GAYWNEWS,  headerMap, bodyMap);
    }

    public static Observable<String> GAYWNEWSDetail(String token , int ID) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("ID", ID);
        return RetrofitServiceManager.PostObservable(HttpConstants.GAYWNEWSDetail,  headerMap, bodyMap);
    }

    public static Observable<String> JXRTList(String token) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", 1);
        bodyMap.put("pageSize", 3);
        bodyMap.put("istop", true);
        bodyMap.put("order", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String> lddpList(String token) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", 1);
        bodyMap.put("pageSize", 3);
        bodyMap.put("order", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.JZDPLists,  headerMap, bodyMap);
    }


    public static Observable<String>jzdpList(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("order", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.JZDPLists,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_all_List(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("order", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_follow_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("isfollow", true);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_isgrade2_list(String token,int pageIndex,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("isgrade2", true);

        if(oort_userid != null){
            bodyMap.put("oort_userid", oort_userid);
        }
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_isgrade1_list(String token,int pageIndex,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("isgrade1", true);
        if(oort_userid != null){
            bodyMap.put("oort_userid", oort_userid);
        }
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_like_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("mod", 2);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_myAct,  headerMap, bodyMap);
    }
    public static Observable<String>dynamic_comment_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("mod", 1);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_myAct,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_topic_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 200);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.TOPICLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_topic_list(String token,int pageIndex,String oort_tuuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("oort_tuuid", oort_tuuid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_search_list(String token,int pageIndex,String keyword) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("keyword", keyword);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }
    public static Observable<String>dynamic_dept_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("isdept", 2);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_iscollect_list(String token,int pageIndex) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("iscollect",true);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_user_list(String token,int pageIndex,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("oort_userid", oort_userid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTLists,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_comment_push(String token,String content,String oort_duuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("content", content);
        bodyMap.put("oort_duuid", oort_duuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_comment_push,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_comment_push_reply(String token,String content,String oort_duuid,String oort_reply_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("content", content);
        bodyMap.put("oort_duuid", oort_duuid);
        bodyMap.put("oort_reply_userid", oort_reply_userid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_comment_push,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_myinfo(String token) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_myinfo,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_profile(String token,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_userid", oort_userid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_profile,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_followlist(String token,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_userid", oort_userid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_followlist,  headerMap, bodyMap);
    }
    public static Observable<String>reply_list(String token,int pageIndex,String oort_uuid,String uid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("uuid", oort_uuid);
        bodyMap.put("taguser", "huawei_news");
        bodyMap.put("uid", uid);


        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.ReplyList,  headerMap, bodyMap);
    }

    public static Observable<String>reply_list_second(String token,int pageIndex,String oort_uuid,String uid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("page", pageIndex);
        bodyMap.put("pageSize", 20);
        bodyMap.put("uuid", oort_uuid);
        bodyMap.put("taguser", "huawei_news");
        bodyMap.put("uid", uid);


        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.ReplyListSecond,  headerMap, bodyMap);
    }

    public static Observable<String>reply_add(String token,int pageIndex,String oort_uuid,String parent_id,String content,int reply_type) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("content", content);
        bodyMap.put("reply_type", reply_type);
        bodyMap.put("uuid", oort_uuid);
        bodyMap.put("taguser", "huawei_news");
        bodyMap.put("parent_id", parent_id);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.ReplyAdd,  headerMap, bodyMap);
    }



    public static Observable<String>dynamic_fanslist(String token,String oort_userid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_userid", oort_userid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_fanslist,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_follow(String token,String oort_userid,int unfollow) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_userid", oort_userid);
        bodyMap.put("unfollow", unfollow);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_follow,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_like(String token,int unlike,String oort_duuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("unlike", unlike);
        bodyMap.put("oort_duuid", oort_duuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_like,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_top(String token,String oort_duuid,int top_no) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_duuid", oort_duuid);
        bodyMap.put("top_no", top_no);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_top,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_grade1(String token,String oort_duuid,int mod) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_duuid", oort_duuid);
        bodyMap.put("mod",mod);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_grade1,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_grade2(String token,String oort_duuid,int mod) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("mod", mod);
        bodyMap.put("oort_duuid", oort_duuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_grade2,  headerMap, bodyMap);
    }
    public static Observable<String>dynamic_del(String token,String oort_duuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_duuid", oort_duuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_del,  headerMap, bodyMap);
    }


    public static Observable<String>dynamic_push(String token,String oort_duuid,String oort_tuuid,List<String> ats,String content,List attts) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_duuid", oort_duuid);
        bodyMap.put("oort_tuuid", oort_tuuid);
        bodyMap.put("at", ats);
        bodyMap.put("attach", attts);
        bodyMap.put("content", content);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_push,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_collect(String token,int uncollect,String oort_duuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("uncollect", uncollect);
        bodyMap.put("oort_duuid", oort_duuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_collect,  headerMap, bodyMap);
    }

    public static Observable<String>dynamic_del_comment(String token,String oort_uuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_uuid", oort_uuid);
        return RetrofitServiceManager.PostObservable(HttpConstants.Dynamic_comment_del,  headerMap, bodyMap);
    }

    public static Observable<String> statistics(String token) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("beginday", StringTimeUtils.getCurrentDate("yyyy-MM-dd"));
        bodyMap.put("endday", StringTimeUtils.getCurrentDate("yyyy-MM-dd"));
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GET_Statistic,  headerMap, bodyMap);
    }


    public static Observable<String> uploadfile(String token,File file,String event_id,String mark,String address) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> map = new HashMap<>();
        map.put("accessToken", token);
        map.put("mark", mark);
        map.put("address", address);
        map.put("event_id", event_id);

        return RetrofitServiceManager.PostFileObservable(HttpConstants.UPLOAD_FILE,  headerMap,file,map);
    }

    public static Observable<String> dynamic_info(String token, String oort_duuid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("oort_duuid", oort_duuid);
        //bodyMap.put("depCode", AppStoreInit.getDecode());
        return RetrofitServiceManager.PostObservable(HttpConstants.GZDTDetail,  headerMap, bodyMap);
    }




    public static Observable<String>notice_list_unread(String token,String uuid,String organid) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", token);

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", token);
        bodyMap.put("uuid", uuid);
        bodyMap.put("organid", organid);
        bodyMap.put("item", 1);
        bodyMap.put("page", 1);
        bodyMap.put("pageNum", 1);
        bodyMap.put("status", 2);
        return RetrofitServiceManager.PostObservable(HttpConstants.notice_list,  headerMap, bodyMap);
    }


    /**
     * 根据应用包名获取应用信息
     * @param
     * @return
     */
    public static Observable<String> getByPackage(String packageName ) {

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", AppStoreInit.getToken());

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", AppStoreInit.getToken());
        bodyMap.put("apppackage", packageName);

        return RetrofitServiceManager.PostObservable(HttpConstants.GET_BY_PACKAGE,  headerMap, bodyMap);
    }
    public static Observable<String> getUserinfo(){
        HashMap<String, Object> headerMap = new HashMap<>();
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("accessToken",AppStoreInit.getToken());
        bodyMap.put("oort_uuid",AppStoreInit.getUUID());
        return RetrofitServiceManager.PostObservable(HttpConstants.GET_USERINFO,  headerMap, bodyMap);
    }
}
