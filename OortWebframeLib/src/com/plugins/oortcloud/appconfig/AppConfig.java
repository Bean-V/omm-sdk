package com.plugins.oortcloud.appconfig;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.constant.Constant;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class AppConfig extends CordovaPlugin {

    private Context context;
    private Activity activity;
    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOCAL_APP_CONFIG);
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = cordova.getActivity().getApplication();
        activity = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getConfigString".equals(action)) {
            String key;
            try{
                key = args.getString(0);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(key))
            {
                callbackContext.success("KEY 不能为空");
                return true;
            }
            String response = sharedPreferences.getString(key,"");
            callbackContext.success(response);
            return true;
        }

        if ("setConfigString".equals(action)) {
            String key;
            try{
                key = args.getString(0);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(key))
            {
                callbackContext.success("KEY 不能为空");
                return true;
            }
            String value;
            try{
                value = args.getString(1);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(value))
            {
                callbackContext.success("value 不能为空");
                return true;
            }
            sharedPreferences.edit().putString(key, value).apply();
            callbackContext.success("ok");
            return true;
        }


        return false;  // Returning false results in a "MethodNotFound" error.
    }

}
