package com.jun.baselibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/6 23:01
 * Version 1.0
 * Description：数据库工厂--创建操作数据库实例
 */
public class DAOFactory {
    private static final String DEFAULT_DB_NAME = "app_database.db";
    private static volatile DAOFactory mDBFactory;

    private final SQLiteDatabase mSQLiteDatabase;

    private final Map<Class<?>, DAO<?>> daoCache = new HashMap<>();

    private static WeakReference<Context> mContextWeakRef;

    private static String mDBName;
    private DAOFactory() {
        //把数据库放到内存卡里面 默认存储data/data/包名/xxx 缺点应用卸载数据就没有了
        File dbRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Objects.requireNonNull(getContext()).getPackageName() + File.separator + "DataBase");
        if (!dbRoot.exists()) {
            boolean mkdirs = dbRoot.mkdirs();
        }
        File dbFile = new File(dbRoot, mDBName);
        //打开或创建数据库
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    public static DAOFactory getInstance() {
        if (getContext() == null || mDBName == null) {
            throw new IllegalStateException("DAOFactory must be initialized first with init()");
        }
        if (mDBFactory == null) {
            synchronized (DAOFactory.class) {
                if (mDBFactory == null) {
                    mDBFactory = new DAOFactory();
                }
            }
        }
        return mDBFactory;
    }
    private static void init(Context context) {
        init(context, DEFAULT_DB_NAME);
    }

    // 初始化时传入 context 和 dbName，后续 getInstance() 无需参数
    public static synchronized void init(Context context, String dbName) {
        if (context == null) throw new IllegalArgumentException("context == null");
        if (dbName == null) throw new IllegalArgumentException("dbName == null");
        mContextWeakRef = new WeakReference<>(context.getApplicationContext());
        mDBName = dbName;
    }

    private static Context getContext() {
        return mContextWeakRef != null ? mContextWeakRef.get() : null;
    }
    @SuppressWarnings("unchecked")
    public <T> DAO<T> getDAO(Class<T> clazz) {
        DAO<?> cachedDAO = daoCache.get(clazz);
        if (cachedDAO == null) {
            DAOImpl<T> newDAO = new DAOImpl<>();
            newDAO.init(mSQLiteDatabase, clazz);
            daoCache.put(clazz, newDAO);
            return newDAO;
        }
        return (DAO<T>) cachedDAO;  // 强制转换（需确保安全）
    }
}
