package com.oortcloud.coo.bean;

import java.util.List;

public class Result<T> {
    /**
     * code : 0
     * success : true
     * data : {"current":0,"total":0,"records":[{"id":0,"tenantId":"","alertLevel":"","receivingAlertNumber":"","receivingOfficer":"","receivingOfficerId":"","receivingOfficerPhone":"","responseUnit":"","alertStatus":"","alertCategory":"","alertType":"","alertSubType":"","incidentLocation":"","alertContent":"","thirdResponseUnit":"","secondResponseUnit":"","firstResponseUnit":"","sessionId":"","groupId":""}],"pages":0,"size":0}
     * msg :
     */

    private int code;
    private boolean success;
    private T data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }




}
