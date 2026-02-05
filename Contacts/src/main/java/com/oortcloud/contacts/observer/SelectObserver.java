package com.oortcloud.contacts.observer;




import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/6 18:03
 * @version： v1.0
 * @function： 处理上传下载数量
 */
public class SelectObserver implements Observable<Observer> {

    private static SelectObserver mInstance;

    private static List<Observer> mUserList ;

    public static SelectObserver getInstance(){
        if (mInstance == null){

            synchronized (SelectObserver.class){
                if (mInstance == null){
                    mInstance = new SelectObserver();

                }
            }

        }
        return mInstance;
    }

    private SelectObserver(){
        mUserList = new ArrayList();
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
    public void notifyMsg() {
        for (Observer user : mUserList) {
            user.notifyMsg();
        }
    }

}
