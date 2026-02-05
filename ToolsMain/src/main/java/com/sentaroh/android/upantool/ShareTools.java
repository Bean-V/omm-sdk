package com.sentaroh.android.upantool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ShareTools {
    public static final  String PACKAGE_WECHAT = "com.tencent.mm";

    private static final int VERSION_CODE_FOR_WEI_XIN_VER7 = 1380;


    /**
     * 分享文件到微信好友 by WXAPI
     *
     * @param thumbId 分享到微信显示的图标
     */
    public static void shareFileToWechat(Context context, File file, int thumbId) {
        if (!isInstallApp(context, PACKAGE_WECHAT)) {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
            return;
        }
        //ANDROID 11上微信分享得走FileProvider
        Log.d("share", "SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            shareWechatFriend(context, file);
            return;
        }
//        //构建发送文件体
//        WXFileObject fileObject = new WXFileObject();
//        /*经实测，不给fileObject设置fileData，也是可以分享文件得，且大小默认10M以内
//        反而是设置了fileData属性的话，分享文件大小不能大于500kb，且在Android11以上无法分享，坑啊，
//        所以，在Android11上需要走FileProvider文件分享的方式*/
//        //设置需要发送的文件byte[]
//        //byte[] fileBytes = readFile(file);
//        //fileObject.setFileData(fileBytes);
//        fileObject.setFilePath(file.getAbsolutePath());
//        fileObject.setContentLengthLimit(1024 * 1024 * 10);
//        //使用媒体消息分享
//        WXMediaMessage msg = new WXMediaMessage(fileObject);
//        //这个title有讲究，最好设置为带后缀的文件名，否则可能分享到微信后无法读取
//        msg.title = file.getName();
//        //设置显示的预览图 需小于32KB
//        if (thumbId <= 0) thumbId = R.mipmap.ic_launcher;
//        msg.thumbData = readBitmap(context, thumbId);
//        //发送请求
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        //创建唯一标识
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession; //WXSceneSession:分享到对话
//        // 通过WXAPIFactory工厂，获取IWXAPI的实例
//        IWXAPI api = WXAPIFactory.createWXAPI(context, WXEntryActivity.APP_ID, true);
//        // 将应用的appId注册到微信
//        api.registerApp(WXEntryActivity.APP_ID);
//        api.sendReq(req);
    }

    // 判断是否安装指定app
    public static boolean isInstallApp(Context context, String app_package) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 图片读取成byte[]
     */
    private static byte[] readBitmap(Context context, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(bos);
        }
        return null;
    }

    /**
     * file文件读取成byte[]
     */
    private static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    //关闭读取file
    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 直接文件到微信好友
     *
     * @param picFile 文件路径
     */
    public static void shareWechatFriend(Context mContext, File picFile) {
        //首先判断是否安装微信
        if (isInstallApp(mContext, ShareTools.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            intent.setPackage(PACKAGE_WECHAT);
            intent.setAction(Intent.ACTION_SEND);
            String type = "*/*";
            for (int i = 0; i < MATCH_ARRAY.length; i++) {
                //判断文件的格式
                if (picFile.getAbsolutePath().toString().contains(MATCH_ARRAY[i][0].toString())) {
                    type = MATCH_ARRAY[i][1];
                    break;
                }
            }
            intent.setType(type);
            Uri uri = null;
            if (picFile != null) {
                //这部分代码主要功能是判断了下文件是否存在，在android版本高过7.0（包括7.0版本）
                //当前APP是不能直接向外部应用提供file开头的的文件路径，
                //需要通过FileProvider转换一下。否则在7.0及以上版本手机将直接crash。
                try {
                    ApplicationInfo applicationInfo = mContext.getApplicationInfo();
                    int targetSDK = applicationInfo.targetSdkVersion;
                    if (targetSDK >= Build.VERSION_CODES.N &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //uri = FileProvider.getUriForFile(mContext,
                                //mContext.getApplicationInfo().processName + ".fileprovider", picFile);

                        uri = FileProvider.getUriForFile(mContext,mContext.getApplicationInfo().processName + ".provider",picFile);//.processName

                    } else {
                        uri = Uri.fromFile(picFile);
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (getVersionCode(mContext, PACKAGE_WECHAT) > VERSION_CODE_FOR_WEI_XIN_VER7) {
                // 微信7.0及以上版本
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
            mContext.startActivity(Intent.createChooser(intent, "分享文件"));
        } else {
            Toast.makeText(mContext, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareMoreToWechatFriend(Context mContext, List filePaths) {


        for(Object o : filePaths){
            String path  = (String) o;
            if(!FileTool.isPicture(path)){
                shareWechatFriend(mContext,new File((String) filePaths.get(0)));
                return;
            }
        }



        //首先判断是否安装微信
        if (isInstallApp(mContext, ShareTools.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            intent.setPackage(PACKAGE_WECHAT);
            intent.setAction(Intent.ACTION_SEND);
            String type = "*/*";
//            for (int i = 0; i < MATCH_ARRAY.length; i++) {
//                //判断文件的格式
//                if (picFile.getAbsolutePath().toString().contains(MATCH_ARRAY[i][0].toString())) {
//                    type = MATCH_ARRAY[i][1];
//                    break;
//                }
//            }
            intent.setType(type);
            Uri uri = null;
            ArrayList uirs = new ArrayList();

            for(Object o : filePaths){
                String path = (String) o;
                File f = new File(path);
                if(f.exists()){
                    uri = FileProvider.getUriForFile(mContext,mContext.getApplicationInfo().processName + ".provider",f);//.processName

                    uirs.add(uri);
                }
            }

            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uirs);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(Intent.createChooser(intent, "分享文件"));
        } else {
            Toast.makeText(mContext, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }


    public static int getVersionCode(Context context,String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // 建立一个文件类型与文件后缀名的匹配表
    private static String[][] MATCH_ARRAY = {
            //{后缀名，    文件类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}


    };


}
