package com.oort.weichat.ui.offline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.oort.weichat.MyApplication.cordovaView
import com.oort.weichat.R
import com.tencent.offline.kv.MmkvUtil
import com.oort.weichat.ui.MainActivity
import com.tencent.offline.kv.RSATokenUil
import com.tencent.offline.db.OffLineDbBase
import kotlinx.coroutines.launch
import org.apache.cordova.engine.SystemWebChromeClient

/**
 * Created by rui
 * on 2021/7/29
 */
class OffLineHomeFragment : Fragment() {

    private var swipe: SwipeRefreshLayout? = null
    private var llContent: LinearLayout? = null

    private val TAG = "OffLineHome"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offline_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initDatas()
        lifecycleScope.launch {
            //获取用户的app列表
            val token = MmkvUtil.getToken()
            val uuid = RSATokenUil.decryptByPrivateKey(token)
            val list = OffLineDbBase.getInstance(activity!!)?.appInfoDao()?.getApps(uuid)
            Log.e(TAG, "list.size:" + (list?.size ?: 0))
            if (list != null && list.size > 0) {
                Log.e(TAG, "list:" + (list[0].apk_url))
                if (list.size >= 2) {
                    Log.e(TAG, "list:" + (list[1].apk_url))
                }
            }

        }

    }

    private fun initDatas() {
        cordovaView.webview.webChromeClient =
            object : SystemWebChromeClient(cordovaView.systemWebViewEngine) {
                //监听进度
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    //设置页面加载进度
                    Log.i("newProgress", "newProgress: $newProgress")
                    if (newProgress != 100) {
                        cordovaView.showLoading()
                    } else {
                        cordovaView.hideLoading()
                        MainActivity.makeStatusBarTransparent(activity)
                    }
                }

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    //设置标题
                }
            }


    }

    private fun initView(view: View) {
        swipe = view.findViewById(R.id.swipe)
        llContent = view.findViewById(R.id.llContent)
        llContent?.addView(cordovaView)
        swipe?.setOnRefreshListener {
            if (swipe?.isRefreshing == false) {
                cordovaView.webview.loadUrl("file:///android_asset/offline/index.html")
            }
            swipe?.postDelayed({ swipe?.isRefreshing = false }, 1500L)
        }
    }

    override fun onPause() {
        super.onPause()
        if (cordovaView != null) {
            cordovaView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (cordovaView != null) {
            cordovaView.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cordovaView != null) {
            cordovaView.onDestroy()
        }
    }
}