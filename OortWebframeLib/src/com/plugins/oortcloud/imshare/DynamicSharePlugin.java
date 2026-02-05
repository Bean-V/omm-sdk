package com.plugins.oortcloud.imshare;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oortcloud.appstore.AppStoreInit;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DynamicSharePlugin extends CordovaPlugin{
    private Context context;
    private CallbackContext mCallbackContext;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        super.execute(action, args, callbackContext);
        mCallbackContext = callbackContext;

        if (action.equals("dynamic")){

            //分享oort动态
//oortSharePlugin  oort_message   [type,args...]
//  title 标题
//  url 链接， 'oort_duuid=' + this.item.oort_duuid,
//  text 内容
//  path 图片链接或者视频链接或者音频 或者附件
//  type 代表path的类型  image:图片 video:视频 audio:音频 attach:附件

            if(args.length() > 0) {
                JSONObject obj = args.getJSONObject(0);
                try {
                    obj.put("sub",obj.getString("text"));

//                    String atttype = obj.getString("type");
//                    if(atttype != null){
//                        if(atttype.equals(""))
//                    }

                    obj.put("atttype", !obj.optString("type").equals("") ?obj.optString("type") : "text" );
                    obj.put("type","dynamic");

                    if(!obj.optString("path").equals("")){
                        obj.put("img",obj.getString("path"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Context mContext = AppStoreInit.getInstance().getApplication();
                String appid = mContext.getApplicationInfo().processName;
                Intent in = new Intent(appid + ".shareFriend");
                in.putExtra("action","shareFriend");
                in.putExtra("content",obj.toString());

                context.startActivity(in);
                return true;
            }
        }


        return true;
    }
}
