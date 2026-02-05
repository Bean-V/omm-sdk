package com.oort.weichat.ui.account

import android.app.Activity
import android.text.TextUtils
import com.oort.weichat.bean.User
import com.oort.weichat.ui.base.CoreManager
import com.tencent.offline.kv.MmkvUtil
import com.oort.weichat.ui.offline.OnffLineMainActivity
import com.oort.weichat.util.ToastUtil
import com.tencent.offline.db.OffLineDbBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Created by rui
 *  on 2021/7/30
 */
object LoginPresenter {

    /**
     * 离线登录
     */
    @JvmStatic

    fun offlineLogin(context: Activity, phone: String) {

        GlobalScope.launch(Dispatchers.Main) {
            val person = withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(context.applicationContext)?.personDao()?.getPerson(phone)
            }

            if (person == null) {
                ToastUtil.showLongToast(context, "当前账号未在本机创建离线环境，请联系管理员创建！")
                return@launch
            }
            val user = User()
            user.telephone = phone
            CoreManager.getInstance(context).setSelf(user)
            //生成Token 本地生成
            MmkvUtil.setToken(person.uuid)
            OnffLineMainActivity.startAndFinish(context)

        }
    }

    @JvmStatic
    private fun md5(content: String): String {
        val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        return hex.toString()
    }

}