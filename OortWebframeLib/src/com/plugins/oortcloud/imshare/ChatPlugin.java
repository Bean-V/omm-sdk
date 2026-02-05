package com.plugins.oortcloud.imshare;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;


public class ChatPlugin extends CordovaPlugin{
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

        if (action.equals("createRoom")){

            //分享oort动态
//oortSharePlugin  oort_message   [type,args...]
//  title 标题
//  url 链接， 'oort_duuid=' + this.item.oort_duuid,
//  text 内容
//  path 图片链接或者视频链接或者音频 或者附件
//  type 代表path的类型  image:图片 video:视频 audio:音频 attach:附件

           JSONObject jobj = JSON.parseObject(args.getString(0));

//            JSONObject jobj = new JSONObject();


//            jobj.put("roomId","55402d6c-61ec-11ef-8b02-a6f71ae7af52");
//            jobj.put("groupPersons","a0754f8-c4c9-4bb2-8969-766ff2fbf80b,6799ea6d-dec6-4b34-961c-a7b5f8c6c900");
//            jobj.put("packageName","com.oort.flow");
//            jobj.put("roomName","5学堂课程审核");


            String[] ids = jobj.getString("groupPersons").split(",");

            EventBus.getDefault().post(new ChatPluginCreateRoomMessage(jobj.getString("roomId"),jobj.getString("roomName"),ids,"",0,jobj.getString("packageName")));
        }


        return true;
    }

    public static String[] convertJSONArrayToStringArray(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }
}
