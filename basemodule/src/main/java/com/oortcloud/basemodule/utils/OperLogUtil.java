

package com.oortcloud.basemodule.utils;

import static com.oortcloud.basemodule.constant.Constant.Aks_Log_Report;
import static com.oortcloud.basemodule.constant.Constant.Aks_Log_Report_Setting;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oortcloud.basemodule.CommonApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 将日志写入文件内
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public final class OperLogUtil {
    private static final int CACHE_QUEUE_SIZE = 1; //缓存最多10条log信息后输出到文件
    @SuppressLint("NewApi")
    private static final SimpleDateFormat LOG_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX");// new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private static final String PREFIX = "";
    private static ExecutorService sLogExecutor = Executors.newSingleThreadExecutor();
    private static boolean sLogEnable = true;
    private static LogLevel sLogLevel = LogLevel.DEBUG;
    private static Queue<String> sMsgQueue = new ArrayBlockingQueue<>(CACHE_QUEUE_SIZE);


    private static ArrayList<String> sMsgArr = new ArrayList<>();

    private static LogFileManager sLogFileManager;

    private static long lastReportDate = new Date().getTime()  / 1000;

    public static int mPostionX = 0;

    public static int mPostionY = 0;

    public static double mLoationLat = 0;

    public static double mLoationLng = 0;

    public static String userId = "";

    public static String userName = "";

    public static int  timeval = 15;

    public static int record_count = 50;

    public static int open = 1;

    public static boolean hasGetSeting = false;

    public static String sfzh = "";
    public static String token = "";
    private static ArrayList tempArr = new ArrayList();


    /**
     * 设置Log开关
     *
     * @param enable 开关项(默认为开).
     */
    public static void setEnable(boolean enable) {
        sLogEnable = enable;
    }

    public static void setLogLevel(LogLevel level) {
        sLogLevel = level;
    }

    /**
     * 设置写入log的文件夹
     *
     * @param dirPath 文件夹地址
     */
    public static void setLogDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new InvalidParameterException();
        }
        sLogFileManager = new LogFileManager(CommonApplication.getAppContext(),dirPath);
    }

    /**
     * 程序退出时调用该方法
     *
     * @param
     */
    public static void close() {
        if (sLogFileManager != null) {
            sLogExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    flushLogToFile();
                }
            });
        }
    }

    /**
     * log for debug
     *
     * @param message log message
     * @param tag     tag
     * @see Log#d(String, String)
     */
    public static void d(String tag, String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.d(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.DEBUG);
        }
    }


    public static void msg(String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.d("操作日志", msg);

            writeToFileIfNeeded("", msg, LogLevel.DEBUG);

//            if(token != null) {
//                //if (token.length() > 0) {
//                    writeToFileIfNeeded("", msg, LogLevel.DEBUG);
//               // }
//            }
        }
    }





    /**
     * log for debug
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#d(String, String, Throwable)
     */
    public static void d(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.d(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.DEBUG);
        }
    }

    /**
     * log for debug
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#d(String, String)
     */
    public static void d(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(PREFIX + format, params);
            Log.d(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.DEBUG);
        }
    }

    /**
     * log for warning
     *
     * @param message log message
     * @param tag     tag
     * @see Log#w(String, String)
     */
    public static void w(String tag, String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.w(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param tag       tag
     * @param throwable throwable
     * @see Log#w(String, Throwable)
     */
    public static void w(String tag, Throwable throwable) {
        if (sLogEnable) {
            Log.w(tag, throwable);
            writeToFileIfNeeded(tag, Log.getStackTraceString(throwable), LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#w(String, String, Throwable)
     */
    public static void w(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.w(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.WARN);
        }
    }

    /**
     * log for warning
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#w(String, String)
     */
    public static void w(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(PREFIX + format, params);
            Log.w(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.WARN);
        }
    }

    /**
     * log for error
     *
     * @param message message
     * @param tag     tag
     * @see Log#i(String, String)
     */
    public static void e(String tag, String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.e(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.ERROR);
        }
    }

    /**
     * log for error
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#i(String, String, Throwable)
     */
    public static void e(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.e(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.ERROR);
        }
    }

    /**
     * log for error
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#e(String, String)
     */
    public static void e(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(PREFIX + format, params);
            Log.e(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.ERROR);
        }
    }

    /**
     * log for information
     *
     * @param message message
     * @param tag     tag
     * @see Log#i(String, String)
     */
    public static void i(String tag, String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.i(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.INFO);
        }
    }

    /**
     * log for information
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#i(String, String, Throwable)
     */
    public static void i(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = message;
            Log.i(tag, PREFIX + msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.INFO);
        }
    }

    /**
     * log for information
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#i(String, String)
     */
    public static void i(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(PREFIX + format, params);
            Log.i(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.INFO);
        }
    }

    /**
     * log for verbos
     *
     * @param message log message
     * @param tag     tag
     * @see Log#v(String, String)
     */
    public static void v(String tag, String message) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.v(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.VERBOSE);
        }
    }

    /**
     * log for verbose
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log#v(String, String, Throwable)
     */
    public static void v(String tag, String message, Throwable throwable) {
        if (sLogEnable) {
            String msg = PREFIX + message;
            Log.v(tag, msg, throwable);
            writeToFileIfNeeded(tag, msg + "\n" + Log.getStackTraceString(throwable), LogLevel.VERBOSE);
        }
    }

    /**
     * log for verbose
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log#v(String, String)
     */
    public static void v(String tag, String format, Object... params) {
        if (sLogEnable) {
            String msg = String.format(PREFIX + format, params);
            Log.v(tag, msg);
            writeToFileIfNeeded(tag, msg, LogLevel.VERBOSE);
        }
    }

    private static void writeToFileIfNeeded(final String tag, final String msg, LogLevel logLevel) {
        if (logLevel.getValue() < sLogLevel.getValue() || sLogFileManager == null) {
            return;
        }
        sLogExecutor.execute(new Runnable() {
            @Override
            public void run() {
                appendLog(tag, msg);
            }
        });
    }

    private static void appendLog(String tag, String msg) {
        String logMsg =  "\n" +  LOG_DATE_TIME_FORMAT.format(new Date()) + ":" + msg;//formatLog(tag, msg);
        sMsgQueue.add(logMsg);
        sMsgArr.add(logMsg);
        // 到达缓存上限，写到文件中
        if (sMsgQueue.size() >= CACHE_QUEUE_SIZE) {
            flushLogToFile();

        }


//        long current  = new Date().getTime() / 1000;
//        if(sMsgArr.size() > record_count || ((current - lastReportDate > timeval) && sMsgArr.size() > 0 )){//sLogFileManager.isNeedToSend()
//
//
//            try {
//                // sLogFileManager.getLogContent()
//
//                if(tempArr.size() > 0 || open == 0){
//                    return;
//                }
//                JSONArray tempList = new JSONArray();
//
//                com.alibaba.fastjson.JSONArray jsonArr = new com.alibaba.fastjson.JSONArray();
//                for(String s : sMsgArr){
//                    tempArr.add(s);
//                    jsonArr.add(JSON.parse(s));
//                }
//
//                //jsonArr.add(JSON.parse(s));
//                reprot(jsonArr.toJSONString(), new ReportRes() {
//                    @Override
//                    public void callback(int code) {
//                        //sLogFileManager.createNewLogFile();
//
//                        if(code == 200) {
//                            sMsgArr.removeAll(tempArr);
//                            tempArr.clear();
//                            lastReportDate = current;
//                        }else{
//                            tempArr.clear();
//                            lastReportDate = current;
//                        }
//
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        if(!hasGetSeting){
//            try {
//                getSeting();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }




    private static void flushLogToFile() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String message : sMsgQueue) {
            stringBuilder.append(message);
        }

        try {
            sLogFileManager.writeLogToFile(stringBuilder.toString());
            sMsgQueue.clear();
        }catch (Exception e){

        }

    }

    private static String formatLog(String tag, String msg) {

        HashMap map = collectDeviceInfo(); //new HashMap();


        map.put("mPositionX",mPostionX);
        map.put("mPositionY",mPostionY);

        Location loc = DeviceGPSUtils.getLocation();

        if(loc != null) {
            mLoationLat = loc.getLatitude();
            mLoationLng = loc.getLongitude();
        }

        map.put("mLoationLat",mLoationLat);
        map.put("mLoationLng",mLoationLng);
        map.put("clientIp",getIp());

        map.put("userId",userId);
        map.put("userName",userName);
        //

        map.put("@timestamp",LOG_DATE_TIME_FORMAT.format(new Date()));
        map.put("destination.id","123");
        map.put("destination.ip","http://20.137.160.53");
        map.put("destination.mac","");
        map.put("destination.name","移动警务");
        map.put("destination.port","32610");
        map.put("destination.sensitive","");
        map.put("destination.url","");
        map.put("device.id", Settings.Secure.getString(getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        map.put("device.type","02");
        map.put("starttm","");
        map.put("endtm","");
        map.put("event.message",userName + msg);
        map.put("event.action","应用访问");
        map.put("event.level","");
        map.put("event.model","阿克苏警务云");
        map.put("event.outcome","成功");
        map.put("event.name","");
        map.put("identity.identify",token);
        map.put("identity.orgcode","");
        map.put("identity.orgid","");
        map.put("identity.orgname","");
        map.put("identity.identify",token);
        map.put("keyword","");
        map.put("lat",mLoationLat);
        map.put("long",mLoationLng);

        map.put("object.id","123");
        map.put("object.name","移动警务");

        map.put("object.type","应用");
        map.put("other.key","");
        map.put("params","");
        map.put("resources","");

        map.put("source.id",Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID));
        map.put("source.ip",getIp());
        map.put("source.mac",getMac());
        map.put("source.port","");

        map.put("session.id",token);

        map.put("subject.id",sfzh);
        map.put("subject.name",userName);
        map.put("subject.type","用户");
        map.put("volume","");

        return JSON.toJSONString(map);
    }

    private static String getIp(){

        String ip = "";
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) interfaces.nextElement();
                Enumeration inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddress = inetAddress.getHostAddress();
                        ip = ipAddress;

                        // do something with the IP address
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }


    private static String getMac(){

        String macAddress = "";
        try {
            File file = new File("/sys/class/net/wlan0/address");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            macAddress = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }



    public static HashMap collectDeviceInfo() {


        Context ctx = getApplication();
        HashMap infos = new HashMap();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "an error occured when collect package info", e);
        }
//        Field[] fields = Build.class.getDeclaredFields();
//        for (Field field : fields) {
//            try {
//                field.setAccessible(true);
//                infos.put(field.getName(), field.get(null).toString());
//            } catch (Exception e) {
//               // Log.e(TAG, "an error occured when collect crash info", e);
//            }
//        }

        return infos;
    }
    public static Application getApplication() {
        Application currentApplication = null;
        try {
            if (currentApplication == null) {
                currentApplication = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            return currentApplication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Log中的常量是int值，不适合给外面使用，这里统一用这个枚举值进行设置
     */
    public enum LogLevel {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        ASSERT(Log.ASSERT);

        private int mValue;

        LogLevel(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }


    public interface ReportRes{
        void callback(int code);
    }


    private static void reprot(String data,ReportRes res) throws IOException {



// 将JSON数组转换为字符串
        String jsonString = data;

// 创建RequestBody对象
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);

// 创建Request对象
        Request request = new Request.Builder()
                .url(Aks_Log_Report)
                .post(requestBody)
                .build();

// 发送请求并处理响应
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            // 处理响应数据
            String responseData = response.body().string();

            JSONObject json = JSON.parseObject(responseData);

            if(json.getInteger("code") == 200){
                if(res != null){
                    res.callback(200);
                }
            }else{
                res.callback(0);
            }

            // ...
        } else {
            // 处理错误
            String errorMessage = response.message();
            res.callback(0);


            // ...
        }


    }



    private static void getSeting() throws IOException {




        if(token.length() == 0) {

            return;
        }
        HashMap map = new HashMap();
        map.put("accessToken",token);
// 将JSON数组转换为字符串
        String jsonString = JSON.toJSONString(map);

// 创建RequestBody对象
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);

// 创建Request对象
        Request request = new Request.Builder()
                .url(Aks_Log_Report_Setting)
                .post(requestBody)
                .build();

// 发送请求并处理响应
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            // 处理响应数据
            String responseData = response.body().string();

            JSONObject json = JSON.parseObject(responseData);

            if(json.getInteger("code") == 200){

                if(json.get("data") != null) {

                    JSONObject data = json.getJSONObject("data");
                    if(data.getJSONObject("report") != null) {
                        JSONObject report = data.getJSONObject("report");
                        record_count = report.getInteger("count");
                        timeval = report.getInteger("times");

                        open = report.getInteger("open");
                        hasGetSeting = true;
                    }
                }

            }else{
            }

            // ...
        } else {
            // 处理错误
            String errorMessage = response.message();



            // ...
        }


    }




}

