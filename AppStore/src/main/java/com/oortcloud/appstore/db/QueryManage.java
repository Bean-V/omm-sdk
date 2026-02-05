package com.oortcloud.appstore.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ommadvance
 * @FileName: QueryManage.java
 * @Function: 查询后后对数据处理
 * @Author: zhangzhijun / @CreateDate: 2020/5/18 3:35
 * @Version: 1.0
 */
public class QueryManage {

        public static List<AppInfo> disposeAppInfoData(Cursor cursor){
            List<AppInfo> appInfos = new ArrayList<>();
            if (cursor != null){
                appInfos= new ArrayList<>();
                try {
                    if (cursor.moveToFirst()) {  //将数据的指针移到第一行的位置
                        do {

                            //遍历Cursor对象，取出数据并打印
                            //getCulumnIndex()：获得某一列在表中对应位置的索引，将这个索引传到相应的取值方法中
                            AppInfo appInfo = createAppInfo(cursor);
                            if (appInfo != null){
                                appInfos.add(appInfo);
                            }


                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    return appInfos;
                }catch (SQLiteException e){
                    Log.v("msg" , e.toString());

                }finally {
                    if (cursor != null){
                        cursor.close();
                    }
                }
            }
            return appInfos;
        }
    private static AppInfo createAppInfo(Cursor cursor){
        AppInfo appInfo = null;
        if (cursor != null){
            appInfo  = new AppInfo();
            appInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            appInfo.setCreated_on(cursor.getLong(cursor.getColumnIndex("created_on")));
            appInfo.setModified_on(cursor.getLong(cursor.getColumnIndex("modified_on")));
            appInfo.setUid(cursor.getString(cursor.getColumnIndex("uid")));
            appInfo.setSupplier_uid(cursor.getString(cursor.getColumnIndex("supplier_uid")));
            appInfo.setApp_id(cursor.getString(cursor.getColumnIndex("app_id")));
            appInfo.setApp_secret(cursor.getString(cursor.getColumnIndex("app_secret")));
            appInfo.setAppweburl(cursor.getString(cursor.getColumnIndex("appweburl")));
            appInfo.setAppentry(cursor.getString(cursor.getColumnIndex("appentry")));
            appInfo.setApplabel(cursor.getString(cursor.getColumnIndex("applabel")));
            appInfo.setApppackage(cursor.getString(cursor.getColumnIndex("apppackage")));
            appInfo.setVersion(cursor.getString(cursor.getColumnIndex("version")));
            appInfo.setVersioncode(cursor.getInt(cursor.getColumnIndex("versioncode")));
            appInfo.setApp_size(cursor.getString(cursor.getColumnIndex("app_size")));
            appInfo.setApk_url(cursor.getString(cursor.getColumnIndex("apk_url")));
            appInfo.setIcon_url(cursor.getString(cursor.getColumnIndex("icon_url")));
            appInfo.setScreenshot_url(cursor.getString(cursor.getColumnIndex("screenshot_url")));
            appInfo.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            appInfo.setEnabled(cursor.getInt(cursor.getColumnIndex("enabled")));
            appInfo.setOneword(cursor.getString(cursor.getColumnIndex("oneword")));
            appInfo.setVer_description(cursor.getString(cursor.getColumnIndex("ver_description")));
            appInfo.setPutaway_time(cursor.getString(cursor.getColumnIndex("putaway_time")));
            appInfo.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            appInfo.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
            appInfo.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            appInfo.setTerminal(cursor.getInt(cursor.getColumnIndex("terminal")));
            appInfo.setDescription(cursor.getString(cursor.getColumnIndex("developers")));
            appInfo.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
            appInfo.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            appInfo.setClassify(cursor.getString(cursor.getColumnIndex("classify")));
            appInfo.setForce_upgrade(cursor.getInt(cursor.getColumnIndex("force_upgrade")));
            appInfo.setRelyservice(cursor.getString(cursor.getColumnIndex("relyservice")));
            appInfo.setNetwork(cursor.getInt(cursor.getColumnIndex("network")));
            appInfo.setDevelop_time(cursor.getLong(cursor.getColumnIndex("develop_time")));
            appInfo.setRegion(cursor.getString(cursor.getColumnIndex("region")));
            appInfo.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            appInfo.setConstruction_unit(cursor.getString(cursor.getColumnIndex("construction_unit")));
            appInfo.setPrincipal(cursor.getString(cursor.getColumnIndex("principal")));
            appInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            appInfo.setDevelop_unit(cursor.getString(cursor.getColumnIndex("develop_unit")));
            appInfo.setOperate_phone(cursor.getString(cursor.getColumnIndex("operate_phone")));
            appInfo.setInstall_num(cursor.getInt(cursor.getColumnIndex("install_num")));
            appInfo.setSend_num(cursor.getInt(cursor.getColumnIndex("send_num")));
            appInfo.setDownload_num(cursor.getInt(cursor.getColumnIndex("download_num")));
            appInfo.setUserang_enable(cursor.getInt(cursor.getColumnIndex("userang_enable")));
            appInfo.setUserang_depart(cursor.getString(cursor.getColumnIndex("userang_depart")));
            appInfo.setUserang_person(cursor.getString(cursor.getColumnIndex("userang_person")));
            appInfo.setHelptext(cursor.getString(cursor.getColumnIndex("helptext")));
            appInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
            appInfo.setPath(cursor.getString(cursor.getColumnIndex("path")));

        }
        return appInfo;
    }

    public static List<ModuleInfo> disposeModuleData(Cursor cursor){
        List<ModuleInfo> moduleInfos = new ArrayList<>();
        if (cursor != null){
            moduleInfos= new ArrayList<>();
            try {
                if (cursor.moveToFirst()) {  //将数据的指针移到第一行的位置
                    do {

                        //遍历Cursor对象，取出数据并打印
                        //getCulumnIndex()：获得某一列在表中对应位置的索引，将这个索引传到相应的取值方法中
                        ModuleInfo moduleInfo = createModuleInfo(cursor);
                       if (moduleInfo != null){
                           moduleInfos.add(moduleInfo);
                       }


                    } while (cursor.moveToNext());
                }
                cursor.close();
                return moduleInfos;
            }catch (SQLiteException e){
                Log.v("msg" , e.toString());

            }finally {
                if (cursor != null){
                    cursor.close();
                }
            }

        }
        return moduleInfos;
    }


    private static ModuleInfo createModuleInfo(Cursor cursor){
        ModuleInfo moduleInfo = null;
        if (cursor != null){
            moduleInfo  = new ModuleInfo();
            moduleInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            moduleInfo.setCreated_on(cursor.getLong(cursor.getColumnIndex("created_on")));
            moduleInfo.setModified_on(cursor.getLong(cursor.getColumnIndex("modified_on")));
            moduleInfo.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
            moduleInfo.setModule_id(cursor.getString(cursor.getColumnIndex("module_id")));
            moduleInfo.setModule_name(cursor.getString(cursor.getColumnIndex("module_name")));
            moduleInfo.setApp_uids(cursor.getString(cursor.getColumnIndex("app_uids")));
            moduleInfo.setModule_order(cursor.getInt(cursor.getColumnIndex("module_order")));
            moduleInfo.setHomepage_type(cursor.getInt(cursor.getColumnIndex("homepage_type")));
            moduleInfo.setIs_edit(cursor.getInt(cursor.getColumnIndex("is_edit")));
            moduleInfo.setIs_show_name(cursor.getInt(cursor.getColumnIndex("is_show_name")));
            moduleInfo.setIs_top_module(cursor.getInt(cursor.getColumnIndex("is_top_module")));

        }
        return moduleInfo;
    }
}
