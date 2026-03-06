package com.oort.weichat.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.TextUtils;

import com.oort.weichat.MyApplication;
import com.oort.weichat.Reporter;
import com.oort.weichat.bean.User;
import com.oort.weichat.bean.event.MessageEventBG;
import com.oort.weichat.sp.UserSp;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.xmpp.util.XmppStringUtil;
import com.oortcloud.basemodule.utils.OperLogUtil;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Random;

/**
 * XMPP连接类
 */
public class XmppConnectionManager {
    private static final String TAG = "zqconnnect";

    /* Handler */ // 这些值在监听状态的messageFragment还有用到，
    private static final int MSG_CONNECTING = 0;// 连接中...
    private static final int MSG_CONNECTED = 1;// 已连接
    private static final int MSG_AUTHENTICATED = 2;// 已认证
    private static final int MSG_CONNECTION_CLOSED = 3;// 连接关闭
    private static final int MSG_CONNECTION_CLOSED_ON_ERROR = 4;// 连接错误
    public static int mXMPPCurrentState;
    private Context mContext;
    private NotifyConnectionListener mNotifyConnectionListener;
    private XMPPTCPConnection mConnection;
    private XReconnectionManager mReconnectionManager;
    private XServerReceivedListener XServerReceivedListener;
    private boolean mIsNetWorkActive;// 当前网络是否连接上
    private boolean doLogining = false;
    private String mLoginUserId;  // 仅用于登陆失败，重新登陆用
    private LoginThread mLoginThread;
    private boolean isReturned;
    private AbstractConnectionListener mAbstractConnectionListener = new AbstractConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {

            OperLogUtil.e(TAG, "connected：已连接");
            mXMPPCurrentState = MSG_CONNECTED;
            if (mNotifyConnectionListener != null) {
                mNotifyConnectionListener.notifyConnected(connection);
            }
        }

        @Override
        public void authenticated(final XMPPConnection connection, boolean resumed) {
            OperLogUtil.e(TAG, "authenticated：认证成功");
            OperLogUtil.e(TAG, "resumed-->" + resumed);

            mXMPPCurrentState = MSG_AUTHENTICATED;
            if (mNotifyConnectionListener != null) {
                mNotifyConnectionListener.notifyAuthenticated(connection);
            }

            if (mConnection.isSmResumptionPossible()) {
                OperLogUtil.e(TAG, "服务端开启了流");
            } else {
                OperLogUtil.e(TAG, "服务端关闭了流");
                MyApplication.IS_OPEN_RECEIPT = true;// 检查服务器是否启用了流管理，如关闭本地请求回执标志位一定为true
            }
        }

        @Override
        public void connectionClosed() {
            OperLogUtil.e(TAG, "connectionClosed：连接关闭");
            mXMPPCurrentState = MSG_CONNECTION_CLOSED;
            if (mNotifyConnectionListener != null) {
                mNotifyConnectionListener.notifyConnectionClosed();
            }

            EventBus.getDefault().post(new MessageEventBG(false, true));
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            OperLogUtil.e(TAG, "connectionClosedOnError：连接异常");
            OperLogUtil.e(TAG, "connectionClosedOnError：" + e.getMessage());
            Reporter.post("xmpp connectionClosedOnError,", e);
            mXMPPCurrentState = MSG_CONNECTION_CLOSED_ON_ERROR;
            if (mNotifyConnectionListener != null) {
                mNotifyConnectionListener.notifyConnectionClosedOnError(e);
            }

            EventBus.getDefault().post(new MessageEventBG(false, true));

            if (TextUtils.equals(e.getMessage(), "Parser got END_DOCUMENT event. This could happen e.g. if the server closed the connection without sending a closing stream element")
                    || TextUtils.equals(e.getMessage(), "Broken pipe")) {
                // 开启流管理的情况下偶现该问题
                // 当message为END_DOCUMENT或Broken pipe时，正常的login以及reconnect都连接不上XMPP了，必须退出当前账号||退出程序 重进才可以，这里我们发送一个广播进行特殊的重连
                MyApplication.getInstance().sendBroadcast(new Intent(Constants.CLOSED_ON_ERROR_END_DOCUMENT));
            } else {
                // 切换网络之后，应该会立即回调到该方法内，网络改变监听才监听到网络改变，
                // 但偶现网络监听监听到网络改变，XMPP还是认证的情况(即监听先于该方法调用)，导致之后回调到该方法内没有去重新登录XMPP了
                // 所以当回调到这里的时候我们模拟发送一个网络改变的广播
                // 调试发现如果每次回调该方法之后都发送一个广播出去，重连速度会变得比较慢，所以我们只针对isReturned的情况发送
                if (isReturned) {
                    isReturned = false;
                    MyApplication.getInstance().sendBroadcast(new Intent(Constants.CLOSED_ON_ERROR_NORMAL));
                }
            }
        }
    };
    private BroadcastReceiver mNetWorkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            OperLogUtil.e(TAG, "监测到网络改变: " + action);
            boolean newNetworkActive = HttpUtil.isGprsOrWifiConnected(mContext);

            // 只有当网络状态真正改变时才处理
            if (newNetworkActive != mIsNetWorkActive) {
                mIsNetWorkActive = newNetworkActive;

                if (mIsNetWorkActive) {// 有网
                    OperLogUtil.e(TAG, "有网，检查XMPP连接状态");
                    // 无论是否已认证，都检查连接状态，确保连接有效
                    String userId = CoreManager.requireSelf(mContext).getUserId();
                    if (!TextUtils.isEmpty(userId)) {
                        // 如果连接已关闭或未认证，尝试重新登录
                        if (!isAuthenticated() || !mConnection.isConnected()) {
                            OperLogUtil.e(TAG, "XMPP未认证或连接已关闭，准备登录");
                            doLogining = true;
                            mLoginUserId = userId;
                            login(mLoginUserId);
                        } else {
                            OperLogUtil.e(TAG, "XMPP已认证且连接正常，无需重连");
                            isReturned = true;
                        }
                    }
                } else {// 无网
                    OperLogUtil.e(TAG, "无网，停止重连");
                    if (mLoginThread != null && mLoginThread.isAlive()) {
                        OperLogUtil.e(TAG, "无网且登录线程isAlive,打断该线程");
                        mLoginThread.interrupt();
                    }
                    logout();
                }
            }
        }
    };

    public XmppConnectionManager(Context context, NotifyConnectionListener listener) {
        mContext = context;
        mNotifyConnectionListener = listener;

        mConnection = new XMPPTCPConnection(getConnectionConfiguration());
        mConnection.addConnectionListener(mAbstractConnectionListener);

        initNetWorkStatusReceiver();
        mReconnectionManager = new XReconnectionManager(mContext, mConnection);
        // 流管理启用生效
        XServerReceivedListener = new XServerReceivedListener();
        mConnection.addStanzaAcknowledgedListener(XServerReceivedListener);
    }

    private XMPPTCPConnectionConfiguration getConnectionConfiguration() {
        final String mXmppHost = CoreManager.requireConfig(MyApplication.getInstance()).XMPPHost;
        int mXmppPort = CoreManager.requireConfig(MyApplication.getInstance()).mXMPPPort;
        String mXmppDomain = CoreManager.requireConfig(MyApplication.getInstance()).XMPPDomain;

        DomainBareJid mDomainBareJid = null;
        try {
            mDomainBareJid = JidCreate.domainBareFrom(mXmppDomain);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
                .setHost(mXmppHost) // 直接使用主机名，让Smack内部处理DNS解析
                .setPort(mXmppPort) // 服务器端口
                .setXmppDomain(mDomainBareJid)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.ifpossible) // 是否开启安全模式
                .setCompressionEnabled(true)
                .setSendPresence(false)
                .setConnectTimeout(10000) // 设置10秒连接超时
                // .setSocketTimeout(10000) // 设置10秒Socket超时
                .setDnssecMode(ConnectionConfiguration.DnssecMode.disabled); // 禁用DNSSEC，加快解析速度
//        if (OperLogUtil.isLoggable("SMACK", OperLogUtil.DEBUG)) {
//            // 为方便测试，留个启用方法，命令运行"adb shell setprop OperLogUtil.tag.SMACK D"启用，
//            builder.enableDefaultDebugger();
//        }
        // 如果本地有用户信息，取出来放进config里用于避免自动重连时崩溃，
        // 自动重连时connection中如果没有username就会从config中拿，还是优先connection中的参数，
        User self = CoreManager.getSelf(mContext);
        if (self != null) {
            builder.setUsernameAndPassword(self.getUserId(), UserSp.getInstance(mContext).getAccessToken());
        }
        Resourcepart mResourcepart;
        if (MyApplication.IS_SUPPORT_MULTI_LOGIN) {
            mResourcepart = Resourcepart.fromOrThrowUnchecked("android");
        } else {
            mResourcepart = Resourcepart.fromOrThrowUnchecked("youjob");
        }
        builder.setResource(mResourcepart);
        return builder.build();
    }

    public XMPPTCPConnection getConnection() {
        return mConnection;
    }

    public boolean isAuthenticated() {
        return mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated();
    }

    private boolean isLoginAllowed() {
        return doLogining && mIsNetWorkActive && (!mConnection.isConnected() || !mConnection.isAuthenticated());
    }

    private boolean isGprsOrWifiConnected() {
        boolean isConnected = HttpUtil.isGprsOrWifiConnected(mContext);
        if (!isConnected) {
            logout();
        } else {
            if (!TextUtils.isEmpty(CoreManager.requireSelf(mContext).getUserId())
                    && !TextUtils.isEmpty(CoreManager.requireSelf(mContext).getPassword()))
                login(CoreManager.requireSelf(mContext).getUserId());
        }
        return isConnected;
    }

    /*********************
     * 网络连接状态
     ***************/
    private void initNetWorkStatusReceiver() {
        // 获取程序启动时的网络状态
        mIsNetWorkActive = isGprsOrWifiConnected();
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Constants.CLOSED_ON_ERROR_NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.registerReceiver(mNetWorkChangeReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }
    }

    public synchronized void login(final String userId) {
        // 检查用户ID和密码是否有效
        String password = UserSp.getInstance(mContext).getAccessToken();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(password)) {
            OperLogUtil.e(TAG, "用户ID或密码为空，无法登录");
            return;
        }

        // 检查是否已经认证且用户匹配
        if (mConnection.isAuthenticated()) {
            if (XmppStringUtil.parseName(mConnection.getUser().toString()).equals(userId)) {
                OperLogUtil.e(TAG, "已经登录且用户匹配，无需重复登录");
                return;
            } else {
                // 用户不匹配，断开连接重新登录
                OperLogUtil.e(TAG, "已经登录但用户不匹配，断开连接重新登录");
                try {
                    mConnection.disconnect();
                } catch (Exception e) {
                    OperLogUtil.e(TAG, "断开连接异常: " + e.getMessage());
                }
            }
        }

        if (mLoginThread != null && mLoginThread.isAlive()) {
            // 正在进行上一个用户的登陆中
            if (mLoginThread.isSameUser(userId, password)) {
                if (mLoginThread.getAttempts() > 13) {
                    OperLogUtil.e(TAG, "当前正在登录，但尝试次数过大，结束当前线程并开始新的登录尝试");
                    // 当尝试次数大于13的时候，尝试的时间变得太长，果断结束，开始一次新的尝试
                    mLoginThread.interrupt();
                    doLogining = false;
                } else {
                    OperLogUtil.e(TAG, "当前正在登录同一用户，重复调用login方法，直接返回" + "attempts--->" + mLoginThread.getAttempts());
                    return;
                }
            } else {
                // 正在登录不同用户，结束当前登录线程
                OperLogUtil.e(TAG, "当前正在登录不同用户，结束当前登录线程");
                mLoginThread.interrupt();
                doLogining = false;
                // 等待线程结束，但设置超时
                long time = System.currentTimeMillis();
                while (mLoginThread != null && mLoginThread.isAlive()) {
                    if (System.currentTimeMillis() - time > 2000) {
                        break;
                    }
                }
            }
        }

        // 开始新的登录尝试
        doLogining = true;
        mLoginUserId = userId;
        OperLogUtil.e(TAG, "开始新的XMPP登录尝试，用户ID: " + userId);

        mLoginThread = new LoginThread(userId, password);
        mLoginThread.start();
    }

    void logout() {
        doLogining = false;
        if (mLoginThread != null && mLoginThread.isAlive()) {
            mLoginThread.interrupt();
        }
        if (mReconnectionManager != null) {
            mReconnectionManager.release();
        }
        if (mConnection == null) {
            return;
        }

        presenceOffline();

        if (mConnection.isConnected()) {
            OperLogUtil.e("zq", "断开连接" + 3);
            mConnection.disconnect();
        }
    }

    void release() {
        mContext.unregisterReceiver(mNetWorkChangeReceiver);
        doLogining = false;
        if (mLoginThread != null && mLoginThread.isAlive()) {
            mLoginThread.interrupt();
        }
        mReconnectionManager.release();

        presenceOffline();

        if (mConnection != null && mConnection.isConnected()) {
            OperLogUtil.e("zq", "断开连接" + 4);
            mConnection.disconnect();
        }
    }

    void sendOnLineMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                presenceOnline();
            }
        }).start();
    }

    private void presenceOnline() {
        Presence presence = new Presence(Presence.Type.available);
        try {
            try {
                mConnection.sendStanza(presence);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void presenceOffline() {
        Presence presence = new Presence(Presence.Type.unavailable);
        try {
            try {
                mConnection.sendStanza(presence);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录 xmpp 线程
     */
    private class LoginThread extends Thread {
        private String loginUserId;
        private String loginPassword;
        private int attempts;
        private int randomBase = new Random().nextInt(11) + 5; // between 5 and 15seconds
        private int connectionTimeInterval = 9;

        LoginThread(String loginUserId, String loginPassword) {
            this.loginUserId = loginUserId;
            this.loginPassword = loginPassword;
            this.setName("Xmpp Login Thread" + loginUserId);
        }

        public boolean isSameUser(String userId, String password) {
            if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(password)) {
                return false;
            }
            return loginUserId.equals(userId) && loginPassword.equals(password);
        }

        public int getAttempts() {
            return attempts;
        }

        /**
         * Returns the number of seconds until the next reconnection attempt.
         *
         * @return the number of seconds until the next reconnection attempt.
         */
        private int timeDelay() {
            attempts++;
            if (attempts > 13) {
                return randomBase * 6 * 5; // between 2.5 and 7.5 minutes
            }
            if (attempts > 7) {
                return randomBase * 6; // between 30 and 90 seconds (~1 minutes)
            }
            return randomBase; // 10 seconds
        }

        /**
         * timeDelay : xmpp connection failed,
         * if attempts <= 7, connection interval is 10 seconds,
         * attempts > 7,interval is 60 seconds,
         * attempts > 13,interval is 5 minutes
         * <p>
         * <p>
         * <p>
         * timeDelay2: xmpp connection failed,
         * if attempts > 21,interval is always 30 seconds,
         * else interval is 9 seconds + attempts
         *
         * @return the number of seconds until the next reconnection attempt.
         */
        private int timeDelay2() {
            attempts++;
            if (connectionTimeInterval >= 30) {
                return connectionTimeInterval;
            }
            return connectionTimeInterval + attempts;
        }

        public void run() {
            while (isLoginAllowed() && mIsNetWorkActive) {
                mXMPPCurrentState = MSG_CONNECTING;
                if (mNotifyConnectionListener != null) {
                    mNotifyConnectionListener.notifyConnecting();
                }
                try {
                    // 确保连接已断开，避免重复连接
                    if (mConnection.isConnected()) {
                        try {
                            mConnection.disconnect();
                        } catch (Exception e) {
                            OperLogUtil.e(TAG, "断开旧连接异常: " + e.getMessage());
                        }
                    }

                    // 重新连接
                    try {
                        mConnection.connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        OperLogUtil.e(TAG, "连接被中断，重试中...");
                        continue;
                    }

                    // 登录XMPP
                    Resourcepart mResourcepart;
                    if (MyApplication.IS_SUPPORT_MULTI_LOGIN) {
                        mResourcepart = Resourcepart.fromOrThrowUnchecked(MyApplication.MULTI_RESOURCE);
                    } else {
                        mResourcepart = Resourcepart.fromOrThrowUnchecked("youjob");
                    }

                    try {
                        mConnection.login(loginUserId, loginPassword, mResourcepart);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        OperLogUtil.e(TAG, "登录被中断，重试中...");
                        continue;
                    }

                    if (mConnection.isAuthenticated()) {// 登录成功 已验证
                        PingManager.getInstanceFor(mConnection).setPingInterval(CoreManager.requireConfig(MyApplication.getInstance()).xmppPingTime);
                        PingManager.getInstanceFor(mConnection).registerPingFailedListener(new PingFailedListener() {
                            @Override
                            public void pingFailed() {
                                OperLogUtil.e(TAG, "ping 失败了");
                                mAbstractConnectionListener.connectionClosed();
                                MyApplication.getInstance().sendBroadcast(new Intent(Constants.PING_FAILED));
                                // 重新启动登录线程
                                if (mIsNetWorkActive) {
                                    String userId = CoreManager.requireSelf(mContext).getUserId();
                                    if (!TextUtils.isEmpty(userId)) {
                                        doLogining = true;
                                        mLoginUserId = userId;
                                        login(mLoginUserId);
                                    }
                                }
                            }
                        });
                        OperLogUtil.e(TAG, "登录成功，设置连接活跃状态");
                    } else {
                        OperLogUtil.e("zq", "登录失败，断开连接");
                        mConnection.disconnect();
                    }
                } catch (SmackException | IOException e) {
                    // 连接或IO异常，继续重试
                    e.printStackTrace();
                    OperLogUtil.e(TAG, "连接异常，重试中...: " + e.getMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();
                    OperLogUtil.e(TAG, "XMPP异常: " + e.getMessage());
                    if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("not-authorized")) {
                        // 认证失败，无需重试
                        MyApplication.getInstance().sendBroadcast(new Intent(Constants.NOT_AUTHORIZED));
                        return;
                    }
                    // 其他XMPP异常，继续重试
                } catch (Exception e) {
                    // 捕获所有其他异常，避免登录线程崩溃
                    e.printStackTrace();
                    OperLogUtil.e(TAG, "登录过程中发生未知异常: " + e.getMessage());
                }

                if (mConnection.isAuthenticated()) {
                    if (!XmppStringUtil.parseName(mConnection.getUser().toString()).equals(loginUserId)) {
                        OperLogUtil.e("zq", "用户不匹配，断开连接");
                        mConnection.disconnect();
                    } else {
                        OperLogUtil.e(TAG, "登录成功，退出登录线程");
                        doLogining = false;
                        break;
                    }
                } else {
                    // 等待重试
                    int remainingSeconds = timeDelay2();
                    OperLogUtil.e(TAG, "登录失败，等待" + remainingSeconds + "秒后重试");

                    // 检查网络状态
                    if (!HttpUtil.isGprsOrWifiConnected(mContext)) {
                        OperLogUtil.e(TAG, "网络已断开，退出登录线程");
                        mIsNetWorkActive = false;
                        logout();
                        break;
                    }

                    // 等待重试
                    while (isLoginAllowed() && mIsNetWorkActive && remainingSeconds > 0) {
                        try {
                            Thread.sleep(1000);
                            remainingSeconds--;
                        }
                        catch (InterruptedException e1) {
                            e1.printStackTrace();
                            OperLogUtil.e(TAG, "重试等待被中断，退出登录线程");
                            return;
                        }
                    }
                }
            }
        }
    }

//    public class SASLPlainMechanism extends SASLMechanism {
//
//        public static final String NAME = PLAIN;
//
//
//
//        @Override
//        protected void authenticateInternal(CallbackHandler cbh) throws SmackException.SmackSaslException {
//            throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
//        }
//
//        @Override
//        protected byte[] getAuthenticationText() {
//            // concatenate and encode username (authcid) and password
//            String authzid;
//            if (authorizationId == null) {
//                authzid = "";
//            } else {
//                authzid = authorizationId.toString();
//            }
//            byte[] authcid = toBytes(authzid + '\u0000' + authenticationId);
//            byte[] passw = toBytes('\u0000' + password);
//
//            return null;//ByteUtils.concat(authcid, passw);
//        }
//
//        @Override
//        public String getName() {
//            return NAME;
//        }
//
//        @Override
//        public int getPriority() {
//            return 411;
//        }
//
//        @Override
//        public SASLPlainMechanism newInstance() {
//            return new SASLPlainMechanism();
//        }
//
//        @Override
//        public void checkIfSuccessfulOrThrow() {       // No check performed
//        }
//
//        @Override
//        public boolean authzidSupported() {
//            return true;
//        }
//    }

}
