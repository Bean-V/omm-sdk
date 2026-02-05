package com.oortcloud.oort_zhifayi;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.TextView;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // 获取 InputMethodManager
//
//
//        findViewById(R.id.tv_one).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//// 显示软键盘
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            }
//        });
//    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_ENTER:
//                    // 处理回车键事件
//                    Log.d("KeyEvent", "Enter key pressed");
//                    return true; // 返回 true 表示已处理事件
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // 调用父类的方法确保事件能继续传递
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        // 调用父类的方法确保事件能继续传递
//        return super.onKeyUp(keyCode, event);
//    }
//
//
//}


//package com.example.event;



        import android.annotation.SuppressLint;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.hardware.Camera;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;

        import androidx.annotation.NonNull;

        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.alibaba.fastjson.JSON;
        import com.oortcloud.basemodule.constant.Constant;
        import com.oortcloud.basemodule.login.LoginTool;
        import com.oortcloud.basemodule.login.okhttp.HttpUtils;
        import com.oortcloud.basemodule.login.okhttp.callback.BaseCallback;
        import com.oortcloud.basemodule.login.okhttp.result.ObjectResult;
        import com.oortcloud.basemodule.utils.PermissionUtil;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.TimeZone;

//import com.example.event.utiLog.dateUtil;



@SuppressLint("DefaultLocale")
public class ZXMainActivity extends ActivityBase implements SurfaceHolder.Callback {


    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mCameraConfigured = false;



    private TextView tv_result; // 声明一个文本视图对象
    private String desc = "";
    private String[] neededPermissions;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_z);
        tv_result = findViewById(R.id.tv_result);
        //initDesktopRecevier(); // 初始化桌面广播

        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        //mSurfaceHolder.addCallback(this);

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> {
            captureImage();
        });


        // 创建一个新的IntentFilter来注册你感兴趣的广播类型
        IntentFilter filter = new IntentFilter("android.intent.action.keyevent");
// 创建一个BroadcastReceiver的实例
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.keyevent".equals(action)) {
                int keycode = intent.getIntExtra("KEY_CODE",-1);
                int keyAction = intent.getIntExtra("KEY_ACTION",-1);
                long keyTime = intent.getLongExtra("KEY_TIME",-1);
                int keyFlags = intent.getIntExtra("KEY_FLAGS", -1);
                Log.e("zfy", "keycode:" + KeyEvent.keyCodeToString(keycode)
                        +" ,keyAction:"+keyAction
                        + " ,keyTime:" + keyTime
                        + " ,keyFlags:"+keyFlags
                        + " ,long press:"+
                        ((keyFlags&KeyEvent.FLAG_LONG_PRESS) !=0));


                if(keyAction == 0){



                    return;
                }
                if(keycode == 134 ){
//                    if(keyFlags == KeyEvent.FLAG_LONG_PRESS) {
//
//                        startActivity(new Intent(MainActivity.this, ActivityPost.class));
//                        return;
//                    }
                    if(PermissionUtil.checkSelfPermissions(ZXMainActivity.this,neededPermissions)) {

                        if(mCamera != null) {
                            captureImage();
                            return;
                        }else {
                            return;
                        }
                    }else {
                        PermissionUtil.requestPermissions(ZXMainActivity.this,10009,neededPermissions);
                        return;
                    }

                }
                if(keycode == 131){


                        Intent intent1 = new Intent(ZXMainActivity.this, ActivityTasks.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                        return;
                }


                if(isVisible) {
                    Intent intent1 = new Intent(ZXMainActivity.this, ActivityScrren.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent1);
                }
            }
                // 处理接收到的广播
            }

        };

// 使用Context注册广播接收器
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);

        }
        // 当你不再需要接收广播时，可以注销广播接收器
        //context.unregisterReceiver(receiver);

        if(false) {
            LoginTool.login(this, "18948726603", "123456", new LoginTool.LoginRes() {
                @Override
                public void loginRes(int code, String userId, String token, String data, String data1, String data2) {

                    String s = userId;
                }
            });
        }

        neededPermissions = new String[]{

                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE,



        };

        PermissionUtil.requestPermissions(this,1000, neededPermissions);


        if(PermissionUtil.checkSelfPermissions(ZXMainActivity.this,neededPermissions)) {
            mSurfaceHolder.addCallback(this);
        }






       if(true) {
            HashMap paras = new HashMap();
            paras.put("accessToken", ZFYConstant.testToken);
            paras.put("keyword", "");
           paras.put("pagesize", String.valueOf(1000));
           paras.put("page", String.valueOf(1));

           paras.put("status", String.valueOf(2));//1:已完成 2:待处理 0:全部

            HttpUtils.post()
                    .url(ZFYConstant.BASE_URL_A_PAAS_V1 + "/mytask_list")
                    .params(paras)
                    .build(false,true)
                    .execute(new BaseCallback<HashMap>(HashMap.class) {

                        @Override
                        public void onResponse(ObjectResult<HashMap> result) {
                            HashMap dictionary = result.getData();

                        }

                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            Exception e1 = e;
                        }
                    });
        }
        if(false){

            String [] pics = new String []{"http://com/test.jpg"};

            HashMap point = new HashMap();
            point.put("address","深圳市福田区松岭路57号");
            point.put("lat","22.71991");
            point.put("lng","114.24779");

            HashMap paras = new HashMap();
            paras.put("accessToken",ZFYConstant.testToken);
            paras.put("describe","每周街道巡更任务");;
            paras.put("id","e01f2701-29d8-47be-a5ef-7240a07fec95");
            paras.put("pics", JSON.toJSONString(pics));

            paras.put("point",JSON.toJSONString(point));

            HttpUtils.post()
                    .url(Constant.BASE_URL + "/mytask_updata")
                    .params(paras)
                    .build()
                    .execute(new BaseCallback<HashMap>(HashMap.class) {

                        @Override
                        public void onResponse(ObjectResult<HashMap> result) {
                            HashMap dictionary = result.getData();

                        }

                        @Override
                        public void onError(okhttp3.Call call, Exception e) {
                            Exception e1 = e;
                        }
                    } );
        }

    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
            if (mCamera == null) {
                mCamera = Camera.open();
            }
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
        return;
        }
        try {
        mCamera.stopPreview();
        } catch (Exception e) {
        e.printStackTrace();
        }
        if (!mCameraConfigured) {
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getSupportedPreviewSizes().get(0);
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
        mCameraConfigured = true;
        }
        try {
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        } catch (Exception e) {
        e.printStackTrace();
        }
        }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void captureImage() {

        try {
            mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    // 处理拍摄的照片数据
                    //Toast.makeText(MainActivity.this, "Picture Taken", Toast.LENGTH_SHORT).show();
                    // 重新开始预览
                    mCamera.startPreview();


                    //Toast.makeText(getApplicationContext(), "正在保存", Toast.LENGTH_LONG).show();
                    //用BitmapFactory.decodeByteArray()方法可以把相机传回的裸数据转换成Bitmap对象
                    Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //接下来的工作就是把Bitmap保存成一个存储卡中的文件
                    //data完成拍完之后照片的数据

                    SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMddHHmmss"); //设置时间格式

                    formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区

                    Date curDate = new Date(System.currentTimeMillis()); //获取当前时间

                    String createDate = formatter.format(curDate);

                    String sd = Environment.getExternalStorageDirectory().getPath();
                    String tf = sd + "/zhifayi";
                    if (!new File(tf).exists()) {
                        new File(tf).mkdir();
                    }
                    File tempFile = new File(tf + "/" + createDate + ".png");

                    try {
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        try {
                            fos.write(data);
                            fos.close();
                            Intent intent = new Intent(ZXMainActivity.this, ActivityPreview.class);
                            intent.putExtra("picPath", tempFile.getAbsolutePath());
                            startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();

                            Toast.makeText(ZXMainActivity.this, "Picture Taken12222" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {

                        Toast.makeText(ZXMainActivity.this, "Picture Taken12222" + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            });
        }catch (Exception e){
            Exception e1 = e;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    // 在发生物理按键动作时触发
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        desc = String.format("%s物理按键的编码是%d", desc, keyCode);
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            desc = String.format("%s，按键为返回键", desc);
//            // 延迟3秒后启动页面关闭任务
//            new Handler(Looper.myLooper()).postDelayed(() -> finish(), 3000);
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            desc = String.format("%s，按键为加大音量键", desc);
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            desc = String.format("%s，按键为减小音量键", desc);
//        }
//        desc = desc + "\n";
//        tv_result.setText(desc);
//        // 返回true表示不再响应系统动作，返回false表示继续响应系统动作
//        return true;
//    }

    // 初始化桌面广播。用于监听按下主页键和任务键
//    private void initDesktopRecevier() {
//        // 创建一个返回桌面的广播接收器
//        mDesktopRecevier = new DesktopRecevier();
//        // 创建一个意图过滤器，只接收关闭系统对话框（即返回桌面）的广播
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        registerReceiver(mDesktopRecevier, intentFilter); // 注册广播接收器
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        } // 注销广播接收器
    }

   // private DesktopRecevier mDesktopRecevier; // 声明一个返回桌面的广播接收器对象



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    // 定义一个返回到桌面的广播接收器
//    class DesktopRecevier extends BroadcastReceiver {
//        private String SYSTEM_DIALOG_REASON_KEY = "reason"; // 键名
//        private String SYSTEM_DIALOG_REASON_HOME = "homekey"; // 主页键
//        private String SYSTEM_DIALOG_REASON_TASK = "recentapps"; // 任务键
//
//        // 在收到返回桌面广播时触发
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//                if (!TextUtils.isEmpty(reason)) {
//                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME)) { // 按下了主页键
//                        desc = String.format("%s%s\t 按键为主页键\n", desc, "");
//                        tv_result.setText(desc);
//                    } else if (reason.equals(SYSTEM_DIALOG_REASON_TASK)) { // 按下了任务键
//                        desc = String.format("%s%s\t 按键为任务键\n", desc, "");
//                        tv_result.setText(desc);
//                    }
//                }
//            }
//        }
//    }
}