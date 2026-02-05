package com.sentaroh.android.upantool.sysTask;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sentaroh.android.upantool.languagelib.MultiLanguageUtil;

import java.util.Locale;

@Entity
public class SysTask {

    @PrimaryKey

    @NonNull
    public String path;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "android_id")
    public String androidId;

    @ColumnInfo(name = "type")
    public String type;

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

    @ColumnInfo(name = "to_md5")
    public String toMd5;

    @ColumnInfo(name = "to_dir_path")
    public String toDirPath;

    @ColumnInfo(name = "to_dir_path_ch")
    public String toDirPath_CH;

    @ColumnInfo(name = "to_dir_path_en")
    public String toDirPath_EN;

    @ColumnInfo(name = "to_dir_path_ja")
    public String toDirPath_JA;

    @ColumnInfo(name = "to_archive_path")
    public String toArchiveDirPath;

    @ColumnInfo(name = "to_archive_path_ch")
    public String toArchiveDirPath_CH;

    @ColumnInfo(name = "to_archive_path_en")
    public String toArchiveDirPath_EN;

    @ColumnInfo(name = "to_archive_path_ja")
    public String toArchiveDirPath_JA;


    @ColumnInfo(name = "sys_node")
    public String sysNode;


    @ColumnInfo(name = "extra0")
    public String extra0;

    @ColumnInfo(name = "extra1")
    public String extra1;
    @ColumnInfo(name = "extra2")
    public String extra2;

    @ColumnInfo(name = "extra3")
    public String extra3;


    public String getCurrentToDir(Context context) {

        String currentToDir = toDirPath_CH;

        Locale local = MultiLanguageUtil.getInstance().getLanguageLocale(context);
        if(local == Locale.SIMPLIFIED_CHINESE){
            currentToDir = toDirPath_CH;
        }else if(local == Locale.ENGLISH){
            currentToDir = toDirPath_EN;
        }else if(local == Locale.JAPAN){
            currentToDir = toDirPath_JA;
        }else if(local.getLanguage().toLowerCase().contains("zh")){
            currentToDir = toDirPath_CH;
        }else if(local.getLanguage().toLowerCase().contains("en")){
            currentToDir = toDirPath_EN;
        }else if(local.getLanguage().toLowerCase().contains("ja")){
            currentToDir = toDirPath_JA;
        }
        return currentToDir;
    }

    public String getCurrentToArchiveDir(Context context) {

        String currentToArchiveDir = toArchiveDirPath_CH;
        Locale local = MultiLanguageUtil.getInstance().getLanguageLocale(context);
        if(local == Locale.SIMPLIFIED_CHINESE){
            currentToArchiveDir = toArchiveDirPath_CH;
        }else if(local == Locale.ENGLISH){
            currentToArchiveDir = toArchiveDirPath_EN;
        }else if(local == Locale.JAPAN){
            currentToArchiveDir = toArchiveDirPath_JA;
        }else if(local.getLanguage().toLowerCase().contains("zh")){
            currentToArchiveDir = toArchiveDirPath_CH;
        }else if(local.getLanguage().toLowerCase().contains("en")){
            currentToArchiveDir = toArchiveDirPath_EN;
        }else if(local.getLanguage().toLowerCase().contains("ja")){
            currentToArchiveDir = toArchiveDirPath_JA;
        }
        return currentToArchiveDir;
    }


}
