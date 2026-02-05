package com.oortcloud.cordova;

import android.content.Context;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oort.weichat.MyApplication;
import com.oort.weichat.ui.base.CoreManager;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.login.net.RequesManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/11/13 16:28
 * @version： v1.0
 * @function： 提供前端获取短视频信息
 */
public class VideoPlugin  extends CordovaPlugin {
    private CoreManager coreManager;
    private Context context;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = cordova.getActivity().getApplication();
        coreManager =  CoreManager.getInstance(MyApplication.getContext());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
        String result = sharedPreferences.getString("videoResult" , "null");
        callbackContext.success(result);
        //从新获取数据进行刷新
        RequesManager.initVideo(sharedPreferences);
        return true;
    }
}
