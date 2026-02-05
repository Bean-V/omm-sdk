package com.sentaroh.android.upantool;

//import USBBroadCastReceiver.ACTION_USB_PERMISSION;

//import static com.oort.upantool.USBBroadCastReceiver.ACTION_USB_PERMISSION;



import android.Manifest;
import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.bluetooth.BluetoothClass;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStatVfs;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.Utilities3.SafManager3;
import com.sentaroh.android.Utilities3.SafStorage3;
import com.sentaroh.android.Utilities3.StringUtil;
import com.sentaroh.android.upantool.record.Record;
import com.sentaroh.android.upantool.record.RecordDao;
import com.sentaroh.android.upantool.record.RecordDatabase;
import com.sentaroh.android.upantool.record.RecordTool;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.ui.widget.CheckView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;

//import me.jahnen.libaums.core.UsbMassStorageDevice;
//import me.jahnen.libaums.core.fs.FileSystem;
//import me.jahnen.libaums.core.fs.UsbFile;
//import me.jahnen.libaums.core.fs.UsbFileInputStream;
//import me.jahnen.libaums.core.fs.UsbFileOutputStream;
//import me.jahnen.libaums.core.partition.Partition;







/**
 * Author ： BlackHao
 * Time : 2018/5/10 14:53
 * Description : USB 操作工具类
 * Logs :
 */

public class UsbHelper {


    public static final String AUTHORITY = "com.android.mtp.documents";
    public static boolean sysSuccess = true;
    private final Uri mMtpUri = DocumentsContract.buildRootsUri(AUTHORITY);
    private Context mContext;
    private StorageManager storageManager;
    private ParcelFileDescriptor uPfd;
    private Uri udocTreeUri;

    public MediaScannerConnection getMediaScanner() {
        return mediaScanner;
    }

    public void setMediaScanner(MediaScannerConnection mediaScanner) {
        this.mediaScanner = mediaScanner;
    }

    private MediaScannerConnection mediaScanner;


    public Thread getThread() {
        return thread;
    }

    private Thread thread;


    public boolean isStopTrans() {
        return stopTrans;
    }

    private boolean stopTrans = false;


    public interface LCUsbTransListener {

        void showProgress(String s, String s1, long l,int postion);//0未插入1插入2没权限3可以访问4弹出
        void checkFinsh(String s, String s1, Boolean finsh);
    }

    public LCUsbTransListener getTransListener() {
        return transListener;
    }

    public void setTransListener(LCUsbTransListener transListener) {
        this.transListener = transListener;
    }


    public void addTransListener(LCUsbTransListener transListener) {

        if(!this.transListeners.contains(transListener)){
            this.transListeners.add(transListener);
        }
    }

    private LCUsbTransListener transListener;


    private List<LCUsbTransListener> transListeners = new ArrayList<>();


    public interface LCUsbListener {

        void refreshState(int state);//0未插入1插入2没权限3可以访问4弹出
        void getSize(long use,long toatal);
        void rootFileReady();

        void requestPermissonVolume(StorageVolume v);
    }

    public interface LCCopyListener {
        void copyProgress();
    }


    ArrayList <LCUsbListener> lcUsbListeners = new ArrayList<LCUsbListener>();

    public void addUsbListener(LCUsbListener listener){
        if(!lcUsbListeners.contains(listener)) {
            lcUsbListeners.add(listener);
        }
    }











    public static final String APPPId="com.oortcloud.danganbao";//com.sentaroh.android
    public static final String APPLICATION_TAG="SMBSync2";
    public static final String PACKAGE_NAME= APPPId;//"com.sentaroh.android."+APPLICATION_TAG;
    public static final String APP_SPECIFIC_DIRECTORY= "Android/data/" + APPPId;


    public SafFile3 getRootFile() {
        if(outUpan){
            rootFile = null;
            return rootFile;
        }
        if(getUsbUUid() != null) {
            rootFile = safMgr.getRootSafFile(usbUUid);



            //DocumentsContract.buildRootsUri()
        }else {
            rootFile = null;
        }
        return rootFile;
    }

    private SafFile3 rootFile = null;

    public SafManager3 safMgr = null;

    public interface PreViewListener {
        public void delFile(File file);
    }


    public void refreshMediaDir(Context c) {
        if (safMgr == null) {
            safMgr = new SafManager3(c);
//            Thread th=new Thread(){
//                @Override
//                public void run() {
//                    safMgr = new SafManager3(c);
//                }
//            };
//            th.start();
        } else {
            safMgr.refreshSafList();
        }
    }



    public PreViewListener getmPreViewListener() {
        return mPreViewListener;
    }

    public void setmPreViewListener(PreViewListener mPreViewListener) {
        this.mPreViewListener = mPreViewListener;
    }

    private PreViewListener mPreViewListener;

    public Boolean wxSelectEnable = false;

    public Boolean canCopyToU() {


        if(outUpan){
            return false;
        }

        if(getRootFile() != null){
            if(rootFile.exists()){
                return true;
            }
        }
        return false;
    }


    public String getSdRootPath(){

        return Environment.getExternalStorageDirectory().getPath();
    }


    public String getUsbRootPath(){
        if(rootFile != null){
            return rootFile.getPath();
        }
        return "";
    }


    private boolean outUpan = false;

    public void stopUsb(){
        outUpan = true;

        toStopTrans(true);
//        ContentResolver cr = mContext.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaDevice.FIELD_VALID, false);
//        cr.update(MediaStore.MediaDevice.CONTENT_URI, values, where, selectionArgs);


        for(LCUsbListener lis : lcUsbListeners){
            lis.getSize(0,0);
            lis.refreshState(4);
        }
    }


    public void toStopTrans(boolean stop){
        stopTrans = stop;
        if(uPfd != null) {
            try {
                if(uPfd != null) {
                    uPfd.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    static int tcount = 0;
    public void initTimer(){



        thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {

                {



                    boolean test = true;
                    while(test)

                    {

                        try{
                            tcount ++;
                            if(tcount > 400){
                                //LogHelper.getInstance().setIS_WRITE_FILE(false);
                            }
                            Thread.sleep(2000);//每隔1s执行一次


                            StorageManager storageManager = context.getSystemService(StorageManager.class);
                            List<StorageVolume> volumeList = null;




//                            String[] str1 = getExtSDCardPath();
//                            String str2 = getUsbDir();

                            //StatFs fs = new StatFs(str2);
                            //String [] strs = sdCardRemounted();
                            LogHelper.getInstance().d("android.os.Build.VERSION.SDK_INT:::" + Build.VERSION.SDK_INT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    volumeList = storageManager.getRecentStorageVolumes();
                                }else {
                                    volumeList = storageManager.getStorageVolumes();
                                }

                                List<StorageVolume> volumeList1 = null;
                                String usbLocation = getUSBStorageLocations();


                                    //volumeList1 = storageManager.getRecentStorageVolumes();

                                    //LogHelper.getInstance().d("volumeList1:" + volumeList1);



                                    if(outUpan){

                                        if(usbLocation == null || usbLocation.equals("")){




                                            ((Activity)context).runOnUiThread(new Runnable() {
                                                //@RequiresApi(api = Build.VERSION_CODES.R)
                                                @Override
                                                public void run() {
                                                    outUpan = false;
                                                    try {
                                                        uPfd.close();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    ;
                                                    uPfd = null;
                                                    for (LCUsbListener lis : lcUsbListeners) {
                                                        lis.refreshState(0);
                                                    }
                                                }
                                            });
                                        }
                                        continue;
                                    }
                                    List<StorageVolume> finalVolumeList = volumeList;

                                if(safMgr != null) {
                                    safMgr.refreshSafList();
                                }

                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        //@RequiresApi(api = Build.VERSION_CODES.R)
                                        @Override
                                        public void run() {

                                            if(usbLocation == null || usbLocation.equals("")){
                                                outUpan = false;
                                                try {

                                                    if(uPfd != null) {
                                                        uPfd.close();
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                ;
                                                uPfd = null;
                                                for (LCUsbListener lis : lcUsbListeners) {
                                                    lis.refreshState(0);
                                                }
                                            }

                                            for (StorageVolume volume : finalVolumeList) {


                                                //LogHelper.getInstance().d("volume:" + volume.getUuid()+"YYYY"+volume.describeContents() + "YYY" + volume.isRemovable() +"YYYY");
                                                long time = System.currentTimeMillis()/1000;
                                                if (volume.isRemovable() && usbLocation.contains(volume.getUuid())) {


                                                    Log.d(TAG, "updateUsbFile1: "+(System.currentTimeMillis()/1000 - time));
                                                    time = System.currentTimeMillis()/1000;
                                                    LogHelper.getInstance().d("isStoragePermissionRequired" + safMgr.isStoragePermissionRequired());
                                                    if (!safMgr.isStoragePermissionRequired()) {
                                                        for (LCUsbListener lis : lcUsbListeners) {
                                                            lis.refreshState(3);
                                                            Log.d(TAG, "updateUsbFile2: "+(System.currentTimeMillis()/1000 - time));
                                                            time = System.currentTimeMillis()/1000;
                                                            continue;
                                                        }
                                                    } else {
                                                        for (LCUsbListener lis : lcUsbListeners) {
                                                            lis.refreshState(2);
                                                        }

                                                        continue;
                                                    }


                                                    LogHelper.getInstance().d("getUsbUUid" + getUsbUUid());
                                                    //isStoragePermissionRequired
                                                    if(getUsbUUid() == null){
                                                        try {
                                                            if(uPfd != null) {
                                                                uPfd.close();
                                                            }
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        uPfd = null;
                                                        for (LCUsbListener lis : lcUsbListeners) {
                                                            lis.refreshState(0);
                                                        }
                                                        continue;
                                                    }

                                                    if(!canCopyToU()){
                                                        continue;
                                                    }


                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String path = safMgr.getRootSafFile(getUsbUUid()).getPath();
                                                            File df = new File(safMgr.getRootSafFile(getUsbUUid()).getPath());

                                                            if (df.exists()) {
                                                                ((Activity)context).runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        for (LCUsbListener lis : lcUsbListeners) {
                                                                            lis.getSize(df.getTotalSpace() - df.getFreeSpace(), df.getTotalSpace());
                                                                        }
                                                                    }
                                                                });


                                                            }else{

                                                                Uri uri = DocumentsContract.buildTreeDocumentUri(
                                                                        "com.android.externalstorage.documents",
                                                                        volume.getUuid()+":"
                                                                );

                                                                List<UriPermission> pers = context.getContentResolver().getPersistedUriPermissions();

                                                                List <String>perStrs = new ArrayList<>();

                                                                for(UriPermission p : pers){
                                                                    perStrs.add(p.getUri().toString());
                                                                }

                                                                if(perStrs.contains(uri.toString())){
                                                                    Uri docTreeUri = DocumentsContract.buildDocumentUriUsingTree(
                                                                            uri,
                                                                            DocumentsContract.getTreeDocumentId(uri)
                                                                    );

                                                                    ParcelFileDescriptor pfd = null;

                                                                    if(udocTreeUri != null && uPfd != null) {
                                                                        if (udocTreeUri.getPath().equals(docTreeUri.getPath())) {
                                                                            pfd = uPfd;
                                                                        }else {
                                                                            try {

                                                                                pfd = context.getContentResolver().openFileDescriptor(udocTreeUri, "r");
                                                                                uPfd = pfd;
                                                                                udocTreeUri = docTreeUri;
                                                                            } catch (FileNotFoundException e) {

                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }else {
                                                                        try {

                                                                            pfd = context.getContentResolver().openFileDescriptor(docTreeUri, "r");
                                                                            uPfd = pfd;
                                                                            udocTreeUri = docTreeUri;
                                                                        } catch (FileNotFoundException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }

                                                                    try {

                                                                        if(pfd == null){

                                                                            return;
                                                                        }
                                                                        StructStatVfs stats = Os.fstatvfs(pfd.getFileDescriptor());


                                                                        //Long usedSpace = stats.f_blocks * blockSize / 1024L;

                                                                        long blockSize = stats.f_bsize;
                                                                        long totalSpace = stats.f_blocks * blockSize;
                                                                        long freeSpace = stats.f_bfree * blockSize;
                                                                        long usedSpace = totalSpace - freeSpace;
                                                                        String freeSpaceStr = Formatter.formatFileSize(context, freeSpace);
                                                                        String totalSpaceStr = Formatter.formatFileSize(context, totalSpace);
                                                                        String usedSpaceStr = Formatter.formatFileSize(context, usedSpace);
                                                                        ((Activity)context).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                for (LCUsbListener lis : lcUsbListeners) {
                                                                                    lis.getSize(usedSpace, totalSpace);
                                                                                }
                                                                            }
                                                                        });


                                                                    } catch (ErrnoException e) {
                                                                        uPfd = null;
                                                                        e.printStackTrace();
                                                                    }
                                                                }else{
                                                                    ((Activity)context).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            for (LCUsbListener lis : lcUsbListeners) {
                                                                                lis.requestPermissonVolume(volume);

                                                                            }
                                                                        }
                                                                    });


                                                                }

                                                            }





                                                        }
                                                    }).start();






                                                }
                                            }



                                        }

                                    });

                            }else {
                                continue;
                            }

                        }catch (InterruptedException e) {

                            e.printStackTrace();

                        }

                    }

                }

            }
        });
        thread.start();

    }



    public int checkedNumOf(File item) {
        int num = -1 ;

        for (File fi : selectFiles){
            if(fi.getAbsolutePath().equals(item.getAbsolutePath())){
                return selectFiles.indexOf(fi) + 1;
            }
        }
        return num == -1 ? CheckView.UNCHECKED : 0;
    }

    public boolean isSelected(File item) {

        for (File fi : selectFiles){
            if(fi.getAbsolutePath().equals(item.getAbsolutePath())){
                return true;
            }
        }
        return false;
    }

    public void addAll(ArrayList cl) {

        for(Object o : cl){
            File f = (File) o;
            addFile(f);
        }

    }

    public void addFile(File l){

        if(isSelected(l)){

        }else{
            selectFiles.add(l);
        }

    }
    public void remove(File item) {

        File f = null;
        for (File fi : selectFiles){
            if(fi.getAbsolutePath().equals(item.getAbsolutePath())){
                f = fi;
            }
        }

        if(f != null) {
            selectFiles.remove(f);
        }
    }

    public void removeAll(ArrayList cl) {

        for(Object o : cl){
            File f = (File) o;
            remove(f);
        }

    }


    public ArrayList<File> getSelectFiles() {
        return selectFiles;
    }

    public void setSelectFiles(ArrayList<File> selectFiles) {
        this.selectFiles = selectFiles;
    }

    ArrayList <File> selectFiles = new ArrayList<>();


    //上下文对象
    private static Context context;




    public File getCurrenNewFolder() {
        return currenNewFolder;
    }

    public void setCurrenNewFolder(File currenNewFolder) {
        this.currenNewFolder = currenNewFolder;
    }

    private File currenNewFolder = null;
    //TAG
    private static String TAG = "UsbHelper";

    public String getLocalUUid() {
        localUUid = SafManager3.SAF_FILE_PRIMARY_UUID;
        return localUUid;
    }

    private String localUUid = null;

    public String getUsbUUid() {
        for(SafStorage3 sv : safMgr.getSafStorageList()){

            if(sv.uuid != SafManager3.SAF_FILE_PRIMARY_UUID && getUSBStorageLocations().contains(sv.uuid)){
                usbUUid = sv.uuid;
            }
        }
        return usbUUid;
    }

    private String usbUUid = null;



    public void initData(Context c){
        context = c;
        initTimer();
        refreshMediaDir(context);
        prepareMediaScanner();
        mContext = c;
        storageManager = (StorageManager) mContext.getSystemService(mContext.STORAGE_SERVICE);

    }

    public UsbHelper() {
    }





    public boolean isUsbIn() {
        return false;
    }



    private static UsbHelper logUtil;

    //单例模式初始化
    public static UsbHelper getInstance() {
        if (logUtil == null) {
            logUtil = new UsbHelper();

        }
        return logUtil;
    }

    /**
     * 注册 USB 监听广播
     */
    private void registerReceiver() {
//        mUsbReceiver = new USBBroadCastReceiver(usbListener);
//        mUsbReceiver.setUsbListener(usbListener);
//        //监听otg插入 拔出
//        IntentFilter usbDeviceStateFilter = new IntentFilter();
//        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        context.registerReceiver(mUsbReceiver, usbDeviceStateFilter);
//        //注册监听自定义广播
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        context.registerReceiver(mUsbReceiver, filter);
    }




    public void saveSDFileToUsb(String from,String to) {

        String from_temp = buildStorageDir(getLocalUUid(), from);

        to = buildStorageDir(getUsbUUid(), to);


        syncCopyLocalToLocal(from_temp,to);

    }


    public void moveCopyExternalToInternal(String from,String to){

        String from_temp = buildStorageDir(getUsbUUid(), from);

        to = buildStorageDir(getLocalUUid(), to);


        syncCopyLocalToLocal(from_temp,to);
    }


     public void moveCopyInternalToExternal(String from,String to){

        String from_temp = buildStorageDir(getLocalUUid(), from);

        to = buildStorageDir(getUsbUUid(), to);



         //copyFileLocalToLocal(new SafFile3(context));
         syncCopyLocalToLocal(from_temp,to);
    }





     public int syncCopyLocalToLocal(String from_path, String to_path) {
        String to_path_converted=to_path;
        SafFile3 mf = new SafFile3(context, from_path);
        ContentProviderClient cpc=null;
        int sync_result=0;
        try {
            cpc=mf.getContentProviderClient();
            sync_result= moveCopyLocalToLocal(from_path, from_path, mf, to_path_converted, to_path_converted, cpc);
        } finally {
            if (cpc!=null) cpc.release();
        }
        return sync_result;
    }




     private int moveCopyLocalToLocal(String from_base, String from_path, SafFile3 mf, String to_base, String to_path, ContentProviderClient cpc) {


        if(outUpan){
            return 2;
        }

        int sync_result = 0;
        try {
            if (mf.exists()) {
                String relative_from_path = from_path.substring(from_base.length());
                    if (relative_from_path.startsWith("/")) relative_from_path = relative_from_path.substring(1);
                if (mf.isDirectory()) { // Directory copy
                    if (true) {
                        if (!mf.canRead()) {
                            return sync_result;
                        }
                        SafFile3[] children = mf.listFiles();
                        if (children != null) {
                            for (SafFile3 element : children) {
                                if (sync_result == 0) {
                                    if (!element.getName().equals(".android_secure")) {
                                        if (!element.isDirectory()) {
                                            sync_result = moveCopyLocalToLocal(from_base, from_path+"/"+element.getName(),
                                                    element, to_base, to_path+"/"+element.getName(), cpc);
                                        } else {
                                            sync_result = moveCopyLocalToLocal(from_base, from_path+"/"+element.getName(),
                                                        element, to_base, to_path+"/"+element.getName(), cpc);

                                        }
                                    }
                                } else {
                                    return sync_result;
                                }

                            }

                        } else {

                        }
                    }
                } else { // file copy
                    long mf_length=mf.length();
                    long mf_last_modified=mf.lastModified();
                    if (true) {
                        String parsed_to_path=to_path;
                        if (from_path.equals(parsed_to_path)) {

                        } else {
                            SafFile3 tf = new SafFile3(context, parsed_to_path);

                            boolean tf_exists = tf.exists();
                            String conf_type="";
                            sync_result= moveCopyLocalToLocalFile(mf, tf, tf_exists) ;



                        }
                    }
                }
            } else {
               return 2;
            }
        } catch (Exception e) {
            return 2;
        }

        return sync_result;
    }


    private static int moveCopyLocalToLocalFile(SafFile3 mf, SafFile3 tf, boolean tf_exists) {
        int sync_result = 0;
        sync_result = copyFileLocalToLocal(mf, tf);

        return sync_result;
    }



    static public int copyFileLocalToLocal(SafFile3 mf, SafFile3 tf) {
        int sync_result=1;
        sync_result= copyFileLocalToLocalUnsetLastModified(mf, tf);
        return sync_result;
    }



    static private int copyFileLocalToLocalUnsetLastModified(SafFile3 mf, SafFile3 tf) {


        int sync_result= 0;
        try {
            createDirectoryToLocalStorage(tf.getParentFile().getPath());

            String to_file_temp = tf.getPath()+".tmp";
            SafFile3 t_df = new SafFile3(context,to_file_temp);

            //if(mf.length() < 1024 * 1024 * 50 || !t_df.exists()) {



            OutputStream out = null;
            if(Constants.COPY_APPEND_DEBUG) {
                if (!t_df.exists() || t_df.length() < 1024) {
                    t_df.deleteIfExists();
                    t_df.createNewFile();
                    out = t_df.getOutputStream();
                    Record rd = RecordTool.getInstance().getRecordByPath(mf.getPath());
                    rd.postion = 0;
                    RecordTool.getInstance().updateRecord(rd);


                } else {

                    long l = t_df.length();
                    Record rd = RecordTool.getInstance().getRecordByPath(mf.getPath());
                    if(rd != null){
                        long size = rd.postion * Constants.SYNC_IO_BUFFER_SIZE;

                        long l1 = l - size;
                        if(l1 >= 0){
                            Log.d(TAG, "00000copyFileLocalToLocalUnsetLastModified: " + t_df.length() / 1024.0 / 1024.0);
                            out = t_df.getOutputStream("wa");
                        }else{
                            rd.postion = 0;
                            RecordTool.getInstance().updateRecord(rd);
                            t_df.deleteIfExists();
                            t_df.createNewFile();
                            out = t_df.getOutputStream();
                        }
                    }else {
                        t_df.deleteIfExists();
                        t_df.createNewFile();
                        out = t_df.getOutputStream();
                    }


                }

            }else{
                t_df.deleteIfExists();
                t_df.createNewFile();

                out = t_df.getOutputStream();
            }
            //}
//
//            Rand
//            //RandomAccessFile



            int result=copyFile(mf.getParentFile().getPath(), tf.getParentFile().getPath(), mf.getName(), mf.length(), mf.getInputStream(), out);



            if(result == 0) {
                tf.deleteIfExists();
                if (!t_df.renameTo(tf)) {

                    return 2;
                }
            }
            scanMediaFile(mf);
        } catch(IOException e) {
            sync_result= 2;
        } catch(Exception e) {
            sync_result= 2;
        }

        return sync_result;

    }




    static private int copyFileLocalToLocalSetLastModified(SafFile3 mf, SafFile3 tf) {


        int sync_result=0;
        try {
            String to_file_temp=tf.getAppDirectoryCache()+"/"+System.currentTimeMillis();//mf.getName();

            createDirectoryToLocalStorage(tf.getParentFile().getPath());

            InputStream is =null;
            long m_saf_length=-1;
            is = mf.getInputStream();

            OutputStream os =null;
            File temp_file=new File(to_file_temp);
            os = new FileOutputStream(temp_file);//stwa.appContext.getContentResolver().openOutputStream(temp_sf.getUri());



            int result=copyFile(mf.getParentFile().getPath(), tf.getParentFile().getPath(), mf.getName(), mf.length(), mf.getInputStream(), os);

            SafFile3 temp_sf=new SafFile3(context, to_file_temp);
            try {
                temp_file.setLastModified(mf.lastModified());
            } catch(Exception e) {
            }
            tf.deleteIfExists();
            if (!temp_sf.moveToWithRename(tf)){

                if (temp_file.exists()) temp_file.delete();
                return 1;
            }

            //String fp=mf.getParent();
            //SyncThread.scanMediaFile(stwa, sti, mf);
        } catch(IOException e) {
            sync_result= 1;
        } catch(Exception e) {
            sync_result= 1;
        }

        return sync_result;
    }


    private final static int SHOW_PROGRESS_THRESHOLD_VALUE = 1024 * 1024 * 10;

    static public int copyFile(String from_dir, String to_dir,
                               String file_name, long file_size, InputStream ifs, OutputStream ofs) throws IOException {


        FileOutputStream pa = (FileOutputStream) ofs;


        //String path = ofs.getFD();

        int testPostion = 0;
        Record rd = RecordTool.getInstance().getRecordByPath(from_dir + "/" + file_name);

        if(rd != null){
            testPostion = rd.postion + 20;
        }
        int io_area_size=  Constants.SYNC_IO_BUFFER_SIZE;
        boolean show_prog = (file_size > SHOW_PROGRESS_THRESHOLD_VALUE);
        int buffer_read_bytes = 0;
        long file_read_bytes = 0;
        byte[] buffer = new byte[io_area_size];
        int postion =0;


        if(rd != null) {
            if (rd.statu != 2 && Constants.COPY_APPEND_DEBUG) {
                if (rd.postion > 0) {
                    postion = rd.postion;


                    if (false) {
                        String to_file_temp = to_dir + "/" + file_name + ".tmp";
                        SafFile3 t_df = new SafFile3(context, to_file_temp);

                        FileInputStream fis0 = null;
                        try {
                            fis0 = (FileInputStream) t_df.getInputStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        fis0.read(buffer);

                        fis0.close();
                    }


                    long read_begin_time = System.currentTimeMillis();

                    long offset = io_area_size * postion;
                    ifs.skip(offset - 1);
                    pa.getChannel().position(offset - 1);


                    file_read_bytes = offset;

                    String to_file_temp = to_dir + "/" + file_name + ".tmp";
                    SafFile3 t_df = new SafFile3(context, to_file_temp);

                    Log.d(TAG, "00000copyFile: " + offset / 1024.0 / 1024.0 + "######" + t_df.length() / 1024.0 / 1024.0);
                }
            }
        }

        sysSuccess = false;

        while ((buffer_read_bytes = ifs.read(buffer)) > 0) {

            if(UsbHelper.getInstance().isStopTrans()){

                ((FileOutputStream) ofs).getFD().sync();
                ifs.close();
                //ofs.flush();
                ofs.close();
                sysSuccess = true;
                return 1;
            }
            ofs.write(buffer, 0, buffer_read_bytes);
            file_read_bytes += buffer_read_bytes;
            postion ++;
 //           if (show_prog && file_size >= file_read_bytes) {
//                int prog=(int)((file_read_bytes * 100) / file_size);
//                SyncThread.showProgressMsg(stwa, sti.getSyncTaskName(), file_name + " " +
//                        stwa.appContext.getString(R.string.msgs_mirror_task_file_copying,(file_read_bytes * 100) / file_size));

            ofs.flush();


            if(rd != null){
                rd.postion = postion;
                RecordTool.getInstance().updateRecord(rd);
            }
                UsbHelper.getInstance().showProgress(from_dir + "/" + file_name,to_dir + "/" + file_name,((file_read_bytes * 100) / file_size),postion);


//            if(postion == 5){
//                List arr = new ArrayList();
//                int a = (int) arr.get(0);
//            }
 //           }


            if(postion ==20 && false){
                //((FileOutputStream) ofs).getFD().sync();


                String to_file_temp = to_dir+ "/" + file_name + ".tmp";
                SafFile3 t_df = new SafFile3(context,to_file_temp);

                long t = (long) (t_df.length()/1024.0);
                int a= 0;

            }


            if(testPostion == postion){

                Record r = rd;

                int a = rd.postion;
            }
            if(Constants.COPY_APPEND_DEBUG && false){

                ifs.close();
                //ofs.flush();
                ofs.close();


                String to_file_temp = to_dir+ "/" + file_name + ".tmp";
                SafFile3 t_df = new SafFile3(context,to_file_temp);

                FileInputStream fis0 = null;
                try {
                    fis0 = (FileInputStream) t_df.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fis0.read(buffer);

                fis0.read(buffer);
                fis0.read(buffer);
                fis0.read(buffer);
                fis0.close();
            }

        }
        long time = System.currentTimeMillis()/1000;
        //ofs.flush();
        ((FileOutputStream) ofs).getFD().sync();
        ifs.close();
        //ofs.flush();
        ofs.close();

        sysSuccess = true;

        Log.d(TAG, "updateUsbFile: "+(System.currentTimeMillis()/1000 - time));

        //UsbHelper.getInstance().checkMd5(from_dir + "/" + file_name,to_dir + "/" + file_name,null);


        return 0;
    }

    private void checkMd5(String s, String s1, String md5) {
        Log.d(TAG, "showProgresscheckMd5: " + s +  "\n" + s1 + "\n"  + md5);

        if(transListener == null){
            return;
        }

        SafFile3 saf = new SafFile3(context,s1);


//        Log.d(TAG, "showProgresscheckMd5: " + s +  "\n" + s1 + "\n"  + md5s);


        Record record = RecordTool.getInstance().getRecordByPath(s);

        if(record == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

//                String md5s = FileTool.getFileMD5(saf);
//
//                if(!record.md5.equals(md5s)){
//                    record.statu = 3;
//                    record.statuDes = "复制失败";
//                    RecordTool.getInstance().updateRecord(record);
//                }else {
//                    record.statu = 2;
//                    record.statuDes = "复制成功";
//                    RecordTool.getInstance().updateRecord(record);
//                }

//                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(transListener != null){
//                            transListener.checkFinsh(s,s1,record.md5.equals(md5s));
//
//                        }
//                    }
//                });


            }
        }).start();

    }

    private void showProgress(String s, String s1, long l,int postion) {

        Log.d(TAG, "showProgress: " + s +  "\n" + s1 + "\n" +l + "%" + "\n" + postion);


        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(transListener != null){
                    transListener.showProgress(s,s1,l,postion);
                }

                for(LCUsbTransListener ls : transListeners){
                    ls.showProgress(s,s1,l,postion);;
                }
            }
        });

    }

    final public static boolean createDirectoryToLocalStorage(String dir) {
        boolean result = false;
        SafFile3 sf = new SafFile3(context, dir);
        return createDirectoryToLocalStorage(sf);
    }

    final public static boolean createDirectoryToLocalStorage(SafFile3 sf) {
        boolean result = false;
        if (!sf.exists()) {
            result = sf.mkdirs();
        }
        return result;
    }










    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    static public ArrayList<File> sortFileLIst(File[] f_array) {
        long b_time=System.currentTimeMillis();
        ArrayList<File>f_list=new ArrayList<File>(f_array.length);
        for(File item:f_array) f_list.add(item);
        Collections.sort(f_list, new Comparator<File>(){
            @Override
            public int compare(File o1, File o2) {
                String l_key=(o1.isDirectory()?"D":"F").concat(o1.getName());
                String r_key=(o2.isDirectory()?"D":"F").concat(o2.getName());
                return l_key.compareToIgnoreCase(r_key);
            }
        });
        return f_list;
    }
//
//    static public int syncCopyInternalToExternal(String from_path, String to_path) {
//
//        File mf = new File(from_path);
//
//        if(false) {
//            File file = new File("/storage/emulated/0/log/hrslog");
//
//            // String to_path = mGp.safMgr.getSdcardRootPath();
//
//            String fromBase = "/storage/emulated/0/log";
//            String fromPath = "/storage/emulated/0/log";
//            String toBase = "/storage/10B6-7648/柔柔弱弱";
//            String toPath = "/storage/10B6-7648/柔柔弱弱/hrslog";
//
//            UsbHelper.moveCopyInternalToExternal(false, fromBase, fromPath, file, toBase, toPath);
//
//
//            return 1;
//
//        }
//
//
//
//
//        int sync_result=moveCopyInternalToExternal(false, from_path, from_path, mf, to_path, to_path);
//
//        return sync_result;
//    }





    static final public String[] getMp4ExifDateTime(InputStream fis)  {
        String[] result=null;
        try {
            Metadata metaData;
            metaData = ImageMetadataReader.readMetadata(fis);
            Mp4Directory directory=null;
            if (metaData!=null) {
                directory=metaData.getFirstDirectoryOfType(Mp4Directory.class);
                if (directory!=null) {
                    String date = directory.getString(Mp4Directory.TAG_CREATION_TIME);
                    result=parseDateValue(date);
                    if (result!=null && result[0].startsWith("1904")) result=null;
                }
            }
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    static private byte[] readExifData(BufferedInputStream bis, int read_size) throws IOException {
        byte[] buff=new byte[read_size];
        int rc=bis.read(buff,0,read_size);
        if (rc>0) return buff;
        else return null;
    }

    static final private String[] parseDateValue(String date_val) {
        String[] result=null;
        if (date_val!=null) {
            String[] dt=date_val.split(" ");
            int year=Integer.parseInt(dt[5]);
            int month=0;
            int day=Integer.parseInt(dt[2]);
            if      (dt[1].equals("Jan")) month=0;
            else if (dt[1].equals("Feb")) month=1;
            else if (dt[1].equals("Mar")) month=2;
            else if (dt[1].equals("Apr")) month=3;
            else if (dt[1].equals("May")) month=4;
            else if (dt[1].equals("Jun")) month=5;
            else if (dt[1].equals("Jul")) month=6;
            else if (dt[1].equals("Aug")) month=7;
            else if (dt[1].equals("Sep")) month=8;
            else if (dt[1].equals("Oct")) month=9;
            else if (dt[1].equals("Nov")) month=10;
            else if (dt[1].equals("Dec")) month=11;

            String[] tm=dt[3].split(":");
            int hours=  Integer.parseInt(tm[0]);
            int minutes=Integer.parseInt(tm[1]);
            int seconds=Integer.parseInt(tm[2]);

            Calendar cal=Calendar.getInstance() ;
            TimeZone tz=TimeZone.getDefault();
            tz.setID(dt[3]);
            cal.setTimeZone(tz);
            cal.set(year, month, day, hours, minutes, seconds);
            result= StringUtil.convDateTimeTo_YearMonthDayHourMinSec(cal.getTimeInMillis()).split(" ");
        }
        return result;
    }

    static public String[] getExifDateTime(InputStream fis) {
        BufferedInputStream bis=new BufferedInputStream(fis, 1024*32);
        String[] result=null;
        try {
            byte[] buff=readExifData(bis, 2);
            if (buff!=null && buff[0]==(byte)0xff && buff[1]==(byte)0xd8) { //JPEG SOI
                while(buff!=null) {// find dde1 jpeg segemnt
                    buff=readExifData(bis, 4);
                    if (buff!=null) {
                        if (buff[0]==(byte)0xff && buff[1]==(byte)0xe1) { //APP1マーカ
                            int seg_size=getIntFrom2Byte(false, buff[2], buff[3]);
                            buff=readExifData(bis, 14);
                            if (buff!=null) {
                                boolean little_endian=false;
                                if (buff[6]==(byte)0x49 && buff[7]==(byte)0x49) little_endian=true;
                                int ifd_offset=getIntFrom4Byte(little_endian, buff[10], buff[11], buff[12], buff[13]);

                                byte[] ifd_buff=new byte[seg_size+ifd_offset];
                                System.arraycopy(buff,6,ifd_buff,0,8);
                                buff=readExifData(bis, seg_size);
                                if (buff!=null) {
                                    System.arraycopy(buff,0,ifd_buff,8,seg_size);
                                    result=process0thIfdTag(little_endian, ifd_buff, ifd_offset);
                                    break;
                                } else {
                                    // stwa.util.addDebugMsg(1,"W","Read Exif date and time failed, because unpredical EOF reached.");
                                    return null;
                                }
                            } else {
                                //stwa.util.addDebugMsg(1,"W","Read Exif date and time failed, because unpredical EOF reached.");
                                return null;
                            }
                        } else {
                            int offset=((int)buff[2]&0xff)*256+((int)buff[3]&0xff)-2;
                            if (offset<1) {
                                //stwa.util.addDebugMsg(1,"W","Read Exif date and time failed, because invalid offset.");
                                return null;
                            }
                            buff=readExifData(bis, offset);
                        }
                    } else {
                        //stwa.util.addDebugMsg(1,"W","Read Exif date and time failed, because unpredical EOF reached.");
                        return null;
                    }
                }

            } else {
                //stwa.util.addDebugMsg(1,"W","Read exif date and time failed, because Exif header can not be found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    static private String[] process0thIfdTag(boolean little_endian, byte[]ifd_buff, int ifd_offset) {
        int count=getIntFrom2Byte(little_endian, ifd_buff[ifd_offset+0], ifd_buff[ifd_offset+1]);
        int i=0;
        int ba=ifd_offset+2;
        String[] result=null;
        while(i<count) {
            int tag_number=getIntFrom2Byte(little_endian, ifd_buff[ba+0], ifd_buff[ba+1]);
            int tag_offset=getIntFrom4Byte(little_endian, ifd_buff[ba+8], ifd_buff[ba+9], ifd_buff[ba+10], ifd_buff[ba+11]);

            if (tag_number==(0x8769&0xffff)) {//Exif IFD
                result=processExifIfdTag(little_endian, ifd_buff, tag_offset);
                break;
            }
            ba+=12;
            i++;
        }
        return result;
    }
    static private int getIntFrom2Byte(boolean little_endian, byte b1, byte b2) {
        int result=0;
        if (little_endian) result=((int)b2&0xff)*256+((int)b1&0xff);
        else result=((int)b1&0xff)*256+((int)b2&0xff);
        return result;
    }

    static private int getIntFrom4Byte(boolean little_endian, byte b1, byte b2, byte b3, byte b4) {
        int result=0;
        if (little_endian) result=((int)b4&0xff)*65536+((int)b3&0xff)*4096+((int)b2&0xff)*256+((int)b1&0xff);
        else result=((int)b1&0xff)*65536+((int)b2&0xff)*4096+((int)b3&0xff)*256+((int)b4&0xff);
        return result;
    }

    static private boolean isMovieFile(String fp) {
        boolean result=false;
        if (fp.toLowerCase().endsWith(".mp4") ||
                fp.toLowerCase().endsWith(".mov")) result=true;
        return result;
    }
    static private boolean isPictureFile(String fp) {
        boolean result=false;
        if (fp.toLowerCase().endsWith(".gif") ||
                fp.toLowerCase().endsWith(".jpg") ||
                fp.toLowerCase().endsWith(".jpeg") ||
                fp.toLowerCase().endsWith(".jpe") ||
                fp.toLowerCase().endsWith(".png")) result=true;
        return result;
    }







//
//
//    /**
//     * 读取 USB 内文件夹下文件列表
//     *
//     * @param usbFolder usb文件夹
//     * @return 文件列表
//     */
//    public ArrayList<UsbFile> getUsbFolderFileList(UsbFile usbFolder) {
//        //更换当前目录
//        currentFolder = usbFolder;
//        ArrayList<UsbFile> usbFiles = new ArrayList<>();
//        try {
//            Collections.addAll(usbFiles, usbFolder.listFiles());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return usbFiles;
//    }
//
//
//    /**
//     * 复制文件到 USB
//     *
//     * @param targetFile       需要复制的文件
//     * @param saveFolder       复制的目标文件夹
//     * @param progressListener 下载进度回调
//     * @return 复制结果
//     */

    static private String[] processExifIfdTag(boolean little_endian, byte[]ifd_buff, int ifd_offset) {
        int count=getIntFrom2Byte(little_endian, ifd_buff[ifd_offset+0], ifd_buff[ifd_offset+1]);
        int i=0;
        int ba=ifd_offset+2;
        String[] date_time=new String[2];
        while(i<count) {
            int tag_number=getIntFrom2Byte(little_endian, ifd_buff[ba+0], ifd_buff[ba+1]);
            int tag_offset=getIntFrom4Byte(little_endian, ifd_buff[ba+8], ifd_buff[ba+9], ifd_buff[ba+10], ifd_buff[ba+11]);
            if (tag_number==(0x9003&0xffff)) {//Date&Time TAG
                String[] date = new String(ifd_buff, tag_offset, 19).split(" ");
                if (date.length==2) {
                    date_time[0]=date[0].replaceAll(":", "/");//Date
                    date_time[1]=date[1];//Time
                    break;
                }
            }
            ba+=12;
            i++;
        }
        return date_time;
    }


















//    /**
//     * 复制 USB文件到本地
//     *
//     * @param targetFile       需要复制的文件
//     * @param savePath         复制的目标文件路径
//     * @param progressListener 下载进度回调
//     * @return 复制结果
//     */



    private String buildStorageDir(String uuid, String dir) {
        String base="";
        if (uuid.equals(SafFile3.SAF_FILE_PRIMARY_UUID)) base= Environment.getExternalStorageDirectory().getPath();
        else base="/storage/"+uuid;
        if (dir.equals("")) return base;
        else {
            if (dir.startsWith("/")) return base + dir;
            else return base + "/" + dir;
        }
    }



    public interface DownloadProgressListener {
    }


    final public static boolean createDirectoryToInternalStorage(String dir) {
        boolean result = false;
        File lf = new File(dir);
        if (!lf.exists()) {
                result = lf.mkdirs();
        }
        return result;
    }



    static public int syncCopyInternalToInternal(String from_path, String to_path) throws IOException {
        File mf = new File(from_path);
        return moveCopyInternalToInternal(false, from_path, from_path, mf, to_path, to_path);
    }


    static public int moveCopyInternalToInternal(boolean move_file,
                                                 String from_base, String from_path, File mf, String to_base, String to_path) throws IOException {
        int sync_result = 0;
        File tf;
        String t_from_path = from_path.substring(from_base.length());
        if (t_from_path.startsWith("/")) t_from_path = t_from_path.substring(1);
        if (mf.exists()) {
            if (mf.isDirectory()) { // Directory copy
                if (mf.canRead()) {
                    createDirectoryToInternalStorage(to_path);
                }
                File[] children_array = mf.listFiles();
                if (children_array != null) {
                    ArrayList<File> children=sortFileLIst(children_array);
                    for (File element : children) {
                        if (sync_result == 0) {
                            if (!element.getName().equals(".android_secure")) {
                                //										Log.v("","from="+from_path);
                                //										Log.v("","to  ="+to_path);
                                if (!from_path.equals(to_path)) {
                                    sync_result = moveCopyInternalToInternal(move_file, from_base, from_path + "/" + element.getName(),
                                            element, to_base, to_path + "/" + element.getName());
                                }
                            }
                        }
                    }
                }
            } else { // file copy

                String parsed_to_path=to_path;
                copyFileInternalToInternal(from_path.substring(0, from_path.lastIndexOf("/")),
                        mf, parsed_to_path.substring(0, parsed_to_path.lastIndexOf("/")), mf.getName());
            }
        }


        return sync_result;
    }



    static public int copyFileInternalToInternal(String from_dir, File mf, String to_dir, String file_name) throws IOException {

        String to_file_dest = to_dir + "/" + file_name;

        String to_dir_tmp = "";
        if (Build.VERSION.SDK_INT>=30) {
            to_dir_tmp=getTempFileDirectory(to_dir);
        } else {
            to_dir_tmp=getTempFileDirectory(to_dir);
//            to_dir_tmp = stwa.gp.internalRootDirectory+"/"+APP_SPECIFIC_DIRECTORY+"/cache";
        }

        File tmp_dir=new File(to_dir_tmp);
        if (!tmp_dir.exists()) tmp_dir.mkdirs();
        String temp_path = to_dir_tmp+"/"+"temp_file.tmp";

        File temp_file = new File(temp_path);
        File t_dir = new File(to_dir);
        if (!t_dir.exists()) t_dir.mkdirs();

        FileInputStream is = new FileInputStream(mf);
        FileOutputStream os = new FileOutputStream(temp_file);

        int result=copyFile(from_dir, to_dir, file_name, mf.length(), is, os);
        if (result==1) {
            temp_file.delete();
            return 1;
        }
        File out_dest = new File(to_file_dest);
        if (out_dest.exists()) out_dest.delete();
        temp_file.renameTo(out_dest);

        return 0;
    }


    static public String getTempFileDirectory(String to_dir) {
        String result="";
        if (to_dir.startsWith("/storage/emulated/0")) {
            result="/storage/emulated/0/"+APP_SPECIFIC_DIRECTORY+"/cache";
        } else {
            String[] dir_parts=to_dir.split("/");
            result="/"+dir_parts[1]+"/"+dir_parts[2]+"/"+APP_SPECIFIC_DIRECTORY+"/cache";
        }
        return result;
    }
    private void prepareMediaScanner() {
        mediaScanner = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
//                if (mGp.settingDebugLevel >= 1)
//                    mStwa.util.addDebugMsg(1, "I", "MediaScanner connected.");
            }
            @Override
            public void onScanCompleted(final String fp, final Uri uri) {
//                if (mGp.settingDebugLevel >= 1)
//                    mStwa.util.addDebugMsg(1, "I", "MediaScanner scan completed. fn=", fp, ", Uri=" + uri);
            }
        });
        mediaScanner.connect();
    }

      public static void scanMediaFile(SafFile3 sf) {
          String fp=sf.getParent();
//        try {
//            MediaScannerConnection.scanFile(stwa.context, new String[]{fp}, null, null);
//            stwa.util.addDebugMsg(1, "I", "Media scanner invoked, fp="+fp);
//        } catch(Exception e) {
//            stwa.util.addLogMsg("W","Media scanner scan failed, fp="+fp);
//            stwa.util.addLogMsg("W",e.getMessage());
//        }
        if (FileTool.getFileExtention(fp).equals("")) {
            //stwa.util.addDebugMsg(1, "I", "MediaScanner scan ignored because file extention does not exists. fp=", fp);
            return;
        }
        if (!UsbHelper.getInstance().getMediaScanner().isConnected()) {
            //stwa.util.addLogMsg("W", fp, "Media scanner not invoked, mdeia scanner was not connected.");
            return;
        }
       // stwa.util.addDebugMsg(1, "I", "MediaScanner scan request issued. fp=", fp);
          UsbHelper.getInstance().getMediaScanner().scanFile(fp, null);
    }






    public static final String INTERNAL_SD = "INTERNAL_SD";
    public static final String EXTERNAL_SD = "EXTERNAL_SD";
    public static final String EXTERNAL_USB = "EXTERNAL_USB";

    // 获取机器所有的存储设备地址
    public Map<String, File> getAllStorageLocations() {
        Map<String, File> map = new HashMap<String, File>(10);
        // 获取内部存储路径,存入INTERNAL_SD中
        map.put(INTERNAL_SD, Environment.getExternalStorageDirectory());

        Scanner scanner = null;
        int mUsbAvailCnt=0;
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                scanner = new Scanner(new BufferedReader(new FileReader("/proc/mounts")));//new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    // 获取SD卡对应路径,存入EXTERNAL_SD中
                    if (line.startsWith("/dev/block/vold/public:179")) {
                        String[] lineElements = line.split(" ");
                        String element = "/storage/" + lineElements[1].substring(lineElements[1].lastIndexOf("/"));
                        Log.d(TAG, "EXTERNAL_SD: " + element);

                        File ff = new File(element);
                        if (ff.exists() && ff.isDirectory()) {
                            Log.d(TAG, "EXTERNAL_SD +=" + ff.getPath());
                            map.put(EXTERNAL_SD, ff);
                        }
                        // 获取Upan对应路径,存入EXTERNAL_USB中
                    } else if (line.startsWith("/dev/block/vold/public:8")) {
                        String[] lineElements = line.split(" ");
                        String element = "/storage/" + lineElements[1].substring(lineElements[1].lastIndexOf("/"));
                        File ff = new File(element);
                        Log.d(TAG, "EXTERNAL_USB: " + element);
                        Log.d(TAG, "EXTERNAL_USB: " + line);
                        if (ff.exists() && ff.isDirectory()) {
                            Log.d(TAG, "EXTERNAL_USB +=" + ff.getPath());
                            mUsbAvailCnt++;
                            String key = EXTERNAL_USB + String.valueOf(mUsbAvailCnt);
                            map.put(key, ff);
                        }
                        SafFile3 sf = new SafFile3(context,ff.getPath());
                        long h = sf.length();
                        int b = 3;
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return map;
    }


    public String getUSBStorageLocations() {
        Map<String, File> map = new HashMap<String, File>(10);
        // 获取内部存储路径,存入INTERNAL_SD中
        map.put(INTERNAL_SD, Environment.getExternalStorageDirectory());

        Scanner scanner = null;
        int mUsbAvailCnt=0;
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                scanner = new Scanner(new BufferedReader(new FileReader("/proc/mounts")));//new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    // 获取SD卡对应路径,存入EXTERNAL_SD中
                    if (line.startsWith("/dev/block/vold/public:179")) {

                        // 获取Upan对应路径,存入EXTERNAL_USB中


                        //LogHelper.getInstance().d("line:" + line);
                    } else if (line.startsWith("/dev/block/vold/public:8")) {
                        String[] lineElements = line.split(" ");
                        String element = "/storage" + lineElements[1].substring(lineElements[1].lastIndexOf("/"));
                        File ff = new File(element);
//                        Log.d(TAG, "EXTERNAL_USB: " + element);
//                        Log.d(TAG, "EXTERNAL_USB: " + line);
//                        if (ff.exists() && ff.isDirectory()) {
//                            Log.d(TAG, "EXTERNAL_USB +=" + ff.getPath());
//                            mUsbAvailCnt++;
//                            String key = EXTERNAL_USB + String.valueOf(mUsbAvailCnt);
//                            map.put(key, ff);
//                        }
//                        SafFile3 sf = new SafFile3(context,ff.getPath());
//                        long h = sf.length();
//                        int b = 3;


//                        Vector<Vector<Object>>s = getRowData();
///mnt/media_rw/AF17-191F /mnt/pass_through/0/AF17-191F  /storage/AF17-191F



                        ///mnt/media_rw/C9A1-BADA  /storage/C9A1-BADA
//                        StatFs statFs = new StatFs("/storage/C9A1-BADA");
//                        long avaibleSize = statFs.getAvailableBytes();//获取U盘可用空间
//                        long totalSize = statFs.getTotalBytes();
//
//                        long d = (long) (totalSize/1024.0/1024/1024);
//
//                        LogHelper.getInstance().d("line000:" + line);
//                        LogHelper.getInstance().d("element:" + element);
                        return ff.getPath();
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return "";
    }



//    public static String getSDCardStoragePath() {
//        Map<String, File> map = getAllStorageLocations();
//        if(map.get(EXTERNAL_SD) == null) {
//            return "null/";
//        }
//        else {
//            return map.get(EXTERNAL_SD).getPath() + "/";
//        }
//    }
//
//    public static String getUSBStoragePath() {
//        int index = getUSBStoragePathIndex();
//        String finalpath = "null";
//        if(mUsbPathArray != null && mUsbPathArray.length > 0) {
//            finalpath = mUsbPathArray[index];
//        }
//        Log.d(TAG, "finalpath: " + finalpath);
//        return finalpath + "/";
//    }


    private String[] getExtSDCardPath() {
        Log.d("SDRemount ","getExtSDCardPath");
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            return (String[])invoke;
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    public String[] sdCardRemounted()
    {
        Log.d("SDRemount ","sdCardRemounted checked");
        String[] data = getExtSDCardPath();
        List<String> list =new ArrayList<>();

        if (data.length > 0){
            for(String path : data){
                if(checkMounted(mContext,path)){
                    list.add(path);
                }
            }
        }

        int count = list.size();
        String[] result =new String[count];
        /*for (int i = 0;i < count; i++)
        {
            result[i]=list.get(i);
        }*/
        return  list.toArray(result);
    }

    private boolean checkMounted(Context context, String mountPoint) {
        if (mountPoint == null) {
            Log.d("SDRemount ","mountPoint == null");
            return false;
        }
        //StorageManager storageManager = (StorageManager) context
        //.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = storageManager.getClass().getMethod(
                    "getVolumeState", String.class);
            String state = (String) getVolumeState.invoke(storageManager,
                    mountPoint);
            //Log.d("SDRemount ","SDCard path " + mountPoint +" and state " +state);
            StatFs statFs = new StatFs(mountPoint);
            long blockSize=statFs.getBlockSizeLong();
            long availableSize=statFs.getAvailableBlocksLong();
            long totalSize=statFs.getBlockCountLong();
            Log.d("SDRemount ","SDCard path " + mountPoint +" and state " +state +
                    " total size "+ totalSize*blockSize + " available size " + availableSize*blockSize);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getTotalSize(String path){
        StatFs statFs = new StatFs(path);
        long blockSize=statFs.getBlockSizeLong();
        //long availableSize=statFs.getAvailableBlocksLong();
        long totalSize=statFs.getBlockCountLong();
        long total = blockSize * totalSize;
        Log.d("SDRemount ","SDCard path " + path +" total size "+ total);

        return total;
    }

    public long getAvailableSize(String path){
        StatFs statFs = new StatFs(path);
        long blockSize=statFs.getBlockSizeLong();
        long availableSize=statFs.getAvailableBlocksLong();
        //long totalSize=statFs.getBlockCountLong();
        long total = blockSize * availableSize;
        Log.d("SDRemount ","SDCard path " + path +" total size "+ total);

        return total;
    }



    /**获取TF卡路径*/
    public String getTFDir() {
        String sdcardDir = null;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((Integer) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((Boolean) isSd.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            if (sdcardDir == null) {
                Log.w(TAG, "sdcardDir null");
                return null;
            } else {
                Log.i(TAG, "sdcardDir " + sdcardDir + File.separator);
                return sdcardDir + File.separator;
            }
        } catch (Exception e) {
            Log.i(TAG, "sdcardDir e " + e.getMessage());
            e.printStackTrace();
        }
        Log.w(TAG, "sdcardDir null");
        return null;
    }

    /**获取USB的路径*/
    public String getUsbDir() {
        String sdcardDir = null;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isUsb = diskInfoClazz.getMethod("isUsb");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                Log.w(TAG, "disk path " + path.get(volumeInfo));
                if ((Integer) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    Log.w(TAG, "is usb " + isUsb.invoke(disk));
                    if (disk != null) {
                        if ((Boolean) isUsb.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            if (sdcardDir == null) {
                Log.w(TAG, "usbpath null");
                return null;
            } else {
                Log.i(TAG, "usbpath " + sdcardDir + File.separator);
                return sdcardDir + File.separator;
            }
        } catch (Exception e) {
            Log.i(TAG, "usbpath e " + e.getMessage());
            e.printStackTrace();
        }
        Log.w(TAG, "usbpath null");
        return null;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showStorageVolumes() {
        StorageStatsManager storageStatsManager = (StorageStatsManager) ((AppCompatActivity)(context)).getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) ((AppCompatActivity)(context)).getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null || storageStatsManager == null) {
            return;
        }
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        for (StorageVolume storageVolume : storageVolumes) {
            final String uuidStr = storageVolume.getUuid();
            UUID uuid = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                uuid = storageVolume.getStorageUuid();
            }else{
                uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
            }
            //final UUID uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
            try {
                Log.d("AppLog", "storage:" + uuid + " : " + storageVolume.getDescription(context) + " : " + storageVolume.getState());
                Log.d("AppLog", "getFreeBytes:" + Formatter.formatShortFileSize(context, storageStatsManager.getFreeBytes(uuid)));
                Log.d("AppLog", "getTotalBytes:" + Formatter.formatShortFileSize(context, storageStatsManager.getTotalBytes(uuid)));
            } catch (Exception e) {
                // IGNORED
            }
        }
    }

    public Vector<Vector<Object>> getRowData() {
        String commond = "reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\USBSTOR\\Enum";
        Vector<Vector<Object>> data = null;
        try {
            //获取注册表信息
            Process ps = null;
            ps = Runtime.getRuntime().exec(commond);
            ps.getOutputStream().close();
            InputStreamReader i = new InputStreamReader(ps.getInputStream());
            String line;
            BufferedReader ir = new BufferedReader(i);
            int count = 0;
            data = new Vector<Vector<Object>>();
            //将信息分离出来
            while ((line = ir.readLine()) != null) {                if (line.contains("USB\\VID")) {
                count++;
                for (String s : line.split("    ")) {
                    if (s.contains("USB\\VID")) {
                        Vector<Object> v = new Vector<Object>();
                        for (String ss : s.split("\\\\")) {
                            if (ss.contains("VID")) {
                                for (String sss : ss.split("&")) {
                                    v.add(sss);
                                }
                            } else if (ss.contains("USB")) {
                                v.add(ss + count);
                            } else {
                                v.add(ss);
                            }
                        }
                        data.add(v);
                    }
                }
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }



    void  getStorageVolumesAccessState(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        StorageStatsManager storageStatsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        }
        for (StorageVolume storageVolume : storageVolumes) {
            Long freeSpace = 0L;
            Long totalSpace = 0L;
            String path = getPath(context, storageVolume);
            if (storageVolume.isPrimary()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        totalSpace = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        freeSpace = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (path != null) {


                Uri uri = DocumentsContract.buildTreeDocumentUri(
                        "com.android.externalstorage.documents",
                        storageVolume.getUuid()
                );

                Uri docTreeUri = DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        DocumentsContract.getTreeDocumentId(uri)
                );

                try {
                  ParcelFileDescriptor  pfd  = context.getContentResolver().openFileDescriptor(docTreeUri,"r");

                    try {
                        StructStatVfs vs = Os.fstatvfs(pfd.getFileDescriptor());
                    } catch (ErrnoException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }



                //new StructStatVfs(storageVolume.getfd)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        totalSpace = storageStatsManager.getTotalBytes(UUID.fromString(storageVolume.getUuid()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                File file = new  File(path);
                freeSpace = file.getFreeSpace();
                totalSpace = file.getTotalSpace();
            }
            Long usedSpace = totalSpace - freeSpace;
            String freeSpaceStr = Formatter.formatFileSize(context, freeSpace);
            String totalSpaceStr = Formatter.formatFileSize(context, totalSpace);
            String usedSpaceStr = Formatter.formatFileSize(context, usedSpace);
            Log.d("AppLog", "${storageVolume.getDescription(context)} - path:$path total:$totalSpaceStr used:$usedSpaceStr free:$freeSpaceStr");
        }
    }




    private String getPath(Context context,StorageVolume storageVolume) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (storageVolume.getDirectory() != null) {
                if (storageVolume.getDirectory().getAbsolutePath() != null) {
                    return storageVolume.getDirectory().getAbsolutePath();
                }
            }
        }
        try {

            Class storageVolumeClazz =Class

                    .forName("android.os.storage.StorageVolume");

            Method getPath =storageVolumeClazz.getMethod("getPath");

            String storagePath = (String)getPath.invoke(storageVolume);

            return storagePath;
        } catch (Exception e) {
        }
        try {
            Class storageVolumeClazz =Class

                    .forName("android.os.storage.StorageVolume");

            Method getPath =storageVolumeClazz.getMethod("getPathFile");

            File file = (File)getPath.invoke(storageVolume);

            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        File [] extDirs = context.getExternalFilesDirs(null);
        for (File extDir : extDirs) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            StorageVolume fileStorageVolume =  storageManager.getStorageVolume(extDir);
            if(fileStorageVolume == null){
                continue;
            }
            if (fileStorageVolume.equals(storageVolume)) {
                File file = extDir;
                while (true) {
                    File parent = file.getParentFile();
                    if(parent == null) {
                        return file.getAbsolutePath();
                    }
                    StorageVolume parentStorageVolume = storageManager.getStorageVolume(parent);
                    if(parentStorageVolume == null) {
                        return file.getAbsolutePath();
                    }
                    if (!parentStorageVolume.equals(storageVolume)) {
                        return file.getAbsolutePath();
                    }
                    file = parent;
                }
            }
        }
        try {
            Parcel parcel = Parcel.obtain();
            storageVolume.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            parcel.readString();
            return parcel.readString();
        } catch (Exception e) {
        }
        return null;
    }




    // 获取 StorageVolume 对象
    public void eject(Context mContext) {
        StorageManager mStorageManager = (StorageManager)
                mContext.getSystemService(Context.STORAGE_SERVICE);

        UsbManager manager = (UsbManager)
                mContext.getSystemService(Context.USB_SERVICE);
        HashMap devs = manager.getDeviceList();

        //UsbDevice dev = (UsbDevice) devs.get("");


        unMount();
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            //暂且称之为获取usb列表
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            //拿到format方法
            Method format = mStorageManager.getClass().getMethod("format", String.class);

            Method unmount = mStorageManager.getClass().getMethod("unmount", String.class);
            //拿到StorageVolume 的getpath方法
            Method getPath = storageVolumeClazz.getMethod("getPath");
            //Method getInternalPath = storageVolumeClazz.getMethod("getInternalPath");
            //拿到StorageVolume 的getId方法
            Method getId = storageVolumeClazz.getMethod("getId");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            //Object result = getVolumeList.invoke(mStorageManager);

            List result = mStorageManager.getStorageVolumes();

            {
                Method getVolumes = mStorageManager.getClass().getMethod("getVolumes");
                Object result1 = getVolumeList.invoke(mStorageManager);
                Method getDisks = mStorageManager.getClass().getMethod("getDisks");
                Object result2 = getDisks.invoke(mStorageManager);

                int a = 3;

            }


            //final int length = Array.getLength(result);
            //遍历列表
            for (int i = 0; i < result.size(); i++) {
                Object storageVolumeElement = result.get(i);//Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);

                // String internalPath = (String) getInternalPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                //如果当前的usb地址和我们定义的地址一样的话，就操作它

                StorageVolume sv = (StorageVolume) storageVolumeElement;
                String getState = sv.getState();


                String usbPath = getUsbUUid();
                if (removable && path.contains(usbPath)) {
                    String id = (String) getId.invoke(storageVolumeElement);
                    //格式化它！！
                    //java.lang.SecurityException: android.permission.MOUNT_FORMAT_FILESYSTEMS: Neither user 10409 nor current process has android.permission.MOUNT_FORMAT_FILESYSTEMS.
                    //unmount.invoke(mStorageManager, id);

                    boolean res1 = mStorageManager.isObbMounted("/sys//devices/platform/ff200000.hisi_usb/ff100000.dwc3/xhci-hcd.0.auto/usb3/3-1/3-1:1.0/host1/target1:0:0/1:0:0:0/block/sde");
                    //boolean res2 =  mStorageManager.isObbMounted(internalPath);

                    boolean res = mStorageManager.unmountObb(path, true, new OnObbStateChangeListener() {
                        @Override
                        public void onObbStateChange(String path, int state) {
                            super.onObbStateChange(path, state);

                            int s = state;
                        }
                    });

                    int b = 9;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public void unMount() {
            try {
                Log.v("DWXD", "issfvs");
                StorageManager mSD = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
                List<Object> list = (List<Object>) StorageManager.class.getMethod("getVolumes").invoke(mSD);
                Log.v("DWXD", "list" + list.toString());
                for (int i = 0; i < list.size(); i++) {
                    Object volume = list.get(i);
                    if (volume != null) {
                        String id = (String) Class.forName("android.os.storage.VolumeInfo").getMethod("getId").invoke(volume);
                        Log.v("DWXD", "is " + id);
                        int type = (int) Class.forName("android.os.storage.VolumeInfo").getMethod("getType").invoke(volume);
                        Log.v("DWXD", "is " + type);
                        // public
                        if (type == 0) {
                            StorageManager.class.getMethod("unmount", String.class).invoke(mSD, id);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("DWXD", e.getStackTrace().toString());

            }
        }

}
