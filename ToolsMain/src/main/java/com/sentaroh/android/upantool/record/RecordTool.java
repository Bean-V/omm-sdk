package com.sentaroh.android.upantool.record;

import static androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2;

import android.content.Context;

import androidx.room.Room;

import com.sentaroh.android.upantool.UsbHelper;

import java.util.ArrayList;
import java.util.List;

public class RecordTool {


    private static RecordTool logUtil;
    private List allDatas;

    public List getAllDatas() {
        return allDatas;
    }

    public List getUnFinsh() {
        return unFinsh;
    }

    private List unFinsh;

    //单例模式初始化
    public static RecordTool getInstance() {
        if (logUtil == null) {
            logUtil = new RecordTool();

        }
        return logUtil;
    }

    private Context context;
    private RecordDao dao;

    public RecordDatabase getDb() {
        return db;
    }

    private RecordDatabase db;
    public void initData(Context c){

        context = c;
        db = Room.databaseBuilder(c.getApplicationContext(),
                RecordDatabase.class, "transRecord").addMigrations(new Migration1To2(1,2)).build();

        dao = db.RecordDao();



        new Thread(new Runnable() {



            @Override
            public void run() {

                allDatas = getAllTransFiles();

                unFinsh = getUnFinshData();


            }
        }).start();

    }


    public List getAllTransFiles(){
       List datas = dao.loadAllByUuId(UsbHelper.getInstance().getUsbUUid());
       return datas;
    }


    public List getUnFinshData(){

        ArrayList datas = new ArrayList();

        ArrayList datas1 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),0);
        ArrayList datas2 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),1);
        ArrayList datas3 = (ArrayList) dao.findByStatu(UsbHelper.getInstance().getUsbUUid(),3);

        if(datas1 != null){
            datas.addAll(datas1);
        }
        if(datas2 != null){
            datas.addAll(datas2);
        }
        if(datas3 != null){
            datas.addAll(datas3);
        }
        return datas;
    }


    public void inserRecord(Record record){
        dao.insertAll(record);
    }

    public void updateRecord(Record record){
        dao.updateRecord(record);
    }



    public void deleteRecords(int statu){
        dao.deleteByStatu(UsbHelper.getInstance().getUsbUUid(),statu);
    }
    public Record getRecordByPath(String path){
        Record r = dao.findByPath(UsbHelper.getInstance().getUsbUUid(),path);
        return r;
    }
}
