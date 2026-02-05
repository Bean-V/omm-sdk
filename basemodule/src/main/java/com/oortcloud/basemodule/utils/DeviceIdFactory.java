package com.oortcloud.basemodule.utils;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oortcloud.basemodule.CommonApplication;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @filename:
 * @author: zhangzhijun/@date: 2020/12/13 15:38
 * @version： v1.0
 * @function：
 */
public class DeviceIdFactory {

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static volatile UUID uuid;
    private static volatile DeviceIdFactory mInstance;

    private DeviceIdFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceIdFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context
                            .getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = CommonApplication.GUID;

                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    String serial = null;
                                    try {
                                        serial = Build.class.getField("SERIAL").get(null).toString();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    }
                                    String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

                                    uuid = new UUID(m_szDevIDShort.hashCode(), serial.hashCode());

                                } else {
                                    final String deviceId = ((TelephonyManager)
                                            context.getSystemService(
                                                    Context.TELEPHONY_SERVICE)).getDeviceId();
                                    uuid = deviceId != null ? UUID
                                            .nameUUIDFromBytes(deviceId
                                                    .getBytes("utf8")) : UUID
                                            .randomUUID();
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit()
                                .putString(PREFS_DEVICE_ID, uuid.toString())
                                .commit();
                    }
                }
            }
        }
    }

    public static DeviceIdFactory getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DeviceIdFactory.class) {
                if (mInstance == null) {
                    mInstance = new DeviceIdFactory(context);
                }
            }
        }
        return mInstance;
    }


    public String  getDeviceUuid() {
        Log.v("msg","getDeviceUuid "+uuid.toString());
        return uuid.toString();
    }


    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
   // private static UUID uuid; // 存储生成的 UUID


    private static final int TARGET_LENGTH = 32;
    public static String processDeviceId(Context context) {


        String id = getDeviceUniqueId(context);
        if (id == null) return null;

        // 裁剪超过32位的部分
        if (id.length() > TARGET_LENGTH) {
            return id.substring(0, TARGET_LENGTH);
        }

        // 不足32位补零
        if (id.length() < TARGET_LENGTH) {
            StringBuilder sb = new StringBuilder(id);
            while (sb.length() < TARGET_LENGTH) {
                sb.append('0'); // 在末尾补零
            }
            return sb.toString();
        }

        // 刚好32位直接返回
        return id;
    }
    // 获取设备唯一标识（优先使用序列号，其次 MAC，最后 Android ID）
    public static String getDeviceUniqueId(Context context) {
        String deviceId = "";

        // 1. 尝试获取设备序列号（需要 READ_PHONE_STATE 权限）
        if (hasReadPhoneStatePermission(context)) {
            deviceId = getSerialNumber();
        }

        // 2. 如果序列号无效，尝试获取 MAC 地址
        if (deviceId == null || deviceId.isEmpty() || deviceId.equals("unknown")) {
            deviceId = getMacAddress();
        }

        // 3. 如果 MAC 地址无效，尝试获取 Android ID
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = getAndroidId(context);
        }

        // 4. 如果所有方法都失败，生成一个随机 UUID 并存储
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = getOrCreateUUID(context);
        }

        Log.v("DeviceUtils", "Device Unique ID: " + deviceId);
        return deviceId;
    }

    // 获取设备序列号（内部方法，需要权限）
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                // Android 8.0+
                serial = Build.SERIAL;
            } else {
                // Android 8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            Log.e("DeviceUtils", "获取设备序列号失败: " + e.getMessage());
        }
        return serial;
    }
    public static String getDeviceIdentifier(Context context) {
        // 1. 尝试获取 Android ID
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        // 2. 尝试获取序列号（仅低版本有效）
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                serial = Build.SERIAL; // 8.0以下可能有效
            }
        } catch (Exception ignored) {}

        // 3. 生成 UUID 作为最终兜底
        if (TextUtils.isEmpty(androidId) && TextUtils.isEmpty(serial)) {
            SharedPreferences prefs = context.getSharedPreferences("device_id", MODE_PRIVATE);
            String uuid = prefs.getString("uuid", null);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                prefs.edit().putString("uuid", uuid).apply();
            }
            return uuid;
        }

        return !TextUtils.isEmpty(androidId) ? androidId : serial;
    }

    // 获取 MAC 地址
    private static String getMacAddress() {
        String mac = "";
        try {
            // 注意：从 Android 6.0+ 开始，通过传统方法获取的 MAC 地址会返回固定值 02:00:00:00:00:00
            // 这里提供一个兼容性实现，但可能无法获取真实 MAC
            java.net.NetworkInterface networkInterface = java.net.NetworkInterface.getByName("wlan0");
            if (networkInterface != null) {
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes != null) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02X:", b));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    mac = sb.toString();
                }
            }
        } catch (Exception e) {
            Log.e("DeviceUtils", "获取 MAC 地址失败: " + e.getMessage());
        }
        return mac;
    }

    // 获取 Android ID
    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.e("DeviceUtils", "获取 Android ID 失败: " + e.getMessage());
            return "";
        }
    }

    // 获取或创建 UUID
    private static String getOrCreateUUID(Context context) {
        if (uuid == null) {
            // 从本地存储读取（如果有）
            String storedUuid = getStoredUUID(context);
            if (storedUuid != null && !storedUuid.isEmpty()) {
                uuid = UUID.fromString(storedUuid);
            } else {
                // 生成新的 UUID 并存储
                uuid = UUID.randomUUID();
                saveUUID(context, uuid.toString());
            }
        }
        return uuid.toString();
    }

    // 存储 UUID 到 SharedPreferences
    private static void saveUUID(Context context, String uuid) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("device_info", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("uuid", uuid);
            editor.apply();
        } catch (Exception e) {
            Log.e("DeviceUtils", "保存 UUID 失败: " + e.getMessage());
        }
    }

    // 从 SharedPreferences 读取 UUID
    private static String getStoredUUID(Context context) {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("device_info", Context.MODE_PRIVATE);
            return prefs.getString("uuid", null);
        } catch (Exception e) {
            Log.e("DeviceUtils", "读取 UUID 失败: " + e.getMessage());
            return null;
        }
    }

    // 检查是否有 READ_PHONE_STATE 权限
    private static boolean hasReadPhoneStatePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }



}
