package com.oortcloud.oort_zhifayi;

import java.io.Serializable;

public class Res<T> implements Serializable {

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    private int code;

    public String getMsg() {
        return msg;
    }
    private String msg;

    public int getCode() {
        return code;
    }


}
