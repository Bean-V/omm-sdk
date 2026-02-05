package com.oort.weichat.ui.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oort.imagepicksdk.ImagePickSdk;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.oort.weichat.AppConfig;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.audio_x.VoiceManager;
import com.oort.weichat.audio_x.VoicePlayer;
import com.oort.weichat.bean.Contacts;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.PrivacySetting;
import com.oort.weichat.bean.PublicMenu;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.VideoFile;
import com.oort.weichat.bean.assistant.GroupAssistantDetail;
import com.oort.weichat.bean.collection.CollectionEvery;
import com.oort.weichat.bean.event.EventNotifyByTag;
import com.oort.weichat.bean.event.EventSyncFriendOperating;
import com.oort.weichat.bean.event.EventTransfer;
import com.oort.weichat.bean.event.EventUploadCancel;
import com.oort.weichat.bean.event.EventUploadFileRate;
import com.oort.weichat.bean.event.MessageEventClickable;
import com.oort.weichat.bean.event.MessageEventRequert;
import com.oort.weichat.bean.event.MessageLocalVideoFile;
import com.oort.weichat.bean.event.MessageUploadChatRecord;
import com.oort.weichat.bean.event.MessageVideoFile;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.ChatRecord;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.bean.redpacket.EventRedReceived;
import com.oort.weichat.bean.redpacket.OpenRedpacket;
import com.oort.weichat.bean.redpacket.RedDialogBean;
import com.oort.weichat.broadcast.MsgBroadcast;
import com.oort.weichat.broadcast.OtherBroadcast;
import com.oort.weichat.call.CallConstants;
import com.oort.weichat.call.Jitsi_connecting_second;
import com.oort.weichat.call.Jitsi_pre;
import com.oort.weichat.call.MessageEventClicAudioVideo;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.VideoFileDao;
import com.oort.weichat.downloader.Downloader;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.helper.PrivacySettingHelper;
import com.oort.weichat.helper.TrillStatisticsHelper;
import com.oort.weichat.helper.UploadEngine;
import com.oort.weichat.helper.YeepayHelper;
import com.oort.weichat.pay.TransferMoneyActivity;
import com.oort.weichat.pay.TransferMoneyDetailActivity;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.contacts.SendContactsActivity;
import com.oort.weichat.ui.dialog.CreateCourseDialog;
import com.oort.weichat.ui.map.MapPickerActivity;
import com.oort.weichat.ui.me.MyCollection;
import com.oort.weichat.ui.me.redpacket.RedDetailsActivity;
import com.oort.weichat.ui.me.redpacket.SendRedPacketActivity;
import com.oort.weichat.ui.message.single.PersonSettingActivity;
import com.oort.weichat.ui.mucfile.XfileUtils;
import com.oort.weichat.util.AsyncUtils;
import com.oort.weichat.util.AudioModeManger;
import com.oort.weichat.util.Base64;
import com.oort.weichat.util.BitmapUtil;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.util.FormStatuManager;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.LogUtils;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.RecorderUtils;
import com.oort.weichat.util.RocketAnimationManager;
import com.oort.weichat.util.SmileyParser;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.VideoCompressUtil;
import com.oort.weichat.util.log.FileUtils;
import com.oort.weichat.util.secure.AES;
import com.oort.weichat.util.secure.SimpleEncryptUtil;
import com.oort.weichat.util.secure.chat.SecureChatUtil;
import com.oort.weichat.video.MessageEventGpu;
import com.oort.weichat.video.VideoRecorderActivity;
import com.oort.weichat.view.ChatBottomView;
import com.oort.weichat.view.ChatBottomView.ChatBottomListener;
import com.oort.weichat.view.ChatContentView;
import com.oort.weichat.view.ChatContentView.MessageEventListener;
import com.oort.weichat.view.NoDoubleClickListener;
import com.oort.weichat.view.PullDownListView;
import com.oort.weichat.view.SelectCardPopupWindow;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.chatHolder.MessageEventClickFire;
import com.oort.weichat.view.photopicker.PhotoPickerActivity;
import com.oort.weichat.view.photopicker.SelectModel;
import com.oort.weichat.view.photopicker.intent.PhotoPickerIntent;
import com.oort.weichat.view.redDialog.RedDialog;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.listener.ChatMessageListener;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.dialog.inputpsw.dialog.PswInputDialog;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.contacts.activity.PersonDetailActivity;
import com.oortcloud.contacts.bean.omm.AttentionUser;
import com.oortcloud.utils.ChangeUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import org.apache.commons.logging.LogFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import Jni.VideoUitls;
import VideoHandle.OnEditorListener;
import fm.jiecao.jcvideoplayer_lib.JCMediaManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JVCideoPlayerStandardforchat;
import fm.jiecao.jcvideoplayer_lib.MessageEvent;
import okhttp3.Call;
import pl.droidsonroids.gif.GifDrawable;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 单聊界面
 */
public class ChatActivity extends BaseActivity implements
        MessageEventListener, ChatBottomListener, ChatMessageListener,
        SelectCardPopupWindow.SendCardS {

    public static final String FRIEND = "friend";
    /*输入红包金额的返回*/
    public static final int REQUEST_CODE_SEND_RED = 13;     // 发红包
    public static final int REQUEST_CODE_SEND_RED_PT = 10;  // 普通红包返回
    public static final int REQUEST_CODE_SEND_RED_KL = 11;  // 口令红包返回
    public static final int REQUEST_CODE_SEND_RED_PSQ = 12; // 拼手气红包返回
    // 发送联系人，
    public static final int REQUEST_CODE_SEND_CONTACT = 21;
    /***********************
     * 拍照和选择照片
     **********************/
    private static final int REQUEST_CODE_CAPTURE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static final int REQUEST_CODE_SELECT_VIDEO = 3;
    private static final int REQUEST_CODE_SEND_COLLECTION = 4;// 我的收藏 返回
    private static final int REQUEST_CODE_SELECT_Locate = 5;
    private static final int REQUEST_CODE_QUICK_SEND = 6;
    private static final int REQUEST_CODE_SELECT_FILE = 7;
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(ChatActivity.class);
    private FastSharedPreferences spUser = FastSharedPreferences.get("USERINFO_SAVE");
    RefreshBroadcastReceiver receiver = new RefreshBroadcastReceiver();
    /*******************************************
     * 自动同步其他端收发的消息 && 获取漫游聊天记录
     ******************************************/
    List<ChatMessage> chatMessages;
    @SuppressWarnings("unused")
    private ChatContentView mChatContentView;
    // 存储聊天消息
    private List<ChatMessage> mChatMessages;
    private ChatBottomView mChatBottomView;
    private ImageView mChatBgIv;// 聊天背景
    private AudioModeManger mAudioModeManger;
    // 当前聊天对象
    private Friend mFriend;
    private String mLoginUserId;
    private String mLoginNickName;
    private RocketAnimationManager mRocketAnimationManager;
    private boolean isSearch;
    private double mSearchTime;
    // 消息转发
    private String instantMessage;

    // 是否为通知栏进入
    private boolean isNotificationComing;
    // 我的黑名单列表
    private List<Friend> mBlackList;
    private TextView mTvTitleLeft;
    // 在线 || 离线...
    private TextView mTvTitle;

    private WebView web;
    // 对方正在输入
    CountDownTimer time = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (coreManager.getConfig().isOpenOnlineStatus) {
                String remarkName = mFriend.getRemarkName();
                if (TextUtils.isEmpty(remarkName)) {
                    mTvTitle.setText(mFriend.getNickName() + getString(R.string.online));
                } else {
                    mTvTitle.setText(remarkName + getString(R.string.online));
                }
            } else {
                mTvTitle.setText(TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName());
            }
        }
    };
    // 是否为阅后即焚
    private int isReadDel;
    private String userId;// 如果isDevice==1，代表当前的聊天对象为我的手机 || 我的电脑，当发消息时，userId要等于自己的id，而非ios || pc...;
    private long mMinId = 0;
    private int mPageSize = 20;
    private boolean mHasMoreData = true;
    private boolean isSecureAlreadyTipd;// 端到端是否提示过了，仅提醒一次
    /**
     * 文件上传响应回调
     * 功能说明：
     * 1. 上传成功：文件上传成功后，通过XMPP发送消息
     * 2. 上传失败：文件上传失败时，更新消息状态为发送失败
     * 3. 状态更新：更新数据库中的消息发送状态
     * 4. 界面刷新：刷新聊天界面显示最新的消息状态
     */
    private UploadEngine.ImFileUploadResponse mUploadResponse = new UploadEngine.ImFileUploadResponse() {

        /**
         * 文件上传成功回调
         *
         * @param toUserId 接收者用户ID
         * @param message 上传成功的消息对象
         */
        @Override
        public void onSuccess(String toUserId, ChatMessage message) {
            sendMsg(message);
        }

        /**
         * 文件上传失败回调
         *
         * @param toUserId 接收者用户ID
         * @param message 上传失败的消息对象
         */
        @Override
        public void onFailure(String toUserId, ChatMessage message) {
            for (int i = 0; i < mChatMessages.size(); i++) {
                ChatMessage msg = mChatMessages.get(i);
                if (message.get_id() == msg.get_id()) {
                    msg.setMessageState(ChatMessageListener.MESSAGE_SEND_FAILED);
                    ChatMessageDao.getInstance().updateMessageSendState(mLoginUserId, mFriend.getUserId(),
                            message.get_id(), ChatMessageListener.MESSAGE_SEND_FAILED);
                    mChatContentView.notifyDataSetInvalidated(false);
                    break;
                }
            }
        }
    };

    private Uri mNewPhotoUri;
    private HashSet<String> mDelayDelMaps = new HashSet<>();// 记录阅后即焚消息的 packedid
    private ChatMessage replayMessage;

    private RedDialog mRedDialog;
    private boolean isUserSaveContentMethod = true;

    public static void start(Context ctx, Friend friend) {
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra(ChatActivity.FRIEND, friend);
        ctx.startActivity(intent);
    }

    /**
     * 通知聊天页面关闭，
     * 比如被删除被拉黑，
     *
     * @param content 弹Toast的内容，
     */
    public static void callFinish(Context ctx, String content, String toUserId) {
        Intent intent = new Intent();
        intent.putExtra("content", content);
        intent.putExtra("toUserId", toUserId);
        intent.setAction(OtherBroadcast.TYPE_DELALL);
        ctx.sendBroadcast(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        SmileyParser.getInstance(MyApplication.getContext()).notifyUpdate();
        /*AndroidBug5497Workaround.assistActivity(this);*/
//        mLoginUserId = spUser.getString("userid", "");
        mLoginUserId = coreManager.getSelf().getUserId();
        mLoginNickName = coreManager.getSelf().getNickName();
        if (getIntent() != null) {

            if (getIntent().getStringExtra("tag") != null && getIntent().getStringExtra("tag").equals("webopen")) {
                mFriend = MainActivity.PUBLIC_FRIEND;
            } else {
                //通讯录启动消息页
                Object attentionUser = getIntent().getSerializableExtra(AppConstant.EXTRA_FRIEND);
                if (attentionUser instanceof AttentionUser) {

                    mFriend = ChangeUtils.changeFriend((AttentionUser) attentionUser);
                } else {
                    mFriend = (Friend) attentionUser;
                }
            }
            isSearch = getIntent().getBooleanExtra("isserch", false);
            if (isSearch) {
                mSearchTime = getIntent().getDoubleExtra("jilu_id", 0);
            }
            instantMessage = getIntent().getStringExtra("messageId");
            isNotificationComing = getIntent().getBooleanExtra(Constants.IS_NOTIFICATION_BAR_COMING, false);

            boolean isvideo = getIntent().getBooleanExtra("isvideo", false);

            if (isvideo) {
                clickVideoChat();
            }
        }
        if (mFriend == null) {
//            ToastUtil.showToast(mContext, getString(R.string.tip_friend_not_found));
            ToastUtil.showToast(mContext, "没有更多消息");
            finish();
            return;
        }
        if (mFriend.getIsDevice() == 1) {
            userId = mLoginUserId;
        }
        // mSipManager = SipManager.getInstance();
        mAudioModeManger = new AudioModeManger();
        mAudioModeManger.register(mContext);
        mRocketAnimationManager = new RocketAnimationManager(this);
        Downloader.getInstance().init(MyApplication.getInstance().mAppDir + File.separator + mLoginUserId
                + File.separator + Environment.DIRECTORY_MUSIC);
        initView();
        // 添加新消息来临监听
        LogUtils.log("fffffff_addChatMessageListener" + mFriend.getNickName() + "/" + mFriend.getRemarkName());
        ListenerManager.getInstance().addChatMessageListener(this);
        // 注册EventBus
        EventBus.getDefault().register(this);
        // 注册广播
        register();

        if (coreManager.getConfig().enableMpModule && mFriend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)) {
            // 未知原因导致系统号的status变为好友，兼容一下
            FriendDao.getInstance().updateFriendStatus(mLoginUserId, userId, Friend.STATUS_SYSTEM);
            initSpecialMenu();
        } else {
            // 获取聊天对象当前的在线状态
            initFriendState();
        }
        if (Constant.DEFAULT_MSG_NO_FIRE) {
            SharedPreferences sp = getSharedPreferences("UserConfig", MODE_PRIVATE);

            // 2. 存储配置（用户 ID 为键，配置为值）
            String userId = mFriend.getUserId();

            // 3. 获取配置（根据用户 ID 读取）
            String savedConfig = sp.getString(userId, null);
            if (savedConfig == null) {

                String config = "1";
                sp.edit().putString(userId, config).apply(); // 存储

                updateDisturbStatus(1, false);
                // 使用配置
            }
        }
    }



    private void updateDisturbStatus(final int type, final boolean isChecked) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", mLoginUserId);
        params.put("toUserId", mFriend.getUserId());
        params.put("type", String.valueOf(type));
        params.put("offlineNoPushMsg", isChecked ? String.valueOf(1) : String.valueOf(0));
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_NOPULL_MSG)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            if (type == 0) {// 消息免打扰

                            } else if (type == 1) {// 阅后即焚
                                PreferenceUtils.putInt(mContext, Constants.MESSAGE_READ_FIRE + mFriend.getUserId() + mLoginUserId, isChecked ? 1 : 0);
                            } else {// 置顶聊天

                            }
                        } else {

                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        // DialogHelper.dismissProgressDialog();
                        // ToastUtil.showNetError(PersonSettingActivity.this);
                    }
                });
    }

    private void initView() {
        mChatMessages = new ArrayList<>();
        mChatBottomView = (ChatBottomView) findViewById(R.id.chat_bottom_view);
        mChatContentView = (ChatContentView) findViewById(R.id.chat_content_view);
        initActionBar();
        mChatBottomView.setChatBottomListener(this);
        mChatBottomView.getmShotsLl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatBottomView.getmShotsLl().setVisibility(View.GONE);
                String shots = PreferenceUtils.getString(mContext, Constants.SCREEN_SHOTS, "No_Shots");
                QuickSendPreviewActivity.startForResult(ChatActivity.this, shots, REQUEST_CODE_QUICK_SEND);
            }
        });
        if (mFriend.getIsDevice() == 1) {
            mChatBottomView.setEquipment(true);
            mChatContentView.setChatListType(ChatContentView.ChatListType.DEVICE);
        }

        mChatContentView.setToUserId(mFriend.getUserId());
        mChatContentView.setData(mChatMessages);
        mChatContentView.setChatBottomView(mChatBottomView);// 需要获取多选菜单的点击事件
        mChatContentView.setMessageEventListener(this);
        mChatContentView.setRefreshListener(new PullDownListView.RefreshingListener() {
            @Override
            public void onHeaderRefreshing() {
                loadDatas(false);
            }
        });
        // 有阅后即焚消息显示时禁止截屏，
        mChatContentView.addOnScrollListener(new AbsListView.OnScrollListener() {
            boolean needSecure = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view instanceof ListView) {
                    int headerCount = ((ListView) view).getHeaderViewsCount();
                    firstVisibleItem -= headerCount;
                    totalItemCount -= headerCount;
                }
                if (firstVisibleItem < 0) {
                    // 如果有header什么的导致firstVisibleItem小于0，无视小于0的部分，
                    firstVisibleItem = 0;
                }
                if (visibleItemCount <= 0) {
                    return;
                }

                List<ChatMessage> visibleList = mChatMessages.subList(firstVisibleItem, Math.min(firstVisibleItem + visibleItemCount, totalItemCount));
                boolean lastSecure = needSecure;
                needSecure = false;
                for (ChatMessage message : visibleList) {
                    if (message.getIsReadDel()
                            && message.getType() != XmppMessage.TYPE_TIP) {// 已方可能存在阅后即焚的tip消息，过滤掉
                        needSecure = true;
                        break;
                    }
                }
                if (needSecure != lastSecure) {
                    if (needSecure) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    } else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    }
                }
            }
        });

        // CoreManager.updateMyBalance();

        if (isNotificationComing) {
            Intent intent = new Intent();
            intent.putExtra(AppConstant.EXTRA_FRIEND, mFriend);
            intent.setAction(Constants.NOTIFY_MSG_SUBSCRIPT);
            sendBroadcast(intent);
        } else {
            FriendDao.getInstance().markUserMessageRead(mLoginUserId, mFriend.getUserId());
        }

        loadDatas(true);
        if (mFriend.getDownloadTime() < mFriend.getTimeSend()) {// 自动同步其他端的聊天记录
            synchronizeChatHistory();
        }

        if (Constant.HAVA_NOTE) {
            findViewById(R.id.tv_top_announce).setVisibility(View.VISIBLE);
        }

        TextView tvTopMessage = findViewById(R.id.tv_top_message);
        TextView tvTopAnnounce = findViewById(R.id.tv_top_announce);
        findViewById(R.id.tv_top_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.vb_chat_group_record).setVisibility(View.GONE);
                setSelectedState(tvTopMessage, true);
                setSelectedState(tvTopAnnounce, false);
            }
        });

        findViewById(R.id.tv_top_announce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedState(tvTopMessage, false);
                setSelectedState(tvTopAnnounce, true);

                ArrayList<String> arr = new ArrayList<>();
                arr.add(mLoginUserId);
                arr.add(mFriend.getUserId());
                arr.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {

                        return o1.compareTo(o2);
                    }
                });

                String id = arr.get(0) + arr.get(1);
//
//                Activity_chat_announce.start(ChatActivity.this,mLoginNickName, UserInfoUtils.getInstance(ChatActivity.this).getLoginUserInfo().getOort_photo(),id);


                findViewById(R.id.vb_chat_group_record).setVisibility(View.VISIBLE);

                String name = mLoginNickName;
                String icon = UserInfoUtils.getInstance(ChatActivity.this).getLoginUserInfo().getOort_photo();
                String announceId = id;

//web.loadUrl("https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693")

                String webUrl = String.format(Constant.BASE_URL + "oort/oortcloud-xpad/p/%s?userName=%s&icon=%s", announceId, name, icon);

                //webUrl = "https://movespeed.m.tmall.com/?ajson=1&parentCatId=0&user_id=2200798683101&item_id=643498606693";
                web.loadUrl(webUrl);

//        v.findViewById(R.id.ll_check).setVisibility(View.GONE);
//        web.loadUrl("https://oortcloudsmart.com/");

                web.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本

            }
        });


        web = findViewById(R.id.web);
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边

                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    return true;
                }

            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    web.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
            }


        });

    }

    private void setSelectedState(TextView textView, boolean isSelected) {
        if (isSelected) {
            // 设置选中状态的背景和文本颜色
            textView.setBackgroundResource(R.drawable.background_chat_top_msg);
            textView.setTextColor(getResources().getColor(R.color.selected_text_color, null));
        } else {
            // 设置非选中状态的背景和文本颜色
            textView.setBackgroundResource(android.R.color.transparent); // 假设非选中时背景透明，你可按需修改
            textView.setTextColor(getResources().getColor(R.color.unselected_text_color, null));
        }
    }

    // 1. 定义后台线程池和主线程Handler
// 优化：使用ThreadPoolExecutor替代Executors，更可控
    private ExecutorService dbExecutor = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "ChatDataLoader-DBThread")
    );
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String TAG = "ChatDataLoader"; // 日志标签，便于过滤

    private void loadDatas(boolean scrollToBottom) {
        if (mChatMessages.size() > 0) {
            mMinId = mChatMessages.get(0).getTimeSend();
        } else {
            ChatMessage chat = ChatMessageDao.getInstance().getLastChatMessage(mLoginUserId, mFriend.getUserId());
            if (chat != null && chat.getTimeSend() != 0) {
                mMinId = chat.getTimeSend() + 2;
            } else {
                mMinId = TimeUtils.sk_time_current_time();
            }
        }

        List<ChatMessage> chatLists;
        if (isSearch) {// 查询时就不做分页限制了，因为被查询的消息可能在二十条以外
            chatLists = ChatMessageDao.getInstance().searchMessagesByTime(mLoginUserId,
                    mFriend.getUserId(), mSearchTime);
        } else {
            chatLists = ChatMessageDao.getInstance().getSingleChatMessages(mLoginUserId,
                    mFriend.getUserId(), mMinId, mPageSize);
        }

        if (chatLists == null || chatLists.size() <= 0) {
            if (!scrollToBottom) {// 加载漫游
                getNetSingle();
            } else {
                if (mFriend.getEncryptType() == 3) {// 本地无消息记录
                    sendSecureChatReadyTip();
                }
            }
        } else {
            mTvTitle.post(new Runnable() {
                @Override
                public void run() {
                    long currTime = TimeUtils.sk_time_current_time();
                    for (int i = 0; i < chatLists.size(); i++) {
                        ChatMessage message = chatLists.get(i);
                        // 防止过期的消息出现在列表中
                        if (message.getDeleteTime() > 0 && message.getDeleteTime() < currTime) {
                            ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), message.getPacketId());
                            continue;
                        }
                        if (message.isVerifySignatureFailed()) {// 单聊验签失败的消息不显示
                            continue;
                        }

                        mChatMessages.add(0, message);
                    }
                    LogUtils.log("fffffffgetChatMessages" + mChatMessages.size());
                    if (isSearch) {
                        isSearch = false;
                        int position = 0;
                        for (int i = 0; i < mChatMessages.size(); i++) {
                            if (mChatMessages.get(i).getDoubleTimeSend() == mSearchTime) {
                                position = i;
                            }
                        }
                        mChatContentView.notifyDataSetInvalidated(position);// 定位到该条消息
                    } else {
                        if (mFriend.getEncryptType() == 3) {// 本地有消息记录
                            sendSecureChatReadyTip();
                        }
                        if (scrollToBottom) {
                            mChatContentView.notifyDataSetInvalidatedForSetSelectionInvalid(scrollToBottom);
                        } else {
                            mChatContentView.notifyDataSetAddedItemsToTop(chatLists.size());
                        }
                    }
                    mChatContentView.headerRefreshingCompleted();
                    if (!mHasMoreData) {
                        mChatContentView.setNeedRefresh(false);
                    }
                }
            });
        }
    }
    private void loadDatas_(boolean scrollToBottom) {
        Log.d(TAG, "开始加载数据，scrollToBottom: " + scrollToBottom);
        // 1. 异步获取最后一条消息，计算mMinId
        ChatMessageDao.getInstance().getLastChatMessageAsync(mLoginUserId, mFriend.getUserId(),
                new ChatMessageDao.OnOperationResultListener<ChatMessage>() {
                    @Override
                    public void onResult(ChatMessage lastChat) {
                        Log.d(TAG, "获取最后一条消息结果: " + (lastChat != null ?
                                "存在（timeSend: " + lastChat.getTimeSend() + "）" : "不存在"));

                        long mMinId;
                        if (mChatMessages.size() > 0) {
                            // 核心优化：mMinId = 最早消息时间戳 - 1，避免重复加载同时间消息
                            mMinId = mChatMessages.get(0).getTimeSend() - 1;
                            Log.d(TAG, "mChatMessages非空，取第一条消息的timeSend-1作为mMinId: " + mMinId);
                        } else {
                            if (lastChat != null && lastChat.getTimeSend() != 0) {
                                mMinId = lastChat.getTimeSend() + 2;
                                Log.d(TAG, "lastChat存在，mMinId = lastChat.timeSend + 2: " + mMinId);
                            } else {
                                mMinId = TimeUtils.sk_time_current_time();
                                Log.d(TAG, "lastChat不存在或timeSend为0，mMinId = 当前时间: " + mMinId);
                            }
                        }

                        // 2. 根据查询类型，异步获取消息列表
                        if (isSearch) {
                            Log.d(TAG, "进入搜索模式，搜索时间: " + mSearchTime);
                            ChatMessageDao.getInstance().searchMessagesByTimeAsync(
                                    mLoginUserId, mFriend.getUserId(), mSearchTime,
                                    new ChatMessageDao.OnOperationResultListener<List<ChatMessage>>() {
                                        @Override
                                        public void onResult(List<ChatMessage> chatLists) {
                                            Log.d(TAG, "搜索消息返回，chatLists大小: " + (chatLists != null ? chatLists.size() : 0));
                                            processChatLists(chatLists, scrollToBottom);
                                        }
                                    }
                            );
                        } else {
                            Log.d(TAG, "进入常规加载模式，mMinId: " + mMinId + ", 分页大小: " + mPageSize);
                            ChatMessageDao.getInstance().getSingleChatMessages(
                                    mLoginUserId, mFriend.getUserId(), mMinId, mPageSize,
                                    new ChatMessageDao.OnOperationResultListener<List<ChatMessage>>() {
                                        @Override
                                        public void onResult(List<ChatMessage> chatLists) {
                                            Log.d(TAG, "常规加载消息返回，chatLists大小: " + (chatLists != null ? chatLists.size() : 0));
                                            processChatLists(chatLists, scrollToBottom);
                                        }
                                    }
                            );
                        }
                    }
                });
    }

    /**
     * 处理查询到的消息列表（过滤、删除过期消息、去重、排序）
     */
    private void processChatLists(List<ChatMessage> chatLists, boolean scrollToBottom) {
        Log.d(TAG, "开始处理消息列表，原始chatLists大小: " + (chatLists != null ? chatLists.size() : 0) + ", scrollToBottom: " + scrollToBottom);
        final List<ChatMessage> filteredChats = new ArrayList<>();

        if (chatLists != null && !chatLists.isEmpty()) {
            long currTime = TimeUtils.sk_time_current_time();
            Log.d(TAG, "当前时间: " + currTime + ", 需要处理的消息数量: " + chatLists.size());
            // 使用计数器确保所有异步删除操作完成后再更新UI
            CountDownLatch deleteLatch = new CountDownLatch(chatLists.size());
            Log.d(TAG, "初始化删除操作计数器，初始值: " + deleteLatch.getCount());

            // 用于去重的临时集合（保留插入顺序）
            LinkedHashSet<ChatMessage> uniqueMessages = new LinkedHashSet<>();

            for (int i = 0; i < chatLists.size(); i++) {
                ChatMessage message = chatLists.get(i);
                Log.d(TAG, "处理第" + (i + 1) + "条消息，packetId: " + message.getPacketId() +
                        ", deleteTime: " + message.getDeleteTime() + ", 签名验证: " + (message.isVerifySignatureFailed() ? "失败" : "成功"));

                // 异步删除过期消息
                if (message.getDeleteTime() > 0 && message.getDeleteTime() < currTime) {
                    Log.d(TAG, "消息过期（deleteTime < 当前时间），执行删除，packetId: " + message.getPacketId());
                    ChatMessageDao.getInstance().deleteSingleChatMessageAsync(
                            mLoginUserId, mFriend.getUserId(), message.getPacketId(),
                            new ChatMessageDao.OnOperationResultListener<Boolean>() {
                                @Override
                                public void onResult(Boolean success) {
                                    Log.d(TAG, "删除过期消息结果，packetId: " + message.getPacketId() + ", 成功: " + success);
                                    deleteLatch.countDown();
                                    Log.d(TAG, "删除计数器更新，剩余: " + deleteLatch.getCount());
                                }
                            }
                    );
                    continue;
                }

                // 过滤签名验证失败的消息
                if (message.isVerifySignatureFailed()) {
                    Log.d(TAG, "消息签名验证失败，过滤掉，packetId: " + message.getPacketId());
                } else {
                    uniqueMessages.add(message); // 自动去重（依赖ChatMessage的equals/hashCode）
                    Log.d(TAG, "消息通过过滤，添加到列表，packetId: " + message.getPacketId() + ", 当前uniqueMessages大小: " + uniqueMessages.size());
                }
                deleteLatch.countDown();
                Log.d(TAG, "消息处理完成，计数器更新，剩余: " + deleteLatch.getCount());
            }

            // 转换为List并按时间倒序排序（确保时间线正确）
            filteredChats.addAll(uniqueMessages);
            filteredChats.sort((msg1, msg2) -> Long.compare(msg2.getTimeSend(), msg1.getTimeSend()));
            Log.d(TAG, "消息去重排序完成，filteredChats最终大小: " + filteredChats.size());

            // 等待所有删除操作完成后再更新UI
            dbExecutor.submit(() -> {
                Log.d(TAG, "进入数据库线程，等待所有删除操作完成...");
                try {
                    deleteLatch.await(); // 等待所有删除操作完成
                    Log.d(TAG, "所有删除操作已完成，准备更新UI，filteredChats最终大小: " + filteredChats.size());
                    updateUIFromChats(filteredChats, scrollToBottom);
                } catch (InterruptedException e) {
                    Log.e(TAG, "等待删除操作被中断", e);
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    updateUIFromChats(filteredChats, scrollToBottom);
                }
            });
        } else {
            Log.d(TAG, "chatLists为空或null，直接更新UI");
            updateUIFromChats(filteredChats, scrollToBottom);
        }
    }

    /**
     * 切换到主线程更新UI
     */
    private void updateUIFromChats(List<ChatMessage> filteredChats, boolean scrollToBottom) {
        Log.d(TAG, "准备更新UI，filteredChats大小: " + filteredChats.size() + ", scrollToBottom: " + scrollToBottom);
        mainHandler.post(() -> {
            Log.d(TAG, "进入主线程更新UI");
            if (filteredChats.isEmpty()) {
                // 空数据处理
                Log.d(TAG, "filteredChats为空");
                if (!scrollToBottom) {
                    Log.d(TAG, "scrollToBottom为false，调用getNetSingle()加载网络消息");
                    getNetSingle();
                } else {
                    Log.d(TAG, "scrollToBottom为true");
                    if (mFriend.getEncryptType() == 3) {
                        Log.d(TAG, "加密类型为3，发送安全聊天就绪提示");
                        sendSecureChatReadyTip();
                    }
                }
            } else {
                Log.d(TAG, "filteredChats非空，开始更新消息列表，大小: " + filteredChats.size());

                // 区分"加载最新消息"和"加载历史消息"的列表更新方式
                if (scrollToBottom) {
                    // 加载最新消息：直接替换列表（覆盖现有数据，显示最新内容）
                    mChatMessages.clear();
                    mChatMessages.addAll(filteredChats);
                    Log.d(TAG, "加载最新消息，mChatMessages更新完成，总大小: " + mChatMessages.size());
                } else {
                    // 加载历史消息：将新消息插入到现有列表的顶部（保留旧消息）
                    // 先记录插入前的大小，用于UI刷新范围计算
                    int previousSize = mChatMessages.size();
                    for (int i = filteredChats.size() - 1; i >= 0; i--) {
                        mChatMessages.add(0, filteredChats.get(i));
                    }
                    Log.d(TAG, "加载历史消息，插入" + filteredChats.size() + "条到顶部，mChatMessages总大小: " + mChatMessages.size());
                }

                if (isSearch) {
                    // 搜索模式处理
                    Log.d(TAG, "处于搜索模式，开始定位搜索位置");
                    isSearch = false;
                    int position = 0;
                    for (int i = 0; i < mChatMessages.size(); i++) {
                        if (mChatMessages.get(i).getDoubleTimeSend() == mSearchTime) {
                            position = i;
                            Log.d(TAG, "找到搜索时间对应的位置: " + position);
                            break;
                        }
                    }
                    Log.d(TAG, "通知UI刷新（搜索定位），目标位置: " + position);
                    mChatContentView.notifyDataSetInvalidated(position);
                } else {
                    if (mFriend.getEncryptType() == 3) {
                        Log.d(TAG, "加密类型为3，发送安全聊天就绪提示");
                        sendSecureChatReadyTip();
                    }
                    // 根据滚动需求更新列表
                    if (scrollToBottom) {
                        Log.d(TAG, "scrollToBottom为true，通知UI刷新（带滚动到底部）");
                        mChatContentView.notifyDataSetInvalidatedForSetSelectionInvalid(scrollToBottom);
                    } else {
                        Log.d(TAG, "scrollToBottom为false，通知UI顶部添加了" + filteredChats.size() + "条消息");
                        mChatContentView.notifyDataSetAddedItemsToTop(filteredChats.size());
                    }
                }
                Log.d(TAG, "通知UI头部刷新完成");
                mChatContentView.headerRefreshingCompleted();
                if (!mHasMoreData) {
                    Log.d(TAG, "没有更多数据，设置UI不需要刷新");
                    mChatContentView.setNeedRefresh(false);
                }
            }
            Log.d(TAG, "UI更新流程结束");
        });
    }

    protected void onSaveContent() {
        if (isUserSaveContentMethod) {
            if (mChatBottomView == null) {
                return;
            }
            String str = mChatBottomView.getmChatEdit().getText().toString().trim();
            // 清除 回车与空格
            str = str.replaceAll("\\s", "");
            str = str.replaceAll("\\n", "");
            if (TextUtils.isEmpty(str)) {
                if (XfileUtils.isNotEmpty(mChatMessages)) {
                    // todo No.2 不知道为什么oppo手机在以下情况下多选状态下侧滑返回还是报这个错误，length=0; index=-1，先干脆另外创建一个list来处理一下逻辑。
                    List<ChatMessage> chatMessages = new ArrayList<>(mChatMessages);
                    for (int i = 0; i < chatMessages.size(); i++) {
                        if (TextUtils.equals(chatMessages.get(i).getPacketId(), AppConfig.apiKey + "tip")) {
                            // 此提示消息不参与其他业务逻辑，仅首次进入提示
                            chatMessages.remove(i);
                            // 移除时必须刷新适配器，之前未刷新时小米手机会崩溃，而且是在消息页面崩溃的，且崩溃日志如下，
                            // java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                            //	at java.util.ArrayList.get(ArrayList.java:437)
                            //	at android.widget.HeaderViewListAdapter.isEnabled(HeaderViewListAdapter.java:164)
                            //	at android.widget.ListView.dispatchDraw(ListView.java:3575)
                            //	at android.view.View.draw(View.java:20237)
                            // 非常的莫名其妙，还一直以为是MessageFragment的问题，最后排查是这里的问题
                            // todo No.1注释原因见todo No.2
                            // if (mChatContentView != null) {
                            //   mChatContentView.notifyDataSetChanged();
                            // }
                            break;
                        }
                    }
                    if (!XfileUtils.isNotEmpty(chatMessages)) {
                        return;
                    }
                    ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);
                    if (chatMessage.getIsReadDel()
                            && (chatMessage.getType() == XmppMessage.TYPE_TEXT
                            || chatMessage.getType() == XmppMessage.TYPE_REPLAY)) {
                        FriendDao.getInstance().updateFriendContent(
                                mLoginUserId,
                                mFriend.getUserId(),
                                getString(R.string.tip_click_to_read),
                                chatMessage.getType(),
                                chatMessage.getTimeSend());
                    } else {
                        // 调用到该方法时，如果msg的isDecrypted状态为未解密[基本为搜索聊天记录跳转]，先解密在更新Friend content
                        String content = chatMessage.getContent();
                        if (!TextUtils.isEmpty(content) && !chatMessage.isDecrypted()) {
                            String key = SecureChatUtil.getSymmetricKey(chatMessage.getPacketId());
                            try {
                                content = AES.decryptStringFromBase64(chatMessage.getContent(), Base64.decode(key));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        FriendDao.getInstance().updateFriendContent(
                                mLoginUserId,
                                mFriend.getUserId(),
                                content,
                                chatMessage.getType(),
                                chatMessage.getTimeSend());
                    }
                }
            } else {// [草稿]
                FriendDao.getInstance().updateFriendContent(
                        mLoginUserId,
                        mFriend.getUserId(),
                        "&8824" + str,
                        XmppMessage.TYPE_TEXT, TimeUtils.sk_time_current_time());
            }
            PreferenceUtils.putString(mContext, "WAIT_SEND" + mFriend.getUserId() + mLoginUserId, str);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 忽略双指操作，避免引起莫名的问题，
        if (ev.getActionIndex() > 0) {
            return true;
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ignore) {
            // 可能触发ViewPager的bug, 找不到手指头，
            // https://stackoverflow.com/a/31306753
            return true;
        }
    }

    private void doBack() {
        if (!TextUtils.isEmpty(instantMessage)) {
            SelectionFrame selectionFrame = new SelectionFrame(this);
            selectionFrame.setSomething(null, getString(R.string.tip_forwarding_quit), new SelectionFrame.OnSelectionFrameClickListener() {
                @Override
                public void cancelClick() {

                }

                @Override
                public void confirmClick() {
                    finish();
                }
            });
            selectionFrame.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!JVCideoPlayerStandardforchat.handlerBack()) {
            doBack();
        }
    }

    @Override
    protected boolean onHomeAsUp() {
        doBack();
        return true;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mBlackList = FriendDao.getInstance().getAllBlacklists(mLoginUserId);
        instantChatMessage();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 听筒/扬声器模式切换
        boolean isSpeaker = PreferenceUtils.getBoolean(mContext,
                Constants.SPEAKER_AUTO_SWITCH + mLoginUserId, true);
        findViewById(R.id.iv_title_center).setVisibility(isSpeaker ? View.GONE : View.VISIBLE);
        mAudioModeManger.setSpeakerPhoneOn(isSpeaker);

        // 获取[草稿]
        String draft = PreferenceUtils.getString(mContext, "WAIT_SEND" + mFriend.getUserId() + mLoginUserId, "");
        if (!TextUtils.isEmpty(draft)) {
            String s = StringUtils.replaceSpecialChar(draft);
            CharSequence content = HtmlUtils.transform200SpanString(s, true);
            mChatBottomView.getmChatEdit().setText(content);
            softKeyboardControl(true);
        }
        // 获取阅后即焚状态(因为用户可能到聊天设置界面 开启/关闭 阅后即焚，所以在onResume时需要重新获取下状态)
        isReadDel = PreferenceUtils.getInt(mContext, Constants.MESSAGE_READ_FIRE + mFriend.getUserId() + mLoginUserId, 0);
        // 记录当前聊天对象的id
        MyApplication.IsRingId = mFriend.getUserId();
        FormStatuManager.getInstance().startPeriodicRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VoicePlayer.instance().stop();

        // 恢复扬声器模式
        mAudioModeManger.setSpeakerPhoneOn(true);

        if (TextUtils.isEmpty(mChatBottomView.getmChatEdit().getText().toString())) {// 清空草稿，以防消息发送出去后，通过onPause--onResume的方式给输入框赋值
            PreferenceUtils.putString(mContext, "WAIT_SEND" + mFriend.getUserId() + mLoginUserId, "");
        }
        // 将当前聊天对象id重置
        MyApplication.IsRingId = "Empty";
        FormStatuManager.getInstance().stopPeriodicRefresh();
    }

    @Override
    public void finish() {
        onSaveContent();
        MsgBroadcast.broadcastMsgUiUpdate(mContext);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioModeManger != null) {
            mAudioModeManger.unregister();
        }
        if (mRocketAnimationManager != null) {
            mRocketAnimationManager.stopAnimation();
        }
        JCVideoPlayer.releaseAllVideos();
        if (mChatBottomView != null) {
            mChatBottomView.recordCancel();
        }
        ListenerManager.getInstance().removeChatMessageListener(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            // 不能在这崩溃，无法判断是否已经注册这个广播，
        }
    }

    /***************************************
     * ChatContentView的回调
     ***************************************/
    @Override
    public void onMyAvatarClick() {
        // 自己的头像点击
        mChatBottomView.reset();
        mChatBottomView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(mContext, BasicInfoActivity.class);
////                Intent intent = new Intent(mContext, PersonDetailActivity.class);
//                intent.putExtra(AppConstant.EXTRA_USER_ID, mLoginUserId);
//                startActivity(intent);


                PersonDetailActivity.actionStart(ChatActivity.this, mLoginUserId);
            }
        }, 100);
    }

    @Override
    public void onFriendAvatarClick(final String friendUserId) {
        // 朋友的头像点击
        mChatBottomView.reset();
        mChatBottomView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(mContext, BasicInfoActivity.class);
////                Intent intent = new Intent(mContext, PersonDetailActivity.class);
//                intent.putExtra(AppConstant.EXTRA_USER_ID, friendUserId);
//                startActivity(intent);
                PersonDetailActivity.actionStart(ChatActivity.this, friendUserId);
            }
        }, 100);
    }

    @Override
    public void LongAvatarClick(ChatMessage chatMessage) {
    }

    @Override
    public void onNickNameClick(String friendUserId) {
    }

    @Override
    public void onMessageClick(ChatMessage chatMessage) {
    }

    @Override
    public void onMessageLongClick(ChatMessage chatMessage) {
    }

    @Override
    public void onEmptyTouch() {
        mChatBottomView.reset();
    }

    @Override
    public void onTipMessageClick(ChatMessage message) {
        if (message.getFileSize() == XmppMessage.TYPE_83) {
            showRedReceivedDetail(message.getFilePath());
        }
    }

    // 查看红包领取详情
    private void showRedReceivedDetail(String redId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(mContext).accessToken);
        params.put("id", redId);

        HttpUtils.get().url(CoreManager.requireConfig(mContext).RENDPACKET_GET)
                .params(params)
                .build()
                .execute(new BaseCallback<OpenRedpacket>(OpenRedpacket.class) {

                    @Override
                    public void onResponse(ObjectResult<OpenRedpacket> result) {
                        if (result.getData() != null) {
                            // 当resultCode==1时，表示可领取
                            // 当resultCode==0时，表示红包已过期、红包已退回、红包已领完
                            OpenRedpacket openRedpacket = result.getData();
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(mContext, RedDetailsActivity.class);
                            bundle.putSerializable("openRedpacket", openRedpacket);
                            bundle.putInt("redAction", 0);
                            if (!TextUtils.isEmpty(result.getResultMsg())) //resultMsg不为空表示红包已过期
                            {
                                bundle.putInt("timeOut", 1);
                            } else {
                                bundle.putInt("timeOut", 0);
                            }

                            bundle.putBoolean("isGroup", false);
                            bundle.putString("mToUserId", mFriend.getUserId());
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }

    @Override
    public void onReplayClick(ChatMessage message) {
        ChatMessage replayMessage = new ChatMessage(message.getObjectId());
        AsyncUtils.doAsync(this, t -> {
            Reporter.post("查询被回复的消息出错<" + message.getObjectId() + ">", t);
        }, c -> {
            List<ChatMessage> chatMessages = ChatMessageDao.getInstance().searchFromMessage(c.getRef(), mLoginUserId, mFriend.getUserId(), replayMessage);

            if (chatMessages == null) {
                // 没查到消息，
                Log.e("Replay", "本地没有查到被回复的消息<" + message.getObjectId() + ">");
                return;
            }
            int index = -1;
            for (int i = 0; i < chatMessages.size(); i++) {
                ChatMessage m = chatMessages.get(i);
                if (TextUtils.equals(m.getPacketId(), replayMessage.getPacketId())) {
                    index = i;
                }
            }
            if (index == -1) {
                Reporter.unreachable();
                return;
            }
            int finalIndex = index;
            c.uiThread(r -> {
                mChatMessages = chatMessages;
                mChatContentView.setData(mChatMessages);
                mChatContentView.notifyDataSetInvalidated(finalIndex);
            });
        });
    }

    /**
     * 点击感叹号重新发送
     */
    @Override
    public void onSendAgain(ChatMessage message) {
        if (message.getType() == XmppMessage.TYPE_VOICE || message.getType() == XmppMessage.TYPE_IMAGE
                || message.getType() == XmppMessage.TYPE_VIDEO || message.getType() == XmppMessage.TYPE_FILE
                || message.getType() == XmppMessage.TYPE_LOCATION) {
            if (!message.isUpload()) {
                // 将需要上传的消息状态置为发送中，防止在上传的时候退出当前界面，回来后[还未上传成功]读取数据库又变为了感叹号
                ChatMessageDao.getInstance().updateMessageSendState(mLoginUserId, mFriend.getUserId(),
                        message.get_id(), ChatMessageListener.MESSAGE_SEND_ING);
                UploadEngine.uploadImFile(IMUserInfoUtil.getInstance().getToken(), spUser.getString("userid", ""), mFriend.getUserId(), message, mUploadResponse);
            } else {
                if (isAuthenticated()) {
                    return;
                }
                sendMsg(message);
            }
        } else {
            if (isAuthenticated()) {
                return;
            }
            sendMsg(message);
        }
    }

    public void deleteMessage(String msgIdListStr) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("messageId", msgIdListStr);
        params.put("delete", "1");  // 1单方删除 2-双方删除
        params.put("type", "1");    // 1单聊记录 2-群聊记录

        HttpUtils.get().url(coreManager.getConfig().USER_DEL_CHATMESSAGE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }

    /**
     * 消息撤回
     */
    @Override
    public void onMessageBack(final ChatMessage chatMessage, final int position) {
        DialogHelper.showMessageProgressDialog(this, getString(R.string.message_revocation));
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("messageId", chatMessage.getPacketId());
        params.put("delete", "2");  // 1单方删除 2-双方删除
        params.put("type", "1");    // 1单聊记录 2-群聊记录

        HttpUtils.get().url(coreManager.getConfig().USER_DEL_CHATMESSAGE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (chatMessage.getType() == XmppMessage.TYPE_VOICE) {// 撤回的为语音消息，停止播放
                            if (VoicePlayer.instance().getVoiceMsgId().equals(chatMessage.getPacketId())) {
                                VoicePlayer.instance().stop();
                            }
                        } else if (chatMessage.getType() == XmppMessage.TYPE_VIDEO) {
                            JCVideoPlayer.releaseAllVideos();
                        }
                        // 发送撤回消息
                        ChatMessage message = new ChatMessage();
                        message.setType(XmppMessage.TYPE_BACK);
                        message.setFromUserId(mLoginUserId);
                        message.setFromUserName(coreManager.getSelf().getNickName());
                        message.setToUserId(mFriend.getUserId());
                        message.setContent(chatMessage.getPacketId());
                        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                        sendMsg(message);

                        ChatMessage chat = mChatMessages.get(position);
                        ChatMessageDao.getInstance().updateMessageBack(mLoginUserId, mFriend.getUserId(), chat.getPacketId(), getString(R.string.you));
                        chat.setType(XmppMessage.TYPE_TIP);
                        chat.setContent(getString(R.string.already_with_draw));
                        mChatContentView.notifyDataSetInvalidated(true);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    @Override
    public void onMessageReplay(ChatMessage chatMessage) {
        replayMessage = chatMessage.cloneAll();
        mChatBottomView.setReplay(chatMessage);
    }

    @Override
    public void cancelReplay() {
        replayMessage = null;
    }

    @Override
    public void onCallListener(int type) {
        if (coreManager.isLogin()) {
            if (type == XmppMessage.TYPE_NO_CONNECT_VOICE || type == XmppMessage.TYPE_END_CONNECT_VOICE) {
                Log.e("zq", "dialAudioCall");
                dial(CallConstants.Audio);
            } else if (type == XmppMessage.TYPE_NO_CONNECT_VIDEO || type == XmppMessage.TYPE_END_CONNECT_VIDEO) {
                Log.e("zq", "dialVideoCall");
                dial(CallConstants.Video);
            } else if (type == XmppMessage.TYPE_NO_CONNECT_SCREEN || type == XmppMessage.TYPE_END_CONNECT_SCREEN) {
                Log.e("zq", "dialScreenCall");
                dial(CallConstants.Screen);
            }
        } else {
            coreManager.autoReconnectShowProgress(this);
        }
    }

    private void dial(final int type) {
        if (MyApplication.IS_OPEN_CLUSTER) {// 集群，调接口获取 meetUrl
            Map<String, String> params = new HashMap<>();
            params.put("access_token", IMUserInfoUtil.getInstance().getToken());
            String area = PreferenceUtils.getString(this, AppConstant.EXTRA_CLUSTER_AREA);
            if (!TextUtils.isEmpty(area)) {
                params.put("area", area);
            }
            params.put("toUserId", mFriend.getUserId());

            HttpUtils.get().url(coreManager.getConfig().OPEN_MEET)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<String>(String.class) {
                        @Override
                        public void onResponse(ObjectResult<String> result) {
                            if (!TextUtils.isEmpty(result.getData())) {
                                JSONObject jsonObject = JSONObject.parseObject(result.getData());
                                realDial(type, jsonObject.getString("meetUrl"));
                            } else {
                                realDial(type, null);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {// 获取网络配置失败，使用默认配置
                            realDial(type, null);
                        }
                    });
        } else {
            realDial(type, null);
        }
    }

    private void realDial(int type, String meetUrl) {
        ChatMessage message = new ChatMessage();
        if (type == CallConstants.Audio) {// 语音通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VOICE);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.voice_call));
        } else if (type == CallConstants.Video) {// 视频通话
            message.setType(XmppMessage.TYPE_IS_CONNECT_VIDEO);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.video_call));
        } else if (type == CallConstants.Screen) {// 屏幕共享
            message.setType(XmppMessage.TYPE_IS_CONNECT_SCREEN);
            message.setContent(getString(R.string.sip_invite) + " " + getString(R.string.screen_call));
        }
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setToUserId(mFriend.getUserId());
        if (!TextUtils.isEmpty(meetUrl)) {
            message.setFilePath(meetUrl);
        }
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        message.setRoomId(UUID.randomUUID().toString());
        sendMsg(message);
        Intent intent = new Intent(this, Jitsi_pre.class);
        intent.putExtra("type", type);
        intent.putExtra("fromuserid", mLoginUserId);
        intent.putExtra("touserid", mFriend.getUserId());
        intent.putExtra("username", mFriend.getNickName());

        intent.putExtra("roomId", message.getRoomId());
        if (!TextUtils.isEmpty(meetUrl)) {
            intent.putExtra("meetUrl", meetUrl);
        }
        startActivity(intent);
    }

    /***************************************
     * ChatBottomView的回调
     ***************************************/

    private void softKeyboardControl(boolean isShow) {
        // 软键盘消失
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm == null) return;
        if (isShow) {
            mChatBottomView.postDelayed(new Runnable() {
                @Override
                public void run() {// 延迟200ms在弹起，否则容易出现页面未完全加载完成软键盘弹起的效果
                    mChatBottomView.getmChatEdit().requestFocus();
                    mChatBottomView.getmChatEdit().setSelection(mChatBottomView.getmChatEdit().getText().toString().length());
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }, 200);
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 发送一条包装好的消息
     *
     * 功能说明：
     * 1. 检查黑名单：如果对方在黑名单中，则不允许发送消息
     * 2. 设置消息属性：设置发送者、接收者、设备标识等信息
     * 3. 处理设备消息：如果是设备消息，需要特殊处理userId
     * 4. 设置消息过期时间：根据聊天记录保存天数设置删除时间
     * 5. 保存到数据库：将消息保存到本地数据库
     * 6. 文件上传处理：如果是文件类型消息，先上传再发送
     * 7. 直接发送：文本等消息直接通过XMPP发送
     *
     * @param message 要发送的消息对象
     */
    private void sendMessage(final ChatMessage message) {

        if (interprect()) {// 该用户在你的黑名单列表内
            ToastUtil.showToast(this, getString(R.string.tip_remote_in_black));
            // 移除掉该条消息
            mChatMessages.remove(message);
            mChatContentView.notifyDataSetInvalidated(true);
            return;
        }

        message.setFromUserId(mLoginUserId);
        PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(this);
        boolean isSupport = privacySetting.getMultipleDevices() == 1;
        if (isSupport) {
            message.setFromId("android");
        } else {
            message.setFromId("youjob");
        }
        if (mFriend.getIsDevice() == 1) {
            message.setToUserId(userId);
            message.setToId(mFriend.getUserId());
        } else {
            message.setToUserId(mFriend.getUserId());

            // sz 消息过期时间
            if (mFriend.getChatRecordTimeOut() == -1 || mFriend.getChatRecordTimeOut() == 0) {// 永久
                message.setDeleteTime(-1);
            } else {
                long deleteTime = TimeUtils.sk_time_current_time() + (long) (mFriend.getChatRecordTimeOut() * 24 * 60 * 60);
                message.setDeleteTime(deleteTime);
            }
        }
        message.setDecrypted(true);
        message.setReSendCount(ChatMessageDao.fillReCount(message.getType()));
        message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());



        Log.e("zq", "-message,getContent-------" +message.getContent());
        Log.e("zq", "-message,getContent-------" +isRocketMessageEnabled(mContext, mFriend.getUserId(), mLoginUserId));

//        // 检查火箭消息状态，如果开启则设置火箭消息标记并播放动画
        if (isRocketMessageEnabled(mContext, mFriend.getUserId(), mLoginUserId)) {
            message.setRocketMessage(); // 设置火箭消息标记
            playRocketAnimation();
        }

        if (isChatPassword) {
            message.setSignature(chatPassWord); // 设置火箭消息标记
            message.setIsEncrypt(1);
            isChatPassword =false;
            chatPassWord = "";
            mChatBottomView.switchToSecretChatMode(false);
        }
        // 将消息保存在数据库了
        ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mFriend.getUserId(), message);

        if (message.getType() == XmppMessage.TYPE_VOICE || message.getType() == XmppMessage.TYPE_IMAGE
                || message.getType() == XmppMessage.TYPE_VIDEO || message.getType() == XmppMessage.TYPE_FILE
                || message.getType() == XmppMessage.TYPE_LOCATION) {// 语音、图片、视频、文件需要上传在发送
            // 位置消息也要上传截图，
            if (!message.isUpload()) {// 未上传
                UploadEngine.uploadImFile(IMUserInfoUtil.getInstance().getToken(), coreManager.getSelf().getUserId(), mFriend.getUserId(), message, mUploadResponse);
            } else {// 已上传 自定义表情默认为已上传
                sendMsg(message);
            }
        } else {// 其他类型直接发送
            sendMsg(message);
        }
    }

    /**
     * 通过XMPP发送消息到服务器
     * 功能说明：
     * 1. 检查连接状态：确保XMPP连接正常，如果离线则自动重连
     * 2. 设备消息处理：如果是设备消息，使用特殊的userId发送
     * 3. 普通消息发送：通过CoreManager发送到指定用户
     *
     * @param message 要发送的消息对象
     */
    private void sendMsg(ChatMessage message) {
        // 一些异步回调进来的也要判断xmpp是否在线，
        // 比如图片上传成功后，
        if (isAuthenticated()) {
            return;
        }
        if (mFriend.getIsDevice() == 1) {
            coreManager.sendChatMessage(userId, message);
        } else {
            coreManager.sendChatMessage(mFriend.getUserId(), message);
        }
    }

    /**
     * 停止播放聊天的录音
     */
    @Override
    public void stopVoicePlay() {
        VoicePlayer.instance().stop();
    }

    @Override
    public void sendAt() {
    }

    @Override
    public void sendAtMessage(String text) {
        sendText(text);// 单聊内包含@符号的消息也需要发出去
    }

    /**
     * 发送文本消息
     *
     * 功能说明：
     * 1. 参数验证：检查文本内容是否为空
     * 2. 连接检查：确保XMPP连接正常
     * 3. 创建消息：创建文本类型或回复类型的消息
     * 4. 回复处理：如果是回复消息，设置被回复消息的JSON数据
     * 5. 阅后即焚：根据设置决定是否启用阅后即焚
     * 6. 界面更新：将消息添加到列表并刷新界面
     * 7. 发送消息：调用sendMessage进行实际发送
     * 8. 红包检测：检查发送的文本是否为红包口令，如果是则自动领取
     *
     * @param text 要发送的文本内容
     */
    @Override
    public void sendText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }

        ChatMessage message = new ChatMessage();
        // 文本类型
        message.setType(XmppMessage.TYPE_TEXT);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);

        // 检查是否开启简单密聊
        boolean isSimpleEncryptEnabled = PreferenceUtils.getBoolean(mContext, Constants.generateSimpleEncryptKey(mFriend.getUserId(), mLoginUserId), false);

        // 检查是否为表情消息，表情消息不加密
        boolean isEmojiMessage = isEmojiOnlyMessage(text);

        if (isSimpleEncryptEnabled && !isEmojiMessage && false) {
            // 使用简单加密，但表情消息不加密
            String encryptedText = SimpleEncryptUtil.encrypt(text, mLoginUserId);
            message.setContent(encryptedText);

            // 测试密聊功能
            SimpleEncryptUtil.testEncrypt();
            SimpleEncryptUtil.testCardEncrypt();
        } else {
            message.setContent(text);
            if (isEmojiMessage) {
                Log.e("SimpleEncrypt", "发送表情消息(不加密): " + text);
            } else {
                Log.e("SimpleEncrypt", "发送普通消息: " + text);
            }
        }
        if (replayMessage != null) {
            message.setType(XmppMessage.TYPE_REPLAY);
            message.setObjectId(replayMessage.toJsonString());
            replayMessage = null;
            mChatBottomView.resetReplay();
        }
        message.setIsReadDel(isReadDel);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);

        sendMessage(message);
        // 遍历消息集合，查询红包类型消息
        for (ChatMessage msg : mChatMessages) {
            if (msg.getType() == XmppMessage.TYPE_RED// 红包
                    && StringUtils.strEquals(msg.getFilePath(), "3")// 口令红包
                    && text.equalsIgnoreCase(msg.getContent())// 发送的文本与口令一致
                    && msg.getFileSize() == 1// 可以领取的状态
                    && !msg.isMySend()) {
                // todo 红包领取状态为本地记录，当对方领取之后清空本地聊天记录时在漫游获取到该消息时，在发送口令，不能让mRedDialog弹出
                // todo 调接口获取红包领取状态
                clickRedPacket(msg);
                // 可能存在多个口令一致的未领取的口令红包，匹配到一个就直接跳出循环
                break;
            }
        }
    }

    /**
     * 点击红包
     */
    public void clickRedPacket(ChatMessage msg) {
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("id", msg.getObjectId());

        HttpUtils.get().url(CoreManager.requireConfig(mContext).RENDPACKET_GET)
                .params(params)
                .build()
                .execute(new BaseCallback<OpenRedpacket>(OpenRedpacket.class) {

                    @Override
                    public void onResponse(ObjectResult<OpenRedpacket> result) {
                        if (result.getResultCode() == 1) {
                            RedDialogBean redDialogBean = new RedDialogBean(msg.getFromUserId(), msg.getFromUserName(),
                                    msg.getContent(), null);
                            mRedDialog = new RedDialog(mContext, redDialogBean, new RedDialog.OnClickRedListener() {
                                @Override
                                public void clickRed() {
                                    // 打开红包
                                    openRedPacket(msg, result.getData().getPacket().getYopRedPacketId());
                                }

                                @Override
                                public void clickTail() {

                                }
                            });
                            mRedDialog.show();
                        } else {
                            // 红包不可领，统统当做已领取处理
                            msg.setFileSize(2);
                            ChatMessageDao.getInstance().updateChatMessageReceiptStatus(mLoginUserId, mFriend.getUserId(), msg.getPacketId());
                            mChatContentView.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }

    /**
     * 打开红包
     */
    public void openRedPacket(final ChatMessage message, String yeepayId) {
        HashMap<String, String> params = new HashMap<String, String>();
        String redId = message.getObjectId();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("id", redId);

        String url;
        if (TextUtils.isEmpty(yeepayId)) {
            url = CoreManager.requireConfig(mContext).REDPACKET_OPEN;
        } else {
            if (!YeepayHelper.checkOpenedOrAsk(mContext)) {
                return;
            }
            url = CoreManager.requireConfig(mContext).YOP_ACCEPT_RED;
        }
        HttpUtils.get().url(url)
                .params(params)
                .build()
                .execute(new BaseCallback<OpenRedpacket>(OpenRedpacket.class) {

                    @Override
                    public void onResponse(ObjectResult<OpenRedpacket> result) {
                        if (mRedDialog != null) {
                            mRedDialog.dismiss();
                        }
                        if (result.getData() != null) {
                            // 标记已经领取过了一次,不可再领取
                            message.setFileSize(2);
                            ChatMessageDao.getInstance().updateChatMessageReceiptStatus(mLoginUserId, mFriend.getUserId(), message.getPacketId());
                            mChatContentView.notifyDataSetChanged();

                            OpenRedpacket openRedpacket = result.getData();
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(mContext, RedDetailsActivity.class);
                            bundle.putSerializable("openRedpacket", openRedpacket);
                            bundle.putInt("redAction", 1);
                            bundle.putInt("timeOut", 0);

                            bundle.putBoolean("isGroup", false);
                            bundle.putString("mToUserId", mFriend.getUserId());
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                            // 更新余额
                            coreManager.updateMyBalance();

                            showReceiverRedLocal(openRedpacket);
                        } else {
                            Toast.makeText(ChatActivity.this, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        if (mRedDialog != null) {
                            mRedDialog.dismiss();
                        }
                    }
                });
    }

    @SuppressLint("StringFormatInvalid")
    private void showReceiverRedLocal(OpenRedpacket openRedpacket) {
        // 本地显示一条领取通知
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFileSize(XmppMessage.TYPE_83);
        chatMessage.setFilePath(openRedpacket.getPacket().getId());
        chatMessage.setFromUserId(mLoginUserId);
        chatMessage.setFromUserName(mLoginNickName);
        chatMessage.setToUserId(mFriend.getUserId());
        chatMessage.setType(XmppMessage.TYPE_TIP);
        chatMessage.setContent(getString(R.string.red_received_self, openRedpacket.getPacket().getUserName()));
        chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        chatMessage.setTimeSend(TimeUtils.sk_time_current_time());
        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mFriend.getUserId(), chatMessage)) {
            mChatMessages.add(chatMessage);
            mChatContentView.notifyDataSetInvalidated(true);
        }
    }

    @Override
    public void sendGif(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_GIF);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent(text);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    @Override
    public void sendCollection(String collection) {
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_IMAGE);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent(collection);
        message.setIsReadDel(isReadDel);
        message.setUpload(true);// 自定义表情，不需要上传
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    /**
     * 发送语音消息
     * 功能说明：
     * 1. 文件验证：检查语音文件是否存在
     * 2. 连接检查：确保XMPP连接正常
     * 3. 文件信息：获取文件大小和时长
     * 4. 消息创建：创建语音类型消息并设置相关属性
     * 5. 阅后即焚：根据设置决定是否启用阅后即焚
     * 6. 界面更新：将消息添加到列表并刷新界面
     * 7. 发送处理：调用sendMessage进行发送（需要先上传文件）
     *
     * @param filePath 语音文件路径
     * @param timeLen 语音时长（秒）
     * @param stringAudio 语音识别结果列表
     */
    @Override
    public void sendVoice(String filePath, int timeLen, ArrayList<String> stringAudio) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }
        File file = new File(filePath);
        long fileSize = file.length();
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_VOICE);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent("");
        message.setFilePath(filePath);
        message.setFileSize((int) fileSize);
        message.setTimeLen(timeLen);
        message.setObjectId(TextUtils.join(",", stringAudio));
        message.setIsReadDel(isReadDel);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     * 功能说明：
     * 1. 文件验证：检查图片文件是否存在
     * 2. 连接检查：确保XMPP连接正常
     * 3. 文件信息：获取文件大小和图片尺寸
     * 4. 消息创建：创建图片类型消息并设置相关属性
     * 5. 图片参数：获取图片的宽高信息
     * 6. 阅后即焚：根据设置决定是否启用阅后即焚
     * 7. 界面更新：将消息添加到列表并刷新界面
     * 8. 发送处理：调用sendMessage进行发送（需要先上传文件）
     *
     * @param file 图片文件对象
     */
    public void sendImage(File file) {
        if (!file.exists()) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }
        long fileSize = file.length();
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_IMAGE);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);

        // 检查是否开启简单密聊
        boolean isSimpleEncryptEnabled = PreferenceUtils.getBoolean(mContext, Constants.generateSimpleEncryptKey(mFriend.getUserId(), mLoginUserId), false);

        if (isSimpleEncryptEnabled && false) {
            // 密聊开启时，加密图片URL（这里暂时使用文件路径作为标识）
            String imageInfo = file.getAbsolutePath();
            String encryptedImageInfo = SimpleEncryptUtil.encrypt(imageInfo, mLoginUserId);
            message.setContent(encryptedImageInfo);
            Log.e("SimpleEncrypt", "发送加密图片: " + imageInfo + " -> " + encryptedImageInfo);
        } else {
            // 密聊未开启，直接设置空内容
            message.setContent("");
            Log.e("SimpleEncrypt", "发送普通图片");
        }

        String filePath = file.getAbsolutePath();
        message.setFilePath(filePath);
        message.setFileSize((int) fileSize);
        int[] imageParam = BitmapUtil.getImageParamByIntsFile(filePath);
        message.setLocation_x(String.valueOf(imageParam[0]));
        message.setLocation_y(String.valueOf(imageParam[1]));
        message.setIsReadDel(isReadDel);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    /**
     * 发送视频消息
     * 功能说明：
     * 1. 文件验证：检查视频文件是否存在
     * 2. 连接检查：确保XMPP连接正常
     * 3. 文件信息：获取文件大小
     * 4. 消息创建：创建视频类型消息并设置相关属性
     * 5. 阅后即焚：根据设置决定是否启用阅后即焚
     * 6. 界面更新：将消息添加到列表并刷新界面
     * 7. 发送处理：调用sendMessage进行发送（需要先上传文件）
     *
     * @param file 视频文件对象
     */
    public void sendVideo(File file) {
        if (!file.exists()) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_VIDEO);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent("");
        String filePath = file.getAbsolutePath();
        message.setFilePath(filePath);
        long fileSize = file.length();
        message.setFileSize((int) fileSize);
        message.setIsReadDel(isReadDel);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    /**
     * 发送文件消息
     * 功能说明：
     * 1. 文件验证：检查文件是否存在
     * 2. 连接检查：确保XMPP连接正常
     * 3. 文件类型判断：自动识别图片和视频文件，调用对应的发送方法
     * 4. 文件信息：获取文件大小
     * 5. 消息创建：创建文件类型消息并设置相关属性
     * 6. 界面更新：将消息添加到列表并刷新界面
     * 7. 发送处理：调用sendMessage进行发送（需要先上传文件）
     *
     * @param file 文件对象
     */
    public void sendFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (isAuthenticated()) {
            return;
        }
        long fileSize = file.length();
        String filePath = file.getAbsolutePath();
        if (FileUtil.isImageFile(filePath)) {
            sendImage(file);
            return;
        } else if (FileUtil.isVideoFile(filePath)) {
            sendVideo(file);
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_FILE);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);

        // 检查是否开启简单密聊
        boolean isSimpleEncryptEnabled = PreferenceUtils.getBoolean(mContext, Constants.generateSimpleEncryptKey(mFriend.getUserId(), mLoginUserId), false);

        if (isSimpleEncryptEnabled && false) {
            // 密聊开启时，加密文件路径
            String encryptedFilePath = SimpleEncryptUtil.encrypt(filePath, mLoginUserId);
            message.setContent(encryptedFilePath);
            Log.e("SimpleEncrypt", "发送加密文件: " + filePath + " -> " + encryptedFilePath);
        } else {
            // 密聊未开启，直接设置空内容
            message.setContent("");
            Log.e("SimpleEncrypt", "发送普通文件");
        }

        message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        message.setFilePath(filePath);
        message.setFileSize((int) fileSize);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    private void sendContacts(List<Contacts> contactsList) {
        for (Contacts contacts : contactsList) {
            sendText(contacts.getName() + '\n' + contacts.getTelephone());
        }
    }

    /**
     * 发送位置消息
     * 功能说明：
     * 1. 连接检查：确保XMPP连接正常
     * 2. 消息创建：创建位置类型消息并设置相关属性
     * 3. 位置信息：设置经纬度和地址信息
     * 4. 截图处理：位置消息包含地图截图，需要先上传
     * 5. 界面更新：将消息添加到列表并刷新界面
     * 6. 发送处理：调用sendMessage进行发送（需要先上传截图）
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @param address 地址描述
     * @param snapshot 位置截图文件路径
     */
    public void sendLocate(double latitude, double longitude, String address, String snapshot) {
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_LOCATION);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        // 上传图片后会给赋值，
        message.setContent("");
        message.setFilePath(snapshot);
        message.setLocation_x(latitude + "");
        message.setLocation_y(longitude + "");
        message.setObjectId(address);
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    @Override
    public void clickPhoto() {
        // 将其置为true
        /*MyApplication.GalleyNotBackGround = true;
        CameraUtil.pickImageSimple(this, REQUEST_CODE_PICK_PHOTO);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        ArrayList<String> imagePaths = new ArrayList<>();
        PhotoPickerIntent intent = new PhotoPickerIntent(ChatActivity.this);
        intent.setSelectModel(SelectModel.MULTI);
        // 已选中的照片地址， 用于回显选中状态
        intent.setSelectedPaths(imagePaths);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
        mChatBottomView.reset();
    }

    @Override
    public void clickCamera() {
       /* mNewPhotoUri = CameraUtil.getOutputMediaFileUri(this, CameraUtil.MEDIA_TYPE_IMAGE);
        CameraUtil.captureImage(this, mNewPhotoUri, REQUEST_CODE_CAPTURE_PHOTO);*/
       /* Intent intent = new Intent(this, EasyCameraActivity.class);
        startActivity(intent);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        mChatBottomView.reset();
        Intent intent = new Intent(this, VideoRecorderActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickStartRecord() {
        // 现拍照录像ui和二为一，统一在clickCamera内处理
       /* Intent intent = new Intent(this, VideoRecorderActivity.class);
        startActivity(intent);*/
    }

    @Override
    public void clickLocalVideo() {
        // 现拍照录像ui和二为一，统一在clickCamera内处理
        /*Intent intent = new Intent(this, LocalVideoActivity.class);
        intent.putExtra(AppConstant.EXTRA_ACTION, AppConstant.ACTION_SELECT);
        intent.putExtra(AppConstant.EXTRA_MULTI_SELECT, true);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);*/
    }

    @Override
    public void clickAudio() {
        if (coreManager.isLogin()) {
            dial(CallConstants.Audio);
        } else {
            coreManager.autoReconnectShowProgress(this);
        }
    }

    @Override
    public void clickVideoChat() {
        if (coreManager.isLogin()) {
            dial(CallConstants.Video);
        } else {
            coreManager.autoReconnectShowProgress(this);
        }
    }

    @Override
    public void clickScreenChat() {
        if (coreManager.isLogin()) {
            dial(CallConstants.Screen);
        } else {
            coreManager.autoReconnectShowProgress(this);
        }
    }

    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void clickFile() {

        int ACTION_REQUEST_PERMISSIONS = 0x001;
        String[] NEEDED_PERMISSIONS = new String[]{

                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        };
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        ImagePickSdk.imagePick(this, new ImagePickConfig(), new ImagePickSdk.ImagePickFinish() {
            @Override
            public void imagePickFinsh(int code, List uris, List<String> paths) {


                for (Object o : paths) {
                    String s = (String) o;
                    // Uri uri = Uri.parse(s);

                    if (new File(s).exists()) {
                        sendFile(new File(s));
                    } else {
                        Uri uri = Uri.parse(s);
                        sendFile(new File(uri.getPath()));
                    }

                }
            }
        });
//        SelectFileDialog dialog = new SelectFileDialog(this, new SelectFileDialog.OptionFileListener() {
//            @Override
//            public void option(List<File> files) {
//                if (files != null && files.size() > 0) {
//                    for (int i = 0; i < files.size(); i++) {
//                        sendFile(files.get(i));
//                    }
//                }
//            }
//
//            @Override
//            public void intent() {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
//            }
//
//        });
//        dialog.show();
    }
    private boolean isChatPassword = false;
    private String chatPassWord = "";
    @Override
    public void clickChatPassword() {
        PswInputDialog pswInputDialog = new PswInputDialog(ChatActivity.this);
        //pswInputDialog.setTitle("请输入管理员密码");
        //showPswDialog()一定要在最前面执行
        pswInputDialog.showPswDialog();

        //隐藏忘记密码的入口
        pswInputDialog.hideForgetPswClickListener();

        //设置忘记密码的点击事件
        pswInputDialog.setOnForgetPswClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, getText(R.string.forget_password), Toast.LENGTH_SHORT).show();
            }
        });

        //设置密码长度
        pswInputDialog.setPswCount(6);
        //设置密码输入完成监听
        pswInputDialog.setListener(new PswInputDialog.OnPopWindowClickListener() {
            @Override
            public void onPopWindowClickListener(String password, boolean complete) {
                if (complete) {
                    if (validatePassword(password)) { // 验证密码
                        mChatBottomView.switchToSecretChatMode(true);
                        ToastUtil.showToast(mContext, "密聊模式已开启");
                        chatPassWord = password;
                        isChatPassword = true;
                    } else {
                        ToastUtil.showToast(mContext, "密码错误，请重新输入");
                    }
                }
//                            Toast.makeText(MainActivity.this, "你输入的密码是：" + psw, Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 密码验证逻辑（可根据实际需求修改）
    private boolean validatePassword(String password) {
        // 示例：简单验证非空，实际项目中应对接后端验证
        return !TextUtils.isEmpty(password);
    }
    @Override
    public void clickContact() {
        SendContactsActivity.start(this, REQUEST_CODE_SEND_CONTACT);
    }

    @Override
    public void clickLocation() {
        Intent intent = new Intent(this, MapPickerActivity.class);
        intent.putExtra(AppConstant.EXTRA_FORM_CAHT_ACTIVITY, true);
        startActivityForResult(intent, REQUEST_CODE_SELECT_Locate);
    }

    @Override
    public void clickCard() {
        SelectCardPopupWindow mSelectCardPopupWindow = new SelectCardPopupWindow(this, this);
        mSelectCardPopupWindow.showAtLocation(findViewById(R.id.root_view),
                Gravity.CENTER, 0, 0);
    }

    @Override
    public void clickRedpacket() {
        Intent intent = new Intent(this, SendRedPacketActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, mFriend.getUserId());
        startActivityForResult(intent, REQUEST_CODE_SEND_RED);
    }

    @Override
    public void clickTransferMoney() {
        Intent intent = new Intent(this, TransferMoneyActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, mFriend.getUserId());
        intent.putExtra(AppConstant.EXTRA_NICK_NAME, TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName());
        startActivity(intent);
    }

    @Override
    public void clickCollection() {
        Intent intent = new Intent(this, MyCollection.class);
        intent.putExtra("IS_SEND_COLLECTION", true);
        startActivityForResult(intent, REQUEST_CODE_SEND_COLLECTION);
    }

    private void clickCollectionSend(
            int type,
            String content,
            int timeLen,
            String filePath,
            long fileSize
    ) {
        if (isAuthenticated()) {
            return;
        }

        if (TextUtils.isEmpty(content)) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(type);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent(content);
        message.setFileSize((int) fileSize);
        message.setTimeLen(timeLen);
        message.setUpload(true);
        if (!TextUtils.isEmpty(filePath)) {
            message.setFilePath(filePath);
        }
        if (type == XmppMessage.TYPE_VOICE
                || type == XmppMessage.TYPE_IMAGE
                || type == XmppMessage.TYPE_VIDEO) {
            message.setIsReadDel(isReadDel);
        }
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    private void clickCollectionSend(CollectionEvery collection) {
        // 不管什么收藏消息类型，都可能有文字，单独发一条文字消息，
        if (!TextUtils.isEmpty(collection.getCollectContent())) {
            sendText(collection.getCollectContent());
        }
        int type = collection.getXmppType();
        if (type == XmppMessage.TYPE_TEXT) {
            // 文字消息发出了文字就可以结束了，
            return;
        } else if (type == XmppMessage.TYPE_IMAGE) {
            // 图片可能有多张，分开发送，
            String allUrl = collection.getUrl();
            for (String url : allUrl.split(",")) {
                clickCollectionSend(type, url, collection.getFileLength(), collection.getFileName(), collection.getFileSize());
            }
            return;
        }
        clickCollectionSend(type, collection.getUrl(), collection.getFileLength(), collection.getFileName(), collection.getFileSize());
    }

    @Override
    public void clickShake() {
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_SHAKE);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);
        message.setContent(getString(R.string.msg_shake));
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
        shake(0);// 戳一戳效果
    }

    /**
     * 检查是否开启火箭消息功能（公共方法）
     * @param context 上下文
     * @param friendId 好友ID
     * @param loginUserId 登录用户ID
     * @return 是否开启火箭消息功能
     */
    public static boolean isRocketMessageEnabled(Context context, String friendId, String loginUserId) {
        return PreferenceUtils.getBoolean(context, Constants.generateRocketMessageKey(friendId, loginUserId), false);
    }

    /**
     * 播放火箭消息动画效果
     */
    private void playRocketAnimation() {
        if (mRocketAnimationManager != null) {
            mRocketAnimationManager.playRocketAnimation();
        }
    }

    /**
     * 播放接收火箭消息动画效果
     */
    private void playRocketReceiveAnimation() {
        if (mRocketAnimationManager != null) {
            mRocketAnimationManager.playRocketReceiveAnimation();
        }
    }


    @Override
    public void clickGroupAssistant(GroupAssistantDetail groupAssistantDetail) {

    }

    /**
     * 得到选中的名片
     */
    @Override
    public void sendCardS(List<Friend> friends) {
        for (int i = 0; i < friends.size(); i++) {
            sendCard(friends.get(i));
        }
    }

    public void sendCard(Friend friend) {
        if (isAuthenticated()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setType(XmppMessage.TYPE_CARD);
        message.setFromUserId(mLoginUserId);
        message.setFromUserName(mLoginNickName);

        // 检查是否开启简单密聊
        boolean isSimpleEncryptEnabled = PreferenceUtils.getBoolean(mContext, Constants.generateSimpleEncryptKey(mFriend.getUserId(), mLoginUserId), false);

        if (isSimpleEncryptEnabled && false) {
            // 密聊开启时，加密完整的名片信息（昵称|用户ID）
            String cardInfo = friend.getNickName() + "|" + friend.getUserId();
            String encryptedCardInfo = SimpleEncryptUtil.encrypt(cardInfo, mLoginUserId);
            message.setContent(encryptedCardInfo);
            Log.e("SimpleEncrypt", "发送加密名片: " + cardInfo + " -> " + encryptedCardInfo);
        } else {
            // 密聊未开启，直接设置名片昵称
            message.setContent(friend.getNickName());
            Log.e("SimpleEncrypt", "发送普通名片: " + friend.getNickName());
        }

        message.setObjectId(friend.getUserId());
        mChatMessages.add(message);
        mChatContentView.notifyDataSetInvalidated(true);
        sendMessage(message);
    }

    @Override
    public void onInputState() {
        // 获得输入状态
        PrivacySetting privacySetting = PrivacySettingHelper.getPrivacySettings(this);
        boolean input = privacySetting.getIsTyping() == 1;
        if (input && coreManager.isLogin()) {
            ChatMessage message = new ChatMessage();
            // 正在输入消息
            message.setType(XmppMessage.TYPE_INPUT);
            message.setFromUserId(mLoginUserId);
            message.setFromUserName(mLoginNickName);
            message.setToUserId(mFriend.getUserId());
            message.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
            message.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            sendMsg(message);
        }
    }

    /**
     * 接收新消息回调
     * 功能说明：
     * 1. 消息过滤：过滤群组消息和验签失败的消息
     * 2. 重复检测：防止收到重复消息（重发机制导致）
     * 3. 设备消息处理：处理设备间的转发消息
     * 4. 多点登录同步：处理自己其他设备发送的消息
     * 5. 消息解密：设置消息为已解密状态
     * 6. 界面更新：将消息添加到聊天列表并刷新界面
     * 7. 振动提示：收到新消息时进行振动提醒
     * 8. 滚动控制：根据是否在底部决定是否自动滚动
     *
     * @param fromUserId 发送者用户ID
     * @param message 接收到的消息对象
     * @param isGroupMsg 是否为群组消息
     * @return true表示消息已处理，false表示忽略该消息
     */

    @Override
    public boolean onNewMessage(String fromUserId, ChatMessage message, boolean isGroupMsg) {

        if (isGroupMsg || message.isVerifySignatureFailed()) {// 群组或单聊验签失败消息不显示
            return false;
        }

        /**
         *  因为重发机制，当对方处于弱网时，不能及时接收我方的消息回执而给我方发送了两条甚至多条一样的消息
         *  而我方则会收到两条甚至多条一样的消息存入数据库(数据库已去重)，如果我正好处于消息发送方的聊天界面
         *  则会回调多次onNewMessage方法，而该方法内又没做去重，所以会出现显示两条一模一样的消息，退出当前界面在进入
         *  该界面又只有一条的问题
         *
         */
        if (mChatMessages.size() > 0) {
            if (mChatMessages.get(mChatMessages.size() - 1).getPacketId().equals(message.getPacketId())) {// 最后一条消息的msgId==新消息的msgId
                Log.e("zq", "收到一条重复消息");
                return false;
            }
        }

        if (mFriend.getIsDevice() == 1) {// 当前界面为我的设备界面 如果收到其他设备的转发消息，也会通知过来
            ChatMessage chatMessage = ChatMessageDao.getInstance().
                    findMsgById(mLoginUserId, mFriend.getUserId(), message.getPacketId());
            if (chatMessage == null) {
                return false;
            }
        }

        message.setDecrypted(true);// 回调过来的消息默认为已解密
        /*
         现在需要支持多点登录，此刻该条消息为我另外一台设备发送的消息，我需要将该条消息存入对应的数据库并更新界面来达到同步
         */
        if (fromUserId.equals(mLoginUserId)
                && !TextUtils.isEmpty(message.getToUserId())
                && message.getToUserId().equals(mFriend.getUserId())) {// 收到自己转发的消息且该条消息为发送给当前聊天界面的
            message.setMySend(true);
            message.setMessageState(MESSAGE_SEND_SUCCESS);
            mChatMessages.add(message);
            if (mChatContentView.shouldScrollToBottom()) {
                mChatContentView.notifyDataSetInvalidated(true);
            } else {
                mChatContentView.notifyDataSetChanged();
            }
            return true;
        }

        if (mFriend.getUserId().compareToIgnoreCase(fromUserId) == 0) {// 是该人的聊天消息
            // 不再在这里解密消息，让TextViewHolder来处理解密逻辑
            // 这样可以保持消息的加密状态，让UI层决定何时解密

            Log.e("zq", "onNewMessage----isRocketMessage----" +message.isRocketMessage());
            // 检查是否为火箭消息，如果是则播放接收动画
            if (message.isRocketMessage()) {
                // 播放接收火箭消息动画
                playRocketReceiveAnimation();
            }

            mChatMessages.add(message);
            if (mChatContentView.shouldScrollToBottom()) {
                mChatContentView.notifyDataSetInvalidated(true);
            } else {
                // 振动提示一下
                Vibrator vibrator = (Vibrator) MyApplication.getContext().getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {100, 400, 100, 400};
                if (vibrator != null) {
                    vibrator.vibrate(pattern, -1);
                }
                mChatContentView.notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    /**
     * 消息发送状态变化回调
     * 功能说明：
     * 1. 状态验证：检查消息ID是否有效
     * 2. 消息查找：在消息列表中查找对应的消息
     * 3. 状态更新：更新消息的发送状态（发送中、已发送、发送失败）
     * 4. 状态保护：防止状态回退（已送达状态不能回退到发送中）
     * 5. 界面刷新：根据滚动位置决定刷新方式
     *
     * @param messageState 消息状态（0=发送中，1=已送达，-1=发送失败）
     * @param msgId 消息的唯一标识ID
     */
    @Override
    public void onMessageSendStateChange(int messageState, String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        LogUtils.log("fffffff" + msgId + "===" + messageState);

        LogUtils.log("fffffffmChatMessages.size" + mChatMessages.size());
        for (int i = 0; i < mChatMessages.size(); i++) {
            ChatMessage msg = mChatMessages.get(i);
            LogUtils.log("fffffff" + msg.getPacketId() + msg.getToUserName());

            if (msgId.equals(msg.getPacketId())) {
                /**
                 * 之前发现对方已经收到消息了，这里还在转圈，退出重进之后又变为送达了，
                 * 调试时发现出现该问题是因为消息状态先更新的1，在更新的0，这里处理下
                 */

                LogUtils.log("aaaaaaaa" + msgId + "===" + messageState);
                if (msg.getMessageState() == 1) {
                    return;
                }
                msg.setMessageState(messageState);
                if (mChatContentView.shouldScrollToBottom()) {
                    mChatContentView.notifyDataSetInvalidated(true);
                } else {
                    mChatContentView.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    /**
     * 复制自com.oort.weichat.ui.me.LocalVideoActivity#helloEventBus(com.oort.weichat.bean.event.MessageVideoFile)
     * 主要是CameraDemoActivity录制结束不走activity result, 而是发EventBus,
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventUploadFileRate message) {
        for (int i = 0; i < mChatMessages.size(); i++) {
            if (mChatMessages.get(i).getPacketId().equals(message.getPacketId())) {
                mChatMessages.get(i).setUploadSchedule(message.getRate());
                // 不能在这里setUpload，上传完成不代表上传成功，服务器可能没有正确返回url,相当于上传失败，
                mChatContentView.notifyDataSetChanged();
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventUploadCancel message) {
        for (int i = 0; i < mChatMessages.size(); i++) {
            if (mChatMessages.get(i).getPacketId().equals(message.getPacketId())) {
                mChatMessages.remove(i);
                mChatContentView.notifyDataSetChanged();
                ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), message.getPacketId());
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageVideoFile message) {
        VideoFile videoFile = new VideoFile();
        videoFile.setCreateTime(TimeUtils.f_long_2_str(System.currentTimeMillis()));
        videoFile.setFileLength(message.timelen);
        videoFile.setFileSize(message.length);
        videoFile.setFilePath(message.path);
        videoFile.setOwnerId(spUser.getString("userid", ""));
        VideoFileDao.getInstance().addVideoFile(videoFile);
        String filePath = message.path;
        if (TextUtils.isEmpty(filePath)) {
            ToastUtil.showToast(this, R.string.record_failed);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtil.showToast(this, R.string.record_failed);
            return;
        }
        sendVideo(file);
    }

    private void compress(File file) {
        String path = file.getPath();
        DialogHelper.showMessageProgressDialog(this, MyApplication.getContext().getString(R.string.compressed));
        final String out = RecorderUtils.getVideoFileByTime();
        String[] cmds = RecorderUtils.ffmpegComprerssCmd(path, out);
        long duration = VideoUitls.getDuration(path);

        VideoCompressUtil.exec(cmds, duration, new OnEditorListener() {
            public void onSuccess() {
                DialogHelper.dismissProgressDialog();
                File outFile = new File(out);
                runOnUiThread(() -> {
                    if (outFile.exists()) {
                        sendVideo(outFile);
                    } else {
                        sendVideo(file);
                    }
                });
            }

            public void onFailure() {
                DialogHelper.dismissProgressDialog();
                runOnUiThread(() -> {
                    sendVideo(file);
                });
            }

            public void onProgress(float progress) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageLocalVideoFile message) {
        compress(message.file);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventRedReceived message) {
        showReceiverRedLocal(message.getOpenRedpacket());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FILE: // 系统管理器返回文件
                    String file_path = FileUtils.getPath(ChatActivity.this, data.getData());
                    Log.e("xuan", "conversionFile: " + file_path);
                    if (file_path == null) {
                        ToastUtil.showToast(mContext, R.string.tip_file_not_supported);
                    } else {
                        sendFile(new File(file_path));
                    }
                    break;
                case REQUEST_CODE_CAPTURE_PHOTO:
                    // 拍照返回
                    if (mNewPhotoUri != null) {
                        photograph(new File(mNewPhotoUri.getPath()));
                    }
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    if (data != null) {
                        boolean isOriginal = data.getBooleanExtra(PhotoPickerActivity.EXTRA_RESULT_ORIGINAL, false);
                        album(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), isOriginal);
                    } else {
                        ToastUtil.showToast(this, R.string.c_photo_album_failed);
                    }
                    break;
                case REQUEST_CODE_SELECT_VIDEO: {
                    // 选择视频的返回
                    if (data == null) {
                        return;
                    }
                    String json = data.getStringExtra(AppConstant.EXTRA_VIDEO_LIST);
                    List<VideoFile> fileList = JSON.parseArray(json, VideoFile.class);
                    if (fileList == null || fileList.size() == 0) {
                        // 不可到达，列表里有做判断，
                        Reporter.unreachable();
                    } else {
                        for (VideoFile videoFile : fileList) {
                            String filePath = videoFile.getFilePath();
                            if (TextUtils.isEmpty(filePath)) {
                                // 不可到达，列表里有做过滤，
                                Reporter.unreachable();
                            } else {
                                File file = new File(filePath);
                                if (!file.exists()) {
                                    // 不可到达，列表里有做过滤，
                                    Reporter.unreachable();
                                } else {
                                    sendVideo(file);
                                }
                            }
                        }
                    }
                    break;
                }
                case REQUEST_CODE_SELECT_Locate: // 选择位置的返回
                    double latitude = data.getDoubleExtra(AppConstant.EXTRA_LATITUDE, 0);
                    double longitude = data.getDoubleExtra(AppConstant.EXTRA_LONGITUDE, 0);
                    String address = data.getStringExtra(AppConstant.EXTRA_ADDRESS);
                    String snapshot = data.getStringExtra(AppConstant.EXTRA_SNAPSHOT);

                    if (latitude != 0 && longitude != 0 && !TextUtils.isEmpty(address)
                            && !TextUtils.isEmpty(snapshot)) {
                        sendLocate(latitude, longitude, address, snapshot);
                    } else {
                        ToastUtil.showToast(mContext, getString(R.string.loc_startlocnotice));
                    }
                    break;
                case REQUEST_CODE_SEND_COLLECTION: {
                    String json = data.getStringExtra("data");
                    CollectionEvery collection = JSON.parseObject(json, CollectionEvery.class);
                    clickCollectionSend(collection);
                    break;
                }
                case REQUEST_CODE_QUICK_SEND:
                    String image = QuickSendPreviewActivity.parseResult(data);
                    sendImage(new File(image));
                    break;
                case REQUEST_CODE_SEND_CONTACT: {
                    List<Contacts> contactsList = SendContactsActivity.parseResult(data);
                    if (contactsList == null) {
                        ToastUtil.showToast(mContext, R.string.simple_data_error);
                    } else {
                        sendContacts(contactsList);
                    }
                    break;
                }
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            switch (requestCode) {
                case REQUEST_CODE_SEND_RED:
                    if (data != null) {
                        ChatMessage chatMessage = new ChatMessage(data.getStringExtra(AppConstant.EXTRA_CHAT_MESSAGE));
                        mChatMessages.add(chatMessage);
                        mChatContentView.notifyDataSetInvalidated(true);
                        sendMessage(chatMessage);
                        // 更新余额
                        CoreManager.updateMyBalance();
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    // 单张图片压缩 拍照
    private void photograph(final File file) {
        Log.e("zq", "压缩前图片路径:" + file.getPath() + "压缩前图片大小:" + file.length() / 1024 + "KB");
        // 拍照出来的图片Luban一定支持，
        Luban.with(this)
                .load(file)
                .ignoreBy(100)     // 原图小于100kb 不压缩
                // .putGear(2)     // 设定压缩档次，默认三挡
                // .setTargetDir() // 指定压缩后的图片路径
                .setCompressListener(new OnCompressListener() { // 设置回调
                    @Override
                    public void onStart() {
                        Log.e("zq", "开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.e("zq", "压缩成功，压缩后图片位置:" + file.getPath() + "压缩后图片大小:" + file.length() / 1024 + "KB");
                        sendImage(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("zq", "压缩失败,原图上传");
                        sendImage(file);
                    }
                }).launch();// 启动压缩
    }

    // 多张图片压缩 相册
    private void album(ArrayList<String> stringArrayListExtra, boolean isOriginal) {
        if (isOriginal) {// 原图发送，不压缩
            Log.e("zq", "原图发送，不压缩，开始发送");
            for (int i = 0; i < stringArrayListExtra.size(); i++) {
                sendImage(new File(stringArrayListExtra.get(i)));
            }
            Log.e("zq", "原图发送，不压缩，发送结束");
            return;
        }

        List<String> list = new ArrayList<>();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < stringArrayListExtra.size(); i++) {
            // Luban只处理特定后缀的图片，不满足的不处理也不走回调，
            // 只能挑出来不压缩，
            // todo luban支持压缩.gif图，但是压缩之后的.gif图用glide加载与转换为gifDrawable都会出问题，所以,gif图不压缩了
            List<String> lubanSupportFormatList = Arrays.asList("jpg", "jpeg", "png", "webp");
            boolean support = false;
            for (int j = 0; j < lubanSupportFormatList.size(); j++) {
                if (stringArrayListExtra.get(i).endsWith(lubanSupportFormatList.get(j))) {
                    support = true;
                    break;
                }
            }
            if (!support) {
                list.add(stringArrayListExtra.get(i));
                fileList.add(new File(stringArrayListExtra.get(i)));
            }
        }

        if (fileList.size() > 0) {
            for (File file : fileList) {// 不压缩的部分，直接发送
                sendImage(file);
            }
        }

        stringArrayListExtra.removeAll(list);

        Luban.with(this)
                .load(stringArrayListExtra)
                .ignoreBy(100)// 原图小于100kb 不压缩
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.e("zq", "开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        sendImage(file);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();// 启动压缩
    }

    /*******************************************
     * 接收到EventBus后的后续操作
     ******************************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventRequert message) {
        requstImageText(message.url);
    }

    private void requstImageText(String url) {
        HttpUtils.get().url(url).build().execute(new BaseCallback<Void>(Void.class) {

            @Override
            public void onResponse(ObjectResult<Void> result) {

            }

            @Override
            public void onError(Call call, Exception e) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventNotifyByTag message) {
        if (TextUtils.equals(message.tag, EventNotifyByTag.Speak)) {
            boolean isSpeaker = PreferenceUtils.getBoolean(MyApplication.getContext(),
                    Constants.SPEAKER_AUTO_SWITCH + CoreManager.requireSelf(MyApplication.getContext()).getUserId(), true);
            findViewById(R.id.iv_title_center).setVisibility(isSpeaker ? View.GONE : View.VISIBLE);
            if (VoiceManager.instance().getMediaPlayer().isPlaying()) {
                // 当前正在播放语音，如果为扬声器切换到语音，仿微信，重新播放一遍
                if (!isSpeaker) {
                    VoiceManager.instance().earpieceUser();
                }
                mAudioModeManger.setSpeakerPhoneOn(isSpeaker);
                if (!isSpeaker) {
                    mTvTitle.postDelayed(() -> VoiceManager.instance().earpieceUser(), 200);
                }
            } else {
                mAudioModeManger.setSpeakerPhoneOn(isSpeaker);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventGpu message) {// 拍照返回
        photograph(new File(message.event));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventTransfer message) {
        mChatContentView.postDelayed(() -> {
            if (message.getChatMessage().getType() == XmppMessage.TYPE_TRANSFER) {// 发送转账消息
                mChatMessages.add(message.getChatMessage());
                mChatContentView.notifyDataSetInvalidated(true);
                sendMessage(message.getChatMessage());
            } else if (message.getChatMessage().getType() == XmppMessage.TYPE_TRANSFER_RECEIVE) {// 转账被领取
                // 对方有可能重发了多条转账消息，需遍历处理
                String id = message.getChatMessage().getContent();
                for (int i1 = 0; i1 < mChatMessages.size(); i1++) {
                    if (TextUtils.equals(mChatMessages.get(i1).getObjectId(), id)) {
                        mChatMessages.get(i1).setFileSize(2);
                    }
                }
                mChatContentView.notifyDataSetChanged();
            } else {// 重发转账消息 || 确认领取
                for (int i = 0; i < mChatMessages.size(); i++) {
                    if (TextUtils.equals(mChatMessages.get(i).getPacketId(),
                            message.getChatMessage().getPacketId())) {
                        if (message.getChatMessage().getType() == TransferMoneyDetailActivity.EVENT_REISSUE_TRANSFER) {
                            ChatMessage chatMessage = mChatMessages.get(i).clone(false);
                            mChatMessages.add(chatMessage);
                            mChatContentView.notifyDataSetInvalidated(true);
                            sendMessage(chatMessage);
                        } else {
                            // 对方有可能重发了多条转账消息，需遍历处理
                            String id = mChatMessages.get(i).getObjectId();
                            for (int i1 = 0; i1 < mChatMessages.size(); i1++) {
                                if (TextUtils.equals(mChatMessages.get(i1).getObjectId(), id)) {
                                    mChatMessages.get(i1).setFileSize(2);
                                    ChatMessageDao.getInstance().updateChatMessageReceiptStatus(mLoginUserId, mFriend.getUserId(), mChatMessages.get(i1).getPacketId());
                                }
                            }
                            mChatContentView.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, 50);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEvent message) {
        Log.e("xuan", "helloEventBus  MessageEvent: " + message.message);
        if (mDelayDelMaps == null || mDelayDelMaps.isEmpty() || mChatMessages == null || mChatMessages.size() == 0) {
            return;
        }

        for (ChatMessage chatMessage : mChatMessages) {
            if (chatMessage.getFilePath().equals(message.message) && mDelayDelMaps.contains(chatMessage.getPacketId())) {
                String packedId = chatMessage.getPacketId();

                if (ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), packedId)) {
                    Log.e("xuan", "删除成功 ");
                } else {
                    Log.e("xuan", "删除失败 " + packedId);
                }
                mDelayDelMaps.remove(packedId);
                mChatContentView.removeItemMessage(packedId);
                break;
            }
        }
    }

    // 阅后即焚的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventClickFire message) {
        Log.e("xuan", "helloEventBus: " + message.event + " ,  " + message.packedId);
        if ("delete".equals(message.event)) {
            mDelayDelMaps.remove(message.packedId);
            ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), message.packedId);
            mChatContentView.removeItemMessage(message.packedId);
        } else if ("delay".equals(message.event)) {
            mDelayDelMaps.add(message.packedId);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void helloEventBus(final MessageEventSipEVent message02) {
//        // 对方在线  准备接受
//        if (message02.message.getType() == XmppMessage.TYPE_CONNECT_VOICE
//                || message02.message.getType() == XmppMessage.TYPE_CONNECT_VIDEO
//                || message02.message.getType() == XmppMessage.TYPE_CONNECT_SCREEN) {
//            EventBus.getDefault().post(new MessageEventSipPreview(mFriend.getUserId(), mFriend, message02.message));
//        }
//    }

    // 音视频会议 先不处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventClicAudioVideo message) {
        if (message.isauido == 0) {// 语音会议
            Log.e("Jitsi_connecting_second",  "语音会议:message.event.getObjectId()");
            Jitsi_connecting_second.start(this, message.event.getObjectId(), spUser.getString("userid", ""), 3);
        } else if (message.isauido == 1) {// 视频会议

            try {
                // object creation of JitsiMeetConferenceOptions
                // class by the name of options
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL(Constant.IM_JIT_SI_URL))
                        .build();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(spUser.getString("userid", ""))
                    .build();
            JitsiMeetActivity.launch(this, options);
//            Jitsi_connecting_second.start(this, message.event.getObjectId(), spUser.getString("userid",""), 4);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageEventClickable message) {
        if (message.event.isMySend()) {
            shake(0);
        } else {
            shake(1);
        }
    }

    // 发送多选消息
    @SuppressLint("StringFormatInvalid")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventMoreSelected message) {
        List<ChatMessage> mSelectedMessageList = new ArrayList<>();
        if (message.getToUserId().equals("MoreSelectedCollection") || message.getToUserId().equals("MoreSelectedEmail")) {// 多选 收藏 || 保存
            moreSelected(false, 0);
            return;
        }
        if (message.getToUserId().equals("MoreSelectedDelete")) {// 多选 删除
            for (int i = 0; i < mChatMessages.size(); i++) {
                if (mChatMessages.get(i).isMoreSelected) {
                    if (ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), mChatMessages.get(i).getPacketId())) {
                        Log.e("more_selected", "删除成功");
                    } else {
                        Log.e("more_selected", "删除失败");
                    }
                    mSelectedMessageList.add(mChatMessages.get(i));
                }
            }

            String mMsgIdListStr = "";
            for (int i = 0; i < mSelectedMessageList.size(); i++) {
                if (i == mSelectedMessageList.size() - 1) {
                    mMsgIdListStr += mSelectedMessageList.get(i).getPacketId();
                } else {
                    mMsgIdListStr += mSelectedMessageList.get(i).getPacketId() + ",";
                }
            }
            deleteMessage(mMsgIdListStr);// 服务端也需要删除

            mChatMessages.removeAll(mSelectedMessageList);
            moreSelected(false, 0);
        } else {// 多选 转发
            if (message.isSingleOrMerge()) {// 合并转发
                List<String> mStringHistory = new ArrayList<>();
                for (int i = 0; i < mChatMessages.size(); i++) {
                    if (mChatMessages.get(i).isMoreSelected) {
                        String body = mChatMessages.get(i).toJsonString();
                        mStringHistory.add(body);
                    }
                }
                String detail = JSON.toJSONString(mStringHistory);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(XmppMessage.TYPE_CHAT_HISTORY);
                chatMessage.setFromUserId(mLoginUserId);
                chatMessage.setFromUserName(mLoginNickName);
                chatMessage.setToUserId(message.getToUserId());
                chatMessage.setContent(detail);
                String s = TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName();
                chatMessage.setObjectId(getString(R.string.chat_history_place_holder, s, mLoginNickName));
                chatMessage.setMySend(true);
                chatMessage.setSendRead(false);
                chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, message.getToUserId(), chatMessage);
                if (message.isGroupMsg()) {
                    coreManager.sendMucChatMessage(message.getToUserId(), chatMessage);
                } else {
                    coreManager.sendChatMessage(message.getToUserId(), chatMessage);
                }
                if (message.getToUserId().equals(mFriend.getUserId())) {// 转发给当前对象
                    mChatMessages.add(chatMessage);
                }
            } else {// 逐条转发
                for (int i = 0; i < mChatMessages.size(); i++) {
                    if (mChatMessages.get(i).isMoreSelected) {
                        ChatMessage chatMessage = ChatMessageDao.getInstance().findMsgById(mLoginUserId, mFriend.getUserId(), mChatMessages.get(i).getPacketId());
                        if (chatMessage.getType() == XmppMessage.TYPE_RED) {
                            chatMessage.setType(XmppMessage.TYPE_TEXT);
                            chatMessage.setContent(getString(R.string.msg_red_packet));
                        } else if (chatMessage.getType() >= XmppMessage.TYPE_IS_CONNECT_VOICE
                                && chatMessage.getType() <= XmppMessage.TYPE_EXIT_VOICE) {
                            chatMessage.setType(XmppMessage.TYPE_TEXT);
                            chatMessage.setContent(getString(R.string.msg_video_voice));
                        } else if (chatMessage.getType() == XmppMessage.TYPE_SHAKE) {
                            chatMessage.setType(XmppMessage.TYPE_TEXT);
                            chatMessage.setContent(getString(R.string.msg_shake));
                        } else if (chatMessage.getType() == XmppMessage.TYPE_TRANSFER) {
                            chatMessage.setType(XmppMessage.TYPE_TEXT);
                            chatMessage.setContent(getString(R.string.tip_transfer_money));
                        }
                        chatMessage.setFromUserId(mLoginUserId);
                        chatMessage.setFromUserName(mLoginNickName);
                        chatMessage.setToUserId(message.getToUserId());
                        chatMessage.setUpload(true);
                        chatMessage.setMySend(true);
                        chatMessage.setSendRead(false);
                        chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                        chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                        mSelectedMessageList.add(chatMessage);
                    }
                }

                for (int i = 0; i < mSelectedMessageList.size(); i++) {
                    ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, message.getToUserId(), mSelectedMessageList.get(i));
                    if (message.isGroupMsg()) {
                        coreManager.sendMucChatMessage(message.getToUserId(), mSelectedMessageList.get(i));
                    } else {
                        coreManager.sendChatMessage(message.getToUserId(), mSelectedMessageList.get(i));
                    }
                    if (message.getToUserId().equals(mFriend.getUserId())) {// 转发给当前对象
                        mChatMessages.add(mSelectedMessageList.get(i));
                    }
                }
            }
        }
        // 现多选转发的取消多选状态放到转发类了，这个方法每个else块单独调用，因为选择转发至多个好友时，如果在第一次回调的event内将多选状态取消
        // 剩余回调多选的消息全部被清空了
        // moreSelected(false, 0);
    }

    public void moreSelected(boolean isShow, int position) {
        mChatBottomView.showMoreSelectMenu(isShow);
        if (isShow) {
            findViewById(R.id.iv_title_left).setVisibility(View.GONE);
            mTvTitleLeft.setVisibility(View.VISIBLE);
            if (!mChatMessages.get(position).getIsReadDel()) {// 非阅后即焚消息才能被选中
                mChatMessages.get(position).setMoreSelected(true);
            }
        } else {
            findViewById(R.id.iv_title_left).setVisibility(View.VISIBLE);
            mTvTitleLeft.setVisibility(View.GONE);
            for (int i = 0; i < mChatMessages.size(); i++) {
                mChatMessages.get(i).setMoreSelected(false);
            }
        }
        mChatContentView.setIsShowMoreSelect(isShow);
        mChatContentView.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final MessageUploadChatRecord message) {
        try {
            final CreateCourseDialog dialog = new CreateCourseDialog(this, new CreateCourseDialog.CoureseDialogConfirmListener() {
                @Override
                public void onClick(String content) {
                    upLoadChatList(message.chatIds, content);
                }
            });

            dialog.show();
        } catch (Exception e) {
            // 出现过一次，复用的layout改了一个控件类型，导致findViewById强转错误，
            Reporter.unreachable(e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventSyncFriendOperating message) {
        if (TextUtils.equals(message.getToUserId(), mFriend.getUserId())) {
            // attention：特殊情况下finish当前界面，多半为删除/被删除，拉黑/被拉黑，现消息列表支持显示陌生人，而本地针对上面的操作只是修改status，
            // 所以此时onDestroy下不调用onSaveConten方法
            isUserSaveContentMethod = false;
            finish();
        }

    }

    private void upLoadChatList(String chatIds, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("messageIds", chatIds);
        params.put("userId", mLoginUserId);
        params.put("courseName", name);
        params.put("createTime", TimeUtils.sk_time_current_time() + "");
        DialogHelper.showDefaulteMessageProgressDialog(this);
        HttpUtils.get().url(coreManager.getConfig().USER_ADD_COURSE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showToast(getApplicationContext(), getString(R.string.tip_create_cource_success));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtherBroadcast.IsRead);
        intentFilter.addAction("Refresh");
        intentFilter.addAction(OtherBroadcast.TYPE_INPUT);
        intentFilter.addAction(OtherBroadcast.MSG_BACK);
        intentFilter.addAction(OtherBroadcast.NAME_CHANGE);
        intentFilter.addAction(OtherBroadcast.MULTI_LOGIN_READ_DELETE);
        intentFilter.addAction(Constants.CHAT_MESSAGE_DELETE_ACTION);
        intentFilter.addAction(Constants.SHOW_MORE_SELECT_MENU);
        intentFilter.addAction(OtherBroadcast.TYPE_DELALL);
        intentFilter.addAction(Constants.CHAT_HISTORY_EMPTY);
        intentFilter.addAction(OtherBroadcast.QC_FINISH);
        intentFilter.addAction(Constants.SIMPLE_ENCRYPT_CHANGED); // 注册密聊变化广播
        intentFilter.addAction(Constants.ROCKET_MESSAGE_CHANGED); // 注册火箭消息变化广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }
    }

    /*******************************************
     * 初始化ActionBar与其点击事件
     ******************************************/
    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBack();
            }
        });

        mTvTitleLeft = (TextView) findViewById(R.id.tv_title_left);
        mTvTitleLeft.setVisibility(View.GONE);
        mTvTitleLeft.setText(getString(R.string.cancel));
        mTvTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreSelected(false, 0);
            }
        });
        mTvTitle = (TextView) findViewById(R.id.tv_title_center);
        String remarkName = mFriend.getRemarkName();
        if (TextUtils.isEmpty(remarkName)) {
            mTvTitle.setText(mFriend.getNickName());
        } else {
            mTvTitle.setText(remarkName);
        }

        ImageView mMore = (ImageView) findViewById(R.id.iv_title_right);
        mMore.setImageResource(R.drawable.chat_more);
        mMore.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                mChatBottomView.reset();
                mChatBottomView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ChatActivity.this, PersonSettingActivity.class);
                        intent.putExtra("ChatObjectId", mFriend.getUserId());
                        startActivity(intent);
                    }
                }, 100);
            }
        });

        if ((mFriend.getStatus() != Friend.STATUS_FRIEND && mFriend.getStatus() != Friend.STATUS_SYSTEM)
                || mFriend.getIsDevice() == 1) {// 非好友/公众号 || 我的设备 不显示更多按钮
            mMore.setVisibility(View.GONE);
        }

        // 加载聊天背景
        mChatBgIv = findViewById(R.id.chat_bg);
        loadBackdrop();
    }

    public void loadBackdrop() {
        String mChatBgPath = PreferenceUtils.getString(this, Constants.SET_CHAT_BACKGROUND_PATH
                + mFriend.getUserId() + mLoginUserId, "reset");

        String mChatBg = PreferenceUtils.getString(this, Constants.SET_CHAT_BACKGROUND
                + mFriend.getUserId() + mLoginUserId, "reset");

        if (TextUtils.isEmpty(mChatBgPath)
                || mChatBg.equals("reset")) {// 未设置聊天背景或者还原了聊天背景
            mChatBgIv.setImageDrawable(null);
            return;
        }

        File file = new File(mChatBgPath);
        if (file.exists()) {// 加载本地
            if (mChatBgPath.toLowerCase().endsWith("gif")) {
                try {
                    GifDrawable gifDrawable = new GifDrawable(file);
                    mChatBgIv.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoadHelper.showFileWithError(
                        ChatActivity.this,
                        file,
                        R.drawable.fez,
                        mChatBgIv
                );
            }
        } else {// 加载网络
            ImageLoadHelper.showImageWithError(
                    ChatActivity.this,
                    mChatBg,
                    R.color.chat_bg,
                    mChatBgIv
            );
        }
    }

    /*******************************************
     * 获取公众号菜单&&获取好友在线状态
     ******************************************/
    private void initFriendState() {
        if (mFriend.getIsDevice() == 1) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", mFriend.getUserId());

        HttpUtils.get().url(coreManager.getConfig().USER_GET_URL)
                .params(params)
                .build()
                .execute(new BaseCallback<User>(User.class) {
                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            User user = result.getData();
                            if (coreManager.getConfig().enableMpModule && user.getUserType() == 2) {
                                // 公众号,获取公众号菜单
                                initSpecialMenu();
                                FriendDao.getInstance().updateFriendStatus(mLoginUserId, userId, Friend.STATUS_SYSTEM);
                                return;
                            }
                            if (coreManager.getConfig().isOpenOnlineStatus) {
                                String name = mTvTitle.getText().toString();
                                switch (user.getOnlinestate()) {
                                    case 0:
                                        mTvTitle.setText(name + "(" + getString(R.string.off_line) + ")");
                                        break;
                                    case 1:
                                        mTvTitle.setText(name + getString(R.string.status_online));
                                        break;
                                }
                            }
                            if (user.getFriends() != null) {// 更新消息免打扰状态 && 更新消息保存天数...
                                FriendDao.getInstance().updateFriendPartStatus(mFriend.getUserId(), user);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    private void initSpecialMenu() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", mFriend.getUserId());

        HttpUtils.get().url(coreManager.getConfig().USER_GET_PUBLIC_MENU)
                .params(params)
                .build()
                .execute(new ListCallback<PublicMenu>(PublicMenu.class) {
                    @Override
                    public void onResponse(ArrayResult<PublicMenu> result) {
                        if (Result.checkSuccess(mContext, result)) {
                            List<PublicMenu> data = result.getData();
                            if (data != null && data.size() > 0) {
                                mChatBottomView.fillRoomMenu(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public void synchronizeChatHistory() {
        // 在调用该方法的时候，用户可能还会去下拉获取漫游，导致出现了重复的消息
        // 当该方法在调用时，禁止用户下拉
        mChatContentView.setNeedRefresh(false);

        long startTime;
        String chatSyncTimeLen = String.valueOf(PrivacySettingHelper.getPrivacySettings(this).getChatSyncTimeLen());
        if (Double.parseDouble(chatSyncTimeLen) == -2) {// 不同步
            mChatContentView.setNeedRefresh(true);
            FriendDao.getInstance().updateDownloadTime(mLoginUserId, mFriend.getUserId(), mFriend.getTimeSend());
            return;
        }
        if (Double.parseDouble(chatSyncTimeLen) == -1 || Double.parseDouble(chatSyncTimeLen) == 0) {// 同步 永久 startTime == downloadTime
            startTime = mFriend.getDownloadTime();
        } else {
            long syncTimeLen = (long) (Double.parseDouble(chatSyncTimeLen) * 24 * 60 * 60);// 得到消息同步时长
            if (mFriend.getTimeSend() - mFriend.getDownloadTime() <= syncTimeLen) {// 未超过消息同步时长
                startTime = mFriend.getDownloadTime();
            } else {// 超过消息同步时长，只同步时长内的消息
                startTime = mFriend.getTimeSend() - syncTimeLen;
            }
        }

        Map<String, String> params = new HashMap();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("receiver", mFriend.getUserId());
        params.put("startTime", String.valueOf(startTime * 1000));// 2010-01-01 00:00:00  服务端返回的数据为倒序返回
        params.put("endTime", String.valueOf(mFriend.getTimeSend() * 1000));
        params.put("pageSize", String.valueOf(Constants.MSG_ROMING_PAGE_SIZE));// 尽量传一个大的值 一次性拉下来
        // params.put("pageIndex", "0");

        HttpUtils.get().url(coreManager.getConfig().GET_CHAT_MSG)
                .params(params)
                .build()
                .execute(new ListCallback<ChatRecord>(ChatRecord.class) {
                    @Override
                    public void onResponse(ArrayResult<ChatRecord> result) {
                        FriendDao.getInstance().updateDownloadTime(mLoginUserId, mFriend.getUserId(), mFriend.getTimeSend());

                        final List<ChatRecord> chatRecordList = result.getData();
                        if (chatRecordList != null && chatRecordList.size() > 0) {
                            new Thread(() -> {
                                chatMessages = new ArrayList<>();

                                for (int i = 0; i < chatRecordList.size(); i++) {
                                    ChatRecord data = chatRecordList.get(i);
                                    String messageBody = data.getBody();
                                    messageBody = messageBody.replaceAll("&quot;", "\"");
                                    ChatMessage chatMessage = new ChatMessage(messageBody);

                                    if (!TextUtils.isEmpty(chatMessage.getFromUserId()) &&
                                            chatMessage.getFromUserId().equals(mLoginUserId)) {
                                        chatMessage.setMySend(true);
                                    }

                                    chatMessage.setSendRead(data.getIsRead() > 0); // 单聊的接口有返回是否已读，
                                    // 漫游的默认已上传
                                    chatMessage.setUpload(true);
                                    chatMessage.setUploadSchedule(100);
                                    chatMessage.setMessageState(MESSAGE_SEND_SUCCESS);

                                    if (TextUtils.isEmpty(chatMessage.getPacketId())) {
                                        if (!TextUtils.isEmpty(data.getMessageId())) {
                                            chatMessage.setPacketId(data.getMessageId());
                                        } else {
                                            chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                                        }
                                    }

                                    if (ChatMessageDao.getInstance().roamingMessageFilter(chatMessage.getType())) {
                                        ChatMessageDao.getInstance().decrypt(false, chatMessage);
                                        ChatMessageDao.getInstance().handlerRoamingSpecialMessage(chatMessage);
                                        if (ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mFriend.getUserId(), chatMessage)) {
                                            if (!chatMessage.isVerifySignatureFailed()) {// 单聊验签失败的消息不显示
                                                chatMessages.add(chatMessage);
                                            }
                                        }
                                    }
                                }

                                mTvTitle.post(() -> {
                                    for (int i = chatMessages.size() - 1; i >= 0; i--) {
                                        mChatMessages.add(chatMessages.get(i));
                                    }
                                    // 有可能本地已经发送或接收到了消息，需要对mChatMessages重新排序
                                    Comparator<ChatMessage> comparator = (c1, c2) -> (int) (c1.getDoubleTimeSend() - c2.getDoubleTimeSend());
                                    Collections.sort(mChatMessages, comparator);
                                    mChatContentView.notifyDataSetInvalidated(true);

                                    mChatContentView.setNeedRefresh(true);
                                });
                            }).start();
                        } else {
                            mChatContentView.setNeedRefresh(true);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mChatContentView.setNeedRefresh(true);
                        ToastUtil.showErrorData(ChatActivity.this);
                    }
                });
    }

    public void getNetSingle() {
        Map<String, String> params = new HashMap();
        long endTime;
        if (mChatMessages != null && mChatMessages.size() > 0) {// 本地有数据，截止时间为本地最早的一条消息的timeSend
            endTime = mChatMessages.get(0).getTimeSend();
        } else {// 本地无数据，截止时间为当前时间
            endTime = TimeUtils.sk_time_current_time();
        }

        long startTime;
        String chatSyncTimeLen = String.valueOf(PrivacySettingHelper.getPrivacySettings(this).getChatSyncTimeLen());
        if (Double.parseDouble(chatSyncTimeLen) == -2) {// 不同步
            return;
        }
        if (Double.parseDouble(chatSyncTimeLen) == -1 || Double.parseDouble(chatSyncTimeLen) == 0) {// 同步 永久
            startTime = 1262275200000L;  // 2010-01-01 00:00:00
        } else {
            long syncTimeLen = (long) (Double.parseDouble(chatSyncTimeLen) * 24 * 60 * 60 * 1000);// 得到消息同步时长
            startTime = TimeUtils.sk_time_current_time() - syncTimeLen;
        }

        // 范围扩大一秒容错，
        startTime -= 1000;

        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("receiver", mFriend.getUserId());
        params.put("startTime", String.valueOf(startTime));// 2010-01-01 00:00:00  服务端返回的数据为倒序返回
        params.put("endTime", String.valueOf(endTime * 1000));
        params.put("pageSize", String.valueOf(Constants.MSG_ROMING_PAGE_SIZE));
        params.put("pageIndex", "0");

        HttpUtils.get().url(coreManager.getConfig().GET_CHAT_MSG)
                .params(params)
                .build()
                .execute(new ListCallback<ChatRecord>(ChatRecord.class) {
                    @Override
                    public void onResponse(ArrayResult<ChatRecord> result) {
                        List<ChatRecord> chatRecordList = result.getData();

                        if (chatRecordList != null && chatRecordList.size() > 0) {
                            long currTime = TimeUtils.sk_time_current_time();
                            for (int i = 0; i < chatRecordList.size(); i++) {
                                ChatRecord data = chatRecordList.get(i);
                                String messageBody = data.getBody();
                                messageBody = messageBody.replaceAll("&quot;", "\"");
                                ChatMessage chatMessage = new ChatMessage(messageBody);

                                //处理PC端发送的文件消息
                                if (chatMessage.getType() == XmppMessage.TYPE_FILE ||
                                        chatMessage.getType() == XmppMessage.TYPE_IMAGE ||
                                        chatMessage.getType() == XmppMessage.TYPE_VIDEO ||
                                        chatMessage.getType() == XmppMessage.TYPE_VOICE) {
                                    if (!TextUtils.isEmpty(chatMessage.getTerminal()) && chatMessage.getTerminal().equals("PC")) {
                                        chatMessage.setType(XmppMessage.TYPE_TEXT);
                                        chatMessage.setContent("收到PC端文件消息,请在PC端查看");
                                    }
                                }
                                // 有一种情况，因为服务器1个小时才去删除一次过期消息，所以可能会拉到已过期的时间
                                if (chatMessage.getDeleteTime() > 1 && chatMessage.getDeleteTime() < currTime) {
                                    // 已过期的消息,扔掉
                                    continue;
                                }

                                if (!TextUtils.isEmpty(chatMessage.getFromUserId()) &&
                                        chatMessage.getFromUserId().equals(mLoginUserId)) {
                                    chatMessage.setMySend(true);
                                }

                                chatMessage.setSendRead(data.getIsRead() > 0); // 单聊的接口有返回是否已读，
                                // 漫游的默认已上传
                                chatMessage.setUpload(true);
                                chatMessage.setUploadSchedule(100);
                                chatMessage.setMessageState(MESSAGE_SEND_SUCCESS);

                                if (TextUtils.isEmpty(chatMessage.getPacketId())) {
                                    if (!TextUtils.isEmpty(data.getMessageId())) {
                                        chatMessage.setPacketId(data.getMessageId());
                                    } else {
                                        chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                                    }
                                }

                                if (ChatMessageDao.getInstance().roamingMessageFilter(chatMessage.getType())) {
                                    ChatMessageDao.getInstance().saveRoamingChatMessage(mLoginUserId, mFriend.getUserId(), chatMessage, false);
                                }
                            }
                            mHasMoreData = chatRecordList.size() >= mPageSize;
                            notifyChatAdapter();
                        } else {
                            mHasMoreData = false;
                            mChatContentView.headerRefreshingCompleted();
                            mChatContentView.setNeedRefresh(false);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void notifyChatAdapter() {
        if (mChatMessages.size() > 0) {
            mMinId = mChatMessages.get(0).getTimeSend();
        } else {
            ChatMessage chat = ChatMessageDao.getInstance().getLastChatMessage(mLoginUserId, mFriend.getUserId());
            if (chat != null && chat.getTimeSend() != 0) {
                mMinId = chat.getTimeSend() + 2;
            } else {
                mMinId = TimeUtils.sk_time_current_time();
            }
        }
        // 代码等跑到这里来说明 mMinId 一定没有查到数据，同步了漫游之后我们再次使用 mMinId 去查询一下数据
        List<ChatMessage> chatLists = ChatMessageDao.getInstance().getSingleChatMessages(mLoginUserId,
                mFriend.getUserId(), mMinId, mPageSize);
        if (chatLists == null || chatLists.size() == 0) {
            mHasMoreData = false;
            mChatContentView.headerRefreshingCompleted();
            mChatContentView.setNeedRefresh(false);
            return;
        }

        for (int i = 0; i < chatLists.size(); i++) {
            ChatMessage message = chatLists.get(i);
            if (message.isVerifySignatureFailed()) {// 单聊验签失败的消息不显示
                continue;
            }
            mChatMessages.add(0, message);
        }

        // 根据timeSend进行排序
       /* Collections.sort(mChatMessages, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage o1, ChatMessage o2) {
                return (int) (o1.getDoubleTimeSend() - o2.getDoubleTimeSend());
            }
        });*/

        mChatContentView.notifyDataSetAddedItemsToTop(chatLists.size());
        mChatContentView.headerRefreshingCompleted();
        if (!mHasMoreData) {
            mChatContentView.setNeedRefresh(false);
        }
    }

    /*******************************************
     * 转发&&拦截
     ******************************************/
    private void instantChatMessage() {
        if (!TextUtils.isEmpty(instantMessage)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String toUserId = getIntent().getStringExtra("fromUserId");
                    ChatMessage chatMessage = ChatMessageDao.getInstance().findMsgById(mLoginUserId, toUserId, instantMessage);
                    TrillStatisticsHelper.share(mContext, coreManager, chatMessage);
                    chatMessage.setFromUserId(mLoginUserId);
                    chatMessage.setFromUserName(mLoginNickName);
                    chatMessage.setToUserId(mFriend.getUserId());
                    chatMessage.setUpload(true);
                    chatMessage.setMySend(true);
                    chatMessage.setSendRead(false);
                    chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                    chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                    mChatMessages.add(chatMessage);
                    mChatContentView.notifyDataSetInvalidated(true);
                    ChatMessageDao.getInstance().saveNewSingleChatMessage(mLoginUserId, mFriend.getUserId(), chatMessage);
                    sendMsg(chatMessage);
                    instantMessage = null;
                }
            }, 1000);
        }
    }

    public boolean interprect() {
        for (Friend friend : mBlackList) {
            if (friend.getUserId().equals(mFriend.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /*******************************************
     * 是否离线&&重连
     ******************************************/
    public boolean isAuthenticated() {
        boolean isLogin = coreManager.isLogin();
        if (!isLogin) {
            coreManager.autoReconnect(this);
        }
        //  离线时发消息也不能return，自动重连...，让消息转圈(有重发)
        // 但这里有可能出现是CoreService断开了，这种情况继续发消息就会崩溃，不能继续，
        return !coreManager.isServiceReady();
    }

    private void sendSecureChatReadyTip() {
        if (isSecureAlreadyTipd) {
            return;
        }
        isSecureAlreadyTipd = true;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(XmppMessage.TYPE_TIP);
        chatMessage.setFromUserId(mLoginUserId);
        chatMessage.setFromUserName(mLoginNickName);
        chatMessage.setContent(getString(R.string.msg_open_secure_chat_ready));
        chatMessage.setPacketId(AppConfig.apiKey + "tip");
        chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        mChatMessages.add(chatMessage);
        mChatContentView.notifyDataSetChanged();
    }

    /*******************************************
     * 接收到广播后的后续操作
     ******************************************/
    public class RefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(OtherBroadcast.IsRead)) {
                // 收到已读的广播 单聊
                Bundle bundle = intent.getExtras();
                String packetId = bundle.getString("packetId");
                boolean isReadChange = bundle.getBoolean("isReadChange");
                for (int i = 0; i < mChatMessages.size(); i++) {
                    ChatMessage msg = mChatMessages.get(i);
                    if (msg.getPacketId().equals(packetId)) {
                        msg.setSendRead(true);// 更新为已读
                        if (isReadChange) {// 阅后即焚已读 且本地数据库已经修改
                            ChatMessage msgById = ChatMessageDao.getInstance().findMsgById(mLoginUserId, mFriend.getUserId(), packetId);
                            if (msgById != null) {
                                if (msg.getType() == XmppMessage.TYPE_VOICE) {
                                    if (!TextUtils.isEmpty(VoicePlayer.instance().getVoiceMsgId())
                                            && packetId.equals(VoicePlayer.instance().getVoiceMsgId())) {// 对方查看该语音时，我正在播放... 需要停止播放
                                        VoicePlayer.instance().stop();
                                    }
                                } else if (msg.getType() == XmppMessage.TYPE_VIDEO) {
                                    if (!TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL)
                                            && msg.getContent().equals(JCMediaManager.CURRENT_PLAYING_URL)) {// 对方查看该ƒ时，我正在播放... 需要退出全屏、停止播放
                                        JCVideoPlayer.releaseAllVideos();
                                    }
                                }

                                msg.setType(msgById.getType());
                                msg.setContent(msgById.getContent());
                            }
                        }
                        mChatContentView.notifyDataSetInvalidated(false);

                        // 收到已读，将离线修改为在线
                        if (coreManager.getConfig().isOpenOnlineStatus) {
                            String titleContent = mTvTitle.getText().toString();
                            if (titleContent.contains(getString(R.string.off_line))) {
                                String changeTitleContent = titleContent.replace(getString(R.string.off_line),
                                        getString(R.string.online));
                                mTvTitle.setText(changeTitleContent);
                            }
                        }
                        break;
                    }
                }
            } else if (action.equals("Refresh")) {
                Bundle bundle = intent.getExtras();
                String packetId = bundle.getString("packetId");
                String fromId = bundle.getString("fromId");
                int type = bundle.getInt("type");
               /* if (type == XmppMessage.TYPE_INPUT && mFriend.getUserId().equals(fromId)) {
                    // 对方正在输入...
                    nameTv.setText(getString("JX_Entering"));
                    time.cancel();
                    time.start();
                }*/
                // 这里表示正在聊天的时候，收到新消息，重新适配一下数据可以立即返回已读回执
                for (int i = 0; i < mChatMessages.size(); i++) {
                    ChatMessage msg = mChatMessages.get(i);
                    // 碰到packetId为空的就是刚刚加进来的消息
                    if (msg.getPacketId() == null) {
                        // 找到该消息，把已读标志设置为false，然后适配数据的时候就可以发现它，就可以回执已读了
                        msg.setSendRead(false); // 收到新消息，默认未读
                        msg.setFromUserId(mFriend.getUserId());
                        msg.setPacketId(packetId);
                        break;
                    }
                }
                mChatContentView.notifyDataSetInvalidated(false);
            } else if (action.equals(OtherBroadcast.TYPE_INPUT)) {
                String fromId = intent.getStringExtra("fromId");
                if (mFriend.getUserId().equals(fromId)) {
                    // 对方正在输入...
                    Log.e("zq", "对方正在输入...");
                    mTvTitle.setText(getString(R.string.entering));
                    time.cancel();
                    time.start();
                }
            } else if (action.equals(OtherBroadcast.MSG_BACK)) {
                String packetId = intent.getStringExtra("packetId");
                if (TextUtils.isEmpty(packetId)) {
                    return;
                }
                for (ChatMessage chatMessage : mChatMessages) {
                    if (packetId.equals(chatMessage.getPacketId())) {
                        if (chatMessage.getType() == XmppMessage.TYPE_VOICE
                                && !TextUtils.isEmpty(VoicePlayer.instance().getVoiceMsgId())
                                && packetId.equals(VoicePlayer.instance().getVoiceMsgId())) {// 语音 && 正在播放的msgId不为空 撤回的msgId==正在播放的msgId
                            // 停止播放语音
                            VoicePlayer.instance().stop();
                        }
                        ChatMessage chat = ChatMessageDao.getInstance().findMsgById(mLoginUserId, mFriend.getUserId(), packetId);
                        chatMessage.setType(chat.getType());
                        chatMessage.setContent(chat.getContent());
                        break;
                    }
                }
                mChatContentView.notifyDataSetInvalidated(true);
            } else if (action.equals(OtherBroadcast.NAME_CHANGE)) {// 修改备注名
                mFriend = FriendDao.getInstance().getFriend(mLoginUserId, mFriend.getUserId());
                if (coreManager.getConfig().isOpenOnlineStatus) {
                    String s = mTvTitle.getText().toString();
                    if (s.contains(getString(R.string.online))) {
                        mTvTitle.setText(TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName()
                                + "(" + getString(R.string.online) + ")");
                    } else {
                        mTvTitle.setText(TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName()
                                + "(" + getString(R.string.off_line) + ")");
                    }
                } else {
                    mTvTitle.setText(TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getNickName() : mFriend.getRemarkName());
                }
            } else if (action.equals(OtherBroadcast.MULTI_LOGIN_READ_DELETE)) {// 兼容 多点登录 阅后即焚 其他端已读了该条消息
                String packet = intent.getStringExtra("MULTI_LOGIN_READ_DELETE_PACKET");
                if (!TextUtils.isEmpty(packet)) {

                    for (int i = 0; i < mChatMessages.size(); i++) {
                        if (mChatMessages.get(i).getPacketId().equals(packet)) {
                            mChatMessages.remove(i);
                            mChatContentView.notifyDataSetInvalidated(true);
                            break;
                        }
                    }
                }
            } else if (action.equals(Constants.CHAT_MESSAGE_DELETE_ACTION)) {

                if (mChatMessages == null || mChatMessages.size() == 0) {
                    return;
                }

                // 用户手动删除
                int position = intent.getIntExtra(Constants.CHAT_REMOVE_MESSAGE_POSITION, -1);
                if (position >= 0 && position < mChatMessages.size()) { // 合法的postion
                    ChatMessage message = mChatMessages.get(position);
                    deleteMessage(message.getPacketId());// 服务端也需要删除
                    if (ChatMessageDao.getInstance().deleteSingleChatMessage(mLoginUserId, mFriend.getUserId(), message.getPacketId())) {
                        mChatMessages.remove(position);
                        mChatContentView.notifyDataSetInvalidated(true);
                        Toast.makeText(mContext, getString(R.string.delete_all_succ), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.delete_failed, Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (action.equals(Constants.SHOW_MORE_SELECT_MENU)) {// 显示多选菜单
                int position = intent.getIntExtra(Constants.CHAT_SHOW_MESSAGE_POSITION, 0);
                moreSelected(true, position);
            } else if (action.equals(OtherBroadcast.TYPE_DELALL)) {
                // attention：特殊情况下finish当前界面，多半为删除/被删除，拉黑/被拉黑，现消息列表支持显示陌生人，而本地针对上面的操作只是修改status，
                // 所以此时onDestroy下不调用onSaveConten方法
                isUserSaveContentMethod = false;

                // 被拉黑 || 删除  @see XChatManger 190
                // 好友被后台删除，xmpp 512,
                String toUserId = intent.getStringExtra("toUserId");
                // 只处理正在聊天对象是删除自己的人的情况，
                if (Objects.equals(mFriend.getUserId(), toUserId)) {
                    String content = intent.getStringExtra("content");
                    if (!TextUtils.isEmpty(content)) {
                        ToastUtil.showToast(mContext, content);
                    }
                    Intent mainIntent = new Intent(mContext, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            } else if (action.equals(Constants.CHAT_HISTORY_EMPTY)) {// 清空聊天记录
                mChatMessages.clear();
                mChatContentView.notifyDataSetChanged();
            } else if (action.equals(OtherBroadcast.QC_FINISH)) {
                int mOperationCode = intent.getIntExtra("Operation_Code", 0);
                if (mOperationCode == 1) {// 更换聊天背景成功 更新当前页面
                    loadBackdrop();
                } else {// 快速创建群组成功 关闭当前页面
                    finish();
                }
            } else if (action.equals(Constants.SIMPLE_ENCRYPT_CHANGED)) {
                // 密聊状态变化，刷新消息列表
                android.util.Log.e("EncryptChanged", "密聊状态变化，刷新消息列表");

                // 延迟刷新，确保SharedPreferences状态完全同步
                mChatContentView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        android.util.Log.e("EncryptChanged", "延迟刷新消息列表");
                        mChatContentView.notifyDataSetChanged();
                    }
                }, 100); // 延迟100ms刷新
            } else if (action.equals(Constants.ROCKET_MESSAGE_CHANGED)) {
                // 火箭消息状态变化，刷新消息列表
                android.util.Log.e("RocketMessageChanged", "火箭消息状态变化，刷新消息列表");

                // 延迟刷新，确保SharedPreferences状态完全同步
                mChatContentView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        android.util.Log.e("RocketMessageChanged", "延迟刷新消息列表");
                        mChatContentView.notifyDataSetChanged();
                    }
                }, 100); // 延迟100ms刷新
            }
        }
    }

    /**
     * 检查文本是否为纯表情消息
     * @param text 要检查的文本
     * @return true 如果是纯表情消息，false 否则
     */
    private boolean isEmojiOnlyMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        // 表情格式：[表情名]，使用正则表达式匹配
        Pattern emojiPattern = Pattern.compile("\\[[^\\]]+\\]");

        // 移除所有表情后，检查是否还有非空白字符
        String textWithoutEmojis = emojiPattern.matcher(text).replaceAll("").trim();

        // 如果移除表情后没有其他内容，则认为是纯表情消息
        return textWithoutEmojis.isEmpty();
    }
}

