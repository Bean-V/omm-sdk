package com.oortcloud.oort_zhifayi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.king.zxing.util.CodeUtils;
import com.oortcloud.basemodule.login.okhttp.HttpUtils;
import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
import com.oortcloud.basemodule.utils.DeviceIdFactory;
import com.oortcloud.basemodule.widget.xupdate.XUpdate;
import com.oortcloud.basemodule.widget.xupdate.entity.UpdateEntity;
import com.oortcloud.basemodule.widget.xupdate.entity.UpdateError;
import com.oortcloud.basemodule.widget.xupdate.listener.IUpdateParseCallback;
import com.oortcloud.basemodule.widget.xupdate.listener.OnUpdateFailureListener;
import com.oortcloud.basemodule.widget.xupdate.proxy.IUpdateHttpService;
import com.oortcloud.basemodule.widget.xupdate.proxy.IUpdateParser;
import com.oortcloud.basemodule.widget.xupdate.utils.UpdateUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ActivityHome extends ActivityBase {

    private Thread thread;
    private String[] neededPermissions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Aria.download(this).register();


        Bitmap mp = CodeUtils.createQRCode("网络有误，请稍后再试或者联系管理员",300);

        ImageView iv = findViewById(R.id.iv_image);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });


        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent1 = new Intent(ActivityHome.this, ActivityScrren.class);
                startActivity(intent1);
                return false;
            }
        });
        iv.setImageBitmap(mp);
        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CUser.taskCount = 3;
               // startActivity(new Intent(ActivityHome.this,MainActivity.class));
            }
        });
        getScanRes();

        hideNavigationBar();


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有获得权限，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1004);
        } else {
            // 已经有了权限，执行需要位置权限的操作
            // 在这里启动位置服务、获取位置等操作
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }



        Aria.download(this).register();
        initUpdate();





//        ((TextView) findViewById(R.id.tv_gpsinfo)).setText("v:"+(BuildConfig.VERSION_NAME));
//        ((TextView) findViewById(R.id.tv_code)).setText("编号:"+DeviceIdFactory.getSerialNumber(this));





    }

    @Override
    protected void onResume() {
        super.onResume();
        test = true;
        CUser.cUser = null;
        ZFYConstant.testToken = "";
        CUser.taskCount = 0;
        CUser.taskId  = "";
        mId = "";
        getData();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    void getData(){
// .url("https://multi.myoumuamua.com/bus/apaas-location-service/task/v1/qrcode_get")
        Log.e("lc_log" ,DeviceIdFactory.getSerialNumber());
        HttpUtils.get()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/qrcode_get")
                .params("no",DeviceIdFactory.getSerialNumber())
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        HashMap dictionary = result.getData();

                        Log.e("lc_log", "onResponse: "+dictionary.toString());
                        Bitmap mp = CodeUtils.createQRCode((String) dictionary.get("qrcode_data"),300);
                        mId = (String) dictionary.get("id");
                        ImageView iv = findViewById(R.id.iv_image);
                        iv.setImageBitmap(mp);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Log.e("lc_log", "Exception: "+e.toString());
                        Exception e1 = e;
                    }
                } );
    }
    private String mId = "";
    void getLoginProgress(){

        HttpUtils.get()
                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/qrcode_get")
                .build()
                .execute(new BaseCallback<HashMap>(HashMap.class) {
                    @Override
                    public void onResponse(ObjectResult<HashMap> result) {
                        HashMap dictionary = result.getData();
                        Bitmap mp = CodeUtils.createQRCode((String) dictionary.get("qrcode_data"),300);
                        mId = (String) dictionary.get("id");
                        ImageView iv = findViewById(R.id.iv_image);
                        iv.setImageBitmap(mp);
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e) {
                        Exception e1 = e;
                    }
                } );
    }

    int tcount = 0;
    boolean test = true;

    public void getScanRes() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {


                    if(!test || mId.isEmpty()){
                        continue;
                    }
                    try {
                        tcount++;
                        if (tcount > 400) {
                            //LogHelper.getInstance().setIS_WRITE_FILE(false);
                        }

                        HttpUtils.post()
                                .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/qrcode_status?no="+ DeviceIdFactory.getSerialNumber())
                                .params("id",mId)
                                .build()
                                .executeSync(new BaseCallback<HashMap>(HashMap.class) {

                                    @Override
                                    public void onResponse(ObjectResult<HashMap> result) {

                                        HashMap dictionary = result.getData();

                                        if(dictionary == null){

                                        }else if(dictionary.get("token") != null){

                                            ZFYConstant.testToken = (String) dictionary.get("token");
                                            test = false;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    CUser.taskCount = 3;
                                                    startActivity(new Intent(ActivityHome.this, ActivityTasks.class));
                                                }
                                            });
                                        }


                                    }

                                    @Override
                                    public void onError(okhttp3.Call call, Exception e) {
                                        Exception e1 = e;
                                    }
                                } );
                        Thread.sleep(2000);//每隔1s执行一次

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }
    void initUpdate(){
        boolean isSilentInstall = false;//FastSharedPreferences.get("USERINFO_SAVE").getBoolean("openUpdate",false);
        XUpdate.get()
                .debug(false)
                .isWifiOnly(false)                                               // By default, only version updates are checked under WiFi
                .isGet(false)                                                    // The default setting uses Get request to check versions
                .isAutoMode(isSilentInstall)                                              // The default setting is non automatic mode
                .param("versionCode", String.valueOf(UpdateUtils.getVersionCode(this)))         // Set default public request parameters
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     // Set listening for version update errors
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != 2004) {          // Handling different errors
                            Log.d("zlm",error.toString());
                        }
                    }
                })
                .supportSilentInstall(isSilentInstall)                                     // Set whether silent installation is supported. The default is true
                .setIUpdateHttpService(new OKHttpUpdateHttpService()  )     //new OKHttpUpdateHttpService()     //new OKHttpUpdateHttpService() // This must be set! Realize the network request function.
                .init(ActivityHome.this.getApplication());



        checkUpdate2();
    }
    private void checkUpdate2() {
        boolean isSilentInstall = false;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            HashMap<String,String> p = new HashMap();

//            p.put("VersionCode",BuildConfig.VERSION_CODE);
//            p.put("app_package",BuildConfig.APPLICATION_ID);


            XUpdate.newBuild(this)
                    .isGet(false)
                    .updateUrl("http://oort.oortcloudsmart.com:31610/" + "oort/oortcloud-appupgrade/v1/getAppVersion")
                    .params(p)
                    .isAutoMode(isSilentInstall)
                    .supportBackgroundUpdate(true)
                    .updateParser(new CustomUpdateParser())
                    .update();// Set up a custom version update parser
        }
    }


    public class CustomUpdateParser implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            Log.d("zlm",json);

            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(json);
            int code = jsonObject.getIntValue("code");
            String msg = jsonObject.getString("msg");

            if (code == 200) {
                com.alibaba.fastjson.JSONObject result = jsonObject.getJSONObject("data");
                long apkSize = result.getLongValue("ApkSize");
                int versionCode = result.getIntValue("VersionCode");
                String versionName = result.getString("VersionName");
                String updateLog = result.getString("ModifyContent");
                String apkUrl = result.getString("DownloadUrl");
                String md5 = result.getString("ApkMd5");
                boolean hasUpdate ;
                boolean isIgnorable ;
                boolean isForce ;
                int status = result.getIntValue("UpdateStatus");
                if (status == 2){  //强制更新
                    isForce = true;
                    isIgnorable = false;
                }else if (status == 1){  //选择更新
                    isForce = false;
                    isIgnorable = true;
                }else{   //不更新
                    isForce = false;
                    isIgnorable = true;
                }
                long currentVersionCode = getAppVersionCode(ActivityHome.this);
                if (versionCode > currentVersionCode && status > 0){  //有新版本
                    hasUpdate = true;
                }else{
                    hasUpdate = false;
                }
                if (result != null) {
                    return new UpdateEntity()
                            .setHasUpdate(hasUpdate)
                            .setIsIgnorable(isIgnorable)
                            .setVersionCode(versionCode)
                            .setVersionName(versionName)
                            .setUpdateContent(updateLog)
                            .setDownloadUrl(apkUrl)
                            .setSize(apkSize/1024)
                            .setForce(isForce)
                            .setMd5(md5);
                }
            }
            return null;
        }


        @Override
        public void parseJson(String json, IUpdateParseCallback callback) throws Exception {
            Log.d("zlm",json);
        }

        @Override
        public boolean isAsyncParser() {
            return false;
        }
    }


    public static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionUpdate", "[getAppVersionCode]-error：" + e.getMessage());
        }
        return appVersionCode;
    }


    private class OKHttpUpdateHttpService implements IUpdateHttpService {
        @Override
        public void asyncGet(@NonNull @NotNull String url, @NonNull @NotNull Map<String, Object> params, @NonNull @NotNull Callback callBack) {
            Log.d("zlm","asyncGet " + url);
        }

        @Override
        public void asyncPost(@NonNull @NotNull String url, @NonNull @NotNull Map<String, Object> params, @NonNull @NotNull Callback callBack) {
            Log.d("zlm","asyncPost " + url);


            HttpUtils.post()
                    .url(url)
                    .params(params)
                    .build()
                    .execute(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            callBack.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            callBack.onSuccess(response.body().string());
                        }
                    });
        }

        @Override
        public void download(@NonNull @NotNull String url, @NonNull @NotNull String path, @NonNull @NotNull String fileName, @NonNull @NotNull DownloadCallback callback) {
            Log.d("zlm","download " + url);

            apkFilePath = path;
            apkDownCallback = callback;
             taskId = Aria.download(this)
                    .load(url)     //读取下载地址
                    .setFilePath(path) //设置文件保存的完整路径
                    .create();   //创建并启动下载

            apkDownCallback.onStart();
        }

        @Override
        public void cancelDownload(@NonNull @NotNull String url) {
            Log.d("zlm","cancelDownload " + url);

            Aria.download(this)
                    .load(taskId)     //读取任务id
                    .stop();       // 停止任务
            //.resume();    // 恢复任务
            //apkDownCallback.on();
        }
    }
    long taskId;

    IUpdateHttpService.DownloadCallback apkDownCallback;
    String apkFilePath;

    //Aria.init(this.getApplicationContext());
     //Aria.download(this).register();
    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning protected void running(DownloadTask task) {

        int p = task.getPercent();	//任务进度百分比
        String speed = task.getConvertSpeed();	//转换单位后的下载速度，单位转换需要在配置文件中打开
       //原始byte长度速度

        task.getFileSize();
        apkDownCallback.onProgress((float) (p/100.0),task.getFileSize());
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        apkDownCallback.onSuccess(new File(apkFilePath));
    }






}