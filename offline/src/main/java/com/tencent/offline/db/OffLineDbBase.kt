package com.tencent.offline.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tencent.offline.dao.AppInfoDao
import com.tencent.offline.dao.PersonDao
import com.tencent.offline.model.OfflineAppInfoEntity
import com.tencent.offline.model.OfflinePersonEntity

import net.sqlcipher.database.SupportFactory

/**
 * Created by rui
 * on 2021/7/29
 */


@Database(
    entities = [OfflinePersonEntity::class, OfflineAppInfoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OffLineDbBase : RoomDatabase() {
    abstract fun personDao(): PersonDao?
    abstract fun appInfoDao(): AppInfoDao?

    //为方便使用，写个单例
    companion object {
        private const val PWD = "SZ-jwb*#2021-N.S-#&#--"
        private const val DB_NAME = "jwb_offline.db"

        @Volatile
        private var dbBase: OffLineDbBase? = null

        fun getInstance(context: Context): OffLineDbBase? {
            if (dbBase == null) {
                synchronized(OffLineDbBase::class.java) {
                    if (dbBase == null) {
                        dbBase = buildAppDatabase(context)
                    }
                }
            }
            return dbBase
        }

        //获取 AppDatabase
        private fun buildAppDatabase(context: Context): OffLineDbBase {

            val factory = SupportFactory(PWD.toByteArray(), null, false)

            return Room.databaseBuilder(context.applicationContext, OffLineDbBase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build()
        }
    }
}
