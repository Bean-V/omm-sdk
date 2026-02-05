package com.oort.weichat.fragment.entity;

/**
 * 子项数据的实体类
 */
public class ChildEntity {

    private String child;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    private Object obj;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public ChildEntity(String child) {
        this.child = child;
    }
    public ChildEntity(String child,String url) {
        this.child = child;
        this.url = url;
    }
    public ChildEntity(String child,String url,Object obj) {
        this.child = child;
        this.url = url;
        this.obj = obj;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }
}
