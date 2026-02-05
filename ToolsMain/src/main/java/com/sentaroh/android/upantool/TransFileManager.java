package com.sentaroh.android.upantool;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.upantool.record.Record;
import com.sentaroh.android.upantool.record.RecordDao;
import com.sentaroh.android.upantool.record.RecordDatabase;
import com.sentaroh.android.upantool.record.RecordTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TransFileManager {




        private static TransFileManager logUtil;
        private Context context;
        private RecordDao dao;

        public RecordDatabase getDb() {
            return db;
        }

        private RecordDatabase db;
        MyThreadPool myThreadPool;

        //单例模式初始化
        public static TransFileManager getInstance() {
            if (logUtil == null) {
                logUtil = new TransFileManager();

            }
            return logUtil;
        }

        public void initData(Context c){
            context = c;

//            db = Room.databaseBuilder(c.getApplicationContext(),
//                    RecordDatabase.class, "transRecord").build();
//
//            dao = db.RecordDao();

//            dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),0);
//
//            dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),1);
//
//            dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),3);
            myThreadPool = new MyThreadPool(3, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());


            UsbHelper.getInstance().addTransListener(new UsbHelper.LCUsbTransListener(){

                @Override
                public void showProgress(String s, String s1, long l, int postion) {

                    for (Fragment_ft.TransFile tf : transList) {
                        if (tf.getPath().equals(s)) {
                            tf.setPostion(postion);
                            break;
                        }
                    }
                }

                @Override
                public void checkFinsh(String s, String s1, Boolean finsh) {

                }
            });

        }


        public interface DesPathSelectListener {

            void desPathSelectFinsh(Context c,String path,Object other);
        }

        public void setPathSelectListener(DesPathSelectListener listener) {
            this.pathSelectListener = listener;
        }


        public void setStatuChangeListener(Fragment_ft.StatuChangeListener listener) {
            this.mItemClickListener = listener;
        }

        public void addStatuChangeListener(Fragment_ft.StatuChangeListener listener) {
            statuChangeListeners.add(listener);
        }


        Fragment_ft.StatuChangeListener mItemClickListener;

    public DesPathSelectListener getPathSelectListener() {
        return pathSelectListener;
    }

    DesPathSelectListener pathSelectListener;


        CopyOnWriteArrayList<Fragment_ft.StatuChangeListener> statuChangeListeners = new CopyOnWriteArrayList<Fragment_ft.StatuChangeListener>();

        public CopyOnWriteArrayList<Fragment_ft.TransFile> getTransList() {
            return transList;
        }

        CopyOnWriteArrayList<Fragment_ft.TransFile> transList = new CopyOnWriteArrayList<Fragment_ft.TransFile>();
        CopyOnWriteArrayList<Fragment_ft.TransFile> transDoneList = new CopyOnWriteArrayList<Fragment_ft.TransFile>();

        public CopyOnWriteArrayList<Fragment_ft.TransFile> getTransDoneList() {
            transDoneList.clear();

            for(Fragment_ft.TransFile tf : transList){

                if(tf.getStatu() == 2) {
                    transDoneList.add(0,tf);
                }
            }
            return transDoneList;
        }

        public CopyOnWriteArrayList<Fragment_ft.TransFile> getTransUnDoneList() {


            transUnDoneList.clear();
            for(Fragment_ft.TransFile tf : transList){

                if(tf.getStatu() != 2) {
                    transUnDoneList.add(tf);
                }
            }
            return transUnDoneList;
        }

        CopyOnWriteArrayList<Fragment_ft.TransFile> transUnDoneList = new CopyOnWriteArrayList<Fragment_ft.TransFile>();



    public void removeAddDone_() {
        transList.removeAll(getTransDoneList());

        for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
            ls.onStatuChange();
        }




    }
    public void removeAddunDone_() {
        transList.removeAll(getTransUnDoneList());

        for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
            ls.onStatuChange();

        }




    }



        public void removeAddDone() {
            transList.removeAll(getTransDoneList());

            for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
                ls.onStatuChange();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {

                    RecordTool.getInstance().deleteRecords(2);

                }
            }).start();


        }
        public void removeAddunDone() {
            transList.removeAll(getTransUnDoneList());

            for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
                ls.onStatuChange();

            }


            new Thread(new Runnable() {
                @Override
                public void run() {

                    RecordTool.getInstance().deleteRecords(0);
                    RecordTool.getInstance().deleteRecords(1);
                    RecordTool.getInstance().deleteRecords(3);

                }
            }).start();

        }


        private boolean inFor = false;
        public void clear() {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (inFor){

                    }


                    Log.d("44444444444", "run: 5555555555555");
                    transList.clear();



                }
            }).start();



        }




        public void addTransFile(Fragment_ft.TransFile tf){

            if(!transList.contains(tf)) {
                transList.add(tf);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {

                    transFile(tf);

                }

            }).start();

        }

    public void addTransFileSys(Fragment_ft.TransFile tf){

        if(!transList.contains(tf)) {
            transList.add(tf);
        }


        transFile(tf);

    }


        public void addTransFiles(ArrayList<Fragment_ft.TransFile> tfs){

            Log.d("44444444444", "run: 5555555555555addTransFiles");


            CopyOnWriteArrayList ls = new CopyOnWriteArrayList();
            ls.add(new Fragment_ft.TransFile());

           // transList.add(new Fragment_ft.TransFile());

//            if(transList.size() == 0){
//                transList = new CopyOnWriteArrayList<>();
//                transDoneList = new CopyOnWriteArrayList<>();
//                transUnDoneList = new CopyOnWriteArrayList<>();
//            }
            boolean res = transList.addAll(tfs);





            ArrayList dbDataLsit = new ArrayList();
            String disk_uuid = UsbHelper.getInstance().getUsbUUid();
            for(Fragment_ft.TransFile tf : tfs){

                SafFile3  fsf = (SafFile3) tf.getFileObj();

                if(fsf.isDirectory()){
                    continue;
                }
                Record record = new Record();
                record.disk_uuid = disk_uuid;
                record.name = tf.getName();
                record.path = tf.getPath();
                record.from = tf.getPath();

                SafFile3 ssf = (SafFile3) tf.getToFileObj();

                record.to = ssf.getPath();
                record.toDirPath = tf.getToDirPath();



                dbDataLsit.add(record);



                Log.d("22222", "showProgressaddTransFiles: " + record.md5);


                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        RecordTool.getInstance().inserRecord(record);
                    }
                };
                myThreadPool.execute(runnable);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        record.md5 = FileTool.getFileMD5(new SafFile3(context,tf.getPath()));
//
////                        dao.insertAll(record);
//                        RecordTool.getInstance().inserRecord(record);
//
//                    }
//                }).start();







//                record.to = tf.getToFileObj();
//                record.md5 = tf.
            }
            //           dao.insertAll(dbDataLsit);


            if(!inFor) {
                start();
            }
        }

        public void addTransFilesFromDB(ArrayList<Fragment_ft.TransFile> tfs){

            transList.addAll(tfs);

            start();
        }



        Fragment_ft.TransFile findStat0inlist(){
            for(Fragment_ft.TransFile tf : transList){
                if(tf.getStatu() == 0){
                    return tf;
                }
            }
            return null;

        }

        public void start(){


            new Thread(new Runnable() {
                @Override
                public void run() {

                    //for(Fragment_ft.TransFile tf : transList)
                        if(transList.size() == 0){
                            return;
                        }
                        int i = 0;

                    Fragment_ft.TransFile temptf = findStat0inlist();
                    while(temptf != null){

                        if(UsbHelper.getInstance().isStopTrans()){
                            return;
                        }
                       Fragment_ft.TransFile tf =  temptf;

                       i ++;
                        inFor = true;
                        if(tf.getStatu() == 2){
                            inFor = false;
                            temptf = findStat0inlist();
                            continue;
                        }

                        transFile(tf);
                        temptf = findStat0inlist();


                    }

                    inFor = false;

                }
            }).start();


        }


        int count = 0;

        int transFile(Fragment_ft.TransFile transFile){

            Fragment_ft.TransFile tf = transFile;
            tf.setStatu(1);
            tf.setStatuDes("正在复制");
            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
                        ls.onStatuChange();
                    }
                }
            });

            SafFile3 f = (SafFile3) tf.getFileObj();
            SafFile3 tof = (SafFile3) tf.getToFileObj();

            FileTool.copyWithPath(tf.getPath(),tf.getToDirPath() + "/" + tf.getName());

            SafFile3 from = new SafFile3(context,tf.getPath());
            String fromMd5 = "";//FileTool.getFileMD5(from);

            if(from != null){
                if(from.exists()){
                    fromMd5 = FileTool.getFileMD5(from);
//                    if(!Constants.COPY_APPEND_DEBUG){
//                        fromMd5 = FileTool.getFileMD5(from);
//                    }
                   //
                }
            }

            SafFile3 to = new SafFile3(context,tof.getPath() + "/" + tf.getName());
            String toMd5= null;
            if(to != null){
                if(to.exists()){
                    toMd5 = FileTool.getFileMD5(to);
//                    if(!Constants.COPY_APPEND_DEBUG) {
//                        toMd5 = FileTool.getFileMD5(to);
//                    }
                }
            }

            Boolean sucess = false;
            Record record = RecordTool.getInstance().getRecordByPath(tf.getPath());
            if(Constants.COPY_APPEND_DEBUG && false) {
                long froms = f.length();
                long tos = tof.length();

                Log.d("000000000", "transFile: " + froms + "######" + tos);

                if(froms == tos){
                    sucess = true;
                }
            }else {





                Log.d("0000", "transFile: 0000000000000000");
                if (from.isDirectory()) {
                    tf.setStatu(2);
                    tf.setStatuDes("复制完成");
                    if (tf.getDeleteWhenFinsh()) {
                        SafFile3 sf = new SafFile3(context, tf.getPath());
                        sf.delete();
                    }
                } else {


                    if (fromMd5 == null) {

                        transList.remove(tf);
                        return 0;
                    }

                    if (toMd5 == null) {
                        tf.setStatu(3);
                        tf.setStatuDes("复制失败");
                    } else if (toMd5.equals(fromMd5)) {
                        tf.setStatu(2);
                        tf.setStatuDes("复制完成");
                        sucess = true;
                        count++;
                        Log.d("lc", "transFile: " + count);
                        if (tf.getDeleteWhenFinsh()) {
                            SafFile3 sf = new SafFile3(context, tf.getPath());
                            sf.delete();
                        }
                    } else {
                        tf.setStatu(3);
                        tf.setStatuDes("复制失败");
                        sucess = false;
                    }
                }
            }



            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(Fragment_ft.StatuChangeListener ls : statuChangeListeners){
                        ls.onStatuChange();
                    }
                }
            });





            if(record != null){
                if (sucess) {
                    record.statu = 2;
                    record.statuDes = "复制完成";
                    RecordTool.getInstance().updateRecord(record);
                } else {
                    record.statu = 3;
                    record.statuDes = "复制失败";
                    record.postion = tf.getPostion();
                    RecordTool.getInstance().updateRecord(record);
                }
            }

            return 0;

        }

    }
