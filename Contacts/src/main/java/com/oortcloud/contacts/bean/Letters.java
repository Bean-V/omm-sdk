package com.oortcloud.contacts.bean;

import java.io.Serializable;

/**
 * @ProjectName: omm-master
 * @FileName: Letters.java
 * @Function: 字母处理类
 * @Author: zhangzhijun / @CreateDate: 20/03/16 21:58
 * @Version: 1.0
 */
public class Letters  implements Serializable {
    private String letters;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
