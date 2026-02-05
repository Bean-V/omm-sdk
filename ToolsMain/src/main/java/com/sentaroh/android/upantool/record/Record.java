package com.sentaroh.android.upantool.record;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey

    @NonNull
    public String path;

    @ColumnInfo(name = "name")
    public String name;


    @ColumnInfo(name = "disk_uuid")
    public String disk_uuid;

    @ColumnInfo(name = "from")
    public String from;

    @ColumnInfo(name = "to")
    public String to;

    @ColumnInfo(name = "postion")
    public int postion;

    @ColumnInfo(name = "statu")
    public int statu;

    @ColumnInfo(name = "statu_des")
    public String statuDes;


    @ColumnInfo(name = "md5")
    public String md5;


    @ColumnInfo(name = "toDirPath")
    public String toDirPath;




}
