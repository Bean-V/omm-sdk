package com.oortcloud.clouddisk.bean;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/28 19:06
 * @version： v1.0
 * @function：
 */
public class EventMessage<T> {

    private List<T> list;
    private DirData<FileInfo> data;
    private String dir;


   public EventMessage(String dir , DirData data){
        this.dir = dir;
        this.data = data;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public DirData<FileInfo> getData() {
        return data;
    }

    public void setData(DirData<FileInfo> data) {
        this.data = data;
    }
}
