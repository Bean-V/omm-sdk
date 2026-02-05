package com.oortcloud.appstore.bean;

import java.io.Serializable;

/**
 * @filename:
 * @function： 评分
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/7/3 14:52
 */
public class Grade implements Serializable {
    private int id ;
    private long created_on;
    private long modified_on;
    private String uid;
    private float score;
    private String extend1;
    private int extend4;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreated_on() {
        return created_on;
    }

    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }

    public long getModified_on() {
        return modified_on;
    }

    public void setModified_on(long modified_on) {
        this.modified_on = modified_on;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public int getExtend4() {
        return extend4;
    }

    public void setExtend4(int extend4) {
        this.extend4 = extend4;
    }
}
