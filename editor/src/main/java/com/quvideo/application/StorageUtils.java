package com.quvideo.application;

import android.content.Context;
import android.text.TextUtils;
import com.quvideo.application.utils.FileUtils;
import java.io.File;

/**
 * @author wangjieming
 * @date 2019-10-16.
 */
public class StorageUtils {

  private static final String PATH_ROOT = ".demo/";
  private static final String PATH_TEMPLATE = ".template/";

  // 素材目录
  private static String mTemplatePath;

  public static void init(Context context) {
    String innerDir = context.getFilesDir().getAbsolutePath();
    if (!innerDir.endsWith(File.separator)) {
      innerDir += File.separator;
    }
    File outerFile = context.getExternalFilesDir(null);
    String outerAppDir = null;
    if (outerFile != null) {
      outerAppDir = outerFile.getAbsolutePath();
      if (!TextUtils.isEmpty(outerAppDir) && !outerAppDir.endsWith(File.separator)) {
        outerAppDir += File.separator;
      }
    }
    if (TextUtils.isEmpty(outerAppDir)) {
      outerAppDir = innerDir;
    }
    mTemplatePath = outerAppDir + PATH_ROOT + PATH_TEMPLATE;
    FileUtils.createMultilevelDirectory(mTemplatePath);
    FileUtils.createNoMediaFileInPath(mTemplatePath);
  }

  public static synchronized String getTemplatePath(Context context) {
    if (TextUtils.isEmpty(mTemplatePath)) {
      init(context);
    }
    return mTemplatePath;
  }
}
