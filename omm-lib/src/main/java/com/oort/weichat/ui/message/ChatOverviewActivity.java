package com.oort.weichat.ui.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.example.qrcode.utils.DecodeUtils;
import com.google.zxing.Result;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.adapter.ChatOverviewAdapter;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.share.ShareToChatActivity;
import com.oort.weichat.ui.tool.MultiImagePreviewActivity;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.BitmapUtil;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.view.SaveWindow;
import com.oort.weichat.view.imageedit.IMGEditActivity;

import java.io.File;
import java.util.List;

public class ChatOverviewActivity extends BaseActivity {
    public static final int REQUEST_IMAGE_EDIT = 1;
    public static String imageChatMessageListStr;
    private ViewPager mViewPager;
    private ChatOverviewAdapter mChatOverviewAdapter;
    private List<ChatMessage> mChatMessages;
    private int mFirstShowPosition;
    private String mCurrentShowUrl;
    private String mEditedPath;
    private SaveWindow mSaveWindow;
    private My_BroadcastReceivers my_broadcastReceiver = new My_BroadcastReceivers();
    private Rect[] rects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_overview);
        // imageChatMessageListStr = getIntent().getStringExtra("imageChatMessageList");
        mChatMessages = JSON.parseArray(imageChatMessageListStr, ChatMessage.class);
        imageChatMessageListStr = "";
        if (mChatMessages == null) {
            finish();
            return;
        }
        mFirstShowPosition = getIntent().getIntExtra("imageChatMessageList_current_position", 0);
        getCurrentShowUrl(mFirstShowPosition);

        initView();
        register();
        hideStatusBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (my_broadcastReceiver != null) {
            unregisterReceiver(my_broadcastReceiver);
        }
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mViewPager = findViewById(R.id.chat_overview_vp);
        mChatOverviewAdapter = new ChatOverviewAdapter(this, mChatMessages);
        mViewPager.setAdapter(mChatOverviewAdapter);
        mViewPager.setCurrentItem(mFirstShowPosition);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                getCurrentShowUrl(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


    }


    private void getCurrentShowUrl(int position) {
        if (position >= mChatMessages.size()) {
            // 以防万一，静态变量可能导致各种无法预料的崩溃，
            return;
        }
        ChatMessage chatMessage = mChatMessages.get(position);
        if (!TextUtils.isEmpty(chatMessage.getFilePath()) && FileUtil.isExist(chatMessage.getFilePath())) {
            mCurrentShowUrl = chatMessage.getFilePath();
        } else {
            mCurrentShowUrl = chatMessage.getContent();
        }
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OtherBroadcast.singledown);
        filter.addAction(OtherBroadcast.longpress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(my_broadcastReceiver, filter,Context.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_EDIT:
                    mCurrentShowUrl = mEditedPath;
                    ChatMessage chatMessage = mChatMessages.get(mViewPager.getCurrentItem());
                    chatMessage.setFilePath(mCurrentShowUrl);
                    mChatMessages.set(mViewPager.getCurrentItem(), chatMessage);
                    mChatOverviewAdapter.refreshItem(mCurrentShowUrl, mViewPager.getCurrentItem());
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class My_BroadcastReceivers extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(OtherBroadcast.singledown)) {
                finish();
            } else if (intent.getAction().equals(OtherBroadcast.longpress)) {
                // 长按屏幕，弹出菜单
                mSaveWindow = new SaveWindow(ChatOverviewActivity.this,
                        BitmapUtil.getImageIsQRcode(ChatOverviewActivity.this, mCurrentShowUrl), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSaveWindow.dismiss();
                        int id = v.getId();
                        if (id == R.id.share_image_chat) {
                            ShareToChatActivity.startShareImageUrl(ChatOverviewActivity.this, mCurrentShowUrl);
                        } else if (id == R.id.share_image_other_apps) {
                            ImageLoadHelper.loadFile(ChatOverviewActivity.this, mCurrentShowUrl, new ImageLoadHelper.FileSuccessCallback() {
                                @Override
                                public void onSuccess(File f) {
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.setType("*/*");
                                    Uri uri = null;
                                    if (f != null) {
                                        //这部分代码主要功能是判断了下文件是否存在，在android版本高过7.0（包括7.0版本）
                                        //当前APP是不能直接向外部应用提供file开头的的文件路径，
                                        //需要通过FileProvider转换一下。否则在7.0及以上版本手机将直接crash。
                                        try {
                                            ApplicationInfo applicationInfo = mContext.getApplicationInfo();
                                            int targetSDK = applicationInfo.targetSdkVersion;
                                            if (targetSDK >= Build.VERSION_CODES.N &&
                                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                //uri = FileProvider.getUriForFile(mContext,
                                                //mContext.getApplicationInfo().processName + ".fileprovider", picFile);

                                                uri = FileProvider.getUriForFile(mContext, mContext.getApplicationInfo().processName + ".sharing.provider", f);//.processName

                                            } else {
                                                uri = Uri.fromFile(f);
                                            }
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(Intent.createChooser(shareIntent, "分享"));
                                }
                            });
                        } else if (id == R.id.save_image) {
                            FileUtil.downImageToGallery(ChatOverviewActivity.this, mCurrentShowUrl);
                        } else if (id == R.id.edit_image) {
                            ImageLoadHelper.loadFile(
                                    ChatOverviewActivity.this,
                                    mCurrentShowUrl,
                                    f -> {
                                        mEditedPath = FileUtil.createImageFileForEdit().getAbsolutePath();
                                        IMGEditActivity.startForResult(ChatOverviewActivity.this, Uri.fromFile(f), mEditedPath, REQUEST_IMAGE_EDIT);
                                    });
                        } else if (id == R.id.identification_qr_code) {// 识别图中二维码
                            ImageLoadHelper.loadFile(
                                    ChatOverviewActivity.this,
                                    mCurrentShowUrl,
                                    f -> {
                                        AsyncUtils.doAsync(mContext, t -> {
                                            Reporter.post("二维码解码失败，" + f.getCanonicalPath(), t);
                                            runOnUiThread(() -> {
                                                Toast.makeText(ChatOverviewActivity.this, R.string.decode_failed, Toast.LENGTH_SHORT).show();
                                            });
                                        }, t -> {
                                            // 做些预处理提升扫码成功率，
                                            // 预读一遍获取图片比例，使用inSampleSize压缩图片分辨率到恰到好处，
                                            Uri decodeUri = Uri.fromFile(f);
                                            Bitmap bitmap = BitmapUtil.getImageIsQRcode(ChatOverviewActivity.this, null, mCurrentShowUrl);
                                            Log.e("zx", "onClick: " + bitmap);
                                            final Result result = DecodeUtils.decodeFromPicture(
                                                    bitmap == null ? DecodeUtils.compressPicture(t.getRef(), decodeUri) : bitmap
                                            );
                                            t.uiThread(c -> {
                                                if (result != null && !TextUtils.isEmpty(result.getText())) {
                                                    HandleQRCodeScanUtil.handleScanResult(mContext, result.getText());
                                                } else {
                                                    Toast.makeText(ChatOverviewActivity.this, R.string.decode_failed, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        });
                                    }
                            );
                        }
                    }
                });
                mSaveWindow.show();
            }
        }
    }
    public void hideStatusBar(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        if (window == null) return;


        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(lp);
    }
}
