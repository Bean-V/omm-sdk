package com.oortcloud.clouddisk.db;

import android.text.TextUtils;

import com.oortcloud.clouddisk.transfer.CopyInfo;
import com.oortcloud.clouddisk.transfer.down.DownLoadInfo;
import com.oortcloud.clouddisk.transfer.down.DownLoadThread;
import com.oortcloud.clouddisk.transfer.down.DownLoadThreadInfo;
import com.oortcloud.clouddisk.transfer.upload.UploadInfo;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/25 17:01
 * @version： v1.0
 * @function：
 */
public class DBManager {

    private DBHelper<UploadInfo> mUploadHelper;
    private DBHelper<DownLoadInfo> mDownHelper;
    private DBHelper<DownLoadThreadInfo> mThreadHelper;
    private DBHelper<CopyInfo> mCopyHelper;
    private static DBManager mDBManager;

   private  DBManager(){
       mUploadHelper = DBHelper.getInstance();
       mUploadHelper.setDaoSupport(UploadInfo.class);
       mDownHelper = DBHelper.getInstance();
       mDownHelper.setDaoSupport(DownLoadInfo.class);
       mThreadHelper = DBHelper.getInstance();
       mThreadHelper.setDaoSupport(DownLoadThreadInfo.class);
       mCopyHelper = DBHelper.getInstance();
       mCopyHelper.setDaoSupport(CopyInfo.class);
   }


    public static DBManager getInstance() {
        if (mDBManager == null){
            synchronized (DBManager.class){
                if (mDBManager == null){
                    mDBManager = new DBManager();
                }
            }
        }

        return mDBManager;
    }

    public  UploadInfo isExistUp( String  key , String value){

       return mUploadHelper.isExist(key , value);

    }
    public DownLoadInfo  isExistDown( String  key , String value){

       return mDownHelper.isExist(key , value);

    }
    public DownLoadThreadInfo isExistThread(String  key , String value){

       return mThreadHelper.isExist(key , value);

    }
    public CopyInfo isExistCopy(String  key , String value){

       return mCopyHelper.isExist(key , value);

    }
    public void insert(UploadInfo uploadInfo){

        mUploadHelper.insert(uploadInfo);
    }

    public void insert(DownLoadInfo downLoadInfo){

        mDownHelper.insert(downLoadInfo);
    }
    public void insert(DownLoadThreadInfo downLoadThreadInfo){

        mThreadHelper.insert(downLoadThreadInfo);
    }
    public void insert(CopyInfo copyInfo){

        mCopyHelper.insert(copyInfo);
    }

    public List queryUp( String  key , String value){
       return mUploadHelper.queryAll(key , value);
    }
    public List queryDown( String  key , String value){
       return mDownHelper.queryAll(key , value);
    }
    public List queryThread( String  key , String value){
       return mThreadHelper.queryAll(key , value);
    }
    public List queryCopy( String  key , String value){
       return mCopyHelper.queryAll(key , value);
    }

    public void update(UploadInfo uploadInfo ,  String  key , String value){
        mUploadHelper.update(uploadInfo ,key , value);
    }
    public void update(DownLoadInfo downLoadInfo ,  String  key , String value){
        mDownHelper.update(downLoadInfo ,key , value);
    }
    public void update(DownLoadThreadInfo downLoadThreadInfo ,  String  key , String value){
        mThreadHelper.update(downLoadThreadInfo ,key , value);
    }
    public void update(CopyInfo copyInfo ,  String  key , String value){
        mCopyHelper.update(copyInfo ,key , value);
    }
    public void deleteUp( String key , String value){

        mUploadHelper.delete(key,value);
    }
    public void deleteDown( String key , String value){

        mDownHelper.delete(key,value);
    }
    public void deleteThread( String key , String value){

        mThreadHelper.delete(key,value);
    }
    public void deleteCopy( String key , String value){

        mCopyHelper.delete(key,value);
    }
}
