package net.yrom.screenrecorder.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import net.yrom.screenrecorder.R;
import net.yrom.screenrecorder.core.RESAudioClient;
import net.yrom.screenrecorder.core.RESCoreParameters;
import net.yrom.screenrecorder.model.DanmakuBean;
import net.yrom.screenrecorder.rtmp.RESFlvData;
import net.yrom.screenrecorder.rtmp.RESFlvDataCollecter;
import net.yrom.screenrecorder.service.ScreenRecordListenerService;
import net.yrom.screenrecorder.task.RtmpStreamingSender;
import net.yrom.screenrecorder.task.ScreenRecorder;
import net.yrom.screenrecorder.tools.LogTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScreenRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private Button mButton;
    private Button mPermissionButton;
    private EditText mRtmpAddET;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mVideoRecorder;
    private RESAudioClient audioClient;
    private RtmpStreamingSender streamingSender;
    private ExecutorService executorService;
    private List<DanmakuBean> danmakuBeanList = new ArrayList<>();
    private String rtmpAddr;
    private boolean isRecording;
    private RESCoreParameters coreParameters;

    public static String LIVE_SWITCHTO_SCREEN = "com.oortcloud.ommjwb" + "live_switchto_screen";// 切换到屏幕共享
    public static String LIVE_SWTICHTO_LIVEROOM = "com.oortcloud.ommjwb" + "live_switchto_liveroom";// 切换到直播间
    private boolean exit_status = false;

    private static final int REQUEST_STREAM = 3;
    private static String[] PERMISSIONS_STREAM = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean authorized = false;
    public static void launchActivity(Context ctx) {
        Intent it = new Intent(ctx, ScreenRecordActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, it, PendingIntent.FLAG_IMMUTABLE);
            pendingIntent.send();
        } catch (Exception e) {
            e.printStackTrace();
        }



//        ctx.startActivity(it);
    }

//    private IScreenRecorderAidlInterface recorderAidlInterface;
//
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            recorderAidlInterface = IScreenRecorderAidlInterface.Stub.asInterface(service);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            recorderAidlInterface = null;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyPermissions();
        Intent intent = getIntent();
        if (intent != null){
            rtmpAddr =  intent.getStringExtra("url");
//            rtmpAddr = "rtmp://oort.oortcloudsmart.com:1935/live/10013635_1614163991";
            Log.v("msg" , rtmpAddr);
            if (TextUtils.isEmpty(rtmpAddr)) {
                Toast.makeText(ScreenRecordActivity.this,"直播异常，请重新打开",Toast.LENGTH_LONG).show();
                finish();
                return;
            }else {
                Log.d("sharescreen", rtmpAddr);
            }
        }else{
            Log.d("sharescreen", "intent is null");
        }

        super.onCreate(savedInstanceState);

            androidx.appcompat.app.ActionBar actionBar1 = getSupportActionBar();
        if (actionBar1 != null){
            actionBar1.setTitle("屏幕共享");
        }

        RESFlvData.initCamera(this);
        setContentView(R.layout.activity_sreeenrecord);
        mButton = (Button) findViewById(R.id.button);
        mPermissionButton = (Button) findViewById(R.id.permission_check);
        mRtmpAddET = (EditText) findViewById(R.id.et_rtmp_address);
        mButton.setOnClickListener(this);
        mPermissionButton.setOnClickListener(this);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

    }
    public void verifyPermissions() {
        int CAMERA_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int RECORD_AUDIO_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int WRITE_EXTERNAL_STORAGE_permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (CAMERA_permission != PackageManager.PERMISSION_GRANTED ||
                RECORD_AUDIO_permission != PackageManager.PERMISSION_GRANTED ||
                WRITE_EXTERNAL_STORAGE_permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STREAM,
                    REQUEST_STREAM
            );
            authorized = false;
        } else {
            authorized = true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
//            MyWindowManager.createSmallWindow(this);
            if (mVideoRecorder != null) {
                stopScreenRecord();
            } else {
                createScreenCapture();
            }

        }else {
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }

            streamingSender = new RtmpStreamingSender();
            streamingSender.sendStart(rtmpAddr);
            RESFlvDataCollecter collecter = new RESFlvDataCollecter() {
                @Override
                public void collect(RESFlvData flvData, int type) {
                        if (streamingSender != null){
                            streamingSender.sendFood(flvData, type);
                            Log.v("msg" , "streamingSender------------is ok");
                        }else {
                            Log.v("msg" , "streamingSender------------is  null");
                        }


                }
            };
            coreParameters = new RESCoreParameters();

            audioClient = new RESAudioClient(coreParameters);

            if (!audioClient.prepare()) {
                LogTools.d("!!!!!audioClient.prepare()failed");
                return;
            }

            mVideoRecorder = new ScreenRecorder(collecter, RESFlvData.mRealSizeWidth, RESFlvData.mRealSizeHeight, RESFlvData.VIDEO_BITRATE, 1, mediaProjection);
            mVideoRecorder.start();
            audioClient.start(collecter);

            executorService = Executors.newCachedThreadPool();
            executorService.execute(streamingSender);

            mButton.setText("结束共享");
            //发送广播
            Intent mintent = new Intent(LIVE_SWITCHTO_SCREEN);
            sendBroadcast(mintent);
//            Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
//            moveTaskToBack(true);
            Intent home=new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.button) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 2);
                } else {
                    if (mVideoRecorder != null) {
                        stopScreenRecord();
                    } else {
                        createScreenCapture();
                    }

                }
            }
        } else if (id == R.id.permission_check) {
            if (authorized) {
                Toast.makeText(this, "已获取相关权限", Toast.LENGTH_SHORT).show();
            } else {
                verifyPermissions();
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoRecorder != null) {
            stopScreenRecord();
        }
        //销毁时发送广播
        if(!exit_status) {
            Intent mintent = new Intent(LIVE_SWTICHTO_LIVEROOM);
            sendBroadcast(mintent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecording) stopScreenRecordService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording) startScreenRecordService();
    }

    private void startScreenRecordService() {
        if (mVideoRecorder != null && mVideoRecorder.getStatus()) {
            Intent runningServiceIT = new Intent(this, ScreenRecordListenerService.class);
          //  bindService(runningServiceIT, connection, BIND_AUTO_CREATE);
            startService(runningServiceIT);
            startAutoSendDanmaku();
        }
    }

    private void startAutoSendDanmaku() {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                while (true) {
                    DanmakuBean danmakuBean = new DanmakuBean();
                    danmakuBean.setMessage(String.valueOf(index++));
                    danmakuBean.setName("little girl");
                    danmakuBeanList.add(danmakuBean);
                    //                        if (recorderAidlInterface != null) {
//                            recorderAidlInterface.sendDanmaku(danmakuBeanList);
//                        }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void stopScreenRecordService() {
        Intent runningServiceIT = new Intent(this, ScreenRecordListenerService.class);
        stopService(runningServiceIT);
        if (mVideoRecorder != null && mVideoRecorder.getStatus()) {
            Toast.makeText(this, "正在进行录屏", Toast.LENGTH_SHORT).show();
        }
    }

    private void createScreenCapture() {
        isRecording = true;
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }


    private void stopScreenRecord() {
        if (mVideoRecorder != null){
            mVideoRecorder.quit();
        }

        mVideoRecorder = null;
        if (streamingSender != null) {
            streamingSender.sendStop();
            streamingSender.quit();
            streamingSender = null;
        }
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
        if (audioClient != null){
            audioClient.stop();
            audioClient = null;
        }

        mButton.setText("共享屏幕");
        //发送广播
        Intent mintent = new Intent(LIVE_SWTICHTO_LIVEROOM);
        sendBroadcast(mintent);
        exit_status = true;
        //回到直播间
        startClassroom(getApplicationContext());
        this.finish();
    }

    public static class RESAudioBuff {
        public boolean isReadyToFill;
        public int audioFormat = -1;
        public byte[] buff;

        public RESAudioBuff(int audioFormat, int size) {
            isReadyToFill = true;
            this.audioFormat = audioFormat;
            buff = new byte[size];
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STREAM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                authorized = true;
            }
        }
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals("com.oortcloud.ommzsxtnew")) {
                    return true;
                }
            }
        }
        return false;
    }

    //如果APP是在后台运行
    public void startClassroom(Context context)
    {
        if (!isRunningForeground(context)) {
            //获取ActivityManager
            ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if (rti.topActivity.getPackageName().equals("com.oortcloud.ommjwb")) {
                    mAm.moveTaskToFront(rti.id, 0);
                    return;
                }
            }
            //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
            Intent resultIntent = new Intent("com.oortcloud.ommjwb.CLASSROOM");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(resultIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Toast.makeText(this, "返回退出屏幕共享", Toast.LENGTH_SHORT).show();
        stopScreenRecord();


    }
}
