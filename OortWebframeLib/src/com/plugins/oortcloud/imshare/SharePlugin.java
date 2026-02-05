package com.plugins.oortcloud.imshare;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;

import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.db.DataInit;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharePlugin extends CordovaPlugin{
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

        if (action.equals("share")){

            //分享oort动态
//oortSharePlugin  oort_message   [type,args...]
//  title 标题
//  url 链接， 'oort_duuid=' + this.item.oort_duuid,
//  text 内容
//  path 图片链接或者视频链接或者音频 或者附件
//  type 代表path的类型  image:图片 video:视频 audio:音频 attach:附件

            JSONObject obj = new JSONObject();
            if(args.length() == 2) {
                //分享app


                try {
                    obj.put("type","share_app");
                    obj.put("package_name",args.getString(1));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Context mContext = AppStoreInit.getInstance().getApplication();
                String appid = mContext.getApplicationInfo().processName;
                Intent in = new Intent(appid + ".shareFriend");
                in.putExtra("action","shareFriend");
                in.putExtra("content",obj.toString());

                in.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
                return true;
            }else if(args.length() > 2){


                //分享app
                AppInfo app = DataInit.getAppinfo(args.getString(1));

                try {
                    obj.put("sub",args.getString(4));

                    obj.put("title",args.getString(3));

                    obj.put("path",args.getString(2));

                    obj.put("type","share_app_content");
                    if(args.length() > 5) {
                        obj.put("img", args.getString(5));
                    }
                    if(args.length() > 6) {
                        obj.put("content", args.getString(6));
                    }

                    obj.put("package_name",args.getString(1));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Context mContext = AppStoreInit.getInstance().getApplication();
                String appid = mContext.getApplicationInfo().processName;
                Intent in = new Intent(appid + ".shareFriend");
                in.putExtra("action","shareFriend");
                in.putExtra("content",obj.toString());

                if(args.length() > 6){
                    in.putExtra("type","review");
                }
                in.setFlags(FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(in);
            }
        }


        return true;
    }
}




