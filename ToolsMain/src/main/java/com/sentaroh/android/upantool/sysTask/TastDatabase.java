package com.sentaroh.android.upantool.sysTask;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SysTask.class}, version = 2)
public abstract class TastDatabase extends RoomDatabase {
    public abstract TastDao tastDao();
}

