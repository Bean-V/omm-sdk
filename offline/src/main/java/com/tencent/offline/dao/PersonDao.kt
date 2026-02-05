package com.tencent.offline.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.tencent.offline.model.OfflinePersonEntity


/**
 * Created by rui
 * on 2021/7/30
 */
@Dao
interface PersonDao {

    /**
     * 插入
     */
    @Insert
    /*suspend*/ fun insertPerson(vararg pseron: OfflinePersonEntity)


    /**
     * 如果是插入数据只需要标记上Insert注解，onConflict = OnConflictStrategy.REPLACE
     * 表明插入一条数据如果主键已经存在，则可以直接替换旧的数据。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersons(persons: MutableList<OfflinePersonEntity>)

    /**
     * 查询用户
     */
    @Query("SELECT * FROM tab_offline_person WHERE login_id=:loginId")
    /*suspend*/ fun getPerson(loginId: String): OfflinePersonEntity

    @Query("SELECT * FROM tab_offline_person WHERE uuid=:uuid")
    /*suspend*/ fun getPersonByUUid(uuid: String): OfflinePersonEntity?

    /**
     * 修改
     */
    @Update()
    /*suspend*/ fun updateUser(person: OfflinePersonEntity?)

    /**
     * 删除
     */
    @Delete()
    /*suspend*/ fun deletePersons(persons: MutableList<OfflinePersonEntity>?)

    /**
     * 删除表中全部数据
     */
    /*suspend*/ fun deleteAll() {
        val tableName = "tab_offline_person"
        val query = SimpleSQLiteQuery("delete from $tableName")
        doDeleteAll(query)
    }

    @RawQuery
    /*suspend*/ fun doDeleteAll(query: SimpleSQLiteQuery):Int?


    /**
     *  查询
     */
    @Query("SELECT * FROM tab_offline_person")
    fun getPersons(): MutableList<OfflinePersonEntity>?
}