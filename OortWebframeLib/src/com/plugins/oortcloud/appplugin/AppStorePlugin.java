package com.plugins.oortcloud.appplugin;

import android.text.TextUtils;

import com.oortcloud.appstore.utils.AppManager;
import com.oortcloud.basemodule.utils.OperLogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/9/16 11:22
 * @version： v1.0
 * @function：提供前端获h5应用信息
 */
public class AppStorePlugin  extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {



        OperLogUtil.msg("h5获取app" +AppManager.getH5Info());
        if (TextUtils.isEmpty(AppManager.getH5Info())){
            callbackContext.error("error");
        }else {

            //callbackContext.error("error");
            callbackContext.success(AppManager.getH5Info());
        }

        return true;
    }
}
