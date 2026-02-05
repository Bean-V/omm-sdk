package com.oort.weichat.fragment.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/1/15 20:10
 */
public class ResData<T> implements Serializable {
    private int count;
    private List<T> lists;
    private T data;
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getLists() {
        return lists;
    }

    public void setLists(List<T> lists) {
        this.lists = lists;
    }

    //精选应用
    private List<T> excellent_list;
    //装机必备
    private List<T> much_use_list;
    //大家都在用
    private List<T> must_list;

    public List<T> getExcellent_list() {
        return excellent_list;
    }

    public void setExcellent_list(List<T> excellent_list) {
        this.excellent_list = excellent_list;
    }

    public List<T> getMuch_use_list() {
        return much_use_list;
    }

    public void setMuch_use_list(List<T> much_use_list) {
        this.much_use_list = much_use_list;
    }

    public List<T> getMust_list() {
        return must_list;
    }

    public void setMust_list(List<T> must_list) {
        this.must_list = must_list;
    }
    //分类
    private List<T> class_list;

    public List<T> getClass_list() {
        return class_list;
    }

    public void setClass_list(List<T> class_list) {
        this.class_list = class_list;
    }

    //分类ALL_APP
    private List<T> app_list;

    public List<T> getApp_list() {
        return app_list;
    }

    public void setApp_list(List<T> app_list) {
        this.app_list = app_list;
    }


    //模块
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }


    private T userInfo;

    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }

    private List<T> dept;

    public List<T> getDept() {
        return dept;
    }

    public void setDept(List<T> dep) {
        this.dept = dep;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
