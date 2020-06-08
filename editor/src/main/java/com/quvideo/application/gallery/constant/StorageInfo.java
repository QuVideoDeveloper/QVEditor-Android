package com.quvideo.application.gallery.constant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import com.quvideo.application.gallery.utils.FileUtils;
import com.quvideo.application.utils.MD5;
import java.io.File;
import java.util.ArrayList;

/**
 * Create by zhengjunfei on 2019/9/2
 */
public class StorageInfo {

  private static ArrayList<String> mStorageList = null;
  private static final String APP_CACHE_PATH_RELATIE = "Android/data/";
  private static Context mContext = null;

  public static void setApplicationContext(Context ctx) {
    if (ctx != null) {
      mContext = ctx.getApplicationContext();
    }
  }

  public synchronized static ArrayList<String> getStorageList() {
    init();
    return mStorageList;
  }

  public static synchronized void clear() {
    if (mStorageList != null) {
      mStorageList.clear();
    }
    mStorageList = null;
  }

  public synchronized static boolean init() {
    if (mStorageList != null) {
      return true;
    }

    mStorageList = getStorageList(mContext, true);
    return true;
  }

  private static ArrayList<String> getStorageList(Context ctx, boolean bCanWrite) {
    ArrayList<String> storageList = null;
    try {
      if (android.os.Build.VERSION.SDK_INT >= 19) {
        storageList = getStorageListViaCacheDirs(ctx, bCanWrite);
      } else {
        storageList = getStorageListViaEnvironment(ctx, bCanWrite);
      }

      if (storageList != null && storageList.size() > 1) {
        storageList = removeInvalidStorage(ctx, storageList, bCanWrite);
      }
    } catch (Throwable e) {

    }

    if (storageList == null) {
      storageList = new ArrayList<String>();
    }

    if (storageList.isEmpty()) {
      storageList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    return storageList;
  }

  private static ArrayList<String> removeInvalidStorage(Context ctx,
      ArrayList<String> storageListCheck, boolean bCanWrite) {
    if (storageListCheck == null) {
      return null;
    }

    String strAppCachePathRelative = "";
    if (ctx != null) {
      strAppCachePathRelative = APP_CACHE_PATH_RELATIE + ctx.getPackageName() + File.separator;
    }

    ArrayList<String> storageList = new ArrayList<String>();
    while (!storageListCheck.isEmpty()) {
      String strMainPath = null;
      File file = null;
      try {
        String strTestSN = MD5.md5(String.valueOf(System.currentTimeMillis()));
        strMainPath = storageListCheck.remove(0);
        String strTestFile =
            strMainPath + File.separator + strAppCachePathRelative + strTestSN + ".dat";
        file = new File(strTestFile);

        FileUtils.createMultilevelDirectory(file.getParent());
        file.createNewFile();
        if (!(file.exists() && file.isFile())) {
          continue;//try to create new file failed
        }

        //same file, but different path name?
        for (int i = storageListCheck.size() - 1; i >= 0; i--) {
          String strFile2 = storageListCheck.get(i)
              + File.separator
              + strAppCachePathRelative
              + strTestSN
              + ".dat";
          File file2 = new File(strFile2);
          if (file2.exists() && file2.isFile()) {
            storageListCheck.remove(i);
          }
        }

        //path can write
        storageList.add(strMainPath);
      } catch (Throwable e) {
        // TODO Auto-generated catch block
        //e.printStackTrace();
        if (strMainPath != null && !bCanWrite) {
          storageList.add(strMainPath);
        }
      } finally {
        try {
          if (file != null && file.exists()) {
            file.delete();
          }
        } catch (Exception e) {

        }
      }
    }

    return storageList;
  }

  @SuppressLint("NewApi")
  private static ArrayList<String> getStorageListViaCacheDirs(Context ctx, boolean bCanWrite) {
    ArrayList<String> storageList = new ArrayList<String>();

    try {
      storageList.add(Environment.getExternalStorageDirectory().getAbsolutePath());

      File[] files = ctx.getExternalCacheDirs();
      if (files != null && files.length > 0) {
        for (File file : files) {
          if (file == null || !(file.isDirectory() && file.canWrite())) {
            continue;
          }

          String strPath = file.getAbsolutePath();
          if (strPath == null) {
            continue;
          }
          int index = strPath.indexOf("/Android/");
          if (index > 0) {
            String strDisk = strPath.substring(0, index);
            if (!storageList.contains(strDisk)) {
              storageList.add(strDisk);
            }
          }
        }
      }
    } catch (Throwable e) {

    }

    return storageList;
  }

  private static ArrayList<String> getStorageListViaEnvironment(Context ctx, boolean bCanWrite) {
    ArrayList<String> storageList = new ArrayList<String>();
    try {
      storageList.add(Environment.getExternalStorageDirectory().getAbsolutePath());

      File file = ctx.getExternalCacheDir();
      if (file != null) {
        String strPath = file.getAbsolutePath();
        int index = strPath.indexOf("/Android/");
        if (index > 0) {
          String strDisk = strPath.substring(0, index);
          if (!storageList.contains(strDisk)) {
            storageList.add(strDisk);
          }
        }
      }

      String strSecondaryDisk = getSecondaryStorage(ctx, bCanWrite);
      if (strSecondaryDisk != null) {
        if (!storageList.contains(strSecondaryDisk)) {
          storageList.add(strSecondaryDisk);
        }
      }
    } catch (Throwable e) {

    }
    return storageList;
  }

  private static String getSecondaryStorage(Context ctx, boolean bCanWrite) {
    try {
      final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
      if (TextUtils.isEmpty(rawSecondaryStoragesStr)) {
        return null;
      }

      String[] paths = rawSecondaryStoragesStr.split(":");
      if (paths == null || paths.length == 0) {
        return null;
      }

      File file;
      for (String path : paths) {
        if (path == null) {
          continue;
        }

        file = new File(path);
        if (file.isDirectory() && file.canWrite()) {
          return file.getAbsolutePath();
        }
      }
    } catch (Throwable e) {

    }

    return null;
  }

  public synchronized static String getMainStorage() {
    init();
    if (mStorageList == null || mStorageList.isEmpty()) {
      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    return mStorageList.get(0);
  }

  public synchronized static String getExtStorage() {
    init();
    if (mStorageList == null || mStorageList.isEmpty()) {
      return null;
    }
    if (mStorageList.size() <= 1) {
      return null;
    }

    return mStorageList.get(1);
  }
}
