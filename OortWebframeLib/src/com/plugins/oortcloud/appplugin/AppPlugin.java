package com.plugins.oortcloud.appplugin;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.appstore.bean.AppStatu;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import proxy.Proxy;

public class AppPlugin extends CordovaPlugin {
    private Context context;
    private Activity activity;
    private String [] permissions = { Manifest.permission.CAMERA };
    private static final String LOG_TAG = "AppPlugin";
    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
    private CallbackContext callbackContext;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = cordova.getActivity().getApplication();
        activity = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Proxy.setProxyCordovaPlugin(this);

        if ("runApp".equals(action)) {
            String packageName;
            String params = "";
            try{
                packageName = args.getString(0);
                if (args.length() >= 2){
                    params = args.getString(1);
                }
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(packageName))
            {
                callbackContext.success("packagename不能为空");
                return true;
            }
            if("com.oortcloud.screenpush".equals(packageName))
            {
                this.startPushscreen(packageName, params);
            }else {
                this.startApp(packageName, params);
            }
            callbackContext.success("ok");
            return true;
        }else if ("runVoiceMeet".equals(action)) {

            this.startVoiceMeet();
            callbackContext.success("ok");
            return true;
        }else if ("runVedioMeet".equals(action)) {

            this.startVedioMeet();
            callbackContext.success("ok");
            return true;
        }else if ("runNearby".equals(action)) {

            this.startNearby();
            callbackContext.success("ok");
            return true;
        }else if ("runDouyin".equals(action)) {

            this.startDouyin();
            callbackContext.success("ok");
            return true;
        }else if ("runLive".equals(action)) {

            this.startLive();
            callbackContext.success("ok");
            return true;
        }else if ("runPushscreen".equals(action)) {

            this.startPushscreen();
            callbackContext.success("ok");
            return true;
        }else if ("runPlayer".equals(action)) {
            String videourl;
            try{
                videourl = args.getString(0);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(videourl) || "null".equals(videourl))
            {
                callbackContext.success("videourl不能为空");
                return true;
            }
            this.startPlayer(videourl);
            callbackContext.success("ok");
            return true;
        }else if ("startOnemap".equals(action)) {

            //如果未安装则直接返回
            if (!checkAppInstalled(context,"com.oortcloud.onemap")){
                callbackContext.error("未安装一张图");
                return true;
            }

            //如果包名已存在而直接打开

            Intent intent = new Intent( "android.action.onemap");
//            intent.putExtra("URL",videourl);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cordova.getActivity().startActivity(intent);

            callbackContext.success("ok");
            return true;
        }

        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void startPlayer(String videourl) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".player");
        intent.putExtra("URL",videourl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }

    private void startPushscreen(String packagename, String params) {

        //如果已经安次旧的，就用旧的
        if (checkAppInstalled(context,"com.daniulive.smartservicescreenpublisher")){
            this.startPushscreen();
            return;
        }

        //如果包名不存在则走安装流程
        if(!checkAppInstalled(context,packagename)){
            this.startApp(packagename, params);
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
        cordova.getActivity().startActivity(mIntent);
        /*Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.daniulive.smartservicescreenpublisher", "com.daniulive.smartpublisher.MainActivity");
        intent.setComponent(cn);
        String url = sharedPreferences.getString("share_screen","");
        intent.putExtra("url", url);
        cordova.getActivity().startActivity(intent);*/
    }
    private void startPushscreen() {

        /*Intent mIntent = new Intent( );
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String url = sharedPreferences.getString("share_screen","");
        mIntent.putExtra("url",url);
        ComponentName comp = new ComponentName("com.oortcloud.screenpush", "com.oortcloud.screenshare.MainActivity");
        mIntent.setComponent(comp);
//        mIntent.setAction("android.intent.action.VIEW");
        cordova.getActivity().startActivity(mIntent);*/
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.daniulive.smartservicescreenpublisher", "com.daniulive.smartpublisher.MainActivity");
        intent.setComponent(cn);
        String url = sharedPreferences.getString("share_screen","");
        intent.putExtra("url", url);
        cordova.getActivity().startActivity(intent);
    }

    private void startVoiceMeet() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".voicemeet");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }
    private void startVedioMeet() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".vediomeet");
        //Intent intent = new Intent(appid + ".meeting");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }
    private void startNearby() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".nearby");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }
    private void startDouyin() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".douyin");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }
    private void startLive() {
        String appid = context.getApplicationInfo().processName;
            Intent intent = new Intent(appid + ".classroom.live");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cordova.getActivity().startActivity(intent);
    }

    private void startApp(String packageName, String params) {
        try {
            String appid = context.getApplicationInfo().processName;
            Intent intent = new Intent(appid + ".app.appManager");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", packageName);
            intent.putExtra("params", params);

            if(AppStatu.getInstance().appStatu == 0) {
                //AppStatu.getInstance().appStatu = 1;
                cordova.getActivity().startActivity(intent);
            }
        }catch (Exception e){
            Log.v("msg" , e.toString());
        }

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

    /**
     * check application's permissions
     */
    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     *
     * @param requestCode The code to get request action
     */
    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    /**
     * processes the result of permission request
     *
     * @param requestCode The code to get request action
     * @param permissions The collection of permissions
     * @param grantResults The result of grant
     */
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        PluginResult result;
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Log.d(LOG_TAG, "Permission Denied!");
                result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                this.callbackContext.sendPluginResult(result);
                return;
            }
        }

        switch(requestCode)
        {
            case 0:

                break;
        }
    }

    /**
     * This plugin launches an external Activity when the camera is opened, so we
     * need to implement the save/restore API in case the Activity gets killed
     * by the OS while it's in the background.
     */
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

}
