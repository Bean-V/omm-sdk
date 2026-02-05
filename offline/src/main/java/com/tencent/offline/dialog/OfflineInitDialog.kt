package com.tencent.offline.dialog

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.TextView
import com.tencent.offline.R

class OfflineInitDialog(context: Context) : Dialog(context, R.style.BaseDialog) {

    companion object {
        val OFFLINEINIT_DATA_1 = 1
        val OFFLINEINIT_DATA_2 = 2
        val OFFLINEINIT_DATA_3 = 3
        val OFFLINEINIT_DATA_4 = 4
        val OFFLINEINIT_DATA_ERROR = 5
    }

    private var dialog: Dialog? = null

    private var tvOk: TextView? = null
    private var tvDesc: TextView? = null
    private var tvProgress: TextView? = null

    init {
        setContentView(R.layout.dialog_init_offline_data)
        dialog = this
        initView()
        canceledOnTouchOutside()
        cancelable()
        setWidth()
    }

    private fun initView() {
        tvOk = dialog?.findViewById(R.id.tvOk)
        tvDesc = dialog?.findViewById(R.id.tvDesc)
        tvProgress = dialog?.findViewById(R.id.tvProgress)
    }

    /**
     * 设置初始化步骤
     * 1 数据解压
     * 2 数据解密
     * 3 数据存入数据库
     * 4 完成
     */
    fun setSetStep(step: Int, desc: String = "") {
        when (step) {
            OFFLINEINIT_DATA_1 -> {
                step1()
            }
            OFFLINEINIT_DATA_2 -> {
                step2()
            }
            OFFLINEINIT_DATA_3 -> {
                step3(desc)
            }

            OFFLINEINIT_DATA_4 -> {
                step4(context)
            }
            else -> {
                elses(context)
            }
        }

    }

    private fun elses(context: Context) {
        tvDesc?.text = "创建离线环境异常 请重试"
        tvDesc?.setTextColor(context.resources.getColor(R.color.c_f10))
        tvProgress?.text = "发生异常 请重试"
        tvOk?.isClickable = true
        tvOk?.text = "退出"
        tvOk?.background = context.resources.getDrawable(R.drawable.ripple_bg_245_r)
    }

    private fun step4(context: Context) {
        tvDesc?.text = "创建离线环境成功"
        tvProgress?.text = "完成"
        tvOk?.isClickable = true
        tvOk?.background = context.resources.getDrawable(R.drawable.ripple_bg_245_r)
        tvOk?.setTextColor(context.resources.getColor(R.color.white_offline))
    }

    private fun step3(desc: String) {
        tvDesc?.text = desc
        tvProgress?.text = "数据持久化中..."
        tvOk?.isClickable = false
    }

    private fun step2() {
        tvDesc?.text = "正在创建离线环境"
        tvProgress?.text = "数据解密中..."
        tvOk?.isClickable = false
    }

    private fun step1() {
        tvDesc?.text = "正在创建离线环境"
        tvProgress?.text = "数据解压中..."
        tvOk?.isClickable = false

    }


    /**
     * 设置宽度
     */
    fun setWidth(ratio: Float = 0.85f): Dialog {
        val window = dialog?.window
        val outMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getRealMetrics(outMetrics)
        val widthPixel = outMetrics.widthPixels
        window?.setLayout(
            (widthPixel * ratio).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return this
    }

    /**
     * 设置是否可返回
     */
    fun cancelable(boolean: Boolean = false): Dialog {
        dialog?.setCancelable(boolean)
        return this
    }

    /**
     * 设置返回键是否有效
     */
    fun canceledOnTouchOutside(boolean: Boolean = false): Dialog {
        dialog?.setCanceledOnTouchOutside(boolean)
        return this
    }

}