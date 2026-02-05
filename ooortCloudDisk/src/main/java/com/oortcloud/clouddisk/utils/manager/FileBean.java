package com.oortcloud.clouddisk.utils.manager;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/24 19:00
 * @version： v1.0
 * @function：
 */
public class FileBean {
    /** 文件的路径*/
    public String path;
    /**文件图片资源的id，drawable或mipmap文件中已经存放doc、xml、xls等文件的图片*/
    public int iconId;

    public FileBean(String path, int iconId) {
        this.path = path;
        this.iconId = iconId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
