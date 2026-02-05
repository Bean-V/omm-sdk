package com.oortcloud.contacts.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/4/22 18:16
 * @version： v1.0
 * @function：
 */
public class SelectAddObserver  implements Observable<Observer>{

    private static SelectAddObserver mInstance;

    private static List<Observer> mUserList ;

    public static SelectAddObserver getInstance(){
        if (mInstance == null){

            synchronized (SelectObserver.class){
                if (mInstance == null){
                    mInstance = new SelectAddObserver();

                }
            }

        }
        return mInstance;
    }

    private SelectAddObserver(){
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
