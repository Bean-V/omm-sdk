package com.oortcloud.clouddisk.db;

import android.text.TextUtils;

import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/15 11:44
 * @version： v1.0
 * @function： 操作数据库帮助类
 */
 class DBHelper<T> {

  private IDaoSupport daoSupport;

  private static DBHelper mDBHelper;

  static DBHelper getInstance() {

      mDBHelper = new DBHelper();

      return mDBHelper;
  }
    private DBHelper(){}

    public void  setDaoSupport(Class clazz){

        daoSupport = DaoSupportFactory.getFactory().
                getDaoSupport(clazz);
    }

    public  T isExist(String  key , String value){

        List uploadIfs = daoSupport.querySupport()
                .selection(key + " = ?").selectionArgs(value).query();
        if (uploadIfs != null &&  uploadIfs.size() > 0 ){
            return (T)uploadIfs.get(0);
        }
        return null;
    }

    public void insert(T t){

        daoSupport.insert(t);
    }

    public List<T> queryAll( String  key , String value){
      if (TextUtils.isEmpty(key)){
          return daoSupport.querySupport().queryAll();
      }else {
          return daoSupport.querySupport()
                  .selection(key+ " = ?").selectionArgs(value).query();
      }

    }
    public void update(T t , String  key , String value){

        daoSupport.update(t,key+ " = ?", value);
    }
    public void delete( String  key , String value){

        daoSupport.delete(key+ " = ?", value);
    }

}
