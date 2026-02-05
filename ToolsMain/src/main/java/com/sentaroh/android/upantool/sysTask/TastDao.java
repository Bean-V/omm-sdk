package com.sentaroh.android.upantool.sysTask;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;



import java.util.List;

@Dao
public interface TastDao {

    @Query("SELECT * FROM systask")
    List<SysTask> getAll();
    @Query("SELECT * FROM systask WHERE disk_uuid LIKE :uuid")
    List<SysTask> loadAllByUuId(String uuid);

    @Query("SELECT * FROM systask WHERE disk_uuid LIKE :uuid AND " +
            "statu LIKE :statu")
    List<SysTask> findByStatu(String uuid, int statu);

    @Query("SELECT * FROM systask WHERE disk_uuid LIKE :uuid AND " +
            "path LIKE :path LIMIT 1")
    SysTask findByPath(String uuid, String path);


    @Query("SELECT * FROM systask WHERE disk_uuid LIKE :uuid AND android_id LIKE :androidId AND " +
            "sys_node NOT LIKE :sysNode LIMIT 10000")
    List<SysTask> findBySysNode(String uuid,String androidId,String sysNode);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SysTask... tasks);

    @Delete
    void delete(SysTask task);

    @Query("Delete FROM SysTask WHERE disk_uuid LIKE :uuid AND " +
            "statu LIKE :statu")
    void deleteByStatu(String uuid, int statu);

    @Update
    void updatetask(SysTask task);
}
