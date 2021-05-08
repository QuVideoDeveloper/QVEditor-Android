package com.quvideo.application.sp;

import android.content.Context;
import android.content.SharedPreferences;

public class DemoSharedPref {

  /**
   * logic private property.
   */
  private SharedPreferences mPreferences;
  private SharedPreferences.Editor mEditor;

  private static final String SP_FILE = "demo_sp_msg";
  private static final String KEY_LAST_VERSION = "last_version";

  private boolean mbInit = false;

  private static volatile DemoSharedPref INSTANCE = null;

  public static DemoSharedPref getInstance() {
    if (null == INSTANCE) {
      synchronized (DemoSharedPref.class) {
        if (null == INSTANCE) {
          INSTANCE = new DemoSharedPref();
        }
      }
    }
    return INSTANCE;
  }

  private DemoSharedPref() {
  }

  /**
   * init method can only call one time.
   */
  public synchronized boolean init(Context context) {
    initPref(context);
    return true;
  }

  private void initPref(Context context) {
    if (mPreferences == null && !mbInit) {
      mPreferences = context.getApplicationContext()
          .getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
      if (mPreferences != null) {
        mEditor = mPreferences.edit();
        mbInit = true;
      }
    }
  }

  public long getLastVersionCode() {
    if (null == mPreferences) {
      return -1;
    }
    return mPreferences.getLong(KEY_LAST_VERSION, -1);
  }

  public void saveLastVersion(long currentVersion) {
    if (null == mPreferences) {
      return;
    }
    mEditor.putLong(KEY_LAST_VERSION, currentVersion);
    mEditor.commit();
  }
}
