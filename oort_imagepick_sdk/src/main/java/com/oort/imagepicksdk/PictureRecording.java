package com.oort.imagepicksdk;


import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.oort.imagepicksdk.configuration.Glide4Engine;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.GlobalSetting;
import com.zhongjh.albumcamerarecorder.settings.MultiMediaSetting;
import com.zhongjh.albumcamerarecorder.settings.RecorderSetting;
import com.zhongjh.common.coordinator.VideoMergeCoordinator;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;
import com.zhongjh.common.listener.VideoEditListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/23-18:35.
 * Version 1.0
 * Description:
 */
public class PictureRecording {
    public static int videotape(Context cx, ImagePickConfig config, ImagePickSdk.ImagePickFinish imagePickFinish){
        if (true) {
            // 1. 配置全局设置（在Application或启动页中初始化一次即可）
            GlobalSetting mGlobalSetting = MultiMediaSetting.from((Activity)cx).choose(MimeType.ofAll());
            CameraSetting cameraSetting = new CameraSetting();
            cameraSetting.duration(600);
            cameraSetting.isClickRecord(true);

//            cameraSetting.enableVideoHighDefinition(true);
            // 支持的类型：图片，视频
            cameraSetting.mimeTypeSet(MimeType.ofAll());
            mGlobalSetting.cameraSetting(cameraSetting);
            cameraSetting.videoMerge(new VideoMergeCoordinator(){

                @Override
                public void onMergeDispose(@NonNull Class<?> aClass) {

                }

                @Override
                public void onMergeDestroy(@NonNull Class<?> aClass) {

                }

                @Override
                public void setVideoMergeListener(@NonNull Class<?> aClass, @NonNull VideoEditListener videoEditListener) {

                }

                @Override
                public void merge(@NonNull Class<?> aClass, @NonNull String s, @NonNull ArrayList<String> arrayList, @NonNull String s1) {

                }
            });
            Context mContext = cx;
            String appid = mContext.getApplicationInfo().processName;
            mGlobalSetting
                    // 设置路径和7.0保护路径等等
                    .allStrategy(new SaveStrategy(true, appid + ".fileProvider", "aabb"))
                    // for glide-V4
                    .imageEngine(new Glide4Engine())
                    // 最大5张图片、最大3个视频、最大1个音频
                    .maxSelectablePerMediaType(null,
                            config.maxPick,
                            1,
                            3,
                            0,
                            0,
                            0)
                    .forResult(new OnResultCallbackListener() {
                        @Override
                        public void onResult(@NonNull List<? extends LocalFile> list) {
                            ArrayList al = new ArrayList();
                            ArrayList fs = new ArrayList();
                            for(LocalFile o : list){
                                al.add(o.getUri().toString());
                                fs.add(o.getPath());
                            }
                            imagePickFinish.imagePickFinsh(1,al,fs);
                        }

                        @Override
                        public void onResultFromPreview(@NonNull List<? extends MultiMedia> list, boolean b) {

                        }
                    });
        }
        return 1;
    }
    public static int image(Context cx, ImagePickConfig config, ImagePickSdk.ImagePickFinish imagePickFinish){

            // 1. 配置全局设置（在Application或启动页中初始化一次即可）
            GlobalSetting mGlobalSetting = MultiMediaSetting.from((Activity)cx).choose(MimeType.ofAll());
            CameraSetting cameraSetting = new CameraSetting();
             cameraSetting.mimeTypeSet(MimeType.ofAll());
            cameraSetting.isClickRecord(false);
            cameraSetting.enableImageHighDefinition(true);
            // 支持的类型：图片，视频


            mGlobalSetting.cameraSetting(cameraSetting);
            cameraSetting.videoMerge(new VideoMergeCoordinator(){

                @Override
                public void onMergeDispose(@NonNull Class<?> aClass) {

                }

                @Override
                public void onMergeDestroy(@NonNull Class<?> aClass) {

                }

                @Override
                public void setVideoMergeListener(@NonNull Class<?> aClass, @NonNull VideoEditListener videoEditListener) {

                }

                @Override
                public void merge(@NonNull Class<?> aClass, @NonNull String s, @NonNull ArrayList<String> arrayList, @NonNull String s1) {

                }
            });
            Context mContext = cx;
            String appid = mContext.getApplicationInfo().processName;
            mGlobalSetting
                    // 设置路径和7.0保护路径等等
                    .allStrategy(new SaveStrategy(true, appid + ".fileProvider", "aabb"))
                    // for glide-V4
                    .imageEngine(new Glide4Engine())
                    // 最大5张图片、最大3个视频、最大1个音频
                    .maxSelectablePerMediaType(null,
                            config.maxPick,
                            1,
                            3,
                            0,
                            0,
                            0)
                    .forResult(new OnResultCallbackListener() {
                        @Override
                        public void onResult(@NonNull List<? extends LocalFile> list) {
                            ArrayList al = new ArrayList();
                            ArrayList fs = new ArrayList();
                            for(LocalFile o : list){
                                al.add(o.getUri().toString());
                                fs.add(o.getPath());
                            }
                            imagePickFinish.imagePickFinsh(1,al,fs);
                        }

                        @Override
                        public void onResultFromPreview(@NonNull List<? extends MultiMedia> list, boolean b) {

                        }
                    });
        return 1;
    }
    public static int recorder(Context cx, ImagePickConfig config, ImagePickSdk.ImagePickFinish imagePickFinish){

        // 1. 配置全局设置（在Application或启动页中初始化一次即可）
        GlobalSetting mGlobalSetting = MultiMediaSetting.from((Activity)cx).choose(MimeType.ofAll());
        RecorderSetting recorderSetting = new RecorderSetting();
        recorderSetting.duration(600);

        // 开启录音功能
        mGlobalSetting.recorderSetting(recorderSetting);

        Context mContext = cx;
        String appid = mContext.getApplicationInfo().processName;
        mGlobalSetting
                // 设置路径和7.0保护路径等等
                .allStrategy(new SaveStrategy(true, appid + ".fileProvider", "aabb"))
                // for glide-V4
                .imageEngine(new Glide4Engine())
                // 最大5张图片、最大3个视频、最大1个音频
                .maxSelectablePerMediaType(null,
                        config.maxPick,
                        1,
                        3,
                        0,
                        0,
                        0)
                .forResult(new OnResultCallbackListener() {
                    @Override
                    public void onResult(@NonNull List<? extends LocalFile> list) {
                        ArrayList al = new ArrayList();
                        ArrayList fs = new ArrayList();
                        for(LocalFile o : list){
                            al.add(o.getUri().toString());
                            fs.add(o.getPath());
                        }
                        imagePickFinish.imagePickFinsh(1,al,fs);
                    }

                    @Override
                    public void onResultFromPreview(@NonNull List<? extends MultiMedia> list, boolean b) {

                    }
                });
        return 1;
    }

}
