package com.plugins.oortcloud.appplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.im.ApplicationMessageSendChat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import org.greenrobot.eventbus.EventBus;
import proxy.Proxy;

public class AppSendMsg extends CordovaPlugin {
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

        if ("SendMsgToUser".equals(action)) {
            String userid;
            String content = "";
            try{
                if (args.length()<2){
                    callbackContext.error("参数不对，至少传2个参数");
                    return true;
                }
                userid = args.getString(0);
                content = args.getString(1);


            }catch(Exception e) {
                callbackContext.error(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(content))
            {
                callbackContext.error("参数不能为空");
                return true;
            }
            EventBus.getDefault().post(new ApplicationMessageSendChat(userid,content));
            callbackContext.success("ok");
            return true;
        }else if ("SendMsg".equals(action)){

            String userid;
            String type = "";
            try{
                if (args.length()<2){
                    callbackContext.error("参数不对，至少传2个参数");
                    return true;
                }
                userid = args.getString(0);
                type = args.getString(1);


            }catch(Exception e) {
                callbackContext.error(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(type))
            {
                callbackContext.error("参数不能为空");
                return true;
            }
            startSendMsg(userid,type);
            callbackContext.success("ok");
            return true;
        }

            return false;  // Returning false results in a "MethodNotFound" error.
    }

    private void startSendMsg(String userid, String type) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid + ".sendmsg");
        intent.putExtra("userid",userid);
        intent.putExtra("type",type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);
    }


}
