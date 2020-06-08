package com.quvideo.application.gallery.utils;

import android.text.TextUtils;
import java.io.File;

/**
 * Create by zhengjunfei on 2019/9/2
 */
public class FileUtils {
  private static final String LOG_TAG = FileUtils.class.getSimpleName();
  public static final String ASSETS_THEME = "assets_android://";

  /**
   *
   */
  public static boolean createMultilevelDirectory(String strPath) {
    if (TextUtils.isEmpty(strPath)) {
      return false;
    }

    File dir = null;
    dir = new File(strPath);
    if (dir.exists() && dir.isDirectory()) {
      return true;
    }

    //make dirs
    dir.mkdirs();

    //check again
    File dirNew = new File(strPath);
    return dirNew.exists() && dirNew.isDirectory();
  }

  /**
   *
   */
  public static boolean isFileExisted(String strFullFileName) {
    if (TextUtils.isEmpty(strFullFileName)) {
      return false;
    }

    if (strFullFileName.startsWith(ASSETS_THEME)) {
      String strAssetsFile = strFullFileName.substring(ASSETS_THEME.length());
      if (TextUtils.isEmpty(strAssetsFile)) {
        return false;
      }
      return ResourceUtils.isAssetsFileExisted(ResourceUtils.mAssetsManager, strAssetsFile);
    } else {
      File file = new File(strFullFileName);
      return (file.isFile() && file.exists());
    }
  }
}
