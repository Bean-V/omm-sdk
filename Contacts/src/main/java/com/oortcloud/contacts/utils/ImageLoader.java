package com.oortcloud.contacts.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.contacts.R;
import com.oortcloud.contacts.bean.Department;
import com.oortcloud.contacts.bean.UserInfo;
import com.oortcloud.contacts.utils.omm.AvatarHelper;

/**
 * @filename:
 * @function： 图片加载
 * @version： v1.0
 * @author: zzj/@date: 2020/6/18 16:48
 */
public class ImageLoader {

    public  static  void  loaderImage(ImageView imageView , UserInfo userInfo){
        if (userInfo != null){
            String path = userInfo.getOort_photo();
            if (TextUtils.isEmpty(userInfo.getOort_photo())){
//                path =  AvatarHelper.getAvatarUrl(userInfo.getImuserid(), true);
//                AvatarHelper.displayAvatar()

                AvatarHelper.displayAvatar(imageView.getContext(),userInfo.getOort_name(),userInfo.getImuserid(),imageView);
                return;

            }
            loadImage(imageView , path);

        }

    }
//    public  static  void  loaderImage(ImageView imageView , UserInfo userInfo){
//        if (userInfo != null){
//            String path = userInfo.getOort_photo();
//            if (TextUtils.isEmpty(userInfo.getOort_photo())){
//                path =  AvatarHelper.getAvatarUrl(userInfo.getImuserid(), true);
//                AvatarHelper.displayAvatar(imageView.getContext(),userInfo.getOort_name(),userInfo.getImuserid(),imageView);
//
//            }
//            loadImage(imageView , path);
//
//        }
//
//    }

    public  static  void  divisionImage(ImageView imageView , Department department){
        if (department != null){
            String path = department.getOort_dept_photo();
            if (TextUtils.isEmpty(department.getOort_dept_photo())){
                path =  AvatarHelper.getAvatarUrl(department.getOort_udid(), true);

            }
            divisionImage(imageView , path);

        }

    }
    public  static  void  loadImage(ImageView imageView , String path){
        Glide.with(CommonApplication.getAppContext())
                .load(path)
//                .crossFade()
                .transform(new CenterCrop())
                .dontAnimate()
                .placeholder(R.mipmap.default_head_portrait)//图片加载出来前，显示的图片
                .error(R.mipmap.default_head_portrait)
                .into(imageView);
    }
    public  static  void  divisionImage(ImageView imageView , String path){
        Glide.with(CommonApplication.getAppContext())
                .load(path)
//                .crossFade()
                .dontAnimate()
                .transform(new CenterCrop())
                .placeholder(R.mipmap.icon_division01)//图片加载出来前，显示的图片
                .error(R.mipmap.icon_division01)
                .into(imageView);
    }
}
