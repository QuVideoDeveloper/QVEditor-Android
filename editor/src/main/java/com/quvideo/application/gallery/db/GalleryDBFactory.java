package com.quvideo.application.gallery.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.quvideo.application.gallery.db.bean.DaoMaster;
import com.quvideo.application.gallery.db.bean.DaoSession;
import com.quvideo.application.gallery.db.impl.MediaDaoImpl;
import org.greenrobot.greendao.database.Database;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc Gallery DB Factory
 * @since 6/27/2019
 */
public class GalleryDBFactory {

  private static volatile GalleryDBFactory instance;
  private static final String DB_NAME = "gallery_common.db";

  private DaoSession daoSession;
  private DBHelper dbHelper;

  private boolean inited;
  private MediaDaoImpl mediaDaoImpl;

  public synchronized static GalleryDBFactory getInstance() {
    if (null == instance) {
      synchronized (GalleryDBFactory.class) {
        if (null == instance) {
          instance = new GalleryDBFactory();
        }
      }
    }
    return instance;
  }

  private GalleryDBFactory() {
  }

  void initDB(Context context) {
    if (inited) {
      return;
    }
    synchronized (this) {
      inited = true;

      dbHelper = new DBHelper(context, DB_NAME);
      Database database = dbHelper.getWritableDb();
      DaoMaster mDaoMaster = new DaoMaster(database);
      daoSession = mDaoMaster.newSession();
      initDAOs(daoSession);
    }
  }

  private void initDAOs(DaoSession daoSession) {
    mediaDaoImpl = new MediaDaoImpl(daoSession);
  }

  public MediaDaoImpl getMediaDaoImpl() {
    return mediaDaoImpl;
  }

  class DBHelper extends DaoMaster.OpenHelper {

    DBHelper(Context context, String name) {
      super(context, name);
    }

    @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      //super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override public void onUpgrade(Database db, int oldVersion, int newVersion) {
      super.onUpgrade(db, oldVersion, newVersion);
      //DBUpgradeHelper.onUpgrade(db, DBProject.class);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      super.onUpgrade(db, oldVersion, newVersion);
    }
  }
}
