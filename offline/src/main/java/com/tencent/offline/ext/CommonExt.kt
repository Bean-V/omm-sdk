package com.tencent.offline.ext

import android.view.View

/**
 * Created by rui
 *  on 2021/7/30
 *  扩展
 */
//点击事件
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}


fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}