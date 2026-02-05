package com.sentaroh.android.upantool.record;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM record")
    List<Record> getAll();
    @Query("SELECT * FROM record WHERE disk_uuid LIKE :uuid")
    List<Record> loadAllByUuId(String uuid);

    @Query("SELECT * FROM record WHERE disk_uuid LIKE :uuid AND " +
            "statu LIKE :statu")
    List<Record> findByStatu(String uuid, int statu);

    @Query("SELECT * FROM record WHERE disk_uuid LIKE :uuid AND " +
            "path LIKE :path LIMIT 1")
    Record findByPath(String uuid, String path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Record... records);

    @Delete
    void delete(Record record);

    @Query("Delete FROM record WHERE disk_uuid LIKE :uuid AND " +
            "statu LIKE :statu")
    void deleteByStatu(String uuid, int statu);

    @Update
    void updateRecord(Record record);
}
