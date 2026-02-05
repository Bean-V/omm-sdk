package com.oort.weichat.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RadioButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 适配RadioButton的图片加载工具：GET请求+本地缓存
 */
public class SimpleImageLoader {
    private static final String TAG = "TabManager_SimpleImageLoader";
    private static final String CACHE_DIR = "tab_icon_cache";

    private final Context mContext;

    public SimpleImageLoader(Context context) {
        this.mContext = context;
    }

    /**
     * 加载图片到RadioButton的复合图标（顶部）
     * @param imageUrl 图片URL
     * @param radioButton 目标RadioButton
     * @param defaultResId 加载失败的默认图标
     */
    public void loadImage(String imageUrl, RadioButton radioButton, int defaultResId) { // 第二个参数改为RadioButton
        if (imageUrl == null || imageUrl.isEmpty()) {
            setDefaultImage(radioButton, defaultResId);
            return;
        }

        // 生成缓存文件名（URL的MD5）
        String cacheFileName = MD5Util.md5(imageUrl) + ".png";
        File cacheFile = new File(getCacheDir(), cacheFileName);

        // 检查本地缓存
        if (cacheFile.exists() && cacheFile.length() > 0) {
            Log.d(TAG, "使用本地缓存：" + cacheFile.getAbsolutePath());
            Bitmap bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            if (bitmap != null) {
                setRadioButtonIcon(radioButton, bitmap); // 设置到RadioButton
            } else {
                Log.w(TAG, "本地缓存损坏，删除并重载");
                cacheFile.delete();
                downloadAndCacheImage(imageUrl, radioButton, cacheFile, defaultResId);
            }
            return;
        }

        // 无缓存，下载并缓存
        Log.d(TAG, "无本地缓存，发起GET请求：" + imageUrl);
        downloadAndCacheImage(imageUrl, radioButton, cacheFile, defaultResId);
    }

    /**
     * 下载图片并缓存
     */
    private void downloadAndCacheImage(String imageUrl, RadioButton radioButton, File cacheFile, int defaultResId) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                // （保持原网络请求逻辑不变）
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL(imageUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    if (connection.getResponseCode() == 200) {
                        inputStream = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            saveBitmapToFile(bitmap, cacheFile);
                            return bitmap;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "GET请求失败：" + e.getMessage());
                } finally {
                    if (inputStream != null) {
                        try { inputStream.close(); } catch (IOException e) { e.printStackTrace(); }
                    }
                    if (connection != null) connection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    Log.d(TAG, "下载并缓存成功：" + cacheFile.getAbsolutePath());
                    setRadioButtonIcon(radioButton, bitmap); // 设置到RadioButton
                } else {
                    Log.e(TAG, "下载失败，显示默认图标");
                    setDefaultImage(radioButton, defaultResId);
                }
            }
        }.execute();
    }

    /**
     * 将Bitmap设置为RadioButton的顶部图标
     */
    private void setRadioButtonIcon(RadioButton radioButton, Bitmap bitmap) {
        Drawable icon = new BitmapDrawable(mContext.getResources(), bitmap);
        // 设置图标到RadioButton的顶部（与原逻辑一致）
        radioButton.setCompoundDrawablesWithIntrinsicBounds(
                null, // 左
                icon, // 上（图标位置）
                null, // 右
                null  // 下
        );
    }

    /**
     * 设置默认图标到RadioButton
     */
    private void setDefaultImage(RadioButton radioButton, int defaultResId) {
        Drawable defaultIcon = mContext.getResources().getDrawable(defaultResId);
        radioButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                defaultIcon,
                null,
                null
        );
    }

    // （以下方法保持不变）
    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Log.d(TAG, "图片缓存成功：" + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "图片缓存失败：" + e.getMessage());
        }
    }

    private File getCacheDir() {
        return new File(mContext.getCacheDir(), CACHE_DIR);
    }
}