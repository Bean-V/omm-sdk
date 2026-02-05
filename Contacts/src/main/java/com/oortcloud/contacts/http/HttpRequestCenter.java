package com.oortcloud.contacts.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.contacts.bean.DeptUserConfig;
import com.oortcloud.contacts.http.util.Base64;
import com.oortcloud.contacts.http.util.IMHelper;
import com.oortcloud.contacts.http.util.MAC;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

/**
 * @filename:
 * @function：网络请求中心
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:34
 */
public class HttpRequestCenter {
    //请求参数常量
    private final static String
            accessToken = "accessToken",
            access_token = "access_token",
            oort_dep_Code = "oort_depcode", //父部门code
            show_User = "showUser",        //是否返回用户 1:是 0:否 默认0
            oort_d_code = "oort_dcode",    //本部门编码
            oort_pd_code = "oort_pdcode", //上级部门编码
            page = "page", //页数
            page_size = "pagesize", //每页条数
            key_word = "keyword", //搜索关键字
            oort_uuid = "oort_uuid", //用户uuid
            tag_tid = "tid",
            tag_uuids = "uuid";

    public static Observable<String> post(String depCode, int showUser) {

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        if (!TextUtils.isEmpty(depCode)){
            bodyMap.put(oort_dep_Code, depCode);
        }
        bodyMap.put(show_User, showUser);

        HashMap<String, Object> headerMap = new HashMap<>();


        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.DEPARTMENT_USER_URL, headerMap, bodyMap);

    }

    public static Observable<String> post(String dep_code) {
        return post(dep_code, 0);
    }

    /**
     * 获取部门详细信息接口
     * @param d_code 部门编码
     * @return
     */
    public static Observable<String> getDeptInfo(String d_code) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(oort_d_code, d_code);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.DEP_INFO_URL, headerMap, bodyMap);
    }



    public static Observable<String> tagDel(String tid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.TAG_DELETE, headerMap, bodyMap);
    }

    public static Observable<String> addressTagDel(String tid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.ADDRESS_TAG_DELETE, headerMap, bodyMap);
    }

    public static Observable<String> delTagUsers(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.TAG_USERDEL, headerMap, bodyMap);
    }

    public static Observable<String> addressDelTagUsers(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.ADDRESS_TAG_USERDEL, headerMap, bodyMap);
    }

    public static Observable<String> addTagUsers(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
       // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.TAG_USERADD, headerMap, bodyMap);
    }

    public static Observable<String> addAddressTagUsers(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.ADDRESS_TAG_USERADD, headerMap, bodyMap);
    }

    public static Observable<String> getTagUsers(String tid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);

        bodyMap.put("pagesize", "1000");
        ///bodyMap.put("keyword", "");
        bodyMap.put("page", "1");

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(Constant.BASE_URL + Constant.TAG_USERLIST, headerMap, bodyMap);
    }

    public static Observable<String> getAddressTagUsers(String tid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);

        bodyMap.put("pagesize", "1000");
        ///bodyMap.put("keyword", "");
        bodyMap.put("page", "1");

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(Constant.BASE_URL + Constant.ADDRESS_TAG_USERLIST, headerMap, bodyMap);
    }

    public static Observable<String> getLatestUsers() {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("pagesize", "1000");
        ///bodyMap.put("keyword", "");
        bodyMap.put("page", "1");

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(Constant.BASE_URL + Constant.USED_TAG_USERGET, headerMap, bodyMap);
    }

    public static Observable<String> getadminLists() {

        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("pagesize", "1000");
        ///bodyMap.put("keyword", "");
        bodyMap.put("page", "1");
        bodyMap.put("type", "2");
        bodyMap.put("userId", IMUserInfoUtil.getInstance().getUserId());
        IMHelper.generateHttpParam(bodyMap);

        HashMap<String, Object> headerMap = new HashMap<>();
//"http://127.0.0.1:8092"
        return RetrofitServiceManager.postDepartmentObservable(Constant.IM_API_BASE +"console/publicNumbers", headerMap, bodyMap);
    }



    public static Observable<String> setLatestUser(String uuid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_uuids, uuid);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(Constant.BASE_URL + Constant.USED_TAG_USERSET, headerMap, bodyMap);
    }


    public static Observable<String> getAllAPP() {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.MYCollectSave, headerMap, bodyMap);
    }

    public static Observable<String> addFav(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.MYCollectSave, headerMap, bodyMap);
    }

    public static Observable<String> getFav(String tid) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put("appId", tid);
        //bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.MYCollectSave, headerMap, bodyMap);
    }

    public static Observable<String> delFav(String tid,List uuids) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put(tag_tid, tid);
        bodyMap.put(tag_uuids, uuids);

        HashMap<String, Object> headerMap = new HashMap<>();
        // headerMap.put("Content-Type","application/json");
        return RetrofitServiceManager.postObservable(Constant.BASE_URL + Constant.MYCollectSave, headerMap, bodyMap);
    }

    /**
     * 部门列表  指定父部门,通过关键字搜索子部门
     * @param keyword 关键字
     * @return
     */
    public static Observable<String> getDistList(String keyword) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());

        bodyMap.put("keyword",keyword);
        bodyMap.put ("oort_depcode", "99");
        bodyMap.put ("iswork",1);
        bodyMap.put ( "showUser",1);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.DEPT_LIST, headerMap, bodyMap);
    }

    /**
     * uuid 获取SSO用户信息
     * @param uuid 用户id
     * @return
     */
    public static Observable<String> getUserInfo(String uuid) {

        HashMap<String, Object> headerMap = new HashMap<>();

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put(oort_uuid, uuid);
        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.GET_USER_INFO, headerMap, bodyMap);
    }
    /**
     * IM userId 获取SSO用户信息
     * @param userId IM用户id
     * @return
     */
    public static Observable<String> getUserInfoByIMUserId(String userId ) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put(access_token, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("userId", userId);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.GET_USER_INFO_BY_USER_ID, headerMap, bodyMap);
    }

    /**
     *  获取我的权限
     * @return
     */
    public static Observable<String> getMyAuthority() {
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.GET_MY_AUTH, headerMap, bodyMap);
    }

    /**
     * 获取设置的部门列表 （返回带有隐藏部门）
     * @param pDeptCode 上级部门编码
     * @return
     */
    public static Observable<String> getDeptList(String pDeptCode) {
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("pdeptcode", pDeptCode);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.GET_DEPT_LIST, headerMap, bodyMap);
    }

    /**
     *  获取设置的部门用户列表(返回带有隐藏用户)
     * @param deptCode 当前部门编码
     * @param isWork 用户离职状态 1:在职;2:离职 0:所有(默认)
     * @return
     */
    public static Observable<String> getUserList(String deptCode, int isWork) {
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("deptcode", deptCode);
        bodyMap.put("iswork", isWork);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postDepartmentObservable(HttpConstants.GET_USER_LIST, headerMap, bodyMap);
    }

    /**
     * 部门排序
     * @param deptCode 排序后的部门编码集
     * @param pDeptCode 上级部门编码
     * @return
     */
    public static Observable<String> sortDept(List<String> deptCode , String pDeptCode) {
        HashMap<String, Object> headerMap = new HashMap<>();

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("deptcode", deptCode);
        bodyMap.put("pdeptcode", pDeptCode);
        return RetrofitServiceManager.postObservable(HttpConstants.SORT_DEPT, headerMap, bodyMap);
    }

    /**
     * 部门用户排序
     * @param uuid 排序后的人员uuid集
     * @param deptCode 所在的部门编码
     * @return
     */
    public static Observable<String> sortUser(List<String>  uuid , String deptCode) {
        //json = new Gson().toJson(deptCode);
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("deptcode", deptCode);
        bodyMap.put("uuid",uuid);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postObservable(HttpConstants.SORT_USER,  headerMap, bodyMap);
    }
    /**
     * 获取默认配置
     * @return
     */
    public static Observable<String> getDefaultConfig() {
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postObservable(HttpConstants.GET_DEFAULT_CONFIG,  headerMap, bodyMap);
    }
    /**
     * 获取部门配置
     * @param deptCode 所在的部门编码
     * @return
     */
    public static Observable<String> getDeptConfig( String deptCode) {
        //json = new Gson().toJson(deptCode);
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("deptcode", deptCode);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postObservable(HttpConstants.GET_DEPT_CONFIG,  headerMap, bodyMap);
    }
    /**
     * 获取用户配置
     * @param deptCode 所在的部门编码
     * @param uuid 人员uuid
     * @return
     */
    public static Observable<String> getUserConfig(String deptCode, String uuid) {
        //json = new Gson().toJson(deptCode);
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("deptcode", deptCode);
        bodyMap.put("oort_uuid",uuid);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postObservable(HttpConstants.GET_USER_CONFIG,  headerMap, bodyMap);
    }
    /**
     * 保存部门配置
     * @param config 配置信息
     * @param deptCode 所在的部门编码集
     * @return
     */
    public static Observable<String> storageDeptConfig(DeptUserConfig config, String deptCode) {
        String json = new Gson().toJson(config);
        Log.v("msg", "DeptUserConfig----->"+  json);
        HashMap<String, Object> headerMap = new HashMap<>();
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("data", config);
        bodyMap.put("deptcode", deptCode);

        return RetrofitServiceManager.postObservable(HttpConstants.STORAGE_DEPT_CONFIG,  headerMap, bodyMap);
    }
    /**
     * 保存用户配置
     * @param config 配置信息
     * @param deptCode 所在的部门编码
     * @param uuid 人员uuid集
     * @return
     */
    public static Observable<String> storageUserConfig(DeptUserConfig config, String deptCode, String  uuid ) {
        //json = new Gson().toJson(deptCode);
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(accessToken, IMUserInfoUtil.getInstance().getToken());
        bodyMap.put("data", config);
        bodyMap.put("deptcode", deptCode);
        bodyMap.put("oort_uuid",uuid);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitServiceManager.postObservable(HttpConstants.STORAGE_USER_CONFIG,  headerMap, bodyMap);
    }
    /**
     * 获取IM好友
     * @return
     */
    public static Observable<String> getIMFriendList() {

        HashMap<String, Object> hailFellow = new HashMap<>();

        IMHelper.generateHttpParam(hailFellow);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitIMServiceManager.postOMMObservable(HttpConstants.FRIENDS_ATTENTION_LIST, hailFellow, headerMap);
    }

    /**
     * 添加IM好友
     * @param toUserId  IM用户id
     * @return
     */
    public static Observable<String> addIMFriend(String toUserId) {

        HashMap<String, Object> friendMap = new HashMap<>();

        friendMap.put("toUserId", toUserId);

        IMHelper.generateHttpParam(friendMap);
        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitIMServiceManager.postOMMObservable(HttpConstants.ADD_FRIENT, friendMap, headerMap);
    }

    /**
     * 查询搜IM人员索列表
     * @param oort_loginId sso登录id
     * @return
     */
    public static Observable<String> getSearchFriend(String oort_loginId) {

        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("pageIndex", String.valueOf(0));
        friendMap.put("maxAge", String.valueOf(200));
        friendMap.put("nickname", oort_loginId);
//        friendMap.put("pageSize",String.valueOf(20));
//        friendMap.put("active", String.valueOf(0));
//        friendMap.put(" language", "zh");
        IMHelper.generateHttpParam(friendMap);
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitIMServiceManager.postOMMObservable(HttpConstants.USER_NEAR, friendMap, headerMap);

    }
    /**
     * 获取IM用户资料
     * @param userId sso登录id
     * @return
     */
    public static Observable<String>  getIMUser(String userId) {

        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("userId", userId);
        IMHelper.generateHttpParam(friendMap);
        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitIMServiceManager.postOMMObservable(HttpConstants.IM_USER_GET, friendMap, headerMap);

    }


    public static Observable<String> createGroupChat(final String roomName, final String roomDesc, int isRead,
                                                     int isLook, int isNeedVerify, int isShowMember, int isAllowSendCard) {
        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("access_token", IMUserInfoUtil.getInstance().getToken());


        HashMap<String, Object> headerMap = new HashMap<>();
        return RetrofitIMServiceManager.postOMMObservable(HttpConstants.ROOM_ADD, friendMap, headerMap);
    }


}
