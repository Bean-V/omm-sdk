package com.oortcloud.appstore.http;

import io.reactivex.Observable;



/**
 * @filename:
 * @function：网络请求中心
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:34
 */
public class HttpRequestCenter {
    /**
     *
     * @return
     */
    public static Observable<String> postAppList(){
        return HttpRequestParam.postAppList();
    }
    /**
     * 推荐页
     *
     * @return
     */
    public static Observable<String> postRecommend() {
        return HttpRequestParam.postRecommend();
    }

    /**
     * 类型分类
     * @return
     */
    public static Observable<String> postClassifyList() {
        return HttpRequestParam.postClassfyList();
    }

    /**
     * 分类页下 APP
     * @return
     */
    public static Observable<String> postClassifyAppList(String classifyUID) {
        return HttpRequestParam.postClassfyAPPList(classifyUID);
    }

    /**
     * 分类页下 ALL_APP
     * @param classifyUID
     * @return
     */
    public static Observable<String> postClassifyAppMore(String classifyUID ,int pageNum , int pageSize) {
        return HttpRequestParam.postClassfyAPPMore(classifyUID , pageNum,  pageSize);

    }

    /**
     *获取推荐页下全部应用
     * @param item
     * @param pageNum
     * @return
     */
    public static Observable<String> postRecommendMore(int  item , int pageNum) {
        return HttpRequestParam.postRecommendMore(item , pageNum);
    }

    /**
     * 搜索 及 获取最新版本
     * @param keyword
     * @return
     */
    public static Observable<String> postSearch(String keyword) {
        return HttpRequestParam.postSearch(keyword);
    }

    /**
     *获取应用分类详情
     * @param
     * @return
     */
    public static Observable<String> postClassifyDetail(String classify) {
        return HttpRequestParam.postClassifyDetail(classify);
    }


    /**
     * 获取责任人相关的App
     * @param
     * @return
     */
    public static Observable<String> postPrincipalAppMore(int pageNum , String appUid) {
        return HttpRequestParam.postPrincipalAppMore(pageNum , appUid);
    }

    /**
     * 添加模块
     * @param module_name
     * @return
     */
    public static Observable<String> addModule(String module_name) {
        return HttpRequestParam.addModule(module_name);
    }
    /**
     * 删除+模块
     * @param module_name
     * @return
     */
    public static Observable<String> deleteModule(String module_name) {
        return HttpRequestParam.deleteModule(module_name);
    }

    /**
     * 编辑模块
     * @param app_uids
     * @param homepage_type
     * @param module_id
     * @param module_name
     * @return
     */
    public static Observable<String> editModule(String app_uids ,int homepage_type ,String module_id , String module_name) {
        return HttpRequestParam.editModule(app_uids , homepage_type , module_id , module_name);
    }

    /**
     * 获取模块  下载过的应用
     * @param pageNum
     * @return
     */
    public static Observable<String> moduleList(int pageNum) {
        return HttpRequestParam.moduleList(pageNum);
    }

    /**
     * 安装
     * @param applabel
     * @param classify
     * @param uid
     * @return
     */
    public static Observable<String> appInstall( String applabel ,String apppackage  , String classify , String uid ,  int versioncode , int termainal) {
        return HttpRequestParam.appInstall(applabel ,apppackage, classify , uid , versioncode, termainal);
    }
    /**
     * 获取安装应用
     * @param
     * @param
     * @param
     * @return
     */
    public static Observable<String> appInstallList(String classify) {
        return HttpRequestParam.appInstallList(classify);
    }

    /**
     * 卸载应用
     * @return
     */
    public static Observable<String> appUNInstall(String uid) {
        return HttpRequestParam.appUNInstall(uid);
    }
    /**
     *  获取安装模块类型
     * @return
     */
    public static Observable<String> meduleClassifyList() {
        return HttpRequestParam.meduleClassifyList();
    }

    /**
     * 此应用负责人的app
     * @return
     */
    public static Observable<String> principalAPP(String uid) {
        return HttpRequestParam.principalAPP(uid);
    }
    /**
     * 此应用负责人的app(显示全部)
     * @return
     */
    public static Observable<String> principalAPPMore(int pageNum ,String uid , int pageSize) {
        return HttpRequestParam.principalAPPMore(pageNum , uid , pageSize);
    }

    /**
     *模块下分类里的app  //h5 web 应用  apk下载过应用
     * @param classify
     * @return
     */
   public static Observable<String> postModuleApplist(String classify) {
        return HttpRequestParam.postModuleApplist(classify);
    }



    /**
     * 获取应用责任人
     * @param oort_uuid 责任人UUDID
     * @return
     */
    public static Observable<String> getUserInfo(String oort_uuid) {
        return HttpRequestParam.getUserInfo(oort_uuid);
    }

  /**
     * 获取部门
     * @param
     * @return
     */
    public static Observable<String> getdeptuser(String oort_depcode) {
        return HttpRequestParam.getdeptuser(oort_depcode);
    }

    /**
     * 版块还原
     * @return
     */
    public static Observable<String> postModuleInit() {
        return HttpRequestParam.postModuleInit();
    }
    /**
     * 移动版块
     * @return
     */
    public static Observable<String> postModuleShift(String module_id ,int module_order) {
        return HttpRequestParam.postModuleShift(module_id , module_order);
    }
    /**
     * 校验版本号
     * @param apppackage
     * @param versioncode
     * @return
     */
    public static Observable<String> verifyversioncode(String apppackage , int versioncode){
        return HttpRequestParam.verifyVersionCode(apppackage , versioncode);
    }

    /**
     * 安装数加一 统计
     * @param apppackage
     * @param versioncode
     * @return
     */
    public static Observable<String> appinstallplusone(String apppackage , int versioncode){
        return HttpRequestParam.appInstallplusone(apppackage , versioncode);
    }
    /**
     *  APP更新列表
     * @return
     */
    public static Observable<String> appUpdateList(){
        return HttpRequestParam.appUpdateList();
    }

    /**
     *  当月最新APP
     * @return
     */
    public static Observable<String> monthNewApp(int pageNum ,  int pageSize){
        return HttpRequestParam.monthNewApp(pageNum , pageSize);
    }
    /**
     *  我的APP
     * @return
     */
    public static Observable<String> myApp(int pageNum ,  int pageSize){
        return HttpRequestParam.MyApp(pageNum , pageSize);
    }

    /**
     *  获取评论
     * @param pageNum
     * @param uid
     * @return
     */
    public static Observable<String>  replySystemList(int pageNum , String uid) {
        return HttpRequestParam.replySystemList(pageNum, uid);
    }
    /**
     *  获取评分
     * @param uid
     * @return
     */
    public static Observable<String>  getGrade( String uid) {
        return HttpRequestParam.replySystemGetGrade(uid);
    }

    /**
     * 获取评分梯级情况
     * @param uid
     * @return
     */
    public static Observable<String>  getGradelevel(String uid ) {
        return HttpRequestParam.getGradelevel(uid);
    }

    /**
     * 评论
     * @param content
     * @param parent_id
     * @param reply_type
     * @param uid
     * @return
     */
    public static Observable<String>   replySystemAdd(String content , String parent_id , int reply_type ,String uid ) {
        return HttpRequestParam.replySystemAdd(content ,parent_id, reply_type , uid );
    }

    /**
     * 评分
     * @param reply_id
     * @param score
     * @return
     */
    public static Observable<String>  replySystemGrade(String reply_id ,float score ) {
        return HttpRequestParam.replySystemGrade(reply_id, score);
    }
    public static Observable<String> GetUserInfo(){
        return HttpRequestParam.getUserinfo();
    }

}
