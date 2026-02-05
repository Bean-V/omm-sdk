package com.oortcloud.appstore.db;

import android.content.ContentValues;

import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;

/**
 * @ProjectName: ommadvance
 * @FileName: DBConstant.java
 * @Function:
 * @Author: zhangzhijun / @CreateDate: 2020/5/18 2:31
 * @UpdateUser: 更新者 /@UpdateDate: 2020/5/18 2:31
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DBConstant {
    //模块表
    public static final String MODULE_TABLE = "module_table";
    //模块下App表
    public static final String TABLE = "table_";
    //用于存储应用市场安装列表信息
    public static final String INSTALL_TABLE =  "install_table";

    public static ContentValues createContentValues(AppInfo appInfo){
        ContentValues values = null;
        if (appInfo != null){
            values = new ContentValues();
            values.put("created_on",appInfo.getCreated_on());
            values.put("modified_on",appInfo.getModified_on());
            values.put("uid",appInfo.getUid());
            values.put("supplier_uid",appInfo.getSupplier_uid());
            values.put("app_id",appInfo.getApp_id());
            values.put("app_secret",appInfo.getApp_secret());
            values.put("appweburl",appInfo.getAppweburl());
            values.put("appentry",appInfo.getAppentry());
            values.put("applabel",appInfo.getApplabel());
            values.put("apppackage",appInfo.getApppackage());
            values.put("version",appInfo.getVersion());
            values.put("versioncode",appInfo.getVersioncode());
            values.put("app_size",appInfo.getApp_size());
            values.put("apk_url",appInfo.getApk_url());
            values.put("icon_url",appInfo.getIcon_url());
            values.put("screenshot_url",appInfo.getScreenshot_url());
            values.put("status",appInfo.getStatus());
            values.put("enabled",appInfo.getEnabled());
            values.put("oneword",appInfo.getOneword());
            values.put("ver_description",appInfo.getVer_description());
            values.put("putaway_time",appInfo.getPutaway_time());
            values.put("description",appInfo.getDescription());
            values.put("intro",appInfo.getIntro());
            values.put("remark",appInfo.getRemark());
            values.put("terminal",appInfo.getTerminal());
            values.put("developers",appInfo.getDevelopers());
            values.put("uuid",appInfo.getUuid());
            values.put("label",appInfo.getLabel());
            values.put("classify",appInfo.getClassify());
            values.put("force_upgrade",appInfo.getForce_upgrade());
            values.put("relyservice",appInfo.getRelyservice());
            values.put("network",appInfo.getNetwork());
            values.put("develop_time",appInfo.getDevelop_time());
            values.put("region",appInfo.getRegion());
            values.put("address",appInfo.getAddress());
            values.put("construction_unit",appInfo.getConstruction_unit());
            values.put("principal",appInfo.getPrincipal());
            values.put("phone",appInfo.getPhone());
            values.put("develop_unit",appInfo.getDevelop_unit());
            values.put("operate_phone",appInfo.getOperate_phone());
            values.put("install_num",appInfo.getInstall_num());
            values.put("send_num",appInfo.getSend_num());
            values.put("download_num",appInfo.getDownload_num());
            values.put("userang_enable",appInfo.getUserang_enable());
            values.put("userang_depart",appInfo.getUserang_depart());
            values.put("userang_person",appInfo.getUserang_person());
            values.put("helptext",appInfo.getHelptext());
            values.put("step",appInfo.getStep());
            values.put("path",appInfo.getPath());

        }
        return values;
    }

    public static ContentValues createContentValues(ModuleInfo moduleInfo){
        ContentValues values = null;
        if (moduleInfo != null){
            values = new ContentValues();
            values.put("created_on",moduleInfo.getCreated_on());
            values.put("modified_on",moduleInfo.getModified_on());
            values.put("uuid",moduleInfo.getUuid());
            values.put("module_id",moduleInfo.getModule_id());
            values.put("module_name",moduleInfo.getModule_name());
            values.put("app_uids",moduleInfo.getApp_uids());
            values.put("module_order",moduleInfo.getModule_order());
            values.put("homepage_type",moduleInfo.getHomepage_type());
            values.put("is_edit" ,moduleInfo.getIs_edit());
            values.put("is_show_name" ,moduleInfo.getIs_show_name());
            values.put("is_top_module" ,moduleInfo.getIs_top_module());

        }
        return values;
    }


}
