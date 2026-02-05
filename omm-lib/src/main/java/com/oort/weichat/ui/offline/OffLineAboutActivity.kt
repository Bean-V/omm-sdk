package com.oort.weichat.ui.offline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.oort.weichat.R
import com.oort.weichat.ui.base.BaseActivity

class OffLineAboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_off_line_about)
        initView()
    }
    private fun initView() {
        findViewById<View>(R.id.iv_title_left).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_title_center).text="关于政务服务平台"
    }
    companion object {
        @JvmStatic
        fun start1(context: Context?) {
            context ?: return
            context.startActivity(Intent(context, OffLineAboutActivity::class.java))
        }
    }
}