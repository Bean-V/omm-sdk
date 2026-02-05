package com.oortcloud.bean;

public class ReportInfo {
    /*depart_code	string    部门编码

    electric	integer    电量

    elements*	string            定位坐标

    interphone*	string            对讲机

    latitude	string    纬度

    longitude	string    经度

    name*	string            名字

    phone*	string            手机号码

    photo	string    头像

    police_id*	string            警号

    police_type	integer    类型（0-民警,1-辅警,2-协警）

    position*	string            岗位名字

    position_type	integer    岗位类型

    shift	string    班次

    signal	integer    信号强弱

    unit*	string            单位
    screen_num	string    屏幕编号
    video_num	string    视频编号*/
    public static String accessToken = "";   //
    public static String oort_uuid = "";
    public static String sn = ""; //手机sn号
    public static String depart_code = "99999000";   //部门编码
    public static String depart_name = "测试部门";   //部门编码
    public static int electric = 0;        //电量
    public static String elements = "";     //坐标位置  必填
    public static String interphone = "";   //对讲机
    public static double latitude = 0;    //纬度
    public static double longitude = 0;    //经度
    public static String name = "";         //名字  必填
    public static String phone = "";        //电话  必填
    public static String photo = "";        //相片
    public static String police_id = "";    //警号  必填
    public static int police_type = 0;     //类型
    public static String position = "测试";     //职位信息  必填
    public static int position_type = 0;   //岗位
    public static String shift = "";        //班次
    public static String remark = "";        //
    public static int signal = 0;          //信号强度
    public static String unit = "测试单位";         //必填
    public static String video_num = "";    //直播地址
    public static String screen_num = "";    //直播地址
    public static String default_mm;
    private volatile static ReportInfo reportInfo;

    private ReportInfo(){

    }
    public static ReportInfo getInstance() {
        if (reportInfo == null) {
            synchronized (ReportInfo.class) {
                if (reportInfo == null) {
                    reportInfo = new ReportInfo();
                }
            }
        }
        return reportInfo;

    }
}
