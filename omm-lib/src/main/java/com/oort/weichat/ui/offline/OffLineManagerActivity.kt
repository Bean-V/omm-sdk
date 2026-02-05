package com.oort.weichat.ui.offline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.oort.weichat.R
import com.oort.weichat.ui.base.BaseActivity
import com.oort.weichat.util.offline.OfflineUtil
import com.tencent.offline.db.OffLineDbBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 离线管理界面  主要功能
 *
 *
 * 1 离线环境初始化
 * 2 离线环境销毁
 */
class OffLineManagerActivity : BaseActivity() {

    private var tvStart: TextView? = null
    private var tvDestroy: TextView? = null
    private var tvOfflineDesc: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_off_line_manager)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.iv_title_left).setOnClickListener { finish() }
        tvDestroy = findViewById(R.id.tvDestroy)
        tvStart = findViewById(R.id.tvStart)
        tvOfflineDesc = findViewById(R.id.tvOfflineDesc)
        tvStart?.setOnClickListener {
            startInit()
        }
        tvDestroy?.setOnClickListener {
            destroyData()
        }
        checkSatu()
    }

    /**
     * 数据销毁
     */
    private fun destroyData() {
        OfflineUtil.destroy(this) { checkSatu() }
    }

    /**
     * 离线初始化
     */
    private fun startInit() {
        GlobalScope.launch(Dispatchers.Main) {
            OfflineUtil.initData(this@OffLineManagerActivity)
            tvStart?.postDelayed({
                checkSatu()
            },300L)


        }

    }

    /**
     * 检查离线状态
     */
    private fun checkSatu() {
        GlobalScope.launch(Dispatchers.Main) {
            //查询库中是否有数据
            val persons = withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(this@OffLineManagerActivity)?.personDao()?.getPersons()
            }
            if (persons == null || persons.isEmpty()) {
                //没有离线数据
                resources?.getColor(R.color.color_666)?.let { tvDestroy?.setTextColor(it) }
                tvDestroy?.isClickable = false
                tvOfflineDesc?.text = "本机已不在离线环境中"
            } else {
                tvDestroy?.isClickable = true
                resources?.getColor(R.color.white)?.let { tvDestroy?.setTextColor(it) }
                tvOfflineDesc?.text = "本机已在离线环境中"
            }

        }
    }


    companion object {
        @JvmStatic
        fun start(context: Context?) {
            context ?: return
            context.startActivity(Intent(context, OffLineManagerActivity::class.java))
        }
    }
}