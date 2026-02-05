package com.oortcloud.cordova;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.live.CreateLiveActivity;
import com.oort.weichat.ui.live.LiveConstants;
import com.oort.weichat.ui.live.LivePlayingActivity;
import com.oort.weichat.ui.live.PushFlowActivity;
import com.oort.weichat.ui.live.bean.LiveRoom;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.SelectionFrame;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.login.net.RequesManager;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/11/23 10:42
 * @version： v1.0
 * @function：
 */
public class LivePlugin extends CordovaPlugin {
    private CoreManager coreManager;
    private Context context;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = MyApplication.getContext();
        coreManager =  CoreManager.getInstance(MyApplication.getContext());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("addLive".equals(action)){
            //加入直播
            Log.v("msg" , args.getString(0));
            String roomStr = args.getString(0);
            if (TextUtils.isEmpty(roomStr)){
                ToastUtil.showToast(context , "参数为null");
            }else {
                LiveRoom room = new Gson().fromJson(roomStr, new TypeToken<LiveRoom>(){}.getType());
                addLive(room);
            }

        }else if ("createLive".equals(action)){
            //创建直播
            isExistLiveRoom();

        }else {
            FastSharedPreferences sharedPreferences = FastSharedPreferences.get(Constant.LOGIN_RESPONSE);
            String result = sharedPreferences.getString("liveResult" , "null");
            callbackContext.success(result);
            //从新获取数据进行刷新
            RequesManager.initLive(sharedPreferences);
        }

        return true;
    }

    //加入直播间
    public static void addLive(LiveRoom room){
        DialogHelper.showDefaulteMessageProgressDialog(MyApplication.getContext());
        CoreManager coreManager =  CoreManager.getInstance(MyApplication.getContext());
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", room.getRoomId());
        params.put("userId", coreManager.getSelf().getUserId());
        params.put("status", "1");

        HttpUtils.get().url(coreManager.getConfig().JOIN_LIVE_ROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            if (false) {
                                Intent intent = new Intent(MyApplication.getContext(), PushFlowActivity.class);
                                intent.putExtra(LiveConstants.LIVE_PUSH_FLOW_URL, room.getUrl());
                                intent.putExtra(LiveConstants.LIVE_ROOM_ID, room.getRoomId());
                                intent.putExtra(LiveConstants.LIVE_CHAT_ROOM_ID, room.getJid());
                                intent.putExtra(LiveConstants.LIVE_ROOM_NAME, room.getName());
                                intent.putExtra(LiveConstants.LIVE_ROOM_PERSON_ID, String.valueOf(room.getUserId()));
                                intent.putExtra(LiveConstants.LIVE_ROOM_NOTICE, room.getNotice());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                            } else {
                                Intent intent = new Intent(MyApplication.getContext(), LivePlayingActivity.class);
                                intent.putExtra(LiveConstants.LIVE_GET_FLOW_URL, room.getUrl());
                                intent.putExtra(LiveConstants.LIVE_ROOM_ID, room.getRoomId());
                                intent.putExtra(LiveConstants.LIVE_CHAT_ROOM_ID, room.getJid());
                                intent.putExtra(LiveConstants.LIVE_ROOM_NAME, room.getName());
                                intent.putExtra(LiveConstants.LIVE_ROOM_PERSON_ID, String.valueOf(room.getUserId()));
                                intent.putExtra(LiveConstants.LIVE_STATUS, room.getStatus());
                                intent.putExtra(LiveConstants.LIVE_ROOM_NOTICE, room.getNotice());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApplication.getContext().startActivity(intent);
                            }
                        } else {
                            Toast.makeText(MyApplication.getContext(), MyApplication.getContext().getString(R.string.kicked_not_in), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                        ToastUtil.showErrorNet(MyApplication.getContext());
                    }
                });
    }

    private void isExistLiveRoom() {

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", coreManager.getSelf().getUserId());

        HttpUtils.get().url(coreManager.getConfig().LIVE_GET_LIVEROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<LiveRoom>(LiveRoom.class) {
                    @Override
                    public void onResponse(final ObjectResult<LiveRoom> result) {
                        Log.v("msg" , result.toString());
                        DialogHelper.dismissProgressDialog();

                        if (result.getResultCode() == 1) {

                            if (result.getData() != null) {
                                LiveRoom liveRoom = result.getData();
                                if (liveRoom.getCurrentState() == 1) {

                                    DialogHelper.tip(context, MyApplication.getContext().getString(R.string.tip_live_locking));
                                } /*else if (liveRoom.getStatus() != 0) {
                                    DialogHelper.tip(LiveActivity.this, getString(R.string.tip_live_room_online));
                                } */else {
                                    SelectionFrame selectionFrame = new SelectionFrame(MyApplication.activity);
                                    selectionFrame.setSomething(null, context.getString(R.string.you_have_one_live_room) + "，" +
                                            context.getString(R.string.start_live) + "？", new SelectionFrame.OnSelectionFrameClickListener() {
                                        @Override
                                        public void cancelClick() {

                                        }

                                        @Override
                                        public void confirmClick() {  // 进入直播间
                                            LiveRoom liveRoom = result.getData();
                                            openLive(liveRoom.getUrl(), liveRoom.getRoomId(), liveRoom.getJid(), liveRoom.getName(), liveRoom.getNotice());
                                            ReportInfo.video_num = liveRoom.getUrl();
                                            ReportInfo.screen_num = liveRoom.getUrl();
                                            FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");
                                            //存储共享链接
                                            sharedPreferences.edit().putString("share_screen", liveRoom.getUrl()).apply();
//
//                                            updatePoliceInfo();
                                        }
                                    });
                                    selectionFrame.show();
                                }
                            } else { // 创建直播间
                                Intent intent = new Intent(context, CreateLiveActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(context);
                    }
                });
    }

    private void openLive(final String url, final String roomId, final String roomJid, final String roomName, final String roomNotice) {
        DialogHelper.showDefaulteMessageProgressDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", roomId);
        params.put("userId", coreManager.getSelf().getUserId());

        HttpUtils.get().url(coreManager.getConfig().JOIN_LIVE_ROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            Intent intent = new Intent(context, PushFlowActivity.class);
                            intent.putExtra(LiveConstants.LIVE_PUSH_FLOW_URL, url);
                            intent.putExtra(LiveConstants.LIVE_ROOM_ID, roomId);
                            intent.putExtra(LiveConstants.LIVE_CHAT_ROOM_ID, roomJid);
                            intent.putExtra(LiveConstants.LIVE_ROOM_NAME, roomName);
                            intent.putExtra(LiveConstants.LIVE_ROOM_PERSON_ID, coreManager.getSelf().getUserId());
                            intent.putExtra(LiveConstants.LIVE_ROOM_NOTICE, roomNotice);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(context);
                    }
                });
    }
}

