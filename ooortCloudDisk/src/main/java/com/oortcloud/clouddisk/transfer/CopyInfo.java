package com.oortcloud.clouddisk.transfer;

import java.io.StringReader;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/28 14:38
 * @version： v1.0
 * @function： 记录复制后文件关系信息  处理复制后相同文件重复下载问题
 */
public class CopyInfo {

    private String file_name;//文件名称
    //当前路径
    private String dir = "";
    //当前路径
    private String file_path = "";
    //父上传文件路径
    private String parent_Dir = "";
    //父存储路径
    private String parent_path = "";

    public CopyInfo(){}

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getParent_Dir() {
        return parent_Dir;
    }

    public void setParent_Dir(String parent_Dir) {
        this.parent_Dir = parent_Dir;
    }

    public String getParent_path() {
        return parent_path;
    }

    public void setParent_path(String parent_path) {
        this.parent_path = parent_path;
    }
}
