package com.oortcloud.clouddisk.http;

import com.oortcloud.clouddisk.BaseApplication;

import java.util.HashMap;

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
     * 文件列表
     *
     * @param dir 文件夹路径
     * @param keyword 搜索关键词
     * @param order 排序字段
     * @param pageNum 页数
     * @param pagesize 每页条数
     * @param sort 顺序倒序
     * @return
     */
    public static Observable<String> fileList(String dir ,String keyword ,String order ,int pageNum , int pagesize ,String sort ) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);

        bodyMap.put("dir", dir);
        bodyMap.put("keyword", keyword);
        bodyMap.put("order", order);
        bodyMap.put("page", pageNum);
        bodyMap.put("pagesize", pagesize);
        bodyMap.put("sort", sort);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.FILE_LS, bodyMap , headerMap);
    }


    /**
     * 创建文件夹
     * @param
     * @return
     */
    public static Observable<String> mkdir(String dir , String name) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.MKDIR, bodyMap , headerMap);
    }


    /**
     *  检查文件是否可以秒传
     *
     * @param dir 文件夹路径/
     * @param fileSha1 文件的sha1值
     * @param filemd5 文件的md5值
     * @param fileSize 文件的大小(单位:字节)
     * @param name 文件名
     * @return
     */
    public static Observable<String> md5File(String dir ,String fileSha1 , String filemd5 ,long fileSize , String name) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("fileSha1", fileSha1);
        bodyMap.put("filemd5", filemd5);
        bodyMap.put("filesize", fileSize);
        bodyMap.put("name", name);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.MD5_FILE, bodyMap , headerMap);
    }

    /**
     * 下载文件
     * @param
     * @return
     */
    public static Observable<String> downFile(String dir , String name) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.DOWN_FILE, bodyMap , headerMap);
    }

    /**
     * 文件/文件夹改名
     * @param
     * @return
     */
    public static Observable<String> newName(String dir , String name ,String newName) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);
        bodyMap.put("newname", newName);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.RENAME, bodyMap , headerMap);
    }

    /**
     * 文件/文件夹移动
     * @param
     * @return
     */
    public static Observable<String> move(String name , String dir ,String newDir) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);
        bodyMap.put("newdir", newDir);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.MOVE, bodyMap , headerMap);
    }
    /**
     * 文件/文件夹移动
     * @param
     * @return
     */
    public static Observable<String> copy(String name , String dir ,String newDir) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);
        bodyMap.put("newdir", newDir);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.COPY, bodyMap , headerMap);
    }

    /**
     * 文件/文件夹删除接口
     * @param
     * @return
     */
    public static Observable<String> delete(boolean confirm , String dir ,String name) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", BaseApplication.TOKEN);
        bodyMap.put("confirm", confirm);
        bodyMap.put("dir", dir);
        bodyMap.put("name", name);

        HashMap<String, Object> headerMap = new HashMap<>();

        return RetrofitServiceManager.postObservable(HttpConstants.DELETE, bodyMap , headerMap);
    }



    /**
     * 获取历史版本
     * @return
     */
    public static Observable<String> history(String appPackage) {
        HashMap<String, Object> bodyMap = new HashMap<>();

        bodyMap.put("accessToken", "");
        bodyMap.put("apppackage", appPackage);
        bodyMap.put("pageNum",1);
        bodyMap.put("pageSize",50);
        bodyMap.put("uuid", "");

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("accessToken", "");

        return RetrofitServiceManager.postObservable(HttpConstants.HISTORY, bodyMap, headerMap);

    }


}
