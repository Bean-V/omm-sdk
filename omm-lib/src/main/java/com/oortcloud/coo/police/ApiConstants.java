package com.oortcloud.coo.police;

import com.oortcloud.basemodule.constant.Constant;

public class ApiConstants {

    public static final String BASE_URL = Constant.BASE_URL;
//    public static final String BASE_URL = "http://mulit-env.oort.oortcloudsmart.com:21410";

    public static final String BASE_COORDINATION = BASE_URL + "oort/apaas-police-coordination/bizPoliceCoordination";
    public static final String COORDINATION_LIST = BASE_COORDINATION  +"/list";
    public static final String COORDINATION_SAVA = BASE_COORDINATION  +"/save";
    public static final String COORDINATION_UPDATE = BASE_COORDINATION  +"/update";
    public static final String COORDINATION_DETAIL = BASE_COORDINATION  +"/detail";
    public static final String COORDINATION_SUBMIT = BASE_COORDINATION  +"/submit";
    public static final String COORDINATION_REMOVE = BASE_COORDINATION  +"/remove";
}
