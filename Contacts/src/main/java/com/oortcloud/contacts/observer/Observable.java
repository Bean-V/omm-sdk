package com.oortcloud.contacts.observer;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/6 17:21
 * @version： v1.0
 * @function： 发布
 */
public interface Observable<T>{

     void addBuyUser(T user);

     void deleteBuyUser(T user);

     void notifyMsg();

}
