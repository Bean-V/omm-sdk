package com.oortcloud.basemodule.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2019/3/13.
 */

public class NetUtil {
    
    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }
    
    /**
     * @return 返回boolean ,是否为wifi网络
     */
    public static boolean hasWifiConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //是否有网络并且已经连接
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
    
    /**
     * @return 返回boolean, 判断网络是否可用, 是否为移动网络
     */
    
    public static boolean hasGPRSConnection(Context context) {
        //获取活动连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (networkInfo != null && networkInfo.isAvailable());
    }
    
    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIpV4Address(Context context) {
        if (hasWifiConnection(context)) {
            //Wifi
            return getLocalIpV4Address_WifiNetwork(context);
        } else if (hasGPRSConnection(context)) {
            return getLocalIpV4Address_MobileNetwork();
        }
        return null;
    }
    
    
    /**
     * 移动网络下 获取本地IP
     *
     * @return
     */
    public static String getLocalIpV4Address_MobileNetwork() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        ipv4 = address.getHostAddress();
                        return ipv4;
                    }
                }
            }
            
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Wifi网络下 获取本地IP
     *
     * @param context
     * @return
     */
    public static String getLocalIpV4Address_WifiNetwork(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }
    
    //获取Wifi ip 地址
    private static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }
    
}
