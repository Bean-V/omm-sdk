package com.oortcloud.appstore.db;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.appstore.bean.ClassifyInfo;
import com.oortcloud.appstore.bean.Data;
import com.oortcloud.appstore.bean.Result;
import com.oortcloud.appstore.http.HttpRequestCenter;
import com.oortcloud.appstore.http.RxBus;

import java.util.List;

/**
 * @filename:
 * @function：管理分类类型
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/4/8 19:06
 */
public class ClassifyManager {
    private static SharedPreferenceManager mPreferenceManager;
    private static ClassifyManager mClassifyManager;
    private ClassifyManager(){
        mPreferenceManager = SharedPreferenceManager.getInstance();
    }
    public static ClassifyManager getInstance(){
        if (mClassifyManager  == null){
            synchronized (AppInfoManager.class){
                if (mClassifyManager == null){
                    mClassifyManager = new ClassifyManager();
                }
                return mClassifyManager;
            }
        }
        return mClassifyManager;
    }

    public static void initClassify(){
        ClassifyManager.getInstance();

                HttpRequestCenter.postClassifyList().subscribe(new RxBus.BusObserver<String>(){
                    @Override
                    public void onNext(String s) {

                        Result<Data<ClassifyInfo>> result = new Gson().fromJson(s,  new TypeToken<Result<Data<ClassifyInfo>>>(){}.getType());
                        if (result.isok()){
                            List<ClassifyInfo> classifyInfos = result.getData().getLists ();
                            if (classifyInfos != null){
                                for (ClassifyInfo classifyInfo : classifyInfos){
                                    mPreferenceManager.putString(classifyInfo.getUid() , classifyInfo.getName());
                                }

                            }
                        }
                    }
                });

    }

    public static String getClassify(String classifyUid){
        if (!TextUtils.isEmpty(classifyUid)){
            if (mPreferenceManager != null){
                return mPreferenceManager.getString(classifyUid);
            }

        }
        return "";
    }
}
