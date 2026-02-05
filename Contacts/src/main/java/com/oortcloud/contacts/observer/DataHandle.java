package com.oortcloud.contacts.observer;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @filename:
 * @author: zzj/@date: 2021/4/22 10:30
 * @version： v1.0
 * @function：
 */
public class DataHandle {

    private static DataHandle mInstance;
    private  Observable mObservable;

    private  ArrayList<UserInfo> mUserInfoData ;
    private  ArrayList<Department> mDepartmentData ;
    private  ArrayList<Activity>  activities ;
    //记录checkBox状态
    private Map<String, Boolean> mMap;

    public static DataHandle getInstance(){
        if (mInstance == null){

            synchronized (SelectObserver.class){
                if (mInstance == null){
                    mInstance = new DataHandle();
                }
            }

        }
        return mInstance;
    }
    private DataHandle(){
        mUserInfoData =  new ArrayList();
        mDepartmentData =  new ArrayList();
        activities =  new ArrayList();
        mObservable = SelectObserver.getInstance();
        mMap = new HashMap<>();
    }
    public void addUser(UserInfo userInfo){
        if (mUserInfoData != null){
            mUserInfoData.add(userInfo);
            mMap.put(userInfo.getOort_uuid() ,true);
        }
        mObservable.notifyMsg();
    }

    public void addUser(List<UserInfo> data){

        if (mUserInfoData != null){
            data.removeAll(mUserInfoData);
            mUserInfoData.addAll(data);

            for (UserInfo userInfo : data){

                mMap.put(userInfo.getOort_uuid() , true);
            }
        }
        mObservable.notifyMsg();

    }

    public void removeUser(UserInfo userInfo){
        if (mUserInfoData != null){
            mUserInfoData.remove(userInfo);
            mMap.remove(userInfo.getOort_uuid() );
        }
        mObservable.notifyMsg();

    }

    public void removeUser(List<UserInfo> data){
        if (mUserInfoData != null){
            mUserInfoData.removeAll(data);
            for (UserInfo userInfo : data){
                mMap.remove(userInfo.getOort_uuid() );
            }
        }
        mObservable.notifyMsg();

    }

    public void addDepartment(Department department){
        if (mUserInfoData != null){
            mDepartmentData.add(department);
            mMap.put(department.getOort_dcode() ,true);
        }
    }
    public void removeDepartment(Department department){
        if (mUserInfoData != null){
            mDepartmentData.remove(department);
            mMap.remove(department.getOort_dcode());
        }
    }
    public List<Department> getDepartData(){
        return mDepartmentData;
    }
    public List<UserInfo> getUserData(){
        return mUserInfoData;
    }
    public Map<String, Boolean> getMap(){
        return mMap;
    }

    public String toJson(){
        String json = new Gson().toJson(mUserInfoData);
        Log.v("msg" , json);
        removeAllActivity();
        return json;
    }
    public void clear(){
        mUserInfoData.clear();
        mDepartmentData.clear();
        mMap.clear();

    }

    public void addActivity(Activity activity){
        activities.add(activity);
    }
    public void removeActivity(Activity activity){
        activities.remove( activity);
    }
    public void removeAllActivity(){
      for (Activity activity : activities){
          activity.finish();
      }
    }

}
