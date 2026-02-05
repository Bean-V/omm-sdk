package com.oort.weichat.util.offline

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leo618.zip.IZipCallback
import com.leo618.zip.ZipManager
import com.oort.weichat.helper.DialogHelper
import com.oort.weichat.ui.account.LoginActivity.LOGIN_UUID
import com.oort.weichat.util.ToastUtil
import com.oortcloud.utils.Base64Utils
import com.oortcloud.utils.RSAUtils
import com.tencent.offline.db.OffLineDbBase
import com.tencent.offline.dialog.OfflineInitDialog
import com.tencent.offline.dialog.OfflineInitDialog.Companion.OFFLINEINIT_DATA_1
import com.tencent.offline.dialog.OfflineInitDialog.Companion.OFFLINEINIT_DATA_2
import com.tencent.offline.dialog.OfflineInitDialog.Companion.OFFLINEINIT_DATA_3
import com.tencent.offline.dialog.OfflineInitDialog.Companion.OFFLINEINIT_DATA_4
import com.tencent.offline.dialog.OfflineInitDialog.Companion.OFFLINEINIT_DATA_ERROR
import com.tencent.offline.model.OfflineAppInfoEntity
import com.tencent.offline.model.OfflinePersonEntity
import com.tencent.offline.model.OfflinePersonModel
import com.tencent.offline.util.DialogUtil
import kotlinx.coroutines.*
import java.io.File


/**
 * Created by rui
 *  on 2021/7/30
 */
object OfflineUtil {

    /**
     * 离线数据路径
     */
    private const val DATA_PATH = "/sdcard/com.oortcloud.offlinefiles/"

    /**
     * 解压路径
     */
    //    const val UNZIP_PATH = DATA_PATH

    // private const val UNZIP_PATH = "/sdcard/com.oortcloud.offlinefiles/"

    @JvmStatic
    fun initDataFile() {
        val file = File(DATA_PATH)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 离线初始化
     *
     * 1 判断压缩包数据是否存在
     *
     * 2 解压数据并解密
     *
     * 3 将数据持久化到数据库中
     *
     */
    interface LoginInterface {
        fun login()
    }


    /**
     * 离线数据初始化
     */
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun initData(context: Context) {
        if (isHasInit(context)) {
            offlineInit(context)
        }
    }

    /**
     * 判断是否已经初始化
     */
    @JvmStatic
    fun isHasInit(context: Context): Boolean {
        initDataFile()
        //查询离线人员
        //判断离线数据包是否存在
        val file = File(DATA_PATH)
        val listFiles = file.listFiles()
        if (listFiles == null || listFiles.isEmpty()) {
            DialogUtil.showNoOfflineData(context)
            return false
        }
        var hasData = false
        listFiles.forEach {
            if (it.name.contains(".zip")) {
                hasData = true
            }
        }
        if (!hasData) {
            DialogUtil.showNoOfflineData(context)
            return false
        }

        return true

    }

    @SuppressLint("StaticFieldLeak")
    @JvmStatic
    private var offlineDialog: OfflineInitDialog? = null

    @ExperimentalCoroutinesApi
    @JvmStatic
    fun offlineInit(context: Context) {
        offlineDialog = DialogUtil.showInitOfflineData(context)
        val file = File(DATA_PATH)
        val listFiles = file.listFiles()
        GlobalScope.launch(Dispatchers.Main) {
            //查询库中是否有数据 子线程执行数据库操作
            listFiles.forEach {
                //有.zip 数据压缩包
                if (it.name.contains(".zip")) {
                    val path = it.name.replace(".zip", "")
                    val fileData = File(DATA_PATH + path)
                    if (!fileData.exists()) {
                        fileData.mkdirs()
                    }
                    offlineDialog?.setSetStep(OFFLINEINIT_DATA_1)
                    unZip(context, it.absolutePath, fileData.absolutePath)
                }
            }
        }


    }


    /**
     * 解压
     */
    @JvmStatic
    private fun unZip(context: Context, zipPath: String, unzipPath: String) {
        ZipManager.unzip(zipPath, unzipPath, object : IZipCallback {
            override fun onStart() {}

            override fun onProgress(percentDone: Int) {}
            override fun onFinish(success: Boolean) {
                offlineDialog?.setSetStep(OFFLINEINIT_DATA_2)
                decryptRSA(context, unzipPath)
            }
        })
    }

    /**
     * 解密
     */
    @JvmStatic
    private fun decryptRSA(context: Context, unzipPath: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val file = File(unzipPath + "/user.json")
            delay(2000L)
            if (!file.exists()) {
                offlineDialog?.setSetStep(OFFLINEINIT_DATA_ERROR)
                Log.e("offlineutil", "11111")
                return@launch
            }


            //解密
            val fileToByte = Base64Utils.fileToByte(unzipPath + "/user.json")
            val json =
                RSAUtils.decryptByPublicKey(Base64Utils.decode(String(fileToByte)))
            val gson = Gson()
            val list: MutableList<OfflinePersonModel> =
                gson.fromJson(json, object : TypeToken<MutableList<OfflinePersonModel>>() {}.type)

            if (list.isEmpty()) {
                offlineDialog?.setSetStep(OFFLINEINIT_DATA_ERROR)
                Log.e("offlineutil", "222222")
                return@launch
            }

            //初始化之前先清空旧数据库
            withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(context.applicationContext)?.personDao()
                    ?.deleteAll()
                OffLineDbBase.getInstance(context.applicationContext)?.appInfoDao()
                    ?.deleteAll()
            }
            var useDefaultPhoto = false
            //获取解密路劲下/photo 文件下的图片
            val photoFile = File(unzipPath + "/photo")
            //文件不存在返回
            if (!photoFile.exists()) {
               /* offlineDialog?.setSetStep(OFFLINEINIT_DATA_ERROR)
                Log.e("offlineutil", "3333333")
                return@launch*/
                useDefaultPhoto = true
            }
            // 不是文件夹返回
            if (!useDefaultPhoto && !photoFile.isDirectory ) {
                /*offlineDialog?.setSetStep(OFFLINEINIT_DATA_ERROR)
                Log.e("offlineutil", "444444444444")
                return@launch*/
                useDefaultPhoto = true
            }

            //找出用户对应的图片  person.photo = "photo/xxxxxx-xxx.jpg"  即 person.photo = "photo/${person.uuid}.jpg"
            var personStr: String = ""
            list.forEach { p ->
                if (useDefaultPhoto){
                    p.photo = ""
                    personStr = p.name + "、"
                }else {
                    photoFile.listFiles().forEach {
                        if (it.name.contains(p.uuid)) {
                            val bitmapString = it.absolutePath
                            p.photo = bitmapString
                            personStr = p.name + "、"
                            Log.e("offlineUtil", "---" + personStr)
                        }
                    }
                }
                LOGIN_UUID = p.uuid
            }
            personStr = "正在建立离线环境，当前离线环境可登录用户为：\n" + personStr
            offlineDialog?.setSetStep(OFFLINEINIT_DATA_3, personStr)


            val r = withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(context.applicationContext)?.personDao()
                    ?.getPersons()
            }
            if (r == null || r.size == 0) {
                val personList = mutableListOf<OfflinePersonEntity>()
                val appList = mutableListOf<OfflineAppInfoEntity>()
                //关联p.uuid 与 app.personID
                list.forEach { p ->
                    if (p.applist != null && ((p.applist?.size ?: 0) > 0))
                        p.applist?.forEach { app ->
                            app.person_id = p.uuid
                            app.apk_url = unzipPath + "/" + app.apk_url
                            app.icon_url = unzipPath + "/" + app.icon_url
                            appList.add(app)
                        }

                    val person = OfflinePersonEntity(
                        p.uuid, p.loginid, p.pwd, p.salt ?: "", p.name ?: "",
                        p.sex, p.photo ?: "", p.depcode ?: "", p.depcode ?: ""
                    )
                    personList.add(person)
                }

                val result = withContext(Dispatchers.IO) {
                    //保存appInfo消息
                    if (appList.size > 0) {
                        OffLineDbBase.getInstance(context)?.appInfoDao()
                            ?.insertApps(appList)
                    }
                    // 保存用户消息
                    if (personList.size > 0) {
                        OffLineDbBase.getInstance(context)?.personDao()
                            ?.insertPersons(personList)
                    }

                    true
                }

                if (result) {
                    offlineDialog?.setSetStep(OFFLINEINIT_DATA_4)
                } else {
                    offlineDialog?.setSetStep(OFFLINEINIT_DATA_ERROR)
                    Log.e("offlineutil", "55555555555555")
                }
            } else {
                offlineDialog?.dismiss()
            }

        }

    }

    /**
     * 销毁
     */
    @JvmStatic
    fun destroy(context: Context, listener: () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {

            val fileData = File(DATA_PATH)
            // 删除解压文件下的所有文件
            if (fileData.exists()) {
                fileData.listFiles().forEach {
                    if (it.isDirectory) {
                        deleteFile(it)
                        it.delete()
                    } else {
                        it.delete()
                    }
                }

            }

            // 删除离线表中的数据
            val result = withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(context)?.appInfoDao()?.deleteAll()
                OffLineDbBase.getInstance(context)?.personDao()?.deleteAll()
                true
            }
            if (result) {
                listener()
            }

        }

    }

    @JvmStatic
    private fun deleteFile(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        val files = file.listFiles()
        for (f in files) {
            if (f.isDirectory) {
                deleteFile(f)
                f.delete()
            } else {
                f.delete()
            }
        }
    }


}