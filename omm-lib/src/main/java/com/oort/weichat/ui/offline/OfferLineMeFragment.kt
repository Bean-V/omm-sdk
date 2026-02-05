package com.oort.weichat.ui.offline

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.makeramen.roundedimageview.RoundedImageView
import com.oort.weichat.R
import com.oort.weichat.helper.AvatarHelper
import com.oortcloud.utils.BitmapUtils
import com.tencent.offline.db.OffLineDbBase
import com.tencent.offline.kv.MmkvUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by rui
 * on 2021/7/29
 */
class OfferLineMeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offline_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }


    private fun initView(view: View) {
        val avatar = view.findViewById<RoundedImageView>(R.id.avatar_img)
        val tvName = view.findViewById<TextView>(R.id.nick_name_tv)
        val tvPhone = view.findViewById<TextView>(R.id.phone_number_tv)

        view.findViewById<RelativeLayout>(R.id.rlSetting).setOnClickListener {
            OffLineManagerActivity.start(context)
        }
        view.findViewById<RelativeLayout>(R.id.setting_rl).setOnClickListener {
            OffLineAboutActivity.start1(context)
        }

        GlobalScope.launch(Dispatchers.Main) {
            activity?:return@launch
            val person = withContext(Dispatchers.IO) {
                OffLineDbBase.getInstance(activity!!)?.personDao()
                    ?.getPersonByUUid(MmkvUtil.getUUid())
            }
            if (person != null) {
                tvName.text = person.name
//                AvatarHelper.getInstance()
//                    .displayAvatar(person.photo, avatar, true)
//                avatar.setImageBitmap(BitmapUtils.base64ToBitmap(person.photo))
                tvPhone.text = person.loginid
            }


        }
    }
}