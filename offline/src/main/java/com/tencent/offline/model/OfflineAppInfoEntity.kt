package com.tencent.offline.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by rui
 *  on 2021/8/4
 */
@Entity(tableName = "tab_offline_app")
class OfflineAppInfoEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "uuid")
    val uuid: String,

    /**
     * 与用户关uuid关联，可以理解为外键
     */
    @ColumnInfo(name = "person_id")
    var person_id: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "apk_url")
    var apk_url: String,

    @ColumnInfo(name = "app_id")
    val app_id: String,

    @ColumnInfo(name = "app_secret")
    val app_secret: String,

    @ColumnInfo(name = "app_size")
    val app_size: String,

    @ColumnInfo(name = "app_entry")
    var appentry: String = "",

    @ColumnInfo(name = "app_label")
    val applabel: String,

    @ColumnInfo(name = "app_package")
    val apppackage: String,

    @ColumnInfo(name = "app_web_url")
    val appweburl: String,

    @ColumnInfo(name = "classify")
    val classify: String,

    @ColumnInfo(name = "develop_unit")
    val develop_unit: String,

    @ColumnInfo(name = "icon_url")
    var icon_url: String,


    @ColumnInfo(name = "intro")
    val intro: String,


    @ColumnInfo(name = "oneword")
    val oneword: String,

    @ColumnInfo(name = "principal")
    val principal: String,

    @ColumnInfo(name = "screenshot_url")
    val screenshot_url: String,


    @ColumnInfo(name = "supplier_uid")
    val supplier_uid: String,


    @ColumnInfo(name = "uid")
    val uid: String,


    @ColumnInfo(name = "ver_description")
    val ver_description: String,

    @ColumnInfo(name = "version")
    val version: String,

    @ColumnInfo(name = "versioncode")
    val versioncode: String,

    @ColumnInfo(name = "offline")
    val offline: String,

    @ColumnInfo(name = "download_num")
    val download_num: String,

    @ColumnInfo(name = "enabled")
    val enabled: String,

    @ColumnInfo(name = "network")
    val network: String


    ) {
    override fun toString(): String {
        return "OfflineAppInfoEntity(uuid='$uuid', person_id='$person_id', address='$address', apk_url='$apk_url', app_id='$app_id', app_secret='$app_secret', app_size='$app_size', appentry='$appentry', applabel='$applabel', apppackage='$apppackage', appweburl='$appweburl', classify='$classify', develop_unit='$develop_unit', icon_url='$icon_url', intro='$intro', oneword='$oneword', principal='$principal', screenshot_url='$screenshot_url', supplier_uid='$supplier_uid', uid='$uid', ver_description='$ver_description', version='$version', versioncode='$versioncode', offline='$offline', download_num='$download_num', enabled='$enabled', network='$network')"
    }
}
