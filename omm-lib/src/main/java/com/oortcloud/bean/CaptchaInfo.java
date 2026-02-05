package com.oortcloud.bean;

import java.io.Serializable;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/7/28 17:05
 * @version： v1.0
 * @function：
 */
public class CaptchaInfo implements Serializable {
    private String slideID;
    private int ypos;

    private String CaptchaID;

    public String getSlideID() {
        return slideID;
    }

    public void setSlideID(String slideID) {
        this.slideID = slideID;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public String getCaptchaID() {
        return CaptchaID;
    }

    public void setCaptchaID(String captchaID) {
        CaptchaID = captchaID;
    }
}
