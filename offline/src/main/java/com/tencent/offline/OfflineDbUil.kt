package com.tencent.offline

import android.content.Context
import com.tencent.offline.db.OffLineDbBase
import com.tencent.offline.kv.MmkvUtil

/**
 * Created by rui
 *  on 2021/8/4
 */
object OfflineDbUil {

    /*suspend*/ fun getAppList(context: Context) =
        OffLineDbBase.getInstance(context)?.appInfoDao()?.getApps(MmkvUtil.getUUid())
}