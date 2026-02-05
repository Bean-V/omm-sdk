package com.oortcloud.contacts.bean;

import com.oortcloud.contacts.bean.omm.User;

import java.util.List;

/**
 * @ProjectName:
 * @FileName: MessageEvent.java
 * @Function: Event事件消息实体类
 * @Author: zzh / @CreateDate: 20/03/13 05:29
 * @Version: 1.0
 */

public class EventMessage<T>{

    private String message;
    private List<T> list ;
    private Department department;
    private DeptInfo deptInfo;
    private String dataType;
    private T t;

    //区分返回部门还是人员 0表示部门 1表示人员 2表示在创建群选择群成员
    private int type = -1;

    public EventMessage(String message) {
        this.message = message;
    }
    public EventMessage(String dataType ,Department department) {
        this.dataType = dataType;
        this.department = department;
    }

    public EventMessage(String dataType , DeptInfo deptInfo) {
        this.dataType = dataType;
        this.deptInfo = deptInfo; }

    public EventMessage(List<T> list) {
        this("" , list);
    }
    public EventMessage(String dataType, List<T> list) {
       this(-1, dataType, list);
    }
    public EventMessage( int type , String dataType ,List<T> list) {
        this.type = type;
        this.dataType = dataType;
        this.list = list;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DeptInfo getDeptInfo() {
        return deptInfo;
    }

    public void setDeptInfo(DeptInfo deptInfo) {
        this.deptInfo = deptInfo;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    private User user;

    public EventMessage( T t){
        this.t = t;
    }

    public EventMessage(String type, T t){
        this.dataType = type;
        this.t = t;
    }

    public T getT() {
        return t;
    }
}
