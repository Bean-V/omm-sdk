package com.oortcloud.appstore.db;

import android.text.TextUtils;

import com.oortcloud.appstore.bean.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AppStore-master
 * @FileName: ModuleTableManager.java
 * @Function:  管理mine模块下的所有数据库表table
 * @Author: zhangzhijun / @CreateDate: 20/03/08 19:22
 * @Version: 1.0
 */
public class ModuleTableManager {
    private static ModuleTableManager mTableManager;
    private  DBManager mDBManager;



    private ModuleTableManager(){}{
        mDBManager = DBManager.getInstance();
    }

    public static ModuleTableManager getInstance(){
        if (mTableManager  == null){
            synchronized (ModuleTableManager.class){
                if (mTableManager == null){
                    mTableManager = new ModuleTableManager();
                }
                return mTableManager;
            }
        }
        return mTableManager;
    }

    public void createTable(String tableName ,String dataStructure){
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
     * 有表时应用添加第一个表中
     * @param meduleInfo
     */
    public void insertData(String tableName , ModuleInfo meduleInfo){
        if (!TextUtils.isEmpty(tableName) && meduleInfo != null){

            if (isContains(tableName , meduleInfo.getModule_id())== null){
                mDBManager.insertData(tableName , DBConstant.createContentValues(meduleInfo));

            }

        }

    }

    public void deleteData(String tableName , ModuleInfo moduleInfo){
        if (!TextUtils.isEmpty(tableName)){
            if (moduleInfo != null){
                mDBManager.deleteData(tableName , moduleInfo.getId());
            }else {
                mDBManager.deleteData(tableName , 0);
            }

        }


    }


    public List queryData(String tableName){
        if (!TextUtils.isEmpty(tableName)){
            return QueryManage.disposeModuleData(mDBManager.queryData(tableName)) ;
        }
        return new ArrayList();
    }



    public void upDate(String tableName , ModuleInfo moduleInfo){
        if (!TextUtils.isEmpty(tableName)&& moduleInfo != null){
            ModuleInfo oldModuleInfo = isContains(tableName ,moduleInfo.getModule_id());

            if (oldModuleInfo != null){
                mDBManager.updateData(tableName ,oldModuleInfo.getId() , DBConstant.createContentValues(moduleInfo) );
            }else {
                insertData(tableName , moduleInfo);
            }
        }
    }



    //表中是否已存在该应用
    public ModuleInfo isContains(String tableName , String moduleId){
        if (!TextUtils.isEmpty(tableName) && !TextUtils.isEmpty(moduleId)){
            List<ModuleInfo> appInfos  = queryData(tableName);
            if (appInfos != null && appInfos.size() > 0){
                for (ModuleInfo info : appInfos){
                    if (moduleId.equals(info.getModule_id())){
                        return info;
                    }
                }
            }
        }

        return null;
    }


}
