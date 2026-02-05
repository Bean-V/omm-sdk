package com.oort.weichat.fragment.vs.bean;


/**
 * tid : 71be0803-e0a9-40fc-bf28-d5ae62351c6a
 * user_id : efb21f7d-128c-49f0-9f6b-d339bcd5e60c
 * name : 默认
 * sort : 0
 * is_open : 1
 * tag_type : 1
 */

public  class Tag {


  private String tid;
  private String user_id;
  private String name;
  private int sort;
  private int is_open;
  private int tag_type;

  public String getTid() {
   return tid;
  }

  public void setTid(String tid) {
   this.tid = tid;
  }

  public String getUser_id() {
   return user_id;
  }

  public void setUser_id(String user_id) {
   this.user_id = user_id;
  }

  public String getName() {
   return name;
  }

  public void setName(String name) {
   this.name = name;
  }

  public int getSort() {
   return sort;
  }

  public void setSort(int sort) {
   this.sort = sort;
  }

  public int getIs_open() {
   return is_open;
  }

  public void setIs_open(int is_open) {
   this.is_open = is_open;
  }

  public int getTag_type() {
   return tag_type;
  }

  public void setTag_type(int tag_type) {
   this.tag_type = tag_type;
  }
}
