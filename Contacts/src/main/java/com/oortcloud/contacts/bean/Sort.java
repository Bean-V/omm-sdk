package com.oortcloud.contacts.bean;

import java.io.Serializable;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/7/1 16:31
 */
public abstract class Sort implements Serializable {

     public  abstract int getSort();

    private String letters;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
