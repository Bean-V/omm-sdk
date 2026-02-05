package com.oort.weichat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.helper.AvatarHelper;
import com.oortcloud.appstore.activity.AppManagerActivity;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AppHelper {

    private static AppHelper logUtil;

    //单例模式初始化
    public static AppHelper getInstance() {
        if (logUtil == null) {
            logUtil = new AppHelper();

        }
        return logUtil;
    }

    private Context context;
    private Activity activity;
    private String [] permissions = { Manifest.permission.CAMERA };
    private static final String LOG_TAG = "AppPlugin";
    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
    private CallbackContext callbackContext;

//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        // your init code here
//        context = context.getApplication();
//        activity = context;
//    }

    public void initialize(Context ctx) {
        // your init code here
        context = ctx;
    }

    public boolean execute(String action, AppInfo info) throws JSONException {

        if ("runApp".equals(action)) {
            String packagename;
            try{
                packagename = info.getApppackage();//args.getString(0);
            }catch(Exception e) {
                //callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(packagename))
            {
                //callbackContext.success("packagename不能为空");
                return true;
            }
            if("com.oortcloud.screenpush".equals(packagename))
            {
                this.startPushscreen(packagename);
            }else {
                this.startApp(packagename);
            }
            //callbackContext.success("ok");
            return true;
        }else if ("runVoiceMeet".equals(action)) {

            this.startVoiceMeet();
            //callbackContext.success("ok");
            return true;
        }else if ("runVedioMeet".equals(action)) {

            this.startVedioMeet();
            //callbackContext.success("ok");
            return true;
        }else if ("runNearby".equals(action)) {

            this.startNearby();
            //callbackContext.success("ok");
            return true;
        }else if ("runDouyin".equals(action)) {

            this.startDouyin();
            //callbackContext.success("ok");
            return true;
        }else if ("runLive".equals(action)) {

            this.startLive();
            //callbackContext.success("ok");
            return true;
        }else if ("runPushscreen".equals(action)) {

            this.startPushscreen();
            //callbackContext.success("ok");
            return true;
        }else if ("runPlayer".equals(action)) {
            String videourl;
            try{
                videourl = info.getApk_url();//args.getString(0);
            }catch(Exception e) {
                //callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(videourl) || "null".equals(videourl))
            {
                //callbackContext.success("videourl不能为空");
                return true;
            }
            this.startPlayer(videourl);
            //callbackContext.success("ok");
            return true;
        }else if ("runAddCardTime".equals(action)){
            startAddCardTime();
            //callbackContext.success("ok");
        }
        else if ("runPayment".equals(action)){
            startPayment();
            //callbackContext.success("ok");
        }

        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void startPlayer(String videourl) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".player");
        intent.putExtra("URL",videourl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startPushscreen(String packagename) {

        //如果已经安次旧的，就用旧的
        if (checkAppInstalled(context,"com.daniulive.smartservicescreenpublisher")){
            this.startPushscreen();
            return;
        }

        //如果包名不存在则走安装流程
        if(!checkAppInstalled(context,packagename)){
            this.startApp(packagename);
            return;
        }

        //如果包名已存在而直接打开

        Intent mIntent = new Intent( );
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String url = sharedPreferences.getString("share_screen","");
        mIntent.putExtra("url",url);
        ComponentName comp = new ComponentName("com.oortcloud.screenpush", "com.oortcloud.screenshare.MainActivity");
        mIntent.setComponent(comp);
//        mIntent.setAction("android.intent.action.VIEW");
        context.startActivity(mIntent);
        /*Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.daniulive.smartservicescreenpublisher", "com.daniulive.smartpublisher.MainActivity");
        intent.setComponent(cn);
        String url = sharedPreferences.getString("share_screen","");
        intent.putExtra("url", url);
        context.startActivity(intent);*/
    }
    private void startPushscreen() {

        /*Intent mIntent = new Intent( );
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String url = sharedPreferences.getString("share_screen","");
        mIntent.putExtra("url",url);
        ComponentName comp = new ComponentName("com.oortcloud.screenpush", "com.oortcloud.screenshare.MainActivity");
        mIntent.setComponent(comp);
//        mIntent.setAction("android.intent.action.VIEW");
        context.startActivity(mIntent);*/
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.daniulive.smartservicescreenpublisher", "com.daniulive.smartpublisher.MainActivity");
        intent.setComponent(cn);
        String url = sharedPreferences.getString("share_screen","");
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private void startVoiceMeet() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".voicemeet");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void startVedioMeet() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".vediomeet");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void startNearby() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".nearby");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void startDouyin() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".douyin");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void startLive() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".classroom.live");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startApp(String packagename ) {
        try {
            String appid = context.getApplicationInfo().processName;


            //Intent intent = new Intent(appid + ".app.appmanager");
            Intent intent = new Intent(context, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", packagename);

            context.startActivity(intent);
        } catch (Exception e) {
            Log.v("msg", e.toString());
        }
    }
    private void startAddCardTime(){
        Intent intent = new Intent("android.action.cardtime");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void startPayment(){
        Intent intent = new Intent("android.action.payment");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private  int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    private  String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    private void appExit() {
        activity.finish();
    }

    private boolean checkAppInstalled( Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty())
            return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }





}






