package com.oort.weichat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;


import com.oort.weichat.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static void initFonts(Context context) {
        initData(context, "/sdcard/.fcconfig/", "fonts.conf");
        initData(context, "/sdcard/.fcconfig/", "cour.ttf");
        initData(context, "/sdcard/.fcconfig/", "courbd.ttf");
        initData(context, "/sdcard/.fcconfig/", "courbi.ttf");
        initData(context, "/sdcard/.fcconfig/", "couri.ttf");
        initData(context, "/sdcard/.fcconfig/", "simkai.ttf");
        initData(context, "/sdcard/.fcconfig/", "simsun.ttc");
        initData(context, "/sdcard/.fcconfig/", "simhei.ttf");
        initData(context, "/sdcard/.fcconfig/", "mingliub.ttf");
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void initData(Context context, String path, String fileName) {
        try {
            File file = fileFromAsset(context, fileName);
            File fileofd = new File(path + fileName);
            if (isFolderExists(path)) {
                if (!fileofd.exists()) {
                    fileofd.createNewFile();
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileofd);
                        byte[] b = new byte[1024];
                        while (fileInputStream.read(b) != -1) {
                            fileOutputStream.write(b);
                        }
                        fileOutputStream.flush();
                        fileInputStream.close();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileofd.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else
                return false;
        }
        return true;
    }

    public static File fileFromAsset(Context context, String assetName) throws IOException {
        File outFile = new File(context.getCacheDir(), assetName);
        if (assetName.contains("/")) {
            outFile.getParentFile().mkdirs();
        }
        copy(context.getAssets().open(assetName), outFile);
        return outFile;
    }

    public static void copy(InputStream inputStream, File output) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(output);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }

    public static boolean isBase64(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
            if (str.length() % 4 != 0) {
                return false;
            }

            char[] strChars = str.toCharArray();
            for (char c : strChars) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                        || c == '+' || c == '/' || c == '=') {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    //file文件读取成byte[]
    public static byte[] readFile(File file) {
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
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件).
     *
     * @param base64Code 编码后的字串
     * @throws Exception
     */
    public static String decoderBase64File(Context context, String base64Code) {
        String path = context.getCacheDir().getAbsolutePath() + "/" + generateFileName() + ".pem";
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(buffer);
            out.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static Bitmap decodeBitmapByBase64(Context context, String base64Str, String type) throws Exception {
        if (TextUtils.isEmpty(type)) return null;
        String path = context.getCacheDir().getAbsolutePath() + "/" + generateFileName() + "." + type;
        decoderBase64File(base64Str, path);
        File file = new File(path);
        if (file == null || !file.exists()) {
            return null;
        }
        if (type.toLowerCase().equals("bmp") || type.toLowerCase().equals("gif") || type.toLowerCase().equals("png")) {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } else if (type.toLowerCase().equals("ofd")) {
            Document document = Document.open(file, null);
            if (document != null && document.isOpen()) {
                //导出路径
                String pngPath = context.getCacheDir().getAbsolutePath() + "/";
                String pngName = generateFileName() + ".png";
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                int dpi = metrics.densityDpi;
                //获取当前页文档宽高（mm）,
                float[] wh = document.getPageWH(0, 0);
                if (wh != null) {
                    //毫米换成像素
                    float width = wh[0] * dpi / 25.4f;
                    int pageZoom = (int) (DeviceUtils.dip2Px(context, 50) / width * 100);//获取50dp宽的图片
                    boolean result = document.exportPictuare(pngPath, pngName, "JPG", "1-3", false, dpi, pageZoom, 0, 0);
                    if (result) {
                        File pngFile = new File(pngPath + pngName);
                        FileInputStream fis = new FileInputStream(pngFile);
                        return BitmapFactory.decodeStream(fis);
                    }
                    document.close();
                }
            }

        }
        return null;
    }*/

    /**
     * decoderBase64File:(将base64字符解码保存文件).
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath = context.getApplicationContext().getFilesDir()
                .getAbsolutePath() + "/signnamepic/";
        return saveBitmap(savePath + generateFileName() + ".png", mBitmap);
    }

    /**
     * 保存bitmap到本地
     *
     * @param path    xxxx/xxxx/xxx.png
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(String path, Bitmap mBitmap) {
        File filePic;
        try {
            filePic = new File(path);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            //注意要保存成png格式，不然背景色变黑
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    public static Map<String, String> showCertInfo(String base64Str) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
            InputStream inStream = new ByteArrayInputStream(bytes);
            //创建X509工厂类
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //创建证书对象
            X509Certificate oCert = (X509Certificate) cf.generateCertificate(inStream);
            inStream.close();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
            String info = null;
            //获得证书版本
            info = String.valueOf(oCert.getVersion());
            map.put("版本", info);
            //获得证书序列号
            info = oCert.getSerialNumber().toString(16);
            map.put("序列号", info);
            //获得证书有效期
            Date beforedate = oCert.getNotBefore();
            info = dateformat.format(beforedate);
            map.put("生效日期", info);
            Date afterdate = oCert.getNotAfter();
            info = dateformat.format(afterdate);
            map.put("失效日期", info);
            //获得证书主体信息
            info = oCert.getSubjectDN().getName();
            map.put("拥有者", info);
            //获得证书颁发者信息
            info = oCert.getIssuerDN().getName();
            map.put("颁发者", info);
            //获得证书签名算法名称
            info = oCert.getSigAlgName();
            map.put("签名算法", info);
        } catch (Exception e) {
            System.out.println("解析证书出错！");
        }
        return map;
    }

    public static Bitmap getDecodeAbleBitmap(String picturePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int sampleSize = options.outHeight / 400;
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(picturePath, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //判断字符串中是否有超链接，若有，则返回超链接
    public static String filterSpecialStr(String data) {
        //sb存放正则匹配的结果
        StringBuffer sb = new StringBuffer();
        //编译正则字符串
        Pattern p = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        //利用正则去匹配
        Matcher matcher = p.matcher(data);
        //如果找到了我们正则里要的东西
        while (matcher.find()) {
            //保存到sb中，"\r\n"表示找到一个放一行，就是换行
            sb.append(matcher.group() + "\r\n");
        }
        return sb.toString();
    }
    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication
                .getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
