package com.quvideo.application.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wuzhongyou on 2017/9/29.
 */
public class DraftDBHelper extends SQLiteOpenHelper {

  private static DraftDBHelper instance = null;

  private static final String DATABASE_NAME = "demo_draft.db";
  private final static int DB_VERSION = 1;

  public static Context appContext;

  public static void setAppContext(Context context) {
    if (appContext == null) {
      synchronized (DraftDBHelper.class) {
        if (appContext == null && context != null) {
          appContext = context.getApplicationContext();
        }
      }
    }
  }

  private DraftDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DB_VERSION);
  }

  public static DraftDBHelper getInstance() {
    if (instance == null) {
      synchronized (DraftDBHelper.class) {
        if (instance == null && appContext != null) {
          instance = new DraftDBHelper(appContext);
        }
      }
    }
    return instance;
  }

  /** Creates underlying database table using DAOs. */
  public static void createAllTables(SQLiteDatabase db) {
    DraftInfoDao.createTable(db);
  }

  /** Drops underlying database table using DAOs. */
  public static void dropAllTables(SQLiteDatabase db) {
    DraftInfoDao.dropTable(db);
  }

  /**
   * When the database is (re)created, create our table
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    createAllTables(db);
  }

  @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    dropAllTables(db);
    createAllTables(db);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVerson) {
  }

  public static synchronized void closeDB() {
    if (instance != null) {
      try {
        SQLiteDatabase var0 = instance.getWritableDatabase();
        var0.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      instance = null;
    }
  }

  /**
   * 删除数据库
   */
  public static void deleteDB(Context context) {
    try {
      context.deleteDatabase(DATABASE_NAME);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
