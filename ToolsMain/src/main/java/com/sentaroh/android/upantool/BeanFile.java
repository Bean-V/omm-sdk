package com.sentaroh.android.upantool;

public class BeanFile {
    private String name;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    private int resId;

    public BeanFile(String name, String path, String size, String type, int typeIcon) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.type = type;
        this.typeIcon = typeIcon;
    }

    public BeanFile(int iId,String name, String path, String size, String type, int typeIcon) {
            this.resId = iId;
            this.name = name;
            this.path = path;
            this.size = size;
            this.type = type;
            this.typeIcon = typeIcon;
    }

    public BeanFile(String name, String path, String size, String type, int typeIcon,Object obj) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.type = type;
        this.typeIcon = typeIcon;
        this.obj = obj;
    }
    private String path;
    private String size;
    private String type;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    private Object obj;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(int typeIcon) {
        this.typeIcon = typeIcon;
    }

    private int typeIcon;


}
