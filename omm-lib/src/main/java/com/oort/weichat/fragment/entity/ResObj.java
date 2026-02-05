package com.oort.weichat.fragment.entity;

import java.io.Serializable;

public class ResObj<T> extends Res implements Serializable {


    public void setData(T data) {
        this.data = data;
    }
    public T getData() {
        return data;
    }
    private T data;
}
