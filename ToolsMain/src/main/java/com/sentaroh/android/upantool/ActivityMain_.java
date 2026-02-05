package com.sentaroh.android.upantool;

//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;

//import static com.sentaroh.android.SMBSync2.Constants.ACTIVITY_REQUEST_CODE_SDCARD_STORAGE_ACCESS;

//import static com.sentaroh.android.Utilities3.SafFile3.SAF_FILE_PRIMARY_UUID;

import static com.sentaroh.android.Utilities3.SafManager3.SAF_FILE_PRIMARY_UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.mjdev.libaums.UsbMassStorageDevice;
//import com.github.mjdev.libaums.fs.FileSystem;
//import com.github.mjdev.libaums.fs.UsbFile;
//import com.github.mjdev.libaums.partition.Partition;

//import com.blackhao.utillibrary.usbHelper.UsbHelper;
//import androidx.appcompat.app.ActionBar;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//import com.github.moduth.blockcanary.BlockCanaryInternals;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sentaroh.android.upantool.Ad.AdManager;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.Utilities3.CallBackListener;
import com.sentaroh.android.Utilities3.ContextMenu.CustomContextMenu;
import com.sentaroh.android.Utilities3.Dialog.CommonDialog;
import com.sentaroh.android.Utilities3.MiscUtil;
import com.sentaroh.android.Utilities3.NotifyEvent;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.Utilities3.SafManager3;
import com.sentaroh.android.upantool.record.Record;
import com.sentaroh.android.upantool.record.RecordTool;
import com.sentaroh.android.upantool.sysTask.TastTool;
import com.tencent.bugly.crashreport.CrashReport;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.aria.AriaDownloader;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.zhihu.matisse.Matisse;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import it.sephiroth.android.library.easing.Cubic;


public class ActivityMain_ extends BaseActivity implements UsbHelper.LCUsbListener {//implements com.oort.upantool.USBBroadCastReceiver.UsbListener


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String TAG = "readfile";
    private  final int REQUEST_EXTERNAL_STORAGE = 1;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private UsbHelper usbHelper;
    private FragmentHome fHome;

    private boolean isTaskTermination = false; // kill is disabled(enable is kill by onDestroy)

    private Context mContext = null;
    private AppCompatActivity mActivity = null;

    private final static int START_STATUS_STARTING = 0;
    private final static int START_STATUS_COMPLETED = 1;
    private final static int START_STATUS_INITIALYZING = 2;
    private int mStartStatus = START_STATUS_STARTING;

    private final static int RESTART_BY_KILLED = 2;
    private final static int RESTART_BY_DESTROYED = 3;
    private int mRestoreType = 0;

    private ServiceConnection mSvcConnection = null;
    private Handler mUiHandler = new Handler();

    private ActionBar mActionBar = null;

    private String mCurrentTab = null;

    private boolean enableMainUi = true;


    private boolean usbConect = false;

    private boolean requestPermssion = false;

    private boolean mSyncTaskListCreateRequired=false;

    private String mTabNameTask="Task", mTabNameSchedule="Schedule", mTabNameHistory="History", mTabNameMessage="Message";


    private static final String LOG_PATH= Environment.getExternalStorageDirectory().getPath() + File.separator + "Omm1233344" +  File.separator;
    private USBDiskReceiver myReceiver;
    private BroadcastReceiver usbreceiver;
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private CustomeMovebutton CustomeMovebutton;
    private Toolbar tb;
    private AlertDialog requestUsbAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int VERSION = Build.VERSION.SDK_INT;
        ////getSupportActionBar().hide();

        LogHelper.getInstance().initLogFile(this);

        setStatusBarLight(true);
        setContentView(R.layout.activity_main);

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        tb.setTitle("");
        mActivity = ActivityMain_.this;

        tb.setVisibility(View.GONE);
        CrashReport.initCrashReport(getApplicationContext(), "e60c2bd154", true);





        mContext = this;
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        mContext = this;

        //openService();
        //startSdcardSelectorActivity();

        usbreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                        || action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) SystemClock.sleep(1000);

                    if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        toast("已插入");


                        readDataAndPermission();
                    }else if (action.equals(Intent.ACTION_MEDIA_REMOVED)){
                        toast("已移除");
                        readSize();
                    }else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
                        toast("已移除");
                        readSize();
                    }else {
                        //toast("未成功");
                        readSize();
                    }
                }
            }
        };



        IntentFilter media_filter = new IntentFilter();
        media_filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        media_filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        media_filter.addAction(Intent.ACTION_MEDIA_EJECT);
        media_filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        media_filter.addDataScheme("file");
        registerReceiver(usbreceiver, media_filter);


//        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int widthPixels = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
//        wmParams = new WindowManager.LayoutParams();
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        wmParams.format= PixelFormat.RGBA_8888;//设置背景图片
//        wmParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;//
//        wmParams.gravity = Gravity.LEFT|Gravity.TOP;//
//        wmParams.x = widthPixels-150;  //设置位置像素
//        wmParams.y = heightPixels-110;
//        wmParams.width=200; //设置图片大小
//        wmParams.height=200;
//        CustomeMovebutton = new CustomeMovebutton(getApplicationContext());
//        com.sentaroh.android.upantool.CustomeMovebutton.setImageResource(R.drawable.btn_voice_rest);
//        wm.addView(CustomeMovebutton, wmParams);
//        com.sentaroh.android.upantool.CustomeMovebutton.setOnSpeakListener(new CustomeMovebutton.OnSpeakListener() {
//            @Override
//            public void onSpeakListener() {
//                Toast.makeText(MainActivity.this, "点击事件", Toast.LENGTH_SHORT).show();
//            }
//        });



        findViewById(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.ll_agreeView).setVisibility(View.GONE);
                SharedPreferences preferences;
                SharedPreferences.Editor editor;
                preferences = PreferenceManager.getDefaultSharedPreferences(ActivityMain_.this);
                editor = preferences.edit();
                editor.putString("firstStatr", "1");
                editor.commit();
                readDataAndPermission();
            }
        });

        findViewById(R.id.tv_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(ActivityMain_.this,ActivityWeb.class));
            }
        });


        //SystemClock.sleep(1000);


       // File [] f = BlockCanaryInternals.getLogFiles();



       // https://oortcloudsmart.com/privacypolicy_dab.html
        firstStart(this);
        initData();
        //registerListen();



        UsbHelper.getInstance().initData(this);
        usbHelper = UsbHelper.getInstance();

        usbHelper.addUsbListener(this);


        RecordTool recordTool = RecordTool.getInstance();
        recordTool.initData(this);
//        if (Build.VERSION.SDK_INT >= 11) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
//        }




        TransFileManager.getInstance().initData(this);








        if (TastTool.getInstance().isSysTask() && !TastTool.getInstance().isSys()) {


            XXPermissions.with(this)
                    // 申请单个权限
                    .permission(Permission.READ_CONTACTS)
                    .permission(Permission.WRITE_CONTACTS)
                    // 设置权限请求拦截器（局部设置）
                    //.interceptor(new PermissionInterceptor())
                    // 设置不触发错误检测机制（局部设置）
                    .unchecked()
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                //toast("获取部分权限成功，但部分权限未正常授予");
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TastTool.getInstance().initData(ActivityMain_.this);
                                    TastTool.getInstance().sys();
                                }
                            }).start();

                            return;
                            //toast("获取录音和日历权限成功");
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            if (doNotAskAgain) {
                                //toast("被永久拒绝授权，请手动授予录音和日历权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(ActivityMain_.this, permissions);
                            } else {
                                toast("err");
                            }
                        }
                    });
        }





    }

   

    public void firstStart(Context context){
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString("firstStatr", "");

        String localLang = context.getResources().getConfiguration().locale.getLanguage();


        //checkUpdate2();
        if(language.equals("1")) {

            AdManager.adInit(this);
            findViewById(R.id.ll_agreeView).setVisibility(View.GONE);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        postInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();





        }else{

            showPrivacy();
        }
    }
    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(ActivityMain_.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        dialog.show();

        String string = getResources().getString(R.string.privacy_tips);
        String key2 = getResources().getString(R.string.privacy_tips_key2);
        int index2 = string.indexOf(key2);

        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.mainColor));
        spannedString.setSpan(colorSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        AbsoluteSizeSpan sizeSpan2 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                ///Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                //startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
       // spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityMain_.this, ActivityWeb.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());

        tv_privacy_tips.setText(spannedString);

        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
//                SPUtil.put(MainActivity.this, SP_PRIVACY, false);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                findViewById(R.id.ll_agreeView).setVisibility(View.GONE);
                SharedPreferences preferences;
                SharedPreferences.Editor editor;
                preferences = PreferenceManager.getDefaultSharedPreferences(ActivityMain_.this);
                editor = preferences.edit();
                editor.putString("firstStatr", "1");
                editor.commit();
                readDataAndPermission();
//                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
//                SPUtil.put(MainActivity.this, SP_PRIVACY, true);

                AdManager.adInit(mContext);
               // AdManager.get().requestPermissionIfNecessary(mContext);

               // fHome.loadAd();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            postInfo();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

    }


    private void checkUpdate2() {
        boolean isSilentInstall = false;

        XUpdate.newBuild(this)
                .updateUrl("http://oort.oortcloudsmart.com:31610/oort/oortcloud-appupgrade/v1/getAppVersion")
                .updateHttpService(AriaDownloader.getUpdateHttpService(this))
                .isAutoMode(isSilentInstall)
                .supportBackgroundUpdate(true)
                .promptThemeColor(getColor(R.color.mainColor))
                .updateParser(new CustomUpdateParser())
                .update();// Set up a custom version update parser

    }

    public class CustomUpdateParser implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            Log.d("zlm",json);

            Gson gson = new Gson();

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
                long currentVersionCode = getAppVersionCode(ActivityMain_.this);
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
            Log.e(TAG, "[getAppVersionCode]-error：" + e.getMessage());
        }
        return appVersionCode;
    }

    private void initData(){
        LinearLayout vp = findViewById(R.id.fragment_container);
        RadioGroup rg = findViewById(R.id.rg);
        final List<Fragment> frags = new ArrayList<>();
        fHome = new FragmentHome();
        frags.add(fHome);
        frags.add(new FragmentFind());
        frags.add(new FragmentSetting());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.home_tab) {//transaction.replace(R.id.fragment_container, (Fragment) frags.get(0));
                    transaction.show(frags.get(0)).hide(frags.get(1)).hide(frags.get(2));
                    tb.setTitle("");
                    tb.setVisibility(View.GONE);
                } else if (checkedRadioButtonId == R.id.find_tab) {//transaction.replace(R.id.fragment_container, (Fragment) frags.get(1));
                    transaction.show(frags.get(1)).hide(frags.get(0)).hide(frags.get(2));
                    tb.setTitle(R.string.find);
                    tb.setVisibility(View.VISIBLE);
                } else if (checkedRadioButtonId == R.id.set_tab) {//transaction.replace(R.id.fragment_container, (Fragment) frags.get(2));
                    transaction.show(frags.get(2)).hide(frags.get(1)).hide(frags.get(0));
                    tb.setTitle(R.string.setting);
                    tb.setVisibility(View.VISIBLE);
                }
                transaction.commitNow();
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        FrameLayout ft = findViewById(R.id.fragment_container);
//        ft.removeAllViews();
        transaction.add(R.id.fragment_container, (Fragment) frags.get(0), String.valueOf(0));
        transaction.add(R.id.fragment_container, (Fragment) frags.get(1), String.valueOf(1)).hide(frags.get(1));
        transaction.add(R.id.fragment_container, (Fragment) frags.get(2), String.valueOf(2)).hide(frags.get(1));
        transaction.commitNow();


       // registerListen();



    }

//
//    private ISvcCallback mSvcCallbackStub = new ISvcCallback.Stub() {
//        @Override
//        public void cbThreadStarted() throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//        }
//
//        @Override
//        public void cbThreadEnded() throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//        }
//
//        @Override
//        public void cbShowConfirmDialog(final String method, final String msg,
//                                        final String pair_a_path, final long pair_a_length, final long pair_a_last_mod,
//                                        final String pair_b_path, final long pair_b_length, final long pair_b_last_mod) throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//        }
//
//        @Override
//        public void cbHideConfirmDialog() throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
//        }
//
//        @Override
//        public void cbWifiStatusChanged(String status, String ssid) throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//        }
//
//        @Override
//        public void cbMediaStatusChanged() throws RemoteException {
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                   // refreshOptionMenu();
////					mGp.syncTaskAdapter.notifyDataSetChanged();
//                   // mGp.syncTaskAdapter.notifyDataSetChanged();
////
////
////                    SafFile3 fi = mGp.safMgr.getSdcardRootSafFile();
////                    if(fi != null && fi.exists()){
////                        usbHelper.initroot_context(ActivityMain_.this, fi);
////                        usbHelper.setmGp(mGp);
////
////
////                        String url = mGp.safMgr.getSdcardRootPath();
////                        // File f1 = new File(url + "/" + "1234333");
////
////
////                        File lf=new File(url);
////
////                        String size =  "" + (long) (lf.getTotalSpace()/1024.0/1024/1024);
////                        String fsize = "" + (long) (lf.getFreeSpace()/1024.0/1024/1024);
////                        fHome.setFreeSize(size);
////                        fHome.setTotalSize(size);
////
////                    }else{
////                        fHome.setFreeSize("0");
////                        fHome.setTotalSize("0");
////                    }
//                }
//            });
//        }
//
//    };


    @Override
    protected void onResume() {
        super.onResume();

        usbHelper.toStopTrans(false);

        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
        sendBroadcast(intent);



//
//        //要检查您是否具有某项权限，请调用 ContextCompat.checkSelfPermission() 方法
////如果应用具有此权限，方法将返回 PackageManager.PERMISSION_GRANTED，并且应用可以继续操作。
////如果应用不具有此权限，方法将返回 PERMISSION_DENIED，且应用必须明确向用户要求权限。
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
//
//            //为了帮助查找用户可能需要解释的情形，Android 提供了一个实用程序方法，即 shouldShowRequestPermissionRationale()。
//            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
//            //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
//            //如果设备规范禁止应用具有该权限，此方法也会返回 false。
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
//
//                //
//
//            } else {
//
//                //如果应用尚无所需的权限，则应用必须调用一个 requestPermissions() 方法，以请求适当的权限。
//                //应用将传递其所需的权限，以及您指定用于识别此权限请求的整型请求代码。
//                //此方法异步运行：它会立即返回，并且在用户响应对话框之后，系统会使用结果调用应用的回调方法，将应用传递的相同请求代码传递到 requestPermissions()。
//                //注：当您的应用调用 requestPermissions() 时，系统将向用户显示一个标准对话框。
//                //您的应用无法配置或更改此对话框。
//                //如果您需要为用户提供任何信息或解释，您应在调用 requestPermissions() 之前进行，如解释应用为什么需要权限中所述。
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 2001);
//
//            }
//        } else {
//           // readFile();
//        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 2001: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    // readFile();
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();

        //(在使用高版本Android SDK时添加如下代码)

        if(findViewById(R.id.ll_agreeView).getVisibility() == View.GONE){
            readDataAndPermission();
        }



        Thread td = UsbHelper.getInstance().getThread();
        if(td != null){
            if(td.isAlive()){

            }else{
                td.run();
            }
        }else{
            UsbHelper.getInstance().initTimer();
        }


    }



//    public void launchActivityResult(Activity a, String req_id, Intent intent, CallBackListener cbl) {
//        int req_code=mActivityLaunchList.size()+1;
//        synchronized (mActivityLaunchList) {
//            mUtil.addDebugMsg(1, "I", "launchActivityResult req_id="+req_id+", req_code="+req_code);
//            mActivityLaunchList.add(new ActivityLaunchItem(req_code, req_id, cbl));
//        }
//        a.startActivityForResult(intent, req_code);
//    }
    protected void registerListen(){

        myReceiver = new USBDiskReceiver();
//                <action android:name="android.intent.action.MEDIA_MOUNTED" />
//                <action android:name="android.intent.action.MEDIA_UNMOUNTED" />
//                <action android:name="android.intent.action.MEDIA_REMOVED" />

        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        itFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        itFilter.addAction("android.intent.action.MEDIA_REMOVED");
        registerReceiver(myReceiver, itFilter);




//        //toast("registerListen");
//        usbHelper.getDeviceList();
    }




    private String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // https://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            final String androidId = "" + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            serial = androidId;
        }

        // if Build.SERIAL get successfuly, the 'id' is unique very likely, if not, we use androidId to guarante 'id' to be unique as possible
        // without any permissions


        // Thanks @Joe!
        // https://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public  String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public String getAppVersion(Context context,String packname){
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packname;
    }

    public String getAppName(Context context,String packname){
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packname;
    }

    public String getAppVersion() {
        if (this == null) {
            return "";
        }
        PackageManager pm = this.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }
    protected void postInfo() throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {


                ApplicationInfo appInfo;

                String appName = getAppName(ActivityMain_.this,ActivityMain_.this.getPackageName());
                String deviceId = getUniquePsuedoID();
                String version = getAppVersion();
                String appPackage = ActivityMain_.this.getPackageName();





                try {
                    URL url = new URL("http://oort.oortcloudsmart.com:31610/oort/oortcloud-aiotpaas/v1/report/device");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");

                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

// 获取输出流
                    OutputStream outputStream = connection.getOutputStream();


                    //HashMap map = new HashMap();
                    JSONObject map = new JSONObject();
                    map.put("appName",appName);
                    map.put("deviceId",deviceId);
                    map.put("terminal",1);
                    map.put("appPackage",appPackage);
                    map.put("version",version);

// 将参数写入输出流中
                    String params = map.toString();
                    outputStream.write(params.getBytes());


                    int response = -1;
                    //connection.connect();

                    response = connection.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        //connection
                        InputStream inputStream = connection.getInputStream();
                    }

                }catch (Exception e){


                    int i =0;
                    Log.d(TAG, "run: " + e.getLocalizedMessage());
                }

            }
        }).run();



    }

    protected void readDataAndPermission(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        AlertDialog alert = null;
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        alert = builder.setTitle("提示")
                                .setMessage("需要您授权文件管理权限，才可以访问文件数据")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                        startActivity(intent);
                                    }
                                }).create();             //创建AlertDialog对象
                        alert.show();


                        return;
                    }else{
                        readSize();
                    }
                }else{
                    readSize();
                }
            }
        }, 1000);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readSize(){


        if (isSdcardDeviceExists(mContext)) {
            if (usbHelper.safMgr.isStoragePermissionRequired()){



                TextView tv = fHome.getUpantip_tv();//new TextView(ActivityMain_.this);
                tv.setText(getString(R.string.tap_to_requset_usb_permisson));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestUsb();
                    }
                });
                //允许获取u盘权限


            } else {

                UsbHelper.getInstance().safMgr.refreshSafList();

            }
        }
    }
    public void requestUsb(){


            Intent intent = null;
            StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            ArrayList<SafManager3.StorageVolumeInfo>vol_list=SafManager3.getStorageVolumeInfo(ActivityMain_.this);
            for(SafManager3.StorageVolumeInfo svi:vol_list) {

                LogHelper.getInstance().d("requestUsb\n" + svi.toString() + "\n" + svi.description + svi.uuid );
                if (svi.uuid != null && svi.isRemovable) {
                    if (Build.VERSION.SDK_INT>=29) {
                        if (!svi.uuid.equals(SAF_FILE_PRIMARY_UUID)) {
                            intent=svi.volume.createOpenDocumentTreeIntent();
                        }
                    } else {
                        if (!svi.uuid.equals(SAF_FILE_PRIMARY_UUID)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent=svi.volume.createAccessIntent(null);
                            }
                        }
                    }
                    if (intent!=null) {
                        try {

                            //startActivityForResult(intent, 466);
                                        launchActivityResult(this, "UUID", intent, new CallBackListener() {
                                            @Override
                                            public void onCallBack(Context context, boolean positive, Object[] objects) {


                                               // ntfy.notifyToListener(true, new Object[]{positive?0:-1, objects[0], uuid});
                                                final int resultCode=Activity.RESULT_OK;//(Integer)objects[0];
                                                final Intent data=(Intent)objects[0];
                                                //final String uuid=(String)objects[2];

                                                if (resultCode == Activity.RESULT_OK) {
                                                    if (data==null || data.getDataString()==null) {
                                                        toast("未允许");
                                                        return;
                                                    }
                                                    if (!usbHelper.safMgr.isRootTreeUri(data.getData())) {
                                                        String em=usbHelper.safMgr.getLastErrorMessage();
                                                        if (em.length()>0) {
                                                            //toast(em);
                                                        }
                                                        //重新获取

//                                                        NotifyEvent ntfy_retry = new NotifyEvent(context);
//                                                        ntfy_retry.setListener(new NotifyEvent.NotifyEventListener() {
//                                                            @Override
//                                                            public void positiveResponse(Context c, Object[] o) {
//                                                                requestStoragePermissionByUuid(activity, ut, uuid, ntfy_response);
//                                                            }
//
//                                                            @Override
//                                                            public void negativeResponse(Context c, Object[] o) {}
//                                                        });
//                                                        ut.showCommonDialogWarn(true, context.getString(R.string.msgs_main_external_storage_select_retry_select_msg),
//                                                                data.getData().getPath(), ntfy_retry);
                                                    } else {
                                                        //ut.addDebugMsg(1, "I", "Selected UUID="+SafManager3.getUuidFromUri(data.getData().toString()));
                                                        String em=usbHelper.safMgr.getLastErrorMessage();
                                                        if (em.length()>0) {
                                                            toast(em);
                                                        }
                                                        boolean rc=usbHelper.safMgr.addUuid(data.getData());
                                                        if (!rc) {
//                                                            String msg=activity.getString(R.string.msgs_storage_permission_msg_add_uuid_failed);
//                                                            String saf_msg=gp.safMgr.getLastErrorMessage();
//                                                            ut.showCommonDialogWarn(false, msg, saf_msg, null);
//                                                            ut.addLogMsg("E", "", msg, "\n", saf_msg);
                                                        }
                                                        //if (p_ntfy!=null) p_ntfy.notifyToListener(true, null);
                                                    }
                                                } else {
//                                                    ut.showCommonDialogWarn(false,
//                                                            context.getString(R.string.msgs_main_external_storage_request_permission),
//                                                            context.getString(R.string.msgs_main_external_storage_select_required_cancel_msg), null);

                                                }
                                            }
                                        });
                        } catch(Exception e) {
//                                        String st= MiscUtil.getStackTraceString(e);
//                                        cu.showCommonDialog(false, "E",
//                                                a.getString(R.string.msgs_storage_permission_msg_saf_error_occured), e.getMessage()+"\n"+st, null);
                        }
                        break;
                    }
                }


        }

    }

    @Override
    public void refreshState(int state) {
        TextView tv = fHome.getUpantip_tv();
        tv.setEnabled(false);
        if(state == 0){
            //show no usb
            usbConect = false;

            fHome.shownousb();

            TransFileManager.getInstance().removeAddDone_();
            TransFileManager.getInstance().removeAddunDone_();
        }
        if(state == 1){
            //show 正在检测read，write
            fHome.showinusbdes();
        }
        if(state == 2){
            //show 请求权限
            fHome.showState2();
            tv.setEnabled(true);


            if(findViewById(R.id.ll_agreeView) != null && requestUsbAlert == null && findViewById(R.id.ll_agreeView).getVisibility() == View.GONE) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if(!Environment.isExternalStorageManager()){
                        return;
                    }else{
                        if (isSdcardDeviceExists(mContext)) {
                            if (usbHelper.safMgr.isStoragePermissionRequired()){

                            }
                        }
                    }
                }
                requestUsbAlert = new AlertDialog.Builder(ActivityMain_.this)
                        .setTitle(getString(R.string.request_u_permisson))
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestUsb();
                            }


                        }).setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();

                            }
                        }).show();
            }


            //new TextView(ActivityMain_.this);
            tv.setText(getString(R.string.tap_to_requset_usb_permisson));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestUsb();
                }
            });
        }
        if(state == 3){

            if(!usbConect){
                //查看是否有未完成的
                usbHelper.toStopTrans(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List list = RecordTool.getInstance().getUnFinshData();

                        if(list.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new androidx.appcompat.app.AlertDialog.Builder(ActivityMain_.this)
                                            .setTitle(getString(R.string.check_task))
                                            .setPositiveButton(getString(R.string.countinue), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {


                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {


                                                            ArrayList tfs = new ArrayList();
                                                             //Object to = UsbHelper.getInstance().getRootFile();
                                                            for (Object obj : list) {
                                                                Record record = (Record) obj;
                                                                SafFile3 file = new SafFile3(mContext,record.path);
                                                                //FileTool.copyToUPanoRoot(file, ActivityWX.this);

                                                                int icon = FileTool.getResIdFromFileName(file.isDirectory(), file.getName());
                                                                Fragment_ft.TransFile tf = new Fragment_ft.TransFile(file.getName(), file.getPath(), file.isDirectory() ? "" : FileTool.getFileSize(file.length()), "", icon, file);
//                                                                tf.setStatu(record.statu);
//
//                                                                tf.setStatuDes(record.statuDes);

                                                                tf.setStatu(0);

                                                                tf.setStatuDes("待复制");

                                                                tf.setCopyDes(0);
                                                                tf.setCopyDes_des("复制到U盘");
                                                                tf.setFileObj(file);
                                                                tf.setToFileObj(new SafFile3(ActivityMain_.this,record.toDirPath));// + "/" + record.name
                                                                tf.setToDirPath(record.toDirPath);
                                                                tfs.add(tf);
                                                            }
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Intent in = new Intent(ActivityMain_.this, Activity_Task.class);
                                                                    startActivity(in);


                                                                    TransFileManager.getInstance().addTransFilesFromDB(tfs);
                                                                    //UsbHelper.getInstance().getSelectFiles().clear();
                                                                    dialog.dismiss();
                                                                }
                                                            });

                                                        }
                                                    }).start();
                                                }


                                            }).setNegativeButton(getString(R.string.fm_cancel), new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    dialog.dismiss();

                                                }
                                            }).show();
                                }
                            });
                        }

                    }
                }).start();




            }

            usbConect = true;
            //show size
            fHome.showState3();


//            if(UsbHelper.getInstance().canCopyToU()) {
//                SafFile3 sfa = new SafFile3(this, UsbHelper.getInstance().getUsbRootPath() + "/" + ".nomedia");
//                try {
//                    sfa.createNewFile();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if(sfa.exists()){
//                    int i = 0;
//                    int b=4;
//                }
//            }
        }
        if(state == 4){
            //show size

            usbConect = false;
            fHome.showState4();
        }
    }

    @Override
    public void getSize(long use, long toatal) {

        //fHome.set

        float tsize = (float) (toatal/1024.0/1024/1024);
        float fsize = (float) (use/1024.0/1024/1024);
        fHome.showSize(String.format("%.2fGB/%.2fGB",fsize,tsize));

    }

    @Override
    public void rootFileReady() {

    }

    @Override
    public void requestPermissonVolume(StorageVolume v) {

        if(requestPermssion){
            return;
        }
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            requestPermssion = true;
            intent = v.createOpenDocumentTreeIntent();

            launchActivityResult(this, "1", intent, new CallBackListener() {
                @Override
                public void onCallBack(Context context, boolean positive, Object[] objects) {

                    if(objects.length > 0){
                        Intent in = (Intent) objects[0];
                        Uri uri = in.getData();
                        if(uri == null){
                            return;
                        }

                        List<UriPermission> pers = context.getContentResolver().getPersistedUriPermissions();

                        List a = pers;
                        int takeFlags =
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                        context.getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        pers = context.getContentResolver().getPersistedUriPermissions();

                    }

                }
            });
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbHelper.lcUsbListeners.remove(this);

    }

    private class ActivityLaunchItem {
        private String requestorId=null;
        private int requestCode=0;
        private CallBackListener callBackListener=null;
        private String permission=null;
        final static public int TYPE_ACTIVITY=0;
        final static public int TYPE_PERMISSION=1;
        private int type=TYPE_ACTIVITY;
        public ActivityLaunchItem(int req_code, String req_id, CallBackListener cbl) {
            this.requestorId=req_id;
            callBackListener=cbl;
            requestCode=req_code;
            type=TYPE_ACTIVITY;
        }
        public ActivityLaunchItem(String permission, CallBackListener cbl) {
            this.permission=permission;
            type=TYPE_PERMISSION;
            callBackListener=cbl;
        }
        public int getType() {return this.type;}
        public String getRequestorId() {return this.requestorId;}
        public int getRequestCode() {return this.requestCode;}
        public String getPermission() {return this.permission;}
        public CallBackListener getCallBackListener() {return callBackListener;}
    }
    private ArrayList<ActivityLaunchItem> mActivityLaunchList=new ArrayList<ActivityLaunchItem>();

    public void launchActivityResult(Activity a, String req_id, Intent intent, CallBackListener cbl) {
        int req_code=mActivityLaunchList.size()+1;
        synchronized (mActivityLaunchList) {
            mActivityLaunchList.add(new ActivityLaunchItem(req_code, req_id, cbl));
        }
        a.startActivityForResult(intent, req_code);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //toast("onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
        synchronized (mActivityLaunchList) {
            ArrayList<ActivityLaunchItem> remove_list = new ArrayList<ActivityLaunchItem>();
            for (ActivityLaunchItem item : mActivityLaunchList) {
                if (item.getType() == ActivityLaunchItem.TYPE_ACTIVITY && item.getRequestCode() == requestCode) {
                    item.callBackListener.onCallBack(mContext, resultCode == 0, new Object[]{data});
                    remove_list.add(item);
                }
            }
            mActivityLaunchList.removeAll(remove_list);
        }

    }

    public boolean isSdcardDeviceExists(Context c) {

        final StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> svs = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            svs = sm.getStorageVolumes();
        }
        return svs.size() > 1;
    }



}