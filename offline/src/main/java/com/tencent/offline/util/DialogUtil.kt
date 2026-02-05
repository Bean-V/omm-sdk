package com.tencent.offline.util

import android.content.Context
import android.widget.TextView
import com.tencent.offline.R
import com.tencent.offline.dialog.BaseDialog
import com.tencent.offline.dialog.OfflineInitDialog
import com.tencent.offline.ext.onClick

object DialogUtil {

    /**
     * 没有离线环境
     */
    @JvmStatic
    fun showNoOfflineData(context: Context?) {
        context ?: return
        val dialog = BaseDialog(context, R.style.BaseDialog, R.layout.dialog_no_offline_data)
        dialog.findViewById<TextView>(R.id.tvOk).onClick { dialog.dismiss() }
        dialog.show()
    }

    @JvmStatic
    fun showInitOfflineData(context: Context?): OfflineInitDialog? {
        context ?: return null
        val dialog = OfflineInitDialog(context)
        dialog.findViewById<TextView>(R.id.tvOk).onClick { dialog.dismiss() }
        dialog.show()
        return dialog
    }
}