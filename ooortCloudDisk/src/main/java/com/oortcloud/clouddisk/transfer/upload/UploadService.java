package com.oortcloud.clouddisk.transfer.upload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oortcloud.clouddisk.BaseApplication;
import com.oortcloud.clouddisk.bean.BlockData;
import com.oortcloud.clouddisk.bean.MD5FileData;
import com.oortcloud.clouddisk.bean.Result;
import com.oortcloud.clouddisk.http.HttpConstants;
import com.oortcloud.clouddisk.http.HttpRequestCenter;
import com.oortcloud.clouddisk.http.HttpResult;
import com.oortcloud.clouddisk.http.bus.RxBusSubscriber;
import com.oortcloud.clouddisk.transfer.Status;
import com.oortcloud.clouddisk.utils.file.FileSafeCode;
import com.oortcloud.clouddisk.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
 * @author: zzj/@date: 2021/1/15 09:30
 * @version： v1.0
 * @function：
 */
public class UploadService extends Service {

    public static final HashMap<String, Call> downCalls = new HashMap<>(); // 处理暂停下载
    public static final HashMap<String, Thread> mThread = new HashMap<>(); // 处理暂停下载
    public static final HashMap<String, UploadListener> listeners = new HashMap<>(); //用来存放各个回调
    private OkHttpClient mHttpClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UploadBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(15, TimeUnit.SECONDS)//设置写操作超时时间
                .readTimeout(15, TimeUnit.SECONDS)//设置读操作超时时间
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

        public UploadService getService(){
            return UploadService.this;
        }
    }

    public void startUpload(UploadInfo uploadInfo , UploadListener uploadListener ,File file){
        if (listeners.get(file.getPath()) == null){
            listeners.put(file.getPath() , uploadListener);
        }
        UploadResponseHandler handler =    new UploadResponseHandler( uploadListener);

        handler.sendStartMessage(file.length());
        new Thread(()->{
            try {

//                String sha1 = FileSafeCode.getSha1(file);
                String md5 = FileSafeCode.getFileMD5(file);
                //创建回调
                md5File(uploadInfo , "" , md5 , file ,handler);
            }catch (Exception e){

            }

        }).start();


    }
    /**
     * 检查文件是否可以秒传
     *
     * @param uploadInfo  下载信息
     * @param fileSha1 文件的sha1值
     * @param fileMd5  文件的md5值
     * @param file     文件
     * @return
     */
    public  void md5File(UploadInfo uploadInfo, String fileSha1, String fileMd5, @NotNull File file , UploadResponseHandler handler) {

        HttpRequestCenter.md5File(uploadInfo.getDir(), fileSha1, fileMd5, file.length(), file.getName()).subscribe(new RxBusSubscriber<String>() {
            @Override
            protected void onEvent(String s) {
                Result<MD5FileData> result = new Gson().fromJson(s, new TypeToken<Result<MD5FileData>>() {}.getType());
                if (result.isOk()) {
                    MD5FileData md5FileData = result.getData();
                    if (md5FileData != null) {
                        if (md5FileData.getFile_exists()) {
//                            ToastUtils.showContent(file.getName() + "已上传云盘");
                            HttpResult.fileList(uploadInfo.getDir(),  "", 1, 50, "");
                            uploadInfo.setCtime(md5FileData.getFile().getCtime());
                            uploadInfo.setContentLength(md5FileData.getFile().getSize());
                            uploadInfo.setStatus(Status.SUCCESS);
                            handler.sendFinishMessage();
                            listeners.remove(uploadInfo.getFile_path());

                        } else {
                            md5FileData.setDir(uploadInfo.getDir());

                            uploadFile(handler , file, md5FileData , md5FileData.getBlock().getOffset());



                        }
                    }

                } else {
                    int resultCode = result.getCode();
                    if (resultCode == 4302){
                        ToastUtils.showContent(file.getName()+"已上传云盘");
                    }else if (resultCode == 4310 ||resultCode == 4315){
                        ToastUtils.showContent(file.getName()+"文件过大，超出存储空间");
                    }
                }

            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
    //上传文件
    public  void uploadFile(UploadResponseHandler handler ,File file, MD5FileData md5FileData ,long offset)   {
        Call call = uploadFile(file, handler ,md5FileData ,offset);

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
                    Result<BlockData> result = new Gson().fromJson(s, new TypeToken<Result<BlockData>>() {}.getType());
                    if (result.isOk()){
                        BlockData blockData = result.getData();
                        if ( blockData != null){
                            if (blockData.isFile_up_finish()){
                                downCalls.remove(file.getPath());
                                listeners.remove(file.getPath());
                                handler.sendFinishMessage();
                                HttpResult.fileList(md5FileData.getDir(), "", 1, 50, "");

                            }else {
                                uploadFile(handler  , file , md5FileData , blockData.getOffset());
                            }

                        }
                    }

                }

            });
        }


    }

    /**
     *  文件上传
     * @param file 文件
     * @param handler 回调
     * @param md5FileData 上传信息
     * @return
     */
    public  Call  uploadFile(File file , UploadResponseHandler handler , MD5FileData md5FileData  ,long offset) {

        // 构建请求 Body
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        byte[] blockBytes =  getBlock( offset , file ,  (int)md5FileData.getBlock().getMaxblocksize());
        String md5 = "";
        try {
            md5 = FileSafeCode.getMD5(blockBytes);
        }catch (Exception e){}

        builder.addFormDataPart("accessToken", BaseApplication.TOKEN)
                .addFormDataPart("dir" ,  md5FileData.getDir())
                .addFormDataPart("name" ,  md5FileData.getBlock().getFilename())
                .addFormDataPart("tagid" ,  md5FileData.getBlock().getTagid())
                .addFormDataPart("blockmd5" ,md5 )
                .addFormDataPart("offset" ,  String.valueOf(offset))
                .addFormDataPart("blockfile",file.getName(),
                        RequestBody.create(blockBytes, MediaType.parse("multipart/form-data")));

        // 这是使用了加强的MultipartBody,通过代理该类的输出流操作，
        // 获取流的总大小和每次写入的大小

        ExMultipartBody exMultipartBody = new ExMultipartBody(builder.build() , handler);

        //监上传文件的进度 构建请求
        final Request request = new Request.Builder()
                .url(HttpConstants.GATEWAY_URL + HttpConstants.UP_FILE_BLOCK)
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

    /**
     * 文件分块
     * @param offset 起始偏移位置
     * @param file 文件
     * @param blockSize 分块大小
     * @return 分块数据
     */
    private static byte[] getBlock(long offset, File file, int blockSize) {

        byte[] result = new byte[blockSize];

        RandomAccessFile accessFile = null;

        try {

            accessFile = new RandomAccessFile(file, "r");

            accessFile.seek(offset);

            int readSize = accessFile.read(result);

            if (readSize == -1) {

                return null;

            } else if (readSize == blockSize) {

                return result;

            } else {

                byte[] tmpByte = new byte[readSize];

                System.arraycopy(result, 0, tmpByte, 0, readSize);

                return tmpByte;

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (accessFile != null) {

                try {

                    accessFile.close();

                } catch (IOException e1) {

                }

            }

        }

        return null;

    }
    public void cancel(String url) {
        Call call = downCalls.get(url);
        if (call != null) {
            call.cancel();//取消
        }
        Log.v("msg" , "----------cancel----");
        downCalls.remove(url);
//        listeners.remove(url);
    }



}
