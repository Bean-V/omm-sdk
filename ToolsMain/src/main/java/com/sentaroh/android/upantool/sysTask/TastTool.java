package com.sentaroh.android.upantool.sysTask;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.room.Room;

import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.Activity_dupfiles;
import com.sentaroh.android.upantool.Activity_migrate_statu;
import com.sentaroh.android.upantool.FileTool;
import com.sentaroh.android.upantool.R;
import com.sentaroh.android.upantool.TimeUtil;
import com.sentaroh.android.upantool.UsbHelper;
import com.sentaroh.android.upantool.languagelib.MultiLanguageUtil;
import com.sentaroh.android.upantool.record.Migration1To2;
import com.sentaroh.android.upantool.record.Record;
import com.sentaroh.android.upantool.record.RecordDao;
import com.sentaroh.android.upantool.record.RecordDatabase;
import com.sentaroh.android.upantool.record.RecordTool;
import com.zhihu.matisse.internal.model.SelectedItemCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TastTool {


    private static TastTool logUtil;
    private List allDatas;
    private ArrayList dataList;
    private Boolean migrateing;
    private int reCopyCount;
    private boolean cancelMigrate;
    private ArrayList<Object> reCopyFilePaths;
    private String sysNode;
    private String TAG = "TastTool";

    public boolean isSys() {
        return isSys;
    }

    private boolean isSys;

    private boolean pause;
    private boolean sysOneFinsh = true;

    public List getAllDatas() {
        return allDatas;
    }

    public List getUnFinsh() {
        return unFinsh;
    }

    private List unFinsh;


    public ArrayList getDataResList() {
        return dataResList;
    }


    private ArrayList dataResList = new ArrayList();


    private ArrayList sysedList = new ArrayList();

    //单例模式初始化
    public static TastTool getInstance() {
        if (logUtil == null) {
            logUtil = new TastTool();

        }
        return logUtil;
    }

    private Context context;
    private TastDao dao;

    public TastDatabase getDb() {
        return db;
    }

    private TastDatabase db;
    public void initData(Context c){

        context = c;
        db = Room.databaseBuilder(c.getApplicationContext(),
                TastDatabase.class, "task").build();

        dao = db.tastDao();


//
//        new Thread(new Runnable() {
//
//
//
//            @Override
//            public void run() {
//
//                allDatas = getAllTransFiles();
//
//                unFinsh = getUnFinshData();
//
//
//            }
//        }).start();

    }


    public List getAllTransFiles(){
        List datas = dao.loadAllByUuId(UsbHelper.getInstance().getUsbUUid());
        return datas;
    }


    public List getUnFinshData() {

        ArrayList datas = new ArrayList();

        ArrayList datas1 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(), 0);
        ArrayList datas2 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(), 1);
        ArrayList datas3 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(), 3);

        if (datas1 != null) {
            datas.addAll(datas1);
        }
        if (datas2 != null) {
            datas.addAll(datas2);
        }
        if (datas3 != null) {
            datas.addAll(datas3);
        }
        return datas;
    }



    public void inserRecord(SysTask task){
        dao.insertAll(task);
    }

    public void updateRecord(SysTask task){
        dao.updatetask(task);
    }



    public void deleteTaskUnExists(String sysNode){
        String android_id = Settings.Secure.ANDROID_ID;

        android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        List<SysTask> datas = dao.findBySysNode(UsbHelper.getInstance().getUsbUUid(),android_id,sysNode);
        for(SysTask st : datas){
            if(pause){
                return;
            }
            SafFile3 sf = new SafFile3(context,st.getCurrentToDir(context) + "/" + st.name);
            if(sf.exists()) {

                FileTool.copyWithPath(sf.getPath(), st.getCurrentToArchiveDir(context) + "/" + st.name);

                sf.deleteIfExists();
                dao.delete(st);
            }
        }
    }




    public void sys(){

        isSys = true;

        while(true){

            SystemClock.sleep(2000);
            if(needToSys()) {

                sysedList = (ArrayList) dao.loadAllByUuId(UsbHelper.getInstance().getUsbUUid());

                changeNameOfUsbSysFolder();

                sysOneFinsh = false;

                sysNode = TimeUtil.getCurrentTime("yyyyMMddhhmmss");
                buildTask();
                transFiles();
                deleteUnExsit();
                sysOneFinsh = true;
            }
        }


    }


    public SysTask getContactSysTask() {

        if(contactSysTask == null){
            String android_id = Settings.Secure.ANDROID_ID;

            android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

            String migratePath = null;

            String migratePath_en = null;

            String migratePath_zh = null;
            String migratePath_ja = null;

            String migratePath_archive_zh = null;

            String migratePath_archive_en = null;
            String migratePath_archive_ja = null;

            String sysRootName =  getFolderName(Build.MODEL + "[" + android_id + "]" + "的同步文件夹");//"";//TimeUtil.getCurrentTime("yyyy-MM-dd")
            migratePath = UsbHelper.getInstance().getUsbRootPath() + "/" + sysRootName  + "/" +  UsbHelper.getInstance().getUsbUUid();


            migratePath_zh = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "的同步文件夹"  + "/" +  UsbHelper.getInstance().getUsbUUid();

            migratePath_en = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "'s synchronization folder"  + "/" +  UsbHelper.getInstance().getUsbUUid();

            migratePath_ja = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "の同期フォルダ"  + "/" +  UsbHelper.getInstance().getUsbUUid();


            migratePath_archive_zh = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "的同步文件夹"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "已归档的文件";

            migratePath_archive_en = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "'s synchronization folder"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "Archive folder";

            migratePath_archive_ja = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "の同期フォルダ"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "アーカイブフォルダ";


            String disk_uuid = UsbHelper.getInstance().getUsbUUid();
            String desDir = migratePath + "/" + "Contacts";

            File file = null;
            try {
                file = FileTool.createContactFileAndToUsb(new SelectedItemCollection.ProgressListioner() {
                    @Override
                    public void progress(int progress) {
                    }

                    @Override
                    public void finsh(List<String> filePath) {
                    }
                },context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SysTask task = new SysTask();
            task.path = file.getAbsolutePath();
            task.name = file.getName();
            task.disk_uuid = disk_uuid;
            task.androidId = android_id;
            task.type = "Contacts";
            task.toDirPath = desDir;
            task.toArchiveDirPath = migratePath + "/" +"Archive folder"+"/Contacts";

            task.toDirPath_CH = migratePath + "/通讯录";
            task.toDirPath_EN = migratePath + "/Contacts";
            task.toDirPath_JA = migratePath + "/アドレス帳";
            task.toArchiveDirPath_CH = migratePath_archive_zh+"/通讯录";
            task.toArchiveDirPath_EN = migratePath_archive_en+"/Contacts";
            task.toArchiveDirPath_JA = migratePath_archive_ja+"/アドレス帳";
            contactSysTask = task;
        }
        return contactSysTask;
    }

    SysTask contactSysTask = null;
    void buildTask(){

        //String[] names = {getString(R.string.contact_backup_title),getString(R.string.file_pic),getString(R.string.file_video),getString(R.string.file_doc),getString(R.string.file_audio),getString(R.string.file_wx)};

//        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//        BluetoothManager managerB = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        String android_id = Settings.Secure.ANDROID_ID;

        android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

        String migratePath = null;

        String migratePath_en = null;

        String migratePath_zh = null;
        String migratePath_ja = null;

        String migratePath_archive_zh = null;

        String migratePath_archive_en = null;
        String migratePath_archive_ja = null;

        String sysRootName =  getFolderName(Build.MODEL + "[" + android_id + "]" + "的同步文件夹");//"";//TimeUtil.getCurrentTime("yyyy-MM-dd")
        migratePath = UsbHelper.getInstance().getUsbRootPath() + "/" + sysRootName  + "/" +  UsbHelper.getInstance().getUsbUUid();


        migratePath_zh = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "的同步文件夹"  + "/" +  UsbHelper.getInstance().getUsbUUid();

        migratePath_en = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "'s synchronization folder"  + "/" +  UsbHelper.getInstance().getUsbUUid();

        migratePath_ja = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "の同期フォルダ"  + "/" +  UsbHelper.getInstance().getUsbUUid();


        migratePath_archive_zh = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "的同步文件夹"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "已归档的文件";

        migratePath_archive_en = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "'s synchronization folder"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "Archive folder";

        migratePath_archive_ja = UsbHelper.getInstance().getUsbRootPath() + "/" +  Build.MODEL + "[" + android_id + "]" + "の同期フォルダ"  + "/" +  UsbHelper.getInstance().getUsbUUid() + "/" + "アーカイブフォルダ";


        String disk_uuid = UsbHelper.getInstance().getUsbUUid();

        String usbRoot = UsbHelper.getInstance().getUsbRootPath();
        dataList = new ArrayList();

        dataResList = new ArrayList();
        int allCount = 0;
        {

                dataList.add(getContactSysTask());

                dataResList.add(new SysNode(context.getString(R.string.contact_backup_title),1,0,R.mipmap.ic_migrate_contact,"Contacts"));

        }
        {
            String desDir = migratePath + "/Pictures";

            ArrayList datas = (ArrayList) FileTool.getPic_(context);

            allCount= 0;
            for(Object o : datas){
                File f = (File) o;

                if(f.getAbsolutePath().startsWith(usbRoot)){
                    continue;
                }
//                if(allCount > 40){
//                    break;
//                }
                SysTask task = new SysTask();
                task.path = f.getAbsolutePath();
                task.name = f.getName();
                task.disk_uuid = disk_uuid;
                task.androidId = android_id;
                task.type = "Pictures";
                task.toDirPath = desDir;
                task.toArchiveDirPath = migratePath + "/" +"Archive folder"+"/Pictures";
                task.toDirPath_CH = migratePath + "/图片";
                task.toDirPath_EN = migratePath + "/Pictures";
                task.toDirPath_JA = migratePath + "/画像";
                task.toArchiveDirPath_CH = migratePath_archive_zh+"/图片";
                task.toArchiveDirPath_EN = migratePath_archive_en+"/Pictures";
                task.toArchiveDirPath_JA = migratePath_archive_ja+"/画像";
                dataList.add(task);
                allCount ++;
            }

            dataResList.add(new SysNode(context.getString(R.string.file_pic),allCount,0,R.mipmap.ic_migrate_pic,"Pictures"));

        }
        {

            allCount = 0;
            String desDir = migratePath + "/Videos";
            ArrayList datas = (ArrayList) FileTool.getVideos_(context);
            for(Object o : datas){
                File f = (File) o;

                if(f.getAbsolutePath().startsWith(usbRoot)){
                    continue;
                }

//                if(allCount > 12){
//                    break;
//                }
                SysTask task = new SysTask();
                task.path = f.getAbsolutePath();
                task.name = f.getName();
                task.disk_uuid = disk_uuid;
                task.androidId = android_id;
                task.type = "Videos";
                task.toDirPath = desDir;
                task.toArchiveDirPath = migratePath + "/" +"Archive folder"+"/Videos";

                task.toDirPath_CH = migratePath + "/视频";
                task.toDirPath_EN = migratePath + "/Videos";
                task.toDirPath_JA = migratePath + "/ビデオ";
                task.toArchiveDirPath_CH = migratePath_archive_zh+"/视频";
                task.toArchiveDirPath_EN = migratePath_archive_en+"/Videos";
                task.toArchiveDirPath_JA = migratePath_archive_ja+"/ビデオ";
                dataList.add(task);
                allCount ++;



                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }
            dataResList.add(new SysNode(context.getString(R.string.file_video),allCount,0,R.mipmap.ic_migrate_video,"Videos"));
        }
        {

            allCount = 0;
            String desDir = migratePath + "/Documents";
            ArrayList datas = (ArrayList) FileTool.getDoc_(context);
            for(Object o : datas){
                File f = (File) o;
                if(f.getAbsolutePath().startsWith(usbRoot)){
                    continue;
                }
                SysTask task = new SysTask();
                task.path = f.getAbsolutePath();
                task.name = f.getName();
                task.disk_uuid = disk_uuid;
                task.androidId = android_id;
                task.type = "Documents";
                task.toDirPath = desDir;
                task.toArchiveDirPath = migratePath + "/" +"Archive folder"+"/Documents";

                task.toDirPath_CH = migratePath + "/文档";
                task.toDirPath_EN = migratePath + "/Documents";
                task.toDirPath_JA = migratePath + "/ドキュメント";
                task.toArchiveDirPath_CH = migratePath_archive_zh+"/文档";
                task.toArchiveDirPath_EN = migratePath_archive_en+"/Documents";
                task.toArchiveDirPath_JA = migratePath_archive_ja+"/ドキュメント";
                dataList.add(task);
                allCount ++;
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());

            }
            dataResList.add(new SysNode(context.getString(R.string.file_doc),allCount,0,R.mipmap.ic_migrate_doc,"Documents"));

        }
        {
            allCount = 0;
            String desDir = migratePath + "/Audios";
            ArrayList datas = (ArrayList) FileTool.getAudios_(context);
            for(Object o : datas){
                File f = (File) o;
                if(f.getAbsolutePath().startsWith(usbRoot)){
                    continue;
                }
                SysTask task = new SysTask();
                task.path = f.getAbsolutePath();
                task.name = f.getName();
                task.disk_uuid = disk_uuid;
                task.androidId = android_id;
                task.type = "Audios";
                task.toDirPath = desDir;
                task.toArchiveDirPath = migratePath + "/" +"Archive folder"+"/Audios";

                task.toDirPath_CH = migratePath + "/音频";
                task.toDirPath_EN = migratePath + "/Audios";
                task.toDirPath_JA = migratePath + "/オーディオ";
                task.toArchiveDirPath_CH = migratePath_archive_zh+"/音频";
                task.toArchiveDirPath_EN = migratePath_archive_en+"/Audios";
                task.toArchiveDirPath_JA = migratePath_archive_ja+"/オーディオ";
                dataList.add(task);
                allCount ++;
                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }

            dataResList.add(new SysNode(context.getString(R.string.file_audio),allCount,0,R.mipmap.ic_migrate_audio,"Audios"));


        }
        {
            allCount = 0;

            HashMap<String,String[]> kmap = new HashMap();
            kmap.put("Pictures", new String[]{"图片","Pictures","画像"});
            kmap.put("Videos", new String[]{"视频","Videos","ビデオ"});
            kmap.put("Docs", new String[]{"文档","Documents","ドキュメン"});
            kmap.put("Others", new String[]{"其他","Others","その他"});

            String picKey = "Pictures";
            String videoKey = "Videos";
            String textKey = "Docs";
            String otherKey = "Others";

            String desDir = migratePath + "/Weixin";
            ArrayList datas = (ArrayList) FileTool.getWeixi__(context);
            for(Object o : datas){
                HashMap map = (HashMap) o;

                String key = (String) map.keySet().toArray()[0];


                List datas1 = (List) map.get(key);
                desDir = migratePath + "/Weixin" + "/" + key;

                for(Object o1 : datas1) {
                    File f = (File) o1;
                    SysTask task = new SysTask();
                    task.path = f.getAbsolutePath();
                    task.name = f.getName();
                    task.disk_uuid = disk_uuid;
                    task.androidId = android_id;
                    task.type = "Weixin";
                    task.toDirPath = desDir;
                    task.toArchiveDirPath = migratePath + "/" + "Archive folder" + "/Weixin"+"/" + key;


                    task.toDirPath_CH = migratePath + "/微信" + "/" + kmap.get(key)[0];
                    task.toDirPath_EN = migratePath + "/WeChat" + "/" + kmap.get(key)[1];
                    task.toDirPath_JA = migratePath + "/ウィチャット" + "/" + kmap.get(key)[2];
                    task.toArchiveDirPath_CH = migratePath_archive_zh+"/微信" + "/" + kmap.get(key)[0];
                    task.toArchiveDirPath_EN = migratePath_archive_en+"/WeChat" + "/" + kmap.get(key)[1];
                    task.toArchiveDirPath_JA = migratePath_archive_ja+"/ウィチャット" + "/" + kmap.get(key)[2];
                    dataList.add(task);
                    allCount ++;
                }



                //FileTool.copyWithPath(f.getAbsolutePath(),desDir + "/" + f.getName());
            }
            dataResList.add(new SysNode(context.getString(R.string.file_wx),allCount,0,R.mipmap.ic_migrate_wx,"Weixin"));

        }


    }



    void transFiles(){


        migrateing = true;

        reCopyCount = 0;

        reCopyFilePaths = new ArrayList<>();
        for(Object o : dataList){
            if(!isSysTask() || pause){
                migrateing = false;
                return;
            }
            SysTask node = (SysTask) o;
            node.sysNode = sysNode;
            int count = dataList.indexOf(o);
            SafFile3 to = new SafFile3(context,node.getCurrentToDir(context) + "/" + node.name);
            if(to != null){
                if(to.exists()){
                    reCopyCount = reCopyCount + 1;

                    reCopyFilePaths.add(node.path);
                    File file = new File(node.path);
                    if(to.length() == file.length()){

                        String toMd5 = FileTool.getFileMD5(to);
                        SysTask task = dao.findByPath(UsbHelper.getInstance().getUsbUUid(),node.path);
                        if(task != null && task.md5 != null) {
                            if (task.md5.equals(toMd5)) {
                                updateRecord(node);
                                SysNode node_ = getNode(node.type);

                                try {
                                    node_.index = node_.index + 1;
                                }catch (Exception e){
                                    Exception e2 = e;
                                }

                                continue;
                            }
                        }
                    }
                }
            }
//            FileTool.copyWithPath(node.path,node.toArchiveDirPath + "/" + node.name);
//            FileTool.copyWithPath(node.path,node.toDirPath + "/" + node.name);

            FileTool.copyWithPath(node.path,node.getCurrentToDir(context) + "/" + node.name);


            SafFile3 from = new SafFile3(context,node.path);
            node.md5 = FileTool.getFileMD5(from);
            if(from.exists()) {
                //to.setLastModified(from.lastModified());
            }

            inserRecord(node);

            SysNode node_ = getNode(node.type);
            //node_.index = node_.index + 1;
            try {
                node_.index = node_.index + 1;
            }catch (Exception e){
                Exception e2 = e;
            }


        }

        migrateing = false;


    }


    SysNode getNode(String type){

        for(Object o : dataResList){
            SysNode node = (SysNode) o;
            if(node.type.equals(type)){
                return node;
            }
        }
        return null;
    }
    void deleteUnExsit(){

       deleteTaskUnExists(sysNode);
    }
    public boolean needToSys(){

        if(UsbHelper.getInstance().canCopyToU() && isSysTask() && !pause){

            return true;
        }


        return false;
    }

    public boolean isSysTask() {

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        String language = preferences.getString("sys_task", "");

        if (language.equals("1")) {
            return true;
        }
        return false;
    }


    public void openSysTask(){

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        String language = preferences.getString("sys_task", "");
        editor.putString("sys_task", "1");
        editor.commit();

        if(UsbHelper.getInstance().canCopyToU()){
            UsbHelper.getInstance().toStopTrans(false);
        }
        if(!isSys){
            sys();
        }
    }

    public void closeSysTask(){

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        String language = preferences.getString("sys_task", "");
        editor.putString("sys_task", "0");
        editor.commit();


        if(UsbHelper.getInstance().canCopyToU()){
            UsbHelper.getInstance().toStopTrans(true);
        }
    }


    public void stopSysToChangeFolderName(){
        pause = true;
        if(UsbHelper.getInstance().canCopyToU()){
            UsbHelper.getInstance().toStopTrans(true);
        }

        while(!sysOneFinsh){

        }
        changeNameOfUsbSysFolder();

        if(UsbHelper.getInstance().canCopyToU()){
            UsbHelper.getInstance().toStopTrans(false);
        }

        pause = false;
        contactSysTask = null;


    }


    public void changeNameOfUsbSysFolder(){

        String android_id = Settings.Secure.ANDROID_ID;
        android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        String rootPreStr = Build.MODEL + "[" + android_id + "]";
        if(UsbHelper.getInstance().canCopyToU()) {
            SafFile3 root = UsbHelper.getInstance().getRootFile();
            if (root.exists()) {
                List<String> list = new ArrayList();
                Collections.addAll(list, new String[]{rootPreStr + "的同步文件夹", rootPreStr + "'s synchronization folder", rootPreStr + "の同期フォルダ"});

                if (list != null) {
                    for (String s : list) {

                        SafFile3 sf = new SafFile3(context,root.getPath() + "/" + s);
                        if (sf.exists() && sf.isDirectory() && sf.getName().contains(android_id)) {
                            String name = getFolderName(rootPreStr + "的同步文件夹");
                            if (sf.getName().contains(name)) {

                                Log.d(TAG, "changeNameOfUsbSysFolder: " + "unchange");
                            } else {

//                                SafFile3 sf01 = new SafFile3(context, sf.getParent() + "/" + name);
//                                sf.renameTo(sf01);
//                                Log.d("333333" , sf.getName() + " ->" + sf01.getName());
                                Log.d(TAG, "changeNameOfUsbSysFolder: " + "changed");
                                changeNameOfUsbSysFolder(sf);


                            }
                        }
                    }
                }
            }
        }

    }

    public void changeNameOfUsbSysFolder(SafFile3 rootFoler){
        String name = getFolderName(rootFoler.getName());
       SafFile3 sf01 = new SafFile3(context,rootFoler.getParent() + "/" + name);
       if(name.equals(rootFoler.getName())){
           sf01 = rootFoler;
       }else{
           rootFoler.renameTo(sf01);
       }


        Log.d("333333" , rootFoler.getName() + " ->" + sf01.getName());

        String android_id = Settings.Secure.ANDROID_ID;
        android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        String rootPreStr = Build.MODEL + "[" + android_id + "]";

        String uuid = UsbHelper.getInstance().getUsbUUid();
        List<String> list = new ArrayList();
        Collections.addAll(list, new String[]{rootPreStr + "的同步文件夹", rootPreStr + "'s synchronization folder", rootPreStr + "の同期フォルダ"});

        Collections.addAll(list, new String[]{uuid});

        Collections.addAll(list, new String[]{"通讯录", "Contacts", "/アドレス帳"});

        Collections.addAll(list, new String[]{"图片", "Pictures", "画像"});

        Collections.addAll(list, new String[]{"视频", "Videos", "ビデオ"});

        Collections.addAll(list, new String[]{"文档", "Documents", "ドキュメント"});
        Collections.addAll(list, new String[]{"音频", "Audios", "オーディオ"});

        Collections.addAll(list, new String[]{"其他", "Others", "その他"});
        Collections.addAll(list, new String[]{"微信", "WeChat", "ウィチャット"});
        Collections.addAll(list, new String[]{"已归档的文件", "Archive folder", "アーカイブフォルダ"});


        if(sf01.exists()){
            if(true){
                for(Object o : list){

                    String s = (String) o;

                    SafFile3 sf = new SafFile3(context,sf01.getPath() + "/" + s);
                    if(sf.exists() && sf.isDirectory()) {
                        changeNameOfUsbSysFolder(sf);
                    }

                }
            }


        }

    }


     String getFolderName(String key){


        int type=0;
        Locale local = MultiLanguageUtil.getInstance().getLanguageLocale(context);
        if(local == Locale.SIMPLIFIED_CHINESE){
            type = 0;
        }else if(local == Locale.ENGLISH){
            type = 1;
        }else if(local == Locale.JAPAN){
            type = 2;
        }


         String android_id = Settings.Secure.ANDROID_ID;
         android_id = Settings.System.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
         String rootPreStr = Build.MODEL + "[" + android_id + "]";

        List<String[]> list = new ArrayList();
        list.add(new String[]{rootPreStr + "的同步文件夹",rootPreStr + "'s synchronization folder",rootPreStr + "の同期フォルダ"});

        list.add(new String[]{"通讯录","Contacts","/アドレス帳"});

        list.add(new String[]{"图片","Pictures","画像"});

        list.add(new String[]{"视频","Videos","ビデオ"});

        list.add(new String[]{"文档","Documents","ドキュメント"});
        list.add(new String[]{"音频","Audios","オーディオ"});

        list.add(new String[]{"其他","Others","その他"});
        list.add(new String[]{"微信","WeChat","ウィチャット"});
        list.add(new String[]{"已归档的文件","Archive folder","アーカイブフォルダ"});


        for(String [] s : list){
            if(Arrays.asList(s).contains(key)){
                return s[type];
            }
        }
        return key;


    }

    public boolean fileIsSysed(String path){

        try {
                //SysTask task = dao.findByPath(UsbHelper.getInstance().getUsbUUid(), path);
            if(sysedList == null){
                return false;
            }
            for(Object o : sysedList){
                SysTask task = (SysTask) o;
                if(task.path.equals(path)){
                    return true;
                }
            }
//            if (task != null) {
//                return true;
//            }

            return false;
        }catch(Exception e){
            Exception e1 = e;
        }
        return false;
    }


}
