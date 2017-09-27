package com.chinadci.mel.mleo.ui.fragments.data.ldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guanghuil@geo-k.cn on 2016/1/20.
 */
public class DBHelper extends SQLiteOpenHelper {

    static DBHelper instance;// 单例
    static Lock instanceLock = new ReentrantLock();// 单例锁
    static final String DB_NAME = "FjNewDb";// 数据库名
    static final int DB_VERSION = 3;// 数据库版本

    public static void destroy() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getDbHelper(Context context) {
        if (instance == null) {
            instanceLock.lock();
            try {
                instance = new DBHelper(context);
            } catch (Exception ignored) {
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Dbs.CREATE_TABLE_SIMPLEAJ);
        db.execSQL(Dbs.CREATE_TABLE_SIMPLEAJ1);
        db.execSQL(Dbs.CREATE_TABLE_SIMPLEAJ2);
        db.execSQL(Dbs.CREATE_TABLE_STNUM);
        db.execSQL(Dbs.CREATE_TABLE_XZQHNUM);
        db.execSQL(Dbs.CREATE_TABLE_XZQHNUM1);
        db.execSQL(Dbs.CREATE_TABLE_INSPECTIONGETTASK);
        db.execSQL(Dbs.CREATE_TABLE_INSPECTIONGETTASK2);
        db.execSQL(Dbs.CREATE_TABLE_PATROLS);
        db.execSQL(Dbs.CREATE_TABLE_PATROLS_TYPE);
        db.execSQL(Dbs.CREATE_TABLE_PATROLS_SP);
        db.execSQL(Dbs.CREATE_TABLE_PATROLS_CH);
        db.execSQL(Dbs.CREATE_TABLE_PATROLS_TH);	//add teng.guo
        db.execSQL(Dbs.CREATE_TABLE_clqk_now);
        db.execSQL(Dbs.CREATE_TABLE_WpzfAjNum);
        db.execSQL(Dbs.CREATE_TABLE_INSPECTIONEDIT);
        db.execSQL(Dbs.CREATE_TABLE_INSPECTIONEDIT_JSDT);
        db.execSQL(Dbs.CREATE_TABLE_JSDT);
        db.execSQL(Dbs.CREATE_TABLE_PHOTO);
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (oldVersion < 2) {
			db.execSQL(Dbs.CREATE_TABLE_PATROLS_TH);
		}
    	if (oldVersion < 3) {
			db.execSQL(Dbs.ADD_INSPECTIONGETTASK2_ZYGDMJ);
		}
    }
}
