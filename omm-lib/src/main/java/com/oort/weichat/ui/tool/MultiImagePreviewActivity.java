package com.oort.weichat.ui.tool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.qrcode.utils.DecodeUtils;
import com.google.zxing.Result;
import com.oort.weichat.AppConstant;
import com.oort.weichat.R;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.message.HandleQRCodeScanUtil;
import com.oort.weichat.ui.share.ShareToChatActivity;
import com.oort.weichat.util.BitmapUtil;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.view.SaveWindow;
import com.oort.weichat.view.ZoomImageView;
import com.oort.weichat.view.imageedit.IMGEditActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片集的预览
 */
public class MultiImagePreviewActivity extends BaseActivity {
    public static final int REQUEST_IMAGE_EDIT = 1;

    SparseArray<View> mViews = new SparseArray<View>();
    private ArrayList<String> mImages;
    private int mPosition;
    private boolean mChangeSelected;
    private ViewPager mViewPager;
    private ImagesAdapter mAdapter;
    private CheckBox mCheckBox;
    private TextView mIndexCountTv;
    private List<Integer> mRemovePosition = new ArrayList<Integer>();
    private String imageUrl;
    private String mRealImageUrl;// 因为viewPager的预加载机制，需要记录当前页面真正的url
    private String mEditedPath;
    private SaveWindow mSaveWindow;
    private My_BroadcastReceivers my_broadcastReceiver = new My_BroadcastReceivers();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_preview);

        if (getIntent() != null) {
            mImages = (ArrayList<String>) getIntent().getSerializableExtra(AppConstant.EXTRA_IMAGES);
            for (String mImage : mImages) {
                Log.e("MultiImage", "dateSource:" + mImage);
            }
            mPosition = getIntent().getIntExtra(AppConstant.EXTRA_POSITION, 0);
            mChangeSelected = getIntent().getBooleanExtra(AppConstant.EXTRA_CHANGE_SELECTED, false);
        }
        if (mImages == null) {
            mImages = new ArrayList<String>();
        }

        initView();
        register();

        hideStatusBar(this);
    }

    private void doFinish() {
        if (mChangeSelected) {
            Intent intent = new Intent();
            ArrayList<String> resultImages = null;
            if (mRemovePosition.size() == 0) {
                resultImages = mImages;
            } else {
                resultImages = new ArrayList<String>();
                for (int i = 0; i < mImages.size(); i++) {
                    if (!isInRemoveList(i)) {
                        resultImages.add(mImages.get(i));
                    }
                }
            }
            intent.putExtra(AppConstant.EXTRA_IMAGES, resultImages);
            setResult(RESULT_OK, intent);
        }
        finish();
        overridePendingTransition(0, 0);// 关闭过场动画
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    @Override
    protected boolean onHomeAsUp() {
        doFinish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (my_broadcastReceiver != null) {
            unregisterReceiver(my_broadcastReceiver);
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndexCountTv = (TextView) findViewById(R.id.index_count_tv);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mViewPager.setPageMargin(10);

        mAdapter = new ImagesAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateSelectIndex(mPosition);

        if (mPosition < mImages.size()) {
            mViewPager.setCurrentItem(mPosition);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                updateSelectIndex(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void updateSelectIndex(final int index) {
        if (mPosition >= mImages.size()) {
            mIndexCountTv.setText(null);
        } else {
            mRealImageUrl = mImages.get(index);
            mIndexCountTv.setText((index + 1) + "/" + mImages.size());
        }

        if (!mChangeSelected) {
            mCheckBox.setVisibility(View.GONE);
            return;
        }

        mCheckBox.setOnCheckedChangeListener(null);
        boolean removed = isInRemoveList(index);
        if (removed) {
            mCheckBox.setChecked(false);
        } else {
            mCheckBox.setChecked(true);
        }
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    removeFromRemoveList(index);
                } else {
                    addInRemoveList(index);
                }
            }
        });
    }

    void addInRemoveList(int position) {
        if (!isInRemoveList(position)) {
            mRemovePosition.add(Integer.valueOf(position));
        }
    }

    void removeFromRemoveList(int position) {
        if (isInRemoveList(position)) {
            mRemovePosition.remove(Integer.valueOf(position));
        }
    }

    boolean isInRemoveList(int position) {
        return mRemovePosition.indexOf(Integer.valueOf(position)) != -1;
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OtherBroadcast.singledown);
        filter.addAction(OtherBroadcast.longpress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(my_broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(my_broadcastReceiver, filter);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_EDIT:
                    mRealImageUrl = mEditedPath;
                    mImages.set(mViewPager.getCurrentItem(), mEditedPath);
                    // 刷新当前页面，
                    mAdapter.refreshCurrent();
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
                doFinish();
            } else if (intent.getAction().equals(OtherBroadcast.longpress)) {
                // 长按屏幕，弹出菜单
                if (TextUtils.isEmpty(imageUrl)) {
                    Toast.makeText(MultiImagePreviewActivity.this, getString(R.string.image_is_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                mSaveWindow = new SaveWindow(MultiImagePreviewActivity.this, BitmapUtil.getImageIsQRcode(MultiImagePreviewActivity.this, mRealImageUrl), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSaveWindow.dismiss();
                        int id = v.getId();
                        if (id == R.id.share_image_chat) {
                            ShareToChatActivity.startShareImageUrl(MultiImagePreviewActivity.this, mRealImageUrl);
                        } else if (id == R.id.share_image_other_apps) {
                            ImageLoadHelper.loadFile(MultiImagePreviewActivity.this, mRealImageUrl, new ImageLoadHelper.FileSuccessCallback() {
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
                            FileUtil.downImageToGallery(MultiImagePreviewActivity.this, mRealImageUrl);
                        } else if (id == R.id.edit_image) {
                            ImageLoadHelper.loadFile(
                                    MultiImagePreviewActivity.this,
                                    mRealImageUrl,
                                    f -> {
                                        mEditedPath = FileUtil.createImageFileForEdit().getAbsolutePath();
                                        IMGEditActivity.startForResult(MultiImagePreviewActivity.this, Uri.fromFile(f), mEditedPath, REQUEST_IMAGE_EDIT);
                                    }
                            );
                        } else if (id == R.id.identification_qr_code) {// 识别图中二维码
                            ImageLoadHelper.loadBitmapCenterCropDontAnimateWithError(
                                    mContext,
                                    mRealImageUrl,
                                    R.drawable.image_download_fail_icon,
                                    b -> {
                                        new Thread(() -> {
                                            final Result result = DecodeUtils.decodeFromPicture(b);
                                            mViewPager.post(() -> {
                                                if (result != null && !TextUtils.isEmpty(result.getText())) {
                                                    HandleQRCodeScanUtil.handleScanResult(mContext, result.getText());
                                                } else {
                                                    Toast.makeText(MultiImagePreviewActivity.this, R.string.decode_failed, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }).start();
                                    }, e -> {
                                        Toast.makeText(MultiImagePreviewActivity.this, R.string.unrecognized, Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    }
                });
                mSaveWindow.show();
            }
        }
    }

    class ImagesAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void refreshCurrent() {
            AvatarHelper.getInstance().displayUrl(mRealImageUrl, (ZoomImageView) mViews.get(mViewPager.getCurrentItem()));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mViews.get(position);
            if (view == null) {
                view = new ZoomImageView(MultiImagePreviewActivity.this);
                mViews.put(position, view);
            }
            // init status
            imageUrl = mImages.get(position);

            // copy from com.oort.weichat.ui.tool.SingleImagePreviewActivity.initView
            ImageView mImageView = (ImageView) view;
            String mImageUri = imageUrl;
            // 网络加载
            if (mImageUri.endsWith(".gif")) {
                ImageLoadHelper.showGifWithError(
                        mContext,
                        mImageUri,
                        R.drawable.image_download_fail_icon,
                        mImageView
                );
            } else {
                ImageLoadHelper.loadBitmapCenterCropDontAnimateWithError(
                        mContext,
                        mImageUri,
                        R.drawable.image_download_fail_icon,
                        b -> {
                            mImageView.setImageBitmap(b);
                        }, e -> {
                            mImageView.setImageResource(R.drawable.image_download_fail_icon);
                        });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = mViews.get(position);
            if (view == null) {
                super.destroyItem(container, position, object);
            } else {
                container.removeView(view);
            }
        }
    }
    public static void hideStatusBar(Activity activity) {
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
