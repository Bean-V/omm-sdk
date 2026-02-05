package com.oortcloud.appstore.db;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.basemodule.CommonApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: AppStore-master
 * @FileName: AppInfoManager.java
 * @Function: app管理类 处理应用更新 下载 还是打开等状态
 * @Author: zhangzhijun / @CreateDate: 20/03/09 10:02
 * @Version: 1.0
 */
public class AppInfoManager {

    private  static DBManager mDBManager;
    private static AppInfoManager mAppManager;

    private AppInfoManager(){
        mDBManager = DBManager.getInstance();
    }
    public static AppInfoManager getInstance(){
        if (mAppManager  == null){
            synchronized (AppInfoManager.class){
                if (mAppManager == null){
                    mAppManager = new AppInfoManager();
                }
                return mAppManager;
            }
        }
        return mAppManager;
    }

    /**
     * @param tableName
     */
    public void createTable(String tableName , String dataStructure ){
        if (!TextUtils.isEmpty(tableName) && !TextUtils.isEmpty(dataStructure)){
            mDBManager.createTable(tableName  , dataStructure );
        }


    }

    /**
     * 删除表
     * @param tableName
     */
    public void deleteTable(String tableName ){
        if (!TextUtils.isEmpty(tableName)){
            mDBManager.deleteTable(tableName);
        }
    }
    /**
     * 当没有表时创建表
     * 有表时应用添加第一个表中
     * @param appInfo
     */
    public void insertAppInfo(String tableName ,AppInfo appInfo){
        if (!TextUtils.isEmpty(tableName)){
            if (appInfo != null){
                Log.e("timetest", "isContains: " );
                if (isContains(tableName , appInfo.getApppackage())== null){
                    Log.e("timetest", "insertData: " );

                    mDBManager.insertData(tableName , DBConstant.createContentValues(appInfo));

                }else {
                    upDateAppInfo(tableName , appInfo);
                }
            }
        }

    }

    public void deleteAppInfo(String tableName , AppInfo appInfo){
        if (!TextUtils.isEmpty(tableName)){
            if (appInfo != null){
                mDBManager.deleteData(tableName , appInfo.getApppackage());
            }else {
                mDBManager.deleteData(tableName , "");
            }

        }


    }


  public List queryAppInfo(String tableName){
        if (!TextUtils.isEmpty(tableName)){
            return QueryManage.disposeAppInfoData(mDBManager.queryData(tableName)) ;
        }
        return new ArrayList<>();
    }



    public void upDateAppInfo(String tableName , AppInfo appInfo){
        if (!TextUtils.isEmpty(tableName)&& appInfo != null){
            AppInfo oldAppInfo = isContains(tableName ,appInfo.getApppackage());

            if (oldAppInfo != null){
                mDBManager.updateData(tableName ,oldAppInfo.getApppackage() , DBConstant.createContentValues(appInfo) );
            }
        }
    }

    //表中是否已存在该应用    包名 版本号
    public boolean isContains(AppInfo appInfo){
        if (!TextUtils.isEmpty(DBConstant.INSTALL_TABLE) && appInfo != null){
            List<AppInfo> appInfos  = queryAppInfo(DBConstant.INSTALL_TABLE);
            if (appInfos != null && appInfos.size() > 0){
                switch (appInfo.getTerminal()){
                    case 0 :
                    case 2 :


                        if(appInfo.getTerminal() == 0){
                            return checkInstalled(CommonApplication.getAppContext(),appInfo.getApppackage());
                        }
                        if (appInfos != null && appInfos.size() > 0){
                            for (AppInfo info : appInfos){
                                if (appInfo.getApppackage().equals(info.getApppackage()) ){
                                    return true;
                                }
                            }
                            return false;
                        }
                        break;
                    case 1 :
                        if (appInfos != null && appInfos.size() > 0){
                            for (AppInfo info : appInfos){
                                if (appInfo.getApppackage().equals(info.getApppackage()) && appInfo.getVersioncode() == info.getVersioncode()){
                                    return true;
                                }
                            }
                            return false;
                        }
                        break;

                }
            }


        }
        return false;
    }


    public boolean isInstalled(AppInfo appInfo){
        if (!TextUtils.isEmpty(DBConstant.INSTALL_TABLE) && appInfo != null){
            List<AppInfo> appInfos  = queryAppInfo(DBConstant.INSTALL_TABLE);
            if (appInfos != null && appInfos.size() > 0){
                switch (appInfo.getTerminal()){
                    case 0 :
                    case 2 :



                        if (appInfos != null && appInfos.size() > 0){
                            for (AppInfo info : appInfos){
                                if (appInfo.getApppackage().equals(info.getApppackage()) ){
                                    return true;
                                }
                            }
                            return false;
                        }
                        break;
                    case 1 :
                        if (appInfos != null && appInfos.size() > 0){
                            for (AppInfo info : appInfos){
                                if (appInfo.getApppackage().equals(info.getApppackage()) && appInfo.getVersioncode() == info.getVersioncode()){
                                    return true;
                                }
                            }
                            return false;
                        }
                        break;

                }
            }


        }
        return false;
    }


    Map<String,List<AppInfo>> tmpMap = new HashMap<>();
    //表中是否已存在该应用
    public AppInfo isContains(String tableName , String packageName){
        if (!TextUtils.isEmpty(tableName) && !TextUtils.isEmpty(packageName)){

            List<AppInfo> appInfos  = tmpMap.get(tableName);
            if(appInfos == null){
                appInfos  = queryAppInfo(tableName);
                if(appInfos.size() > 0){
                    tmpMap.put(tableName,appInfos);
                }
            }
            if (appInfos != null && appInfos.size() > 0){
                for (AppInfo info : appInfos){
                    if (packageName.equals(info.getApppackage())){
                        return info;
                    }
                }
            }
        }

        return null;
    }



    public static boolean checkInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception x) {
            return false;
        }
        return true;
    }

}
