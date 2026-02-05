package com.oort.weichat.fragment.entity;

import java.io.Serializable;

public class OORTFile implements Serializable {
        /**
         * url : http://oort.oortcloudsmart.com:31610/oort/oortwj1/group1/default/20230724/10/09/4/10000004.jpg
         * md5 : 94efeb5e8d9d61977dff18baf478078e
         * path : /group1/default/20230724/10/09/4/10000004.jpg
         * domain : http://go-fastdfs-management:8080
         * scene : default
         * size : 36823
         * mtime : 1690164562
         * scenes : default
         * retmsg :
         * retcode : 0
         * src : /group1/default/20230724/10/09/4/10000004.jpg
         * duration : 0
         */

        private String url;
        private String md5;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMtime() {
        return mtime;
    }

    public void setMtime(int mtime) {
        this.mtime = mtime;
    }

    public String getScenes() {
        return scenes;
    }

    public void setScenes(String scenes) {
        this.scenes = scenes;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private String path;
        private String domain;
        private String scene;
        private int size;
        private int mtime;
        private String scenes;
        private String retmsg;
        private int retcode;
        private String src;
        private int duration;

}
