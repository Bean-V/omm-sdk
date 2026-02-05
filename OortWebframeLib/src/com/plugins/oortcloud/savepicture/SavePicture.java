package com.plugins.oortcloud.savepicture;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 * @Company: 奥尔特云（深圳）智慧科技有限公司
 * @Author: lukezhang
 * @Date: 2022/8/11 0:24
 */
public class SavePicture extends CordovaPlugin {

    private Context context;
    private CallbackContext mCallbackContext;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        if (action.equals("save_action")){
            String base64;
            try {
                base64 = args.getString(0);

            } catch (JSONException e) {
                callbackContext.error("参数错误");
                return true;
            }
            Bitmap bitmap = stringToBitmap(base64);
            if (bitmap != null) {
                savePhoto(bitmap);
            }else{
                callbackContext.error("保存失败");
            }
            return true;
        }


        return super.execute(action, args, callbackContext);
    }

    private void savePhoto(Bitmap stringToBitmap) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (saveImage29(stringToBitmap)){
                mCallbackContext.success("保存成功");
            }else{
                mCallbackContext.error("保存失败");
            }
        }else{
            if (saveImage(stringToBitmap)){
                mCallbackContext.success("保存成功");
            }else {
                mCallbackContext.error("保存失败");
            }
        }
    }


    public static Bitmap stringToBitmap(String string) {

        Bitmap bitmap = null;

        try {

            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);

            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return bitmap;

    }



    /**
     * API 29及以下保存图片到相册的方法
     *
     * @param toBitmap 要保存的图片
     */
    private boolean saveImage(Bitmap toBitmap) {
        String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), toBitmap, "截图", "推广二维码");
        if (!TextUtils.isEmpty(insertImage)) {
            Toast.makeText(context, "图片保存成功!" + insertImage, Toast.LENGTH_SHORT).show();
            Log.d("打印保存路径", insertImage + "-");
            return true;
        }
        return false;
    }

    /**
     * API29 中的最新保存图片到相册的方法
     */
    private boolean saveImage29(Bitmap toBitmap) {
        //开始一个新的进程执行保存图片的操作
        Uri insertUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        //使用use可以自动关闭流
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri, "rw");
            if (toBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                Log.d("保存成功", "success");
                return true;
            } else {
                Log.e("保存失败", "fail");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
