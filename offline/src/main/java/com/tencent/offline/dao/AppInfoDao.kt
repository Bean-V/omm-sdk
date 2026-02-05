package com.tencent.offline.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.tencent.offline.model.OfflineAppInfoEntity
import com.tencent.offline.model.OfflinePersonEntity

/**
 * Created by rui
 *  on 2021/8/4
 */
@Dao
interface AppInfoDao {
    /**
     * 插入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /*suspend*/ fun insertPerson(vararg info: OfflineAppInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApps(infoList: MutableList<OfflineAppInfoEntity>)


    /**
     * 查询 用户的UUid
     */
    @Query("SELECT * FROM tab_offline_app WHERE person_id=:personID")
    /*suspend*/ fun getApps(personID: String): MutableList<OfflineAppInfoEntity>?

    /**
     * 删除表中全部数据
     */
    /*suspend*/ fun deleteAll() {
        val tableName = "tab_offline_app"
        val query = SimpleSQLiteQuery("delete from $tableName")
        doDeleteAll(query)
    }

    @RawQuery
    /*suspend*/ fun doDeleteAll(query: SimpleSQLiteQuery): Int?
}