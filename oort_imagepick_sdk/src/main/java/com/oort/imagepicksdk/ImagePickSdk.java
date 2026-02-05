package com.oort.imagepicksdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.oort.imagepicksdk.configuration.Glide4Engine;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.sentaroh.android.upantool.Activity_FM_;
import com.sentaroh.android.upantool.Activity_audio;
import com.sentaroh.android.upantool.FileTool;
import com.sentaroh.android.upantool.ItemPickManager;
import com.sentaroh.android.upantool.UsbHelper;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.listener.OnOperClikListener;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.GlobalSetting;
import com.zhongjh.albumcamerarecorder.settings.MultiMediaSetting;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;

import java.util.ArrayList;
import java.util.List;

public class ImagePickSdk {
    public interface ImagePickFinish{

        public void imagePickFinsh(int code,List uris,List<String> paths);

    }

    public static int imagePick(Context cx, ImagePickConfig config, ImagePickFinish imagePickFinish){


        ActivityTypes.config = config;
        ActivityTypes.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
            @Override
            public void imagePickFinsh(int code, List uris,List<String> paths) {
                if(imagePickFinish != null) {
                    imagePickFinish.imagePickFinsh(code,uris,paths);
                }
            }
        };
        cx.startActivity(new Intent(cx,ActivityTypes.class));
        return 1;
    }

    public static int ablumImagePick(Context cx,ImagePickConfig config,ImagePickFinish imagePickFinish){
        if (true) {
            Matisse.from((Activity)cx)
                    .choose(com.zhihu.matisse.MimeType.ofImage(), false)
                    .countable(true)
                    .maxSelectable(config.maxPick)
                    //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(
                            cx.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .setOnSelectedListener((uriList, pathList) -> {
                        Log.e("onSelected", "onSelected: pathList=" + pathList);
                    })
                    .showSingleMediaType(true)
                    .originalEnable(true)
                    .maxOriginalSize(10)
                    .showCopy(false)
                    .autoHideToolbarOnSingleTap(true)
                    .setOnCheckedListener(isChecked -> {
                        Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                    })
                    .setOnOperClikListener(new OnOperClikListener() {
                        @Override
                        public void onOperClikListener(AppCompatActivity act, List filePaths, int type, Callback callback) {

                            ArrayList al = new ArrayList();
                            for(Object o : filePaths){
                                String s = (String) o;
                                Uri uri = FileTool.getUirFromPath(cx,s);
                                al.add(uri.toString());
                            }
                            imagePickFinish.imagePickFinsh(1,al,filePaths);
                        }

                        @Override
                        public void onShowBadgetener(TextView mbadgeTv) {
                            //badgeTv = mbadgeTv;
                        }

                        @Override
                        public void onShowCopy(FrameLayout copy) {
                            //mcopy = copy;
                        }
                    })
                    .forResult(110);
        }
        return 1;
    }


    public static int takeImagePick(Context cx,ImagePickConfig config,ImagePickFinish imagePickFinish){
        if (true) {
            GlobalSetting mGlobalSetting = MultiMediaSetting.from((Activity)cx).choose(MimeType.ofAll());

            CameraSetting cameraSetting = new CameraSetting();
            // 支持的类型：图片，视频
            cameraSetting.mimeTypeSet(MimeType.ofAll());
            mGlobalSetting.cameraSetting(cameraSetting);

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


    public static int audioPick(Context cx,ImagePickConfig config,ImagePickFinish imagePick){
        if (true) {
            ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                @Override
                public void imagePickFinsh(int code, List uris,List<String>paths) {
                    imagePick.imagePickFinsh(code,uris,paths);
                }
            };
            Intent in = new Intent(cx, Activity_audio.class);
            cx.startActivity(in);

        }
        return 1;
    }
    public static int attPick(Context cx,ImagePickConfig config,ImagePickFinish imagePick){
        if (true) {

            UsbHelper.getInstance().initData(cx);
            ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                @Override
                public void imagePickFinsh(int code, List uris,List<String>paths) {
                    imagePick.imagePickFinsh(code,uris,paths);
                }
            };
            Intent in = new Intent(cx, Activity_FM_.class);
            cx.startActivity(in);

        }
        return 1;
    }

}
