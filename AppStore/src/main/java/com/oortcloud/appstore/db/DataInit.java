package com.oortcloud.appstore.db;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.bean.AppStatu;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.ModuleInfo;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.appstore.http.RxBus;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;




/**
 * @ProjectName: ommadvance
 * @FileName: DataInit.java
 * @Function:  对初始化数据
 * @Author: zhangzhijun / @CreateDate: 2020/5/18 5:16
 * @UpdateUser: 更新者 /@UpdateDate: 2020/5/18 5:16
 * @Version: 1.0
 */
public class DataInit {


    public static interface CallBack{
        public void requestAppsSuc();
        public void requestAppsFail( String msg);
    }

    public static CallBack getCallBack() {
        return callBack;
    }

    public static void setCallBack(CallBack callBack) {
        DataInit.callBack = callBack;
    }

    public  static CallBack callBack;

    public static HashMap<String,AppInfo> tmpAppInfos = new HashMap();

    public static HashMap<String,AppInfo> tmpAppStatuInfos = new HashMap();

    public static void saveAppInfo(AppInfo info){

        tmpAppInfos.put(info.getApppackage(),info);
    }



    public static void saveAppInfos(List<AppInfo> infos){
        if(infos == null){
            return;
        }

        for(AppInfo info : infos){
            saveAppInfo(info);
        }
        getAppinfos();
    }


    public static AppInfo getAppinfo(String packageName){

        return tmpAppStatuInfos.get(packageName);
    }

    public static List<AppInfo> getAppinfos(){
        ArrayList<AppInfo> apps = new ArrayList();
        if(tmpAppInfos != null) {
            apps.addAll(tmpAppInfos.values());
            ArrayList uids = new ArrayList();
            for(AppInfo in : apps){
                uids.add(in.getUid());
            }


            HttpRequestParam.statusList(mToken,uids).subscribe(new RxBus.BusObserver<String>(){
                @Override
                public void onNext(String s) {
                    Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                    if (result.isok()){


                        for(AppInfo info : result.getData().getList()){
                            tmpAppInfos.put(info.getApppackage(),info);
                            tmpAppStatuInfos.put(info.getApppackage(),info);
                        }
                        EventBus.getDefault().post("applyStatu");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    OperLogUtil.v("msg" , e.toString());
                }
            });
        }
        return apps;
    }


    public static ModuleInfo myModuleInfo;
    //初始化所有下载应用数据
    public static void installinit(String tableName , String token , String uuid){

            new Thread(() -> {
                HttpRequestParam.appInstallList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Result<Data<AppInfo>> result = new Gson().fromJson(s, new TypeToken<Result<Data<AppInfo>>>(){}.getType());
                        if (result.isok()){

                            storageAppInfo(tableName , result.getData().getList());

                            saveAppInfos(result.getData().getList());

                            if(callBack != null){
                                callBack.requestAppsSuc();
                            }
                        }else {
                            if(callBack != null){
                                callBack.requestAppsFail(result.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(callBack != null){
                            callBack.requestAppsFail(e.getLocalizedMessage());
                        }
                    }
                });

            }).start();

    }

    public static String mToken;

    //初始化模块数据
    public static void moduleinit(String token , String uuid , Handler handler){
        new Thread(()->{
            mToken = token;
            ModuleTableManager moduleTableManager =  ModuleTableManager.getInstance();
            if (moduleTableManager != null){
                moduleTableManager.createTable(DBConstant.MODULE_TABLE , TableDataStructure.MODULE_INFO);
                HttpRequestParam.moduleList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        OperLogUtil.v("msg"  ,s);
                        Result<Data<ModuleInfo<AppInfo>>> result = new Gson().fromJson(s,new TypeToken<Result<Data<ModuleInfo<AppInfo>>>>(){}.getType());
                        if (result.isok()){

                            FastSharedPreferences.get("httpRes").edit().putString("moduleList_" + uuid, s).apply();

                            List<ModuleInfo<AppInfo>> moduleInfoList = result.getData().getList();
                            if (moduleInfoList != null ) {
                                moduleTableManager.deleteData(DBConstant.MODULE_TABLE , null);

                                for (ModuleInfo moduleInfo : moduleInfoList) {

                                    moduleTableManager.insertData(DBConstant.MODULE_TABLE , moduleInfo);
                                    //初始化模块下App数据
                                    storageAppInfo(DBConstant.TABLE + moduleInfo.getModule_id() , moduleInfo.getApp_list());

                                    saveAppInfos(moduleInfo.getApp_list());


                                }

                                if(moduleInfoList.size() > 0){
                                    myModuleInfo = moduleInfoList.get(0);
                                }else{
                                    myModuleInfo = null;
                                }

                            }
                            if (handler != null){
                                handler.sendEmptyMessage(result.getCode());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        OperLogUtil.v("msg" , e.toString());
                    }
                });

            }

        }).start();


    }


    public static void getApps(String token , String uuid , Handler handler){
        new Thread(()->{

            ModuleTableManager moduleTableManager =  ModuleTableManager.getInstance();
            if (moduleTableManager != null){
                moduleTableManager.createTable(DBConstant.MODULE_TABLE , TableDataStructure.MODULE_INFO);
                HttpRequestParam.moduleList(token , uuid).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        OperLogUtil.v("msg"  ,s);
                        Result<Data<ModuleInfo<AppInfo>>> result = new Gson().fromJson(s,new TypeToken<Result<Data<ModuleInfo<AppInfo>>>>(){}.getType());
                        if (result.isok()){

                            List<ModuleInfo<AppInfo>> moduleInfoList = result.getData().getList();
                            if (moduleInfoList != null ) {
                                moduleTableManager.deleteData(DBConstant.MODULE_TABLE , null);

                                for (ModuleInfo moduleInfo : moduleInfoList) {

                                    moduleTableManager.insertData(DBConstant.MODULE_TABLE , moduleInfo);
                                    //初始化模块下App数据
                                    storageAppInfo(DBConstant.TABLE + moduleInfo.getModule_id() , moduleInfo.getApp_list());

                                    saveAppInfos(moduleInfo.getApp_list());
                                }

                                if(moduleInfoList.size() > 0){
                                    myModuleInfo = moduleInfoList.get(0);
                                }else{
                                    myModuleInfo = null;
                                }

                            }
                            if (handler != null){
                                handler.sendEmptyMessage(result.getCode());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        OperLogUtil.v("msg" , e.toString());
                    }
                });

            }

        }).start();


    }


    private static void storageAppInfo(String tableName , List<AppInfo> appInfoList){
        AppInfoManager mAppManager =  AppInfoManager.getInstance();
        if (mAppManager != null){
            mAppManager.createTable(tableName , TableDataStructure.APP_INFO);
            if (appInfoList != null ) {
                mAppManager.deleteAppInfo(tableName , null);

                for (AppInfo appInfo : appInfoList) {

                   // OperLogUtil.e("timetest", "storageAppInfo: " );
                    mAppManager.insertAppInfo(tableName , appInfo);
                }
            }
        }

    }


    public static void addToMyModule(AppInfo appInfo){

        if(!Constant.IsAddAPPSToHomeWhenUse){
            return;
        }

        if(myModuleInfo == null || appInfo == null){

            return;
        }

        String [] uncontains = {"com.jwb_home.oort","com.work_dynamics.oort","com.daily_activity.oort"};
        List uncontainsList =  Arrays.asList(uncontains);
        if(uncontainsList.contains(appInfo.getApppackage()) && Constant.NotAddSomeAPPSToHomeWhenUse){

            return;
        }


        if(AppStatu.homeRefrash == 1){
            AppStatu.homeRefrash = 2;
        }
        List uuids = new ArrayList();

        if(myModuleInfo.getApp_list() != null) {
            for (Object info : myModuleInfo.getApp_list()) {
                AppInfo i = (AppInfo) info;
                uuids.add(i.getUid());
            }
        }

        if(uuids.contains(appInfo.getUid())){
            return;
        }else {
            AppInfoManager.getInstance().insertAppInfo(DBConstant.TABLE + myModuleInfo.getModule_id(), appInfo);
            uuids.add(appInfo.getUid());
        }

        String json = new Gson().toJson(uuids);
        HttpRequestCenter.editModule(json, 1, myModuleInfo.getModule_id(), myModuleInfo.getModule_name()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result result = new Gson().fromJson(s, new TypeToken<Result>() {
                }.getType());
                if (result.isok()) {
                    EventBus.getDefault().post(new MessageRefrashHomeApp());
                    DataInit.moduleinit(AppStoreInit.getToken(), AppStoreInit.getUUID(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == result.getCode()) {
                            }
                        }
                    });
                }

            }
        });
    }



}
