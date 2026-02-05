package com.oortcloud.clouddisk.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;

import java.util.Objects;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/13 17:35
 * @version： v1.0
 * @function： 上传后文件信息
 */
public class FileInfo implements Parcelable {
    //文件上传时间戳
    private int ctime;
    //是否文件夹 0:否 1:是
    private int is_dir;
    //文件mimetype
    private String mime_type;
    //文件修改时间戳
    private int mtime;
    //文件名称
    private String name;
    //文件大小
    private int size;
    //网盘存储目录
    private String dir;

    public FileInfo() {
    }
    public FileInfo(String dir, String name) {
        this.dir = dir;
        this.name = name;
    }

    public int getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }

    public int getIs_dir() {
        return is_dir;
    }

    public void setIs_dir(int is_dir) {
        this.is_dir = is_dir;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public int getMtime() {
        return mtime;
    }

    public void setMtime(int mtime) {
        this.mtime = mtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    //类型(接口获取数据)  正在上传/正在下载
    private int type;
    //文件大小
    private long contentLength;
    //当前文件上传进度
    private long completeSize;
    //当前上传进度条进度
    private int progress;
    //状态
    private int status;
    //文件路径
    private String file_path;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    protected FileInfo(Parcel in) {
        ctime = in.readInt();
        is_dir = in.readInt();
        mime_type = in.readString();
        mtime = in.readInt();
        name = in.readString();
        size = in.readInt();
        dir = in.readString();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ctime);
        dest.writeInt(is_dir);
        dest.writeString(mime_type);
        dest.writeInt(mtime);
        dest.writeString(name);
        dest.writeInt(size);
        dest.writeString(dir);

    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj instanceof FileInfo) {

            FileInfo fileInfo = (FileInfo) obj;
            return (this.getName()).equals( fileInfo.getName());

        }
        return false;
    }

}
