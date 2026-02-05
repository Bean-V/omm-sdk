package com.oortcloud.bean;

import java.io.Serializable;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 15:29
 * @version： v1.0
 * @function：
 */
public class Data<T> implements Serializable {
    private int count;
    private T lists;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getLists() {
        return lists;
    }

    public void setLists(T lists) {
        this.lists = lists;
    }
}
