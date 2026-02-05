package com.oort.weichat.fragment.entity;

import java.io.Serializable;

public class DynamicTopic implements Serializable {

    private int oort_dcount;

    public int getOort_dcount() {
        return oort_dcount;
    }

    public void setOort_dcount(int oort_dcount) {
        this.oort_dcount = oort_dcount;
    }

    public int getOort_top() {
        return oort_top;
    }

    public void setOort_top(int oort_top) {
        this.oort_top = oort_top;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getOort_name() {
        return oort_name;
    }

    public void setOort_name(String oort_name) {
        this.oort_name = oort_name;
    }

    public String getOort_tuuid() {
        return oort_tuuid;
    }

    public void setOort_tuuid(String oort_tuuid) {
        this.oort_tuuid = oort_tuuid;
    }

    private int oort_top;
    private int created_at;
    private String oort_name;
    private String oort_tuuid;



}
