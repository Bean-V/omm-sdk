package com.oortcloud.clouddisk.bean;

import java.io.File;
import java.io.Serializable;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/5 18:39
 * @version： v1.0
 * @function：检查文件是否可以秒传 info
 */
public class MD5FileData implements Serializable {


    private Block block;
    private File file;
    private boolean file_exists;
    private String dir;

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile_exists(boolean file_exists) {
        this.file_exists = file_exists;
    }

    public boolean getFile_exists() {
        return file_exists;
    }

    public boolean isFile_exists() {
        return file_exists;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public class Block  implements Serializable {
        //文件md5
        private String filemd5;
         //文件名
        private String filename;
         //文件大小
        private long filesize;
        //文件mimetype
        private String filetype;
        //每个分块大小(单位:字节)
        private long maxblocksize;
        //文件偏移位置
        private int offset;
        //上传标记
        private String tagid;

        public void setFilemd5(String filemd5) {
            this.filemd5 = filemd5;
        }

        public String getFilemd5() {
            return filemd5;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilesize(long filesize) {
            this.filesize = filesize;
        }

        public long getFilesize() {
            return filesize;
        }

        public void setFiletype(String filetype) {
            this.filetype = filetype;
        }

        public String getFiletype() {
            return filetype;
        }

        public void setMaxblocksize(long maxblocksize) {
            this.maxblocksize = maxblocksize;
        }

        public long getMaxblocksize() {
            return maxblocksize;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }

        public void setTagid(String tagid) {
            this.tagid = tagid;
        }

        public String getTagid() {
            return tagid;
        }

    }


    public class File  implements Serializable  {
        //文件上传时间戳
        private long ctime;
        //是否文件夹 0:否 1:是
        private int is_dir;
        //文件mimetype
        private String mime_type;
        //文件修改时间戳
        private long mtime;
        //文件名称
        private String name;
        //文件大小
        private long size;

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public long getCtime() {
            return ctime;
        }

        public void setIs_dir(int is_dir) {
            this.is_dir = is_dir;
        }

        public int getIs_dir() {
            return is_dir;
        }

        public void setMime_type(String mime_type) {
            this.mime_type = mime_type;
        }

        public String getMime_type() {
            return mime_type;
        }

        public void setMtime(long mtime) {
            this.mtime = mtime;
        }

        public long getMtime() {
            return mtime;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getSize() {
            return size;
        }

    }
}
