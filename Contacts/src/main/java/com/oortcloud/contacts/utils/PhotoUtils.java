package com.oortcloud.contacts.utils;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Result;
import com.oortcloud.contacts.bean.omm.Users;
import com.oortcloud.contacts.http.HttpRequestCenter;
import com.oortcloud.contacts.http.bus.RxBusSubscriber;
import com.oortcloud.contacts.utils.omm.AvatarHelper;


import java.util.List;

/**
 * @filename:
 * @function： 处理头像图片
 * @version： v1.0
 * @author: zhangzhijun/@date: 2020/6/18 16:48
 */
public class PhotoUtils {

    public  static  void  setPortait(ImageView imageView , String phone){
        HttpRequestCenter.getSearchFriend(phone).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {

                Result<List<Users>> result = new Gson().fromJson(s , new TypeToken<Result<List<Users>>>(){}.getType());
                if (result.getResultCode() == 1) {
                    if (result.getData() != null && result.getData().size() > 0){

                        Glide.with(CommonApplication.getAppContext())
                                .load(AvatarHelper.getAvatarUrl(result.getData().get(0).getUserId(), true))
//                                .crossFade()
                                .dontAnimate()
                                .placeholder(R.mipmap.default_head_portrait)//图片加载出来前，显示的图片
                                .error(R.mipmap.default_head_portrait)
                                .into(imageView);
                    }else {
                        imageView.setImageResource(R.mipmap.default_head_portrait);
                    }

                }else {
                    Log.v("msg" , "---------2");
                    imageView.setImageResource(R.mipmap.default_head_portrait);
                }
            }

        });
//
    }
    public  static  void  loadPortrait(ImageView imageView , String url){
        Glide.with(CommonApplication.getAppContext())
                .load(url)
//                .crossFade()
                .dontAnimate()
                .placeholder(R.mipmap.default_head_portrait)//图片加载出来前，显示的图片
                .error(R.mipmap.default_head_portrait)
                .into(imageView);
//
    }
}
