package com.oortcloud.clouddisk.transfer.upload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.oortcloud.clouddisk.http.HttpConstants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/19 16:55
 * @version： v1.0
 * @function： fastaf 上传文件
 */
public class FastafUploadService  extends Service {


    public static  final HashMap<String, Call> downCalls = new HashMap<>(); // 处理暂停下载
    public static  final HashMap<String, UploadListener> listeners = new HashMap<>(); //用来存放各个下载的请求
    private OkHttpClient mHttpClient;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FastafUploadService.UploadBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(5, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(5, TimeUnit.SECONDS)//设置读操作超时时间
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }


    /**
     * 获取当前Service的实例
     * @return
     */
    public class UploadBinder extends Binder {

        public FastafUploadService getService(){
            return FastafUploadService.this;
        }
    }

    public void startUpload(UploadListener uploadListener , File file){
        listeners.put(file.getPath() , uploadListener);
        UploadResponseHandler handler =    new UploadResponseHandler( uploadListener);
        new Thread(()->{
            uploadFile(handler , file);
        }).start();


    }

    //上传文件
    public  void uploadFile(UploadResponseHandler handler ,File file)   {

        Call call = uploadFile(file, handler);

        if (call != null){
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v("msg" , e.toString());
                    if (e.toString().contains("Socket closed") ||e.toString().contains("Canceled")){
                        //暂停
                        handler.sendPauseMessage();
                    }else {
                        handler.sendFailureMessage(UploadResponseHandler.FailureCode.Socket);

                        downCalls.remove(file.getPath());
                        listeners.remove(file.getPath());
                    }

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();

                    Log.v("msg" , "-----s"+s );
//                    Result<BlockData> result = new Gson().fromJson(s, new TypeToken<Result<BlockData>>() {}.getType());
//                    if (result.isOk()){
//
//                                HttpResult.listFile(listeners..getDir(), "", "", 1, 50, "");
//                                handler.sendFinishMessage();
//                                downCalls.remove(file.getPath());
//                                listeners.remove(file.getPath());
//
//
//
//                        }
//                    }

                }

            });
        }


    }

    /**
     *  文件上传
     * @param file 文件
     * @param handler 回调
     * @return
     */
    public  Call  uploadFile(File file , UploadResponseHandler handler ) {

        // 构建请求 Body
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("file",file.getName(),
                        RequestBody.create(file, MediaType.parse("multipart/form-data")));

        // 这是使用了加强的MultipartBody,通过代理该类的输出流操作，
        // 获取流的总大小和每次写入的大小

        ExMultipartBody exMultipartBody = new ExMultipartBody(builder.build() , handler);
        try {
            //开始
            handler.sendStartMessage(exMultipartBody.contentLength());
        }catch (Exception e){}

        //监上传文件的进度 构建请求
        final Request request = new Request.Builder()
                .url(HttpConstants.GATEWAY_URL + HttpConstants.RESUME_BIG_FILE)
                .post(exMultipartBody).build();

        Call call = null;
        if (handler.isDownloading()){
            call = mHttpClient.newCall(request);
            downCalls.put(file.getPath()  , call);

        }else {
            handler.sendPauseMessage();
        }

        return call;
    }

}
