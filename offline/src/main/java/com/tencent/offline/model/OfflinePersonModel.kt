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
class OfflinePersonModel(
    val uuid: String,
    val loginid: String,

    val pwd: String,

    val salt: String?,

    val name: String?,

    val sex: Int,

    var photo: String?,
    var depname: String?,

    var depcode: String?,

    val applist: MutableList<OfflineAppInfoEntity>?

)