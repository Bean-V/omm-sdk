package com.oort.imagepicksdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.oort.imagepicksdk.configuration.Glide4Engine;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.sentaroh.android.upantool.ActivityWX_;
import com.sentaroh.android.upantool.Activity_FM_;
import com.sentaroh.android.upantool.Activity_audio;
import com.sentaroh.android.upantool.FileTool;
import com.sentaroh.android.upantool.ItemPickManager;
import com.sentaroh.android.upantool.UsbHelper;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.listener.OnOperClikListener;
import com.zhongjh.albumcamerarecorder.listener.OnResultCallbackListener;
import com.zhongjh.albumcamerarecorder.preview.BasePreviewActivity;
import com.zhongjh.albumcamerarecorder.settings.CameraSetting;
import com.zhongjh.albumcamerarecorder.settings.GlobalSetting;
import com.zhongjh.albumcamerarecorder.settings.MultiMediaSetting;
import com.zhongjh.albumcamerarecorder.settings.RecorderSetting;
import com.zhongjh.common.entity.LocalFile;
import com.zhongjh.common.entity.MediaExtraInfo;
import com.zhongjh.common.entity.MultiMedia;
import com.zhongjh.common.entity.SaveStrategy;
import com.zhongjh.common.enums.MimeType;
import com.zhongjh.common.utils.MediaUtils;

import java.util.ArrayList;
import java.util.List;

public class ActivityTypes extends BaseActivity {

    private MyAdapter mAdapter;
    private ArrayList mlist;

    GlobalSetting mGlobalSetting;
    protected static final int REQUEST_CODE_CHOOSE = 236;
    private final static int PROGRESS_MAX = 100;
    private String TAG = "ActivityTypes";
    private Toolbar tb;

    public static ImagePickConfig config;


    public static ItemPickManager.ImagePickFinish itemPickFinsh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // 设置状态栏文字黑色
            decorView.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(getResources().getColor(R.color.main_color)); // 状态栏背景设为白色（否则可能看不清黑色文字）
        }

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       // setStatusBarLight(true);
        tb.setTitle("文件");

        tb.setNavigationIcon(com.sentaroh.android.upantool.R.mipmap.ic_fm_back);


        tb.setTitleTextColor(ContextCompat.getColor(this, R.color.nav_text_color)); // 设置标题颜色

// 设置导航图标和颜色
        Drawable backIcon = ContextCompat.getDrawable(this, com.sentaroh.android.upantool.R.mipmap.ic_fm_back);
        if (backIcon != null) {
            backIcon = DrawableCompat.wrap(backIcon);
            DrawableCompat.setTint(backIcon, ContextCompat.getColor(this, R.color.nav_icon_color)); // 设置图标颜色
            tb.setNavigationIcon(backIcon);
        }

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });




        UsbHelper.getInstance().initData(this);
        mlist = new ArrayList<Gnode>();

        mlist.add(new Gnode(R.mipmap.ic_home_pic,"图片",1,""));
        mlist.add(new Gnode(R.mipmap.ic_home_videa,"视频",2,""));
        mlist.add(new Gnode(R.mipmap.ic_home_audio,"音频",3,""));
       // mlist.add(new Gnode(R.mipmap.ic_home_wx,"微信",4,""));
        mlist.add(new Gnode(R.mipmap.ic_fm_filetype_folder,"本机文件",5,""));
        mlist.add(new Gnode(R.mipmap.ic_home_take,"拍照",6,""));
        mlist.add(new Gnode(R.mipmap.ic_home_audio,"录音",7,""));




        mAdapter = new MyAdapter<Gnode>((ArrayList<Gnode>) mlist, R.layout.layout_item_types) {
            @Override
            public void bindView(ViewHolder holder, ActivityTypes.Gnode obj) {
                //View v = LayoutInflater.from(gv.getContext()).inflate(R.layout.item_fragment_filelist,gv,false);
                holder.setText(R.id.tv_name, obj.getiName());
                holder.setImageResource(R.id.img_icon,obj.getiId());
            }

        };

        GridView gv = findViewById(R.id.gv_filesrc);

        Context cx = this;
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Gnode n = (Gnode) mlist.get(position);

               if(n.getItype() == 1){
                   ItemPickManager.itemPickFinsh = itemPickFinsh;
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
                                       if(ItemPickManager.itemPickFinsh != null) {
                                           ItemPickManager.itemPickFinsh.imagePickFinsh(1, al,filePaths);
                                           finish();
                                       }
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
               }
                if(n.getItype() == 2){
                    if (true) {
                        ItemPickManager.itemPickFinsh = itemPickFinsh;
                        Matisse.from((Activity)cx)
                                .choose(com.zhihu.matisse.MimeType.ofVideo(), false)
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
                                        if(ItemPickManager.itemPickFinsh != null) {
                                            ItemPickManager.itemPickFinsh.imagePickFinsh(1, al,filePaths);
                                            finish();
                                        }
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

                }


                if(n.getItype() == 3){

                    ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                        @Override
                        public void imagePickFinsh(int code, List uris,List<String> paths) {
                            itemPickFinsh.imagePickFinsh(code,uris,paths);
                            finish();
                        }
                    };

                    Intent in = new Intent(cx, Activity_audio.class);
                    cx.startActivity(in);
                }

                if(n.getItype() == 4){
                    ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                        @Override
                        public void imagePickFinsh(int code, List uris,List<String> paths) {
                            itemPickFinsh.imagePickFinsh(code,uris,paths);
                            finish();
                        }
                    };
                    Intent in = new Intent(ActivityTypes.this, ActivityWX_.class);
                    startActivity(in);
                }

                if(n.getItype() == 5){
                    ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                        @Override
                        public void imagePickFinsh(int code, List uris,List<String> paths) {
                            itemPickFinsh.imagePickFinsh(code,uris,paths);
                            finish();
                        }
                    };
                    Intent in = new Intent(ActivityTypes.this, Activity_FM_.class);
                    startActivity(in);
                }

                if(n.getItype() == 6){
                    ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                        @Override
                        public void imagePickFinsh(int code, List uris,List<String> paths) {
                            itemPickFinsh.imagePickFinsh(code,uris,paths);
                            finish();
                        }
                    };
                    mGlobalSetting = MultiMediaSetting.from(ActivityTypes.this).choose(MimeType.ofAll());

                    CameraSetting cameraSetting = new CameraSetting();
                    // 支持的类型：图片，视频
                    cameraSetting.mimeTypeSet(MimeType.ofAll());
                    mGlobalSetting.cameraSetting(cameraSetting);
                    openMain();
                }

                if(n.getItype() == 7){
                    ItemPickManager.itemPickFinsh = new ItemPickManager.ImagePickFinish() {
                        @Override
                        public void imagePickFinsh(int code, List uris,List<String> paths) {
                            itemPickFinsh.imagePickFinsh(code,uris,paths);
                            finish();
                        }
                    };
                    RecorderSetting recorderSetting = new RecorderSetting();

                    mGlobalSetting = MultiMediaSetting.from(ActivityTypes.this).choose(MimeType.ofAll());
                    mGlobalSetting.recorderSetting(recorderSetting);
                    openMain();
                }

                //


            }
        });


        gv.setAdapter(mAdapter);

    }


    protected void openMain() {

        openMain(0,0,0);

    }


    protected void openMain(int alreadyImageCount, int alreadyVideoCount, int alreadyAudioCount) {

        Context mContext = getApplication();
        String appid = mContext.getApplicationInfo().processName;
        mGlobalSetting
                // 设置路径和7.0保护路径等等
                .allStrategy(new SaveStrategy(true, appid + ".fileProvider", "aabb"))
                // for glide-V4
                .imageEngine(new Glide4Engine())
                // 最大5张图片、最大3个视频、最大1个音频
                .maxSelectablePerMediaType(null,
                        5,
                        3,
                        3,
                        alreadyImageCount,
                        alreadyVideoCount,
                        alreadyAudioCount)
                .forResult(new OnResultCallbackListener() {
                    @Override
                    public void onResult(@NonNull List<? extends LocalFile> list) {
                        ArrayList al = new ArrayList();
                        ArrayList fs = new ArrayList();
                        for(LocalFile o : list){
                            al.add(o.getPath());
                            fs.add(o.getPath());
                        }
                        ItemPickManager.itemPickFinsh.imagePickFinsh(1,al,fs);
                    }

                    @Override
                    public void onResultFromPreview(@NonNull List<? extends MultiMedia> list, boolean b) {

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            // 如果是在预览界面点击了确定
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                // 获取选择的数据
                ArrayList<MultiMedia> selected = MultiMediaSetting.obtainMultiMediaResult(data);
                if (selected == null) {
                    return;
                }
//                // 循环判断，如果不存在，则删除
//                for (int i = getMaskProgressLayout().getImagesAndVideos().size() - 1; i >= 0; i--) {
//                    int k = 0;
//                    for (MultiMedia multiMedia : selected) {
//                        if (!getMaskProgressLayout().getImagesAndVideos().get(i).equals(multiMedia)) {
//                            k++;
//                        }
//                    }
//                    if (k == selected.size()) {
//                        // 所有都不符合，则删除
//                        getMaskProgressLayout().removePosition(i);
//                    }
//                }
            } else {
                List<LocalFile> result = MultiMediaSetting.obtainLocalFileResult(data);
                for (LocalFile localFile : result) {
                    // 绝对路径,AndroidQ如果存在不属于自己App下面的文件夹则无效
                    Log.i(TAG, "onResult id:" + localFile.getId());
                    Log.i(TAG, "onResult 绝对路径:" + localFile.getPath());
                    Log.d(TAG, "onResult 旧图路径:" + localFile.getOldPath());
                    Log.d(TAG, "onResult 原图路径:" + localFile.getOriginalPath());
                    Log.i(TAG, "onResult Uri:" + localFile.getUri());
                    Log.d(TAG, "onResult 旧图Uri:" + localFile.getOldUri());
                    Log.d(TAG, "onResult 原图Uri:" + localFile.getOriginalUri());
                    Log.i(TAG, "onResult 文件大小: " + localFile.getSize());
                    Log.i(TAG, "onResult 视频音频长度: " + localFile.getDuration());
                    Log.i(TAG, "onResult 是否选择了原图: " + localFile.isOriginal());
                    if (localFile.isImageOrGif()) {
                        if (localFile.isImage()) {
                            Log.d(TAG, "onResult 图片类型");
                        } else if (localFile.isImage()) {
                            Log.d(TAG, "onResult 图片类型");
                        }
                    } else if (localFile.isVideo()) {
                        Log.d(TAG, "onResult 视频类型");
                    } else if (localFile.isAudio()) {
                        Log.d(TAG, "onResult 音频类型");
                    }
                    Log.i(TAG, "onResult 具体类型:" + localFile.getMimeType());
                    // 某些手机拍摄没有自带宽高，那么我们可以自己获取
                    if (localFile.getWidth() == 0 && localFile.isVideo()) {
                        MediaExtraInfo mediaExtraInfo = MediaUtils.getVideoSize(getApplication(), localFile.getPath());
                        localFile.setWidth(mediaExtraInfo.getWidth());
                        localFile.setHeight(mediaExtraInfo.getHeight());
                        localFile.setDuration(mediaExtraInfo.getDuration());
                    }
                    Log.i(TAG, "onResult 宽高: " + localFile.getWidth() + "x" + localFile.getHeight());
                }
//                          getMaskProgressLayout().addLocalFileStartUpload(result);
            }
        }
    }

    public class Gnode {
        private int iId;
        private String iName;

        public int getItype() {
            return itype;
        }

        public void setItype(int itype) {
            this.itype = itype;
        }

        private int itype;

        public String getIcount() {
            return icount;
        }

        public void setIcount(String icount) {
            this.icount = icount;
        }

        private String icount;

        public Gnode() {
        }

        public Gnode(int iId, String iName,int type,String icount) {
            this.iId = iId;
            this.iName = iName;
            this.icount = icount;
            this.itype = type;
        }

        public int getiId() {
            return iId;
        }

        public String getiName() {
            return iName;
        }

        public void setiId(int iId) {
            this.iId = iId;
        }

        public void setiName(String iName) {
            this.iName = iName;
        }
    }



}