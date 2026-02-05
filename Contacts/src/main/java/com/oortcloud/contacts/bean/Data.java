package com.oortcloud.contacts.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 15:14
 */

/**
 * 用户 部门数据类
 *
 */
public class Data<T> implements Serializable {
    private List<Department> dept;
    private List<UserInfo> user;
    private T deptInfo;
    private T userInfo;
    private List<T> list;

    public List<Department> getDept() {
        return dept;
    }

    public void setDept(List<Department> dept) {
        this.dept = dept;
    }

    public List<UserInfo> getUser() {
        return user;
    }

    public void setUser(List<UserInfo> user) {
        this.user = user;
    }

    public T getDeptInfo() {
        return deptInfo;
    }

    public void setDeptInfo(T deptInfo) {
        this.deptInfo = deptInfo;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }
}
