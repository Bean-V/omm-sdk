package com.oortcloud.appstore.db;

/**
 * @ProjectName: ommadvance
 * @FileName: TableDataStructure.java
 * @Function: 表数据结构
 * @Author: zhangzhijun / @CreateDate: 2020/5/18 8:29
 * @UpdateUser: 更新者 /@UpdateDate: 2020/5/18 8:29
 * @Version: 1.0
 */
public class TableDataStructure {

    // 表数据结构
    public static final String APP_INFO = "("
            +"id integer primary key autoincrement,"
            +"created_on Long,"
            +"modified_on Long,"
            +"uid text,"
            +"supplier_uid text,"
            +"app_id text,"
            +"app_secret text,"
            +"appweburl text,"
            +"appentry text,"
            +"applabel text,"
            +"apppackage text,"
            +"version text,"
            +"versioncode integer,"
            +"app_size text,"
            +"apk_url text,"
            +"icon_url text,"
            +"screenshot_url text,"
            +"status integer,"
            +"enabled integer,"
            +"oneword text,"
            +"ver_description text,"
            +"putaway_time text,"
            +"description text,"
            +"intro text,"
            +"remark text,"
            +"terminal integer,"
            +"developers text,"
            +"uuid text,"
            +"label text,"
            +"classify text,"
            +"force_upgrade integer,"
            +"relyservice text,"
            +"network integer,"
            +"develop_time Long,"
            +"region text,"
            +"address text,"
            +"construction_unit text,"
            +"principal text,"
            +"phone text,"
            +"develop_unit text,"
            +"operate_phone text,"
            +"install_num integer,"
            +"send_num integer,"
            +"download_num integer,"
            +"userang_enable integer,"
            +"userang_depart text,"
            +"userang_person text,"
            +"helptext text,"
            +"step integer,"
            +"path text)";

    // 表数据结构
    public static final String MODULE_INFO = "("
            +"id integer primary key autoincrement,"
            +"created_on Long,"
            +"modified_on Long,"
            +"uuid text,"
            +"module_id text,"
            +"module_name text,"
            +"app_uids text,"
            +"module_order integer,"
            +"is_edit integer,"
            +"is_top_module integer,"
            +"is_show_name integer,"
            +"homepage_type integer)";
}
