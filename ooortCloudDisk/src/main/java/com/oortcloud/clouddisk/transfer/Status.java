package com.oortcloud.clouddisk.transfer;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/14 17:25
 * @version： v1.0
 * @function： 上传/下载 状态常量
 */
public class Status {
    public static final int FAIL = 0; //失败
    public static final int SUCCESS = 1; //成功
    public static final int PROGRESS = 2; //进行中
    public static final int PAUSED = 3; //暂停

    //上传类型
    public static final int TYPE_UPLOAD = 1;
    //下载类型
    public static final int TYPE_DOWN = 2;
}
