package cn.com.jit.provider.lib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import java.util.Iterator;

import cn.com.jit.mctk.log.config.MLog;

/**
 * @author xin_wang@jit.com.cn
 */
public class JbProviderClient {

    public final static String TAG = "jjjclient";
    private static JbProviderClient client;
    private Context mContext;
    private ProviderListen listen;
    AuthSuccessReceiver successReceiver;
    AuthCanelReceiver canelReceiver;
    private static final String CANEL_BROADCAST = "com.ydjw.unifyauthorize.ACTION_LOGOUT";

    private JbProviderClient() {
    }

    public void regBroadcast() {
        MLog.e(TAG, "--------------regBroadcast11111------------");
        setSuccessBroadcast();
        setCanelBroadcast();
    }

    public void unRegBroadcast() {
        MLog.e(TAG, "--------------unRegBroadcast------------");
        mContext.unregisterReceiver(successReceiver);
        mContext.unregisterReceiver(canelReceiver);
    }

    public void setProviderListen(ProviderListen listen) {
        MLog.e(TAG, "--------------setProviderListen------------");
        this.listen = listen;
    }

    public static synchronized JbProviderClient getInstance(Context ctx) {
        if (client == null && ctx != null) {
            client = new JbProviderClient();
        }
        client.mContext = ctx;
        return client;
    }

    public Response call() {
        MLog.e(TAG, "--------------call1111------------");
//        int taskId = ((Activity)mContext).getTaskId();
//        MLog.e(TAG, "--------------taskId:------------" + taskId);
        if (listen == null) {
            throw new RuntimeException("listen no set");
        }
        Response response = new Response();
        Uri uri = Uri.parse("content://com.ydjw.ua.getCredential");
        ContentResolver resolver = mContext.getContentResolver();
        Bundle params = new Bundle();
        params.putString("messageId", "com.aks.jwy");//消息ID
        params.putString("version", "1");
        params.putString("appId", "e2fb8cd3-b719-450b-8a44-f230761e3c2d");//应用唯一标识【应用注册时，由发布系统提供】
        params.putString("orgId", "650000000000");//区域代码【应用注册时，由应用发布系统提供】
        params.putString("networkAreaCode", "2");//网络类型【应用注册时，由应用发布系统提供】
        params.putString("packageName", "com.aks.jwy");//包名,填写自己app的包名
        Bundle bundle = null;
        try {
            bundle = resolver.call(uri, "", null, params);
        }catch (Exception e){
            MLog.e(TAG, "--------------bundle------------>" + e);
            return null;
        }
//        MLog.e(TAG, "--------------bundle------------>" + bundle);
        if(bundle!=null){
            MLog.e(TAG,"call result:" + showBundleData(bundle));
            int resultCode = bundle.getInt("resultCode");
            String message = bundle.getString("message");
            String userCredential = bundle.getString("userCredential");
            String appCredential = bundle.getString("appCredential");
            String version = bundle.getString("version");
            String packageName = bundle.getString("packageName");
            String messageId = bundle.getString("messageId");
            response.setResultCode(resultCode);
            response.setMessage(message);
            response.setUserCredential(userCredential);
            response.setAppCredential(appCredential);
            response.setVersion(version);
            response.setPackageName(packageName);
            response.setMessageId(messageId);
            response.setBundle(bundle);
        }
        return response;

    }


    public static String showBundleData(Bundle bundle) {
        if(bundle == null) {
            return null;
        } else {
            String content = "Bundle{";

            String key;
            for(Iterator var3 = bundle.keySet().iterator(); var3.hasNext(); content = content + " " + key + " => " + bundle.get(key) + ";") {
                key = (String)var3.next();
            }

            content = content + " }Bundle";
            return content;
        }
    }

    private class Table {
        private final static String column1 = "resultCode";
        private final static String column2 = "message";
        private final static String column3 = "billStr";
    }


    private class AuthSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.e(TAG, "--------------AuthSuccessReceiver onReceive------------");
            JbProviderClient.this.listen.authSuccess();
        }
    }

    private class AuthCanelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.e(TAG, "--------------AuthCanelReceiver onReceive------------");
            JbProviderClient.this.listen.authCanel();
        }
    }


    public interface ProviderListen {
        public void authSuccess();
        public void authCanel();
    }

    private void setSuccessBroadcast() {
        MLog.e(TAG, "--------------setSuccessBroadcast------------");
        successReceiver = new AuthSuccessReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.ydjw.ua.ACTION_LOGIN");
        mContext.registerReceiver(successReceiver, iFilter);
    }

    private void setCanelBroadcast() {
        MLog.e(TAG, "--------------setCanelBroadcast------------");
        canelReceiver = new AuthCanelReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(CANEL_BROADCAST);
        mContext.registerReceiver(canelReceiver, iFilter);
    }
}
