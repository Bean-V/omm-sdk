package com.oortcloud.oort_zhifayi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static boolean compressAndReplace(String originalImagePath) {
        // 加载原图片
        Bitmap originalBitmap = BitmapFactory.decodeFile(originalImagePath);
        if (originalBitmap == null) {
            return false;
        }

        // 压缩图片
        Bitmap compressedBitmap = compressBitmap(originalBitmap,originalImagePath);
        if (compressedBitmap == null) {
            originalBitmap.recycle();
            return false;
        }

        // 保存压缩后的图片到原路径文件
        boolean saved = saveBitmapToFile(compressedBitmap, originalImagePath);

        // 释放资源
        originalBitmap.recycle();
        compressedBitmap.recycle();

        return saved;
    }

    private static Bitmap compressBitmap(Bitmap bitmap,String originalImagePath) {
        // 设置压缩后的图片最大宽度和高度
        int maxWidth = 1024;
        int maxHeight = 1024;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算压缩比例
        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // 使用矩阵进行压缩
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 创建压缩后的图片
        Bitmap compressedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        // 如果原图有旋转信息，进行旋转修正
        try {
            ExifInterface exif = new ExifInterface(originalImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    compressedBitmap = rotateBitmap(compressedBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    compressedBitmap = rotateBitmap(compressedBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    compressedBitmap = rotateBitmap(compressedBitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressedBitmap;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static boolean saveBitmapToFile(Bitmap bitmap, String filePath) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
