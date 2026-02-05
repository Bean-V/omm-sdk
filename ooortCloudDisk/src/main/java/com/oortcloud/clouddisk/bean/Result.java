package com.oortcloud.clouddisk.bean;


import java.io.Serializable;

/**
 * @filename:
 * @function：
 * @version：
 * @author: zhangzhijun
 * @date: 2019/12/31 15:44
 */
public class Result<T> implements Serializable {
    private int code;
    private T data;
    private String msg;

    private int resultCode;



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public boolean isOk() {
        return code == 200;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
