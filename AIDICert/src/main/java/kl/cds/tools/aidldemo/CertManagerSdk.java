package kl.cds.tools.aidldemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.oortcloud.basemodule.utils.OperLogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import kl.cds.constant.CertInfoType;
import kl.cds.utils.Settings;
import koal.cert.tools.ICertManager;
import koal.cert.tools.ResultBean;

/**
 * 证书管理SDK的入口类，提供服务绑定、功能启动等核心功能
 */
public class CertManagerSdk {
    private static final String TAG = "CertManagerSdk";
    private static CertManagerSdk instance;

    private Context context;
    private ICertManager certManager = null;
    private ServiceConnection connCertManager;
    private SdkListener listener;
    private boolean isServiceConnected = false;


    // 新增：维护登录状态
    private boolean isLogin = false;
    private String certPath = null; // 当前证书路径
    // 私有构造函数，实现单例模式
    private CertManagerSdk(Context context) {
        this.context = context.getApplicationContext();
        initServiceConnection();
    }

    // 获取SDK实例
    public static synchronized CertManagerSdk getInstance(Context context) {
        if (instance == null) {
            instance = new CertManagerSdk(context);
        }
        return instance;
    }

    // 初始化SDK
    public void init(SdkListener listener) {
        this.listener = listener;
        setupRemoteService();
    }

    // 设置SVS服务器配置
    public void setSvsConfig(String host, String port, String pin) {
        Settings.SVS_SERVER_HOST = host;
        Settings.SVS_SERVER_PORT = port;
        Settings.PIN = pin;
    }

    // 启动证书管理功能
    public void startCertManager(Activity activity) {
        if (isServiceConnected && certManager != null) {
            Intent intent = new Intent(activity, CertManagerActivity.class);
            activity.startActivity(intent);
        } else {
            if (listener != null) {
                listener.onServiceNotConnected();
            }
        }
    }

    // 启动SVS签名验签功能
    public void startSvsSignVerify(Activity activity) {
        if (isServiceConnected && certManager != null) {
            Intent intent = new Intent(activity, CertManagerSVSActivity.class);
            activity.startActivity(intent);
        } else {
            if (listener != null) {
                listener.onServiceNotConnected();
            }
        }
    }

    // 启动小工具功能
    public void startLittleTools(Activity activity) {
        Intent intent = new Intent(activity, LittleToolsActivity.class);
        activity.startActivity(intent);
    }

    // 检查远程应用是否安装
    public boolean isRemoteAppInstalled() {
        try {
            context.getPackageManager().getPackageInfo(Settings.REMOTE_APP_PKG_NAME, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 获取远程应用信息
    public String getRemoteAppInfo() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(Settings.REMOTE_APP_PKG_NAME, 0);
            return String.format("移动证书助手： 已安装\n版本名称： %s\n版本号： %d",
                    packageInfo.versionName, packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return "移动证书助手：未安装";
        }
    }

    // 获取证书管理器接口
    public ICertManager getCertManager() {
        return certManager;
    }

    // 释放资源
    public void release() {
        if (context != null && connCertManager != null) {
            try {
                context.unbindService(connCertManager);
                Log.d(TAG, "Service unbound");
            } catch (Exception e) {
                Log.e(TAG, "Error unbinding service", e);
            }
            certManager = null;
            isServiceConnected = false;
        }
    }

    // 初始化服务连接
    private void initServiceConnection() {
        connCertManager = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "ICertManager onServiceConnected");

                try {
                    service.linkToDeath(mCertManagerDeathRecipient, 0);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error linking to death recipient", e);
                }

                certManager = ICertManager.Stub.asInterface(service);
                isServiceConnected = true;

                if (listener != null) {
                    listener.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "ICertManager onServiceDisconnected");
                certManager = null;
                isServiceConnected = false;

                if (listener != null) {
                    listener.onServiceDisconnected();
                }
            }
        };
    }

    // 设置远程服务
    private void setupRemoteService() {
        if (isRemoteAppInstalled()) {
            setupCertManagerService();
        } else {
            if (listener != null) {
                listener.onRemoteAppNotInstalled();
            }
        }
    }

    // 设置证书管理服务
    private void setupCertManagerService() {
        Intent intent = new Intent();
        intent.setAction("koal.cert.tools.CertManagerService");
        intent.setPackage(Settings.REMOTE_APP_PKG_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        context.bindService(intent, connCertManager, Context.BIND_AUTO_CREATE);
    }

    // 服务死亡回调
    private IBinder.DeathRecipient mCertManagerDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "mCertManagerDeathRecipient.binderDied");

            certManager = null;
            isServiceConnected = false;

            if (listener != null) {
                listener.onServiceDied();
            }

            // 重新绑定远程服务
            setupCertManagerService();
        }
    };

    /**
     * 登录证书管理服务
     * @param certIndex 证书索引（对应 Settings.CERT_INDEX）
     * @param pin 证书PIN码
     */
    public void login(int certIndex, String pin) {
        if (!isServiceConnected || certManager == null) {
            if (listener != null) {
                listener.onServiceNotConnected();
            }
            return;
        }

        try {
            // 获取证书列表，选择指定索引的证书路径
            List<String> certList = certManager.SOF_GetUserList();
            if (certIndex < 0 || certIndex >= certList.size()) {
                if (listener != null) {
                    listener.onLoginFailed("证书索引无效");
                }
                return;
            }
            certPath = certList.get(certIndex);

            // 调用登录接口
            ResultBean resultBean = certManager.SOF_Login(certPath, pin);
            if (resultBean.getErrorCode() == resultBean.OPER_SUC) {
                isLogin = true;
                if (listener != null) {
                    listener.onLoginSuccess();
                }
            } else {
                isLogin = false;
                if (listener != null) {
                    listener.onLoginFailed("登录失败：" + resultBean.getDetail() + " " + resultBean.getMessage());
                }
            }
        } catch (RemoteException e) {
            isLogin = false;
            if (listener != null) {
                listener.onLoginFailed("登录异常：" + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    /**
     * 从证书信息中提取身份证号（需先登录）
     * 逻辑移植自 CertManagerActivity 的 getSFZHFromCertInfo 方法
     */
    public void obtainIdCardFromCert() {
        if (!isServiceConnected || certManager == null) {
            if (listener != null) {
                listener.onServiceNotConnected();
            }
            return;
        }
        if (!isLogin || TextUtils.isEmpty(certPath)) {
            if (listener != null) {
                listener.onIdCardObtainFailed("请先登录证书");
            }
            return;
        }

        new Thread(() -> { // 耗时操作放在子线程
            try {
                // 导出当前登录证书的信息
                String b64Cert = certManager.SOF_ExportUserCert(certPath);
                if (TextUtils.isEmpty(b64Cert)) {
                    if (listener != null) {
                        listener.onIdCardObtainFailed("获取证书信息失败");
                    }
                    return;
                }

                // 反射获取 CertInfoType 中 "SGD_CERT_SUBJECT_CN" 对应的字段值（证书主题CN字段）
                Field cnField = null;
                Field[] fields = CertInfoType.class.getFields();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    OperLogUtil.msg(field.getName() + mod);
                    if (Modifier.isPublic(mod) && Modifier.isStatic(mod) || Modifier.isFinal(mod)
                            ) {
                        if("SGD_CERT_SUBJECT_CN".equals(field.getName())) {
                            cnField = field;
                            break;
                        }
                    }
                }

                if (cnField == null) {
                    if (listener != null) {
                        listener.onIdCardObtainFailed("未找到证书主题字段");
                    }
                    return;
                }

                // 调用接口获取证书主题信息（格式可能为 "姓名 身份证号"）
                int cnFieldValue = (int) cnField.get(null);
                String subjectInfo = certManager.SOF_GetCertInfo(b64Cert, cnFieldValue);
                if (TextUtils.isEmpty(subjectInfo)) {
                    if (listener != null) {
                        listener.onIdCardObtainFailed("证书主题信息为空");
                    }
                    return;
                }

                // 提取身份证号（假设格式为 "姓名 18位身份证号"，取最后一部分）
                String[] parts = subjectInfo.split(" ");
                String idCard = parts.length > 0 ? parts[parts.length - 1] : "";

                // 校验身份证号格式（简单校验18位）
                if (idCard.length() == 18 && idCard.matches("^[0-9Xx]+$")) {
                    if (listener != null) {
                        listener.onIdCardObtained(idCard);
                    }
                } else {
                    if (listener != null) {
                        listener.onIdCardObtainFailed("身份证号格式异常：" + subjectInfo);
                    }
                }

            } catch (Exception e) {
                if (listener != null) {
                    listener.onIdCardObtainFailed("提取身份证号异常：" + e.getMessage() + Arrays.toString(e.getStackTrace()));
                }
                e.printStackTrace();
            }
        }).start();
    }

    // SDK监听器接口
    public interface SdkListener {
        void onServiceConnected();
        void onServiceDisconnected();
        void onServiceDied();
        void onRemoteAppNotInstalled();
        void onServiceNotConnected();

        // 新增：登录结果回调
        void onLoginSuccess();              // 登录成功
        void onLoginFailed(String errorMsg); // 登录失败

        // 新增：身份证号获取结果回调
        void onIdCardObtained(String idCard); // 成功获取身份证号
        void onIdCardObtainFailed(String errorMsg); // 获取失败
    }
}
