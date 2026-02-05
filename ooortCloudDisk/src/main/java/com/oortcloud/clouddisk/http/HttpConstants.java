package com.oortcloud.clouddisk.http;


import android.os.Environment;

import com.oortcloud.clouddisk.BaseApplication;

import java.io.File;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/30 11:07
 */
public class HttpConstants {

    //外网访问  http://192.168.199.1:31777/oort/oortcloud-sync-helper-upload/
    //内网  10.157.134.31:32610/oort/oortcloud-sync-helper-download/

    //文件服务
//    public static  String BASE_URL = "http://192.168.88.117:32610";
    public static  String GATEWAY_URL = "http://oort.oortcloudsmart.com:31410";
   //阿克苏
//    public static String GATEWAY_URL = "http://20.137.160.53:32610";
    //
    public static final String NET_DISK_API = "/oort/oortcloud-netdisk";

//    public static final String BASE_FILE = BASE_IP + ":42610";



    //下载文件  支持断点续传
    public static final String DOWN_FILE = NET_DISK_API +  "/netdisk/v1/Downfile";

    //获取文件信息
    public static final String GET_FILE_INFO = NET_DISK_API +  "/netdisk/v1/Getfileinfo";

     //检查文件是否可以秒传
    public static final String MD5_FILE = NET_DISK_API +  "/netdisk/v1/MD5file";

     //分块上传文件
    public static final String UP_FILE_BLOCK = NET_DISK_API +  "/netdisk/v1/Upfileblock";

     //文件/文件夹复制
    public static final String COPY = NET_DISK_API +  "/netdisk/v1/copy";

    //文件列表
    public static final String FILE_LS = NET_DISK_API +  "/netdisk/v1/ls";

    //创建文件夹
    public static final String  MKDIR = NET_DISK_API +  "/netdisk/v1/mkdir";

    //文件/文件夹移动
    public static final String MOVE = NET_DISK_API +  "/netdisk/v1/move";

    //文件/文件夹改名
    public static final String RENAME = NET_DISK_API +  "/netdisk/v1/rename";

    //文件/文件夹删除接口
    public static final String DELETE = NET_DISK_API +  "/netdisk/v1/rm";

    //获取用户信息
    public static final String GET_USER_INFO = NET_DISK_API +  "/v1/getUserInfo";

    //应用历史版本API
    public static final String PLATFORM_API = "/oort/oortcloud-admin-platform";

    //应用历史版本
    public static final String HISTORY = PLATFORM_API + "/supplier-platform/app/history";

    //fastdfs文件文件处理API
    public static final String FASTDF_SERVICE_API = "/oort/oortcloud-fastdfsservice";
    //断点续传
    public static final String RESUME_BIG_FILE = FASTDF_SERVICE_API + "/fastdfs/v1/resumeBigFile";

    public static final String BASE_PATH =  Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "" + BaseApplication.getInstance().getContext().getApplicationInfo().processName ;

    //存储路径
    public static final String USER_PATH =  BASE_PATH + File.separator + BaseApplication.UUID; //存储路径

}
