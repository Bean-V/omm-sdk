package com.tencent.offline.kv


import com.tencent.mmkv.MMKV
import com.tencent.offline.kv.RSATokenUil.encryptByPublicKey

/**
 * Created by rui
 * on 2021/8/2
 * 离线token
 */
object MmkvUtil {
    private const val TOKEN = "token"

    fun setToken(token: String?) {
        token ?: return
        //公钥加密
        val s = encryptByPublicKey(token)
        MMKV.defaultMMKV().putString(TOKEN, s)
    }

    fun getToken(): String {
        return MMKV.defaultMMKV().getString(TOKEN, "") ?: ""
    }

    fun getUUid():String {
        val token = getToken()
        return RSATokenUil.decryptByPrivateKey(token)
    }
}