package com.oortcloud.clouddisk.observer;

import com.oortcloud.clouddisk.db.DBManager;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;
import com.oortcloud.clouddisk.widget.navigationbar.DefaultNavigationBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/6 18:03
 * @version： v1.0
 * @function： 处理上传下载数量
 */
public class CountObserver implements Observable<Observer> {

    private static CountObserver mInstance;

    private static List<Observer> mUserList ;

    public static CountObserver getInstance(){
        if (mInstance == null){

            synchronized (CountObserver.class){
                if (mInstance == null){

                    mInstance = new CountObserver();
                    mUserList = new ArrayList();
                }
            }

        }
        return mInstance;
    }

    @Override
    public void addBuyUser(Observer user) {
        mUserList.add(user);
    }

    @Override
    public void deleteBuyUser(Observer user) {
        mUserList.remove(user);
    }

    @Override
    public void notify(int msg) {
        for (Observer user : mUserList) {
            user.notifyMsg(msg);
        }
    }

    public void sendNotify() {
        int count = 0 ;
        DBManager dbManager = DBManager.getInstance();
        List<UploadInfo> uploadList = dbManager.queryUp("" ,"");
        if (uploadList != null){
            for (UploadInfo uploadInfo : uploadList){
                if (uploadInfo.getStatus() != Status.SUCCESS){
                    count++;
                }
            }
        }

        List<DownLoadInfo> downList = dbManager.queryDown("" ,"");

        if (downList != null){
            for (DownLoadInfo downLoadInfo : downList){
                if (downLoadInfo.getStatus() != Status.SUCCESS){
                    count++;
                }
            }
        }

        this.notify(count);
    }

}
