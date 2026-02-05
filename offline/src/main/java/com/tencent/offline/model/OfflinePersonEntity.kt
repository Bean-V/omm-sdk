package com.tencent.offline.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by rui
 * on 2021/7/30
 * 离线人员信息表
 */
@Entity(tableName = "tab_offline_person")
class OfflinePersonEntity(
    @PrimaryKey(autoGenerate = false)
    val uuid: String,

    @ColumnInfo(name = "login_id")
    val loginid: String,

    @ColumnInfo(name = "pwd")
    val pwd: String,

    @ColumnInfo(name = "salt")
    val salt: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "sex")
    val sex: Int,

    @ColumnInfo(name = "photo")
    var photo: String,


    @ColumnInfo(name = "depname")
    var depName: String,

    @ColumnInfo(name = "depcode")
    var depCode: String,

//    @Ignore
//    val applist: MutableList<OfflineAppInfoEntity>?



) {
    override fun toString(): String {
        return "OfflinePersonEntity(uuid='$uuid', loginid='$loginid', pwd='$pwd', salt='$salt', name='$name', sex=$sex, photo='$photo', depName='$depName', depCode='$depCode')"
    }
}