package com.oortcloud.clouddisk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @filename:
 * @function： 获取文件目录
 * @version： v1.0
 * @author: zzj/@date: 2020/6/23 11:48
 *
 */
public class DirData<T> implements Serializable {

    private int counts;

    private String dir;
    private List<T> list;
    private int page;
    private int pages;
    private int pagesize;
    //设置的存储配额 -1:默认 其他(比如100M 1G)
    private String quota;
    //已使用的存储大小
    private int used;

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }
}
