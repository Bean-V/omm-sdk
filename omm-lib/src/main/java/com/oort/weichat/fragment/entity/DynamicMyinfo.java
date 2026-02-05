package com.oort.weichat.fragment.entity;

import java.io.Serializable;
public class DynamicMyinfo implements Serializable {

        private int fans;

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getDynamics() {
        return dynamics;
    }

    public void setDynamics(int dynamics) {
        this.dynamics = dynamics;
    }

    public int getDynamic_grade1() {
        return dynamic_grade1;
    }

    public void setDynamic_grade1(int dynamic_grade1) {
        this.dynamic_grade1 = dynamic_grade1;
    }

    public int getDynamic_grade2() {
        return dynamic_grade2;
    }

    public void setDynamic_grade2(int dynamic_grade2) {
        this.dynamic_grade2 = dynamic_grade2;
    }

    public int getCollects() {
        return collects;
    }

    public void setCollects(int collects) {
        this.collects = collects;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    private int follows;
        private int dynamics;
        private int dynamic_grade1;
        private int dynamic_grade2;
        private int collects;
        private int likes;
        private int comments;
}
