package com.sentaroh.android.upantool.record;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 2)
public abstract class RecordDatabase extends RoomDatabase {
    public abstract RecordDao RecordDao();
}


