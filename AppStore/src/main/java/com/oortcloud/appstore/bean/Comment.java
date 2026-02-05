package com.oortcloud.appstore.bean;

import java.io.Serializable;

/**
 * @filename:
 * @function：
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/28 16:43
 */
public class Comment implements Serializable {
    private int id;
    private long created_on;
    private long modified_on;
    //评论id
    private String reply_id;
    // 目标uid
    private String uid;
    //评论父Id
    private String parent_id;

    private String uuid;
    //评论内容
    private String content;
    //点赞数
    private int likeNum;
    //评论情况
    private int status;
    //评论的头像地址
    private String portrait;
    //手机号
    private String phone;
    //评论的名字
    private String name;
    //评论的名字
    private float score;

    private String extend1;
    private String extend2;
    private String extend3;
    private int extend4;
    private int extend5;

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

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public int getExtend4() {
        return extend4;
    }

    public void setExtend4(int extend4) {
        this.extend4 = extend4;
    }

    public int getExtend5() {
        return extend5;
    }

    public void setExtend5(int extend5) {
        this.extend5 = extend5;
    }

}
