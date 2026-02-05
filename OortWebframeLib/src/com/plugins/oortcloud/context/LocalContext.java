package com.plugins.oortcloud.context;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.idcardreader.ZKTool;
import com.oortcloud.basemodule.CommonApplication;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.HeadInfo;
import com.oortcloud.basemodule.im.MessageEventChangeUI;
import com.oortcloud.basemodule.utils.LocaleHelper;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.plugins.oortcloud.context.message.GroupCreateEvent;
import com.plugins.oortcloud.context.message.MessageEventPreviewPics;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import proxy.Proxy;

public class LocalContext extends CordovaPlugin {

    private Context context;
    private Activity activity;

    private static final String LOG_TAG = "LocalContext";
    private String [] permissions = { Manifest.permission.CAMERA };
    private CallbackContext callbackContext;
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    private ZKTool tool;
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
        if ("getToken".equals(action)) {

            String token = spUser.getString("token","");
            if (token.equals("") ){
//                startTokenOverdue();
                callbackContext.error("fail");
                return true;
            }
            callbackContext.success(token);
            return true;
        }

        else if ("getGateway".equals(action)) {
            String gateway = CommonApplication.getGateway();
            callbackContext.success(gateway);
            return true;
        }
        else if ("get3Gateway".equals(action)) {
            String gateway = Constant.BASE_3CLASSURL;//CommonApplication.getGateway();
            callbackContext.success(gateway);
            return true;
        }else if ("startCheckCard".equals(action)) {

            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {


                        if(tool == null) {
                            tool = new ZKTool(activity);
                        }

                        tool.startIDCardReader(new ZKTool.ReadCallback() {


                            @Override
                            public void callbackpr(com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo info) {
                                callbackContext.success(JSON.toJSONString(info));
                            }

                            @Override
                            public void callback(com.zkteco.android.biometric.module.idcard.meta.IDCardInfo info) {
                                callbackContext.success(JSON.toJSONString(info));
                            }
                        });
                        // 在这里执行读卡器操作，这里仅作示例

                        // 读卡操作完成后，调用回调函数并传递读取到的数据
//                        callbackContext.success(cardData);

//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                SystemClock.sleep(3000);
//                                callbackContext.success("ddddddd");
//                            }
//                        }).start();


////
//                        IDCardManager.INSTANCE.startRead(activity, new IDCardUtil.IDUsbListener() {
//                            @Override
//                            public void onIdUsbListener(@Nullable String idCard, @NonNull byte[] photo) {
//                                HashMap map = new HashMap();
//                                map.put("idCard",idCard);
//                                map.put("photo", Base64.encode(photo, Base64.DEFAULT));
//
//
//                                IDCardManager.INSTANCE.stopRead();
//
//                                callbackContext.success(JSON.toJSONString(map));
//                            }
//                        });
                    } catch (Exception e) {
                        // 处理读卡器操作中的异常，并通过回调函数返回错误信息
                        callbackContext.error("An error occurred while reading card: " + e.getMessage());
                    }
                }
            });
            return true;
            //callbackContext.success("ffffff444");
//            cordova.getThreadPool().execute(new Runnable() {
//                @Override
//                public void run() {
//
//                    new Thread(new Runnable() {
//                       @Override
//                        public void run() {
//                        SystemClock.sleep(5000);
//
//                           try {
//
//                               cordova.getActivity().runOnUiThread(new Runnable() {
//                                   @Override
//                                   public void run() {
//                                       try {
//                                           callbackContext.success("ffffff00");
//                                       } catch (Exception e) {
//                                           // 处理异常
//                                       }
//                                   }
//                               });
//                               //callbackContext.success("ffffff00");
//                           } catch (Exception e) {
//
//                               Exception e1 = e;
//                           }
//                        }
//                   });
//                    //SystemClock.sleep(5000);
//
//
//                }
//
////                    new Thread(new Runnable() {
//////                        @Override
//////                        public void run() {
//////
//////
//////
//////                        }
////                    });
////                }
//            });

//            SystemClock.sleep(1000);
//            callbackContext.success("ffffff00");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    SystemClock.sleep(10000);
//                    callbackContext.success("ffffff00");
//
//                }
//            });


        }
        else if ("isTab".equals(action)) {
            Boolean istab = CommonApplication.isTab();
            if (istab)
                callbackContext.success("true");
            else
                callbackContext.success("false");
            return true;
        }
        /*else if ("get3Gateway".equals(action)) {
            String gateway = CommonApplication.get3Gateway();
            callbackContext.success(gateway);
            return true;
        }*/

        else if ("getHeadInfo".equals(action)) {
            JSONObject jsonObj = new JSONObject(HeadInfo.headParams);
            callbackContext.success(jsonObj);
            return true;
        }

        else if ("getLocalIp".equals(action)) {
            String localip = CommonApplication.getLocalIp();
            callbackContext.success(localip);
            return true;
        }
        else if ("getLocalPath".equals(action)) {
            String path = Constant.LOCAL_PATH;
            callbackContext.success(path);
            return true;
        }
        else if ("getUserId".equals(action)) {
            String userid = spUser.getString("userid","");
            if (userid.equals("") ){
//                startTokenOverdue();
                callbackContext.error("fail");
                return true;
            }
            callbackContext.success(userid);
            return true;
        }
        else if ("runH5App".equals(action)) {
            String url;
            try{
                url = args.getString(0);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(url))
            {
                callbackContext.success("URL不能为空");
                return true;
            }
            this.startH5App(url);
            callbackContext.success("ok");
            return true;
        }else if ("loadHtmlText".equals(action)) {
            String content;
            String title;
            try{
                content = args.getString(0);
                title = args.getString(1);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(content))
            {
                callbackContext.success("content不能为空");
                return true;
            }
            this.startLoadWebText(content,title);
            callbackContext.success("ok");
            return true;
        }else if ("openNewsDetail".equals(action)) {
            String newsId;
            String userId;
            try{
                newsId = args.getString(0);
                userId = args.getString(1);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(newsId))
            {
                callbackContext.success("content不能为空");
                return true;
            }
            this.startLoadWebNewsDetailText(newsId,userId);
            callbackContext.success("ok");
            return true;
        }
        else if ("getTaskInfo".equals(action)) {
            FastSharedPreferences info = FastSharedPreferences.get("TASKINFO");
            String taskinfo = info.getString("task","");
            callbackContext.success(taskinfo);
            return true;
        }
        else if ("openTaskList".equals(action)) {
            this.openTaskList();
            callbackContext.success("ok");
            return true;
        }
        else if ("openMyInfo".equals(action)) {
            this.openMyInfo();
            callbackContext.success("ok");
            return true;
        }
        else if ("appExit".equals(action)) {
            Boolean istab = CommonApplication.isTab();
            this.appExit();
            callbackContext.success("ok");
            return true;
        }
        else if ("openSetting".equals(action)) {
            this.gotosetting();
            callbackContext.success("ok");
            return true;
        }
        else if ("openMessageUI".equals(action)) {
            this.openMessageUI();
            callbackContext.success("ok");
            return true;
        }
        else if ("startAppstore".equals(action))
        {
            String token ;
            String uuid ;
            try{
                token = args.getString(0);
                uuid = args.getString(1);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            if (TextUtils.isEmpty(token))
            {
                callbackContext.success("token不能为空");
                return true;
            }
            if (TextUtils.isEmpty(uuid))
            {
                callbackContext.success("uuid不能为空");
                return true;
            }
            this.startAppstore(token,uuid);
            callbackContext.success("ok");
            return true;
        }
        else if ("startDebug".equals(action))
        {
            this.startDebug();
            callbackContext.success("ok");
            return true;
        }

        else if ("startLive".equals(action))
        {
            //android permission auto add
            if(!hasPermisssion()) {
                requestPermissions(0);
            } else {
                this.startLive();
            }

            callbackContext.success("ok");
            return true;
        }

        else if ("getSNNumber".equals(action)){
            String sn = CommonApplication.getmSeralNum();
            callbackContext.success(sn);
            return true;
        }else if ("getNavColor".equals(action)){
            callbackContext.success("#1156a6");
            return true;
        }
        else if ("getAppVersion".equals(action)){
            String ver = packageName(context);
            callbackContext.success(ver);
            return true;
        }else if("getLanguage".equals(action)){
            String lan = LocaleHelper.getLanguage(context);
            callbackContext.success(lan);
            return true;
        }else if("tokenOverdue".equals(action)){
            this.startTokenOverdue();
            callbackContext.success("ok");
            return true;
        } else if ("previewPic".equals(action)){
            List picArr = new ArrayList<>() ;
            int index= 0; ;
            try{
                Object s = JSON.parse(args.toString());

                if(s instanceof com.alibaba.fastjson.JSONArray){
                    com.alibaba.fastjson.JSONArray ls = (com.alibaba.fastjson.JSONArray) s;
                    com.alibaba.fastjson.JSONArray arr = (com.alibaba.fastjson.JSONArray) ls.get(0);
                    picArr.addAll(arr);
                    index = args.getInt(1);
                    EventBus.getDefault().post(new MessageEventPreviewPics(picArr,index));
                    callbackContext.success("ok");
                }else {
                    callbackContext.error("err");
                }

            }catch(Exception e) {
                callbackContext.error(e.getMessage());
                return true;
            }
//            if (TextUtils.isEmpty(token))
//            {
//                callbackContext.success("token不能为空");
//                return true;
//            }
//            if (TextUtils.isEmpty(uuid))
//            {
//                callbackContext.success("uuid不能为空");
//                return true;
//            }
//            this.startAppstore(token,uuid);
//            callbackContext.success("ok");
            return true;
        } else if ("createGroupIM".equals(action)){
            cordova.getThreadPool().execute(() -> {
                try {
                    JSONObject params = args.getJSONObject(0);
                    String groupCallBackURL = params.optString("groupCallBackURL");
                    String groupAvatar = params.optString("groupAvatar");
                    JSONObject otherOptions = params.optJSONObject("OtherOptions");
                    GroupCreateEvent event = new GroupCreateEvent.Builder(
                            params.getString("groupName"),
                            convertMembers(params.getJSONArray("groupMembers")),
                            callbackContext)
                            .setGroupDesc(params.optString("groupDesc", ""))
                            .setGroupType(params.optString("groupType", "normal"))
                            .setCallbackUrl(params.optString("groupCallBackURL", ""))
                            //.set
                            .buildPending();

                    // 发送到主线程处理
                    cordova.getActivity().runOnUiThread(() -> {
                        EventBus.getDefault().post(event);
                    });

                } catch (JSONException e) {
                    callbackContext.error("参数解析错误: " + e.getMessage());
                }
            });
            return true;

            //return true;
        }


        else if ("canRefresh".equals(action)){
            boolean isrefresh;
            try{
                isrefresh = args.getBoolean(0);
            }catch(Exception e) {
                callbackContext.success(e.getMessage());
                return true;
            }
            CommonApplication.canRefresh = isrefresh;
            callbackContext.success("ok");
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }

    private List<String> convertMembers(JSONArray array) throws JSONException {
        List<String> members = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            members.add(array.getString(i));
        }
        return members;
    }

    private void openMessageUI() {
        EventBus.getDefault().post(new MessageEventChangeUI(1));
    }

    private void gotosetting() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".setting");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
//        activity.finish();
    }

    private void openMyInfo() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".myinfo");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openTaskList() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".chat");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        FastSharedPreferences info = FastSharedPreferences.get("TASKINFO");
//        intent.putExtra("friend",info.getSerializable("friend",""));
        intent.putExtra("tag","webopen");
        context.startActivity(intent);
    }

    private void startGetTaskInfo(String userid, int size) {

    }

    private void startTokenOverdue() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".tokenoverdue");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void startAppstore(String token, String uuid) {
        String appid = context.getApplicationInfo().processName;
                Intent intent = new Intent(appid+ ".app.store");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("token",token);
        intent.putExtra("UUID",uuid);
        context.startActivity(intent);
    }

    private void startDebug() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".app.debug");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startH5App(String url) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".web.container");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private void startLoadWebText(String cotent,String title) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".web.container_text");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("content", cotent);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }


    private void startLoadWebNewsDetailText(String newsId,String userId) {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".web.container_text");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("newsId", newsId);
        intent.putExtra("userId", userId);
        intent.putExtra("type", "2");
        context.startActivity(intent);
    }

    private void startLive() {
        String appid = context.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".classroom.live");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.getActivity().startActivity(intent);

        /*cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 你要执行的代码

            }
        });*/

        /*cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 你要执行的代码
                Intent intent = new Intent("android.action.classroom.live");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cordova.getActivity().startActivity(intent);
            }
        });*/

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
                startLive();
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



